#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

BASE=/mnt/c/Users/Windows/Desktop/ethiopia-working-dir

# Build common classpath from target/load jars
CP=""
for jar in $BASE/jSS7/map/load/target/load/*.jar; do
  CP="$CP:$jar"
done

echo "=== 1. Compile SCTP-impl NettySctpChannelInboundHandlerAdapter ==="
cd $BASE/sctp/sctp-impl
SCTP_CP="$CP"
for jar in ../sctp-api/target/*.jar; do
  if [ -f "$jar" ]; then SCTP_CP="$SCTP_CP:$jar"; fi
done
javac -cp "$SCTP_CP" -d src/main/java \
  src/main/java/org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.java
# Update jar
cd src/main/java
jar uf $BASE/jSS7/map/load/target/load/sctp-impl.jar \
  org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.class
echo "Updated sctp-impl.jar"

echo ""
echo "=== 2. Compile m3ua-impl modified files ==="
cd $BASE/jSS7/m3ua/impl
M3UA_CP="$CP"
for jar in ../../map/load/target/load/sctp-api.jar; do
  M3UA_CP="$M3UA_CP:$jar"
done
# Compile all modified FSM files + SCTPShellExecutor
javac -cp "$M3UA_CP" -d src/main/java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToAct.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsPendToAct.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsDwnToInact.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToInact.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToPendRemAspInac.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToActRemAspAct.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/RemAsStatePenTimeout.java \
  src/main/java/org/restcomm/protocols/ss7/m3ua/impl/oam/SCTPShellExecutor.java
# Update jar
cd src/main/java
jar uf $BASE/jSS7/map/load/target/load/m3ua-impl.jar \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToAct.class \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsPendToAct.class \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsDwnToInact.class \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToInact.class \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToPendRemAspInac.class \
  org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToActRemAspAct.class \
  org/restcomm/protocols/ss7/m3ua/impl/RemAsStatePenTimeout.class \
  org/restcomm/protocols/ss7/m3ua/impl/oam/SCTPShellExecutor.class
echo "Updated m3ua-impl.jar"

echo ""
echo "=== 3. Compile map/load Client.java and Server.java ==="
cd $BASE/jSS7/map/load
javac -cp "target/classes:$CP:src/main/resources" -d target/classes \
  src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java \
  src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java
echo "Compiled Client.java and Server.java"

echo ""
echo "=== All compilations complete ==="
