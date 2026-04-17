import os

fixes = [
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/DigitsImpl.java",
        "field_anno": '    @JacksonXmlProperty(localName = "genericDigits")\n    private byte[] data;',
        "getter_anno": '    @JacksonXmlProperty(localName = "genericDigits")\n    @Override\n    public byte[] getData() {',
        "ignore_props": '@JsonIgnoreProperties({"genericDigits", "genericNumber"})\n',
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/BearerCapImpl.java",
        "field_anno": '    @JacksonXmlProperty(localName = "userServiceInformation")\n    private byte[] data;',
        "getter_anno": '    @JacksonXmlProperty(localName = "userServiceInformation")\n    @Override\n    public byte[] getData() {',
        "ignore_props": '@JsonIgnoreProperties({"userServiceInformation"})\n',
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CalledPartyNumberCapImpl.java",
        "field_anno": '    @JacksonXmlProperty(localName = "isupCalledPartyNumber")\n    private byte[] data;',
        "getter_anno": '    @JacksonXmlProperty(localName = "isupCalledPartyNumber")\n    @Override\n    public byte[] getData() {',
        "ignore_props": '@JsonIgnoreProperties({"calledPartyNumber"})\n',
    },
    {
        "path": "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/isup/CauseCapImpl.java",
        "field_anno": '    @JacksonXmlProperty(localName = "isupCauseIndicators")\n    private byte[] data;',
        "getter_anno": '    @JacksonXmlProperty(localName = "isupCauseIndicators")\n    @Override\n    public byte[] getData() {',
        "ignore_props": '@JsonIgnoreProperties({"causeIndicators"})\n',
    },
]

for fix in fixes:
    with open(fix["path"], "r", encoding="utf-8") as f:
        content = f.read()

    # Add JsonIgnoreProperties import if missing
    if "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;" not in content:
        content = content.replace(
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;",
            "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;\nimport com.fasterxml.jackson.annotation.JsonIgnoreProperties;"
        )

    # Add @JsonIgnoreProperties before @JacksonXmlRootElement if not present
    if fix["ignore_props"].strip() not in content:
        content = content.replace(
            "@JacksonXmlRootElement",
            fix["ignore_props"] + "@JacksonXmlRootElement"
        )

    # Move annotation from field to getter
    if fix["field_anno"] in content:
        content = content.replace(fix["field_anno"], "    private byte[] data;")
    if fix["getter_anno"] not in content:
        content = content.replace(
            "    @Override\n    public byte[] getData() {",
            fix["getter_anno"]
        )

    with open(fix["path"], "w", encoding="utf-8") as f:
        f.write(content)
    print("Fixed", os.path.basename(fix["path"]))
