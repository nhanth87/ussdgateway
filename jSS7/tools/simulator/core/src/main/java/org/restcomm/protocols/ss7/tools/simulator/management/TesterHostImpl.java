
package org.restcomm.protocols.ss7.tools.simulator.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import java.io.StringWriter;
import java.io.StringReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPart;
import org.restcomm.protocols.ss7.sccp.SccpStack;
import org.restcomm.protocols.ss7.tools.simulator.Stoppable;
import org.restcomm.protocols.ss7.tools.simulator.common.AddressNatureType;
import org.restcomm.protocols.ss7.tools.simulator.common.ConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicMan;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaMan;
import org.restcomm.protocols.ss7.tools.simulator.level2.NatureOfAddressType;
import org.restcomm.protocols.ss7.tools.simulator.level2.NumberingPlanSccpType;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpMan;
import org.restcomm.protocols.ss7.tools.simulator.level3.CapMan;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapMan;
import org.restcomm.protocols.ss7.tools.simulator.level3.NumberingPlanMapType;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiClientMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiServerMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapScfMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapSsfMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiClientMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiServerMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsClientMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsServerMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.psi.TestPsiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.psi.TestPsiServerMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.NumberingPlanIdentificationType;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TypeOfNumberType;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientMan;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerMan;


/**
 *
 * @author sergey vetyutnev
 *
 */
public class TesterHostImpl extends NotificationBroadcasterSupport implements TesterHostInterface, Stoppable {
    private static final Logger logger = Logger.getLogger(TesterHostImpl.class);

    private static final String TESTER_HOST_PERSIST_DIR_KEY = "testerhost.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    public static String SOURCE_NAME = "HOST";
    public static String SS7_EVENT = "SS7Event";

    private static final String CLASS_ATTRIBUTE = "type";
    private static final String TAB_INDENT = "\t";
    private static final String PERSIST_FILE_NAME_OLD = "simulator.xml";
    private static final String PERSIST_FILE_NAME = "simulator2.xml";
    private static final String CONFIGURATION_DATA = "configurationData";

    private final String appName;
    private String persistDir = null;
    private final StringBuilder persistFile = new StringBuilder();

    // SETTINGS
    private boolean isStarted = false;
    private boolean needQuit = false;
    private boolean needStore = false;
    private ConfigurationData configurationData = new ConfigurationData();
    private long sequenceNumber = 0;

    // Layers
    private Stoppable instance_L1_B = null;
    private Stoppable instance_L2_B = null;
    private Stoppable instance_L3_B = null;
    private Stoppable instance_TestTask_B = null;

    // levels
    M3uaMan m3ua;
    DialogicMan dialogic;
    SccpMan sccp;
    MapMan map;
    CapMan cap;
    TestUssdClientMan testUssdClientMan;
    TestUssdServerMan testUssdServerMan;
    TestSmsClientMan testSmsClientMan;
    TestSmsServerMan testSmsServerMan;
    TestCapSsfMan testCapSsfMan;
    TestCapScfMan testCapScfMan;
    TestAtiClientMan testAtiClientMan;
    TestAtiServerMan testAtiServerMan;
    TestCheckImeiClientMan testCheckImeiClientMan;
    TestCheckImeiServerMan testCheckImeiServerMan;
    TestLcsClientMan testLcsClientMan;
    TestLcsServerMan testLcsServerMan;
    TestPsiServerMan testPsiServerMan;

    // testers

    protected SccpMan createSccpMan() {
        return new SccpMan(appName);
    }

    protected M3uaMan createM3uaMan() {
        return new M3uaMan(appName);
    }

    protected DialogicMan createDialogicMan() {
        return new DialogicMan(appName);
    }

    protected void initiateExt() {
    }

