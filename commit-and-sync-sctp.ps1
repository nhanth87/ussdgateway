# Commit and sync SCTP performance optimizations to nhanth87 GitHub
$token = "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"
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

Write-Host "=== Committing SCTP performance optimizations ===" -ForegroundColor Cyan

# Commit changes in sctp
$projectDir = Join-Path $baseDir "sctp"
git -C $projectDir add -A
git -C $projectDir status

# Check if there are changes to commit
$status = git -C $projectDir status --porcelain
if ($status) {
    git -C $projectDir commit -m "v2.0.13: High/Medium priority SCTP performance optimizations

- Add PooledByteBufAllocator.DEFAULT explicitly (fixes Netty 4.2 AdaptiveByteBufAllocator CPU overhead)
- Add FixedRecvByteBufAllocator(8192) for consistent read buffer sizes
- Reduce WriteBuffer watermarks from 64MB/32MB to 16MB/8MB (prevent memory bloat)
- Set default SCTP_INIT_MAXSTREAMS to 256 for high-throughput scenarios
- Implement ThreadLocal output buffer pooling in PooledNioSctpChannel
- Optimized fast path for direct single-buffer writes

Expected improvements:
- ~80% CPU overhead reduction (AdaptiveByteBufAllocator -> PooledByteBufAllocator)
- Better memory utilization with smaller write buffers
- Reduced allocations in hot path with output buffer pooling
- Increased multiplexing with 256 SCTP streams"
    
    Write-Host "Committed changes in sctp" -ForegroundColor Green
} else {
    Write-Host "No changes to commit in sctp" -ForegroundColor Yellow
}

# Sync to both master and main branches
Write-Host ""
Write-Host "=== Syncing to master and main branches ===" -ForegroundColor Cyan

# Set remote URL
git -C $projectDir remote set-url origin "https://nhanth87:$token@github.com/nhanth87/sctp.git"

# Get current branch
$currentBranch = git -C $projectDir branch --show-current
Write-Host "Current branch: $currentBranch" -ForegroundColor Yellow

if ($currentBranch -eq "master") {
    # Push master first
    git -C $projectDir push origin master
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Pushed master" -ForegroundColor Green
    }
    
    # Checkout main and merge
    git -C $projectDir checkout main
    git -C $projectDir merge master --no-edit
    git -C $projectDir push origin main
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Pushed main" -ForegroundColor Green
    }
    
    # Switch back to master
    git -C $projectDir checkout master
} elseif ($currentBranch -eq "main") {
    # Push main first
    git -C $projectDir push origin main
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Pushed main" -ForegroundColor Green
    }
    
    # Merge main into master
    git -C $projectDir checkout master
    git -C $projectDir merge main --no-edit
    git -C $projectDir push origin master
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Pushed master" -ForegroundColor Green
    }
    
    # Switch back to main
    git -C $projectDir checkout main
}

Write-Host ""
Write-Host "=== Done ===" -ForegroundColor Cyan