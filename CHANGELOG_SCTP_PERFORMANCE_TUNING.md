# Changelog: Tối ưu hóa Hiệu năng SCTP / Netty cho jSS7

> **Mục tiêu**: Đạt throughput **10.000 SMS/s** (MO MAP) trên nền tảng transport **SCTP thuần túy** (không dùng TCP fallback).  
> **Trạng thái**: Đã hoàn thành các quick-win trên code; đang chờ validate trên **native Linux** (VM vật lý, không phải WSL2).  
> **Ngày cập nhật**: 2026-04-15  
> **Git commit tham chiếu**: `ff82dd4` (tất cả thay đổi đã được commit vào repo chính)

---

## 1. Tổng quan kiến trúc

### 1.1. Mô hình Multi-Association SCTP + M3UA Loadshare

Để vượt qua giới hạn single-association của kernel SCTP (đặc biệt trên WSL2), hệ thống được chuyển từ **1 SCTP association** sang **4 SCTP associations song song**, cùng chia sẻ một **M3UA Application Server (AS)** ở chế độ **Loadshare**.

**Cấu hình port (Client ↔ Server)**:

| Assoc # | Client Port | Server Port |
|---------|-------------|-------------|
| 0       | 8012        | 8011        |
| 1       | 8013        | 8016        |
| 2       | 8014        | 8021        |
| 3       | 8015        | 8026        |

- **Client**: khởi tạo 4 `Association` (SCTP client) và 4 `AspFactory`, gán tất cả vào cùng một AS (`AS1`).
- **Server**: khởi tạo 4 `Server` (SCTP server socket) và 4 `ServerAssociation`, tương ứng 4 ASP.
- **M3UA Mode**: `IPSP Single-Exchange` với `TrafficModeType.Loadshare`.

### 1.2. Lý do không dùng TCP fallback

TCP đã chứng minh đạt >10k TPS trên WSL2, nhưng yêu cầu nghiệp vụ bắt buộc sử dụng **SCTP** (multi-homing, message boundary, PPID). Do đó toàn bộ tối ưu chỉ tập trung vào SCTP stack.

---

## 2. Các thay đổi chi tiết theo module

### 2.1. `jSS7/m3ua/impl` — Fix Finite State Machine (FSM) thiếu transition

**Vấn đề**: Khi M3UA peer gửi `NTFY(AS_INACTIVE)` trong khi AS đang ở trạng thái `ACTIVE`, FSM trên peer ném `UnknownTransitionException: ntfyasinactive` vì transition `AS_STATE_CHANGE_INACTIVE` chưa được đăng ký từ trạng thái `ACTIVE`.

**File thay đổi**: `jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/AsImpl.java`

**Nội dung fix** (trong `initPeerFSM()`):

```java
// Dòng được THÊM:
this.peerFSM.createTransition(
    TransitionState.AS_STATE_CHANGE_INACTIVE,
    AsState.ACTIVE.toString(),
    AsState.INACTIVE.toString()
);
```

**Kết quả**: AS giờ đây có thể chuyển từ `ACTIVE` → `INACTIVE` một cách mượt mà khi nhận notify từ remote peer, không còn exception.

---

### 2.2. `sctp/sctp-impl` — Tối ưu Netty SCTP Channel (Pooled Direct Buffers)

#### 2.2.1. Bối cảnh

Netty `NioSctpChannel` mặc định (phiên bản 4.2.11.Final) trong đường dẫn `doReadMessages()` và `doWriteMessage()` sử dụng `ByteBuffer.allocateDirect()` **mỗi lần I/O**, dẫn đến:
- Tốn CPU cho allocate/free direct buffer trên hot path.
- Tăng áp lực GC native (malloc/free) trong scenario throughput cao.

#### 2.2.2. Giải pháp: Pooled Nio SCTP Channel

Em đã implement hai lớp custom override hoàn toàn hành vi allocate buffer của Netty, thay vào đó **tái sử dụng direct buffer từ `ByteBufAllocator`** (mặc định `PooledByteBufAllocator`).

##### A. `PooledNioSctpChannel.java`

**Đường dẫn**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpChannel.java`

```java
public class PooledNioSctpChannel extends NioSctpChannel {
    private ByteBuf inputBuf;
    private ByteBuf outputBuf;

