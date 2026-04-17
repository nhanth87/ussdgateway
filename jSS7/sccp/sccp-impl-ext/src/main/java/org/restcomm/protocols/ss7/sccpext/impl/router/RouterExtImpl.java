
package org.restcomm.protocols.ss7.sccpext.impl.router;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.jctools.maps.NonBlockingHashMap;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.indicator.AddressIndicator;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.RemoteSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.Router;
import org.restcomm.protocols.ss7.sccp.Rule;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.SccpStack;
import org.restcomm.protocols.ss7.sccp.impl.oam.SccpOAMMessage;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0001Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0010Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0011Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.NoGlobalTitle;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccpext.impl.congestion.NetworkIdStateImpl;
import org.restcomm.protocols.ss7.sccpext.impl.congestion.SccpCongestionControl;
import org.restcomm.protocols.ss7.sccpext.router.RouterExt;

/**
*
* @author Amit Bhayani
* @author sergey vetyutnev
*
*/
public class RouterExtImpl implements RouterExt {
    private static final Logger logger = Logger.getLogger(RouterImpl.class);

    private static final String SCCP_ROUTER_PERSIST_DIR_KEY = "sccprouter.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String PERSIST_FILE_NAME = "sccprouter3_ext.xml";

    private static final String RULE = "rule";
    private static final String ROUTING_ADDRESS = "routingAddress";

    private final StringBuilder persistFile = new StringBuilder();

    private String persistDir = null;

    private RuleComparatorFactory ruleComparatorFactory = null;
    // rule list
    private RuleMap<Integer, Rule> rulesMap = new RuleMap<Integer, Rule>();
    private SccpAddressMap<Integer, SccpAddressImpl> routingAddresses = new SccpAddressMap<Integer, SccpAddressImpl>();

    private final String name;
    private final SccpStack sccpStack;
    private final Router router;

    public RouterExtImpl(String name, SccpStack sccpStack, Router router) {
        this.name = name;
        this.sccpStack = sccpStack;
        this.router = router;
        this.ruleComparatorFactory = RuleComparatorFactory.getInstance("RuleComparatorFactory");
    }

    public String getName() {
        return name;
    }

    public String getPersistDir() {
        return persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
    }

    public void start() {
        this.persistFile.setLength(0);

        if (persistDir != null) {
            this.persistFile.append(persistDir).append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFile.append(System.getProperty(SCCP_ROUTER_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        }

        logger.info(String.format("SCCP RouterExt configuration file path %s", persistFile.toString()));

        this.load();

        logger.info("Started SCCP Router");
    }

    public void stop() {
        this.store();
    }

    /**
     * Looks up rule for translation.
     *
     * @param calledParty called party address
     * @return the rule with match to the called party address
     */
    public Rule findRule(SccpAddress calledParty, SccpAddress callingParty, boolean isMtpOriginated, int msgNetworkId) {

        for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
            Rule rule = e.getValue();
            if (rule.matches(calledParty, callingParty, isMtpOriginated, msgNetworkId)) {
                return rule;
            }
        }
        return null;
    }

    @Override
    public Rule getRule(int id) {
        return this.rulesMap.get(id);
    }

    @Override
    public SccpAddress getRoutingAddress(int id) {
        return this.routingAddresses.get(id);
    }

    @Override
    public Map<Integer, Rule> getRules() {
        Map<Integer, Rule> rulesMapTmp = new HashMap<Integer, Rule>();
        rulesMapTmp.putAll(rulesMap);
        return rulesMapTmp;
    }

    @Override
    public Map<Integer, SccpAddress> getRoutingAddresses() {
        Map<Integer, SccpAddress> routingAddressesTmp = new HashMap<Integer, SccpAddress>();
        routingAddressesTmp.putAll(routingAddresses);
        return routingAddressesTmp;
    }

    @Override
    public void addRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            int pAddressId, int sAddressId, Integer newCallingPartyAddressAddressId, int networkId, SccpAddress patternCallingAddress) throws Exception {

        Rule ruleTmp = this.getRule(id);

        if (ruleTmp != null) {
            throw new Exception(SccpOAMMessage.RULE_ALREADY_EXIST);
        }

        int maskumberOfSecs = (mask.split("/").length - 1);
        int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

        if (maskumberOfSecs != patternNumberOfSecs) {
            throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
        }

        SccpAddress pAddress = this.getRoutingAddress(pAddressId);
        if (pAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
        }

        int primAddNumberOfSecs = (pAddress.getGlobalTitle().getDigits().split("/").length - 1);
        if (maskumberOfSecs != primAddNumberOfSecs) {
            throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
        }

        if (sAddressId != -1) {
            SccpAddress sAddress = this.getRoutingAddress(sAddressId);
            if (sAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
            }

            int secAddNumberOfSecs = (sAddress.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != secAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
            }
        }

        if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
            throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
        }

