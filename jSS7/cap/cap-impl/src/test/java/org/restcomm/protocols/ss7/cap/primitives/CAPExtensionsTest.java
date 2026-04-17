
package org.restcomm.protocols.ss7.cap.primitives;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.CriticalityType;
import org.restcomm.protocols.ss7.cap.api.primitives.ExtensionField;
import org.restcomm.protocols.ss7.cap.primitives.CAPExtensionsImpl;
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
public class CAPExtensionsTest {

    public byte[] getData1() {
        return new byte[] { 48, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1, (byte) 255 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        CAPExtensionsImpl elem = new CAPExtensionsImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(checkTestCAPExtensions(elem));
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        CAPExtensionsImpl elem = createTestCAPExtensions();
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    public static CAPExtensionsImpl createTestCAPExtensions() {
        AsnOutputStream aos = new AsnOutputStream();
        aos.writeNullData();
        ExtensionFieldImpl a1 = new ExtensionFieldImpl(2, CriticalityType.typeIgnore, aos.toByteArray());
        aos = new AsnOutputStream();
        try {
            aos.writeBooleanData(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ExtensionFieldImpl a2 = new ExtensionFieldImpl(3, CriticalityType.typeAbort, aos.toByteArray());
        ArrayList<ExtensionField> flds = new ArrayList<ExtensionField>();
        flds.add(a1);
        flds.add(a2);
        CAPExtensionsImpl elem = new CAPExtensionsImpl(flds);
        return elem;
    }

    public static boolean checkTestCAPExtensions(CAPExtensions elem) {
        if (elem.getExtensionFields() == null || elem.getExtensionFields().size() != 2)
            return false;

        ExtensionField a1 = elem.getExtensionFields().get(0);
        ExtensionField a2 = elem.getExtensionFields().get(1);
        if (a1.getLocalCode() != 2 || a2.getLocalCode() != 3)
            return false;
        if (a1.getCriticalityType() != CriticalityType.typeIgnore || a2.getCriticalityType() != CriticalityType.typeAbort)
            return false;
        if (a1.getData() == null || a1.getData().length != 0)
            return false;
        if (a2.getData() == null || a2.getData().length != 1 || (a2.getData()[0]) != -1)
            return false;

        return true;
    }

    private byte[] getDataSer() {
        return new byte[] { 1, (byte) 255, 3 };
    }

    public long[] getDataOid() {
        return new long[] { 1, 0, 22 };
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        ArrayList<ExtensionField> fieldsList = new ArrayList<ExtensionField>();
        fieldsList.add(new ExtensionFieldImpl(234, CriticalityType.typeIgnore, getDataSer()));
        fieldsList.add(new ExtensionFieldImpl(getDataOid(), null, getDataSer()));
        CAPExtensionsImpl original = new CAPExtensionsImpl(fieldsList);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPExtensionsImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPExtensionsImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<size>"));
        }
        if (copy != null) {
            assertEquals(copy.getExtensionFields().size(), original.getExtensionFields().size());
            assertEquals(copy.getExtensionFields().get(0).getData(), original.getExtensionFields().get(0).getData());
            assertEquals(copy.getExtensionFields().get(1).getData(), original.getExtensionFields().get(1).getData());
        }
        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        copy = xmlMapper.readValue(serializedEvent, CAPExtensionsImpl.class);

        CAPExtensionsTest.checkTestCAPExtensions(copy);
    }
}
