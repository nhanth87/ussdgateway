path = r'C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\map-impl\src\main\java\org\restcomm\protocols\ss7\map\primitives\BitStringBase.java'
with open(path, 'r') as f:
    content = f.read()
old = '    @JacksonXmlProperty(isAttribute = true)\n    protected BitSetStrictLength bitString;'
new = '    protected BitSetStrictLength bitString;'
content = content.replace(old, new)
with open(path, 'w') as f:
    f.write(content)
print('done')
