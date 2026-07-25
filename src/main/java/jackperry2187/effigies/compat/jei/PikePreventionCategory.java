package jackperry2187.effigies.compat.jei;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.client.MobRenderHelper;
import jackperry2187.effigies.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

//? if mc12011 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class PikePreventionCategory implements IRecipeCategory<PikePreventionRecipe> {

    public static final IRecipeType<PikePreventionRecipe> RECIPE_TYPE =
        IRecipeType.create(Effigies.MOD_ID, "pike_prevention", PikePreventionRecipe.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;

    private static final int PIKE_SLOT_X = 4;
    private static final int PIKE_SLOT_Y = 17;
    private static final int HEAD_SLOT_X = 26;
    private static final int HEAD_SLOT_Y = 17;
    private static final int ARROW_X = 52;
    private static final int ARROW_Y = 19;
    private static final int MOB_CENTER_X = 118;
    private static final int MOB_CENTER_Y = 25;
    private static final int MOB_RENDER_SIZE = 40;

    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public PikePreventionCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(ModItems.getAllPikeItems().getFirst());
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public IRecipeType<PikePreventionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("effigies.jei.pike_prevention");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PikePreventionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(PIKE_SLOT_X, PIKE_SLOT_Y)
            .addItemStacks(ModItems.getAllPikeItems().stream().map(ItemStack::new).toList());

        builder.addInputSlot(HEAD_SLOT_X, HEAD_SLOT_Y)
            .add(recipe.headItem());
    }

    //? if mc12011 {
    @Override
    public void draw(PikePreventionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        MobRenderHelper.renderEntity(guiGraphics, recipe.entityTypeId(), MOB_CENTER_X, MOB_CENTER_Y, MOB_RENDER_SIZE);
    }
    //?} else {
    /*@Override
    public void draw(PikePreventionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        MobRenderHelper.renderEntity(guiGraphics, recipe.entityTypeId(), MOB_CENTER_X, MOB_CENTER_Y, MOB_RENDER_SIZE);
    }
    *///?}

    @Override
    public void getTooltip(ITooltipBuilder tooltip, PikePreventionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isMouseOverMob(mouseX, mouseY)) {
            Identifier id = Identifier.tryParse(recipe.entityTypeId());
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
                tooltip.add(type.getDescription());
            }
        }
    }

    private static boolean isMouseOverMob(double mouseX, double mouseY) {
        int halfSize = MOB_RENDER_SIZE / 2;
        return mouseX >= MOB_CENTER_X - halfSize && mouseX <= MOB_CENTER_X + halfSize
            && mouseY >= MOB_CENTER_Y - halfSize && mouseY <= MOB_CENTER_Y + halfSize;
    }

    @Override
    public Identifier getIdentifier(PikePreventionRecipe recipe) {
        return recipe.recipeId();
    }
}
