$file = "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\tcap-ansi\tcap-ansi-impl\pom.xml"
$content = Get-Content $file -Raw
$pattern = '(\s*<dependency>\s*<groupId>com\.thoughtworks\.xstream</groupId>\s*<artifactId>xstream</artifactId>\s*</dependency>)'
$content = $content -replace $pattern, ''
Set-Content -Path $file -Value $content
Write-Host "Done"
