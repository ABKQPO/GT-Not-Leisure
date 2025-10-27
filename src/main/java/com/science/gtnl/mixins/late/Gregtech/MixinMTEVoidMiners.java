package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.science.gtnl.config.MainConfig;

import bwcrossmod.galacticgreg.MTEVoidMiners;
import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;

@Mixin(value = { MTEVoidMiners.VMUV.class, MTEVoidMiners.VMZPM.class, MTEVoidMiners.VMLUV.class }, remap = false)
public abstract class MixinMTEVoidMiners {

    @ModifyArg(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"),
        index = 0)
    private static IHatchElement<?>[] modifyAtLeastArgs(IHatchElement<?>[] elements) {
        if (!MainConfig.enableVoidMinerTweak) return elements;
        for (IHatchElement<?> e : elements) {
            if (e == HatchElement.Energy) {
                IHatchElement<?>[] modified = new IHatchElement<?>[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    if (elements[i] == HatchElement.Energy) {
                        modified[i] = HatchElement.Energy.or(HatchElement.ExoticEnergy);
                    } else {
                        modified[i] = elements[i];
                    }
                }
                return modified;
            }
        }
        return elements;
    }
}
