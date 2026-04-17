package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.m3ua.parameter.InfoString;
import org.restcomm.protocols.ss7.m3ua.parameter.Parameter;

/**
 *
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "infoString")
public class InfoStringImpl extends ParameterImpl implements InfoString {

    @JsonProperty("string")
    private String string;

    protected InfoStringImpl(byte[] value) {
        this.tag = Parameter.INFO_String;
        this.string = new String(value);
    }

    protected InfoStringImpl(String string) {
        this.tag = Parameter.INFO_String;
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    @Override
    protected byte[] getValue() {
        return this.string.getBytes();
    }

    @Override
    public String toString() {
        return String.format("InfoString : string = %s ", this.string);
    }

}
