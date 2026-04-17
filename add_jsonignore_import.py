import os
import glob

files = glob.glob("/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/errors/CAPErrorMessage*Impl.java")
for f in files:
    with open(f, "r", encoding="utf-8") as fh:
        content = fh.read()
    if "import com.fasterxml.jackson.annotation.JsonIgnore;" not in content:
        content = content.replace(
            "package org.restcomm.protocols.ss7.cap.errors;",
            "package org.restcomm.protocols.ss7.cap.errors;\nimport com.fasterxml.jackson.annotation.JsonIgnore;"
        )
        with open(f, "w", encoding="utf-8") as fh:
            fh.write(content)
        print("Added import to", os.path.basename(f))
