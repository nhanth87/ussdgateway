import re

path = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/M3UAManagementImpl.java'
with open(path, 'r') as f:
    content = f.read()

# 1. Add RouteEntry inner class before M3UAConfig
route_entry_class = '''
    public static class RouteEntry {
        @com.fasterxml.jackson.annotation.JsonProperty("key")
        public String key;
        @com.fasterxml.jackson.annotation.JsonProperty("value")
        public RouteAsImpl value;
    }

    /**
     * Configuration class for M3UA persistence
     */
'''
if 'public static class RouteEntry' not in content:
    content = content.replace(
        '    /**\n     * Configuration class for M3UA persistence\n     */',
        route_entry_class,
        1
    )

# 2. Update M3UAConfig fields
old_asp = '''        @JsonProperty("aspFactories")
        @JacksonXmlElementWrapper(localName = "aspFactories")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "aspFactory")
        @JsonDeserialize(contentAs = AspFactoryImpl.class)
        public CopyOnWriteArrayList<AspFactory> aspFactories;'''
new_asp = '''        @JsonProperty("aspFactories")
        @JacksonXmlElementWrapper(localName = "aspFactories")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "aspFactory")
        public CopyOnWriteArrayList<AspFactoryImpl> aspFactories;'''
content = content.replace(old_asp, new_asp)

old_as = '''        @JsonProperty("appServers")
        @JacksonXmlElementWrapper(localName = "appServers")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "as")
        @JsonDeserialize(contentAs = AsImpl.class)
        public CopyOnWriteArrayList<As> appServers;'''
new_as = '''        @JsonProperty("appServers")
        @JacksonXmlElementWrapper(localName = "appServers")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "as")
        public CopyOnWriteArrayList<AsImpl> appServers;'''
content = content.replace(old_as, new_as)

old_route = '''        @JsonProperty("route")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "routeEntry")
        public RouteMap route;'''
new_route = '''        @JsonProperty("routeEntries")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(localName = "route")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "routeEntry")
        public java.util.ArrayList<RouteEntry> routeEntries;'''
content = content.replace(old_route, new_route)

# 3. Update store() to convert RouteMap to RouteEntry list
old_store_route = '            config.route = this.routeManagement.route;'
new_store_route = '''            config.routeEntries = new java.util.ArrayList<>();
            for (java.util.Map.Entry<String, RouteAsImpl> entry : this.routeManagement.route.entrySet()) {
                RouteEntry re = new RouteEntry();
                re.key = entry.getKey();
                re.value = entry.getValue();
                config.routeEntries.add(re);
            }'''
content = content.replace(old_store_route, new_store_route)

# 4. Update loadVer2() to convert RouteEntry list back to RouteMap
old_load_route = '        this.routeManagement.route = config.route != null ? config.route : new RouteMap<>();'
new_load_route = '''        RouteMap<String, RouteAsImpl> routeMap = new RouteMap<>();
        if (config.routeEntries != null) {
            for (RouteEntry re : config.routeEntries) {
                routeMap.put(re.key, re.value);
            }
        }
        this.routeManagement.route = routeMap;'''
content = content.replace(old_load_route, new_load_route)

with open(path, 'w') as f:
    f.write(content)
print('Patched M3UAManagementImpl.java')
