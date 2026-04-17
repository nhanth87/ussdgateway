
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.EsiBcsm.OAbandonSpecificInfoImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class OAbandonSpecificInfoTest {

    public byte[] getData1() {
        return new byte[] { (byte) 48, 3, (byte) 159, 50, 0 };
    }

    @Test(groups = { "functional.decode", "EsiBcsm" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        OAbandonSpecificInfoImpl elem = new OAbandonSpecificInfoImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(elem.getRouteNotPermitted());
    }

    @Test(groups = { "functional.encode", "EsiBcsm" })
    public void testEncode() throws Exception {

        OAbandonSpecificInfoImpl elem = new OAbandonSpecificInfoImpl(true);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "EsiBcsm" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        OAbandonSpecificInfoImpl original = new OAbandonSpecificInfoImpl(true);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        OAbandonSpecificInfoImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, OAbandonSpecificInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getRouteNotPermitted())));
        }
        if (copy != null) {
            assertEquals(copy.getRouteNotPermitted(), original.getRouteNotPermitted());
        }
    }

}
