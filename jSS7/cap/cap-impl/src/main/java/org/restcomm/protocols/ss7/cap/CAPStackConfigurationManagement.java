package org.restcomm.protocols.ss7.cap;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class CAPStackConfigurationManagement {
    private static final String PERSIST_FILE_NAME = "management.xml";
    private static final String CAP_MANAGEMENT_PERSIST_DIR_KEY = "capmanagement.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String DEFAULT_CONFIG_FILE_NAME = "CapStack";

    private static final String TIMER_CIRCUIT_SWITCHED_CALL_CONTROL_SHORT = "timercircuitswitchedcallcontrolshort";
    private static final String TIMER_CIRCUIT_SWITCHED_CALL_CONTROL_MEDIUM = "timercircuitswitchedcallcontrolmedium";
    private static final String TIMER_CIRCUIT_SWITCHED_CALL_CONTROL_LONG = "timercircuitswitchedcallcontrollong";
    private static final String TIMER_SMS_SHORT = "timersmsshort";
    private static final String TIMER_GPRS_SHORT = "timergprsshort";

    private static CAPStackConfigurationManagement instance = new CAPStackConfigurationManagement();

    private StringBuilder persistFile = new StringBuilder();
    private String configFileName = DEFAULT_CONFIG_FILE_NAME;
    private String persistDir = null;

    private int _Timer_CircuitSwitchedCallControl_Short = 6000; // 1 - 10 sec
    private int _Timer_CircuitSwitchedCallControl_Medium = 30000; // 1 - 60 sec
    private int _Timer_CircuitSwitchedCallControl_Long = 300000; // 1 s - 30 minutes
    private int _Timer_Sms_Short = 10000; // 1 - 20 sec
    private int _Timer_Gprs_Short = 10000; // 1 - 20 sec

    private CAPStackConfigurationManagement() {
    }

    public static CAPStackConfigurationManagement getInstance() {
        return instance;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
        this.setPersistFile();
    }

    private void setPersistFile() {
        this.persistFile.setLength(0);

        if (persistDir != null) {
            this.persistFile.append(persistDir).append(File.separator).append(this.configFileName).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFile.append(System.getProperty(CAP_MANAGEMENT_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY))).append(File.separator).append(this.configFileName)
                    .append("_").append(PERSIST_FILE_NAME);
        }
    }

    /**
     * Persist
     */
    public void store() {
        try (Writer writer = new FileWriter(persistFile.toString())) {
            CAPJacksonXMLHelper.getXmlMapper().writeValue(writer, this);
        } catch (IOException e) {
            System.err.println(String.format("Error while persisting the CAP Resource state in file=%s", persistFile.toString()));
            e.printStackTrace();
        }
    }

    /**
     * Load and create LinkSets and Link from persisted file
     * <p>
     * load() is called from CAPStackImpl
     */
    public void load() {
        try {
            setPersistFile();
            File file = new File(persistFile.toString());
            if (!file.exists()) {
                return;
            }
            try (Reader reader = new FileReader(file)) {
                CAPStackConfigurationManagement loaded = CAPJacksonXMLHelper.getXmlMapper().readValue(reader, CAPStackConfigurationManagement.class);
                // Copy loaded values to this instance
                this._Timer_CircuitSwitchedCallControl_Short = loaded._Timer_CircuitSwitchedCallControl_Short;
                this._Timer_CircuitSwitchedCallControl_Medium = loaded._Timer_CircuitSwitchedCallControl_Medium;
                this._Timer_CircuitSwitchedCallControl_Long = loaded._Timer_CircuitSwitchedCallControl_Long;
                this._Timer_Sms_Short = loaded._Timer_Sms_Short;
                this._Timer_Gprs_Short = loaded._Timer_Gprs_Short;
            }
        } catch (Exception e) {
            System.err.println(String.format("Error while load the CAP Resource state from file=%s", persistFile.toString()));
            e.printStackTrace();
        }
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public int getTimerCircuitSwitchedCallControlShort() {
        return _Timer_CircuitSwitchedCallControl_Short;
    }

    public int getTimerCircuitSwitchedCallControlMedium() {
        return _Timer_CircuitSwitchedCallControl_Medium;
    }

    public int getTimerCircuitSwitchedCallControlLong() {
        return _Timer_CircuitSwitchedCallControl_Long;
    }

    public int getTimerSmsShort() {
        return _Timer_Sms_Short;
    }

    public int getTimerGprsShort() {
        return _Timer_Gprs_Short;
    }

    public void set_Timer_CircuitSwitchedCallControl_Short(int _Timer_CircuitSwitchedCallControl_Short) {
        this._Timer_CircuitSwitchedCallControl_Short = _Timer_CircuitSwitchedCallControl_Short;
        this.store();
    }

    public void set_Timer_CircuitSwitchedCallControl_Medium(int _Timer_CircuitSwitchedCallControl_Medium) {
        this._Timer_CircuitSwitchedCallControl_Medium = _Timer_CircuitSwitchedCallControl_Medium;
        this.store();
    }

    public void set_Timer_CircuitSwitchedCallControl_Long(int _Timer_CircuitSwitchedCallControl_Long) {
        this._Timer_CircuitSwitchedCallControl_Long = _Timer_CircuitSwitchedCallControl_Long;
        this.store();
    }

    public void set_Timer_Sms_Short(int _Timer_Sms_Short) {
        this._Timer_Sms_Short = _Timer_Sms_Short;
        this.store();
    }

    public void set_Timer_Gprs_Short(int _Timer_Gprs_Short) {
        this._Timer_Gprs_Short = _Timer_Gprs_Short;
        this.store();
    }
}
