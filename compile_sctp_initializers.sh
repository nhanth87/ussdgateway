#!/bin/bash
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/sctp/sctp-impl
NETTY=/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/target/load
javac -cp "target/classes:target/test-classes:$NETTY/netty-common-4.2.11.Final.jar:$NETTY/netty-buffer-4.2.11.Final.jar:$NETTY/netty-resolver-4.2.11.Final.jar:$NETTY/netty-transport-4.2.11.Final.jar:$NETTY/netty-transport-native-unix-common-4.2.11.Final.jar:$NETTY/netty-transport-sctp-4.2.11.Final.jar:$NETTY/netty-codec-4.2.11.Final.jar:$NETTY/netty-codec-base-4.2.11.Final.jar:$NETTY/netty-handler-4.2.11.Final.jar" -d target/classes src/main/java/org/mobicents/protocols/sctp/netty/NettySctpClientChannelInitializer.java src/main/java/org/mobicents/protocols/sctp/netty/NettySctpServerChannelInitializer.java
jar uf target/sctp-impl-2.0.13-SNAPSHOT.jar -C target/classes org/mobicents/protocols/sctp/netty/NettySctpClientChannelInitializer.class -C target/classes org/mobicents/protocols/sctp/netty/NettySctpServerChannelInitializer.class
cp target/sctp-impl-2.0.13-SNAPSHOT.jar /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/target/load/sctp-impl.jar
echo 'SCTP initializers updated with JDK 11'
