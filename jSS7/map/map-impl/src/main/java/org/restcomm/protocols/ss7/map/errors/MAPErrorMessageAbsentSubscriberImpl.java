
package org.restcomm.protocols.ss7.map.errors;

import java.io.IOException;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.errors.AbsentSubscriberReason;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorCode;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageAbsentSubscriber;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;

/**
 *
 * @author sergey vetyutnev
 * @author amit bhayani
 */
@JacksonXmlRootElement(localName = "mAPErrorMessageAbsentSubscriberImpl")
public class MAPErrorMessageAbsentSubscriberImpl extends MAPErrorMessageImpl implements MAPErrorMessageAbsentSubscriber {

    public static final int AbsentSubscriberReason_TAG = 0;

    private MAPExtensionContainer extensionContainer;
    private AbsentSubscriberReason absentSubscriberReason;

    private Boolean mwdSet;

    /**
     * For MAP V2-3
     *
     * @param extensionContainer
     * @param absentSubscriberReason
     */
    public MAPErrorMessageAbsentSubscriberImpl(MAPExtensionContainer extensionContainer, AbsentSubscriberReason absentSubscriberReason) {
        super((long) MAPErrorCode.absentSubscriber);
        this.extensionContainer = extensionContainer;
        this.absentSubscriberReason = absentSubscriberReason;
    }

    /**
     * For MAP V1
     *
     * @param mwdSet
     */
    public MAPErrorMessageAbsentSubscriberImpl(Boolean mwdSet) {
        super((long) MAPErrorCode.absentSubscriber);
        this.mwdSet = mwdSet;
    }

    public MAPErrorMessageAbsentSubscriberImpl() {
        super((long) MAPErrorCode.absentSubscriber);
    }

    public boolean isEmAbsentSubscriber() {
        return true;
    }

    public MAPErrorMessageAbsentSubscriber getEmAbsentSubscriber() {
        return this;
    }

    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    public AbsentSubscriberReason getAbsentSubscriberReason() {
        return this.absentSubscriberReason;
    }

    public void setExtensionContainer(MAPExtensionContainer extensionContainer) {
        this.extensionContainer = extensionContainer;
    }

    public void setAbsentSubscriberReason(AbsentSubscriberReason absentSubscriberReason) {
        this.absentSubscriberReason = absentSubscriberReason;
    }

    @Override
    public Boolean getMwdSet() {
        return mwdSet;
    }

    @Override
    public void setMwdSet(Boolean val) {
        mwdSet = val;
    }

    public int getTag() throws MAPException {
        if (this.mwdSet != null)
            return Tag.BOOLEAN;
        else
            return Tag.SEQUENCE;
    }

    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    public boolean getIsPrimitive() {
        if (this.mwdSet != null)
            return true;
        else
            return false;
    }

    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding MAPErrorMessageAbsentSubscriber: "
                    + e.getMessage(), e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding MAPErrorMessageAbsentSubscriber: "
                    + e.getMessage(), e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding MAPErrorMessageAbsentSubscriber: "
                    + e.getMessage(), e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding MAPErrorMessageAbsentSubscriber: "
                    + e.getMessage(), e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream localAsnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.extensionContainer = null;
        this.absentSubscriberReason = null;
        this.mwdSet = null;

        switch (localAsnInputStream.getTag()) {
            case Tag.SEQUENCE:
                if (localAsnInputStream.getTagClass() != Tag.CLASS_UNIVERSAL || localAsnInputStream.isTagPrimitive())
                    throw new MAPParsingComponentException(
                            "Error decoding MAPErrorMessageAbsentSubscriber: bad tag class or parameter is primitive",
                            MAPParsingComponentExceptionReason.MistypedParameter);

                AsnInputStream ais = localAsnInputStream.readSequenceStreamData(length);

                while (true) {
                    if (ais.available() == 0)
                        break;

                    int tag = ais.readTag();

                    switch (ais.getTagClass()) {
                        case Tag.CLASS_UNIVERSAL:
                            switch (tag) {
                                case Tag.SEQUENCE:
                                    this.extensionContainer = new MAPExtensionContainerImpl();
                                    ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                    break;

                                default:
                                    ais.advanceElement();
                                    break;
                            }
                            break;

                        case Tag.CLASS_CONTEXT_SPECIFIC:
                            switch (tag) {
                                case AbsentSubscriberReason_TAG:
                                    int code = (int) ais.readInteger();
                                    this.absentSubscriberReason = AbsentSubscriberReason.getInstance(code);
                                    break;

                                default:
                                    ais.advanceElement();
                                    break;
                            }
                            break;

                        default:
                            ais.advanceElement();
                            break;
                    }
                }
                break;

            case Tag.BOOLEAN:
                if (localAsnInputStream.getTagClass() != Tag.CLASS_UNIVERSAL || !localAsnInputStream.isTagPrimitive())
                    throw new MAPParsingComponentException(
                            "Error decoding MAPErrorMessageAbsentSubscriber: bad tag class or parameter is not primitive",
                            MAPParsingComponentExceptionReason.MistypedParameter);

                this.mwdSet = localAsnInputStream.readBooleanData(length);
                break;

            default:
                throw new MAPParsingComponentException("Error decoding MAPErrorMessageAbsentSubscriber: bad tag",
                        MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding MAPErrorMessageAbsentSubscriber: " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.mwdSet != null) {
            try {
                asnOutputStream.writeBooleanData(this.mwdSet);
            } catch (IOException e) {
                throw new MAPException("IOException when encoding MAPErrorMessageAbsentSubscriber: " + e.getMessage(), e);
            }
        } else {
            if (this.absentSubscriberReason == null && this.extensionContainer == null)
                return;

            try {
                if (this.extensionContainer != null)
                    ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);
                if (this.absentSubscriberReason != null)
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, AbsentSubscriberReason_TAG,
                            this.absentSubscriberReason.getCode());

            } catch (IOException e) {
                throw new MAPException("IOException when encoding MAPErrorMessageAbsentSubscriber: " + e.getMessage(), e);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding MAPErrorMessageAbsentSubscriber: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("MAPErrorMessageAbsentSubscriber [");
        if (this.mwdSet != null)
            sb.append("mwdSet=" + this.mwdSet.toString());
        if (this.extensionContainer != null)
            sb.append(", extensionContainer=" + this.extensionContainer.toString());
        if (this.absentSubscriberReason != null)
            sb.append(", absentSubscriberReason=" + this.absentSubscriberReason.toString());
        sb.append("]");

        return sb.toString();
    }

}
