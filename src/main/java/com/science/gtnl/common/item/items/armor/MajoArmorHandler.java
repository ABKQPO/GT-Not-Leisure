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
    private static final UUID TRAVELER_HEALTH_BONUS_ID = UUID.fromString("6ab9847c-96a6-4e8a-a109-a65c7d2c7898");
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
    private static final AttributeModifier TRAVELER_HEALTH_BONUS = new AttributeModifier(
        TRAVELER_HEALTH_BONUS_ID,
        "Majo traveler maximum health",
        80.0D,
        0);
    private static final String COOLDOWN_TAG = "GTNLMajoRobeCooldown";
    private static final String TRAVELER_ACTIVE_TAG = "GTNLMajoTravelerActive";
    private static final String TRAVELER_FLIGHT_TAG = "GTNLMajoTravelerFlight";
    private static final int EMERGENCY_COOLDOWN = 60 * 20;
    private static final int EMERGENCY_DURATION = 15 * 20;
    private static final int TRAVELER_EFFECT_DURATION = 60;

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
        String playerName = player.getGameProfile()
            .getName();
        boolean travelerSet = wearingHat && wearingRobe
            && ("DreamYao520".equals(playerName) || "SereiaWe".equals(playerName));

        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.maxHealth), HEALTH_BONUS, wearingRobe);
        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.maxHealth), TRAVELER_HEALTH_BONUS, travelerSet);
        updateModifier(
            player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance),
            KNOCKBACK_BONUS,
            wearingRobe);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        updateTravelerSet(player, data, travelerSet);

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
        if (!event.wasDeath) {
            NBTTagCompound originalData = event.original.getEntityData();
            NBTTagCompound newData = event.entityPlayer.getEntityData();
            if (originalData.getBoolean(TRAVELER_ACTIVE_TAG)) newData.setBoolean(TRAVELER_ACTIVE_TAG, true);
            if (originalData.getBoolean(TRAVELER_FLIGHT_TAG)) newData.setBoolean(TRAVELER_FLIGHT_TAG, true);
        }
    }

    private static void updateTravelerSet(EntityPlayer player, NBTTagCompound data, boolean enabled) {
        if (enabled) {
            data.setBoolean(TRAVELER_ACTIVE_TAG, true);
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                data.setBoolean(TRAVELER_FLIGHT_TAG, true);
                player.sendPlayerAbilities();
            }
            refreshEffect(player, Potion.resistance, 4, TRAVELER_EFFECT_DURATION);
            refreshEffect(player, Potion.damageBoost, 19, TRAVELER_EFFECT_DURATION);
            refreshEffect(player, Potion.waterBreathing, 0, TRAVELER_EFFECT_DURATION);
            refreshEffect(player, Potion.regeneration, 4, TRAVELER_EFFECT_DURATION);
            return;
        }
        if (!data.getBoolean(TRAVELER_ACTIVE_TAG)) return;
        data.removeTag(TRAVELER_ACTIVE_TAG);
        removeTravelerEffect(player, Potion.resistance, 4);
        removeTravelerEffect(player, Potion.damageBoost, 19);
        removeTravelerEffect(player, Potion.waterBreathing, 0);
        removeTravelerEffect(player, Potion.regeneration, 4);
        if (data.getBoolean(TRAVELER_FLIGHT_TAG)) {
            data.removeTag(TRAVELER_FLIGHT_TAG);
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }
        }
    }

    private static void removeTravelerEffect(EntityPlayer player, Potion potion, int amplifier) {
        PotionEffect current = player.getActivePotionEffect(potion);
        if (current != null && current.getAmplifier() == amplifier
            && current.getDuration() <= TRAVELER_EFFECT_DURATION) player.removePotionEffect(potion.id);
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
