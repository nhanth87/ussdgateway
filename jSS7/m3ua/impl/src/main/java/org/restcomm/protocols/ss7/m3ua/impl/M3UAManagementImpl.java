
package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.Management;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.ErrorRetryAction;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.M3UACounterProvider;
import org.restcomm.protocols.ss7.m3ua.M3UAManagementEventListener ;
import org.restcomm.protocols.ss7.m3ua.RouteAs;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSM;
import org.restcomm.protocols.ss7.m3ua.impl.message.MessageFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.impl.oam.M3UAOAMMessages;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.impl.scheduler.M3UAScheduler;
import org.restcomm.protocols.ss7.m3ua.message.MessageClass;
import org.restcomm.protocols.ss7.m3ua.M3UAManagement;
import org.restcomm.protocols.ss7.m3ua.message.MessageFactory;
import org.restcomm.protocols.ss7.m3ua.message.MessageType;
import org.restcomm.protocols.ss7.m3ua.message.transfer.PayloadData;
import org.restcomm.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.ProtocolData;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.mtp.Mtp3EndCongestionPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3PausePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3ResumePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3StatusPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPartBaseImpl;
import org.restcomm.protocols.ss7.mtp.RoutingLabelFormat;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;

/**
 * @author amit bhayani
 */
public class M3UAManagementImpl extends Mtp3UserPartBaseImpl implements M3UAManagement {

    private static final Logger logger = Logger.getLogger(M3UAManagementImpl.class);
    private static final String AS_LIST = "asList";
    private static final String ASP_FACTORY_LIST = "aspFactoryList";
    private static final String DPC_VS_AS_LIST = "route";

    private static final String MAX_AS_FOR_ROUTE_PROP = "maxasforroute";
    private static final String MAX_SEQUENCE_NUMBER_PROP = "maxsequencenumber";
    private static final String HEART_BEAT_TIME_PROP = "heartbeattime";
    private static final String STATISTICS_ENABLED = "statisticsenabled";
    private static final String STATISTICS_TASK_DELAY = "statisticsdelay";
    private static final String STATISTICS_TASK_PERIOD = "statisticsperiod";
    private static final String ROUTING_KEY_MANAGEMENT_ENABLED = "routingkeymanagementenabled";

    private static final String M3UA_PERSIST_DIR_KEY = "m3ua.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String PERSIST_FILE_NAME = "m3ua1.xml";

    private static final String TAB_INDENT = "\t";

    protected static final int MAX_SEQUENCE_NUMBER = 256;

    protected CopyOnWriteArrayList<As> appServers = new CopyOnWriteArrayList<As>();
    protected CopyOnWriteArrayList<AspFactory> aspFactories = new CopyOnWriteArrayList<AspFactory>();
    
    // Congestion tracking per DPC
    private ConcurrentHashMap<Integer, AtomicInteger> congDpcList = new ConcurrentHashMap<Integer, AtomicInteger>();

    protected M3UAScheduler m3uaScheduler = new M3UAScheduler();
    protected M3UACounterProviderImpl m3uaCounterProvider;

    private final StringBuilder persistFile = new StringBuilder();

    private final String name;

    private String persistDir = null;

    protected ParameterFactory parameterFactory = new ParameterFactoryImpl();
    protected MessageFactory messageFactory = new MessageFactoryImpl();

    protected Management transportManagement = null;
    protected boolean sctpLibNettySupport = false;

    protected ScheduledExecutorService fsmTicker;

    protected int maxAsForRoute = 2;

    protected int timeBetweenHeartbeat = 10000; // 10 sec default

    private M3UARouteManagement routeManagement;

    private boolean statisticsEnabled = false;
    private long statisticsTaskDelay = 5000;
    private long statisticsTaskPeriod = 5000;
    private boolean routingKeyManagementEnabled = false;

    protected final CopyOnWriteArrayList<M3UAManagementEventListener> managementEventListeners = new CopyOnWriteArrayList<M3UAManagementEventListener>();

    /**
     * Maximum sequence number received from SCTP user. If SCTP users sends seq number greater than max, packet will be
     * dropped and error message will be logged
     */
    private int maxSequenceNumber = MAX_SEQUENCE_NUMBER;

    public M3UAManagementImpl(String name, String productName, Ss7ExtInterface ss7ExtInterface) {
        super(productName, ss7ExtInterface);
        this.name = name;


        this.routeManagement = new M3UARouteManagement(this);
        this.m3uaCounterProvider = new M3UACounterProviderImpl(this);

    }

    public String getName() {
        return name;
    }

    public int getMaxSequenceNumber() {
        return maxSequenceNumber;
    }

    /**
     * Set the maximum SLS that can be used by SCTP. Internally SLS vs SCTP Stream Sequence Number is maintained. Stream Seq
     * Number 0 is for management.
     *
     * @param maxSequenceNumber the maxSls to set
     */
    public void setMaxSequenceNumber(int maxSequenceNumber) throws Exception {
        if (this.isStarted)
            throw new Exception("MaxSequenceNumber parameter can be updated only when M3UA stack is NOT running");

        if (maxSequenceNumber < 1) {
            maxSequenceNumber = 1;
        } else if (maxSequenceNumber > MAX_SEQUENCE_NUMBER) {
            maxSequenceNumber = MAX_SEQUENCE_NUMBER;
        }
        this.maxSequenceNumber = maxSequenceNumber;
    }

