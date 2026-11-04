package io.github.johidog.klassen.mixin;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(AbstractArrow.class)
public abstract class ArrowEffectsMixin {

    private static final ResourceLocation CLASS_LAYER = new ResourceLocation("klassen", "class");
    private static final Set<ResourceLocation> ARCHER = Set.of(
        new ResourceLocation("klassen", "bogenschuetze_anfaenger"),
        new ResourceLocation("klassen", "schuetze"),
        new ResourceLocation("klassen", "meisterschuetze"));

    private static int archerTier(Player player) {
        Map<OriginLayer, Origin> origins = ModComponents.ORIGIN.get(player).getOrigins();
        for (Map.Entry<OriginLayer, Origin> e : origins.entrySet()) {
            if (e.getKey().getIdentifier().equals(CLASS_LAYER) && e.getValue() != null) {
                ResourceLocation id = e.getValue().getIdentifier();
                if (id.equals(new ResourceLocation("klassen", "meisterschuetze"))) return 3;
                if (id.equals(new ResourceLocation("klassen", "schuetze"))) return 2;
                if (ARCHER.contains(id)) return 1;
            }
        }
        return 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void klassen$arrowTrail(CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.level() instanceof ServerLevel world && self.getOwner() instanceof Player player) {
            if (archerTier(player) > 0) {
                world.sendParticles(ParticleTypes.END_ROD,
                    self.getX(), self.getY() + 0.1, self.getZ(), 2, 0.04, 0.04, 0.04, 0.01);
                world.sendParticles(ParticleTypes.CRIT,
                    self.getX(), self.getY() + 0.1, self.getZ(), 1, 0.08, 0.08, 0.08, 0.02);
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void klassen$arrowImpact(EntityHitResult hit, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.level() instanceof ServerLevel world && self.getOwner() instanceof Player player) {
            int tier = archerTier(player);
            if (tier > 0) {
                var target = hit.getEntity();
                world.sendParticles(ParticleTypes.FLASH, target.getX(), target.getY() + 0.5, target.getZ(), 3, 0.1, 0.1, 0.1, 0.0);
                world.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.5, target.getZ(), 12, 0.35, 0.35, 0.35, 0.08);
                world.sendParticles(ParticleTypes.POOF, target.getX(), target.getY() + 0.5, target.getZ(), 6, 0.25, 0.25, 0.25, 0.05);
                target.hurt(world.damageSources().arrow(self, player), 1.5f * tier);
            }
        }
    }
}
