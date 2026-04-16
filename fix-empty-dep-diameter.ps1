# Fix empty XStream dependency in jain-slee.diameter pom.xml
$file = 'C:\Users\Windows\Desktop\ethiopia-working-dir\jain-slee.diameter\pom.xml'
$content = Get-Content -Path $file -Raw
$pattern = '<dependency>\s*<!-- XStream removed - using Jackson XML for serialization -->\s*</dependency>\s*'
$newContent = $content -replace $pattern, ''
[System.IO.File]::WriteAllText($file, $newContent)
Write-Host "Fixed empty XStream dependency in jain-slee.diameter pom.xml"
