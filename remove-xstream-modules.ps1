# Remove xstream dependencies from sub-module pom.xml files

$files = @(
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\tcap-ansi\tcap-ansi-impl\pom.xml",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\oam\common\jmx\pom.xml",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\oam\common\statistics\impl\pom.xml"
)

foreach ($file in $files) {
    Write-Host "Processing: $file"
    $content = Get-Content $file -Raw
    # Remove xstream dependency with version
    $content = $content -replace '(\s*<dependency>\s*<groupId>com\.thoughtworks\.xstream</groupId>\s*<artifactId>xstream</artifactId>\s*<version>\$\{xstream\.version\}</version>\s*</dependency>)', ''
    # Remove xstream dependency without version
    $content = $content -replace '(\s*<dependency>\s*<groupId>com\.thoughtworks\.xstream</groupId>\s*<artifactId>xstream\s*</dependency>)', ''
    Set-Content -Path $file -Value $content
    Write-Host "Done: $file"
}

Write-Host "All xstream dependencies removed from sub-modules"
