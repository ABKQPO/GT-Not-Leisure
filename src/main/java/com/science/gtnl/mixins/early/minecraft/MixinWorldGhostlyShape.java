package com.science.gtnl.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;

@Mixin(value = World.class, remap = true)
public abstract class MixinWorldGhostlyShape {

    @Shadow
    public List<EntityPlayer> playerEntities;

    @Inject(
        method = "getClosestVulnerablePlayer(DDDD)Lnet/minecraft/entity/player/EntityPlayer;",
        at = @At("RETURN"),
        cancellable = true)
    private void gtnl$ignoreGhostlyShapePlayers(double x, double y, double z, double range,
        CallbackInfoReturnable<EntityPlayer> cir) {
        if (!MainConfig.effect.enableGhostlyShape) return;

        EntityPlayer closest = cir.getReturnValue();
        if (closest == null || !closest.isPotionActive(EffectLoader.ghostly_shape)) return;

        EntityPlayer replacement = null;
        double closestDistanceSq = -1.0D;

        for (EntityPlayer candidate : this.playerEntities) {
            if (candidate == closest || !candidate.isEntityAlive()
                || candidate.capabilities.disableDamage
                || candidate.isPotionActive(EffectLoader.ghostly_shape)) {
                continue;
            }

            double distanceSq = candidate.getDistanceSq(x, y, z);
            if ((range < 0.0D || distanceSq < range * range)
                && (closestDistanceSq < 0.0D || distanceSq < closestDistanceSq)) {
                closestDistanceSq = distanceSq;
                replacement = candidate;
            }
        }

        cir.setReturnValue(replacement);
    }
}
