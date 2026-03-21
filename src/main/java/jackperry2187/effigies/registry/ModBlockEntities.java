package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.block.entity.AntiPikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
//? if fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
//?} else {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
*///?}

public final class ModBlockEntities {
    private ModBlockEntities() {
    }

    //? if fabric {
    private static BlockEntityType<PikeBlockEntity> PIKE;
    private static BlockEntityType<PikeHeadBlockEntity> PIKE_HEAD;
    private static BlockEntityType<AntiPikeBlockEntity> ANTI_PIKE;

    public static void register() {
        PIKE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Effigies.id("pike"),
            FabricBlockEntityTypeBuilder.create(PikeBlockEntity::new,
                ModBlocks.woodenPike(),
                ModBlocks.stonePike(),
                ModBlocks.copperPike(),
                ModBlocks.ironPike(),
                ModBlocks.goldenPike(),
                ModBlocks.diamondPike(),
                ModBlocks.netheritePike()
            ).build()
        );
        PIKE_HEAD = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Effigies.id("pike_head"),
            FabricBlockEntityTypeBuilder.create(PikeHeadBlockEntity::new,
                ModBlocks.pikeHead()
            ).build()
        );
        ANTI_PIKE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Effigies.id("anti_pike"),
            FabricBlockEntityTypeBuilder.create(AntiPikeBlockEntity::new,
                ModBlocks.antiPike()
            ).build()
        );
    }

    public static BlockEntityType<PikeBlockEntity> pike() {
        return PIKE;
    }

    public static BlockEntityType<PikeHeadBlockEntity> pikeHead() {
        return PIKE_HEAD;
    }

    public static BlockEntityType<AntiPikeBlockEntity> antiPike() {
        return ANTI_PIKE;
    }
    //?} else {
    /*public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Effigies.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PikeBlockEntity>> PIKE =
        BLOCK_ENTITIES.register(
            "pike",
            () -> new BlockEntityType<>(PikeBlockEntity::new,
                ModBlocks.woodenPike(),
                ModBlocks.stonePike(),
                ModBlocks.copperPike(),
                ModBlocks.ironPike(),
                ModBlocks.goldenPike(),
                ModBlocks.diamondPike(),
                ModBlocks.netheritePike()
            )
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PikeHeadBlockEntity>> PIKE_HEAD =
        BLOCK_ENTITIES.register(
            "pike_head",
            () -> new BlockEntityType<>(PikeHeadBlockEntity::new,
                ModBlocks.pikeHead()
            )
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AntiPikeBlockEntity>> ANTI_PIKE =
        BLOCK_ENTITIES.register(
            "anti_pike",
            () -> new BlockEntityType<>(AntiPikeBlockEntity::new,
                ModBlocks.antiPike()
            )
        );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    public static BlockEntityType<PikeBlockEntity> pike() {
        return PIKE.get();
    }

    public static BlockEntityType<PikeHeadBlockEntity> pikeHead() {
        return PIKE_HEAD.get();
    }

    public static BlockEntityType<AntiPikeBlockEntity> antiPike() {
        return ANTI_PIKE.get();
    }
    *///?}
}
