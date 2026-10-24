package com.science.gtnl.common.railgun;

import com.science.gtnl.config.MainConfig;

public enum RailgunChargeTier {

    NONE(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    EHV1(10, 5.5, 0.45, 0.6, 6, 4, 5, 32, 8, 2, 20),
    EHV2(24, 8, 0.55, 1.2, 12, 6, 25, 96, 14, 3, 26),
    EHV3(40, 12, 0.65, 2, 20, 10, 50, 256, 24, 4, 32);

    public final int chargeTicks;
    public final double impactRadius;
    public final double impactRatio;
    public final double recoilSpeed;
    public final float recoilPitch;
    public final double terrainRadius;
    public final double terrainHardness;
    public final int lightning;
    public final int chainSegments;
    public final int chainForks;
    public final double chainRadius;

    RailgunChargeTier(int chargeTicks, double impactRadius, double impactRatio, double recoilSpeed, float recoilPitch,
        double terrainRadius, double terrainHardness, int lightning, int chainSegments, int chainForks,
        double chainRadius) {
        this.chargeTicks = chargeTicks;
        this.impactRadius = impactRadius;
        this.impactRatio = impactRatio;
        this.recoilSpeed = recoilSpeed;
        this.recoilPitch = recoilPitch;
        this.terrainRadius = terrainRadius;
        this.terrainHardness = terrainHardness;
        this.lightning = lightning;
        this.chainSegments = chainSegments;
        this.chainForks = chainForks;
        this.chainRadius = chainRadius;
    }

    public double baseDamage() {
        var config = MainConfig.item.railgun;
        return switch (this) {
            case EHV1 -> config.baseDamageEhv1;
            case EHV2 -> config.baseDamageEhv2;
            case EHV3 -> config.baseDamageEhv3;
            default -> 0;
        };
    }

    public double shotEu() {
        var config = MainConfig.item.railgun;
        return switch (this) {
            case EHV1 -> config.shotEuEhv1;
            case EHV2 -> config.shotEuEhv2;
            case EHV3 -> config.shotEuEhv3;
            default -> 0;
        };
    }

    public static RailgunChargeTier fromUnits(int units) {
        return units >= EHV3.chargeTicks ? EHV3
            : units >= EHV2.chargeTicks ? EHV2 : units >= EHV1.chargeTicks ? EHV1 : NONE;
    }

    public static RailgunChargeTier byOrdinal(int ordinal) {
        RailgunChargeTier[] tiers = values();
        return ordinal >= 0 && ordinal < tiers.length ? tiers[ordinal] : NONE;
    }
}
