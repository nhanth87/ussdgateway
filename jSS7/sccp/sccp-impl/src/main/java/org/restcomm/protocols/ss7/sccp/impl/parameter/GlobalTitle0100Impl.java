
package org.restcomm.protocols.ss7.sccp.impl.parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.message.ParseException;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0100;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;

/**
 * @author baranowb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalTitle0100Impl extends AbstractGlobalTitle implements GlobalTitle0100 {

    @JacksonXmlProperty(localName = "natureOfAddress")
    private NatureOfAddress natureOfAddress;
    
    @JacksonXmlProperty(localName = "numberingPlan")
    private NumberingPlan numberingPlan;
    
    @JacksonXmlProperty(localName = "translationType")
    private int translationType;

    public GlobalTitle0100Impl() {
    }

    /**
     * @param digits
     * @param translationType
     * @param encodingScheme
     * @param numberingPlan
     * @param natureOfAddress
     */
    public GlobalTitle0100Impl(final String digits,final int translationType, final EncodingScheme encodingScheme,final NumberingPlan numberingPlan, final NatureOfAddress natureOfAddress) {
        super();

        if(digits == null){
            throw new IllegalArgumentException();
        }
        if(encodingScheme == null){
            throw new IllegalArgumentException();
        }
        if(numberingPlan == null){
            throw new IllegalArgumentException();
        }
        if(natureOfAddress == null){
            throw new IllegalArgumentException();
        }
        super.encodingScheme = encodingScheme;
        this.translationType = translationType;
        this.natureOfAddress = natureOfAddress;
        this.numberingPlan = numberingPlan;
        super.digits = digits;

    }

    @Override
    public GlobalTitleIndicator getGlobalTitleIndicator() {
        return GlobalTitleIndicator.GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS;
    }

    @Override
    public EncodingScheme getEncodingScheme() {
        return this.encodingScheme;
    }

    @Override
    public int getTranslationType() {
        return this.translationType;
    }

    @Override
    public NatureOfAddress getNatureOfAddress() {
        return this.natureOfAddress;
    }

    @Override
    public NumberingPlan getNumberingPlan() {
        return this.numberingPlan;
    }

    @Override
    public void decode(final InputStream in,final ParameterFactory factory, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        try{
        this.translationType = in.read() & 0xff;

        int b = in.read() & 0xff;

        this.encodingScheme = factory.createEncodingScheme((byte) (b & 0x0f));
        this.numberingPlan = NumberingPlan.valueOf((b & 0xf0) >> 4);
        b = in.read() & 0xff;
        this.natureOfAddress = NatureOfAddress.valueOf(b);
        super.digits = this.encodingScheme.decode(in);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public void encode(OutputStream out, final boolean removeSpc, final SccpProtocolVersion sccpProtocolVersion) throws ParseException {
        try{
        if(super.digits == null){
            throw new IllegalStateException();
        }
        out.write(this.translationType);
        out.write((this.numberingPlan.getValue() << 4) | this.encodingScheme.getSchemeCode());
        out.write(this.natureOfAddress.getValue());
        this.encodingScheme.encode(digits, out);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((natureOfAddress == null) ? 0 : natureOfAddress.hashCode());
        result = prime * result + ((numberingPlan == null) ? 0 : numberingPlan.hashCode());
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
        GlobalTitle0100Impl other = (GlobalTitle0100Impl) obj;
        if (natureOfAddress != other.natureOfAddress)
            return false;
        if (numberingPlan != other.numberingPlan)
            return false;
        if (translationType != other.translationType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GlobalTitle0100Impl [digits=" + digits + ", natureOfAddress=" + natureOfAddress + ", numberingPlan=" + numberingPlan
                + ", translationType=" + translationType + ", encodingScheme=" + encodingScheme + "]";
    }


}
