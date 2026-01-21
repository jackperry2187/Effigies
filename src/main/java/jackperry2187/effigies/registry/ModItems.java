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
    private static Item COPPER_PIKE;
    private static Item IRON_PIKE;
    private static Item GOLDEN_PIKE;
    private static Item DIAMOND_PIKE;
    private static Item NETHERITE_PIKE;

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

        Identifier copperPikeId = Effigies.id("copper_pike");
        COPPER_PIKE = Registry.register(Registries.ITEM, copperPikeId,
            new BlockItem(ModBlocks.copperPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, copperPikeId))
                .useBlockPrefixedTranslationKey()));

        Identifier ironPikeId = Effigies.id("iron_pike");
        IRON_PIKE = Registry.register(Registries.ITEM, ironPikeId,
            new BlockItem(ModBlocks.ironPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, ironPikeId))
                .useBlockPrefixedTranslationKey()));

        Identifier goldenPikeId = Effigies.id("golden_pike");
        GOLDEN_PIKE = Registry.register(Registries.ITEM, goldenPikeId,
            new BlockItem(ModBlocks.goldenPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, goldenPikeId))
                .useBlockPrefixedTranslationKey()));

        Identifier diamondPikeId = Effigies.id("diamond_pike");
        DIAMOND_PIKE = Registry.register(Registries.ITEM, diamondPikeId,
            new BlockItem(ModBlocks.diamondPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, diamondPikeId))
                .useBlockPrefixedTranslationKey()));

        Identifier netheritePikeId = Effigies.id("netherite_pike");
        NETHERITE_PIKE = Registry.register(Registries.ITEM, netheritePikeId,
            new BlockItem(ModBlocks.netheritePike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, netheritePikeId))
                .useBlockPrefixedTranslationKey()));
    }
    //?} else {
    /*public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Effigies.MOD_ID);

    public static final DeferredItem<BlockItem> WOODEN_PIKE =
        ITEMS.registerSimpleBlockItem("wooden_pike", ModBlocks.WOODEN_PIKE);
    public static final DeferredItem<BlockItem> STONE_PIKE =
        ITEMS.registerSimpleBlockItem("stone_pike", ModBlocks.STONE_PIKE);
    public static final DeferredItem<BlockItem> COPPER_PIKE =
        ITEMS.registerSimpleBlockItem("copper_pike", ModBlocks.COPPER_PIKE);
    public static final DeferredItem<BlockItem> IRON_PIKE =
        ITEMS.registerSimpleBlockItem("iron_pike", ModBlocks.IRON_PIKE);
    public static final DeferredItem<BlockItem> GOLDEN_PIKE =
        ITEMS.registerSimpleBlockItem("golden_pike", ModBlocks.GOLDEN_PIKE);
    public static final DeferredItem<BlockItem> DIAMOND_PIKE =
        ITEMS.registerSimpleBlockItem("diamond_pike", ModBlocks.DIAMOND_PIKE);
    public static final DeferredItem<BlockItem> NETHERITE_PIKE =
        ITEMS.registerSimpleBlockItem("netherite_pike", ModBlocks.NETHERITE_PIKE);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
    *///?}
}
