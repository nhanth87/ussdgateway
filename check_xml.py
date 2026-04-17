import xml.etree.ElementTree as ET
try:
    ET.parse('/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/target/test-classes/M3UAManagementTest_m3ua1.xml')
    print('OK')
except Exception as e:
    print('ERROR:', e)
