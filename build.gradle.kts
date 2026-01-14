plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("java")
    id("maven-publish")
}

// Stonecutter version constants
val mcVersion = stonecutter.current.version.substringBefore("-")
val loader = stonecutter.current.version.substringAfter("-")
val isFabric = loader == "fabric"
val isNeoforge = loader == "neoforge"

// Version-specific properties
val javaVersion = if (mcVersion == "1.20.1") 17 else 21
// For NeoForge 1.21.6+, use MC 1.21.8 since NeoForge skipped 1.21.6/1.21.7 releases
val minecraftVersion = if (isNeoforge && mcVersion == "1.21.6") "1.21.8" else mcVersion
val modVersion: String by project
val mavenGroup: String by project

version = "$modVersion-$mcVersion-$loader"
group = mavenGroup

base {
    archivesName.set("gentlereminders")
}

// Configure Architectury
architectury {
    platformSetupLoomIde()
    if (isFabric) fabric() else neoForge()
}

// Configure Loom
loom {
    silentMojangMappingsLicense()
    
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
}

// Version-specific dependency versions
val fabricLoaderVersion = when (mcVersion) {
    "1.20.1" -> "0.18.4"
    "1.21.1" -> "0.18.4"
    else -> "0.18.4"
}

val fabricApiVersion = when (mcVersion) {
    "1.20.1" -> "0.92.6+1.20.1"
    "1.21.1" -> "0.116.7+1.21.1"
    else -> "0.128.2+1.21.6"
}

val yarnMappings = when (mcVersion) {
    "1.20.1" -> "1.20.1+build.10"
    "1.21.1" -> "1.21.1+build.3"
    else -> "1.21.6+build.1"
}

val neoforgeVersion = when (mcVersion) {
    "1.21.1" -> "21.1.77"
    else -> "21.8.52"  // For 1.21.6+, NeoForge skipped to 1.21.8
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    
    if (isFabric) {
        mappings("net.fabricmc:yarn:$yarnMappings:v2")
        modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
        modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    } else {
        mappings(loom.layered {
            officialMojangMappings()
        })
    }
}

// NeoForge dependency - configuration is set up by loom.platform=neoforge in gradle.properties
if (isNeoforge) {
    dependencies {
        add("neoForge", "net.neoforged:neoforge:$neoforgeVersion")
    }
}

// Configure source sets to use Stonecutter's preprocessed sources
// chiseledSrc contains the version-specific processed code
sourceSets {
    main {
        java {
            setSrcDirs(listOf(file("build/chiseledSrc/main/java")))
        }
        resources {
            setSrcDirs(listOf(file("build/chiseledSrc/main/resources")))
        }
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

// Handle duplicate files in Jar and Copy tasks
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.processResources {
    // For 1.21.6+ builds, use a version range to support future versions
    val minecraftVersionRange = when (mcVersion) {
        "1.20.1" -> "1.20.1"
        "1.21.1" -> "1.21.1"
        else -> ">=1.21.6"  // Support 1.21.6 and later
    }
    
    val props = mapOf(
        "version" to modVersion,
        "minecraft_version" to minecraftVersionRange,
        "java_version" to javaVersion,
        "loader" to loader
    )
    
    inputs.properties(props)
    
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand(props)
    }
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}

// Stonecutter preprocessor configuration
stonecutter {
    swap("mcVersion", mcVersion)
    const("fabric", isFabric)
    const("neoforge", isNeoforge)
    
    // Version comparisons
    val mcSemver = mcVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val mc1201 = listOf(1, 20, 1)
    val mc1211 = listOf(1, 21, 1)
    val mc1216 = listOf(1, 21, 6)
    
    fun compareVersions(v1: List<Int>, v2: List<Int>): Int {
        for (i in 0 until maxOf(v1.size, v2.size)) {
            val a = v1.getOrElse(i) { 0 }
            val b = v2.getOrElse(i) { 0 }
            if (a != b) return a.compareTo(b)
        }
        return 0
    }
    
    const("mc1201", compareVersions(mcSemver, mc1201) == 0)
    const("mc1211", compareVersions(mcSemver, mc1211) == 0)
    const("mc1216", compareVersions(mcSemver, mc1216) >= 0)
    const("mcGte121", compareVersions(mcSemver, mc1211) >= 0)
    const("mcGte1216", compareVersions(mcSemver, mc1216) >= 0)
}
