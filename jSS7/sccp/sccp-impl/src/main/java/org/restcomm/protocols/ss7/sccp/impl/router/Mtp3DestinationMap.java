package org.restcomm.protocols.ss7.sccp.impl.router;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper class for Mtp3Destination map to support Jackson serialization
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mtp3DestinationMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;

    public Mtp3DestinationMap() {
        super();
    }
}
