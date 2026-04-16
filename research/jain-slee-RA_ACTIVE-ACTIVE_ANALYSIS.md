# jain-slee.RA Active-Active Clustering Analysis Report

**Generated:** January 2025  
**Project:** jain-slee.diameter / jain-slee.ss7 (JAIN SLEE Resource Adaptors)  
**Analysis Scope:** High Availability (HA) and Active-Active Clustering Capabilities

---

## Executive Summary

This report analyzes the feasibility of implementing **active-active clustering** for jain-slee Resource Adaptors (RAs) in the RestComm/Mobicents ecosystem. The analysis covers:

1. **Current HA Infrastructure** - Existing jdiameter-ha module
2. **Session Replication Mechanisms** - How Diameter sessions are replicated
3. **Active-Active Requirements** - What's needed for true active-active mode
4. **Implementation Strategy** - Recommendations for achieving active-active clustering

### Key Findings

| Component | Status | Notes |
|-----------|--------|-------|
| jdiameter-ha module | ✅ Exists | Session replication via MobicentsCache |
| SessionDatasource | ✅ Implemented | ReplicatedSessionDatasource with cluster support |
| Timer Replication | ✅ Implemented | ReplicatedTimerFacilityImpl |
| Load Balancing | ⚠️ Partial | SCTP multi-homing support |
| Client Connection Pool | ⚠️ Needs work | Multiple peers not fully utilized |

---

## 1. Current HA Architecture

### 1.1 jdiameter-ha Module Structure

```
jdiameter-ha/
├── api/
│   └── ISessionClusteredData.java       # Marker interface
└── impl/
    ├── data/
    │   └── ReplicatedSessionDatasource.java  # Core replication logic
    ├── client/
    │   ├── acc/
    │   ├── auth/
    │   ├── cca/
    │   ├── cxdx/
    │   ├── gx/
    │   ├── rf/
    │   ├── ro/
    │   ├── rx/
    │   ├── s13/
    │   ├── sh/
    │   ├── slg/
    │   └── slh/
    │       └── *SessionDataReplicatedImpl.java  # Per-app replicated data
    ├── common/
    │   └── *ReplicatedSessionDataFactory.java  # Factory pattern
    └── timer/
        └── ReplicatedTimerFacilityImpl.java    # Timer state replication
```

### 1.2 Replication Mechanism

The HA implementation uses **MobicentsCache** (based on Infinispan) for distributed caching:

```java
// From ReplicatedSessionDatasource.java
public ReplicatedSessionDatasource(IContainer container, ISessionDatasource localDataSource, String cacheConfigFilename) {
    // Create distributed cache
    MobicentsCache mcCache = new MobicentsCache(cacheConfigFilename);
    TransactionManager txMgr = mcCache.getTxManager();
    
    // Create cluster with election
    this.mobicentsCluster = new DefaultMobicentsCluster(
        mcCache, 
        txMgr, 
        new DefaultClusterElector()
    );
    
    // Register data removal listener
    this.mobicentsCluster.addDataRemovalListener(this);
    this.mobicentsCluster.startCluster();
}
```

### 1.3 Session State Management

**Key Classes:**
- `AppSessionDataReplicatedImpl` - Base class for replicated session data
- `ReplicatedSessionDatasource` - Manages session lifecycle across cluster nodes

**Session Flow:**
1. Session created on Node A
2. Session state replicated to cluster cache
3. Node B can access session from cache
4. On Node A failure, Node B takes over

---

## 2. Active-Active Clustering Requirements

### 2.1 What is Active-Active?

| Mode | Description | Node A | Node B |
|------|-------------|--------|--------|
| **Active-Passive** | One node active, one standby | Active | Passive (standby) |
| **Active-Active** | Both nodes active, shared load | Active | Active |

### 2.2 Requirements for Active-Active

#### 2.2.1 Session State Replication ✅
- [x] Session data replicated via MobicentsCache
- [x]Application session factories registered
- [x] Timer state replication via ReplicatedTimerFacilityImpl

#### 2.2.2 Connection Distribution ⚠️
- [ ] SCTP multi-homing (already implemented in jdiameter)
- [ ] Peer table replication across nodes
- [ ] Request routing based on session affinity

#### 2.2.3 Client Connection Pooling ⚠️
- [ ] Multiple peer connections per node
- [ ] Connection failover without message loss
- [ ] Reconnection strategies

#### 2.2.4 Message Ordering & Deduplication ⚠️
- [ ] CE (Client Election) for multi-node environments
- [ ] Message deduplication for retransmissions
- [ ] Idempotency handling

#### 2.2.5 Load Balancing ✅
- [x] DNS-based load balancing support
- [x] Round-robin connection distribution
- [x] Session affinity by Destination-Host AVP

---

## 3. Implementation Analysis

### 3.1 Current Active-Active Capabilities

#### ✅ Already Working

**1. Session Replication**
```java
// Sessions stored in distributed cache
public static final String SESSIONS = "/diameter/appsessions";
public static final FqnWrapper SESSIONS_FQN = FqnWrapper.fromStringWrapper(SESSIONS);

// Session lookup - checks local first, then replicated
public BaseSession getSession(String sessionId) {
    if (this.localDataSource.exists(sessionId)) {
        return this.localDataSource.getSession(sessionId);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
        this.makeLocal(sessionId);
        return this.localDataSource.getSession(sessionId);
    }
    return null;
}
```

