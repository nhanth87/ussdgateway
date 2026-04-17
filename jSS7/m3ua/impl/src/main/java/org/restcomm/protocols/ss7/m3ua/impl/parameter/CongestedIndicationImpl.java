package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.m3ua.parameter.CongestedIndication;
import org.restcomm.protocols.ss7.m3ua.parameter.Parameter;

/**
 *
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "congestedIndication")
public class CongestedIndicationImpl extends ParameterImpl implements CongestedIndication {

    @JsonProperty("level")
    private CongestionLevel level;

    protected CongestedIndicationImpl(CongestionLevel level) {
        this.level = level;
        this.tag = Parameter.Congestion_Indications;
    }

    protected CongestedIndicationImpl(byte[] data) {
        // data[0], data[1] and data[2] are reserved
        this.level = CongestionLevel.getCongestionLevel(data[3]);
        this.tag = Parameter.Congestion_Indications;
    }

    @Override
    protected byte[] getValue() {
        byte[] data = new byte[4];
        data[0] = 0;// Reserved
        data[1] = 0; // Reserved
        data[2] = 0;// Reserved
        data[3] = (byte) level.getLevel();

        return data;
    }

    public CongestionLevel getCongestionLevel() {
        return this.level;
    }

    @Override
    public String toString() {
        return String.format("CongestedIndication level=%s", level);
    }

}
