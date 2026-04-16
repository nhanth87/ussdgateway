#!/bin/bash
# Run MAP SMS-MO Load Test Server
# Usage: ./run_server.sh [SCTP|TCP]

CHANNEL_TYPE="${1:-SCTP}"
HOST_IP="${2:-127.0.0.1}"
HOST_PORT="${3:-8011}"
PEER_IP="${4:-127.0.0.1}"
PEER_PORT="${5:-8012}"

CP="classes:$(find lib -name '*.jar' | tr '\n' ':')"

java -cp "$CP" \
  -Xms4g -Xmx4g \
  -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Server \
  "$CHANNEL_TYPE" "$HOST_IP" "$HOST_PORT" -1 "$PEER_IP" "$PEER_PORT" \
  IPSP 101 102 2 1 3 2 8 8 64
