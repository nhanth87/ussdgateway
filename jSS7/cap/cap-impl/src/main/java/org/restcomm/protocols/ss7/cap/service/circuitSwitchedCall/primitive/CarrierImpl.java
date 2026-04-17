package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;


/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "carrier")
public class CarrierImpl extends OctetStringBase implements Carrier {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public CarrierImpl() {
        super(4, 4, "Carrier");
    }

    public CarrierImpl(byte[] data) {
        super(4, 4, "Carrier", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}

