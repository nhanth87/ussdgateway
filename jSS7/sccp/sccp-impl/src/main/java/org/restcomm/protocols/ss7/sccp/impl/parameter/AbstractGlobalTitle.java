
package org.restcomm.protocols.ss7.sccp.impl.parameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.message.ParseException;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;

/**
 * @author baranowb
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GlobalTitle0100Impl.class, name = "GlobalTitle0100Impl"),
    @JsonSubTypes.Type(value = GlobalTitle0011Impl.class, name = "GlobalTitle0011Impl"),
    @JsonSubTypes.Type(value = GlobalTitle0010Impl.class, name = "GlobalTitle0010Impl"),
    @JsonSubTypes.Type(value = GlobalTitle0001Impl.class, name = "GlobalTitle0001Impl"),
    @JsonSubTypes.Type(value = NoGlobalTitle.class, name = "NoGlobalTitle")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractGlobalTitle extends AbstractParameter implements GlobalTitle {

    protected String digits;

    //not codable, just used to encode/decode digits in a common way.
    protected EncodingScheme encodingScheme;

    protected static final String GLOBAL_TITLE_INDICATOR = "gti";
    protected static final String DIGITS = "digits";
    protected static final String TRANSLATION_TYPE = "tt";
    protected static final String NUMBERING_PLAN = "np";
    protected static final String NATURE_OF_ADDRESS_INDICATOR = "nai";
    protected static final String ENCODING_SCHEME = "es";

    public AbstractGlobalTitle() {
    }

    @Override
    public String getDigits() {
        return this.digits;
    }

    @Override
    public void decode(byte[] b, final ParameterFactory factory, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        this.decode(new ByteArrayInputStream(b), factory, sccpProtocolVersion);
    }

    @Override
    public byte[] encode(final boolean removeSpc, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.encodingScheme.encode(digits, baos);
        return baos.toByteArray();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((digits == null) ? 0 : digits.hashCode());
        result = prime * result + ((encodingScheme == null) ? 0 : encodingScheme.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractGlobalTitle other = (AbstractGlobalTitle) obj;
        if (digits == null) {
            if (other.digits != null)
                return false;
        } else if (!digits.equals(other.digits))
            return false;
        if (encodingScheme == null) {
            if (other.encodingScheme != null)
                return false;
        } else if (!encodingScheme.equals(other.encodingScheme))
            return false;
        return true;
    }

}
