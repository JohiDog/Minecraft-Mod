package io.github.johidog.klassen.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.johidog.klassen.jei.JeiDataLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Liest die eigenen Layer/Origin-JSONs vom Classpath (funktioniert im Jar und in Dev),
 * damit die UI unabhaengig von der Origins-API-Version ist.
 */
public final class KlassenData {

    public record UpgradeInfo(String advancement, String targetOrigin) {
    }

    public record OriginInfo(ResourceLocation id, String nameKey, String descKey, int impact, String iconItem, List<UpgradeInfo> upgrades) {
    }

    public record LayerInfo(ResourceLocation id, String nameKey, int order, boolean enabled, boolean hidden, List<ResourceLocation> origins) {
    }

    public record Model(List<LayerInfo> layers, List<OriginInfo> origins) {

        public OriginInfo origin(ResourceLocation id) {
            for (OriginInfo o : origins) {
                if (o.id().equals(id)) return o;
            }
            return null;
        }

        public LayerInfo layer(ResourceLocation id) {
            for (LayerInfo l : layers) {
                if (l.id().equals(id)) return l;
            }
            return null;
        }
    }

    private KlassenData() {
    }

    public static Model load() {
        List<LayerInfo> layers = new ArrayList<>();
        List<OriginInfo> origins = new ArrayList<>();
        for (String path : JeiDataLoader.listPublic("data/klassen/origins")) {
            try {
                JsonObject json = readClasspathJson(path);
                ResourceLocation id = new ResourceLocation("klassen", path.substring("data/klassen/origins/".length(), path.length() - 5).replace('/', ':'));
                List<UpgradeInfo> ups = new ArrayList<>();
                if (json.has("upgrades") && json.get("upgrades").isJsonArray()) {
                    for (JsonElement el : json.getAsJsonArray("upgrades")) {
                        JsonObject u = el.getAsJsonObject();
                        ups.add(new UpgradeInfo(u.get("condition").getAsString(), u.get("origin").getAsString()));
                    }
                }
                origins.add(new OriginInfo(
                    id,
                    json.get("name").getAsJsonObject().get("translate").getAsString(),
                    json.get("description").getAsJsonObject().get("translate").getAsString(),
                    json.has("impact") ? json.get("impact").getAsInt() : 0,
                    json.getAsJsonObject("icon").get("item").getAsString(),
                    ups));
            } catch (Exception ignored) {
            }
        }
        for (String path : JeiDataLoader.listPublic("data/klassen/origin_layers")) {
            try {
                JsonObject json = readClasspathJson(path);
                ResourceLocation id = new ResourceLocation("klassen", path.substring("data/klassen/origin_layers/".length(), path.length() - 5));
                List<ResourceLocation> originIds = new ArrayList<>();
                for (JsonElement el : json.getAsJsonArray("origins")) {
                    if (el.isJsonPrimitive()) {
                        originIds.add(new ResourceLocation(el.getAsString()));
                    } else {
                        for (JsonElement o : el.getAsJsonObject().getAsJsonArray("origins")) {
                            originIds.add(new ResourceLocation(o.getAsString()));
                        }
                    }
                }
                layers.add(new LayerInfo(
                    id,
                    json.get("name").getAsJsonObject().get("translate").getAsString(),
                    json.has("order") ? json.get("order").getAsInt() : 999,
                    json.has("enabled") ? json.get("enabled").getAsBoolean() : true,
                    json.has("hidden") && json.get("hidden").getAsBoolean(),
                    originIds));
            } catch (Exception ignored) {
            }
        }
        layers.sort(Comparator.comparingInt(LayerInfo::order));
        return new Model(layers, origins);
    }

    private static JsonObject readClasspathJson(String path) throws Exception {
        try (var in = KlassenData.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("nicht gefunden: " + path);
            return com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
