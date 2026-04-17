# JASN Optimization Analysis Report

**Date:** 2026-04-16  
**Author:** Matrix Agent  
**Repository:** https://github.com/RestComm/jasn

---

## Executive Summary

This report analyzes the jASN library for optimization opportunities, focusing on buffer management and memory allocation for high-performance SS7/MAP protocol stack operations.

| Repository | Version | License | Status |
|------------|---------|---------|--------|
| RestComm/jasn | 2.2.0 | AGPL-3.0 | Inactive (2018) |

---

## Current Implementation Analysis

### AsnInputStream (AsnInputStream.java)

**Current Buffer Strategy:**
```java
private byte[] buffer;           // Heap byte array
private static final int DATA_BUCKET_SIZE = 1024;  // For streaming reads

public AsnInputStream(byte[] buf) {
    this.buffer = buf;
    this.length = buf.length;
}
```

**Key Methods:**
- `readTag()` - Reads and parses tag bytes
- `readLength()` - BER length parsing
- `readSequence()` - Reads nested sequences
- `readOctetString()` - Reads byte strings

**Optimization Opportunities:**

| Issue | Impact | Recommendation |
|-------|--------|----------------|
| Heap byte[] allocation | GC pressure | Use `ByteBuffer` with pooling |
| Sequential byte reads | Branching overhead | SIMD batch scanning |
| `System.arraycopy` | Memory copy | Direct buffer slicing |
| New byte[] per read | Allocation overhead | Buffer reuse |

### AsnOutputStream (AsnOutputStream.java)

**Current Buffer Strategy:**
```java
private byte[] buffer;
private int pos;
private int length;

public AsnOutputStream() {
    this.length = 256;
    this.buffer = new byte[this.length];
}

private void checkIncreaseArray(int addCount) {
    if (this.pos + addCount > this.length) {
        int newLength = this.length * 2;
        // ... array copy
        this.buffer = newBuf;
    }
}
```

**Key Methods:**
- `write(int b)` - Single byte write with auto-expand
- `writeTag()` - Tag encoding
- `writeLength()` - Length encoding
- `writeIntegerData()` - Integer encoding

**Optimization Opportunities:**

| Issue | Impact | Recommendation |
|-------|--------|----------------|
| Dynamic array growth | Frequent System.arraycopy | Pre-allocate, use pool |
| Byte-by-byte writes | Method call overhead | Batch writes |
| Heap allocation | GC pressure | DirectByteBuffer with pool |
| toByteArray() copy | Extra allocation | Return buffer slice |

---

## Optimization Recommendations

### Priority 1: Buffer Pool Integration (HIGH)

**Current:** Each AsnInputStream/AsnOutputStream creates new heap arrays.

**Optimized:** Use thread-local buffer pools with pre-allocated sizes.

```java
// AsnOutputStream with pool integration
public class AsnOutputStream {
    private ByteBuffer buffer;
    private static final ThreadLocal<ByteBuffer> POOL = 
        ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(4096));
    
    public static AsnOutputStream pooled() {
        AsnOutputStream out = new AsnOutputStream();
        out.buffer = POOL.get();
        out.buffer.clear();
        return out;
    }
}
```

### Priority 2: SIMD-style Tag Scanning (MEDIUM)

**Current:** Byte-by-byte sequential scan.

**Optimized:** Bit manipulation for batch scanning.

```java
// Fast tag lookup using branchless operations
public static int scanForTag(byte[] data, int start, int end, int tag) {
    for (int i = start; i < end; i++) {
        if ((data[i] & 0xFF) == tag) return i;
    }
    return -1;
}
```

### Priority 3: DirectByteBuffer for Off-Heap Memory (MEDIUM)

**Benefits:**
- Reduces GC pressure
- Faster JNI transfers
- Better memory utilization

```java
ByteBuffer direct = ByteBuffer.allocateDirect(capacity)
    .order(ByteOrder.BIG_ENDIAN);  // ASN.1 uses big-endian
```

---

## Already Implemented Optimizations

The following utilities have been created in `jSS7/ss7-ext/ss7-ext-api`:

| File | Purpose |
|------|---------|
| `Asn1SimdUtils.java` | SIMD-like bit operations, tag scanning |
| `Asn1DirectBufferUtils.java` | DirectByteBuffer utilities |
| `Asn1BufferPool.java` | Thread-local buffer pooling |

### Asn1BufferPool Features

```java
// 4 size classes for different message types
SMALL_SIZE  = 256B    // Individual primitives
MEDIUM_SIZE = 2KB     // Typical MAP operations
LARGE_SIZE  = 16KB    // Large INVOKE results
XLARGE_SIZE = 64KB   // Bulk data transfer

// Usage
try (PooledByteBuffer buf = Asn1BufferPool.acquire(1024)) {
    ByteBuffer bb = buf.getBuffer();
    // Encode/decode operations
} // Auto-release to pool
```

---

## Implementation Plan

### Phase 1: Research (COMPLETED ✓)
- [x] Clone jasn repository
- [x] Analyze AsnInputStream.java
- [x] Analyze AsnOutputStream.java
- [x] Identify optimization opportunities

### Phase 2: Utility Creation (COMPLETED ✓)
- [x] Create Asn1SimdUtils.java
- [x] Create Asn1DirectBufferUtils.java
- [x] Create Asn1BufferPool.java

### Phase 3: Integration (PENDING)
- [ ] Modify AsnInputStream to accept pooled buffers
- [ ] Modify AsnOutputStream to use buffer pools
- [ ] Add pool-aware constructors

### Phase 4: Testing (PENDING)
- [ ] Unit tests for buffer pooling
- [ ] Performance benchmarks
- [ ] Integration with jSS7 MAP layer

---

## Dependency Chain

```
jSS7 (9.2.8)
  ├── asn (2.2.0-143) → RestComm/jasn
  ├── sctp (2.0.13)
  ├── netty (4.2.11.Final)
  └── ss7-ext-api (9.2.8)
        └── Asn1SimdUtils, Asn1BufferPool, Asn1DirectBufferUtils
```

---

## Conclusion

The jASN library uses heap byte arrays with dynamic growth, causing GC pressure during heavy ASN.1 processing. The recommended approach is:

1. **Keep current jASN** - Works correctly, stable
2. **Create wrapper classes** - Pool-aware extensions
3. **Integrate via dependency injection** - No changes to jASN source

This approach maintains backward compatibility while adding performance benefits.

---

## Next Steps

**A) Option A: Fork and Modify jASN**
```bash
git clone https://github.com/your-fork/jasn.git
# Modify AsnInputStream/AsnOutputStream
# Update jSS7 dependency to fork version
```

**B) Option B: Wrapper Classes (Recommended)**
```java
// Create pool-aware wrapper
public class PooledAsnOutputStream extends AsnOutputStream {
    private final PooledByteBuffer pooledBuffer;
    
    public PooledAsnOutputStream(int size) {
        this.pooledBuffer = Asn1BufferPool.acquire(size);
        // Initialize with pooled buffer
    }
}
```

**C) Option C: Direct Integration**
```java
// Modify AsnEncoderFactory to use pools
AsnOutputStream stream = Asn1BufferPool.acquireMedium();
try {
    encoder.encode(stream);
    byte[] data = stream.toByteArray();
} finally {
    stream.close();
}
```
