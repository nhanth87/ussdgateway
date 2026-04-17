package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.LowLayerCompatibility;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;


/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "lowLayerCompatibility")
public class LowLayerCompatibilityImpl extends OctetStringBase implements LowLayerCompatibility {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public LowLayerCompatibilityImpl() {
        super(1, 16, "LowLayerCompatibility");
    }

    public LowLayerCompatibilityImpl(byte[] data) {
        super(1, 16, "LowLayerCompatibility", data);
    }

    public byte[] getData() {
        return data;
    }
}

