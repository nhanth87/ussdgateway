# Configure GitHub token and push all projects
$token = "ghp_v0RrrOvnXvnqKDzAP3Q9PbVBPIU88C3pQk"
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

# Set credential helper
git config --global credential.helper store

# Save token to credential store
@"
https://nhanth87:$token@github.com
"@ | Out-File -FilePath "$env:USERPROFILE\.git-credentials" -Encoding ASCII

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Pushing $project ===" -ForegroundColor Cyan
        git -C $projectDir push origin master
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Pushed $project" -ForegroundColor Green
        } else {
            Write-Host "Failed to push $project" -ForegroundColor Red
        }
        Write-Host ""
    }
}

Write-Host "Done!" -ForegroundColor Cyan
