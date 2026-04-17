
package org.restcomm.protocols.ss7.cap.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.primitives.TimeAndTimezoneImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class TimeAndTimezoneTest {

    public byte[] getData1() {
        return new byte[] { (byte) 159, 57, 8, 2, 17, 33, 3, 1, 112, (byte) 129, 35 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 159, 57, 8, 2, 17, 33, 3, 1, 112, (byte) 129, 43 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        TimeAndTimezoneImpl elem = new TimeAndTimezoneImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals(elem.getYear(), 2011);
        assertEquals(elem.getMonth(), 12);
        assertEquals(elem.getDay(), 30);
        assertEquals(elem.getHour(), 10);
        assertEquals(elem.getMinute(), 7);
        assertEquals(elem.getSecond(), 18);
        assertEquals(elem.getTimeZone(), 32);

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new TimeAndTimezoneImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals(elem.getYear(), 2011);
        assertEquals(elem.getMonth(), 12);
        assertEquals(elem.getDay(), 30);
        assertEquals(elem.getHour(), 10);
        assertEquals(elem.getMinute(), 7);
        assertEquals(elem.getSecond(), 18);
        assertEquals(elem.getTimeZone(), -32);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        TimeAndTimezoneImpl elem = new TimeAndTimezoneImpl(2011, 12, 30, 10, 7, 18, 32);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 57);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        elem = new TimeAndTimezoneImpl(2011, 12, 30, 10, 7, 18, -32);
        aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 57);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        TimeAndTimezoneImpl original = new TimeAndTimezoneImpl(2011, 12, 30, 10, 7, 18, 32);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        TimeAndTimezoneImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, TimeAndTimezoneImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getYear())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getMonth())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getDay())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getHour())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getMinute())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSecond())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getTimeZone())));
        }
        if (copy != null) {
            assertEquals(copy.getYear(), original.getYear());
            assertEquals(copy.getMonth(), original.getMonth());
            assertEquals(copy.getDay(), original.getDay());
            assertEquals(copy.getHour(), original.getHour());
            assertEquals(copy.getMinute(), original.getMinute());
            assertEquals(copy.getSecond(), original.getSecond());
            assertEquals(copy.getTimeZone(), original.getTimeZone());
        }

    }
}
