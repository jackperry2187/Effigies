package jackperry2187.effigies;

import jackperry2187.effigies.config.ConfigSettings;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum PikeTier {
    WOODEN(
        0,
        SoundType.WOOD,
        1.0F,
        1.0F
    ),
    STONE(
        1,
        SoundType.STONE,
        1.5F,
        6.0F
    ),
    COPPER(
        2,
        SoundType.COPPER,
        3.0F,
        6.0F
    ),
    IRON(
        4,
        SoundType.METAL,
        5.0F,
        6.0F
    ),
    GOLDEN(
        6,
        SoundType.METAL,
        3.0F,
        6.0F
    ),
    DIAMOND(
        8,
        SoundType.METAL,
        5.0F,
        6.0F
    ),
    NETHERITE(
        16,
        SoundType.NETHERITE_BLOCK,
        50.0F,
        1200.0F
    );

    private final int chunkRadius;
    private final SoundType soundType;
    private final float hardness;
    private final float resistance;

    PikeTier(
        int chunkRadius,
        SoundType soundType,
        float hardness,
        float resistance
    ) {
        this.chunkRadius = chunkRadius;
        this.soundType = soundType;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    /**
     * Returns the chunk radius for this pike tier.
     * If config is initialized, returns the configured value.
     * Otherwise, returns the hardcoded default.
     */
    public int chunkRadius() {
        if (ConfigSettings.isInitialized()) {
            return ConfigSettings.getPikeRadius(this);
        }
        return chunkRadius; // fallback to hardcoded default
    }

    /**
     * Returns the hardcoded default chunk radius.
     * Used internally, config values take precedence via chunkRadius().
     */
    public int defaultChunkRadius() {
        return chunkRadius;
    }

    //? if fabric {
    public BlockBehaviour.Properties blockProperties(Identifier id) {
        return BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, id))
            .strength(hardness, resistance)
            .sound(soundType)
            .noOcclusion();
    }
    //?} else {
    /*public BlockBehaviour.Properties blockProperties(String modId, String blockName) {
        return BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(modId, blockName)))
            .strength(hardness, resistance)
            .sound(soundType)
            .noOcclusion();
    }
    *///?}
}
