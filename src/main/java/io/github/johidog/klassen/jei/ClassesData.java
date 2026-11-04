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

public final class ClassesData {

    private ClassesData() {
    }

    public static List<ClassInfo> load() {
        List<ClassInfo> out = new ArrayList<>();
        for (JsonObject json : JeiDataLoader.loadJsonObjects("data/klassen/jei_classes")) {
            try {
                for (JsonElement el : json.getAsJsonArray("entries")) {
                    JsonObject e = el.getAsJsonObject();
                    ItemStack icon = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(e.get("icon").getAsString())));
                    Component title = Component.translatable(e.get("title").getAsString());
                    List<Component> lines = new ArrayList<>();
                    for (JsonElement line : e.getAsJsonArray("lines")) {
                        lines.add(Component.translatable(line.getAsString()));
                    }
                    out.add(new ClassInfo(icon, title, lines));
                }
            } catch (Exception ex) {
                KlassenMod.LOGGER.warn("JEI-Klasse konnte nicht geladen werden: {}", ex.getMessage());
            }
        }
        return out;
    }
}
