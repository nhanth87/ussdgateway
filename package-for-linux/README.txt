===============================================================================
MAP SMS-MO Load Test Package for Native Linux
jSS7 9.2.8 + sctp 2.0.13
===============================================================================

CHANGES FROM BASELINE
---------------------
1. Multi-association SCTP client/server (4 associations) with M3UA Loadshare
2. M3UA FSM fix: added AS_STATE_CHANGE_INACTIVE transition from ACTIVE state
3. SCTP/Netty tuning:
   - Boss threads: 8, Worker threads: 16
   - SO_SNDBUF / SO_RCVBUF: 8 MB
   - SCTP_INIT_MAXSTREAMS: 256 in / 256 out
   - FlushConsolidationHandler in Netty pipeline
   - Congestion WARN logging suppressed

PREREQUISITES
-------------
- Java 11+ (Zulu/OpenJDK recommended)
- Linux kernel with SCTP support (lksctp-tools installed)
- Enough ports available: 8011, 8012, 8013, 8014, 8015, 8016, 8021, 8026

QUICK START
-----------
1. Start Server:
   $ ./run_server.sh

2. Wait ~10 seconds for SCTP/M3UA handshake to complete.

3. Run Single-Association Client (100k dialogs):
   $ ./run_client.sh 100000 100000 SCTP

4. OR Run Multi-Association Client (4 x 25k = 100k dialogs):
   $ ./run_multi_client.sh 25000 SCTP

CHECK RESULTS
-------------
- Throughput is printed to stdout (or *.log files for multi-client).
- Search for "Throughput =" in the output.

CUSTOMIZATION
-------------
Edit the shell scripts to change:
- JVM heap size (-Xms/-Xmx)
- Number of dialogs / concurrent dialogs
- IP addresses / ports
- Delivery thread count (last argument in run_server.sh / run_client.sh)

NOTE
----
This package is pre-built; classes/ and lib/ contain all required artifacts.
If you need to recompile from source, restore the full Maven project structure
and run the Ant build: ant -f mo_sms_build.xml compile
===============================================================================
