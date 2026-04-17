package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPMessageType;
import org.restcomm.protocols.ss7.cap.api.CAPOperationCode;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CallingPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CauseCap;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.OriginalCalledNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.RedirectingPartyIDCap;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.CalledPartyBCDNumber;
import org.restcomm.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.restcomm.protocols.ss7.cap.api.primitives.TimeAndTimezone;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.BearerCapability;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.CGEncountered;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.IPSSPCapabilities;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.InitialDPArgExtension;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.isup.CallingPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.isup.CauseCapImpl;
import org.restcomm.protocols.ss7.cap.isup.DigitsImpl;
import org.restcomm.protocols.ss7.cap.isup.LocationNumberCapImpl;
import org.restcomm.protocols.ss7.cap.isup.OriginalCalledNumberCapImpl;
import org.restcomm.protocols.ss7.cap.isup.RedirectingPartyIDCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsImpl;
import org.restcomm.protocols.ss7.cap.primitives.CalledPartyBCDNumberImpl;
import org.restcomm.protocols.ss7.cap.primitives.TimeAndTimezoneImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.BearerCapabilityImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.CarrierImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.IPSSPCapabilitiesImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.InitialDPArgExtensionImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwoImpl;
import org.restcomm.protocols.ss7.inap.api.INAPException;
import org.restcomm.protocols.ss7.inap.api.INAPParsingComponentException;
import org.restcomm.protocols.ss7.inap.api.isup.CallingPartysCategoryInap;
import org.restcomm.protocols.ss7.inap.api.isup.HighLayerCompatibilityInap;
import org.restcomm.protocols.ss7.inap.api.isup.RedirectionInformationInap;
import org.restcomm.protocols.ss7.inap.isup.CallingPartysCategoryInapImpl;
import org.restcomm.protocols.ss7.inap.isup.HighLayerCompatibilityInapImpl;
import org.restcomm.protocols.ss7.inap.isup.RedirectionInformationInapImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.service.callhandling.CallReferenceNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGIndex;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.callhandling.CallReferenceNumberImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.SubscriberStateImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CUGIndexImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CUGInterlockImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "initialDPRequest")
public class InitialDPRequestImpl extends CircuitSwitchedCallMessageImpl implements InitialDPRequest {

    public static final int _ID_serviceKey = 0;
    public static final int _ID_calledPartyNumber = 2;
    public static final int _ID_callingPartyNumber = 3;
    public static final int _ID_callingPartysCategory = 5;
    public static final int _ID_cGEncountered = 7;
    public static final int _ID_iPSSPCapabilities = 8;
    public static final int _ID_locationNumber = 10;
    public static final int _ID_originalCalledPartyID = 12;
    public static final int _ID_extensions = 15;
    public static final int _ID_highLayerCompatibility = 23;
    public static final int _ID_additionalCallingPartyNumber = 25;
    public static final int _ID_bearerCapability = 27;
    public static final int _ID_eventTypeBCSM = 28;
    public static final int _ID_redirectingPartyID = 29;
    public static final int _ID_redirectionInformation = 30;
    public static final int _ID_cause = 17;
    public static final int _ID_serviceInteractionIndicatorsTwo = 32;
    public static final int _ID_carrier = 37;
    public static final int _ID_cug_Index = 45;
    public static final int _ID_cug_Interlock = 46;
    public static final int _ID_cug_OutgoingAccess = 47;
    public static final int _ID_iMSI = 50;
    public static final int _ID_subscriberState = 51;
    public static final int _ID_locationInformation = 52;
    public static final int _ID_ext_basicServiceCode = 53;
    public static final int _ID_callReferenceNumber = 54;
    public static final int _ID_mscAddress = 55;
    public static final int _ID_calledPartyBCDNumber = 56;
    public static final int _ID_timeAndTimezone = 57;
    public static final int _ID_callForwardingSS_Pending = 58;
    public static final int _ID_initialDPArgExtension = 59;

