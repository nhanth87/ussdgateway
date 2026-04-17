import os
import re
from pathlib import Path

BASE_DIR = Path("C:/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java")
REPORT_PATH = Path("C:/Users/Windows/Desktop/ethiopia-working-dir/cap_impl_jackson_annotations_report.md")

JACKSON_ANNOTATION_PATTERN = re.compile(
    r'@(JacksonXmlProperty|JacksonXmlRootElement|JsonIgnore|JsonProperty|JsonRootName|JsonAnyGetter|JsonAnySetter|JsonCreator|JsonValue|JsonSerialize|JsonDeserialize)'
)

# Regex to capture annotation lines and the following declaration line
ANNOTATION_BLOCK_RE = re.compile(
    r'^(\s*)((?:@[A-Za-z0-9_]+(?:\([^)]*\))?\s*)*@(JacksonXmlProperty|JacksonXmlRootElement|JsonIgnore|JsonProperty|JsonRootName|JsonAnyGetter|JsonAnySetter|JsonCreator|JsonValue|JsonSerialize|JsonDeserialize)(?:\([^)]*\))?\s*(?:@[A-Za-z0-9_]+(?:\([^)]*\))?\s*)*)(.*?;|\{)?$',
    re.MULTILINE
)

def extract_annotations(content):
    """Extract Jackson annotations with their targets."""
    results = []
    lines = content.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i]
        m = JACKSON_ANNOTATION_PATTERN.search(line)
        if m:
            # collect all annotation lines
            ann_lines = [line]
            j = i + 1
            # keep consuming lines that look like annotations or are blank/comment
            while j < len(lines):
                next_line = lines[j].strip()
                if next_line == '' or next_line.startswith('//'):
                    j += 1
                    continue
                if next_line.startswith('@'):
                    ann_lines.append(lines[j])
                    j += 1
                    continue
                break
            # the declaration line is the first non-annotation, non-comment, non-empty line
            if j < len(lines):
                decl_line = lines[j].strip()
                # Determine target type and name
                target = parse_target(decl_line)
                for al in ann_lines:
                    for ann_match in JACKSON_ANNOTATION_PATTERN.finditer(al):
                        ann_name = ann_match.group(1)
                        # extract full annotation text
                        ann_text = extract_full_annotation(al, ann_match.start())
                        results.append({
                            'annotation': ann_name,
                            'full': ann_text.strip(),
                            'target': target,
                            'decl_line': decl_line
                        })
                i = j + 1
                continue
        i += 1
    return results

def extract_full_annotation(line, start_idx):
    """Given a line and the index of '@', extract the full annotation including parentheses."""
    paren_depth = 0
    in_ann = False
    result = []
    for idx in range(start_idx, len(line)):
        ch = line[idx]
        if ch == '@':
            in_ann = True
        if in_ann:
            result.append(ch)
            if ch == '(':
                paren_depth += 1
            elif ch == ')':
                paren_depth -= 1
                if paren_depth == 0:
                    break
            elif paren_depth == 0 and ch.isalnum():
                # allow simple identifier to finish when whitespace or next @ encountered later
                # but we break on whitespace only if next non-space is @ or nothing
                pass
    # if no parens, break at first non-identifier after annotation name
    s = ''.join(result).strip()
    if '(' not in s:
        # take until whitespace or end
        parts = s.split()
        return parts[0] if parts else s
    return s

