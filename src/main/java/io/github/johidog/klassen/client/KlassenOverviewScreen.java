package io.github.johidog.klassen.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class KlassenOverviewScreen extends Screen {

    private List<OriginLayer> layers;

    protected KlassenOverviewScreen() {
        super(Component.translatable("screen.klassen.overview.title"));
    }

    @Override
    protected void init() {
        layers = OriginLayers.getLayers().stream()
            .filter(OriginLayer::isEnabled)
            .sorted(Comparator.comparingInt(OriginLayer::getOrder))
            .toList();
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(width / 2 - 100, height - 28, 200, 20)
            .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);

        Minecraft client = Minecraft.getInstance();
        Map<OriginLayer, Origin> current = client.player != null
            ? Origin.get(client.player)
            : Map.of();

        int y = 26;
        for (OriginLayer layer : layers) {
            if (layer.isHidden()) continue;
            graphics.drawString(font, layer.getName(), 20, y, 0x55FFFF, false);
            y += 12;
            for (ResourceLocation originId : layer.getOrigins()) {
                Origin origin;
                try {
                    origin = OriginRegistry.get(originId);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                Origin selected = current.get(layer);
                boolean isSelected = selected != null && selected.equals(origin);
                int color = isSelected ? 0x55FF55 : 0xCCCCCC;
                graphics.renderItem(origin.getDisplayItem(), 22, y);
                graphics.drawString(font,
                    Component.translatable("screen.klassen.overview.entry", origin.getName(), origin.getImpact()),
                    44, y + 4, color, false);
                y += 16;
                JsonObject json = origin.toJson();
                JsonArray upgrades = json.has("upgrades") && json.get("upgrades").isJsonArray()
                    ? json.getAsJsonArray("upgrades") : null;
                if (upgrades != null) {
                    for (JsonElement el : upgrades) {
                        JsonObject up = el.getAsJsonObject();
                        ResourceLocation targetId = new ResourceLocation(up.get("origin").getAsString());
                        Origin target;
                        try {
                            target = OriginRegistry.get(targetId);
                        } catch (IllegalArgumentException e) {
                            target = null;
                        }
                        Component line = Component.translatable("screen.klassen.overview.upgrade",
                            target != null ? target.getName() : Component.literal(targetId.toString()),
                            up.get("condition").getAsString());
                        graphics.drawString(font, line, 48, y, 0xFFAA00, false);
                        y += 10;
                    }
                }
            }
            y += 6;
            if (y > height - 40) break;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
