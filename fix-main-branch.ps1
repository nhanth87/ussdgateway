# Merge master into main and push for sctp and jSS7
$token = "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"
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

$projects = @("sctp", "jSS7")

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    Write-Host "=== Fixing $project ===" -ForegroundColor Cyan
    
    # Checkout main
    git -C $projectDir checkout main
    
    # Merge master into main
    git -C $projectDir merge master
    
    # Update remote URL with token
    $currentUrl = git -C $projectDir remote get-url origin
    if ($currentUrl -notlike "*$token*") {
        $newUrl = $currentUrl -replace 'https://', "https://nhanth87:$token@"
        git -C $projectDir remote set-url origin $newUrl
    }
    
    # Push main
    git -C $projectDir push origin main
    
    # Back to master
    git -C $projectDir checkout master
    
    Write-Host "Fixed $project" -ForegroundColor Green
    Write-Host ""
}

Write-Host "Done!" -ForegroundColor Cyan
