
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "extBearerServiceCodeImpl")
public class ExtBearerServiceCodeImpl extends OctetStringBase implements ExtBearerServiceCode {
    public ExtBearerServiceCodeImpl() {
        super(1, 5, "ExtBearerServiceCode");
    }

    public ExtBearerServiceCodeImpl(byte[] data) {
        super(1, 5, "ExtBearerServiceCode", data);
    }

    public ExtBearerServiceCodeImpl(BearerServiceCodeValue value) {
        super(1, 5, "ExtBearerServiceCode");
        setBearerServiceCode(value);
    }

    public void setBearerServiceCode(BearerServiceCodeValue value) {
//        if (value != null)
//            this.data = new byte[] { (byte) (value.getBearerServiceCode()) };

        if (value != null)
            this.data = new byte[] { (byte) (value.getCode()) };
    }

    public byte[] getData() {
        return data;
    }

    public BearerServiceCodeValue getBearerServiceCodeValue() {
        if (data == null || data.length < 1)
            return null;
        else
            return BearerServiceCodeValue.getInstance(this.data[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._PrimitiveName);
        sb.append(" [");

        sb.append("Value=");
        sb.append(this.getBearerServiceCodeValue());

        sb.append(", Data=[");
        if (data != null) {
            for (int i1 : data) {
                sb.append(i1);
                sb.append(", ");
            }
        }
        sb.append("]");

        sb.append("]");

        return sb.toString();
    }

}
