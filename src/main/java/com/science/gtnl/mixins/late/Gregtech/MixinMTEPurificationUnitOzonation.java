package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.enums.HatchElement.InputBus;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.HatchElementBuilder;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitOzonation;

@Mixin(value = MTEPurificationUnitOzonation.class, remap = false)
public class MixinMTEPurificationUnitOzonation {

    @Redirect(
        method = "lambda$static$0",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeastList(Ljava/util/List;)Lgregtech/api/util/HatchElementBuilder;"))
    private static HatchElementBuilder<?> redirectAtLeastList(HatchElementBuilder<MTEPurificationUnitOzonation> builder,
        List<IHatchElement<? super MTEPurificationUnitOzonation>> originalList) {
        List<IHatchElement<? super MTEPurificationUnitOzonation>> newList = new ArrayList<>(originalList);
        newList.add(InputBus);
        return builder.atLeastList(newList);
    }
}
