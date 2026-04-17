/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2026, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.protocols.ss7.utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * High-performance buffer pool for ASN.1 encoding/decoding operations.
 * 
 * Features:
 * - Multiple pool sizes for different message types
 * - Thread-local caching to reduce contention
 * - Lazy initialization to reduce startup overhead
 * - Statistics tracking for monitoring
 * - Automatic growth when needed
 * 
 * Buffer sizes are aligned to common ASN.1 message sizes:
 * - Small (256B): Individual primitives, short strings
 * - Medium (2KB): Typical MAP operations, short messages
 * - Large (16KB): Large INVOKE results, extended operations
 * - XLarge (64KB): Bulk data transfer, long messages
 * 
 * @author nhanth87
 * @version 2.1.0
 */
public final class Asn1BufferPool {

    private Asn1BufferPool() {
        // Utility class
    }

    // Pool configuration
    public static final int SMALL_SIZE = 256;
    public static final int MEDIUM_SIZE = 2048;
    public static final int LARGE_SIZE = 16384;
    public static final int XLARGE_SIZE = 65536;

    // Maximum buffers per thread per size class
    private static final int MAX_PER_THREAD = 4;
    private static final int MAX_POOL_SIZE = 100;

    // Global pool instance
    private static volatile GlobalPool globalPool;

    /**
     * Acquire a buffer from the pool.
     * Returns a buffer sized appropriately for the expected content.
     * 
     * @param expectedSize Expected content size in bytes
     * @return Pooled buffer ready for use
     */
    public static PooledByteBuffer acquire(int expectedSize) {
        return acquire(expectedSize, false);
    }

    /**
     * Acquire a buffer from the pool.
     * 
     * @param expectedSize Expected content size
     * @param direct If true, prefer direct buffer; if false, let pool decide
     * @return Pooled buffer ready for use
     */
    public static PooledByteBuffer acquire(int expectedSize, boolean direct) {
        int sizeClass = getSizeClass(expectedSize);
        ByteBuffer buffer = threadLocalPools[getSizeClassIndex(sizeClass)].acquire(sizeClass, direct);
        return new PooledByteBuffer(buffer, sizeClass);
    }

    /**
     * Acquire a small buffer (up to 256 bytes).
     */
    public static PooledByteBuffer acquireSmall() {
        ByteBuffer buffer = threadLocalPools[0].acquire(SMALL_SIZE, false);
        return new PooledByteBuffer(buffer, SMALL_SIZE);
    }

    /**
     * Acquire a medium buffer (up to 2KB).
     */
    public static PooledByteBuffer acquireMedium() {
        ByteBuffer buffer = threadLocalPools[1].acquire(MEDIUM_SIZE, false);
        return new PooledByteBuffer(buffer, MEDIUM_SIZE);
    }

    /**
     * Acquire a large buffer (up to 16KB).
     */
    public static PooledByteBuffer acquireLarge() {
        ByteBuffer buffer = threadLocalPools[2].acquire(LARGE_SIZE, false);
        return new PooledByteBuffer(buffer, LARGE_SIZE);
    }

    /**
     * Acquire an extra-large buffer (up to 64KB).
     */
    public static PooledByteBuffer acquireXLarge() {
        ByteBuffer buffer = threadLocalPools[3].acquire(XLARGE_SIZE, false);
        return new PooledByteBuffer(buffer, XLARGE_SIZE);
    }

    /**
     * Release a buffer back to the pool.
     * Automatically called by PooledByteBuffer.close().
     */
    static void release(ByteBuffer buffer, int sizeClass) {
        if (buffer == null) {
            return;
        }

        // Reset buffer state
        buffer.clear();

        int index = getSizeClassIndex(sizeClass);
        threadLocalPools[index].release(buffer);
    }

    /**
     * Get the appropriate size class for a given expected size.
     */
    private static int getSizeClass(int expectedSize) {
        if (expectedSize <= SMALL_SIZE) {
            return SMALL_SIZE;
        } else if (expectedSize <= MEDIUM_SIZE) {
            return MEDIUM_SIZE;
        } else if (expectedSize <= LARGE_SIZE) {
            return LARGE_SIZE;
        } else {
            return XLARGE_SIZE;
        }
    }

    /**
     * Get the pool index for a size class.
     */
    private static int getSizeClassIndex(int sizeClass) {
        switch (sizeClass) {
            case SMALL_SIZE: return 0;
            case MEDIUM_SIZE: return 1;
            case LARGE_SIZE: return 2;
            case XLARGE_SIZE: return 3;
            default: return 3;
        }
    }

    // Thread-local pools - one per size class
    private static final ThreadLocalPool[] threadLocalPools = {
        new ThreadLocalPool(SMALL_SIZE),
        new ThreadLocalPool(MEDIUM_SIZE),
        new ThreadLocalPool(LARGE_SIZE),
        new ThreadLocalPool(XLARGE_SIZE)
    };

