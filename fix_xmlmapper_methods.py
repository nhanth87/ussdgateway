import os
import re

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

test_method_re = re.compile(r'.*\b(public|protected)\s+void\s+(testXMLSerialize.*|testXMLSerializaion.*|testSerialization.*)\s*\*?\(.*')

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    if 'xmlMapper.writeValueAsString' not in ''.join(lines):
        return False

    # First pass: remove all existing xmlMapper declarations
    cleaned = []
    for line in lines:
        if re.match(r'[ \t]*XmlMapper xmlMapper = new XmlMapper\(\);\s*\n', line):
            continue
        if re.match(r'[ \t]*xmlMapper\.enable\(SerializationFeature\.INDENT_OUTPUT\);\s*\n', line):
            continue
        cleaned.append(line)

    # Second pass: insert xmlMapper declaration after the opening brace of each test method
    result = []
    i = 0
    while i < len(cleaned):
        line = cleaned[i]
        result.append(line)
        if test_method_re.match(line):
            # Find the opening brace line
            if '{' in line:
                # brace on same line
                indent = len(line) - len(line.lstrip())
                spaces = line[:indent] + '    '
                result.append(f'{spaces}XmlMapper xmlMapper = new XmlMapper();\n')
                result.append(f'{spaces}xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);\n')
            else:
                i += 1
                result.append(cleaned[i])
                if '{' in cleaned[i]:
                    indent = len(cleaned[i]) - len(cleaned[i].lstrip())
                    spaces = cleaned[i][:indent] + '    '
                    result.append(f'{spaces}XmlMapper xmlMapper = new XmlMapper();\n')
                    result.append(f'{spaces}xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);\n')
        i += 1

    new_content = ''.join(result)
    old_content = ''.join(lines)
    if new_content == old_content:
        return False

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)
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
                print('Fixed method scope:', filepath)


if __name__ == '__main__':
    main()
