#!/usr/bin/env python3
"""
Fix duplicate variable declarations within test methods.
"""
import os
import re

BASE_DIR = r"C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java"

test_files = []
for root, dirs, files in os.walk(BASE_DIR):
    for f in files:
        if f.endswith("Test.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            if "XmlMapper" in content:
                test_files.append(path)

print(f"Found {len(test_files)} files to check")

# Match @Test public void testXML...() throws Exception { ... }
METHOD_PATTERN = re.compile(
    r"(?P<prefix>^\s*@Test\(groups = \{[^}]+\}\)\s*\n"
    r"\s*public void testXML\w+\(\) throws Exception \{)"
    r"(?P<body>(?:.|\n)*?)"
    r"(?P<suffix>^\s*\})",
    re.MULTILINE,
)


def fix_method_body(body):
    lines = body.splitlines(keepends=True)
    result_lines = []
    seen_xmlmapper = False
    seen_serialized_event = False
    i = 0
    while i < len(lines):
        line = lines[i]
        # Check for XmlMapper xmlMapper = new XmlMapper();
        if re.search(r'XmlMapper xmlMapper = new XmlMapper\(\);', line):
            if seen_xmlmapper:
                # Skip this line and the next xmlMapper.enable line if present
                if i + 1 < len(lines) and 'xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);' in lines[i+1]:
                    i += 2
                    continue
                else:
                    i += 1
                    continue
            else:
                seen_xmlmapper = True
                result_lines.append(line)
                i += 1
                continue
        # Check for xmlMapper.enable(SerializationFeature.INDENT_OUTPUT); 
        if re.search(r'xmlMapper\.enable\(SerializationFeature\.INDENT_OUTPUT\);', line):
            if seen_xmlmapper:
                # Check if we already have one in result_lines
                has_enable = any('xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);' in l for l in result_lines)
                if has_enable:
                    i += 1
                    continue
            result_lines.append(line)
            i += 1
            continue
        # Check for String serializedEvent = xmlMapper.writeValueAsString
        if re.search(r'String serializedEvent = xmlMapper\.writeValueAsString', line):
            if seen_serialized_event:
                new_line = line.replace('String serializedEvent = ', 'serializedEvent = ')
                result_lines.append(new_line)
            else:
                seen_serialized_event = True
                result_lines.append(line)
            i += 1
            continue
        result_lines.append(line)
        i += 1
    return ''.join(result_lines)


def process_file(path):
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    
    def replacer(m):
        prefix = m.group("prefix")
        body = m.group("body")
        suffix = m.group("suffix")
        fixed_body = fix_method_body(body)
        return prefix + fixed_body + suffix
    
    new_content = METHOD_PATTERN.sub(replacer, content)
    
    if new_content != content:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(new_content)
        return True
    return False


changed_files = 0
for path in test_files:
    if process_file(path):
        changed_files += 1
        print(f"Fixed: {path}")

print(f"\nTotal files fixed: {changed_files}")
