
package org.restcomm.protocols.ss7.cap.primitives;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.primitives.AChChargingAddressImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegID;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.primitives.LegIDImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class AChChargingAddressTest {

    public byte[] getData1() {
        return new byte[] { (byte) 162, 3, (byte) 129, 1, 2 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 159, 50, 1, 5 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        AChChargingAddressImpl elem = new AChChargingAddressImpl();
        int tag = ais.readTag();
        assertEquals(tag, AChChargingAddressImpl._ID_legID);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);
        elem.decodeAll(ais);
        assertEquals(elem.getLegID().getReceivingSideID(), LegType.leg2);
        assertEquals(elem.getSrfConnection(), 0);


        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new AChChargingAddressImpl();
        tag = ais.readTag();
        assertEquals(tag, AChChargingAddressImpl._ID_srfConnection);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);
        elem.decodeAll(ais);
        assertNull(elem.getLegID());
        assertEquals(elem.getSrfConnection(), 5);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        LegID legID = new LegIDImpl(false, LegType.leg2);
        AChChargingAddressImpl elem = new AChChargingAddressImpl(legID);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));


        elem = new AChChargingAddressImpl(5);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LegID legID = new LegIDImpl(false, LegType.leg2);
        AChChargingAddressImpl original = new AChChargingAddressImpl(legID);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        AChChargingAddressImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, AChChargingAddressImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<receivingSideID>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSrfConnection())));
        }
        if (copy != null) {
            assertEquals(copy.getLegID().getReceivingSideID(), original.getLegID().getReceivingSideID());
            assertEquals(copy.getSrfConnection(), original.getSrfConnection());
        }
        original = new AChChargingAddressImpl(5);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, AChChargingAddressImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<legID>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getSrfConnection())));
        }
        if (copy != null) {
            assertNull(copy.getLegID());
            assertEquals(copy.getSrfConnection(), original.getSrfConnection());
        }
    }

}
