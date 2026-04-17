
package org.restcomm.protocols.ss7.map.service.callhandling;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.ByteArrayContainer;
import org.restcomm.protocols.ss7.map.api.service.callhandling.CallReferenceNumber;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "callReferenceNumberImpl")
public class CallReferenceNumberImpl extends OctetStringBase implements CallReferenceNumber {
    public CallReferenceNumberImpl() {
        super(1, 8, "CallReferenceNumber");
    }

    public CallReferenceNumberImpl(byte[] data) {
        super(1, 8, "CallReferenceNumber", data);
    }

    public byte[] getData() {
        return data;
    }

}
