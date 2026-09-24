package com.science.gtnl.common.item.items.armor;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class MajoArmorHandler {

    private static final UUID HEALTH_BONUS_ID = UUID.fromString("45cb6540-2701-487b-863f-8908d529bf26");
    private static final UUID KNOCKBACK_BONUS_ID = UUID.fromString("29d935ba-3ae3-4300-8cba-b3bb95287792");
    private static final AttributeModifier HEALTH_BONUS = new AttributeModifier(
        HEALTH_BONUS_ID,
        "Majo robe maximum health",
        20.0D,
        0);
    private static final AttributeModifier KNOCKBACK_BONUS = new AttributeModifier(
        KNOCKBACK_BONUS_ID,
        "Majo robe knockback resistance",
        1.0D,
        0);
    private static final String COOLDOWN_TAG = "GTNLMajoRobeCooldown";
    private static final int EMERGENCY_COOLDOWN = 60 * 20;
    private static final int EMERGENCY_DURATION = 15 * 20;

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.worldObj.isRemote) return;

        NBTTagCompound data = player.getEntityData();
        int cooldown = data.getInteger(COOLDOWN_TAG);
        if (cooldown > 0) data.setInteger(COOLDOWN_TAG, cooldown - 1);

        ItemStack hat = player.getCurrentArmor(3);
        ItemStack robe = player.getCurrentArmor(2);
        boolean wearingHat = hat != null && hat.getItem() instanceof MajoHat;
        boolean wearingRobe = robe != null && robe.getItem() instanceof MajoRobe;

        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.maxHealth), HEALTH_BONUS, wearingRobe);
        updateModifier(
            player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance),
            KNOCKBACK_BONUS,
            wearingRobe);
        if (!wearingRobe && player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        if (wearingHat) {
            player.removePotionEffect(Potion.blindness.id);
            player.removePotionEffect(Potion.confusion.id);
            // Keep the duration above 200 ticks to avoid the vanilla night-vision flicker.
            refreshEffect(player, Potion.nightVision, 0, 460);
        }

        if (wearingRobe) {
            if (PotionWarpWard.instance != null) refreshEffect(player, PotionWarpWard.instance, 0, 60);
            if (player.getHealth() < 10.0F && cooldown == 0) {
                refreshEffect(player, Potion.regeneration, 4, EMERGENCY_DURATION);
                refreshEffect(player, Potion.resistance, 2, EMERGENCY_DURATION);
                data.setInteger(COOLDOWN_TAG, EMERGENCY_COOLDOWN);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        int cooldown = event.original.getEntityData()
            .getInteger(COOLDOWN_TAG);
        if (cooldown > 0) event.entityPlayer.getEntityData()
            .setInteger(COOLDOWN_TAG, cooldown);
    }

    private static void updateModifier(IAttributeInstance attribute, AttributeModifier modifier, boolean enabled) {
        if (attribute == null) return;
        AttributeModifier current = attribute.getModifier(modifier.getID());
        if (enabled && current == null) attribute.applyModifier(modifier);
        if (!enabled && current != null) attribute.removeModifier(current);
    }

    private static void refreshEffect(EntityPlayer player, Potion potion, int amplifier, int duration) {
        PotionEffect current = player.getActivePotionEffect(potion);
        if (current != null) {
            if (current.getAmplifier() > amplifier) return;
            if (current.getAmplifier() == amplifier && current.getDuration() > duration / 2) return;
        }
        player.addPotionEffect(new PotionEffect(potion.id, duration, amplifier, true));
    }
}
