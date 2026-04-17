package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.BearerCapability;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.InitialDPArgExtension;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.LowLayerCompatibility;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.inap.api.INAPException;
import org.restcomm.protocols.ss7.inap.api.INAPParsingComponentException;
import org.restcomm.protocols.ss7.inap.api.isup.HighLayerCompatibilityInap;
import org.restcomm.protocols.ss7.inap.isup.HighLayerCompatibilityInapImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.service.callhandling.UUData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4Functionalities;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.callhandling.UUDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.MSClassmark2Impl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OfferedCamel4FunctionalitiesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;


/**
 *
 * @author sergey vetyutnev
 * @author alerant appngin
 *
 */
@JacksonXmlRootElement(localName = "initialDPArgExtension")
public class InitialDPArgExtensionImpl extends SequenceBase implements InitialDPArgExtension {

    public static final int _ID_gmscAddress = 0;
    public static final int _ID_forwardingDestinationNumber = 1;
    public static final int _ID_ms_Classmark2 = 2;
    public static final int _ID_iMEI = 3;
    public static final int _ID_supportedCamelPhases = 4;
    public static final int _ID_offeredCamel4Functionalities = 5;
    public static final int _ID_bearerCapability2 = 6;
    public static final int _ID_ext_basicServiceCode2 = 7;
    public static final int _ID_highLayerCompatibility2 = 8;
    public static final int _ID_lowLayerCompatibility = 9;
    public static final int _ID_lowLayerCompatibility2 = 10;
    public static final int _ID_enhancedDialledServicesAllowed = 11;
    public static final int _ID_uu_Data = 12;
    public static final int _ID_collectInformationAllowed = 13;
    public static final int _ID_releaseCallArgExtensionAllowed = 14;

    private static final String IS_CAP_VERSION_3_OR_LATER = "isCAPVersion3orLater";
    private static final String GMSC_ADDRESS = "gmscAddress";
    private static final String FORWARDING_DESTINATION_NUMBER = "forwardingDestinationNumber";
    private static final String MS_CLASSMARK2 = "msClassmark2";
    private static final String IMEI = "imei";
    private static final String SUPPORTED_CAMEL_PHASES = "supportedCamelPhases";
    private static final String OFFERED_CAMEL4_FUNCTIONALITIES = "offeredCamel4Functionalities";
    private static final String BEARER_CAPABILITY2 = "bearerCapability2";
    private static final String EXT_BASIC_SERVICE_CODE2 = "extBasicServiceCode2";
    private static final String HIGH_LAYER_COMPATIBILITY2 = "highLayerCompatibility2";
    private static final String LOW_LAYER_COMPATIBILITY = "lowLayerCompatibility";
    private static final String LOW_LAYER_COMPATIBILITY2 = "lowLayerCompatibility2";
    private static final String ENHANCED_DIALLED_SERVICES_ALLOWED = "enhancedDialledServicesAllowed";
    private static final String UU_DATA = "uuData";
    private static final String COLLECT_INFORMATION_ALLOWED = "collectInformationAllowed";
    private static final String RELEASE_CALL_ARG_EXTENSION_ALLOWED = "releaseCallArgExtensionAllowed";

    public static final String _PrimitiveName = "InitialDPArgExtension";

    @JacksonXmlProperty(localName = "gmscAddress")
    private ISDNAddressString gmscAddress;
    @JacksonXmlProperty(localName = "forwardingDestinationNumber")
    private CalledPartyNumberCap forwardingDestinationNumber;
    @JacksonXmlProperty(localName = "msClassmark2")
    private MSClassmark2 msClassmark2;
    @JacksonXmlProperty(localName = "imei")
    private IMEI imei;
    @JacksonXmlProperty(localName = "supportedCamelPhases")
    private SupportedCamelPhases supportedCamelPhases;
    @JacksonXmlProperty(localName = "offeredCamel4Functionalities")
    private OfferedCamel4Functionalities offeredCamel4Functionalities;
    @JacksonXmlProperty(localName = "bearerCapability2")
    private BearerCapability bearerCapability2;
    @JacksonXmlProperty(localName = "extBasicServiceCode2")
    private ExtBasicServiceCode extBasicServiceCode2;
    @JacksonXmlProperty(localName = "highLayerCompatibility2")
    private HighLayerCompatibilityInap highLayerCompatibility2;
    @JacksonXmlProperty(localName = "lowLayerCompatibility")
    private LowLayerCompatibility lowLayerCompatibility;
    @JacksonXmlProperty(localName = "lowLayerCompatibility2")
    private LowLayerCompatibility lowLayerCompatibility2;
    @JacksonXmlProperty(localName = "enhancedDialledServicesAllowed")
    private boolean enhancedDialledServicesAllowed;
    @JacksonXmlProperty(localName = "uuData")
    private UUData uuData;
    @JacksonXmlProperty(localName = "collectInformationAllowed")
    private boolean collectInformationAllowed;
    @JacksonXmlProperty(localName = "releaseCallArgExtensionAllowed")
    private boolean releaseCallArgExtensionAllowed;

