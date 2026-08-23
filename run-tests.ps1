param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$Gradle = Join-Path $ProjectRoot "gradlew.bat"

$Targets = @(
    @{ Project = "1.21.11-fabric"; Label = "1.21.11 Fabric" },
    @{ Project = "1.21.11-neoforge"; Label = "1.21.11 NeoForge" },
    @{ Project = "26.1-fabric"; Label = "26.1 Fabric" },
    @{ Project = "26.1-neoforge"; Label = "26.1 NeoForge" }
)

function Clear-TestRunDirectory {
    param(
        [string]$Project
    )

    $runDirectory = Join-Path $ProjectRoot "run\gametest\$Project"
    if (Test-Path $runDirectory) {
        Remove-Item $runDirectory -Recurse -Force
    }
}

function Invoke-GradleTest {
    param(
        [hashtable]$Target
    )

    Clear-TestRunDirectory -Project $Target.Project
    Write-Host "`n=== GameTests: $($Target.Label) ===" -ForegroundColor Cyan
    & $Gradle ":$($Target.Project):runEffigiesGameTests" "--no-daemon"
    $script:LastTestExitCode = $LASTEXITCODE
}

if (-not $SkipBuild) {
    Write-Host "`n=== Building and testing all Effigies targets ===" -ForegroundColor Cyan
    foreach ($target in $Targets) {
        Clear-TestRunDirectory -Project $target.Project
    }

    & $Gradle "chiseledBuild" "--no-daemon" "--no-parallel"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build or GameTests failed. See each target's build logs and reports." -ForegroundColor Red
        exit $LASTEXITCODE
    }

    Write-Host "Build and GameTests passed for all four targets." -ForegroundColor Green
    exit 0
}

$failedTargets = @()
foreach ($target in $Targets) {
    Invoke-GradleTest -Target $target
    $exitCode = $script:LastTestExitCode
    if ($exitCode -ne 0) {
        $failedTargets += "$($target.Label) (exit code $exitCode)"
    }
}

if ($failedTargets.Count -gt 0) {
    Write-Host "`nGameTest failures:" -ForegroundColor Red
    foreach ($failedTarget in $failedTargets) {
        Write-Host "  $failedTarget" -ForegroundColor Red
    }
    exit 1
}

Write-Host "`nAll four GameTest suites passed." -ForegroundColor Green
exit 0
