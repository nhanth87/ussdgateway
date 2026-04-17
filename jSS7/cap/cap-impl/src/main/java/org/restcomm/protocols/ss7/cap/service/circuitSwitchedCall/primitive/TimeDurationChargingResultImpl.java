
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

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
import org.restcomm.protocols.ss7.cap.api.primitives.AChChargingAddress;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.ReceivingSideID;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.TimeDurationChargingResult;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.TimeInformation;
import org.restcomm.protocols.ss7.cap.primitives.AChChargingAddressImpl;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsImpl;
import org.restcomm.protocols.ss7.cap.primitives.ReceivingSideIDImpl;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;


/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
@JacksonXmlRootElement(localName = "timeDurationChargingResult")
public class TimeDurationChargingResultImpl extends SequenceBase implements TimeDurationChargingResult {

    private static final String PARTY_TO_CHARGE = "partyToCharge";
    private static final String TIME_INFORMATION = "timeInformation";
    private static final String LEG_ACTIVE = "legActive";
    private static final String CALL_LEG_RELEASED_AT_TCP_EXPIRY = "callLegReleasedAtTcpExpiry";
    private static final String EXTENSIONS = "extensions";
    private static final String ACH_CHARGING_ADDRESS = "aChChargingAddress";

    public static final int _ID_partyToCharge = 0;
    public static final int _ID_timeInformation = 1;
    public static final int _ID_legActive = 2;
    public static final int _ID_callLegReleasedAtTcpExpiry = 3;
    public static final int _ID_extensions = 4;
    public static final int _ID_aChChargingAddress = 5;

    @JacksonXmlProperty(localName = "partyToCharge")
    private ReceivingSideID partyToCharge;
    @JacksonXmlProperty(localName = "timeInformation")
    private TimeInformation timeInformation;
    @JacksonXmlProperty(localName = "legActive")
    private boolean legActive;
    @JacksonXmlProperty(localName = "callLegReleasedAtTcpExpiry")
    private boolean callLegReleasedAtTcpExpiry;
    @JacksonXmlProperty(localName = "extensions")
    private CAPExtensions extensions;
    @JacksonXmlProperty(localName = "aChChargingAddress")
    private AChChargingAddress aChChargingAddress;

    public TimeDurationChargingResultImpl() {
        super("TimeDurationChargingResult");
    }

    public TimeDurationChargingResultImpl(ReceivingSideID partyToCharge, TimeInformation timeInformation,
            boolean legActive, boolean callLegReleasedAtTcpExpiry, CAPExtensions extensions,
            AChChargingAddress aChChargingAddress) {
        super("TimeDurationChargingResult");
        this.partyToCharge = partyToCharge;
        this.timeInformation = timeInformation;
        this.legActive = legActive;
        this.callLegReleasedAtTcpExpiry = callLegReleasedAtTcpExpiry;
        this.extensions = extensions;
        this.aChChargingAddress = aChChargingAddress;
    }

    @Override
    public ReceivingSideID getPartyToCharge() {
        return partyToCharge;
    }

    @Override
    public TimeInformation getTimeInformation() {
        return timeInformation;
    }

    @Override
    public boolean getLegActive() {
        return legActive;
    }

    @Override
    public boolean getCallLegReleasedAtTcpExpiry() {
        return callLegReleasedAtTcpExpiry;
    }

    @Override
    public CAPExtensions getExtensions() {
        return extensions;
    }

    @Override
    public AChChargingAddress getAChChargingAddress() {
        return aChChargingAddress;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException,
            MAPParsingComponentException, IOException, AsnException {

        this.partyToCharge = null;
        this.timeInformation = null;
        this.legActive = true;
        this.callLegReleasedAtTcpExpiry = false;
        this.extensions = null;
        this.aChChargingAddress = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _ID_partyToCharge:
                        this.partyToCharge = new ReceivingSideIDImpl();
                        ((ReceivingSideIDImpl) this.partyToCharge).decodeAll(ais);
                        break;
                    case _ID_timeInformation:
                        this.timeInformation = new TimeInformationImpl();
                        ((TimeInformationImpl) this.timeInformation).decodeAll(ais);
                        break;
                    case _ID_legActive:
                        this.legActive = ais.readBoolean();
                        break;
                    case _ID_callLegReleasedAtTcpExpiry:
                        ais.readNull();
                        this.callLegReleasedAtTcpExpiry = true;
                        break;
                    case _ID_extensions:
                        this.extensions = new CAPExtensionsImpl();
                        ((CAPExtensionsImpl) this.extensions).decodeAll(ais);
                        break;
                    case _ID_aChChargingAddress:
                        this.aChChargingAddress = new AChChargingAddressImpl();
                        ((AChChargingAddressImpl) this.aChChargingAddress).decodeAll(ais);
                        break;

                    default:
                        ais.advanceElement();
                        break;
                }
            } else {
                ais.advanceElement();
            }
        }

        if (this.partyToCharge == null)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": partyToCharge is mandatory but not found",
                    CAPParsingComponentExceptionReason.MistypedParameter);

        if (this.timeInformation == null)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": timeInformation is mandatory but not found",
                    CAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {

        if (this.partyToCharge == null)
            throw new CAPException("Error while encoding " + _PrimitiveName + ": partyToCharge must not be null");
        if (this.timeInformation == null)
            throw new CAPException("Error while encoding " + _PrimitiveName + ": timeInformation must not be null");

        try {
            ((ReceivingSideIDImpl) this.partyToCharge).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _ID_partyToCharge);

            ((TimeInformationImpl) this.timeInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _ID_timeInformation);

            if (!this.legActive)
                asnOutputStream.writeBoolean(Tag.CLASS_CONTEXT_SPECIFIC, _ID_legActive, false);

            if (this.callLegReleasedAtTcpExpiry)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_callLegReleasedAtTcpExpiry);

            if (this.extensions != null)
                ((CAPExtensionsImpl) this.extensions).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_extensions);

            if (this.aChChargingAddress != null)
                ((AChChargingAddressImpl) this.aChChargingAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_aChChargingAddress);

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

        if (this.partyToCharge != null) {
            sb.append("partyToCharge=");
            sb.append(partyToCharge.toString());
        }
        if (this.timeInformation != null) {
            sb.append(", timeInformation=");
            sb.append(timeInformation.toString());
        }
        sb.append(", legActive=");
        sb.append(legActive);
        if (this.callLegReleasedAtTcpExpiry) {
            sb.append(", callLegReleasedAtTcpExpiry");
        }
        if (this.extensions != null) {
            sb.append(", extensions=");
            sb.append(extensions.toString());
        }
        if (this.aChChargingAddress != null) {
            sb.append(", aChChargingAddress=");
            sb.append(aChChargingAddress.toString());
        }

        sb.append("]");

        return sb.toString();
    }
}

