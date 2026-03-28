package com.science.gtnl.mixins.late.Stick;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.enderio.core.client.handlers.OreDictTooltipHandler;
import com.science.gtnl.common.item.items.Stick;

@Mixin(OreDictTooltipHandler.class)
public class MixinOreDictTooltipHandler {

    @Redirect(
        method = "onItemTooltip",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item redirectGetNameForObject(ItemStack instance) {
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
