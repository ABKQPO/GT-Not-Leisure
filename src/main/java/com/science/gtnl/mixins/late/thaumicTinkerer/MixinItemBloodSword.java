package com.science.gtnl.mixins.late.thaumicTinkerer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.reavaritia.common.items.InfinitySword;
import com.science.gtnl.config.MainConfig;

import thaumic.tinkerer.common.item.ItemBloodSword;

@Mixin(value = ItemBloodSword.EventHandler.class, remap = false)
public abstract class MixinItemBloodSword {

    @Inject(method = "onDamageTaken", at = @At("HEAD"), cancellable = true)
    public void onDamageTaken(LivingAttackEvent event, CallbackInfo ci) {
        if (!MainConfig.re_avaritia.infinity_sword.enableBypass) return;
        Entity entity = event.source.getSourceOfDamage();
        if (!(entity instanceof EntityPlayer player)) return;
        if (!player.isSneaking()) return;
        if (!event.source.damageType.contains(InfinitySword.INFINITY_DAMAGE.damageType)) return;
        ci.cancel();
    }
}
