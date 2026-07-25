package jackperry2187.effigies.compat.jei;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class EffigiesJeiPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        Effigies.LOGGER.info("[JEI] Effigies JEI plugin discovered (getPluginUid called)");
        return Effigies.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        Effigies.LOGGER.info("[JEI] Registering Pike Prevention recipe category");
        registration.addRecipeCategories(
            new PikePreventionCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<Item> pikes = ModItems.getAllPikeItems();
        Effigies.LOGGER.info("[JEI] Registering {} pike items as recipe catalysts", pikes.size());
        for (Item pike : pikes) {
            registration.addCraftingStation(PikePreventionCategory.RECIPE_TYPE, pike);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PikePreventionRecipe> recipes = buildRecipesFromConfig();
        Effigies.LOGGER.info("[JEI] Registering {} pike prevention recipes", recipes.size());
        registration.addRecipes(PikePreventionCategory.RECIPE_TYPE, recipes);
    }

    private static List<PikePreventionRecipe> buildRecipesFromConfig() {
        List<PikePreventionRecipe> recipes = new ArrayList<>();
        Map<String, String> mappings = ConfigSettings.getBlockToEntityMappings();

        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            String blockId = entry.getKey();
            String entityId = entry.getValue();

            Identifier blockIdentifier = Identifier.tryParse(blockId);
            if (blockIdentifier == null || !BuiltInRegistries.BLOCK.containsKey(blockIdentifier)) continue;

            Item headItem = BuiltInRegistries.BLOCK.getValue(blockIdentifier).asItem();

            ItemStack headStack = new ItemStack(headItem);
            if (headStack.isEmpty()) continue;

            Identifier recipeId = Effigies.id("pike_prevention/" + blockId.replace(":", "_"));
            recipes.add(new PikePreventionRecipe(headStack, entityId, recipeId));
        }

        return recipes;
    }
}
