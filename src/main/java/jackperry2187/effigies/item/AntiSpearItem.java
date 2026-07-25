package jackperry2187.effigies.item;

import jackperry2187.effigies.Effigies;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class AntiSpearItem extends Item {
    public static final ToolMaterial MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
        2500,
        0.0f,
        5.0f,
        15,
        TagKey.create(Registries.ITEM, Effigies.id("repairs_anti_spear"))
    );

    public AntiSpearItem(Item.Properties properties) {
        super(properties);
    }
}
