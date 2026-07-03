package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

import appeng.entity.EntityTinyTNTPrimed;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;

@Mixin(value = EntityTinyTNTPrimed.class, remap = false)
public abstract class MixinEntityTinyTNTPrimed extends EntityTNTPrimed {

    @Unique
    private static final float TINY_TNT_DYNAMO_POWER = 1.0F;

    public MixinEntityTinyTNTPrimed(World p_i1729_1_) {
        super(p_i1729_1_);
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true, remap = true)
    private void injectDynamoCheck(CallbackInfo ci) {
        int ex = (int) posX;
        int ey = (int) posY;
        int ez = (int) posZ;

        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (!worldObj.blockExists(x, y, z)) continue;

                    TileEntity tile = worldObj.getTileEntity(x, y, z);

                    if (tile instanceof BaseMetaTileEntity te) {
                        IMetaTileEntity mte = te.getMetaTileEntity();

                        if (mte instanceof ExplosionDynamoHatch machine) {
                            double dx = te.getXCoord() + 0.5 - posX;
                            double dy = te.getYCoord() + 0.5 - posY;
                            double dz = te.getZCoord() + 0.5 - posZ;
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (distance <= 10.0) {
                                double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                                long addedEU = (long) (TINY_TNT_DYNAMO_POWER * 20480 * efficiency);

                                long stored = machine.getEUVar();
                                long maxEU = machine.maxEUStore();

                                if (stored + addedEU <= maxEU) {
                                    machine.setEUVar(stored + addedEU);
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
