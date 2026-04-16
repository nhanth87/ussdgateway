import xml.etree.ElementTree as ET

files = [
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/pom.xml',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/pom.xml',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/pom.xml',
]

for f in files:
    tree = ET.parse(f)
    root = tree.getroot()
    ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
    parent = root.find('m:parent', ns)
    if parent is not None:
        gid = parent.find('m:groupId', ns)
        aid = parent.find('m:artifactId', ns)
        ver = parent.find('m:version', ns)
        print(f"{f} -> parent: {gid.text}:{aid.text}:{ver.text}")
    else:
        print(f"{f} -> no parent")
    props = root.find('m:properties', ns)
    if props is not None:
        sctp_ver = props.find('m:sctp.version', ns)
        if sctp_ver is not None:
            print(f"  sctp.version = {sctp_ver.text}")
