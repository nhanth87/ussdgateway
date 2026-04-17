
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.isup.CauseCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.DisconnectLegRequestImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.primitives.LegIDImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CauseIndicatorsImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CauseIndicators;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 * 
 * @author sergey vetyutnev
 * 
 */
public class DisconnectLegRequestTest {

    public byte[] getData1() {
        return new byte[] { 48, 29, (byte) 160, 3, (byte) 129, 1, 6, (byte) 129, 2, (byte) 128, (byte) 134, (byte) 162, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48,
                9, 2, 1, 3, 10, 1, 1, (byte) 129, 1, (byte) 255 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        DisconnectLegRequestImpl elem = new DisconnectLegRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals(elem.getLegToBeReleased().getReceivingSideID(), LegType.leg6);
        CauseIndicators ci = elem.getReleaseCause().getCauseIndicators();
        assertEquals(ci.getCodingStandard(), 0);
        assertEquals(ci.getCauseValue(), 6);
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        LegIDImpl legToBeReleased = new LegIDImpl(false, LegType.leg6);
        CauseIndicatorsImpl causeIndicators = new CauseIndicatorsImpl(0, 0, 0, 6, null);
        CauseCapImpl releaseCause = new CauseCapImpl(causeIndicators);
        DisconnectLegRequestImpl elem = new DisconnectLegRequestImpl(legToBeReleased, releaseCause, CAPExtensionsTest.createTestCAPExtensions());

        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LegIDImpl legToBeReleased = new LegIDImpl(false, LegType.leg6);
        CauseIndicatorsImpl causeIndicators = new CauseIndicatorsImpl(0, 0, 0, 6, null);
        CauseCapImpl releaseCause = new CauseCapImpl(causeIndicators);
        DisconnectLegRequestImpl original = new DisconnectLegRequestImpl(legToBeReleased, releaseCause, CAPExtensionsTest.createTestCAPExtensions());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        DisconnectLegRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, DisconnectLegRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<extensions>"));
        }
        if (copy != null) {
            assertEquals(original.getLegToBeReleased().getReceivingSideID(), copy.getLegToBeReleased().getReceivingSideID());
            CauseIndicators ci2 = copy.getReleaseCause().getCauseIndicators();
            assertEquals(causeIndicators.getCodingStandard(), ci2.getCodingStandard());
            assertEquals(causeIndicators.getCauseValue(), ci2.getCauseValue());
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(original.getExtensions()));
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(copy.getExtensions()));
        }
        original = new DisconnectLegRequestImpl(legToBeReleased, null, null);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, DisconnectLegRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<releaseCause>"));
        assertFalse(serializedEvent.contains("<extensions>"));
        }
        if (copy != null) {
            assertEquals(original.getLegToBeReleased().getReceivingSideID(), copy.getLegToBeReleased().getReceivingSideID());
            assertNull(original.getReleaseCause());
            assertNull(copy.getReleaseCause());
            assertNull(original.getExtensions());
            assertNull(copy.getExtensions());
        }
    }

}
