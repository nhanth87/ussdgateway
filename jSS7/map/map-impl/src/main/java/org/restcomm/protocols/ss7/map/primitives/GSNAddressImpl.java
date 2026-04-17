
package org.restcomm.protocols.ss7.map.primitives;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddressAddressType;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "gsnAddress")
public class GSNAddressImpl extends OctetStringBase implements GSNAddress {

    public GSNAddressImpl() {
        super(5, 17, "GSNAddress");
    }

    public GSNAddressImpl(byte[] data) {
        super(5, 17, "GSNAddress", data);
    }

    public GSNAddressImpl(GSNAddressAddressType addressType, byte[] addressData) throws MAPException {
        super(5, 17, "GSNAddress", null);

        if (addressType == null)
            throw new MAPException("addressType argument must not be null");
        if (addressData == null)
            throw new MAPException("addressData argument must not be null");

        fillData(addressType, addressData);
    }

    private void fillData(GSNAddressAddressType addressType, byte[] addressData) throws MAPException {
        switch (addressType) {
        case IPv4:
            if (addressData.length != 4)
                throw new MAPException("addressData argument must have length=4 for IPv4");
            break;
        case IPv6:
            if (addressData.length != 16)
                throw new MAPException("addressData argument must have length=4 for IPv6");
            break;
        }

        this.data = new byte[addressData.length + 1];
        this.data[0] = (byte) addressType.createGSNAddressFirstByte();
        System.arraycopy(addressData, 0, this.data, 1, addressData.length);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public GSNAddressAddressType getGSNAddressAddressType() {
        if (data == null || data.length == 0)
            return null;
        int val = data[0] & 0xFF;
        return GSNAddressAddressType.getFromGSNAddressFirstByte(val);
    }

    @Override
    public byte[] getGSNAddressData() {
        GSNAddressAddressType type = getGSNAddressAddressType();
        if (type == null)
            return null;

        switch (type) {
        case IPv4:
            if (data.length >= 5) {
                byte[] res = new byte[4];
                System.arraycopy(this.data, 1, res, 0, 4);
                return res;
            }
            break;
        case IPv6:
            if (data.length >= 17) {
                byte[] res = new byte[16];
                System.arraycopy(this.data, 1, res, 0, 16);
                return res;
            }
            break;
        }

        return null;
    }

    @Override
    public String toString() {
        GSNAddressAddressType type = getGSNAddressAddressType();
        byte[] val = getGSNAddressData();

        if (type != null && val != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(_PrimitiveName);
            sb.append(" [");

            sb.append("type=");
            sb.append(type);
            sb.append(", data=[");
            sb.append(printDataArr(val));
            sb.append("]");

            sb.append("]");

            return sb.toString();
        } else {
            return super.toString();
        }
    }

    protected String printDataArr(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (arr != null) {
            for (byte b : arr) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(b & 0xFF);
            }
        }

        return sb.toString();
    }

}