    /**
     * Statistics for pool monitoring.
     */
    public static final class PoolStats {
        public final long hits;
        public final long misses;
        public final long created;
        public final long recycled;
        public final long overflowCreated;
        public final int[] poolSizes;

        PoolStats(long hits, long misses, long created, long recycled,
                  long overflowCreated, int[] poolSizes) {
            this.hits = hits;
            this.misses = misses;
            this.created = created;
            this.recycled = recycled;
            this.overflowCreated = overflowCreated;
            this.poolSizes = poolSizes;
        }

        public double getHitRate() {
            long total = hits + misses;
            return total > 0 ? (double) hits / total : 0.0;
        }

        @Override
        public String toString() {
            return String.format(
                "PoolStats{hits=%d, misses=%d, hitRate=%.2f%%, created=%d, recycled=%d, overflow=%d, pools=%s}",
                hits, misses, getHitRate() * 100, created, recycled, overflowCreated, java.util.Arrays.toString(poolSizes)
            );
        }
    }

    /**
     * Get pool statistics.
     */
    public static PoolStats getStats() {
        long hits = 0, misses = 0, created = 0, recycled = 0, overflow = 0;
        int[] sizes = new int[4];

        for (int i = 0; i < threadLocalPools.length; i++) {
            ThreadLocalPool pool = threadLocalPools[i];
            hits += pool.hits.get();
            misses += pool.misses.get();
            created += pool.created.get();
            recycled += pool.recycled.get();
            overflow += pool.overflowCreated.get();
            sizes[i] = pool.getQueueSize();
        }

        return new PoolStats(hits, misses, created, recycled, overflow, sizes);
    }

    /**
     * Reset all pool statistics.
     */
    public static void resetStats() {
        for (ThreadLocalPool pool : threadLocalPools) {
            pool.hits.set(0);
            pool.misses.set(0);
            pool.created.set(0);
            pool.recycled.set(0);
            pool.overflowCreated.set(0);
        }
    }

    /**
     * Clear all pools (emergency cleanup).
     */
    public static void clearAll() {
        for (ThreadLocalPool pool : threadLocalPools) {
            pool.clear();
        }
    }

    /**
     * Thread-local buffer pool for one size class.
     */
    private static final class ThreadLocalPool {
        private final int bufferSize;
        private final ThreadLocal<Deque<ByteBuffer>> threadLocal;
        private final AtomicInteger queueSize = new AtomicInteger(0);

        // Statistics
        private final AtomicLong hits = new AtomicLong(0);
        private final AtomicLong misses = new AtomicLong(0);
        private final AtomicLong created = new AtomicLong(0);
        private final AtomicLong recycled = new AtomicLong(0);
        private final AtomicLong overflowCreated = new AtomicLong(0);

        ThreadLocalPool(int bufferSize) {
            this.bufferSize = bufferSize;
            this.threadLocal = ThreadLocal.withInitial(() -> new ArrayDeque<>(MAX_PER_THREAD));
        }

        ByteBuffer acquire(int sizeClass, boolean preferDirect) {
            Deque<ByteBuffer> queue = threadLocal.get();

            synchronized (queue) {
                ByteBuffer buffer = queue.pollFirst();
                if (buffer != null) {
                    queueSize.decrementAndGet();
                    hits.incrementAndGet();
                    recycled.incrementAndGet();
                    buffer.clear();
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    return buffer;
                }
            }

            misses.incrementAndGet();

            // Check if we should use direct or heap
            if (preferDirect || bufferSize >= MEDIUM_SIZE) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
                buffer.order(ByteOrder.BIG_ENDIAN);
                created.incrementAndGet();
                return buffer;
            } else {
                return ByteBuffer.allocate(bufferSize).order(ByteOrder.BIG_ENDIAN);
            }
        }

        void release(ByteBuffer buffer) {
            if (buffer == null || buffer.capacity() != bufferSize) {
                // Buffer doesn't match our size class
                return;
            }

            Deque<ByteBuffer> queue = threadLocal.get();

            synchronized (queue) {
                if (queue.size() < MAX_PER_THREAD) {
                    buffer.clear();
                    queue.addFirst(buffer);
                    queueSize.incrementAndGet();
                } else {
                    // Pool full, let GC handle it
                    overflowCreated.incrementAndGet();
                }
            }
        }

        int getQueueSize() {
            return queueSize.get();
        }

