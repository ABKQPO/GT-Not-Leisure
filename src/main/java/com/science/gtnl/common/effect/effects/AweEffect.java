package com.science.gtnl.common.effect.effects;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.google.common.collect.ImmutableList;
import com.science.gtnl.common.effect.EffectBase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AweEffect extends EffectBase {

    public static final DamageSource awe_damage = new DamageSource("damage.gtnl.awe").setExplosion()
        .setDamageBypassesArmor()
        .setDamageIsAbsolute()
        .setDamageAllowedInCreativeMode()
        .setMagicDamage();

    private static final Set<EntityPlayer> affectedPlayers = new HashSet<>();
    private static final Random random = new Random();

    public AweEffect(int id) {
        super(id, "awe", false, 0xFF00FF, 1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer player) {
            PotionEffect effect = player.getActivePotionEffect(this);

            if (effect != null && !player.capabilities.isCreativeMode) {
                int level = effect.getAmplifier();
                double pullSpeed = level * 0.02;

                if (!affectedPlayers.contains(player)) {
                    float damage = 50.0F + random.nextFloat() * 10.0F;
                    player.attackEntityFrom(awe_damage, damage);
                    affectedPlayers.add(player);
                }

                player.capabilities.isFlying = false;
                player.capabilities.allowFlying = false;

                player.motionY = -pullSpeed;
                player.setSneaking(true);

                if (player.rotationPitch < 45) {
                    player.rotationPitch += (float) (0.2 * level);
                }
                if (player.rotationPitch > 45) {
                    player.rotationPitch -= (float) (0.2 * level);
                }

            } else {

                affectedPlayers.remove(player);
                if (player.capabilities.isCreativeMode && !player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
            }
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLiving, BaseAttributeMap attributeMap,
        int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLiving, attributeMap, amplifier);

        if (entityLiving instanceof EntityPlayerMP player) {
            for (PotionEffect effect : ImmutableList.copyOf(player.getActivePotionEffects())) {
                player.removePotionEffect(effect.getPotionID());
            }

            if (!player.capabilities.isCreativeMode) {
                player.playerNetServerHandler
                    .kickPlayerFromServer(StatCollector.translateToLocal("gtnl.effect.awe.kick"));
            } else {
                if (!player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
            }
        }
    }
}
