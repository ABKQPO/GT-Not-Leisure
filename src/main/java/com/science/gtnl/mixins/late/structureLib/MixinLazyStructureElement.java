package com.science.gtnl.mixins.late.structureLib;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.science.gtnl.common.machine.tooltip.StructureTooltipHooks;

@Mixin(targets = "com.gtnewhorizon.structurelib.structure.LazyStructureElement", remap = false)
public abstract class MixinLazyStructureElement {

    @Shadow
    private IStructureElement<Object> get(Object t) {
        throw new AssertionError();
    }

    @Inject(method = "getDescription", at = @At("HEAD"), cancellable = true)
    private void gtnl$resolveBeforeDescribing(Object context, CallbackInfoReturnable<List<String>> cir) {
        if (!StructureTooltipHooks.isDescribing(context)) return;
        cir.setReturnValue(get(context).getDescription(context));
    }
}
