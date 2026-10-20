package com.science.gtnl.client.hud;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import org.lwjgl.opengl.GL11;

public class HudElement {

    public static final String DEFAULT_LANG_PREFIX = "gtnl.hud.element.";

    public final String id;
    public final float anchorX, anchorY, minX, minY, maxX, maxY;
    private final Offset defaultX, defaultY;
    private final String lang;
    private final BooleanSupplier inUse, visible;
    private final DoubleSupplier baseScale;
    private final Drawer preview, renderer;

    private HudElement(Builder builder) {
        this.id = builder.id;
        this.anchorX = builder.anchorX;
        this.anchorY = builder.anchorY;
        this.minX = builder.minX;
        this.minY = builder.minY;
        this.maxX = builder.maxX;
        this.maxY = builder.maxY;
        this.defaultX = builder.defaultX;
        this.defaultY = builder.defaultY;
        this.lang = builder.lang != null ? builder.lang : DEFAULT_LANG_PREFIX + builder.id;
        this.inUse = builder.inUse;
        this.visible = builder.visible;
        this.baseScale = builder.baseScale;
        this.preview = builder.preview;
        this.renderer = builder.renderer;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String lang() {
        return lang;
    }

    public boolean isInUse() {
        return inUse.getAsBoolean();
    }

    public boolean isVisible() {
        return visible.getAsBoolean();
    }

    public float baseScale() {
        return validScale(baseScale.getAsDouble());
    }

    static float validScale(double scale) {
        return Double.isFinite(scale) && scale > 0 ? (float) scale : 1F;
    }

    public boolean hasRenderer() {
        return renderer != null;
    }

    float defaultX(float scale) {
        return defaultX.at(scale);
    }

    float defaultY(float scale) {
        return defaultY.at(scale);
    }

    void preview(HudLayout.Placement place, float partialTicks) {
        draw(preview, place, partialTicks);
    }

    void render(HudLayout.Placement place, float partialTicks) {
        draw(renderer, place, partialTicks);
    }

    private static void draw(Drawer drawer, HudLayout.Placement place, float partialTicks) {
        if (drawer == null) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        try {
            drawer.draw(place, partialTicks);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    public static class Builder {

        private final String id;
        private float anchorX = 0.5F, anchorY = 0.5F, minX, minY, maxX, maxY;
        private Offset defaultX = Offset.NONE, defaultY = Offset.NONE;
        private String lang;
        private BooleanSupplier inUse = () -> true, visible = () -> true;
        private DoubleSupplier baseScale = () -> 1;
        private Drawer preview, renderer;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id, "id");
            if (id.isEmpty()) throw new IllegalArgumentException("HUD element id must not be empty");
        }

        public Builder anchor(float x, float y) {
            this.anchorX = x;
            this.anchorY = y;
            return this;
        }

        public Builder bounds(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            return this;
        }

        public Builder offset(Offset x, Offset y) {
            this.defaultX = Objects.requireNonNull(x, "x");
            this.defaultY = Objects.requireNonNull(y, "y");
            return this;
        }

        public Builder lang(String key) {
            this.lang = Objects.requireNonNull(key, "key");
            return this;
        }

        public Builder inUseWhen(BooleanSupplier condition) {
            this.inUse = Objects.requireNonNull(condition, "condition");
            return this;
        }

        public Builder visibleWhen(BooleanSupplier condition) {
            this.visible = Objects.requireNonNull(condition, "condition");
            return this;
        }

        public Builder baseScale(DoubleSupplier scale) {
            this.baseScale = Objects.requireNonNull(scale, "scale");
            return this;
        }

        public Builder preview(Drawer drawer) {
            this.preview = Objects.requireNonNull(drawer, "drawer");
            return this;
        }

        public Builder render(Drawer drawer) {
            this.renderer = Objects.requireNonNull(drawer, "drawer");
            return this;
        }

        public HudElement build() {
            if (!(maxX > minX && maxY > minY))
                throw new IllegalStateException("HUD element " + id + " needs bounds with max greater than min");
            return new HudElement(this);
        }
    }

    @FunctionalInterface
    public interface Offset {

        Offset NONE = scale -> 0F;

        /**
         * Offset of the element from its anchor before the player moves it, in GUI pixels, for the given scale.
         */
        float at(float scale);
    }

    @FunctionalInterface
    public interface Drawer {

        /**
         * Draws the element around {@code place.x(), place.y()} in GUI pixels, the point its bounds are measured from;
         * the drawer applies {@code place.scale()} itself. The matrix, colour and enable/blend states are restored
         * afterwards, so the drawer may translate, scale and change them freely.
         */
        void draw(HudLayout.Placement place, float partialTicks);
    }
}
