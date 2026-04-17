#!/usr/bin/env python3
"""
Fix remaining Javolution XML blocks where variables are reused (no type declarations).
"""
import os
import re

BASE_DIR = r"C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java"

# Find all test files that still contain XMLObjectWriter or XMLObjectReader
test_files = []
for root, dirs, files in os.walk(BASE_DIR):
    for f in files:
        if f.endswith("Test.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            if "XMLObjectWriter" in content or "XMLObjectReader" in content:
                test_files.append(path)

print(f"Found {len(test_files)} files with remaining Javolution blocks")

# Pattern for reused variables (no type declarations)
# This handles cases like:
# baos = new ByteArrayOutputStream();
# writer = XMLObjectWriter.newInstance(baos);
# writer.setIndentation("\t");
# writer.write(original, "name", Class.class);
# writer.close();
# rawData = baos.toByteArray();
# serializedEvent = new String(rawData);
# System.out.println(serializedEvent);
# bais = new ByteArrayInputStream(rawData);
# reader = XMLObjectReader.newInstance(bais);
# Class copy = reader.read("name", Class.class);
REUSE_PATTERN = re.compile(
    r"(?P<indent>\s*)baos = new ByteArrayOutputStream\(\);\s*\n"
    r"\s*writer = XMLObjectWriter\.newInstance\(baos\);\s*\n"
    r"(?:\s*//[^\n]*\n|\s*writer\.setIndentation\([^\n]*\n)*"
    r"\s*writer\.write\((?P<orig>\w+), \"(?P<tag>[^\"]+)\", (?P<class>[\w\.]+\.class)\);\s*\n"
    r"\s*writer\.close\(\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*rawData = baos\.toByteArray\(\);\s*\n"
    r"\s*serializedEvent = new String\(rawData\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*System\.out\.println\(serializedEvent\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*bais = new ByteArrayInputStream\(rawData\);\s*\n"
    r"\s*reader = XMLObjectReader\.newInstance\(bais\);\s*\n"
    r"\s*(?P<class2>[\w\.]+) (?P<copy>\w+) = reader\.read\(\"(?P<tag2>[^\"]+)\", (?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)

# Even more lenient pattern that allows extra lines between key statements
REUSE_PATTERN2 = re.compile(
    r"(?P<indent>\s*)baos = new ByteArrayOutputStream\(\);[^\n]*\n"
    r"\s*writer = XMLObjectWriter\.newInstance\(baos\);[^\n]*\n"
    r"(?:\s*//[^\n]*\n|\s*writer\.setIndentation\([^\n]*\n|\s*writer\.write\([^\n]*\n|\s*writer\.close\(\);[^\n]*\n)*"
    r"(?:\s*rawData = baos\.toByteArray\(\);[^\n]*\n|\s*serializedEvent = new String\(rawData\);[^\n]*\n|\s*System\.out\.println\(serializedEvent\);[^\n]*\n)*"
    r"\s*bais = new ByteArrayInputStream\(rawData\);[^\n]*\n"
    r"\s*reader = XMLObjectReader\.newInstance\(bais\);[^\n]*\n"
    r"\s*(?P<class2>[\w\.]+) (?P<copy>\w+) = reader\.read\(\"(?P<tag2>[^\"]+)\", (?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)


def replace_reuse_block(content, pattern):
    changed = True
    while changed:
        changed = False
        m = pattern.search(content)
        if m:
            orig_match = None
            class_match = None
            # Try to extract orig and class from the writer.write line in the matched text
            matched_text = m.group(0)
            write_m = re.search(r'writer\.write\((\w+),\s*"([^"]+)",\s*([\w\.]+\.class)\)', matched_text)
            if write_m:
                orig_name = write_m.group(1)
                cls = write_m.group(3)
            else:
                orig_name = "original"
                cls = m.group("class3")
            
            copy_name = m.group("copy")
            cls_name = cls.replace('.class', '')
            
            replacement = f"""XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String serializedEvent = xmlMapper.writeValueAsString({orig_name});
        System.out.println(serializedEvent);

        {cls_name} {copy_name} = xmlMapper.readValue(serializedEvent, {cls});"""
            content = content[:m.start()] + replacement + content[m.end():]
            changed = True
    return content


changed_files = 0
for path in test_files:
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    original = content
    
    content = replace_reuse_block(content, REUSE_PATTERN)
    content = replace_reuse_block(content, REUSE_PATTERN2)
    
    if content != original:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(content)
        changed_files += 1
        print(f"Fixed: {path}")

print(f"\nTotal files fixed: {changed_files}")
