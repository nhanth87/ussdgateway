
package org.restcomm.protocols.ss7.sccp.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jctools.maps.NonBlockingHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPart;
import org.restcomm.protocols.ss7.sccp.MaxConnectionCountReached;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.SccpConnection;
import org.restcomm.protocols.ss7.sccp.SccpListener;
import org.restcomm.protocols.ss7.sccp.SccpManagementEventListener;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.sccp.SccpStack;
import org.restcomm.protocols.ss7.sccp.impl.message.MessageFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.message.SccpDataMessageImpl;
import org.restcomm.protocols.ss7.sccp.impl.message.SccpNoticeMessageImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.LocalReferenceImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.message.MessageFactory;
import org.restcomm.protocols.ss7.sccp.message.SccpDataMessage;
import org.restcomm.protocols.ss7.sccp.message.SccpNoticeMessage;
import org.restcomm.protocols.ss7.sccp.parameter.LocalReference;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.sccp.parameter.ProtocolClass;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.ss7.congestion.ExecutorCongestionMonitor;

import java.util.Map;

/**
 *
 * @author Oleg Kulikov
 * @author baranowb
 * @author sergey vetyutnev
 *
 */
public class SccpProviderImpl implements SccpProvider, Serializable {

    private static final Logger logger = Logger.getLogger(SccpProviderImpl.class);

    private transient SccpStackImpl stack;
    protected NonBlockingHashMap<Integer, SccpListener> ssnToListener = new NonBlockingHashMap<Integer, SccpListener>();
    protected CopyOnWriteArrayList<SccpManagementEventListener> managementEventListeners = new CopyOnWriteArrayList<SccpManagementEventListener>();

    private MessageFactoryImpl messageFactory;
    private ParameterFactoryImpl parameterFactory;

    //<ssn - congestion level>
    private ConcurrentHashMap<Integer, Integer> congestionSsn = new ConcurrentHashMap<Integer, Integer>();

    SccpProviderImpl(SccpStackImpl stack) {
        this.stack = stack;
        this.messageFactory = stack.messageFactory;
        this.parameterFactory = new ParameterFactoryImpl();
    }

    public CopyOnWriteArrayList<SccpManagementEventListener> getManagementEventListeners() {
        return this.managementEventListeners;
    }

    public MessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    public ParameterFactory getParameterFactory() {
        return this.parameterFactory;
    }

    public void registerSccpListener(int ssn, SccpListener listener) {
        synchronized (this) {
            SccpListener existingListener = ssnToListener.get(ssn);
            if (existingListener != null && existingListener != listener) {
                if (logger.isEnabledFor(Level.WARN)) {
                    logger.warn(String.format("Registering SccpListener=%s for already existing SccpListener=%s for SSN=%d",
                            listener, existingListener, ssn));
                }
            }
            NonBlockingHashMap<Integer, SccpListener> newListener = new NonBlockingHashMap<Integer, SccpListener>();
            newListener.putAll(ssnToListener);
            newListener.put(ssn, listener);
            ssnToListener = newListener;

            this.stack.broadcastChangedSsnState(ssn, true);
        }
    }

    public void deregisterSccpListener(int ssn) {
        synchronized (this) {
            NonBlockingHashMap<Integer, SccpListener> newListener = new NonBlockingHashMap<Integer, SccpListener>();
            newListener.putAll(ssnToListener);
            SccpListener existingListener = newListener.remove(ssn);
            if (existingListener == null) {
                if (logger.isEnabledFor(Level.WARN)) {
                    logger.warn(String.format("No existing SccpListener=%s for SSN=%d", existingListener, ssn));
                }
            }
            ssnToListener = newListener;

            this.stack.broadcastChangedSsnState(ssn, false);
        }
    }

    public void registerManagementEventListener(SccpManagementEventListener listener) {
        synchronized (this) {
            if (this.managementEventListeners.contains(listener))
                return;

            CopyOnWriteArrayList<SccpManagementEventListener> newManagementEventListeners = new CopyOnWriteArrayList<SccpManagementEventListener>();
            newManagementEventListeners.addAll(this.managementEventListeners);
            newManagementEventListeners.add(listener);
            this.managementEventListeners = newManagementEventListeners;
        }
    }

    public void deregisterManagementEventListener(SccpManagementEventListener listener) {
        synchronized (this) {
            if (!this.managementEventListeners.contains(listener))
                return;

            CopyOnWriteArrayList<SccpManagementEventListener> newManagementEventListeners = new CopyOnWriteArrayList<SccpManagementEventListener>();
            newManagementEventListeners.addAll(this.managementEventListeners);
            newManagementEventListeners.remove(listener);
            this.managementEventListeners = newManagementEventListeners;
        }
    }

    public SccpListener getSccpListener(int ssn) {
        return ssnToListener.get(ssn);
    }

    public NonBlockingHashMap<Integer, SccpListener> getAllSccpListeners() {
        return ssnToListener;
    }

    public SccpConnection newConnection(int localSsn, ProtocolClass protocol) throws MaxConnectionCountReached {
        return stack.newConnection(localSsn, protocol);
    }

    @Override
    public ConcurrentHashMap<LocalReference, SccpConnection> getConnections() {
        ConcurrentHashMap<LocalReference, SccpConnection> connections = new ConcurrentHashMap<>();

        if (stack.connections != null) {
            for (Map.Entry<Integer, SccpConnectionImpl> entry: stack.connections.entrySet()) {
                connections.put(new LocalReferenceImpl(entry.getKey()), entry.getValue());
            }
        }
        return connections;
    }

    @Override
    public void send(SccpDataMessage message) throws IOException {
        try{
            SccpDataMessageImpl msg = ((SccpDataMessageImpl) message);
            stack.send(msg);
        } catch(Exception e) {
            logger.error(e);
            throw new IOException(e);
        }
    }

    @Override
    public void send(SccpNoticeMessage message) throws IOException {
        try{
            SccpNoticeMessageImpl msg = ((SccpNoticeMessageImpl) message);
            stack.send(msg);
        }catch(Exception e){
            throw new IOException(e);
        }
    }

    public int getMaxUserDataLength(SccpAddress calledPartyAddress, SccpAddress callingPartyAddress, int msgNetworkId) {
        return this.stack.getMaxUserDataLength(calledPartyAddress, callingPartyAddress, msgNetworkId);
    }

    @Override
    public Map<Integer, NetworkIdState> getNetworkIdStateList() {
        return this.stack.ss7ExtSccpDetailedInterface.getNetworkIdList(-1);
    }

    @Override
    public void coordRequest(int ssn) {
        // TODO Auto-generated method stub

    }

    @Override
    public ExecutorCongestionMonitor[] getExecutorCongestionMonitorList() {
        ArrayList<ExecutorCongestionMonitor> res = new ArrayList<ExecutorCongestionMonitor>();
        for (Map.Entry<Integer, Mtp3UserPart> e : this.stack.mtp3UserParts.entrySet()) {
            Mtp3UserPart mup = e.getValue();
            ExecutorCongestionMonitor ecm = mup.getExecutorCongestionMonitor();
            if (ecm != null)
                res.add(ecm);
        }

        ExecutorCongestionMonitor[] ress = new ExecutorCongestionMonitor[res.size()];
        res.toArray(ress);
        return ress;
    }

    @Override
    public SccpStack getSccpStack() {
        return this.stack;
    }

    public ConcurrentHashMap<Integer, Integer> getCongestionSsn() {
        return this.congestionSsn;
    }

    @Override
    public void updateSPCongestion(Integer ssn, Integer congestionLevel) {
        congestionSsn.put(ssn, congestionLevel);
    }

}
