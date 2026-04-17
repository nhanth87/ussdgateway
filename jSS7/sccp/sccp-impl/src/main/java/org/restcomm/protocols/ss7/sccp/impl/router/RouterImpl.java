package org.restcomm.protocols.ss7.sccp.impl.router;

import java.util.Map;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.LongMessageRule;
import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;
import org.restcomm.protocols.ss7.sccp.Mtp3Destination;
import org.restcomm.protocols.ss7.sccp.Mtp3ServiceAccessPoint;
import org.restcomm.protocols.ss7.sccp.Router;
import org.restcomm.protocols.ss7.sccp.SccpStack;
import org.restcomm.protocols.ss7.sccp.impl.oam.SccpOAMMessage;

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
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * The default implementation for the SCCP router.
 * </p>
 *
 * <p>
 * The SCCP router allows to add/remove/list routing rules and implements persistence for the routing rules.
 * </p>
 * <p>
 * RouterImpl when {@link #start() started} looks for file <tt>sccprouter.xml</tt> containing serialized information of
 * underlying {@link }. Set the directory path by calling {@link #setPersistDir(String)} to direct RouterImpl to look at
 * specified directory for underlying serialized file.
 * </p>
 * <p>
 * If directory path is not set, RouterImpl searches for system property <tt>sccprouter.persist.dir</tt> to get the path for
 * directory
 * </p>
 *
 * <p>
 * Even if <tt>sccprouter.persist.dir</tt> system property is not set, RouterImpl will look at property <tt>user.dir</tt>
 * </p>
 *
 * <p>
 * Implementation of SCCP routing mechanism makes routing decisions based on rules. Each rule consists of three elements:
 * <ul>
 * <li>
 * <p>
 * The <i>pattern</i> determines pattern to which destination address is compared. It has complex structure which looks as
 * follows:
 * <ul>
 * <li>
 * <p>
 * <i>translation type</i> (tt) integer numer which is used in a network to indicate the preferred method of global title
 * analysis
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>numbering plan</i> (np) integer value which inidcates which numbering plan will be used for the global title. Its value
 * aids the routing system in determining the correct network system to route message to.
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>nature of address</i> (noa) integer value which indicates address type., Specifically it indicates the scope of the
 * address value, such as whether it is an international number (i.e. including the country code), a "national" or domestic
 * number (i.e. without country code), and other formats such as "local" format.
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>digits</i> (digits) actual address
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>sub-system number</i> (ssn) identifies application in SCCP routing network.
 * </p>
 * </li>
 * </ul>
 * </p>
 * </li>
 * <li>
 * <p>
 * The <i>translation</i> determines target for messages which destination address matches pattern. It has exactly the same
 * structure as pattern .
 * </p>
 * </li>
 * <li>
 * <p>
 * The <i>mtpinfo</i> determines mtp layer information. If translation does not indicate local address, this information is used
 * to send message through MTP layer. It has following structure:
 * <ul>
 * <li>
 * <p>
 * <i>name</i> (name) identifying one of link sets used by SCCP
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>originating point code</i> (opc) local point code used as originating MTP address
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>adjacent point code</i> (apc) remote point code used as destination MTP address
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>signaling link selection</i> (sls) indentifies link in set
 * </p>
 * </li>
 * </ul>
 * </p>
 * </li>
 * </ul>
 * </p>
 * <p>
 * While the <i>pattern</i> is mandatory, <i>translation</i> and <i>mtpinfo</i> is optional. Following combinations are possible
 * <ul>
 * <li>
 * <p>
 * <i>pattern</i> and <i>translation</i> : specifies local routing
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>pattern</i> and <i>mtpinfo</i> : specifies remote routing using specified mtp routing info and no translation needed
 * </p>
 * </li>
 * <li>
 * <p>
 * <i>pattern</i>, <i>translation</i> and <i>mtpinfo</i> specifies remote routing using specified mtp routing info after
 * applying specified translation
 * </p>
 * </li>
 * </ul>
 * </p>
 *
 * @author amit bhayani
 * @author kulikov
 */
public class RouterImpl implements Router {

    private static final Logger logger = Logger.getLogger(RouterImpl.class);

    private static final String SCCP_ROUTER_PERSIST_DIR_KEY = "sccprouter.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String PERSIST_FILE_NAME = "sccprouter4.xml";

    private static final String LONG_MESSAGE_RULE = "longMessageRule";
    private static final String MTP3_SERVICE_ACCESS_POINT = "sap";

    private String persistFile;

    private static final String TAB_INDENT = "\t";

    private String persistDir = null;

    private LongMessageRuleMap<Integer, LongMessageRule> longMessageRules = new LongMessageRuleMap<>();
    private Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> saps = new Mtp3ServiceAccessPointMap<>();

    private final String name;
    private final SccpStack sccpStack;

    public RouterImpl(String name, SccpStack sccpStack) {
        this.name = name;
        this.sccpStack = sccpStack;


    }

    public String getName() {
        return this.name;
    }

    public String getPersistDir() {
        return this.persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
    }

    public void start() {
        if (persistDir != null) {
            this.persistFile = persistDir + File.separator + this.name + "_" + PERSIST_FILE_NAME;
        } else {
            this.persistFile = System.getProperty(SCCP_ROUTER_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY))
                    + File.separator + this.name + "_" + PERSIST_FILE_NAME;
        }

        logger.info(String.format("SCCP Router configuration file path %s", this.persistFile));

        this.load();

        logger.info("Started SCCP Router");
    }

    public void stop() {
        this.store();
    }

    public LongMessageRule findLongMessageRule(int dpc) {
        for (Map.Entry<Integer, LongMessageRule> e : this.longMessageRules.entrySet()) {
            LongMessageRule rule = e.getValue();
            if (rule.matches(dpc)) {
                return rule;
            }
        }
        return null;
    }

    public Mtp3ServiceAccessPoint findMtp3ServiceAccessPoint(int dpc, int sls) {
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.matches(dpc, sls)) {
                return sap;
            }
        }
        return null;
    }

    public Mtp3ServiceAccessPoint findMtp3ServiceAccessPoint(int dpc, int sls, int networkId) {
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.matches(dpc, sls)) {
                if (sap.getNetworkId() == networkId) {
                    return sap;
                }
            }
        }
        return null;
    }

    public Mtp3ServiceAccessPoint findMtp3ServiceAccessPointForIncMes(int localPC, int remotePC, String localGtDigits) {
        // a first step - sap's with LocalGtDigits
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.getLocalGtDigits() != null && sap.getLocalGtDigits().length() > 0) {
                if (sap.getOpc() == localPC && sap.matches(remotePC)
                        && (localGtDigits != null && localGtDigits.equals(sap.getLocalGtDigits()))) {
                    return sap;
                }
            }
        }

        // a second step - sap's without LocalGtDigits
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.getLocalGtDigits() == null || sap.getLocalGtDigits().length() == 0) {
                if (sap.getOpc() == localPC && sap.matches(remotePC)) {
                    return sap;
                }
            }
        }

        return null;
    }

    public LongMessageRule getLongMessageRule(int id) {
        return this.longMessageRules.get(id);
    }

    public Mtp3ServiceAccessPoint getMtp3ServiceAccessPoint(int id) {
        return this.saps.get(id);
    }

    @Override
    public boolean spcIsLocal(int spc) {
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.getOpc() == spc) {
                return true;
            }
        }
        return false;
    }

    //Check if the SCP is local using the networkId
    public boolean spcIsLocal(int spc, int networkId) {
        for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
            Mtp3ServiceAccessPoint sap = e.getValue();
            if (sap.getOpc() == spc && sap.getNetworkId() == networkId) {
                return true;
            }
        }
        return false;
    }

    public Map<Integer, LongMessageRule> getLongMessageRules() {
        Map<Integer, LongMessageRule> longMessageRulesTmp = new HashMap<>();
        longMessageRulesTmp.putAll(longMessageRules);
        return longMessageRulesTmp;
    }

    public Map<Integer, Mtp3ServiceAccessPoint> getMtp3ServiceAccessPoints() {
        Map<Integer, Mtp3ServiceAccessPoint> sapsTmp = new HashMap<>();
        sapsTmp.putAll(saps);
        return sapsTmp;
    }

    public void addLongMessageRule(int id, int firstSpc, int lastSpc, LongMessageRuleType ruleType) throws Exception {
        if (this.getLongMessageRule(id) != null) {
            throw new Exception(SccpOAMMessage.LMR_ALREADY_EXIST);
        }

        LongMessageRuleImpl longMessageRule = new LongMessageRuleImpl(firstSpc, lastSpc, ruleType);

        synchronized (this) {
            LongMessageRuleMap<Integer, LongMessageRule> newLongMessageRule = new LongMessageRuleMap<>();
            newLongMessageRule.putAll(this.longMessageRules);
            newLongMessageRule.put(id, longMessageRule);
            this.longMessageRules = newLongMessageRule;
            this.store();
        }
    }

    public void modifyLongMessageRule(int id, int firstSpc, int lastSpc, LongMessageRuleType ruleType) throws Exception {
        if (this.getLongMessageRule(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.LMR_DOESNT_EXIST, name));
        }

        LongMessageRuleImpl longMessageRule = new LongMessageRuleImpl(firstSpc, lastSpc, ruleType);

        synchronized (this) {
            LongMessageRuleMap<Integer, LongMessageRule> newLongMessageRule = new LongMessageRuleMap<>();
            newLongMessageRule.putAll(this.longMessageRules);
            newLongMessageRule.put(id, longMessageRule);
            this.longMessageRules = newLongMessageRule;
            this.store();
        }
    }

    public void modifyLongMessageRule(int id, Integer firstSpc, Integer lastSpc, LongMessageRuleType ruleType) throws Exception {
        LongMessageRule oldLmr = this.getLongMessageRule(id);
        if (oldLmr == null) {
            throw new Exception(String.format(SccpOAMMessage.LMR_DOESNT_EXIST, name));
        }

        if(firstSpc == null)
            firstSpc = oldLmr.getFirstSpc();
        if(lastSpc == null)
            lastSpc = oldLmr.getLastSpc();
        if(ruleType == null)
            ruleType = oldLmr.getLongMessageRuleType();

        LongMessageRuleImpl longMessageRule = new LongMessageRuleImpl(firstSpc, lastSpc, ruleType);

        synchronized (this) {
            LongMessageRuleMap<Integer, LongMessageRule> newLongMessageRule = new LongMessageRuleMap<>();
            newLongMessageRule.putAll(this.longMessageRules);
            newLongMessageRule.put(id, longMessageRule);
            this.longMessageRules = newLongMessageRule;
            this.store();
        }
    }

    public void removeLongMessageRule(int id) throws Exception {
        if (this.getLongMessageRule(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.LMR_DOESNT_EXIST, name));
        }

        synchronized (this) {
            LongMessageRuleMap<Integer, LongMessageRule> newLongMessageRule = new LongMessageRuleMap<>();
            newLongMessageRule.putAll(this.longMessageRules);
            newLongMessageRule.remove(id);
            this.longMessageRules = newLongMessageRule;
            this.store();
        }
    }

    public void addMtp3Destination(int sapId, int destId, int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask)
            throws Exception {
        Mtp3ServiceAccessPoint sap = this.getMtp3ServiceAccessPoint(sapId);
        if (sap == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }
        // TODO Synchronize??
        sap.addMtp3Destination(destId, firstDpc, lastDpc, firstSls, lastSls, slsMask);
        this.store();
    }

    public void modifyMtp3Destination(int sapId, int destId, int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask)
            throws Exception {
        Mtp3ServiceAccessPoint sap = this.getMtp3ServiceAccessPoint(sapId);

        if (sap == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }
        Mtp3DestinationImpl dest = (Mtp3DestinationImpl) sap.getMtp3Destination(destId);

        if(dest == null)
            throw new Exception(String.format(SccpOAMMessage.DEST_DOESNT_EXIST, name));
        if(firstDpc == -99)
            firstDpc = dest.getFirstDpc();
        if(lastDpc == -99)
            lastDpc = dest.getLastDpc();
        if(firstSls == -99)
            firstSls = dest.getFirstSls();
        if(lastSls == -99)
            lastSls = dest.getLastSls();
        if(slsMask == -99)
            slsMask = dest.getSlsMask();

        sap.modifyMtp3Destination(destId, firstDpc, lastDpc, firstSls, lastSls, slsMask);
        this.store();
    }

    public void removeMtp3Destination(int sapId, int destId) throws Exception {
        Mtp3ServiceAccessPoint sap = this.getMtp3ServiceAccessPoint(sapId);

        if (sap == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }

        sap.removeMtp3Destination(destId);
        this.store();
    }

    public void addMtp3ServiceAccessPoint(int id, int mtp3Id, int opc, int ni, int networkId, String localGtDigits) throws Exception {
        if (this.getMtp3ServiceAccessPoint(id) != null) {
            throw new Exception(SccpOAMMessage.SAP_ALREADY_EXIST);
        }

        if (this.sccpStack.getMtp3UserPart(mtp3Id) == null) {
            throw new Exception(SccpOAMMessage.MUP_DOESNT_EXIST);
        }

        if (localGtDigits != null && (localGtDigits.equals("null") || localGtDigits.equals("")))
            localGtDigits = null;


        Mtp3ServiceAccessPointImpl sap = new Mtp3ServiceAccessPointImpl(mtp3Id, opc, ni, this.name, networkId, localGtDigits);
        synchronized (this) {
            Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> newSap = new Mtp3ServiceAccessPointMap<>();
            newSap.putAll(this.saps);
            newSap.put(id, sap);
            this.saps = newSap;
            this.store();
        }
    }

    public void modifyMtp3ServiceAccessPoint(int id, int mtp3Id, int opc, int ni, int networkId, String localGtDigits) throws Exception {
        Mtp3ServiceAccessPointImpl sap = (Mtp3ServiceAccessPointImpl) this.getMtp3ServiceAccessPoint(id);
        if (sap == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }

        if(mtp3Id == -99)
            mtp3Id = sap.getMtp3Id();
        if(opc == -99)
            opc = sap.getOpc();
        if(ni == -99)
            ni = sap.getNi();
        if(networkId == -99)
            networkId = sap.getNetworkId();
        if(localGtDigits == null)
            localGtDigits = sap.getLocalGtDigits();

        if (this.sccpStack.getMtp3UserPart(mtp3Id) == null) {
            throw new Exception(SccpOAMMessage.MUP_DOESNT_EXIST);
        }

        if (localGtDigits != null && (localGtDigits.equals("null") || localGtDigits.equals("")))
            localGtDigits = null;

        Mtp3ServiceAccessPointImpl newSap = new Mtp3ServiceAccessPointImpl(mtp3Id, opc, ni, this.name, networkId, localGtDigits);
        synchronized (this) {
            Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> newSaps = new Mtp3ServiceAccessPointMap<>();
            newSaps.putAll(this.saps);
            newSaps.put(id, newSap);
            this.saps = newSaps;
            this.store();
        }
    }

    public void modifyMtp3ServiceAccessPointOld(int id, Integer mtp3Id, Integer opc, Integer ni, Integer networkId, String localGtDigits) throws Exception {
        Mtp3ServiceAccessPointImpl sap = (Mtp3ServiceAccessPointImpl) this.getMtp3ServiceAccessPoint(id);
        if (sap == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }

        if (mtp3Id != null && this.sccpStack.getMtp3UserPart(mtp3Id) == null) {
            throw new Exception(SccpOAMMessage.MUP_DOESNT_EXIST);
        }

        if (localGtDigits != null && (localGtDigits.equals("null") || localGtDigits.equals("")))
            localGtDigits = null;

        if(mtp3Id == null)
            mtp3Id = sap.getMtp3Id();
        if(opc == null)
            opc = sap.getOpc();
        if(ni == null)
            ni = sap.getNi();
        if(networkId == null)
            networkId = sap.getNetworkId();
        if(localGtDigits == null)
            localGtDigits = sap.getLocalGtDigits();

        Mtp3ServiceAccessPointImpl newSap = new Mtp3ServiceAccessPointImpl(mtp3Id, opc, ni, this.name, networkId, localGtDigits);

        synchronized (this) {
            Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> newSaps = new Mtp3ServiceAccessPointMap<>();
            newSaps.putAll(this.saps);
            newSaps.put(id, newSap);
            this.saps = newSaps;
            this.store();
            this.store();
        }
    }


    public void removeMtp3ServiceAccessPoint(int id) throws Exception {
        if (this.getMtp3ServiceAccessPoint(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.SAP_DOESNT_EXIST, name));
        }

        synchronized (this) {
            Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> newSap = new Mtp3ServiceAccessPointMap<>();
            newSap.putAll(this.saps);
            newSap.remove(id);
            this.saps = newSap;
            this.store();
        }
    }

    public void removeAllResources() {

        synchronized (this) {
            if (this.longMessageRules.size() == 0 && this.saps.size() == 0)
                // no resources allocated - nothing to do
                return;

            longMessageRules = new LongMessageRuleMap<>();
            saps = new Mtp3ServiceAccessPointMap<>();

            // We store the cleared state
            this.store();
        }
    }

    /**
     * Persist
     */
    public void store() {
        try {
            javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory.newInstance();
            javax.xml.stream.XMLStreamWriter writer = factory.createXMLStreamWriter(new FileWriter(this.persistFile));
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeStartElement("RouterConfig");
            writer.writeCharacters("\n");

            writer.writeStartElement("longMessageRules");
            writer.writeCharacters("\n");
            for (Map.Entry<Integer, LongMessageRule> e : this.longMessageRules.entrySet()) {
                LongMessageRuleImpl v = (LongMessageRuleImpl) e.getValue();
                writer.writeStartElement("longMessageRule");
                writer.writeAttribute("id", String.valueOf(e.getKey()));
                writer.writeAttribute("firstSpc", String.valueOf(v.getFirstSpc()));
                writer.writeAttribute("lastSpc", String.valueOf(v.getLastSpc()));
                writer.writeAttribute("ruleType", v.getLongMessageRuleType().toString());
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");

            writer.writeStartElement("saps");
            writer.writeCharacters("\n");
            for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
                Mtp3ServiceAccessPointImpl v = (Mtp3ServiceAccessPointImpl) e.getValue();
                writer.writeStartElement("mtp3ServiceAccessPoint");
                writer.writeAttribute("id", String.valueOf(e.getKey()));
                writer.writeAttribute("mtp3Id", String.valueOf(v.getMtp3Id()));
                writer.writeAttribute("opc", String.valueOf(v.getOpc()));
                writer.writeAttribute("ni", String.valueOf(v.getNi()));
                writer.writeAttribute("networkId", String.valueOf(v.getNetworkId()));
                String localGt = v.getLocalGtDigits();
                if (localGt != null) {
                    writer.writeAttribute("localGtDigits", localGt);
                }
                writer.writeCharacters("\n");
                writer.writeStartElement("mtp3DestinationMap");
                writer.writeCharacters("\n");
                for (Map.Entry<Integer, Mtp3Destination> de : v.getMtp3Destinations().entrySet()) {
                    Mtp3DestinationImpl d = (Mtp3DestinationImpl) de.getValue();
                    writer.writeStartElement("mtp3Destination");
                    writer.writeAttribute("id", String.valueOf(de.getKey()));
                    writer.writeAttribute("firstDpc", String.valueOf(d.getFirstDpc()));
                    writer.writeAttribute("lastDpc", String.valueOf(d.getLastDpc()));
                    writer.writeAttribute("firstSls", String.valueOf(d.getFirstSls()));
                    writer.writeAttribute("lastSls", String.valueOf(d.getLastSls()));
                    writer.writeAttribute("slsMask", String.valueOf(d.getSlsMask()));
                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }
                writer.writeEndElement();
                writer.writeCharacters("\n");
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");

            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            writer.close();
        } catch (Exception e) {
            logger.error("Error while persisting the Rule state in file", e);
        }
    }

    /**
     * Load and create LinkSets and Link from persisted file
     */
    public void load() {
        try {
            File f = new File(this.persistFile);
            if (f.exists()) {
                loadVer4(this.persistFile);
            } else {
                String s1 = this.persistFile.replace("4.xml", "3.xml");
                f = new File(s1);
                if (f.exists()) {
                    logger.warn("Legacy SCCP Router config format (v3) not supported, using defaults");
                } else {
                    s1 = this.persistFile.replace("4.xml", "2.xml");
                    f = new File(s1);
                    if (f.exists()) {
                        logger.warn("Legacy SCCP Router config format (v2) not supported, using defaults");
                    } else {
                        s1 = this.persistFile.replace("4.xml", ".xml");
                        f = new File(s1);
                        if (f.exists()) {
                            logger.warn("Legacy SCCP Router config format (v1) not supported, using defaults");
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
        }
    }

    protected void loadVer4(String fn) throws FileNotFoundException {
        try (FileReader reader = new FileReader(fn)) {
            loadVer4(reader);
        } catch (IOException e) {
            logger.error(String.format("Failed to close FileReader for %s", fn), e);
        }
    }

    protected void loadVer4(FileReader reader) throws FileNotFoundException {
        try {
            javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
            factory.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, false);
            javax.xml.stream.XMLStreamReader r = factory.createXMLStreamReader(reader);
            LongMessageRuleMap<Integer, LongMessageRule> newLongMessageRules = new LongMessageRuleMap<>();
            Mtp3ServiceAccessPointMap<Integer, Mtp3ServiceAccessPoint> newSaps = new Mtp3ServiceAccessPointMap<>();

            while (r.hasNext()) {
                int event = r.next();
                if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT) {
                    String name = r.getLocalName();
                    if ("longMessageRules".equals(name)) {
                        while (r.hasNext()) {
                            event = r.next();
                            if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT && "longMessageRules".equals(r.getLocalName())) break;
                            if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT && "longMessageRule".equals(r.getLocalName())) {
                                int id = Integer.parseInt(r.getAttributeValue(null, "id"));
                                int firstSpc = Integer.parseInt(r.getAttributeValue(null, "firstSpc"));
                                int lastSpc = Integer.parseInt(r.getAttributeValue(null, "lastSpc"));
                                LongMessageRuleType ruleType = LongMessageRuleType.valueOf(r.getAttributeValue(null, "ruleType"));
                                LongMessageRuleImpl v = new LongMessageRuleImpl(firstSpc, lastSpc, ruleType);
                                newLongMessageRules.put(id, v);
                            }
                        }
                    } else if ("saps".equals(name)) {
                        while (r.hasNext()) {
                            event = r.next();
                            if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT && "saps".equals(r.getLocalName())) break;
                            if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT && "mtp3ServiceAccessPoint".equals(r.getLocalName())) {
                                int id = Integer.parseInt(r.getAttributeValue(null, "id"));
                                int mtp3Id = Integer.parseInt(r.getAttributeValue(null, "mtp3Id"));
                                int opc = Integer.parseInt(r.getAttributeValue(null, "opc"));
                                int ni = Integer.parseInt(r.getAttributeValue(null, "ni"));
                                int networkId = Integer.parseInt(r.getAttributeValue(null, "networkId"));
                                String localGtDigits = r.getAttributeValue(null, "localGtDigits");
                                Mtp3ServiceAccessPointImpl v = new Mtp3ServiceAccessPointImpl(mtp3Id, opc, ni, null, networkId, localGtDigits);
                                while (r.hasNext()) {
                                    event = r.next();
                                    if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT && "mtp3ServiceAccessPoint".equals(r.getLocalName())) break;
                                    if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT && "mtp3DestinationMap".equals(r.getLocalName())) {
                                        while (r.hasNext()) {
                                            event = r.next();
                                            if (event == javax.xml.stream.XMLStreamConstants.END_ELEMENT && "mtp3DestinationMap".equals(r.getLocalName())) break;
                                            if (event == javax.xml.stream.XMLStreamConstants.START_ELEMENT && "mtp3Destination".equals(r.getLocalName())) {
                                                int destId = Integer.parseInt(r.getAttributeValue(null, "id"));
                                                int firstDpc = Integer.parseInt(r.getAttributeValue(null, "firstDpc"));
                                                int lastDpc = Integer.parseInt(r.getAttributeValue(null, "lastDpc"));
                                                int firstSls = Integer.parseInt(r.getAttributeValue(null, "firstSls"));
                                                int lastSls = Integer.parseInt(r.getAttributeValue(null, "lastSls"));
                                                int slsMask = Integer.parseInt(r.getAttributeValue(null, "slsMask"));
                                                try {
                                                    v.addMtp3Destination(destId, firstDpc, lastDpc, firstSls, lastSls, slsMask);
                                                } catch (Exception ex) {
                                                    logger.error("Failed to add destination", ex);
                                                }
                                            }
                                        }
                                    }
                                }
                                newSaps.put(id, v);
                            }
                        }
                    }
                }
            }
            r.close();
            this.longMessageRules = newLongMessageRules;
            this.saps = newSaps;
            for (Map.Entry<Integer, Mtp3ServiceAccessPoint> e : this.saps.entrySet()) {
                Mtp3ServiceAccessPoint sap = e.getValue();
                ((Mtp3ServiceAccessPointImpl)sap).setStackName(name);
            }
        } catch (Exception e) {
            logger.error("Failed to parse RouterConfig from XML", e);
        }
    }
}
