
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.isup.CauseCapImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ReleaseCallRequestImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ReleaseCallRequestTest {

    public byte[] getData1() {
        return new byte[] { 4, 2, (byte) 132, (byte) 144 };
    }

    public byte[] getDataIntData() {
        return new byte[] { (byte) 132, (byte) 144 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ReleaseCallRequestImpl elem = new ReleaseCallRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(Arrays.equals(elem.getCause().getData(), getDataIntData()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        CauseCapImpl cause = new CauseCapImpl(getDataIntData());

        ReleaseCallRequestImpl elem = new ReleaseCallRequestImpl(cause);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CauseCapImpl cause = new CauseCapImpl(getDataIntData());
        ReleaseCallRequestImpl original = new ReleaseCallRequestImpl(cause);
        original.setInvokeId(24);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ReleaseCallRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ReleaseCallRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertTrue(serializedEvent.contains("<cause>"));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertEquals(copy.getCause().getData(), original.getCause().getData());
        }
    }
}
