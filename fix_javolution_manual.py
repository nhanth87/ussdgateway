import os
import re

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

files_to_fix = [
    # map-impl remaining
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/errors/MAPErrorMessageTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/primitives/GlobalCellIdTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/service/callhandling/UUIndicatorTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/service/mobility/subscriberManagement/ExtBasicServiceCodeTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/service/mobility/subscriberManagement/ExtTeleserviceCodeTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/service/supplementary/ProcessUnstructuredSSRequestTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/map-impl/src/test/java/org/restcomm/protocols/ss7/map/service/supplementary/UnstructuredSSResponseTest.java',
    # inap-impl remaining
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/inap/inap-impl/src/test/java/org/restcomm/protocols/ss7/inap/primitives/LegIDTest.java',
    '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/inap/inap-impl/src/test/java/org/restcomm/protocols/ss7/inap/primitives/MiscCallInfoTest.java',
]

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    new_lines = []
    i = 0
    modified = False
    mapper_inserted = False

    write_re = re.compile(r'writer\.write\(([^,]+),\s*"([^"]+)",\s*([^)]+)\)\s*;')
    read_re = re.compile(r'reader\.read\("([^"]+)",\s*([^)]+)\)\s*;')

    while i < len(lines):
        line = lines[i]

        # Detect start of write block
        if ('ByteArrayOutputStream baos = new ByteArrayOutputStream()' in line or
            'baos = new ByteArrayOutputStream()' in line):
            # Capture block until System.out.println(serializedEvent);
            block = [line]
            j = i + 1
            var_name = None
            clazz_name = None
            while j < len(lines):
                block.append(lines[j])
                m = write_re.search(lines[j])
                if m:
                    var_name = m.group(1).strip()
                    clazz_name = m.group(3).strip()
                if 'System.out.println(serializedEvent);' in lines[j]:
                    break
                j += 1
            if var_name and clazz_name and j < len(lines):
                indent = len(line) - len(line.lstrip())
                spaces = line[:indent]
                if not mapper_inserted:
                    new_lines.append(f'{spaces}XmlMapper xmlMapper = new XmlMapper();\n')
                    new_lines.append(f'{spaces}xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);\n')
                    mapper_inserted = True
                new_lines.append(f'{spaces}String serializedEvent = xmlMapper.writeValueAsString({var_name});\n\n')
                new_lines.append(f'{spaces}System.out.println(serializedEvent);\n')
                i = j + 1
                modified = True
                continue

        # Detect start of read block
        if ('ByteArrayInputStream bais = new ByteArrayInputStream(rawData)' in line or
            'bais = new ByteArrayInputStream(rawData)' in line):
            block = [line]
            j = i + 1
            tag_name = None
            clazz_name = None
            copy_assignment = None
            while j < len(lines):
                block.append(lines[j])
                m = read_re.search(lines[j])
                if m:
                    tag_name = m.group(1)
                    clazz_name = m.group(2).strip()
                    # find copy var name
                    parts = lines[j].strip().split('=')
                    if len(parts) >= 2:
                        copy_assignment = parts[0].strip()
                if m:
                    break
                j += 1
            if copy_assignment and clazz_name and j < len(lines):
                indent = len(line) - len(line.lstrip())
                spaces = line[:indent]
                # Remove .class from copy type if present
                decl = copy_assignment
                if '.class' in clazz_name:
                    clazz_name_clean = clazz_name.replace('.class', '')
                    decl = decl.replace(clazz_name, clazz_name_clean)
                new_lines.append(f'{spaces}{decl} = xmlMapper.readValue(serializedEvent, {clazz_name});\n')
                i = j + 1
                modified = True
                continue

        new_lines.append(line)
        i += 1

    if not modified:
        return False

    content = ''.join(new_lines)
    # add imports if missing
    if 'import com.fasterxml.jackson.dataformat.xml.XmlMapper;' not in content:
        jackson_imports = (
            'import com.fasterxml.jackson.dataformat.xml.XmlMapper;\n'
            'import com.fasterxml.jackson.databind.SerializationFeature;\n'
        )
        if 'import ' in content:
            last_import_idx = content.rfind('import ')
            last_import_end = content.find('\n', last_import_idx) + 1
            content = content[:last_import_end] + jackson_imports + content[last_import_end:]
        else:
            pkg_idx = content.find('package ')
            if pkg_idx != -1:
                pkg_end = content.find(';', pkg_idx) + 2
                content = content[:pkg_end] + '\n' + jackson_imports + content[pkg_end:]
            else:
                content = jackson_imports + content

    # remove unused javolution imports
    content = re.sub(r'import\s+javolution\.xml\.XMLObjectWriter;\s*\n', '', content)
    content = re.sub(r'import\s+javolution\.xml\.XMLObjectReader;\s*\n', '', content)
    if 'ByteArrayOutputStream' not in content:
        content = re.sub(r'import\s+java\.io\.ByteArrayOutputStream;\s*\n', '', content)
    if 'ByteArrayInputStream' not in content:
        content = re.sub(r'import\s+java\.io\.ByteArrayInputStream;\s*\n', '', content)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    return True


for fp in files_to_fix:
    if os.path.exists(fp):
        if fix_file(fp):
            print('Fixed', fp)
        else:
            print('No change', fp)
    else:
        print('Missing', fp)
