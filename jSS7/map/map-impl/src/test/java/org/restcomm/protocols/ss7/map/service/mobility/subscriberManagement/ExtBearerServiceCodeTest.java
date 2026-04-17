
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBearerServiceCodeImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ExtBearerServiceCodeTest {

    private byte[] getEncodedData1() {
        return new byte[] { (byte) 130, 1, 38 };
    }

    private byte[] getData1() {
        return new byte[] { 38 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] rawData = getEncodedData1();
        AsnInputStream asn = new AsnInputStream(rawData);
        int tag = asn.readTag();
        ExtBearerServiceCodeImpl impl = new ExtBearerServiceCodeImpl();
        impl.decodeAll(asn);
        assertTrue(Arrays.equals(impl.getData(), this.getData1()));
        assertEquals(impl.getBearerServiceCodeValue(), BearerServiceCodeValue.padAccessCA_9600bps);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        ExtBearerServiceCodeImpl impl = new ExtBearerServiceCodeImpl(this.getData1());
        AsnOutputStream asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS, Tag.CLASS_CONTEXT_SPECIFIC, 2);
        byte[] encodedData = asnOS.toByteArray();
        byte[] rawData = getEncodedData1();
        assertTrue(Arrays.equals(rawData, encodedData));

        impl = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.padAccessCA_9600bps);
        asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS, Tag.CLASS_CONTEXT_SPECIFIC, 2);
        encodedData = asnOS.toByteArray();
        rawData = getEncodedData1();
        assertTrue(Arrays.equals(rawData, encodedData));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        ExtBearerServiceCodeImpl original = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.padAccessCA_9600bps);

        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        ExtBearerServiceCodeImpl copy = xmlMapper.readValue(serializedEvent, ExtBearerServiceCodeImpl.class);

        assertEquals(copy.getBearerServiceCodeValue(), original.getBearerServiceCodeValue());
    }
}
