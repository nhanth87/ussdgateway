# Fix BOM in all Java files in jSS7
Get-ChildItem -Path "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7" -Include "*.java" -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
    $bytes = [System.IO.File]::ReadAllBytes($_.FullName)
    if ($bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $newBytes = $bytes[3..($bytes.Length-1)]
        [System.IO.File]::WriteAllBytes($_.FullName, $newBytes)
        Write-Host "Fixed: $($_.FullName)" -ForegroundColor Green
    }
}
Write-Host "Done!" -ForegroundColor Cyan
