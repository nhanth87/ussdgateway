#!/bin/bash
# Setup dependencies for MAP Load Test
# This script copies JARs from sibling modules' target directories

set -e

LOAD_DIR="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load"
TARGET_LOAD="$LOAD_DIR/target/load"
JSS7_ROOT="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7"

echo "Creating target/load directory..."
mkdir -p "$TARGET_LOAD"

echo "Copying map-load.jar..."
cp "$LOAD_DIR/target/map-load-9.2.7.jar" "$TARGET_LOAD/map-load.jar"

echo "Copying SS7 module JARs..."
# Map modules
cp "$JSS7_ROOT/map/map-api/target/map-api-9.2.7.jar" "$TARGET_LOAD/map-api.jar" 2>/dev/null || echo "  map-api not found"
cp "$JSS7_ROOT/map/map-impl/target/map-impl-9.2.7.jar" "$TARGET_LOAD/map-impl.jar" 2>/dev/null || echo "  map-impl not found"

# SCTP modules
cp "$JSS7_ROOT/../sctp/sctp-api/target/sctp-api-2.0.12.jar" "$TARGET_LOAD/sctp-api.jar" 2>/dev/null || echo "  sctp-api not found"
cp "$JSS7_ROOT/../sctp/sctp-impl/target/sctp-impl-2.0.12.jar" "$TARGET_LOAD/sctp-impl.jar" 2>/dev/null || echo "  sctp-impl not found"

# SCCP modules
cp "$JSS7_ROOT/sccp/sccp-api/target/sccp-api-9.2.7.jar" "$TARGET_LOAD/sccp-api.jar" 2>/dev/null || echo "  sccp-api not found"
cp "$JSS7_ROOT/sccp/sccp-impl/target/sccp-impl-9.2.7.jar" "$TARGET_LOAD/sccp-impl.jar" 2>/dev/null || echo "  sccp-impl not found"
cp "$JSS7_ROOT/sccp/sccp-api-ext/target/sccp-api-ext-9.2.7.jar" "$TARGET_LOAD/sccp-api-ext.jar" 2>/dev/null || echo "  sccp-api-ext not found"
cp "$JSS7_ROOT/sccp/sccp-impl-ext/target/sccp-impl-ext-9.2.7.jar" "$TARGET_LOAD/sccp-impl-ext.jar" 2>/dev/null || echo "  sccp-impl-ext not found"

# TCAP modules
cp "$JSS7_ROOT/tcap/tcap-api/target/tcap-api-9.2.7.jar" "$TARGET_LOAD/tcap-api.jar" 2>/dev/null || echo "  tcap-api not found"
cp "$JSS7_ROOT/tcap/tcap-impl/target/tcap-impl-9.2.7.jar" "$TARGET_LOAD/tcap-impl.jar" 2>/dev/null || echo "  tcap-impl not found"

# M3UA modules
cp "$JSS7_ROOT/m3ua/m3ua-api/target/m3ua-api-9.2.7.jar" "$TARGET_LOAD/m3ua-api.jar" 2>/dev/null || echo "  m3ua-api not found"
cp "$JSS7_ROOT/m3ua/m3ua-impl/target/m3ua-impl-9.2.7.jar" "$TARGET_LOAD/m3ua-impl.jar" 2>/dev/null || echo "  m3ua-impl not found"

# MTP modules
cp "$JSS7_ROOT/mtp/mtp-api/target/mtp-api-9.2.7.jar" "$TARGET_LOAD/mtp-api.jar" 2>/dev/null || echo "  mtp-api not found"
cp "$JSS7_ROOT/mtp/mtp/target/mtp-9.2.7.jar" "$TARGET_LOAD/mtp.jar" 2>/dev/null || echo "  mtp not found"

# ISUP modules
cp "$JSS7_ROOT/isup/isup-api/target/isup-api-9.2.7.jar" "$TARGET_LOAD/isup-api.jar" 2>/dev/null || echo "  isup-api not found"
cp "$JSS7_ROOT/isup/isup-impl/target/isup-impl-9.2.7.jar" "$TARGET_LOAD/isup-impl.jar" 2>/dev/null || echo "  isup-impl not found"

# Statistics modules
cp "$JSS7_ROOT/statistics/statistics-api/target/statistics-api-9.2.7.jar" "$TARGET_LOAD/statistics-api.jar" 2>/dev/null || echo "  statistics-api not found"
cp "$JSS7_ROOT/statistics/statistics-impl/target/statistics-impl-9.2.7.jar" "$TARGET_LOAD/statistics-impl.jar" 2>/dev/null || echo "  statistics-impl not found"

# Congestion
cp "$JSS7_ROOT/congestion/target/congestion-9.2.7.jar" "$TARGET_LOAD/restcomm-congestion.jar" 2>/dev/null || echo "  congestion not found"

# SS7 ext modules
cp "$JSS7_ROOT/ss7ext/ss7-ext-api/target/ss7-ext-api-9.2.7.jar" "$TARGET_LOAD/ss7-ext-api.jar" 2>/dev/null || echo "  ss7-ext-api not found"
cp "$JSS7_ROOT/ss7ext/ss7-ext-impl/target/ss7-ext-impl-9.2.7.jar" "$TARGET_LOAD/ss7-ext-impl.jar" 2>/dev/null || echo "  ss7-ext-impl not found"

# ASN
cp "$JSS7_ROOT/../ASN/asn/target/asn-1.0.0.Final.jar" "$TARGET_LOAD/asn.jar" 2>/dev/null || echo "  asn not found"

# Stream
cp "$JSS7_ROOT/../stream/stream/target/stream-1.0.0.Final.jar" "$TARGET_LOAD/stream.jar" 2>/dev/null || echo "  stream not found"

# Commons (check multiple locations)
cp "$JSS7_ROOT/../commons/commons/target/commons-1.0.0.Final.jar" "$TARGET_LOAD/commons.jar" 2>/dev/null || \
cp "$JSS7_ROOT/../mobicents-commons/commons/target/commons-*.jar" "$TARGET_LOAD/commons.jar" 2>/dev/null || echo "  commons not found"

echo ""
echo "Done! Files in target/load:"
ls -la "$TARGET_LOAD/"
