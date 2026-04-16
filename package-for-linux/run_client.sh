#!/bin/bash
# Run MAP SMS-MO Load Test Client (single association)
# Usage: ./run_client.sh [NUM_DIALOGS] [CONCURRENT_DIALOGS] [SCTP|TCP]

NUM_DIALOGS="${1:-100000}"
CONCURRENT_DIALOGS="${2:-100000}"
CHANNEL_TYPE="${3:-SCTP}"
HOST_IP="${4:-127.0.0.1}"
HOST_PORT="${5:-8012}"
PEER_IP="${6:-127.0.0.1}"
PEER_PORT="${7:-8011}"

CP="classes:$(find lib -name '*.jar' | tr '\n' ':')"

java -cp "$CP" \
  -Xms4g -Xmx4g \
  -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Client \
  "$NUM_DIALOGS" "$CONCURRENT_DIALOGS" "$CHANNEL_TYPE" \
  "$HOST_IP" "$HOST_PORT" -1 "$PEER_IP" "$PEER_PORT" \
  IPSP 101 102 1 2 3 2 8 8 \
  1111112 9960639999 2 64 0
