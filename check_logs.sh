#!/bin/bash
for i in 0 1 2 3; do
  echo "=== Client $i ==="
  grep -n "Throughput" /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/client_multi_${i}.log
  grep -c "Dialog released" /mnt/c/Users/Windows/Desktop/ethiopia-working-dir/client_multi_${i}.log
done
