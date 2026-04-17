package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;



import org.restcomm.protocols.ss7.m3ua.parameter.DestinationPointCode;
import org.restcomm.protocols.ss7.m3ua.parameter.Parameter;

/**
 *
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "destinationPointCode")
public class DestinationPointCodeImpl extends ParameterImpl implements DestinationPointCode {

    private static final String DPC = "dpc";
    private static final String MASK = "mask";

    @JsonProperty("destPC")
    private int destPC = 0;
    @JsonProperty("mask")
    private short mask = 0;
    private byte[] value;

    public DestinationPointCodeImpl() {
        this.tag = Parameter.Destination_Point_Code;
    }

    protected DestinationPointCodeImpl(byte[] value) {
        this.tag = Parameter.Destination_Point_Code;
        this.value = value;
        this.mask = value[0];

        destPC = 0;
        destPC |= value[1] & 0xFF;
        destPC <<= 8;
        destPC |= value[2] & 0xFF;
        destPC <<= 8;
        destPC |= value[3] & 0xFF;
    }

    protected DestinationPointCodeImpl(int pc, short mask) {
        this.tag = Parameter.Destination_Point_Code;
        this.destPC = pc;
        this.mask = mask;
        encode();
    }

    private void encode() {
        // create byte array taking into account data, point codes and
        // indicators;
        this.value = new byte[4];
        // encode point code with mask
        value[0] = (byte) this.mask;// Mask

        value[1] = (byte) (destPC >> 16);
        value[2] = (byte) (destPC >> 8);
        value[3] = (byte) (destPC);
    }

    public int getPointCode() {
        return destPC;
    }

    @Override
    protected byte[] getValue() {
        return value;
    }

    public short getMask() {
        return this.mask;
    }

    @Override
    public String toString() {
        return String.format("DestinationPointCode dpc=%d mask=%d", destPC, mask);
    }

}
