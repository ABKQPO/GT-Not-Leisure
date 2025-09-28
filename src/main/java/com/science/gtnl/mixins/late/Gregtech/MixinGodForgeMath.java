package com.science.gtnl.mixins.late.Gregtech;

import static tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.science.gtnl.common.machine.multiblock.FOGAlloyBlastSmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGAlloySmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;

import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEForgeOfGods;
import tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath;

@Mixin(value = GodforgeMath.class, remap = false)
public abstract class MixinGodForgeMath {

    @Inject(method = "allowModuleConnection", at = @At("HEAD"), cancellable = true)
    private static void recipesLoader(MTEBaseModule module, MTEForgeOfGods godforge,
        CallbackInfoReturnable<Boolean> cir) {
        if (module instanceof FOGAlloySmelterModule) {
            cir.setReturnValue(true);
            return;
        }
        if ((module instanceof FOGAlloyBlastSmelterModule || module instanceof FOGExtractorModule)
            && godforge.isUpgradeActive(FDIM)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "calculateMaxParallelForModules(Ltectech/thing/metaTileEntity/multi/godforge/MTEBaseModule;Ltectech/thing/metaTileEntity/multi/godforge/MTEForgeOfGods;I)V",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onCalculateMaxParallelForModulesTail(MTEBaseModule module, MTEForgeOfGods godforge,
        int fuelFactor, CallbackInfo ci, int baseParallel, float fuelFactorMultiplier, float heatMultiplier,
        float upgradeAmountMultiplier, int node53, boolean isMoltenOrSmeltingWithUpgrade, float totalBonuses,
        int maxParallel) {
        if (module instanceof FOGAlloySmelterModule) {
            baseParallel = 4096;
            module.setMaxParallel((int) (baseParallel * totalBonuses));
        } else if (module instanceof FOGExtractorModule) {
            baseParallel = 2048;
            module.setMaxParallel((int) (baseParallel * totalBonuses));
        } else if (module instanceof FOGAlloyBlastSmelterModule) {
            baseParallel = 1024;
            module.setMaxParallel((int) (baseParallel * totalBonuses));
        }
    }

}
