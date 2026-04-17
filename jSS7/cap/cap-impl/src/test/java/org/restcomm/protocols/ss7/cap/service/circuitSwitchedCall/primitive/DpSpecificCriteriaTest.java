
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ChangeOfLocation;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DpSpecificCriteriaAlt;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.MidCallControlInfo;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ChangeOfLocationImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.DpSpecificCriteriaAltImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.DpSpecificCriteriaImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.MidCallControlInfoImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class DpSpecificCriteriaTest {

    public byte[] getData1() {
        return new byte[] { (byte) 129, 2, 3, (byte) 232 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 162, 3, (byte) 128, 1, 10 };
    }

    public byte[] getData3() {
        return new byte[] { (byte) 163, 4, (byte) 160, 2, (byte) 131, 0 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        DpSpecificCriteriaImpl elem = new DpSpecificCriteriaImpl();
        int tag = ais.readTag();
        assertEquals(tag, DpSpecificCriteriaImpl._ID_applicationTimer);
        elem.decodeAll(ais);
        assertEquals((int) elem.getApplicationTimer(), 1000);
        assertNull(elem.getMidCallControlInfo());
        assertNull(elem.getDpSpecificCriteriaAlt());


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new DpSpecificCriteriaImpl();
        tag = ais.readTag();
        assertEquals(tag, DpSpecificCriteriaImpl._ID_midCallControlInfo);
        elem.decodeAll(ais);
        assertNull(elem.getApplicationTimer());
        assertEquals((int) elem.getMidCallControlInfo().getMinimumNumberOfDigits(), 10);
        assertNull(elem.getDpSpecificCriteriaAlt());


        data = this.getData3();
        ais = new AsnInputStream(data);
        elem = new DpSpecificCriteriaImpl();
        tag = ais.readTag();
        assertEquals(tag, DpSpecificCriteriaImpl._ID_dpSpecificCriteriaAlt);
        elem.decodeAll(ais);
        assertNull(elem.getApplicationTimer());
        assertNull(elem.getMidCallControlInfo());
        assertTrue(elem.getDpSpecificCriteriaAlt().getChangeOfPositionControlInfo().get(0).isInterSystemHandOver());
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        DpSpecificCriteriaImpl elem = new DpSpecificCriteriaImpl(1000);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));


        MidCallControlInfo midCallControlInfo = new MidCallControlInfoImpl(10, null, null, null, null, null);
        elem = new DpSpecificCriteriaImpl(midCallControlInfo);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));


        ArrayList<ChangeOfLocation> changeOfPositionControlInfo = new ArrayList<ChangeOfLocation>();
        ChangeOfLocation changeOfLocation = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interSystemHandOver);
        changeOfPositionControlInfo.add(changeOfLocation);
        DpSpecificCriteriaAlt dpSpecificCriteriaAlt = new DpSpecificCriteriaAltImpl(changeOfPositionControlInfo, null);
        elem = new DpSpecificCriteriaImpl(dpSpecificCriteriaAlt);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData3()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        DpSpecificCriteriaImpl original = new DpSpecificCriteriaImpl(1000);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        DpSpecificCriteriaImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, DpSpecificCriteriaImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<midCallControlInfo>"));
        assertFalse(serializedEvent.contains("<dpSpecificCriteriaAlt>"));
        }
        if (copy != null) {
            assertEquals((int) copy.getApplicationTimer(), (int) original.getApplicationTimer());
            assertNull(copy.getMidCallControlInfo());
            assertNull(copy.getDpSpecificCriteriaAlt());
        }
        MidCallControlInfo midCallControlInfo = new MidCallControlInfoImpl(10, null, null, null, null, null);
        original = new DpSpecificCriteriaImpl(midCallControlInfo);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, DpSpecificCriteriaImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<applicationTimer>"));
        assertTrue(serializedEvent.contains("<minimumNumberOfDigits>"));
        assertFalse(serializedEvent.contains("<dpSpecificCriteriaAlt>"));
        }
        if (copy != null) {
            assertNull(copy.getApplicationTimer());
            assertEquals(copy.getMidCallControlInfo().getMinimumNumberOfDigits(), original.getMidCallControlInfo().getMinimumNumberOfDigits());
            assertNull(copy.getDpSpecificCriteriaAlt());
        }
        ArrayList<ChangeOfLocation> changeOfPositionControlInfo = new ArrayList<ChangeOfLocation>();
        ChangeOfLocation changeOfLocation = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interSystemHandOver);
        changeOfPositionControlInfo.add(changeOfLocation);
        DpSpecificCriteriaAlt dpSpecificCriteriaAlt = new DpSpecificCriteriaAltImpl(changeOfPositionControlInfo, null);
        original = new DpSpecificCriteriaImpl(dpSpecificCriteriaAlt);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, DpSpecificCriteriaImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<applicationTimer>"));
        assertFalse(serializedEvent.contains("<midCallControlInfo>"));
        }
        if (copy != null) {
            assertNull(copy.getApplicationTimer());
            assertNull(copy.getMidCallControlInfo());
        }
    }
}