    protected boolean isCAPVersion3orLater;

    /**
     * This constructor is for deserializing purposes
     */
    public InitialDPArgExtensionImpl() {
        super("InitialDPArgExtension");
    }

    public InitialDPArgExtensionImpl(boolean isCAPVersion3orLater) {
        super("InitialDPArgExtension");

        this.isCAPVersion3orLater = isCAPVersion3orLater;
    }

    public InitialDPArgExtensionImpl(ISDNAddressString gmscAddress, CalledPartyNumberCap forwardingDestinationNumber,
            MSClassmark2 msClassmark2, IMEI imei, SupportedCamelPhases supportedCamelPhases,
            OfferedCamel4Functionalities offeredCamel4Functionalities, BearerCapability bearerCapability2,
            ExtBasicServiceCode extBasicServiceCode2, HighLayerCompatibilityInap highLayerCompatibility2,
            LowLayerCompatibility lowLayerCompatibility, LowLayerCompatibility lowLayerCompatibility2,
            boolean enhancedDialledServicesAllowed, UUData uuData, boolean collectInformationAllowed,
            boolean releaseCallArgExtensionAllowed, boolean isCAPVersion3orLater) {
        super("InitialDPArgExtension");

        this.gmscAddress = gmscAddress;
        this.forwardingDestinationNumber = forwardingDestinationNumber;
        this.msClassmark2 = msClassmark2;
        this.imei = imei;
        this.supportedCamelPhases = supportedCamelPhases;
        this.offeredCamel4Functionalities = offeredCamel4Functionalities;
        this.bearerCapability2 = bearerCapability2;
        this.extBasicServiceCode2 = extBasicServiceCode2;
        this.highLayerCompatibility2 = highLayerCompatibility2;
        this.lowLayerCompatibility = lowLayerCompatibility;
        this.lowLayerCompatibility2 = lowLayerCompatibility2;
        this.enhancedDialledServicesAllowed = enhancedDialledServicesAllowed;
        this.uuData = uuData;
        this.collectInformationAllowed = collectInformationAllowed;
        this.releaseCallArgExtensionAllowed = releaseCallArgExtensionAllowed;
        this.isCAPVersion3orLater = isCAPVersion3orLater;
    }

    @JsonIgnore
    @Override
    public ISDNAddressString getGmscAddress() {
        return gmscAddress;
    }

    @JsonIgnore
    @Override
    public CalledPartyNumberCap getForwardingDestinationNumber() {
        return forwardingDestinationNumber;
    }

    @JsonIgnore
    @Override
    public MSClassmark2 getMSClassmark2() {
        return msClassmark2;
    }

    @JsonIgnore
    @Override
    public IMEI getIMEI() {
        return imei;
    }

    @JsonIgnore
    @Override
    public SupportedCamelPhases getSupportedCamelPhases() {
        return supportedCamelPhases;
    }

    @JsonIgnore
    @Override
    public OfferedCamel4Functionalities getOfferedCamel4Functionalities() {
        return offeredCamel4Functionalities;
    }

    @JsonIgnore
    @Override
    public BearerCapability getBearerCapability2() {
        return bearerCapability2;
    }

    @JsonIgnore
    @Override
    public ExtBasicServiceCode getExtBasicServiceCode2() {
        return extBasicServiceCode2;
    }

    @JsonIgnore
    @Override
    public HighLayerCompatibilityInap getHighLayerCompatibility2() {
        return highLayerCompatibility2;
    }

    @JsonIgnore
    @Override
    public LowLayerCompatibility getLowLayerCompatibility() {
        return lowLayerCompatibility;
    }

    @JsonIgnore
    @Override
    public LowLayerCompatibility getLowLayerCompatibility2() {
        return lowLayerCompatibility2;
    }

    @JsonIgnore
    @Override
    public boolean getEnhancedDialledServicesAllowed() {
        return enhancedDialledServicesAllowed;
    }

    @JsonIgnore
    @Override
    public UUData getUUData() {
        return uuData;
    }