**2. Session Factory Registration**
```java
appSessionDataFactories.put(IAuthSessionData.class, new AuthReplicatedSessionDataFactory(this));
appSessionDataFactories.put(IAccSessionData.class, new AccReplicatedSessionDataFactory(this));
appSessionDataFactories.put(ICCASessionData.class, new CCAReplicatedSessionDataFactory(this));
// ... all application sessions
```

**3. Timer Replication**
- `DiameterTimerTaskData` - Stores timer state
- `ReplicatedTimerFacilityImpl` - Distributes timer events

### 3.2 Gaps for True Active-Active

#### Gap 1: Client Election (CE) ⚠️
**Issue:** When multiple nodes receive the same request, only one should process it.

**Current:** No CE mechanism implemented

**Recommendation:** Implement CE based on session ID hash:
```java
public class ClusterElection {
    private final DefaultClusterElector elector;
    
    public boolean isElectedForSession(String sessionId) {
        // Hash-based election for session ownership
        int hash = sessionId.hashCode();
        String nodeId = mobicentsCluster.getClusterNodeId();
        return (hash % clusterSize) == getNodeIndex(nodeId);
    }
}
```

#### Gap 2: Peer Connection Sharing ⚠️
**Issue:** Each node maintains its own peer connections.

**Current:** `PeerTableImpl` is local to each node.

**Recommendation:** Implement distributed peer table:
```java
// DistributedPeerTable.java
public class DistributedPeerTable {
    private ConcurrentHashMap<String, PeerDescriptor> peerConnections;
    
    public Peer getPeer(URI peerUri) {
        return peerConnections.computeIfAbsent(
            peerUri.toString(),
            uri -> createOrGetPeerFromCluster(uri)
        );
    }
}
```

#### Gap 3: Request-Response Routing ⚠️
**Issue:** Responses may return to different node than requestor.

**Current:** Responses routed based on Hop-by-Hop ID

**Recommendation:** Implement session-aware routing:
```java
public class SessionAwareRouter {
    private ConcurrentHashMap<String, String> sessionNodeMap;
    
    public Route route(Response response) {
        String sessionId = extractSessionId(response);
        String targetNode = sessionNodeMap.get(sessionId);
        return new Route(targetNode, response);
    }
}
```

---

## 4. Implementation Strategy

### Phase 1: Foundation (Low Risk)

| Task | Effort | Risk | Description |
|------|--------|------|-------------|
| Enable cluster by default | Low | Low | Set `isClustered=true` in configuration |
| Add session ownership tracking | Medium | Medium | Track which node owns each session |
| Implement CE for new sessions | Medium | Medium | Election on session creation |

### Phase 2: Connection Management (Medium Risk)

| Task | Effort | Risk | Description |
|------|--------|------|-------------|
| Distribute peer connections | High | High | Share peer connections across nodes |
| Implement connection failover | High | High | Seamless failover without message loss |
| Add peer health monitoring | Medium | Medium | Detect and react to peer failures |

### Phase 3: Message Routing (High Risk)

| Task | Effort | Risk | Description |
|------|--------|------|-------------|
| Session-aware response routing | High | High | Route responses to correct node |
| Message deduplication | Medium | Medium | Handle retransmissions correctly |
| Idempotency verification | Medium | Medium | Ensure safe message replay |

---

## 5. Configuration for Active-Active

### 5.1 jdiameter-cache.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<infinispan>
    <cache-container name="diameter-ha">
        <!-- Distributed cache for session replication -->
        <distributed-cache name="sessions" 
            owners="2"
            segments="256"
            l1-lifespan="60000">
            <locking isolation="REPEATABLE_READ"/>
            <transaction mode="FULL_XA"/>
        </distributed-cache>
        
        <!-- Replicated cache for peer table -->
        <replicated-cache name="peer-table">
            <locking isolation="READ_COMMITTED"/>
        </replicated-cache>
    </cache-container>
</infinispan>
```

### 5.2 Resource Adaptor Configuration
```xml
<ra>
    <property name="ClusterMode" value="true"/>
    <property name="ActiveActiveEnabled" value="true"/>
    <property name="SessionReplicationEnabled" value="true"/>
    <property name="TimerReplicationEnabled" value="true"/>
    <property name="ClientElectionStrategy" value="hash-based"/>
</ra>
```

---

## 6. Testing Recommendations

### 6.1 Unit Tests
1. Session replication under concurrent load
2. Timer state synchronization
3. Peer table distributed operations

### 6.2 Integration Tests
1. Two-node cluster session failover
2. Active-active load distribution
3. Connection failover and recovery

### 6.3 Performance Tests
1. Session replication latency
2. Cache synchronization overhead
3. Election performance at scale

---

## 7. Conclusion

### 7.1 Current State
The jdiameter-ha module provides **solid foundation** for active-active clustering:
- ✅ Session state replication
- ✅ Timer state replication  
- ✅ Factory pattern for extensible session types

### 7.2 Gaps
- ⚠️ Client Election (CE) for multi-node request handling
- ⚠️ Distributed peer connection management
- ⚠️ Session-aware message routing

### 7.3 Recommendations

1. **Short-term:** Enable HA mode by default, test session failover
2. **Medium-term:** Implement Client Election for active-active
3. **Long-term:** Full distributed peer management and routing

### 7.4 Success Criteria

| Metric | Target |
|--------|--------|
| Session Failover Time | < 500ms |
| Message Loss on Failover | 0% |
| Active-Active Load Distribution | ±10% |
| Cluster Recovery Time | < 5s |

---

*Report generated by Matrix Agent*  
*Analysis based on: jdiameter-ha source code (v2.0.0-303)*
