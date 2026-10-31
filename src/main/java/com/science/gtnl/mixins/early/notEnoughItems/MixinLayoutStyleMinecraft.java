package com.science.gtnl.mixins.early.notEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IDrawableResourceAccessor;
import com.science.gtnl.config.OtherConfig;

import codechicken.nei.Button;
import codechicken.nei.Image;
import codechicken.nei.LayoutStyleMinecraft;

@Mixin(value = LayoutStyleMinecraft.class, remap = false)
public class MixinLayoutStyleMinecraft {

    @Unique
    private static final String GTNL$SPECIAL_CHEAT_ICON = "nei:textures/items/cheat_speical.png";

    @Inject(
        method = "drawButton",
        at = @At(value = "INVOKE", target = "Lcodechicken/nei/LayoutManager;drawIcon(IILcodechicken/nei/Image;)V"),
        cancellable = true)
    private void beforeDrawIcon(Button b, int mousex, int mousey, CallbackInfo ci) {
        Image icon = b.getRenderIcon();
        if (!OtherConfig.not_enough_items.enableSpecialCheatIcon || !(icon instanceof AccessorDrawableResource accessor)
            || !(icon instanceof IDrawableResourceAccessor dr)
            || !GTNL$SPECIAL_CHEAT_ICON.equals(String.valueOf(accessor.getResourceLocation()))) return;
        int specialType = OtherConfig.not_enough_items.specialIconType;
        dr.gtnl$draw(b.x + (b.w - icon.width) / 2, b.y + (b.h - icon.height) / 2, specialType);
        ci.cancel();
    }
}
