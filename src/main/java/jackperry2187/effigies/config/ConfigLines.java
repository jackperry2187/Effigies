package jackperry2187.effigies.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jackperry2187.effigies.config.DefaultSettings.*;

/**
 * Generates the config file template content.
 */
public final class ConfigLines {
    private ConfigLines() {}

    /**
     * Returns the list of lines that form the config file template.
     * Includes comments explaining each option and default values.
     */
    public static List<String> getBaseConfigLines() {
        List<String> lines = new ArrayList<>(Arrays.asList(
            "### Effigies Configuration",
            "### This file is auto-generated.",
            "# Config version - do not modify manually",
            "configVersion=" + CONFIG_VERSION,
            "",
            "### Pike Radius Values",
            "# Radius values are measured in chunks (16 blocks per chunk).",
            "# A radius of -1 disables the pike tier (it will never block spawns).",
            "# A radius of 0 means the pike only affects the chunk it's placed in.",
            "# A radius of 1 means the pike affects its chunk plus 1 chunk in each direction (3x3 area).",
            
            "",
            "# Wooden Pike - Basic tier, affects only its own chunk",
            "wooden_pike_radius=" + WOODEN_PIKE_RADIUS,
            "",
            "# Stone Pike - Affects a small area",
            "stone_pike_radius=" + STONE_PIKE_RADIUS,
            "",
            "# Copper Pike - Slightly larger range",
            "copper_pike_radius=" + COPPER_PIKE_RADIUS,
            "",
            "# Iron Pike - Medium range",
            "iron_pike_radius=" + IRON_PIKE_RADIUS,
            "",
            "# Golden Pike - Large range",
            "golden_pike_radius=" + GOLDEN_PIKE_RADIUS,
            "",
            "# Diamond Pike - Very large range",
            "diamond_pike_radius=" + DIAMOND_PIKE_RADIUS,
            "",
            "# Netherite Pike - Maximum range",
            "netherite_pike_radius=" + NETHERITE_PIKE_RADIUS,
            "",
            "### Block to Entity Mappings",
            "# Format: block_id=entity_id",
            "# These define which blocks can be placed on pikes and which mobs they prevent from spawning.",
            "# Uses namespace:id format - works with vanilla and modded blocks/entities.",
            "# Example: minecraft:sand=minecraft:zombie (sand block prevents zombie spawns)",
            "# Example: ecologics:coconut_planks=ecologics:penguin (modded blocks/entities work too)",
            "# Check the Minecraft console for errors/warnings if mappings are not working."
        ));
        lines.addAll(DEFAULT_BLOCK_ENTITY_MAPPINGS);
        return lines;
    }
}
