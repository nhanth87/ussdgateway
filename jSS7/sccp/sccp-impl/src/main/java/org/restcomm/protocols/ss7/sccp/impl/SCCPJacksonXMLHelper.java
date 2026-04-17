package org.restcomm.protocols.ss7.sccp.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.*;
import java.util.Map;

import org.restcomm.protocols.ss7.sccp.impl.router.LongMessageRuleMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3DestinationMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3ServiceAccessPointMap;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;

/**
 * Jackson XML helper for SCCP module XML serialization.
 * Replaces XStream for better performance and security.
 */
public class SCCPJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();
    
    static {
        // Configure XmlMapper
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Register custom module to handle Map<Integer, V> serialization
        // Jackson XML default behavior uses numeric keys as element names, which is invalid XML.
        // We override it to serialize entries as <entry><key>...</key><value>...</value></entry>.
        SimpleModule module = new SimpleModule("SccpMapModule");
        module.addSerializer(new SccpMapSerializer());
        module.addDeserializer(LongMessageRuleMap.class, new SccpMapDeserializer<>(LongMessageRuleMap.class));
        module.addDeserializer(Mtp3DestinationMap.class, new SccpMapDeserializer<>(Mtp3DestinationMap.class));
        module.addDeserializer(Mtp3ServiceAccessPointMap.class, new SccpMapDeserializer<>(Mtp3ServiceAccessPointMap.class));
        module.addDeserializer(RemoteSubSystemMap.class, new SccpMapDeserializer<>(RemoteSubSystemMap.class));
        module.addDeserializer(RemoteSignalingPointCodeMap.class, new SccpMapDeserializer<>(RemoteSignalingPointCodeMap.class));
        module.addDeserializer(ConcernedSignalingPointCodeMap.class, new SccpMapDeserializer<>(ConcernedSignalingPointCodeMap.class));
        xmlMapper.registerModule(module);
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
    
    public static <T> T fromXML(Reader reader, Class<T> valueType) throws IOException {
        return xmlMapper.readValue(reader, valueType);
    }
    
    public static <T> T fromXML(String xml, Class<T> valueType) throws IOException {
        return xmlMapper.readValue(xml, valueType);
    }
    
    @Deprecated
    public static Object fromXML(Reader reader) throws IOException {
        // This method is deprecated because Jackson needs type information
        // Use the typed version fromXML(Reader, Class<T>) instead
        throw new UnsupportedOperationException("Use fromXML(Reader, Class<T>) instead for type safety");
    }
    
    @Deprecated
    public static Object fromXML(String xml) throws IOException {
        // This method is deprecated because Jackson needs type information
        // Use the typed version fromXML(String, Class) instead
        throw new UnsupportedOperationException("Use fromXML(String, Class<T>) instead for type safety");
    }

    /**
     * Custom serializer for SCCP Map wrappers that writes entries as
     * <entry><key>...</key><value>...</value></entry> instead of using
     * numeric keys as invalid XML element names.
     */
    @SuppressWarnings("rawtypes")
    public static class SccpMapSerializer extends StdSerializer<Map> {
        private static final long serialVersionUID = 1L;

        public SccpMapSerializer() {
            super(Map.class);
        }

        @Override
        public void serialize(Map value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Object entryObj : value.entrySet()) {
                Map.Entry entry = (Map.Entry) entryObj;
                gen.writeFieldName("entry");
                gen.writeStartObject();
                gen.writeObjectField("key", entry.getKey());
                gen.writeObjectField("value", entry.getValue());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    /**
     * Custom deserializer for SCCP Map wrappers that reads entries in the
     * <entry><key>...</key><value>...</value></entry> format.
     */
    @SuppressWarnings("rawtypes")
    public static class SccpMapDeserializer<T extends Map> extends JsonDeserializer<T> implements ContextualDeserializer {
        private static final long serialVersionUID = 1L;

        private final Class<T> mapClass;
        private final com.fasterxml.jackson.databind.JavaType valueType;

        public SccpMapDeserializer(Class<T> mapClass) {
            this(mapClass, null);
        }

        public SccpMapDeserializer(Class<T> mapClass, com.fasterxml.jackson.databind.JavaType valueType) {
            this.mapClass = mapClass;
            this.valueType = valueType;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            if (property != null && valueType == null) {
                com.fasterxml.jackson.databind.JavaType mapType = property.getType();
                com.fasterxml.jackson.databind.JavaType vt = mapType.containedType(1);
                return new SccpMapDeserializer<>(mapClass, vt);
            }
            return this;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            T map;
            try {
                map = mapClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IOException("Failed to instantiate " + mapClass, e);
            }

            while (p.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = p.getCurrentName();
                if ("entry".equals(fieldName)) {
                    p.nextToken(); // START_OBJECT
                    Object key = null;
                    Object value = null;
                    while (p.nextToken() != JsonToken.END_OBJECT) {
                        String entryField = p.getCurrentName();
                        p.nextToken();
                        if ("key".equals(entryField)) {
                            key = ctxt.readValue(p, Integer.class);
                        } else if ("value".equals(entryField)) {
                            if (valueType != null) {
                                value = ctxt.readValue(p, valueType);
                            } else {
                                value = p.readValueAs(Object.class);
                            }
                        }
                    }
                    if (key != null) {
                        map.put(key, value);
                    }
                }
            }
            return map;
        }
    }
}