    private static final String IS_CAP_VERSION_3_OR_LATER = "isCAPVersion3orLater";
    private static final String SERVICE_KEY = "serviceKey";
    private static final String CALLED_PARTY_NUMBER = "calledPartyNumber";
    private static final String CALLING_PARTY_NUMBER = "callingPartyNumber";
    private static final String CALLING_PARTYS_CATEGORY = "callingPartysCategory";
    private static final String CG_ENCOUNTERED = "cgEncountered";
    private static final String IPSSP_CAPABILITIES = "ipsspCapabilities";
    private static final String LOCATION_NUMBER = "locationNumber";
    private static final String ORIGINAL_CALLED_PARTY_ID = "originalCalledPartyID";
    private static final String EXTENSIONS = "extensions";
    private static final String HIGH_LAYER_COMPATIBILITY = "highLayerCompatibility";
    private static final String ADDITIONAL_CALLING_PARTY_NUMBER = "additionalCallingPartyNumber";
    private static final String BEARER_CAPABILITY = "bearerCapability";
    private static final String EVENT_TYPE_BCSM = "eventTypeBCSM";
    private static final String REDIRECTING_PARTY_ID = "redirectingPartyID";
    private static final String REDIRECTION_INFORMATION = "redirectionInformation";
    private static final String CAUSE = "cause";
    private static final String SERVICE_INTERACTION_INDICATORS_TWO = "serviceInteractionIndicatorsTwo";
    private static final String CARRIER = "carrier";
    private static final String CUG_INDEX = "cugIndex";
    private static final String CUG_INTERLOCK = "cugInterlock";
    private static final String CUG_OUTGOING_ACCESS = "cugOutgoingAccess";
    private static final String IMSI = "imsi";
    private static final String SUBSCRIBER_STATE = "subscriberState";
    private static final String LOCATION_INFORMATION = "locationInformation";
    private static final String EXT_BASIC_SERVICE_CODE = "extBasicServiceCode";
    private static final String CALL_REFERENCE_NUMBER = "callReferenceNumber";
    private static final String MSC_ADDRESS = "mscAddress";
    private static final String CALLED_PARTY_BCD_NUMBER = "calledPartyBCDNumber";
    private static final String TIME_AND_TIMEZONE = "timeAndTimezone";
    private static final String CALL_FORWARDING_SS_PENDING = "callForwardingSSPending";
    private static final String INITIAL_DP_ARG_EXTENSION = "initialDPArgExtension";

    public static final String _PrimitiveName = "InitialDPRequestIndication";

    @JacksonXmlProperty(localName = "serviceKey")
    private int serviceKey;
    @JacksonXmlProperty(localName = "calledPartyNumber")
    private CalledPartyNumberCap calledPartyNumber;
    @JacksonXmlProperty(localName = "callingPartyNumber")
    private CallingPartyNumberCap callingPartyNumber;
    @JacksonXmlProperty(localName = "callingPartysCategory")
    private CallingPartysCategoryInap callingPartysCategory;
    @JacksonXmlProperty(localName = "cgEncountered")
    private CGEncountered CGEncountered;
    @JacksonXmlProperty(localName = "ipsspCapabilities")
    private IPSSPCapabilities IPSSPCapabilities;
    @JacksonXmlProperty(localName = "locationNumber")
    private LocationNumberCap locationNumber;
    @JacksonXmlProperty(localName = "originalCalledPartyID")
    private OriginalCalledNumberCap originalCalledPartyID;
    @JacksonXmlProperty(localName = "extensions")
    private CAPExtensions extensions;
    @JacksonXmlProperty(localName = "highLayerCompatibility")
    private HighLayerCompatibilityInap highLayerCompatibility;
    @JacksonXmlProperty(localName = "additionalCallingPartyNumber")
    private Digits additionalCallingPartyNumber;
    @JacksonXmlProperty(localName = "bearerCapability")
    private BearerCapability bearerCapability;
    @JacksonXmlProperty(localName = "eventTypeBCSM")
    private EventTypeBCSM eventTypeBCSM;
    @JacksonXmlProperty(localName = "redirectingPartyID")
    private RedirectingPartyIDCap redirectingPartyID;
    @JacksonXmlProperty(localName = "redirectionInformation")
    private RedirectionInformationInap redirectionInformation;
    @JacksonXmlProperty(localName = "cause")
    private CauseCap cause;
    @JacksonXmlProperty(localName = "serviceInteractionIndicatorsTwo")
    private ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo;
    @JacksonXmlProperty(localName = "carrier")
    private Carrier carrier;
    @JacksonXmlProperty(localName = "cugIndex")
    private CUGIndex cugIndex;
    @JacksonXmlProperty(localName = "cugInterlock")
    private CUGInterlock cugInterlock;
    @JacksonXmlProperty(localName = "cugOutgoingAccess")
    private boolean cugOutgoingAccess;
    @JacksonXmlProperty(localName = "imsi")
    private IMSI imsi;
    @JacksonXmlProperty(localName = "subscriberState")
    private SubscriberState subscriberState;
    @JacksonXmlProperty(localName = "locationInformation")
    private LocationInformation locationInformation;
    @JacksonXmlProperty(localName = "extBasicServiceCode")
    private ExtBasicServiceCode extBasicServiceCode;
    @JacksonXmlProperty(localName = "callReferenceNumber")
    private CallReferenceNumber callReferenceNumber;
    @JacksonXmlProperty(localName = "mscAddress")
    private ISDNAddressString mscAddress;
    @JacksonXmlProperty(localName = "calledPartyBCDNumber")
    private CalledPartyBCDNumber calledPartyBCDNumber;
    @JacksonXmlProperty(localName = "timeAndTimezone")
    private TimeAndTimezone timeAndTimezone;
    @JacksonXmlProperty(localName = "callForwardingSSPending")
    private boolean callForwardingSSPending;
    @JacksonXmlProperty(localName = "initialDPArgExtension")
    private InitialDPArgExtension initialDPArgExtension;

