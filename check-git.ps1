# Check .git for each project
Get-ChildItem 'C:\Users\Windows\Desktop\ethiopia-working-dir' -Directory | ForEach-Object {
    $hasGit = Test-Path (Join-Path $_.FullName '.git')
    Write-Output "$($_.Name): $hasGit"
}