def parse_target(decl_line):
    """Parse a Java declaration to determine target type and name."""
    decl_line = decl_line.strip()
    if decl_line.endswith(';'):
        decl_line = decl_line[:-1].strip()
    # Remove generic parameters for easier parsing
    # Simple regex-based parsing
    # class declaration
    if re.match(r'^(public\s+|protected\s+|private\s+|static\s+|abstract\s+|final\s+)*class\s+', decl_line):
        m = re.search(r'class\s+(\w+)', decl_line)
        return ('class', m.group(1) if m else 'UnknownClass')
    # interface declaration
    if re.match(r'^(public\s+|protected\s+|private\s+|static\s+|abstract\s+|final\s+)*interface\s+', decl_line):
        m = re.search(r'interface\s+(\w+)', decl_line)
        return ('interface', m.group(1) if m else 'UnknownInterface')
    # enum declaration
    if re.match(r'^(public\s+|protected\s+|private\s+|static\s+|abstract\s+|final\s+)*enum\s+', decl_line):
        m = re.search(r'enum\s+(\w+)', decl_line)
        return ('enum', m.group(1) if m else 'UnknownEnum')
    # method declaration (has parentheses)
    if '(' in decl_line and ')' in decl_line:
        # skip if it looks like a field initialized with lambda/new anonymous class - heuristics
        # It's a method if there is a word before '(' and after possible return type
        before_paren = decl_line.split('(')[0].strip()
        tokens = before_paren.split()
        if len(tokens) >= 1:
            return ('method', tokens[-1])
        return ('method', 'unknown')
    # field declaration
    tokens = decl_line.split()
    if len(tokens) >= 2:
        # last token before any assignment is the field name
        name = tokens[-1].split('=')[0].strip()
        return ('field', name)
    if len(tokens) == 1:
        return ('field', tokens[0])
    return ('unknown', decl_line)

def find_getter_for_field(content, field_name):
    """Find getter method name for a field."""
    # common patterns: getField, isField, hasField
    capitalized = field_name[0].upper() + field_name[1:]
    # Use [\w\[\]<>.,?]+ to match return types including arrays and generics
    type_pattern = r'[\w\[\]<>.,?]+'
    patterns = [
        rf'\b(public\s+{type_pattern}\s+)get{capitalized}\s*\(',
        rf'\b(public\s+{type_pattern}\s+)is{capitalized}\s*\(',
        rf'\b(public\s+{type_pattern}\s+)has{capitalized}\s*\('
    ]
    for pat in patterns:
        m = re.search(pat, content)
        if m:
            method_prefix = m.group(1) if m.lastindex else ''
            # determine implied property name from getter
            if field_name.startswith('is') and len(field_name) > 2 and field_name[2].isupper():
                # boolean field starting with is
                return f'get{capitalized}', field_name
            return m.group(0).strip().split()[-1].replace('(', ''), field_name
    return None, field_name

def implied_property_name(field_name):
    """Return the implied JavaBean property name for a field."""
    # if field starts with isXxx and is boolean-like, property name could be xxx or isXxx
    if field_name.startswith('is') and len(field_name) > 2 and field_name[2].isupper():
        return field_name[2].lower() + field_name[3:]
    return field_name

