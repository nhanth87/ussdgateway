#!/bin/bash
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

# Build classpath from all jars in target/load plus target/classes
CP="target/classes"
for jar in target/load/*.jar; do
  CP="$CP:$jar"
done
CP="$CP:src/main/resources"

echo "Compiling Client.java..."
javac -cp "$CP" -d target/classes src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java
echo "Done"
