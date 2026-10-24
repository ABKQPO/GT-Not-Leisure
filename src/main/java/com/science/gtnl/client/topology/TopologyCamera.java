package com.science.gtnl.client.topology;

import net.minecraft.util.MathHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyCamera {

    public static final float MIN_ZOOM = 0.12F;
    public static final float MAX_ZOOM = 8.0F;
    private static final float DEFAULT_ZOOM = 0.85F;
    // Framing the whole graph must not shrink the nodes past the point where their icon is still recognisable.
    private static final float FIT_MIN_ZOOM = 0.45F;
    private static final float RESPONSE = 14.0F;

    private float x, y, zoom = DEFAULT_ZOOM;
    private float targetX, targetY, targetZoom = DEFAULT_ZOOM;

    public void advance(float delta) {
        float factor = 1.0F - (float) Math.exp(-RESPONSE * delta);
        x += (targetX - x) * factor;
        y += (targetY - y) * factor;
        zoom += (targetZoom - zoom) * factor;
        if (Math.abs(targetX - x) < 0.01F) x = targetX;
        if (Math.abs(targetY - y) < 0.01F) y = targetY;
        if (Math.abs(targetZoom - zoom) < 0.0005F) zoom = targetZoom;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float zoom() {
        return zoom;
    }

    public void pan(float screenDeltaX, float screenDeltaY) {
        targetX -= screenDeltaX / zoom;
        targetY -= screenDeltaY / zoom;
    }

    public void zoomBy(float step, float anchorScreenX, float anchorScreenY) {
        float before = targetZoom;
        targetZoom = MathHelper.clamp_float(targetZoom * step, MIN_ZOOM, MAX_ZOOM);
        if (targetZoom == before) return;
        targetX += anchorScreenX / before - anchorScreenX / targetZoom;
        targetY += anchorScreenY / before - anchorScreenY / targetZoom;
    }

    public void recenter() {
        targetX = 0;
        targetY = 0;
        targetZoom = DEFAULT_ZOOM;
    }

    public void focus(float graphX, float graphY) {
        targetX = graphX;
        targetY = graphY;
    }

    public void fit(float graphRadius, float viewWidth, float viewHeight) {
        targetX = 0;
        targetY = 0;
        if (graphRadius <= 1.0F) {
            targetZoom = DEFAULT_ZOOM;
            return;
        }
        // A fixed margin eats most of a Minecraft sized viewport, so it scales with the smaller edge.
        float margin = Math.min(48.0F, Math.min(viewWidth, viewHeight) * 0.12F);
        float available = Math.max(80.0F, Math.min(viewWidth, viewHeight) - margin * 2);
        targetZoom = MathHelper.clamp_float(available / (graphRadius * 2), FIT_MIN_ZOOM, MAX_ZOOM);
    }
}
