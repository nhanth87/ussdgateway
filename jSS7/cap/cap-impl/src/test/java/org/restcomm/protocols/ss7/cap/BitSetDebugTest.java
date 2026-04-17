package org.restcomm.protocols.ss7.cap;

import org.mobicents.protocols.asn.BitSetStrictLength;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;
import org.testng.annotations.Test;

public class BitSetDebugTest {
    @Test
    public void testSerialize() throws Exception {
        BitSetStrictLength bs = new BitSetStrictLength(4);
        bs.set(0);
        bs.set(1);
        bs.set(2);
        String xml = CAPJacksonXMLHelper.getXmlMapper().writeValueAsString(bs);
        System.out.println("BitSetStrictLength XML: " + xml);
    }
}
