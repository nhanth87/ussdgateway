# Push all projects with unpushed commits
$token = "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"
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

$projects = @{
    "sctp" = "https://nhanth87:$token@github.com/nhanth87/sctp.git"
    "jSS7" = "https://nhanth87:$token@github.com/nhanth87/jss7.git"
    "jdiameter" = "https://nhanth87:$token@github.com/nhanth87/jdiameter.git"
    "jain-slee.ss7" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.ss7.git"
    "jain-slee.diameter" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.diameter.git"
    "gmlc" = "https://nhanth87:$token@github.com/nhanth87/gmlc.git"
}

foreach ($project in $projects.Keys) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        $branch = git -C $projectDir branch --show-current
        if (-not $branch) { $branch = "master" }
        
        Write-Host "=== Pushing $project ($branch) ===" -ForegroundColor Cyan
        
        # Update remote URL
        git -C $projectDir remote set-url origin $projects[$project]
        
        # Push
        git -C $projectDir push origin $branch
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Pushed $project" -ForegroundColor Green
        } else {
            Write-Host "Failed to push $project" -ForegroundColor Red
        }
        Write-Host ""
    }
}

Write-Host "Done!" -ForegroundColor Cyan
