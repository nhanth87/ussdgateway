#!/bin/bash
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl
count=0
for f in target/surefire-reports/*.txt; do
  if grep -q 'FAILURE' "$f"; then
    count=$((count + 1))
  fi
done
echo "$count"