    public TesterHostImpl(String appName, String persistDir) {
        initiateExt();

        this.appName = appName;
        this.persistDir = persistDir;

        this.m3ua = createM3uaMan();
        this.m3ua.setTesterHost(this);

        this.dialogic = createDialogicMan();
        this.dialogic.setTesterHost(this);

        this.sccp = createSccpMan();
        this.sccp.setTesterHost(this);

        this.map = new MapMan(appName);
        this.map.setTesterHost(this);

        this.cap = new CapMan(appName);
        this.cap.setTesterHost(this);

        this.testUssdClientMan = new TestUssdClientMan(appName);
        this.testUssdClientMan.setTesterHost(this);

        this.testUssdServerMan = new TestUssdServerMan(appName);
        this.testUssdServerMan.setTesterHost(this);

        this.testSmsClientMan = new TestSmsClientMan(appName);
        this.testSmsClientMan.setTesterHost(this);

        this.testSmsServerMan = new TestSmsServerMan(appName);
        this.testSmsServerMan.setTesterHost(this);

        this.testCapSsfMan = new TestCapSsfMan(appName);
        this.testCapSsfMan.setTesterHost(this);

        this.testCapScfMan = new TestCapScfMan(appName);
        this.testCapScfMan.setTesterHost(this);

        this.testAtiClientMan = new TestAtiClientMan(appName);
        this.testAtiClientMan.setTesterHost(this);

        this.testAtiServerMan = new TestAtiServerMan(appName);
        this.testAtiServerMan.setTesterHost(this);

        this.testCheckImeiClientMan = new TestCheckImeiClientMan(appName);
        this.testCheckImeiClientMan.setTesterHost(this);

        this.testCheckImeiServerMan = new TestCheckImeiServerMan(appName);
        this.testCheckImeiServerMan.setTesterHost(this);

        this.testLcsClientMan = new TestLcsClientMan(appName);
        this.testLcsClientMan.setTesterHost(this);

        this.testLcsServerMan = new TestLcsServerMan(appName);
        this.testLcsServerMan.setTesterHost(this);

        this.testPsiServerMan = new TestPsiServerMan(appName);
        this.testPsiServerMan.setTesterHost(this);

        this.setupLog4j(appName);

        this.persistFile.setLength(0);
        StringBuilder persistFileOld = new StringBuilder();

        if (persistDir != null) {
            persistFileOld.append(persistDir).append(File.separator).append(this.appName).append("_")
                    .append(PERSIST_FILE_NAME_OLD);
            this.persistFile.append(persistDir).append(File.separator).append(this.appName).append("_")
                    .append(PERSIST_FILE_NAME);
        } else {
            persistFileOld.append(System.getProperty(TESTER_HOST_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(this.appName).append("_").append(PERSIST_FILE_NAME_OLD);
            this.persistFile.append(System.getProperty(TESTER_HOST_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(this.appName).append("_").append(PERSIST_FILE_NAME);
        }

        File fnOld = new File(persistFileOld.toString());
        File fn = new File(persistFile.toString());

        if (this.loadOld(fnOld)) {
            this.store();
        } else {
            this.load(fn);
        }
        if (fnOld.exists())
            fnOld.delete();

    }

    public ConfigurationData getConfigurationData() {
        return this.configurationData;
    }

    public M3uaMan getM3uaMan() {
        return this.m3ua;
    }

    public DialogicMan getDialogicMan() {
        return this.dialogic;
    }

    public SccpMan getSccpMan() {
        return this.sccp;
    }

    public MapMan getMapMan() {
        return this.map;
    }

    public CapMan getCapMan() {
        return this.cap;
    }

    public TestUssdClientMan getTestUssdClientMan() {
        return this.testUssdClientMan;
    }

    public TestUssdServerMan getTestUssdServerMan() {
        return this.testUssdServerMan;
    }

    public TestSmsClientMan getTestSmsClientMan() {
        return this.testSmsClientMan;
    }

    public TestSmsServerMan getTestSmsServerMan() {
        return this.testSmsServerMan;
    }

    public TestCapSsfMan getTestCapSsfMan() {
        return this.testCapSsfMan;
    }

    public TestCapScfMan getTestCapScfMan() {
        return this.testCapScfMan;
    }

    public TestAtiClientMan getTestAtiClientMan() {
        return this.testAtiClientMan;
    }

    public TestAtiServerMan getTestAtiServerMan() {
        return this.testAtiServerMan;
    }

    public TestCheckImeiClientMan getTestCheckImeiClientMan() {
        return this.testCheckImeiClientMan;
    }

    public TestCheckImeiServerMan getTestCheckImeiServerMan() {
        return this.testCheckImeiServerMan;
    }

    public TestLcsClientMan getTestLcsClientMan() {
        return this.testLcsClientMan;
    }

    public TestLcsServerMan getTestLcsServerMan() {
        return this.testLcsServerMan;
    }

    public TestPsiServerMan getTestPsiServerMan() { return this.testPsiServerMan; }

    private void setupLog4j(String appName) {

        // InputStream inStreamLog4j = getClass().getResourceAsStream("/log4j.properties");

        String propFileName = appName + ".log4j.properties";
        File f = new File("./" + propFileName);
        if (f.exists()) {

            try {
                InputStream inStreamLog4j = new FileInputStream(f);
                Properties propertiesLog4j = new Properties();

                propertiesLog4j.load(inStreamLog4j);
                PropertyConfigurator.configure(propertiesLog4j);
            } catch (Exception e) {
                e.printStackTrace();
                BasicConfigurator.configure();
            }
        } else {
            BasicConfigurator.configure();
        }

        // logger.setLevel(Level.TRACE);
        logger.debug("log4j configured");

    }

    public void sendNotif(String source, String msg, Throwable e, Level logLevel) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement st : e.getStackTrace()) {
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(st.toString());
        }
        this.doSendNotif(source, msg + " - " + e.toString(), sb.toString());

        logger.log(logLevel, msg, e);
        // if (showInConsole) {
        // logger.error(msg, e);
        // } else {
        // logger.debug(msg, e);
        // }
    }

    public void sendNotif(String source, String msg, String userData, Level logLevel) {

        this.doSendNotif(source, msg, userData);

        logger.log(Level.INFO, msg + "\n" + userData);
//        logger.log(logLevel, msg + "\n" + userData);

        // if (showInConsole) {
        // logger.warn(msg);
        // } else {
        // logger.debug(msg);
        // }
    }

    private synchronized void doSendNotif(String source, String msg, String userData) {
        Notification notif = new Notification(SS7_EVENT + "-" + source, "TesterHost", ++sequenceNumber,
                System.currentTimeMillis(), msg);
        notif.setUserData(userData);
        this.sendNotification(notif);
    }

    public boolean isNeedQuit() {
        return needQuit;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public Instance_L1 getInstance_L1() {
        return configurationData.getInstance_L1();
    }

    @Override
    public void setInstance_L1(Instance_L1 val) {
        configurationData.setInstance_L1(val);
        this.markStore();
    }

    @Override
    public Instance_L2 getInstance_L2() {
        return configurationData.getInstance_L2();
    }

    @Override
    public void setInstance_L2(Instance_L2 val) {
        configurationData.setInstance_L2(val);
        this.markStore();
    }

    @Override
    public Instance_L3 getInstance_L3() {
        return configurationData.getInstance_L3();
    }

    @Override
    public void setInstance_L3(Instance_L3 val) {
        configurationData.setInstance_L3(val);
        this.markStore();
    }

    @Override
    public Instance_TestTask getInstance_TestTask() {
        return configurationData.getInstance_TestTask();
    }

    @Override
    public void setInstance_TestTask(Instance_TestTask val) {
        configurationData.setInstance_TestTask(val);
        this.markStore();
    }

    @Override
    public String getInstance_L1_Value() {
        return configurationData.getInstance_L1().toString();
    }

    @Override
    public String getInstance_L2_Value() {
        return configurationData.getInstance_L2().toString();
    }

    @Override
    public String getInstance_L3_Value() {
        return configurationData.getInstance_L3().toString();
    }

    @Override
    public String getInstance_TestTask_Value() {
        return configurationData.getInstance_TestTask().toString();
    }

    @Override
    public String getState() {
        return TesterHostImpl.SOURCE_NAME + ": " + (this.isStarted() ? "Started" : "Stopped");
    }

    @Override
    public String getL1State() {
        if (this.instance_L1_B != null)
            return this.instance_L1_B.getState();
        else
            return "";
    }

    @Override
    public String getL2State() {
        if (this.instance_L2_B != null)
            return this.instance_L2_B.getState();
        else
            return "";
    }

    @Override
    public String getL3State() {
        if (this.instance_L3_B != null)
            return this.instance_L3_B.getState();
        else
            return "";
    }

    @Override
    public String getTestTaskState() {
        if (this.instance_TestTask_B != null)
            return this.instance_TestTask_B.getState();
        else
            return "";
    }

    @Override
    public void start() {

        this.store();
        this.stop();

        // L1
        boolean started = false;
        Mtp3UserPart mtp3UserPart = null;
        switch (this.configurationData.getInstance_L1().intValue()) {
            case Instance_L1.VAL_M3UA:
                this.instance_L1_B = this.m3ua;
                started = this.m3ua.start();
                mtp3UserPart = this.m3ua.getMtp3UserPart();
                break;
            case Instance_L1.VAL_DIALOGIC:
                this.instance_L1_B = this.dialogic;
                started = this.dialogic.start();
                mtp3UserPart = this.dialogic.getMtp3UserPart();
                break;

            default:
                // TODO: implement others test tasks ...
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_L1." + this.configurationData.getInstance_L1().toString()
                        + " has not been implemented yet", "", Level.WARN);
                break;
        }
        if (!started) {
            this.sendNotif(TesterHostImpl.SOURCE_NAME, "Layer 1 has not started", "", Level.WARN);
            this.stop();
            return;
        }

        // L2
        started = false;
        SccpStack sccpStack = null;
        switch (this.configurationData.getInstance_L2().intValue()) {
            case Instance_L2.VAL_SCCP:
                if (mtp3UserPart == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing SCCP: No Mtp3UserPart is defined at L1", "",
                            Level.WARN);
                } else {
                    this.instance_L2_B = this.sccp;
                    this.sccp.setMtp3UserPart(mtp3UserPart);
                    started = this.sccp.start();
                    sccpStack = this.sccp.getSccpStack();
                }
                break;
            case Instance_L2.VAL_ISUP:
                // TODO Implement L2 = ISUP
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_L2.VAL_ISUP has not been implemented yet", "", Level.WARN);
                break;

            default:
                // TODO: implement others test tasks ...
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_L2." + this.configurationData.getInstance_L2().toString()
                        + " has not been implemented yet", "", Level.WARN);
                break;
        }
        if (!started) {
            this.sendNotif(TesterHostImpl.SOURCE_NAME, "Layer 2 has not started", "", Level.WARN);
            this.stop();
            return;
        }

        // L3
        started = false;
        MapMan curMap = null;
        CapMan curCap = null;
        switch (this.configurationData.getInstance_L3().intValue()) {
            case Instance_L3.VAL_MAP:
                if (sccpStack == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing TCAP+MAP: No SccpStack is defined at L2", "",
                            Level.WARN);
                } else {
                    this.instance_L3_B = this.map;
                    this.map.setSccpStack(sccpStack);
                    started = this.map.start();
                    curMap = this.map;
                }
                break;
            case Instance_L3.VAL_CAP:
                if (sccpStack == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing TCAP+CAP: No SccpStack is defined at L2", "",
                            Level.WARN);
                } else {
                    this.instance_L3_B = this.cap;
                    this.cap.setSccpStack(sccpStack);
                    started = this.cap.start();
                    curCap = this.cap;
                }
                break;
            case Instance_L3.VAL_INAP:
                // TODO: implement INAP .......
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_L3.VAL_INAP has not been implemented yet", "", Level.WARN);
                break;

