package io.github.johidog.klassen.client;

import io.github.apace100.origins.origin.Origin;
import io.github.johidog.klassen.mixin.ClientAdvancementsAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KlassenOverviewScreen extends Screen {

    private static final int PANEL_MARGIN = 24;
    private static final int ROW_HEIGHT = 20;
    private static final int HEADER_HEIGHT = 18;
    private static final ResourceLocation RACE_LAYER = new ResourceLocation("klassen", "race");

    private KlassenData.Model model;
    private int scroll;
    private int contentHeight;
    private KlassenData.OriginInfo hovered;

    protected KlassenOverviewScreen() {
        super(Component.translatable("screen.klassen.overview.title"));
    }

    @Override
    protected void init() {
        model = KlassenData.load();
        contentHeight = 24;
        for (KlassenData.LayerInfo layer : model.layers()) {
            if (!layer.enabled() || layer.hidden()) continue;
            contentHeight += HEADER_HEIGHT + layer.origins().size() * ROW_HEIGHT + 8;
        }
        scroll = 0;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(width / 2 - 100, height - 26, 200, 20)
            .build());
    }

    private int maxScroll() {
        return Math.max(0, contentHeight - (height - 60));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll - (int) (amount * 24)));
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);

        int px = PANEL_MARGIN;
        int py = 34;
        int pw = width - PANEL_MARGIN * 2;
        int ph = height - 60;

        graphics.fill(px, 8, px + pw, 30, 0xC0101018);
        graphics.renderOutline(px, 8, pw, 22, 0xFF8A2BE2);
        graphics.drawCenteredString(font, title, width / 2, 14, 0xE0C0FF);
        graphics.fill(px, py, px + pw, py + ph, 0xA0141420);
        graphics.renderOutline(px, py, pw, ph, 0xFF444466);

        Minecraft client = Minecraft.getInstance();
        Map<?, Origin> currentRaw = client.player != null ? Origin.get(client.player) : Map.of();
        ResourceLocation currentRaceId = currentRaceId(currentRaw);

        drawAscensionProgress(graphics, client, currentRaceId, px, py, pw);

        graphics.enableScissor(px + 2, py + 2, px + pw - 2, py + ph - 2);
        hovered = null;
        int y = py + 26 - scroll;
        int layerIndex = 0;
        for (KlassenData.LayerInfo layer : model.layers()) {
            if (!layer.enabled() || layer.hidden()) continue;
            int headerColor = new int[]{0xFF55FFFF, 0xFFFFAA00, 0xFF55FF55, 0xFFFF6688, 0xFF66AAFF}[layerIndex % 5];
            graphics.fill(px + 4, y, px + pw - 4, y + HEADER_HEIGHT - 2, 0x60222233);
            graphics.drawString(font, Component.translatable(layer.nameKey()), px + 10, y + 5, headerColor, false);
            y += HEADER_HEIGHT;
            for (ResourceLocation originId : layer.origins()) {
                KlassenData.OriginInfo info = model.origin(originId);
                if (info == null) continue;
                int rowY = y;
                boolean isSelected = originId.equals(currentIdForLayer(currentRaw, layer.id()));
                boolean hover = mouseX >= px + 4 && mouseX <= px + pw - 4 && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
                if (hover) hovered = info;

                if (hover) graphics.fill(px + 4, rowY, px + pw - 4, rowY + ROW_HEIGHT, 0x40FFFFFF);
                if (isSelected) {
                    graphics.fill(px + 4, rowY, px + 8, rowY + ROW_HEIGHT, 0xFF55FF55);
                    graphics.drawString(font, Component.literal("✔"), px + pw - 18, rowY + 6, 0x55FF55, false);
                }

                graphics.fill(px + 10, rowY + 2, px + 28, rowY + 20, 0xFF0A0A12);
                graphics.renderOutline(px + 10, rowY + 2, 18, 18, isSelected ? 0xFF55FF55 : 0xFF666688);
                graphics.renderItem(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(info.iconItem()))), px + 12, rowY + 4);

                Component name = Component.translatable("screen.klassen.overview.entry",
                    Component.translatable(info.nameKey()), impactPips(info.impact()));
                graphics.drawString(font, name, px + 34, rowY + 6, isSelected ? 0x77FF77 : 0xF0F0FF, true);

                List<String> next = new ArrayList<>();
                for (KlassenData.UpgradeInfo up : info.upgrades()) {
                    KlassenData.OriginInfo target = model.origin(new ResourceLocation(up.targetOrigin()));
                    next.add(target != null ? Component.translatable(target.nameKey()).getString() : up.targetOrigin());
                }
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

        if (maxScroll() > 0) {
            int trackX = px + pw - 6;
            graphics.fill(trackX, py + 2, trackX + 3, py + ph - 2, 0x40FFFFFF);
            int thumbH = Math.max(20, ph * ph / (contentHeight + ph));
            int thumbY = py + 2 + (ph - 4 - thumbH) * scroll / maxScroll();
            graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, 0xFFAA66FF);
        }

        if (hovered != null) {
            int tipY = py + ph - 22;
            graphics.fill(px + 4, tipY, px + pw - 4, tipY + 18, 0xE0000000);
            graphics.renderOutline(px + 4, tipY, pw - 8, 18, 0xFF8A2BE2);
            graphics.drawString(font, Component.translatable(hovered.descKey()), px + 10, tipY + 5, 0xCCCCFF, false);
        }
    }

    private static ResourceLocation currentRaceId(Map<?, Origin> current) {
        ResourceLocation id = currentIdForLayer(current, RACE_LAYER);
        return id;
    }

    private static ResourceLocation currentIdForLayer(Map<?, Origin> current, ResourceLocation layerId) {
        for (Map.Entry<?, Origin> entry : current.entrySet()) {
            if (entry.getKey() instanceof io.github.apace100.origins.origin.OriginLayer layer
                && layer.getIdentifier().equals(layerId) && entry.getValue() != null) {
                return entry.getValue().getIdentifier();
            }
        }
        return null;
    }

    private void drawAscensionProgress(GuiGraphics g, Minecraft client, ResourceLocation currentRaceId, int px, int py, int pw) {
        if (currentRaceId == null) return;
        KlassenData.OriginInfo race = model.origin(currentRaceId);
        if (race == null) return;

        String advId = null;
        String targetName = null;
        if (!race.upgrades().isEmpty()) {
            KlassenData.UpgradeInfo up = race.upgrades().get(0);
            advId = up.advancement();
            KlassenData.OriginInfo target = model.origin(new ResourceLocation(up.targetOrigin()));
            targetName = target != null ? Component.translatable(target.nameKey()).getString() : up.targetOrigin();
        }

        float pct;
        Component label;
        if (advId == null) {
            pct = 1.0f;
            label = Component.translatable("screen.klassen.progress.max");
        } else {
            pct = readAdvancementPercent(client, advId);
            label = Component.translatable("screen.klassen.progress", targetName, Math.round(pct * 100) + "%");
        }

        int x = px + 4, y = py + 4, w = pw - 8, h = 16;
        g.fill(x, y, x + w, y + h, 0xFF101018);
        g.renderOutline(x, y, w, h, 0xFF8A2BE2);
        int fw = (int) ((w - 2) * Math.max(0f, Math.min(1f, pct)));
        if (fw > 0) g.fill(x + 1, y + 1, x + 1 + fw, y + h - 1, pct >= 1f ? 0xFF44DD66 : 0xFF44AAFF);
        g.drawCenteredString(font, label, x + w / 2, y + 4, 0xFFFFFF);
    }

    private static float readAdvancementPercent(Minecraft client, String advId) {
        try {
            if (client.getConnection() == null) return 0f;
            var advancements = client.getConnection().getAdvancements();
            Advancement adv = advancements.getAdvancements().get(new ResourceLocation(advId));
            if (adv == null) return 0f;
            AdvancementProgress prog = ((ClientAdvancementsAccessor) advancements).klassen$getProgressMap().get(adv);
            if (prog == null) return 0f;
            return prog.isDone() ? 1f : prog.getPercent();
        } catch (Throwable t) {
            return 0f;
        }
    }

    private static String impactPips(int impact) {
        return "●".repeat(Math.max(0, impact)) + "○".repeat(Math.max(0, 3 - impact));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
