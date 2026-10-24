package com.science.gtnl.client.topology;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Flat control surfaces for the chrome that floats on the star map.
 * <p>
 * Those controls carry no vanilla plate: a textured panel over the canvas hides the graph and fights the dark ground.
 * They are drawn as tinted vector pills instead, which keeps the map readable underneath while still showing hover
 * and latched state. The console is a real panel and keeps the themed vanilla look.
 */
@SideOnly(Side.CLIENT)
public class TopologyChrome {

    private static final float IDLE_ALPHA = 0.55F;
    private static final float HOVER_ALPHA = 0.85F;
    private static final float ACTIVE_ALPHA = 0.9F;
    private static final float OUTLINE_ALPHA = 0.7F;
    private static final float OUTLINE_WEIGHT = 1.0F;

    private TopologyChrome() {}

    public static void pill(TopologyDraw draw, float x, float y, float width, float height, float corner,
        boolean hovered, boolean active) {
        if (width <= 0 || height <= 0) return;
        int fill = active ? TopologyPalette.withAlpha(TopologyPalette.CHROME_ACTIVE, ACTIVE_ALPHA)
            : TopologyPalette.withAlpha(TopologyPalette.CHROME_FILL, hovered ? HOVER_ALPHA : IDLE_ALPHA);
        draw.roundedRect(x, y, x + width, y + height, corner, fill);
        if (!hovered && !active) return;
        draw.roundedOutline(
            x,
            y,
            x + width,
            y + height,
            corner,
            OUTLINE_WEIGHT,
            TopologyPalette.withAlpha(active ? TopologyPalette.ACCENT : TopologyPalette.SPOKE, OUTLINE_ALPHA));
    }
}
