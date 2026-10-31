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
import com.science.gtnl.common.machine.multiblock.GodforgeModuleHooks;

import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;
import tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath;

@Mixin(value = GodforgeMath.class, remap = false)
public abstract class MixinGodForgeMath {

    @Inject(method = "allowModuleConnection", at = @At("HEAD"), cancellable = true)
    private static void recipesLoader(MTEBaseModule _module, ForgeOfGodsData data,
        CallbackInfoReturnable<Boolean> cir) {
        if (GodforgeModuleHooks.connects(_module, data)) cir.setReturnValue(true);
    }

    @ModifyConstant(
        method = "calculateMaxParallelForModules(Ltectech/thing/metaTileEntity/multi/godforge/MTEBaseModule;Ltectech/thing/metaTileEntity/multi/godforge/util/ForgeOfGodsData;I)V",
        constant = @Constant(intValue = 0, ordinal = 0))
    private static int onCalculateMaxParallelForModules(int constant, MTEBaseModule _module, ForgeOfGodsData data,
        int fuelFactor) {
        if (_module instanceof FOGAlloySmelterModule) {
            return 4096;
        } else if (_module instanceof FOGExtractorModule) {
            return 2048;
        } else if (_module instanceof FOGAlloyBlastSmelterModule) {
            return 512;
        } else if (_module instanceof FOGSolarMuonCatalystModule) {
            return 256;
        }
        return constant;
    }

}
