package jackperry2187.effigies;

//? if fabric {
import net.minecraft.block.AbstractBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.world.level.block.SoundType;
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

    public int chunkRadius() {
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
    /*public BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of()
            .strength(hardness, resistance)
            .sound(soundType)
            .noOcclusion();
    }
    *///?}
}
