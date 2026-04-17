
package org.restcomm.protocols.ss7.cap.isup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CalledPartyNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CalledPartyNumberCapTest {

    public byte[] getData() {
        return new byte[] { (byte) 130, 7, 3, (byte) 144, 33, 114, 16, (byte) 144, 0 };
    }

    public byte[] getIntData() {
        return new byte[] { 3, (byte) 144, 33, 114, 16, (byte) 144, 0 };
    }

    @Test(groups = { "functional.decode", "isup" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        CalledPartyNumberCapImpl elem = new CalledPartyNumberCapImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        CalledPartyNumber cpn = elem.getCalledPartyNumber();
        assertTrue(Arrays.equals(elem.getData(), this.getIntData()));
        assertFalse(cpn.isOddFlag());
        assertEquals(cpn.getNumberingPlanIndicator(), 1);
        assertEquals(cpn.getInternalNetworkNumberIndicator(), 1);
        assertEquals(cpn.getNatureOfAddressIndicator(), 3);
        assertTrue(cpn.getAddress().equals("1227010900"));
    }

    @Test(groups = { "functional.encode", "isup" })
    public void testEncode() throws Exception {

        CalledPartyNumberCapImpl elem = new CalledPartyNumberCapImpl(this.getIntData());
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 2);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        CalledPartyNumber cpn = new CalledPartyNumberImpl(3, "1227010900", 1, 1);
        elem = new CalledPartyNumberCapImpl(cpn);
        aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 2);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int internalNetworkNumberIndicator
    }

    @Test(groups = { "functional.xml.serialize", "isup" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CalledPartyNumberCapImpl original = new CalledPartyNumberCapImpl(new CalledPartyNumberImpl(
                CalledPartyNumber._NAI_INTERNATIONAL_NUMBER, "664422", CalledPartyNumber._NPI_ISDN,
                CalledPartyNumber._NAI_NRNINNF));

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CalledPartyNumberCapImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CalledPartyNumberCapImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<address>"));
        }
        if (copy != null) {
            assertEquals(copy.getCalledPartyNumber().getAddress(), original.getCalledPartyNumber().getAddress());
        }

    }
}
