# Push to nhanth87 GitHub repos
$token = "ghp_v0RrrOvnXvnqKDzAP3Q9PbVBPIU88C3pQk"
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

# Map projects to their nhanth87 repos
$repos = @{
    "sctp" = "https://nhanth87:$token@github.com/nhanth87/sctp.git"
    "jSS7" = "https://nhanth87:$token@github.com/nhanth87/jss7.git"
    "jdiameter" = "https://nhanth87:$token@github.com/nhanth87/jdiameter.git"
    "jain-slee.ss7" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.ss7.git"
    "jain-slee.diameter" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.diameter.git"
    "gmlc" = "https://nhanth87:$token@github.com/nhanth87/gmlc.git"
}

foreach ($project in $repos.Keys) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Pushing $project ===" -ForegroundColor Cyan
        
        # Get current branch
        $branch = git -C $projectDir branch --show-current
        if (-not $branch) { $branch = "master" }
        
        # Set new remote URL
        git -C $projectDir remote set-url origin $repos[$project]
        
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
