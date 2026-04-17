package org.restcomm.protocols.ss7.sccp.impl;

import org.jctools.maps.NonBlockingHashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper class for RemoteSignalingPointCode map to support Jackson serialization
 * Extends NonBlockingHashMap for thread-safe concurrent access
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSignalingPointCodeMap<K, V> extends NonBlockingHashMap<K, V> {
    private static final long serialVersionUID = 1L;

    public RemoteSignalingPointCodeMap() {
        super();
    }
}
