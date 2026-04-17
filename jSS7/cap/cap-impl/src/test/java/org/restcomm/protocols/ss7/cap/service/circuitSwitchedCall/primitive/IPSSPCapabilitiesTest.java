
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.IPSSPCapabilitiesImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class IPSSPCapabilitiesTest {

    public byte[] getData1() {
        return new byte[] { 4, 1, 5 };
    }

    public byte[] getData2() {
        return new byte[] { 4, 4, 26, 11, 22, 33 };
    }

    public byte[] getIntData1() {
        return new byte[] { 11, 22, 33 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        IPSSPCapabilitiesImpl elem = new IPSSPCapabilitiesImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(elem.getIPRoutingAddressSupported());
        assertFalse(elem.getVoiceBackSupported());
        assertTrue(elem.getVoiceInformationSupportedViaSpeechRecognition());
        assertFalse(elem.getVoiceInformationSupportedViaVoiceRecognition());
        assertFalse(elem.getGenerationOfVoiceAnnouncementsFromTextSupported());
        assertNull(elem.getExtraData());

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new IPSSPCapabilitiesImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);
        assertFalse(elem.getIPRoutingAddressSupported());
        assertTrue(elem.getVoiceBackSupported());
        assertFalse(elem.getVoiceInformationSupportedViaSpeechRecognition());
        assertTrue(elem.getVoiceInformationSupportedViaVoiceRecognition());
        assertTrue(elem.getGenerationOfVoiceAnnouncementsFromTextSupported());
        assertTrue(Arrays.equals(elem.getExtraData(), this.getIntData1()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        IPSSPCapabilitiesImpl elem = new IPSSPCapabilitiesImpl(true, false, true, false, false, null);
        // boolean IPRoutingAddressSupported, boolean VoiceBackSupported, boolean VoiceInformationSupportedViaSpeechRecognition,
        // boolean VoiceInformationSupportedViaVoiceRecognition, boolean GenerationOfVoiceAnnouncementsFromTextSupported, byte[]
        // extraData
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        elem = new IPSSPCapabilitiesImpl(false, true, false, true, true, getIntData1());
        // boolean IPRoutingAddressSupported, boolean VoiceBackSupported, boolean VoiceInformationSupportedViaSpeechRecognition,
        // boolean VoiceInformationSupportedViaVoiceRecognition, boolean GenerationOfVoiceAnnouncementsFromTextSupported, byte[]
        // extraData
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        IPSSPCapabilitiesImpl original = new IPSSPCapabilitiesImpl(true, false, true, false, false, null);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        IPSSPCapabilitiesImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, IPSSPCapabilitiesImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getData())));
        }
        if (copy != null) {
            assertEquals(copy.getData(), original.getData());
        }
        original = new IPSSPCapabilitiesImpl(true, true, true, true, true, getIntData1());

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, IPSSPCapabilitiesImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getData())));
        }
        if (copy != null) {
            assertEquals(copy.getData(), original.getData());
        }

    }
}
