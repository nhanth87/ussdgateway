#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

NDIALOGS=${1:-1}
TIMEOUT=${2:-120}
CONCURRENT=${3:-400}

echo "=== Killing any leftover Java processes ==="
killall -9 java 2>/dev/null || true
sleep 2

echo "=== Starting MAP Load Test Server ==="
nohup ant -f mo_sms_build.xml server -Dtest.server.channelType=sctp -Dtest.server.hostIp=0.0.0.0 -Dtest.server.hostPort=8011 -Dtest.server.originatingPc=2 -Dtest.server.destinationPc=1 > /tmp/map-server-${NDIALOGS}.log 2>&1 &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

sleep 10

echo "=== Starting MAP Load Test Client (NDIALOGS=$NDIALOGS, CONCURRENT=$CONCURRENT, TIMEOUT=$TIMEOUT) ==="
timeout $TIMEOUT ant -f mo_sms_build.xml client -Dtest.client.numOfDialogs=$NDIALOGS -Dtest.client.concurrentDialog=$CONCURRENT -Dtest.client.channelType=sctp -Dtest.client.rampUpPeriod=0 > /tmp/map-client-${NDIALOGS}.log 2>&1 || true

echo "=== Killing server ==="
kill -9 $SERVER_PID 2>/dev/null || true
killall -9 java 2>/dev/null || true

echo "=== Test complete ==="
