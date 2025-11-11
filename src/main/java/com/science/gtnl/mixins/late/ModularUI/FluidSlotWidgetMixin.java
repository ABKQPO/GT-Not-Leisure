package com.science.gtnl.mixins.late.ModularUI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SyncedWidget;
import com.llamalad7.mixinextras.sugar.Local;
import com.reavaritia.common.item.InfinityBucket;

@Mixin(value = FluidSlotWidget.class, remap = false)
public abstract class FluidSlotWidgetMixin extends SyncedWidget {

    @Shadow
    protected abstract ItemStack fillFluid(@NotNull FluidStack heldFluid, boolean processFullStack);

    @Shadow
    protected abstract ItemStack drainFluid(boolean processFullStack);

    @Inject(
        method = "transferFluid",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gtnewhorizons/modularui/common/widget/FluidSlotWidget;fillFluid(Lnet/minecraftforge/fluids/FluidStack;Z)Lnet/minecraft/item/ItemStack;",
            ordinal = 1),
        cancellable = true)
    private void beforeSecondFillFluid(Widget.ClickData clickData, CallbackInfoReturnable<ItemStack> cir,
        @Local(name = "heldFluid") FluidStack heldFluid, @Local(name = "processFullStack") boolean processFullStack,
        @Local(name = "heldItem") ItemStack heldItem) {
        if (fillFluid(heldFluid, processFullStack) == null && heldItem.getItem() instanceof InfinityBucket) {
            cir.setReturnValue(drainFluid(processFullStack));
        }
    }
}
