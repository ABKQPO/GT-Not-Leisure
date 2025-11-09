package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gregtech.api.util.GTOreDictUnificator;

@Mixin(value = GTOreDictUnificator.class, remap = false)
public abstract class MixinGTOreDictUnificator {

    @ModifyConstant(
        method = "get(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;JZZ)Lnet/minecraft/item/ItemStack;",
        constant = @Constant(longValue = 1))
    private static long ModifyItemStackMax(long constant) {
        return 0;
    }
}
