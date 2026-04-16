# Push using GIT_ASKPASS environment variable
$token = "ghp_v0RrrOvnXvnqKDzAP3Q9PbVBPIU88C3pQk"
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Pushing $project ===" -ForegroundColor Cyan
        
        # Set environment for this git command
        $env:GITHUB_TOKEN = $token
        
        git -C $projectDir -c "credential.helper=!echo password=$token" push origin master 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Pushed $project" -ForegroundColor Green
        } else {
            Write-Host "Failed to push $project" -ForegroundColor Red
        }
        Write-Host ""
    }
}

Write-Host "Done!" -ForegroundColor Cyan
