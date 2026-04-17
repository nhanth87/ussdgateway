package org.restcomm.protocols.ss7.sccp.impl.parameter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0011Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.sccp.SCCPJacksonXMLHelper;

/**
 *
 * @author kulikov
 */
public class GT0011Test {

    private byte[] data = new byte[] { 0, 0x12, 0x09, 0x32, 0x26, 0x59, 0x18 };
    private ParameterFactoryImpl factory = new ParameterFactoryImpl();
    public GT0011Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    /**
     * Test of decode method, of class GT0011Codec.
     */
    @Test(groups = { "parameter", "functional.decode" })
    public void testDecode() throws Exception {
        // wrap data with input stream
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        // create GT object and read data from stream
        GlobalTitle0011Impl gt1 = new GlobalTitle0011Impl();
        gt1.decode(in, factory, SccpProtocolVersion.ITU);

        // check results
        assertEquals(gt1.getTranslationType(), 0);
        assertEquals(gt1.getNumberingPlan(), NumberingPlan.ISDN_TELEPHONY);
        assertEquals(gt1.getDigits(), "9023629581");
    }

    /**
     * Test of encode method, of class GT0011Codec.
     */
    @Test(groups = { "parameter", "functional.encode" })
    public void testEncode() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        GlobalTitle0011Impl gt = new GlobalTitle0011Impl("9023629581",0, BCDEvenEncodingScheme.INSTANCE, NumberingPlan.ISDN_TELEPHONY);

        gt.encode(bout, false, SccpProtocolVersion.ITU);

        byte[] res = bout.toByteArray();

        boolean correct = Arrays.equals(data, res);
        assertTrue(correct, "Incorrect encoding");
    }

    @Test(groups = { "parameter", "functional.encode" })
    public void testSerialization() throws Exception {
        XmlMapper xmlMapper = SCCPJacksonXMLHelper.getXmlMapper();
        GlobalTitle0011Impl gt = new GlobalTitle0011Impl("9023629581",0, BCDEvenEncodingScheme.INSTANCE, NumberingPlan.ISDN_TELEPHONY);

        String xml = xmlMapper.writeValueAsString(gt);
        System.out.println(xml);

        assertTrue(xml.contains("9023629581"));
        assertTrue(xml.contains("ISDN_TELEPHONY"));
    }

}
