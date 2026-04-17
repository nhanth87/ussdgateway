package org.restcomm.protocols.ss7.sccp.impl.router;

import org.restcomm.protocols.ss7.sccp.Mtp3Destination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 */
@JacksonXmlRootElement(localName = "mtp3Destination")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mtp3DestinationImpl implements Mtp3Destination {

    @JacksonXmlProperty private int firstDpc;
    @JacksonXmlProperty private int lastDpc;
    @JacksonXmlProperty private int firstSls;
    @JacksonXmlProperty private int lastSls;
    @JacksonXmlProperty private int slsMask;

    public Mtp3DestinationImpl() {
    }

    public Mtp3DestinationImpl(int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask) {
        this.firstDpc = firstDpc;
        this.lastDpc = lastDpc;
        this.firstSls = firstSls;
        this.lastSls = lastSls;
        this.slsMask = slsMask;
    }

    public int getFirstDpc() {
        return this.firstDpc;
    }

    public int getLastDpc() {
        return this.lastDpc;
    }

    public int getFirstSls() {
        return this.firstSls;
    }

    public int getLastSls() {
        return this.lastSls;
    }

    public int getSlsMask() {
        return this.slsMask;
    }

    public boolean match(int dpc, int sls) {
        sls = (sls & this.slsMask);
        if (dpc >= this.firstDpc && dpc <= this.lastDpc && sls >= this.firstSls && sls <= this.lastSls)
            return true;
        else
            return false;
    }

    public boolean match(int dpc) {
        if (dpc >= this.firstDpc && dpc <= this.lastDpc)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("firstDpc=").append(this.firstDpc).append(", lastDpc=").append(this.lastDpc).append(", firstSls=")
                .append(this.firstSls).append(", lastSls=").append(this.lastSls).append(", slsMask=").append(this.slsMask);
        return sb.toString();
    }
}
