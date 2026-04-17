package org.restcomm.protocols.ss7.cap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.io.IOException;
import java.lang.reflect.Modifier;
import org.mobicents.protocols.asn.BitSetStrictLength;

public class CAPJacksonXMLHelper {
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    static {
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        XML_MAPPER.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        XML_MAPPER.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        XML_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        SimpleModule module = new SimpleModule("capjacksonxml-module") {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addAbstractTypeResolver(new AutoImplAbstractTypeResolver());
            }
        };
        module.addSerializer(BitSetStrictLength.class, new BitSetStrictLengthSerializer());
        module.addDeserializer(BitSetStrictLength.class, new BitSetStrictLengthDeserializer());
        XML_MAPPER.registerModule(module);
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
    }

    private static class BitSetStrictLengthSerializer extends StdSerializer<BitSetStrictLength> {
        public BitSetStrictLengthSerializer() {
            super(BitSetStrictLength.class);
        }

        @Override
        public void serialize(BitSetStrictLength value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("strictLength", value.getStrictLength());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.getStrictLength(); i++) {
                sb.append(value.get(i) ? '1' : '0');
            }
            gen.writeStringField("bits", sb.toString());
            gen.writeEndObject();
        }
    }

    private static class BitSetStrictLengthDeserializer extends StdDeserializer<BitSetStrictLength> {
        public BitSetStrictLengthDeserializer() {
            super(BitSetStrictLength.class);
        }

        @Override
        public BitSetStrictLength deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int strictLength = 0;
            String bits = null;
            while (p.nextToken() != com.fasterxml.jackson.core.JsonToken.END_OBJECT) {
                String fieldName = p.currentName();
                p.nextToken();
                if ("strictLength".equals(fieldName)) {
                    strictLength = p.getIntValue();
                } else if ("bits".equals(fieldName)) {
                    bits = p.getValueAsString();
                } else {
                    p.skipChildren();
                }
            }
            BitSetStrictLength result = new BitSetStrictLength(strictLength);
            if (bits != null) {
                for (int i = 0; i < bits.length() && i < strictLength; i++) {
                    if (bits.charAt(i) == '1') {
                        result.set(i);
                    }
                }
            }
            return result;
        }
    }

    private static class AutoImplAbstractTypeResolver extends AbstractTypeResolver {
        @Override
        public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
            Class<?> raw = type.getRawClass();
            if (!raw.isInterface() && !Modifier.isAbstract(raw.getModifiers())) {
                return null;
            }
            Package pkgObj = raw.getPackage();
            if (pkgObj == null) {
                return null;
            }
            String simple = raw.getSimpleName();
            String pkg = pkgObj.getName();

            String[] candidates = new String[] {
                pkg + "." + simple + "Impl",
                pkg.replace(".api.", ".") + "." + simple + "Impl",
                pkg + ".impl." + simple + "Impl",
            };

            for (String candidate : candidates) {
                if (candidate.equals(raw.getName())) continue;
                try {
                    Class<?> impl = Class.forName(candidate);
                    if (raw.isAssignableFrom(impl)) {
                        return config.constructType(impl);
                    }
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            return null;
        }
    }
}
