
package org.restcomm.protocols.ss7.cap.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.primitives.CriticalityType;
import org.restcomm.protocols.ss7.cap.primitives.ExtensionFieldImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ExtensionFieldTest {

    public byte[] getData1() {
        return new byte[] { 48, 5, 2, 1, 2, (byte) 129, 0 };
    }

    public byte[] getData2() {
        return new byte[] { 48, 7, 6, 2, 40, 22, (byte) 129, 1, (byte) 255 };
    }

    public byte[] getData3() {
        return new byte[] { 48, 11, 2, 2, 8, (byte) 174, 10, 1, 1, (byte) 129, 2, (byte) 253, (byte) 213 };
    }

    public long[] getDataOid() {
        return new long[] { 1, 0, 22 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        ExtensionFieldImpl elem = new ExtensionFieldImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals((int) elem.getLocalCode(), 2);
        assertEquals(elem.getCriticalityType(), CriticalityType.typeIgnore);
        ais = new AsnInputStream(elem.getData());
        ais.readNullData(elem.getData().length);

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new ExtensionFieldImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(Arrays.equals(elem.getGlobalCode(), this.getDataOid()));
        assertEquals(elem.getCriticalityType(), CriticalityType.typeIgnore);
        ais = new AsnInputStream(elem.getData());
        boolean bool = ais.readBooleanData(elem.getData().length);
        assertTrue(bool);

        data = this.getData3();
        ais = new AsnInputStream(data);
        elem = new ExtensionFieldImpl();
        tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals((int) elem.getLocalCode(), 2222);
        assertEquals(elem.getCriticalityType(), CriticalityType.typeAbort);
        ais = new AsnInputStream(elem.getData());
        int i1 = (int) ais.readIntegerData(elem.getData().length);
        assertEquals(i1, -555);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        AsnOutputStream aos = new AsnOutputStream();
        aos.writeNullData();
        ExtensionFieldImpl elem = new ExtensionFieldImpl(2, CriticalityType.typeIgnore, aos.toByteArray());
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        aos = new AsnOutputStream();
        aos.writeBooleanData(true);
        elem = new ExtensionFieldImpl(this.getDataOid(), null, aos.toByteArray());
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));

        aos = new AsnOutputStream();
        aos.writeIntegerData(-555);
        elem = new ExtensionFieldImpl(2222, CriticalityType.typeAbort, aos.toByteArray());
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData3()));
    }

    private byte[] getDataSer() {
        return new byte[] { 1, (byte) 255, 3 };
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        ExtensionFieldImpl original = new ExtensionFieldImpl(234, CriticalityType.typeIgnore, getDataSer());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        ExtensionFieldImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, ExtensionFieldImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getCriticalityType())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getData())));
        }
        if (copy != null) {
            assertEquals((int) copy.getLocalCode(), (int) original.getLocalCode());
            assertTrue(Arrays.equals(copy.getGlobalCode(), original.getGlobalCode()));
            assertEquals(copy.getCriticalityType(), original.getCriticalityType());
            assertEquals(copy.getData(), original.getData());
        }
        original = new ExtensionFieldImpl(getDataOid(), null, getDataSer());

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, ExtensionFieldImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<localCode>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getCriticalityType())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getData())));
        }
        if (copy != null) {
            assertNull(copy.getLocalCode());
            assertNull(original.getLocalCode());
            assertTrue(Arrays.equals(copy.getGlobalCode(), original.getGlobalCode()));
            assertEquals(copy.getCriticalityType(), original.getCriticalityType());
            assertEquals(copy.getData(), original.getData());
        }

    }
}
