import os
import shutil
import hashlib

group_id = "org.mobicents.protocols.sctp"
artifact_id = "sctp-impl"
version = "2.0.13-SNAPSHOT"
jar_path = "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/sctp/sctp-impl/target/sctp-impl-2.0.13-SNAPSHOT.jar"

repo_dir = os.path.expanduser("~/.m2/repository")
artifact_dir = os.path.join(repo_dir, group_id.replace(".", "/"), artifact_id, version)

os.makedirs(artifact_dir, exist_ok=True)

# Remove any partial lock files
for f in os.listdir(artifact_dir):
    if f.endswith(".part") or f.endswith(".part.lock"):
        os.remove(os.path.join(artifact_dir, f))

dest_jar = os.path.join(artifact_dir, f"{artifact_id}-{version}.jar")
shutil.copy(jar_path, dest_jar)

# Generate simple POM
pom_content = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>{group_id}</groupId>
  <artifactId>{artifact_id}</artifactId>
  <version>{version}</version>
  <packaging>jar</packaging>
</project>
"""
dest_pom = os.path.join(artifact_dir, f"{artifact_id}-{version}.pom")
with open(dest_pom, "w") as f:
    f.write(pom_content)

# Create maven-metadata-local.xml
metadata = f"""<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>{group_id}</groupId>
  <artifactId>{artifact_id}</artifactId>
  <versioning>
    <snapshot>
      <localCopy>true</localCopy>
    </snapshot>
    <lastUpdated>20260415000000</lastUpdated>
    <snapshotVersions>
      <snapshotVersion>
        <extension>jar</extension>
        <value>{version}</value>
        <updated>20260415000000</updated>
      </snapshotVersion>
      <snapshotVersion>
        <extension>pom</extension>
        <value>{version}</value>
        <updated>20260415000000</updated>
      </snapshotVersion>
    </snapshotVersions>
  </versioning>
</metadata>
"""
with open(os.path.join(artifact_dir, "maven-metadata-local.xml"), "w") as f:
    f.write(metadata)

# Create _remote.repositories
with open(os.path.join(artifact_dir, "_remote.repositories"), "w") as f:
    f.write("#NOTE: This is a Maven Resolver internal implementation file, its format can be changed without prior notice.\n")
    f.write(f"{artifact_id}-{version}.jar>=\n")
    f.write(f"{artifact_id}-{version}.pom>=\n")

print(f"Installed {artifact_id}-{version} to {artifact_dir}")
