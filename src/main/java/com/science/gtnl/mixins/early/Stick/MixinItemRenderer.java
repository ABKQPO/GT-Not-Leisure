package com.science.gtnl.mixins.early.Stick;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.common.item.items.Stick;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @ModifyVariable(
        method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true,
        remap = false)
    private ItemStack replaceStack(ItemStack original, EntityLivingBase entity, ItemStack stack, int pass,
        IItemRenderer.ItemRenderType type) {
        if (original == null) return null;

        if (!(original.getItem() instanceof Stick stick)) {
            return original;
        }

        if (stick.isShiftDown()) {
            return original;
        }

        ItemStack fake = Stick.getDisguisedStack(original);
        return fake != null ? fake : original;
    }
}
