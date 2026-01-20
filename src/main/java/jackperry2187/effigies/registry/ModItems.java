package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
//? if fabric {
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

public final class ModItems {
    private ModItems() {
    }

    //? if fabric {
    private static Item WOODEN_PIKE;
    private static Item STONE_PIKE;

    public static void register() {
        Identifier woodenPikeId = Effigies.id("wooden_pike");
        WOODEN_PIKE = Registry.register(Registries.ITEM, woodenPikeId,
            new BlockItem(ModBlocks.woodenPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, woodenPikeId))
                .useBlockPrefixedTranslationKey()));

        Identifier stonePikeId = Effigies.id("stone_pike");
        STONE_PIKE = Registry.register(Registries.ITEM, stonePikeId,
            new BlockItem(ModBlocks.stonePike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, stonePikeId))
                .useBlockPrefixedTranslationKey()));
    }
    //?} else {
    /*public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Effigies.MOD_ID);

    public static final DeferredItem<BlockItem> WOODEN_PIKE =
        ITEMS.registerSimpleBlockItem("wooden_pike", ModBlocks.WOODEN_PIKE);
    public static final DeferredItem<BlockItem> STONE_PIKE =
        ITEMS.registerSimpleBlockItem("stone_pike", ModBlocks.STONE_PIKE);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
    *///?}
}
