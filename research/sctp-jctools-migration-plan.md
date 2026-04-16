# SCTP Migration: Javolution → JCTools

## Executive Summary

JCTools (Java Concurrency Tools) là thư viện hiện đại, high-performance thay thế cho Javolution đã cũ. JCTools cung cấp lock-free data structures với throughput cao hơn và memory footprint thấp hơn.

---

## JCTools Overview

### Maven Dependency
```xml
<dependency>
    <groupId>org.jctools</groupId>
    <artifactId>jctools-core</artifactId>
    <version>4.0.3</version>
</dependency>
```

### Key Features vs Javolution

| Feature | Javolution | JCTools |
|---------|------------|---------|
| Last Update | 2012 | 2024 (Active) |
| License | BSD | Apache 2.0 |
| Lock-free | Partial | Full |
| Cache-friendly | Yes | Optimized |
| Object Pooling | Yes | Yes |
| Memory Barriers | Basic | Advanced (x86 optimized) |

### Benchmarks (from JCTools repo)

```
Throughput (ops/sec) - Single Producer Single Consumer:
- ArrayBlockingQueue:     ~2M
- LinkedTransferQueue:    ~4M  
- JCTools SPSC:          ~20M (5-10x faster)

Throughput (ops/sec) - Multi Producer Single Consumer:
- ConcurrentLinkedQueue:  ~3M
- LinkedBlockingQueue:    ~1M
- JCTools MPSC:          ~15M (3-5x faster)
```

---

## Migration Mapping

### 1. Lists → Queues (JCTools)

| Javolution | JCTools | Use Case | Threading Model |
|------------|---------|----------|-----------------|
| `FastList<T>` (FIFO queue) | `ConcurrentCircularArrayQueue<T>` | Message queue, work queue | MPMC |
| `FastList<T>` (LIFO stack) | `ConcurrentStack<T>` | Stack operations | MPMC |
| `ArrayList<T>` | `ConcurrentCircularArrayQueue<T>` | Dynamic list | MPMC |

**Recommendation for SCTP:**
```java
// Current Javolution
private FastList<ChangeRequest> pendingChanges = new FastList<ChangeRequest>();

// JCTools replacement
private final ConcurrentCircularArrayQueue<ChangeRequest> pendingChanges = 
    new ConcurrentCircularArrayQueue<>(1024);
```

### 2. Maps → NonBlockingHashMap (JCTools)

| Javolution | JCTools | Use Case |
|------------|---------|----------|
| `FastMap<K,V>` | `NonBlockingHashMap<K,V>` | Association lookup |
| `ConcurrentHashMap<K,V>` | `NonBlockingHashMap<K,V>` | High-concurrency maps |

**Recommendation for SCTP:**
```java
// Current Javolution
protected FastMap<String, Association> associations = new FastMap<String, Association>();

// JCTools replacement
protected final NonBlockingHashMap<String, Association> associations = 
    new NonBlockingHashMap<>();
```

### 3. Sets → ConcurrentSet (JCTools)

| Javolution | JCTools | Use Case |
|------------|---------|----------|
| `FastSet<T>` | `ConcurrentHashMap.newKeySet()` hoặc custom | Unique collections |

**Recommendation for SCTP:**
```java
// Current Javolution
FastSet<SocketAddress> peerAddresses = new FastSet<SocketAddress>();

// JCTools replacement
Set<SocketAddress> peerAddresses = ConcurrentHashMap.newKeySet();
```

### 4. Object Pool → MessagePassingQueue (JCTools)

| Javolution | JCTools | Use Case |
|------------|---------|----------|
| `FastList<T>` pool | `MpscArrayQueue<T>` | Object pooling |

**Recommendation for PayloadDataPool:**
```java
// Current Javolution
private final FastList<PayloadData> pool = new FastList<PayloadData>();

// JCTools replacement - MPSC queue for high-throughput pooling
private final MpscArrayQueue<PayloadData> pool;
```

### 5. XML Serialization

Javolution XML không có equivalent trong JCTools. Cần migrate sang:

**Option A: Jackson XML**
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.16.0</version>
</dependency>
```

**Option B: XStream**
```xml
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.20</version>
</dependency>
```

**Recommendation:** XStream - Easier migration from Javolution XML

### 6. TextBuilder → StringBuilder

```java
// Current Javolution
private final TextBuilder persistFile = TextBuilder.newInstance();

// Standard Java
private final StringBuilder persistFile = new StringBuilder();
```

---

## Detailed Migration Plan

### Phase 1: Dependencies (pom.xml)

**Remove:**
```xml
<!-- Remove Javolution -->
<dependency>
    <groupId>javolution</groupId>
    <artifactId>javolution</artifactId>
    <version>${javolution.version}</version>
