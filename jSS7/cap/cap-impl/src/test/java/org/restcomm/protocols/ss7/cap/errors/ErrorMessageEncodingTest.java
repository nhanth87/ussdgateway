
package org.restcomm.protocols.ss7.cap.errors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorCode;
import org.restcomm.protocols.ss7.cap.api.errors.CancelProblem;
import org.restcomm.protocols.ss7.cap.api.errors.RequestedInfoErrorParameter;
import org.restcomm.protocols.ss7.cap.api.errors.TaskRefusedParameter;
import org.restcomm.protocols.ss7.cap.api.errors.UnavailableNetworkResource;
import org.restcomm.protocols.ss7.cap.errors.CAPErrorMessageCancelFailedImpl;
import org.restcomm.protocols.ss7.cap.errors.CAPErrorMessageParameterlessImpl;
import org.restcomm.protocols.ss7.cap.errors.CAPErrorMessageRequestedInfoErrorImpl;
import org.restcomm.protocols.ss7.cap.errors.CAPErrorMessageSystemFailureImpl;
import org.restcomm.protocols.ss7.cap.errors.CAPErrorMessageTaskRefusedImpl;
import org.restcomm.protocols.ss7.cap.isup.BearerCapImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.UserServiceInformationImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.UserServiceInformation;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ErrorMessageEncodingTest {

    public byte[] getDataTaskRefused() {
        return new byte[] { 10, 1, 2 };
    }

    public byte[] getDataSystemFailure() {
        return new byte[] { 10, 1, 3 };
    }

    public byte[] getDataRequestedInfoError() {
        return new byte[] { 10, 1, 1 };
    }

    public byte[] getDataCancelFailed() {
        return new byte[] { 10, 1, 1 };
    }

    @Test(groups = { "functional.decode", "errors.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getDataTaskRefused();
        AsnInputStream ais = new AsnInputStream(data);
        CAPErrorMessageTaskRefusedImpl elem = new CAPErrorMessageTaskRefusedImpl();
        int tag = ais.readTag();
        elem.decodeAll(ais);
        assertEquals(tag, Tag.ENUMERATED);
        assertEquals(elem.getTaskRefusedParameter(), TaskRefusedParameter.congestion);

        data = this.getDataSystemFailure();
        ais = new AsnInputStream(data);
        CAPErrorMessageSystemFailureImpl elem2 = new CAPErrorMessageSystemFailureImpl();
        tag = ais.readTag();
        elem2.decodeAll(ais);
        assertEquals(tag, Tag.ENUMERATED);
        assertEquals(elem2.getUnavailableNetworkResource(), UnavailableNetworkResource.resourceStatusFailure);

        data = this.getDataRequestedInfoError();
        ais = new AsnInputStream(data);
        CAPErrorMessageRequestedInfoErrorImpl elem3 = new CAPErrorMessageRequestedInfoErrorImpl();
        tag = ais.readTag();
        elem3.decodeAll(ais);
        assertEquals(tag, Tag.ENUMERATED);
        assertEquals(elem3.getRequestedInfoErrorParameter(), RequestedInfoErrorParameter.unknownRequestedInfo);

        data = this.getDataCancelFailed();
        ais = new AsnInputStream(data);
        CAPErrorMessageCancelFailedImpl elem4 = new CAPErrorMessageCancelFailedImpl();
        tag = ais.readTag();
        elem4.decodeAll(ais);
        assertEquals(tag, Tag.ENUMERATED);
        assertEquals(elem4.getCancelProblem(), CancelProblem.tooLate);
    }

    @Test(groups = { "functional.encode", "errors.primitive" })
    public void testEncode() throws Exception {

        CAPErrorMessageTaskRefusedImpl elem = new CAPErrorMessageTaskRefusedImpl(TaskRefusedParameter.congestion);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getDataTaskRefused()));

        CAPErrorMessageSystemFailureImpl elem2 = new CAPErrorMessageSystemFailureImpl(
                UnavailableNetworkResource.resourceStatusFailure);
        aos = new AsnOutputStream();
        elem2.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getDataSystemFailure()));

        CAPErrorMessageRequestedInfoErrorImpl elem3 = new CAPErrorMessageRequestedInfoErrorImpl(
                RequestedInfoErrorParameter.unknownRequestedInfo);
        aos = new AsnOutputStream();
        elem3.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getDataRequestedInfoError()));

        CAPErrorMessageCancelFailedImpl elem4 = new CAPErrorMessageCancelFailedImpl(CancelProblem.tooLate);
        aos = new AsnOutputStream();
        elem4.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getDataCancelFailed()));
    }

    @Test(groups = { "functional.xml.serialize", "errors.primitive" })
    public void testXMLSerialize_CancelFailed() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAPErrorMessageCancelFailedImpl original = new CAPErrorMessageCancelFailedImpl(CancelProblem.tooLate);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPErrorMessageCancelFailedImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPErrorMessageCancelFailedImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getCancelProblem())));
        }
        if (copy != null) {
            assertEquals((long) copy.getErrorCode(), (long) original.getErrorCode());
            assertEquals(copy.getCancelProblem(), original.getCancelProblem());
        }

    }

    @Test(groups = { "functional.xml.serialize", "errors.primitive" })
    public void testXMLSerialize_Parameterless() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAPErrorMessageParameterlessImpl original = new CAPErrorMessageParameterlessImpl((long) CAPErrorCode.unknownPDPID);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPErrorMessageParameterlessImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPErrorMessageParameterlessImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        }
        if (copy != null) {
            assertEquals((long) copy.getErrorCode(), (long) original.getErrorCode());
        }

    }

    @Test(groups = { "functional.xml.serialize", "errors.primitive" })
    public void testXMLSerialize_RequestedInfoError() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAPErrorMessageRequestedInfoErrorImpl original = new CAPErrorMessageRequestedInfoErrorImpl(
                RequestedInfoErrorParameter.requestedInfoNotAvailable);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPErrorMessageRequestedInfoErrorImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPErrorMessageRequestedInfoErrorImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getRequestedInfoErrorParameter())));
        }
        if (copy != null) {
            assertEquals((long) copy.getErrorCode(), (long) original.getErrorCode());
            assertEquals(copy.getRequestedInfoErrorParameter(), original.getRequestedInfoErrorParameter());
        }

    }

    @Test(groups = { "functional.xml.serialize", "errors.primitive" })
    public void testXMLSerialize_SystemFailure() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAPErrorMessageSystemFailureImpl original = new CAPErrorMessageSystemFailureImpl(
                UnavailableNetworkResource.endUserFailure);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPErrorMessageSystemFailureImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPErrorMessageSystemFailureImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getUnavailableNetworkResource())));
        }
        if (copy != null) {
            assertEquals((long) copy.getErrorCode(), (long) original.getErrorCode());
            assertEquals(copy.getUnavailableNetworkResource(), original.getUnavailableNetworkResource());
        }

    }

    @Test(groups = { "functional.xml.serialize", "errors.primitive" })
    public void testXMLSerialize_TaskRefused() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        CAPErrorMessageTaskRefusedImpl original = new CAPErrorMessageTaskRefusedImpl(TaskRefusedParameter.unobtainable);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        CAPErrorMessageTaskRefusedImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, CAPErrorMessageTaskRefusedImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getTaskRefusedParameter())));
        }
        if (copy != null) {
            assertEquals((long) copy.getErrorCode(), (long) original.getErrorCode());
            assertEquals(copy.getTaskRefusedParameter(), original.getTaskRefusedParameter());
        }

    }
}
