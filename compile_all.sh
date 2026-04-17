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

echo "=== 1. Compile SCTP-impl modified files ==="
cd $BASE/sctp/sctp-impl
SCTP_CP="$CP"
for jar in ../sctp-api/target/*.jar; do
  if [ -f "$jar" ]; then SCTP_CP="$SCTP_CP:$jar"; fi
done
javac -cp "$SCTP_CP" -d src/main/java \
  src/main/java/org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.java \
  src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpChannel.java \
  src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpServerChannel.java \
  src/main/java/org/mobicents/protocols/sctp/netty/NettyAssociationImpl.java \
  src/main/java/org/mobicents/protocols/sctp/netty/NettyServerImpl.java
# Update jar
cd src/main/java
jar uf $BASE/jSS7/map/load/target/load/sctp-impl.jar \
  org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.class \
  org/mobicents/protocols/sctp/netty/PooledNioSctpChannel.class \
  org/mobicents/protocols/sctp/netty/PooledNioSctpServerChannel.class \
  org/mobicents/protocols/sctp/netty/NettyAssociationImpl.class \
  org/mobicents/protocols/sctp/netty/NettyServerImpl.class
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
echo "=== 3. Compile tcap-impl modified files ==="
cd $BASE/jSS7/tcap/tcap-impl
javac -cp "$CP" -d src/main/java \
  src/main/java/org/restcomm/protocols/ss7/tcap/TCAPProviderImpl.java \
  src/main/java/org/restcomm/protocols/ss7/tcap/DialogImpl.java
cd src/main/java
jar uf $BASE/jSS7/map/load/target/load/tcap-impl.jar \
  org/restcomm/protocols/ss7/tcap/TCAPProviderImpl.class \
  org/restcomm/protocols/ss7/tcap/DialogImpl.class
echo "Updated tcap-impl.jar"

echo ""
echo "=== 4. Compile map-impl modified files ==="
cd $BASE/jSS7/map/map-impl
javac -cp "$CP" -d src/main/java \
  src/main/java/org/restcomm/protocols/ss7/map/MAPDialogImpl.java
cd src/main/java
jar uf $BASE/jSS7/map/load/target/load/map-impl.jar \
  org/restcomm/protocols/ss7/map/MAPDialogImpl.class
echo "Updated map-impl.jar"

echo ""
echo "=== 5. Compile cap-impl modified files ==="
cd $BASE/jSS7/cap/cap-impl
CAP_CP="$CP:target/classes"
for jar in ../cap-api/target/*.jar; do
  if [ -f "$jar" ]; then CAP_CP="$CAP_CP:$jar"; fi
done
javac -cp "$CAP_CP" -d src/main/java \
  src/main/java/org/restcomm/protocols/ss7/cap/CAPDialogImpl.java
cd src/main/java
CAP_JAR=$(ls $BASE/jSS7/cap/cap-impl/target/cap-impl-*.jar | head -n 1)
jar uf "$CAP_JAR" org/restcomm/protocols/ss7/cap/CAPDialogImpl.class
echo "Updated $CAP_JAR"
# Also copy to load package directory if needed
cp "$CAP_JAR" $BASE/jSS7/map/load/target/load/cap-impl.jar 2>/dev/null || true

echo ""
echo "=== 6. Compile Ss7ThreadPoolTuner ==="
cd $BASE/jSS7/map/load
javac -cp "target/classes:$CP" -d target/classes \
  src/main/java/org/restcomm/protocols/ss7/map/load/Ss7ThreadPoolTuner.java
echo "Compiled Ss7ThreadPoolTuner.class"

echo ""
echo "=== 7. Compile map/load Client.java and Server.java ==="
cd $BASE/jSS7/map/load
javac -cp "target/classes:$CP:src/main/resources" -d target/classes \
  src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java \
  src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java
echo "Compiled Client.java and Server.java"

echo ""
echo "=== All compilations complete ==="
