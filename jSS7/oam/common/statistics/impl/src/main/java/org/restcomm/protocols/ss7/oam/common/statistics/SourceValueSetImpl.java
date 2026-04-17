
package org.restcomm.protocols.ss7.oam.common.statistics;

import java.util.Map;
import java.util.UUID;

import org.jctools.maps.NonBlockingHashMap;
import org.restcomm.protocols.ss7.oam.common.statistics.api.SourceValueCounter;
import org.restcomm.protocols.ss7.oam.common.statistics.api.SourceValueSet;

/**
*
* @author sergey vetyutnev
*
*/
public class SourceValueSetImpl implements SourceValueSet {

    private UUID sessionId;
    private Map<String, SourceValueCounter> counters = new NonBlockingHashMap<String, SourceValueCounter>();

    public void addCounter(SourceValueCounter val) {
        this.counters.put(val.getCounterDef().getCounterName(), val);
    }

    public SourceValueSetImpl(UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public UUID getSessionId() {
        return sessionId;
    }

    @Override
    public Map<String, SourceValueCounter> getCounters() {
        return this.counters;
    }

    @Override
    public String toString() {
        return "SourceValueSetImpl [sessionId=" + sessionId + ", counters size=" + counters.size() + "]";
    }

}
