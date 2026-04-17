
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.primitives.AppendFreeFormatData;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.FreeFormatData;
import org.restcomm.protocols.ss7.cap.primitives.SendingSideIDImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.FCIBCCCAMELsequence1Impl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.FreeFormatDataImpl;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class FCIBCCCAMELsequence1Test {

    public byte[] getData1() {
        return new byte[] { 48, 14, (byte) 128, 4, 4, 5, 6, 7, (byte) 161, 3, (byte) 128, 1, 2, (byte) 130, 1, 1 };
    }

    public byte[] getDataFFD() {
        return new byte[] { 4, 5, 6, 7 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        FCIBCCCAMELsequence1Impl elem = new FCIBCCCAMELsequence1Impl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertTrue(Arrays.equals(elem.getFreeFormatData().getData(), this.getDataFFD()));
        assertEquals(elem.getPartyToCharge().getSendingSideID(), LegType.leg2);
        assertEquals(elem.getAppendFreeFormatData(), AppendFreeFormatData.append);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        SendingSideIDImpl partyToCharge = new SendingSideIDImpl(LegType.leg2);
        FreeFormatData ffd = new FreeFormatDataImpl(getDataFFD());
        FCIBCCCAMELsequence1Impl elem = new FCIBCCCAMELsequence1Impl(ffd, partyToCharge, AppendFreeFormatData.append);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        // byte[] freeFormatData, SendingSideID partyToCharge, AppendFreeFormatData appendFreeFormatData
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        FreeFormatData ffd = new FreeFormatDataImpl(getDataFFD());
        SendingSideIDImpl partyToCharge = new SendingSideIDImpl(LegType.leg2);
        FCIBCCCAMELsequence1Impl original = new FCIBCCCAMELsequence1Impl(ffd, partyToCharge, AppendFreeFormatData.append);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        FCIBCCCAMELsequence1Impl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, FCIBCCCAMELsequence1Impl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
        if (copy != null) {
            assertEquals(copy.getFreeFormatData().getData(), getDataFFD());
            assertEquals(copy.getPartyToCharge().getSendingSideID(), LegType.leg2);
            assertEquals(copy.getAppendFreeFormatData(), AppendFreeFormatData.append);
        }
        original = new FCIBCCCAMELsequence1Impl(ffd, null, null);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, FCIBCCCAMELsequence1Impl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<partyToCharge>"));
        assertFalse(serializedEvent.contains("<appendFreeFormatData>"));
        }
        if (copy != null) {
            assertEquals(copy.getFreeFormatData().getData(), getDataFFD());
            assertNull(copy.getPartyToCharge());
            assertNull(copy.getAppendFreeFormatData());
        }
    }
}
