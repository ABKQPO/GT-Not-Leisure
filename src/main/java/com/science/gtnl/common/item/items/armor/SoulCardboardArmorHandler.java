package com.science.gtnl.common.item.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.science.gtnl.loader.EffectLoader;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoulCardboardArmorHandler {

    public static final int JUMP_AMPLIFIER = 0;
    public static final int SPEED_AMPLIFIER = 1;
    public static final int GHOSTLY_SHAPE_DURATION = 60;

    private static final int REFRESH_DURATION = 20;

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (!isWearingFullSet(player)) return;

        applySetBonus(player, Potion.jump, JUMP_AMPLIFIER);
        applySetBonus(player, Potion.moveSpeed, SPEED_AMPLIFIER);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (!isWearingFullSet(player)) return;

        player.addPotionEffect(new PotionEffect(EffectLoader.ghostly_shape.id, GHOSTLY_SHAPE_DURATION, 0));
    }

    public static boolean isWearingFullSet(EntityPlayer player) {
        for (int slot = 0; slot < 4; slot++) {
            ItemStack armor = player.getCurrentArmor(slot);
            if (armor == null || !(armor.getItem() instanceof SoulCardboardArmor)) {
                return false;
            }
        }
        return true;
    }

    private static void applySetBonus(EntityPlayer player, Potion potion, int amplifier) {
        PotionEffect active = player.getActivePotionEffect(potion);
        if (active != null) {
            if (active.getAmplifier() > amplifier) return;
            if (active.getDuration() > REFRESH_DURATION / 2) return;
        }

        player.addPotionEffect(new PotionEffect(potion.id, REFRESH_DURATION, amplifier, true));
    }
}
