# Push with token embedded in URL
$token = "ghp_v0RrrOvnXvnqKDzAP3Q9PbVBPIU88C3pQk"
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Pushing $project ===" -ForegroundColor Cyan
        
        # Get current remote URL
        $remoteUrl = git -C $projectDir remote get-url origin
        
        # Replace https:// with token-based URL
        $newUrl = $remoteUrl -replace 'https://', "https://nhanth87:$token@"
        
        # Set new URL temporarily
        git -C $projectDir remote set-url origin $newUrl
        
        # Push
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
