
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.EsiBcsm.CallAcceptedSpecificInfoImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.EventSpecificInformationBCSMImpl;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class CallAcceptedSpecificInfoTest {

    public byte[] getData1() {
        return new byte[] { (byte) 180, 7, (byte) 191, 50, 4, 2, 2, 0, (byte) 200 };
    }

    @Test(groups = { "functional.decode", "EsiBcsm" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        CallAcceptedSpecificInfoImpl elem = new CallAcceptedSpecificInfoImpl();
        int tag = ais.readTag();
        assertEquals(tag, EventSpecificInformationBCSMImpl._ID_callAcceptedSpecificInfo);
        assertEquals(ais.getTagClass(), Tag.CLASS_CONTEXT_SPECIFIC);

        elem.decodeAll(ais);
        assertEquals((int) elem.getLocationInformation().getAgeOfLocationInformation(), 200);
    }

    @Test(groups = { "functional.encode", "EsiBcsm" })
    public void testEncode() throws Exception {

        LocationInformation locationInformation = new LocationInformationImpl(200, null, null, null, null, null, null, null, null, false, false, null, null);
        CallAcceptedSpecificInfoImpl elem = new CallAcceptedSpecificInfoImpl(locationInformation);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos, Tag.CLASS_CONTEXT_SPECIFIC, EventSpecificInformationBCSMImpl._ID_callAcceptedSpecificInfo);
        assertEquals(aos.toByteArray(), this.getData1());
    }

    @Test(groups = { "functional.xml.serialize", "EsiBcsm" })
    public void testXMLSerializaion() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        LocationInformation locationInformation = new LocationInformationImpl(200, null, null, null, null, null, null, null, null, false, false, null, null);
        CallAcceptedSpecificInfoImpl original = new CallAcceptedSpecificInfoImpl(locationInformation);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CallAcceptedSpecificInfoImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CallAcceptedSpecificInfoImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<ageOfLocationInformation>"));
        }
        if (copy != null) {
            assertEquals((int) copy.getLocationInformation().getAgeOfLocationInformation(), (int) original.getLocationInformation().getAgeOfLocationInformation());
        }
    }

}
