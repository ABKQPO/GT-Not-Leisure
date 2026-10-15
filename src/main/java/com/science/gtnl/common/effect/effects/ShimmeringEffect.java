package com.science.gtnl.common.effect.effects;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.science.gtnl.common.effect.EffectBase;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.utils.ColorUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ShimmeringEffect extends EffectBase {

    public ShimmeringEffect(int id) {
        super(id, "shimmering", false, ColorUtils.rgb(ColorUtils.effectShimmering), 3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer player && !player.worldObj.isRemote) {
            PotionEffect effect = player.getActivePotionEffect(this);
            if (effect != null) {
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
                player.velocityChanged = true;
                player.fallDistance = 0f;
                player.onGround = false;
                player.jumpMovementFactor = 0f;
                player.setSprinting(false);
                player.setInvisible(false);

                World world = player.worldObj;

                int x = MathHelper.floor_double(player.posX);
                int y = MathHelper.floor_double(player.posY);
                int z = MathHelper.floor_double(player.posZ);

                if (player.posY > -2) {
                    player.setPositionAndUpdate(player.posX, player.posY - 0.2, player.posZ);
                }

                if (isAir1x2x1(world, x, y, z) || player.posY < 0) {
                    player.removePotionEffect(this.id);
                    player.motionX = 0;
                    player.motionY = 0;
                    player.motionZ = 0;
                    player.jumpMovementFactor = 0.02f;
                    player.onGround = true;
                }
            }
        }
    }

    private boolean isAir1x2x1(World world, int x, int y, int z) {
        if (!isPassableOrEmpty(world, x, y, z) || !isPassableOrEmpty(world, x, y + 1, z)) {
            return false;
        }

        Block blockBelow = world.getBlock(x, y - 1, z);
        if (blockBelow == null || blockBelow.getMaterial()
            .isLiquid()) {
            return false;
        }

        AxisAlignedBB aabb = blockBelow.getCollisionBoundingBoxFromPool(world, x, y - 1, z);
        return aabb != null && aabb.maxY <= y;
    }

    private boolean isPassableOrEmpty(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == null || world.isAirBlock(x, y, z)
            || !block.getMaterial()
                .blocksMovement();
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityPlayer.isPotionActive(EffectLoader.shimmering)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.entityPlayer.isPotionActive(EffectLoader.shimmering)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer player) {
            if (player.isPotionActive(EffectLoader.shimmering)) {
                if (event.source == DamageSource.inWall) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (player.isPotionActive(EffectLoader.shimmering)) {
            Entity source = event.source.getEntity();
            if (source instanceof EntityLivingBase && !(source instanceof IBossDisplayData)) {
                event.setCanceled(true);
            }
        }
    }
}
