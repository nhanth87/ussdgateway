package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Jackson XML helper for M3UA module XML serialization.
 * Replaces XStream to avoid Java module system issues.
 * @deprecated Use M3UAJacksonXMLHelper instead
 */
@Deprecated
public class JacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        // Configure for pretty printing
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);

        // Configure to allow deserialization of generic types
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Configure polymorphic type handling
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("org.restcomm.protocols.ss7.m3ua")
                .build();
        xmlMapper.activateDefaultTyping(ptv, com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);
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

    @SuppressWarnings("unchecked")
    public static <T> T fromXML(String xml) throws IOException {
        return (T) xmlMapper.readValue(xml, Object.class);
    }
}
