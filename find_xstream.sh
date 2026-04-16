#!/bin/bash
for proj in /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7 /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/sctp /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jdiameter; do
  echo "=== XStream Java refs in $proj ==="
  grep -ri "xstream" "$proj" --include="*.java" -l 2>/dev/null | head -20
  echo "=== XStream XML refs in $proj ==="
  grep -ri "SccpAddressImpl#" "$proj" --include="*.xml" -l 2>/dev/null | head -20
  grep -ri "class=\"org.restcomm" "$proj" --include="*.xml" -l 2>/dev/null | head -20
done
