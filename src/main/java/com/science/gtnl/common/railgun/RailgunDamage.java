package com.science.gtnl.common.railgun;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;

public final class RailgunDamage {

    public record DamageContext(double firstDamage, double bypassRatio, double chainDecay, int chainSegments,
        int chainForkCount, double chainRadius, boolean isMaxCharged, boolean pvp) {}

    static boolean striking;

    private RailgunDamage() {}

    private static boolean storm(World world, EntityPlayer player) {
        return world.isThundering() && world.canBlockSeeTheSky(
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ));
    }

    private static double stormRadius(double radius, boolean storm) {
        return radius + (storm ? RailgunDefaults.STORM_CHAIN_RADIUS_BONUS : 0);
    }

    public static DamageContext buildBeam(EntityPlayer player, RailgunData.Modules modules, World world, boolean pvp,
        double base) {
        boolean storm = storm(world, player);
        int compute = modules.count(RailgunModuleType.COMPUTE);
        int chains = compute > 0
            ? RailgunDefaults.CHAIN_BASE + (compute - 1) * 2 + (storm ? RailgunDefaults.STORM_CHAIN_BONUS : 0)
            : 0;
        return new DamageContext(
            base * (storm ? RailgunDefaults.STORM_DAMAGE_MUL : 1),
            MainConfig.item.railgun.beamBypass,
            RailgunDefaults.CHAIN_DECAY,
            Math.min(RailgunDefaults.CHAIN_HARD_CAP, chains),
            compute > 0 ? 1 : 0,
            stormRadius(RailgunDefaults.CHAIN_RADIUS, storm),
            false,
            pvp);
    }

    public static DamageContext buildCharged(EntityPlayer player, RailgunChargeTier tier, RailgunData.Modules modules,
        World world, boolean pvp) {
        boolean storm = storm(world, player);
        int compute = modules.count(RailgunModuleType.COMPUTE);
        int chains = (compute > 0 ? tier.chainSegments + (compute - 1) * 4 : 0)
            + (storm ? RailgunDefaults.STORM_CHAIN_BONUS : 0);
        int forks = (compute > 0 ? tier.chainForks + compute - 1 : 0) + (storm ? 1 : 0);
        return new DamageContext(
            tier.baseDamage() * (storm ? RailgunDefaults.STORM_DAMAGE_MUL : 1),
            MainConfig.item.railgun.chargedBypass,
            RailgunDefaults.CHAIN_DECAY,
            Math.min(RailgunDefaults.CHAIN_HARD_CAP, chains),
            Math.min(RailgunDefaults.CHAIN_FORK_HARD_CAP, forks),
            stormRadius(tier.chainRadius, storm),
            tier == RailgunChargeTier.EHV3,
            pvp);
    }

    public static DamageSource source(EntityPlayer player) {
        return new EntityDamageSource("gtnl.railgun", player).setDamageBypassesArmor();
    }

    public static void hitObject(EntityPlayerMP player, Entity target, EquipmentVec reference, double damage) {
        striking = true;
        try {
            if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) return;
        } finally {
            striking = false;
        }
        RailgunTargetRules.damageAnchor(target, reference)
            .attackEntityFrom(source(player), (float) Math.min(damage, Float.MAX_VALUE));
    }

    public static double finalDamage(double base, double bypass, double armor) {
        return Math.max(0, base) * (1 - Math.max(0, Math.min(0.8, armor)) * (1 - Math.max(0, Math.min(1, bypass))));
    }

    public static void applyAll(EntityPlayerMP player, List<RailgunChainResolver.Hit> hits, DamageContext context,
        boolean resetInvulnerability, EquipmentVec reference) {
        for (var hit : hits) {
            EntityLivingBase target = hit.target();
            if (!RailgunTargetRules.canAffect(player, target, context.pvp())) continue;
            double damage = finalDamage(hit.damage(), context.bypassRatio(), target.getTotalArmorValue() / 25.0);
            if (!Double.isFinite(damage) || damage <= 0) continue;
            if (resetInvulnerability) target.hurtResistantTime = 0;
            Entity anchor = RailgunTargetRules.damageAnchor(target, reference);
            boolean applied = anchor.attackEntityFrom(source(player), (float) Math.min(damage, Float.MAX_VALUE));
            if (applied && target.isEntityAlive()
                && (!(target instanceof EntityPlayer) || MainConfig.item.railgun.paralysisOnPlayers))
                target.addPotionEffect(
                    new PotionEffect(
                        EffectLoader.electromagneticParalysis.id,
                        RailgunDefaults.PARALYSIS_DURATION_TICKS,
                        0));
        }
    }
}
