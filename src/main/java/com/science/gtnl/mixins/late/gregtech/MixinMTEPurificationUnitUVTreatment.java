package com.science.gtnl.mixins.late.gregtech;

import static gregtech.api.enums.HatchElement.InputBus;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.HatchElementBuilder;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitUVTreatment;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mixin(value = MTEPurificationUnitUVTreatment.class, remap = false)
public class MixinMTEPurificationUnitUVTreatment {

    @Redirect(
        method = "lambda$static$2",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeastList(Ljava/util/List;)Lgregtech/api/util/HatchElementBuilder;"))
    private static HatchElementBuilder<?> injectExtraHatches(
        HatchElementBuilder<MTEPurificationUnitUVTreatment> builder,
        List<IHatchElement<? super MTEPurificationUnitUVTreatment>> elements) {
        List<IHatchElement<? super MTEPurificationUnitUVTreatment>> newList = new ObjectArrayList<>(elements);
        newList.add(InputBus);
        return builder.atLeastList(newList);
    }
}
