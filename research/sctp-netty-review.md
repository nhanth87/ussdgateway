# Netty SCTP NIO Implementation Review

**Review Date:** 2026-04-07  
**Files Reviewed:**
- `NettyAssociationImpl.java`
- `NettySctpChannelInboundHandlerAdapter.java`
- `NettySctpClientHandler.java`
- `NettySctpServerHandler.java`

---

## Tổng Quan

Implementation Netty SCTP NIO của Restcomm jSS7 có cấu trúc cơ bản đúng, sử dụng async I/O và event-driven architecture. Tuy nhiên có **một số vấn đề nghiêm trọng** cần fix.

---

## 1. Send() Method Analysis

### Vị trí
`NettyAssociationImpl.java` (line 353-369)

### Code hiện tại
```java
@Override
public void send(PayloadData payloadData) throws Exception {
    if (logger.isDebugEnabled()) {
        logger.debug(String.format("Tx : Ass=%s %s", this.getName(), payloadData));
    }

    NettySctpChannelInboundHandlerAdapter handler = checkSocketIsOpen();

    final ByteBuf byteBuf = payloadData.getByteBuf();
    if (this.ipChannelType == IpChannelType.SCTP) {
        SctpMessage sctpMessage = new SctpMessage(payloadData.getPayloadProtocolId(), 
                payloadData.getStreamNumber(),
                payloadData.isUnordered(), byteBuf);
        handler.writeAndFlush(sctpMessage);
    } else {
        handler.writeAndFlush(byteBuf);
    }
}
```

### ✅ Đúng
1. **Sử dụng `writeAndFlush()`** - Đúng cho async I/O
2. **Tạo SctpMessage đúng** - Với protocolId, streamNumber, unordered flag
3. **Kiểm tra socket mở** - Qua `checkSocketIsOpen()`

### ⚠️ Vấn đề

| Vấn đề | Mức độ | Mô tả |
|--------|--------|-------|
| Không kiểm tra `isWritable()` | MEDIUM | Có thể gây OOM khi channel congested |
| Không có backpressure | MEDIUM | Không control flow từ application |
| Không release ByteBuf khi fail | LOW | Có thể gây leak trong một số case |

---

## 2. writeAndFlush() Method Analysis

### Vị trí
`NettySctpChannelInboundHandlerAdapter.java` (line 201-214)

### Code hiện tại
```java
protected void writeAndFlush(Object message) {
    Channel ch = this.channel;
    if (ch != null) {
        ChannelFuture future = ch.writeAndFlush(message);

        long curMillisec = System.currentTimeMillis();
        long secPart = curMillisec / 500;
        if (lastCongestionMonitorSecondPart < secPart) {
            lastCongestionMonitorSecondPart = secPart;
            CongestionMonitor congestionMonitor = new CongestionMonitor();
            future.addListener(congestionMonitor);
        }
    }
}
```

### ✅ Đúng
1. **Có congestion monitoring** - Tốt cho việc detect delay
2. **Kiểm tra channel != null** - Basic null check

### ⚠️ Vấn đề

| Vấn đề | Mức độ | Mô tả |
|--------|--------|-------|
| Không kiểm tra `ch.isActive()` | HIGH | Có thể write vào channel đã close |
| Không kiểm tra `ch.isWritable()` | HIGH | Có thể gây OOM khi channel congested |
| Không xử lý write fail | MEDIUM | Chỉ monitor congestion, không handle error |

### Khuyến nghị fix
```java
protected void writeAndFlush(Object message) {
    Channel ch = this.channel;
    if (ch != null && ch.isActive()) {
        // Check writability to prevent OOM
        if (!ch.isWritable()) {
            logger.warn("Channel not writable, possible congestion");
            // Option 1: Drop message
            // Option 2: Buffer và retry sau
            // Option 3: Block (nếu dùng sync mode)
        }
        
        ChannelFuture future = ch.writeAndFlush(message);
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        
        // Congestion monitoring...
    } else {
        // Release message nếu không write được
        if (message instanceof ReferenceCounted) {
            ((ReferenceCounted) message).release();
        }
    }
}
```

---

## 3. channelRead() Method Analysis - **VẤN ĐỀ NGHIÊM TRỌNG**

### Vị trí
`NettySctpChannelInboundHandlerAdapter.java` (line 178-199)

### Code hiện tại
```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    PayloadData payload;
    if (this.association.getIpChannelType() == IpChannelType.SCTP) {
        SctpMessage sctpMessage = (SctpMessage) msg;
        ByteBuf byteBuf = sctpMessage.content();
        payload = new PayloadData(byteBuf.readableBytes(), byteBuf, 
                sctpMessage.isComplete(), sctpMessage.isUnordered(),
                sctpMessage.protocolIdentifier(), sctpMessage.streamIdentifier());
    } else {
        ByteBuf byteBuf = (ByteBuf) msg;
        payload = new PayloadData(byteBuf.readableBytes(), byteBuf, true, false, 0, 0);
    }

    if (logger.isDebugEnabled()) {
        logger.debug(String.format("Rx : Ass=%s %s", this.association.getName(), payload));
    }

    this.association.read(payload);
    // } finally {
    //     ReferenceCountUtil.release(msg);
    // }
}
```

### ✅ Đúng
1. **Unwrap SctpMessage đúng** - Lấy content, protocolId, streamId
2. **Pass lên association layer** - Đúng flow

