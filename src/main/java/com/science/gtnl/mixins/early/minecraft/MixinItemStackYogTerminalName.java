package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.yogsothoth.YogToken;

@Mixin(ItemStack.class)
public abstract class MixinItemStackYogTerminalName {

    @Shadow
    public NBTTagCompound stackTagCompound;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void gtnl$yogTerminalName(CallbackInfoReturnable<String> cir) {
        if (stackTagCompound == null || !stackTagCompound.hasKey(YogToken.LINK_TAG)) return;
        cir.setReturnValue(
            StatCollector.translateToLocalFormatted("item.gtnl.yog_sothoth_terminal.disguised", cir.getReturnValue()));
    }
}
