# SCTP Netty Performance Optimization Analysis Report

## Executive Summary

This report analyzes the netty-sctp implementation in the sctp project to identify potential further performance improvements beyond the existing optimizations documented in `CHANGELOG_SCTP_PERFORMANCE_TUNING.md`. The analysis focuses on buffer pooling, memory management, EventLoop threading, channel configuration, and SCTP-specific optimizations.

The current implementation has achieved significant milestones including pooled direct buffers (PooledNioSctpChannel), multi-association architecture with M3UA Loadshare, and tuned kernel buffer sizes. However, several opportunities remain for achieving the 10,000 SMS/s target on native Linux.

---

## 1. Introduction

### 1.1 Analysis Objectives

The objective of this analysis is to identify bottlenecks and optimization opportunities in the netty-sctp implementation that could push throughput beyond current limits. The analysis covers the sctp/sctp-impl module, specifically the Netty-based SCTP channel implementations.

### 1.2 Current State Summary

Based on the CHANGELOG and code analysis, the following optimizations have been implemented:

The system has transitioned from a single SCTP association to a multi-association architecture using 4 parallel associations sharing an M3UA Application Server in Loadshare mode. This architectural change addresses kernel SCTP limitations particularly relevant in WSL2 environments where single-association throughput was capped around 800-1,400 TPS. The implementation includes custom PooledNioSctpChannel and PooledNioSctpServerChannel classes that override default buffer allocation behavior to use Netty's PooledByteBufAllocator instead of ByteBuffer.allocateDirect() on every I/O operation. Additionally, socket buffer sizes have been increased to 8 MB for both send and receive buffers, SCTP_NODELAY is enabled, and FlushConsolidationHandler has been added to reduce individual flush() calls.

### 1.3 Methodology

This analysis examined the source code in sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/, compared with upstream Netty 4.2.12.Final SCTP implementation, and referenced Netty community performance issues and best practices.

---

## 2. Current Implementation Analysis

### 2.1 PooledNioSctpChannel Implementation

**File**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpChannel.java`

The PooledNioSctpChannel implements buffer pooling at the channel level. The doReadMessages() method allocates a buffer using the RecvByteBufAllocator.Handle and the channel's configured allocator, which uses PooledByteBufAllocator by default. The doWriteMessage() method handles buffer conversion when data is not direct or requires multiple NIO buffers.

**Current implementation pattern**:
```java
ByteBuf buffer = allocHandle.allocate(config().getAllocator());
// ... receive data into buffer ...
buf.add(new SctpMessage(messageInfo, buffer.writerIndex(...)));
```

The implementation correctly uses Netty's buffer allocator API and properly handles buffer lifecycle. However, there are potential improvements in buffer size selection and handling of the notification handler.

### 2.2 EventLoop Configuration

**File**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpManagementImpl.java`

The EventLoop configuration uses fixed thread counts:
```java
private int bossGroupThreadCount = 4;
private int workerGroupThreadCount = 4;
```

The NioEventLoopGroup is instantiated with these counts:
```java
this.bossGroup = new NioEventLoopGroup(this.getBossGroupThreadCount(),
    new DefaultThreadFactory("Sctp-BossGroup-" + this.name));
this.workerGroup = new NioEventLoopGroup(this.getWorkerGroupThreadCount(),
    new DefaultThreadFactory("Sctp-WorkerGroup-" + this.name));
```

### 2.3 Channel Pipeline Configuration

**Files**:
- `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpClientChannelInitializer.java`
- `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpServerChannelInitializer.java`

Both initializers configure the same pipeline pattern:
```java
ch.pipeline().addLast(new SctpMessageCompletionHandler(),
    new FlushConsolidationHandler(),
    new NettySctpClientHandler(...));
```

Write buffer watermarks are set to:
- High: 64 MB
- Low: 32 MB

### 2.4 PayloadDataPool Implementation

**File**: `sctp/sctp-api/src/main/java/org/mobicents/protocols/api/PayloadDataPool.java`

This is a sophisticated implementation using JCTools MpscArrayQueue with adaptive sizing. For 500K msg/s target, it pre-allocates 100,000 objects. The pool uses atomic operations for thread safety and includes hit rate monitoring.

---

## 3. Identified Bottlenecks and Optimization Opportunities

