path = "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/primitives/CAPAsnPrimitive.java"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()
c = c.replace("import java.io.Serializable;", "import java.io.Serializable;\nimport com.fasterxml.jackson.annotation.JsonIgnore;")
c = c.replace("    int getTag() throws CAPException;", "    @JsonIgnore\n    int getTag() throws CAPException;")
c = c.replace("    int getTagClass();", "    @JsonIgnore\n    int getTagClass();")
c = c.replace("    boolean getIsPrimitive();", "    @JsonIgnore\n    boolean getIsPrimitive();")
with open(path, "w", encoding="utf-8") as f:
    f.write(c)
print("done")
