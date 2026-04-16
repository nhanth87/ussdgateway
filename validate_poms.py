import xml.etree.ElementTree as ET
import sys

files = [
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/mtp/mtp-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/api/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap/tcap-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap-ansi/tcap-ansi-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/sniffer/sniffer-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/oam/common/statistics/impl/pom.xml",
]

for f in files:
    try:
        ET.parse(f)
        print(f"OK: {f}")
    except Exception as e:
        print(f"FAIL: {f} -> {e}")
        sys.exit(1)
