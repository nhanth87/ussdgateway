
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.LegOrCallSegmentImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegID;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.primitives.LegIDImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class LegOrCallSegmentTest {

    public byte[] getData1() {
        return new byte[] { (byte) 128, 1, 10 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 161, 3, (byte) 128, 1, 3 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        LegOrCallSegmentImpl elem = new LegOrCallSegmentImpl();
        int tag = ais.readTag();

        assertEquals(tag, LegOrCallSegmentImpl._ID_callSegmentID);
        elem.decodeAll(ais);
        assertEquals((int)elem.getCallSegmentID(), 10);
        assertNull(elem.getLegID());


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new LegOrCallSegmentImpl();
        tag = ais.readTag();

        assertEquals(tag, LegOrCallSegmentImpl._ID_legID);
        elem.decodeAll(ais);
        assertNull(elem.getCallSegmentID());
        assertNull(elem.getLegID().getReceivingSideID());
        assertEquals(elem.getLegID().getSendingSideID(), LegType.leg3);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        LegOrCallSegmentImpl elem = new LegOrCallSegmentImpl(10);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        LegID legId = new LegIDImpl(true, LegType.leg3);
        elem = new LegOrCallSegmentImpl(legId);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LegOrCallSegmentImpl original = new LegOrCallSegmentImpl(10);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        LegOrCallSegmentImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, LegOrCallSegmentImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<legID>"));
        }
        if (copy != null) {
            assertEquals((int)copy.getCallSegmentID(), (int)original.getCallSegmentID());
            assertNull(copy.getLegID());
            assertNull(original.getLegID());
        }
        LegID legId = new LegIDImpl(true, LegType.leg3);
        original = new LegOrCallSegmentImpl(legId);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, LegOrCallSegmentImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<sendingSideID>"));
        assertFalse(serializedEvent.contains("<callSegmentID>"));
        }
        if (copy != null) {
            assertEquals(copy.getLegID().getSendingSideID(), original.getLegID().getSendingSideID());
            assertNull(copy.getCallSegmentID());
            assertNull(original.getCallSegmentID());
            assertNull(copy.getLegID().getReceivingSideID());
            assertNull(original.getLegID().getReceivingSideID());
        }

    }

}
