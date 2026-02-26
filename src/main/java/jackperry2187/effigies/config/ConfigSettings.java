package jackperry2187.effigies.config;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.PikeTier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores configuration values read from the config file.
 * Values are populated during mod initialization and accessed at runtime.
 */
public final class ConfigSettings {
    private ConfigSettings() {}

    private static boolean isInitialized = false;

    // Config values - populated from file
    public static int configVersion;
    public static int woodenPikeRadius;
    public static int stonePikeRadius;
    public static int copperPikeRadius;
    public static int ironPikeRadius;
    public static int goldenPikeRadius;
    public static int diamondPikeRadius;
    public static int netheritePikeRadius;

    // Block ID -> Entity ID mappings (e.g. "minecraft:skeleton_skull" -> "minecraft:skeleton")
    private static Map<String, String> blockToEntityMappings = new HashMap<>();

    /**
     * Returns true if the config has been initialized.
     * Used by PikeTier to determine whether to use config values or defaults.
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Gets the chunk radius for a specific pike tier from the config.
     * @param tier The pike tier to get the radius for
     * @return The configured chunk radius
     */
    public static int getPikeRadius(PikeTier tier) {
        return switch (tier) {
            case WOODEN -> woodenPikeRadius;
            case STONE -> stonePikeRadius;
            case COPPER -> copperPikeRadius;
            case IRON -> ironPikeRadius;
            case GOLDEN -> goldenPikeRadius;
            case DIAMOND -> diamondPikeRadius;
            case NETHERITE -> netheritePikeRadius;
        };
    }

    /**
     * Gets the maximum configured pike radius across all tiers.
     * Used by PikeRegistry for efficient spawn checking.
     */
    public static int getMaxPikeRadius() {
        return Math.max(woodenPikeRadius,
            Math.max(stonePikeRadius,
                Math.max(copperPikeRadius,
                    Math.max(ironPikeRadius,
                        Math.max(goldenPikeRadius,
                            Math.max(diamondPikeRadius, netheritePikeRadius))))));
    }

    /**
     * Gets the entity ID mapped to the given block ID, or null if not configured.
     */
    @Nullable
    public static String getEntityIdForBlock(String blockId) {
        return blockToEntityMappings.get(blockId);
    }

    /**
     * Returns an unmodifiable view of the block-to-entity mappings.
     * Used for config sync and logging.
     */
    public static Map<String, String> getBlockToEntityMappings() {
        return Collections.unmodifiableMap(blockToEntityMappings);
    }

    /**
     * Initializes config values by reading from the config file.
     * Should be called after InitializeConfig.generateConfigFile().
     */
    public static void initialize() {
        if (isInitialized) {
            Effigies.LOGGER.warn("ConfigSettings already initialized!");
            return;
        }

        Path configFile = InitializeConfig.getConfigFilePath();
        if (!Files.exists(configFile)) {
            Effigies.LOGGER.error("Config file does not exist at {}!", configFile);
            throw new RuntimeException("Config file does not exist!");
        }

        readConfigFile(configFile);
        isInitialized = true;
        Effigies.LOGGER.info("Config loaded successfully!");
    }

    /**
     * Applies config values synced from the server.
     * Called on the client when receiving the config sync packet.
     */
    public static void applySyncedValues(ConfigSyncPayload payload) {
        woodenPikeRadius = payload.woodenPikeRadius();
        stonePikeRadius = payload.stonePikeRadius();
        copperPikeRadius = payload.copperPikeRadius();
        ironPikeRadius = payload.ironPikeRadius();
        goldenPikeRadius = payload.goldenPikeRadius();
        diamondPikeRadius = payload.diamondPikeRadius();
        netheritePikeRadius = payload.netheritePikeRadius();
        blockToEntityMappings = new HashMap<>(payload.blockEntityMappings());
        Effigies.LOGGER.info("Config synced from server: wooden={}, stone={}, copper={}, iron={}, golden={}, diamond={}, netherite={}, mappings={}",
            woodenPikeRadius, stonePikeRadius, copperPikeRadius, ironPikeRadius,
            goldenPikeRadius, diamondPikeRadius, netheritePikeRadius, blockToEntityMappings.size());
    }

    /**
     * Reloads config values from the config file.
     * Called on server start to ensure local config values are restored
     * (e.g. after returning from a multiplayer server to singleplayer).
     */
    public static void reload() {
        Path configFile = InitializeConfig.getConfigFilePath();
        if (!Files.exists(configFile)) {
            Effigies.LOGGER.warn("Config file not found during reload, keeping current values");
            return;
        }
        readConfigFile(configFile);
        Effigies.LOGGER.info("Config reloaded from file");
    }