            default:
                // TODO: implement others test tasks ...
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_L3." + this.configurationData.getInstance_L3().toString()
                        + " has not been implemented yet", "", Level.WARN);
                break;
        }
        if (!started) {
            this.sendNotif(TesterHostImpl.SOURCE_NAME, "Layer 3 has not started", "", Level.WARN);
            this.stop();
            return;
        }

        // Testers
        started = false;
        switch (this.configurationData.getInstance_TestTask().intValue()) {
            case Instance_TestTask.VAL_USSD_TEST_CLIENT:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME,
                            "Error initializing USSD_TEST_CLIENT: No MAP stack is defined at L3", "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testUssdClientMan;
                    this.testUssdClientMan.setMapMan(curMap);
                    started = this.testUssdClientMan.start();
                }
                break;

            case Instance_TestTask.VAL_USSD_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME,
                            "Error initializing USSD_TEST_SERVER: No MAP stack is defined at L3", "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testUssdServerMan;
                    this.testUssdServerMan.setMapMan(curMap);
                    started = this.testUssdServerMan.start();
                }
                break;

            case Instance_TestTask.VAL_SMS_TEST_CLIENT:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing SMS_TEST_CLIENT: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testSmsClientMan;
                    this.testSmsClientMan.setMapMan(curMap);
                    started = this.testSmsClientMan.start();
                }
                break;

            case Instance_TestTask.VAL_SMS_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing SMS_TEST_SERVER: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testSmsServerMan;
                    this.testSmsServerMan.setMapMan(curMap);
                    started = this.testSmsServerMan.start();
                }
                break;

            case Instance_TestTask.VAL_CAP_TEST_SCF:
                if (curCap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME,
                            "Error initializing VAL_CAP_TEST_SCF: No CAP stack is defined at L3", "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testCapScfMan;
                    this.testCapScfMan.setCapMan(curCap);
                    started = this.testCapScfMan.start();
                }
                break;

            case Instance_TestTask.VAL_CAP_TEST_SSF:
                if (curCap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME,
                            "Error initializing VAL_CAP_TEST_SSF: No CAP stack is defined at L3", "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testCapSsfMan;
                    this.testCapSsfMan.setCapMan(curCap);
                    started = this.testCapSsfMan.start();
                }
                break;

            case Instance_TestTask.VAL_ATI_TEST_CLIENT:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing ATI_TEST_CLIENT: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testAtiClientMan;
                    this.testAtiClientMan.setMapMan(curMap);
                    started = this.testAtiClientMan.start();
                }
                break;

            case Instance_TestTask.VAL_ATI_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing ATI_TEST_SERVER: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testAtiServerMan;
                    this.testAtiServerMan.setMapMan(curMap);
                    started = this.testAtiServerMan.start();
                }
                break;

            case Instance_TestTask.VAL_CHECK_IMEI_TEST_CLIENT:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing CHECK_IMEI_TEST_CLIENT: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testCheckImeiClientMan;
                    this.testCheckImeiClientMan.setMapMan(curMap);
                    started = this.testCheckImeiClientMan.start();
                }
                break;

            case Instance_TestTask.VAL_CHECK_IMEI_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing CHECK_IMEI_TEST_SERVER: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testCheckImeiServerMan;
                    this.testCheckImeiServerMan.setMapMan(curMap);
                    started = this.testCheckImeiServerMan.start();
                }
                break;

            case Instance_TestTask.VAL_MAP_LCS_TEST_CLIENT:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing MAP_LCS_TEST_SERVER: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testLcsClientMan;
                    this.testLcsClientMan.setMapMan(curMap);
                    started = this.testLcsClientMan.start();
                }
                break;

            case Instance_TestTask.VAL_MAP_LCS_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing MAP_LCS_TEST_CLIENT: No MAP stack is defined at L3",
                            "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testLcsServerMan;
                    this.testLcsServerMan.setMapMan(curMap);
                    started = this.testLcsServerMan.start();
                }
                break;

            case Instance_TestTask.VAL_PSI_TEST_SERVER:
                if (curMap == null) {
                    this.sendNotif(TesterHostImpl.SOURCE_NAME, "Error initializing MAP_PSI_TEST_SERVER: No MAP stack is defined at L3",
                        "", Level.WARN);
                } else {
                    this.instance_TestTask_B = this.testPsiServerMan;
                    this.testPsiServerMan.setMapMan(curMap);
                    started = this.testPsiServerMan.start();
                }
                break;

            default:
                // TODO: implement others test tasks ...
                this.sendNotif(TesterHostImpl.SOURCE_NAME, "Instance_TestTask."
                        + this.configurationData.getInstance_TestTask().toString() + " has not been implemented yet", "",
                        Level.WARN);
                break;
        }
        if (!started) {
            this.sendNotif(TesterHostImpl.SOURCE_NAME, "Testing task has not started", "", Level.WARN);
            this.stop();
            return;
        }

        this.isStarted = true;
    }

    @Override
    public void stop() {

        this.isStarted = false;

        // TestTask
        if (this.instance_TestTask_B != null) {
            this.instance_TestTask_B.stop();
            this.instance_TestTask_B = null;
        }

        // L3
        if (this.instance_L3_B != null) {
            this.instance_L3_B.stop();
            this.instance_L3_B = null;
        }

        // L2
        if (this.instance_L2_B != null) {
            this.instance_L2_B.stop();
            this.instance_L2_B = null;
        }

        // L1
        if (this.instance_L1_B != null) {
            this.instance_L1_B.stop();
            this.instance_L1_B = null;
        }
    }

    @Override
    public void execute() {
        if (this.instance_L1_B != null) {
            this.instance_L1_B.execute();
        }
        if (this.instance_L2_B != null) {
            this.instance_L2_B.execute();
        }
        if (this.instance_L3_B != null) {
            this.instance_L3_B.execute();
        }
        if (this.instance_TestTask_B != null) {
            this.instance_TestTask_B.execute();
        }
    }

    @Override
    public void quit() {
        this.stop();
        this.store();
        this.needQuit = true;
    }

    @Override
    public void putInstance_L1Value(String val) {
        Instance_L1 x = Instance_L1.createInstance(val);
        if (x != null)
            this.setInstance_L1(x);
    }

    @Override
    public void putInstance_L2Value(String val) {
        Instance_L2 x = Instance_L2.createInstance(val);
        if (x != null)
            this.setInstance_L2(x);
    }

    @Override
    public void putInstance_L3Value(String val) {
        Instance_L3 x = Instance_L3.createInstance(val);
        if (x != null)
            this.setInstance_L3(x);
    }

    @Override
    public void putInstance_TestTaskValue(String val) {
        Instance_TestTask x = Instance_TestTask.createInstance(val);
        if (x != null)
            this.setInstance_TestTask(x);
    }

    public String getName() {
        return appName;
    }

    public String getPersistDir() {
        return persistDir;
    }

