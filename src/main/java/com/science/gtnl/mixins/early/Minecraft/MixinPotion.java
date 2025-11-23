package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Potion.class)
public class MixinPotion {

    @Mutable
    @Final
    @Shadow
    public static Potion[] potionTypes;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onPotionTypesInit(CallbackInfo ci) {
        int newLength = 256;
        Potion[] newPotionTypes = new Potion[newLength];

        System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);

        potionTypes = newPotionTypes;

    }
}
