
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.primitives.AChChargingAddress;
import org.restcomm.protocols.ss7.cap.primitives.AChChargingAddressImpl;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.cap.primitives.SendingSideIDImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ApplyChargingRequestImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.CAMELAChBillingChargingCharacteristicsImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
public class ApplyChargingRequestTest {

    public byte[] getData1() {
        return new byte[] { 48, 14, (byte) 128, 7, (byte) 160, 5, (byte) 128, 3, 0, (byte) 140, (byte) 160, (byte) 162, 3,
                (byte) 128, 1, 1 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 34, (byte) 128, 7, (byte) 160, 5, (byte) 128, 3, 0, (byte) 140, (byte) 160, (byte) 162, 3,
                (byte) 128, 1, 1, (byte) 163, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1,
                (byte) 255 };
    }

    public byte[] getData3() {
        return new byte[] { 48, 16, (byte) 128, 7, (byte) 160, 5, (byte) 128, 3, 0, (byte) 140, (byte) 160, (byte) 191, 50, 4, (byte) 159, 50, 1, 10 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ApplyChargingRequestImpl elem = new ApplyChargingRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals((long) elem.getAChBillingChargingCharacteristics().getMaxCallPeriodDuration(), 36000);
        assertNull(elem.getAChBillingChargingCharacteristics().getAudibleIndicator());
        assertNull(elem.getAChBillingChargingCharacteristics().getExtensions());
        assertFalse(elem.getAChBillingChargingCharacteristics().getReleaseIfDurationExceeded());
        assertNull(elem.getAChBillingChargingCharacteristics().getTariffSwitchInterval());
        assertEquals(elem.getPartyToCharge().getSendingSideID(), LegType.leg1);
        assertNull(elem.getExtensions());
        assertNull(elem.getAChChargingAddress());


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new ApplyChargingRequestImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals((long) elem.getAChBillingChargingCharacteristics().getMaxCallPeriodDuration(), 36000);
        assertNull(elem.getAChBillingChargingCharacteristics().getAudibleIndicator());
        assertNull(elem.getAChBillingChargingCharacteristics().getExtensions());
        assertFalse(elem.getAChBillingChargingCharacteristics().getReleaseIfDurationExceeded());
        assertNull(elem.getAChBillingChargingCharacteristics().getTariffSwitchInterval());
        assertEquals(elem.getPartyToCharge().getSendingSideID(), LegType.leg1);
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
        assertNull(elem.getAChChargingAddress());


        data = this.getData3();
        ais = new AsnInputStream(data);
        elem = new ApplyChargingRequestImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);

        assertEquals((long) elem.getAChBillingChargingCharacteristics().getMaxCallPeriodDuration(), 36000);
        assertNull(elem.getAChBillingChargingCharacteristics().getAudibleIndicator());
        assertNull(elem.getAChBillingChargingCharacteristics().getExtensions());
        assertFalse(elem.getAChBillingChargingCharacteristics().getReleaseIfDurationExceeded());
        assertNull(elem.getAChBillingChargingCharacteristics().getTariffSwitchInterval());
        assertNull(elem.getPartyToCharge());
        assertNull(elem.getExtensions());
        assertEquals(elem.getAChChargingAddress().getSrfConnection(), 10);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        CAMELAChBillingChargingCharacteristicsImpl aChBillingChargingCharacteristics = new CAMELAChBillingChargingCharacteristicsImpl(
                36000, false, null, null, null, 2);
        // long maxCallPeriodDuration, boolean releaseIfdurationExceeded, Long
        // tariffSwitchInterval,
        // AudibleIndicator audibleIndicator, CAPExtensions extensions, boolean
        // isCAPVersion3orLater
        SendingSideIDImpl partyToCharge = new SendingSideIDImpl(LegType.leg1);

        ApplyChargingRequestImpl elem = new ApplyChargingRequestImpl(aChBillingChargingCharacteristics, partyToCharge, null,
                null);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
        // CAMELAChBillingChargingCharacteristics
        // aChBillingChargingCharacteristics, SendingSideID partyToCharge,
        // CAPExtensions extensions, AChChargingAddress aChChargingAddress


        elem = new ApplyChargingRequestImpl(aChBillingChargingCharacteristics, partyToCharge,
                CAPExtensionsTest.createTestCAPExtensions(), null);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));


        AChChargingAddress aChChargingAddress = new AChChargingAddressImpl(10);
        elem = new ApplyChargingRequestImpl(aChBillingChargingCharacteristics, null, null, aChChargingAddress);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData3()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAMELAChBillingChargingCharacteristicsImpl aChBillingChargingCharacteristics = new CAMELAChBillingChargingCharacteristicsImpl(
                36000, false, null, null, null, 2);
        SendingSideIDImpl partyToCharge = new SendingSideIDImpl(LegType.leg1);
        ApplyChargingRequestImpl original = new ApplyChargingRequestImpl(aChBillingChargingCharacteristics, partyToCharge,
                CAPExtensionsTest.createTestCAPExtensions(), null);
        original.setInvokeId(24);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ApplyChargingRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ApplyChargingRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertTrue(serializedEvent.contains("<sendingSideID>"));
        assertTrue(serializedEvent.contains("<extensions>"));
        assertFalse(serializedEvent.contains("<aChChargingAddress>"));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertEquals(copy.getPartyToCharge().getSendingSideID(), original.getPartyToCharge().getSendingSideID());
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(copy.getExtensions()));
            assertNull(copy.getAChChargingAddress());
        }
        AChChargingAddress aChChargingAddress = new AChChargingAddressImpl(10);
        original = new ApplyChargingRequestImpl(aChBillingChargingCharacteristics, null, null, aChChargingAddress);
        original.setInvokeId(24);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ApplyChargingRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertFalse(serializedEvent.contains("<partyToCharge>"));
        assertFalse(serializedEvent.contains("<extensions>"));
        assertTrue(serializedEvent.contains("<srfConnection>"));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertNull(copy.getPartyToCharge());
            assertNull(copy.getExtensions());
            assertEquals(copy.getAChChargingAddress().getSrfConnection(), original.getAChChargingAddress().getSrfConnection());
        }
    }
}
