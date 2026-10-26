package com.science.gtnl.mixins.late.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.yogsothoth.YogBaublesView;

import baubles.api.BaublesApi;

@Mixin(value = BaublesApi.class, remap = false)
public abstract class MixinBaublesApiYogTerminal {

    @Inject(method = "getBaubles", at = @At("RETURN"), cancellable = true)
    private static void gtnl$exposeYogTerminals(EntityPlayer player, CallbackInfoReturnable<IInventory> cir) {
        IInventory original = cir.getReturnValue();
        IInventory view = YogBaublesView.wrap(player, original);
        if (view != original) cir.setReturnValue(view);
    }
}