    private boolean isCAPVersion3orLater;

    /**
     * This constructor is only for deserialization purpose
     */
    public InitialDPRequestImpl() {
    }

    public InitialDPRequestImpl(boolean isCAPVersion3orLater) {
        this.isCAPVersion3orLater = isCAPVersion3orLater;
    }

    public InitialDPRequestImpl(int serviceKey, CalledPartyNumberCap calledPartyNumber,
            CallingPartyNumberCap callingPartyNumber, CallingPartysCategoryInap callingPartysCategory,
            CGEncountered CGEncountered, IPSSPCapabilities IPSSPCapabilities, LocationNumberCap locationNumber,
            OriginalCalledNumberCap originalCalledPartyID, CAPExtensions extensions,
            HighLayerCompatibilityInap highLayerCompatibility, Digits additionalCallingPartyNumber,
            BearerCapability bearerCapability, EventTypeBCSM eventTypeBCSM, RedirectingPartyIDCap redirectingPartyID,
            RedirectionInformationInap redirectionInformation, CauseCap cause,
            ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo, Carrier carrier, CUGIndex cugIndex,
            CUGInterlock cugInterlock, boolean cugOutgoingAccess, IMSI imsi, SubscriberState subscriberState,
            LocationInformation locationInformation, ExtBasicServiceCode extBasicServiceCode,
            CallReferenceNumber callReferenceNumber, ISDNAddressString mscAddress, CalledPartyBCDNumber calledPartyBCDNumber,
            TimeAndTimezone timeAndTimezone, boolean callForwardingSSPending, InitialDPArgExtension initialDPArgExtension,
            boolean isCAPVersion3orLater) {
        this.serviceKey = serviceKey;
        this.calledPartyNumber = calledPartyNumber;
        this.callingPartyNumber = callingPartyNumber;
        this.callingPartysCategory = callingPartysCategory;
        this.CGEncountered = CGEncountered;
        this.IPSSPCapabilities = IPSSPCapabilities;
        this.locationNumber = locationNumber;
        this.originalCalledPartyID = originalCalledPartyID;
        this.extensions = extensions;
        this.highLayerCompatibility = highLayerCompatibility;
        this.additionalCallingPartyNumber = additionalCallingPartyNumber;
        this.bearerCapability = bearerCapability;
        this.eventTypeBCSM = eventTypeBCSM;
        this.redirectingPartyID = redirectingPartyID;
        this.redirectionInformation = redirectionInformation;
        this.cause = cause;
        this.serviceInteractionIndicatorsTwo = serviceInteractionIndicatorsTwo;
        this.carrier = carrier;
        this.cugIndex = cugIndex;
        this.cugInterlock = cugInterlock;
        this.cugOutgoingAccess = cugOutgoingAccess;
        this.imsi = imsi;
        this.subscriberState = subscriberState;
        this.locationInformation = locationInformation;
        this.extBasicServiceCode = extBasicServiceCode;
        this.callReferenceNumber = callReferenceNumber;
        this.mscAddress = mscAddress;
        this.calledPartyBCDNumber = calledPartyBCDNumber;
        this.timeAndTimezone = timeAndTimezone;
        this.callForwardingSSPending = callForwardingSSPending;
        this.initialDPArgExtension = initialDPArgExtension;
        this.isCAPVersion3orLater = isCAPVersion3orLater;
    }

