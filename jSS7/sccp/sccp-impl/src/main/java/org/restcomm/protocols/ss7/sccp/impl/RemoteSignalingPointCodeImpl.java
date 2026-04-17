package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.sccp.RemoteSignalingPointCode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "remoteSignalingPointCode")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSignalingPointCodeImpl implements RemoteSignalingPointCode {

    @JacksonXmlProperty private int remoteSpc;
    @JacksonXmlProperty private int remoteSpcFlag;
    @JacksonXmlProperty private int mask;
    @JacksonXmlProperty protected boolean remoteSpcProhibited;
    @JacksonXmlProperty protected boolean remoteSccpProhibited;

    @JacksonXmlProperty protected int rl;
    @JacksonXmlProperty protected int rsl;

    private transient RemoteSignalingPointCodeExt remoteSignalingPointCodeExt;

    public RemoteSignalingPointCodeImpl() {
    }

    public RemoteSignalingPointCodeImpl(int remoteSpc, int remoteSpcFlag, int mask, boolean isProhibited) {
        this.remoteSpc = remoteSpc;
        this.remoteSpcFlag = remoteSpcFlag;
        this.mask = mask;
        this.remoteSccpProhibited = isProhibited;
        this.remoteSpcProhibited = isProhibited;
    }

    public void createRemoteSignalingPointCodeExt(Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface) {
        remoteSignalingPointCodeExt = ss7ExtSccpDetailedInterface.createRemoteSignalingPointCodeExt(this);
    }

    public RemoteSignalingPointCodeExt getRemoteSignalingPointCodeExt() {
        return remoteSignalingPointCodeExt;
    }

    public int getRemoteSpc() {
        return this.remoteSpc;
    }

    public int getRemoteSpcFlag() {
        return this.remoteSpcFlag;
    }

    public int getMask() {
        return this.mask;
    }

    public boolean isRemoteSpcProhibited() {
        return this.remoteSpcProhibited;
    }

    public boolean isRemoteSccpProhibited() {
        return this.remoteSccpProhibited;
    }

    protected void setProhibitedState(boolean remoteSpcProhibited, boolean remoteSccpProhibited) {
        this.remoteSpcProhibited = remoteSpcProhibited;
        this.remoteSccpProhibited = remoteSccpProhibited;
    }

    protected void setRemoteSpcProhibited(boolean remoteSpcProhibited) {
        this.remoteSpcProhibited = remoteSpcProhibited;
    }

    protected void setRemoteSccpProhibited(boolean remoteSccpProhibited) {
        this.remoteSccpProhibited = remoteSccpProhibited;
    }

    /**
     * @param remoteSpc the remoteSpc to set
     */
    protected void setRemoteSpc(int remoteSpc) {
        this.remoteSpc = remoteSpc;
    }

    /**
     * @param remoteSpcFlag the remoteSpcFlag to set
     */
    protected void setRemoteSpcFlag(int remoteSpcFlag) {
        this.remoteSpcFlag = remoteSpcFlag;
    }

    /**
     * @param mask the mask to set
     */
    protected void setMask(int mask) {
        this.mask = mask;
    }

    @Override
    public int getCurrentRestrictionLevel() {
        return rl;
    }

    public int getCurrentRestrictionSubLevel() {
        return rsl;
    }

    void clearCongLevel() {
        remoteSignalingPointCodeExt.clearCongLevel();
    }

    void increaseCongLevel(int level) {
        remoteSignalingPointCodeExt.increaseCongLevel(level);
    }

    /**
     * Do not use this method directly except of debugging. Use clearCongLevel(), increaseCongLevel(), decreaseCongLevel()
     *
     * @param value
     */
    public void setCurrentRestrictionLevel(int value) {
        this.rl = value;
        this.rsl = 0;
    }

    public void setRl(int val) {
        rl = val;
    }

    public void setRsl(int val) {
        rsl = val;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("rsp=").append(this.remoteSpc).append(" rsp-flag=").append(this.remoteSpcFlag).append(" mask=")
                .append(this.mask).append(" rsp-prohibited=").append(this.remoteSpcProhibited).append(" rsccp-prohibited=")
                .append(this.remoteSccpProhibited).append(" rl=").append(rl).append(" rsl=").append(rsl);
        return sb.toString();
    }
}
