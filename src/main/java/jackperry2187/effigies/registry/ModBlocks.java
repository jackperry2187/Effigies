package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.block.AntiPikeBlock;
import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.PikeHeadBlock;
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
    private static Block COPPER_PIKE;
    private static Block IRON_PIKE;
    private static Block GOLDEN_PIKE;
    private static Block DIAMOND_PIKE;
    private static Block NETHERITE_PIKE;
    private static Block PIKE_HEAD;
    private static Block ANTI_PIKE;

    public static void register() {
        Identifier woodenPikeId = Effigies.id("wooden_pike");
        WOODEN_PIKE = Registry.register(Registries.BLOCK, woodenPikeId, new PikeBlock(PikeTier.WOODEN, woodenPikeId));

        Identifier stonePikeId = Effigies.id("stone_pike");
        STONE_PIKE = Registry.register(Registries.BLOCK, stonePikeId, new PikeBlock(PikeTier.STONE, stonePikeId));

        Identifier copperPikeId = Effigies.id("copper_pike");
        COPPER_PIKE = Registry.register(Registries.BLOCK, copperPikeId, new PikeBlock(PikeTier.COPPER, copperPikeId));

        Identifier ironPikeId = Effigies.id("iron_pike");
        IRON_PIKE = Registry.register(Registries.BLOCK, ironPikeId, new PikeBlock(PikeTier.IRON, ironPikeId));

        Identifier goldenPikeId = Effigies.id("golden_pike");
        GOLDEN_PIKE = Registry.register(Registries.BLOCK, goldenPikeId, new PikeBlock(PikeTier.GOLDEN, goldenPikeId));

        Identifier diamondPikeId = Effigies.id("diamond_pike");
        DIAMOND_PIKE = Registry.register(Registries.BLOCK, diamondPikeId, new PikeBlock(PikeTier.DIAMOND, diamondPikeId));

        Identifier netheritePikeId = Effigies.id("netherite_pike");
        NETHERITE_PIKE = Registry.register(Registries.BLOCK, netheritePikeId, new PikeBlock(PikeTier.NETHERITE, netheritePikeId));

        Identifier pikeHeadId = Effigies.id("pike_head");
        PIKE_HEAD = Registry.register(Registries.BLOCK, pikeHeadId, new PikeHeadBlock(pikeHeadId));

        Identifier antiPikeId = Effigies.id("anti_pike");
        ANTI_PIKE = Registry.register(Registries.BLOCK, antiPikeId, new AntiPikeBlock(antiPikeId));
    }

    public static Block woodenPike() {
        return WOODEN_PIKE;
    }

    public static Block stonePike() {
        return STONE_PIKE;
    }

    public static Block copperPike() {
        return COPPER_PIKE;
    }

    public static Block ironPike() {
        return IRON_PIKE;
    }

    public static Block goldenPike() {
        return GOLDEN_PIKE;
    }

    public static Block diamondPike() {
        return DIAMOND_PIKE;
    }

    public static Block netheritePike() {
        return NETHERITE_PIKE;
    }

    public static Block pikeHead() {
        return PIKE_HEAD;
    }

    public static Block antiPike() {
        return ANTI_PIKE;
    }
    //?} else {
    /*public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(Effigies.MOD_ID);

    public static final DeferredBlock<PikeBlock> WOODEN_PIKE =
        BLOCKS.register("wooden_pike", () -> new PikeBlock(PikeTier.WOODEN, PikeTier.WOODEN.blockProperties(Effigies.MOD_ID, "wooden_pike")));
    public static final DeferredBlock<PikeBlock> STONE_PIKE =
        BLOCKS.register("stone_pike", () -> new PikeBlock(PikeTier.STONE, PikeTier.STONE.blockProperties(Effigies.MOD_ID, "stone_pike")));
    public static final DeferredBlock<PikeBlock> COPPER_PIKE =
        BLOCKS.register("copper_pike", () -> new PikeBlock(PikeTier.COPPER, PikeTier.COPPER.blockProperties(Effigies.MOD_ID, "copper_pike")));
    public static final DeferredBlock<PikeBlock> IRON_PIKE =
        BLOCKS.register("iron_pike", () -> new PikeBlock(PikeTier.IRON, PikeTier.IRON.blockProperties(Effigies.MOD_ID, "iron_pike")));
    public static final DeferredBlock<PikeBlock> GOLDEN_PIKE =
        BLOCKS.register("golden_pike", () -> new PikeBlock(PikeTier.GOLDEN, PikeTier.GOLDEN.blockProperties(Effigies.MOD_ID, "golden_pike")));
    public static final DeferredBlock<PikeBlock> DIAMOND_PIKE =
        BLOCKS.register("diamond_pike", () -> new PikeBlock(PikeTier.DIAMOND, PikeTier.DIAMOND.blockProperties(Effigies.MOD_ID, "diamond_pike")));
    public static final DeferredBlock<PikeBlock> NETHERITE_PIKE =
        BLOCKS.register("netherite_pike", () -> new PikeBlock(PikeTier.NETHERITE, PikeTier.NETHERITE.blockProperties(Effigies.MOD_ID, "netherite_pike")));
    public static final DeferredBlock<PikeHeadBlock> PIKE_HEAD =
        BLOCKS.register("pike_head", () -> new PikeHeadBlock(PikeHeadBlock.neoForgeProperties()));
    public static final DeferredBlock<AntiPikeBlock> ANTI_PIKE =
        BLOCKS.register("anti_pike", () -> new AntiPikeBlock(AntiPikeBlock.neoForgeProperties()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static Block woodenPike() {
        return WOODEN_PIKE.get();
    }

    public static Block stonePike() {
        return STONE_PIKE.get();
    }

    public static Block copperPike() {
        return COPPER_PIKE.get();
    }

    public static Block ironPike() {
        return IRON_PIKE.get();
    }

    public static Block goldenPike() {
        return GOLDEN_PIKE.get();
    }

    public static Block diamondPike() {
        return DIAMOND_PIKE.get();
    }

    public static Block netheritePike() {
        return NETHERITE_PIKE.get();
    }

    public static Block pikeHead() {
        return PIKE_HEAD.get();
    }

    public static Block antiPike() {
        return ANTI_PIKE.get();
    }
    *///?}
}
