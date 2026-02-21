# Configuration System Architecture - Gentle Reminders Mod

This document provides a comprehensive summary of the configuration file system architecture used in the Gentle Reminders Minecraft mod. It covers creating configuration files from templates on game load, reading existing configurations, and all the cross-platform/cross-version considerations needed to replicate this system in another project.

---

## Table of Contents

1. [Overview](#overview)
2. [File Structure](#file-structure)
3. [Core Components](#core-components)
4. [Initialization Flow](#initialization-flow)
5. [Version & Platform Considerations](#version--platform-considerations)
6. [Detailed Implementation Guide](#detailed-implementation-guide)
7. [Configuration File Format](#configuration-file-format)
8. [Code Reference](#code-reference)

---

## Overview

The configuration system follows these principles:

1. **Template-based creation**: When the mod loads, it checks if a config file exists. If not, it creates one from a template with default values.
2. **Version migration**: The config has a version number. If the stored version differs from the current version, the file is regenerated (to handle schema changes).
3. **Runtime reading**: Config values are read into static fields for easy access throughout the mod.
4. **Runtime editing**: Config values can be modified during gameplay and persisted back to the file.

### Key Features

- **Client-side only**: Configuration is only loaded on the client (uses `@Environment(EnvType.CLIENT)` / `@OnlyIn(Dist.CLIENT)`)
- **TOML-like format**: Uses a simple key=value format with support for arrays and comments
- **Cross-platform**: Works on both Fabric and NeoForge mod loaders
- **Cross-version**: Supports multiple Minecraft versions (1.20.1, 1.21.1, 1.21.6+)

---

## File Structure

```
src/main/java/your/modid/
├── YourMod.java                    # Main mod class (common initialization)
├── YourModClient.java              # Client-specific initialization
├── config/
│   ├── InitializeConfig.java      # Config file creation & version checking
│   ├── ConfigLines.java           # Template content generator
│   ├── DefaultSettings.java       # Default values definition
│   └── client/
│       ├── ConfigSettings.java    # Runtime config storage & reading
│       └── EditConfigSettings.java # Config modification utilities
├── fabric/
│   ├── YourModFabric.java         # Fabric entry point (main)
│   └── YourModClientFabric.java   # Fabric entry point (client)
├── neoforge/
│   └── YourModNeoForge.java       # NeoForge entry point (combined)
└── util/
    └── Message.java               # Data class example (serializable to config)
```

---

## Core Components

### 1. InitializeConfig.java

**Purpose**: Creates the config file if it doesn't exist, or regenerates it if the version has changed.

**Key responsibilities**:
- Resolve the config directory path (platform-specific)
- Check if config file exists
- Verify config version matches current version
- Create/overwrite config file with template content

### 2. ConfigLines.java

**Purpose**: Generates the list of strings that form the config file template.

**Key responsibilities**:
- Define all config keys with documentation comments
- Include default values from `DefaultSettings`
- Handle complex types (arrays, objects) serialization

### 3. DefaultSettings.java

**Purpose**: Central location for all default configuration values.

**Key responsibilities**:
- Define constants for each config option
- Provide type-safe default values
- Handle platform-specific types (Formatting vs ChatFormatting)

### 4. ConfigSettings.java

**Purpose**: Read config file at startup and store values in static fields.

**Key responsibilities**:
- Parse config file line by line
- Convert string values to appropriate types
- Store values in static fields for runtime access
- Handle complex parsing (arrays, nested structures)

### 5. EditConfigSettings.java

**Purpose**: Modify config values at runtime and persist changes.

**Key responsibilities**:
- Find and replace specific config values
- Handle array element modifications
- Write changes back to file

---

## Initialization Flow

```
Game Startup
     │
     ▼
┌────────────────────────────────┐
│  Mod Loader Entry Point        │
│  (Fabric: onInitialize         │
│   NeoForge: constructor)       │
└────────────────────────────────┘
     │
     ▼
┌────────────────────────────────┐
│  YourMod.init()                │
│  - Check if client-side        │
│  - Call generateConfigFile()   │
└────────────────────────────────┘
     │
     ▼
┌────────────────────────────────┐
│  InitializeConfig              │
│  .generateConfigFile()         │
│  ┌──────────────────────────┐  │
│  │ File exists?             │  │
│  │   YES → Check version    │  │
│  │   NO  → Create file      │  │
│  └──────────────────────────┘  │
│  ┌──────────────────────────┐  │
│  │ Version matches?         │  │
│  │   YES → Continue         │  │
│  │   NO  → Overwrite file   │  │
│  └──────────────────────────┘  │
└────────────────────────────────┘
     │
     ▼
┌────────────────────────────────┐
│  Client Entry Point            │
│  (Fabric: onInitializeClient   │
│   NeoForge: in constructor)    │
└────────────────────────────────┘
     │
     ▼
┌────────────────────────────────┐
│  ConfigSettings.initialize()   │
│  - Read all lines from file    │
│  - Parse key=value pairs       │
│  - Store in static fields      │
│  - Process complex structures  │
└────────────────────────────────┘
     │
     ▼
  Config Ready for Use
```

---

## Version & Platform Considerations

### Stonecutter Preprocessor Syntax

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) for multi-version/multi-platform support. Stonecutter uses comment-based preprocessor directives:

```java
//? if fabric {
// Code for Fabric loader
//?} else {
/*// Code for NeoForge loader
*///?}
```

### Platform Differences

#### 1. Config Directory Path

| Platform | API |
|----------|-----|
| Fabric | `FabricLoader.getInstance().getConfigDir()` |
| NeoForge | `FMLPaths.CONFIGDIR.get()` |

```java
//? if fabric {
private final static Path configPath = FabricLoader.getInstance().getConfigDir();
//?} else {
/*private final static Path configPath = FMLPaths.CONFIGDIR.get();
*///?}
```

#### 2. Client-Side Annotations

| Platform | Annotation | Enum |
|----------|------------|------|
| Fabric | `@Environment(value = EnvType.CLIENT)` | `EnvType.CLIENT` |
| NeoForge | `@OnlyIn(Dist.CLIENT)` | `Dist.CLIENT` |

```java
//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class ConfigSettings {
```

#### 3. Environment Detection

| Platform | Code |
|----------|------|
| Fabric | `FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT` |
| NeoForge | `FMLEnvironment.dist == Dist.CLIENT` |

#### 4. Text Components

| Platform | Text Class | Mutable Class | Formatting Class |
|----------|-----------|---------------|------------------|
| Fabric | `Text` | `MutableText` | `Formatting` |
| NeoForge | `Component` | `MutableComponent` | `ChatFormatting` |

```java
//? if fabric {
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
//?} else {
/*import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
*///?}
```

#### 5. Resource Identifiers

| Platform | Class | Creation (1.20.1) | Creation (1.21.1+) |
|----------|-------|-------------------|---------------------|
| Fabric | `Identifier` | `new Identifier("namespace", "path")` | `Identifier.of("namespace", "path")` |
| NeoForge | `ResourceLocation` | N/A | `ResourceLocation.fromNamespaceAndPath("namespace", "path")` |

```java
//? if fabric {
//? if mcGte121 {
private static final Identifier TEXTURE = Identifier.of("modid", "textures/example.png");
//?} else {
/*private static final Identifier TEXTURE = new Identifier("modid", "textures/example.png");
*///?}
//?} else {
/*private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("modid", "textures/example.png");
*///?}
```

### Minecraft Version Differences

| Version | Java | Identifier API | Notable Changes |
|---------|------|----------------|-----------------|
| 1.20.1 | 17 | `new Identifier()` | Legacy constructors |
| 1.21.1 | 21 | `Identifier.of()` | New factory methods |
| 1.21.6+ | 21 | `Identifier.of()` | Same as 1.21.1 |

### Entry Point Differences

#### Fabric

Uses separate entry points defined in `fabric.mod.json`:

```json
{
    "entrypoints": {
        "main": ["your.modid.fabric.YourModFabric"],
        "client": ["your.modid.fabric.YourModClientFabric"]
    }
}
```

- `main` entry point: Runs on both client and server
- `client` entry point: Runs only on client

#### NeoForge

Uses a single entry point with `@Mod` annotation:

```java
@Mod(YourMod.MOD_ID)
public class YourModNeoForge {
    public YourModNeoForge(IEventBus modEventBus) {
        YourMod.init();
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ConfigSettings.initialize();
            NeoForge.EVENT_BUS.register(this);
        }
    }
}
```

---

## Detailed Implementation Guide

### Step 1: Create Default Settings

Create a class to hold all default configuration values:

```java
package your.modid.config;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class DefaultSettings {
    // Config version - increment when schema changes
    public static final int configVersion = 1;
    
    // Simple settings
    public static final boolean enabled = true;
    public static final int someNumber = 10;
    public static final String someString = "default";
    
    // Complex settings (if needed)
    // public static final List<YourType> defaultList = new ArrayList<>();
}
```

### Step 2: Create Config Template Generator

Create a class that generates the config file content:

```java
package your.modid.config;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static your.modid.config.DefaultSettings.*;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class ConfigLines {
    public static List<String> getBaseConfigLines() {
        return new ArrayList<>(Arrays.asList(
                "### Your Mod Config",
                "### This file is auto-generated. Edit values below.",
                "",
                "# Config version - do not modify manually",
                "configVersion=" + configVersion,
                "",
                "# Enable or disable the mod",
                "enabled=" + enabled,
                "",
                "# A numeric setting",
                "someNumber=" + someNumber,
                "",
                "# A string setting (use quotes)",
                "someString=\"" + someString + "\""
        ));
    }
}
```

### Step 3: Create Config Initializer

Create a class to handle file creation and version checking:

```java
package your.modid.config;

import your.modid.YourMod;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static your.modid.config.ConfigLines.getBaseConfigLines;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class InitializeConfig {
    //? if fabric {
    private final static Path configPath = FabricLoader.getInstance().getConfigDir();
    //?} else {
    /*private final static Path configPath = FMLPaths.CONFIGDIR.get();
    *///?}
    private final static Path modConfigFile = configPath.resolve(YourMod.MOD_ID + "-config.toml");

    public static void generateConfigFile() {
        try {
            if (Files.exists(modConfigFile)) {
                YourMod.LOGGER.info("Config file already exists!");
                if (!checkConfigVersion()) {
                    YourMod.LOGGER.info("Config version mismatch, regenerating config file!");
                    Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.TRUNCATE_EXISTING);
                }
                YourMod.LOGGER.info("Config version is correct!");
            } else {
                YourMod.LOGGER.info("Creating config file!");
                Files.createFile(modConfigFile);
                Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            YourMod.LOGGER.error("Failed to create config file!");
            throw new RuntimeException(e);
        }
    }

    public static boolean checkConfigVersion() throws IOException {
        if (!Files.exists(modConfigFile)) Files.createFile(modConfigFile);
        boolean isCorrectVersion = false;

        for (String line : Files.readAllLines(modConfigFile)) {
            if (line.startsWith("#")) continue; // Skip comments
            if (line.startsWith("configVersion=")) {
                String version = line.split("=")[1];
                if (Integer.parseInt(version) == DefaultSettings.configVersion) {
                    isCorrectVersion = true;
                    break;
                }
            }
        }

        return isCorrectVersion;
    }
}
```

### Step 4: Create Runtime Config Settings

Create a class to read and store config values:

```java
package your.modid.config.client;

import your.modid.YourMod;
import your.modid.config.DefaultSettings;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.nio.file.Files;
import java.nio.file.Path;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class ConfigSettings {
    private static boolean isInitialized = false;

    //? if fabric {
    public static final Path configPath = FabricLoader.getInstance().getConfigDir();
    //?} else {
    /*public static final Path configPath = FMLPaths.CONFIGDIR.get();
    *///?}
    public static final Path modConfigFile = configPath.resolve(YourMod.MOD_ID + "-config.toml");

    // Config values - populated from file
    public static int configVersion;
    public static boolean enabled;
    public static int someNumber;
    public static String someString;

    public static void initialize() {
        if (isInitialized) {
            YourMod.LOGGER.error("ConfigSettings already initialized!");
            return;
        }

        if (!Files.exists(modConfigFile)) {
            YourMod.LOGGER.error("Config file does not exist!");
            throw new RuntimeException("Config file does not exist!");
        }

        readConfigFile();
        isInitialized = true;
    }

    private static void readConfigFile() {
        try {
            for (String line : Files.readAllLines(modConfigFile)) {
                // Skip comments
                if (line.startsWith("#")) continue;
                
                // Parse each config key
                if (line.startsWith("configVersion=")) {
                    configVersion = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("enabled=")) {
                    enabled = Boolean.parseBoolean(line.split("=")[1]);
                } else if (line.startsWith("someNumber=")) {
                    someNumber = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("someString=")) {
                    // Remove surrounding quotes for strings
                    someString = line.split("=")[1].strip().replace("\"", "");
                }
            }
        } catch (Exception e) {
            YourMod.LOGGER.error("Failed to read config file!");
            throw new RuntimeException(e);
        }
    }
}
```

### Step 5: Create Config Editor (Optional)

Create utilities to modify config at runtime:

```java
package your.modid.config.client;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static your.modid.config.client.ConfigSettings.modConfigFile;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class EditConfigSettings {
    /**
     * Set a simple key=value pair in the config file
     * @param key The config key to modify
     * @param value The new value
     * @return true if the key was found and modified
     */
    public static boolean setFileValue(String key, String value) {
        try {
            boolean hit = false;
            List<String> lines = Files.readAllLines(modConfigFile);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.strip().startsWith(key + "=")) continue;
                hit = true;
                lines.set(i, key + "=" + value);
                break;
            }
            Files.write(modConfigFile, lines, StandardOpenOption.TRUNCATE_EXISTING);
            return hit;
        } catch (IOException e) {
            return false;
        }
    }
}
```

### Step 6: Create Main Mod Class

```java
package your.modid;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
*///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static your.modid.config.InitializeConfig.generateConfigFile;

public class YourMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("Your Mod");
    public static final String MOD_ID = "yourmodid";

    public static void init() {
        LOGGER.info("Initializing Your Mod...");

        //? if fabric {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            generateConfigFile();
        } else {
            LOGGER.warn("This is a client-side mod!");
        }
        //?} else {
        /*if (FMLEnvironment.dist == Dist.CLIENT) {
            generateConfigFile();
        } else {
            LOGGER.warn("This is a client-side mod!");
        }
        *///?}

        LOGGER.info("Initialized Successfully!");
    }
}
```

### Step 7: Create Platform Entry Points

#### Fabric Entry Points

**YourModFabric.java** (main entry point):

```java
package your.modid.fabric;

//? if fabric {
import your.modid.YourMod;
import net.fabricmc.api.ModInitializer;

public class YourModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YourMod.init();
    }
}
//?} else {
/*public class YourModFabric {
    // NeoForge stub
}*/
//?}
```

**YourModClientFabric.java** (client entry point):

```java
package your.modid.fabric;

//? if fabric {
import your.modid.config.client.ConfigSettings;
import net.fabricmc.api.ClientModInitializer;

public class YourModClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigSettings.initialize();
        
        // Register other client-side features here
    }
}
//?} else {
/*public class YourModClientFabric {
    // NeoForge stub
}*/
//?}
```

#### NeoForge Entry Point

**YourModNeoForge.java**:

```java
//? if neoforge {
/*package your.modid.neoforge;

import your.modid.YourMod;
import your.modid.config.client.ConfigSettings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(YourMod.MOD_ID)
public class YourModNeoForge {
    public YourModNeoForge(IEventBus modEventBus) {
        YourMod.init();
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ConfigSettings.initialize();
            NeoForge.EVENT_BUS.register(this);
        }
    }
    
    // Register event handlers as needed
}
*///?} else {
package your.modid.neoforge;

public class YourModNeoForge {
    // Fabric stub
}
//?}
```

### Step 8: Configure Mod Descriptors

#### fabric.mod.json

```json
{
    "schemaVersion": 1,
    "id": "yourmodid",
    "version": "${version}",
    "name": "Your Mod Name",
    "entrypoints": {
        "main": ["your.modid.fabric.YourModFabric"],
        "client": ["your.modid.fabric.YourModClientFabric"]
    },
    "depends": {
        "fabricloader": ">=0.16.0",
        "minecraft": "${minecraft_version}",
        "java": ">=${java_version}",
        "fabric-api": "*"
    }
}
```

#### META-INF/neoforge.mods.toml

```toml
modLoader = "javafml"
loaderVersion = "[1,)"
license = "MIT"

[[mods]]
modId = "yourmodid"
version = "${version}"
displayName = "Your Mod Name"

[[dependencies.yourmodid]]
modId = "neoforge"
type = "required"
versionRange = "[21.0,)"
ordering = "NONE"
side = "BOTH"

[[dependencies.yourmodid]]
modId = "minecraft"
type = "required"
versionRange = "[${minecraft_version},)"
ordering = "NONE"
side = "BOTH"
```

---

## Configuration File Format

The generated config file uses a simple TOML-like format:

```toml
### Your Mod Config
### This file is auto-generated. Edit values below.

# Config version - do not modify manually
configVersion=1

# Enable or disable the mod
enabled=true

# A numeric setting
someNumber=10

# A string setting (use quotes)
someString="default"

# For arrays (if needed)
items=[
 {id=0, name="item1", value=100},
 {id=1, name="item2", value=200},
]
```

### Parsing Rules

1. Lines starting with `#` are comments and skipped
2. Empty lines are skipped
3. Key-value pairs use `key=value` format
4. Strings should be quoted with `"`
5. Booleans are `true` or `false`
6. Numbers are parsed as integers
7. Arrays use `key=[` to start and `]` to end

---

## Code Reference

### Summary Table

| File | Purpose | Platform-Specific |
|------|---------|-------------------|
| `DefaultSettings.java` | Default values | Yes (Text types) |
| `ConfigLines.java` | Template generator | Minimal |
| `InitializeConfig.java` | File creation | Yes (config path) |
| `ConfigSettings.java` | Runtime storage | Yes (config path, types) |
| `EditConfigSettings.java` | File modification | Minimal |
| `YourMod.java` | Common init | Yes (environment check) |
| `YourModFabric.java` | Fabric entry | Fabric only |
| `YourModClientFabric.java` | Fabric client entry | Fabric only |
| `YourModNeoForge.java` | NeoForge entry | NeoForge only |

### Key Dependencies

#### Fabric

```groovy
modImplementation("net.fabricmc:fabric-loader:${loader_version}")
modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
```

#### NeoForge

```groovy
implementation("net.neoforged:neoforge:${neoforge_version}")
```

### Stonecutter Setup (for multi-version)

See `build.gradle.kts` and `stonecutter.gradle.kts` for the full setup. Key constants:

```kotlin
stonecutter {
    const("fabric", isFabric)
    const("neoforge", isNeoforge)
    const("mcGte121", compareVersions(mcSemver, mc1211) >= 0)
}
```

---

## Best Practices

1. **Always increment configVersion** when changing the config schema to trigger regeneration
2. **Use comments generously** in the config template to help users understand options
3. **Validate parsed values** and fall back to defaults if parsing fails
4. **Log errors clearly** when config reading fails
5. **Keep ConfigSettings static fields** for easy access throughout the mod
6. **Test on both platforms** if supporting Fabric and NeoForge
7. **Handle missing keys gracefully** by using default values if a key is not found

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Config not created | `init()` not called on client | Verify entry point registration |
| Config regenerated every launch | configVersion mismatch | Ensure DefaultSettings matches file |
| NullPointerException reading config | File doesn't exist | Call `generateConfigFile()` before `initialize()` |
| Wrong config path | Platform detection failed | Check Stonecutter preprocessor directives |
| Values not updating | Reading from file instead of memory | Read from ConfigSettings.* static fields |

---

*Generated from analysis of the Gentle Reminders mod configuration system.*
