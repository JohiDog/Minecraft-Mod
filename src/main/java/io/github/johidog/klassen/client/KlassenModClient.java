package io.github.johidog.klassen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KlassenModClient implements ClientModInitializer {

    public static KeyMapping overviewKey;

    @Override
    public void onInitializeClient() {
        overviewKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.klassen.overview",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.klassen"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (overviewKey.consumeClick()) {
                if (!(client.screen instanceof KlassenOverviewScreen)) {
                    client.setScreen(new KlassenOverviewScreen());
                }
            }
        });

        KlassenHudOverlay.register();
        ItemTooltipCallback.EVENT.register(new KlassenTooltips());
        BowEffectsClient.register();
    }
}
