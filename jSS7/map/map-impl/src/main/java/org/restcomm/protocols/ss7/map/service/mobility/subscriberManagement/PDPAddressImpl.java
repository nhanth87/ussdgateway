
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDPAddress;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "pDPAddressImpl")
public class PDPAddressImpl extends OctetStringBase implements PDPAddress {
    public PDPAddressImpl() {
        super(1, 16, "PDPAddress");
    }

    public PDPAddressImpl(byte[] data) {
        super(1, 16, "PDPAddress", data);
    }

    public byte[] getData() {
        return data;
    }

}
