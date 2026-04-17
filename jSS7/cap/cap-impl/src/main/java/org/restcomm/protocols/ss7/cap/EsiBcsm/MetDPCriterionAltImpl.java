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
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.MetDPCriterionAlt;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.inap.api.INAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;


/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "metDPCriterionAlt")
public class MetDPCriterionAltImpl extends SequenceBase implements MetDPCriterionAlt {

    public MetDPCriterionAltImpl() {
        super("MetDPCriterionAlt");
    }

    @Override
    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException, IOException, AsnException, MAPParsingComponentException,
            INAPParsingComponentException {
        // TODO Auto-generated method stub

//        this.routeNotPermitted = false;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
//                    case _ID_routeNotPermitted:
//                        ais.readNull();
//                        this.routeNotPermitted = true;
//                        break;

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
//        try {
//            if (this.routeNotPermitted)
//                asnOs.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_routeNotPermitted);
//        } catch (IOException e) {
//            throw new CAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
//        } catch (AsnException e) {
//            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
//        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

//        if (this.routeNotPermitted) {
//            sb.append("routeNotPermitted");
//        }

        sb.append("]");

        return sb.toString();
    }
}

