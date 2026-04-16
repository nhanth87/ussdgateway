#!/bin/bash
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load
killall -9 java 2>/dev/null || true
sleep 2
ant -f mo_sms_build.xml server -Dtest.channel.type=SCTP -Dtest.server.hostIp=127.0.0.1 -Dtest.server.hostPort=8011 -Dtest.server.peerIp=127.0.0.1 -Dtest.server.peerPort=8012 -Dtest.server.deliveryTransferMessageThreadCount=64 > /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/server.log 2>&1 &
SERVER_PID=$!
sleep 8
ant -f mo_sms_build.xml client -Dtest.channel.type=SCTP -Dtest.client.hostIp=127.0.0.1 -Dtest.client.hostPort=8012 -Dtest.client.peerIp=127.0.0.1 -Dtest.client.peerPort=8011 -Dtest.client.numOfDialogs=100000 -Dtest.client.concurrentDialog=100000 -Dtest.client.deliveryTransferMessageThreadCount=64 -Dtest.client.rampUpPeriod=0 > /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/client.log 2>&1 &
CLIENT_PID=$!
echo "Server PID: $SERVER_PID, Client PID: $CLIENT_PID"
wait $CLIENT_PID
echo "Client finished"
