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
    id("dev.kikugie.stonecutter") version "0.5+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    
    create(rootProject) {
        versions("1.20.1-fabric", "1.21.1-fabric", "1.21.1-neoforge", "1.21.6-fabric", "1.21.6-neoforge")
        vcsVersion = "1.21.6-fabric"
    }
}

rootProject.name = "gentlereminders"
