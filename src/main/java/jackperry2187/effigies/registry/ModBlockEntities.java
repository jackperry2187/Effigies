package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
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
    }

    public static BlockEntityType<PikeBlockEntity> pike() {
        return PIKE;
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

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    public static BlockEntityType<PikeBlockEntity> pike() {
        return PIKE.get();
    }
    *///?}
}
