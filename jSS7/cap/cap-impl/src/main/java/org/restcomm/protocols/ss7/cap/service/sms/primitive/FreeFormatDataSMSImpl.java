package org.restcomm.protocols.ss7.cap.service.sms.primitive;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import org.restcomm.protocols.ss7.cap.api.service.sms.primitive.FreeFormatDataSMS;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.ByteArrayContainer;


/**
 *
 * @author Lasith Waruna Perera
 * @author alerant appngin
 *
 */
@JacksonXmlRootElement(localName = "freeFormatDataSMS")
public class FreeFormatDataSMSImpl extends OctetStringBase implements FreeFormatDataSMS {

    private static final String DATA = "data";

    public FreeFormatDataSMSImpl() {
        super(1, 160, "FreeFormatDataSMS");
    }

    public FreeFormatDataSMSImpl(byte[] data) {
        super(1, 160, "FreeFormatDataSMS", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}

