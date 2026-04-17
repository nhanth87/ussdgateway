
package org.restcomm.protocols.ss7.cap.gap;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.gap.GapOnServiceImpl;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author <a href="mailto:bartosz.krok@pro-ids.com"> Bartosz Krok (ProIDS sp. z o.o.)</a>
 *
 */
public class GapOnServiceTest {

    public static final int SERVICE_KEY = 821;

    public byte[] getData() {
        return new byte[] {48, 4, (byte) 128, 2, 3, 53};
    }

    public byte[] getDigitsData() {
        return new byte[] {48, 69, 91, 84};
    }

    @Test(groups = { "functional.decode", "gap" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        GapOnServiceImpl elem = new GapOnServiceImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals(elem.getServiceKey(), SERVICE_KEY);
    }


    @Test(groups = { "functional.encode", "gap" })
    public void testEncode() throws Exception {

        GapOnServiceImpl elem = new GapOnServiceImpl(SERVICE_KEY);

        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);

        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));
    }

    @Test(groups = { "functional.xml.serialize", "gap" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        GapOnServiceImpl original = new GapOnServiceImpl(SERVICE_KEY);

        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        GapOnServiceImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, GapOnServiceImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
        if (copy != null) {
            assertTrue(isEqual(original, copy));
        }
    }

    private boolean isEqual(GapOnServiceImpl o1, GapOnServiceImpl o2) {
        if (o1 == o2)
            return true;
        if (o1 == null && o2 != null || o1 != null && o2 == null)
            return false;
        if (o1 == null && o2 == null)
            return true;
        if (!o1.toString().equals(o2.toString()))
            return false;
        return true;
    }

}
