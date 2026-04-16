# Check branch status for all projects
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== $project ===" -ForegroundColor Cyan
        git -C $projectDir branch -vv
        Write-Host ""
    }
}
