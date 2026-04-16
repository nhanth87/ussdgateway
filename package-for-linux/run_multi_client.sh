#!/bin/bash
# Run 4 parallel MAP SMS-MO clients over 4 SCTP associations
# Server must be started first with multi-association support

NUM_DIALOGS_PER_CLIENT="${1:-25000}"
CHANNEL_TYPE="${2:-SCTP}"

CP="classes:$(find lib -name '*.jar' | tr '\n' ':')"

# Client 0 -> server port 8011
java -cp "$CP" -Xms4g -Xmx4g -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Client \
  "$NUM_DIALOGS_PER_CLIENT" "$NUM_DIALOGS_PER_CLIENT" "$CHANNEL_TYPE" \
  127.0.0.1 8012 -1 127.0.0.1 8011 \
  IPSP 101 102 1 2 3 2 8 8 \
  1111112 9960639999 2 64 0 > client_0.log 2>&1 &

# Client 1 -> server port 8016
java -cp "$CP" -Xms4g -Xmx4g -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Client \
  "$NUM_DIALOGS_PER_CLIENT" "$NUM_DIALOGS_PER_CLIENT" "$CHANNEL_TYPE" \
  127.0.0.1 8013 -1 127.0.0.1 8016 \
  IPSP 101 102 1 2 3 2 8 8 \
  1111112 9960639999 2 64 0 > client_1.log 2>&1 &

# Client 2 -> server port 8021
java -cp "$CP" -Xms4g -Xmx4g -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Client \
  "$NUM_DIALOGS_PER_CLIENT" "$NUM_DIALOGS_PER_CLIENT" "$CHANNEL_TYPE" \
  127.0.0.1 8014 -1 127.0.0.1 8021 \
  IPSP 101 102 1 2 3 2 8 8 \
  1111112 9960639999 2 64 0 > client_2.log 2>&1 &

# Client 3 -> server port 8026
java -cp "$CP" -Xms4g -Xmx4g -XX:+UseG1GC \
  org.restcomm.protocols.ss7.map.load.sms.mo.Client \
  "$NUM_DIALOGS_PER_CLIENT" "$NUM_DIALOGS_PER_CLIENT" "$CHANNEL_TYPE" \
  127.0.0.1 8015 -1 127.0.0.1 8026 \
  IPSP 101 102 1 2 3 2 8 8 \
  1111112 9960639999 2 64 0 > client_3.log 2>&1 &

echo "4 client processes started."
echo "Wait for completion with: wait"
