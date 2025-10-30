package com.science.gtnl.mixins.late.ThaumicTinkerer;

import static com.reavaritia.common.item.InfinitySword.cancelBloodSword;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.reavaritia.common.item.InfinitySword;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumic.tinkerer.common.item.ItemBloodSword;

@Mixin(value = ItemBloodSword.EventHandler.class, remap = false)
public abstract class MixinItemBloodSword {

    @SubscribeEvent
    @Inject(method = "onDamageTaken", at = @At("HEAD"), cancellable = true)
    public void onDamageTaken(LivingAttackEvent event, CallbackInfo ci) {
        Entity source = event.source.getSourceOfDamage();
        if (source instanceof EntityLivingBase entityLivingBase) {
            ItemStack itemInUse = entityLivingBase.getHeldItem();
            if (cancelBloodSword && itemInUse != null && itemInUse.getItem() instanceof InfinitySword) {
                ci.cancel();
            }
        }
    }
}
