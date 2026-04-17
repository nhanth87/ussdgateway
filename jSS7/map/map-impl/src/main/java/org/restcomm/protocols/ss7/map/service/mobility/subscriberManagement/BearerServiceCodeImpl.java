
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
 *
 * @author sergey vetyutnev
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "bearerServiceCodeImpl")
public class BearerServiceCodeImpl extends OctetStringLength1Base implements BearerServiceCode {

    public BearerServiceCodeImpl() {
        super("BearerServiceCode");
    }

    public BearerServiceCodeImpl(int data) {
        super("BearerServiceCode", data);
    }

    public BearerServiceCodeImpl(BearerServiceCodeValue value) {
        // super("BearerServiceCode", value != null ? value.getBearerServiceCode() : 0);
        super("BearerServiceCode", value != null ? value.getCode() : 0);
    }

    public int getData() {
        return data;
    }

    public BearerServiceCodeValue getBearerServiceCodeValue() {
        return BearerServiceCodeValue.getInstance(this.data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._PrimitiveName);
        sb.append(" [");

        sb.append("Value=");
        sb.append(this.getBearerServiceCodeValue());

        sb.append(", Data=");
        sb.append(this.data);

        sb.append("]");

        return sb.toString();
    }

}
