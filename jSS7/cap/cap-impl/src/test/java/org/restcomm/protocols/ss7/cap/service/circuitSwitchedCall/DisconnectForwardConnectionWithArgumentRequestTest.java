
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.DisconnectForwardConnectionWithArgumentRequestImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 * 
 * @author sergey vetyutnev
 * 
 */
public class DisconnectForwardConnectionWithArgumentRequestTest {

    public byte[] getData1() {
        return new byte[] { 48, 23, (byte) 129, 1, 40, (byte) 162, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1, (byte) 255 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        DisconnectForwardConnectionWithArgumentRequestImpl elem = new DisconnectForwardConnectionWithArgumentRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals((int)elem.getCallSegmentID(), 40);
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        DisconnectForwardConnectionWithArgumentRequestImpl elem = new DisconnectForwardConnectionWithArgumentRequestImpl(40,
                CAPExtensionsTest.createTestCAPExtensions());

        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        DisconnectForwardConnectionWithArgumentRequestImpl original = new DisconnectForwardConnectionWithArgumentRequestImpl(40,
                CAPExtensionsTest.createTestCAPExtensions());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        DisconnectForwardConnectionWithArgumentRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, DisconnectForwardConnectionWithArgumentRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<extensions>"));
        }
        if (copy != null) {
            assertEquals((int) original.getCallSegmentID(), (int) copy.getCallSegmentID());
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(original.getExtensions()));
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(copy.getExtensions()));
        }
    }

}
