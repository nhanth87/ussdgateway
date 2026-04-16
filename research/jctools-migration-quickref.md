# JCTools Migration Quick Reference

## Dependency

```xml
<dependency>
    <groupId>org.jctools</groupId>
    <artifactId>jctools-core</artifactId>
    <version>4.0.3</version>
</dependency>
```

## Quick Mapping

| Javolution | JCTools/Standard Java | When to Use |
|------------|----------------------|-------------|
| `FastList<T>` | `MpscArrayQueue<T>` | If used as FIFO queue |
| `FastList<T>` | `CopyOnWriteArrayList<T>` | If read-heavy, rare writes |
| `FastMap<K,V>` | `NonBlockingHashMap<K,V>` | High-concurrency map |
| `FastSet<T>` | `ConcurrentHashMap.newKeySet()` | Concurrent set |
| `TextBuilder` | `StringBuilder` | String building |

## Key API Differences

### Queue Operations

```java
// Javolution FastList (as queue)
list.pollFirst();      // → queue.relaxedPoll()
list.addLast(item);    // → queue.relaxedOffer(item)
list.size();           // → queue.size() (may be approximate)

// JCTools
MpscArrayQueue<T> queue = new MpscArrayQueue<>(capacity);  // Power of 2
queue.relaxedOffer(item);     // Lock-free insert
T item = queue.relaxedPoll(); // Lock-free remove
T item = queue.relaxedPeek(); // Lock-free peek
```

### Map Operations

```java
// Javolution
FastMap<K,V> map = new FastMap<>();
map.put(k, v);
map.get(k);
map.keySet();

// JCTools (drop-in replacement)
NonBlockingHashMap<K,V> map = new NonBlockingHashMap<>();
map.put(k, v);       // Lock-free
map.get(k);          // Lock-free
map.keySet();        // Weakly consistent iteration
```

## Thread-Safety Patterns

### 1. SPSC (Single Producer Single Consumer)
```java
// Use case: One thread produces, one thread consumes
private final SpscArrayQueue<T> queue = new SpscArrayQueue<>(1024);
```

### 2. MPSC (Multi Producer Single Consumer)
```java
// Use case: Multiple threads produce, one thread consumes
private final MpscArrayQueue<T> queue = new MpscArrayQueue<>(1024);
```

### 3. Read-Mostly Collections
```java
// Use case: Frequent reads, rare writes
private final CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<>();
private final NonBlockingHashMap<K,V> map = new NonBlockingHashMap<>();
```

## XML Migration (XStream)

```java
// Add dependency
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.20</version>
</dependency>

// Replace Javolution XML with XStream
XStream xstream = new XStream();
xstream.alias("association", AssociationImpl.class);

// Serialize
String xml = xstream.toXML(object);

// Deserialize  
Object obj = xstream.fromXML(xml);
```

## Files to Update

### sctp-api
- `Management.java` - Return types
- `Server.java` - Return types
- `PayloadDataPool.java` - Implement with MpscArrayQueue

### sctp-impl  
- `ManagementImpl.java` - Collections + XML
- `ServerImpl.java` - Collections + XML
- `AssociationImpl.java` - XML annotations
- `SelectorThread.java` - FastSet → ConcurrentHashMap.newKeySet()

### sctp-netty-impl
- `NettySctpManagementImpl.java` - Collections + XML
- `NettyServerImpl.java` - Collections + XML
- `NettyAssociationImpl.java` - XML annotations

## Build Verification

```bash
cd sctp
mvn clean install -DskipTests
```

## Performance Testing

```bash
# Run stress tests
mvn test -Dtest=ConcurrencyTest

# Profile with high throughput
java -jar sctp-perf-test.jar --rate=500000 --duration=60
```
