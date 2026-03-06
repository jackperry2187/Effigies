package jackperry2187.effigies.compat.jei;

//? if fabric {
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
*///?}

public record PikePreventionRecipe(
    ItemStack headItem,
    String entityTypeId,
    Identifier recipeId
) {}
