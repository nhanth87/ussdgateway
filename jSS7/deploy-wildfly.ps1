# jSS7 WildFly 24 Deployment Script
# Usage: .\deploy-wildfly.ps1 [-WildFlyHome "C:\path\to\wildfly"]

param(
    [string]$WildFlyHome = $env:WILDFLY_HOME,
    [switch]$Undeploy = $false,
    [switch]$List = $false
)

$jss7Version = "9.0.0-318"
$sctpVersion = "2.0.2-17"

# Check WildFly home
if (-not $WildFlyHome) {
    Write-Error "WildFly home not specified. Please set WILDFLY_HOME environment variable or use -WildFlyHome parameter."
    exit 1
}

if (-not (Test-Path "$WildFlyHome\standalone\configuration")) {
    Write-Error "Invalid WildFly home directory: $WildFlyHome"
    exit 1
}

Write-Host "WildFly home: $WildFlyHome" -ForegroundColor Green

$modulesDir = "$WildFlyHome\modules"
$ss7ModuleDir = "$modulesDir\org\restcomm\ss7\main"
$extensionModuleDir = "$modulesDir\org\restcomm\ss7\extension\main"
$commonsModuleDir = "$modulesDir\org\restcomm\ss7\commons\main"

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$modulesTarget = "$baseDir\service\wildfly\modules\target\module"
$extensionTarget = "$baseDir\service\wildfly\extension\target\module"
$commonsTarget = "$baseDir\service\wildfly\restcomm-ss7-wildfly-commons\target\module"

function List-Modules {
    Write-Host "`nDeployed jSS7 modules:" -ForegroundColor Cyan
    if (Test-Path "$modulesDir\org\restcomm") {
        Get-ChildItem "$modulesDir\org\restcomm" -Recurse -Filter "*.jar" | ForEach-Object {
            Write-Host "  $($_.FullName.Replace($modulesDir, '').TrimStart('\'))" -ForegroundColor Gray
        }
    } else {
        Write-Host "  No jSS7 modules found." -ForegroundColor Yellow
    }
}

function Undeploy-Modules {
    Write-Host "`nUndeploying jSS7 from WildFly..." -ForegroundColor Yellow
    
    if (Test-Path "$modulesDir\org\restcomm\ss7") {
        Remove-Item "$modulesDir\org\restcomm\ss7" -Recurse -Force
        Write-Host "  Removed: org/restcomm/ss7" -ForegroundColor Green
    }
    
    Write-Host "Undeploy completed!" -ForegroundColor Green
}

function Deploy-Modules {
    Write-Host "`nDeploying jSS7 $jss7Version to WildFly 24..." -ForegroundColor Cyan
    
    # Create directories
    New-Item -ItemType Directory -Force -Path $ss7ModuleDir | Out-Null
    New-Item -ItemType Directory -Force -Path $extensionModuleDir | Out-Null
    New-Item -ItemType Directory -Force -Path $commonsModuleDir | Out-Null
    
    # Deploy main SS7 modules
    Write-Host "  Deploying SS7 main modules..." -ForegroundColor Gray
    Copy-Item "$modulesTarget\main\*.jar" $ss7ModuleDir -Force
    Copy-Item "$modulesTarget\main\module.xml" $ss7ModuleDir -Force
    
    # Deploy extension module
    Write-Host "  Deploying SS7 extension module..." -ForegroundColor Gray
    Copy-Item "$extensionTarget\main\*.jar" $extensionModuleDir -Force
    Copy-Item "$extensionTarget\main\module.xml" $extensionModuleDir -Force
    
    # Deploy commons module
    Write-Host "  Deploying SS7 commons module..." -ForegroundColor Gray
    Copy-Item "$commonsTarget\main\*.jar" $commonsModuleDir -Force
    Copy-Item "$commonsTarget\main\module.xml" $commonsModuleDir -Force
    
    Write-Host "`nDeployment completed successfully!" -ForegroundColor Green
    Write-Host "`nNext steps:" -ForegroundColor Cyan
    Write-Host "  1. Edit $WildFlyHome\standalone\configuration\standalone.xml" -ForegroundColor White
    Write-Host "  2. Add SS7 extension and subsystem configuration" -ForegroundColor White
    Write-Host "  3. Start WildFly: $WildFlyHome\bin\standalone.bat" -ForegroundColor White
}

# Main execution
if ($List) {
    List-Modules
} elseif ($Undeploy) {
    Undeploy-Modules
} else {
    Undeploy-Modules
    Deploy-Modules
}
