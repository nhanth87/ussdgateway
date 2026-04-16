# Fix all empty XStream dependencies in jain-slee.diameter
Get-ChildItem -Path 'C:\Users\Windows\Desktop\ethiopia-working-dir\jain-slee.diameter' -Include 'pom.xml' -Recurse | ForEach-Object {
    $content = Get-Content -Path $_.FullName -Raw
    $pattern = '<dependency>\s*<!-- XStream removed - using Jackson XML for serialization -->\s*</dependency>\s*'
    if ($content -match $pattern) {
        $newContent = $content -replace $pattern, ''
        [System.IO.File]::WriteAllText($_.FullName, $newContent)
        Write-Host "Fixed: $($_.FullName)" -ForegroundColor Green
    }
}
Write-Host "Done!" -ForegroundColor Cyan
