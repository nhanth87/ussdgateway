import os
import re

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'
helper_by_module = {
    'cap/cap-impl': ('org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper', 'CAPJacksonXMLHelper'),
    'map/map-impl': ('org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper', 'MAPJacksonXMLHelper'),
    'inap/inap-impl': ('org.restcomm.protocols.ss7.inap.INAPJacksonXMLHelper', 'INAPJacksonXMLHelper'),
    'isup/isup-impl': ('org.restcomm.protocols.ss7.isup.ISUPJacksonXMLHelper', 'ISUPJacksonXMLHelper'),
    'sccp/sccp-impl': ('org.restcomm.protocols.ss7.sccp.SCCPJacksonXMLHelper', 'SCCPJacksonXMLHelper'),
    'sccp/sccp-impl-ext': ('org.restcomm.protocols.ss7.sccpext.SCCPExtJacksonXMLHelper', 'SCCPExtJacksonXMLHelper'),
}

def update_file(path, helper_fq, helper_simple):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'XmlMapper xmlMapper = new XmlMapper()' not in content:
        return False

    # Add import if missing
    import_line = f'import {helper_fq};'
    if import_line not in content:
        # Insert after existing imports or package
        lines = content.splitlines(keepends=True)
        last_import = -1
        for i, line in enumerate(lines):
            if line.startswith('import '):
                last_import = i
        if last_import >= 0:
            lines.insert(last_import + 1, import_line + '\n')
        else:
            for i, line in enumerate(lines):
                if line.startswith('package '):
                    lines.insert(i + 1, import_line + '\n')
                    break
        content = ''.join(lines)

    # Replace instantiation and enable line
    content = re.sub(
        r'([ \t]*)XmlMapper xmlMapper = new XmlMapper\(\);\s*\n\s*xmlMapper\.enable\(SerializationFeature\.INDENT_OUTPUT\);\s*\n',
        r'\1XmlMapper xmlMapper = ' + helper_simple + '.getMapper();\n',
        content
    )
    # Handle case where enable line might be missing or formatted differently
    content = re.sub(
        r'([ \t]*)XmlMapper xmlMapper = new XmlMapper\(\);\s*\n',
        r'\1XmlMapper xmlMapper = ' + helper_simple + '.getMapper();\n',
        content
    )

    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    return True

for root, _, files in os.walk(base_dir):
    if 'src/test/java' not in root:
        continue
    rel = os.path.relpath(root, base_dir)
    module_key = None
    for key in helper_by_module:
        if rel.startswith(key):
            module_key = key
            break
    if not module_key:
        continue
    helper_fq, helper_simple = helper_by_module[module_key]
    for fname in files:
        if not fname.endswith('.java'):
            continue
        path = os.path.join(root, fname)
        if update_file(path, helper_fq, helper_simple):
            print('Updated', path)
