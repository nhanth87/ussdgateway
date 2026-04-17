# Sync master to main for all projects
$token = "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"
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

$repos = @{
    "sctp" = "https://nhanth87:$token@github.com/nhanth87/sctp.git"
    "jSS7" = "https://nhanth87:$token@github.com/nhanth87/jss7.git"
    "jdiameter" = "https://nhanth87:$token@github.com/nhanth87/jdiameter.git"
    "jain-slee.ss7" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.ss7.git"
    "jain-slee.diameter" = "https://nhanth87:$token@github.com/nhanth87/jain-slee.diameter.git"
    "gmlc" = "https://nhanth87:$token@github.com/nhanth87/gmlc.git"
}

foreach ($projName in $repos.Keys) {
    $projectDir = Join-Path $baseDir $projName
    if (-not (Test-Path (Join-Path $projectDir '.git'))) {
        Write-Host "=== ${projName}: Not a git repo, skipping ===" -ForegroundColor Yellow
        continue
    }
    
    Write-Host ""
    Write-Host "=== Processing ${projName} ===" -ForegroundColor Cyan
    
    # Set remote URL
    git -C $projectDir remote set-url origin $repos[$projName]
    
    # Fetch all branches
    git -C $projectDir fetch --all
    
    # Check if master exists locally
    $hasMaster = git -C $projectDir rev-parse --verify master 2>$null
    # Check if main exists locally
    $hasMain = git -C $projectDir rev-parse --verify main 2>$null
    
    if ($hasMaster -and $hasMain) {
        Write-Host "${projName}: Has both master and main - syncing master to main" -ForegroundColor Green
        
        # Checkout main
        git -C $projectDir checkout main
        # Merge master into main
        git -C $projectDir merge master --no-edit
        # Push main
        git -C $projectDir push origin main
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "${projName}: Successfully synced master to main" -ForegroundColor Green
        } else {
            Write-Host "${projName}: Failed to sync" -ForegroundColor Red
        }
    }
    elseif ($hasMaster -and -not $hasMain) {
        Write-Host "${projName}: Has only master - creating main from master" -ForegroundColor Yellow
        
        # Create main from master
        git -C $projectDir checkout -b main master
        # Push main
        git -C $projectDir push origin main
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "${projName}: Successfully created main from master" -ForegroundColor Green
        } else {
            Write-Host "${projName}: Failed to create main" -ForegroundColor Red
        }
    }
    elseif (-not $hasMaster -and $hasMain) {
        Write-Host "${projName}: Has only main - already synced" -ForegroundColor Green
    }
    else {
        Write-Host "${projName}: No master or main found" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=== Sync Complete ===" -ForegroundColor Cyan