        void clear() {
            Deque<ByteBuffer> queue = threadLocal.get();
            synchronized (queue) {
                queue.clear();
                queueSize.set(0);
            }
        }
    }

    /**
     * Global fallback pool for cross-thread buffer sharing.
     */
    private static final class GlobalPool {
        private final ConcurrentHashMap<Integer, Deque<ByteBuffer>> pools = new ConcurrentHashMap<>();

        ByteBuffer acquire(int sizeClass) {
            Deque<ByteBuffer> queue = pools.computeIfAbsent(sizeClass, k -> new ArrayDeque<>());

            synchronized (queue) {
                ByteBuffer buffer = queue.pollFirst();
                if (buffer != null) {
                    buffer.clear();
                    return buffer;
                }
            }

            return ByteBuffer.allocateDirect(sizeClass).order(ByteOrder.BIG_ENDIAN);
        }

        void release(ByteBuffer buffer) {
            if (buffer == null) {
                return;
            }

            int sizeClass = buffer.capacity();
            Deque<ByteBuffer> queue = pools.get(sizeClass);

            if (queue == null) {
                return;
            }

            synchronized (queue) {
                if (queue.size() < MAX_POOL_SIZE) {
                    buffer.clear();
                    queue.addLast(buffer);
                }
            }
        }
    }

    /**
     * Wrapper for pooled buffer with automatic release.
     * Implements AutoCloseable for try-with-resources.
     */
    public static final class PooledByteBuffer implements AutoCloseable {
        private final ByteBuffer buffer;
        private final int sizeClass;
        private boolean released = false;

        PooledByteBuffer(ByteBuffer buffer, int sizeClass) {
            this.buffer = buffer;
            this.sizeClass = sizeClass;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }

        public int getSizeClass() {
            return sizeClass;
        }

        /**
         * Get remaining capacity for encoding.
         */
        public int remaining() {
            return buffer.remaining();
        }

        /**
         * Get current position.
         */
        public int position() {
            return buffer.position();
        }

        /**
         * Set position.
         */
        public PooledByteBuffer position(int pos) {
            buffer.position(pos);
            return this;
        }

        /**
         * Get limit.
         */
        public int limit() {
            return buffer.limit();
        }

        /**
         * Set limit.
         */
        public PooledByteBuffer limit(int lim) {
            buffer.limit(lim);
            return this;
        }

        /**
         * Flip for read.
         */
        public PooledByteBuffer flip() {
            buffer.flip();
            return this;
        }

        /**
         * Clear for write.
         */
        public PooledByteBuffer clear() {
            buffer.clear();
            return this;
        }

        /**
         * Get bytes from buffer.
         */
        public byte get() {
            return buffer.get();
        }

        /**
         * Put byte to buffer.
         */
        public PooledByteBuffer put(byte b) {
            buffer.put(b);
            return this;
        }

        /**
         * Get bytes from buffer at offset.
         */
        public byte get(int index) {
            return buffer.get(index);
        }

        /**
         * Put byte at offset.
         */
        public PooledByteBuffer put(int index, byte b) {
            buffer.put(index, b);
            return this;
        }

        /**
         * Bulk put from byte array.
         */
        public PooledByteBuffer put(byte[] src) {
            buffer.put(src);
            return this;
        }

        /**
         * Bulk put from byte array at offset.
         */
        public PooledByteBuffer put(byte[] src, int offset, int length) {
            buffer.put(src, offset, length);
            return this;
        }

        /**
         * Bulk get into byte array.
         */
        public PooledByteBuffer get(byte[] dst) {
            buffer.get(dst);
            return this;
        }

        /**
         * Bulk get into byte array at offset.
         */
        public PooledByteBuffer get(byte[] dst, int offset, int length) {
            buffer.get(dst, offset, length);
            return this;
        }

        /**
         * Slice the buffer.
         */
        public ByteBuffer slice() {
            return buffer.slice().order(ByteOrder.BIG_ENDIAN);
        }

        /**
         * Duplicate the buffer.
         */
        public ByteBuffer duplicate() {
            return buffer.duplicate().order(ByteOrder.BIG_ENDIAN);
        }

        /**
         * Check if buffer is direct.
         */
        public boolean isDirect() {
            return buffer.isDirect();
        }

        /**
         * Release buffer back to pool.
         */
        @Override
        public void close() {
            if (!released) {
                released = true;
                Asn1BufferPool.release(buffer, sizeClass);
            }
        }

        /**
         * Explicit release without auto-close.
         */
        public void release() {
            close();
        }

        /**
         * Convert to byte array.
         */
        public byte[] toArray() {
            int pos = buffer.position();
            buffer.flip();
            byte[] result = new byte[buffer.remaining()];
            buffer.get(result);
            buffer.position(pos);
            return result;
        }

        /**
         * Convert to byte array with specified length.
         */
        public byte[] toArray(int length) {
            byte[] result = new byte[length];
            buffer.flip();
            int actualLength = Math.min(length, buffer.remaining());
            buffer.get(result, 0, actualLength);
            return result;
        }
    }
}
