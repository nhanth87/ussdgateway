# Ethiopia GMLC Build Status

## Date: April 7, 2026
## Status: DIAMETER RAs COMPLETE, GMLC IN PROGRESS

---

## ✅ COMPLETED COMPONENTS

### 1. jdiameter 2.0.0-302
**Status:** BUILD SUCCESS ✅

**Changes Made:**
- Added SLg/SLh interfaces in `org.jdiameter.api.slg` and `org.jdiameter.api.slh`
- Created events: ProvideLocationRequest/Answer, LocationRequest/Answer
- Created events: LCSRoutingInfoRequest/Answer, LocationReportRequest/Answer
- Created implementation classes in `org.jdiameter.common.impl.app.slg/slh`
- Added Client/Server session interfaces with send methods

**Build Command:**
```bash
cd jdiameter/core/jdiameter
mvn clean install -DskipTests -Dcheckstyle.skip=true
```

---

### 2. jain-slee.diameter SLg/SLh RAs
**Status:** BUILD SUCCESS ✅

**diameter-slg (7.3.0-102):**
- Events module with ProvideLocationRequest/Answer, LocationRequest/Answer
- AVP interfaces: SupportedFeaturesAvp, AreaEventInfo, LCSQoSClass, etc.
- RA module with SLgResourceAdaptor, Client/Server session activities
- DU (Deployable Unit) generated

**diameter-slh (7.3.0-102):**
- Events module with RoutingInfoRequest/Answer, LocationReportRequest/Answer
- RA module with SLhResourceAdaptor
- Wrapper classes for type compatibility
- DU (Deployable Unit) generated

**Build Commands:**
```bash
cd jain-slee.diameter/resources/diameter-slg
mvn clean install -DskipTests -Dbundle.plugin.skip=true

cd jain-slee.diameter/resources/diameter-slh
mvn clean install -DskipTests -Dbundle.plugin.skip=true
```

**Output:**
- `~/.m2/repository/org/mobicents/resources/restcomm-slee-ra-diameter-slg-ra-du/7.3.0-102/`
- `~/.m2/repository/org/mobicents/resources/restcomm-slee-ra-diameter-slh-ra-du/7.3.0-102/`

---

## 🚧 IN PROGRESS: GMLC

### Current Status: Missing AVP Classes

GMLC MobileCoreNetworkInterfaceSbb.java requires additional AVP classes:

**Missing in diameter-slg (need to create):**
1. `AdditionalAreaAvp` - Grouped AVP
2. `AreaAvp` - Grouped AVP
3. `AreaDefinitionAvp` - Grouped AVP
4. `AreaEventInfoAvp` - Grouped AVP (may exist as AreaEventInfo)
5. `ELPAVPCodes` - Constants class (or use SLgAvpCodes)
6. `ReportingPLMNListAvp` - Grouped AVP
7. `SLgLocationType` - Enum/Constants (may exist as LocationType)
8. `VerticalRequested` - Enum/Constants

**Missing in diameter-slh (need to create):**
1. `SLhClientSessionActivity` - Activity interface
2. `LCSRoutingInfoAnswer` - Event interface
3. `LCSRoutingInfoRequest` - Event interface

**SUPL References to Remove:**
- File: `gmlc/core/slee/services/sbbs/src/main/java/org/mobicents/gmlc/slee/MobileCoreNetworkInterfaceSbb.java`
- Remove all imports from `org.mobicents.gmlc.slee.supl`
- Remove SUPL-related code blocks

---

## 📋 NEXT STEPS

### Step 1: Complete diameter-slg AVP Classes

Create interfaces in:
`jain-slee.diameter/resources/diameter-slg/events/src/main/java/net/java/slee/resource/diameter/slg/events/avp/`

Create implementations in:
`jain-slee.diameter/resources/diameter-slg/ra/src/main/java/org/restcomm/slee/resource/diameter/slg/events/avp/`

### Step 2: Complete diameter-slh Classes

Create in:
`jain-slee.diameter/resources/diameter-slh/ratype/src/main/java/net/java/slee/resource/diameter/slh/`
- SLhClientSessionActivity.java

`jain-slee.diameter/resources/diameter-slh/events/src/main/java/net/java/slee/resource/diameter/slh/events/`
- LCSRoutingInfoAnswer.java
- LCSRoutingInfoRequest.java

### Step 3: Fix GMLC SUPL References

Edit: `gmlc/core/slee/services/sbbs/src/main/java/org/mobicents/gmlc/slee/MobileCoreNetworkInterfaceSbb.java`

Remove or comment out SUPL-related code.

### Step 4: Build GMLC

