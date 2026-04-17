path = r'C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java\org\restcomm\protocols\ss7\cap\service\circuitSwitchedCall\primitive\InitialDPArgExtensionTest.java'
with open(path, 'r') as f:
    content = f.read()
old = """        try {
            copy = xmlMapper.readValue(serializedEvent, InitialDPArgExtensionImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<gmscAddress>"));
        assertFalse(serializedEvent.contains("<forwardingDestinationNumber>"));
        assertTrue(serializedEvent.contains("<mSClassmark2>"));
        assertTrue(serializedEvent.contains("<iMEI>"));
        assertTrue(serializedEvent.contains("<phase1Supported>"));
        assertTrue(serializedEvent.contains("<phase4Supported>"));
        assertTrue(serializedEvent.contains("<moveLeg>"));
        assertTrue(serializedEvent.contains("<changeOfPositionDP>"));
        assertTrue(serializedEvent.contains("<lowLayerCompatibility>"));
        assertTrue(serializedEvent.contains("<lowLayerCompatibility2>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getEnhancedDialledServicesAllowed())));
        assertTrue(serializedEvent.contains("<uUIndicator>"));
        assertTrue(serializedEvent.contains(String.valueOf(original.getCollectInformationAllowed())));
        assertTrue(serializedEvent.contains(String.valueOf(original.getReleaseCallArgExtensionAllowed())));
        }"""
new = """        try {
            copy = xmlMapper.readValue(serializedEvent, InitialDPArgExtensionImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }"""
content = content.replace(old, new)
with open(path, 'w') as f:
    f.write(content)
print('done')
