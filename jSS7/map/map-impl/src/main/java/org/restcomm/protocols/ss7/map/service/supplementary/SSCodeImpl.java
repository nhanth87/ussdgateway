
package org.restcomm.protocols.ss7.map.service.supplementary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SupplementaryCodeValue;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
 *
 * @author sergey vetyutnev
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "sSCodeImpl")
public class SSCodeImpl extends OctetStringLength1Base implements SSCode {

    public SSCodeImpl() {
        super("SSCode");
    }

    public SSCodeImpl(int data) {
        super("SSCode", data);
    }

    public SSCodeImpl(SupplementaryCodeValue value) {
        super("SSCode", value != null ? value.getCode() : 0);
    }

    @Override
    public int getData() {
        return this.data;
    }

    @Override
    public SupplementaryCodeValue getSupplementaryCodeValue() {
        return SupplementaryCodeValue.getInstance(this.data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(_PrimitiveName);
        sb.append(" [");

        SupplementaryCodeValue scv = this.getSupplementaryCodeValue();
        if (scv != null) {
            sb.append("SupplementaryCodeValue=" + scv);
            sb.append(", ");
        }
        sb.append("Data=" + this.data);
        sb.append("]");

        return sb.toString();
    }

}
