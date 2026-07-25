param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot

$Targets = @(
    @{
        Label    = "1.21.11 Fabric"
        LibsDir  = Join-Path $ProjectRoot "versions\1.21.11-fabric\build\libs"
        ModsDir  = "C:\Users\Jackson\curseforge\minecraft\Instances\MFC-FAB-1.21.11\mods"
    },
    @{
        Label    = "1.21.11 NeoForge"
        LibsDir  = Join-Path $ProjectRoot "versions\1.21.11-neoforge\build\libs"
        ModsDir  = "C:\Users\Jackson\curseforge\minecraft\Instances\NEO-1.21.11\mods"
    },
    @{
        Label    = "26.1 Fabric"
        LibsDir  = Join-Path $ProjectRoot "versions\26.1-fabric\build\libs"
        ModsDir  = "C:\Users\Jackson\curseforge\minecraft\Instances\FAB-26.1\mods"
    },
    @{
        Label    = "26.1 NeoForge"
        LibsDir  = Join-Path $ProjectRoot "versions\26.1-neoforge\build\libs"
        ModsDir  = "C:\Users\Jackson\curseforge\minecraft\Instances\NEO-26.1\mods"
    }
)

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

    $existing = Get-ChildItem $ModsDir -Filter "effigies-*.jar" -ErrorAction SilentlyContinue
    foreach ($jar in $existing) {
        Write-Host "  Removing old jar: $($jar.Name)" -ForegroundColor Yellow
        Remove-Item $jar.FullName -Force
    }

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

foreach ($target in $Targets) {
    Deploy-Jar -LibsDir $target.LibsDir -ModsDir $target.ModsDir -Label $target.Label
}

Write-Host "`nDone!" -ForegroundColor Green
