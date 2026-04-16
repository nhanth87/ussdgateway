import os

# Standard replacement for netty-all dependency block
# We try to preserve indentation from the original file

def make_replacement(indent):
    modules = [
        "netty-common",
        "netty-buffer",
        "netty-transport",
        "netty-transport-sctp",
        "netty-handler",
        "netty-codec",
        "netty-codec-base",
    ]
    lines = ["<!-- netty -->"]
    for mod in modules:
        lines.append("<dependency>")
        lines.append("<groupId>io.netty</groupId>")
        lines.append(f"<artifactId>{mod}</artifactId>")
        lines.append("<version>${netty.version}</version>")
        lines.append("</dependency>")
    return "\n".join(f"{indent}{l}" for l in lines)

# File-specific replacements
replacements = []

# 1. m3ua/impl/pom.xml - has netty-all + netty-transport-sctp
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/pom.xml",
    """        <!-- netty -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-sctp</artifactId>
            <version>${netty.version}</version>
        </dependency>""",
    """        <!-- netty -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-common</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-buffer</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-sctp</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-handler</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-codec</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-codec-base</artifactId>
            <version>${netty.version}</version>
        </dependency>"""
))

# 2. tcap/tcap-impl/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap/tcap-impl/pom.xml",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>""",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 3. mtp/mtp-impl/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/mtp/mtp-impl/pom.xml",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>""",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 4. sniffer/sniffer-impl/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/sniffer/sniffer-impl/pom.xml",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>		""",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 5. m3ua/api/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/api/pom.xml",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>""",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 6. tcap-ansi/tcap-ansi-impl/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tcap-ansi/tcap-ansi-impl/pom.xml",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>""",
    """			<!-- netty -->
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 7. oam/common/statistics/impl/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/oam/common/statistics/impl/pom.xml",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>""",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-buffer</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-transport-sctp</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-handler</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-codec-base</artifactId>
				<version>${netty.version}</version>
			</dependency>"""
))

# 8. service/wildfly/modules/pom.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/service/wildfly/modules/pom.xml",
    """        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>${netty.version}</version>
        </dependency>""",
    """        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-common</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-buffer</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-sctp</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-handler</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-codec</artifactId>
            <version>${netty.version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-codec-base</artifactId>
            <version>${netty.version}</version>
        </dependency>"""
))

# 9. map/load/pom.xml - just remove netty-all since individual modules already exist below
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/pom.xml",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
				<version>${netty.version}</version>
			</dependency>
			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>""",
    """			<dependency>
				<groupId>io.netty</groupId>
				<artifactId>netty-common</artifactId>"""
))

# 10. tools/trace-parser/bootstrap/src/main/assembly/descriptor.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tools/trace-parser/bootstrap/src/main/assembly/descriptor.xml",
    """					<include>io.netty:netty-all</include>""",
    """					<include>io.netty:netty-common</include>
					<include>io.netty:netty-buffer</include>
					<include>io.netty:netty-transport</include>
					<include>io.netty:netty-transport-sctp</include>
					<include>io.netty:netty-handler</include>
					<include>io.netty:netty-codec</include>
					<include>io.netty:netty-codec-base</include>"""
))

# 11. tools/simulator/bootstrap/src/main/assembly/descriptor.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/tools/simulator/bootstrap/src/main/assembly/descriptor.xml",
    """					<include>io.netty:netty-all</include>""",
    """					<include>io.netty:netty-common</include>
					<include>io.netty:netty-buffer</include>
					<include>io.netty:netty-transport</include>
					<include>io.netty:netty-transport-sctp</include>
					<include>io.netty:netty-handler</include>
					<include>io.netty:netty-codec</include>
					<include>io.netty:netty-codec-base</include>"""
))

# 12. service/wildfly/modules/src/main/module/module.xml
replacements.append((
    "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/service/wildfly/modules/src/main/module/module.xml",
    """        <resource-root path="netty-all-${netty.version}.jar"/>""",
    """        <resource-root path="netty-common-${netty.version}.jar"/>
        <resource-root path="netty-buffer-${netty.version}.jar"/>
        <resource-root path="netty-transport-${netty.version}.jar"/>
        <resource-root path="netty-transport-sctp-${netty.version}.jar"/>
        <resource-root path="netty-handler-${netty.version}.jar"/>
        <resource-root path="netty-codec-${netty.version}.jar"/>
        <resource-root path="netty-codec-base-${netty.version}.jar"/>"""
))

success = []
not_found = []
for path, old, new in replacements:
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    if old in content:
        content = content.replace(old, new)
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        success.append(path)
    else:
        not_found.append(path)

print("SUCCESS:")
for p in success:
    print(f"  {p}")
print("NOT FOUND:")
for p in not_found:
    print(f"  {p}")
