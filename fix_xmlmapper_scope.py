import os
import re

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        text = f.read()

    if 'xmlMapper.writeValueAsString' not in text:
        return False

    original = text

    # Remove existing XmlMapper declarations
    text = re.sub(r'[ \t]*XmlMapper xmlMapper = new XmlMapper\(\);\s*\n', '', text)
    text = re.sub(r'[ \t]*xmlMapper\.enable\(SerializationFeature\.INDENT_OUTPUT\);\s*\n', '', text)

    # Replace every usage with inline declaration
    text = re.sub(
        r'([ \t]+)String serializedEvent = xmlMapper\.writeValueAsString\(',
        r'\1XmlMapper xmlMapper = new XmlMapper();\n'
        r'\1xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);\n'
        r'\1String serializedEvent = xmlMapper.writeValueAsString(',
        text
    )

    if text == original:
        return False

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)
    return True


def main():
    for root, _, files in os.walk(base_dir):
        if 'src/test/java' not in root:
            continue
        for fname in files:
            if not fname.endswith('.java'):
                continue
            filepath = os.path.join(root, fname)
            if fix_file(filepath):
                print('Fixed', filepath)


if __name__ == '__main__':
    main()
