#!/bin/bash
set -e
for dir in jSS7 sctp jdiameter jain-slee.ss7 jain-slee.diameter; do
  path="/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/$dir"
  if [ -d "$path/.git" ]; then
    echo "=== Committing $dir ==="
    git -C "$path" add -A
    if ! git -C "$path" diff --cached --quiet; then
      git -C "$path" commit -m "fix: replace netty-all with individual modules, cleanup XStream remnants, add netty-codec-base dependencies"
      echo "Committed $dir"
    else
      echo "No changes to commit in $dir"
    fi
  fi
done
