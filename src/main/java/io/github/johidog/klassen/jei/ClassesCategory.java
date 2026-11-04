package io.github.johidog.klassen.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClassesCategory implements IRecipeCategory<ClassInfo> {

    public static final ResourceLocation UID = new ResourceLocation("klassen", "classes");
    public static final RecipeType<ClassInfo> TYPE = new RecipeType<>(UID, ClassInfo.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ClassesCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 70);
        this.icon = guiHelper.createDrawableIngredient(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public RecipeType<ClassInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.klassen.classes.title");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ClassInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 24).addItemStack(recipe.icon());
    }

    @Override
    public void draw(ClassInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.title(), 28, 8, 0xFFAA00, false);
        int y = 22;
        for (Component line : recipe.lines()) {
            guiGraphics.drawString(Minecraft.getInstance().font, line, 28, y, 0xFFFFFF, false);
            y += 10;
        }
    }
}