</dependency>
```

**Add:**
```xml
<!-- Add JCTools -->
<dependency>
    <groupId>org.jctools</groupId>
    <artifactId>jctools-core</artifactId>
    <version>4.0.3</version>
</dependency>

<!-- Add XStream for XML serialization -->
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.20</version>
</dependency>
```

### Phase 2: Core Collections

#### Files to Modify:

1. **Management.java** (API)
   - `FastList<Server>` → `ConcurrentCircularArrayQueue<Server>` hoặc `CopyOnWriteArrayList<Server>`
   - `FastMap<String, Association>` → `NonBlockingHashMap<String, Association>`

2. **Server.java** (API)
   - `FastList<String>` → `ConcurrentCircularArrayQueue<String>`
   - `FastList<Association>` → `ConcurrentCircularArrayQueue<Association>`

3. **PayloadDataPool.java**
   - `FastList<PayloadData>` → `MpscArrayQueue<PayloadData>` (Multi-Producer Single-Consumer)
   - Pool size: 100,000
   - Batch size: 1024

4. **ManagementImpl.java**
   - `FastList<Server>` → `CopyOnWriteArrayList<Server>` (read-heavy, rare writes)
   - `FastMap<String, Association>` → `NonBlockingHashMap<String, Association>`
   - `FastList<ChangeRequest>` → `MpscArrayQueue<ChangeRequest>` (SPSC pattern)

5. **SelectorThread.java**
   - `FastSet<SocketAddress>` → `ConcurrentHashMap.newKeySet()`

### Phase 3: XML Serialization Migration

Current Javolution XML:
```java
protected static final XMLFormat<AssociationImpl> ASSOCIATION_XML = 
    new XMLFormat<AssociationImpl>(AssociationImpl.class) {
    public void read(javolution.xml.XMLFormat.InputElement xml, AssociationImpl association) 
        throws XMLStreamException { ... }
    public void write(AssociationImpl association, javolution.xml.XMLFormat.OutputElement xml) 
        throws XMLStreamException { ... }
};
```

XStream replacement:
```java
XStream xstream = new XStream();
xstream.alias("association", AssociationImpl.class);
xstream.alias("server", ServerImpl.class);

// Serialize
String xml = xstream.toXML(association);

// Deserialize
AssociationImpl association = (AssociationImpl) xstream.fromXML(xml);
```

### Phase 4: Netty Implementation

1. **NettySctpManagementImpl.java**
   - Same collection changes as ManagementImpl
   - XML persistence → XStream

2. **NettyServerImpl.java**
   - `FastList<String>` → `CopyOnWriteArrayList<String>`
   - `FastList<Association>` → `CopyOnWriteArrayList<Association>`

3. **NettyAssociationImpl.java**
   - XML serialization → XStream annotations

---

## Performance Expectations

| Metric | Javolution | JCTools | Improvement |
|--------|------------|---------|-------------|
| Queue ops/s | ~5M | ~20M | 4x |
| Map get/put | ~3M | ~10M | 3x |
| Memory overhead | High | Low | 30% less |
| GC pressure | Medium | Low | 50% less |

---

## Files Affected

### sctp-api Module:
- `Management.java` - Change return types
- `Server.java` - Change return types
- `PayloadDataPool.java` - Rewrite with JCTools queues

### sctp-impl Module:
- `ManagementImpl.java` - Collections + XML
- `ServerImpl.java` - Collections + XML
- `AssociationImpl.java` - Collections + XML
- `SelectorThread.java` - Collections
- `SctpXMLBinding.java` - Remove/replace with XStream
- `NettySctpManagementImpl.java` - Collections + XML
- `NettyServerImpl.java` - Collections + XML
- `NettyAssociationImpl.java` - Collections + XML
- `NettySctpXMLBinding.java` - Remove/replace with XStream

---

## Risks & Mitigation

| Risk | Mitigation |
|------|------------|
| XML format incompatibility | Implement migration path or dual-read support |
| Different iteration order | Test thoroughly with concurrent access |
| Memory model differences | JCTools uses VarHandles, well-tested |
| API changes | Comprehensive unit tests |

---

## Testing Strategy

1. **Unit Tests:** Verify all collection operations
2. **Concurrency Tests:** Stress test with 500K+ msg/s
3. **XML Compatibility:** Test old config file loading
4. **Memory Profiling:** Verify no leaks

---

## Migration Priority

1. **HIGH:** Core collections (ManagementImpl, PayloadDataPool)
2. **MEDIUM:** Netty implementation
3. **LOW:** XML serialization (keep compatible with old format)
