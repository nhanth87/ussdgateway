package org.restcomm.protocols.ss7.cap.primitives;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.cap.api.primitives.CriticalityType;
import org.restcomm.protocols.ss7.cap.api.primitives.ExtensionField;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.ByteArrayContainer;
import org.restcomm.protocols.ss7.map.primitives.OidContainer;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "extensionField")
public class ExtensionFieldImpl implements ExtensionField, CAPAsnPrimitive {

    public static final int _ID_value = 1;

    private static final String DATA = "data";
    private static final String LOCAL_CODE = "localCode";
    private static final String GLOBAL_CODE = "globalCode";
    private static final String CRITICALITY_TYPE = "criticalityType";

    private static final String DEFAULT_STRING = null;

    public static final String _PrimitiveName = "ExtensionField";

    private Integer localCode;
    private long[] globalCode;
    private CriticalityType criticalityType;
    public byte[] data;

    public ExtensionFieldImpl() {
    }

    public ExtensionFieldImpl(Integer localCode, CriticalityType criticalityType, byte[] data) {
        this.localCode = localCode;
        this.criticalityType = criticalityType;
        this.data = data;
    }

    public ExtensionFieldImpl(long[] globalCode, CriticalityType criticalityType, byte[] data) {
        this.globalCode = globalCode;
        this.criticalityType = criticalityType;
        this.data = data;
    }

    @Override
    public Integer getLocalCode() {
        return localCode;
    }

    @Override
    public long[] getGlobalCode() {
        return globalCode;
    }

    @Override
    public CriticalityType getCriticalityType() {
        return criticalityType;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public void setLocalCode(Integer localCode) {
        this.localCode = localCode;
        this.globalCode = null;
    }

    @Override
    public void setGlobalCode(long[] globalCode) {
        this.localCode = null;
        this.globalCode = globalCode;
    }

    @Override
    public void setCriticalityType(CriticalityType criticalityType) {
        this.criticalityType = criticalityType;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int getTag() throws CAPException {
        return Tag.SEQUENCE;
    }

    @Override
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    @Override
    public boolean getIsPrimitive() {
        return false;
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

    private void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException, IOException, AsnException {

        this.localCode = null;
        this.globalCode = null;
        this.criticalityType = CriticalityType.typeIgnore;
        this.data = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            switch (num) {
                case 0:
                    // localCode or globalCode
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 0 bad tag class or not primitive",
                                CAPParsingComponentExceptionReason.MistypedParameter);
                    switch (tag) {
                        case Tag.INTEGER:
                            this.localCode = (int) ais.readInteger();
                            break;
                        case Tag.OBJECT_IDENTIFIER:
                            this.globalCode = ais.readObjectIdentifier();
                            break;
                        default:
                            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter 0 bad tag", CAPParsingComponentExceptionReason.MistypedParameter);
                    }
                    break;

                default: {
                    switch (ais.getTagClass()) {
                        case Tag.CLASS_UNIVERSAL: {
                            if (tag == Tag.ENUMERATED) {
                                int i1 = (int) ais.readInteger();
                                this.criticalityType = CriticalityType.getInstance(i1);
                                if (this.criticalityType == null) {
                                    throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": Bad criticalityType value",
                                            CAPParsingComponentExceptionReason.MistypedParameter);
                                }
                            } else {
                                throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Bad tag of the CLASS_UNIVERSAL field",
                                        CAPParsingComponentExceptionReason.MistypedParameter);
                            }
                        }
                            break;

                        case Tag.CLASS_CONTEXT_SPECIFIC: {
                            if (tag == _ID_value) {
                                int len = ais.readLength();
                                if (ais.available() < len) {
                                    throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": not enouph data for a value field",
                                            CAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                this.data = new byte[len];
                                ais.read(this.data);
                            } else {
                                throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Bad tag of the CLASS_CONTEXT_SPECIFIC field",
                                        CAPParsingComponentExceptionReason.MistypedParameter);
                            }
                        }
                            break;

                        default:
                            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter 1 or 2 has bad tag class",
                                    CAPParsingComponentExceptionReason.MistypedParameter);
                    }
                }
            }

            num++;
        }

        if (this.data == null)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": value field is mandatory but not found", CAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws CAPException {

        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws CAPException {

        try {
            asnOutputStream.writeTag(tagClass, false, tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream aos) throws CAPException {

        try {
            if ((this.localCode == null && this.globalCode == null) || (this.localCode != null && this.globalCode != null))
                throw new CAPException("Error while decoding " + _PrimitiveName
                        + ": at least localCode or globalCode field must not be null");
            if (this.data == null)
                throw new CAPException("Error while decoding " + _PrimitiveName + ": value field must not be null");

            if (this.localCode != null) {
                aos.writeInteger(this.localCode);
            } else {
                aos.writeObjectIdentifier(this.globalCode);
            }

            if (this.criticalityType != null && this.criticalityType != CriticalityType.typeIgnore) {
                aos.writeInteger(Tag.CLASS_UNIVERSAL, Tag.ENUMERATED, this.criticalityType.getCode());
            }

            aos.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, true, _ID_value);
            int pos = aos.StartContentDefiniteLength();
            aos.write(data);
            aos.FinalizeContent(pos);
        } catch (IOException e) {
            throw new CAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (this.localCode != null) {
            sb.append("localCode=");
            sb.append(this.localCode);
        }
        if (this.globalCode != null) {
            sb.append("globalCode=[");
            sb.append(printDataArrLong(globalCode));
            sb.append("]");
        }
        if (this.criticalityType != null) {
            sb.append(", criticalityType=");
            sb.append(criticalityType);
        }
        if (this.data != null) {
            sb.append(", data=[");
            sb.append(printDataArr(data));
            sb.append("]");
        }
        sb.append("]");

        return sb.toString();
    }

    private String printDataArrLong(long[] arr) {
        StringBuilder sb = new StringBuilder();
        for (long b : arr) {
            sb.append(b);
            sb.append(", ");
        }

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

