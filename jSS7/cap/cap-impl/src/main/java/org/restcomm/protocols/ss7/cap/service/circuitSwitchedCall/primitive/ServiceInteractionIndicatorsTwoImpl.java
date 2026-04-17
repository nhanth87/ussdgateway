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
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.BackwardServiceInteractionInd;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ConnectedNumberTreatmentInd;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.CwTreatmentIndicator;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.EctTreatmentIndicator;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ForwardServiceInteractionInd;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.HoldTreatmentIndicator;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.inap.api.primitives.BothwayThroughConnectionInd;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "serviceInteractionIndicatorsTwo")
public class ServiceInteractionIndicatorsTwoImpl extends SequenceBase implements ServiceInteractionIndicatorsTwo {

    public static final int _ID_forwardServiceInteractionInd = 0;
    public static final int _ID_backwardServiceInteractionInd = 1;
    public static final int _ID_bothwayThroughConnectionInd = 2;
    public static final int _ID_connectedNumberTreatmentInd = 4;
    public static final int _ID_nonCUGCall = 13;
    public static final int _ID_holdTreatmentIndicator = 50;
    public static final int _ID_cwTreatmentIndicator = 51;
    public static final int _ID_ectTreatmentIndicator = 52;

    private static final String FORWARD_SERVICE_INTERACTION_IND = "forwardServiceInteractionInd";
    private static final String BACKWARD_SERVICE_INTERACTION_IND = "backwardServiceInteractionInd";
    private static final String BOTHWAY_THROUGH_CONNECTION_IND = "bothwayThroughConnectionInd";
    private static final String CONNECTED_NUMBER_TREATMENT_IND = "connectedNumberTreatmentInd";
    private static final String NON_CUG_CALL = "nonCUGCall";
    private static final String HOLD_TREATMENT_INDICATOR = "holdTreatmentIndicator";
    private static final String CW_TREATMENT_INDICATOR = "cwTreatmentIndicator";
    private static final String ECT_TREATMENT_INDICATOR = "ectTreatmentIndicator";

    private ForwardServiceInteractionInd forwardServiceInteractionInd;
    private BackwardServiceInteractionInd backwardServiceInteractionInd;
    private BothwayThroughConnectionInd bothwayThroughConnectionInd;
    private ConnectedNumberTreatmentInd connectedNumberTreatmentInd;
    private boolean nonCUGCall;
    private HoldTreatmentIndicator holdTreatmentIndicator;
    private CwTreatmentIndicator cwTreatmentIndicator;
    private EctTreatmentIndicator ectTreatmentIndicator;

    public ServiceInteractionIndicatorsTwoImpl() {
        super("ServiceInteractionIndicatorsTwo");
    }

    public ServiceInteractionIndicatorsTwoImpl(ForwardServiceInteractionInd forwardServiceInteractionInd,
            BackwardServiceInteractionInd backwardServiceInteractionInd, BothwayThroughConnectionInd bothwayThroughConnectionInd,
            ConnectedNumberTreatmentInd connectedNumberTreatmentInd, boolean nonCUGCall, HoldTreatmentIndicator holdTreatmentIndicator,
            CwTreatmentIndicator cwTreatmentIndicator, EctTreatmentIndicator ectTreatmentIndicator) {
        super("ServiceInteractionIndicatorsTwo");

        this.forwardServiceInteractionInd = forwardServiceInteractionInd;
        this.backwardServiceInteractionInd = backwardServiceInteractionInd;
        this.bothwayThroughConnectionInd = bothwayThroughConnectionInd;
        this.connectedNumberTreatmentInd = connectedNumberTreatmentInd;
        this.nonCUGCall = nonCUGCall;
        this.holdTreatmentIndicator = holdTreatmentIndicator;
        this.cwTreatmentIndicator = cwTreatmentIndicator;
        this.ectTreatmentIndicator = ectTreatmentIndicator;
    }

    @Override
    public ForwardServiceInteractionInd getForwardServiceInteractionInd() {
        return forwardServiceInteractionInd;
    }

    @Override
    public BackwardServiceInteractionInd getBackwardServiceInteractionInd() {
        return backwardServiceInteractionInd;
    }

    @Override
    public BothwayThroughConnectionInd getBothwayThroughConnectionInd() {
        return bothwayThroughConnectionInd;
    }

    @Override
    public ConnectedNumberTreatmentInd getConnectedNumberTreatmentInd() {
        return connectedNumberTreatmentInd;
    }

    @Override
    public boolean getNonCUGCall() {
        return nonCUGCall;
    }

    @Override
    public HoldTreatmentIndicator getHoldTreatmentIndicator() {
        return holdTreatmentIndicator;
    }

    @Override
    public CwTreatmentIndicator getCwTreatmentIndicator() {
        return cwTreatmentIndicator;
    }

