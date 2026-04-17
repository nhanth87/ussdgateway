
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "supportedCamelPhasesImpl")
public class SupportedCamelPhasesImpl extends BitStringBase implements SupportedCamelPhases {

    private static final int _INDEX_Phase1 = 0;
    private static final int _INDEX_Phase2 = 1;
    private static final int _INDEX_Phase3 = 2;
    private static final int _INDEX_Phase4 = 3;
    public SupportedCamelPhasesImpl() {
        super(1, 16, 4, "SupportedCamelPhases");
    }

    public SupportedCamelPhasesImpl(boolean phase1, boolean phase2, boolean phase3, boolean phase4) {
        super(1, 16, 4, "SupportedCamelPhases");

        this.setData(phase1, phase2, phase3, phase4);
    }

    protected void setData(boolean phase1, boolean phase2, boolean phase3, boolean phase4) {
        if (phase1)
            this.bitString.set(_INDEX_Phase1);
        if (phase2)
            this.bitString.set(_INDEX_Phase2);
        if (phase3)
            this.bitString.set(_INDEX_Phase3);
        if (phase4)
            this.bitString.set(_INDEX_Phase4);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase1Supported()
     */
    public boolean getPhase1Supported() {
        return this.bitString.get(_INDEX_Phase1);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase2Supported()
     */
    public boolean getPhase2Supported() {
        return this.bitString.get(_INDEX_Phase2);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase3Supported()
     */
    public boolean getPhase3Supported() {
        return this.bitString.get(_INDEX_Phase3);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberManagement. SupportedCamelPhases#getPhase4Supported()
     */
    public boolean getPhase4Supported() {
        return this.bitString.get(_INDEX_Phase4);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SupportedCamelPhases [");

        if (getPhase1Supported())
            sb.append("Phase1Supported, ");
        if (getPhase2Supported())
            sb.append("Phase2Supported, ");
        if (getPhase3Supported())
            sb.append("Phase3Supported, ");
        if (getPhase4Supported())
            sb.append("Phase4Supported, ");

        sb.append("]");

        return sb.toString();
    }
}
