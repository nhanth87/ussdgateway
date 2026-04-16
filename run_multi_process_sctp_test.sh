#!/bin/bash
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load

killall -9 java 2>/dev/null || true
sleep 2

# Build classpath
CP=$(find /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/target/load -name '*.jar' | tr '\n' ':')
CP="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/target/classes:${CP}"

# Start server with 4 associations
ant -f mo_sms_build.xml server \
  -Dtest.channel.type=SCTP \
  -Dtest.server.hostIp=127.0.0.1 -Dtest.server.hostPort=8011 \
  -Dtest.server.peerIp=127.0.0.1 -Dtest.server.peerPort=8012 \
  -Dtest.server.deliveryTransferMessageThreadCount=64 \
  > /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/server_multi.log 2>&1 &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"
sleep 10

# Start 4 client instances
CLIENT_DIALOGS=25000
for i in 0 1 2 3; do
  CLIENT_HOST_PORT=$((8012 + i))
  CLIENT_PEER_PORT=$((8011 + (i * 5)))
  LOGFILE="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/client_multi_${i}.log"
  java -cp "$CP" \
    -Xms4g -Xmx4g \
    -Dcom.sun.management.jmxremote \
    org.restcomm.protocols.ss7.map.load.sms.mo.Client \
    "$CLIENT_DIALOGS" "$CLIENT_DIALOGS" SCTP \
    127.0.0.1 "$CLIENT_HOST_PORT" -1 \
    127.0.0.1 "$CLIENT_PEER_PORT" \
    IPSP 101 102 1 2 3 2 8 8 \
    1111112 9960639999 2 64 0 \
    > "$LOGFILE" 2>&1 &
  echo "Client $i started (host=$CLIENT_HOST_PORT peer=$CLIENT_PEER_PORT)"
  sleep 1
done

echo "Waiting for all clients to finish..."
wait
echo "All clients finished"