    @Override
    public EctTreatmentIndicator getEctTreatmentIndicator() {
        return ectTreatmentIndicator;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException, IOException, AsnException {

        this.forwardServiceInteractionInd = null;
        this.backwardServiceInteractionInd = null;
        this.bothwayThroughConnectionInd = null;
        this.connectedNumberTreatmentInd = null;
        this.nonCUGCall = false;
        this.holdTreatmentIndicator = null;
        this.cwTreatmentIndicator = null;
        this.ectTreatmentIndicator = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                case _ID_forwardServiceInteractionInd:
                    this.forwardServiceInteractionInd = new ForwardServiceInteractionIndImpl();
                    ((ForwardServiceInteractionIndImpl) this.forwardServiceInteractionInd).decodeAll(ais);
                    break;
                case _ID_backwardServiceInteractionInd:
                    this.backwardServiceInteractionInd = new BackwardServiceInteractionIndImpl();
                    ((BackwardServiceInteractionIndImpl) this.backwardServiceInteractionInd).decodeAll(ais);
                    break;
                case _ID_bothwayThroughConnectionInd:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".bothwayThroughConnectionInd: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    int i1 = (int) ais.readInteger();
                    this.bothwayThroughConnectionInd = BothwayThroughConnectionInd.getInstance(i1);
                    break;
                case _ID_connectedNumberTreatmentInd:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".connectedNumberTreatmentInd: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    i1 = (int) ais.readInteger();
                    this.connectedNumberTreatmentInd = ConnectedNumberTreatmentInd.getInstance(i1);
                    break;
                case _ID_nonCUGCall:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".nonCUGCall: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    ais.readNull();
                    this.nonCUGCall = true;
                    break;
                case _ID_holdTreatmentIndicator:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".holdTreatmentIndicator: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    byte[] data = ais.readOctetString();
                    if (data == null || data.length == 0)
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".holdTreatmentIndicator: Parameter length is null", CAPParsingComponentExceptionReason.MistypedParameter);
                    i1 = data[0] & 0xFF;
                    this.holdTreatmentIndicator = HoldTreatmentIndicator.getInstance(i1);
                    break;
                case _ID_cwTreatmentIndicator:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".cwTreatmentIndicator: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    data = ais.readOctetString();
                    if (data == null || data.length == 0)
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".cwTreatmentIndicator: Parameter length is null", CAPParsingComponentExceptionReason.MistypedParameter);
                    i1 = data[0] & 0xFF;
                    this.cwTreatmentIndicator = CwTreatmentIndicator.getInstance(i1);
                    break;
                case _ID_ectTreatmentIndicator:
                    if (!ais.isTagPrimitive())
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".ectTreatmentIndicator: Parameter is not primitive", CAPParsingComponentExceptionReason.MistypedParameter);
                    data = ais.readOctetString();
                    if (data == null || data.length == 0)
                        throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".ectTreatmentIndicator: Parameter length is null", CAPParsingComponentExceptionReason.MistypedParameter);
                    i1 = data[0] & 0xFF;
                    this.ectTreatmentIndicator = EctTreatmentIndicator.getInstance(i1);
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

        try {

            if (this.forwardServiceInteractionInd != null) {
                ((ForwardServiceInteractionIndImpl) this.forwardServiceInteractionInd).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_forwardServiceInteractionInd);
            }
            if (this.backwardServiceInteractionInd != null) {
                ((BackwardServiceInteractionIndImpl) this.backwardServiceInteractionInd).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_backwardServiceInteractionInd);
            }
            if (this.bothwayThroughConnectionInd != null) {
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_bothwayThroughConnectionInd, this.bothwayThroughConnectionInd.getCode());
            }
            if (this.connectedNumberTreatmentInd != null) {
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_connectedNumberTreatmentInd, this.connectedNumberTreatmentInd.getCode());
            }
            if (this.nonCUGCall) {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_nonCUGCall);
            }
            if (this.holdTreatmentIndicator != null) {
                byte[] data = new byte[] { (byte) this.holdTreatmentIndicator.getCode() };
                asnOutputStream.writeOctetString(Tag.CLASS_CONTEXT_SPECIFIC, _ID_holdTreatmentIndicator, data);
            }
            if (this.cwTreatmentIndicator != null) {
                byte[] data = new byte[] { (byte) this.cwTreatmentIndicator.getCode() };
                asnOutputStream.writeOctetString(Tag.CLASS_CONTEXT_SPECIFIC, _ID_cwTreatmentIndicator, data);
            }
            if (this.ectTreatmentIndicator != null) {
                byte[] data = new byte[] { (byte) this.ectTreatmentIndicator.getCode() };
                asnOutputStream.writeOctetString(Tag.CLASS_CONTEXT_SPECIFIC, _ID_ectTreatmentIndicator, data);
            }
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

        if (this.forwardServiceInteractionInd != null) {
            sb.append("forwardServiceInteractionInd=");
            sb.append(forwardServiceInteractionInd.toString());
        }
        if (this.backwardServiceInteractionInd != null) {
            sb.append(", backwardServiceInteractionInd=");
            sb.append(backwardServiceInteractionInd.toString());
        }
        if (this.bothwayThroughConnectionInd != null) {
            sb.append(", bothwayThroughConnectionInd=");
            sb.append(bothwayThroughConnectionInd.toString());
        }
        if (this.connectedNumberTreatmentInd != null) {
            sb.append(", connectedNumberTreatmentInd=");
            sb.append(connectedNumberTreatmentInd.toString());
        }
        if (this.nonCUGCall) {
            sb.append(", nonCUGCall");
        }
        if (this.holdTreatmentIndicator != null) {
            sb.append(", holdTreatmentIndicator=");
            sb.append(holdTreatmentIndicator.toString());
        }
        if (this.cwTreatmentIndicator != null) {
            sb.append(", cwTreatmentIndicator=");
            sb.append(cwTreatmentIndicator.toString());
        }
        if (this.ectTreatmentIndicator != null) {
            sb.append(", ectTreatmentIndicator=");
            sb.append(ectTreatmentIndicator.toString());
        }

        sb.append("]");

        return sb.toString();
    }
}

