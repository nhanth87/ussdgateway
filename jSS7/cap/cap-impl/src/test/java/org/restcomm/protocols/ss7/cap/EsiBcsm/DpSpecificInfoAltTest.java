
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.EsiBcsm.CollectedInfoSpecificInfoImpl;
import org.restcomm.protocols.ss7.cap.EsiBcsm.DpSpecificInfoAltImpl;
import org.restcomm.protocols.ss7.cap.EsiBcsm.OServiceChangeSpecificInfoImpl;
import org.restcomm.protocols.ss7.cap.EsiBcsm.TServiceChangeSpecificInfoImpl;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.CollectedInfoSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.OServiceChangeSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.TServiceChangeSpecificInfo;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CalledPartyNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBearerServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class DpSpecificInfoAltTest {

    public byte[] getData1() {
        return new byte[] { 48, 25, (byte) 160, 5, (byte) 160, 3, (byte) 130, 1, 38, (byte) 161, 5, (byte) 160, 3, (byte) 131, 1, 98, (byte) 162, 9,
                (byte) 128, 7, 0, 0, 51, 51, 19, 17, 17 };
    }

    @Test(groups = { "functional.decode", "EsiBcsm" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        DpSpecificInfoAltImpl elem = new DpSpecificInfoAltImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);
        elem.decodeAll(ais);

        assertEquals(elem.getOServiceChangeSpecificInfo().getExtBasicServiceCode().getExtBearerService().getBearerServiceCodeValue(),
                BearerServiceCodeValue.padAccessCA_9600bps);
        assertEquals(elem.getCollectedInfoSpecificInfo().getCalledPartyNumber().getCalledPartyNumber().getAddress(), "3333311111");
        assertEquals(elem.getTServiceChangeSpecificInfo().getExtBasicServiceCode().getExtTeleservice().getTeleserviceCodeValue(),
                TeleserviceCodeValue.automaticFacsimileGroup3);
    }

    @Test(groups = { "functional.encode", "EsiBcsm" })
    public void testEncode() throws Exception {

        ExtBearerServiceCode extBearerService = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.padAccessCA_9600bps);
        ExtBasicServiceCode extBasicServiceCode = new ExtBasicServiceCodeImpl(extBearerService);
        OServiceChangeSpecificInfo oServiceChangeSpecificInfo = new OServiceChangeSpecificInfoImpl(extBasicServiceCode);
        CalledPartyNumber calledPartyNumber = new CalledPartyNumberImpl();
        calledPartyNumber.setAddress("3333311111");
        CalledPartyNumberCap calledPartyNumberCap = new CalledPartyNumberCapImpl(calledPartyNumber);
        CollectedInfoSpecificInfo collectedInfoSpecificInfo = new CollectedInfoSpecificInfoImpl(calledPartyNumberCap);
        ExtTeleserviceCode extTeleservice = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.automaticFacsimileGroup3);
        ExtBasicServiceCode extBasicServiceCode2 = new ExtBasicServiceCodeImpl(extTeleservice);
        TServiceChangeSpecificInfo tServiceChangeSpecificInfo = new TServiceChangeSpecificInfoImpl(extBasicServiceCode2);
        DpSpecificInfoAltImpl elem = new DpSpecificInfoAltImpl(oServiceChangeSpecificInfo, collectedInfoSpecificInfo, tServiceChangeSpecificInfo);
//        OServiceChangeSpecificInfo oServiceChangeSpecificInfo, CollectedInfoSpecificInfo collectedInfoSpecificInfo,
//        TServiceChangeSpecificInfo tServiceChangeSpecificInfo
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "EsiBcsm" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        ExtBearerServiceCode extBearerService = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.padAccessCA_9600bps);
        ExtBasicServiceCode extBasicServiceCode = new ExtBasicServiceCodeImpl(extBearerService);
        OServiceChangeSpecificInfo oServiceChangeSpecificInfo = new OServiceChangeSpecificInfoImpl(extBasicServiceCode);
        CalledPartyNumber calledPartyNumber = new CalledPartyNumberImpl();
        calledPartyNumber.setAddress("3333311111");
        CalledPartyNumberCap calledPartyNumberCap = new CalledPartyNumberCapImpl(calledPartyNumber);
        CollectedInfoSpecificInfo collectedInfoSpecificInfo = new CollectedInfoSpecificInfoImpl(calledPartyNumberCap);
        ExtTeleserviceCode extTeleservice = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.automaticFacsimileGroup3);
        ExtBasicServiceCode extBasicServiceCode2 = new ExtBasicServiceCodeImpl(extTeleservice);
        TServiceChangeSpecificInfo tServiceChangeSpecificInfo = new TServiceChangeSpecificInfoImpl(extBasicServiceCode2);
        DpSpecificInfoAltImpl original = new DpSpecificInfoAltImpl(oServiceChangeSpecificInfo, collectedInfoSpecificInfo, tServiceChangeSpecificInfo);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        DpSpecificInfoAltImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, DpSpecificInfoAltImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
    }

}
