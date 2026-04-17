package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.VariablePartTime;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "variablePartTime")
public class VariablePartTimeImpl extends OctetStringBase implements VariablePartTime {

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    public VariablePartTimeImpl() {
        super(2, 2, "VariablePartDate");
    }

    public VariablePartTimeImpl(byte[] data) {
        super(2, 2, "VariablePartDate");

        this.data = data;
    }

    public VariablePartTimeImpl(int hour, int minute) {
        super(2, 2, "VariablePartDate");

        setParameters(hour, minute);
    }

    protected void setParameters(int hour, int minute) {
        this.data = new byte[2];

        this.data[0] = (byte) this.encodeByte(hour);
        this.data[1] = (byte) this.encodeByte(minute);
    }

    protected void setHour(int val) {
        if (this.data == null || this.data.length != 2)
            return;

        this.data[0] = (byte) this.encodeByte(val);
    }

    protected void setMinute(int val) {
        if (this.data == null || this.data.length != 2)
            return;

        this.data[1] = (byte) this.encodeByte(val);
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public int getHour() {
        if (this.data == null || this.data.length != 2)
            return 0;

        return this.decodeByte(data[0]);
    }

    @Override
    public int getMinute() {
        if (this.data == null || this.data.length != 2)
            return 0;

        return this.decodeByte(data[1]);
    }

    private int decodeByte(int bt) {
        return (bt & 0x0F) * 10 + ((bt & 0xF0) >> 4);
    }

    private int encodeByte(int val) {
        return (val / 10) | (val % 10) << 4;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.data != null && this.data.length == 2) {
            sb.append("hour=");
            sb.append(this.getHour());
            sb.append(", minute=");
            sb.append(this.getMinute());
        }

        sb.append("]");

        return sb.toString();
    }
}

