import os
import re

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

test_method_re = re.compile(r'.*\b(public|protected)\s+void\s+(testXMLSerialize.*|testXMLSerializaion.*|testSerialization.*)\s*\*?\(.*')

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    if 'serializedEvent' not in ''.join(lines):
        return False

    # First pass: change all String serializedEvent = xmlMapper.writeValueAsString to simple assignment
    cleaned = []
    for line in lines:
        cleaned.append(re.sub(r'String serializedEvent = xmlMapper\.writeValueAsString\(', 'serializedEvent = xmlMapper.writeValueAsString(', line))

    # Second pass: in each test method, change the first occurrence back to declaration
    result = []
    i = 0
    in_test_method = False
    declared = False
    while i < len(cleaned):
        line = cleaned[i]
        if test_method_re.match(line):
            in_test_method = True
            declared = False
        if in_test_method and not declared:
            if 'serializedEvent = xmlMapper.writeValueAsString(' in line:
                line = line.replace('serializedEvent = xmlMapper.writeValueAsString(', 'String serializedEvent = xmlMapper.writeValueAsString(', 1)
                declared = True
        # crude method exit detection: closing brace at same indent as method signature
        if in_test_method and re.match(r'\s*}\s*\n', line):
            in_test_method = False
            declared = False
        result.append(line)
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
                print('Fixed serializedEvent:', filepath)


if __name__ == '__main__':
    main()
