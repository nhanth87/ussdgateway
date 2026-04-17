package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.StringReader;
import java.util.concurrent.CopyOnWriteArrayList;
import org.testng.annotations.Test;

public class JacksonTest {
    @Test
    public void testJackson() throws Exception {
        XmlMapper mapper = M3UAJacksonXMLHelper.getXmlMapper();
        
        M3UAManagementImpl.M3UAConfig config = new M3UAManagementImpl.M3UAConfig();
        config.timeBetweenHeartbeat = 10000;
        config.aspFactories = new CopyOnWriteArrayList<>();
        config.appServers = new CopyOnWriteArrayList<>();
        config.routeEntries = new java.util.ArrayList<>();
        
        String xml = M3UAJacksonXMLHelper.toXML(config);
        System.out.println("=== SERIALIZED XML ===");
        System.out.println(xml);
        
        System.out.println("=== DESERIALIZING ===");
        try {
            M3UAManagementImpl.M3UAConfig config2 = mapper.readValue(new StringReader(xml), M3UAManagementImpl.M3UAConfig.class);
            System.out.println("routeEntries = " + config2.routeEntries);
            System.out.println("appServers = " + config2.appServers);
            System.out.println("aspFactories = " + config2.aspFactories);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
