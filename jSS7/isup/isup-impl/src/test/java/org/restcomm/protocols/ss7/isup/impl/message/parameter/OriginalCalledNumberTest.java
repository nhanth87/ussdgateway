
package org.restcomm.protocols.ss7.isup.impl.message.parameter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.restcomm.protocols.ss7.isup.impl.message.parameter.OriginalCalledNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.NAINumber;
import org.restcomm.protocols.ss7.isup.message.parameter.OriginalCalledNumber;
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
public class OriginalCalledNumberTest {
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
        return new byte[] { (byte) 131, 68, 0x21, 0x43, 0x05 };
    }

    private byte[] getData2() {
        return new byte[] { 0, 8 };
    }

    @Test(groups = { "functional.decode", "parameter" })
    public void testDecode() throws Exception {

        OriginalCalledNumberImpl prim = new OriginalCalledNumberImpl();
        prim.decode(getData());

        assertEquals(prim.getAddress(), "12345");
        assertEquals(prim.getNatureOfAddressIndicator(), NAINumber._NAI_NATIONAL_SN);
        assertEquals(prim.getNumberingPlanIndicator(), CallingPartyNumber._NPI_TELEX);
        assertEquals(prim.getAddressRepresentationRestrictedIndicator(), CallingPartyNumber._APRI_RESTRICTED);
        assertTrue(prim.isOddFlag());

        prim = new OriginalCalledNumberImpl();
        prim.decode(getData2());

        assertEquals(prim.getAddress(), "");
        assertEquals(prim.getNatureOfAddressIndicator(), 0);
        assertEquals(prim.getNumberingPlanIndicator(), 0);
        assertEquals(prim.getAddressRepresentationRestrictedIndicator(), CallingPartyNumber._APRI_NOT_AVAILABLE);
        assertFalse(prim.isOddFlag());

    }

    @Test(groups = { "functional.encode", "parameter" })
    public void testEncode() throws Exception {

        OriginalCalledNumberImpl prim = new OriginalCalledNumberImpl(OriginalCalledNumber._NAI_NATIONAL_SN, "12345",
                OriginalCalledNumber._NPI_TELEX, OriginalCalledNumber._APRI_RESTRICTED);
        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationRestrictedIndicator

        byte[] data = getData();
        byte[] encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));

        prim = new OriginalCalledNumberImpl(0, "", 0, OriginalCalledNumber._APRI_NOT_AVAILABLE);

        data = getData2();
        encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));

    }

    @Test(groups = { "functional.xml.serialize", "parameter" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = ISUPJacksonXMLHelper.getXmlMapper();
        OriginalCalledNumberImpl original = new OriginalCalledNumberImpl(OriginalCalledNumber._NAI_NATIONAL_SN, "12345",
                OriginalCalledNumber._NPI_TELEX, OriginalCalledNumber._APRI_RESTRICTED);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        OriginalCalledNumberImpl copy = xmlMapper.readValue(serializedEvent, OriginalCalledNumberImpl.class);

        assertEquals(copy.getNatureOfAddressIndicator(), original.getNatureOfAddressIndicator());
        assertEquals(copy.getAddress(), original.getAddress());
        assertEquals(copy.getNumberingPlanIndicator(), original.getNumberingPlanIndicator());
        assertEquals(copy.getAddressRepresentationRestrictedIndicator(), original.getAddressRepresentationRestrictedIndicator());
    }

}
