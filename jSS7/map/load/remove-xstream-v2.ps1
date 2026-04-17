$file = "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\pom.xml"
$content = Get-Content $file -Raw

# Pattern to remove xstream, mxparser, xmlpull artifactItem blocks
$pattern = '(?s)<!-- xstream -->.*?</artifactItem>\s*</artifactItem>\s*<artifactItem>\s*<groupId>io\.github\.x-stream</groupId>.*?</artifactItem>\s*</artifactItem>\s*<artifactItem>\s*<groupId>xmlpull</groupId>.*?</artifactItem>\s*</artifactItem>\s*'
$replacement = '<!-- License jars (xstream/mxparser/xmlpull removed - now using Jackson XML) -->'
$content = $content -replace $pattern, $replacement

Set-Content -Path $file -Value $content -NoNewline
Write-Host "Removed xstream dependencies"
