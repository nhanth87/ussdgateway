import os
import re

files = [
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/DigitsImpl.java", "genericDigits", "genericNumber"),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/BearerCapImpl.java", "userServiceInformation", None),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CalledPartyNumberCapImpl.java", "calledPartyNumber", None),
    ("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CauseCapImpl.java", "causeIndicators", None),
]

for path, prop1, prop2 in files:
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # Add import JsonIgnoreProperties if missing
    if "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;" not in content:
        content = content.replace(
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;",
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;\nimport com.fasterxml.jackson.annotation.JsonIgnoreProperties;"
        )

    # Build annotation string
    if prop2:
        anno = f'@JsonIgnoreProperties({{"{prop1}", "{prop2}"}})'
    else:
        anno = f'@JsonIgnoreProperties("{prop1}")'

    # Add before @JacksonXmlRootElement if not present
    if anno not in content:
        content = content.replace(
            '@JacksonXmlRootElement',
            f'{anno}\n@JacksonXmlRootElement'
        )

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Fixed", os.path.basename(path))
