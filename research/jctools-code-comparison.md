# JCTools Code Migration Examples

## 1. PayloadDataPool - Object Pooling

### Javolution (Current)
```java
import javolution.util.FastList;

public class PayloadDataPool {
    private static final int DEFAULT_POOL_SIZE = 100_000;
    private final FastList<PayloadData> pool = new FastList<>();
    private final AtomicLong acquiredCount = new AtomicLong(0);
    private final AtomicLong releasedCount = new AtomicLong(0);
    
    public PayloadData acquire() {
        PayloadData data = pool.pollFirst();  // Remove from front (FIFO)
        acquiredCount.incrementAndGet();
        return data != null ? data.reset() : new PayloadData();
    }
    
    public void release(PayloadData data) {
        if (data != null && data.isPooled()) {
            data.clear();
            pool.addLast(data);  // Add to end
            releasedCount.incrementAndGet();
        }
    }
}
```

### JCTools (Target)
```java
import org.jctools.queues.MpscArrayQueue;

public class PayloadDataPool {
    private static final int DEFAULT_POOL_SIZE = 100_000;
    private final MpscArrayQueue<PayloadData> pool;
    private final AtomicLong acquiredCount = new AtomicLong(0);
    private final AtomicLong releasedCount = new AtomicLong(0);
    
    public PayloadDataPool() {
        // Round up to power of 2 for efficiency
        int size = 1;
        while (size < DEFAULT_POOL_SIZE) size <<= 1;
        this.pool = new MpscArrayQueue<>(size);
    }
    
    public PayloadData acquire() {
        PayloadData data = pool.relaxedPoll();  // Lock-free poll
        acquiredCount.incrementAndGet();
        return data != null ? data.reset() : new PayloadData();
    }
    
    public void release(PayloadData data) {
        if (data != null && data.isPooled()) {
            data.clear();
            pool.relaxedOffer(data);  // Lock-free offer
            releasedCount.incrementAndGet();
        }
    }
}
```

---

## 2. ManagementImpl - Association Storage

### Javolution (Current)
```java
import javolution.util.FastList;
import javolution.util.FastMap;

public class ManagementImpl implements Management {
    protected FastList<Server> servers = new FastList<Server>();
    protected FastMap<String, Association> associations = new FastMap<String, Association>();
    
    @Override
    public List<Server> getServers() {
        return new FastList<Server>(servers);  // Defensive copy
    }
    
    @Override
    public Map<String, Association> getAssociations() {
        return new FastMap<String, Association>(associations);
    }
    
    public void addServer(Server server) {
        servers.add(server);
        associations.put(server.getName(), server);
    }
}
```

### JCTools (Target)
```java
import org.jctools.maps.NonBlockingHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManagementImpl implements Management {
    // CopyOnWriteArrayList: Read-heavy, rare writes, thread-safe iteration
    protected final CopyOnWriteArrayList<Server> servers = new CopyOnWriteArrayList<>();
    // NonBlockingHashMap: Lock-free, high concurrency
    protected final NonBlockingHashMap<String, Association> associations = new NonBlockingHashMap<>();
    
    @Override
    public List<Server> getServers() {
        return new ArrayList<>(servers);  // Defensive copy from snapshot
    }
    
    @Override
    public Map<String, Association> getAssociations() {
        return new HashMap<>(associations);  // Defensive copy
    }
    
    public void addServer(Server server) {
        servers.add(server);  // Copy-on-write, thread-safe
        associations.put(server.getName(), server);  // Lock-free
    }
}
```

---

## 3. ChangeRequest Queue - Pending Changes

### Javolution (Current)
```java
private FastList<ChangeRequest> pendingChanges = new FastList<ChangeRequest>();

// In selector loop
ChangeRequest changeRequest = pendingChanges.pollFirst();
while (changeRequest != null) {
    processChange(changeRequest);
    changeRequest = pendingChanges.pollFirst();
}
```

### JCTools (Target)
```java
import org.jctools.queues.SpscArrayQueue;

// Single Producer (management thread), Single Consumer (selector thread)
private final SpscArrayQueue<ChangeRequest> pendingChanges = new SpscArrayQueue<>(1024);

// In selector loop
ChangeRequest changeRequest = pendingChanges.relaxedPoll();
while (changeRequest != null) {
    processChange(changeRequest);
    changeRequest = pendingChanges.relaxedPoll();
}
```

---

## 4. SelectorThread - Peer Addresses

### Javolution (Current)
```java
import javolution.util.FastSet;

public class SelectorThread implements Runnable {
    protected FastSet<SocketAddress> peerAddresses = new FastSet<SocketAddress>();
    
    public void addPeerAddress(SocketAddress address) {
        peerAddresses.add(address);
    }
    
    public boolean hasPeerAddress(SocketAddress address) {
        return peerAddresses.contains(address);
    }
}
```

### JCTools (Target)
```java
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SelectorThread implements Runnable {
    // ConcurrentHashMap.newKeySet(): Backed by CHM, efficient concurrent operations
    protected final Set<SocketAddress> peerAddresses = ConcurrentHashMap.newKeySet();
    
    public void addPeerAddress(SocketAddress address) {
        peerAddresses.add(address);
    }
    
    public boolean hasPeerAddress(SocketAddress address) {
        return peerAddresses.contains(address);
    }
}
```

---

## 5. XML Serialization - XStream Migration

