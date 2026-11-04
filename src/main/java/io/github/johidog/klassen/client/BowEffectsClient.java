package io.github.johidog.klassen.client;

import io.github.johidog.klassen.KlassenChecks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class BowEffectsClient {

    private static final TagKey<Item> BOEGEN = TagKey.create(Registries.ITEM, new ResourceLocation("klassen", "boegen"));

    private BowEffectsClient() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BowEffectsClient::tick);
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        Player player = client.player;
        ClientLevel world = client.level;
        if (KlassenChecks.archerTier(player) <= 0) return;

        ItemStack held = player.getMainHandItem();
        boolean holdingBow = held.is(BOEGEN) || held.is(Items.BOW) || player.getOffhandItem().is(BOEGEN);
        if (!holdingBow) return;

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0f);
        Vec3 hand = eye.add(look.scale(0.7)).add(0, -0.15, 0);
        long time = world.getGameTime();

        // 1) Halte-Effekt: schwebende Partikel um die Bogenhand
        double a = time * 0.15;
        for (int i = 0; i < 3; i++) {
            double ang = a + i * (Math.PI * 2 / 3);
            world.addParticle(ParticleTypes.END_ROD,
                hand.x + Math.cos(ang) * 0.25, hand.y + Math.sin(ang * 1.3) * 0.15, hand.z + Math.sin(ang) * 0.25,
                0, 0.02, 0);
        }
        if (time % 10 == 0) {
            world.addParticle(ParticleTypes.GLOW, hand.x, hand.y, hand.z, 0, 0.01, 0);
        }

        // 2) Lade-Effekt: Partikel-Ring um den Spieler, skaliert mit Ladung
        if (player.isUsingItem() && (player.getUseItem().is(BOEGEN) || player.getUseItem().is(Items.BOW))) {
            int duration = player.getUseItem().getUseDuration();
            int remaining = player.getUseItemRemainingTicks();
            float charge = duration > 0 ? 1.0f - (float) remaining / duration : 0f;
            int count = 2 + (int) (charge * 10);
            double r = 0.6 + charge * 0.5;
            for (int i = 0; i < count; i++) {
                double ang = time * 0.4 + i * (Math.PI * 2 / count);
                world.addParticle(charge > 0.9 ? ParticleTypes.FLASH : ParticleTypes.ELECTRIC_SPARK,
                    player.getX() + Math.cos(ang) * r, player.getY() + 0.3 + (i % 3) * 0.5, player.getZ() + Math.sin(ang) * r,
                    0, 0.03, 0);
            }
            if (charge > 0.95f && time % 5 == 0) {
                world.addParticle(ParticleTypes.GLOW,
                    player.getX(), player.getEyeY(), player.getZ(), 0, 0.05, 0);
            }
        }
    }
}
