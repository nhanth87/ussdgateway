# Check local commits for all projects
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        $branch = git -C $projectDir branch --show-current
        if (-not $branch) { $branch = "master" }
        
        $ahead = git -C $projectDir rev-list --count "@{upstream}..HEAD" 2>$null
        if (-not $ahead) { $ahead = "?" }
        
        Write-Host "=== $project ($branch) ===" -ForegroundColor Cyan
        git -C $projectDir log --oneline -3
        Write-Host "Ahead remote: $ahead" -ForegroundColor $(if ($ahead -and $ahead -ne "0" -and $ahead -ne "?") { "Yellow" } else { "Green" })
        Write-Host ""
    }
}
