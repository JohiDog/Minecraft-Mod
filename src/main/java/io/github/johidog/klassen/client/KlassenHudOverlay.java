package io.github.johidog.klassen.client;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class KlassenHudOverlay {

    private static final ResourceLocation CLASS_LAYER = new ResourceLocation("klassen", "class");

    private KlassenHudOverlay() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(KlassenHudOverlay::render);
    }

    private static void render(GuiGraphics graphics, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui || client.screen != null) {
            return;
        }
        OriginLayer classLayer = OriginLayers.getNullableLayer(CLASS_LAYER);
        if (classLayer == null) {
            return;
        }
        Map<OriginLayer, Origin> origins = Origin.get(client.player);
        Origin origin = origins.get(classLayer);
        if (origin == null || origin == Origin.EMPTY) {
            return;
        }

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        // Slot links neben der Hotbar (Hotbar ist zentriert, 182px breit, 22px hoch)
        int x = screenWidth / 2 - 91 - 26;
        int y = screenHeight - 23;

        graphics.fill(x - 2, y - 2, x + 22, y + 22, 0x90000000);
        graphics.renderOutline(x - 2, y - 2, 24, 24, 0xFF555555);
        graphics.renderItem(origin.getDisplayItem(), x + 2, y + 2);

        graphics.drawString(client.font, Component.translatable("hud.klassen.ability"), x + 26, y + 1, 0x55FFFF, false);
        graphics.drawString(client.font, origin.getName(), x + 26, y + 10, 0xFFFFFF, false);
        graphics.drawString(client.font, Component.translatable("hud.klassen.ability_key"), x + 26, y + 19 - 8, 0xFFAA00, false);
    }
}
