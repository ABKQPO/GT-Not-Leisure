package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.compat.ae2fc.FluidCraftItems;

import gregtech.api.util.GTUtility;

@Mixin(value = GTUtility.class, remap = false)
public class MixinGTUtility {

    @Inject(method = "getFluidFromContainerOrFluidDisplay", at = @At("HEAD"), cancellable = true)
    private static void injectGetFluidFromContainerOrFluidDisplay(ItemStack stack,
        CallbackInfoReturnable<FluidStack> cir) {
        if (FluidCraftItems.isFluidItem(stack)) cir.setReturnValue(FluidCraftItems.fluidIn(stack));
    }
}
