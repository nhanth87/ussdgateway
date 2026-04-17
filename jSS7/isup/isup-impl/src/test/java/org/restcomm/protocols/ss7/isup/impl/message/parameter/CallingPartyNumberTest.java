
package org.restcomm.protocols.ss7.isup.impl.message.parameter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.restcomm.protocols.ss7.isup.impl.message.parameter.CallingPartyNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.NAINumber;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.isup.ISUPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CallingPartyNumberTest {
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeTest
    public void setUp() {
    }

    @AfterTest
    public void tearDown() {
    }

    private byte[] getData() {
        return new byte[] { (byte) 0x83, (byte) 0xC2, 0x21, 0x43, 0x05 };
    }

    private byte[] getData2() {
        return new byte[] { (byte) 0x03, (byte) 0xC2, 0x21, 0x43, 0x65 };
    }

    private byte[] getData3() {
        return new byte[] { (byte) 0x00, 0x0B };
    }

    @Test(groups = { "functional.decode", "parameter" })
    public void testDecode() throws Exception {

        CallingPartyNumberImpl prim = new CallingPartyNumberImpl();
        prim.decode(getData());

        assertEquals(prim.getAddress(), "12345");
        assertEquals(prim.getNatureOfAddressIndicator(), NAINumber._NAI_NATIONAL_SN);
        assertEquals(prim.getNumberingPlanIndicator(), CallingPartyNumber._NPI_TELEX);
        assertEquals(prim.getNumberIncompleteIndicator(), CallingPartyNumber._NI_INCOMPLETE);
        assertEquals(prim.getAddressRepresentationRestrictedIndicator(), CallingPartyNumber._APRI_ALLOWED);
        assertEquals(prim.getScreeningIndicator(), CallingPartyNumber._SI_USER_PROVIDED_FAILED);
        assertTrue(prim.isOddFlag());

        prim = new CallingPartyNumberImpl();
        prim.decode(getData2());

        assertEquals(prim.getAddress(), "123456");
        assertEquals(prim.getNatureOfAddressIndicator(), NAINumber._NAI_NATIONAL_SN);
        assertEquals(prim.getNumberingPlanIndicator(), CallingPartyNumber._NPI_TELEX);
        assertEquals(prim.getNumberIncompleteIndicator(), CallingPartyNumber._NI_INCOMPLETE);
        assertEquals(prim.getAddressRepresentationRestrictedIndicator(), CallingPartyNumber._APRI_ALLOWED);
        assertEquals(prim.getScreeningIndicator(), CallingPartyNumber._SI_USER_PROVIDED_FAILED);
        assertFalse(prim.isOddFlag());

        prim = new CallingPartyNumberImpl();
        prim.decode(getData3());

        assertEquals(prim.getAddress(), "");
        assertEquals(prim.getNatureOfAddressIndicator(), 0);
        assertEquals(prim.getNumberingPlanIndicator(), 0);
        assertEquals(prim.getNumberIncompleteIndicator(), 0);
        assertEquals(prim.getAddressRepresentationRestrictedIndicator(), CallingPartyNumber._APRI_NOT_AVAILABLE);
        assertEquals(prim.getScreeningIndicator(), CallingPartyNumber._SI_NETWORK_PROVIDED);
        assertFalse(prim.isOddFlag());
    }

    @Test(groups = { "functional.encode", "parameter" })
    public void testEncode() throws Exception {

        CallingPartyNumberImpl prim = new CallingPartyNumberImpl(NAINumber._NAI_NATIONAL_SN, "12345",
                CallingPartyNumber._NPI_TELEX, CallingPartyNumber._NI_INCOMPLETE, CallingPartyNumber._APRI_ALLOWED,
                CallingPartyNumber._SI_USER_PROVIDED_FAILED);
        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int numberIncompleteIndicator,
        // int addressRepresentationREstrictedIndicator, int screeningIndicator

        byte[] data = getData();
        byte[] encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));

        prim = new CallingPartyNumberImpl(NAINumber._NAI_NATIONAL_SN, "123456", CallingPartyNumber._NPI_TELEX,
                CallingPartyNumber._NI_INCOMPLETE, CallingPartyNumber._APRI_ALLOWED,
                CallingPartyNumber._SI_USER_PROVIDED_FAILED);

        data = getData2();
        encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));

        prim = new CallingPartyNumberImpl(NAINumber._NAI_NATIONAL_SN, "123456", CallingPartyNumber._NPI_TELEX,
                CallingPartyNumber._NI_INCOMPLETE, CallingPartyNumber._APRI_NOT_AVAILABLE,
                CallingPartyNumber._SI_USER_PROVIDED_FAILED);

        data = getData3();
        encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));
    }

    @Test(groups = { "functional.xml.serialize", "parameter" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = ISUPJacksonXMLHelper.getXmlMapper();
        CallingPartyNumberImpl original = new CallingPartyNumberImpl(NAINumber._NAI_NATIONAL_SN, "12345",
                CallingPartyNumber._NPI_TELEX, CallingPartyNumber._NI_INCOMPLETE, CallingPartyNumber._APRI_ALLOWED,
                CallingPartyNumber._SI_USER_PROVIDED_FAILED);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        CallingPartyNumberImpl copy = xmlMapper.readValue(serializedEvent, CallingPartyNumberImpl.class);

        assertEquals(copy.getNatureOfAddressIndicator(), original.getNatureOfAddressIndicator());
        assertEquals(copy.getAddress(), original.getAddress());
        assertEquals(copy.getNumberingPlanIndicator(), original.getNumberingPlanIndicator());
        assertEquals(copy.getNumberIncompleteIndicator(), original.getNumberIncompleteIndicator());
        assertEquals(copy.getAddressRepresentationRestrictedIndicator(), original.getAddressRepresentationRestrictedIndicator());
        assertEquals(copy.getScreeningIndicator(), original.getScreeningIndicator());
    }

}
