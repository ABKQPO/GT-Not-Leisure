package com.science.gtnl.common.railgun;

public enum RailgunChargeTier {

    NONE(0, 0, 0, 0, 0, 0, 0, 0),
    EHV1(10, 5.5, 0.45, 0.6, 6, 4, 5, 32),
    EHV2(24, 8, 0.55, 1.2, 12, 6, 25, 96),
    EHV3(40, 12, 0.65, 2, 20, 10, 50, 256);

    public final int chargeTicks;
    public final double impactRadius;
    public final double impactRatio;
    public final double recoilSpeed;
    public final float recoilPitch;
    public final double terrainRadius;
    public final double terrainHardness;
    public final int lightning;

    RailgunChargeTier(int chargeTicks, double impactRadius, double impactRatio, double recoilSpeed, float recoilPitch,
        double terrainRadius, double terrainHardness, int lightning) {
        this.chargeTicks = chargeTicks;
        this.impactRadius = impactRadius;
        this.impactRatio = impactRatio;
        this.recoilSpeed = recoilSpeed;
        this.recoilPitch = recoilPitch;
        this.terrainRadius = terrainRadius;
        this.terrainHardness = terrainHardness;
        this.lightning = lightning;
    }

    public static RailgunChargeTier fromUnits(int units) {
        return units >= 40 ? EHV3 : units >= 24 ? EHV2 : units >= 10 ? EHV1 : NONE;
    }
}
