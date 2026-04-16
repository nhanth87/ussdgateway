# Fix BOM in all Java files recursively
Get-ChildItem -Path "C:\Users\Windows\Desktop\ethiopia-working-dir" -Recurse -Include "*.java" -ErrorAction SilentlyContinue | Where-Object { $_.FullName -notmatch "\\target\\" } | ForEach-Object {
    $file = $_.FullName
    $content = [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8)
    if ($content.StartsWith("`u{FEFF}")) {
        $content = $content.Substring(1)
        [System.IO.File]::WriteAllText($file, $content, (New-Object System.Text.UTF8Encoding $False))
        Write-Host "Fixed: $file" -ForegroundColor Green
    }
}
Write-Host "Done!" -ForegroundColor Cyan
