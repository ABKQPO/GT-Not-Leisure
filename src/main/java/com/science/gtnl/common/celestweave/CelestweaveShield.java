package com.science.gtnl.common.celestweave;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.reavaritia.common.items.InfinitySword;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class CelestweaveShield {

    private static final String LIGHTNING = "gtnl.celestweave.lightning";
    private static final Map<EntityPlayer, Absorb> PENDING = new WeakHashMap<>();
    private static final Map<EntityPlayer, Hit> HANDLED = new WeakHashMap<>();

    private record Absorb(DamageSource source, float fraction) {}

    private record Hit(DamageSource source, long tick) {}

    public static boolean shielded(ItemStack chest) {
        return chest != null && CelestweaveData.active(chest, CelestweaveModule.SHIELD);
    }

    public static boolean bypass(DamageSource source, float amount) {
        if (source == DamageSource.outOfWorld && amount >= Float.MAX_VALUE / 2) return true;
        if ("administrative.kill".equals(source.damageType)) return true;
        return MainConfig.re_avaritia.infinity_sword.enableBypass
            && source.getSourceOfDamage() instanceof EntityPlayer attacker
            && attacker.isSneaking()
            && source.damageType != null
            && source.damageType.contains(InfinitySword.INFINITY_DAMAGE.damageType);
    }

    public static boolean canAbsorb(EntityPlayer player) {
        if (EquipmentEnergy.free(player) || CelestweaveEnergy.availableSet(player) > 0) return true;
        return MainConfig.item.celestweave.wirelessFallback && EquipmentEnergy.wirelessAvailable(player) >= 1;
    }

    public static boolean environment(DamageSource source) {
        return source == DamageSource.inFire || source == DamageSource.onFire
            || source == DamageSource.lava
            || source == DamageSource.fall
            || source == DamageSource.inWall
            || source == DamageSource.drown;
    }

    private static void cancel(LivingAttackEvent event, EntityPlayer player) {
        event.setCanceled(true);
        HANDLED.remove(player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void attack(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayerMP player)) return;
        DamageSource source = event.source;
        long now = player.worldObj.getTotalWorldTime();
        Hit seen = HANDLED.remove(player);
        if (seen != null && seen.source() == source && seen.tick() == now) return;
        HANDLED.put(player, new Hit(source, now));
        PENDING.remove(player);
        float amount = event.ammount;
        if (!Float.isFinite(amount) || amount <= 0 || source == DamageSource.outOfWorld || bypass(source, amount))
            return;
        ItemStack chest = CelestweaveData.piece(player, CelestweaveArmor.CHEST);
        if (chest == null) return;
        boolean invulnerable = player.hurtResistantTime > player.maxHurtResistantTime / 2;
        if (!invulnerable && CelestweaveData.active(chest, CelestweaveModule.REFLECT)
            && reflect(player, chest, source, amount)) {
            cancel(event, player);
            return;
        }
        if (!shielded(chest) || !canAbsorb(player)) return;
        float last = invulnerable ? player.lastDamage : 0F;
        float incoming = amount - last;
        if (incoming <= 0) return;
        var config = MainConfig.item.celestweave;
        float cap = Math.min(incoming, config.maxAbsorbPerHit);
        if (cap <= 0) return;
        double cost = Math.min(cap * config.shieldEuPerDamage, config.maxEuPerHit);
        double paid = EquipmentEnergy.free(player) ? cost : CelestweaveEnergy.drainSetUpTo(player, cost);
        if (paid < cost && config.wirelessFallback) paid += EquipmentEnergy.takeWireless(player, cost - paid);
        float absorbed = cost <= 0 ? cap : (float) (cap * Math.min(1, paid / cost));
        if (absorbed <= 0) return;
        if (source.isFireDamage()) player.extinguish();
        if (source == DamageSource.fall) {
            CelestweaveServerEvents.landFx(player, player.fallDistance);
        } else if (!environment(source)) {
            Entity attacker = source.getEntity();
            if (attacker == null) CelestweaveServerEvents
                .sendFx(player, CelestweaveFxPacket.SHIELD_AMBIENT, player.posX, player.posY + 1, player.posZ);
            else CelestweaveServerEvents.sendFx(
                player,
                CelestweaveFxPacket.SHIELD_HIT,
                attacker.posX,
                attacker.posY + attacker.height * 0.5,
                attacker.posZ);
        }
        if (incoming - absorbed > 0.01F) {
            PENDING.put(player, new Absorb(source, absorbed / incoming));
            return;
        }
        cancel(event, player);
        if (!invulnerable) player.hurtResistantTime = player.maxHurtResistantTime;
        player.lastDamage = amount;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void hurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayerMP player)) return;
        HANDLED.remove(player);
        Absorb absorb = PENDING.remove(player);
        if (absorb == null || absorb.source() != event.source) return;
        event.ammount = Math.max(0F, event.ammount * (1F - absorb.fraction()));
    }

    private static boolean reflect(EntityPlayerMP player, ItemStack chest, DamageSource source, float amount) {
        var config = MainConfig.item.celestweave;
        if (source.isProjectile() && CelestweaveModuleSetting.REFLECT_PROJECTILE.on(chest)
            && CelestweaveEnergy.drain(player, CelestweaveArmor.CHEST, chest, config.reflectEu)) {
            Entity projectile = source.getSourceOfDamage();
            if (projectile != null) CelestweaveServerEvents
                .sendFx(player, CelestweaveFxPacket.REFLECT, projectile.posX, projectile.posY, projectile.posZ);
            return true;
        }
        if (!(source.getEntity() instanceof EntityLivingBase attacker) || attacker == player
            || "thorns".equals(source.damageType)
            || LIGHTNING.equals(source.damageType)
            || !CelestweaveEnergy.drain(player, CelestweaveArmor.CHEST, chest, config.reflectEu)) return false;
        strike(player, chest, attacker, amount);
        return false;
    }

    private static void strike(EntityPlayerMP player, ItemStack chest, EntityLivingBase attacker, float amount) {
        var config = MainConfig.item.celestweave;
        double radius = config.reflectRadius;
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Object entry : player.worldObj
            .getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(radius, radius, radius))) {
            if (entry instanceof EntityLivingBase living && living != attacker
                && living instanceof IMob
                && !living.isDead
                && living.getDistanceSqToEntity(player) <= radius * radius) targets.add(living);
        }
        targets.sort(Comparator.comparingDouble(living -> living.getDistanceSqToEntity(player)));
        if (!attacker.isDead && attacker.getDistanceSqToEntity(player) <= radius * radius * 4) targets.add(0, attacker);
        if (targets.size() > config.maxReflectTargets) targets.subList(config.maxReflectTargets, targets.size())
            .clear();
        float damage = amount * config.reflectRatio;
        boolean ignite = CelestweaveModuleSetting.REFLECT_IGNITE.on(chest);
        boolean blind = CelestweaveModuleSetting.REFLECT_BLIND.on(chest);
        for (EntityLivingBase target : targets) {
            boolean hurt = damage <= 0
                || target.attackEntityFrom(new EntityDamageSource(LIGHTNING, player).setMagicDamage(), damage);
            if (!hurt && target instanceof EntityPlayer) continue;
            if (ignite) target.setFire(5);
            if (blind) {
                target.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
            }
            CelestweaveServerEvents
                .sendFx(player, CelestweaveFxPacket.LIGHTNING, target.posX, target.boundingBox.minY, target.posZ);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void death(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayerMP player)) return;
        DamageSource source = event.source;
        if (source == DamageSource.outOfWorld || bypass(source, Float.MAX_VALUE)) return;
        ItemStack chest = CelestweaveData.piece(player, CelestweaveArmor.CHEST);
        if (!shielded(chest) || !CelestweaveEnergy.drainWithWireless(player, MainConfig.item.celestweave.deathSaveEu))
            return;
        event.setCanceled(true);
        player.setHealth(1F);
        player.extinguish();
        player.hurtResistantTime = player.maxHurtResistantTime * 3;
        CelestweaveServerEvents.sendFx(player, CelestweaveFxPacket.SAVE, player.posX, player.posY + 1, player.posZ);
    }
}
