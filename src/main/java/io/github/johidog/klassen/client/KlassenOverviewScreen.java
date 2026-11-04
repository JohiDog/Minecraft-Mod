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

import java.util.ArrayList;
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

        int y = 28;
        for (OriginLayer layer : layers) {
            if (layer.isHidden()) continue;
            graphics.fill(16, y - 2, width - 16, y + 10, 0x40303030);
            graphics.drawString(font, layer.getName(), 20, y, 0x55FFFF, false);
            y += 14;
            for (ResourceLocation originId : layer.getOrigins()) {
                Origin origin;
                try {
                    origin = OriginRegistry.get(originId);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                Origin selected = current.get(layer);
                boolean isSelected = selected != null && selected.equals(origin);
                int color = isSelected ? 0x55FF55 : 0xDDDDDD;

                graphics.renderItem(origin.getDisplayItem(), 22, y);
                Component name = Component.translatable("screen.klassen.overview.entry", origin.getName(), origin.getImpact());
                graphics.drawString(font, name, 44, y + 4, color, false);

                List<String> next = upgradeTargets(origin);
                if (!next.isEmpty()) {
                    int nx = 44 + font.width(name) + 8;
                    graphics.drawString(font, Component.literal("→ " + String.join(" / ", next)), nx, y + 4, 0xFFAA00, false);
                }
                if (isSelected) {
                    graphics.drawString(font, Component.literal("✔"), 20 + 190, y + 4, 0x55FF55, false);
                }
                y += 17;
            }
            y += 8;
            if (y > height - 40) break;
        }
    }

    private static List<String> upgradeTargets(Origin origin) {
        List<String> out = new ArrayList<>();
        JsonObject json = origin.toJson();
        if (json.has("upgrades") && json.get("upgrades").isJsonArray()) {
            JsonArray upgrades = json.getAsJsonArray("upgrades");
            for (JsonElement el : upgrades) {
                JsonObject up = el.getAsJsonObject();
                ResourceLocation targetId = new ResourceLocation(up.get("origin").getAsString());
                try {
                    Origin target = OriginRegistry.get(targetId);
                    out.add(target.getName().getString());
                } catch (IllegalArgumentException e) {
                    out.add(targetId.toString());
                }
            }
        }
        return out;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
