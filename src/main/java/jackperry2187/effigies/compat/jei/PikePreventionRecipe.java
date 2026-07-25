package jackperry2187.effigies.compat.jei;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record PikePreventionRecipe(
    ItemStack headItem,
    String entityTypeId,
    Identifier recipeId
) {}