### 3.1 EventLoop Threading Model Issues

**Issue 1: Fixed Thread Count Not Scaled to Association Count**

**Location**: `NettySctpManagementImpl.java` lines ~260-270

**Problem**: The bossGroupThreadCount and workerGroupThreadCount are fixed at 4 regardless of the number of associations. With 4 associations, this may cause thread contention or could be underutilized depending on CPU cores.

**Current configuration**:
```java
private int bossGroupThreadCount = 4;
private int workerGroupThreadCount = 4;
```

**Recommendation**: Scale EventLoop threads based on association count and CPU cores. For 4 associations on an 8+ core system, consider:
```java
// Dynamic calculation based on associations and cores
int cores = Runtime.getRuntime().availableProcessors();
int threadCount = Math.max(2, Math.min(cores, associations.size() * 2));
```

### 3.2 Buffer Management Improvements

**Issue 2: Fixed Read Buffer Size in PooledNioSctpChannel**

**Location**: `PooledNioSctpChannel.java` doReadMessages()

**Problem**: The read buffer size is determined by the RecvByteBufAllocator, which uses adaptive sizing based on previous reads. For consistent high-throughput scenarios, this may not be optimal.

**Current flow**:
```java
ByteBuf buffer = allocHandle.allocate(config().getAllocator());
```

**Recommendation**: For SCTP with known message sizes (typical MAP messages are 200-500 bytes), consider using a FixedRecvByteBufAllocator with an appropriately sized buffer:
```java
// In channel configuration or initializer
ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(8192));
```

**Issue 3: Output Buffer Re-allocation on Each Write**

**Location**: `PooledNioSctpChannel.java` doWriteMessage()

**Problem**: When the output buffer capacity is insufficient for the payload, a new buffer is allocated:
```java
if (outputBuf == null || outputBuf.capacity() < dataLen) {
    if (outputBuf != null) {
        outputBuf.release();
    }
    outputBuf = config().getAllocator().directBuffer(dataLen);
}
```

**Recommendation**: Implement buffer pooling for output buffers similar to input buffers, or use a pre-allocated buffer sized for maximum expected payload.

### 3.3 Channel Pipeline Optimization

**Issue 4: SctpMessageCompletionHandler May Cause Redundant Releases**

**Location**: `NettySctpClientChannelInitializer.java` and `NettySctpServerChannelInitializer.java`

**Problem**: The SctpMessageCompletionHandler is in the pipeline before FlushConsolidationHandler. This handler may interact with message lifecycle in ways that could cause redundant retain/release cycles.

**Current pipeline**:
```java
ch.pipeline().addLast(new SctpMessageCompletionHandler(),
    new FlushConsolidationHandler(),
    new NettySctpClientHandler(...));
```

**Recommendation**: Evaluate if SctpMessageCompletionHandler is necessary given that PooledNioSctpChannel manages its own buffer lifecycle. Consider removing it or repositioning in the pipeline.

### 3.4 Notification Handler Optimization

**Issue 5: SctpNotificationHandler Created Per Channel Instance**

**Location**: `PooledNioSctpChannel.java` constructor

**Problem**: A new SctpNotificationHandler is created for each channel instance:
```java
this.notificationHandler = new SctpNotificationHandler(this);
```

