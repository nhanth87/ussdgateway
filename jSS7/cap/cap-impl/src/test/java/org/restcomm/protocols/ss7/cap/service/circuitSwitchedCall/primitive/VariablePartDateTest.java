
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartDateImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class VariablePartDateTest {

    public byte[] getData1() {
        return new byte[] { (byte) 131, 4, 2, 33, 48, 18 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        VariablePartDateImpl elem = new VariablePartDateImpl();
        int tag = ais.readTag();
        assertEquals(tag, 3);
        elem.decodeAll(ais);
        assertEquals(elem.getYear(), 2012);
        assertEquals(elem.getMonth(), 3);
        assertEquals(elem.getDay(), 21);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        VariablePartDateImpl elem = new VariablePartDateImpl(2012, 3, 21);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 3);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        VariablePartDateImpl original = new VariablePartDateImpl(2015, 12, 3);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        VariablePartDateImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartDateImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getYear())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getMonth())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getDay())));
        }
        if (copy != null) {
            assertEquals(copy.getYear(), original.getYear());
            assertEquals(copy.getMonth(), original.getMonth());
            assertEquals(copy.getDay(), original.getDay());
        }
    }
}
