package org.restcomm.protocols.ss7.tcap;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Jackson XML helper for TCAP module XML serialization.
 * Replaces XStream XML serialization.
 */
public class TCAPJacksonXMLHelper {
    private static final XmlMapper xmlMapper;

    static {
        xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static void toXML(Object obj, Writer writer) throws IOException {
        xmlMapper.writeValue(writer, obj);
    }

    public static String toXML(Object obj) throws IOException {
        return xmlMapper.writeValueAsString(obj);
    }

    public static <T> T fromXML(Reader reader, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(reader, clazz);
    }

    public static <T> T fromXML(String xml, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(xml, clazz);
    }

    // Legacy methods for backward compatibility (without class parameter)
    public static Object fromXML(Reader reader) throws IOException {
        // Default to TCAPStackImpl.TCAPConfig for legacy usage
        return fromXML(reader, TCAPStackImpl.TCAPConfig.class);
    }

    public static Object fromXML(String xml) throws IOException {
        // Default to TCAPStackImpl.TCAPConfig for legacy usage
        return fromXML(xml, TCAPStackImpl.TCAPConfig.class);
    }
}
