# jSS7 MAP Load Test - Test Report

**Date:** 2026-04-13  
**Tester:** Jenny  
**Status:** ⚠️ BLOCKED - SCTP Compatibility Issue

---

## 1. Build Status

### ✅ Map-Load Module Compiled Successfully
```
[INFO] BUILD SUCCESS
[INFO] Total time: 18.607 s
[INFO] Artifact: map-load-9.2.7.jar
```

### ✅ Dependencies Ready (30 JARs)
- sctp-api.jar, sctp-impl.jar
- m3ua-api.jar, m3ua-impl.jar  
- map-api.jar, map-impl.jar
- sccp-api.jar, sccp-impl.jar
- tcap-api.jar, tcap-impl.jar
- netty-all.jar, jctools-core.jar
- All other SS7 stack dependencies

### ✅ Docker Images Built
| Image | Size | Status |
|-------|------|--------|
| jss7-map-load-test:latest | ~600MB | ✅ Ready (Eclipse Temurin + Ant) |
| jss7-sctp:latest | ~650MB | ✅ Ready (+ lksctp-tools, libsctp1) |

---

## 2. Test Execution Attempts

### Attempt 1: TCP Mode with Original Image
**Command:**
```bash
docker run --rm --privileged -e TEST_SERVER_CHANNEL_TYPE=tcp jss7-map-load-test:latest server
```

**Result:** ❌ FAILED
```
Exception in thread "main" java.lang.NoSuchFieldError: SCTP_NODELAY
    at org.mobicents.protocols.sctp.netty.NettyServerImpl.applySctpOptions
```

### Attempt 2: SCTP Mode with Ubuntu + lksctp-tools
**Command:**
```bash
docker run --rm --privileged -e TEST_SERVER_CHANNEL_TYPE=sctp jss7-sctp:latest server
```

**Result:** ❌ FAILED
```
Exception in thread "main" java.lang.NoSuchFieldError: SCTP_NODELAY
    at org.mobicents.protocols.sctp.netty.NettyServerImpl.applySctpOptions
```

### Attempt 3: Docker Compose with TCP Mode
**Command:**
```bash
docker compose -f docker-compose-test.yml up
```

**Result:** ❌ FAILED
- Server container exited with same error
- Client container stopped (exit code 137)

---

## 3. Root Cause Analysis

### 🔴 Primary Issue: `NoSuchFieldError: SCTP_NODELAY`

**Location:** `NettyServerImpl.java:389`

**Cause:**
The Netty SCTP implementation tries to access `SctpStandardSocketOptions.SCTP_NODELAY` at runtime, which requires:
1. **Native SCTP library** (libsctp) - ✅ Installed in container
2. **SCTP Kernel Module** - ❌ Not available in Docker Desktop Windows
3. **JVM SCTP Support** - ❌ JVM cannot load SCTP constants without kernel support

**Code Flow:**
```
Server.main()
  → initSCTP()
    → NettySctpManagementImpl.start()
      → NettyServerImpl.start()
        → initSocket()
          → applySctpOptions() ← FAILS HERE
            → SctpStandardSocketOptions.SCTP_NODELAY ← Not available
```

### 🔴 Secondary Issue: TCP Mode Still Loads SCTP Classes

Even when `channelType=tcp` is specified, the code path still goes through `NettyServerImpl` which tries to apply SCTP socket options. This is a **bug in the SCTP library** - it should not attempt to load SCTP constants when using TCP mode.

---

## 4. Environment Details

### Docker Desktop (Windows)
```
OS: Windows (WSL2 backend)
Docker: Desktop Linux Engine
SCTP Kernel Module: ❌ Not available
Privileged Mode: ✅ Enabled
lksctp-tools: ✅ Installed
libsctp1: ✅ Installed
```

### Container Configuration
```dockerfile
FROM eclipse-temurin:17-jdk
RUN apt-get install -y lksctp-tools libsctp1 tcpdump
```

---

## 5. Workarounds Attempted

| Workaround | Status | Result |
|------------|--------|--------|
| Install lksctp-tools | ✅ Done | Still fails - kernel module missing |
| Install libsctp1 | ✅ Done | Still fails - JVM needs kernel support |
| Use --privileged | ✅ Done | Cannot overcome missing kernel module |
| TCP channel type | ✅ Done | Code still tries to load SCTP classes |
| Ubuntu 22.04 base | ✅ Done | Same issue - missing Windows SCTP driver |

---

## 6. Recommended Solutions

### Option 1: Run on Native Linux (Recommended)
```bash
# On Ubuntu/CentOS with SCTP support
sudo modprobe sctp
sudo sysctl -w net.sctp.sctp_mem="4096 8192 12288"
./run-test-docker.sh
```

### Option 2: Fix SCTP Library
Modify `NettyServerImpl.applySctpOptions()` to check channel type before applying SCTP options:
```java
if (channelType == ChannelType.SCTP) {
    // Apply SCTP-specific options
    socketChannel.config().setOption(SctpStandardSocketOptions.SCTP_NODELAY, true);
}
```

### Option 3: Use Alternative SCTP Implementation
Use TCP-only transport for testing purposes (requires code changes in SCTP library).

### Option 4: WSL2 with Custom Kernel
Build custom WSL2 kernel with SCTP support:
```bash
# Build kernel with CONFIG_IP_SCTP=y
# Replace WSL2 kernel
# Run Docker with custom kernel
```

---

## 7. Files Created

```
jSS7/map/load/
├── Dockerfile.sctp          ✅ SCTP-enabled image
├── Dockerfile.minimal       ✅ Minimal Ubuntu image
├── docker-compose-test.yml  ✅ Test orchestration
├── docker-compose-sctp.yml  ✅ Full SCTP setup
├── run-docker-test.ps1      ✅ PowerShell test script
├── run-test-tcp.sh          ✅ Bash test script
└── TEST-REPORT.md           ✅ This report
```

---

## 8. Conclusion

**Status:** ⚠️ Cannot run on Docker Desktop Windows

**Reason:** Docker Desktop on Windows uses WSL2 which does not have SCTP kernel module support. The jSS7 MAP Load Test requires SCTP kernel module at runtime, even when configured for TCP mode (due to Netty SCTP implementation loading SCTP classes eagerly).

**Next Steps:**
1. Run on native Linux host with `sudo modprobe sctp`
2. Fix SCTP library to support TCP-only mode without loading SCTP classes
3. Use cloud-based Linux VM with SCTP support

---

## 9. PCAP Status

**PCAP File:** ❌ Not generated  
**Reason:** Test could not start due to SCTP compatibility issue

**Expected PCAP Contents (when working):**
- SCTP INIT/INIT_ACK chunks
- M3UA ASP_UP/ASP_ACTIVE messages
- SCCP UDT messages
- TCAP BEGIN/CONTINUE/END
- MAP MO-SMS operations

---

*Report generated by Jenny - AI Assistant*
