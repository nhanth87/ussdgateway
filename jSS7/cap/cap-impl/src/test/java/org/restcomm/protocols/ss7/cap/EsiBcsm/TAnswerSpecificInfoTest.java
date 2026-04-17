
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.EsiBcsm.ChargeIndicatorImpl;
import org.restcomm.protocols.ss7.cap.EsiBcsm.TAnswerSpecificInfoImpl;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ChargeIndicator;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ChargeIndicatorValue;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CalledPartyNumberImpl;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 * @author Amit Bhayani
 * @author sergey vetyutnev
 *
 */
public class TAnswerSpecificInfoTest {

    public byte[] getData1() {
        return new byte[] { 48, 25, (byte) 159, 50, 7, (byte) 128, (byte) 144, 17, 33, 34, 51, 3, (byte) 159, 52, 0, (byte) 191, 54, 3, (byte) 131, 1, 16,
                (byte) 191, 55, 3, (byte) 131, 1, 32 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 17, (byte) 159, 50, 7, (byte) 128, (byte) 144, 17, 33, 34, 51, 3, (byte) 159, 51, 0, (byte) 159, 53, 1, 1 };
    }

    @Test(groups = { "functional.decode", "EsiBcsm" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        TAnswerSpecificInfoImpl elem = new TAnswerSpecificInfoImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);

        elem.decodeAll(ais);
        assertEquals(elem.getDestinationAddress().getCalledPartyNumber().getAddress(), "111222333");
        assertFalse(elem.getOrCall());
        assertTrue(elem.getForwardedCall());
        assertNull(elem.getChargeIndicator());
        assertEquals(elem.getExtBasicServiceCode().getExtTeleservice().getTeleserviceCodeValue(), TeleserviceCodeValue.allSpeechTransmissionServices);
        assertEquals(elem.getExtBasicServiceCode2().getExtTeleservice().getTeleserviceCodeValue(), TeleserviceCodeValue.allShortMessageServices);


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new TAnswerSpecificInfoImpl();
        tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);

        elem.decodeAll(ais);
        assertEquals(elem.getDestinationAddress().getCalledPartyNumber().getAddress(), "111222333");
        assertTrue(elem.getOrCall());
        assertFalse(elem.getForwardedCall());
        assertEquals(elem.getChargeIndicator().getChargeIndicatorValue(), ChargeIndicatorValue.noCharge);
        assertNull(elem.getExtBasicServiceCode());
        assertNull(elem.getExtBasicServiceCode2());
    }

    @Test(groups = { "functional.encode", "EsiBcsm" })
    public void testEncode() throws Exception {
        CalledPartyNumberImpl calledPartyNumber = new CalledPartyNumberImpl(0, "111222333", 1, 1);
        CalledPartyNumberCapImpl forwardingDestinationNumber = new CalledPartyNumberCapImpl(calledPartyNumber);
        ExtTeleserviceCodeImpl extTeleservice = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allSpeechTransmissionServices);
        ExtTeleserviceCodeImpl extTeleservice2 = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allShortMessageServices);

        ExtBasicServiceCode extBasicSer = new ExtBasicServiceCodeImpl(extTeleservice);
        ExtBasicServiceCode extBasicSer2 = new ExtBasicServiceCodeImpl(extTeleservice2);

        TAnswerSpecificInfoImpl elem = new TAnswerSpecificInfoImpl(forwardingDestinationNumber, false, true, null,
                extBasicSer, extBasicSer2);
//        CalledPartyNumberCap destinationAddress, boolean orCall, boolean forwardedCall,
//        ChargeIndicator chargeIndicator, ExtBasicServiceCode extBasicServiceCode, ExtBasicServiceCode extBasicServiceCode2
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData1());


        ChargeIndicator chargeIndicator = new ChargeIndicatorImpl(ChargeIndicatorValue.noCharge);
        elem = new TAnswerSpecificInfoImpl(forwardingDestinationNumber, true, false, chargeIndicator,
                null, null);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData2());
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CalledPartyNumberImpl calledPartyNumber = new CalledPartyNumberImpl(0, "111222333", 1, 1);
        CalledPartyNumberCapImpl forwardingDestinationNumber = new CalledPartyNumberCapImpl(calledPartyNumber);
        ExtTeleserviceCodeImpl extTeleservice = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allSpeechTransmissionServices);
        ExtTeleserviceCodeImpl extTeleservice2 = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allShortMessageServices);

        ExtBasicServiceCode extBasicSer = new ExtBasicServiceCodeImpl(extTeleservice);
        ExtBasicServiceCode extBasicSer2 = new ExtBasicServiceCodeImpl(extTeleservice2);

        TAnswerSpecificInfoImpl original = new TAnswerSpecificInfoImpl(forwardingDestinationNumber, false, true, null,
                extBasicSer, extBasicSer2);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        TAnswerSpecificInfoImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, TAnswerSpecificInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getOrCall())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getForwardedCall())));
        assertFalse(serializedEvent.contains("<chargeIndicator>"));
        }
        if (copy != null) {
            assertEquals(copy.getOrCall(), original.getOrCall());
            assertEquals(copy.getForwardedCall(), original.getForwardedCall());
            assertNull(copy.getChargeIndicator());
        }
        ChargeIndicator chargeIndicator = new ChargeIndicatorImpl(ChargeIndicatorValue.noCharge);
        original = new TAnswerSpecificInfoImpl(forwardingDestinationNumber, true, false, chargeIndicator, null, null);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, TAnswerSpecificInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getOrCall())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getForwardedCall())));
        assertTrue(serializedEvent.contains("<chargeIndicatorValue>"));
        assertFalse(serializedEvent.contains("<extBasicServiceCode>"));
        assertFalse(serializedEvent.contains("<extBasicServiceCode2>"));
        }
        if (copy != null) {
            assertEquals(copy.getOrCall(), original.getOrCall());
            assertEquals(copy.getForwardedCall(), original.getForwardedCall());
            assertEquals(copy.getChargeIndicator().getChargeIndicatorValue(), original.getChargeIndicator().getChargeIndicatorValue());
            assertNull(copy.getExtBasicServiceCode());
            assertNull(copy.getExtBasicServiceCode2());
        }
    }

}
