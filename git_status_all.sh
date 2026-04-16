#!/bin/bash
for dir in jSS7 sctp jdiameter jainslee-jss7 jainslee.diameter jain-slee.ss7 jain-slee.diameter; do
  path="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/$dir"
  if [ -d "$path/.git" ]; then
    echo "=== $dir ==="
    git -C "$path" status --short
  fi
done
