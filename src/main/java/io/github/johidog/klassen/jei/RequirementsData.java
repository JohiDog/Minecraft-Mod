package io.github.johidog.klassen.jei;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.johidog.klassen.KlassenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequirementsData {

    private RequirementsData() {
    }

    public static List<RequirementInfo> load() {
        List<RequirementInfo> out = new ArrayList<>();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("jei_requirements", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("item").getAsString())));
                List<Component> lines = new ArrayList<>();
                if (json.has("lines")) {
                    for (JsonElement el : json.getAsJsonArray("lines")) {
                        lines.add(Component.translatable(el.getAsString()));
                    }
                }
                out.add(new RequirementInfo(stack, lines));
            } catch (Exception e) {
                KlassenMod.LOGGER.warn("JEI-Requirement {} konnte nicht geladen werden: {}", entry.getKey(), e.getMessage());
            }
        }
        return out;
    }
}
