package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;

import appeng.entity.EntityTinyTNTPrimed;

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
                    if (ExplosionDynamoHatch
                        .absorbExplosion(worldObj, x, y, z, posX, posY, posZ, TINY_TNT_DYNAMO_POWER * 20480)) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}
