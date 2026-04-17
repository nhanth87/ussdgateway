# jSS7 MAP Load Test - Status Report

## Date: 2026-04-12

---

## ✅ Completed Tasks

### 1. Docker Image Build
```
Repository: jss7-map-load-test:latest
Size: 591MB
Status: ✅ SUCCESS
```

### 2. Dependencies Ready
- **31 JAR files** in `target/load/`
- All required dependencies present:
  - sctp-api.jar, sctp-impl.jar
  - m3ua-api.jar, m3ua-impl.jar
  - map-api.jar, map-impl.jar
  - netty-all.jar, jctools-core.jar
  - All other SS7 stack JARs

### 3. Docker Scripts Created
| Script | Purpose |
|--------|---------|
| `Dockerfile` | Multi-stage build with Alpine + Ant + tcpdump |
| `docker-entrypoint.sh` | Entrypoint for server/client commands |
| `run-test-docker.sh` | Bash script to run 1000 TPS test with pcap |
| `run-test-with-pcap.ps1` | PowerShell version for Windows |
| `run-direct-test.ps1` | Direct Java execution (no Docker) |

---

## ❌ Current Blocker: Java 17 + SCTP Compatibility

### Error Details:
```
Exception in thread "main" java.lang.NoSuchFieldError: SCTP_NODELAY
    at org.mobicents.protocols.sctp.netty.NettyServerImpl.applySctpOptions
```

### Root Causes:
1. **SCTP Kernel Module**: Alpine Linux container lacks SCTP kernel support
2. **Java 17 Module System**: Reflection access restrictions for XStream serialization
3. **SCTP Options**: Code tries to apply SCTP socket options even with TCP channel type

### Attempted Fixes:
- ✅ Added `--add-opens` JVM flags for Java 17
- ✅ Tried `--privileged` container mode
- ✅ Switched between TCP and SCTP channel types
- ⚠️ SCTP kernel module not available in Docker Desktop Windows

---

## 🔧 Workarounds Available

### Option 1: Run on Linux Host (Recommended)
```bash
# Native Linux with SCTP kernel module
sudo modprobe sctp
./run-test-docker.sh 1000 60
```

### Option 2: Use Direct Java Execution
```bash
# Run directly without Docker
cd jss7/map/load
mvn install -Passemble -DskipTests
./run-direct-test.ps1 -TPS 1000 -Duration 60
```

### Option 3: Use TCP Only (Limited)
Modify source code to skip SCTP option application when using TCP channel.

---

## 📊 Expected Test Output (When Working)

### PCAP File:
- Location: `pcap-output/jss7-test-<timestamp>.pcap`
- Size: ~10-50 MB (depending on test duration)
- Contains: SCTP/M3UA/SCCP/TCAP/MAP protocols

### Log Files:
- `pcap-output/server-<timestamp>.log`
- `pcap-output/client-<timestamp>.log`

### Performance Metrics:
- Target TPS: 1000
- Expected Actual TPS: 900-1100 (±10%)
- Concurrent Dialogs: 2000

---

## 🚀 Next Steps

1. **For Development Testing**: Use Option 2 (Direct Java)
2. **For Production**: Use Option 1 (Linux host with SCTP)
3. **For CI/CD**: Fix SCTP library to handle TCP mode without SCTP options

---

## 📁 Files Modified

```
jss7/map/load/
├── Dockerfile                    ✅ Multi-stage build
├── docker-entrypoint.sh          ✅ Fixed LF line endings
├── mo_sms_build.xml              ✅ Java 17 flags added
├── run-test-docker.sh            ✅ Test script with pcap
├── run-test-with-pcap.ps1        ✅ PowerShell version
├── run-direct-test.ps1           ✅ Direct execution
├── docker-compose.yml            ✅ Orchestration
├── DOCKER-README.md              ✅ Documentation
└── TEST-STATUS.md               ✅ This file
```

---

## 📝 Git Commits

```
ae19f99 Add Docker test scripts with pcap capture support
1230d1a Fix ant build files for load test app
c16f326 Update SCTP version from 2.0.8 to 2.0.11
```

---

## 🎯 Summary

- **Docker Setup**: ✅ Complete
- **Dependencies**: ✅ All present
- **Scripts**: ✅ Ready
- **SCTP/Java17 Issue**: ⚠️ Blocker for containerized run
- **Workaround**: Use direct Java execution or Linux host

**Recommendation**: For immediate testing, use `run-direct-test.ps1` on Windows or run on native Linux with SCTP kernel module.