//    public void setPersistDir(String persistDir) {
//        this.persistDir = persistDir;
//    }

    public void markStore() {
        needStore = true;
    }

    public void checkStore() {
        if (needStore) {
            needStore = false;
            this.store();
        }
    }

    public synchronized void store() {
        try {
            StringWriter writer = new StringWriter();
            ToolsJacksonXMLHelper.toXML(this.configurationData, writer);
            
            FileOutputStream fos = new FileOutputStream(persistFile.toString());
            fos.write(writer.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            this.sendNotif(SOURCE_NAME, "Error while persisting the Host state in file", e, Level.ERROR);
        }
    }

    private boolean load(File fn) {
        try {
            if (!fn.exists()) {
                this.sendNotif(SOURCE_NAME, "Error while reading the Host state from file: file not found: " + persistFile, "",
                        Level.WARN);
                return false;
            }

            FileInputStream fis = new FileInputStream(fn);
            byte[] buffer = new byte[(int) fn.length()];
            fis.read(buffer);
            fis.close();
            
            String xml = new String(buffer);
            this.configurationData = (ConfigurationData) ToolsJacksonXMLHelper.fromXML(xml);

            return true;

        } catch (Exception ex) {
            this.sendNotif(SOURCE_NAME, "Error while reading the Host state from file", ex, Level.WARN);
            return false;
        }
    }

    private boolean loadOld(File fn) {
        try {
            if (!fn.exists()) {
                return false;
            }

            // Legacy Javolution XMLFormat is incompatible with XStream
            // The old format used XMLObjectReader with custom XMLFormat classes
            // which cannot be directly parsed by XStream without custom converters.
            // Users should use the new format (simulator2.xml) instead.
            this.sendNotif(SOURCE_NAME, "Old format file " + fn.getName() + " cannot be loaded. " +
                    "Please use the new configuration format.", (Throwable) null, Level.WARN);
            return false;

        } catch (Exception ex) {
            this.sendNotif(SOURCE_NAME, "Error while reading the Host state from file", ex, Level.WARN);
            return false;
        }
    }
}
