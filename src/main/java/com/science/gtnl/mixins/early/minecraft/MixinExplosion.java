package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

@Mixin(value = Explosion.class, remap = true)
public abstract class MixinExplosion {

    @Shadow
    private World worldObj;
    @Shadow
    public double explosionX;
    @Shadow
    public double explosionY;
    @Shadow
    public double explosionZ;
    @Shadow
    public float explosionSize;

    @Inject(method = "doExplosionA", at = @At("HEAD"), cancellable = true)
    private void interceptExplosionStart(CallbackInfo ci) {
        if (ExplosionDynamoHatch.absorbAround(worldObj, explosionX, explosionY, explosionZ, explosionSize)) ci.cancel();
    }
}
