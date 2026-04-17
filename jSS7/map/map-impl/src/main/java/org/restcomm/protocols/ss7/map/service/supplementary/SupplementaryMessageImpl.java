
package org.restcomm.protocols.ss7.map.service.supplementary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.MessageImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SupplementaryMessage;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;

/**
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "supplementaryMessageImpl")
public abstract class SupplementaryMessageImpl extends MessageImpl implements SupplementaryMessage, MAPAsnPrimitive {

    private static final Logger logger = Logger.getLogger(SupplementaryMessageImpl.class);

    private static final byte DEFAULT_DATA_CODING_SCHEME = 0x0f;
    protected CBSDataCodingScheme ussdDataCodingSch;
    protected USSDString ussdString;

    /**
     *
     */
    public SupplementaryMessageImpl() {
        super();
    }

    public SupplementaryMessageImpl(CBSDataCodingScheme ussdDataCodingSch, USSDString ussdString) {
        this.ussdDataCodingSch = ussdDataCodingSch;
        this.ussdString = ussdString;
    }

    public MAPDialogSupplementary getMAPDialog() {
        return (MAPDialogSupplementary) super.getMAPDialog();
    }

    public CBSDataCodingScheme getDataCodingScheme() {
        return ussdDataCodingSch;
    }

    public void setDataCodingScheme(CBSDataCodingScheme ussdDataCodingSch) {
        this.ussdDataCodingSch = ussdDataCodingSch;
    }

    public USSDString getUSSDString() {
        return this.ussdString;
    }

    public void setUSSDString(USSDString ussdString) {
        this.ussdString = ussdString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(", ussdDataCodingSch=");
        sb.append(ussdDataCodingSch);
        if (ussdString != null) {
            sb.append(", ussdString=");
            try {
                sb.append(ussdString.getString(null));
            } catch (Exception e) {
            }
        }

        sb.append("]");

        return sb.toString();
    }

}