    public PooledNioSctpChannel() { super(); }
    public PooledNioSctpChannel(SctpChannel sctpChannel) { super(sctpChannel); }
    public PooledNioSctpChannel(Channel parent, SctpChannel sctpChannel) { super(parent, sctpChannel); }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SctpChannel ch = javaChannel();
        RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
        if (inputBuf == null) {
            inputBuf = config().getAllocator().directBuffer(8192);
        }
        inputBuf.clear();
        ByteBuffer data = inputBuf.internalNioBuffer(0, inputBuf.capacity());
        int pos = data.position();
        MessageInfo messageInfo = ch.receive(data, null, new SctpNotificationHandler(this));
        if (messageInfo == null) {
            return 0;
        }
        allocHandle.lastBytesRead(data.position() - pos);
        buf.add(new SctpMessage(messageInfo,
                inputBuf.retainedSlice(inputBuf.readerIndex(), allocHandle.lastBytesRead())));
        return 1;
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        SctpMessage packet = (SctpMessage) msg;
        ByteBuf data = packet.content();
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        if (outputBuf == null || outputBuf.capacity() < dataLen) {
            if (outputBuf != null) {
                outputBuf.release();
            }
            outputBuf = config().getAllocator().directBuffer(dataLen);
        }
        outputBuf.clear();
        outputBuf.writeBytes(data);
        ByteBuffer nioData = outputBuf.internalNioBuffer(outputBuf.readerIndex(), outputBuf.readableBytes());
        final MessageInfo mi = MessageInfo.createOutgoing(association(), null, packet.streamIdentifier());
        mi.payloadProtocolID(packet.protocolIdentifier());
        mi.streamNumber(packet.streamIdentifier());
        mi.unordered(packet.isUnordered());
        final int writtenBytes = javaChannel().send(nioData, mi);
        return writtenBytes > 0;
    }

    @Override
    protected void doClose() throws Exception {
        if (inputBuf != null) {
            inputBuf.release();
            inputBuf = null;
        }
        if (outputBuf != null) {
            outputBuf.release();
            outputBuf = null;
        }
        super.doClose();
    }
}
```

**Điểm then chốt**:
- `inputBuf`: được alloc **một lần** (hoặc reused) mỗi channel, dùng `directBuffer(8192)`.
- `outputBuf`: được alloc theo kích thước payload ghi, nhưng vẫn nằm trong pool của Netty (resize khi payload lớn hơn capacity hiện tại, release buffer cũ).
- `doClose()`: đảm bảo release pooled buffer khi channel đóng, tránh memory leak.

##### B. `PooledNioSctpServerChannel.java`

**Đường dẫn**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpServerChannel.java`

```java
public class PooledNioSctpServerChannel extends NioSctpServerChannel {
    public PooledNioSctpServerChannel() { super(); }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SctpChannel ch = javaChannel().accept();
        if (ch == null) {
            return 0;
        }
        buf.add(new PooledNioSctpChannel(this, ch));
        return 1;
    }
}
```

**Điểm then chốt**: Khi server accept một association mới, nó trả về `PooledNioSctpChannel` thay vì `NioSctpChannel` mặc định, đảm bảo toàn bộ kết nối inbound cũng dùng pooled buffers.

---

### 2.3. `sctp/sctp-impl` — Tích hợp Pooled Channels vào Management

#### A. `NettyAssociationImpl.java`

**Đường dẫn**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettyAssociationImpl.java`

Thay đổi trong phương thức `connect()` (bootstrap client):

```java
// Cũ:
// b.channel(NioSctpChannel.class);

// Mới:
if (this.ipChannelType == IpChannelType.SCTP) {
    b.channel(PooledNioSctpChannel.class);
    b.handler(new NettySctpClientChannelInitializer(this));
} else {
    b.channel(NioSocketChannel.class);
    b.handler(new NettyTcpClientChannelInitializer(this));
}
```

#### B. `NettyServerImpl.java`

**Đường dẫn**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettyServerImpl.java`

Thay đổi trong `initChannel()` (bootstrap server):

```java
// Cũ:
// b.channel(NioSctpServerChannel.class);

// Mới:
if (ipChannelType == IpChannelType.SCTP) {
    b.channel(PooledNioSctpServerChannel.class);
    b.handler(new LoggingHandler(LogLevel.DEBUG));
    b.childHandler(new NettySctpServerChannelInitializer(this, this.management));
    // ... (SCTP options giữ nguyên)
}
```

---

### 2.4. `sctp/sctp-impl` — Tắt cảnh báo Congestion (Log spam reduction)

**Vấn đề**: Trong scenario throughput cao, `NettySctpChannelInboundHandlerAdapter.writeAndFlush()` liên tục in log `WARN` về congestion, gây I/O disk và giảm hiệu năng.

