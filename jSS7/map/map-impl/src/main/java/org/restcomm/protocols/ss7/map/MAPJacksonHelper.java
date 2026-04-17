package org.restcomm.protocols.ss7.map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Jackson helper for MAP module XML serialization.
 * Replaces XStream for better performance and security.
 */
public class MAPJacksonHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        // Configure XML mapper
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        // Allow unknown properties during deserialization (backward compatibility)
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Allow empty beans during serialization
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static String toXML(Object obj) {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to XML", e);
        }
    }

    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, Object.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Error deserializing XML", e);
        }
    }

    public static <T> T fromXML(String xml, Class<T> clazz) {
        try {
            return xmlMapper.readValue(xml, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("Error deserializing XML to " + clazz.getName(), e);
        }
    }

    public static void toXML(Object obj, Writer writer) throws IOException {
        xmlMapper.writeValue(writer, obj);
    }

    public static <T> T fromXML(Reader reader, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(reader, clazz);
    }
}