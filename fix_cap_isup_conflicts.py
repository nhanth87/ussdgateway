import os
import re

files = [
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/DigitsImpl.java", "genericDigits", "getGenericDigits"),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/BearerCapImpl.java", "userServiceInformation", "getUserServiceInformation"),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CalledPartyNumberCapImpl.java", "isupCalledPartyNumber", "getCalledPartyNumber"),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CauseCapImpl.java", "isupCauseIndicators", "getCauseIndicators"),
]

for path, local_name, getter_name in files:
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # Add import JsonIgnore if missing
    if "import com.fasterxml.jackson.annotation.JsonIgnore;" not in content:
        content = content.replace(
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;",
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;\nimport com.fasterxml.jackson.annotation.JsonIgnore;"
        )

    # Remove @JacksonXmlProperty from field data
    content = re.sub(
        rf'\s*@JacksonXmlProperty\(localName = "{local_name}"\)\n(\s+private byte\[\] data;)',
        r'\1',
        content
    )

    # Add @JacksonXmlProperty to getData() if not already
    if f'@JacksonXmlProperty(localName = "{local_name}")' not in content or 'getData()' in content:
        content = re.sub(
            r'(\s+@Override\n\s+public byte\[\] getData\(\))',
            rf'    @JacksonXmlProperty(localName = "{local_name}")\n\1',
            content
        )

    # Add @JsonIgnore to the specific getter
    pattern = rf'(\s+public .+ {getter_name}\(\).*)'
    if f'@JsonIgnore\n{getter_name}' not in content.replace(' ', '').replace('\n', ''):
        content = re.sub(pattern, r'    @JsonIgnore\n\1', content, count=1)

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Fixed", os.path.basename(path))