### Javolution (Current) - AssociationImpl.java
```java
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class AssociationImpl implements Association {
    protected static final XMLFormat<AssociationImpl> ASSOCIATION_XML = 
        new XMLFormat<AssociationImpl>(AssociationImpl.class) {
        
        public void read(javolution.xml.XMLFormat.InputElement xml, AssociationImpl association) 
            throws XMLStreamException {
            association.name = xml.get("name");
            association.hostAddress = xml.get("hostAddress");
            association.hostPort = xml.get("hostPort");
        }
        
        public void write(AssociationImpl association, javolution.xml.XMLFormat.OutputElement xml) 
            throws XMLStreamException {
            xml.setAttribute("name", association.name);
            xml.setAttribute("hostAddress", association.hostAddress);
            xml.setAttribute("hostPort", association.hostPort);
        }
    };
}
```

### XStream (Target) - AssociationImpl.java
```java
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("association")
public class AssociationImpl implements Association {
    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private String hostAddress;
    @XStreamAsAttribute
    private int hostPort;
    
    // No manual XML methods needed - XStream handles via reflection
}
```

### XStream Serialization Helper
```java
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SctpXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        xstream.processAnnotations(AssociationImpl.class);
        xstream.processAnnotations(ServerImpl.class);
        xstream.processAnnotations(AssociationMap.class);
        xstream.alias("associationMap", AssociationMap.class);
    }
    
    public static String toXML(Object obj) {
        return xstream.toXML(obj);
    }
    
    public static <T> T fromXML(String xml) {
        return (T) xstream.fromXML(xml);
    }
    
    public static void toXMLFile(Object obj, String filename) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            xstream.toXML(obj, writer);
        }
    }
    
    public static <T> T fromXMLFile(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            return (T) xstream.fromXML(reader);
        }
    }
}
```

---

## 6. Complete ManagementImpl Migration Example

### Javolution (Current)
```java
package org.mobicents.protocols.sctp;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLBinding;

public class ManagementImpl implements Management {
    protected FastList<Server> servers = new FastList<Server>();
    protected FastMap<String, Association> associations = new FastMap<String, Association>();
    protected FastList<ChangeRequest> pendingChanges = new FastList<ChangeRequest>();
    protected static final SctpXMLBinding binding = new SctpXMLBinding();
    
    public void load(String persistDir) throws FileNotFoundException {
        XMLObjectReader reader = XMLObjectReader.newInstance(
            new FileInputStream(persistDir + File.separator + "SctpManagement.xml"));
        reader.setBinding(binding);
        associations = reader.read("associations", FastMap.class);
        servers = reader.read("servers", FastList.class);
        reader.close();
    }
    
    public void store(String persistDir) throws Exception {
        XMLObjectWriter writer = XMLObjectWriter.newInstance(
            new FileOutputStream(persistDir + File.separator + "SctpManagement.xml"));
        writer.setBinding(binding);
        writer.write(associations, "associations", FastMap.class);
        writer.write(servers, "servers", FastList.class);
        writer.close();
    }
}
```

### JCTools + XStream (Target)
```java
package org.mobicents.protocols.sctp;

import org.jctools.maps.NonBlockingHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.thoughtworks.xstream.XStream;

public class ManagementImpl implements Management {
    // Lock-free, high-concurrency map
    protected final NonBlockingHashMap<String, Association> associations = new NonBlockingHashMap<>();
    // Optimized for read-heavy, rare writes
    protected final CopyOnWriteArrayList<Server> servers = new CopyOnWriteArrayList<>();
    // SPSC queue for pending changes (if single selector thread)
    protected final SpscArrayQueue<ChangeRequest> pendingChanges = new SpscArrayQueue<>(1024);
    
    private static final XStream xstream = new XStream();
    
    static {
        xstream.processAnnotations(AssociationImpl.class);
        xstream.processAnnotations(ServerImpl.class);
    }
    
    public void load(String persistDir) throws IOException {
        String filename = persistDir + File.separator + "SctpManagement.xml";
        try (Reader reader = new FileReader(filename)) {
            Map<String, Association> loadedAssociations = (Map<String, Association>) 
                xstream.fromXML(reader);
            associations.clear();
            associations.putAll(loadedAssociations);
        }
        // Similarly for servers...
    }
    
    public void store(String persistDir) throws IOException {
        String filename = persistDir + File.separator + "SctpManagement.xml";
        try (Writer writer = new FileWriter(filename)) {
            xstream.toXML(new HashMap<>(associations), writer);
        }
        // Similarly for servers...
    }
}
```

---

## 7. Performance Comparison Summary

| Operation | Javolution | JCTools | Speedup |
|-----------|------------|---------|---------|
| Map get() | ~3M ops/s | ~10M ops/s | 3.3x |
| Map put() | ~2M ops/s | ~8M ops/s | 4x |
| Queue poll() | ~4M ops/s | ~20M ops/s | 5x |
| Queue offer() | ~3M ops/s | ~18M ops/s | 6x |
| List iteration | ~5M ops/s | ~8M ops/s | 1.6x |

**Note:** Numbers are approximate and depend on hardware/contention.

---

## 8. Queue Selection Guide

| Use Case | JCTools Queue | Description |
|----------|---------------|-------------|
| Single producer, Single consumer | `SpscArrayQueue` | Fastest, no CAS needed |
| Multi producer, Single consumer | `MpscArrayQueue` | Good for work queues |
| Single producer, Multi consumer | `SpmcArrayQueue` | Rare use case |
| Multi producer, Multi consumer | `MpmcArrayQueue` | Most flexible |
| Unbounded queue | `MpscUnboundedArrayQueue` | Dynamic growth |
| Linked queue | `MpscLinkedQueue` | No capacity limit |

### SCTP Recommendations:
- **PayloadDataPool**: `MpscArrayQueue` (Multi-producer: many threads release, Single-consumer: pool)
- **PendingChanges**: `SpscArrayQueue` (if single selector thread) or `MpscArrayQueue`
- **Servers list**: `CopyOnWriteArrayList` (read-heavy)
- **Associations map**: `NonBlockingHashMap` (high concurrency lookups)
