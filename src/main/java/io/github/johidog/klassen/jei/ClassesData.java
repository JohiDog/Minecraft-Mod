package io.github.johidog.klassen.jei;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.johidog.klassen.KlassenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ClassesData {

    private ClassesData() {
    }

    public static List<ClassInfo> load() {
        List<ClassInfo> out = new ArrayList<>();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("jei_classes", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
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
                KlassenMod.LOGGER.warn("JEI-Klasse {} konnte nicht geladen werden: {}", entry.getKey(), ex.getMessage());
            }
        }
        return out;
    }
}