    @JsonIgnore
    @Override
    public boolean getCollectInformationAllowed() {
        return collectInformationAllowed;
    }

    @JsonIgnore
    @Override
    public boolean getReleaseCallArgExtensionAllowed() {
        return releaseCallArgExtensionAllowed;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException, MAPParsingComponentException, INAPParsingComponentException,
            IOException, AsnException {

        this.gmscAddress = null;
        this.forwardingDestinationNumber = null;
        this.msClassmark2 = null;
        this.imei = null;
        this.supportedCamelPhases = null;
        this.offeredCamel4Functionalities = null;
        this.bearerCapability2 = null;
        this.extBasicServiceCode2 = null;
        this.highLayerCompatibility2 = null;
        this.lowLayerCompatibility = null;
        this.lowLayerCompatibility2 = null;
        this.enhancedDialledServicesAllowed = false;
        this.uuData = null;
        this.collectInformationAllowed = false;
        this.releaseCallArgExtensionAllowed = false;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _ID_gmscAddress:
                        if (isCAPVersion3orLater) {
                            this.gmscAddress = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.gmscAddress).decodeAll(ais);
                        } else {
                            // in CAP V2 naCarrierInformation parameter - we do not
                            // implement it
                            ais.advanceElement();
                        }
                        break;
                    case _ID_forwardingDestinationNumber:
                        if (isCAPVersion3orLater) {
                            this.forwardingDestinationNumber = new CalledPartyNumberCapImpl();
                            ((CalledPartyNumberCapImpl) this.forwardingDestinationNumber).decodeAll(ais);
                        } else {
                            // in CAP V2 gmscAddress parameter
                            this.gmscAddress = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.gmscAddress).decodeAll(ais);
                        }
                        break;
                    case _ID_ms_Classmark2:
                        this.msClassmark2 = new MSClassmark2Impl();
                        ((MSClassmark2Impl) this.msClassmark2).decodeAll(ais);
                        break;
                    case _ID_iMEI:
                        this.imei = new IMEIImpl();
                        ((IMEIImpl) this.imei).decodeAll(ais);
                        break;
                    case _ID_supportedCamelPhases:
                        this.supportedCamelPhases = new SupportedCamelPhasesImpl();
                        ((SupportedCamelPhasesImpl) this.supportedCamelPhases).decodeAll(ais);
                        break;
                    case _ID_offeredCamel4Functionalities:
                        this.offeredCamel4Functionalities = new OfferedCamel4FunctionalitiesImpl();
                        ((OfferedCamel4FunctionalitiesImpl) this.offeredCamel4Functionalities).decodeAll(ais);
                        break;
                    case _ID_bearerCapability2:
                        AsnInputStream ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.bearerCapability2 = new BearerCapabilityImpl();
                        ((BearerCapabilityImpl) this.bearerCapability2).decodeAll(ais2);
                        break;
                    case _ID_ext_basicServiceCode2:
                        ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.extBasicServiceCode2 = new ExtBasicServiceCodeImpl();
                        ((ExtBasicServiceCodeImpl) this.extBasicServiceCode2).decodeAll(ais2);
                        break;
                    case _ID_highLayerCompatibility2:
                        this.highLayerCompatibility2 = new HighLayerCompatibilityInapImpl();
                        ((HighLayerCompatibilityInapImpl) this.highLayerCompatibility2).decodeAll(ais);
                        break;
                    case _ID_lowLayerCompatibility:
                        this.lowLayerCompatibility = new LowLayerCompatibilityImpl();
                        ((LowLayerCompatibilityImpl) this.lowLayerCompatibility).decodeAll(ais);
                        break;
                    case _ID_lowLayerCompatibility2:
                        this.lowLayerCompatibility2 = new LowLayerCompatibilityImpl();
                        ((LowLayerCompatibilityImpl) this.lowLayerCompatibility2).decodeAll(ais);
                        break;
                    case _ID_enhancedDialledServicesAllowed:
                        ais.readNull();
                        this.enhancedDialledServicesAllowed = true;
                        break;
                    case _ID_uu_Data:
                        this.uuData = new UUDataImpl();
                        ((UUDataImpl) this.uuData).decodeAll(ais);
                        break;
                    case _ID_collectInformationAllowed:
                        this.collectInformationAllowed = true;
                        ais.readNull();
                        break;
                    case _ID_releaseCallArgExtensionAllowed:
                        this.releaseCallArgExtensionAllowed = true;
                        ais.readNull();
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
            if (isCAPVersion3orLater) {
                if (this.gmscAddress != null)
                    ((ISDNAddressStringImpl) this.gmscAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_gmscAddress);
                if (this.forwardingDestinationNumber != null)
                    ((CalledPartyNumberCapImpl) this.forwardingDestinationNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                            _ID_forwardingDestinationNumber);
            } else {
                if (this.gmscAddress != null)
                    ((ISDNAddressStringImpl) this.gmscAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                            _ID_forwardingDestinationNumber);
            }

