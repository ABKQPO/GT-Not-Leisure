package com.science.gtnl.client.railgun;

import com.science.gtnl.client.fx.world.FxArcRenderer;
import com.science.gtnl.client.fx.world.FxArcRenderer.Color;
import com.science.gtnl.client.fx.world.FxArcRenderer.Style;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.config.MainConfig;

public class RailgunArcs {

    private static final Style PLASMA = new Style(
        new Color(1F, 0.3F, 0.6F),
        new Color(1F, 0.55F, 0.75F),
        new Color(1F, 0.8F, 0.9F),
        new Color(1F, 0.38F, 0.68F),
        new Color(1F, 0.75F, 0.88F),
        1);
    private static final Style HIGH_VOLTAGE = new Style(
        new Color(0.25F, 0.75F, 1F),
        new Color(0.55F, 0.88F, 1F),
        new Color(0.85F, 0.97F, 1F),
        new Color(0.4F, 0.82F, 1F),
        new Color(0.7F, 0.94F, 1F),
        1);
    private static final Style IMPACT_SPARK = new Style(
        new Color(0.9F, 0.25F, 0.55F),
        new Color(0.95F, 0.535F, 0.715F),
        new Color(1F, 0.82F, 0.88F),
        new Color(0.95F, 0.33F, 0.63F),
        new Color(1F, 0.76F, 0.86F),
        0.8125);

    private RailgunArcs() {}

    public static void chain(EquipmentVec from, EquipmentVec to, int lifetime, boolean ehv) {
        if (from == null || to == null || !from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(6, (int) Math.round(length * 1.5));
        double spread = Math.min(0.55, 0.08 + length * 0.018);
        spawn(from, to, segments, spread, lifetime, ehv ? PLASMA : HIGH_VOLTAGE);
    }

    public static void plasma(EquipmentVec from, EquipmentVec to, int segments, double spread, int lifetime) {
        spawn(from, to, segments, spread, lifetime, PLASMA);
    }

    public static void impactSpark(EquipmentVec from, EquipmentVec to, int lifetime) {
        if (from == null || to == null || !from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(4, Math.min(14, (int) Math.round(length * 2.5)));
        double spread = Math.min(0.35, 0.06 + length * 0.04);
        spawn(from, to, segments, spread, lifetime, IMPACT_SPARK);
    }

    private static void spawn(EquipmentVec from, EquipmentVec to, int segments, double spread, int lifetime,
        Style style) {
        FxArcRenderer.spawn(from, to, segments, spread, lifetime, style, MainConfig.client.enableRailgunBloom);
    }
}
