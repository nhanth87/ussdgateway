
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.MSClassmark2Impl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
*
* @author sergey vetyutnev
*
*/
public class MSClassmark2Test {

    private byte[] getMSClassmark2Data() {
        return new byte[] { 11, 12, 13 };
    }

    @Test(groups = { "functional.xml.serialize", "mobility.subscriberInformation" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        MSClassmark2Impl original = new MSClassmark2Impl(getMSClassmark2Data());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        MSClassmark2Impl copy = xmlMapper.readValue(serializedEvent, MSClassmark2Impl.class);

        assertEquals(copy.getData(), original.getData());
    }

}