    @Override
    public CAPMessageType getMessageType() {
        return CAPMessageType.initialDP_Request;
    }

    @Override
    public int getOperationCode() {
        return CAPOperationCode.initialDP;
    }

    @JsonIgnore
    @Override
    public int getServiceKey() {
        return this.serviceKey;
    }

    @JsonIgnore
    @Override
    public CalledPartyNumberCap getCalledPartyNumber() {
        return this.calledPartyNumber;
    }

    @JsonIgnore
    @Override
    public CallingPartyNumberCap getCallingPartyNumber() {
        return callingPartyNumber;
    }

    @JsonIgnore
    @Override
    public CallingPartysCategoryInap getCallingPartysCategory() {
        return callingPartysCategory;
    }

    @JsonIgnore
    @Override
    public CGEncountered getCGEncountered() {
        return CGEncountered;
    }

    @JsonIgnore
    @Override
    public IPSSPCapabilities getIPSSPCapabilities() {
        return IPSSPCapabilities;
    }

    @JsonIgnore
    @Override
    public LocationNumberCap getLocationNumber() {
        return locationNumber;
    }

    @JsonIgnore
    @Override
    public OriginalCalledNumberCap getOriginalCalledPartyID() {
        return originalCalledPartyID;
    }

    @JsonIgnore
    @Override
    public CAPExtensions getExtensions() {
        return extensions;
    }

    @JsonIgnore
    @Override
    public HighLayerCompatibilityInap getHighLayerCompatibility() {
        return highLayerCompatibility;
    }

    @JsonIgnore
    @Override
    public Digits getAdditionalCallingPartyNumber() {
        return additionalCallingPartyNumber;
    }

    @JsonIgnore
    @Override
    public BearerCapability getBearerCapability() {
        return bearerCapability;
    }

    @JsonIgnore
    @Override
    public EventTypeBCSM getEventTypeBCSM() {
        return eventTypeBCSM;
    }

    @JsonIgnore
    @Override
    public RedirectingPartyIDCap getRedirectingPartyID() {
        return redirectingPartyID;
    }

    @JsonIgnore
    @Override
    public RedirectionInformationInap getRedirectionInformation() {
        return redirectionInformation;
    }

    @JsonIgnore
    @Override
    public CauseCap getCause() {
        return cause;
    }

    @JsonIgnore
    @Override
    public ServiceInteractionIndicatorsTwo getServiceInteractionIndicatorsTwo() {
        return serviceInteractionIndicatorsTwo;
    }

    @JsonIgnore
    @Override
    public Carrier getCarrier() {
        return carrier;
    }

    @JsonIgnore
    @Override
    public CUGIndex getCugIndex() {
        return cugIndex;
    }

    @JsonIgnore
    @Override
    public CUGInterlock getCugInterlock() {
        return cugInterlock;
    }

    @JsonIgnore
    @Override
    public boolean getCugOutgoingAccess() {
        return cugOutgoingAccess;
    }

    @JsonIgnore
    @Override
    public IMSI getIMSI() {
        return imsi;
    }

    @JsonIgnore
    @Override
    public SubscriberState getSubscriberState() {
        return subscriberState;
    }

    @JsonIgnore
    @Override
    public LocationInformation getLocationInformation() {
        return locationInformation;
    }

    @JsonIgnore
    @Override
    public ExtBasicServiceCode getExtBasicServiceCode() {
        return extBasicServiceCode;
    }

    @JsonIgnore
    @Override
    public CallReferenceNumber getCallReferenceNumber() {
        return callReferenceNumber;
    }

    @JsonIgnore
    @Override
    public ISDNAddressString getMscAddress() {
        return mscAddress;
    }

    @JsonIgnore
    @Override
    public CalledPartyBCDNumber getCalledPartyBCDNumber() {
        return calledPartyBCDNumber;
    }

    @JsonIgnore
    @Override
    public TimeAndTimezone getTimeAndTimezone() {
        return timeAndTimezone;
    }

    @JsonIgnore
    @Override
    public boolean getCallForwardingSSPending() {
        return callForwardingSSPending;
    }

