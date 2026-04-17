
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartTimeImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class VariablePartTimeTest {

    public byte[] getData1() {
        return new byte[] { (byte) 130, 2, 81, 48 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        VariablePartTimeImpl elem = new VariablePartTimeImpl();
        int tag = ais.readTag();
        assertEquals(tag, 2);
        elem.decodeAll(ais);
        assertEquals(elem.getHour(), 15);
        assertEquals(elem.getMinute(), 3);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        VariablePartTimeImpl elem = new VariablePartTimeImpl(15, 3);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 2);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        VariablePartTimeImpl original = new VariablePartTimeImpl(11, 12);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        VariablePartTimeImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartTimeImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getHour())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getMinute())));
        }
        if (copy != null) {
            assertEquals(copy.getHour(), original.getHour());
            assertEquals(copy.getMinute(), original.getMinute());
        }
    }
}
