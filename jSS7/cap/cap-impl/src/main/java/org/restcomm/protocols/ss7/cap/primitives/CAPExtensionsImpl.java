package org.restcomm.protocols.ss7.cap.primitives;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;
import java.util.ArrayList;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.ExtensionField;
import org.restcomm.protocols.ss7.map.primitives.ArrayListSerializingBase;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "cAPExtensions")
public class CAPExtensionsImpl implements CAPExtensions, CAPAsnPrimitive {

    public static final String _PrimitiveName = "CAPExtensions";

    private static final String EXTENSION_FIELD = "extensionField";
    private static final String EXTENSION_FIELD_LIST = "extensionFieldList";

    private ArrayList<ExtensionField> extensionFields;

    public CAPExtensionsImpl() {
    }

    public CAPExtensionsImpl(ArrayList<ExtensionField> fieldsList) {
        this.extensionFields = fieldsList;
    }

    @Override
    public ArrayList<ExtensionField> getExtensionFields() {
        return extensionFields;
    }

    @Override
    public void setExtensionFields(ArrayList<ExtensionField> fieldsList) {
        this.extensionFields = fieldsList;
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

        this.extensionFields = null;

        ArrayList<ExtensionField> res = new ArrayList<ExtensionField>();
        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || tag != Tag.SEQUENCE || ais.isTagPrimitive())
                throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                        + ": Bad ExtensionField tag or tag class or is primitive",
                        CAPParsingComponentExceptionReason.MistypedParameter);

            ExtensionFieldImpl elem = new ExtensionFieldImpl();
            elem.decodeAll(ais);
            res.add(elem);
        }

        this.extensionFields = res;
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
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {

        if (this.extensionFields == null)
            throw new CAPException("Error while decoding " + _PrimitiveName + ": extensionFields field must not be null");
        if (this.extensionFields.size() < 1 || this.extensionFields.size() > 10)
            throw new CAPException("Error while decoding " + _PrimitiveName
                    + ": extensionFields field length must be from 1 to 10");

        for (ExtensionField fld : this.extensionFields) {
            ((ExtensionFieldImpl) fld).encodeAll(asnOutputStream);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.extensionFields != null) {
            boolean isFirst = true;
            for (ExtensionField fld : this.extensionFields) {
                if (isFirst)
                    isFirst = false;
                else
                    sb.append("\n");
                sb.append(fld.toString());
            }
        }
        sb.append("]");

        return sb.toString();
    }
public static class CAPExtensions_ExtensionFields extends ArrayListSerializingBase<ExtensionField> {

        public CAPExtensions_ExtensionFields() {
            super(EXTENSION_FIELD, ExtensionFieldImpl.class);
        }

        public CAPExtensions_ExtensionFields(ArrayList<ExtensionField> data) {
            super(EXTENSION_FIELD, ExtensionFieldImpl.class, data);
        }

    }
}

