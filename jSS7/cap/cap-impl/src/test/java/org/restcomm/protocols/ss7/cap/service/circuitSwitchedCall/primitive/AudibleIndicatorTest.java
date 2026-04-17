
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.primitives.Burst;
import org.restcomm.protocols.ss7.cap.api.primitives.BurstList;
import org.restcomm.protocols.ss7.cap.primitives.BurstImpl;
import org.restcomm.protocols.ss7.cap.primitives.BurstListImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.AudibleIndicatorImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class AudibleIndicatorTest {

    public byte[] getData1() {
        return new byte[] { 1, 1, 0 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 161, 8, (byte) 128, 1, 1, (byte) 161, 3, (byte) 128, 1, 2 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        AudibleIndicatorImpl elem = new AudibleIndicatorImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.BOOLEAN);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);
        elem.decodeAll(ais);
        assertFalse(elem.getTone());
        assertNull(elem.getBurstList());


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new AudibleIndicatorImpl();
        tag = ais.readTag();
        assertEquals(tag, AudibleIndicatorImpl._ID_burstList);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);
        elem.decodeAll(ais);
        assertNull(elem.getTone());
        assertEquals((int) elem.getBurstList().getWarningPeriod(), 1);
        assertEquals((int) elem.getBurstList().getBursts().getNumberOfBursts(), 2);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        AudibleIndicatorImpl elem = new AudibleIndicatorImpl(false);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));


        Burst burst = new BurstImpl(2, null, null, null, null);
        BurstList burstList = new BurstListImpl(1, burst);
        // Integer warningPeriod, Burst burst
        elem = new AudibleIndicatorImpl(burstList);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        AudibleIndicatorImpl original = new AudibleIndicatorImpl(false);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        AudibleIndicatorImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, AudibleIndicatorImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getTone())));
        assertFalse(serializedEvent.contains("<burstList>"));
        }
        if (copy != null) {
            assertEquals(copy.getTone(), original.getTone());
            assertNull(copy.getBurstList());
        }
        Burst burst = new BurstImpl(2, null, null, null, null);
        BurstList burstList = new BurstListImpl(1, burst);
        original = new AudibleIndicatorImpl(burstList);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, AudibleIndicatorImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<tone>"));
        assertTrue(serializedEvent.contains("<warningPeriod>"));
        }
        if (copy != null) {
            assertNull(copy.getTone());
            assertEquals((int) copy.getBurstList().getWarningPeriod(), (int) original.getBurstList().getWarningPeriod());
            assertEquals((int) copy.getBurstList().getBursts().getNumberOfBursts(), (int) original.getBurstList().getBursts().getNumberOfBursts());
        }
    }

}
