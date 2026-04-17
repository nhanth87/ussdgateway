package org.restcomm.protocols.ss7.cap.EsiBcsm;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ChargeIndicator;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ChargeIndicatorValue;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringLength1Base;

/**
*
* @author sergey vetyutnev
*
*/

@JacksonXmlRootElement(localName = "chargeIndicator")
public class ChargeIndicatorImpl extends OctetStringLength1Base implements ChargeIndicator {

    private static final String DEFAULT_VALUE = "";

    public ChargeIndicatorImpl() {
        super("ChargeIndicator");
    }

    public ChargeIndicatorImpl(int data) {
        super("ChargeIndicator", data);
    }

    public ChargeIndicatorImpl(ChargeIndicatorValue value) {
        super("ChargeIndicator");

        if (value != null)
            this.data = value.getCode();
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public ChargeIndicatorValue getChargeIndicatorValue() {
        return ChargeIndicatorValue.getInstance(data);
    }

    public String getValue() {
        ChargeIndicatorValue value = getChargeIndicatorValue();
        return value != null ? value.toString() : DEFAULT_VALUE;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        ChargeIndicatorValue value = this.getChargeIndicatorValue();
        if (value != null) {
            sb.append("chargeIndicatorValue=");
            sb.append(value);
        }

        sb.append("]");

        return sb.toString();
    }

}

