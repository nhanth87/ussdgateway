
package org.restcomm.protocols.ss7.isup.impl.message.parameter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.restcomm.protocols.ss7.isup.impl.message.parameter.CallingPartyCategoryImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyCategory;
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
public class CallingPartyCategoryTest {
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
        return new byte[] { 9 };
    }

    @Test(groups = { "functional.decode", "parameter" })
    public void testDecode() throws Exception {

        CallingPartyCategoryImpl prim = new CallingPartyCategoryImpl();
        prim.decode(getData());

        assertEquals(prim.getCallingPartyCategory(), CallingPartyCategory._OPERATOR_NATIONAL);
    }

    @Test(groups = { "functional.encode", "parameter" })
    public void testEncode() throws Exception {

        CallingPartyCategoryImpl prim = new CallingPartyCategoryImpl(CallingPartyCategory._OPERATOR_NATIONAL);

        byte[] data = getData();
        byte[] encodedData = prim.encode();

        assertTrue(Arrays.equals(data, encodedData));

    }

    @Test(groups = { "functional.xml.serialize", "parameter" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = ISUPJacksonXMLHelper.getXmlMapper();
        CallingPartyCategoryImpl original = new CallingPartyCategoryImpl(CallingPartyCategory._OPERATOR_NATIONAL);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        CallingPartyCategoryImpl copy = xmlMapper.readValue(serializedEvent, CallingPartyCategoryImpl.class);

        assertEquals(copy.getCallingPartyCategory(), original.getCallingPartyCategory());
    }

}
