import re

write_block_re = re.compile(
    r'(?P<indent>[ \t]*)(?P<decl>(?:ByteArrayOutputStream\s+baos\s*=\s*new\s+ByteArrayOutputStream\(\)\s*;|baos\s*=\s*new\s+ByteArrayOutputStream\(\)\s*;))\s*\n'
    r'[ \t]*XMLObjectWriter\s+writer\s*=\s*XMLObjectWriter\.newInstance\(baos\)\s*;\s*\n'
    r'(?:[ \t]*//[^\n]*\n)*'
    r'[ \t]*writer\.setIndentation\("[^"]*"\)\s*;\s*(?://[^\n]*\n)*'
    r'[ \t]*writer\.write\((?P<var>[^,]+),\s*"(?P<tag>[^"]+)"\,\s*(?P<clazz>[^)]+)\)\s*;\s*\n'
    r'[ \t]*writer\.close\(\)\s*;\s*\n\s*\n'
    r'[ \t]*byte\[\]\s+rawData\s*=\s*baos\.toByteArray\(\)\s*;\s*\n'
    r'[ \t]*String\s+serializedEvent\s*=\s*new\s+String\(rawData\)\s*;\s*\n\s*\n'
    r'[ \t]*System\.out\.println\(serializedEvent\)\s*;',
    re.MULTILINE
)

with open('/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/test/java/org/restcomm/protocols/ss7/cap/EsiBcsm/CallAcceptedSpecificInfoTest.java','r') as f:
    text = f.read()
print('Matches write:', len(list(write_block_re.finditer(text))))
print('XMLObjectWriter in text:', 'XMLObjectWriter' in text)
