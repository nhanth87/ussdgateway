$file = "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\pom.xml"
$lines = Get-Content $file
$newLines = @()
$skip = 0

for ($i = 0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    
    if ($skip -gt 0) {
        $skip--
        continue
    }
    
    if ($line -match "xstream" -or $line -match "mxparser" -or $line -match "xmlpull") {
        if ($line -match "comment") { continue }
        # Found xstream/mxparser/xmlpull, skip this and 7 more lines
        $skip = 7
        continue
    }
    
    $newLines += $line
}

Set-Content -Path $file -Value $newLines
Write-Host "Removed xstream dependencies from pom.xml"