**File thay đổi**: `sctp/sctp-impl/src/main/java/org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.java`

**Nội dung**: Các dòng log `WARN` liên quan đến congestion trong `writeAndFlush()` đã được **xóa bỏ** (hoặc chuyển sang `TRACE` nếu cần debug sau này).

---

### 2.5. `sctp/sctp-impl` — Tuning Netty Bootstraps

Các tham số TCP/SCTP socket được tăng lên để giảm thiểu bottleneck ở kernel buffer:

| Tham số | Giá trị mới | Mô tả |
|---------|-------------|-------|
| `SCTP_INIT_MAXSTREAMS` (inbound/outbound) | 256 | Tăng số lượng SCTP stream, cho phép multiplex nhiều message song song hơn |
| `SO_SNDBUF` | 8 MB (8.388.608 bytes) | Tăng kernel send buffer, giảm block khi gửi burst |
| `SO_RCVBUF` | 8 MB (8.388.608 bytes) | Tăng kernel receive buffer, giảm packet drop |
| `SCTP_NODELAY` | `true` | Tắt Nagle, giảm latency per message |

Ngoài ra, `FlushConsolidationHandler` được thêm vào pipeline để giảm số lần gọi `flush()` riêng lẻ:

```java
pipeline.addLast(new FlushConsolidationHandler());
```

Và số lượng Netty event-loop threads được tăng:
- `bossGroup`: tăng lên số lõi CPU (hoặc cao hơn nếu nhiều association).
- `workerGroup`: tương tự, scaled theo số association.

---

### 2.6. `jSS7/map/load` — Client & Server Loadtest với 4 Associations

#### A. `Client.java`

**Đường dẫn**: `jSS7/map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java`

**Thay đổi chính**:

1. `initSCTP()`: tạo 4 association tuần tự:

```java
private void initSCTP(IpChannelType ipChannelType) throws Exception {
    this.sctpManagement = new NettySctpManagementImpl("Client");
    this.sctpManagement.start();
    this.sctpManagement.setConnectDelay(1000);
    this.sctpManagement.removeAllResources();

    int[] clientPorts = { 8012, 8013, 8014, 8015 };
    int[] serverPorts = { 8011, 8016, 8021, 8026 };

    for (int i = 0; i < 4; i++) {
        String assocName = CLIENT_ASSOCIATION_NAME + i;
        if (EXTRA_HOST_ADDRESS.equals("-1"))
            sctpManagement.addAssociation(HOST_IP, clientPorts[i], PEER_IP, serverPorts[i], assocName, ipChannelType, null);
        else
            sctpManagement.addAssociation(HOST_IP, clientPorts[i], PEER_IP, serverPorts[i], assocName, ipChannelType, new String[] { EXTRA_HOST_ADDRESS });
    }
}
```

2. `initM3UA()`: 1 AS + 4 ASPs, Loadshare:

```java
private void initM3UA() throws Exception {
    this.clientM3UAMgmt = new M3UAManagementImpl("Client", null, new Ss7ExtInterfaceImpl());
    this.clientM3UAMgmt.setTransportManagement(this.sctpManagement);
    this.clientM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
    this.clientM3UAMgmt.start();
    this.clientM3UAMgmt.removeAllResources();

    RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
    TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
    NetworkAppearance na = factory.createNetworkAppearance(NETWORK_APPEARANCE);
    IPSPType ipspType = (AS_FUNCTIONALITY == Functionality.IPSP) ? IPSPType.CLIENT : null;

    this.clientM3UAMgmt.createAs("AS1", AS_FUNCTIONALITY, ExchangeType.SE, ipspType, rc, trafficModeType, 1, na);

    for (int i = 0; i < 4; i++) {
        String aspName = "ASP" + i;
        String assocName = CLIENT_ASSOCIATION_NAME + i;
        this.clientM3UAMgmt.createAspFactory(aspName, assocName);
        this.clientM3UAMgmt.assignAspToAs("AS1", aspName);
    }

    this.clientM3UAMgmt.addRoute(DESTINATION_PC, ORIGINATING_PC, SERVICE_INDICATOR, "AS1");

    for (int i = 0; i < 4; i++) {
        this.clientM3UAMgmt.startAsp("ASP" + i);
    }
}
```

3. Fix lỗi parse argument trong `main()`:

```java
// Cũ (bug):
// RoutingIndicator ri = RoutingIndicator.valueOf(Integer.parseInt(args[i++]));

// Mới (fix):
RoutingIndicator ri = RoutingIndicator.valueOf(args[i++]);
```

