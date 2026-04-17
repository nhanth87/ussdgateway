package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.sccp.RemoteSubSystem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "remoteSubSystem")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSubSystemImpl implements RemoteSubSystem {

    @JacksonXmlProperty private int remoteSpc;
    @JacksonXmlProperty private int remoteSsn;
    @JacksonXmlProperty private int remoteSsnFlag;
    @JacksonXmlProperty private boolean markProhibitedWhenSpcResuming;

    @JacksonXmlProperty private boolean remoteSsnProhibited;

    public RemoteSubSystemImpl() {

    }

    public RemoteSubSystemImpl(int remoteSpc, int remoteSsn, int remoteSsnFlag, boolean markProhibitedWhenSpcResuming) {
        this.remoteSpc = remoteSpc;
        this.remoteSsn = remoteSsn;
        this.remoteSsnFlag = remoteSsnFlag;
        this.markProhibitedWhenSpcResuming = markProhibitedWhenSpcResuming;
    }

    public boolean isRemoteSsnProhibited() {
        return this.remoteSsnProhibited;
    }

    public void setRemoteSsnProhibited(boolean remoteSsnProhibited) {
        this.remoteSsnProhibited = remoteSsnProhibited;
    }

    public int getRemoteSpc() {
        return this.remoteSpc;
    }

    /**
     * @param remoteSpc the remoteSpc to set
     */
    protected void setRemoteSpc(int remoteSpc) {
        this.remoteSpc = remoteSpc;
    }

    /**
     * @param remoteSsn the remoteSsn to set
     */
    protected void setRemoteSsn(int remoteSsn) {
        this.remoteSsn = remoteSsn;
    }

    /**
     * @param remoteSsnFlag the remoteSsnFlag to set
     */
    protected void setRemoteSsnFlag(int remoteSsnFlag) {
        this.remoteSsnFlag = remoteSsnFlag;
    }

    /**
     * @param markProhibitedWhenSpcResuming the markProhibitedWhenSpcResuming to set
     */
    protected void setMarkProhibitedWhenSpcResuming(boolean markProhibitedWhenSpcResuming) {
        this.markProhibitedWhenSpcResuming = markProhibitedWhenSpcResuming;
    }

    public int getRemoteSsn() {
        return this.remoteSsn;
    }

    public int getRemoteSsnFlag() {
        return this.remoteSsnFlag;
    }

    public boolean getMarkProhibitedWhenSpcResuming() {
        return markProhibitedWhenSpcResuming;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("rsp=").append(this.remoteSpc).append(" rss=").append(this.remoteSsn).append(" rss-flag=")
                .append(this.remoteSsnFlag).append(" rss-prohibited=").append(this.remoteSsnProhibited);
        if (this.markProhibitedWhenSpcResuming)
            sb.append(" markProhibitedWhenSpcResuming");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + remoteSpc;
        result = prime * result + remoteSsn;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteSubSystemImpl other = (RemoteSubSystemImpl) obj;
        if (remoteSpc != other.remoteSpc)
            return false;
        if (remoteSsn != other.remoteSsn)
            return false;
        return true;
    }
}