        synchronized (this) {
            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size() + 1)];
            int count = 0;

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void modifyRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            int pAddressId, int sAddressId, Integer newCallingPartyAddressAddressId, int networkId, SccpAddress patternCallingAddress) throws Exception {
        synchronized (this) {
            Rule ruleTmp = this.getRule(id);

            if (ruleTmp == null) {
                throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
            }

            int maskumberOfSecs = (mask.split("/").length - 1);
            int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

            if (maskumberOfSecs != patternNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
            }

            SccpAddress pAddress = this.getRoutingAddress(pAddressId);

            if (pAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
            }
            int primAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != primAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
            }

            if (sAddressId != -1) {
                SccpAddress sAddress = this.getRoutingAddress(sAddressId);
                if (sAddress == null) {
                    throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
                }
                int secAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
                if (maskumberOfSecs != secAddNumberOfSecs) {
                    throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
                }
            }

            if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
                throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
            }

            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size())];
            int count = 0;

            // Remove the old rule so that it doesn't overwrite the new modifications
            this.removeRule( id );

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void modifyRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            Integer pAddressId, Integer sAddressId, Integer newCallingPartyAddressAddressId, Integer networkId, SccpAddress patternCallingAddress) throws Exception {
        synchronized (this) {
            Rule ruleTmp = this.getRule(id);

            if (ruleTmp == null) {
                throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
            }

            if(networkId == null)
                networkId = ruleTmp.getNetworkId();
            if(newCallingPartyAddressAddressId == null)
                newCallingPartyAddressAddressId = ruleTmp.getNewCallingPartyAddressId();
            if(sAddressId == null)
                sAddressId = ruleTmp.getSecondaryAddressId();
            if(pAddressId == null)
                pAddressId = ruleTmp.getPrimaryAddressId();
            if(mask == null)
                mask = ruleTmp.getMask();
            if(originationType == null)
                originationType = ruleTmp.getOriginationType();
            if(algo == null)
                algo = ruleTmp.getLoadSharingAlgorithm();
            if(ruleType == null)
                ruleType = ruleTmp.getRuleType();

            int maskumberOfSecs = (mask.split("/").length - 1);
            int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

            if (maskumberOfSecs != patternNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
            }

            SccpAddress pAddress = this.getRoutingAddress(pAddressId);

            if (pAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
            }
            int primAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != primAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
            }

            if (sAddressId != -1) {
                SccpAddress sAddress = this.getRoutingAddress(sAddressId);
                if (sAddress == null) {
                    throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
                }
                int secAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
                if (maskumberOfSecs != secAddNumberOfSecs) {
                    throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
                }
            }

            if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
                throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
            }

            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size())];
            int count = 0;

            // Remove the old rule so that it doesn't overwrite the new modifications
            this.removeRule( id );

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void removeRule(int id) throws Exception {

        if (this.getRule(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
        }

        synchronized (this) {
            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            newRule.putAll(this.rulesMap);
            newRule.remove(id);
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void addRoutingAddress(int primAddressId, SccpAddress primaryAddress) throws Exception {

        if (this.getRoutingAddress(primAddressId) != null) {
            throw new Exception(SccpOAMMessage.ADDRESS_ALREADY_EXIST);
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, (SccpAddressImpl) primaryAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    @Override
    public void modifyRoutingAddress(int primAddressId, SccpAddress primaryAddress) throws Exception {
        if (this.getRoutingAddress(primAddressId) == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, (SccpAddressImpl) primaryAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    @Override
    public void modifyRoutingAddress(int primAddressId, Integer ai, Integer pc, Integer ssnValue, Integer tt, Integer npValue,
            Integer naiValue, String digits) throws Exception {
        RoutingIndicator ri;
        GlobalTitle gt = null;
        int dpc;
        int ssn;

        SccpAddressImpl sccpAddress = (SccpAddressImpl) this.getRoutingAddress(primAddressId);

        if (sccpAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        if(ai != null) {
            AddressIndicator aiObj = new AddressIndicator(ai.byteValue(), SccpProtocolVersion.ITU);
            ri = aiObj.getRoutingIndicator();
        } else {
            ri = sccpAddress.getAddressIndicator().getRoutingIndicator();
        }

        if(pc != null) {
            dpc = pc;
        } else {
            dpc = sccpAddress.getSignalingPointCode();
        }

        if(ssnValue != null) {
            ssn = ssnValue;
        } else {
            ssn = sccpAddress.getSubsystemNumber();
        }

        if(tt != null || npValue != null || naiValue != null || digits != null) {
            gt = modifyGt(tt, npValue, naiValue, digits, sccpAddress);
        } else {
            gt = sccpAddress.getGlobalTitle();
        }
        SccpAddressImpl modifiedSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, modifiedSccpAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    public SccpAddress modifySccpAddress(SccpAddress sccpAddress, Integer ai, Integer pc, Integer ssnValue, Integer tt, Integer npValue,
            Integer naiValue, String digits) throws Exception {
        RoutingIndicator ri;
        GlobalTitle gt = null;
        int dpc;
        int ssn;

        if (sccpAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        if(ai != null) {
            AddressIndicator aiObj = new AddressIndicator(ai.byteValue(), SccpProtocolVersion.ITU);
            ri = aiObj.getRoutingIndicator();
        } else {
            ri = sccpAddress.getAddressIndicator().getRoutingIndicator();
        }

        if(pc != null) {
            dpc = pc;
        } else {
            dpc = sccpAddress.getSignalingPointCode();
        }

        if(ssnValue != null) {
            ssn = ssnValue;
        } else {
            ssn = sccpAddress.getSubsystemNumber();
        }

        if(tt != null || npValue != null || naiValue != null || digits != null) {
            gt = modifyGt(tt, npValue, naiValue, digits, sccpAddress);
        } else {
            gt = sccpAddress.getGlobalTitle();
        }
        return new SccpAddressImpl(ri, gt, dpc, ssn);
    }

    private GlobalTitle modifyGt(Integer ttValue, Integer npValue, Integer naiValue, String digits, SccpAddress sccpAddress) {

        GlobalTitle gt = null;

        if(digits == null)
            digits = sccpAddress.getGlobalTitle().getDigits();

        NumberingPlan np = null;
        NatureOfAddress nai = null;
        Integer tt = null;

        if (naiValue != null)
            nai = NatureOfAddress.valueOf(naiValue);

        if(npValue != null)
            np = NumberingPlan.valueOf(npValue);

        if(ttValue != null)
            tt = ttValue;

        switch (sccpAddress.getGlobalTitle().getGlobalTitleIndicator()) {
            case GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY:
                if(nai == null)
                    nai = ((GlobalTitle0001Impl)sccpAddress.getGlobalTitle()).getNatureOfAddress();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, nai);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY:
                if(tt == null)
                    tt = ((GlobalTitle0010Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME:
                if(np == null)
                    np = ((GlobalTitle0011Impl)sccpAddress.getGlobalTitle()).getNumberingPlan();
                if(tt == null)
                    tt = ((GlobalTitle0011Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt, np, null);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS:
                if(nai == null)
                    nai = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getNatureOfAddress();
                if(np == null)
                    np = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getNumberingPlan();
                if(tt == null)
                    tt = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt, np, null, nai);
                break;

            case NO_GLOBAL_TITLE_INCLUDED:
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits);
                break;
        }
        return gt;
    }

    @Override
    public void removeRoutingAddress(int id) throws Exception {
        if (this.getRoutingAddress(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.remove(id);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    public void removeAllResources() {

        synchronized (this) {
            if (this.rulesMap.size() == 0 && this.routingAddresses.size() == 0)
                // no resources allocated - nothing to do
                return;

            rulesMap = new RuleMap<Integer, Rule>();
            routingAddresses = new SccpAddressMap<Integer, SccpAddressImpl>();

            // We store the cleared state
            this.store();
        }
    }

    public NonBlockingHashMap<Integer, NetworkIdState> getNetworkIdStateList() {
        return getNetworkIdList(-1);
    }

    public NonBlockingHashMap<Integer, NetworkIdState> getNetworkIdList(int affectedPc) {
        NonBlockingHashMap<Integer, NetworkIdState> res = new NonBlockingHashMap<Integer, NetworkIdState>();

        for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
            Rule rule = e.getValue();
            NetworkIdStateImpl networkIdState = getRoutingAddressStatusForRoutingRule(rule, affectedPc);
            if (networkIdState != null) {
                NetworkIdState prevNetworkIdState = res.get(rule.getNetworkId());
                if (prevNetworkIdState != null) {
                    if (prevNetworkIdState.isAvailable()) {
                        if (networkIdState.isAvailable()) {
                            if (prevNetworkIdState.getCongLevel() < networkIdState.getCongLevel()) {
                                res.put(rule.getNetworkId(), networkIdState);
                            }
                        } else {
                            res.put(rule.getNetworkId(), networkIdState);
                        }
                    }
                } else {
                    res.put(rule.getNetworkId(), networkIdState);
                }
            }
        }

        return res;
    }

    private NetworkIdStateImpl getRoutingAddressStatusForRoutingRule(Rule rule, int affectedPc) {
        SccpAddress translationAddressPri = getRoutingAddress(rule.getPrimaryAddressId());
        NetworkIdStateImpl rspStatusPri = getRoutingAddressStatusForRoutingAddress(translationAddressPri, affectedPc);

        if (rule.getRuleType() == RuleType.DOMINANT || rule.getRuleType() == RuleType.LOADSHARED) {
            SccpAddress translationAddressSec = getRoutingAddress(rule.getSecondaryAddressId());
            NetworkIdStateImpl rspStatusSec = getRoutingAddressStatusForRoutingAddress(translationAddressSec, affectedPc);

            if (rspStatusPri.isAffectedByPc() || rspStatusSec.isAffectedByPc()) {
                if (rule.getRuleType() == RuleType.DOMINANT) {
                    if (rspStatusPri.isAvailable())
                        return rspStatusPri;

                    return rspStatusSec;
                }
                if (rule.getRuleType() == RuleType.LOADSHARED) {
                    if (rspStatusPri.isAvailable()) {
                        if (rspStatusSec.isAvailable()) {
                            if (rspStatusPri.getCongLevel() >= rspStatusSec.getCongLevel())
                                return rspStatusPri;
                            else
                                return rspStatusSec;
                        } else {
                            return rspStatusPri;
                        }
                    } else {
                        if (rspStatusSec.isAvailable()) {
                            return rspStatusSec;
                        } else {
                            // both are prohibited - we can select any response
                            return rspStatusPri;
                        }
                    }
                }
            } else {
                return null;
            }
        } else {
            if (rspStatusPri.isAffectedByPc())
                return rspStatusPri;
            else
                return null;
        }

        return null;
    }

    private NetworkIdStateImpl getRoutingAddressStatusForRoutingAddress(SccpAddress routingAddress, int affectedPc) {
        if (routingAddress != null && routingAddress.getAddressIndicator().isPCPresent()) {
            boolean affectedByPc = true;
            if ((affectedPc >= 0 && routingAddress.getSignalingPointCode() != affectedPc))
                affectedByPc = false;
            boolean spcIsLocal = router.spcIsLocal(routingAddress.getSignalingPointCode());
            if (spcIsLocal) {
                return new NetworkIdStateImpl(affectedByPc);
            }

            RemoteSignalingPointCode remoteSpc = sccpStack.getSccpResource().getRemoteSpcByPC(
                    routingAddress.getSignalingPointCode());
            if (remoteSpc == null) {
                return new NetworkIdStateImpl(affectedByPc);
            }
            if (remoteSpc.isRemoteSpcProhibited()) {
                return new NetworkIdStateImpl(false, affectedByPc);
            }
            int congLevel = SccpCongestionControl.generateSccpUserCongLevel(remoteSpc.getCurrentRestrictionLevel());
            if (congLevel > 0) {
                return new NetworkIdStateImpl(congLevel, affectedByPc);
            }
            return new NetworkIdStateImpl(affectedByPc);
        }

        // we return here value that this affectedPc does not affect this rule
        return new NetworkIdStateImpl(false);
    }

    /**
     * Persist
     */
    public void store() {
        try (FileWriter fw = new FileWriter(this.persistFile.toString())) {
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");

            // Write rulesMap
            sb.append("<rule>\n");
            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                sb.append("  <id value=\"").append(e.getKey()).append("\"/>\n");
                RuleImpl rule = (RuleImpl) e.getValue();
                sb.append("  <value ruleType=\"").append(escapeXml(rule.getRuleType().getValue())).append("\"");
                sb.append(" loadSharingAlgo=\"").append(escapeXml(rule.getLoadSharingAlgorithm().getValue())).append("\"");
                sb.append(" originatingType=\"").append(escapeXml(rule.getOriginationType().getValue())).append("\"");
                sb.append(" mask=\"").append(escapeXml(rule.getMask())).append("\"");
                sb.append(" paddress=\"").append(rule.getPrimaryAddressId()).append("\"");
                sb.append(" saddress=\"").append(rule.getSecondaryAddressId()).append("\"");
                sb.append(" networkId=\"").append(rule.getNetworkId()).append("\"");
                if (rule.getNewCallingPartyAddressId() != null) {
                    sb.append(" ncpaddress=\"").append(rule.getNewCallingPartyAddressId()).append("\"");
                }
                sb.append(">\n");

                writeSccpAddress(sb, (SccpAddressImpl) rule.getPattern(), "patternSccpAddress", "    ");

                if (rule.getPatternCallingAddress() != null) {
                    writeSccpAddress(sb, (SccpAddressImpl) rule.getPatternCallingAddress(), "patternCallingAddress", "    ");
                }

                sb.append("  </value>\n");
            }
            sb.append("</rule>\n");

            // Write routingAddresses
            sb.append("<routingAddress>\n");
            for (Map.Entry<Integer, SccpAddressImpl> e : this.routingAddresses.entrySet()) {
                sb.append("  <id value=\"").append(e.getKey()).append("\"/>\n");
                writeSccpAddress(sb, e.getValue(), "sccpAddress", "    ");
            }
            sb.append("</routingAddress>\n");

            fw.write(sb.toString());
        } catch (Exception e) {
            logger.error("Error while persisting the Rule state in file", e);
        }
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Load and create LinkSets and Link from persisted file
     */
    protected void load() {
        try {
            File f = new File(persistFile.toString());
            if (f.exists()) {
                loadVer4(persistFile.toString());
            } else {
                String s1 = persistFile.toString().replace("3_ext.xml", "2_ext.xml");
                f = new File(s1);
                if (f.exists()) {
                    logger.warn("Legacy SCCP RouterExt config format v2 not supported, using defaults");
                } else {
                    s1 = persistFile.toString().replace("3_ext.xml", "_ext.xml");
                    f = new File(s1);
                    if (f.exists()) {
                        logger.warn("Legacy SCCP RouterExt config format v1 not supported, using defaults");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
        }
    }

    private void moveBackupToRoutingAddress(SccpAddressMap<Integer, SccpAddress> backupAddresses) {
        NonBlockingHashMap<Integer, Integer> lstChange = new NonBlockingHashMap<Integer, Integer>();
        for (Integer bId : backupAddresses.keySet()) {
            SccpAddress addr = backupAddresses.get(bId);

            int i1 = bId + 100;
            while (true) {
                if (routingAddresses.get(i1) == null)
                    break;
                i1++;
            }
            routingAddresses.put(i1, (SccpAddressImpl) addr);
            lstChange.put(bId, i1);
        }

        for (Rule rule : rulesMap.values()) {
            Integer newVal = lstChange.get(rule.getSecondaryAddressId());
            if (newVal != null) {
                ((RuleImpl) rule).setSecondaryAddressId(newVal);
            }
        }
    }

    protected void loadVer4(String fn) throws FileNotFoundException {
        try {
            String content = new String(Files.readAllBytes(new File(fn).toPath()));
            // Javolution may produce multiple root elements; wrap to make valid XML
            if (!content.trim().startsWith("<?xml")) {
                content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>" + content + "</root>";
            } else {
                int idx = content.indexOf("?>");
                if (idx != -1) {
                    content = content.substring(0, idx + 2) + "\n<root>" + content.substring(idx + 2) + "</root>";
                } else {
                    content = "<root>" + content + "</root>";
                }
            }
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
            loadVer4(reader);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse RouterConfig from XML for " + fn, e);
        }
    }

    protected void loadVer4(XMLStreamReader reader) throws Exception {
        rulesMap = new RuleMap<Integer, Rule>();
        routingAddresses = new SccpAddressMap<Integer, SccpAddressImpl>();

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String localName = reader.getLocalName();
                if ("rule".equals(localName)) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLStreamConstants.END_ELEMENT && "rule".equals(reader.getLocalName())) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT && "id".equals(reader.getLocalName())) {
                            Integer id = Integer.valueOf(reader.getAttributeValue(null, "value"));
                            RuleImpl rule = null;
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLStreamConstants.START_ELEMENT && "value".equals(reader.getLocalName())) {
                                    rule = readRule(reader);
                                    break;
                                }
                            }
                            if (rule != null) {
                                rule.setRuleId(id);
                                rule.configure();
                                rulesMap.put(id, rule);
                            }
                        }
                    }
                } else if ("routingAddress".equals(localName)) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLStreamConstants.END_ELEMENT && "routingAddress".equals(reader.getLocalName())) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT && "id".equals(reader.getLocalName())) {
                            Integer id = Integer.valueOf(reader.getAttributeValue(null, "value"));
                            SccpAddressImpl addr = null;
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLStreamConstants.START_ELEMENT && "sccpAddress".equals(reader.getLocalName())) {
                                    addr = readSccpAddress(reader, "sccpAddress");
                                    break;
                                }
                            }
                            if (addr != null) {
                                routingAddresses.put(id, addr);
                            }
                        }
                    }
                }
            }
        }
    }

    private void writeSccpAddress(StringBuilder sb, SccpAddressImpl addr, String tagName, String indent) {
        sb.append(indent).append("<").append(tagName).append(" pc=\"").append(addr.getSignalingPointCode()).append("\"");
        sb.append(" ssn=\"").append(addr.getSubsystemNumber()).append("\">\n");

        sb.append(indent).append("  <ai value=\"").append(addr.getAddressIndicator().getValue(SccpProtocolVersion.ITU)).append("\"/>\n");

        GlobalTitle gt = addr.getGlobalTitle();
        if (gt != null) {
            sb.append(indent).append("  <gt");
            String gtType;
            switch (gt.getGlobalTitleIndicator()) {
                case GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY:
                    gtType = "GT0001";
                    break;
                case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY:
                    gtType = "GT0010";
                    break;
                case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME:
                    gtType = "GT0011";
                    break;
                case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS:
                    gtType = "GT0100";
                    break;
                default:
                    gtType = "NoGlobalTitle";
                    break;
            }
            sb.append(" type=\"").append(gtType).append("\"");
            if (gt instanceof org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0100) {
                org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0100 g = (org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0100) gt;
                sb.append(" tt=\"").append(g.getTranslationType()).append("\"");
                sb.append(" es=\"").append(g.getEncodingScheme().getSchemeCode()).append("\"");
                sb.append(" np=\"").append(g.getNumberingPlan().getValue()).append("\"");
                sb.append(" nai=\"").append(g.getNatureOfAddress().getValue()).append("\"");
            } else if (gt instanceof org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0011) {
                org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0011 g = (org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0011) gt;
                sb.append(" tt=\"").append(g.getTranslationType()).append("\"");
                sb.append(" es=\"").append(g.getEncodingScheme().getSchemeCode()).append("\"");
                sb.append(" np=\"").append(g.getNumberingPlan().getValue()).append("\"");
            } else if (gt instanceof org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0010) {
                org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0010 g = (org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0010) gt;
                sb.append(" tt=\"").append(g.getTranslationType()).append("\"");
            } else if (gt instanceof org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0001) {
                org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0001 g = (org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0001) gt;
                sb.append(" nai=\"").append(g.getNatureOfAddress().getValue()).append("\"");
            }
            sb.append(" digits=\"").append(escapeXml(gt.getDigits())).append("\"/>\n");
        }
        sb.append(indent).append("</").append(tagName).append(">\n");
    }

    private SccpAddressImpl readSccpAddress(XMLStreamReader reader, String tagName) throws Exception {
        int pc = Integer.parseInt(reader.getAttributeValue(null, "pc"));
        int ssn = Integer.parseInt(reader.getAttributeValue(null, "ssn"));
        int aiValue = 0;
        GlobalTitle gt = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && tagName.equals(reader.getLocalName())) {
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();
                if ("ai".equals(name)) {
                    aiValue = Integer.parseInt(reader.getAttributeValue(null, "value"));
                } else if ("gt".equals(name)) {
                    gt = readGlobalTitle(reader);
                }
            }
        }

        AddressIndicator ai = new AddressIndicator((byte) aiValue, SccpProtocolVersion.ITU);
        SccpAddressImpl addr = new SccpAddressImpl(ai.getRoutingIndicator(), gt, pc, ssn);
        return addr;
    }

    private org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme encodingSchemeFromCode(int code) {
        switch (code) {
            case 1: return org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme.INSTANCE;
            case 2: return org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme.INSTANCE;
            default: return org.restcomm.protocols.ss7.sccp.impl.parameter.DefaultEncodingScheme.INSTANCE;
        }
    }

    private GlobalTitle readGlobalTitle(XMLStreamReader reader) throws Exception {
        String type = reader.getAttributeValue(null, "type");
        String digits = reader.getAttributeValue(null, "digits");
        if (digits == null) digits = "";

        GlobalTitle gt = null;
        if ("GT0100".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            int es = Integer.parseInt(reader.getAttributeValue(null, "es"));
            int np = Integer.parseInt(reader.getAttributeValue(null, "np"));
            int nai = Integer.parseInt(reader.getAttributeValue(null, "nai"));
            gt = new GlobalTitle0100Impl(digits, tt, encodingSchemeFromCode(es), NumberingPlan.valueOf(np), NatureOfAddress.valueOf(nai));
        } else if ("GT0011".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            int es = Integer.parseInt(reader.getAttributeValue(null, "es"));
            int np = Integer.parseInt(reader.getAttributeValue(null, "np"));
            gt = new GlobalTitle0011Impl(digits, tt, encodingSchemeFromCode(es), NumberingPlan.valueOf(np));
        } else if ("GT0010".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            gt = new GlobalTitle0010Impl(digits, tt);
        } else if ("GT0001".equals(type)) {
            int nai = Integer.parseInt(reader.getAttributeValue(null, "nai"));
            gt = new GlobalTitle0001Impl(digits, NatureOfAddress.valueOf(nai));
        } else if ("NoGlobalTitle".equals(type)) {
            gt = new NoGlobalTitle();
        }

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && "gt".equals(reader.getLocalName())) {
                break;
            }
        }
        return gt;
    }

    private RuleImpl readRule(XMLStreamReader reader) throws Exception {
        RuleType ruleType = RuleType.getInstance(reader.getAttributeValue(null, "ruleType"));
        LoadSharingAlgorithm loadSharingAlgo = LoadSharingAlgorithm.getInstance(reader.getAttributeValue(null, "loadSharingAlgo"));
        OriginationType originationType = OriginationType.getInstance(reader.getAttributeValue(null, "originatingType"));
        String mask = reader.getAttributeValue(null, "mask");
        int pAddressId = Integer.parseInt(reader.getAttributeValue(null, "paddress"));
        int sAddressId = Integer.parseInt(reader.getAttributeValue(null, "saddress"));
        int networkId = Integer.parseInt(reader.getAttributeValue(null, "networkId"));
        String ncp = reader.getAttributeValue(null, "ncpaddress");
        Integer newCallingPartyAddressAddressId = ncp != null ? Integer.valueOf(ncp) : null;

        SccpAddress pattern = null;
        SccpAddress patternCallingAddress = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && "value".equals(reader.getLocalName())) {
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();
                if ("patternSccpAddress".equals(name)) {
                    pattern = readSccpAddress(reader, "patternSccpAddress");
                } else if ("patternCallingAddress".equals(name)) {
                    patternCallingAddress = readSccpAddress(reader, "patternCallingAddress");
                }
            }
        }

        RuleImpl rule = new RuleImpl(ruleType, loadSharingAlgo, originationType, pattern, mask, networkId, patternCallingAddress);
        rule.setPrimaryAddressId(pAddressId);
        rule.setSecondaryAddressId(sAddressId);
        rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);
        return rule;
    }

    public static void makeOldConfigCopy(String persistDir, String name) {
        StringBuilder persistFile = new StringBuilder();

        if (persistDir != null) {
            persistFile.append(persistDir).append(File.separator).append(name).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFile.append(System.getProperty(SCCP_ROUTER_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(name).append("_").append(PERSIST_FILE_NAME);
        }

        String s1 = persistFile.toString().replace("3_ext.xml", "2.xml");
        File f1 = new File(s1);
        if (f1.exists()) {
            String s2 = persistFile.toString().replace("3_ext.xml", "2_ext.xml");
            File f2 = new File(s2);
            try {
                Files.copy(f1.toPath(), f2.toPath());
            } catch (Exception e) {
                // we ignore errors here
            }
        }
    }
}
