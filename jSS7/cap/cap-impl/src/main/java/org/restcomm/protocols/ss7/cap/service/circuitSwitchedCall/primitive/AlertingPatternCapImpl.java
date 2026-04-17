package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.AlertingPatternCap;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.map.api.primitives.AlertingPattern;
import org.restcomm.protocols.ss7.map.primitives.AlertingPatternImpl;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "alertingPatternCap")
public class AlertingPatternCapImpl extends OctetStringBase implements AlertingPatternCap {

    private static final String ALERTING_PATTERN = "alertingPattern";

    public AlertingPatternCapImpl() {
        super(3, 3, "AlertingPatternCap");
    }

    public AlertingPatternCapImpl(byte[] data) {
        super(3, 3, "AlertingPatternCap", data);
    }

    public AlertingPatternCapImpl(AlertingPattern alertingPattern) {
        super(3, 3, "AlertingPatternCap");
        setAlertingPattern(alertingPattern);
    }

    public void setAlertingPattern(AlertingPattern alertingPattern) {

        if (alertingPattern == null)
            return;

        this.data = new byte[3];
        this.data[2] = (byte) alertingPattern.getData();
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public AlertingPattern getAlertingPattern() {

        if (this.data != null && this.data.length == 3)
            return new AlertingPatternImpl(this.data[2]);
        else
            return null;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        AlertingPattern ap = this.getAlertingPattern();
        if (ap != null) {
            sb.append("AlertingPattern=");
            sb.append(ap.toString());
        }
        sb.append("]");

        return sb.toString();
    }
}

