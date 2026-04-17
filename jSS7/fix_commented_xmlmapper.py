#!/usr/bin/env python3
"""
Fix cases where XmlMapper declaration got merged with a comment line.
"""
import os

BASE_DIR = r"C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\test\java"

test_files = []
for root, dirs, files in os.walk(BASE_DIR):
    for f in files:
        if f.endswith("Test.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            if "// Writes the area to a file.XmlMapper xmlMapper = new XmlMapper();" in content:
                test_files.append(path)

print(f"Found {len(test_files)} files to fix")

for path in test_files:
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    
    content = content.replace(
        "// Writes the area to a file.XmlMapper xmlMapper = new XmlMapper();",
        "// Writes the area to a file.\n        XmlMapper xmlMapper = new XmlMapper();"
    )
    
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(content)
    print(f"Fixed: {path}")

print(f"\nTotal files fixed: {len(test_files)}")
