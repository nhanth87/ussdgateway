
package org.restcomm.protocols.ss7.oam.common.statistics;

import java.util.Date;
import java.util.UUID;

import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterCampaign;
import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterDefSet;
import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterOutputFormat;
import org.restcomm.protocols.ss7.oam.common.statistics.api.CounterValueSet;
import org.restcomm.protocols.ss7.oam.common.statistics.api.SourceValueSet;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


/**
*
* @author sergey vetyutnev
*
*/
public class CounterCampaignImpl implements CounterCampaign {

    private static final long serialVersionUID = -185667602668518572L;

    @JacksonXmlProperty(isAttribute = true)
    private String name;
    private CounterDefSet counterSet;
    @JacksonXmlProperty(isAttribute = true)
    private String counterSetName;
    @JacksonXmlProperty(isAttribute = true)
    private int duration;
    @JacksonXmlProperty(isAttribute = true)
    private CounterOutputFormat outputFormat = CounterOutputFormat.VERBOSE;
    @JacksonXmlProperty(isAttribute = true)
    private boolean shortCampaign;

    private Date startTime;
    private UUID lastSessionId;
    private CounterValueSet lastCounterValueSet;
    private SourceValueSet lastSourceValueSet;

    public CounterCampaignImpl() {
    }

    public CounterCampaignImpl(String name, String counterSetName, CounterDefSet counterSet, int duration, boolean shortCampaign, CounterOutputFormat outputFormat) {
        this.name = name;
        this.counterSetName = counterSetName;
        this.counterSet = counterSet;
        this.duration = duration;
        if (outputFormat != null)
            this.outputFormat = outputFormat;
        else
            this.outputFormat = CounterOutputFormat.VERBOSE;
        this.shortCampaign = shortCampaign;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCounterSetName() {
        return counterSetName;
    }

    @Override
    public int getDuration() {
       return duration;
    }

    @Override
    public CounterOutputFormat getOutputFormat() {
        return outputFormat;
    }

    @Override
    public int getOutputFormatInt() {
        return outputFormat.getCode();
    }

    @Override
    public boolean isShortCampaign() {
        return shortCampaign;
    }

    @Override
    public CounterDefSet getCounterSet() {
        return counterSet;
    }

    public void setCounterSet(CounterDefSet counterSet) {
        this.counterSet = counterSet;
    }

    @Override
    public CounterValueSet getLastCounterValueSet() {
        return lastCounterValueSet;
    }

    public void setCounterValueSet(CounterValueSet val) {
        lastCounterValueSet = val;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public UUID getLastSessionId() {
        return lastSessionId;
    }

    public void setLastSessionId(UUID lastSessionId) {
        this.lastSessionId = lastSessionId;
    }

    public SourceValueSet getLastSourceValueSet() {
        return lastSourceValueSet;
    }

    public void setLastSourceValueSet(SourceValueSet lastSourceValueSet) {
        this.lastSourceValueSet = lastSourceValueSet;
    }

}
