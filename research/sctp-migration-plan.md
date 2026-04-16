# SCTP Migration Plan: Netty 4.1.115 + Javolution Optimization

## Overview
Migration from Netty 4.1.105 to 4.1.115 (latest stable), optimize with Javolution collections, and implement object pooling.

---

## 1. Dependency Updates

### Current Versions
- Netty: 4.1.105.Final
- Javolution: 5.5.1

### Target Versions
- Netty: 4.1.115.Final (latest stable, 4.2 not yet released)
- Javolution: 5.5.1 (latest available) or 6.0.0 if found

### Netty Module Breakdown (Replace netty-all)

```xml
<!-- Individual Netty modules instead of netty-all -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-common</artifactId>
    <version>${netty.version}</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-buffer</artifactId>
    <version>${netty.version}</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-transport</artifactId>
    <version>${netty.version}</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-transport-sctp</artifactId>
    <version>${netty.version}</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-handler</artifactId>
    <version>${netty.version}</version>
</dependency>
```

---

## 2. Javolution Collection Migration

### Replace Java Collections with Javolution

| Java Collection | Javolution Equivalent |
|----------------|---------------------|
| `ArrayList<T>` | `FastList<T>` |
| `HashMap<K,V>` | `FastMap<K,V>` |
| `HashSet<T>` | `FastSet<T>` |
| `StringBuilder` | `TextBuilder` |
| `AtomicInteger` | `RealtimeInteger` (if needed) |

### Files to Update
- `NettyAssociationMap.java` - Already uses FastMap ✓
- `NettySctpManagementImpl.java` - Uses FastList, FastMap
- `NettyServerImpl.java` - Check for Java collections
- All handler classes

---

## 3. ByteBuf Migration (Zero-Copy)

### Replace byte[] with ByteBuf

**Current:**
```java
byte[] data = new byte[1024];
// fill data
PayloadData payload = new PayloadData(data.length, data, ...);
```

**Target:**
```java
ByteBuf buffer = allocator.buffer(1024);
// write to buffer
try {
    PayloadData payload = new PayloadData(buffer.readableBytes(), buffer, ...);
    association.send(payload);
} finally {
    // Buffer released after send
}
```

### Files to Update
- `NettyAssociationImpl.java` - send() method
- `PayloadData.java` - Support ByteBuf constructors
- All encoding/decoding logic

---

## 4. PayloadData Object Pool

### Pool Implementation

```java
public class PayloadDataPool {
    private static final int MAX_POOL_SIZE = 1024;
    private final Queue<PayloadData> pool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
    
    public PayloadData acquire(int dataLength, ByteBuf byteBuf, boolean complete, 
                               boolean unordered, int payloadProtocolId, int streamNumber) {
        PayloadData data = pool.poll();
        if (data != null) {
            data.reset(dataLength, byteBuf, complete, unordered, payloadProtocolId, streamNumber);
            return data;
        }
        return new PayloadData(dataLength, byteBuf, complete, unordered, payloadProtocolId, streamNumber);
    }
    
    public void release(PayloadData data) {
        if (pool.size() < MAX_POOL_SIZE) {
            data.clear(); // Release ByteBuf reference
            pool.offer(data);
        }
    }
}
```

### Modified PayloadData

```java
public class PayloadData {
    // ... existing fields ...
    
    public void reset(int dataLength, ByteBuf byteBuf, boolean complete, 
                      boolean unordered, int payloadProtocolId, int streamNumber) {
        this.dataLength = dataLength;
        this.byteBuf = byteBuf;
        this.complete = complete;
        this.unordered = unordered;
        this.payloadProtocolId = payloadProtocolId;
        this.streamNumber = streamNumber;
        this.retryCount = 0;
    }
    
    public void clear() {
        if (this.byteBuf != null) {
            ReferenceCountUtil.release(this.byteBuf);
            this.byteBuf = null;
        }
    }
}
```

---

## 5. Sync/Lock Optimization

### Current Issues
1. `synchronized` blocks in management methods
2. `volatile` variables without proper atomic operations
3. `lastCongestionMonitorSecondPart` race condition

### Optimizations

**Replace synchronized with Javolution Realtime:**
```java
// Current
private volatile long lastCongestionMonitorSecondPart;

// Optimized
private final RealtimeLong lastCongestionMonitorSecondPart = new RealtimeLong(0);
```

**Lock-free collections:**
```java
// Current
protected FastList<Server> servers = new FastList<Server>();

// Optimized - Use concurrent collections if needed
protected final ConcurrentRealtimeList<Server> servers = new ConcurrentRealtimeList<Server>();
```

**Thread-local buffers:**
```java
private static final FastThreadLocal<ByteBuf> THREAD_LOCAL_BUFFER = 
    new FastThreadLocal<ByteBuf>() {
        @Override
        protected ByteBuf initialValue() {
            return Unpooled.buffer(INITIAL_CAPACITY);
        }
    };
```

---

## 6. Implementation Order

1. **Update pom.xml** - Dependencies
2. **Update PayloadData** - Add pooling support
3. **Create PayloadDataPool** - Object pool
4. **Migrate byte[] to ByteBuf** - Zero-copy
5. **Optimize collections** - Javolution everywhere
6. **Fix sync/lock issues** - Thread safety
7. **Build & Test**

---

## 7. Performance Targets

- Reduce GC pressure by 30-40% with object pooling
- Improve throughput with zero-copy ByteBuf
- Reduce lock contention with lock-free collections
- Better memory locality with Javolution
