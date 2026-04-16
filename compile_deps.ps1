#!/usr/bin/env pwsh
$ErrorActionPreference = "Continue"

Write-Host "Compiling jdiameter..." -ForegroundColor Cyan
Set-Location "C:\Users\Windows\Desktop\ethiopia-working-dir\jdiameter"
& mvn clean install -DskipTests -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true 2>&1 | Select-Object -Last 50

if ($LASTEXITCODE -ne 0) {
    Write-Host "jdiameter failed!" -ForegroundColor Red
    exit 1
}
Write-Host "jdiameter SUCCESS!" -ForegroundColor Green

Write-Host "Compiling jain-slee.diameter..." -ForegroundColor Cyan
Set-Location "C:\Users\Windows\Desktop\ethiopia-working-dir\jain-slee.diameter"
& mvn clean install -DskipTests -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true 2>&1 | Select-Object -Last 50

if ($LASTEXITCODE -ne 0) {
    Write-Host "jain-slee.diameter failed!" -ForegroundColor Red
    exit 1
}
Write-Host "jain-slee.diameter SUCCESS!" -ForegroundColor Green
Write-Host "All dependencies compiled successfully!" -ForegroundColor Green
