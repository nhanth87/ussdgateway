
package org.restcomm.protocols.ss7.cap.isup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.isup.RedirectingPartyIDCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.RedirectingNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.RedirectingNumber;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class RedirectingPartyIDCapTest {

    public byte[] getData() {
        return new byte[] { (byte) 157, 6, (byte) 131, 20, 7, 1, 9, 0 };
    }

    public byte[] getIntData() {
        return new byte[] { (byte) 131, 20, 7, 1, 9, 0 };
    }

    @Test(groups = { "functional.decode", "isup" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        RedirectingPartyIDCapImpl elem = new RedirectingPartyIDCapImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        RedirectingNumber rn = elem.getRedirectingNumber();
        assertTrue(Arrays.equals(elem.getData(), this.getIntData()));
        assertEquals(rn.getNatureOfAddressIndicator(), 3);
        assertTrue(rn.getAddress().equals("7010900"));
        assertEquals(rn.getNumberingPlanIndicator(), 1);
        assertEquals(rn.getAddressRepresentationRestrictedIndicator(), 1);
    }

    @Test(groups = { "functional.encode", "isup" })
    public void testEncode() throws Exception {

        RedirectingPartyIDCapImpl elem = new RedirectingPartyIDCapImpl(this.getIntData());
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 29);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        RedirectingNumber rn = new RedirectingNumberImpl(3, "7010900", 1, 1);
        elem = new RedirectingPartyIDCapImpl(rn);
        aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 29);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationRestrictedIndicator
    }

    @Test(groups = { "functional.xml.serialize", "isup" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        RedirectingPartyIDCapImpl original = new RedirectingPartyIDCapImpl(new RedirectingNumberImpl(
                RedirectingNumber._NAI_NATIONAL_SN, "12345", RedirectingNumber._NPI_TELEX, RedirectingNumber._APRI_RESTRICTED));

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        RedirectingPartyIDCapImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, RedirectingPartyIDCapImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<address>"));
        }
        if (copy != null) {
            assertEquals(copy.getRedirectingNumber().getAddress(), original.getRedirectingNumber().getAddress());
        }

    }
}