    /**
     * Reads and parses the config file, populating static fields.
     */
    private static void readConfigFile(Path configFile) {
        // Set defaults first in case some values are missing
        configVersion = DefaultSettings.CONFIG_VERSION;
        woodenPikeRadius = DefaultSettings.WOODEN_PIKE_RADIUS;
        stonePikeRadius = DefaultSettings.STONE_PIKE_RADIUS;
        copperPikeRadius = DefaultSettings.COPPER_PIKE_RADIUS;
        ironPikeRadius = DefaultSettings.IRON_PIKE_RADIUS;
        goldenPikeRadius = DefaultSettings.GOLDEN_PIKE_RADIUS;
        diamondPikeRadius = DefaultSettings.DIAMOND_PIKE_RADIUS;
        netheritePikeRadius = DefaultSettings.NETHERITE_PIKE_RADIUS;

        // Populate default block-to-entity mappings
        blockToEntityMappings = new HashMap<>();
        for (String mapping : DefaultSettings.DEFAULT_BLOCK_ENTITY_MAPPINGS) {
            String[] parts = mapping.split("=", 2);
            if (parts.length == 2) {
                blockToEntityMappings.put(parts[0].trim(), parts[1].trim());
            }
        }

        try {
            boolean foundMappings = false;
            for (String line : Files.readAllLines(configFile)) {
                // Skip comments and empty lines
                if (line.startsWith("#") || line.trim().isEmpty()) continue;

                // Parse key=value pairs
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (key.contains(":")) {
                            // Block-to-entity mapping (namespace:id format)
                            if (!isValidNamespacedId(key)) {
                                Effigies.LOGGER.error("Invalid block ID format in mapping: '{}={}' (expected namespace:id instead of {})", key, value, key);
                                continue;
                            }
                            if (!isValidNamespacedId(value)) {
                                Effigies.LOGGER.error("Invalid entity ID format in mapping: '{}={}' (expected namespace:id instead of {})", key, value, value);
                                continue;
                            }
                            if (!foundMappings) {
                                // First mapping found from file replaces all defaults
                                blockToEntityMappings.clear();
                                foundMappings = true;
                            }
                            blockToEntityMappings.put(key, value);
                        } else if (value.contains(":")) {
                            Effigies.LOGGER.error("Invalid block ID format in mapping: '{}={}' (expected namespace:id instead of {})", key, value, key);
                        } else {
                            parseConfigValue(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Effigies.LOGGER.error("Failed to read config file, using defaults!", e);
        }

        // Log the loaded values
        Effigies.LOGGER.info("Pike radii: wooden={}, stone={}, copper={}, iron={}, golden={}, diamond={}, netherite={}",
            woodenPikeRadius, stonePikeRadius, copperPikeRadius, ironPikeRadius,
            goldenPikeRadius, diamondPikeRadius, netheritePikeRadius);
        Effigies.LOGGER.info("Block-to-entity mappings: {}", blockToEntityMappings);
    }

    /**
     * Parses a single config key-value pair and updates the appropriate field.
     */
    private static void parseConfigValue(String key, String value) {
        try {
            switch (key) {
                case "configVersion" -> configVersion = Integer.parseInt(value);
                case "wooden_pike_radius" -> woodenPikeRadius = parseRadius(value, "wooden", DefaultSettings.WOODEN_PIKE_RADIUS);
                case "stone_pike_radius" -> stonePikeRadius = parseRadius(value, "stone", DefaultSettings.STONE_PIKE_RADIUS);
                case "copper_pike_radius" -> copperPikeRadius = parseRadius(value, "copper", DefaultSettings.COPPER_PIKE_RADIUS);
                case "iron_pike_radius" -> ironPikeRadius = parseRadius(value, "iron", DefaultSettings.IRON_PIKE_RADIUS);
                case "golden_pike_radius" -> goldenPikeRadius = parseRadius(value, "golden", DefaultSettings.GOLDEN_PIKE_RADIUS);
                case "diamond_pike_radius" -> diamondPikeRadius = parseRadius(value, "diamond", DefaultSettings.DIAMOND_PIKE_RADIUS);
                case "netherite_pike_radius" -> netheritePikeRadius = parseRadius(value, "netherite", DefaultSettings.NETHERITE_PIKE_RADIUS);
                default -> Effigies.LOGGER.warn("Unknown config key '{}' with value '{}', ignoring", key, value);
            }
        } catch (NumberFormatException e) {
            Effigies.LOGGER.warn("Invalid value for {}: {}", key, value);
        }
    }

    /**
     * Parses a radius value. A value of -1 disables the pike tier
     * (it will never block spawns). All other negative values are invalid.
     */
    private static int parseRadius(String value, String pikeName, int defaultValue) {
        int radius = Integer.parseInt(value);
        if (radius < -1) {
            Effigies.LOGGER.warn("Invalid radius for {} pike: {}, using default: {}", 
                pikeName, radius, defaultValue);
            return defaultValue;
        }
        return radius;
    }

    /**
     * Validates that a string is a valid namespaced ID (namespace:path),
     * with both namespace and path being non-empty.
     */
    private static boolean isValidNamespacedId(String id) {
        int colonIndex = id.indexOf(':');
        if (colonIndex <= 0 || colonIndex >= id.length() - 1) {
            return false;
        }
        // Ensure there's only one colon
        return id.indexOf(':', colonIndex + 1) == -1;
    }
}
