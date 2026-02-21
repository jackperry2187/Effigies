package jackperry2187.effigies.config;

import jackperry2187.effigies.Effigies;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static jackperry2187.effigies.config.ConfigLines.getBaseConfigLines;

/**
 * Handles config file creation and version checking.
 * Creates the config file from template if it doesn't exist,
 * or regenerates it if the version has changed.
 */
public final class InitializeConfig {
    private InitializeConfig() {}

    //? if fabric {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    //?} else {
    /*private static final Path configPath = FMLPaths.CONFIGDIR.get();
    *///?}

    private static final Path modConfigFile = configPath.resolve(Effigies.MOD_ID + "-config.toml");

    /**
     * Gets the path to the config file.
     * Used by ConfigSettings to read the file.
     */
    public static Path getConfigFilePath() {
        return modConfigFile;
    }

    /**
     * Generates the config file if it doesn't exist or if the version has changed.
     * Should be called during mod initialization.
     */
    public static void generateConfigFile() {
        try {
            if (Files.exists(modConfigFile)) {
                Effigies.LOGGER.info("Config file already exists at {}", modConfigFile);
                if (!checkConfigVersion()) {
                    Effigies.LOGGER.info("Config version mismatch, regenerating config file!");
                    Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.TRUNCATE_EXISTING);
                } else {
                    Effigies.LOGGER.info("Config version is correct!");
                }
            } else {
                Effigies.LOGGER.info("Creating config file at {}", modConfigFile);
                Files.createDirectories(modConfigFile.getParent());
                Files.createFile(modConfigFile);
                Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            Effigies.LOGGER.error("Failed to create config file!", e);
            throw new RuntimeException("Failed to create Effigies config file", e);
        }
    }

    /**
     * Checks if the config file version matches the current version.
     * @return true if version matches, false if regeneration is needed
     */
    private static boolean checkConfigVersion() throws IOException {
        for (String line : Files.readAllLines(modConfigFile)) {
            if (line.startsWith("#")) continue; // Skip comments
            if (line.startsWith("configVersion=")) {
                String version = line.split("=")[1].trim();
                try {
                    int fileVersion = Integer.parseInt(version);
                    if (fileVersion == DefaultSettings.CONFIG_VERSION) {
                        return true;
                    }
                    Effigies.LOGGER.warn("Config version {} does not match current version {}", 
                        fileVersion, DefaultSettings.CONFIG_VERSION);
                    return false;
                } catch (NumberFormatException e) {
                    Effigies.LOGGER.warn("Invalid config version format: {}", version);
                    return false;
                }
            }
        }
        // configVersion not found
        Effigies.LOGGER.warn("Config version not found in file");
        return false;
    }
}
