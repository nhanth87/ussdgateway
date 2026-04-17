#!/usr/bin/env python3
"""
Fix ALL remaining Javolution XML blocks, handling reused variables without type declarations.
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

# Pattern that handles both typed and untyped variable reuse
# We match from baos = ... to the reader.read(...) line
PATTERN = re.compile(
    r"(?P<indent>\s*)(?:(?:ByteArrayOutputStream)\s+)?baos\s*=\s*new\s+ByteArrayOutputStream\(\);[^\n]*\n"
    r"\s*(?:(?:XMLObjectWriter)\s+)?writer\s*=\s*XMLObjectWriter\.newInstance\(baos\);[^\n]*\n"
    r"(?:\s*//[^\n]*\n|\s*writer\.setIndentation\([^\n]*\n|\s*writer\.write\([^\n]*\n|\s*writer\.close\(\);[^\n]*\n|\s*\n)*"
    r"(?:\s*(?:(?:byte\[\])\s+)?rawData\s*=\s*baos\.toByteArray\(\);[^\n]*\n|\s*(?:(?:String)\s+)?serializedEvent\s*=\s*new\s+String\(rawData\);[^\n]*\n|\s*System\.out\.println\(serializedEvent\);[^\n]*\n|\s*\n)*"
    r"\s*(?:(?:ByteArrayInputStream)\s+)?bais\s*=\s*new\s+ByteArrayInputStream\(rawData\);[^\n]*\n"
    r"\s*(?:(?:XMLObjectReader)\s+)?reader\s*=\s*XMLObjectReader\.newInstance\(bais\);[^\n]*\n"
    r"\s*(?:(?P<class2>[\w\.]+)\s+)?(?P<copy>\w+)\s*=\s*reader\.read\(\"(?P<tag2>[^\"]+)\",\s*(?P<class3>[\w\.]+\.class)\);",
    re.MULTILINE,
)


def process_file(path):
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    original = content
    
    # Try pattern repeatedly
    while True:
        m = PATTERN.search(content)
        if not m:
            break
        matched_text = m.group(0)
        
        # Extract orig and class from writer.write line
        write_m = re.search(r'writer\.write\((\w+),\s*"([^"]+)",\s*([\w\.]+\.class)\)', matched_text)
        if write_m:
            orig_name = write_m.group(1)
            cls = write_m.group(3)
        else:
            # fallback
            orig_name = "original"
            cls = m.group("class3")
        
        copy_name = m.group("copy")
        cls_name = cls.replace('.class', '')
        
        # Determine if copy was declared with type in this block
        had_type = m.group("class2") is not None
        copy_decl = f"{cls_name} {copy_name}" if had_type else copy_name
        
        replacement = f"""XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String serializedEvent = xmlMapper.writeValueAsString({orig_name});
        System.out.println(serializedEvent);

        {copy_decl} = xmlMapper.readValue(serializedEvent, {cls});"""
        content = content[:m.start()] + replacement + content[m.end():]
    
    if content != original:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(content)
        return True
    return False


changed_files = 0
for path in test_files:
    if process_file(path):
        changed_files += 1
        print(f"Fixed: {path}")

print(f"\nTotal files fixed: {changed_files}")
