#!/bin/bash
set -e
cd /mnt/c/Users/Windows/Desktop/ethiopia-working-dir

git config user.email "dev@local"
git config user.name "Local Dev"

# Temporarily rename nested .git dirs so outer repo can track their contents
mv jSS7/.git jSS7/.git.bak
mv sctp/.git sctp/.git.bak

trap 'mv jSS7/.git.bak jSS7/.git 2>/dev/null || true; mv sctp/.git.bak sctp/.git 2>/dev/null || true' EXIT

# Remove submodule entries so we can track contents
git rm --cached jSS7 sctp 2>/dev/null || true

# Add ignore file
git add .gitignore

# Add top-level scripts and package
git add package-for-linux/
git add map-sms-mo-loadtest-linux.tar.gz
git add do_commit.sh run_sctp_test.sh run_multi_process_sctp_test.sh check_logs.sh compile_all.sh rebuild_mapload.sh 2>/dev/null || true

# Add jSS7 key files
git add jSS7/pom.xml
git add jSS7/map/load/mo_sms_build.xml
git add jSS7/map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java
git add jSS7/map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java
git add jSS7/map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/TestHarnessSmsMo.java
git add jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/AsImpl.java

# Add all jSS7 submodule poms (version bump)
find jSS7 -maxdepth 2 -name pom.xml | while read f; do
  git add "$f"
done

# Add sctp key files
git add sctp/pom.xml
git add sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.java
git add sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettyServerImpl.java
git add sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpClientChannelInitializer.java
git add sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpServerChannelInitializer.java

# Add all sctp submodule poms (version bump)
find sctp -maxdepth 2 -name pom.xml | while read f; do
  git add "$f"
done

git commit -m "Bump jSS7 to 9.2.8 and sctp to 2.0.13; implement multi-association SCTP load test with M3UA Loadshare, Netty tuning, FSM fix; add Linux test package"
echo "Commit successful"
