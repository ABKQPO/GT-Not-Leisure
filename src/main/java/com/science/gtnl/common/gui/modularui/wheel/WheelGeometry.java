package com.science.gtnl.common.gui.modularui.wheel;

import com.science.gtnl.client.fx.FxAngle;

public class WheelGeometry {

    private WheelGeometry() {}

    public static float slotAngle(int index, int count) {
        return FxAngle.TAU * index / count;
    }

    public static int slotAt(float angle, int count) {
        if (count <= 0) return -1;
        float step = FxAngle.TAU / count;
        return (int) Math.floor(FxAngle.normalizePositive(angle + step / 2) / step) % count;
    }
}
