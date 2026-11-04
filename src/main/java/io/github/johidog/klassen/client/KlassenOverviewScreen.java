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

    private static final int PANEL_MARGIN = 24;
    private static final int ROW_HEIGHT = 20;
    private static final int HEADER_HEIGHT = 18;

    private List<OriginLayer> layers;
    private int scroll;
    private int contentHeight;
    private Origin hovered;

    protected KlassenOverviewScreen() {
        super(Component.translatable("screen.klassen.overview.title"));
    }

    @Override
    protected void init() {
        layers = OriginLayers.getLayers().stream()
            .filter(OriginLayer::isEnabled)
            .sorted(Comparator.comparingInt(OriginLayer::getOrder))
            .toList();
        contentHeight = 0;
        for (OriginLayer layer : layers) {
            if (layer.isHidden()) continue;
            contentHeight += HEADER_HEIGHT + layer.getOrigins().size() * ROW_HEIGHT + 8;
        }
        scroll = 0;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(width / 2 - 100, height - 26, 200, 20)
            .build());
    }

    private int maxScroll() {
        int view = height - 60;
        return Math.max(0, contentHeight - view);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll - (int) (verticalAmount * 24)));
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);

        int px = PANEL_MARGIN;
        int py = 34;
        int pw = width - PANEL_MARGIN * 2;
        int ph = height - 60;

        // Panel + Titel
        graphics.fill(px, 8, px + pw, 30, 0xC0101018);
        graphics.renderOutline(px, 8, pw, 22, 0xFF8A2BE2);
        graphics.drawCenteredString(font, title, width / 2, 14, 0xE0C0FF);
        graphics.fill(px, py, px + pw, py + ph, 0xA0141420);
        graphics.renderOutline(px, py, pw, ph, 0xFF444466);

        Minecraft client = Minecraft.getInstance();
        Map<OriginLayer, Origin> current = client.player != null ? Origin.get(client.player) : Map.of();

        graphics.enableScissor(px + 2, py + 2, px + pw - 2, py + ph - 2);
        hovered = null;
        int y = py + 6 - scroll;
        int layerIndex = 0;
        for (OriginLayer layer : layers) {
            if (layer.isHidden()) continue;
            int headerColor = new int[]{0xFF55FFFF, 0xFFFFAA00, 0xFF55FF55, 0xFFFF6688, 0xFF66AAFF}[layerIndex % 5];
            graphics.fill(px + 4, y, px + pw - 4, y + HEADER_HEIGHT - 2, 0x60222233);
            graphics.drawString(font, layer.getName(), px + 10, y + 5, headerColor, false);
            y += HEADER_HEIGHT;
            for (ResourceLocation originId : layer.getOrigins()) {
                Origin origin;
                try {
                    origin = OriginRegistry.get(originId);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                int rowY = y;
                Origin selected = current.get(layer);
                boolean isSelected = selected != null && selected.equals(origin);
                boolean hover = mouseX >= px + 4 && mouseX <= px + pw - 4 && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
                if (hover) hovered = origin;

                if (hover) graphics.fill(px + 4, rowY, px + pw - 4, rowY + ROW_HEIGHT, 0x40FFFFFF);
                if (isSelected) {
                    graphics.fill(px + 4, rowY, px + 8, rowY + ROW_HEIGHT, 0xFF55FF55);
                    graphics.drawString(font, Component.literal("✔"), px + pw - 18, rowY + 6, 0x55FF55, false);
                }

                // Icon-Slot
                graphics.fill(px + 10, rowY + 2, px + 28, rowY + 20, 0xFF0A0A12);
                graphics.renderOutline(px + 10, rowY + 2, 18, 18, isSelected ? 0xFF55FF55 : 0xFF666688);
                graphics.renderItem(origin.getDisplayItem(), px + 12, rowY + 4);

                Component name = Component.translatable("screen.klassen.overview.entry", origin.getName(), impactPips(origin.getImpact().getImpactValue()));
                graphics.drawString(font, name, px + 34, rowY + 6, isSelected ? 0x77FF77 : 0xF0F0FF, true);

                List<String> next = upgradeTargets(origin);
                if (!next.isEmpty()) {
                    int nx = px + 34 + font.width(name) + 10;
                    graphics.drawString(font, Component.literal("→ " + String.join(" / ", next)), nx, rowY + 6, 0xFFCC44, false);
                }
                y += ROW_HEIGHT;
            }
            y += 8;
            layerIndex++;
        }
        graphics.disableScissor();

        // Scrollbar
        if (maxScroll() > 0) {
            int trackX = px + pw - 6;
            graphics.fill(trackX, py + 2, trackX + 3, py + ph - 2, 0x40FFFFFF);
            int thumbH = Math.max(20, ph * ph / (contentHeight + ph));
            int thumbY = py + 2 + (ph - 4 - thumbH) * scroll / maxScroll();
            graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, 0xFFAA66FF);
        }

        // Tooltip (Description des Hover-Eintrags)
        if (hovered != null) {
            int tipY = py + ph - 22;
            graphics.fill(px + 4, tipY, px + pw - 4, tipY + 18, 0xE0000000);
            graphics.renderOutline(px + 4, tipY, pw - 8, 18, 0xFF8A2BE2);
            graphics.drawString(font, hovered.getDescription(), px + 10, tipY + 5, 0xCCCCFF, false);
        }
    }

    private static String impactPips(int impact) {
        return "●".repeat(Math.max(0, impact)) + "○".repeat(Math.max(0, 3 - impact));
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
                    out.add(OriginRegistry.get(targetId).getName().getString());
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