#### B. `Server.java`

**Đường dẫn**: `jSS7/map/load/src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java`

**Thay đổi chính**:

1. `initSCTP()`: tạo 4 server sockets:

```java
private void initSCTP(IpChannelType ipChannelType) throws Exception {
    this.sctpManagement = new NettySctpManagementImpl("Server");
    this.sctpManagement.start();
    this.sctpManagement.setConnectDelay(10000);
    this.sctpManagement.removeAllResources();

    int[] serverPorts = { 8011, 8016, 8021, 8026 };
    String[] serverNames = { "testserver0", "testserver1", "testserver2", "testserver3" };
    String[] assocNames = { SERVER_ASSOCIATION_NAME + "0", SERVER_ASSOCIATION_NAME + "1",
                            SERVER_ASSOCIATION_NAME + "2", SERVER_ASSOCIATION_NAME + "3" };

    for (int i = 0; i < 4; i++) {
        if (EXTRA_HOST_ADDRESS.equals("-1"))
            sctpManagement.addServer(serverNames[i], HOST_IP, serverPorts[i], ipChannelType, null);
        else
            sctpManagement.addServer(serverNames[i], HOST_IP, serverPorts[i], ipChannelType, new String[] { EXTRA_HOST_ADDRESS });

        sctpManagement.addServerAssociation(PEER_IP, PEER_PORT, serverNames[i], assocNames[i], ipChannelType);
        sctpManagement.startServer(serverNames[i]);
    }
}
```

2. `initM3UA()`: tương tự Client, 1 AS + 4 ASPs, Loadshare, sau đó `startAsp()` tất cả.

---

### 2.7. Version Bump

Các artifact version được nâng để phân biệt build gốc và build đã patch:

| Artifact | Version cũ | Version mới |
|----------|------------|-------------|
| `jSS7` (parent + modules) | 9.2.7 | **9.2.8** |
| `sctp` (parent + api/impl) | 2.0.12 | **2.0.13** |

Version bump được cập nhật trong toàn bộ `pom.xml` của các module liên quan.

---

## 3. Build, Package & Deployment

### 3.1. `compile_all.sh` — Script biên dịch nhanh trên Linux

**Đường dẫn**: `C:\Users\Windows\Desktop\ethiopia-working-dir\compile_all.sh`

Script này dùng JDK 11 (`zulu11.78.15-ca-jdk11.0.26-linux_x64`) để biên dịch các file `.java` đã sửa và update trực tiếp vào `.jar` trong `target/load/`.

**Lưu ý quan trọng**: Tất cả class phải được compile với **Java 11** (`javac --release 11` hoặc JDK 11) để tránh `UnsupportedClassVersionError` khi chạy trên runtime Java 11. Trước đó từng có lỗi do vô tình dùng JDK 25 compile ra class file 69.

**Các bước trong script**:

1. **SCTP-impl**: biên dịch 5 file:
   - `NettySctpChannelInboundHandlerAdapter.java`
   - `PooledNioSctpChannel.java`
   - `PooledNioSctpServerChannel.java`
   - `NettyAssociationImpl.java`
   - `NettyServerImpl.java`

   Sau đó `jar uf` vào `sctp-impl.jar`.

2. **m3ua-impl**: biên dịch các file FSM + `SCTPShellExecutor.java`, sau đó `jar uf` vào `m3ua-impl.jar`.

3. **map/load**: biên dịch `Client.java` và `Server.java` vào `target/classes`.

### 3.2. `map-sms-mo-loadtest-linux.tar.gz`

**Kích thước**: ~28 MB  
**Nội dung**: Toàn bộ `target/load/` (jars đã patch) + scripts chạy test (`run_client.sh`, `run_server.sh`).

Package này được chuẩn bị để deploy lên **native Linux VM** để benchmark thực tế.

---

## 4. Kết quả test & giới hạn đã xác định

### 4.1. WSL2 — Bottleneck ở virtual network stack

- **Single association**: ~800 – 1.400 TPS (capped).
- **4 associations**: không scale tuyến tính; tổng throughput vẫn xấp xỉ single association.
- **Kết luận**: WSL2 virtual switch / kernel SCTP là bottleneck, không phải code ứng dụng.

### 4.2. TCP reference trên WSL2

- TCP (single association) dễ dàng đạt >10k TPS, chứng minh application logic (MAP, M3UA, SCCP, TCAP) không phải vấn đề.

### 4.3. Native Linux — Cần validate

