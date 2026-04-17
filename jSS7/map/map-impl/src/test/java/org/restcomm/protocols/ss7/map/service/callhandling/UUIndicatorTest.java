
package org.restcomm.protocols.ss7.map.service.callhandling;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.restcomm.protocols.ss7.map.service.callhandling.UUIndicatorImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class UUIndicatorTest {

    @Test(groups = { "functional.xml.serialize", "callhandling" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        UUIndicatorImpl original = new UUIndicatorImpl(136);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        UUIndicatorImpl copy = xmlMapper.readValue(serializedEvent, UUIndicatorImpl.class);

        assertEquals(copy.getData(), original.getData());

    }

}
