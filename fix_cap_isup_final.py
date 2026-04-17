import os

fixes = [
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/DigitsImpl.java",
        "local_name": "genericDigits",
        "getter_to_ignore": [
            "    @Override\n    public GenericDigits getGenericDigits() throws CAPException {",
            "    @Override\n    public GenericNumber getGenericNumber() throws CAPException {",
        ],
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/BearerCapImpl.java",
        "local_name": "userServiceInformation",
        "getter_to_ignore": [
            "    @Override\n    public UserServiceInformation getUserServiceInformation() throws CAPException {",
        ],
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CalledPartyNumberCapImpl.java",
        "local_name": "isupCalledPartyNumber",
        "getter_to_ignore": [
            "    @Override\n    public CalledPartyNumber getCalledPartyNumber() throws CAPException {",
        ],
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CauseCapImpl.java",
        "local_name": "isupCauseIndicators",
        "getter_to_ignore": [
            "    @Override\n    public CauseIndicators getCauseIndicators() throws CAPException {",
        ],
    },
]

for fix in fixes:
    with open(fix["path"], "r", encoding="utf-8") as f:
        content = f.read()

    # Add JsonIgnore import if missing
    if "import com.fasterxml.jackson.annotation.JsonIgnore;" not in content:
        content = content.replace(
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;",
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;\nimport com.fasterxml.jackson.annotation.JsonIgnore;"
        )

    # Remove @JacksonXmlProperty from field data
    field_anno = f'    @JacksonXmlProperty(localName = "{fix["local_name"]}")\n    private byte[] data;'
    if field_anno in content:
        content = content.replace(field_anno, "    private byte[] data;")

    # Add @JacksonXmlProperty to getData()
    getter_target = "    @Override\n    public byte[] getData() {"
    getter_replacement = f'    @JacksonXmlProperty(localName = "{fix["local_name"]}")\n{getter_target}'
    if getter_replacement not in content:
        content = content.replace(getter_target, getter_replacement, 1)

    # Add @JsonIgnore to specific getters
    for getter in fix["getter_to_ignore"]:
        ignored = "    @JsonIgnore\n" + getter
        if ignored not in content:
            content = content.replace(getter, ignored, 1)

    with open(fix["path"], "w", encoding="utf-8") as f:
        f.write(content)
    print("Fixed", os.path.basename(fix["path"]))
