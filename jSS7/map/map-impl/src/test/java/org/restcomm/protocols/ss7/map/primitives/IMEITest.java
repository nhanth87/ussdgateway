
package org.restcomm.protocols.ss7.map.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class IMEITest {

    private byte[] getEncodedData() {
        return new byte[] { 4, 8, 33, 67, 101, (byte) 135, 9, 33, 67, 101 };
    }

    private byte[] getEncodedDataImeiLengthLessThan15() {
        return new byte[] { 4, 1, -15 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] rawData = getEncodedData();

        AsnInputStream asn = new AsnInputStream(rawData);

        int tag = asn.readTag();
        IMEIImpl imei = new IMEIImpl();
        imei.decodeAll(asn);

        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertEquals(imei.getIMEI(), "1234567890123456");

        // Testing IMEI length != 15
        rawData = getEncodedDataImeiLengthLessThan15();
        asn = new AsnInputStream(rawData);

        tag = asn.readTag();
        imei = new IMEIImpl();
        imei.decodeAll(asn);

        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertEquals(imei.getIMEI(), "1");
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        IMEIImpl imei = new IMEIImpl("1234567890123456");
        AsnOutputStream asnOS = new AsnOutputStream();

        imei.encodeAll(asnOS);

        byte[] encodedData = asnOS.toByteArray();

        byte[] rawData = getEncodedData();

        assertTrue(Arrays.equals(rawData, encodedData));

        // Testing IMEI length != 15
        imei = new IMEIImpl("1");
        asnOS = new AsnOutputStream();
        imei.encodeAll(asnOS);

        encodedData = asnOS.toByteArray();
        rawData = getEncodedDataImeiLengthLessThan15();

        assertTrue(Arrays.equals(rawData, encodedData));
    }

    @Test(groups = { "functional.serialize", "primitives" })
    public void testSerialization() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        IMEIImpl original = new IMEIImpl("1234567890123456");
        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(original);
        oos.close();

        // deserialize
        byte[] pickled = out.toByteArray();
        InputStream in = new ByteArrayInputStream(pickled);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();
        IMEIImpl copy = (IMEIImpl) o;

        // test result
        assertEquals(copy.getIMEI(), original.getIMEI());

    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        IMEIImpl original = new IMEIImpl("12345123450000");

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        IMEIImpl copy = xmlMapper.readValue(serializedEvent, IMEIImpl.class);

        assertEquals(copy.getIMEI(), original.getIMEI());
    }

}
