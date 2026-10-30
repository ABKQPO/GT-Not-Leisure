package com.science.gtnl.common.render;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SpoceEffect {

    public static final List<SpoceEffect> effects = new ObjectArrayList<>();

    public double x, y, z;
    public float rotSpeed;
    public float globalScale;
    public List<Layer> layers = new ObjectArrayList<>();
    public boolean removed = false;

    private float lastAnimProgress = 0;
    private float animProgress = 0;
    private int life = -1;

    public SpoceEffect(double x, double y, double z, float speed, float scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotSpeed = speed;
        this.globalScale = scale;
    }

    public void addLayer(double radius, int color, boolean renderLines) {
        layers.add(new Layer(radius, color, renderLines));
    }

    public void remove() {
        removed = true;
    }

    public SpoceEffect setLife(int life) {
        this.life = life;
        return this;
    }

    public void update() {
        lastAnimProgress = animProgress;
        if (animProgress < 1.0f) {
            animProgress = Math.min(animProgress + 0.03f, 1.0f);
        }

        if (life > 0) life--;
        if (life == 0) removed = true;
    }

    public float getInterp(float partial) {
        float linearT = lastAnimProgress + (animProgress - lastAnimProgress) * partial;
        return 1.0f - (float) Math.pow(1.0 - linearT, 3);
    }

    public static SpoceEffect addEffect(SpoceEffect effect) {
        synchronized (effects) {
            effects.add(effect);
        }
        return effect;
    }

    public static void clearAll() {
        synchronized (effects) {
            effects.clear();
        }
    }

    public static class Layer {

        public double radius;
        public float a, r, g, b;
        public boolean renderLines;
        public float[] cachedVerts = new float[0];
        public boolean dirty = true;

        public Layer(double radius, int color, boolean renderLines) {
            this.radius = radius;
            this.renderLines = renderLines;
            this.a = ((color >> 24) & 0xFF) == 0 ? 0.5f : ((color >> 24) & 0xFF) / 255f;
            this.r = ((color >> 16) & 0xFF) / 255f;
            this.g = ((color >> 8) & 0xFF) / 255f;
            this.b = (color & 0xFF) / 255f;
        }
    }
}
