package com.science.gtnl.mixins.late.galaxySpace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IRocketRecipeIdProvider;

import galaxyspace.core.nei.RocketRecipeHandler;

@Mixin(value = RocketRecipeHandler.class, remap = false)
public abstract class MixinRocketRecipeHandler {

    @Inject(method = "getRecipeId", at = @At("RETURN"), cancellable = true)
    private void onGetRecipeId(CallbackInfoReturnable<String> cir) {
        Object self = this;

        if (self instanceof IRocketRecipeIdProvider provider) {
            String customId = provider.getCustomRecipeId();
            if (customId != null && !customId.isEmpty()) {
                cir.setReturnValue(customId);
            }
        }
    }
}
