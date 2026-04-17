package org.restcomm.protocols.ss7.m3ua.impl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.UserCauseImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.UserCause;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.TrafficModeTypeImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.StatusImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.Status;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ServiceIndicatorsImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ServiceIndicators;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.RoutingKeyImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingKey;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.RoutingContextImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.RegistrationStatusImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RegistrationStatus;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.RegistrationResultImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RegistrationResult;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ProtocolDataImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ProtocolData;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.OPCListImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.OPCList;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.NetworkAppearanceImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.LocalRKIdentifierImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.LocalRKIdentifier;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.InfoStringImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.InfoString;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.HeartbeatDataImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.HeartbeatData;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ErrorCodeImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ErrorCode;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.DiagnosticInfoImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.DiagnosticInfo;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.DestinationPointCodeImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.DestinationPointCode;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.DeregistrationStatusImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.DeregistrationStatus;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.DeregistrationResultImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.DeregistrationResult;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.CorrelationIdImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.CorrelationId;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.CongestedIndicationImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.CongestedIndication;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ConcernedDPCImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ConcernedDPC;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.AffectedPointCodeImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.AffectedPointCode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.restcomm.protocols.ss7.m3ua.parameter.ASPIdentifier;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ASPIdentifierImpl;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.As;

/**
 * Jackson XML helper for M3UA module XML serialization.
 * Replaces XStream to avoid Java module system issues.
 */
public class M3UAJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        // Configure for pretty printing
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);

        // Configure to allow deserialization of unknown properties
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Configure to allow serialization of empty beans (needed for complex objects with no serializable fields)
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        SimpleModule m3uaModule = new SimpleModule("m3ua-module");
        m3uaModule.addAbstractTypeMapping(ASPIdentifier.class, ASPIdentifierImpl.class);
        m3uaModule.addAbstractTypeMapping(Asp.class, AspImpl.class);
        m3uaModule.addAbstractTypeMapping(As.class, AsImpl.class);
        m3uaModule.addAbstractTypeMapping(AffectedPointCode.class, AffectedPointCodeImpl.class);
        m3uaModule.addAbstractTypeMapping(ASPIdentifier.class, ASPIdentifierImpl.class);
        m3uaModule.addAbstractTypeMapping(ConcernedDPC.class, ConcernedDPCImpl.class);
        m3uaModule.addAbstractTypeMapping(CongestedIndication.class, CongestedIndicationImpl.class);
        m3uaModule.addAbstractTypeMapping(CorrelationId.class, CorrelationIdImpl.class);
        m3uaModule.addAbstractTypeMapping(DeregistrationResult.class, DeregistrationResultImpl.class);
        m3uaModule.addAbstractTypeMapping(DeregistrationStatus.class, DeregistrationStatusImpl.class);
        m3uaModule.addAbstractTypeMapping(DestinationPointCode.class, DestinationPointCodeImpl.class);
        m3uaModule.addAbstractTypeMapping(DiagnosticInfo.class, DiagnosticInfoImpl.class);
        m3uaModule.addAbstractTypeMapping(ErrorCode.class, ErrorCodeImpl.class);
        m3uaModule.addAbstractTypeMapping(HeartbeatData.class, HeartbeatDataImpl.class);
        m3uaModule.addAbstractTypeMapping(InfoString.class, InfoStringImpl.class);
        m3uaModule.addAbstractTypeMapping(LocalRKIdentifier.class, LocalRKIdentifierImpl.class);
        m3uaModule.addAbstractTypeMapping(NetworkAppearance.class, NetworkAppearanceImpl.class);
        m3uaModule.addAbstractTypeMapping(OPCList.class, OPCListImpl.class);
        m3uaModule.addAbstractTypeMapping(ParameterFactory.class, ParameterFactoryImpl.class);
        m3uaModule.addAbstractTypeMapping(ProtocolData.class, ProtocolDataImpl.class);
        m3uaModule.addAbstractTypeMapping(RegistrationResult.class, RegistrationResultImpl.class);
        m3uaModule.addAbstractTypeMapping(RegistrationStatus.class, RegistrationStatusImpl.class);
        m3uaModule.addAbstractTypeMapping(RoutingContext.class, RoutingContextImpl.class);
        m3uaModule.addAbstractTypeMapping(RoutingKey.class, RoutingKeyImpl.class);
        m3uaModule.addAbstractTypeMapping(ServiceIndicators.class, ServiceIndicatorsImpl.class);
        m3uaModule.addAbstractTypeMapping(Status.class, StatusImpl.class);
        m3uaModule.addAbstractTypeMapping(TrafficModeType.class, TrafficModeTypeImpl.class);
        m3uaModule.addAbstractTypeMapping(UserCause.class, UserCauseImpl.class);
        xmlMapper.registerModule(m3uaModule);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    /**
     * Safely serialize object to XML, handling circular references.
     * If serialization fails, returns empty string to allow application to continue.
     */
    public static void toXML(Object obj, Writer writer) throws IOException {
        try {
            xmlMapper.writeValue(writer, obj);
        } catch (Exception e) {
            // Log warning but don't fail completely
            writer.write("<!-- Serialization error: " + e.getMessage() + " -->");
        }
    }

    /**
     * Safely serialize object to XML string, handling circular references.
     */
    public static String toXML(Object obj) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            xmlMapper.writeValue(writer, obj);
            return writer.toString();
        } catch (Exception e) {
            return "<!-- Serialization error: " + e.getMessage() + " -->";
        }
    }

    public static <T> T fromXML(Reader reader, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(reader, clazz);
    }

    public static Object fromXML(String xml) throws IOException {
        return xmlMapper.readValue(xml, Object.class);
    }
}