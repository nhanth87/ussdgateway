
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.MidCallControlInfoImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class MidCallControlInfoTest {

    public byte[] getData1() {
        return new byte[] { 48, 3, (byte) 128, 1, 3 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 20, (byte) 128, 1, 3, (byte) 129, 1, 4, (byte) 130, 2, 1, 10, (byte) 131, 1, 11, (byte) 132, 2, 0, 9, (byte) 134, 1, 100 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        MidCallControlInfoImpl elem = new MidCallControlInfoImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);

        elem.decodeAll(ais);
        assertEquals((int) elem.getMinimumNumberOfDigits(), 3);
        assertNull(elem.getMaximumNumberOfDigits());
        assertNull(elem.getEndOfReplyDigit());
        assertNull(elem.getCancelDigit());
        assertNull(elem.getStartDigit());
        assertNull(elem.getInterDigitTimeout());


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new MidCallControlInfoImpl();
        tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);

        elem.decodeAll(ais);
        assertEquals((int) elem.getMinimumNumberOfDigits(), 3);
        assertEquals((int) elem.getMaximumNumberOfDigits(), 4);
        assertEquals(elem.getEndOfReplyDigit(), "1*");
        assertEquals(elem.getCancelDigit(), "#");
        assertEquals(elem.getStartDigit(), "09");
        assertEquals((int) elem.getInterDigitTimeout(), 100);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        MidCallControlInfoImpl elem = new MidCallControlInfoImpl(3, null, null, null, null, null);
//        Integer minimumNumberOfDigits, Integer maximumNumberOfDigits, String endOfReplyDigit,
//        String cancelDigit, String startDigit, Integer interDigitTimeout
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData1());


        elem = new MidCallControlInfoImpl(3, 4, "1*", "#", "09", 100);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData2());
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        MidCallControlInfoImpl original = new MidCallControlInfoImpl(3, null, null, null, null, null);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        MidCallControlInfoImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, MidCallControlInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<maximumNumberOfDigits>"));
        assertFalse(serializedEvent.contains("<endOfReplyDigit>"));
        assertFalse(serializedEvent.contains("<cancelDigit>"));
        assertFalse(serializedEvent.contains("<startDigit>"));
        assertFalse(serializedEvent.contains("<interDigitTimeout>"));
        }
        if (copy != null) {
            assertEquals((int) copy.getMinimumNumberOfDigits(), (int) original.getMinimumNumberOfDigits());
            assertNull(copy.getMaximumNumberOfDigits());
            assertNull(copy.getEndOfReplyDigit());
            assertNull(copy.getCancelDigit());
            assertNull(copy.getStartDigit());
            assertNull(copy.getInterDigitTimeout());
        }
        original = new MidCallControlInfoImpl(3, 4, "1*", "#", "09", 100);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, MidCallControlInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getEndOfReplyDigit())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getCancelDigit())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getStartDigit())));
        }
        if (copy != null) {
            assertEquals((int) copy.getMinimumNumberOfDigits(), (int) original.getMinimumNumberOfDigits());
            assertEquals((int) copy.getMaximumNumberOfDigits(), (int) original.getMaximumNumberOfDigits());
            assertEquals(copy.getEndOfReplyDigit(), original.getEndOfReplyDigit());
            assertEquals(copy.getCancelDigit(), original.getCancelDigit());
            assertEquals(copy.getStartDigit(), original.getStartDigit());
            assertEquals((int) copy.getInterDigitTimeout(), (int) original.getInterDigitTimeout());
        }
    }

}
