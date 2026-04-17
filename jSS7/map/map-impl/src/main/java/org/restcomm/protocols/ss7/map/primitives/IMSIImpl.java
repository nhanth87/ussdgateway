
package org.restcomm.protocols.ss7.map.primitives;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "imsi")
public class IMSIImpl extends TbcdString implements IMSI {

    public IMSIImpl() {
        super(3, 8, "IMSI");
    }

    public IMSIImpl(String data) {
        super(3, 8, "IMSI", data);
    }

    public String getData() {
        return this.data;
    }

}
