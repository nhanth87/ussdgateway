
package org.restcomm.protocols.ss7.oam.common.tcap;

import java.util.Date;

import java.util.Map;

import org.jctools.maps.NonBlockingHashMap;
import org.restcomm.protocols.ss7.oam.common.jmx.MBeanHost;
import org.restcomm.protocols.ss7.oam.common.statistics.CounterCampaignImpl;
import org.restcomm.protocols.ss7.oam.common.statistics.CounterCampaignMap;
import org.restcomm.protocols.ss7.oam.common.statistics.CounterProviderManagement;
import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterMediator;

/**
*
* @author sergey vetyutnev
*
*/
public class CounterProviderProxy extends CounterProviderManagement {

    public CounterProviderProxy(MBeanHost beanHost) {
        super(beanHost);
    }

    public CounterCampaignMap getCounterCampaignMap() {
        return this.lstCounterCampaign;
    }

    public Map<String, CounterMediator> getCounterMediatorLst() {
        return this.lstCounterMediator;
    }

    protected void processCampaign(String ccName, Date endTime) {
        CounterCampaignImpl cc = (CounterCampaignImpl) this.getCampaign(ccName);
        super.processCampaign(cc, endTime);
    }
}
