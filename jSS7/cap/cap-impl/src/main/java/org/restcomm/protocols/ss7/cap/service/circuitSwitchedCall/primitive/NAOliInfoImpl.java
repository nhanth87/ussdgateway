package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.NAOliInfo;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringLength1Base;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "nAOliInfo")
public class NAOliInfoImpl extends OctetStringLength1Base implements NAOliInfo {

    private static final String VALUE = "value";

    public NAOliInfoImpl() {
        super("NAOliInfo");
    }

    public NAOliInfoImpl(int data) {
        super("NAOliInfo", data);
    }

    @Override
    public int getData() {
        return data;
    }
}

