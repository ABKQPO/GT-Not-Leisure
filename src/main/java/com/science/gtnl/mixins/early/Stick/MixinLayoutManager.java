package com.science.gtnl.mixins.early.Stick;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.common.item.items.Stick;

import codechicken.nei.KeyManager;
import codechicken.nei.LayoutManager;
import codechicken.nei.guihook.IContainerDrawHandler;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.guihook.IContainerObjectHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;

@Mixin(value = LayoutManager.class, remap = false)
public abstract class MixinLayoutManager implements IContainerInputHandler, IContainerTooltipHandler,
    IContainerDrawHandler, IContainerObjectHandler, KeyManager.IKeyStateTracker {

    @Redirect(
        method = "handleItemDisplayName",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
            remap = true))
    public Item getItemR(ItemStack instance) {
        var item = instance.getItem();
        if (item instanceof Stick stick && !stick.isShiftDown()) {
            var fake = Stick.getDisguisedStack(instance);
            if (fake != null) {
                return fake.getItem();
            }
        }
        return item;
    }
}
