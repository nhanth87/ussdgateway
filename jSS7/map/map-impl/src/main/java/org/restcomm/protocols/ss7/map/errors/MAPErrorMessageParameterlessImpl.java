package org.restcomm.protocols.ss7.map.errors;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageParameterless;

/**
 * The MAP ReturnError message without any parameters
 *
 * @author sergey vetyutnev
 * @author amit bhayani
 */
@JacksonXmlRootElement(localName = "mAPErrorMessageParameterlessImpl")
public class MAPErrorMessageParameterlessImpl extends MAPErrorMessageImpl implements MAPErrorMessageParameterless {

    public MAPErrorMessageParameterlessImpl() {
    }

    public MAPErrorMessageParameterlessImpl(Long errorCode) {
        super(errorCode);
    }

    public boolean isEmParameterless() {
        return true;
    }

    public MAPErrorMessageParameterless getEmParameterless() {
        return this;
    }

    public int getTag() throws MAPException {
        throw new MAPException("MAPErrorMessageParameterless does not support encoding");
    }

    public int getTagClass() {
        return 0;
    }

    public boolean getIsPrimitive() {
        return false;
    }

    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
    }

    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
    }

    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    }

    @Override
    public String toString() {
        return "MAPErrorMessageParameterless [errorCode=" + this.errorCode + "]";
    }

}
