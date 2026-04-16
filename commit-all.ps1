# Commit XStream -> Jackson XML migration for all projects
$projects = @("sctp", "jSS7", "jdiameter", "jain-slee.ss7", "jain-slee.diameter", "gmlc")
$baseDir = "C:\Users\Windows\Desktop\ethiopia-working-dir"

foreach ($project in $projects) {
    $projectDir = Join-Path $baseDir $project
    if (Test-Path (Join-Path $projectDir '.git')) {
        Write-Host "=== Committing $project ===" -ForegroundColor Cyan
        
        # Stage all changes
        git -C $projectDir add -A
        
        # Check if there are changes
        $status = git -C $projectDir status --porcelain
        if ($status) {
            # Commit
            git -C $projectDir commit -m "refactor: replace XStream with Jackson XML for Java 17 compatibility

- Removed XStream dependencies from pom.xml files
- Renamed helper classes from *XStreamHelper to *JacksonXMLHelper
- Updated all references to use Jackson XML serialization
- Pumped version following dependency order"
            Write-Host "Committed $project" -ForegroundColor Green
        } else {
            Write-Host "No changes in $project" -ForegroundColor Yellow
        }
        Write-Host ""
    }
}

Write-Host "Done!" -ForegroundColor Cyan
