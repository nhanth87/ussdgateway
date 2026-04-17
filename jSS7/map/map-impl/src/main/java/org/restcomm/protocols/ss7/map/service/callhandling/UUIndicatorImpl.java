
package org.restcomm.protocols.ss7.map.service.callhandling;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.callhandling.UUIndicator;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "uUIndicatorImpl")
public class UUIndicatorImpl extends OctetStringLength1Base implements UUIndicator {
    public UUIndicatorImpl() {
        super("UUIndicator");
    }

    public UUIndicatorImpl(int data) {
        super("UUIndicator", data);
    }

    public int getData() {
        return data;
    }

}
