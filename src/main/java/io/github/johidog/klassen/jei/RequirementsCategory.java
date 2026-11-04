package io.github.johidog.klassen.jei;

import mezz.jei.api.constants.VanillaTypes;
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

import java.util.List;

public class RequirementsCategory implements IRecipeCategory<RequirementInfo> {

    public static final ResourceLocation UID = new ResourceLocation("klassen", "requirements");
    public static final RecipeType<RequirementInfo> TYPE = new RecipeType<>(UID, RequirementInfo.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RequirementsCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.NETHER_STAR));
    }

    @Override
    public RecipeType<RequirementInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.klassen.requirements.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RequirementInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 22)
            .addItemStack(recipe.stack());
    }

    @Override
    public void draw(RequirementInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font,
            Component.translatable("jei.klassen.requires"), 28, 6, 0xFFAA00, false);
        guiGraphics.drawString(Minecraft.getInstance().font,
            Component.translatable("jei.klassen.requires.hint"), 28, 16, 0x999999, false);
        int y = 34;
        for (Component line : recipe.lines()) {
            guiGraphics.drawString(Minecraft.getInstance().font, line, 28, y, 0xFFFFFF, false);
            y += 10;
        }
    }
}
