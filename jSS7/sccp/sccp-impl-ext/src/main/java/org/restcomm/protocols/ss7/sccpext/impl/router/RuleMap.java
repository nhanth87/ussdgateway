package org.restcomm.protocols.ss7.sccpext.impl.router;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper class for Rule map to support Jackson serialization
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleMap<Integer, Rule> extends HashMap<Integer, Rule> {
    private static final long serialVersionUID = 1L;

    public RuleMap() {
        super();
    }
}
