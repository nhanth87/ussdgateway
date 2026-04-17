
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class SupportedCamelPhasesTest {

    private byte[] getEncodedData() {
        return new byte[] { 3, 2, 4, (byte) 160 };
    }

    @Test(groups = { "functional.decode", "mobility.subscriberManagement" })
    public void testDecode() throws Exception {

        byte[] rawData = getEncodedData();

        AsnInputStream asn = new AsnInputStream(rawData);

        int tag = asn.readTag();
        SupportedCamelPhasesImpl impl = new SupportedCamelPhasesImpl();
        impl.decodeAll(asn);

        assertEquals(tag, Tag.STRING_BIT);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertTrue(impl.getPhase1Supported());
        assertFalse(impl.getPhase2Supported());
        assertTrue(impl.getPhase3Supported());
        assertFalse(impl.getPhase4Supported());
    }

    @Test(groups = { "functional.encode", "mobility.subscriberManagement" })
    public void testEncode() throws Exception {

        SupportedCamelPhasesImpl impl = new SupportedCamelPhasesImpl(true, false, true, false);
        AsnOutputStream asnOS = new AsnOutputStream();

        impl.encodeAll(asnOS);

        byte[] encodedData = asnOS.toByteArray();

        byte[] rawData = getEncodedData();

        assertTrue(Arrays.equals(rawData, encodedData));
    }

    @Test(groups = { "functional.xml.serialize", "mobility.subscriberManagement" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        SupportedCamelPhasesImpl original = new SupportedCamelPhasesImpl(true, false, true, false);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        SupportedCamelPhasesImpl copy = xmlMapper.readValue(serializedEvent, SupportedCamelPhasesImpl.class);

        assertEquals(copy.getPhase1Supported(), original.getPhase1Supported());
        assertEquals(copy.getPhase2Supported(), original.getPhase2Supported());
        assertEquals(copy.getPhase3Supported(), original.getPhase3Supported());
        assertEquals(copy.getPhase4Supported(), original.getPhase4Supported());
    }

}
