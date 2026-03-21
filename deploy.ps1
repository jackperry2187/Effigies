param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$ProjectRoot   = $PSScriptRoot
$FabricLibs    = Join-Path $ProjectRoot "versions\1.21.11-fabric\build\libs"
$NeoForgeLibs  = Join-Path $ProjectRoot "versions\1.21.11-neoforge\build\libs"
$FabricMods    = "C:\Users\Jackson\curseforge\minecraft\Instances\MFC-FAB-1.21.11\mods"
$NeoForgeMods  = "C:\Users\Jackson\curseforge\minecraft\Instances\NEO-1.21.11\mods"

if (-not $SkipBuild) {
    Write-Host "`n=== Building Effigies ===" -ForegroundColor Cyan
    & "$ProjectRoot\gradlew.bat" chiseledBuild --no-daemon
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "Build succeeded." -ForegroundColor Green
}

function Deploy-Jar {
    param(
        [string]$LibsDir,
        [string]$ModsDir,
        [string]$Label
    )

    Write-Host "`n=== Deploying $Label ===" -ForegroundColor Cyan

    # Remove any existing effigies jars from the mods folder (but nothing else)
    $existing = Get-ChildItem $ModsDir -Filter "effigies-*.jar" -ErrorAction SilentlyContinue
    foreach ($jar in $existing) {
        Write-Host "  Removing old jar: $($jar.Name)" -ForegroundColor Yellow
        Remove-Item $jar.FullName -Force
    }

    # Find the latest non-sources jar
    $latest = Get-ChildItem $LibsDir -Filter "effigies-*.jar" |
              Where-Object { $_.Name -notlike "*-sources.jar" } |
              Sort-Object LastWriteTime -Descending |
              Select-Object -First 1

    if (-not $latest) {
        Write-Host "  No jar found in $LibsDir" -ForegroundColor Red
        exit 1
    }

    Write-Host "  Copying $($latest.Name) -> $ModsDir" -ForegroundColor Green
    Copy-Item $latest.FullName -Destination $ModsDir
}

Deploy-Jar -LibsDir $FabricLibs   -ModsDir $FabricMods   -Label "Fabric"
Deploy-Jar -LibsDir $NeoForgeLibs -ModsDir $NeoForgeMods -Label "NeoForge"

Write-Host "`nDone!" -ForegroundColor Green
