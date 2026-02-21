package jackperry2187.effigies;

import jackperry2187.effigies.config.ConfigSettings;

//? if fabric {
import net.minecraft.block.AbstractBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
*///?}

public enum PikeTier {
    WOODEN(
        0,
        //? if fabric {
        BlockSoundGroup.WOOD,
        //?} else {
        /*SoundType.WOOD,
        *///?}
        1.0F,
        1.0F
    ),
    STONE(
        1,
        //? if fabric {
        BlockSoundGroup.STONE,
        //?} else {
        /*SoundType.STONE,
        *///?}
        1.5F,
        6.0F
    ),
    COPPER(
        2,
        //? if fabric {
        BlockSoundGroup.COPPER,
        //?} else {
        /*SoundType.COPPER,
        *///?}
        3.0F,
        6.0F
    ),
    IRON(
        4,
        //? if fabric {
        BlockSoundGroup.METAL,
        //?} else {
        /*SoundType.METAL,
        *///?}
        5.0F,
        6.0F
    ),
    GOLDEN(
        6,
        //? if fabric {
        BlockSoundGroup.METAL,
        //?} else {
        /*SoundType.METAL,
        *///?}
        3.0F,
        6.0F
    ),
    DIAMOND(
        8,
        //? if fabric {
        BlockSoundGroup.METAL,
        //?} else {
        /*SoundType.METAL,
        *///?}
        5.0F,
        6.0F
    ),
    NETHERITE(
        16,
        //? if fabric {
        BlockSoundGroup.NETHERITE,
        //?} else {
        /*SoundType.NETHERITE_BLOCK,
        *///?}
        50.0F,
        1200.0F
    );

    private final int chunkRadius;
    //? if fabric {
    private final BlockSoundGroup soundType;
    //?} else {
    /*private final SoundType soundType;
    *///?}
    private final float hardness;
    private final float resistance;

    PikeTier(
        int chunkRadius,
        //? if fabric {
        BlockSoundGroup soundType,
        //?} else {
        /*SoundType soundType,
        *///?}
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
    public AbstractBlock.Settings blockProperties(Identifier id) {
        return AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(hardness, resistance)
            .sounds(soundType)
            .nonOpaque();
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
