cd C:\Users\Windows\Desktop\ethiopia-working-dir\jain-slee.diameter
$env:MAVEN_OPTS="--add-exports java.base/sun.net.util=ALL-UNNAMED --add-opens java.base/sun.net.util=ALL-UNNAMED"
mvn clean install -DskipTests
