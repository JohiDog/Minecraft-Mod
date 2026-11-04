package io.github.johidog.klassen.mixin;

import io.github.johidog.klassen.KlassenChecks;
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


    @Inject(method = "tick", at = @At("HEAD"))
    private void klassen$arrowTrail(CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.level() instanceof ServerLevel world && self.getOwner() instanceof Player player) {
            if (KlassenChecks.archerTier(player) > 0) {
                double speed = self.getDeltaMovement().length();
                int n = 2 + (int) (speed * 4);
                world.sendParticles(ParticleTypes.END_ROD,
                    self.getX(), self.getY() + 0.1, self.getZ(), n, 0.05, 0.05, 0.05, 0.02);
                world.sendParticles(ParticleTypes.GLOW,
                    self.getX(), self.getY() + 0.1, self.getZ(), n, 0.1, 0.1, 0.1, 0.0);
                world.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    self.getX(), self.getY() + 0.1, self.getZ(), 1, 0.15, 0.15, 0.15, 0.05);
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void klassen$arrowImpact(EntityHitResult hit, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.level() instanceof ServerLevel world && self.getOwner() instanceof Player player) {
            int tier = KlassenChecks.archerTier(player);
            if (tier > 0) {
                var target = hit.getEntity();
                world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, target.getX(), target.getY() + 0.5, target.getZ(), 1, 0, 0, 0, 0);
                world.sendParticles(ParticleTypes.FLASH, target.getX(), target.getY() + 0.5, target.getZ(), 4, 0.15, 0.15, 0.15, 0.0);
                world.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.5, target.getZ(), 20 + tier * 10, 0.5, 0.5, 0.5, 0.1);
                world.sendParticles(ParticleTypes.POOF, target.getX(), target.getY() + 0.5, target.getZ(), 10, 0.4, 0.4, 0.4, 0.05);
                world.sendParticles(ParticleTypes.GLOW, target.getX(), target.getY() + 0.5, target.getZ(), 15, 0.6, 0.6, 0.6, 0.02);
                target.hurt(world.damageSources().arrow(self, player), 2.0f * tier);
            }
        }
    }
}
