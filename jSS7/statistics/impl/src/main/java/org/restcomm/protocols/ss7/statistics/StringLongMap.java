
package org.restcomm.protocols.ss7.statistics;

import java.util.Map;

import org.jctools.maps.NonBlockingHashMap;

import org.restcomm.protocols.ss7.statistics.api.LongValue;
import org.restcomm.protocols.ss7.statistics.api.StatDataCollectorType;
import org.restcomm.protocols.ss7.statistics.api.StatResult;

/**
*
* @author sergey vetyutnev
*
*/
public class StringLongMap extends StatDataCollectorAbstractImpl {

    private Map<String, LongValue> data = new NonBlockingHashMap<String, LongValue>();

    public StringLongMap(String campaignName) {
        super(campaignName);
    }

    public StatResult restartAndGet() {
        synchronized (this) {
            StatResultStringLongMap res = new StatResultStringLongMap(this.data);
            this.data = new NonBlockingHashMap<String, LongValue>();
            this.reset();
            return res;
        }
    }

    protected void reset() {
        synchronized (this) {
            this.data.clear();
        }
    }

    @Override
    public void updateData(long newVal) {
    }

    @Override
    public void updateData(String name) {
        synchronized (this) {
            LongValue longValue = data.get(name);
            if (longValue == null) {
                longValue = new LongValue();
                data.put(name, longValue);
            }
            longValue.updateValue();
        }
    }

    @Override
    public StatDataCollectorType getStatDataCollectorType() {
        return StatDataCollectorType.StringLongMap;
    }

    public class StatResultStringLongMap implements StatResult {

        private Map<String, LongValue> data;

        public StatResultStringLongMap(Map<String, LongValue> data) {
            this.data = data;
        }

        @Override
        public long getLongValue() {
            return 0;
        }

        @Override
        public Map<String, LongValue> getStringLongValue() {
            return data;
        }

    }
}
