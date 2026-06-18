package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PotionEffect.class, remap = true)
public abstract class MixinPotionEffect {

    @Shadow
    public abstract int getPotionID();

    @Shadow
    public abstract int getAmplifier();

    @Inject(method = "writeCustomPotionEffectToNBT", at = @At("TAIL"))
    private void injectWriteCustomPotionEffectToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        tag.setInteger("Id", getPotionID());
        tag.setInteger("Amplifier", getAmplifier());
    }

    @Inject(method = "readCustomPotionEffectFromNBT", at = @At("TAIL"), cancellable = true)
    private static void injectReadCustomPotionEffectFromNBT(NBTTagCompound tag,
        CallbackInfoReturnable<PotionEffect> cir) {
        int id = tag.getInteger("Id");
        if (id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] != null) {
            int amplifier = tag.getInteger("Amplifier");
            int duration = tag.getInteger("Duration");
            boolean ambient = tag.getBoolean("Ambient");
            PotionEffect effect = new PotionEffect(id, duration, amplifier, ambient);
            cir.setReturnValue(effect);
        }
    }
}
