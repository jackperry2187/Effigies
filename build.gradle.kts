import java.io.ByteArrayInputStream
import java.net.URI
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.13.467"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("java")
    id("maven-publish")
}

val mcVersion = stonecutter.current.version
val loader = stonecutter.current.project.substringAfterLast("-")
val isFabric = loader == "fabric"
val isNeoforge = loader == "neoforge"
val is261 = stonecutter.current.parsed >= "26.1"

val javaVersion = if (is261) 25 else 21
val minecraftVersion = mcVersion
val modVersion: String by project
val mavenGroup: String by project
val gameTestSourceSet = if (isNeoforge) sourceSets.create("gametest") else null

version = "$modVersion-$mcVersion-$loader"
group = mavenGroup

base {
    archivesName.set("effigies")
}

architectury {
    platformSetupLoomIde()
    if (isFabric) fabric() else neoForge()
}

loom {
    silentMojangMappingsLicense()

    if (isFabric) {
        accessWidenerPath.set(rootProject.file("src/main/resources/effigies.accesswidener"))
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = if (name.equals("gameTest", ignoreCase = true)) {
            "../../run/gametest/${stonecutter.current.project}"
        } else {
            "../../run"
        }
    }

    if (isNeoforge) {
        runs {
            create("gameTestServer") {
                name("Game Test Server")
                environment("server")
                forgeTemplate("gameTestServer")
                source("gametest")
                mods {
                    create("effigies") {
                        sourceSet("main")
                        sourceSet("gametest")
                    }
                }
                property("neoforge.enabledGameTestNamespaces", "effigies")
                runDir("../../run/gametest/${stonecutter.current.project}")
            }
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.blamejared.com/")
}

val fabricLoaderVersion = "0.18.4"
val fabricApiVersion = if (is261) "0.144.3+26.1" else "0.141.1+1.21.11"
val neoforgeVersion = if (is261) "26.1.0.1-beta" else "21.11.35-beta"
val jeiVersion = if (is261) "29.2.0.21" else "27.4.0.15"

// ---------------------------------------------------------------------------
// Architectury Loom 26.1 hacks
// Loom does not natively support unobfuscated MC. The blocks below generate
// local-maven artefacts that satisfy Loom's expectations while performing
// identity (no-op) remapping.
// ---------------------------------------------------------------------------

if (is261 && isFabric) {
    val localMaven = rootDir.resolve(".gradle/local-maven")
    val intermediaryDir = localMaven.resolve("net/fabricmc/intermediary/$mcVersion")
    val jarFile = intermediaryDir.resolve("intermediary-$mcVersion-v2.jar")
    if (!jarFile.exists()) {
        intermediaryDir.mkdirs()
        intermediaryDir.resolve("intermediary-$mcVersion.pom").writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
                     xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <modelVersion>4.0.0</modelVersion>
              <groupId>net.fabricmc</groupId>
              <artifactId>intermediary</artifactId>
              <version>$mcVersion</version>
            </project>
        """.trimIndent())
        val tinyContent = "tiny\t2\t0\tofficial\tintermediary\n".toByteArray()
        ZipOutputStream(jarFile.outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry("mappings/mappings.tiny"))
            zip.write(tinyContent)
            zip.closeEntry()
        }
    }
    repositories {
        maven(localMaven) {
            name = "LocalIntermediary"
            content { includeModule("net.fabricmc", "intermediary") }
        }
    }
}

if (is261 && isNeoforge) {
    val localMaven = rootDir.resolve(".gradle/local-maven")
    val neoformVersion = "$mcVersion-1"
    val neoformDir = localMaven.resolve("net/neoforged/neoform/$neoformVersion")
    val patchedZip = neoformDir.resolve("neoform-$neoformVersion.zip")

    if (!patchedZip.exists()) {
        neoformDir.mkdirs()
        val neoformUrl = URI(
            "https://maven.neoforged.net/releases/net/neoforged/neoform/$neoformVersion/neoform-$neoformVersion.zip"
        ).toURL()
        val originalBytes = neoformUrl.readBytes()

        val entries = linkedMapOf<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(originalBytes)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                entries[entry.name] = if (entry.isDirectory) ByteArray(0) else zis.readBytes()
                entry = zis.nextEntry
            }
        }

        val json = com.google.gson.JsonParser.parseString(String(entries["config.json"]!!)).asJsonObject
        json.getAsJsonObject("data").addProperty("mappings", "config/joined.tsrg")
        json.addProperty("official", true)
        val functions = json.getAsJsonObject("functions")
        for (key in functions.keySet()) {
            val func = functions.getAsJsonObject(key)
            if (func.has("classpath") && !func.has("version")) {
                val classpath = func.getAsJsonArray("classpath")
                if (classpath.size() > 0) func.addProperty("version", classpath[0].asString)
                func.remove("classpath")
            }
            func.remove("java_version")
            if (!func.has("repo")) func.addProperty("repo", "https://maven.neoforged.net/releases/")
        }
        val joinedSteps = json.getAsJsonObject("steps").getAsJsonArray("joined")
        for (i in 0 until joinedSteps.size()) {
            val step = joinedSteps[i].asJsonObject
            if (step.get("type").asString == "preProcessJar") step.addProperty("name", "rename")
        }
        entries["config.json"] = com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(json).toByteArray()
        entries["config/joined.tsrg"] = "tsrg2 left right\n".toByteArray()

        ZipOutputStream(patchedZip.outputStream()).use { zos ->
            for ((name, bytes) in entries) {
                zos.putNextEntry(ZipEntry(name))
                if (bytes.isNotEmpty()) zos.write(bytes)
                zos.closeEntry()
            }
        }
        neoformDir.resolve("neoform-$neoformVersion.pom").writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
                     xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <modelVersion>4.0.0</modelVersion>
              <groupId>net.neoforged</groupId>
              <artifactId>neoform</artifactId>
              <version>$neoformVersion</version>
            </project>
        """.trimIndent())
    }

    repositories {
        exclusiveContent {
            forRepository { maven(localMaven) { name = "LocalNeoForm" } }
            filter { includeModule("net.neoforged", "neoform") }
        }
    }

    dependencies {
        components {
            withModule("net.neoforged.accesstransformers:at-cli") {
                allVariants { withDependencies {
                    removeAll { it.group == "org.ow2.asm" }
                    add("org.ow2.asm:asm:9.9.1")
                    add("org.ow2.asm:asm-tree:9.9.1")
                    add("org.ow2.asm:asm-commons:9.9.1")
                }}
            }
            withModule("net.neoforged:accesstransformers") {
                allVariants { withDependencies {
                    removeAll { it.group == "org.ow2.asm" }
                    add("org.ow2.asm:asm:9.9.1")
                    add("org.ow2.asm:asm-tree:9.9.1")
                    add("org.ow2.asm:asm-commons:9.9.1")
                }}
            }
        }
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.ow2.asm") useVersion("9.9.1")
        }
    }

    run {
        val loomCacheDir = file("${gradle.gradleUserHomeDir}/caches/fabric-loom")
        loomCacheDir.listFiles()?.filter { it.name.endsWith(".lock") && it.isFile }?.forEach {
            val content = it.readText().trim()
            val isStale = content == "disowned" || content.toLongOrNull()?.let { pid ->
                ProcessHandle.of(pid).isEmpty
            } ?: false
            if (isStale) it.delete()
        }
        val versionCacheDir = loomCacheDir.resolve(mcVersion)
        if (versionCacheDir.isDirectory) {
            versionCacheDir.listFiles()?.filter {
                it.isDirectory && it.name.startsWith("loom.mappings.") && it.name.contains("neoforge")
            }?.forEach { mappingDir ->
                val mojangTiny = mappingDir.resolve("mappings-mojang.tiny")
                val baseTiny = mappingDir.resolve("mappings-base.tiny")
                if (baseTiny.exists() && (!mojangTiny.exists() || mojangTiny.length() == 0L)) {
                    mojangTiny.writeText("tiny\t2\t0\tofficial\tintermediary\tnamed\tmojang\n")
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dependencies
// ---------------------------------------------------------------------------

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    if (is261) {
        mappings(loom.layered { })
    } else {
        mappings(loom.layered { officialMojangMappings() })
    }

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
        if (is261) {
            implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
            compileOnly("mezz.jei:jei-$minecraftVersion-common-api:$jeiVersion")
            compileOnly("mezz.jei:jei-$minecraftVersion-fabric-api:$jeiVersion")
        } else {
            modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
            modCompileOnly(files(rootProject.file("libs/jei-$minecraftVersion-common-api-intermediary-$jeiVersion.jar")))
            modCompileOnly(files(rootProject.file("libs/jei-$minecraftVersion-fabric-api-$jeiVersion.jar")))
        }
    }

    if (isNeoforge) {
        compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:$jeiVersion")
    }
}

if (isFabric) {
    fabricApi {
        configureTests {
            createSourceSet = true
            modId = "effigies-gametest"
            enableGameTests = true
            enableClientGameTests = false
            eula = true
        }
    }
    loom {
        runConfigs.named("gameTest") {
            runDir = "../../run/gametest/${stonecutter.current.project}"
        }
    }
    sourceSets["gametest"].java.exclude("**/neoforge12111/**", "**/neoforge261/**")
}

if (isNeoforge) {
    gameTestSourceSet!!.java.exclude("**/EffigiesFabricGameTests.java")
    if (is261) {
        gameTestSourceSet.java.exclude("**/neoforge12111/**")
    } else {
        gameTestSourceSet.java.exclude("**/neoforge261/**")
    }
    gameTestSourceSet.resources.exclude("fabric.mod.json")

    dependencies {
        add("neoForge", "net.neoforged:neoforge:$neoforgeVersion")
    }
}

// ---------------------------------------------------------------------------
// Java & tasks
// ---------------------------------------------------------------------------

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

if (isNeoforge) {
    val mainSourceSet = sourceSets.main.get()
    val testSourceSet = gameTestSourceSet!!

    configurations[testSourceSet.implementationConfigurationName]
        .extendsFrom(configurations["implementation"])
    configurations[testSourceSet.runtimeOnlyConfigurationName]
        .extendsFrom(configurations["runtimeOnly"])

    testSourceSet.compileClasspath += mainSourceSet.output + configurations["compileClasspath"]
    testSourceSet.runtimeClasspath += testSourceSet.output + mainSourceSet.output + configurations["runtimeClasspath"]

    tasks.named<JavaCompile>(testSourceSet.compileJavaTaskName) {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
    }
}

val gameTestTaskName = if (isFabric) "runGameTest" else "runGameTestServer"
tasks.register("runEffigiesGameTests") {
    group = "verification"
    description = "Runs the Effigies GameTests for ${stonecutter.current.project}."
    dependsOn(gameTestTaskName)
}

val targetOrder = stonecutter.tree.nodes.map { it.metadata.project }
val currentTargetIndex = targetOrder.indexOf(stonecutter.current.project)
if (currentTargetIndex > 0) {
    val previousTarget = targetOrder[currentTargetIndex - 1]
    tasks.named("build") {
        mustRunAfter(":$previousTarget:build")
    }
    tasks.named("runEffigiesGameTests") {
        mustRunAfter(":$previousTarget:runEffigiesGameTests")
    }
}

if (isNeoforge) {
    tasks.named("build") {
        dependsOn("runGameTestServer")
    }
}

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
    val minecraftVersionRange = if (is261) ">=26.1" else ">=1.21.11"
    val neoforgeVersionRange = if (is261) "[26.1,)" else "[21.11,)"

    val props = mapOf(
        "version" to modVersion,
        "minecraft_version" to minecraftVersionRange,
        "java_version" to javaVersion,
        "loader" to loader,
        "neoforge_version_range" to neoforgeVersionRange,
        "access_widener_entry" to when {
            isFabric -> """"accessWidener": "effigies.accesswidener","""
            else -> ""
        }
    )

    inputs.properties(props)

    if (!is261) {
        exclude("effigies.classtweaker")
    }
    if (isNeoforge) {
        exclude("effigies.accesswidener")
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand(props)
    }
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

if (is261 && isFabric) {
    val restoreFabricGameTestMetadata = tasks.register("restoreFabricGameTestMetadata") {
        doLast {
            val metadata = file("build/resources/main/fabric.mod.json")
            if (metadata.exists()) {
                metadata.writeText(metadata.readText().replace(
                    "\"accessWidener\": \"effigies.classtweaker\"",
                    "\"accessWidener\": \"effigies.accesswidener\""
                ))
            }
        }
    }

    tasks.named("runGameTest") {
        doFirst {
            val metadata = file("build/resources/main/fabric.mod.json")
            if (metadata.exists()) {
                metadata.writeText(metadata.readText().replace(
                    "\"accessWidener\": \"effigies.accesswidener\"",
                    "\"accessWidener\": \"effigies.classtweaker\""
                ))
            }
        }
        finalizedBy(restoreFabricGameTestMetadata)
    }

    tasks.named("remapJar") {
        doLast {
            val jar = outputs.files.singleFile
            val classTweakerContent = rootProject.file("src/main/resources/effigies.classtweaker").readBytes()
            val entries = linkedMapOf<String, ByteArray>()
            ZipInputStream(jar.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    when {
                        entry.name == "effigies.accesswidener" -> {
                            entries["effigies.classtweaker"] = classTweakerContent
                        }
                        entry.name == "fabric.mod.json" -> {
                            val content = String(zis.readBytes())
                            entries[entry.name] = content.replace("effigies.accesswidener", "effigies.classtweaker").toByteArray()
                        }
                        !entry.isDirectory -> {
                            entries[entry.name] = zis.readBytes()
                        }
                        else -> {
                            entries[entry.name] = ByteArray(0)
                        }
                    }
                    entry = zis.nextEntry
                }
            }
            ZipOutputStream(jar.outputStream()).use { zos ->
                for ((name, bytes) in entries) {
                    zos.putNextEntry(ZipEntry(name))
                    if (bytes.isNotEmpty()) zos.write(bytes)
                    zos.closeEntry()
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}

