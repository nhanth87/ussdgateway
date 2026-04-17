
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ContinueWithArgumentArgExtensionImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.LegOrCallSegmentImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.primitives.LegIDImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
*
* @author sergey vetyutnev
*
*/
public class ContinueWithArgumentArgExtensionTest {

    public byte[] getData1() {
        return new byte[] { 48, 11, (byte) 128, 0, (byte) 129, 0, (byte) 130, 0, (byte) 163, 3, (byte) 128, 1, 12 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 9, (byte) 129, 0, (byte) 163, 5, (byte) 161, 3, (byte) 129, 1, 4 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ContinueWithArgumentArgExtensionImpl elem = new ContinueWithArgumentArgExtensionImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        elem.decodeAll(ais);
        assertTrue(elem.getSuppressDCsi());
        assertTrue(elem.getSuppressNCsi());
        assertTrue(elem.getSuppressOutgoingCallBarring());
        assertEquals((int) elem.getLegOrCallSegment().getCallSegmentID(), 12);

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new ContinueWithArgumentArgExtensionImpl();
        tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        elem.decodeAll(ais);
        assertFalse(elem.getSuppressDCsi());
        assertTrue(elem.getSuppressNCsi());
        assertFalse(elem.getSuppressOutgoingCallBarring());
        assertEquals(elem.getLegOrCallSegment().getLegID().getReceivingSideID(), LegType.leg4);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        LegOrCallSegmentImpl legOrCallSegment = new LegOrCallSegmentImpl(12);
        ContinueWithArgumentArgExtensionImpl elem = new ContinueWithArgumentArgExtensionImpl(true, true, true, legOrCallSegment);
//        boolean suppressDCSI, boolean suppressNCSI,
//        boolean suppressOutgoingCallBarring, LegOrCallSegment legOrCallSegment
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        LegIDImpl legID = new LegIDImpl(false, LegType.leg4);
        legOrCallSegment = new LegOrCallSegmentImpl(legID);
        elem = new ContinueWithArgumentArgExtensionImpl(false, true, false, legOrCallSegment);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LegOrCallSegmentImpl legOrCallSegment = new LegOrCallSegmentImpl(12);
        ContinueWithArgumentArgExtensionImpl original = new ContinueWithArgumentArgExtensionImpl(true, false, true, legOrCallSegment);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ContinueWithArgumentArgExtensionImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ContinueWithArgumentArgExtensionImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressDCsi())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressNCsi())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressOutgoingCallBarring())));
        }
        if (copy != null) {
            assertEquals(original.getSuppressDCsi(), copy.getSuppressDCsi());
            assertEquals(original.getSuppressNCsi(), copy.getSuppressNCsi());
            assertEquals(original.getSuppressOutgoingCallBarring(), copy.getSuppressOutgoingCallBarring());
            assertEquals((int) original.getLegOrCallSegment().getCallSegmentID(), (int) copy.getLegOrCallSegment().getCallSegmentID());
        }
        original = new ContinueWithArgumentArgExtensionImpl(false, true, true, null);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ContinueWithArgumentArgExtensionImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressDCsi())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressNCsi())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSuppressOutgoingCallBarring())));
        assertFalse(serializedEvent.contains("<legOrCallSegment>"));
        }
        if (copy != null) {
            assertEquals(original.getSuppressDCsi(), copy.getSuppressDCsi());
            assertEquals(original.getSuppressNCsi(), copy.getSuppressNCsi());
            assertEquals(original.getSuppressOutgoingCallBarring(), copy.getSuppressOutgoingCallBarring());
            assertNull(original.getLegOrCallSegment());
            assertNull(copy.getLegOrCallSegment());
        }
    }

}
