package com.science.gtnl.common.effect.effects;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.science.gtnl.common.effect.EffectBase;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.mixins.early.minecraft.AccessorFoodStats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PotionGhostlyShape extends EffectBase {

    private static final Set<EntityPlayer> affectedPlayers = new HashSet<>();

    public PotionGhostlyShape(int id) {
        super(id, "ghostly_shape", false, 0x99ccff, 4);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        PotionEffect effect = player.getActivePotionEffect(this);
        if (effect == null) {
            affectedPlayers.remove(player);
            return;
        }

        FoodStats fs = player.getFoodStats();
        AccessorFoodStats acc = (AccessorFoodStats) fs;

        acc.setFoodlevel(fs.getFoodLevel());
        acc.setFoodSaturationLevel(fs.getSaturationLevel());
        acc.setFoodExhaustionLevel(0.0f);
        acc.setFoodTimer(0);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!(entity instanceof EntityPlayer player)) return;

        if (!player.onGround && player.motionY < 0) {
            player.motionY *= 0.8;
        }

        if (player.isBurning()) {
            player.extinguish();
        }

        player.hurtResistantTime = 0;

        if (!affectedPlayers.contains(player)) {
            List<EntityLivingBase> nearby = player.worldObj
                .getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(32, 32, 32));
            for (EntityLivingBase e : nearby) {
                if (e instanceof EntityCreature creature && creature.getAttackTarget() == player) {
                    creature.setAttackTarget(null);
                }
            }
            affectedPlayers.add(player);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public void onEntityTarget(LivingSetAttackTargetEvent event) {
        if (event.target instanceof EntityPlayer player && event.entityLiving instanceof EntityCreature creature) {
            if (player.isPotionActive(EffectLoader.ghostly_shape)) {
                creature.setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer player) {
            if (player.isPotionActive(EffectLoader.ghostly_shape)) {
                player.worldObj.playSoundAtEntity(player, "random.hurt", 1.0F, 1.0F);
                player.attackEntityFrom(DamageSource.generic, 0);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerUseItem(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player.worldObj.isRemote) return;
        if (!player.isPotionActive(EffectLoader.ghostly_shape)) return;

        boolean actionSuccessful = false;

        if (Objects.requireNonNull(event.action) == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.entityPlayer
                .canPlayerEdit(event.x, event.y, event.z, event.face, event.entityPlayer.getCurrentEquippedItem())) {
                actionSuccessful = true;
            }
        }

        if (actionSuccessful) {
            player.removePotionEffect(EffectLoader.ghostly_shape.id);
            player.addChatMessage(new ChatComponentTranslation("Info_GhostlyShape_Cancel"));
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player.worldObj.isRemote) return;
        if (player.isPotionActive(EffectLoader.ghostly_shape)) {
            player.removePotionEffect(EffectLoader.ghostly_shape.id);
            player.addChatMessage(new ChatComponentTranslation("Info_GhostlyShape_Cancel"));
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player.worldObj.isRemote) return;
        if (player.isPotionActive(EffectLoader.ghostly_shape)) {
            player.removePotionEffect(EffectLoader.ghostly_shape.id);
            player.addChatMessage(new ChatComponentTranslation("Info_GhostlyShape_Cancel"));
        }
    }
}
