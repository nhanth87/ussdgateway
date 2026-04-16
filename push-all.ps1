# Push all projects to remote
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Pushing $project ===" -ForegroundColor Cyan
        git -C $projectDir push origin master
        Write-Host "Pushed $project" -ForegroundColor Green
        Write-Host ""
    }
}

Write-Host "Done!" -ForegroundColor Cyan
