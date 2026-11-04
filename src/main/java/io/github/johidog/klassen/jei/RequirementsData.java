package io.github.johidog.klassen.jei;

import io.github.johidog.klassen.KlassenMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class RequirementsData {

    private RequirementsData() {
    }

    public static List<RequirementInfo> load() {
        List<RequirementInfo> out = new ArrayList<>();
        for (JsonObject json : JeiDataLoader.loadJsonObjects("data/klassen/jei_requirements")) {
            try {
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("item").getAsString())));
                List<Component> lines = new ArrayList<>();
                if (json.has("lines")) {
                    for (JsonElement el : json.getAsJsonArray("lines")) {
                        lines.add(Component.translatable(el.getAsString()));
                    }
                }
                out.add(new RequirementInfo(stack, lines));
            } catch (Exception e) {
                KlassenMod.LOGGER.warn("JEI-Requirement konnte nicht geladen werden: {}", e.getMessage());
            }
        }
        return out;
    }
}
