package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.IItemStackExtra;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(value = ItemStack.class, remap = true)
public abstract class MixinItemStack {

    @Shadow
    private Item field_151002_e;

    @SideOnly(Side.CLIENT)
    @Inject(method = "getItemSpriteNumber", at = @At("HEAD"), cancellable = true)
    private void getSpriteNumberExtra(CallbackInfoReturnable<Integer> cir) {
        if (field_151002_e instanceof IItemStackExtra iItemStackExtra) {
            cir.setReturnValue(iItemStackExtra.getSpriteNumberExtra((ItemStack) (Object) this));
        }
    }
}
