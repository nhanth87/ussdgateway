
package org.restcomm.protocols.ss7.map.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdOrLAIImpl;
import org.restcomm.protocols.ss7.map.primitives.LAIFixedLengthImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 * @author amit bhayani
 *
 */
public class CellGlobalIdOrServiceAreaIdOrLAITest {
    MAPParameterFactory MAPParameterFactory = new MAPParameterFactoryImpl();

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

    @Test(groups = { "functional.decode", "service.lsm" })
    public void testDecode() throws Exception {
        byte[] data = new byte[] { (byte) 0x80, 0x07, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b };

        AsnInputStream asn = new AsnInputStream(data);
        int tag = asn.readTag();

        CellGlobalIdOrServiceAreaIdOrLAIImpl cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl();
        cellGlobalIdOrServiceAreaIdOrLAI.decodeAll(asn);

        assertNotNull(cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength());
        assertTrue(Arrays.equals(new byte[] { 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b }, cellGlobalIdOrServiceAreaIdOrLAI
                .getCellGlobalIdOrServiceAreaIdFixedLength().getData()));

    }

    @Test(groups = { "functional.encode", "service.lsm" })
    public void testEncode() throws Exception {

        byte[] data = new byte[] { (byte) 0x80, 0x07, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b };

        CellGlobalIdOrServiceAreaIdFixedLength par = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(new byte[] { 0x05, 0x06,
                0x07, 0x08, 0x09, 0x0a, 0x0b });
        CellGlobalIdOrServiceAreaIdOrLAIImpl cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(par);
        AsnOutputStream asnOS = new AsnOutputStream();
        cellGlobalIdOrServiceAreaIdOrLAI.encodeAll(asnOS);

        byte[] encodedData = asnOS.toByteArray();

        assertTrue(Arrays.equals(data, encodedData));

    }

    @Test(groups = { "functional.xml.serialize", "service.lsm" })
    public void testSerialization() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        CellGlobalIdOrServiceAreaIdFixedLength par = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(250, 1, 4444, 3333);
        CellGlobalIdOrServiceAreaIdOrLAIImpl original = new CellGlobalIdOrServiceAreaIdOrLAIImpl(par);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        CellGlobalIdOrServiceAreaIdOrLAIImpl copy = xmlMapper.readValue(serializedEvent, CellGlobalIdOrServiceAreaIdOrLAIImpl.class);

        assertEquals(copy.getCellGlobalIdOrServiceAreaIdFixedLength().getMCC(), original
                .getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
        assertEquals(copy.getCellGlobalIdOrServiceAreaIdFixedLength().getMNC(), original
                .getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
        assertEquals(copy.getCellGlobalIdOrServiceAreaIdFixedLength().getLac(), original
                .getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
        assertEquals(copy.getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode(), original
                .getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());

        LAIFixedLengthImpl par2 = new LAIFixedLengthImpl(250, 1, 4444);
        original = new CellGlobalIdOrServiceAreaIdOrLAIImpl(par2);

        // Writes the area to a file.
        serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        copy = xmlMapper.readValue(serializedEvent, CellGlobalIdOrServiceAreaIdOrLAIImpl.class);

        assertEquals(copy.getLAIFixedLength().getMCC(), original.getLAIFixedLength().getMCC());
        assertEquals(copy.getLAIFixedLength().getMNC(), original.getLAIFixedLength().getMNC());
        assertEquals(copy.getLAIFixedLength().getLac(), original.getLAIFixedLength().getLac());
    }
}
