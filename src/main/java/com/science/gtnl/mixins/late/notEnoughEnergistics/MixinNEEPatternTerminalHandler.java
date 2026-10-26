package com.science.gtnl.mixins.late.notEnoughEnergistics;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.vfyjxf.nee.nei.NEEPatternTerminalHandler;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.item.ItemUtils;

@Mixin(value = NEEPatternTerminalHandler.class, remap = false)
public abstract class MixinNEEPatternTerminalHandler {

    @Redirect(
        method = "packProcessRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;",
            remap = true,
            ordinal = 1))
    public ItemStack gtnl$replaceOutputStack(ItemStack instance) {
        if (!MainConfig.other.not_enough_items.enableReplaceHotIngotInPattern) return instance.copy();
        return ItemUtils.coolHotIngot(instance);
    }
}
