pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true

    create(rootProject) {
        version("1.21.11-fabric", "1.21.11")
        version("1.21.11-neoforge", "1.21.11")
        version("26.1-fabric", "26.1")
        version("26.1-neoforge", "26.1")
        vcsVersion = "1.21.11-fabric"
    }
}

rootProject.name = "effigies"
