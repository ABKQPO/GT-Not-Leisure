package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;

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
        int ex = (int) explosionX;
        int ey = (int) explosionY;
        int ez = (int) explosionZ;
        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (!worldObj.blockExists(x, y, z)) continue;

                    TileEntity tile = worldObj.getTileEntity(x, y, z);
                    if (tile instanceof BaseMetaTileEntity te) {
                        IMetaTileEntity mte = te.getMetaTileEntity();
                        if (mte instanceof ExplosionDynamoHatch machine) {
                            double dx = te.getXCoord() + 0.5 - explosionX;
                            double dy = te.getYCoord() + 0.5 - explosionY;
                            double dz = te.getZCoord() + 0.5 - explosionZ;
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (distance <= 10.0) {
                                double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                                long addedEU = (long) (explosionSize * 20480 * efficiency);
                                long stored = machine.getEUVar();
                                long maxEU = machine.maxEUStore();
                                if (stored + addedEU <= maxEU) {
                                    machine.setEUVar(stored + addedEU);
                                    ci.cancel();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
