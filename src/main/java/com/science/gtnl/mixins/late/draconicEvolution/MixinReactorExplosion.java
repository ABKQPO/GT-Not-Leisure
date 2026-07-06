package com.science.gtnl.mixins.late.draconicEvolution;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.ReactorExplosion;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;

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
        int ex = xCoord;
        int ey = yCoord;
        int ez = zCoord;

        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (!world.blockExists(x, y, z)) continue;

                    TileEntity tile = world.getTileEntity(x, y, z);

                    if (tile instanceof BaseMetaTileEntity te) {
                        IMetaTileEntity mte = te.getMetaTileEntity();

                        if (mte instanceof ExplosionDynamoHatch machine) {
                            double dx = te.getXCoord() + 0.5 - xCoord;
                            double dy = te.getYCoord() + 0.5 - yCoord;
                            double dz = te.getZCoord() + 0.5 - zCoord;
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (distance <= 10.0) {
                                double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                                long addedEU = (long) (this.power * 10000 * 20480 * efficiency);

                                long stored = machine.getEUVar();
                                long maxEU = machine.maxEUStore();

                                if (stored + addedEU <= maxEU) {
                                    machine.setEUVar(stored + addedEU);
                                    this.isDead = true;
                                    ci.cancel();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
