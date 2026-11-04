package io.github.johidog.klassen.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class KlassenJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation("klassen", "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new RequirementsCategory(registration.getJeiHelpers().getGuiHelper()),
            new ClassesCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RequirementsCategory.TYPE, RequirementsData.load());
        registration.addRecipes(ClassesCategory.TYPE, ClassesData.load());
    }
}
