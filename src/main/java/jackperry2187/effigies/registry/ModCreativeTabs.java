package jackperry2187.effigies.registry;

import jackperry2187.effigies.Effigies;
//? if mc12011 && fabric {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?} else if mc261 && fabric {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
*///?}

//? if fabric {
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
    private static CreativeModeTab EFFIGIES_TAB;

    public static void register() {
        Identifier tabId = Effigies.id("effigies");
        EFFIGIES_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            tabId,
            //? if mc12011 && fabric {
            FabricItemGroup.builder()
            //?} else if mc261 && fabric {
            /*FabricCreativeModeTab.builder()
            *///?}
                .icon(() -> new ItemStack(ModBlocks.woodenPike()))
                .title(Component.translatable("itemGroup.effigies"))
                .displayItems((parameters, output) -> {
                    output.accept(ModBlocks.woodenPike());
                    output.accept(ModBlocks.stonePike());
                    output.accept(ModBlocks.copperPike());
                    output.accept(ModBlocks.ironPike());
                    output.accept(ModBlocks.goldenPike());
                    output.accept(ModBlocks.diamondPike());
                    output.accept(ModBlocks.netheritePike());
                    output.accept(ModItems.antiSpear());
                    output.accept(ModBlocks.antiPike());
                    output.accept(ModItems.grimoire());
                })
                .build()
        );
    }

    public static CreativeModeTab effigiesTab() {
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
                output.accept(ModBlocks.copperPike());
                output.accept(ModBlocks.ironPike());
                output.accept(ModBlocks.goldenPike());
                output.accept(ModBlocks.diamondPike());
                output.accept(ModBlocks.netheritePike());
                output.accept(ModItems.antiSpear());
                output.accept(ModBlocks.antiPike());
                output.accept(ModItems.grimoire());
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
