
package org.restcomm.protocols.ss7.map.service.callhandling;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.service.callhandling.CallReferenceNumberImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CallReferenceNumberTest {

    public byte[] getData() {
        return new byte[] { 4, 5, 19, -6, 61, 61, -22 };
    };

    public byte[] getDataVal() {
        return new byte[] { 19, -6, 61, 61, -22 };
    };

    @Test(groups = { "functional.decode", "service.callhandling" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();

        AsnInputStream asn = new AsnInputStream(data);
        int tag = asn.readTag();

        CallReferenceNumberImpl prim = new CallReferenceNumberImpl();
        prim.decodeAll(asn);

        assertNotNull(prim.getData());
        assertTrue(Arrays.equals(getDataVal(), prim.getData()));

    }

    @Test(groups = { "functional.decode", "service.callhandling" })
    public void testEncode() throws Exception {

        CallReferenceNumberImpl prim = new CallReferenceNumberImpl(getDataVal());

        AsnOutputStream asn = new AsnOutputStream();
        prim.encodeAll(asn);

        assertTrue(Arrays.equals(asn.toByteArray(), this.getData()));
    }

    @Test(groups = { "functional.xml.serialize", "service.callhandling" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        CallReferenceNumberImpl original = new CallReferenceNumberImpl(getDataVal());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        CallReferenceNumberImpl copy = xmlMapper.readValue(serializedEvent, CallReferenceNumberImpl.class);

        assertEquals(copy.getData(), original.getData());

    }
}
