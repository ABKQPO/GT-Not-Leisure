package com.science.gtnl.mixins.late.draconicEvolution;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.ReactorExplosion;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

@Mixin(value = ReactorExplosion.class, remap = false)
public abstract class MixinReactorExplosion {

    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private int xCoord;
    @Shadow
    @Final
    private int yCoord;
    @Shadow
    @Final
    private int zCoord;
    @Shadow
    @Final
    private float power;
    @Shadow
    private boolean isDead;

    @Inject(method = "updateProcess", at = @At("HEAD"), cancellable = true)
    private void injectDynamoCheck(CallbackInfo ci) {
        for (int x = xCoord - 10; x <= xCoord + 10; x++) {
            for (int y = yCoord - 10; y <= yCoord + 10; y++) {
                for (int z = zCoord - 10; z <= zCoord + 10; z++) {
                    if (ExplosionDynamoHatch
                        .absorbExplosion(world, x, y, z, xCoord, yCoord, zCoord, this.power * 10000 * 20480)) {
                        this.isDead = true;
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}
