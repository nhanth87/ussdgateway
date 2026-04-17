package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


import org.restcomm.protocols.ss7.m3ua.parameter.LocalRKIdentifier;
import org.restcomm.protocols.ss7.m3ua.parameter.Parameter;
import org.restcomm.protocols.ss7.m3ua.parameter.RegistrationResult;
import org.restcomm.protocols.ss7.m3ua.parameter.RegistrationStatus;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;

@JacksonXmlRootElement(localName = "registrationResult")
public class RegistrationResultImpl extends ParameterImpl implements RegistrationResult {

    @JsonProperty("localRKId")
    private LocalRKIdentifier localRKId;
    @JsonProperty("status")
    private RegistrationStatus status;
    @JsonProperty("rc")
    private RoutingContext rc;

    private ByteBuf buf = Unpooled.buffer(24);
    private byte[] value;

    public RegistrationResultImpl(byte[] data) {
        this.tag = Parameter.Registration_Result;
        int pos = 0;

        while (pos < data.length) {
            short tag = (short) ((data[pos] & 0xff) << 8 | (data[pos + 1] & 0xff));
            short len = (short) ((data[pos + 2] & 0xff) << 8 | (data[pos + 3] & 0xff));

            byte[] value = new byte[len - 4];

            System.arraycopy(data, pos + 4, value, 0, value.length);
            pos += len;
            // parameters.put(tag, factory.createParameter(tag, value));
            switch (tag) {
                case ParameterImpl.Local_Routing_Key_Identifier:
                    this.localRKId = new LocalRKIdentifierImpl(value);
                    break;

                case ParameterImpl.Routing_Context:
                    this.rc = new RoutingContextImpl(value);
                    break;

                case ParameterImpl.Registration_Status:
                    this.status = new RegistrationStatusImpl(value);
                    break;

            }

            // The Parameter Length does not include any padding octets. We have to consider padding here
            pos += (pos % 4);
        }// end of while
    }

    public RegistrationResultImpl(LocalRKIdentifier localRKId, RegistrationStatus status, RoutingContext rc) {
        this.tag = Parameter.Registration_Result;
        this.localRKId = localRKId;
        this.status = status;
        this.rc = rc;

        this.encode();
    }

    private void encode() {
        ((LocalRKIdentifierImpl) this.localRKId).write(buf);

        ((RoutingContextImpl) rc).write(buf);

        ((RegistrationStatusImpl) this.status).write(buf);

        int length = buf.readableBytes();
        value = new byte[length];
        buf.getBytes(buf.readerIndex(), value);
    }

    @Override
    protected byte[] getValue() {
        return this.value;
    }

    public LocalRKIdentifier getLocalRKIdentifier() {
        return this.localRKId;
    }

    public RegistrationStatus getRegistrationStatus() {
        return this.status;
    }

    public RoutingContext getRoutingContext() {
        return this.rc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegistrationResult(");
        if (localRKId != null) {
            sb.append(localRKId.toString());
        }

        if (status != null) {
            sb.append(status.toString());
        }

        if (rc != null) {
            sb.append(rc.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