    @JsonIgnore
    @Override
    public InitialDPArgExtension getInitialDPArgExtension() {
        return initialDPArgExtension;
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
        } catch (MAPParsingComponentException e) {
            throw new CAPParsingComponentException("MAPParsingComponentException when decoding " + _PrimitiveName + ": "
                    + e.getMessage(), e, CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (INAPParsingComponentException e) {
            throw new CAPParsingComponentException("INAPParsingComponentException when decoding " + _PrimitiveName + ": "
                    + e.getMessage(), e, CAPParsingComponentExceptionReason.MistypedParameter);
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
        } catch (MAPParsingComponentException e) {
            throw new CAPParsingComponentException("MAPParsingComponentException when decoding " + _PrimitiveName + ": "
                    + e.getMessage(), e, CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (INAPParsingComponentException e) {
            throw new CAPParsingComponentException("INAPParsingComponentException when decoding " + _PrimitiveName + ": "
                    + e.getMessage(), e, CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws INAPParsingComponentException, CAPParsingComponentException,
            MAPParsingComponentException, IOException, AsnException {

        this.serviceKey = 0;
        this.calledPartyNumber = null;
        this.callingPartyNumber = null;
        this.callingPartysCategory = null;
        this.CGEncountered = null;
        this.IPSSPCapabilities = null;
        this.locationNumber = null;
        this.originalCalledPartyID = null;
        this.extensions = null;
        this.highLayerCompatibility = null;
        this.additionalCallingPartyNumber = null;
        this.bearerCapability = null;
        this.eventTypeBCSM = null;
        this.redirectingPartyID = null;
        this.redirectionInformation = null;
        this.cause = null;
        this.serviceInteractionIndicatorsTwo = null;
        this.carrier = null;
        this.cugIndex = null;
        this.cugInterlock = null;
        this.cugOutgoingAccess = false;
        this.imsi = null;
        this.subscriberState = null;
        this.locationInformation = null;
        this.extBasicServiceCode = null;
        this.callReferenceNumber = null;
        this.mscAddress = null;
        this.calledPartyBCDNumber = null;
        this.timeAndTimezone = null;
        this.callForwardingSSPending = false;
        this.initialDPArgExtension = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            int i1;

            switch (num) {
                case 0:
                    // serviceKey
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || tag != _ID_serviceKey || !ais.isTagPrimitive())
                        throw new CAPParsingComponentException(
                                "Error while decoding InitialDPRequest: Parameter 0 bad tag or tag class or not primitive",
                                CAPParsingComponentExceptionReason.MistypedParameter);
                    this.serviceKey = (int) ais.readInteger();
                    break;

                default:
                if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                    switch (tag) {
                    case _ID_calledPartyNumber:
                        this.calledPartyNumber = new CalledPartyNumberCapImpl();
                        ((CalledPartyNumberCapImpl) this.calledPartyNumber).decodeAll(ais);
                        break;
                    case _ID_callingPartyNumber:
                        this.callingPartyNumber = new CallingPartyNumberCapImpl();
                        ((CallingPartyNumberCapImpl) this.callingPartyNumber).decodeAll(ais);
                        break;
                    case _ID_callingPartysCategory:
                        this.callingPartysCategory = new CallingPartysCategoryInapImpl();
                        ((CallingPartysCategoryInapImpl) this.callingPartysCategory).decodeAll(ais);
                        break;
                    case _ID_cGEncountered:
                        i1 = (int) ais.readInteger();
                        this.CGEncountered = CGEncountered.getInstance(i1);
                        break;
                    case _ID_iPSSPCapabilities:
                        this.IPSSPCapabilities = new IPSSPCapabilitiesImpl();
                        ((IPSSPCapabilitiesImpl) this.IPSSPCapabilities).decodeAll(ais);
                        break;
                    case _ID_locationNumber:
                        this.locationNumber = new LocationNumberCapImpl();
                        ((LocationNumberCapImpl) this.locationNumber).decodeAll(ais);
                        break;
                    case _ID_originalCalledPartyID:
                        this.originalCalledPartyID = new OriginalCalledNumberCapImpl();
                        ((OriginalCalledNumberCapImpl) this.originalCalledPartyID).decodeAll(ais);
                        break;
                    case _ID_extensions:
                        this.extensions = new CAPExtensionsImpl();
                        ((CAPExtensionsImpl) this.extensions).decodeAll(ais);
                        break;
                    case _ID_highLayerCompatibility:
                        this.highLayerCompatibility = new HighLayerCompatibilityInapImpl();
                        ((HighLayerCompatibilityInapImpl) this.highLayerCompatibility).decodeAll(ais);
                        break;
                    case _ID_additionalCallingPartyNumber:
                        this.additionalCallingPartyNumber = new DigitsImpl();
                        ((DigitsImpl) this.additionalCallingPartyNumber).decodeAll(ais);
                        this.additionalCallingPartyNumber.setIsGenericNumber();
                        break;
                    case _ID_bearerCapability:
                        AsnInputStream ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.bearerCapability = new BearerCapabilityImpl();
                        ((BearerCapabilityImpl) this.bearerCapability).decodeAll(ais2);
                        break;
                    case _ID_eventTypeBCSM:
                        i1 = (int) ais.readInteger();
                        this.eventTypeBCSM = EventTypeBCSM.getInstance(i1);
                        break;
                    case _ID_redirectingPartyID:
                        this.redirectingPartyID = new RedirectingPartyIDCapImpl();
                        ((RedirectingPartyIDCapImpl) this.redirectingPartyID).decodeAll(ais);
                        break;
                    case _ID_redirectionInformation:
                        this.redirectionInformation = new RedirectionInformationInapImpl();
                        ((RedirectionInformationInapImpl) this.redirectionInformation).decodeAll(ais);
                        break;
                    case _ID_cause:
                        this.cause = new CauseCapImpl();
                        ((CauseCapImpl) this.cause).decodeAll(ais);
                        break;
                    case _ID_serviceInteractionIndicatorsTwo:
                        this.serviceInteractionIndicatorsTwo = new ServiceInteractionIndicatorsTwoImpl();
                        ((ServiceInteractionIndicatorsTwoImpl) this.serviceInteractionIndicatorsTwo).decodeAll(ais);
                        break;
                    case _ID_carrier:
                        this.carrier = new CarrierImpl();
                        ((CarrierImpl) this.carrier).decodeAll(ais);
                        break;
                    case _ID_cug_Index:
                        this.cugIndex = new CUGIndexImpl();
                        ((CUGIndexImpl) this.cugIndex).decodeAll(ais);
                        break;
                    case _ID_cug_Interlock:
                        this.cugInterlock = new CUGInterlockImpl();
                        ((CUGInterlockImpl) this.cugInterlock).decodeAll(ais);
                        break;
                    case _ID_cug_OutgoingAccess:
                        ais.readNull();
                        this.cugOutgoingAccess = true;
                        break;
                    case _ID_iMSI:
                        int len = ais.readLength();
                        if (len == 0) {
                            ais.advanceElementData(len);
                        } else {
                            this.imsi = new IMSIImpl();
                            ((IMSIImpl) this.imsi).decodeData(ais, len);
                        }
                        break;
                    case _ID_subscriberState:
                        ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.subscriberState = new SubscriberStateImpl();
                        ((SubscriberStateImpl) this.subscriberState).decodeAll(ais2);
                        break;
                    case _ID_locationInformation:
                        this.locationInformation = new LocationInformationImpl();
                        ((LocationInformationImpl) this.locationInformation).decodeAll(ais);
                        break;
                    case _ID_ext_basicServiceCode:
                        ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.extBasicServiceCode = new ExtBasicServiceCodeImpl();
                        ((ExtBasicServiceCodeImpl) this.extBasicServiceCode).decodeAll(ais2);
                        break;
                    case _ID_callReferenceNumber:
                        this.callReferenceNumber = new CallReferenceNumberImpl();
                        ((CallReferenceNumberImpl) this.callReferenceNumber).decodeAll(ais);
                        break;
                    case _ID_mscAddress:
                        this.mscAddress = new ISDNAddressStringImpl();
                        ((ISDNAddressStringImpl) this.mscAddress).decodeAll(ais);
                        break;
                    case _ID_calledPartyBCDNumber:
                        this.calledPartyBCDNumber = new CalledPartyBCDNumberImpl();
                        ((CalledPartyBCDNumberImpl) this.calledPartyBCDNumber).decodeAll(ais);
                        break;
                    case _ID_timeAndTimezone:
                        this.timeAndTimezone = new TimeAndTimezoneImpl();
                        ((TimeAndTimezoneImpl) this.timeAndTimezone).decodeAll(ais);
                        break;
                    case _ID_callForwardingSS_Pending:
                        ais.readNull();
                        this.callForwardingSSPending = true;
                        break;
                    case _ID_initialDPArgExtension:
                        this.initialDPArgExtension = new InitialDPArgExtensionImpl(this.isCAPVersion3orLater);
                        ((InitialDPArgExtensionImpl) this.initialDPArgExtension).decodeAll(ais);
                        break;

                    default:
                        ais.advanceElement();
                        break;
                    }
                } else {
                    ais.advanceElement();
                }
                break;
            }

            num++;
        }

        if (num < 1)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Needs at least 1 mandatory parameters, found " + num,
                    CAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws CAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws CAPException {

        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {

        try {
            asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_serviceKey, this.serviceKey);

            if (this.calledPartyNumber != null)
                ((CalledPartyNumberCapImpl) this.calledPartyNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_calledPartyNumber);
            if (this.callingPartyNumber != null)
                ((CallingPartyNumberCapImpl) this.callingPartyNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_callingPartyNumber);
            if (this.callingPartysCategory != null)
                ((CallingPartysCategoryInapImpl) this.callingPartysCategory).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_callingPartysCategory);
            if (this.CGEncountered != null) {
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_cGEncountered, this.CGEncountered.getCode());
            }
            if (this.IPSSPCapabilities != null)
                ((IPSSPCapabilitiesImpl) this.IPSSPCapabilities).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_iPSSPCapabilities);
            if (this.locationNumber != null)
                ((LocationNumberCapImpl) this.locationNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_locationNumber);
            if (this.originalCalledPartyID != null)
                ((OriginalCalledNumberCapImpl) this.originalCalledPartyID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_originalCalledPartyID);
            if (this.extensions != null)
                ((CAPExtensionsImpl) this.extensions).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_extensions);
            if (this.highLayerCompatibility != null)
                ((HighLayerCompatibilityInapImpl) this.highLayerCompatibility).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_highLayerCompatibility);
            if (this.additionalCallingPartyNumber != null)
                ((DigitsImpl) this.additionalCallingPartyNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_additionalCallingPartyNumber);
            if (this.bearerCapability != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _ID_bearerCapability);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((BearerCapabilityImpl) this.bearerCapability).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (this.eventTypeBCSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_eventTypeBCSM, this.eventTypeBCSM.getCode());
            if (this.redirectingPartyID != null)
                ((RedirectingPartyIDCapImpl) this.redirectingPartyID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_redirectingPartyID);
            if (this.redirectionInformation != null)
                ((RedirectionInformationInapImpl) this.redirectionInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_redirectionInformation);
            if (this.cause != null) {
                ((CauseCapImpl) this.cause).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_cause);
            }
            if (this.serviceInteractionIndicatorsTwo != null) {
                ((ServiceInteractionIndicatorsTwoImpl) this.serviceInteractionIndicatorsTwo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_serviceInteractionIndicatorsTwo);
            }
            if (this.carrier != null) {
                ((CarrierImpl) this.carrier).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_carrier);
            }
            if (this.cugIndex != null) {
                ((CUGIndexImpl) this.cugIndex).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_cug_Index);
            }
            if (this.cugInterlock != null) {
                ((CUGInterlockImpl) this.cugInterlock).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_cug_Interlock);
                this.cugInterlock = new CUGInterlockImpl();
            }
            if (this.cugOutgoingAccess) {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_cug_OutgoingAccess);
            }
            if (this.imsi != null)
                ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_iMSI);
            if (this.subscriberState != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _ID_subscriberState);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((SubscriberStateImpl) this.subscriberState).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (this.locationInformation != null)
                ((LocationInformationImpl) this.locationInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_locationInformation);
            if (this.extBasicServiceCode != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _ID_ext_basicServiceCode);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((ExtBasicServiceCodeImpl) this.extBasicServiceCode).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (this.callReferenceNumber != null)
                ((CallReferenceNumberImpl) this.callReferenceNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_callReferenceNumber);
            if (this.mscAddress != null)
                ((ISDNAddressStringImpl) this.mscAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_mscAddress);
            if (this.calledPartyBCDNumber != null)
                ((CalledPartyBCDNumberImpl) this.calledPartyBCDNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_calledPartyBCDNumber);
            if (this.timeAndTimezone != null)
                ((TimeAndTimezoneImpl) this.timeAndTimezone).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_timeAndTimezone);
            if (this.callForwardingSSPending)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_callForwardingSS_Pending);
            if (this.initialDPArgExtension != null)
                ((InitialDPArgExtensionImpl) this.initialDPArgExtension).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _ID_initialDPArgExtension);

        } catch (IOException e) {
            throw new CAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (INAPException e) {
            throw new CAPException("INAPException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (MAPException e) {
            throw new CAPException("MAPException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }
@Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        this.addInvokeIdInfo(sb);

        sb.append(", serviceKey=");
        sb.append(serviceKey);
        if (this.calledPartyNumber != null) {
            sb.append(", calledPartyNumber=");
            sb.append(calledPartyNumber.toString());
        }
        if (this.callingPartyNumber != null) {
            sb.append(", callingPartyNumber=");
            sb.append(callingPartyNumber.toString());
        }
        if (this.callingPartysCategory != null) {
            sb.append(", callingPartysCategory=");
            sb.append(callingPartysCategory.toString());
        }
        if (this.CGEncountered != null) {
            sb.append(", CGEncountered=");
            sb.append(CGEncountered.toString());
        }
        if (this.IPSSPCapabilities != null) {
            sb.append(", IPSSPCapabilities=");
            sb.append(IPSSPCapabilities.toString());
        }
        if (this.locationNumber != null) {
            sb.append(", locationNumber=");
            sb.append(locationNumber.toString());
        }
        if (this.originalCalledPartyID != null) {
            sb.append(", originalCalledPartyID=");
            sb.append(originalCalledPartyID.toString());
        }
        if (this.extensions != null) {
            sb.append(", extensions=");
            sb.append(extensions.toString());
        }
        if (this.highLayerCompatibility != null) {
            sb.append(", highLayerCompatibility=");
            sb.append(highLayerCompatibility.toString());
        }
        if (this.additionalCallingPartyNumber != null) {
            sb.append(", additionalCallingPartyNumber=");
            sb.append(additionalCallingPartyNumber.toString());
        }
        if (this.bearerCapability != null) {
            sb.append(", bearerCapability=");
            sb.append(bearerCapability.toString());
        }
        if (this.eventTypeBCSM != null) {
            sb.append(", eventTypeBCSM=");
            sb.append(eventTypeBCSM.toString());
        }
        if (this.redirectingPartyID != null) {
            sb.append(", redirectingPartyID=");
            sb.append(redirectingPartyID.toString());
        }
        if (this.redirectionInformation != null) {
            sb.append(", redirectionInformation=");
            sb.append(redirectionInformation.toString());
        }
        if (this.cause != null) {
            sb.append(", cause=");
            sb.append(cause.toString());
        }
        if (this.serviceInteractionIndicatorsTwo != null) {
            sb.append(", serviceInteractionIndicatorsTwo=");
            sb.append(serviceInteractionIndicatorsTwo.toString());
        }
        if (this.carrier != null) {
            sb.append(", carrier=");
            sb.append(carrier.toString());
        }
        if (this.cugIndex != null) {
            sb.append(", cugIndex=");
            sb.append(cugIndex.toString());
        }
        if (this.cugInterlock != null) {
            sb.append(", cugInterlock=");
            sb.append(cugInterlock.toString());
        }
        if (this.cugOutgoingAccess) {
            sb.append(", cugOutgoingAccess");
        }
        if (this.imsi != null) {
            sb.append(", imsi=");
            sb.append(imsi.toString());
        }
        if (this.subscriberState != null) {
            sb.append(", subscriberState=");
            sb.append(subscriberState.toString());
        }
        if (this.locationInformation != null) {
            sb.append(", locationInformation=");
            sb.append(locationInformation.toString());
        }
        if (this.extBasicServiceCode != null) {
            sb.append(", extBasicServiceCode=");
            sb.append(extBasicServiceCode.toString());
        }
        if (this.callReferenceNumber != null) {
            sb.append(", callReferenceNumber=");
            sb.append(callReferenceNumber.toString());
        }
        if (this.mscAddress != null) {
            sb.append(", mscAddress=");
            sb.append(mscAddress.toString());
        }
        if (this.calledPartyBCDNumber != null) {
            sb.append(", calledPartyBCDNumber=");
            sb.append(calledPartyBCDNumber.toString());
        }
        if (this.timeAndTimezone != null) {
            sb.append(", timeAndTimezone=");
            sb.append(timeAndTimezone.toString());
        }
        if (this.callForwardingSSPending) {
            sb.append(", callForwardingSSPending");
        }
        if (this.initialDPArgExtension != null) {
            sb.append(", initialDPArgExtension=");
            sb.append(initialDPArgExtension.toString());
        }

        sb.append("]");

        return sb.toString();
    }
}

// added:
// CGEncountered
// Cause
// serviceInteractionIndicatorsTwo
// carrier
// cugIndex
// cugInterlock
// cugOutgoingAccess