    public String getPersistDir() {
        return persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
        this.persistFile.setLength(0);
    }

    public int getMaxAsForRoute() {
        return maxAsForRoute;
    }

    public void setMaxAsForRoute(int maxAsForRoute) throws Exception {
        if (this.isStarted)
            throw new Exception("MaxAsForRoute parameter can be updated only when M3UA stack is NOT running");

        if (maxAsForRoute < 1) {
            maxAsForRoute = 1;
        } else if (maxAsForRoute > 4) {
            maxAsForRoute = 4;
        }

        this.maxAsForRoute = maxAsForRoute;
    }

    public int getHeartbeatTime() {
        return this.timeBetweenHeartbeat;
    }

    public void setHeartbeatTime(int timeBetweenHeartbeat) throws Exception {
        if (!this.isStarted)
            throw new Exception("HeartbeatTime parameter can be updated only when M3UA stack is running");

        if (timeBetweenHeartbeat < 1000) {
            // minimum 1 sec is reasonable?
            timeBetweenHeartbeat = 1000;
        }

        this.timeBetweenHeartbeat = timeBetweenHeartbeat;

        this.store();
    }

    @Override
    public void setUseLsbForLinksetSelection(boolean useLsbForLinksetSelection) throws Exception {
        if (!this.isStarted)
            throw new Exception("UseLsbForLinksetSelection parameter can be updated only when M3UA stack is running");

        super.setUseLsbForLinksetSelection(useLsbForLinksetSelection);

        this.store();
    }

    @Override
    public void setRoutingLabelFormat(RoutingLabelFormat routingLabelFormat) throws Exception {
        if (this.isStarted)
            throw new Exception("RoutingLabelFormat parameter can be updated only when M3UA stack is NOT running");

        super.setRoutingLabelFormat(routingLabelFormat);
    }

    @Override
    public void setDeliveryMessageThreadCount(int deliveryMessageThreadCount) throws Exception {
        if (this.isStarted)
            throw new Exception("DeliveryMessageThreadCount parameter can be updated only when M3UA stack is NOT running");

        super.setDeliveryMessageThreadCount(deliveryMessageThreadCount);
    }

    @Override
    public String getRoutingLabelFormatStr() {
        return super.getRoutingLabelFormat().toString();
    }

    public Management getTransportManagement() {
        return transportManagement;
    }

    public void setTransportManagement(Management transportManagement) {
        this.transportManagement = transportManagement;
        if (transportManagement != null && transportManagement.getClass().getSimpleName().contains("Netty"))
            sctpLibNettySupport = true;
    }

    public boolean isSctpLibNettySupport() {
        return sctpLibNettySupport;
    }

    public void start() throws Exception {
        if (this.transportManagement == null) {
            throw new NullPointerException("TransportManagement is null");
        }

        if (maxAsForRoute < 1 || maxAsForRoute > 4) {
            throw new Exception("Max AS for a route can be minimum 1 or maximum 4");
        }

        super.start();

        this.preparePersistFile();
        logger.info(String.format("M3UA configuration file path %s", persistFile.toString()));

        // XStream handles circular references differently; no binding needed

        try {
            this.load();
        } catch (FileNotFoundException e) {
            logger.warn(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
        }

        fsmTicker = Executors.newSingleThreadScheduledExecutor();
        fsmTicker.scheduleAtFixedRate(m3uaScheduler, 500, 500, TimeUnit.MILLISECONDS);

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onServiceStarted();
            } catch (Throwable ee) {
                logger.error("Exception while invoking M3UAManagementEventListener.onServiceStarted", ee);
            }
        }