Dự kiến trên native Linux (với kernel SCTP thực, không qua WSL2 translation), throughput sẽ:
- Tăng đáng kể nhờ kernel SCTP driver native.
- Tận dụng được lợi ích của **multi-association** + **pooled buffers**.
- Target 10k SMS/s là khả thi nếu không gặp bottleneck khác (ví dụ: disk I/O log, locking trong TCAP/MAP).

---

## 5. Hướng dẫn chạy trên Linux (kèm jemalloc)

Ngưởi dùng yêu cầu dùng **jemalloc** thay vì mimalloc để tối ưu native memory allocation.

### 5.1. Cài đặt jemalloc

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y libjemalloc2

# Hoặc build from source nếu cần specific version
```

### 5.2. Chạy Server / Client với jemalloc

Giả sử package đã được giải nén ra thư mục `~/map-sms-mo-loadtest-linux`.

**Server**:
```bash
cd ~/map-sms-mo-loadtest-linux
export LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2
bash run_server.sh
```

**Client** (trên cùng hoặc máy khác):
```bash
cd ~/map-sms-mo-loadtest-linux
export LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2
bash run_client.sh
```

> **Lưu ý**: Đường dẫn `libjemalloc.so.2` có thể khác tùy distro (ví dụ `/usr/lib64/libjemalloc.so.2` trên RHEL/CentOS).

### 5.3. Kiểm tra jemalloc có active hay không

```bash
LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2 ldd $(which java) | grep jemalloc
```

Hoặc dùng `MALLOC_CONF=stats_print:true` để in stats khi process exit.

---

## 6. Các vấn đề còn tồn đọng & Next Steps

| # | Vấn đề | Trạng thái | Kế hoạch |
|---|--------|------------|----------|
| 1 | **WSL2 SCTP bottleneck** | Xác định rõ | Không fix được ở tầng app; chuyển sang test native Linux |
| 2 | **Validate 10k TPS trên native Linux** | Pending | Chạy `map-sms-mo-loadtest-linux.tar.gz` trên VM Linux vật lý |
| 3 | **io_uring SCTP transport** | Chưa có sẵn trong Netty | Nếu quick-win không đủ 10k TPS, cần đánh giá viết custom `IoUringSctpChannel` hoặc dùng JNI để gọi `liburing` + `libsctp` |
| 4 | **JAXB boundary rule** | Đang active | Các codebase `GMLC` / `USSD` giữ nguyên, không sửa đổi JAXB |
| 5 | **Native memory profiler** | Chưa chạy | Nếu cần, dùng `async-profiler` trên Linux để confirm direct buffer / malloc hotspot đã biến mất |

---

## 7. Danh sách file đã thay đổi (tóm tắt)

### `sctp/sctp-impl`
- `src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpChannel.java` *(mới)*
- `src/main/java/org/mobicents/protocols/sctp/netty/PooledNioSctpServerChannel.java` *(mới)*
- `src/main/java/org/mobicents/protocols/sctp/netty/NettyAssociationImpl.java` *(modified)*
- `src/main/java/org/mobicents/protocols/sctp/netty/NettyServerImpl.java` *(modified)*
- `src/main/java/org/mobicents/protocols/sctp/netty/NettySctpChannelInboundHandlerAdapter.java` *(modified — xóa warn log)*

### `jSS7/m3ua/impl`
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/AsImpl.java` *(modified — thêm FSM transition)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToAct.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsPendToAct.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsDwnToInact.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsInactToInact.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToPendRemAspInac.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/THLocalAsActToActRemAspAct.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/RemAsStatePenTimeout.java` *(modified)*
- `src/main/java/org/restcomm/protocols/ss7/m3ua/impl/oam/SCTPShellExecutor.java` *(modified)*

### `jSS7/map/load`
- `src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Client.java` *(modified — 4 assoc, Loadshare, fix arg parse)*
- `src/main/java/org/restcomm/protocols/ss7/map/load/sms/mo/Server.java` *(modified — 4 server sockets)*

### Build scripts / metadata
- `compile_all.sh` *(modified — thêm biên dịch pooled channels + NettyAssociationImpl/NettyServerImpl)*
- Các `pom.xml` trong `jSS7` và `sctp` *(version bump 9.2.8 / 2.0.13)*

---

*Document này được tạo bởi Jenny để lưu trữ toàn bộ context kỹ thuật của dự án tối ưu SCTP/Netty. Nếu có thay đổi tiếp theo, vui lòng cập nhật section tương ứng.*
