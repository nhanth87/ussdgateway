#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

NDIALOGS=${1:-40000}
TIMEOUT=${2:-90}
CONCURRENT=${3:-10000}

echo "=== Killing any leftover Java processes ==="
killall -9 java 2>/dev/null || true
sleep 2

echo "=== Starting MAP Load Test Server (TCP) ==="
nohup ant -f mo_sms_build.xml server -Dtest.server.channelType=tcp -Dtest.server.hostIp=0.0.0.0 -Dtest.server.hostPort=8011 -Dtest.server.originatingPc=2 -Dtest.server.destinationPc=1 > /tmp/map-server-tcp-${NDIALOGS}.log 2>&1 &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

sleep 10

echo "=== Starting MAP Load Test Client (TCP, NDIALOGS=$NDIALOGS, CONCURRENT=$CONCURRENT) ==="
timeout $TIMEOUT ant -f mo_sms_build.xml client -Dtest.client.numOfDialogs=$NDIALOGS -Dtest.client.concurrentDialog=$CONCURRENT -Dtest.client.channelType=tcp -Dtest.client.rampUpPeriod=0 > /tmp/map-client-tcp-${NDIALOGS}.log 2>&1 || true

echo "=== Killing server ==="
kill -9 $SERVER_PID 2>/dev/null || true
killall -9 java 2>/dev/null || true

echo "=== Test complete ==="
