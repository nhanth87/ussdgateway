# jSS7 SccpListener Warning Fix - Session Summary

**Date:** 2026-04-14  
**Author:** Matrix Agent

---

## Problem Description

### Warning Message
```
Registering SccpListener=org.restcomm.protocols.ss7.tcap.TCAPProviderImpl@5ed731d0 
for already existing SccpListener=org.restcomm.protocols.ss7.tcap.TCAPProviderImpl@5ed731d0 for SSN=8
```

### Location
This warning appears in Docker logs for both server and client of the map-load test application.

---

## Root Cause Analysis

### Finding
The warning was a **false positive**. The same TCAPProviderImpl instance was being registered for the same SSN, which is normal behavior during stack restart cycles.

### Original Code (SccpProviderImpl.java)
```java
public void registerSccpListener(int ssn, SccpListener listener) {
    synchronized (this) {
        SccpListener existingListener = ssnToListener.get(ssn);
        if (existingListener != null) {  // <-- Too broad condition
            if (logger.isEnabledFor(Level.WARN)) {
                logger.warn(String.format("Registering SccpListener=%s for already existing SccpListener=%s for SSN=%d",
                        listener, existingListener, ssn));
            }
        }
        // ... registration logic
    }
}
```

### Problem
The condition `if (existingListener != null)` warns whenever ANY listener is already registered, even when it's the **same** listener being re-registered (which is normal during stack restart).

---

## Solution Applied

### Modified File
`C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\sccp\sccp-impl\src\main\java\org\restcomm\protocols\ss7\sccp\impl\SccpProviderImpl.java`

### Fix
Changed line 80 from:
```java
if (existingListener != null) {
```

To:
```java
if (existingListener != null && existingListener != listener) {
```

### Explanation
- **Warns** when a **DIFFERENT** listener tries to replace an existing one (potential issue)
- **Does NOT warn** when the **SAME** listener is re-registered (normal during stack restart)

---

## Additional Changes

### mo_sms_build.xml
Removed `depends="setup-deps"` from server and client targets to allow running independently:

```xml
<!-- Before -->
<target name="server" description="run the Load Test Server." depends="setup-deps">

<!-- After -->
<target name="server" description="run the Load Test Server.">
```

---

## Git Commit

### Commit ID
`d75aede`

### Branch
`master`

### Pushed to
`git@github.com:nhanth87/jss7.git`

### Commit Message
```
Fix SccpListener registration warning when same listener is re-registered

- Modified SccpProviderImpl.registerSccpListener() to only warn when
  a DIFFERENT listener replaces an existing one, not when the same
  listener is re-registered (normal behavior during stack restart)
- Removed depends=setup-deps from server/client targets in mo_sms_build.xml
  to allow running targets independently after initial setup
```

---

## Docker Build & Test

### Docker Setup
- **Location:** `C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\docker-compose.yml`
- **Dockerfile:** Builds Eclipse Temurin 17 JDK based image
- **Build Command:** `docker compose build --no-cache`
- **Run Command:** `docker compose up -d`

### JAR Dependencies Location
- **Local:** `C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\target\load\`
- **Docker:** `/opt/target/load/`

### Important JAR Names (build.xml expects these names)
| Actual Name | Expected Name |
|-------------|---------------|
| congestion.jar | restcomm-congestion.jar |
| restcomm-congestion-9.2.7.jar | restcomm-congestion.jar |

### Build Process
1. Run `ant -f mo_sms_build.xml setup-deps` to copy dependencies
2. Rebuild project modules (sccp-impl, etc.)
3. Copy updated JARs to `target/load/`
4. Rebuild Docker: `docker compose build --no-cache`

### Key JARs to Update After Code Changes
- `sccp-impl.jar` - Contains SccpProviderImpl fix
- `map-load.jar` - Contains load test application

---

## Verification

### Before Fix
```
[java] 831  [main] WARN  org.restcomm.protocols.ss7.sccp.impl.SccpProviderImpl  - 
Registering SccpListener=...TCAPProviderImpl@... for already existing SccpListener=...TCAPProviderImpl@... for SSN=8
```

### After Fix
No SccpListener warning appears in logs.

---

## Build Issues Encountered & Solutions

### Issue 1: congestion JAR naming mismatch
- **Problem:** `congestion.jar` vs `restcomm-congestion.jar`
- **Solution:** Rename or copy to match expected name

### Issue 2: ClassNotFoundException for ExecutorCongestionMonitor
- **Problem:** Wrong JAR name in classpath
- **Solution:** Ensure `restcomm-congestion.jar` exists with correct class

### Issue 3: OSGi Bundle Plugin failures
- **Problem:** Maven OSGi plugin throws ArrayIndexOutOfBoundsException
- **Solution:** Use `mvn clean compile resources:resources` then manually create JAR with `jar cf`

### Issue 4: NoClassDefFoundError for map API classes
- **Problem:** JARs not copied to target/load
- **Solution:** Run `ant -f mo_sms_build.xml setup-deps`

---

## Related Files

### Source Files
- `sccp/sccp-impl/src/main/java/org/restcomm/protocols/ss7/sccp/impl/SccpProviderImpl.java`
- `map/load/mo_sms_build.xml`

### Docker Files
- `map/load/docker-compose.yml`
- `map/load/Dockerfile`

### Test Application Files
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java`
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java`
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mt/Server.java`
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mt/Client.java`
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/ussd/Server.java`
- `map/load/src/main/java/org/restcomm/protocols/ss7/map/load/ussd/Client.java`

---

## Notes

### TCP Port Mappings
- Host port 8011 → Container port 8011 (SCTP)
- Host port 8012 → Container port 8012 (SCTP)

### Health Check
Docker containers use TCP health check on port 8011.

### Server Log Location
- In Docker: `/opt/jss7-load-test/server/log4j-server.log`
- Local log: `map/load/server/log4j-server.log`
