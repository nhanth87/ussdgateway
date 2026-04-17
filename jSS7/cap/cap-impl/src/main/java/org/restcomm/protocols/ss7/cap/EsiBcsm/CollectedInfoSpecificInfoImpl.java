package org.restcomm.protocols.ss7.cap.EsiBcsm;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.CollectedInfoSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "collectedInfoSpecificInfo")
public class CollectedInfoSpecificInfoImpl extends SequenceBase implements CollectedInfoSpecificInfo {

    public static final int _ID_calledPartyNumber = 0;

    @JacksonXmlProperty(localName = "calledPartyNumber")
    private CalledPartyNumberCap calledPartyNumber;

    public CollectedInfoSpecificInfoImpl() {
        super("CollectedInfoSpecificInfo");
    }

    public CollectedInfoSpecificInfoImpl(CalledPartyNumberCap calledPartyNumber) {
        super("CollectedInfoSpecificInfo");
        this.calledPartyNumber = calledPartyNumber;
    }

    @Override
    public CalledPartyNumberCap getCalledPartyNumber() {
        return calledPartyNumber;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException,
            MAPParsingComponentException, IOException, AsnException {

        this.calledPartyNumber = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _ID_calledPartyNumber:
                        this.calledPartyNumber = new CalledPartyNumberCapImpl();
                        ((CalledPartyNumberCapImpl) this.calledPartyNumber).decodeAll(ais);
                        break;

                    default:
                        ais.advanceElement();
                        break;
                }
            } else {
                ais.advanceElement();
            }
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {

        if (this.calledPartyNumber != null) {
            ((CalledPartyNumberCapImpl) this.calledPartyNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_calledPartyNumber);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.calledPartyNumber != null) {
            sb.append("calledPartyNumber=");
            sb.append(calledPartyNumber.toString());
        }

        sb.append("]");

        return sb.toString();
    }
}

