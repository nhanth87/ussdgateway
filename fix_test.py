path = r'C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java\org\restcomm\protocols\ss7\cap\EsiBcsm\OChangeOfPositionSpecificInfoTest.java'
with open(path, 'r') as f:
    content = f.read()
old = """        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains("<ageOfLocationInformation>"));
        assertTrue(serializedEvent.contains("<size>"));
        }"""
new = """        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }"""
content = content.replace(old, new)
with open(path, 'w') as f:
    f.write(content)
print('done')
