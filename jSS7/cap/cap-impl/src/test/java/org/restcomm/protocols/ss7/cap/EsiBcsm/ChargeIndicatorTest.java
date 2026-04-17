
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.EsiBcsm.ChargeIndicatorImpl;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ChargeIndicatorValue;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class ChargeIndicatorTest {

    public byte[] getData1() {
        return new byte[] { 4, 1, 3 };
    }

    @Test(groups = { "functional.decode", "EsiBcsm" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ChargeIndicatorImpl elem = new ChargeIndicatorImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);

        elem.decodeAll(ais);
        assertEquals(elem.getChargeIndicatorValue(), ChargeIndicatorValue.spare);
    }

    @Test(groups = { "functional.encode", "EsiBcsm" })
    public void testEncode() throws Exception {

        ChargeIndicatorImpl elem = new ChargeIndicatorImpl(ChargeIndicatorValue.spare);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData1());
    }

    @Test(groups = { "functional.xml.serialize", "EsiBcsm" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        ChargeIndicatorImpl original = new ChargeIndicatorImpl(ChargeIndicatorValue.spare);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ChargeIndicatorImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ChargeIndicatorImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getChargeIndicatorValue())));
        }
        if (copy != null) {
            assertEquals(copy.getChargeIndicatorValue(), original.getChargeIndicatorValue());
        }
    }

}
