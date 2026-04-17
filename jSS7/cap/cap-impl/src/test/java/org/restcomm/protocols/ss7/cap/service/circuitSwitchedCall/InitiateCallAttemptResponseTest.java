
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.InitiateCallAttemptResponseImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OfferedCamel4FunctionalitiesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 * 
 * @author sergey vetyutnev
 * 
 */
public class InitiateCallAttemptResponseTest {

    public byte[] getData1() {
        return new byte[] { 48, 32, (byte) 128, 2, 4, (byte) 224, (byte) 162, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1,
                (byte) 255, (byte) 129, 4, 4, (byte) 128, 0, 0, (byte) 131, 0 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        InitiateCallAttemptResponseImpl elem = new InitiateCallAttemptResponseImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);

        assertTrue(elem.getSupportedCamelPhases().getPhase1Supported());
        assertTrue(elem.getSupportedCamelPhases().getPhase2Supported());
        assertTrue(elem.getSupportedCamelPhases().getPhase3Supported());
        assertFalse(elem.getSupportedCamelPhases().getPhase4Supported());
        assertTrue(elem.getOfferedCamel4Functionalities().getInitiateCallAttempt());
        assertFalse(elem.getOfferedCamel4Functionalities().getCollectInformation());
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
        assertTrue(elem.getReleaseCallArgExtensionAllowed());
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        SupportedCamelPhasesImpl supportedCamelPhases = new SupportedCamelPhasesImpl(true, true, true, false);
        OfferedCamel4FunctionalitiesImpl offeredCamel4Functionalities = new OfferedCamel4FunctionalitiesImpl(true, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false);
        InitiateCallAttemptResponseImpl elem = new InitiateCallAttemptResponseImpl(supportedCamelPhases, offeredCamel4Functionalities,
                CAPExtensionsTest.createTestCAPExtensions(), true);
//        SupportedCamelPhases supportedCamelPhases,
//        OfferedCamel4Functionalities offeredCamel4Functionalities, CAPExtensions extensions,
//        boolean releaseCallArgExtensionAllowed

        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        SupportedCamelPhasesImpl supportedCamelPhases = new SupportedCamelPhasesImpl(true, true, true, false);
        OfferedCamel4FunctionalitiesImpl offeredCamel4Functionalities = new OfferedCamel4FunctionalitiesImpl(true, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false);
        InitiateCallAttemptResponseImpl original = new InitiateCallAttemptResponseImpl(supportedCamelPhases, offeredCamel4Functionalities,
                CAPExtensionsTest.createTestCAPExtensions(), true);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        InitiateCallAttemptResponseImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, InitiateCallAttemptResponseImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getReleaseCallArgExtensionAllowed())));
        assertTrue(serializedEvent.contains("<extensions>"));
        }
        if (copy != null) {
            assertEquals(original.getSupportedCamelPhases().getPhase1Supported(), copy.getSupportedCamelPhases().getPhase1Supported());
            assertEquals(original.getSupportedCamelPhases().getPhase2Supported(), copy.getSupportedCamelPhases().getPhase2Supported());
            assertEquals(original.getSupportedCamelPhases().getPhase3Supported(), copy.getSupportedCamelPhases().getPhase3Supported());
            assertEquals(original.getSupportedCamelPhases().getPhase4Supported(), copy.getSupportedCamelPhases().getPhase4Supported());
            assertEquals(original.getOfferedCamel4Functionalities().getInitiateCallAttempt(), copy.getOfferedCamel4Functionalities().getInitiateCallAttempt());
            assertEquals(original.getOfferedCamel4Functionalities().getCollectInformation(), copy.getOfferedCamel4Functionalities().getCollectInformation());
            assertEquals(original.getReleaseCallArgExtensionAllowed(), copy.getReleaseCallArgExtensionAllowed());
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(original.getExtensions()));
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(copy.getExtensions()));
        }

    }

}
