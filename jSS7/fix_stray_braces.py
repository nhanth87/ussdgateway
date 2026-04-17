#!/usr/bin/env python3
"""
Remove stray closing braces that were left behind when empty if (copy != null) blocks were removed.
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
            if "if (copy != null)" in content or "} catch (Exception e) {" in content:
                test_files.append(path)

print(f"Found {len(test_files)} files to check")

# Pattern: catch block closing followed immediately by a stray } on the next line (same indentation)
STRAY_BRACE_PATTERN = re.compile(
    r'(\s+\}\s*catch\s*\(\s*Exception\s+e\s*\)\s*\{\s*\n'
    r'(?:\s*//[^\n]*\n|\s*assert[^;]+;\s*\n)*'
    r'\s+\}\s*\n)'
    r'(\s+\}\s*\n)',
    re.MULTILINE,
)

fixed_files = 0
for path in test_files:
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    
    new_content = STRAY_BRACE_PATTERN.sub(r'\1', content)
    
    if new_content != content:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(new_content)
        fixed_files += 1
        print(f"Fixed: {path}")

print(f"\nTotal files fixed: {fixed_files}")
