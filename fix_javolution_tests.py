import os
import re
import sys

# Modules to process
modules = [
    ('cap', 'cap/cap-impl'),
    ('map', 'map/map-impl'),
    ('isup', 'isup/isup-impl'),
    ('inap', 'inap/inap-impl'),
]

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

# Regex to match javolution write block
write_block_re = re.compile(
    r'(?P<indent>[ \t]*)(?P<decl>(?:ByteArrayOutputStream\s+baos\s*=\s*new\s+ByteArrayOutputStream\(\)\s*;|baos\s*=\s*new\s+ByteArrayOutputStream\(\)\s*;))\s*\n'
    r'[ \t]*XMLObjectWriter\s+writer\s*=\s*XMLObjectWriter\.newInstance\(baos\)\s*;\s*\n'
    r'(?:[ \t]*//[^\n]*\n)*'
    r'[ \t]*writer\.setIndentation\("[^"]*"\)\s*;\s*(?://[^\n]*\n)*'
    r'[ \t]*writer\.write\((?P<var>[^,]+),\s*"(?P<tag>[^"]+)",\s*(?P<clazz>[^)]+)\)\s*;\s*\n'
    r'[ \t]*writer\.close\(\)\s*;\s*\n\s*\n'
    r'[ \t]*byte\[\]\s+rawData\s*=\s*baos\.toByteArray\(\)\s*;\s*\n'
    r'[ \t]*String\s+serializedEvent\s*=\s*new\s+String\(rawData\)\s*;\s*\n\s*\n'
    r'[ \t]*System\.out\.println\(serializedEvent\)\s*;',
    re.MULTILINE
)

# Regex to match javolution read block
read_block_re = re.compile(
    r'(?P<indent>[ \t]*)(?P<decl>(?:ByteArrayInputStream\s+bais\s*=\s*new\s+ByteArrayInputStream\(rawData\)\s*;|bais\s*=\s*new\s+ByteArrayInputStream\(rawData\)\s*;))\s*\n'
    r'[ \t]*XMLObjectReader\s+reader\s*=\s*XMLObjectReader\.newInstance\(bais\)\s*;\s*\n'
    r'[ \t]*(?P<copy>[A-Za-z0-9_<>]+)\s+(?P<copyVar>[a-zA-Z0-9_]+)\s*=\s*reader\.read\("(?P<tag>[^"]+)",\s*(?P<clazz>[^)]+)\)\s*;',
    re.MULTILINE
)

# Regex to remove imports
import_writer_re = re.compile(r'import\s+javolution\.xml\.XMLObjectWriter;\s*\n')
import_reader_re = re.compile(r'import\s+javolution\.xml\.XMLObjectReader;\s*\n')
import_baos_re = re.compile(r'import\s+java\.io\.ByteArrayOutputStream;\s*\n')
import_bais_re = re.compile(r'import\s+java\.io\.ByteArrayInputStream;\s*\n')

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if 'XMLObjectWriter' not in content:
        return False

    original = content

    # Replace write blocks
    def replace_write(m):
        indent = m.group('indent')
        var = m.group('var').strip()
        # clazz = m.group('clazz').strip()
        return (
            f'{indent}XmlMapper xmlMapper = new XmlMapper();\n'
            f'{indent}xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);\n'
            f'{indent}String serializedEvent = xmlMapper.writeValueAsString({var});\n\n'
            f'{indent}System.out.println(serializedEvent);'
        )

    content = write_block_re.sub(replace_write, content)

    # Replace read blocks
    def replace_read(m):
        indent = m.group('indent')
        clazz = m.group('clazz').strip()
        copy_var = m.group('copyVar').strip()
        return (
            f'{indent}{clazz} {copy_var} = xmlMapper.readValue(serializedEvent, {clazz});'
        )

    content = read_block_re.sub(replace_read, content)

    # Add Jackson imports if not present
    if 'XmlMapper' not in content:
        jackson_imports = (
            'import com.fasterxml.jackson.dataformat.xml.XmlMapper;\n'
            'import com.fasterxml.jackson.databind.SerializationFeature;\n'
        )
        # Insert after package line or after existing imports
        if 'import ' in content:
            # Find last import and insert after
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

    # Remove unused imports
    content = import_writer_re.sub('', content)
    content = import_reader_re.sub('', content)
    # Keep BAOS/BAIS if still used elsewhere (rare), but remove if no longer present
    if 'ByteArrayOutputStream' not in content:
        content = import_baos_re.sub('', content)
    if 'ByteArrayInputStream' not in content:
        content = import_bais_re.sub('', content)

    if content == original:
        return False

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    return True


def main():
    for name, rel_path in modules:
        test_dir = os.path.join(base_dir, rel_path, 'src', 'test', 'java')
        if not os.path.isdir(test_dir):
            print(f'Skipping {name}: {test_dir} not found')
            continue
        count = 0
        for root, _, files in os.walk(test_dir):
            for fname in files:
                if not fname.endswith('.java'):
                    continue
                filepath = os.path.join(root, fname)
                with open(filepath, 'r', encoding='utf-8') as f:
                    text = f.read()
                if 'XMLObjectWriter' not in text and 'XMLObjectReader' not in text:
                    continue
                if process_file(filepath):
                    count += 1
                    print(f'  Modified: {filepath}')
        print(f'{name}: modified {count} files')


if __name__ == '__main__':
    main()
