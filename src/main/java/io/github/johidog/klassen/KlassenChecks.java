package io.github.johidog.klassen;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public final class KlassenChecks {

    public static final ResourceLocation CLASS_LAYER = new ResourceLocation("klassen", "class");

    private KlassenChecks() {
    }

    /** 0 = kein Bogenschuetze, sonst Stufe 1-3 */
    public static int archerTier(Player player) {
        Map<OriginLayer, Origin> origins = ModComponents.ORIGIN.get(player).getOrigins();
        for (Map.Entry<OriginLayer, Origin> e : origins.entrySet()) {
            if (e.getKey().getIdentifier().equals(CLASS_LAYER) && e.getValue() != null) {
                ResourceLocation id = e.getValue().getIdentifier();
                if (id.equals(new ResourceLocation("klassen", "meisterschuetze"))) return 3;
                if (id.equals(new ResourceLocation("klassen", "schuetze"))) return 2;
                if (id.equals(new ResourceLocation("klassen", "bogenschuetze_anfaenger"))) return 1;
            }
        }
        return 0;
    }
}
