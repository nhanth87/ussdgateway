package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author kulikov
 */
@JacksonXmlRootElement(localName = "unknownParameter")
public class UnknownParameterImpl extends ParameterImpl {

    @JsonProperty("value")
    private byte[] value;

    protected UnknownParameterImpl(int tag, int length, byte[] value) {
        this.tag = (short) tag;
        this.length = (short) length;
        this.value = value;
    }

    @Override
    protected byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Unknown parameter: tag=%d, length=%d", tag, length);
    }

}
