
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class RAIdentityTest {

    private byte[] getData() {
        return new byte[] { 1, 2, 3, 4, 5, 6 };
    }

    @Test(groups = { "functional.xml.serialize", "subscriberInformation" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        RAIdentityImpl original = new RAIdentityImpl(getData());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        RAIdentityImpl copy = xmlMapper.readValue(serializedEvent, RAIdentityImpl.class);

        assertEquals(copy.getData(), original.getData());
    }

}
