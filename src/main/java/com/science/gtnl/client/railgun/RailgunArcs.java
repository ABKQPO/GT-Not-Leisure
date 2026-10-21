package com.science.gtnl.client.railgun;

import com.gtnllib.client.fx.world.FxArcRenderer;
import com.gtnllib.client.fx.world.FxArcRenderer.Style;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunArcs {

    private RailgunArcs() {}

    public static void chain(Vec3d from, Vec3d to, int lifetime, boolean ehv) {
        if (from == null || to == null || !from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(6, (int) Math.round(length * 1.5));
        double spread = Math.min(0.55, 0.08 + length * 0.018);
        spawn(
            from,
            to,
            segments,
            spread,
            lifetime,
            ehv ? RailgunOrbRenderer.PLASMA_ARC : RailgunOrbRenderer.HIGH_VOLTAGE_ARC);
    }

    public static void plasma(Vec3d from, Vec3d to, int segments, double spread, int lifetime) {
        spawn(from, to, segments, spread, lifetime, RailgunOrbRenderer.PLASMA_ARC);
    }

    public static void impactSpark(Vec3d from, Vec3d to, int lifetime) {
        if (from == null || to == null || !from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(4, Math.min(14, (int) Math.round(length * 2.5)));
        double spread = Math.min(0.35, 0.06 + length * 0.04);
        spawn(from, to, segments, spread, lifetime, RailgunOrbRenderer.IMPACT_SPARK_ARC);
    }

    private static void spawn(Vec3d from, Vec3d to, int segments, double spread, int lifetime, Style style) {
        FxArcRenderer.spawn(from, to, segments, spread, lifetime, style, MainConfig.client.enableRailgunBloom);
    }
}
