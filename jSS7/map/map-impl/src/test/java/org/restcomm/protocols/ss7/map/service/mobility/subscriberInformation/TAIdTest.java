
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class TAIdTest {

    private byte[] getData() {
        return new byte[] { 12, 23, 45, 67, 78 };
    }

    @Test(groups = { "functional.xml.serialize", "subscriberInformation" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        TAIdImpl original = new TAIdImpl(getData());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        TAIdImpl copy = xmlMapper.readValue(serializedEvent, TAIdImpl.class);

        assertEquals(copy.getData(), original.getData());

    }

}
