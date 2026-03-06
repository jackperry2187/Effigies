package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.item.PikeItem;

import java.util.List;

//? if fabric {
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.world.item.Item;
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
            new PikeItem(ModBlocks.woodenPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, woodenPikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.WOODEN));

        Identifier stonePikeId = Effigies.id("stone_pike");
        STONE_PIKE = Registry.register(Registries.ITEM, stonePikeId,
            new PikeItem(ModBlocks.stonePike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, stonePikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.STONE));

        Identifier copperPikeId = Effigies.id("copper_pike");
        COPPER_PIKE = Registry.register(Registries.ITEM, copperPikeId,
            new PikeItem(ModBlocks.copperPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, copperPikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.COPPER));

        Identifier ironPikeId = Effigies.id("iron_pike");
        IRON_PIKE = Registry.register(Registries.ITEM, ironPikeId,
            new PikeItem(ModBlocks.ironPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, ironPikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.IRON));

        Identifier goldenPikeId = Effigies.id("golden_pike");
        GOLDEN_PIKE = Registry.register(Registries.ITEM, goldenPikeId,
            new PikeItem(ModBlocks.goldenPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, goldenPikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.GOLDEN));

        Identifier diamondPikeId = Effigies.id("diamond_pike");
        DIAMOND_PIKE = Registry.register(Registries.ITEM, diamondPikeId,
            new PikeItem(ModBlocks.diamondPike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, diamondPikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.DIAMOND));

        Identifier netheritePikeId = Effigies.id("netherite_pike");
        NETHERITE_PIKE = Registry.register(Registries.ITEM, netheritePikeId,
            new PikeItem(ModBlocks.netheritePike(), new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, netheritePikeId))
                .useBlockPrefixedTranslationKey(), PikeTier.NETHERITE));
    }

    public static List<Item> getAllPikeItems() {
        return List.of(
            WOODEN_PIKE, STONE_PIKE, COPPER_PIKE, IRON_PIKE,
            GOLDEN_PIKE, DIAMOND_PIKE, NETHERITE_PIKE
        );
    }
    //?} else {
    /*public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Effigies.MOD_ID);

    public static final DeferredItem<PikeItem> WOODEN_PIKE =
        ITEMS.registerItem("wooden_pike", props -> new PikeItem(ModBlocks.woodenPike(), props.useBlockDescriptionPrefix(), PikeTier.WOODEN));
    public static final DeferredItem<PikeItem> STONE_PIKE =
        ITEMS.registerItem("stone_pike", props -> new PikeItem(ModBlocks.stonePike(), props.useBlockDescriptionPrefix(), PikeTier.STONE));
    public static final DeferredItem<PikeItem> COPPER_PIKE =
        ITEMS.registerItem("copper_pike", props -> new PikeItem(ModBlocks.copperPike(), props.useBlockDescriptionPrefix(), PikeTier.COPPER));
    public static final DeferredItem<PikeItem> IRON_PIKE =
        ITEMS.registerItem("iron_pike", props -> new PikeItem(ModBlocks.ironPike(), props.useBlockDescriptionPrefix(), PikeTier.IRON));
    public static final DeferredItem<PikeItem> GOLDEN_PIKE =
        ITEMS.registerItem("golden_pike", props -> new PikeItem(ModBlocks.goldenPike(), props.useBlockDescriptionPrefix(), PikeTier.GOLDEN));
    public static final DeferredItem<PikeItem> DIAMOND_PIKE =
        ITEMS.registerItem("diamond_pike", props -> new PikeItem(ModBlocks.diamondPike(), props.useBlockDescriptionPrefix(), PikeTier.DIAMOND));
    public static final DeferredItem<PikeItem> NETHERITE_PIKE =
        ITEMS.registerItem("netherite_pike", props -> new PikeItem(ModBlocks.netheritePike(), props.useBlockDescriptionPrefix(), PikeTier.NETHERITE));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static List<Item> getAllPikeItems() {
        return List.of(
            WOODEN_PIKE.get(), STONE_PIKE.get(), COPPER_PIKE.get(), IRON_PIKE.get(),
            GOLDEN_PIKE.get(), DIAMOND_PIKE.get(), NETHERITE_PIKE.get()
        );
    }
    *///?}
}
