# GMLC SBB Refactoring Plan

> **Created:** April 7, 2026  
> **Status:** Draft  
> **Priority:** High  
> **Estimated Effort:** 4-6 sprints

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Current State Analysis](#2-current-state-analysis)
3. [Problems Identified](#3-problems-identified)
4. [Refactoring Goals](#4-refactoring-goals)
5. [Target Architecture](#5-target-architecture)
6. [Phase-by-Phase Implementation](#6-phase-by-phase-implementation)
7. [Detailed Refactoring Steps](#7-detailed-refactoring-steps)
8. [Risk Mitigation](#8-risk-mitigation)
9. [Success Metrics](#9-success-metrics)
10. [Appendix: Code Metrics](#appendix-code-metrics)

---

## 1. Executive Summary

The GMLC (Gateway Mobile Location Centre) SLEE application contains severe architectural issues that impact maintainability, testability, and performance. The primary SBB `MobileCoreNetworkInterfaceSbb` has grown to **11,889 lines** and violates multiple software engineering principles.

### Key Findings

| Metric | Current | Target | Status |
|--------|---------|--------|--------|
| Main SBB Lines | 11,889 | < 500 | [CRITICAL] |
| CDR SBB Lines | 3,906 | < 500 | [WARNING] |
| Event Handlers | 53 | < 10 per SBB | [CRITICAL] |
| If Statements | 2,273 | < 200 per SBB | [CRITICAL] |
| Imports | 364 | < 50 | [CRITICAL] |

---

## 2. Current State Analysis

### 2.1 SBB Inventory

```
+------------------------------------------------------------------+
|                        GMLC SBB Structure                        |
+------------------------------------------------------------------+
|                                                                  |
|  [CRITICAL] MobileCoreNetworkInterfaceSbb.java                   |
|     Lines: 11,889 | Size: 835 KB | Type: God Class               |
|     Protocols: SS7 MAP, Diameter SLg, SLh, Sh, HTTP              |
|     Handlers: 53 | If Statements: 2,273                          |
|                                                                  |
|     +-- SS7 MAP Handler (~2,000 lines)                           |
|     |   * SendRoutingInfoForLCS                                  |
|     |   * ProvideSubscriberLocation                              |
|     |   * SubscriberLocationReport                               |
|     |   * AnyTimeInterrogation                                   |
|     |   * ProvideSubscriberInfo                                  |
|     |   * SendRoutingInfoForSM                                   |
|     |                                                            |
|     +-- Diameter SLg Handler (~1,500 lines)                      |
|     |   * ProvideLocationRequest/Answer                          |
|     |   * LocationReportRequest/Answer                           |
|     |                                                            |
|     +-- Diameter SLh Handler (~1,000 lines)                      |
|     |   * RoutingInfoRequest/Answer                              |
|     |                                                            |
|     +-- Diameter Sh Handler (~800 lines)                         |
|     |   * UserDataRequest/Answer                                 |
|     |                                                            |
|     +-- HTTP Handler (~500 lines)                                |
|     |   * MLP Request/Response                                   |
|     |                                                            |
|     +-- CDR Logic (~1,000 lines)                                 |
|                                                                  |
+------------------------------------------------------------------+
|                                                                  |
|  [WARNING] CDRGeneratorSbb.java                                  |
|     Lines: 3,906 | Size: 205 KB | Type: Child SBB                |
|     Issue: Extends God Class, contains ~3,490 line method        |
|                                                                  |
+------------------------------------------------------------------+
|                                                                  |
|  [OK] GMLCBaseSbb.java                                           |
|     Lines: 200 | Size: 10 KB | Type: Base Class                  |
|     Status: Acceptable                                           |
|                                                                  |
|  [OK] CDRSbb.java                                                |
|     Lines: 145 | Size: 5.7 KB | Type: SBB                        |
|     Status: Acceptable                                           |
|                                                                  |
|  [OK] Interface files (< 10 lines each)                          |
|                                                                  |
+------------------------------------------------------------------+
```

### 2.2 Protocol Distribution in MobileCoreNetworkInterfaceSbb

| Protocol | References | % of Code | Responsibility |
|----------|-----------|-----------|----------------|
| SS7 MAP | 1,160 | ~35% | GSM/UMTS Location Services |
| Diameter | 935 | ~30% | LTE/IMS Location Services |
| HTTP | 152 | ~5% | MLP REST API |
| CDR/Internal | ~800 | ~25% | Billing/Logging |
| Utilities | ~150 | ~5% | Helper methods |

---

## 3. Problems Identified

### 3.1 Critical Issues

#### 3.1.1 God Class Anti-Pattern
**File:** `MobileCoreNetworkInterfaceSbb.java`

```java
// [DONT] CURRENT: One class handles everything
public abstract class MobileCoreNetworkInterfaceSbb extends GMLCBaseSbb 
    implements Sbb, CDRInterfaceParent {
    
    // SS7 MAP
    public void onSendRoutingInfoForLCSRequest(...) { /* 500+ lines */ }
    public void onProvideSubscriberLocationRequest(...) { /* 400+ lines */ }
    
    // Diameter SLg
    public void onProvideLocationRequest(...) { /* 300+ lines */ }
    public void onLocationReportRequest(...) { /* 300+ lines */ }
    
    // Diameter SLh
    public void onRoutingInfoRequest(...) { /* 200+ lines */ }
    
    // Diameter Sh
    public void onUserDataRequest(...) { /* 200+ lines */ }
    
    // HTTP
    public void onGet(HttpServletRequestEvent event, ...) { /* 200+ lines */ }
    public void onPost(HttpServletRequestEvent event, ...) { /* 200+ lines */ }
    
    // Total: 53 event handlers, 2,273 if statements
}
```

**Impact:**
- Impossible to unit test
- Changes in one protocol affect others
- Code review takes hours
- New developers overwhelmed

#### 3.1.2 Deep Inheritance Hierarchy
**File:** `CDRGeneratorSbb.java`

```java
// [DONT] CURRENT: Deep inheritance
CDRGeneratorSbb 
    -> extends MobileCoreNetworkInterfaceSbb 
        -> extends GMLCBaseSbb
            -> implements Sbb

// Result: CDRGeneratorSbb inherits 11,889 lines it doesn't need!
```

#### 3.1.3 Long Method
**File:** `CDRGeneratorSbb.java`

```java
// [DONT] CURRENT: One method with ~3,490 lines
protected String generateCDR() {
    // Line 1-500: MAP handling
    // Line 501-1000: Diameter SLg handling
    // Line 1001-1500: Diameter SLh handling
    // Line 1501-2000: Diameter Sh handling
    // Line 2001-2500: HTTP handling
    // Line 2501-3000: Formatting
    // Line 3001-3490: Writing
}
```

### 3.2 Moderate Issues

| Issue | Location | Impact |
|-------|----------|--------|
| Excessive imports | 364 imports | Slow compilation, classpath conflicts |
| Mixed concerns | Business logic + Protocol handling | Hard to change protocols |
| Tight coupling | Direct RA calls everywhere | Cannot mock for testing |
| No service layer | Logic in SBB methods | Cannot reuse logic |

### 3.3 Technical Debt

```
+------------------------------------------------------------------+
|                    Technical Debt Estimate                       |
+------------------------------------------------------------------+
|                                                                  |
|  Code Duplication                                                |
|  +-- Diameter AVP extraction: ~200 lines duplicated              |
|  +-- MAP error handling: ~150 lines duplicated                   |
|  +-- Response formatting: ~300 lines duplicated                  |
|                                                                  |
|  Commented Code                                                  |
|  +-- SUPL/ULP code: ~500 lines (fully commented)                 |
|  +-- SMPP code: ~200 lines                                       |
|  +-- Deprecated methods: ~100 lines                              |
|                                                                  |
|  TODO/FIXME Comments                                             |
|  +-- 15 TODO items                                               |
|  +-- 8 FIXME items                                               |
|                                                                  |
+------------------------------------------------------------------+
```

---

## 4. Refactoring Goals

### 4.1 Primary Goals

| # | Goal | Success Criteria |
|---|------|------------------|
| 1 | **Single Responsibility** | Each SBB handles one protocol only |
| 2 | **Testability** | All business logic unit testable |
| 3 | **Maintainability** | Max 500 lines per SBB |
| 4 | **Protocol Isolation** | Change one protocol without affecting others |
| 5 | **Composition over Inheritance** | Flat class hierarchy |

### 4.2 Target Architecture Metrics

| Metric | Before | After |
|--------|--------|-------|
| Max SBB lines | 11,889 | < 500 |
| Total SBB files | 6 | ~20 |
| Event handlers per SBB | 53 | < 10 |
| Inheritance depth | 3 | 1-2 |
| Test coverage | ~0% | > 80% |

---

## 5. Target Architecture

### 5.1 Proposed Structure

```
+------------------------------------------------------------------+
|                    Target GMLC Architecture                      |
+------------------------------------------------------------------+
|                                                                  |
|  +----------------------------------------------------------+   |
|  |              GmlcGatewaySbb (Orchestrator)               |   |
|  |  * Receives all initial events                           |   |
|  |  * Routes to appropriate handler SBB                     |   |
|  |  * Manages correlation IDs                               |   |
|  |  Lines: ~300                                             |   |
|  +----------------------------------------------------------+   |
|                              |                                   |
|          +-------------------+-------------------+               |
|          |                   |                   |               |
|          v                   v                   v               |
|  +--------------+  +----------------+  +----------------+       |
|  | MapHandlerSbb|  |DiameterHandlerSbb| | HttpHandlerSbb |       |
|  |   (SS7 MAP)  |  |  (SLg/SLh/Sh)  |  |   (MLP API)    |       |
|  |  Lines: ~400 |  |  Lines: ~400   |  |  Lines: ~200   |       |
|  +------+-------+  +--------+-------+  +--------+-------+       |
|         |                   |                   |               |
|         |    +--------------+-------------------+               |
|         |    |             |                                   |
|         v    v             v                                   |
|  +----------------------------------------------------------+   |
|  |                    Service Layer (POJOs)                 |   |
|  |  +-------------+  +-------------+  +-----------------+   |   |
|  |  |MapService   |  |DiameterSvc  |  |LocationService    |   |   |
|  |  |* ATI        |  |* SLg PLR/LRA|  |* Coordinate conv  |   |   |
|  |  |* PSI        |  |* SLh RIR/RIA|  |* Format validation|   |   |
|  |  |* SRI-LCS    |  |* Sh UDR/UDA |  |* Response builder |   |   |
|  |  |* PSL        |  |             |  |                   |   |   |
|  |  |* SLR        |  |             |  |                   |   |   |
|  |  +-------------+  +-------------+  +-----------------+   |   |
|  +----------------------------------------------------------+   |
|                              |                                   |
|                              v                                   |
|  +----------------------------------------------------------+   |
|  |                    Repository/DAO Layer                  |   |
|  |  +-------------+  +-------------+  +-------------+       |   |
|  |  |CDRRepository|  |LocationRepo |  |ConfigRepo   |       |   |
|  |  +-------------+  +-------------+  +-------------+       |   |
|  +----------------------------------------------------------+   |
|                                                                  |
+------------------------------------------------------------------+
```

### 5.2 New SBB Structure

```
gmlc/core/slee/services/sbbs/src/main/java/org/mobicents/gmlc/slee/
|
├── GMLCBaseSbb.java (unchanged)
│
├── orchestrator/
│   └── GmlcGatewaySbb.java          # Main entry point
│
├── handlers/
│   ├── MapHandlerSbb.java           # SS7 MAP protocols
│   ├── DiameterSlgHandlerSbb.java   # Diameter SLg
│   ├── DiameterSlhHandlerSbb.java   # Diameter SLh
│   ├── DiameterShHandlerSbb.java    # Diameter Sh
│   └── HttpHandlerSbb.java          # HTTP/MLP
│
├── cdr/
│   ├── CDRGeneratorSbb.java         # Refactored, no inheritance
│   └── plain/
│       └── CDRService.java          # Extracted business logic
│
└── interfaces/
    ├── MobileCoreNetworkInterfaceSbbLocalObject.java
    └── CDRSBBLocalObject.java
```

### 5.3 Service Layer

```
gmlc/core/slee/services/library/src/main/java/org/mobicents/gmlc/
│
├── service/
│   ├── MapService.java              # MAP protocol logic
│   ├── DiameterService.java         # Diameter common logic
│   ├── LocationService.java         # Location calculations
│   ├── CDRService.java              # CDR generation
│   └── HttpService.java             # HTTP/MLP processing
│
├── model/
│   ├── LocationRequest.java
│   ├── LocationResponse.java
│   └── CDRRecord.java
│
└── repository/
    ├── CDRRepository.java
    └── LocationCache.java
```

---

## 6. Phase-by-Phase Implementation

### Phase 1: Foundation (Sprint 1-2)
**Goal:** Create service layer without breaking existing code

| Task | Owner | Effort | Deliverable |
|------|-------|--------|-------------|
| 1.1 Create service interfaces | Architect | 3d | Service contracts |
| 1.2 Implement MapService | Backend | 5d | Extracted MAP logic |
| 1.3 Unit test MapService | QA | 3d | >80% coverage |
| 1.4 Create repository interfaces | Architect | 2d | Repository contracts |

**Phase 1 Exit Criteria:**
- [ ] MapService fully tested
- [ ] No changes to existing SBBs
- [ ] CI/CD pipeline passes

### Phase 2: Protocol Separation (Sprint 3-5)
**Goal:** Create new handler SBBs

| Task | Owner | Effort | Deliverable |
|------|-------|--------|-------------|
| 2.1 Create MapHandlerSbb | Backend | 5d | New MAP handler |
| 2.2 Migrate MAP event handlers | Backend | 5d | Delegated to service |
| 2.3 Create DiameterSlgHandlerSbb | Backend | 5d | SLg handler |
| 2.4 Create DiameterSlhHandlerSbb | Backend | 3d | SLh handler |
| 2.5 Create DiameterShHandlerSbb | Backend | 3d | Sh handler |
| 2.6 Integration testing | QA | 5d | All protocols work |

**Phase 2 Exit Criteria:**
- [ ] All protocol handlers created
- [ ] Feature parity with original
- [ ] Integration tests pass

### Phase 3: Orchestrator and Routing (Sprint 6-7)
**Goal:** Create gateway SBB and routing logic

| Task | Owner | Effort | Deliverable |
|------|-------|--------|-------------|
| 3.1 Create GmlcGatewaySbb | Architect | 5d | Main orchestrator |
| 3.2 Implement event routing | Backend | 5d | Protocol routing |
| 3.3 Correlation ID management | Backend | 3d | Cross-SBB tracking |
| 3.4 Update sbb-jar.xml | DevOps | 2d | New SBB definitions |
| 3.5 System testing | QA | 5d | End-to-end validation |

**Phase 3 Exit Criteria:**
- [ ] New architecture functional
- [ ] All existing tests pass
- [ ] Performance benchmarks met

### Phase 4: CDR Refactoring (Sprint 8-9)
**Goal:** Fix CDR inheritance and long method

| Task | Owner | Effort | Deliverable |
|------|-------|--------|-------------|
| 4.1 Extract CDRService | Backend | 5d | Business logic in POJO |
| 4.2 Break long method | Backend | 5d | <50 lines per method |
| 4.3 Remove inheritance | Backend | 3d | Composition pattern |
| 4.4 CDR testing | QA | 5d | All CDR scenarios covered |

**Phase 4 Exit Criteria:**
- [ ] CDRGeneratorSbb < 500 lines
- [ ] No inheritance from God class
- [ ] CDR functionality preserved

### Phase 5: Cleanup (Sprint 10)
**Goal:** Remove old code and document

| Task | Owner | Effort | Deliverable |
|------|-------|--------|-------------|
| 5.1 Deprecate old SBB | Architect | 3d | Mark @Deprecated |
| 5.2 Remove unused code | Backend | 5d | Delete dead code |
| 5.3 Update documentation | Tech Writer | 5d | Architecture docs |
| 5.4 Migration guide | Architect | 3d | For operations team |
| 5.5 Final validation | QA | 5d | Full regression |

---

## 7. Detailed Refactoring Steps

### 7.1 Extract MapService

#### Step 1: Create Interface
```java
// New file: MapService.java
public interface MapService {
    SendRoutingInfoForLCSResponse handleSriLCS(SendRoutingInfoForLCSRequest request);
    ProvideSubscriberLocationResponse handlePsl(ProvideSubscriberLocationRequest request);
    SubscriberLocationReportResponse handleSlr(SubscriberLocationReportRequest request);
    AnyTimeInterrogationResponse handleAti(AnyTimeInterrogationRequest request);
    ProvideSubscriberInfoResponse handlePsi(ProvideSubscriberInfoRequest request);
}
```

#### Step 2: Implement Service
```java
public class MapServiceImpl implements MapService {
    private final MAPProvider mapProvider;
    private final LocationService locationService;
    
    @Override
    public SendRoutingInfoForLCSResponse handleSriLCS(SendRoutingInfoForLCSRequest request) {
        // Extract logic from MobileCoreNetworkInterfaceSbb
        // Make it testable (no SBB dependencies)
    }
}
```

#### Step 3: Create Handler SBB
```java
public abstract class MapHandlerSbb extends GMLCBaseSbb {
    private MapService mapService = new MapServiceImpl();
    
    public void onSendRoutingInfoForLCSRequest(
            SendRoutingInfoForLCSRequest event, 
            ActivityContextInterface aci) {
        
        SendRoutingInfoForLCSResponse response = 
            mapService.handleSriLCS(event);
        
        // Send response via MAP RA
    }
}
```

### 7.2 Break Long Method (CDR)

#### Before: ~3,490 lines
```java
protected String generateCDR() {
    // Massive method handling all protocols
}
```

#### After: Multiple small methods
```java
// CDRService.java
public class CDRService {
    private final CDRFormatter formatter;
    private final CDRWriter writer;
    
    public String generateCDR(CDRContext context) {
        return formatter.format(context);
    }
    
    public void writeCDR(String cdr) {
        writer.write(cdr);
    }
}

// CDRFormatter.java
public class CDRFormatter {
    public String format(CDRContext context) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(formatHeader(context));
        sb.append(formatLocation(context));
        sb.append(formatSubscriber(context));
        sb.append(formatTimestamp(context));
        
        return sb.toString();
    }
    
    private String formatHeader(CDRContext context) {
        // ~20 lines
    }
    
    private String formatLocation(CDRContext context) {
        // ~30 lines
    }
    // ... more small methods
}
```

### 7.3 Composition Pattern

#### Before: Deep Inheritance
```java
public abstract class CDRGeneratorSbb extends MobileCoreNetworkInterfaceSbb {
    // Inherits 11,889 lines unnecessarily
}
```

#### After: Composition
```java
public abstract class CDRGeneratorSbb extends GMLCBaseSbb {
    
    // Services via composition
    private final CDRService cdrService = ServiceLocator.getCDRService();
    private final LocationService locationService = ServiceLocator.getLocationService();
    
    // CMP fields only for CDR-specific state
    public abstract CDRState getCdrState();
    public abstract void setCdrState(CDRState state);
    
    public void generateCDR() {
        CDRContext context = buildContext();
        String cdr = cdrService.generateCDR(context);
        cdrService.writeCDR(cdr);
    }
}
```

### 7.4 Event Routing Pattern

```java
public abstract class GmlcGatewaySbb extends GMLCBaseSbb {
    
    // Child relations to handler SBBs
    public abstract ChildRelation getMapHandlerChildRelation();
    public abstract ChildRelation getDiameterHandlerChildRelation();
    public abstract ChildRelation getHttpHandlerChildRelation();
    
    // Route SS7 MAP events
    public void onSendRoutingInfoForLCSRequest(
            SendRoutingInfoForLCSRequest event,
            ActivityContextInterface aci) {
        
        ChildRelation relation = getMapHandlerChildRelation();
        MapHandlerSbbLocalObject child = (MapHandlerSbbLocalObject) relation.create();
        
        // Store correlation
        setCorrelationId(generateCorrelationId());
        
        // Forward to child
        aci.attach(child);
        aci.detach(sbbContext.getSbbLocalObject());
    }
    
    // Route Diameter events
    public void onProvideLocationRequest(
            ProvideLocationRequest event,
            ActivityContextInterface aci) {
        
        ChildRelation relation = getDiameterHandlerChildRelation();
        DiameterHandlerSbbLocalObject child = 
            (DiameterHandlerSbbLocalObject) relation.create();
        
        aci.attach(child);
        aci.detach(sbbContext.getSbbLocalObject());
    }
}
```

---

## 8. Risk Mitigation

### 8.1 High-Risk Items

| Risk | Probability | Impact | Mitigation |
|------|------------|--------|------------|
| Regression in production | Medium | Critical | Feature flags, canary deployment |
| Performance degradation | Medium | High | Benchmark before/after |
| Timeline slip | High | Medium | Parallel workstreams |
| Developer resistance | Low | Medium | Training, pair programming |

### 8.2 Rollback Plan

```
+------------------------------------------------------------------+
|                    Rollback Strategy                             |
+------------------------------------------------------------------+
|                                                                  |
|  1. Feature Flags                                                |
|     - New SBBs behind configuration flag                         |
|     - Can disable without redeployment                           |
|                                                                  |
|  2. Database Compatibility                                       |
|     - No schema changes in Phase 1-4                             |
|     - New tables only in Phase 5                                 |
|                                                                  |
|  3. Parallel Deployment                                          |
|     - Old and new SBBs coexist                                   |
|     - Routing decides which to use                               |
|                                                                  |
|  4. Hotfix Capability                                            |
|     - Original SBB preserved until Phase 5                       |
|     - Can patch old version if needed                            |
|                                                                  |
+------------------------------------------------------------------+
```

---

## 9. Success Metrics

### 9.1 Code Quality Metrics

| Metric | Baseline | Target | Measurement |
|--------|----------|--------|-------------|
| Lines per SBB | 11,889 | < 500 | SonarQube |
| Cyclomatic Complexity | Very High | < 10/method | SonarQube |
| Code Duplication | ~650 lines | < 50 lines | SonarQube |
| Test Coverage | ~0% | > 80% | JaCoCo |

### 9.2 Performance Metrics

| Metric | Baseline | Target | Measurement |
|--------|----------|--------|-------------|
| Request latency | TBD | No regression | JMeter |
| Throughput | TBD | No regression | JMeter |
| Memory usage | TBD | -20% | JConsole |
| Startup time | TBD | No regression | Timer |

### 9.3 Maintainability Metrics

| Metric | Baseline | Target | Measurement |
|--------|----------|--------|-------------|
| Onboarding time | Days | Hours | Survey |
| Bug fix time | Days | Hours | JIRA |
| Feature addition | Weeks | Days | JIRA |

---

## Appendix: Code Metrics

### Detailed Metrics from Source Analysis

```
MobileCoreNetworkInterfaceSbb.java
==================================
File Size:           835.77 KB
Lines of Code:       11,889
Imports:             364
Package:             org.mobicents.gmlc.slee

Methods:
  Event Handlers:    53
  Public Methods:    93
  Private Methods:   20
  Protected Methods: 22

Complexity:
  If Statements:     2,273
  Switch Statements: 14
  Try Blocks:        78
  Catch Blocks:      94
  While Loops:       12
  For Loops:         45

Protocol References:
  SS7 MAP:           1,160
  Diameter:          935
  HTTP:              152

CDRGeneratorSbb.java
====================
File Size:           205.90 KB
Lines of Code:       3,906
Imports:             87

Methods:
  Total:             12
  Longest Method:    ~3,490 lines

Complexity:
  If Statements:     456
  Switch Statements: 3
```

---

## References

1. [JAIN SLEE 1.1 Specification](https://jcp.org/en/jsr/detail?id=240)
2. [Refactoring: Improving the Design of Existing Code](https://martinfowler.com/books/refactoring.html) - Martin Fowler
3. [Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882) - Robert C. Martin
4. [Working Effectively with Legacy Code](https://www.amazon.com/Working-Effectively-Legacy-Michael-Feathers/dp/0131177052) - Michael Feathers

---

## Change Log

| Date | Version | Author | Changes |
|------|---------|--------|---------|
| 2026-04-07 | 1.0 | AI Assistant | Initial draft |

---

## Next Steps

1. Review this plan with the architecture team
2. Create JIRA epics for each phase
3. Set up feature flag infrastructure
4. Schedule kickoff meeting

---

**Document Info:**
- Location: `research/refactory.md`
- Size: ~31KB
- Format: Markdown
- Encoding: UTF-8