        logger.info("Started M3UAManagement");
    }

    private void preparePersistFile() {
        if (this.persistFile.length() > 0)
            return;

        if (persistDir != null) {
            this.persistFile.append(persistDir).append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFile.append(System.getProperty(M3UA_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        }
    }

    public void stop() throws Exception {

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onServiceStopped();
            } catch (Throwable ee) {
                logger.error("Exception while invoking onServiceStopped", ee);
            }
        }

        this.store();

        this.stopFactories();
        super.stop();

        fsmTicker.shutdown();
    }

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }

    @Override
    public void addM3UAManagementEventListener(M3UAManagementEventListener m3uaManagementEventListener) {
        if (!this.managementEventListeners.contains(m3uaManagementEventListener)) {
            this.managementEventListeners.add(m3uaManagementEventListener);
        }
    }

    @Override
    public void removeM3UAManagementEventListener(M3UAManagementEventListener m3uaManagementEventListener) {
        this.managementEventListeners.remove(m3uaManagementEventListener);
    }

    public List<As> getAppServers() {
        return new ArrayList<As>(this.appServers);
    }

    public List<AspFactory> getAspfactories() {
        return new ArrayList<AspFactory>(this.aspFactories);
    }

    public Map<String, RouteAs> getRoute() {
        Map<String, RouteAs> routeTmp = new HashMap<String, RouteAs>();
        routeTmp.putAll(this.routeManagement.route);
        return routeTmp;
    }
    
    public ConcurrentHashMap<Integer, AtomicInteger> getCongDpcList() {
        return congDpcList;
    }

    protected As getAs(String asName) {
        for (As as : appServers) {
            if (as.getName().equals(asName)) {
                return as;
            }
        }
        return null;
    }

    /**
     * <p>
     * Create new {@link AsImpl}
     * </p>
     * <p>
     * Command is m3ua as create <as-name> <AS | SGW | IPSP> mode <SE | DE> ipspType <client | server > rc <routing-context>
     * traffic-mode <traffic mode> min-asp <minimum asp active for TrafficModeType.Loadshare> network-appearance <network
     * appearance>
     * </p>
     * <p>
     * where mode is optional, by default SE. ipspType should be specified if type is IPSP. rc is optional and traffic-mode is
     * also optional, default is Loadshare
     * </p>
     *
     * @return As
     * @throws Exception
     */
    public As createAs(String asName, Functionality functionality, ExchangeType exchangeType, IPSPType ipspType,
            RoutingContext routingContext, TrafficModeType trafficModeType, int minAspActiveForLoadbalance, NetworkAppearance networkAppearance)
            throws Exception {

        As as = this.getAs(asName);
        if (as != null) {
            throw new Exception(String.format(M3UAOAMMessages.CREATE_AS_FAIL_NAME_EXIST, asName));
        }

        // TODO check if RC is already taken?

        if (exchangeType == null) {
            exchangeType = ExchangeType.SE;
        }

        if (functionality == Functionality.IPSP && ipspType == null) {
            ipspType = IPSPType.CLIENT;
        }

        as = new AsImpl(asName, routingContext, trafficModeType, minAspActiveForLoadbalance, functionality, exchangeType, ipspType, networkAppearance);
        ((AsImpl) as).setM3UAManagement(this);
        FSM localFSM = ((AsImpl) as).getLocalFSM();
        this.m3uaScheduler.execute(localFSM);

        FSM peerFSM = ((AsImpl) as).getPeerFSM();
        this.m3uaScheduler.execute(peerFSM);

        appServers.add(as);

        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAsCreated(as);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAsCreated", ee);
            }

        }

        return as;
    }

    public AsImpl destroyAs(String asName) throws Exception {
        AsImpl as = (AsImpl) this.getAs(asName);
        if (as == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_AS_FOUND, asName));
        }

        if (as.appServerProcs.size() != 0) {
            throw new Exception(String.format(M3UAOAMMessages.DESTROY_AS_FAILED_ASP_ASSIGNED, asName));
        }

        for (RouteAsImpl asList : this.routeManagement.route.values()) {
            if(asList.hasAs(as)){
                throw new Exception(String.format(M3UAOAMMessages.AS_USED_IN_ROUTE_ERROR, asName, asList));
            }
        }

        FSM asLocalFSM = as.getLocalFSM();
        if (asLocalFSM != null) {
            asLocalFSM.cancel();
        }

        FSM asPeerFSM = as.getPeerFSM();
        if (asPeerFSM != null) {
            asPeerFSM.cancel();
        }

        appServers.remove(as);

        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAsDestroyed(as);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAsDestroyed", ee);
            }
        }

        return as;
    }

    /**
     * Create new {@link AspFactoryImpl} without passing optional aspid and heartbeat is false
     *
     * @param aspName
     * @param associationName
     * @return AspFactory
     * @throws Exception
     */
    public AspFactory createAspFactory(String aspName, String associationName) throws Exception {
        return this.createAspFactory(aspName, associationName, false);
    }

    /**
     * Create new {@link AspFactoryImpl} without passing optional aspid
     *
     * @param aspName
     * @param associationName
     * @return AspFactory
     * @throws Exception
     */
    public AspFactory createAspFactory(String aspName, String associationName, boolean isHeartBeatEnabled) throws Exception {
        long aspid = 0L;
        boolean regenerateFlag = true;

        while (regenerateFlag) {
            aspid = AspFactoryImpl.generateId();
            if (aspFactories.size() == 0) {
                // Special case where this is first Asp added
                break;
            }

            for (AspFactory aspFactory : aspFactories) {
                AspFactoryImpl aspFactoryImpl = (AspFactoryImpl) aspFactory;
                if (aspid == aspFactoryImpl.getAspId().getAspId()) {
                    regenerateFlag = true;
                    break;
                } else {
                    regenerateFlag = false;
                }
            }// for
        }// while

        return this.createAspFactory(aspName, associationName, aspid, isHeartBeatEnabled);
    }

    /**
     * <p>
     * Create new {@link AspFactoryImpl}
     * </p>
     * <p>
     * Command is m3ua asp create <asp-name> <sctp-association> aspid <aspid> heartbeat <true|false>
     * </p>
     * <p>
     * asp-name and sctp-association is mandatory where as aspid is optional. If aspid is not passed, next available aspid wil
     * be used
     * </p>
     *
     * @param aspName
     * @param associationName
     * @param aspId
     * @return AspFactory
     * @throws Exception
     */
    public AspFactory createAspFactory(String aspName, String associationName, long aspId, boolean isHeartBeatEnabled)
            throws Exception {
        AspFactoryImpl factory = this.getAspFactory(aspName);

        if (factory != null) {
            throw new Exception(String.format(M3UAOAMMessages.CREATE_ASP_FAIL_NAME_EXIST, aspName));
        }

        Association association = this.transportManagement.getAssociation(associationName);
        if (association == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_ASSOCIATION_FOUND, associationName));
        }

        if (association.isStarted()) {
            throw new Exception(String.format(M3UAOAMMessages.ASSOCIATION_IS_STARTED, associationName));
        }

        if (association.getAssociationListener() != null) {
            throw new Exception(String.format(M3UAOAMMessages.ASSOCIATION_IS_ASSOCIATED, associationName));
        }

        for (AspFactory aspFactory : aspFactories) {
            AspFactoryImpl aspFactoryImpl = (AspFactoryImpl) aspFactory;
            if (aspId == aspFactoryImpl.getAspId().getAspId()) {
                throw new Exception(String.format(M3UAOAMMessages.ASP_ID_TAKEN, aspId));
            }
        }

        factory = new AspFactoryImpl(aspName, this.getMaxSequenceNumber(), aspId, isHeartBeatEnabled);
        factory.setM3UAManagement(this);
        factory.setAssociation(association);
        factory.setTransportManagement(this.transportManagement);

        aspFactories.add(factory);

        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspFactoryCreated(factory);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspFactoryCreated", ee);
            }
        }

        return factory;
    }

    public AspFactoryImpl destroyAspFactory(String aspName) throws Exception {
        AspFactoryImpl aspFactory = this.getAspFactory(aspName);
        if (aspFactory == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_ASP_FOUND, aspName));
        }

        if (aspFactory.aspList.size() != 0) {
            throw new Exception("ASPs are still assigned to As. Unassign all");
        }
        aspFactory.unsetAssociation();
        this.aspFactories.remove(aspFactory);
        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspFactoryDestroyed(aspFactory);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspFactoryDestroyed", ee);
            }
        }

        return aspFactory;
    }

    /**
     * Associate {@link AspImpl} to {@link AsImpl}
     *
     * @param asName
     * @param aspName
     * @return AspImpl
     * @throws Exception
     */
    public AspImpl assignAspToAs(String asName, String aspName) throws Exception {
        // check ASP and AS exist with given name
        AsImpl asImpl = (AsImpl) this.getAs(asName);

        if (asImpl == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_AS_FOUND, asName));
        }

        AspFactoryImpl aspFactory = this.getAspFactory(aspName);

        if (aspFactory == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_ASP_FOUND, aspName));
        }

        // If ASP already assigned to AS we don't want to re-assign
        for (Asp asp : asImpl.appServerProcs) {
            AspImpl aspImpl = (AspImpl) asp;
            if (aspImpl.getName().equals(aspName)) {
                throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_THIS_AS, aspName,
                        asName));
            }
        }

        List<Asp> aspImplList = new java.util.ArrayList<>(aspFactory.aspList);

        // Checks for RoutingContext. We know that for null RC there will always
        // be a single ASP assigned to AS and ASP cannot be shared
        if (asImpl.getRoutingContext() == null) {
            // If AS has Null RC, this should be the first assignment of ASP to
            // AS
            if (aspImplList.size() != 0) {
                throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_OTHER_AS, aspName,
                        asName));
            }
        } else if (aspImplList.size() > 0) {
            // RoutingContext is not null, make sure there is no ASP that is
            // assigned to AS with null RC
            Asp asp = aspImplList.get(0);
            if (asp != null && asp.getAs().getRoutingContext() == null) {
                throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_OTHER_AS_WITH_NULL_RC,
                        aspName, asName));
            }
        }

        if (aspFactory.getFunctionality() != null && aspFactory.getFunctionality() != asImpl.getFunctionality()) {
            throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_OTHER_AS_TYPE, aspName,
                    asName, aspFactory.getFunctionality()));
        }

        if (aspFactory.getExchangeType() != null && aspFactory.getExchangeType() != asImpl.getExchangeType()) {
            throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_OTHER_AS_EXCHANGETYPE,
                    aspName, asName, aspFactory.getExchangeType()));
        }

        if (aspFactory.getIpspType() != null && aspFactory.getIpspType() != asImpl.getIpspType()) {
            throw new Exception(String.format(M3UAOAMMessages.ADD_ASP_TO_AS_FAIL_ALREADY_ASSIGNED_TO_OTHER_IPSP_TYPE, aspName,
                    asName, aspFactory.getIpspType()));
        }

        aspFactory.setExchangeType(asImpl.getExchangeType());
        aspFactory.setFunctionality(asImpl.getFunctionality());
        aspFactory.setIpspType(asImpl.getIpspType());

        AspImpl aspImpl = aspFactory.createAsp();
        FSM aspLocalFSM = aspImpl.getLocalFSM();
        m3uaScheduler.execute(aspLocalFSM);

        FSM aspPeerFSM = aspImpl.getPeerFSM();
        m3uaScheduler.execute(aspPeerFSM);
        asImpl.addAppServerProcess(aspImpl);

        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspAssignedToAs(asImpl, aspImpl);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspAssignedToAs", ee);
            }
        }

        return aspImpl;
    }

    public Asp unassignAspFromAs(String asName, String aspName) throws Exception {
        // check ASP and AS exist with given name
        AsImpl asImpl = (AsImpl) this.getAs(asName);

        if (asImpl == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_AS_FOUND, asName));
        }

        AspImpl aspImpl = asImpl.removeAppServerProcess(aspName);
        aspImpl.getAspFactory().destroyAsp(aspImpl);
        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspUnassignedFromAs(asImpl, aspImpl);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspUnassignedFromAs", ee);
            }
        }

        return aspImpl;
    }

    /**
     * This method should be called by management to start the ASP
     *
     * @param aspName The name of the ASP to be started
     * @throws Exception
     */
    public void startAsp(String aspName) throws Exception {
        AspFactoryImpl aspFactoryImpl = this.getAspFactory(aspName);

        if (aspFactoryImpl == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_ASP_FOUND, aspName));
        }

        if (aspFactoryImpl.getStatus()) {
            throw new Exception(String.format(M3UAOAMMessages.ASP_ALREADY_STARTED, aspName));
        }

        if (aspFactoryImpl.aspList.size() == 0) {
            throw new Exception(String.format(M3UAOAMMessages.ASP_NOT_ASSIGNED_TO_AS, aspName));
        }

        aspFactoryImpl.start();
        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspFactoryStarted(aspFactoryImpl);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspFactoryStarted", ee);
            }

        }
    }

    /**
     * This method should be called by management to stop the ASP
     *
     * @param aspName The name of the ASP to be stopped
     * @throws Exception
     */
    public void stopAsp(String aspName) throws Exception {

        this.doStopAsp(aspName, true);
    }

    private void doStopAsp(String aspName, boolean needStore) throws Exception {
        AspFactoryImpl aspFactoryImpl = this.getAspFactory(aspName);

        if (aspFactoryImpl == null) {
            throw new Exception(String.format(M3UAOAMMessages.NO_ASP_FOUND, aspName));
        }

        if (!aspFactoryImpl.getStatus()) {
            List<Asp> listAsp = new java.util.ArrayList<>(aspFactoryImpl.aspList);
            Iterator<Asp> aspIterator = listAsp.iterator();
            Asp asp;
            As as = null;
            if (aspIterator.hasNext()) {
                asp = aspIterator.next();
                if (asp.getName().equalsIgnoreCase(aspName))
                    as = asp.getAs();
            }
            if (as != null) {
                if (!as.getState().getName().equalsIgnoreCase("ACTIVE")) {
                    if (!aspFactoryImpl.getAssociation().isConnected()) {
                        // Throw this exception only if:
                        // the AS to which this ASP is bound is NOT ACTIVE &
                        // the SCTP association of this ASP is DOWN
                        throw new Exception(String.format(M3UAOAMMessages.ASP_ALREADY_STOPPED, aspName));
                    }
                }
            }
        }

        aspFactoryImpl.stop();

        if (needStore)
            this.store();

        // TODO : Should calling
        // m3uaManagementEventListener.onAspFactoryStopped() be before actual
        // stop of aspFactory? The problem is ASP_DOWN and AS_INACTIV callbacks
        // are before AspFactoryStopped. Is it ok?
        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onAspFactoryStopped(aspFactoryImpl);
            } catch (Throwable ee) {
                logger.error("Exception while invoking onAspFactoryStopped", ee);
            }
        }
    }

    public void addRoute(int dpc, int opc, int si, String asName) throws Exception {
        this.routeManagement.addRoute(dpc, opc, si, asName, TrafficModeType.Loadshare);
    }

    public void addRoute(int dpc, int opc, int si, String asName, int trafficModeType) throws Exception {
        this.routeManagement.addRoute(dpc, opc, si, asName, trafficModeType);
    }

    public void removeRoute(int dpc, int opc, int si, String asName) throws Exception {
        this.routeManagement.removeRoute(dpc, opc, si, asName);
    }

    public void removeAllResources() throws Exception {

        if (!this.isStarted) {
            throw new Exception(String.format("Management=%s not started", this.name));
        }

        if (this.appServers.size() == 0 && this.aspFactories.size() == 0)
            // no resources allocated - nothing to do
            return;

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Removing allocated resources: AppServers=%d, AspFactories=%d", this.appServers.size(),
                    this.aspFactories.size()));
        }

        this.stopFactories();

        // Remove routes
        this.routeManagement.removeAllResources();

        // Unassign asp from as
        Map<String, List<String>> lstAsAsp = new HashMap<String, List<String>>();

        for (As as : this.appServers) {
            AsImpl asImpl = (AsImpl) as;
            List<String> lstAsps = new ArrayList<String>();

            for (Asp asp : asImpl.appServerProcs) {
                AspImpl aspImpl = (AspImpl) asp;
                lstAsps.add(aspImpl.getName());
            }
            lstAsAsp.put(asImpl.getName(), lstAsps);
        }

        for (Map.Entry<String, List<String>> entry : lstAsAsp.entrySet()) {
            String asName = entry.getKey();
            List<String> lstAsps = entry.getValue();

            for (String aspName : lstAsps) {
                this.unassignAspFromAs(asName, aspName);
            }

        }

        // Remove all AspFactories
        ArrayList<AspFactory> lstAspFactory = new ArrayList<AspFactory>();
        for (AspFactory aspFact : this.aspFactories) {
            lstAspFactory.add(aspFact);
        }
        for (AspFactory aspFact : lstAspFactory) {
            this.destroyAspFactory(aspFact.getName());
        }

        // Remove all AppServers
        ArrayList<String> lst = new ArrayList<String>();
        for (As asImpl : this.appServers) {
            lst.add(asImpl.getName());
        }
        for (String n : lst) {
            this.destroyAs(n);
        }

        // We store the cleared state
        this.store();

        for (M3UAManagementEventListener m3uaManagementEventListener : this.managementEventListeners) {
            try {
                m3uaManagementEventListener.onRemoveAllResources();
            } catch (Throwable ee) {
                logger.error("Exception while invoking onRemoveAllResources", ee);
            }
        }
    }

    private void stopFactories() throws Exception {
        // Stopping asp factories
        boolean someFactoriesIsStopped = false;
        for (AspFactory aspFact : this.aspFactories) {
            AspFactoryImpl aspFactImpl = (AspFactoryImpl) aspFact;
            if (aspFactImpl.started) {
                someFactoriesIsStopped = true;
                this.doStopAsp(aspFact.getName(), false);
            }
        }
        // waiting 5 seconds till stopping factories
        if (someFactoriesIsStopped) {
            for (int step = 1; step < 50; step++) {
                boolean allStopped = true;
                for (AspFactory aspFact : this.aspFactories) {
                    if (aspFact.getAssociation() != null && aspFact.getAssociation().isConnected()) {
                        allStopped = false;
                        break;
                    }
                }
                if (allStopped)
                    break;

                Thread.sleep(100);
            }
        }
    }

    public void sendTransferMessageToLocalUser(Mtp3TransferPrimitive msg, int seqControl) {
        super.sendTransferMessageToLocalUser(msg, seqControl);
    }

    public void sendPauseMessageToLocalUser(Mtp3PausePrimitive msg) {
        super.sendPauseMessageToLocalUser(msg);
    }

    public void sendResumeMessageToLocalUser(Mtp3ResumePrimitive msg) {
        super.sendResumeMessageToLocalUser(msg);
    }

    public void sendStatusMessageToLocalUser(Mtp3StatusPrimitive msg) {
        super.sendStatusMessageToLocalUser(msg);
    }

    public void sendEndCongestionMessageToLocalUser(Mtp3EndCongestionPrimitive msg) {
        super.sendEndCongestionMessageToLocalUser(msg);
    }

    private AspFactoryImpl getAspFactory(String aspName) {
        for (AspFactory aspFactory : aspFactories) {
            AspFactoryImpl aspFactoryImpl = (AspFactoryImpl) aspFactory;
            if (aspFactoryImpl.getName().equals(aspName)) {
                return aspFactoryImpl;
            }
        }
        return null;
    }

    /**
     * Persist
     */
    public void store() {
        try {
            this.preparePersistFile();
            M3UAConfig config = new M3UAConfig();
            config.timeBetweenHeartbeat = this.timeBetweenHeartbeat;
            config.statisticsEnabled = this.statisticsEnabled;
            config.statisticsTaskDelay = this.statisticsTaskDelay;
            config.statisticsTaskPeriod = this.statisticsTaskPeriod;
            config.routingKeyManagementEnabled = this.routingKeyManagementEnabled;
            config.useLsbForLinksetSelection = this.isUseLsbForLinksetSelection();
            config.aspFactories = new CopyOnWriteArrayList<>();
            for (AspFactory af : this.aspFactories) {
                config.aspFactories.add((AspFactoryImpl) af);
            }
            config.appServers = new CopyOnWriteArrayList<>();
            for (As as : this.appServers) {
                config.appServers.add((AsImpl) as);
            }
            config.routeEntries = new java.util.ArrayList<>();
            for (java.util.Map.Entry<String, RouteAsImpl> entry : this.routeManagement.route.entrySet()) {
                RouteEntry re = new RouteEntry();
                re.key = entry.getKey();
                re.value = entry.getValue();
                config.routeEntries.add(re);
            }

            String xml = M3UAJacksonXMLHelper.toXML(config);
            try (FileWriter writer = new FileWriter(persistFile.toString())) {
                writer.write(xml);
            }
        } catch (Exception e) {
            logger.error("Error while persisting the Rule state in file", e);
        }
    }


    public static class RouteEntry {
        @com.fasterxml.jackson.annotation.JsonProperty("key")
        public String key;
        @com.fasterxml.jackson.annotation.JsonProperty("value")
        public RouteAsImpl value;
    }

    /**
     * Configuration class for M3UA persistence
     */

    @JacksonXmlRootElement(localName = "m3uaConfig")
    public static class M3UAConfig {
        @JsonProperty("timeBetweenHeartbeat")
        public int timeBetweenHeartbeat;
        @JsonProperty("statisticsEnabled")
        public boolean statisticsEnabled;
        @JsonProperty("statisticsTaskDelay")
        public long statisticsTaskDelay;
        @JsonProperty("statisticsTaskPeriod")
        public long statisticsTaskPeriod;
        @JsonProperty("routingKeyManagementEnabled")
        public boolean routingKeyManagementEnabled;
        @JsonProperty("useLsbForLinksetSelection")
        public boolean useLsbForLinksetSelection;
        @JsonProperty("aspFactories")
        @JacksonXmlElementWrapper(localName = "aspFactories")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "aspFactory")
        public CopyOnWriteArrayList<AspFactoryImpl> aspFactories;
        @JsonProperty("appServers")
        @JacksonXmlElementWrapper(localName = "appServers")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "as")
        public CopyOnWriteArrayList<AsImpl> appServers;
        @JsonProperty("routeEntries")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(localName = "route")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "routeEntry")
        public java.util.ArrayList<RouteEntry> routeEntries;
    }

    /**
     * Load and create LinkSets and Link from persisted file
     *
     * @throws Exception
     */
    public void load() throws FileNotFoundException {
        try {
            this.preparePersistFile();

            File file = new File(persistFile.toString());
            if (file.exists()) {
                // we have V2 config
                loadVer2(persistFile.toString());
            } else {
                String s1 = persistFile.toString().replace("1.xml", ".xml");
                file = new File(s1);

                if (file.exists()) {
                    loadVer1(s1);
                }

                this.store();
                file.delete();
            }
            M3UAErrorManagementState.getInstance().loadConfig(persistFile.toString());
        } catch (FileNotFoundException e) {
            logger.warn(String.format("Failed to load the M3UA configuration file. \n%s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format("Failed to load the M3UA configuration file. \n%s", e.getMessage()));
        }
    }

    private void validateStatisticsVariable(M3UAConfig config) {
        try {
            this.statisticsEnabled = config.statisticsEnabled;
        } catch (Exception e) {
            this.statisticsEnabled = false;
            logger.warn("Setting the default value for " + STATISTICS_ENABLED + " in  false");
        }

        try {
            this.statisticsTaskDelay = config.statisticsTaskDelay;
        } catch (Exception e) {
            this.statisticsTaskDelay = 5000;
            logger.warn("Setting the default value for " + STATISTICS_TASK_DELAY + " in  5000");
        }

        try {
            this.statisticsTaskPeriod = config.statisticsTaskPeriod;
        } catch (Exception e) {
            this.statisticsTaskPeriod = 5000;
            logger.warn("Setting the default value for " + STATISTICS_TASK_PERIOD + " in  5000");
        }
    }

    protected void loadActualData(M3UAConfig config) throws IOException {
        try {
            this.timeBetweenHeartbeat = config.timeBetweenHeartbeat;
            validateStatisticsVariable(config);
            this.routingKeyManagementEnabled = config.routingKeyManagementEnabled;
        } catch (java.lang.Exception e) {
            // ignore.
            // For backward compatibility we can ignore if these values are not defined
            logger.error("Error while reading attribute", e);
        }

        try {
            super.setUseLsbForLinksetSelection(config.useLsbForLinksetSelection);
        } catch (Exception e) {
        }

        if (config.aspFactories != null) {
            aspFactories = new java.util.concurrent.CopyOnWriteArrayList<AspFactory>((java.util.Collection<? extends AspFactory>) config.aspFactories);
        } else {
            aspFactories = new java.util.concurrent.CopyOnWriteArrayList<AspFactory>();
        }
        if (config.appServers != null) {
            appServers = new java.util.concurrent.CopyOnWriteArrayList<As>((java.util.Collection<? extends As>) config.appServers);
        } else {
            appServers = new java.util.concurrent.CopyOnWriteArrayList<As>();
        }
        RouteMap<String, RouteAsImpl> routeMap = new RouteMap<>();
        if (config.routeEntries != null) {
            for (RouteEntry re : config.routeEntries) {
                routeMap.put(re.key, re.value);
            }
        }
        this.routeManagement.route = routeMap;

        this.routeManagement.reset();

        // Create Asp's and assign to each of the AS. Schedule the FSMs
        for (As as : appServers) {
            AsImpl asImpl = (AsImpl) as;
            asImpl.init();
            asImpl.setM3UAManagement(this);
            FSM asLocalFSM = asImpl.getLocalFSM();
            m3uaScheduler.execute(asLocalFSM);

            FSM asPeerFSM = asImpl.getPeerFSM();
            m3uaScheduler.execute(asPeerFSM);

            // All the Asp's for this As added in temp list
            List<Asp> tempAsp = new ArrayList<Asp>();
            tempAsp.addAll(asImpl.appServerProcs);

            // Clear Asp's from this As
            asImpl.appServerProcs.clear();

            for (Asp asp : tempAsp) {
                AspImpl aspImpl = (AspImpl) asp;

                try {
                    // Now let the Asp's be created from respective
                    // AspFactory and added to As
                    this.assignAspToAs(asImpl.getName(), aspImpl.getName());
                } catch (Exception e) {
                    logger.error("Error while assigning Asp to As on loading from xml file", e);
                }
            }
        }

        // Set the transportManagement
        for (AspFactory aspFactory : aspFactories) {
            AspFactoryImpl factory = (AspFactoryImpl) aspFactory;
            factory.setTransportManagement(this.transportManagement);
            factory.setM3UAManagement(this);
            try {
                Association association = this.transportManagement.getAssociation(factory.associationName);
                if (association != null) {
                    factory.setAssociation(association);
                }
            } catch (Throwable e1) {
                logger.error(String.format("Error setting Association=%s for the AspFactory=%s while loading from XML",
                        factory.associationName, factory.getName()), e1);
            }

            if (factory.getStatus()) {
                try {
                    factory.start();
                } catch (Exception e) {
                    logger.error(String.format("Error starting the AspFactory=%s while loading from XML", factory.getName()), e);
                }
            }
        }
    }

    private void loadVer1(String fn) throws IOException {
        // Legacy format not supported with XStream - will use defaults
        logger.warn("Legacy M3UA config format (v1) not supported, using defaults");
    }

    protected void loadVer2(String fn) throws IOException {
        try (FileReader reader = new FileReader(fn)) {
            M3UAConfig config = M3UAJacksonXMLHelper.fromXML(reader, M3UAConfig.class);
            if (config.aspFactories != null) {
                for (AspFactoryImpl factory : config.aspFactories) {
                    factory.aspList.clear();
                }
            }
            this.loadActualData(config);
        } catch (Exception e) {
            logger.error("Failed to parse M3UA config from XML: " + e.getMessage(), e);
            throw new IOException("Failed to parse M3UA config", e);
        }
    }

    @Override
    public void sendMessage(Mtp3TransferPrimitive mtp3TransferPrimitive) throws IOException {
        ProtocolData data = this.parameterFactory.createProtocolData(mtp3TransferPrimitive.getOpc(),
                mtp3TransferPrimitive.getDpc(), mtp3TransferPrimitive.getSi(), mtp3TransferPrimitive.getNi(),
                mtp3TransferPrimitive.getMp(), mtp3TransferPrimitive.getSls(), mtp3TransferPrimitive.getData());

        PayloadData payload = (PayloadData) messageFactory.createMessage(MessageClass.TRANSFER_MESSAGES, MessageType.PAYLOAD);
        payload.setData(data);

        AsImpl asImpl = this.routeManagement.getAsForRoute(data.getDpc(), data.getOpc(), data.getSI(), data.getSLS());
        if (asImpl == null) {
            logger.error(String.format("Tx : No AS found for routing message %s", payload));
            throw new IOException(String.format("Tx : No AS found for routing message %s", payload));
        }
        payload.setNetworkAppearance(asImpl.getNetworkAppearance());
        payload.setRoutingContext(asImpl.getRoutingContext());
        asImpl.write(payload);
    }

    @Override
    public boolean getStatisticsEnabled() {
        return statisticsEnabled;
    }

    @Override
    public void setStatisticsEnabled(boolean val) throws Exception {
        if (!this.isStarted)
            throw new Exception("StatisticsEnabled parameter can be updated only when M3UA management is running");

        this.m3uaCounterProvider = new M3UACounterProviderImpl(this);

        statisticsEnabled = val;

        this.store();
    }

    @Override
    public long getStatisticsTaskDelay() {
        return statisticsTaskDelay;
    }

    @Override
    public void setStatisticsTaskDelay(long statisticsTaskDelay) throws Exception {
        if (!this.isStarted) {
            if (!this.statisticsEnabled)
                throw new Exception("StatisticsTaskDelay parameter can be updated only when M3UA management is running and " +
                    "StatisticsEnabled is set to true");
        }
        this.statisticsTaskDelay = statisticsTaskDelay;
        this.store();
    }

    @Override
    public long getStatisticsTaskPeriod() {
        return statisticsTaskPeriod;
    }

    @Override
    public void setStatisticsTaskPeriod(long statisticsTaskPeriod) throws Exception {
        if (!this.isStarted) {
            if (!this.statisticsEnabled)
                throw new Exception("StatisticsTaskPeriod parameter can be updated only when M3UA management is running and " +
                    "StatisticsEnabled is set to true");
        }
        this.statisticsTaskPeriod = statisticsTaskPeriod;
        this.store();
    }

    @Override
    public M3UACounterProvider getCounterProvider() {
        return m3uaCounterProvider;
    }

    public M3UACounterProviderImpl getCounterProviderImpl() {
        return m3uaCounterProvider;
    }

    @Override
    public boolean getRoutingKeyManagementEnabled() {
        return routingKeyManagementEnabled;
    }

    @Override
    public void setRoutingKeyManagementEnabled(boolean routingKeyManagementEnabled) {
        this.routingKeyManagementEnabled = routingKeyManagementEnabled;
    }

    @Override
    public void setErrorRetryAction(int errorCode, String name, int retryCount) {
        M3UAErrorManagementState.getInstance().addErrorAction(new ErrorRetryActionImpl(errorCode, name, retryCount));
    }

    @Override
    public List<ErrorRetryAction> getErrorRetry() {
        return M3UAErrorManagementState.getInstance().getErrorRetry();
    }

    @Override
    public void removeErrorAction(int errorCode, String name, int retryCount) {
        // remove the error action from the list.
        M3UAErrorManagementState.getInstance().removeErrorAction(new ErrorRetryActionImpl(errorCode, name, retryCount));
    }
}
