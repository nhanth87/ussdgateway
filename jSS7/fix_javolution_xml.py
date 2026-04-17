#!/usr/bin/env python3
"""
Batch-fix Javolution XML tests in cap/cap-impl to use Jackson XML instead.
"""
import os
import re
import glob

BASE_DIR = r"C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java"

# Find all test files that import XMLObjectWriter or XMLObjectReader
test_files = []
for root, dirs, files in os.walk(BASE_DIR):
    for f in files:
        if f.endswith("Test.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            if "XMLObjectWriter" in content or "XMLObjectReader" in content:
                test_files.append(path)

print(f"Found {len(test_files)} files to process")

JACKSON_IMPORTS = """import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;"""

# Regex for a Javolution write/read block
# This pattern captures the variable name of the original object, the XML element name, and the class name
WRITE_READ_PATTERN = re.compile(
    r"ByteArrayOutputStream baos = new ByteArrayOutputStream\(\);\s*\n"
    r"\s*XMLObjectWriter writer = XMLObjectWriter\.newInstance\(baos\);\s*\n"
    r"(?:\s*// writer\.setBinding\(binding\); // Optional\.\s*\n)?"
    r"\s*writer\.setIndentation\(\"\\t\"\); // Optional \(use tabulation for[^\n]*\n"
    r"\s*writer\.write\((?P<orig>\w+), \"(?P<tag>[^\"]+)\", (?P<class>[\w\.]+\.class)\);\s*\n"
    r"\s*writer\.close\(\);\s*\n"
    r"\s*\n"
    r"\s*byte\[\] rawData = baos\.toByteArray\(\);\s*\n"
    r"\s*String serializedEvent = new String\(rawData\);\s*\n"
    r"\s*\n"
    r"\s*System\.out\.println\(serializedEvent\);\s*\n"
    r"\s*\n"
    r"\s*ByteArrayInputStream bais = new ByteArrayInputStream\(rawData\);\s*\n"
    r"\s*XMLObjectReader reader = XMLObjectReader\.newInstance\(bais\);\s*\n"
    r"\s*(?P<class2>[\w\.]+) (?P<copy>\w+) = reader\.read\(\"(?P<tag2>[^\"]+)\", (?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)

# Simpler pattern for cases where comments or spacing differ slightly
WRITE_READ_PATTERN2 = re.compile(
    r"ByteArrayOutputStream baos = new ByteArrayOutputStream\(\);\s*\n"
    r"\s*XMLObjectWriter writer = XMLObjectWriter\.newInstance\(baos\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*writer\.write\((?P<orig>\w+), \"(?P<tag>[^\"]+)\", (?P<class>[\w\.]+\.class)\);\s*\n"
    r"\s*writer\.close\(\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*byte\[\] rawData = baos\.toByteArray\(\);\s*\n"
    r"\s*String serializedEvent = new String\(rawData\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*System\.out\.println\(serializedEvent\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*ByteArrayInputStream bais = new ByteArrayInputStream\(rawData\);\s*\n"
    r"\s*XMLObjectReader reader = XMLObjectReader\.newInstance\(bais\);\s*\n"
    r"\s*(?P<class2>[\w\.]+) (?P<copy>\w+) = reader\.read\(\"(?P<tag2>[^\"]+)\", (?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)

# Even more lenient pattern
WRITE_READ_PATTERN3 = re.compile(
    r"ByteArrayOutputStream baos = new ByteArrayOutputStream\(\);\s*\n"
    r"\s*XMLObjectWriter writer = XMLObjectWriter\.newInstance\(baos\);[^\n]*\n"
    r"(?:\s*//[^\n]*\n|\s*writer\.setIndentation\([^\n]*\n)*"
    r"\s*writer\.write\((?P<orig>\w+), \"(?P<tag>[^\"]+)\", (?P<class>[\w\.]+\.class)\);\s*\n"
    r"\s*writer\.close\(\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*byte\[\] rawData = baos\.toByteArray\(\);\s*\n"
    r"\s*String serializedEvent = new String\(rawData\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*System\.out\.println\(serializedEvent\);\s*\n"
    r"(?:[^\n]*\n)*?"
    r"\s*ByteArrayInputStream bais = new ByteArrayInputStream\(rawData\);\s*\n"
    r"\s*XMLObjectReader reader = XMLObjectReader\.newInstance\(bais\);\s*\n"
    r"\s*(?P<class2>[\w\.]+) (?P<copy>\w+) = reader\.read\(\"(?P<tag2>[^\"]+)\", (?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)

def replace_block(content, pattern, orig_name, tag_name, class_name, copy_name, start, end):
    replacement = f"""XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String serializedEvent = xmlMapper.writeValueAsString({orig_name});
        System.out.println(serializedEvent);

        {class_name} {copy_name} = xmlMapper.readValue(serializedEvent, {class_name}.class);"""
    return content[:start] + replacement + content[end:]


def process_file(path):
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()

    original_content = content

    # Remove Javolution imports
    content = re.sub(
        r"import javolution\.xml\.XMLObjectReader;\s*\n",
        "",
        content,
    )
    content = re.sub(
        r"import javolution\.xml\.XMLObjectWriter;\s*\n",
        "",
        content,
    )

    # Add Jackson imports if not present
    if "XmlMapper" not in content:
        # Find a good place to insert imports (after the last import)
        last_import_match = None
        for m in re.finditer(r"^import .*;$", content, re.MULTILINE):
            last_import_match = m
        if last_import_match:
            insert_pos = last_import_match.end()
            content = content[:insert_pos] + "\n" + JACKSON_IMPORTS + content[insert_pos:]
        else:
            # insert after package declaration
            pkg_match = re.search(r"^package .*;$", content, re.MULTILINE)
            if pkg_match:
                insert_pos = pkg_match.end()
                content = content[:insert_pos] + "\n\n" + JACKSON_IMPORTS + content[insert_pos:]

    # Try replacing write/read blocks
    patterns = [WRITE_READ_PATTERN, WRITE_READ_PATTERN2, WRITE_READ_PATTERN3]
    for pattern in patterns:
        # Keep trying until no more matches
        while True:
            m = pattern.search(content)
            if not m:
                break
            orig_name = m.group("orig")
            tag_name = m.group("tag")
            class_name = m.group("class")
            copy_name = m.group("copy")
            class2 = m.group("class2")
            class3 = m.group("class3")
            # Use the short class name from class3 if available, else from class
            cls = class_name
            if class3 and not class3.endswith(".class"):
                cls = class3
            elif class2 and not class2.endswith(".class"):
                cls = class2 + ".class"
            
            replacement = f"""XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String serializedEvent = xmlMapper.writeValueAsString({orig_name});
        System.out.println(serializedEvent);

        {cls.replace('.class', '')} {copy_name} = xmlMapper.readValue(serializedEvent, {cls});"""
            content = content[:m.start()] + replacement + content[m.end():]

    if content != original_content:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(content)
        return True
    return False


changed = 0
for path in test_files:
    if process_file(path):
        changed += 1
        print(f"Changed: {path}")

print(f"\nTotal files changed: {changed}")
