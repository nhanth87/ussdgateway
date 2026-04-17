package org.restcomm.protocols.ss7.isup.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Jackson XML helper for ISUP module XML serialization.
 * Replaces XStream XML serialization.
 */
public class ISUPJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();
    
    static {
        // Configure XML mapper
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }
    
    public static <T> T fromXML(Reader reader, Class<T> clazz) {
        try {
            return xmlMapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
    
    public static Object fromXML(Reader reader) {
        try {
            return xmlMapper.readValue(reader, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
    
    public static <T> T fromXML(String xml, Class<T> clazz) {
        try {
            return xmlMapper.readValue(xml, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
    
    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}
