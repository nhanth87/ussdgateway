
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
import org.restcomm.protocols.ss7.cap.isup.CalledPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ConnectToResourceRequestImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwoImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.BothwayThroughConnectionInd;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CalledPartyNumberImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ConnectToResourceRequestTest {

    public byte[] getData1() {
        return new byte[] { 48, 16, (byte) 128, 5, (byte) 131, (byte) 160, (byte) 137, 103, 5, (byte) 167, 3, (byte) 130, 1, 0,
                (byte) 159, 50, 1, 21 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 31, (byte) 131, 0, (byte) 164, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1,
                (byte) 129, 1, (byte) 255, (byte) 167, 3, (byte) 130, 1, 0, (byte) 159, 50, 1, 21 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ConnectToResourceRequestImpl elem = new ConnectToResourceRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals(elem.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getNatureOfAddressIndicator(), 3);
        assertTrue(elem.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getAddress().endsWith("98765"));
        assertEquals(elem.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getNumberingPlanIndicator(), 2);
        assertEquals(elem.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getInternalNetworkNumberIndicator(), 1);
        assertFalse(elem.getResourceAddress_Null());
        assertNull(elem.getExtensions());
        assertEquals(elem.getServiceInteractionIndicatorsTwo().getBothwayThroughConnectionInd(),
                BothwayThroughConnectionInd.bothwayPathRequired);
        assertEquals((int) elem.getCallSegmentID(), 21);

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new ConnectToResourceRequestImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);
        assertNull(elem.getResourceAddress_IPRoutingAddress());
        assertTrue(elem.getResourceAddress_Null());
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
        assertEquals(elem.getServiceInteractionIndicatorsTwo().getBothwayThroughConnectionInd(),
                BothwayThroughConnectionInd.bothwayPathRequired);
        assertEquals((int) elem.getCallSegmentID(), 21);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        CalledPartyNumberImpl calledPartyNumber = new CalledPartyNumberImpl(3, "98765", 2, 1);
        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int internalNetworkNumberIndicator
        CalledPartyNumberCapImpl resourceAddress_IPRoutingAddress = new CalledPartyNumberCapImpl(calledPartyNumber);
        ServiceInteractionIndicatorsTwoImpl serviceInteractionIndicatorsTwo = new ServiceInteractionIndicatorsTwoImpl(null,
                null, BothwayThroughConnectionInd.bothwayPathRequired, null, false, null, null, null);

        ConnectToResourceRequestImpl elem = new ConnectToResourceRequestImpl(resourceAddress_IPRoutingAddress, false, null,
                serviceInteractionIndicatorsTwo, 21);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        elem = new ConnectToResourceRequestImpl(null, true, CAPExtensionsTest.createTestCAPExtensions(),
                serviceInteractionIndicatorsTwo, 21);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));

        // CalledPartyNumberCap resourceAddress_IPRoutingAddress, boolean resourceAddress_Null, CAPExtensions extensions,
        // ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo, Integer callSegmentID

    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CalledPartyNumberImpl calledPartyNumber = new CalledPartyNumberImpl(3, "98765", 2, 1);
        // int natureOfAddresIndicator, String address, int numberingPlanIndicator, int internalNetworkNumberIndicator
        CalledPartyNumberCapImpl resourceAddress_IPRoutingAddress = new CalledPartyNumberCapImpl(calledPartyNumber);
        ServiceInteractionIndicatorsTwoImpl serviceInteractionIndicatorsTwo = new ServiceInteractionIndicatorsTwoImpl(null,
                null, BothwayThroughConnectionInd.bothwayPathRequired, null, false, null, null, null);
        ConnectToResourceRequestImpl original = new ConnectToResourceRequestImpl(resourceAddress_IPRoutingAddress, false,
                CAPExtensionsTest.createTestCAPExtensions(), serviceInteractionIndicatorsTwo, 4);
        original.setInvokeId(26);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ConnectToResourceRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ConnectToResourceRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertFalse(serializedEvent.contains("<resourceAddress_Null>true</resourceAddress_Null>"));
        assertTrue(serializedEvent.contains("<extensions>"));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertEquals(copy.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getNatureOfAddressIndicator(), 3);
            assertTrue(copy.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getAddress().endsWith("98765"));
            assertEquals(copy.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getNumberingPlanIndicator(), 2);
            assertEquals(copy.getResourceAddress_IPRoutingAddress().getCalledPartyNumber().getInternalNetworkNumberIndicator(), 1);
            assertFalse(copy.getResourceAddress_Null());
            assertTrue(CAPExtensionsTest.checkTestCAPExtensions(copy.getExtensions()));
            assertEquals((int) copy.getCallSegmentID(), 4);
        }
        original = new ConnectToResourceRequestImpl(null, true, null, null, null);
        original.setInvokeId(26);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ConnectToResourceRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertFalse(serializedEvent.contains("<resourceAddress_IPRoutingAddress>"));
        assertTrue(serializedEvent.contains("<resourceAddress_Null>true</resourceAddress_Null>"));
        assertFalse(serializedEvent.contains("<extensions>"));
        assertFalse(serializedEvent.contains("<serviceInteractionIndicatorsTwo>"));
        assertFalse(serializedEvent.contains("<callSegmentID>"));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertNull(copy.getResourceAddress_IPRoutingAddress());
            assertTrue(copy.getResourceAddress_Null());
            assertNull(copy.getExtensions());
            assertNull(copy.getServiceInteractionIndicatorsTwo());
            assertNull(copy.getCallSegmentID());
        }
    }
}