def analyze_conflicts(filepath, annotations, content):
    """Analyze potential Jackson conflicts for a file."""
    conflicts = []
    # Get class-level annotations
    class_anns = [a for a in annotations if a['target'][0] == 'class']
    # Group field annotations
    field_anns = {}
    for a in annotations:
        if a['target'][0] == 'field':
            field_anns.setdefault(a['target'][1], []).append(a)
    # Group method annotations
    method_anns = {}
    for a in annotations:
        if a['target'][0] == 'method':
            method_anns.setdefault(a['target'][1], []).append(a)

    # 1. JacksonXmlProperty on fields with mismatching getter
    for field, anns in field_anns.items():
        jackson_xml_props = [a for a in anns if a['annotation'] == 'JacksonXmlProperty']
        for jp in jackson_xml_props:
            # Check localName or property name
            local_name = None
            m_local = re.search(r'localName\s*=\s*"([^"]+)"', jp['full'])
            if m_local:
                local_name = m_local.group(1)
            getter_name, implied_name = find_getter_for_field(content, field)
            if getter_name:
                # REAL conflict: localName differs from field name, and a getter exists for the field.
                # Jackson will treat field (named localName) and getter (named field_name) as two separate properties.
                if local_name and local_name != field:
                    conflicts.append(
                        f"`@JacksonXmlProperty(localName='{local_name}')` on field `{field}` conflicts with getter `{getter_name}` — "
                        f"Jackson will create **two** properties (`{local_name}` from the field and `{field}` from the getter)."
                    )
                # If localName matches field name (or no localName), field and getter merge into one property -> no conflict.
            # Also check if getter has a different Jackson annotation
            if getter_name and getter_name in method_anns:
                conflicts.append(
                    f"Field `{field}` has `@JacksonXmlProperty` but getter `{getter_name}` also has Jackson annotations — potential serialization conflict."
                )

    # 2. @JacksonXmlRootElement combined with @JsonRootName or other root naming
    for ca in class_anns:
        if ca['annotation'] == 'JacksonXmlRootElement':
            if any(a['annotation'] == 'JsonRootName' for a in class_anns):
                conflicts.append(
                    f"Class has both `@JacksonXmlRootElement` and `@JsonRootName` — root element names may conflict between JSON and XML serialization."
                )

    # 3. @JsonIgnore combined with @JacksonXmlProperty or @JsonProperty on same element
    for target_name, anns in list(field_anns.items()) + list(method_anns.items()):
        has_ignore = any(a['annotation'] == 'JsonIgnore' for a in anns)
        has_property = any(a['annotation'] in ('JacksonXmlProperty', 'JsonProperty') for a in anns)
        if has_ignore and has_property:
            conflicts.append(
                f"Target `{target_name}` has both `@JsonIgnore` and `@JacksonXmlProperty`/`@JsonProperty` — `@JsonIgnore` usually wins, making the property annotation ineffective."
            )

    # 4. @JacksonXmlProperty on methods with mismatched implied names
    for method_name, anns in method_anns.items():
        jackson_xml_props = [a for a in anns if a['annotation'] == 'JacksonXmlProperty']
        for jp in jackson_xml_props:
            m_local = re.search(r'localName\s*=\s*"([^"]+)"', jp['full'])
            if m_local:
                local_name = m_local.group(1)
                # derive implied property from method name
                implied = ''
                if method_name.startswith('get') and len(method_name) > 3:
                    implied = method_name[3].lower() + method_name[4:]
                elif method_name.startswith('is') and len(method_name) > 2:
                    implied = method_name[2].lower() + method_name[3:]
                elif method_name.startswith('set') and len(method_name) > 3:
                    implied = method_name[3].lower() + method_name[4:]
                if implied and implied != local_name:
                    conflicts.append(
                        f"`@JacksonXmlProperty(localName='{local_name}')` on method `{method_name}` implies a different property name (`{implied}`) — may create duplicate properties."
                    )

    return conflicts

def main():
    java_files = list(BASE_DIR.rglob('*.java'))
    files_with_annotations = []
    for jf in java_files:
        content = jf.read_text(encoding='utf-8', errors='replace')
        if JACKSON_ANNOTATION_PATTERN.search(content):
            anns = extract_annotations(content)
            conflicts = analyze_conflicts(jf, anns, content)
            files_with_annotations.append({
                'path': jf,
                'annotations': anns,
                'conflicts': conflicts
            })

    # Sort for determinism
    files_with_annotations.sort(key=lambda x: str(x['path']))

    lines = []
    lines.append("# CAP-Impl Jackson Annotation Audit Report\n")
    lines.append(f"**Base Directory:** `{BASE_DIR}`\n")
    lines.append(f"**Files Analyzed:** {len(files_with_annotations)}\n")
    lines.append("---\n")

    total_conflicts = 0
    for entry in files_with_annotations:
        rel_path = entry['path'].relative_to(BASE_DIR)
        lines.append(f"## `{rel_path}`\n")
        # List annotations
        if entry['annotations']:
            lines.append("### Annotations\n")
            for a in entry['annotations']:
                lines.append(f"- **{a['annotation']}** — `{a['full']}` on {a['target'][0]} `{a['target'][1]}`")
            lines.append("")
        if entry['conflicts']:
            lines.append("### ⚠️ Potential Conflicts\n")
            for c in entry['conflicts']:
                lines.append(f"- {c}")
            lines.append("")
            total_conflicts += len(entry['conflicts'])
        else:
            lines.append("*No obvious conflicts detected.*\n")

    lines.append("---\n")
    lines.append(f"## Summary\n")
    lines.append(f"- **Total files with Jackson annotations:** {len(files_with_annotations)}")
    lines.append(f"- **Total potential conflicts identified:** {total_conflicts}")
    lines.append("")

    REPORT_PATH.write_text('\n'.join(lines), encoding='utf-8')
    print(f"Report written to {REPORT_PATH}")

if __name__ == '__main__':
    main()