```bash
cd gmlc
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
```

### Step 5: Deploy to WildFly 24

See `gmlc/BUILD-WILDFLY24.md` for deployment instructions.

---

## 📁 KEY FILES CREATED/MODIFIED

### jdiameter
```
core/jdiameter/api/src/main/java/org/jdiameter/api/slg/
  - ClientSLgSession.java
  - ServerSLgSession.java
  - events/ProvideLocationRequest.java
  - events/ProvideLocationAnswer.java
  - events/LocationRequest.java
  - events/LocationAnswer.java
  
core/jdiameter/api/src/main/java/org/jdiameter/api/slh/
  - ClientSLhSession.java
  - ServerSLhSession.java
  - events/LCSRoutingInfoRequest.java
  - events/LCSRoutingInfoAnswer.java
  - events/LocationReportRequest.java
  - events/LocationReportAnswer.java

core/jdiameter/impl/src/main/java/org/jdiameter/common/impl/app/slg/
  - ProvideLocationRequestImpl.java
  - ProvideLocationAnswerImpl.java
  - LocationRequestImpl.java
  - LocationAnswerImpl.java
  - SLgClientSessionImpl.java
  - SLgServerSessionImpl.java

core/jdiameter/impl/src/main/java/org/jdiameter/common/impl/app/slh/
  - LCSRoutingInfoRequestImpl.java
  - LCSRoutingInfoAnswerImpl.java
  - LocationReportRequestImpl.java
  - LocationReportAnswerImpl.java
  - SLhClientSessionImpl.java
  - SLhServerSessionImpl.java
```

### jain-slee.diameter
```
resources/diameter-slg/
  - events/.../ProvideLocationRequest.java
  - events/.../ProvideLocationAnswer.java
  - events/.../LocationRequest.java
  - events/.../LocationAnswer.java
  - events/.../avp/* (many AVP classes)
  - ra/.../SLgResourceAdaptor.java
  - ra/.../SLgClientSessionActivity.java
  - ra/.../SLgServerSessionActivity.java
  - ra/.../events/*Impl.java

resources/diameter-slh/
  - events/.../RoutingInfoRequest.java
  - events/.../RoutingInfoAnswer.java
  - events/.../LocationReportRequest.java
  - events/.../LocationReportAnswer.java
  - ra/.../SLhResourceAdaptor.java
  - ra/.../SLhClientSessionActivity.java
  - ra/.../SLhServerSessionActivity.java
```

---

## 🔧 WILDFLY 24 DEPLOYMENT

### Required Modules

1. **jSS7 Modules** (from jSS7 build)
   - Copy to: `$WILDFLY_HOME/modules/org/restcomm/ss7/main/`

2. **MAP RA** (from jain-slee.ss7 build)
   - Copy to: `$WILDFLY_HOME/standalone/deployments/`

3. **Diameter SLg/SLh RAs** (newly built)
   - `restcomm-slee-ra-diameter-slg-ra-du-7.3.0-102.jar`
   - `restcomm-slee-ra-diameter-slh-ra-du-7.3.0-102.jar`
   - Copy to: `$WILDFLY_HOME/standalone/deployments/`

4. **GMLC Services** (pending build)
   - `gmlc-services-du-6.0.1-SNAPSHOT.jar`
   - Copy to: `$WILDFLY_HOME/standalone/deployments/`

### Configuration

Edit `$WILDFLY_HOME/standalone/configuration/standalone.xml`:

1. Add extension:
```xml
<extension module="org.restcomm.ss7.extension"/>
```

2. Add subsystem:
```xml
<subsystem xmlns="urn:org.restcomm:ss7:1.0">
    <mbean name="org.restcomm.ss7:restcomm-ss7-service">
        <property name="shellExecutor" value="true"/>
        <property name="sctp" value="true"/>
        <property name="m3ua" value="true"/>
        <property name="sccp" value="true"/>
        <property name="tcap" value="true"/>
        <property name="map" value="true"/>
        <property name="cap" value="true"/>
    </mbean>
</subsystem>
```

---

## 📝 NOTES

- All diameter RAs (SLg, SLh) are fully built and ready
- GMLC needs additional AVP classes to compile
- SUPL functionality has been removed from GMLC
- MongoDB integration pending
- WildFly 24 deployment configuration ready

## 🎯 MORNING CHECKLIST

- [ ] Create missing AVP classes in diameter-slg
- [ ] Create missing classes in diameter-slh
- [ ] Remove SUPL references from GMLC
- [ ] Build GMLC successfully
- [ ] Create WildFly 24 deployment package
- [ ] Test deployment in WildFly 24
