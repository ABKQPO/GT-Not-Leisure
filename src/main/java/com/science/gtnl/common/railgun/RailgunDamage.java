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

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;

public final class RailgunDamage {

    public record DamageContext(double firstDamage, double bypassRatio, double chainDecay, int chainSegments,
        int chainForkCount, double chainRadius, boolean isMaxCharged, boolean isBeam, boolean pvp) {}

    private RailgunDamage() {}

    private static boolean storm(World world, EntityPlayer player) {
        return world.isThundering() && world.canBlockSeeTheSky(
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ));
    }

    public static DamageContext buildBeam(EntityPlayer player, RailgunData.Modules modules, World world, boolean pvp,
        double base) {
        boolean storm = storm(world, player);
        int compute = modules.count(RailgunModuleType.COMPUTE);
        int chains = compute > 0 ? RailgunDefaults.CHAIN_BASE + (compute - 1) * 2 + (storm ? 2 : 0) : 0;
        return new DamageContext(
            base * (storm ? RailgunDefaults.STORM_DAMAGE_MUL : 1),
            MainConfig.item.railgun.beamBypass,
            RailgunDefaults.CHAIN_DECAY,
            Math.min(50, chains),
            compute > 0 ? 1 : 0,
            16 + (storm ? 4 : 0),
            false,
            true,
            pvp);
    }

    public static DamageContext buildCharged(EntityPlayer player, RailgunChargeTier tier, RailgunData.Modules modules,
        World world, boolean pvp) {
        var config = MainConfig.item.railgun;
        boolean storm = storm(world, player);
        int compute = modules.count(RailgunModuleType.COMPUTE);
        double base = switch (tier) {
            case EHV1 -> config.baseDamageEhv1;
            case EHV2 -> config.baseDamageEhv2;
            case EHV3 -> config.baseDamageEhv3;
            default -> 0;
        };
        int index = Math.max(0, tier.ordinal() - 1);
        int chains = (compute > 0 ? new int[] { 8, 14, 24 }[index] + Math.max(0, compute - 1) * 4 : 0)
            + (storm ? 2 : 0);
        int forks = (compute > 0 ? new int[] { 2, 3, 4 }[index] + Math.max(0, compute - 1) : 0) + (storm ? 1 : 0);
        return new DamageContext(
            base * (storm ? RailgunDefaults.STORM_DAMAGE_MUL : 1),
            config.chargedBypass,
            RailgunDefaults.CHAIN_DECAY,
            Math.min(50, chains),
            Math.min(8, forks),
            new int[] { 20, 26, 32 }[index] + (storm ? 4 : 0),
            tier == RailgunChargeTier.EHV3,
            false,
            pvp);
    }

    public static DamageSource source(EntityPlayer player) {
        return new EntityDamageSource("gtnl.railgun", player).setDamageBypassesArmor();
    }

    public static double finalDamage(double base, double bypass, double armor) {
        return Math.max(0, base) * (1 - Math.max(0, Math.min(0.8, armor)) * (1 - Math.max(0, Math.min(1, bypass))));
    }

    public static void applyAll(EntityPlayerMP player, List<RailgunChainResolver.Hit> hits, DamageContext context,
        boolean resetInvulnerability, RailgunVec reference) {
        for (var hit : hits) {
            EntityLivingBase target = hit.target();
            if (!RailgunTargetRules.canAffect(player, target, MainConfig.item.railgun.damagePlayers && context.pvp()))
                continue;
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
