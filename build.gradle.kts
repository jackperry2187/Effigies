plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.13.467"
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
val javaVersion = 21
val minecraftVersion = mcVersion
val modVersion: String by project
val mavenGroup: String by project

version = "$modVersion-$mcVersion-$loader"
group = mavenGroup

base {
    archivesName.set("effigies")
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

val fabricLoaderVersion = "0.18.4"
val fabricApiVersion = "0.141.1+1.21.11"
val yarnMappings = "1.21.11+build.4"

val neoforgeVersion = "21.11.35-beta"

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

// Stonecutter automatically handles shared sources (root src/) and
// version-specific sources (versions/{version}/src/) - no custom sourceSets needed

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
    val minecraftVersionRange = ">=1.21.11"
    
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
    val mc12011 = listOf(1, 20, 11)
    
    fun compareVersions(v1: List<Int>, v2: List<Int>): Int {
        for (i in 0 until maxOf(v1.size, v2.size)) {
            val a = v1.getOrElse(i) { 0 }
            val b = v2.getOrElse(i) { 0 }
            if (a != b) return a.compareTo(b)
        }
        return 0
    }
    
    const("mc12011", compareVersions(mcSemver, mc12011) == 0)
}
