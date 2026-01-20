package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.block.PikeBlock;
//? if fabric {
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

public final class ModBlocks {
    private ModBlocks() {
    }

    //? if fabric {
    private static Block WOODEN_PIKE;
    private static Block STONE_PIKE;

    public static void register() {
        Identifier woodenPikeId = Effigies.id("wooden_pike");
        WOODEN_PIKE = Registry.register(Registries.BLOCK, woodenPikeId, new PikeBlock(PikeTier.WOODEN, woodenPikeId));

        Identifier stonePikeId = Effigies.id("stone_pike");
        STONE_PIKE = Registry.register(Registries.BLOCK, stonePikeId, new PikeBlock(PikeTier.STONE, stonePikeId));
    }

    public static Block woodenPike() {
        return WOODEN_PIKE;
    }

    public static Block stonePike() {
        return STONE_PIKE;
    }
    //?} else {
    /*public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(Effigies.MOD_ID);

    public static final DeferredBlock<PikeBlock> WOODEN_PIKE =
        BLOCKS.register("wooden_pike", () -> new PikeBlock(PikeTier.WOODEN, PikeTier.WOODEN.blockProperties(Effigies.MOD_ID, "wooden_pike")));
    public static final DeferredBlock<PikeBlock> STONE_PIKE =
        BLOCKS.register("stone_pike", () -> new PikeBlock(PikeTier.STONE, PikeTier.STONE.blockProperties(Effigies.MOD_ID, "stone_pike")));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static Block woodenPike() {
        return WOODEN_PIKE.get();
    }

    public static Block stonePike() {
        return STONE_PIKE.get();
    }
    *///?}
}
