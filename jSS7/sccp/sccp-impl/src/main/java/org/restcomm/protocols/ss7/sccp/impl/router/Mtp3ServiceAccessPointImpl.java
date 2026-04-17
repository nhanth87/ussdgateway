package org.restcomm.protocols.ss7.sccp.impl.router;

import java.util.HashMap;
import java.util.Map;

import org.restcomm.protocols.ss7.sccp.Mtp3Destination;
import org.restcomm.protocols.ss7.sccp.Mtp3ServiceAccessPoint;
import org.restcomm.protocols.ss7.sccp.impl.oam.SccpOAMMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
@JacksonXmlRootElement(localName = "mtp3ServiceAccessPoint")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mtp3ServiceAccessPointImpl implements Mtp3ServiceAccessPoint {

    @JacksonXmlProperty private int mtp3Id;
    @JacksonXmlProperty private int opc;
    @JacksonXmlProperty private int ni;
    @JacksonXmlProperty private String stackName;
    @JacksonXmlProperty private int networkId;
    @JacksonXmlProperty private String localGtDigits;

    @JacksonXmlProperty private Mtp3DestinationMap<Integer, Mtp3Destination> dpcList = new Mtp3DestinationMap<Integer, Mtp3Destination>();

    public Mtp3ServiceAccessPointImpl() {
    }

    public Mtp3ServiceAccessPointImpl(int mtp3Id, int opc, int ni, String stackName, int networkId, String localGtDigits) {
        this.mtp3Id = mtp3Id;
        this.opc = opc;
        this.ni = ni;
        this.stackName = stackName;
        this.networkId = networkId;
        this.localGtDigits = localGtDigits;
    }

    /**
     * @param stackName the stackName to set
     */
    protected void setStackName(String stackName) {
        this.stackName = stackName;
    }

    public int getMtp3Id() {
        return this.mtp3Id;
    }

    public int getOpc() {
        return this.opc;
    }

    public int getNi() {
        return this.ni;
    }

    public int getNetworkId() {
        return this.networkId;
    }

    @Override
    public String getLocalGtDigits() {
        return this.localGtDigits;
    }

    public void setLocalGtDigits(String val) {
        this.localGtDigits = val;
    }

    public Mtp3Destination getMtp3Destination(int destId) {
        return this.dpcList.get(destId);
    }

    public Map<Integer, Mtp3Destination> getMtp3Destinations() {
        Map<Integer, Mtp3Destination> dpcListTmp = new HashMap<Integer, Mtp3Destination>();
        dpcListTmp.putAll(dpcList);
        return dpcListTmp;
    }

    public void addMtp3Destination(int destId, int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask)
            throws Exception {

        if (getMtp3Destination(destId) != null) {
            throw new Exception(SccpOAMMessage.DEST_ALREADY_EXIST);
        }

        Mtp3DestinationImpl dest = new Mtp3DestinationImpl(firstDpc, lastDpc, firstSls, lastSls, slsMask);

        synchronized (this) {
            Mtp3DestinationMap<Integer, Mtp3Destination> newDpcList = new Mtp3DestinationMap<Integer, Mtp3Destination>();
            newDpcList.putAll(this.dpcList);
            newDpcList.put(destId, dest);
            this.dpcList = newDpcList;
        }
    }

    public void modifyMtp3Destination(int destId, int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask)
            throws Exception {
        if (getMtp3Destination(destId) == null) {
            throw new Exception(String.format(SccpOAMMessage.DEST_DOESNT_EXIST, this.stackName));
        }

        Mtp3DestinationImpl dest = new Mtp3DestinationImpl(firstDpc, lastDpc, firstSls, lastSls, slsMask);

        synchronized (this) {
            Mtp3DestinationMap<Integer, Mtp3Destination> newDpcList = new Mtp3DestinationMap<Integer, Mtp3Destination>();
            newDpcList.putAll(this.dpcList);
            newDpcList.put(destId, dest);
            this.dpcList = newDpcList;
        }
    }

    public void removeMtp3Destination(int destId) throws Exception {
        if (getMtp3Destination(destId) == null) {
            throw new Exception(String.format(SccpOAMMessage.DEST_DOESNT_EXIST, this.stackName));
        }

        synchronized (this) {
            Mtp3DestinationMap<Integer, Mtp3Destination> newDpcList = new Mtp3DestinationMap<Integer, Mtp3Destination>();
            newDpcList.putAll(this.dpcList);
            newDpcList.remove(destId);
            this.dpcList = newDpcList;
        }
    }

    public boolean matches(int dpc, int sls) {
        for (Map.Entry<Integer, Mtp3Destination> e : this.dpcList.entrySet()) {
            if (e.getValue().match(dpc, sls))
                return true;
        }
        return false;
    }

    public boolean matches(int dpc) {
        for (Map.Entry<Integer, Mtp3Destination> e : this.dpcList.entrySet()) {
            if (e.getValue().match(dpc))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("mtp3Id=").append(this.mtp3Id).append(", opc=").append(this.opc).append(", ni=").append(this.ni)
                .append(", networkId=").append(this.networkId).append(", localGtDigits=").append(this.localGtDigits)
                .append(", dpcList=[");

        boolean isFirst = true;
        for (Map.Entry<Integer, Mtp3Destination> e : this.dpcList.entrySet()) {
            Integer id = e.getKey();
            Mtp3Destination dest = e.getValue();
            if (isFirst)
                isFirst = false;
            else
                sb.append(", ");
            sb.append("[key=");
            sb.append(id);
            sb.append(", ");
            sb.append(dest.toString());
            sb.append("], ");
        }
        sb.append("]");

        return sb.toString();
    }
}
