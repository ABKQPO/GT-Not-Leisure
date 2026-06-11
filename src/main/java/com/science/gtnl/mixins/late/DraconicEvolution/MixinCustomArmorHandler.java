package com.science.gtnl.mixins.late.DraconicEvolution;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.draconicevolution.common.items.armor.CustomArmorHandler;
import com.reavaritia.common.items.InfinitySword;
import com.science.gtnl.config.MainConfig;

@Mixin(value = CustomArmorHandler.class, remap = false)
public abstract class MixinCustomArmorHandler {

    @Inject(method = "onLivingDeath", at = @At(value = "HEAD"), cancellable = true)
    private void injectDeath(LivingDeathEvent event, CallbackInfo ci) {
        if (!MainConfig.re_avaritia.infinity_sword.enableBypass) return;
        Entity entity = event.source.getSourceOfDamage();
        if (!(entity instanceof EntityPlayer player)) return;
        if (!player.isSneaking()) return;
        if (!event.source.damageType.contains(InfinitySword.INFINITY_DAMAGE.damageType)) return;
        ci.cancel();
    }
}
