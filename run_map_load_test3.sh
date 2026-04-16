#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

echo "=== Killing any leftover Java processes ==="
killall -9 java 2>/dev/null || true
sleep 10

echo "=== Starting MAP Load Test Server ==="
ant -f mo_sms_build.xml server -Dtest.server.channelType=sctp -Dtest.server.hostIp=0.0.0.0 -Dtest.server.hostPort=8011 > /tmp/map-server3.log 2>&1 &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

# Wait for server to fully start
sleep 10

echo "=== Starting MAP Load Test Client ==="
timeout 30 ant -f mo_sms_build.xml client -Dtest.client.numOfDialogs=1 -Dtest.client.channelType=sctp > /tmp/map-client3.log 2>&1 || true

echo "=== Killing server ==="
kill -TERM -$SERVER_PID 2>/dev/null || true
sleep 2
kill -9 -$SERVER_PID 2>/dev/null || true
killall -9 java 2>/dev/null || true

echo "=== Test complete ==="
