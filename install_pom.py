import os
import shutil

repo_dir = os.path.expanduser("~/.m2/repository")
pom_dir = os.path.join(repo_dir, "org/restcomm/protocols/ss7/ss7-parent/9.2.7")
os.makedirs(pom_dir, exist_ok=True)

source_pom = "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/pom.xml"
dest_pom = os.path.join(pom_dir, "ss7-parent-9.2.7.pom")
shutil.copy(source_pom, dest_pom)

# clean up .part files
for f in os.listdir(pom_dir):
    if f.endswith(".part") or f.endswith(".part.lock"):
        os.remove(os.path.join(pom_dir, f))

metadata = """<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>org.restcomm.protocols.ss7</groupId>
  <artifactId>ss7-parent</artifactId>
  <versioning>
    <versions>
      <version>9.2.7</version>
    </versions>
    <lastUpdated>20260415000000</lastUpdated>
  </versioning>
</metadata>
"""
with open(os.path.join(pom_dir, "maven-metadata-local.xml"), "w") as f:
    f.write(metadata)

print(f"Installed ss7-parent-9.2.7.pom to {pom_dir}")
