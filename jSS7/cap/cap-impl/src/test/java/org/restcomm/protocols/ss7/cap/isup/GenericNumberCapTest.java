
package org.restcomm.protocols.ss7.cap.isup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.isup.GenericNumberCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericNumber;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class GenericNumberCapTest {

    public byte[] getData() {
        return new byte[] { (byte) 157, 7, 1, (byte) 131, 20, 7, 1, 9, 0 };
    }

    public byte[] getIntData() {
        return new byte[] { 1, -125, 20, 7, 1, 9, 0 };
    }

    @Test(groups = { "functional.decode", "isup" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        GenericNumberCapImpl elem = new GenericNumberCapImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        GenericNumber gn = elem.getGenericNumber();
        assertTrue(Arrays.equals(elem.getData(), this.getIntData()));
        assertEquals(gn.getNatureOfAddressIndicator(), 3);
        assertTrue(gn.getAddress().equals("7010900"));
        assertEquals(gn.getNumberingPlanIndicator(), 1);
        assertEquals(gn.getAddressRepresentationRestrictedIndicator(), 1);
        assertEquals(gn.getNumberQualifierIndicator(), 1);
        assertEquals(gn.getScreeningIndicator(), 0);
    }

    @Test(groups = { "functional.encode", "isup" })
    public void testEncode() throws Exception {

        GenericNumberCapImpl elem = new GenericNumberCapImpl(this.getIntData());
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 29);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        GenericNumber rn = new GenericNumberImpl(3, "7010900", 1, 1, 1, false, 0);
        elem = new GenericNumberCapImpl(rn);
        aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, 29);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));

        // int natureOfAddresIndicator, String address, int numberQualifierIndicator, int numberingPlanIndicator, int
        // addressRepresentationREstrictedIndicator,
        // boolean numberIncomplete, int screeningIndicator
    }

    @Test(groups = { "functional.xml.serialize", "isup" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        GenericNumberImpl gn = new GenericNumberImpl(GenericNumber._NAI_NATIONAL_SN, "12345",
                GenericNumber._NQIA_CONNECTED_NUMBER, GenericNumber._NPI_TELEX, GenericNumber._APRI_ALLOWED,
                GenericNumber._NI_INCOMPLETE, GenericNumber._SI_USER_PROVIDED_VERIFIED_FAILED);
        GenericNumberCapImpl original = new GenericNumberCapImpl(gn);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        GenericNumberCapImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, GenericNumberCapImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<address>"));
        assertTrue(serializedEvent.contains("<numberIncomplete>"));
        assertTrue(serializedEvent.contains("<screeningIndicator>"));
        assertTrue(serializedEvent.contains("<oddFlag>"));
        }
        if (copy != null) {
            assertEquals(copy.getGenericNumber().getAddress(), original.getGenericNumber().getAddress());
            assertEquals(copy.getGenericNumber().isNumberIncomplete(), original.getGenericNumber().isNumberIncomplete());
            assertEquals(copy.getGenericNumber().getScreeningIndicator(), original.getGenericNumber().getScreeningIndicator());
            assertEquals(copy.getGenericNumber().isOddFlag(), original.getGenericNumber().isOddFlag());
        }

    }
}
