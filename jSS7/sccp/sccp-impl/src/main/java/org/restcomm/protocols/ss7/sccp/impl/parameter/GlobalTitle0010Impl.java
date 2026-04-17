
package org.restcomm.protocols.ss7.sccp.impl.parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.message.ParseException;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0010;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;

/**
 * @author baranowb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalTitle0010Impl extends AbstractGlobalTitle implements GlobalTitle0010 {

    @JacksonXmlProperty(localName = "translationType")
    private int translationType;

    public GlobalTitle0010Impl() {
    }

    /**
     * @param digits
     * @param translationType
     */
    public GlobalTitle0010Impl(final String digits,final int translationType) {
        this();

        if(digits == null){
            throw new IllegalArgumentException();
        }
        this.translationType = translationType;
        super.digits = digits;
        super.encodingScheme = getEncodingScheme(translationType);
    }

    protected EncodingScheme getEncodingScheme(final int translationType) {
        // TODO: we need to add here code for national EncodingScheme for GT0010
        // now we just use even BCD EncodingScheme for encoding/decoding as a default / fake implementing

        return BCDEvenEncodingScheme.INSTANCE;
    }

    @Override
    public GlobalTitleIndicator getGlobalTitleIndicator() {
        return GlobalTitleIndicator.GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY;
    }

    @Override
    public int getTranslationType() {
        return this.translationType;
    }

    @Override
    public void decode(final InputStream in,final ParameterFactory factory, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        try{
        this.translationType = in.read() & 0xff;
        super.encodingScheme = getEncodingScheme(translationType);
        super.digits = this.encodingScheme.decode(in);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public void encode(final OutputStream out, final boolean removeSpc, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        try {
            out.write(this.translationType);
            if(super.digits == null){
                throw new IllegalStateException();
            }
            this.encodingScheme.encode(digits, out);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + translationType;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        GlobalTitle0010Impl other = (GlobalTitle0010Impl) obj;
        if (translationType != other.translationType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GlobalTitle0010Impl [digits=" + digits + ", translationType=" + translationType + ", encodingScheme="
                + encodingScheme + "]";
    }

}
