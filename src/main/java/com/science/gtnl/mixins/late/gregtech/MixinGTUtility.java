package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.glodblock.github.common.item.ItemFluidPacket;

import gregtech.api.enums.Mods;
import gregtech.api.util.GTUtility;

@Mixin(value = GTUtility.class, remap = false)
public class MixinGTUtility {

    @Inject(method = "getFluidFromContainerOrFluidDisplay", at = @At("HEAD"), cancellable = true)
    private static void injectGetFluidFromContainerOrFluidDisplay(ItemStack itemStack,
        CallbackInfoReturnable<FluidStack> cir) {
        if (!Mods.AE2FluidCraft.isModLoaded()) return;
        if (itemStack == null) return;
        if (itemStack.getItem() instanceof ItemFluidPacket) {
            cir.setReturnValue(ItemFluidPacket.getFluidStack(itemStack));
        }
        if (itemStack.getItem() instanceof ItemFluidDrop) {
            cir.setReturnValue(ItemFluidDrop.getFluidStack(itemStack));
        }
    }
}
