#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

M2=~/.m2/repository
SCTP=/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/sctp/sctp-impl

# Build classpath from m2 repo
CP="$M2/org/mobicents/protocols/sctp/sctp-api/2.0.13-SNAPSHOT/sctp-api-2.0.13-SNAPSHOT.jar"
CP="$CP:$M2/io/netty/netty-common/4.2.11.Final/netty-common-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-buffer/4.2.11.Final/netty-buffer-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-resolver/4.2.11.Final/netty-resolver-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-transport/4.2.11.Final/netty-transport-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-transport-native-unix-common/4.2.11.Final/netty-transport-native-unix-common-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-handler/4.2.11.Final/netty-handler-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-codec/4.2.11.Final/netty-codec-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-codec-base/4.2.11.Final/netty-codec-base-4.2.11.Final.jar"
CP="$CP:$M2/io/netty/netty-transport-sctp/4.2.11.Final/netty-transport-sctp-4.2.11.Final.jar"
CP="$CP:$M2/log4j/log4j/1.2.8/log4j-1.2.8.jar"
CP="$CP:$M2/org/jctools/jctools-core/4.0.3/jctools-core-4.0.3.jar"
CP="$CP:$M2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar"
CP="$CP:$M2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar"
CP="$CP:$M2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar"
CP="$CP:$M2/com/fasterxml/jackson/dataformat/jackson-dataformat-xml/2.15.2/jackson-dataformat-xml-2.15.2.jar"

echo "Compiling sctp-impl..."
mkdir -p $SCTP/target/classes
find $SCTP/src/main/java -name '*.java' > /tmp/sctp-sources.txt
javac -cp "$CP" -d $SCTP/target/classes @/tmp/sctp-sources.txt

echo "Creating sctp-impl jar..."
jar cf $SCTP/target/sctp-impl-2.0.13-SNAPSHOT.jar -C $SCTP/target/classes .

echo "Installing to local maven repo..."
mvn install:install-file \
  -Dfile=$SCTP/target/sctp-impl-2.0.13-SNAPSHOT.jar \
  -DgroupId=org.mobicents.protocols.sctp \
  -DartifactId=sctp-impl \
  -Dversion=2.0.13-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true \
  -q

echo "Done"
