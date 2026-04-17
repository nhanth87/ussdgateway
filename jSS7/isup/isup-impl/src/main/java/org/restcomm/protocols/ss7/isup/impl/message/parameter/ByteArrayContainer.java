
package org.restcomm.protocols.ss7.isup.impl.message.parameter;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ByteArrayContainer {

    private byte[] data;

    public ByteArrayContainer() {
    }

    public ByteArrayContainer(byte[] val) {
        this.data = val;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] val) {
        data = val;
    }

}
