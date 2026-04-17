package org.restcomm.protocols.ss7.cap.isup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.primitives.CAPAsnPrimitive;
import org.restcomm.protocols.ss7.isup.ParameterException;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericDigitsImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericDigits;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericNumber;

/**
 *
 *
 * @author sergey vetyutnev
 *
 */

@JacksonXmlRootElement(localName = "digits")
public class DigitsImpl implements Digits, CAPAsnPrimitive {

    public static final String _PrimitiveName = "Digits";

    private byte[] data;
    private boolean isGenericDigits;
    private boolean isGenericNumber;

    public DigitsImpl() {
    }

    public DigitsImpl(byte[] data) {
        this.data = data;
    }

    public DigitsImpl(GenericDigits genericDigits) throws CAPException {
        this.setGenericDigits(genericDigits);
    }

    public DigitsImpl(GenericNumber genericNumber) throws CAPException {
        this.setGenericNumber(genericNumber);
    }

    @JacksonXmlProperty(localName = "genericDigits")
    @Override
    public byte[] getData() {
        return data;
    }

    @JsonIgnore
    @Override
    public GenericDigits getGenericDigits() throws CAPException {
        if (this.data == null)
            throw new CAPException("The data has not been filled");
        if (!this.isGenericDigits)
            throw new CAPException("Primitive is not marked as GenericDigits (use setGenericDigits() before)");

        try {
            GenericDigitsImpl ocn = new GenericDigitsImpl();
            ocn.decode(this.data);
            return ocn;
        } catch (ParameterException e) {
            throw new CAPException("ParameterException when decoding GenericDigits: " + e.getMessage(), e);
        }
    }

    @JsonIgnore
    @Override
    public GenericNumber getGenericNumber() throws CAPException {
        if (this.data == null)
            throw new CAPException("The data has not been filled");
        if (!this.isGenericNumber)
            throw new CAPException("Primitive is not marked as GenericNumber (use setGenericNumber() before)");

        try {
            GenericNumberImpl ocn = new GenericNumberImpl();
            ocn.decode(this.data);
            return ocn;
        } catch (ParameterException e) {
            throw new CAPException("ParameterException when decoding GenericNumber: " + e.getMessage(), e);
        }
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void setGenericDigits(GenericDigits genericDigits) throws CAPException {

        if (genericDigits == null)
            throw new CAPException("The genericDigits parameter must not be null");
        try {
            this.data = ((GenericDigitsImpl) genericDigits).encode();
            setIsGenericDigits();
        } catch (ParameterException e) {
            throw new CAPException("ParameterException when encoding genericDigits: " + e.getMessage(), e);
        }
    }

    @Override
    public void setGenericNumber(GenericNumber genericNumber) throws CAPException {

        if (genericNumber == null)
            throw new CAPException("The genericNumber parameter must not be null");
        try {
            this.data = ((GenericNumberImpl) genericNumber).encode();
            setIsGenericNumber();
        } catch (ParameterException e) {
            throw new CAPException("ParameterException when encoding genericNumber: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean getIsGenericDigits() {
        return isGenericDigits;
    }

    @Override
    public boolean getIsGenericNumber() {
        return isGenericNumber;
    }

    @Override
    public void setIsGenericDigits() {
        isGenericDigits = true;
        isGenericNumber = false;
    }

    @Override
    public void setIsGenericNumber() {
        isGenericNumber = true;
        isGenericDigits = false;
    }

    @Override
    public int getTag() throws CAPException {
        return Tag.STRING_OCTET;
    }

    @Override
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    @Override
    public boolean getIsPrimitive() {
        return true;
    }

    @Override
    public void decodeAll(AsnInputStream asnInputStream) throws CAPParsingComponentException {

        try {
            int length = asnInputStream.readLength();
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void decodeData(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException {

        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException,
            IOException, AsnException {

        this.data = asnInputStream.readOctetStringData(length);
        if (this.data.length < 2 || this.data.length > 16)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": data must be from 2 to 16 bytes length, found: " + this.data.length,
                    CAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws CAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws CAPException {

        try {
            asnOutputStream.writeTag(tagClass, true, tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {

        if (this.data == null)
            throw new CAPException("data field must not be null");
        if (this.data.length < 2 && this.data.length > 16)
            throw new CAPException("data field length must be from 2 to 16");

        asnOutputStream.writeOctetStringData(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.data != null) {
            sb.append("data=[");
            sb.append(printDataArr(this.data));
            sb.append("]");
            try {
                if (this.getIsGenericNumber()) {
                    GenericNumber gn = this.getGenericNumber();
                    sb.append(", genericNumber");
                    sb.append(gn.toString());
                }

                if (this.getIsGenericDigits()) {
                    GenericDigits gd = this.getGenericDigits();
                    sb.append(", genericDigits");
                    sb.append(gd.toString());
                }
            } catch (CAPException e) {
            }
        }

        sb.append("]");

        return sb.toString();
    }

    private String printDataArr(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int b : arr) {
            sb.append(b);
            sb.append(", ");
        }

        return sb.toString();
    }


}

