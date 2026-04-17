# jSS7 WildFly 24 Deployment Guide

## Prerequisites

- WildFly 24.0.1.Final installed
- jSS7 built successfully (version 9.0.0-318)
- Java 11 or higher

## Build jSS7 for WildFly 24

```bash
cd jSS7
mvn clean install -DskipTests
```

## Deploy to WildFly 24

### Option 1: Using PowerShell Script

```powershell
# Set WildFly home environment variable (or pass as parameter)
$env:WILDFLY_HOME = "C:\path\to\wildfly-24.0.1.Final"

# Deploy jSS7 modules
.\deploy-wildfly.ps1

# Or specify WildFly home directly
.\deploy-wildfly.ps1 -WildFlyHome "C:\path\to\wildfly-24.0.1.Final"

# List deployed modules
.\deploy-wildfly.ps1 -List

# Undeploy
.\deploy-wildfly.ps1 -Undeploy
```

### Option 2: Using Ant (if Ant is installed)

```bash
# Set environment variable
set WILDFLY_HOME=C:\path\to\wildfly-24.0.1.Final

# Deploy
ant deploy

# Or specific targets
ant deploy-modules
ant undeploy
ant list-modules
```

### Option 3: Manual Deployment

1. Create module directories:
```
$WILDFLY_HOME/modules/org/restcomm/ss7/main/
$WILDFLY_HOME/modules/org/restcomm/ss7/extension/main/
$WILDFLY_HOME/modules/org/restcomm/ss7/commons/main/
```

2. Copy JAR files:
- From `service/wildfly/modules/target/module/main/` → `org/restcomm/ss7/main/`
- From `service/wildfly/extension/target/module/main/` → `org/restcomm/ss7/extension/main/`
- From `service/wildfly/restcomm-ss7-wildfly-commons/target/module/main/` → `org/restcomm/ss7/commons/main/`

3. Copy module.xml files to respective directories.

## Configure WildFly

Edit `$WILDFLY_HOME/standalone/configuration/standalone.xml`:

### 1. Add Extension

```xml
<extensions>
    ...
    <extension module="org.restcomm.ss7.extension"/>
</extensions>
```

### 2. Add Subsystem

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

### 3. Add Socket Binding (if needed)

```xml
<socket-binding-group name="standard-sockets" ...>
    ...
    <socket-binding name="ss7-sctp" port="2905"/>
    <socket-binding name="ss7-shell" port="3435"/>
</socket-binding-group>
```

## Start WildFly

```bash
$WILDFLY_HOME/bin/standalone.bat
```

## Verify Deployment

Check logs for successful SS7 service startup:
```
INFO  [org.restcomm.ss7.service.SS7ExtensionService] (MSC service thread 1-2) jSS7 Extension Service started
```

## Module Structure

```
$WILDFLY_HOME/modules/
└── org/restcomm/ss7/
    ├── main/                          # Core SS7 modules
    │   ├── module.xml
    │   ├── scheduler-9.0.0-318.jar
    │   ├── sctp-api-2.0.2-17.jar
    │   ├── sctp-impl-2.0.2-17.jar
    │   ├── m3ua-api-9.0.0-318.jar
    │   ├── m3ua-impl-9.0.0-318.jar
    │   ├── sccp-api-9.0.0-318.jar
    │   ├── sccp-impl-9.0.0-318.jar
    │   ├── tcap-api-9.0.0-318.jar
    │   ├── tcap-impl-9.0.0-318.jar
    │   ├── map-api-9.0.0-318.jar
    │   ├── map-impl-9.0.0-318.jar
    │   ├── cap-api-9.0.0-318.jar
    │   ├── cap-impl-9.0.0-318.jar
    │   └── ...
    ├── extension/main/                # WildFly extension
    │   ├── module.xml
    │   └── restcomm-ss7-wildfly-extension-9.0.0-318.jar
    └── commons/main/                  # Common utilities
        ├── module.xml
        └── restcomm-ss7-wildfly-commons-9.0.0-318.jar
```

## Troubleshooting

### ClassNotFoundException
- Verify all JARs are copied correctly
- Check module.xml for correct resource paths

### Port Conflicts
- Change socket binding ports in standalone.xml

### Memory Issues
- Increase heap size: `-Xmx2g -Xms1g`

## Version Information

- jSS7 Version: 9.0.0-318
- WildFly Version: 24.0.1.Final
- Java Version: 11
- SCTP Version: 2.0.2-17