### 🔴 VẤN ĐỀ NGHIÊM TRỌNG

**MEMORY LEAK!** Không có `ReferenceCountUtil.release(msg)`

Netty sử dụng reference counting cho ByteBuf. Khi nhận message từ `channelRead()`, Netty tạo một ByteBuf với refCount = 1. Nếu không release, memory sẽ leak.

**Code bị comment out:**
```java
// } finally {
//     ReferenceCountUtil.release(msg);
// }
```

### Khuyến nghị fix
```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
        PayloadData payload;
        if (this.association.getIpChannelType() == IpChannelType.SCTP) {
            SctpMessage sctpMessage = (SctpMessage) msg;
            ByteBuf byteBuf = sctpMessage.content();
            // Tăng refCount vì PayloadData sẽ giữ reference
            byteBuf.retain();
            payload = new PayloadData(byteBuf.readableBytes(), byteBuf, 
                    sctpMessage.isComplete(), sctpMessage.isUnordered(),
                    sctpMessage.protocolIdentifier(), sctpMessage.streamIdentifier());
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            byteBuf.retain();
            payload = new PayloadData(byteBuf.readableBytes(), byteBuf, true, false, 0, 0);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx : Ass=%s %s", this.association.getName(), payload));
        }

        this.association.read(payload);
    } finally {
        // Luôn release message gốc từ Netty
        ReferenceCountUtil.release(msg);
    }
}
```

**Lưu ý:** PayloadData cần gọi `byteBuf.release()` khi xử lý xong, hoặc implement `AutoCloseable`.

---

## 4. Các Vấn Đề Khác

### 4.1 Exception Handling
Trong `exceptionCaught()` chỉ log và close, không có retry logic:
```java
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Exception caught for Association: " + this.association.getName() + "\n", cause);
    ctx.close();
}
```

### 4.2 Channel Unregistered
Trong `channelUnregistered()` có gọi `scheduleConnect()` cho client - tốt cho auto-reconnect.

### 4.3 Association State Management
Có `markAssociationUp()` và `markAssociationDown()` - đúng pattern.

---

## 5. So Sánh với Best Practices

| Best Practice | Status | Note |
|--------------|--------|------|
| Reference counting | ❌ FAIL | Memory leak |
| Writability check | ❌ FAIL | No backpressure |
| Async write | ✅ PASS | Đúng pattern |
| Congestion monitoring | ✅ PASS | Có implementation |
| Auto reconnect | ✅ PASS | Client có retry |
| Proper exception handling | ⚠️ PARTIAL | Chưa có retry strategy |

---

## 6. Khuyến Nghị Fix Priority

### 🔴 HIGH (Fix ngay)
1. **Memory leak trong channelRead()** - Thêm `ReferenceCountUtil.release(msg)`
2. **Thêm `isActive()` check** trong writeAndFlush()

### 🟡 MEDIUM (Fix sau)
3. **Thêm `isWritable()` check** cho backpressure
4. **Xử lý write failure** - Listener để log/handle error

### 🟢 LOW (Cải thiện)
5. **Tối ưu congestion monitoring** - Giảm tần suất check
6. **Thêm metrics** - Byte in/out, error rate

---

## 7. Code Fix Reference

### Fix 1: channelRead() với proper reference counting
```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
        PayloadData payload = decodeMessage(msg);
        if (payload != null) {
            this.association.read(payload);
        }
    } finally {
        ReferenceCountUtil.release(msg);
    }
}

private PayloadData decodeMessage(Object msg) {
    if (this.association.getIpChannelType() == IpChannelType.SCTP) {
        SctpMessage sctpMessage = (SctpMessage) msg;
        ByteBuf byteBuf = sctpMessage.content();
        byteBuf.retain(); // Tăng refCount
        return new PayloadData(byteBuf.readableBytes(), byteBuf, 
                sctpMessage.isComplete(), sctpMessage.isUnordered(),
                sctpMessage.protocolIdentifier(), sctpMessage.streamIdentifier());
    } else {
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.retain();
        return new PayloadData(byteBuf.readableBytes(), byteBuf, true, false, 0, 0);
    }
}
```

### Fix 2: writeAndFlush() với safety checks
```java
protected void writeAndFlush(Object message) {
    Channel ch = this.channel;
    if (ch == null || !ch.isActive()) {
        ReferenceCountUtil.release(message);
        return;
    }
    
    if (!ch.isWritable()) {
        logger.warn("Channel not writable for Association={}", association.getName());
        // Option: buffer hoặc drop
    }
    
    ChannelFuture future = ch.writeAndFlush(message);
    future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    
    // Congestion monitoring...
}
```

---

## Kết Luận

Implementation Netty SCTP NIO có cấu trúc đúng nhưng có **memory leak nghiêm trọng** trong `channelRead()` và thiếu **safety checks** trong `writeAndFlush()`. Cần fix ngay để tránh OOM trong production.

| Priority | Issue | File | Line |
|----------|-------|------|------|
| 🔴 HIGH | Memory leak | NettySctpChannelInboundHandlerAdapter | 178-199 |
| 🔴 HIGH | Missing isActive() check | NettySctpChannelInboundHandlerAdapter | 201-214 |
| 🟡 MEDIUM | Missing isWritable() check | NettySctpChannelInboundHandlerAdapter | 201-214 |
