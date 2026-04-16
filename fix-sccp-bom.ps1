# Fix BOM in sccp-impl Java files
$files = Get-ChildItem "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\sccp\sccp-impl\src\main\java\org\restcomm\protocols\ss7\sccp\impl" -Filter "*.java" -Recurse
foreach ($file in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    if ($bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $newBytes = $bytes[3..($bytes.Length-1)]
        [System.IO.File]::WriteAllBytes($file.FullName, $newBytes)
        Write-Host "Fixed: $($file.FullName)" -ForegroundColor Green
    }
}
Write-Host "Done!" -ForegroundColor Cyan
