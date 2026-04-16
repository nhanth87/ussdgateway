#!/bin/bash
set -e
export JAVA_HOME=~/zulu-java/zulu11.78.15-ca-jdk11.0.26-linux_x64
export PATH=$JAVA_HOME/bin:$PATH

cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

NDIALOGS=${1:-100000}
TIMEOUT=${2:-600}
PCAP_FILE="/tmp/jss7_map_${NDIALOGS}dialogs_$(date +%Y%m%d_%H%M%S).pcap"

echo "=== Killing any leftover Java processes ==="
killall -9 java 2>/dev/null || true
sleep 2

echo "=== Starting PCAP capture: $PCAP_FILE ==="
nohup tcpdump -i any -w "$PCAP_FILE" -s 0 sctp or port 8011 2>/dev/null &
TCPDUMP_PID=$!
sleep 3

echo "=== Starting MAP Load Test Server ==="
nohup ant -f mo_sms_build.xml server -Dtest.server.channelType=sctp -Dtest.server.hostIp=0.0.0.0 -Dtest.server.hostPort=8011 -Dtest.server.originatingPc=2 -Dtest.server.destinationPc=1 > /tmp/map-server-${NDIALOGS}.log 2>&1 &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

sleep 10

echo "=== Starting MAP Load Test Client (NDIALOGS=$NDIALOGS, TIMEOUT=$TIMEOUT) ==="
timeout $TIMEOUT ant -f mo_sms_build.xml client -Dtest.client.numOfDialogs=$NDIALOGS -Dtest.client.channelType=sctp -Dtest.client.rampUpPeriod=0 > /tmp/map-client-${NDIALOGS}.log 2>&1 || true

echo "=== Stopping PCAP capture ==="
kill $TCPDUMP_PID 2>/dev/null || true
sleep 2

echo "=== Killing server ==="
kill -9 $SERVER_PID 2>/dev/null || true
killall -9 java 2>/dev/null || true

echo "=== Test complete ==="
echo "PCAP file: $PCAP_FILE"
echo "Server log: /tmp/map-server-${NDIALOGS}.log"
echo "Client log: /tmp/map-client-${NDIALOGS}.log"
