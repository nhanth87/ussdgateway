import os

netty_modules = [
    "netty-common",
    "netty-buffer",
    "netty-transport",
    "netty-transport-sctp",
    "netty-handler",
    "netty-codec",
    "netty-codec-base",
]

pom_files = [
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap/tcap-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/mtp/mtp-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/sniffer/sniffer-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/api/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap-ansi/tcap-ansi-impl/pom.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/oam/common/statistics/impl/pom.xml",
]

for path in pom_files:
    with open(path, 'r', newline='') as f:
        lines = f.readlines()
    
    new_lines = []
    i = 0
    replaced = False
    while i < len(lines):
        line = lines[i]
        if '<artifactId>netty-all</artifactId>' in line:
            # Find the indentation of the <dependency> line
            dep_idx = i - 1
            while dep_idx >= 0 and '<dependency>' not in lines[dep_idx]:
                dep_idx -= 1
            # Check if there's a comment line before <dependency>
            comment_idx = dep_idx - 1
            has_comment = comment_idx >= 0 and '<!-- netty -->' in lines[comment_idx]
            start_idx = comment_idx if has_comment else dep_idx
            
            # Find the end of this dependency block (</dependency>)
            end_idx = i
            while end_idx < len(lines) and '</dependency>' not in lines[end_idx]:
                end_idx += 1
            
            indent = ''
            if start_idx >= 0:
                # Determine indentation from the <dependency> or comment line
                ref_line = lines[dep_idx] if dep_idx >= 0 else line
                stripped = ref_line.lstrip()
                if stripped:
                    indent = ref_line[:len(ref_line) - len(stripped)]
            
            # Generate replacement
            if has_comment:
                new_lines.append(lines[comment_idx])  # keep <!-- netty --> comment
            for mod in netty_modules:
                new_lines.append(f"{indent}<dependency>\n")
                new_lines.append(f"{indent}\t<groupId>io.netty</groupId>\n")
                new_lines.append(f"{indent}\t<artifactId>{mod}</artifactId>\n")
                new_lines.append(f"{indent}\t<version>${'{netty.version}'}</version>\n")
                new_lines.append(f"{indent}</dependency>\n")
            
            i = end_idx + 1
            replaced = True
            continue
        new_lines.append(line)
        i += 1
    
    with open(path, 'w', newline='') as f:
        f.writelines(new_lines)
    print(f"{'REPLACED' if replaced else 'NO CHANGE'}: {path}")

# map/load/pom.xml - just remove netty-all dependency block
map_load_path = "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/pom.xml"
with open(map_load_path, 'r', newline='') as f:
    lines = f.readlines()
new_lines = []
i = 0
replaced = False
while i < len(lines):
    if '<artifactId>netty-all</artifactId>' in lines[i]:
        # Find start of dependency block
        start = i - 2  # likely <dependency> and maybe comment
        while start >= 0 and '<dependency>' not in lines[start]:
            start -= 1
        # Check comment
        comment_start = start
        if start > 0 and '<!-- netty -->' in lines[start - 1]:
            comment_start = start - 1
        # Find end
        end = i
        while end < len(lines) and '</dependency>' not in lines[end]:
            end += 1
        # skip from comment_start to end
        i = end + 1
        replaced = True
        continue
    new_lines.append(lines[i])
    i += 1
with open(map_load_path, 'w', newline='') as f:
    f.writelines(new_lines)
print(f"{'REPLACED' if replaced else 'NO CHANGE'}: {map_load_path}")

# Assembly descriptors
assembly_files = [
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tools/trace-parser/bootstrap/src/main/assembly/descriptor.xml",
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tools/simulator/bootstrap/src/main/assembly/descriptor.xml",
]
for path in assembly_files:
    with open(path, 'r', newline='') as f:
        lines = f.readlines()
    new_lines = []
    replaced = False
    for line in lines:
        if '<include>io.netty:netty-all</include>' in line:
            indent = line[:len(line) - len(line.lstrip())]
            for mod in netty_modules:
                new_lines.append(f"{indent}<include>io.netty:{mod}</include>\n")
            replaced = True
        else:
            new_lines.append(line)
    with open(path, 'w', newline='') as f:
        f.writelines(new_lines)
    print(f"{'REPLACED' if replaced else 'NO CHANGE'}: {path}")
