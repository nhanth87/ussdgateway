
package org.restcomm.protocols.ss7.oam.common.statistics;

import java.util.Map;

import org.jctools.maps.NonBlockingHashMap;
import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterDef;
import org.restcomm.protocols.ss7.oam.common.statistics.api.SourceValueCounter;
import org.restcomm.protocols.ss7.oam.common.statistics.api.SourceValueObject;

/**
*
* @author sergey vetyutnev
*
*/
public class SourceValueCounterImpl implements SourceValueCounter {

    private CounterDef counterDef;
    private Map<String, SourceValueObject> objects = new NonBlockingHashMap<String, SourceValueObject>();

    public SourceValueCounterImpl(CounterDef counterDef) {
        this.counterDef = counterDef;
    }

    public void addObject(SourceValueObject val) {
        objects.put(val.getObjectName(), val);
    }

    @Override
    public CounterDef getCounterDef() {
        return counterDef;
    }

    @Override
    public Map<String, SourceValueObject> getObjects() {
        return this.objects;
    }

    @Override
    public String toString() {
        return "SourceValueCounterImpl [counterDef=" + counterDef + ", objects=" + objects + "]";
    }

}
