package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
//? if fabric {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

public final class ModCreativeTabs {
    private ModCreativeTabs() {
    }

    //? if fabric {
    private static ItemGroup EFFIGIES_TAB;

    public static void register() {
        Identifier tabId = Effigies.id("effigies");
        EFFIGIES_TAB = Registry.register(
            Registries.ITEM_GROUP,
            tabId,
            FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModBlocks.woodenPike()))
                .displayName(Text.translatable("itemGroup.effigies"))
                .entries((context, entries) -> {
                    entries.add(ModBlocks.woodenPike());
                    entries.add(ModBlocks.stonePike());
                })
                .build()
        );
    }

    public static ItemGroup effigiesTab() {
        return EFFIGIES_TAB;
    }
    //?} else {
    /*public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Effigies.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EFFIGIES_TAB =
        CREATIVE_TABS.register("effigies", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.woodenPike()))
            .title(Component.translatable("itemGroup.effigies"))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.woodenPike());
                output.accept(ModBlocks.stonePike());
            })
            .build()
        );

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }

    public static CreativeModeTab effigiesTab() {
        return EFFIGIES_TAB.get();
    }
    *///?}
}
