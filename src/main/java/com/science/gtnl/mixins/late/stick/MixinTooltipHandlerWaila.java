package com.science.gtnl.mixins.late.stick;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.item.items.stick;

import mcp.mobius.waila.handlers.nei.TooltipHandlerWaila;
import mcp.mobius.waila.utils.ModIdentification;

@Mixin(value = TooltipHandlerWaila.class, remap = false)
public abstract class MixinTooltipHandlerWaila {

    @Inject(method = "handleItemTooltip", at = @At("HEAD"), cancellable = true)
    public void handleItemTooltip(GuiContainer arg0, ItemStack itemstack, int arg2, int arg3, List<String> currenttip,
        CallbackInfoReturnable<List<String>> cir) {
        if (itemstack != null) {
            if (itemstack.getItem() instanceof Stick stick && !stick.isShiftDown()) {
                ItemStack fakeItem = Stick.getDisguisedStack(itemstack);
                if (fakeItem != null) {
                    String fakeCanonicalName = ModIdentification.nameFromStack(fakeItem);
                    if (fakeCanonicalName != null && !fakeCanonicalName.isEmpty()) {
                        currenttip.add("§9§o" + fakeCanonicalName);
                    }
                    cir.setReturnValue(currenttip);
                }
            }
        }
    }
}
