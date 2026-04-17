$file = "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\Dockerfile"
$lines = Get-Content $file
$newLines = @()
$skip = 0

foreach ($line in $lines) {
    if ($skip -gt 0) {
        $skip--
        continue
    }
    
    if ($line -match "xstream\.jar") {
        # Found xstream line, skip until we find javolution
        $newLines += "# Download missing dependencies (xstream removed - now using Jackson XML)"
        $skip = 2  # Skip next 2 lines (xmlpull and mxparser)
        continue
    }
    
    if ($line -match "xmlpull\.jar|mxparser\.jar") {
        $skip = 1
        continue
    }
    
    if ($line -match "javolution\.jar") {
        $newLines += $line
        continue
    }
    
    $newLines += $line
}

Set-Content -Path $file -Value $newLines
Write-Host "Dockerfile updated successfully"
Get-Content $file | Select-Object -First 35
