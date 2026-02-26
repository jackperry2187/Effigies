package jackperry2187.effigies.config;

import java.util.List;

/**
 * Default configuration values for Effigies.
 * These values are used when creating the config file template
 * and as fallbacks if config parsing fails.
 */
public final class DefaultSettings {
    private DefaultSettings() {}

    /**
     * Config version - increment when schema changes to trigger regeneration.
     */
    public static final int CONFIG_VERSION = 2;

    /**
     * Default chunk radius for each pike tier.
     * Radius of -1 = disabled (pike never blocks spawns).
     * Radius of 0 = only the chunk the pike is in.
     * Radius of N = the pike's chunk plus N chunks in each direction.
     */
    public static final int WOODEN_PIKE_RADIUS = 0;
    public static final int STONE_PIKE_RADIUS = 1;
    public static final int COPPER_PIKE_RADIUS = 2;
    public static final int IRON_PIKE_RADIUS = 4;
    public static final int GOLDEN_PIKE_RADIUS = 6;
    public static final int DIAMOND_PIKE_RADIUS = 8;
    public static final int NETHERITE_PIKE_RADIUS = 16;

    /**
     * Default block-to-entity mappings for pikes.
     * Format: "namespace:block_id=namespace:entity_id"
     */
    public static final List<String> DEFAULT_BLOCK_ENTITY_MAPPINGS = List.of(
        "minecraft:skeleton_skull=minecraft:skeleton",
        "minecraft:wither_skeleton_skull=minecraft:wither_skeleton",
        "minecraft:zombie_head=minecraft:zombie",
        "minecraft:creeper_head=minecraft:creeper",
        "minecraft:piglin_head=minecraft:piglin"
    );
}
