
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.SubscriberStateImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class SubscriberStateTest {

    private byte[] getEncodedData1() {
        return new byte[] { (byte) 128, 0 };
    }

    private byte[] getEncodedData2() {
        return new byte[] { (byte) 129, 0 };
    }

    private byte[] getEncodedData3() {
        return new byte[] { 10, 1, 1 };
    }

    @Test(groups = { "functional.decode", "primitives" })
    public void testDecode() throws Exception {

        byte[] rawData = getEncodedData1();
        AsnInputStream asn = new AsnInputStream(rawData);
        int tag = asn.readTag();
        SubscriberStateImpl impl = new SubscriberStateImpl();
        impl.decodeAll(asn);
        assertEquals(impl.getSubscriberStateChoice(), SubscriberStateChoice.assumedIdle);
        assertNull(impl.getNotReachableReason());

        rawData = getEncodedData2();
        asn = new AsnInputStream(rawData);
        tag = asn.readTag();
        impl = new SubscriberStateImpl();
        impl.decodeAll(asn);
        assertEquals(impl.getSubscriberStateChoice(), SubscriberStateChoice.camelBusy);
        assertNull(impl.getNotReachableReason());

        rawData = getEncodedData3();
        asn = new AsnInputStream(rawData);
        tag = asn.readTag();
        impl = new SubscriberStateImpl();
        impl.decodeAll(asn);
        assertEquals(impl.getSubscriberStateChoice(), SubscriberStateChoice.netDetNotReachable);
        assertEquals(impl.getNotReachableReason(), NotReachableReason.imsiDetached);
    }

    @Test(groups = { "functional.encode", "primitives" })
    public void testEncode() throws Exception {

        SubscriberStateImpl impl = new SubscriberStateImpl(SubscriberStateChoice.assumedIdle, null);
        AsnOutputStream asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS);
        byte[] encodedData = asnOS.toByteArray();
        byte[] rawData = getEncodedData1();
        assertTrue(Arrays.equals(rawData, encodedData));

        impl = new SubscriberStateImpl(SubscriberStateChoice.camelBusy, null);
        asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS);
        encodedData = asnOS.toByteArray();
        rawData = getEncodedData2();
        assertTrue(Arrays.equals(rawData, encodedData));

        impl = new SubscriberStateImpl(SubscriberStateChoice.netDetNotReachable, NotReachableReason.imsiDetached);
        asnOS = new AsnOutputStream();
        impl.encodeAll(asnOS);
        encodedData = asnOS.toByteArray();
        rawData = getEncodedData3();
        assertTrue(Arrays.equals(rawData, encodedData));
    }

    @Test(groups = { "functional.xml.serialize", "primitives" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        SubscriberStateImpl original = new SubscriberStateImpl(SubscriberStateChoice.assumedIdle, null);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        SubscriberStateImpl copy = xmlMapper.readValue(serializedEvent, SubscriberStateImpl.class);

        assertEquals(copy.getSubscriberStateChoice(), original.getSubscriberStateChoice());
        assertEquals(copy.getNotReachableReason(), original.getNotReachableReason());

        original = new SubscriberStateImpl(SubscriberStateChoice.netDetNotReachable, NotReachableReason.imsiDetached);

        // Writes the area to a file.
        serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        copy = xmlMapper.readValue(serializedEvent, SubscriberStateImpl.class);

        assertEquals(copy.getSubscriberStateChoice(), original.getSubscriberStateChoice());
        assertEquals(copy.getNotReachableReason(), original.getNotReachableReason());
    }
}
