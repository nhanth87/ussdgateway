
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.protocols.asn.BitSetStrictLength;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSGIdImpl;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class CSGIdTest {

    private BitSetStrictLength getData() {
        BitSetStrictLength res = new BitSetStrictLength(27);
        res.set(1);
        res.set(5);
        res.set(10);
        res.set(26);
        return res;
    }

    @Test(groups = { "functional.xml.serialize", "subscriberInformation" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        CSGIdImpl original = new CSGIdImpl(getData());

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);

        System.out.println(serializedEvent);

        CSGIdImpl copy = xmlMapper.readValue(serializedEvent, CSGIdImpl.class);

        for (int i1 = 0; i1 < original.getData().getStrictLength(); i1++) {
            assertEquals(copy.getData().get(i1), original.getData().get(i1));
        }
        assertEquals(copy.getData().getStrictLength(), original.getData().getStrictLength());

    }

}
