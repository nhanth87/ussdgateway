
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TEID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "tEIDImpl")
public class TEIDImpl extends OctetStringBase implements TEID {
    public TEIDImpl() {
        super(4, 4, "TEID");
    }

    public TEIDImpl(byte[] data) {
        super(4, 4, "TEID", data);
    }

    public byte[] getData() {
        return data;
    }
}
