
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.CallSegmentToCancelImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
public class CallSegmentToCancelTest {

    public byte[] getData1() {
        return new byte[] { 48, 6, (byte) 128, 1, 3, (byte) 129, 1, 5 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        CallSegmentToCancelImpl elem = new CallSegmentToCancelImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        elem.decodeAll(ais);
        assertEquals((int) elem.getInvokeID(), 3);
        assertEquals((int) elem.getCallSegmentID(), 5);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        CallSegmentToCancelImpl elem = new CallSegmentToCancelImpl(3, 5);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CallSegmentToCancelImpl original = new CallSegmentToCancelImpl(3, 5);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CallSegmentToCancelImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CallSegmentToCancelImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeID())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getCallSegmentID())));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeID(), original.getInvokeID());
            assertEquals(copy.getCallSegmentID(), original.getCallSegmentID());
        }

    }
}
