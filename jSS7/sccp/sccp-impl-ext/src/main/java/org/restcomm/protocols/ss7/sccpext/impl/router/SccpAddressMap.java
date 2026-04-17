package org.restcomm.protocols.ss7.sccpext.impl.router;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper class for SccpAddress map to support Jackson serialization
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SccpAddressMap<Integer, SccpAddressImpl> extends HashMap<Integer, SccpAddressImpl> {
    private static final long serialVersionUID = 1L;

    public SccpAddressMap() {
        super();
    }
}
