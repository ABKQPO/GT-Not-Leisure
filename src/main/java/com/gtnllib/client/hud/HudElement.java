package com.gtnllib.client.hud;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import org.lwjgl.opengl.GL11;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class HudElement {

    public static final String DEFAULT_LANG_PREFIX = "gtnllib.hud.element.";
    public static final Choice STANDARD_STYLE = new Choice("standard", "gtnllib.hud.style.standard");
    public static final float DEFAULT_IDLE_SECONDS = 3F, DEFAULT_IDLE_ALPHA = 0F;

    public final String id;
    public final float anchorX, anchorY, minX, minY, maxX, maxY;
    private final Offset defaultX, defaultY;
    private final String lang;
    private final BooleanSupplier inUse, active;
    private final IntSupplier signature;
    private final DoubleSupplier baseScale;
    private final List<Choice> styles;
    private final List<HudOption> options;
    private final HudColor color;
    private final HudTiming timing;
    private final float idleSeconds, idleAlpha;
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
        this.active = builder.active;
        this.signature = builder.signature;
        this.baseScale = builder.baseScale;
        this.styles = Collections.unmodifiableList(new ObjectArrayList<>(builder.styles));
        this.options = Collections.unmodifiableList(new ObjectArrayList<>(builder.options));
        this.color = builder.color;
        this.timing = builder.timing;
        this.idleSeconds = builder.idleSeconds;
        this.idleAlpha = builder.idleAlpha;
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

    public boolean isActive() {
        return active.getAsBoolean();
    }

    public int signature() {
        return signature.getAsInt();
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

    public List<Choice> styles() {
        return styles;
    }

    public Choice style(String id) {
        for (Choice style : styles) if (style.id.equals(id)) return style;
        return styles.get(0);
    }

    public List<HudOption> options() {
        return options;
    }

    public HudOption option(String id) {
        for (HudOption option : options) if (option.id.equals(id)) return option;
        throw new IllegalArgumentException("HUD element " + this.id + " has no option " + id);
    }

    public boolean colorable() {
        return color != null;
    }

    public HudColor defaultColor() {
        return color;
    }

    public HudTiming defaultTiming() {
        return timing;
    }

    public float defaultIdleSeconds() {
        return idleSeconds;
    }

    public float defaultIdleAlpha() {
        return idleAlpha;
    }

    public List<Setting> settings(HudTiming current) {
        List<Setting> settings = new ObjectArrayList<>();
        Collections.addAll(settings, Setting.SHOWN, Setting.SCALE, Setting.OPACITY);
        if (colorable()) settings.add(Setting.COLOR);
        settings.add(Setting.TIMING);
        if (current == HudTiming.CHANGED) Collections.addAll(settings, Setting.IDLE_SECONDS, Setting.IDLE_ALPHA);
        if (styles.size() > 1) settings.add(Setting.STYLE);
        return settings;
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

    public record Choice(String id, String lang) {

        public Choice {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(lang, "lang");
            if (id.isEmpty()) throw new IllegalArgumentException("HUD choice id must not be empty");
        }
    }

    public enum Setting {

        SHOWN,
        SCALE,
        OPACITY,
        COLOR,
        TIMING,
        IDLE_SECONDS,
        IDLE_ALPHA,
        STYLE;

        private static final String LANG_PREFIX = "gtnllib.hud.setting.";

        public String lang() {
            return LANG_PREFIX + HudLayout.id(this);
        }
    }

    public static class Builder {

        private final String id;
        private float anchorX = 0.5F, anchorY = 0.5F, minX, minY, maxX, maxY;
        private Offset defaultX = Offset.NONE, defaultY = Offset.NONE;
        private String lang;
        private BooleanSupplier inUse = () -> true, active = () -> true;
        private IntSupplier signature = () -> 0;
        private DoubleSupplier baseScale = () -> 1;
        private final List<Choice> styles = new ObjectArrayList<>();
        private final List<HudOption> options = new ObjectArrayList<>();
        private HudColor color;
        private HudTiming timing = HudTiming.ALWAYS;
        private float idleSeconds = DEFAULT_IDLE_SECONDS, idleAlpha = DEFAULT_IDLE_ALPHA;
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

        public Builder activeWhen(BooleanSupplier condition) {
            this.active = Objects.requireNonNull(condition, "condition");
            return this;
        }

        public Builder changeWhen(IntSupplier signature) {
            this.signature = Objects.requireNonNull(signature, "signature");
            return this;
        }

        public Builder baseScale(DoubleSupplier scale) {
            this.baseScale = Objects.requireNonNull(scale, "scale");
            return this;
        }

        public Builder style(Choice style) {
            styles.add(Objects.requireNonNull(style, "style"));
            return this;
        }

        public Builder style(String id, String lang) {
            return style(new Choice(id, lang));
        }

        public Builder option(HudOption option) {
            options.add(Objects.requireNonNull(option, "option"));
            return this;
        }

        public Builder color(HudColor fallback) {
            this.color = Objects.requireNonNull(fallback, "fallback");
            return this;
        }

        public Builder timing(HudTiming fallback) {
            this.timing = Objects.requireNonNull(fallback, "fallback");
            return this;
        }

        public Builder idle(float seconds, float alpha) {
            if (!(seconds >= 0 && seconds <= HudLayout.MAX_IDLE_SECONDS) || !(alpha >= 0 && alpha <= 1))
                throw new IllegalArgumentException("Bad HUD idle " + seconds + "s at " + alpha + " for " + id);
            this.idleSeconds = seconds;
            this.idleAlpha = alpha;
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
            if (styles.isEmpty()) throw new IllegalStateException("HUD element " + id + " needs at least one style");
            Set<String> styleIds = new ObjectOpenHashSet<>(), optionIds = new ObjectOpenHashSet<>();
            for (Choice style : styles) if (!styleIds.add(style.id))
                throw new IllegalStateException("HUD element " + id + " repeats style " + style.id);
            for (HudOption option : options) if (!optionIds.add(option.id))
                throw new IllegalStateException("HUD element " + id + " repeats option " + option.id);
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
         * the drawer applies {@code place.scale()} itself and multiplies every alpha it draws by {@code place.alpha()}.
         * {@code place.color()}, {@code place.style()} and the option readers carry the player's settings. When
         * {@code place.resting()} is true the player keeps the element on screen while nothing is going on, so the
         * drawer shows its resting look instead of nothing. The matrix, colour and enable/blend states are restored
         * afterwards, so the drawer may translate, scale and change them freely.
         */
        void draw(HudLayout.Placement place, float partialTicks);
    }
}
