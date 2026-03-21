package jackperry2187.effigies.item;

import jackperry2187.effigies.Effigies;

//? if fabric {
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
//?} else {
/*import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
*///?}

public class AntiSpearItem extends Item {
    //? if fabric {
    public static final ToolMaterial MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
        2500,
        0.0f,
        5.0f,
        15,
        TagKey.of(RegistryKeys.ITEM, Effigies.id("repairs_anti_spear"))
    );
    //?} else {
    /*public static final ToolMaterial MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
        2500,
        0.0f,
        5.0f,
        15,
        TagKey.create(Registries.ITEM, Effigies.id("repairs_anti_spear"))
    );
    *///?}

    //? if fabric {
    public AntiSpearItem(Item.Settings settings) {
        super(settings);
    }
    //?} else {
    /*public AntiSpearItem(Item.Properties properties) {
        super(properties);
    }
    *///?}
}
