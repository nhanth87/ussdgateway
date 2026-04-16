#!/bin/bash
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir
JSS7=jSS7
M2=~/.m2/repository

echo '=== jSS7 modules ==='
for f in \
  "$JSS7/map/map-api/target/map-api-9.2.7.jar" \
  "$JSS7/map/map-impl/target/map-impl-9.2.7.jar" \
  "$JSS7/sccp/sccp-api/target/sccp-api-9.2.7.jar" \
  "$JSS7/sccp/sccp-impl/target/sccp-impl-9.2.7.jar" \
  "$JSS7/sccp/sccp-api-ext/target/sccp-api-ext-9.2.7.jar" \
  "$JSS7/sccp/sccp-impl-ext/target/sccp-impl-ext-9.2.7.jar" \
  "$JSS7/tcap/tcap-api/target/tcap-api-9.2.7.jar" \
  "$JSS7/tcap/tcap-impl/target/tcap-impl-9.2.7.jar" \
  "$JSS7/m3ua/api/target/m3ua-api-9.2.7.jar" \
  "$JSS7/m3ua/impl/target/m3ua-impl-9.2.7.jar" \
  "$JSS7/mtp/mtp-api/target/mtp-api-9.2.7.jar" \
  "$JSS7/mtp/mtp-impl/target/mtp-9.2.7.jar" \
  "$JSS7/isup/isup-api/target/isup-api-9.2.7.jar" \
  "$JSS7/isup/isup-impl/target/isup-impl-9.2.7.jar" \
  "$JSS7/ss7-ext/ss7-ext-api/target/ss7-ext-api-9.2.7.jar" \
  "$JSS7/ss7-ext/ss7-ext-impl/target/ss7-ext-impl-9.2.7.jar" \
  "$JSS7/statistics/api/target/restcomm-statistics-9.2.7.jar" \
  "$JSS7/statistics/impl/target/restcomm-statistics-9.2.7.jar" \
  "$JSS7/congestion/target/restcomm-congestion-9.2.7.jar" \
  "$JSS7/map/load/target/map-load-9.2.7.jar"; do
  if [ -f "$f" ]; then echo OK: "$f"; else echo MISSING: "$f"; fi
done

echo '=== SCTP modules ==='
for f in \
  "sctp/sctp-api/target/sctp-api-2.0.13-SNAPSHOT.jar" \
  "sctp/sctp-impl/target/sctp-impl-2.0.13-SNAPSHOT.jar"; do
  if [ -f "$f" ]; then echo OK: "$f"; else echo MISSING: "$f"; fi
done

echo '=== Maven dependencies ==='
for f in \
  "$M2/io/netty/netty-common/4.2.11.Final/netty-common-4.2.11.Final.jar" \
  "$M2/io/netty/netty-buffer/4.2.11.Final/netty-buffer-4.2.11.Final.jar" \
  "$M2/io/netty/netty-resolver/4.2.11.Final/netty-resolver-4.2.11.Final.jar" \
  "$M2/io/netty/netty-transport/4.2.11.Final/netty-transport-4.2.11.Final.jar" \
  "$M2/io/netty/netty-transport-native-unix-common/4.2.11.Final/netty-transport-native-unix-common-4.2.11.Final.jar" \
  "$M2/io/netty/netty-handler/4.2.11.Final/netty-handler-4.2.11.Final.jar" \
  "$M2/io/netty/netty-codec/4.2.11.Final/netty-codec-4.2.11.Final.jar" \
  "$M2/io/netty/netty-codec-base/4.2.11.Final/netty-codec-base-4.2.11.Final.jar" \
  "$M2/io/netty/netty-transport-sctp/4.2.11.Final/netty-transport-sctp-4.2.11.Final.jar" \
  "$M2/org/jctools/jctools-core/4.0.3/jctools-core-4.0.3.jar" \
  "$M2/com/google/guava/guava/18.0/guava-18.0.jar" \
  "$M2/log4j/log4j/1.2.14/log4j-1.2.14.jar" \
  "$M2/javolution/javolution/5.5.1/javolution-5.5.1.jar" \
  "$M2/concurrent/concurrent/1.3.4/concurrent-1.3.4.jar" \
  "$M2/org/apache/commons/commons-email/1.3.2/commons-email-1.3.2.jar" \
  "$M2/com/fasterxml/jackson/dataformat/jackson-dataformat-xml/2.15.2/jackson-dataformat-xml-2.15.2.jar" \
  "$M2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar" \
  "$M2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar" \
  "$M2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar" \
  "$M2/com/fasterxml/woodstox/woodstox-core/6.5.1/woodstox-core-6.5.1.jar" \
  "$M2/org/codehaus/woodstox/stax2-api/4.2.1/stax2-api-4.2.1.jar"; do
  if [ -f "$f" ]; then echo OK: "$f"; else echo MISSING: "$f"; fi
done

echo '=== External libs (GMLC) ==='
for f in \
  "gmlc/core/bootstrap/target/restcomm-gmlc-server/lib/asn-2.2.0-143.jar" \
  "gmlc/core/bootstrap/target/restcomm-gmlc-server/lib/stream-1.0.0.CR1.jar" \
  "gmlc/core/bootstrap/target/restcomm-gmlc-server/lib/commons-1.0.0.CR1.jar"; do
  if [ -f "$f" ]; then echo OK: "$f"; else echo MISSING: "$f"; fi
done