**Analysis**: While this is necessary for the inheritance chain to work correctly (Netty's notification handler requires the channel reference), the SctpNotificationHandler itself is lightweight. This is not a significant bottleneck.

**Recommendation**: No change needed. This is the correct pattern.

### 3.5 Write Buffer Watermark Configuration

**Issue 6: High Water Mark May Be Too Generous**

**Location**: `NettySctpClientChannelInitializer.java` and `NettySctpServerChannelInitializer.java`

**Problem**: WriteBufferHighWaterMark set to 64 MB may cause memory bloat under congestion. With multiple streams and 4 associations, worst-case pending data could reach 256 MB.

**Current configuration**:
```java
ch.config().setWriteBufferHighWaterMark(64 * 1024 * 1024); // 64 MB
ch.config().setWriteBufferLowWaterMark(32 * 1024 * 1024);  // 32 MB
```

**Recommendation**: For high-throughput scenarios, consider reducing to more aggressive watermarks:
```java
ch.config().setWriteBufferHighWaterMark(16 * 1024 * 1024);  // 16 MB
ch.config().setWriteBufferLowWaterMark(8 * 1024 * 1024);   // 8 MB
```

### 3.6 Netty AdaptiveByteBufAllocator Considerations

**Issue 7: Default Allocator May Exhibit Performance Variability**

**Reference**: Netty Issue #15292 (Performance issue in using Netty 4.2 with default AdaptiveByteBufAllocator)

**Problem**: The AdaptiveByteBufAllocator in Netty 4.2 shows significant CPU overhead compared to the pooled allocator. The issue reports ~400% CPU usage with adaptive vs ~220% with pooled for similar throughput.

**Current behavior**: The implementation uses `config().getAllocator()` which returns the channel's configured allocator. By default, Netty 4.2 uses AdaptiveByteBufAllocator.

**Recommendation**: Explicitly configure PooledByteBufAllocator for consistent performance:
```java
// In channel initializer
ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
```

**Note**: This is especially important if upgrading from Netty 4.1 to 4.2, as the default changed from pooled to adaptive.

### 3.7 SCTP Specific Optimizations

**Issue 8: SCTP_INIT_MAXSTREAMS Not Explicitly Configured**

**Location**: `NettyAssociationImpl.java` applySctpOptions() and `NettyServerImpl.java` applySctpOptions()

**Problem**: The SCTP_INIT_MAXSTREAMS option is set via getOptionSctpInitMaxstreams(), but if null (default), no explicit stream count is negotiated.

**Current code**:
```java
b.option(SctpChannelOption.SCTP_INIT_MAXSTREAMS, this.management.getOptionSctpInitMaxstreams());
```

**Recommendation**: Ensure SCTP_INIT_MAXSTREAMS is explicitly set for high-throughput scenarios. The changelog mentions 256 streams, but verify this is being applied:
```java
// Ensure this returns a non-null value
InitMaxStreams maxStreams = SctpStandardSocketOptions.InitMaxStreams.create(256, 256);
b.option(SctpChannelOption.SCTP_INIT_MAXSTREAMS, maxStreams);
```

### 3.8 Missing io_uring Transport

**Issue 9: Using NIO Instead of io_uring**

**Reference**: CHANGELOG mentions io_uring SCTP transport as future work

**Problem**: On Linux with kernel 5.x+, io_uring provides significant performance improvements over NIO for high-throughput scenarios.

**Analysis**: Netty 4.2 has preliminary io_uring support. However, SCTP over io_uring may not be fully implemented in Netty yet.

**Recommendation**: 
1. Monitor Netty's io_uring SCTP support
2. Consider JNI bindings to liburing + libsctp for maximum performance if Netty io_uring SCTP is not available

---

## 4. Specific Code Location Recommendations

### 4.1 PooledNioSctpChannel.java - doReadMessages()

**Lines 38-67**

**Recommended changes**:
1. Consider using FixedRecvByteBufAllocator for consistent buffer sizes
2. Add fast path for empty receive (when messageInfo is null)
3. Pre-calculate buffer positions to avoid redundant operations

### 4.2 PooledNioSctpChannel.java - doWriteMessage()

**Lines 69-99**

**Recommended changes**:
1. Implement output buffer pooling or pre-allocation
2. Add fast path when data is already direct and single-buffer
3. Consider caching the MessageInfo objects for common configurations

### 4.3 NettySctpChannelInboundHandlerAdapter.java - channelRead()

**Lines 170-210**

**Recommended changes**:
1. The current implementation retains ByteBuf twice (once in channelRead, once when creating PayloadData). Audit the retain/release cycle to ensure correct reference counting.
2. Consider using a faster path that bypasses ReferenceCountUtil for known buffer types.

### 4.4 NettySctpManagementImpl.java - start()

**Lines ~260-270**

**Recommended changes**:
1. Add dynamic thread count calculation based on association count
2. Consider using EpollEventLoopGroup on Linux for better scalability (if transport supports SCTP)

### 4.5 NettySctpClientChannelInitializer.java - initChannel()

**Lines 30-34**

**Recommended changes**:
1. Explicitly set PooledByteBufAllocator
2. Use FixedRecvByteBufAllocator(8192) for consistent read sizes
3. Tune write buffer watermarks based on deployment requirements

---

## 5. Implementation Priority Matrix

| Priority | Issue | Effort | Impact | Risk |
|----------|-------|--------|--------|------|
| High | Configure PooledByteBufAllocator explicitly | Low | High | Low |
| High | Set SCTP_INIT_MAXSTREAMS to 256 explicitly | Low | Medium | Low |
| Medium | Tune write buffer watermarks (64MB -> 16MB) | Low | Medium | Low |
| Medium | Implement output buffer pooling in PooledNioSctpChannel | Medium | Medium | Medium |
| Medium | Dynamic EventLoop thread scaling | Medium | Medium | Medium |
| Low | Investigate SctpMessageCompletionHandler necessity | Low | Low | Low |
| Low | io_uring transport evaluation | High | High | High |

---

## 6. Concrete Implementation Recommendations

### 6.1 Immediate Wins (Low Effort, High Impact)

**Recommendation 1: Explicit PooledByteBufAllocator**

In `NettySctpClientChannelInitializer.java` and `NettySctpServerChannelInitializer.java`, add:
```java
@Override
protected void initChannel(SctpChannel ch) throws Exception {
    // Use pooled allocator explicitly for consistent performance
    ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
    
    // Use fixed buffer size for consistent read performance
    ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(8192));
    
    // Tuned write buffers for high-throughput
    ch.config().setWriteBufferHighWaterMark(16 * 1024 * 1024);
    ch.config().setWriteBufferLowWaterMark(8 * 1024 * 1024);
    
    ch.pipeline().addLast(new SctpMessageCompletionHandler(),
        new FlushConsolidationHandler(),
        new NettySctpClientHandler(this.nettyAssociationImpl));
}
```

**Recommendation 2: Ensure SCTP_INIT_MAXSTREAMS is Set**

In `NettySctpManagementImpl.java`, ensure the default for high-throughput is applied:
```java
public InitMaxStreams getOptionSctpInitMaxstreams() {
    if (optionSctpInitMaxstreams_MaxInStreams != null && 
        optionSctpInitMaxstreams_MaxOutStreams != null) {
        return SctpStandardSocketOptions.InitMaxStreams.create(
            optionSctpInitMaxstreams_MaxInStreams,
            optionSctpInitMaxstreams_MaxOutStreams);
    } else {
        // Default to 256 streams for high-throughput scenarios
        return SctpStandardSocketOptions.InitMaxStreams.create(256, 256);
    }
}
```

### 6.2 Medium-Term Improvements

**Recommendation 3: Output Buffer Pooling in PooledNioSctpChannel**

Replace the current output buffer approach with true pooling:

```java
private static final ThreadLocal<ByteBuf> OUTPUT_BUFFER = 
    new ThreadLocal<ByteBuf>() {
        @Override
        protected ByteBuf initialValue() {
            return PooledByteBufAllocator.DEFAULT.directBuffer(8192);
        }
    };

@Override
protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
    SctpMessage packet = (SctpMessage) msg;
    ByteBuf data = packet.content();
    int dataLen = data.readableBytes();
    if (dataLen == 0) {
        return true;
    }

    ByteBuf outputBuf = OUTPUT_BUFFER.get();
    if (outputBuf.capacity() < dataLen) {
        outputBuf.capacity(dataLen);
    }
    outputBuf.clear();
    outputBuf.writeBytes(data);

    ByteBuffer nioData = outputBuf.internalNioBuffer(
        outputBuf.readerIndex(), outputBuf.readableBytes());
    
    final MessageInfo mi = MessageInfo.createOutgoing(
        association(), null, packet.streamIdentifier());
    mi.payloadProtocolID(packet.protocolIdentifier());
    mi.streamNumber(packet.streamIdentifier());
    mi.unordered(packet.isUnordered());

    return javaChannel().send(nioData, mi) > 0;
}
```

**Recommendation 4: Dynamic EventLoop Thread Scaling**

In `NettySctpManagementImpl.java`:
```java
private void calculateThreadCounts() {
    int cores = Runtime.getRuntime().availableProcessors();
    int assocCount = this.associations.size();
    
    // For SCTP, each association benefits from dedicated worker threads
    // Boss threads handle acceptance - 1 per server socket
    this.bossGroupThreadCount = Math.max(2, cores / 4);
    
    // Worker threads should scale with associations
    // but not exceed core count * 2
    this.workerGroupThreadCount = Math.min(cores * 2, assocCount * 2);
}
```

### 6.3 Advanced Optimizations (Future Work)

**Recommendation 5: Profile and Validate Before io_uring**

Before investing in io_uring transport:
1. Profile the current implementation with async-profiler on native Linux
2. Confirm that kernel SCTP is the bottleneck, not application logic
3. Validate that io_uring SCTP support exists in Netty

---

## 7. Validation Methodology

To validate these recommendations, the following testing methodology is recommended:

1. **Baseline Measurement**: Run the existing implementation on native Linux (not WSL2) with 4 associations and document TPS achieved.

2. **Incremental Changes**: Apply recommendations one at a time and measure impact:
   - First: PooledByteBufAllocator + FixedRecvByteBufAllocator
   - Second: SCTP_INIT_MAXSTREAMS = 256
   - Third: Write buffer watermark tuning
   - Fourth: Output buffer optimization

3. **Profiling Points**:
   - Use async-profiler with `-e malloc -f profile.html` to identify memory allocation hotspots
   - Use `-e perf-cycles -f flamegraph.html` to identify CPU hotspots
   - Monitor jemalloc stats with `MALLOC_CONF=stats_print:true`

4. **Metrics to Collect**:
   - Messages per second (TPS)
   - CPU utilization per core
   - Memory allocation rate
   - GC pause times
   - Kernel SCTP buffer utilization

---

## 8. Conclusion

The current netty-sctp implementation has achieved significant optimizations through pooled buffers, multi-association architecture, and tuned kernel parameters. The identified opportunities focus on:

1. **Explicit allocator configuration** to avoid AdaptiveByteBufAllocator overhead discovered in Netty 4.2
2. **Buffer size tuning** through FixedRecvByteBufAllocator and output buffer pooling
3. **SCTP stream configuration** ensuring 256 streams are negotiated
4. **Write buffer watermark reduction** to prevent memory bloat

The highest-priority recommendations are those with low effort and high impact: configuring PooledByteBufAllocator explicitly and ensuring SCTP_INIT_MAXSTREAMS is set. These changes should provide immediate performance improvements on native Linux without introducing significant risk.

The 10,000 SMS/s target appears achievable based on the current architectural improvements and these additional optimizations, assuming native Linux kernel SCTP does not introduce unexpected bottlenecks. The next validation step should be running the updated implementation on a native Linux VM with proper profiling to confirm the optimization impact.

---

## Sources

[1] [Netty NioSctpChannel API Documentation](https://netty.io/4.2/api/io/netty/channel/sctp/nio/NioSctpChannel.html) - High Reliability - Official Netty API documentation

[2] [Netty Issue #15292 - Performance issue with AdaptiveByteBufAllocator](https://github.com/netty/netty/issues/15292) - High Reliability - Official Netty issue tracker

[3] [CHANGELOG_SCTP_PERFORMANCE_TUNING.md](C:\Users\Windows\Desktop\ethiopia-working-dir\CHANGELOG_SCTP_PERFORMANCE_TUNING.md) - High Reliability - Project internal documentation

[4] [PooledNioSctpChannel.java](C:\Users\Windows\Desktop\ethiopia-working-dir\sctp\sctp-impl\src\main\java\org\mobicents\protocols\sctp\netty\PooledNioSctpChannel.java) - High Reliability - Primary source code

[5] [NettySctpManagementImpl.java](C:\Users\Windows\Desktop\ethiopia-working-dir\sctp\sctp-impl\src\main\java\org\mobicents\protocols\sctp\netty\NettySctpManagementImpl.java) - High Reliability - Primary source code

[6] [PayloadDataPool.java](C:\Users\Windows\Desktop\ethiopia-working-dir\sctp\sctp-api\src\main\java\org\mobicents\protocols\api\PayloadDataPool.java) - High Reliability - Primary source code

[7] [Netty Threading Model Documentation](https://medium.com/@nikolaykudinov/threads-in-netty-a-detailed-introduction-part-1-6218bc60a91a) - Medium Reliability - Community blog

[8] [Netty 4.2.0 Release Notes](https://netty.io/news/2025/04/03/4-2-0.html) - High Reliability - Official Netty release notes