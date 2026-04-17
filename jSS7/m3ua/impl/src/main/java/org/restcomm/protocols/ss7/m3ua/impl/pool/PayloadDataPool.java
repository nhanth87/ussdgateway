package org.restcomm.protocols.ss7.m3ua.impl.pool;

import org.jctools.queues.MpscArrayQueue;
import org.restcomm.protocols.ss7.m3ua.impl.message.transfer.PayloadDataImpl;

/**
 * Object pool for PayloadDataImpl to reduce GC pressure.
 * Uses JCTools MpscArrayQueue for high-performance concurrent access.
 * 
 * @author jSS7 Developer
 */
public class PayloadDataPool {
    
    private static final int DEFAULT_CAPACITY = 1024;
    private final MpscArrayQueue<PayloadDataImpl> pool;
    
    /**
     * Creates a PayloadDataPool with default capacity (1024)
     */
    public PayloadDataPool() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates a PayloadDataPool with specified capacity
     * 
     * @param capacity the maximum number of objects to pool
     */
    public PayloadDataPool(int capacity) {
        this.pool = new MpscArrayQueue<>(capacity);
    }
    
    /**
     * Borrow a PayloadDataImpl from the pool.
     * If pool is empty, creates a new instance.
     * 
     * @return a PayloadDataImpl instance (either pooled or new)
     */
    public PayloadDataImpl borrow() {
        PayloadDataImpl payload = pool.relaxedPoll();
        if (payload == null) {
            payload = new PayloadDataImpl();
        } else {
            // Reset the borrowed object for reuse
            payload.reset();
        }
        return payload;
    }
    
    /**
     * Return a PayloadDataImpl to the pool.
     * If pool is full, the object is discarded (GC'd).
     * 
     * @param payload the PayloadDataImpl to return to pool
     */
    public void release(PayloadDataImpl payload) {
        if (payload != null) {
            // Clear all parameters before returning to pool
            payload.clearParameters();
            pool.relaxedOffer(payload);
        }
    }
    
    /**
     * Get the current size of the pool
     * 
     * @return number of available objects in pool
     */
    public int size() {
        return pool.size();
    }
    
    /**
     * Get the capacity of the pool
     * 
     * @return maximum capacity of the pool
     */
    public int capacity() {
        return pool.capacity();
    }
    
    /**
     * Clear all objects from the pool
     */
    public void clear() {
        pool.clear();
    }
}
