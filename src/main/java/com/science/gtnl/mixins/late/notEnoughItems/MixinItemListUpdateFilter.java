package com.science.gtnl.mixins.late.notEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.compat.nei.NEIItemListFilter;

import codechicken.nei.RestartableTask;

@Mixin(targets = "codechicken.nei.ItemListLoader$6", remap = false)
public class MixinItemListUpdateFilter {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void gtnl$runSequentialFilter(CallbackInfo ci) {
        NEIItemListFilter.filter((RestartableTask) (Object) this);
        ci.cancel();
    }
}
