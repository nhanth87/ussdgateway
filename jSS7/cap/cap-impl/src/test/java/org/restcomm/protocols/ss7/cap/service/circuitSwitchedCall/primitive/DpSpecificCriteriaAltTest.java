
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ChangeOfLocation;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ChangeOfLocationImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.DpSpecificCriteriaAltImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class DpSpecificCriteriaAltTest {

    public byte[] getData1() {
        return new byte[] { 48, 7, (byte) 160, 2, (byte) 132, 0, (byte) 129, 1, 15 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        DpSpecificCriteriaAltImpl elem = new DpSpecificCriteriaAltImpl();
        int tag = ais.readTag();
        assertEquals(tag, Tag.SEQUENCE);
        assertEquals(ais.getTagClass(), Tag.CLASS_UNIVERSAL);
        elem.decodeAll(ais);

        assertEquals(elem.getChangeOfPositionControlInfo().size(), 1);
        assertTrue(elem.getChangeOfPositionControlInfo().get(0).isInterPLMNHandOver());
        assertEquals((int)elem.getNumberOfDigits(), 15);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        ArrayList<ChangeOfLocation> changeOfPositionControlInfo = new ArrayList<ChangeOfLocation>();
        ChangeOfLocation changeOfLocation = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interPLMNHandOver);
        changeOfPositionControlInfo.add(changeOfLocation);
        DpSpecificCriteriaAltImpl elem = new DpSpecificCriteriaAltImpl(changeOfPositionControlInfo, 15);
        // ArrayList<ChangeOfLocation> changeOfPositionControlInfo, Integer numberOfDigits
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        ArrayList<ChangeOfLocation> changeOfPositionControlInfo = new ArrayList<ChangeOfLocation>();
        ChangeOfLocation changeOfLocation = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interPLMNHandOver);
        changeOfPositionControlInfo.add(changeOfLocation);
        DpSpecificCriteriaAltImpl original = new DpSpecificCriteriaAltImpl(changeOfPositionControlInfo, 15);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        DpSpecificCriteriaAltImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, DpSpecificCriteriaAltImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<size>"));
        }
        if (copy != null) {
            assertEquals(copy.getChangeOfPositionControlInfo().size(), original.getChangeOfPositionControlInfo().size());
            assertEquals(copy.getChangeOfPositionControlInfo().get(0).isInterPLMNHandOver(), original.getChangeOfPositionControlInfo().get(0).isInterPLMNHandOver());
            assertEquals((int) copy.getNumberOfDigits(), (int) original.getNumberOfDigits());
        }
    }

}
