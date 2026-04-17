
package org.restcomm.protocols.ss7.map.service.callhandling;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.callhandling.UUI;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "uUIImpl")
public class UUIImpl extends OctetStringBase implements UUI {
    public UUIImpl() {
        super(1, 131, "UUI");
    }

    public UUIImpl(byte[] data) {
        super(1, 131, "UUI", data);
    }

    public byte[] getData() {
        return data;
    }

}
