import re

path = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/sccp/sccp-impl-ext/src/main/java/org/restcomm/protocols/ss7/sccpext/impl/router/RouterExtImpl.java'
with open(path, 'r') as f:
    content = f.read()

start_marker = '    /**\n     * Persist\n     */\n    public void store() {'
start_idx = content.find(start_marker)
if start_idx == -1:
    print('ERROR: start marker not found')
    exit(1)

end_marker = '\n    public static void makeOldConfigCopy(String persistDir, String name) {'
end_idx = content.find(end_marker)
if end_idx == -1:
    print('ERROR: end marker not found')
    exit(1)

replacement = '''    /**
     * Persist
     */
    public void store() {
        try {
            RouterConfig config = new RouterConfig();
            config.rulesMap = this.rulesMap;
            config.routingAddresses = this.routingAddresses;

            String xml = SCCPJacksonXMLHelper.toXML(config);
            try (FileWriter writer = new FileWriter(this.persistFile.toString())) {
                writer.write(xml);
            }
        } catch (Exception e) {
            logger.error("Error while persisting the Rule state in file", e);
        }
    }

    /**
     * Configuration class for RouterExt persistence
     */
    @JacksonXmlRootElement(localName = "RouterConfig")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouterConfig {
        @JacksonXmlProperty public RuleMap<Integer, Rule> rulesMap;
        @JacksonXmlProperty public SccpAddressMap<Integer, SccpAddressImpl> routingAddresses;
    }

    /**
     * Load and create LinkSets and Link from persisted file
     */
    protected void load() {
        try {
            File f = new File(persistFile.toString());
            if (f.exists()) {
                loadVer4(persistFile.toString());
            } else {
                String s1 = persistFile.toString().replace("3_ext.xml", "2_ext.xml");
                f = new File(s1);
                if (f.exists()) {
                    logger.warn("Legacy SCCP RouterExt config format v2 not supported, using defaults");
                } else {
                    s1 = persistFile.toString().replace("3_ext.xml", "_ext.xml");
                    f = new File(s1);
                    if (f.exists()) {
                        logger.warn("Legacy SCCP RouterExt config format v1 not supported, using defaults");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn(String.format("Failed to load the SS7 configuration file. \\n%s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format("Failed to load the SS7 configuration file. \\n%s", e.getMessage()));
        }
    }

    private void moveBackupToRoutingAddress(SccpAddressMap<Integer, SccpAddress> backupAddresses) {
        NonBlockingHashMap<Integer, Integer> lstChange = new NonBlockingHashMap<Integer, Integer>();
        for (Integer bId : backupAddresses.keySet()) {
            SccpAddress addr = backupAddresses.get(bId);

            int i1 = bId + 100;
            while (true) {
                if (routingAddresses.get(i1) == null)
                    break;
                i1++;
            }
            routingAddresses.put(i1, (SccpAddressImpl) addr);
            lstChange.put(bId, i1);
        }

        for (Rule rule : rulesMap.values()) {
            Integer newVal = lstChange.get(rule.getSecondaryAddressId());
            if (newVal != null) {
                ((RuleImpl) rule).setSecondaryAddressId(newVal);
            }
        }
    }

    protected void loadVer4(String fn) throws FileNotFoundException {
        try (FileReader reader = new FileReader(fn)) {
            loadVer4(reader);
        } catch (IOException e) {
            logger.error(String.format("Failed to close FileReader for %s", fn), e);
        }
    }

    protected void loadVer4(FileReader reader) throws FileNotFoundException {
        try {
            RouterConfig config = SCCPJacksonXMLHelper.fromXML(reader, RouterConfig.class);
            if (config != null) {
                rulesMap = config.rulesMap;
                routingAddresses = config.routingAddresses;
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to parse RouterConfig from XML"), e);
        }
    }
'''

new_content = content[:start_idx] + replacement + content[end_idx:]
with open(path, 'w') as f:
    f.write(new_content)
print('OK')