            if (msClassmark2 != null) {
                ((MSClassmark2Impl) this.msClassmark2).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_ms_Classmark2);
            }
            if (imei != null) {
                ((IMEIImpl) this.imei).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_iMEI);
            }
            if (supportedCamelPhases != null) {
                ((SupportedCamelPhasesImpl) this.supportedCamelPhases).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_supportedCamelPhases);
            }
            if (offeredCamel4Functionalities != null) {
                ((OfferedCamel4FunctionalitiesImpl) this.offeredCamel4Functionalities).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_offeredCamel4Functionalities);
            }
            if (bearerCapability2 != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _ID_bearerCapability2);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((BearerCapabilityImpl) this.bearerCapability2).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (extBasicServiceCode2 != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _ID_ext_basicServiceCode2);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((ExtBasicServiceCodeImpl) this.extBasicServiceCode2).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (highLayerCompatibility2 != null) {
                ((HighLayerCompatibilityInapImpl) this.highLayerCompatibility2).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_highLayerCompatibility2);
            }
            if (lowLayerCompatibility != null) {
                ((LowLayerCompatibilityImpl) this.lowLayerCompatibility).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_lowLayerCompatibility);
            }
            if (lowLayerCompatibility2 != null) {
                ((LowLayerCompatibilityImpl) this.lowLayerCompatibility2).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_lowLayerCompatibility2);
            }
            if (enhancedDialledServicesAllowed) {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_enhancedDialledServicesAllowed);
            }
            if (uuData != null) {
                ((UUDataImpl) this.uuData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_uu_Data);
            }
            if (collectInformationAllowed) {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_collectInformationAllowed);
            }
            if (releaseCallArgExtensionAllowed) {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _ID_releaseCallArgExtensionAllowed);
            }
        } catch (IOException e) {
            throw new CAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (MAPException e) {
            throw new CAPException("MAPException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (INAPException e) {
            throw new CAPException("INAPException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }
@Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.gmscAddress != null) {
            sb.append(", gmscAddress=");
            sb.append(gmscAddress);
        }
        if (this.forwardingDestinationNumber != null) {
            sb.append(", forwardingDestinationNumber=");
            sb.append(forwardingDestinationNumber);
        }
        if (this.msClassmark2 != null) {
            sb.append(", msClassmark2=");
            sb.append(msClassmark2.toString());
        }
        if (this.imei != null) {
            sb.append(", imei=");
            sb.append(imei.toString());
        }
        if (this.supportedCamelPhases != null) {
            sb.append(", supportedCamelPhases=");
            sb.append(supportedCamelPhases.toString());
        }
        if (this.offeredCamel4Functionalities != null) {
            sb.append(", offeredCamel4Functionalities=");
            sb.append(offeredCamel4Functionalities.toString());
        }
        if (this.bearerCapability2 != null) {
            sb.append(", bearerCapability2=");
            sb.append(bearerCapability2.toString());
        }
        if (this.extBasicServiceCode2 != null) {
            sb.append(", extBasicServiceCode2=");
            sb.append(extBasicServiceCode2.toString());
        }
        if (this.highLayerCompatibility2 != null) {
            sb.append(", highLayerCompatibility2=");
            sb.append(highLayerCompatibility2.toString());
        }
        if (this.lowLayerCompatibility != null) {
            sb.append(", lowLayerCompatibility=");
            sb.append(lowLayerCompatibility.toString());
        }
        if (this.lowLayerCompatibility2 != null) {
            sb.append(", lowLayerCompatibility2=");
            sb.append(lowLayerCompatibility2.toString());
        }
        if (this.enhancedDialledServicesAllowed) {
            sb.append(", enhancedDialledServicesAllowed");
        }
        if (this.uuData != null) {
            sb.append(", uuData=");
            sb.append(uuData.toString());
        }
        if (this.collectInformationAllowed) {
            sb.append(", collectInformationAllowed");
        }
        if (this.releaseCallArgExtensionAllowed) {
            sb.append(", releaseCallArgExtensionAllowed");
        }

        sb.append("]");

        return sb.toString();
    }
}

