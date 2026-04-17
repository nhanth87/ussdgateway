package org.restcomm.protocols.ss7.inap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.lang.reflect.Modifier;

public class INAPJacksonXMLHelper {
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    static {
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule("inapjacksonxml-module") {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addAbstractTypeResolver(new AutoImplAbstractTypeResolver());
            }
        };
        module.addAbstractTypeMapping(org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyCategory.class,
                org.restcomm.protocols.ss7.isup.impl.message.parameter.CallingPartyCategoryImpl.class);
        module.addAbstractTypeMapping(org.restcomm.protocols.ss7.isup.message.parameter.UserTeleserviceInformation.class,
                org.restcomm.protocols.ss7.isup.impl.message.parameter.UserTeleserviceInformationImpl.class);
        module.addAbstractTypeMapping(org.restcomm.protocols.ss7.isup.message.parameter.RedirectionInformation.class,
                org.restcomm.protocols.ss7.isup.impl.message.parameter.RedirectionInformationImpl.class);
        XML_MAPPER.registerModule(module);
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
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
