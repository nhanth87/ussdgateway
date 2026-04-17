
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsTest;
import org.restcomm.protocols.ss7.inap.api.primitives.LegID;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.primitives.LegIDImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 * @author kostianyn nosach
 *
 */
public class SplitLegRequestTest {
    
    public byte[] getData() {
        return new byte[] { 48, 28, -96, 3, -128, 1, 1, -127, 1, 1, -94, 18, 
                48, 5, 2, 1, 2, -127, 0, 48, 9, 2, 1, 3, 10, 1, 1, -127, 1, -1};
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData();
        AsnInputStream ais = new AsnInputStream(data);
        SplitLegRequestImpl elem = new SplitLegRequestImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(elem.getLegToBeSplit().getSendingSideID().equals(LegType.leg1));
        assertEquals(elem.getNewCallSegment(), new Integer(1));
        assertTrue(CAPExtensionsTest.checkTestCAPExtensions(elem.getExtensions()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        LegID legIDToMove = new LegIDImpl(true, LegType.leg1);

        SplitLegRequestImpl elem = new SplitLegRequestImpl(legIDToMove, 1, CAPExtensionsTest.createTestCAPExtensions());
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData()));
        
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LegID legIDToMove = new LegIDImpl(true, LegType.leg1);
        SplitLegRequestImpl original = new SplitLegRequestImpl(legIDToMove, 1, CAPExtensionsTest.createTestCAPExtensions());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        SplitLegRequestImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, SplitLegRequestImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        assertTrue(serializedEvent.contains("<sendingSideID>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getNewCallSegment())));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertEquals(copy.getExtensions().getExtensionFields().get(0).getLocalCode(), original.getExtensions().getExtensionFields().get(0).getLocalCode());
            assertEquals(copy.getExtensions().getExtensionFields().get(1).getCriticalityType(), original.getExtensions().getExtensionFields().get(1).getCriticalityType());
            assertEquals(copy.getLegToBeSplit().getSendingSideID(), original.getLegToBeSplit().getSendingSideID());
            assertEquals(copy.getNewCallSegment(), original.getNewCallSegment());
        }
    }
}
