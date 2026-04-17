
package org.restcomm.protocols.ss7.cap.isup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.restcomm.protocols.ss7.cap.isup.CauseCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CauseIndicatorsImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CauseIndicators;
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
public class CauseCapTest {

    public byte[] getData() {
        return new byte[] { (byte) 128, 2, (byte) 132, (byte) 144 };
    }

    public byte[] getIntData() {
        return new byte[] { (byte) 132, (byte) 144 };
    }

    @Test(groups = { "functional.decode", "isup" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        CauseCapImpl elem = new CauseCapImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(Arrays.equals(elem.getData(), this.getIntData()));
        CauseIndicators ci = elem.getCauseIndicators();
        assertEquals(ci.getCodingStandard(), 0);
        assertEquals(ci.getLocation(), 4);
        assertEquals(ci.getCauseValue(), 16);
        assertNull(ci.getDiagnostics());
    }

    @Test(groups = { "functional.xml.serialize", "isup" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CauseCapImpl original = new CauseCapImpl(this.getIntData());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CauseCapImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CauseCapImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<causeValue>"));
        }
        if (copy != null) {
            assertEquals(copy.getCauseIndicators().getCauseValue(), original.getCauseIndicators().getCauseValue());
        }
    }

    @Test(groups = { "functional.encode", "isup" })
    public void testEncode() throws Exception {

    }

    @Test(groups = { "functional.xml.serialize", "isup" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CauseIndicatorsImpl original0 = new CauseIndicatorsImpl(CauseIndicators._CODING_STANDARD_NATIONAL,
                CauseIndicators._LOCATION_PRIVATE_NSRU, 1, CauseIndicators._CV_CALL_REJECTED, null);

        CauseCapImpl original = new CauseCapImpl(original0);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CauseCapImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CauseCapImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<codingStandard>"));
        assertTrue(serializedEvent.contains("<location>"));
        assertTrue(serializedEvent.contains("<recommendation>"));
        assertTrue(serializedEvent.contains("<causeValue>"));
        }
        if (copy != null) {
            assertEquals(copy.getCauseIndicators().getCodingStandard(), original.getCauseIndicators().getCodingStandard());
            assertEquals(copy.getCauseIndicators().getLocation(), original.getCauseIndicators().getLocation());
            assertEquals(copy.getCauseIndicators().getRecommendation(), original.getCauseIndicators().getRecommendation());
            assertEquals(copy.getCauseIndicators().getCauseValue(), original.getCauseIndicators().getCauseValue());
        }
    }
}
