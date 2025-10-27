package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.config.MainConfig;

import bwcrossmod.galacticgreg.MTEVoidMiners;
import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.HatchElementBuilder;

@Mixin(value = { MTEVoidMiners.VMUV.class, MTEVoidMiners.VMZPM.class, MTEVoidMiners.VMLUV.class }, remap = false)
public abstract class MixinMTEVoidMiners {

    @Unique
    private static final boolean GTNL$enableMixin = MainConfig.enableVoidMinerTweak;

    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"))
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static HatchElementBuilder redirectAtLeast(HatchElementBuilder instance, IHatchElement<?>[] elements) {
        if (!GTNL$enableMixin) return instance.atLeast(elements);
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
                return instance.atLeast(modified);
            }
        }
        return instance.atLeast(elements);
    }
}
