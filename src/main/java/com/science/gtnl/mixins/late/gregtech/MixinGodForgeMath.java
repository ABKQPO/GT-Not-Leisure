package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.machine.multiblock.FOGAlloyBlastSmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGAlloySmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;
import com.science.gtnl.common.machine.multiblock.FOGSolarMuonCatalystModule;

import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath;

@Mixin(value = GodforgeMath.class, remap = false)
public abstract class MixinGodForgeMath {

    @Inject(method = "allowModuleConnection", at = @At("HEAD"), cancellable = true)
    private static void recipesLoader(MTEBaseModule module, ForgeOfGodsData data, CallbackInfoReturnable<Boolean> cir) {
        if (module instanceof FOGAlloySmelterModule) {
            cir.setReturnValue(true);
            return;
        }
        if ((module instanceof FOGAlloyBlastSmelterModule || module instanceof FOGExtractorModule)
            && data.isUpgradeActive(ForgeOfGodsUpgrade.FDIM)) {
            cir.setReturnValue(true);
            return;
        }
        if (module instanceof FOGSolarMuonCatalystModule && data.isUpgradeActive(ForgeOfGodsUpgrade.QGPIU)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(
        method = "calculateMaxParallelForModules(Ltectech/thing/metaTileEntity/multi/godforge/MTEBaseModule;Ltectech/thing/metaTileEntity/multi/godforge/util/ForgeOfGodsData;I)V",
        constant = @Constant(intValue = 0, ordinal = 0))
    private static int onCalculateMaxParallelForModules(int constant, MTEBaseModule module, ForgeOfGodsData data,
        int fuelFactor) {
        if (module instanceof FOGAlloySmelterModule) {
            return 4096;
        } else if (module instanceof FOGExtractorModule) {
            return 2048;
        } else if (module instanceof FOGAlloyBlastSmelterModule) {
            return 512;
        } else if (module instanceof FOGSolarMuonCatalystModule) {
            return 256;
        }
        return constant;
    }

}
