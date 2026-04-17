
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class APNTest {

    public byte[] getData1() {
        return new byte[] { 4, 2, 1, 42 };
    };

    public byte[] getData2() {
        return new byte[] { 4, 15, 10, 66, 76, 65, 67, 75, 66, 69, 82, 82, 89, 3, 78, 69, 84 };
    };

    public byte[] getData3() {
        return new byte[] { 4, 12, 11, 121, 101, 115, 105, 110, 116, 101, 114, 110, 101, 116 };
    };

    @Test(groups = { "functional.decode", "mobility.subscriberManagement" })
    public void testDecode() throws Exception {
        byte[] data = this.getData1();
        AsnInputStream asn = new AsnInputStream(data);
        int tag = asn.readTag();
        APNImpl prim = new APNImpl();
        prim.decodeAll(asn);

        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertEquals(prim.getApn(), "*");


        data = this.getData2();
        asn = new AsnInputStream(data);
        tag = asn.readTag();
        prim = new APNImpl();
        prim.decodeAll(asn);

        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertEquals(prim.getApn(), "BLACKBERRY.NET");


        data = this.getData3();
        asn = new AsnInputStream(data);
        tag = asn.readTag();
        prim = new APNImpl();
        prim.decodeAll(asn);

        assertEquals(tag, Tag.STRING_OCTET);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertEquals(prim.getApn(), "yesinternet");
    }

    @Test(groups = { "functional.encode", "mobility.subscriberManagement" })
    public void testEncode() throws Exception {
        APNImpl prim = new APNImpl("*");

        AsnOutputStream asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertEquals(asn.toByteArray(), this.getData1());


        prim = new APNImpl("BLACKBERRY.NET");

        asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertEquals(asn.toByteArray(), this.getData2());


        prim = new APNImpl("yesinternet");

        asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertEquals(asn.toByteArray(), this.getData3());
    }

    @Test(groups = { "functional.xml.serialize", "mobility.subscriberManagement" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        APNImpl original = new APNImpl("eee.com");

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        APNImpl copy = xmlMapper.readValue(serializedEvent, APNImpl.class);

        assertEquals(copy.getApn(), original.getApn());
    }

}
