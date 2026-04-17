
package org.restcomm.protocols.ss7.map.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.primitives.LAIFixedLengthImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class LAIFixedLengthTest {

    public byte[] getData() {
        return new byte[] { 4, 5, 82, (byte) 240, 16, 17, 92 };
    };

    public byte[] getDataVal() {
        return new byte[] { 82, (byte) 240, 16, 17, 92 };
    };

    public byte[] getData2() {
        return new byte[] { 4, 5, 16, 97, 66, 1, 77 };
    };

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();

        AsnInputStream asn = new AsnInputStream(data);
        int tag = asn.readTag();

        LAIFixedLengthImpl prim = new LAIFixedLengthImpl();
        prim.decodeAll(asn);

        assertNotNull(prim.getData());
        assertTrue(Arrays.equals(getDataVal(), prim.getData()));

        assertEquals(prim.getMCC(), 250);
        assertEquals(prim.getMNC(), 1);
        assertEquals(prim.getLac(), 4444);

        data = this.getData2();

        asn = new AsnInputStream(data);
        tag = asn.readTag();

        prim = new LAIFixedLengthImpl();
        prim.decodeAll(asn);

        assertNotNull(prim.getData());

        assertEquals(prim.getMCC(), 11);
        assertEquals(prim.getMNC(), 246);
        assertEquals(prim.getLac(), 333);
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testEncode() throws Exception {

        LAIFixedLengthImpl prim = new LAIFixedLengthImpl(250, 1, 4444);

        AsnOutputStream asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertTrue(Arrays.equals(asn.toByteArray(), this.getData()));

        prim = new LAIFixedLengthImpl(getDataVal());

        asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertTrue(Arrays.equals(asn.toByteArray(), this.getData()));

        prim = new LAIFixedLengthImpl(11, 246, 333);

        asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertTrue(Arrays.equals(asn.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        LAIFixedLengthImpl original = new LAIFixedLengthImpl(250, 1, 4444);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        LAIFixedLengthImpl copy = xmlMapper.readValue(serializedEvent, LAIFixedLengthImpl.class);

        assertEquals(copy.getMCC(), original.getMCC());
        assertEquals(copy.getMNC(), original.getMNC());
        assertEquals(copy.getLac(), original.getLac());

    }
}
