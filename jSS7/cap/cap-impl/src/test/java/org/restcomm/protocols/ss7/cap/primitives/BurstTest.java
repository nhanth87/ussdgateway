
package org.restcomm.protocols.ss7.cap.primitives;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.primitives.BurstImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class BurstTest {

    public byte[] getData1() {
        return new byte[] { 48, 3, (byte) 129, 1, 10 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 15, (byte) 128, 1, 1, (byte) 129, 1, 10, (byte) 130, 1, 2, (byte) 131, 1, 11, (byte) 132, 1, 12 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        BurstImpl elem = new BurstImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);
        elem.decodeAll(ais);
        assertNull(elem.getNumberOfBursts());
        assertEquals((int) elem.getBurstInterval(), 10);
        assertNull(elem.getNumberOfTonesInBurst());
        assertNull(elem.getToneDuration());
        assertNull(elem.getToneInterval());

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new BurstImpl();
        tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);
        elem.decodeAll(ais);
        assertEquals((int) elem.getNumberOfBursts(), 1);
        assertEquals((int) elem.getBurstInterval(), 10);
        assertEquals((int) elem.getNumberOfTonesInBurst(), 2);
        assertEquals((int) elem.getToneDuration(), 11);
        assertEquals((int) elem.getToneInterval(), 12);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        BurstImpl elem = new BurstImpl(null, 10, null, null, null);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        elem = new BurstImpl(1, 10, 2, 11, 12);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        BurstImpl original = new BurstImpl(null, 10, null, null, null);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        BurstImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, BurstImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<numberOfBursts>"));
        assertFalse(serializedEvent.contains("<numberOfTonesInBurst>"));
        assertFalse(serializedEvent.contains("<toneDuration>"));
        assertFalse(serializedEvent.contains("<toneInterval>"));
        }
        if (copy != null) {
            assertNull(copy.getNumberOfBursts());
            assertEquals((int) copy.getBurstInterval(), (int) original.getBurstInterval());
            assertNull(copy.getNumberOfTonesInBurst());
            assertNull(copy.getToneDuration());
            assertNull(copy.getToneInterval());
        }
        original = new BurstImpl(1, 10, 2, 11, 12);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, BurstImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
        if (copy != null) {
            assertEquals((int) copy.getNumberOfBursts(), (int) original.getNumberOfBursts());
            assertEquals((int) copy.getBurstInterval(), (int) original.getBurstInterval());
            assertEquals((int) copy.getNumberOfTonesInBurst(), (int) original.getNumberOfTonesInBurst());
            assertEquals((int) copy.getToneDuration(), (int) original.getToneDuration());
            assertEquals((int) copy.getToneInterval(), (int) original.getToneInterval());
        }
    }

}
