package org.restcomm.protocols.ss7.sccp.impl.parameter;

import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.message.ParseException;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;

/**
 * @author amit bhayani
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoGlobalTitle extends AbstractGlobalTitle {


    public NoGlobalTitle() {
    }

    public NoGlobalTitle(String digits) {
        super.digits = digits;
    }

    @Override
    public GlobalTitleIndicator getGlobalTitleIndicator() {
        return GlobalTitleIndicator.NO_GLOBAL_TITLE_INCLUDED;
    }

    @Override
    public String getDigits() {
        return super.digits;
    }

    @Override
    public void decode(final InputStream in, final ParameterFactory factory, final SccpProtocolVersion sccpProtocolVersion)
            throws ParseException {
        this.digits = this.encodingScheme.decode(in);
    }

    @Override
    public void encode(OutputStream out, final boolean removeSpc, final SccpProtocolVersion sccpProtocolVersion)
            throws ParseException {
        if (this.digits == null) {
            throw new IllegalStateException();
        }
        this.encodingScheme.encode(this.digits, out);
    }

}
