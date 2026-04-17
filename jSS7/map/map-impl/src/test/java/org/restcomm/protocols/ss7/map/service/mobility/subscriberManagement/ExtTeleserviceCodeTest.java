
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ExtTeleserviceCodeTest {

    byte[] data = new byte[] { 4, 1, 0x11 };
    byte[] dataEncoded = new byte[] { 0x11 };

    byte[] data2 = new byte[] { (byte) 131, 1, 16 };
    byte[] data3 = new byte[] { 4, 1, 34 };

    @Test(groups = { "functional.decode", "subscriberManagement" })
    public void testDecode() throws Exception {

        AsnInputStream asn = new AsnInputStream(data);
        int tag = asn.readTag();
        assertEquals(tag, Tag.STRING_OCTET);

        ExtTeleserviceCodeImpl impl = new ExtTeleserviceCodeImpl();
        impl.decodeAll(asn);

        assertTrue(Arrays.equals(impl.getData(), dataEncoded));
        assertEquals(impl.getTeleserviceCodeValue(), TeleserviceCodeValue.telephony);

        asn = new AsnInputStream(data2);
        tag = asn.readTag();
        assertEquals(tag, 3);

        impl = new ExtTeleserviceCodeImpl();
        impl.decodeAll(asn);

        assertEquals(impl.getTeleserviceCodeValue(), TeleserviceCodeValue.allSpeechTransmissionServices);

        asn = new AsnInputStream(data3);
        tag = asn.readTag();
        assertEquals(tag, Tag.STRING_OCTET);

        impl = new ExtTeleserviceCodeImpl();
        impl.decodeAll(asn);

        assertEquals(impl.getTeleserviceCodeValue(), TeleserviceCodeValue.shortMessageMO_PP);
    }

    @Test(groups = { "functional.encode", "subscriberManagement" })
    public void testEncode() throws Exception {

        ExtTeleserviceCodeImpl impl = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.telephony);
        AsnOutputStream asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS);
        byte[] encodedData = asnOS.toByteArray();
        byte[] rawData = data;
        assertTrue(Arrays.equals(rawData, encodedData));

        impl = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allSpeechTransmissionServices);
        asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS, Tag.CLASS_CONTEXT_SPECIFIC, 3);
        encodedData = asnOS.toByteArray();
        rawData = data2;
        assertTrue(Arrays.equals(rawData, encodedData));

        impl = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMO_PP);
        asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS);
        encodedData = asnOS.toByteArray();
        rawData = data3;
        assertTrue(Arrays.equals(rawData, encodedData));
    }

    @Test(groups = { "functional.xml.serialize", "subscriberManagement" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        ExtTeleserviceCodeImpl original = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.telephony);

        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        ExtTeleserviceCodeImpl copy = xmlMapper.readValue(serializedEvent, ExtTeleserviceCodeImpl.class);

        assertEquals(copy.getTeleserviceCodeValue(), original.getTeleserviceCodeValue());
    }

}
