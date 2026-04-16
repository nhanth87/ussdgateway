#!/bin/bash
rm -f /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/target/classes/org/restcomm/protocols/ss7/map/load/sms/mo/*.class
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH
ant -f mo_sms_build.xml compile
jar cf target/map-load-9.2.7.jar -C target/classes .
echo 'Rebuilt with JDK 11'
