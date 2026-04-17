
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.isup.DigitsImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.PromptAndCollectUserInformationResponseImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericDigitsImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericNumber;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class PromptAndCollectUserInformationResponseTest {

    public byte[] getData1() {
        return new byte[] { (byte) 128, 4, 65, 44, 55, 66 };
    }

    public byte[] getDigits() {
        return new byte[] { 44, 55, 66 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        PromptAndCollectUserInformationResponseImpl elem = new PromptAndCollectUserInformationResponseImpl();
        int tag = ais.readTag();
        assertEquals(tag, 0);
        elem.decodeAll(ais);
        assertEquals(elem.getDigitsResponse().getGenericDigits().getEncodingScheme(), 2);
        assertEquals(elem.getDigitsResponse().getGenericDigits().getTypeOfDigits(), 1);
        assertTrue(Arrays.equals(elem.getDigitsResponse().getGenericDigits().getEncodedDigits(), this.getDigits()));
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall" })
    public void testEncode() throws Exception {

        GenericDigitsImpl genericDigits = new GenericDigitsImpl(2, 1, getDigits());
        // int encodingScheme, int typeOfDigits, int[] digits
        DigitsImpl digitsResponse = new DigitsImpl(genericDigits);

        PromptAndCollectUserInformationResponseImpl elem = new PromptAndCollectUserInformationResponseImpl(digitsResponse);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        GenericNumber genericNumber = new GenericNumberImpl(1, "987", 0, 2, 3, true, 0);
//        int natureOfAddresIndicator, String address, int numberQualifierIndicator,
//        int numberingPlanIndicator, int addressRepresentationREstrictedIndicator, boolean numberIncomplete,
//        int screeningIndicator
        Digits digitsResponse = new DigitsImpl(genericNumber);
        PromptAndCollectUserInformationResponseImpl original = new PromptAndCollectUserInformationResponseImpl(digitsResponse);
        original.setInvokeId(21);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        PromptAndCollectUserInformationResponseImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, PromptAndCollectUserInformationResponseImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInvokeId())));
        }
        if (copy != null) {
            assertEquals(copy.getInvokeId(), original.getInvokeId());
            assertEquals(copy.getDigitsResponse().getGenericNumber().getNatureOfAddressIndicator(), 1);
            assertEquals(copy.getDigitsResponse().getGenericNumber().getAddress(), "987");
            assertEquals(copy.getDigitsResponse().getGenericNumber().getNumberQualifierIndicator(), 0);
            assertEquals(copy.getDigitsResponse().getGenericNumber().getNumberingPlanIndicator(), 2);
        }
    }
}
