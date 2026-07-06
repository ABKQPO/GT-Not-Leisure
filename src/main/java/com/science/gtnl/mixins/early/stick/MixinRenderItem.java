package com.science.gtnl.mixins.early.stick;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.common.item.items.stick;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Redirect(
        method = "renderItemIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item redirectGetItem(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof Stick stick && !stick.isShiftDown()) {
            ItemStack disguised = Stick.getDisguisedStack(stack);
            if (disguised != null) {
                return disguised.getItem();
            }
        }
        return stack.getItem();
    }

    @Redirect(
        method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item redirectGetItemForBlockCheck(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof Stick stick && !stick.isShiftDown()) {
            ItemStack disguised = Stick.getDisguisedStack(stack);
            if (disguised != null) {
                return disguised.getItem();
            }
        }
        return stack.getItem();
    }

    @ModifyVariable(method = "renderItemAndEffectIntoGUI", at = @At("HEAD"), argsOnly = true, index = 3)
    private ItemStack redirectItemStack(ItemStack original) {
        if (original != null && original.getItem() instanceof Stick stick && !stick.isShiftDown()) {
            ItemStack fake = Stick.getDisguisedStack(original);
            if (fake != null) {
                return fake;
            }
        }
        return original;
    }

}
