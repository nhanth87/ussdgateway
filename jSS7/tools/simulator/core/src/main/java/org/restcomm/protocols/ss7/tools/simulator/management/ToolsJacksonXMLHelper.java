package org.restcomm.protocols.ss7.tools.simulator.management;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.*;

import org.restcomm.protocols.ss7.tools.simulator.common.ConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level3.CapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapScfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapSsfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.psi.TestPsiServerConfigurationData;

/**
 * Jackson XML helper for TOOLS simulator module XML serialization.
 * Replaces XStream for better performance and Java 17+ compatibility.
 */
public class ToolsJacksonXMLHelper {
    private static final XmlMapper xmlMapper;

    static {
        xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // Note: Jackson handles type information differently than XStream
        // Classes need @JacksonXmlRootElement for proper XML element naming
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static void toXML(Object obj, Writer writer) {
        try {
            xmlMapper.writeValue(writer, obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }

    public static String toXML(Object obj) {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }

    public static Object fromXML(Reader reader) {
        try {
            return xmlMapper.readValue(reader, ConfigurationData.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }

    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, ConfigurationData.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}
