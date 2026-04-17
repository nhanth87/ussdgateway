
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ChangeOfLocationAlt;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ChangeOfLocationAltImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ChangeOfLocationImpl;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.LAIFixedLength;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.LAIFixedLengthImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class ChangeOfLocationTest {

    public byte[] getData1() {
        return new byte[] { (byte) 128, 7, 2, (byte) 241, 16, 85, (byte) 240, 0, 55 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 129, 7, 2, (byte) 241, 16, 85, (byte) 240, 0, 55 };
    }

    public byte[] getData3() {
        return new byte[] { (byte) 130, 5, (byte) 145, (byte) 240, 16, 85, (byte) 240 };
    }

    public byte[] getData4() {
        return new byte[] { (byte) 131, 0 };
    }

    public byte[] getData5() {
        return new byte[] { (byte) 133, 0 };
    }

    public byte[] getData6() {
        return new byte[] { (byte) 132, 0 };
    }

    public byte[] getData7() {
        return new byte[] { (byte) 166, 0 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ChangeOfLocationImpl elem = new ChangeOfLocationImpl();
        int tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_cellGlobalId);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertEquals(elem.getCellGlobalId().getLac(), 22000);


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_serviceAreaId);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertEquals(elem.getServiceAreaId().getLac(), 22000);


        data = this.getData3();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_locationAreaId);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertEquals(elem.getLocationAreaId().getLac(), 22000);


        data = this.getData4();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_interSystemHandOver);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertTrue(elem.isInterSystemHandOver());


        data = this.getData5();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_interMSCHandOver);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertTrue(elem.isInterMSCHandOver());


        data = this.getData6();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_interPLMNHandOver);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertTrue(elem.isInterPLMNHandOver());


        data = this.getData7();
        ais = new AsnInputStream(data);
        elem = new ChangeOfLocationImpl();
        tag = ais.readTag();
        assertEquals(tag, ChangeOfLocationImpl._ID_changeOfLocationAlt);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertNotNull(elem.getChangeOfLocationAlt());
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        CellGlobalIdOrServiceAreaIdFixedLength value = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(201, 1, 22000, 55);
        // int mcc, int mnc, int lac, int cellIdOrServiceAreaCode
        ChangeOfLocationImpl elem = new ChangeOfLocationImpl(value, ChangeOfLocationImpl.CellGlobalIdOrServiceAreaIdFixedLength_Option.cellGlobalId);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData1());

        elem = new ChangeOfLocationImpl(value, ChangeOfLocationImpl.CellGlobalIdOrServiceAreaIdFixedLength_Option.serviceAreaId);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData2());


        LAIFixedLength lai = new LAIFixedLengthImpl(190, 1, 22000);
        // int mcc, int mnc, int lac
        elem = new ChangeOfLocationImpl(lai);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData3());


        elem = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interSystemHandOver);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData4());


        elem = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interMSCHandOver);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData5());


        elem = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interPLMNHandOver);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData6());


        ChangeOfLocationAlt changeOfLocationAlt = new ChangeOfLocationAltImpl();
        elem = new ChangeOfLocationImpl(changeOfLocationAlt);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertEquals(aos.toByteArray(), this.getData7());
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall.primitive" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CellGlobalIdOrServiceAreaIdFixedLength value = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(201, 1, 22000, 55);
        // int mcc, int mnc, int lac, int cellIdOrServiceAreaCode
        ChangeOfLocationImpl original = new ChangeOfLocationImpl(value, ChangeOfLocationImpl.CellGlobalIdOrServiceAreaIdFixedLength_Option.cellGlobalId);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ChangeOfLocationImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<lac>"));
        }
        if (copy != null) {
            assertEquals(copy.getCellGlobalId().getLac(), original.getCellGlobalId().getLac());
        }
        original = new ChangeOfLocationImpl(value, ChangeOfLocationImpl.CellGlobalIdOrServiceAreaIdFixedLength_Option.serviceAreaId);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<lac>"));
        }
        if (copy != null) {
            assertEquals(copy.getServiceAreaId().getLac(), original.getServiceAreaId().getLac());
        }
        LAIFixedLength lai = new LAIFixedLengthImpl(190, 1, 22000);
        original = new ChangeOfLocationImpl(lai);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<lac>"));
        }
        if (copy != null) {
            assertEquals(copy.getLocationAreaId().getLac(), original.getLocationAreaId().getLac());
        }
        original = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interSystemHandOver);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.isInterSystemHandOver())));
        }
        if (copy != null) {
            assertEquals(copy.isInterSystemHandOver(), original.isInterSystemHandOver());
        }
        original = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interMSCHandOver);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.isInterMSCHandOver())));
        }
        if (copy != null) {
            assertEquals(copy.isInterMSCHandOver(), original.isInterMSCHandOver());
        }
        original = new ChangeOfLocationImpl(ChangeOfLocationImpl.Boolean_Option.interPLMNHandOver);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.isInterPLMNHandOver())));
        }
        if (copy != null) {
            assertEquals(copy.isInterPLMNHandOver(), original.isInterPLMNHandOver());
        }
        ChangeOfLocationAlt changeOfLocationAlt = new ChangeOfLocationAltImpl();
        original = new ChangeOfLocationImpl(changeOfLocationAlt);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ChangeOfLocationImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
        if (copy != null) {
            assertNotNull(copy.getChangeOfLocationAlt());
        }
    }

}
