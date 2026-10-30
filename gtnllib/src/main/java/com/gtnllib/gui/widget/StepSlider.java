package com.gtnllib.gui.widget;

import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.value.IDoubleValue;
import com.cleanroommc.modularui.api.value.IIntValue;
import com.cleanroommc.modularui.api.widget.IGuiAction;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.cleanroommc.modularui.widget.Widget;
import com.google.gson.JsonObject;
import com.gtnllib.utils.ColorMath;

public class StepSlider extends Widget<StepSlider> implements Interactable {

    private static final ResourceLocation TICK_SOUND = new ResourceLocation("random.click");
    private static final float LOW_PITCH = 1.15F, HIGH_PITCH = 1.5F;
    private static final int TRACK_THICKNESS = 4, TICK_GAP = 2, MINOR_TICK = 1, MAJOR_TICK = 3, MIN_TICK_SPACING = 2;
    private static final long SOUND_GAP_MILLIS = 40;
    private static final String COMPACT_CHARS = "0123456789%.-+×s";
    private static final int[] COMPACT_GLYPHS = { 0b111_101_101_101_111, 0b010_110_010_010_111, 0b111_001_111_100_111,
        0b111_001_111_001_111, 0b101_101_111_001_001, 0b111_100_111_001_111, 0b111_100_111_101_111,
        0b111_001_001_010_010, 0b111_101_111_101_111, 0b111_101_111_001_111, 0b101_001_010_100_101,
        0b000_000_000_000_100, 0b000_000_111_000_000, 0b000_010_111_010_000, 0b000_101_010_101_000,
        0b000_011_100_001_110 };
    private static final int COMPACT_HEIGHT = 5, COMPACT_TAG_HEIGHT = 9, COMPACT_TAG_PADDING = 2;
    private static final int INACTIVE_FILL = 0xFF4A4A50, INACTIVE_TAG = 0xFF707078, INACTIVE_TEXT = 0xFF2A2A2E;

    private DoubleSupplier getter = () -> 0;
    private DoubleConsumer setter = value -> {};
    private DoubleFunction<String> format = value -> String.valueOf(Math.round(value * 100) / 100D);
    private BooleanSupplier active = () -> true;
    private Runnable onRelease;
    private double min = 0, max = 1;
    private int steps = 10, milestoneEvery = 5;
    private int[] colors, tickColors;
    private int tagWidth;
    private boolean vertical, dragging;
    private long lastSound;

    public StepSlider() {
        disableThemeBackground(true);
        disableHoverThemeBackground(true);
        listenGuiAction((IGuiAction.MouseReleased) button -> {
            if (dragging) release();
            return false;
        });
    }

    public StepSlider value(DoubleSupplier getter, DoubleConsumer setter) {
        this.getter = getter;
        this.setter = setter;
        return this;
    }

    public StepSlider value(IDoubleValue<?> value) {
        if (value instanceof IIntValue<?>integer)
            return value(value::getDoubleValue, number -> integer.setIntValue((int) Math.round(number)));
        return value(value::getDoubleValue, value::setDoubleValue);
    }

    public StepSlider range(double min, double max, int steps) {
        if (!(max > min) || steps < 1) throw new IllegalArgumentException("Bad slider range " + min + ".." + max);
        this.min = min;
        this.max = max;
        this.steps = steps;
        return this;
    }

    public StepSlider milestones(int every) {
        this.milestoneEvery = Math.max(0, every);
        return this;
    }

    public StepSlider format(DoubleFunction<String> format) {
        this.format = format;
        return this;
    }

    public StepSlider activeWhen(BooleanSupplier active) {
        this.active = active;
        return this;
    }

    public StepSlider onRelease(Runnable onRelease) {
        this.onRelease = onRelease;
        return this;
    }

    public StepSlider vertical(boolean vertical) {
        this.vertical = vertical;
        return this;
    }

    public StepSlider colors(int track, int fill, int cursor, int cursorText) {
        this.colors = new int[] { track, fill, cursor, cursorText };
        return this;
    }

    public StepSlider tagWidth(int width) {
        this.tagWidth = Math.max(0, width);
        return this;
    }

    public StepSlider tickColors(int tick, int milestone) {
        this.tickColors = new int[] { tick, milestone };
        return this;
    }

    public int step() {
        long step = Math.round((getter.getAsDouble() - min) / (max - min) * steps);
        return (int) Math.max(0, Math.min(steps, step));
    }

    public double valueAt(int step) {
        return min + (max - min) * Math.max(0, Math.min(steps, step)) / steps;
    }

    public static float notchPosition(int step, int steps, int length, boolean vertical) {
        float along = (length - 3F) * Math.max(0, Math.min(steps, step)) / steps;
        return vertical ? length - 2F - along : 1F + along;
    }

    public static int nearestStep(int mouse, int steps, int length, boolean vertical) {
        float along = vertical ? length - 2F - mouse : mouse - 1F;
        return Math.max(0, Math.min(steps, Math.round(along / Math.max(1F, length - 3F) * steps)));
    }

    private int length() {
        return vertical ? getArea().h() : getArea().w();
    }

    private int thickness() {
        return vertical ? getArea().w() : getArea().h();
    }

    private int mouse(ModularGuiContext context) {
        return vertical ? context.getMouseY() : context.getMouseX();
    }

    private int stepAt(int mouse) {
        int step = nearestStep(mouse, steps, length(), vertical);
        if (milestoneEvery > 0 && Interactable.hasShiftDown())
            step = Math.min(steps, Math.round(step / (float) milestoneEvery) * milestoneEvery);
        return step;
    }

    private boolean milestone(int step) {
        return milestoneEvery > 0 && (step % milestoneEvery == 0 || step == steps);
    }

    private boolean select(int step) {
        step = Math.max(0, Math.min(steps, step));
        if (Math.abs(valueAt(step) - getter.getAsDouble()) < (max - min) / steps * 1.0E-3) return false;
        setter.accept(valueAt(step));
        long now = Minecraft.getSystemTime();
        if (now - lastSound < SOUND_GAP_MILLIS) return true;
        lastSound = now;
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(
                PositionedSoundRecord
                    .func_147674_a(TICK_SOUND, LOW_PITCH + (HIGH_PITCH - LOW_PITCH) * step / (float) steps));
        return true;
    }

    private void release() {
        dragging = false;
        if (onRelease != null) onRelease.run();
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (mouseButton != 0 || !active.getAsBoolean()) return Result.IGNORE;
        dragging = true;
        select(stepAt(mouse(getContext())));
        return Result.SUCCESS;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        if (dragging) select(stepAt(mouse(getContext())));
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        if (!active.getAsBoolean()) return false;
        int notches = milestoneEvery > 0 && Interactable.hasShiftDown() ? milestoneEvery : 1;
        if (select(step() + (scrollDirection.isUp() ? notches : -notches)) && onRelease != null) onRelease.run();
        return true;
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        WidgetThemeEntry<Theme> entry = theme.getWidgetTheme(Theme.KEY);
        return entry != null ? entry : theme.getFallback();
    }

    private Theme resolveColors(WidgetTheme theme) {
        Theme base = theme instanceof Theme slider ? slider : Theme.KEY.getDefaultValue();
        if (colors == null && tickColors == null) return base;
        int[] main = colors != null ? colors : new int[] { base.track, base.fill, base.cursor, base.cursorText };
        int[] ticks = tickColors != null ? tickColors : new int[] { base.tick, base.milestone };
        return new Theme(
            main[0],
            main[1],
            main[2],
            main[3],
            ticks[0],
            ticks[1],
            base.trackSkin,
            base.fillSkin,
            base.tagSkin);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        Theme palette = resolveColors(widgetTheme.getTheme());
        int length = length(), thickness = thickness(), current = step();
        boolean on = active.getAsBoolean();
        float trackStart = Math.round((thickness - TRACK_THICKNESS) / 2F);
        float cursor = notchPosition(current, steps, length, vertical);
        float origin = notchPosition(0, steps, length, vertical);
        int fill = on ? palette.fill : INACTIVE_FILL, light = shade(fill, 1.35F), edge = shade(palette.track, 0.6F);
        float fillStart = Math.min(origin, cursor), fillLength = Math.abs(cursor - origin) + 1;
        if (vertical || palette.trackSkin == null) {
            bar(0, trackStart - 1, length, TRACK_THICKNESS + 2, edge, edge);
            bar(1, trackStart, length - 2, TRACK_THICKNESS, palette.track, palette.track);
        } else palette.trackSkin.draw(0, (int) trackStart - 1, length, TRACK_THICKNESS + 2, 0);
        if (vertical || palette.fillSkin == null) bar(fillStart, trackStart, fillLength, TRACK_THICKNESS, light, fill);
        else palette.fillSkin
            .draw(Math.round(fillStart), (int) trackStart, Math.round(fillLength), TRACK_THICKNESS, fill);
        float tickStart = trackStart + TRACK_THICKNESS + TICK_GAP;
        boolean minorTicks = (length - 3F) / steps >= MIN_TICK_SPACING;
        for (int step = 0; step <= steps; step++) {
            boolean major = milestone(step);
            if (!major && !minorTicks) continue;
            int color = step <= current && on ? light : major ? palette.milestone : palette.tick;
            bar(
                notchPosition(step, steps, length, vertical),
                tickStart,
                1,
                major ? MAJOR_TICK : MINOR_TICK,
                color,
                color);
        }
        if (on && isHovering() && !dragging) {
            int hovered = stepAt(mouse(context));
            int ghost = shade(palette.cursor, 0.9F);
            if (hovered != current) bar(
                notchPosition(hovered, steps, length, vertical),
                trackStart - 1,
                1,
                TRACK_THICKNESS + 2,
                ghost,
                ghost);
        }
        drawTag(format.apply(valueAt(current)), cursor, trackStart + TRACK_THICKNESS / 2F, on, palette);
    }

    private void bar(float along, float across, float alongSize, float acrossSize, int acrossStart, int acrossEnd) {
        if (vertical)
            GuiDraw.drawRect(across, along, acrossSize, alongSize, acrossStart, acrossEnd, acrossStart, acrossEnd);
        else GuiDraw.drawRect(along, across, alongSize, acrossSize, acrossStart, acrossStart, acrossEnd, acrossEnd);
    }

    private void drawTag(String text, float cursor, float trackCenter, boolean on, Theme palette) {
        int textWidth = compactWidth(text), tagHeight = COMPACT_TAG_HEIGHT;
        int tagWidth = this.tagWidth > 0 ? this.tagWidth : fittedTagWidth(text);
        int alongSize = vertical ? tagHeight : tagWidth, acrossSize = vertical ? tagWidth : tagHeight;
        int along = clamp(Math.round(cursor + 0.5F - alongSize / 2F), length() - alongSize);
        int across = clamp(Math.round(trackCenter - acrossSize / 2F), thickness() - acrossSize);
        int tagX = vertical ? across : along, tagY = vertical ? along : across;
        int tag = on ? palette.cursor : INACTIVE_TAG, textColor = on ? palette.cursorText : INACTIVE_TEXT;
        boolean held = on && (dragging || isHovering());
        if (vertical || palette.tagSkin == null) {
            GuiDraw.drawRect(tagX, tagY, tagWidth, tagHeight, shade(tag, held ? 0.75F : 0.55F));
            GuiDraw.drawRect(
                tagX + 1,
                tagY + 1,
                tagWidth - 2,
                tagHeight - 2,
                tag,
                tag,
                shade(tag, 0.88F),
                shade(tag, 0.88F));
        } else palette.tagSkin.draw(tagX, tagY, tagWidth, tagHeight, held ? tag : shade(tag, 0.88F));
        drawCompact(
            text,
            tagX + Math.round((tagWidth - textWidth) / 2F),
            tagY + (tagHeight - COMPACT_HEIGHT) / 2,
            textColor);
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(max, value));
    }

    private static int glyph(char c) {
        int index = COMPACT_CHARS.indexOf(c);
        return index < 0 ? 0 : COMPACT_GLYPHS[index];
    }

    public static DoubleFunction<String> decimals(int digits) {
        String pattern = "%." + digits + "f";
        // Why: the compact font has no comma, which many locales write as the decimal point.
        return value -> String.format(Locale.ROOT, pattern, value);
    }

    public static DoubleFunction<String> whole() {
        return value -> String.valueOf(Math.round(value));
    }

    public static int fittedTagWidth(String text) {
        return compactWidth(text) + COMPACT_TAG_PADDING * 2;
    }

    public static int compactWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) width += glyphWidth(text.charAt(i)) + (i > 0 ? 1 : 0);
        return width;
    }

    private static int glyphWidth(char c) {
        return c == '.' ? 1 : 3;
    }

    private static void drawCompact(String text, int x, int y, int color) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int glyph = glyph(c), glyphWidth = glyphWidth(c);
            for (int row = 0; row < COMPACT_HEIGHT; row++) {
                int start = -1;
                for (int column = 0; column <= glyphWidth; column++) {
                    boolean lit = column < glyphWidth
                        && (glyph >> ((COMPACT_HEIGHT - 1 - row) * 3 + 2 - column) & 1) != 0;
                    if (lit && start < 0) start = column;
                    else if (!lit && start >= 0) {
                        GuiDraw.drawRect(x + start, y + row, column - start, 1, color);
                        start = -1;
                    }
                }
            }
            x += glyphWidth + 1;
        }
    }

    private static int shade(int argb, float factor) {
        return argb & 0xFF000000 | ColorMath.scale(argb, factor);
    }

    public static class Theme extends WidgetTheme {

        public static final String TRACK = "trackColor", FILL = "fillColor", CURSOR = "cursorColor",
            CURSOR_TEXT = "cursorTextColor", TICK = "tickColor", MILESTONE = "milestoneColor", TRACK_SKIN = "trackSkin",
            FILL_SKIN = "fillSkin", TAG_SKIN = "tagSkin";
        private static final int DEFAULT_SIZE = 18;
        public static final WidgetThemeKey<Theme> KEY = register(
            new Theme(0xFF1E1A22, 0xFFB98A3C, 0xFFF4E2B0, 0xFF442000, 0x55FFFFFF, 0xB0FFFFFF));

        public final int track, fill, cursor, cursorText, tick, milestone;
        public final SkinDrawable trackSkin, fillSkin, tagSkin;

        public Theme(int track, int fill, int cursor, int cursorText, int tick, int milestone) {
            this(track, fill, cursor, cursorText, tick, milestone, null, null, null);
        }

        public Theme(int track, int fill, int cursor, int cursorText, int tick, int milestone, SkinDrawable trackSkin,
            SkinDrawable fillSkin, SkinDrawable tagSkin) {
            super(DEFAULT_SIZE, DEFAULT_SIZE, null, Color.WHITE.main, Color.TEXT_COLOR_DARK, false, Color.WHITE.main);
            this.track = track;
            this.fill = fill;
            this.cursor = cursor;
            this.cursorText = cursorText;
            this.tick = tick;
            this.milestone = milestone;
            this.trackSkin = trackSkin;
            this.fillSkin = fillSkin;
            this.tagSkin = tagSkin;
        }

        public Theme(Theme parent, JsonObject json, JsonObject fallback) {
            super(parent, json, fallback);
            track = JsonHelper.getColor(json, parent.track, TRACK);
            fill = JsonHelper.getColor(json, parent.fill, FILL);
            cursor = JsonHelper.getColor(json, parent.cursor, CURSOR);
            cursorText = JsonHelper.getColor(json, parent.cursorText, CURSOR_TEXT);
            tick = JsonHelper.getColor(json, parent.tick, TICK);
            milestone = JsonHelper.getColor(json, parent.milestone, MILESTONE);
            trackSkin = SkinDrawable.read(json, TRACK_SKIN, parent.trackSkin);
            fillSkin = SkinDrawable.read(json, FILL_SKIN, parent.fillSkin);
            tagSkin = SkinDrawable.read(json, TAG_SKIN, parent.tagSkin);
        }

        private static WidgetThemeKey<Theme> register(Theme defaults) {
            return IThemeApi.get()
                .widgetThemeKeyBuilder("gtnllibStepSlider", Theme.class)
                .defaultTheme(defaults)
                .defaultHoverTheme(defaults)
                .parser(Theme::new)
                .register();
        }

        public static class Builder extends WidgetThemeBuilder<Theme, Builder> {

            public Builder colors(int track, int fill, int cursor, int cursorText) {
                add(TRACK, track);
                add(FILL, fill);
                add(CURSOR, cursor);
                add(CURSOR_TEXT, cursorText);
                return this;
            }

            public Builder skins(SkinDrawable track, SkinDrawable fill, SkinDrawable tag) {
                SkinDrawable.write(this, TRACK_SKIN, track);
                SkinDrawable.write(this, FILL_SKIN, fill);
                SkinDrawable.write(this, TAG_SKIN, tag);
                return this;
            }

            public Builder tickColors(int tick, int milestone) {
                add(TICK, tick);
                add(MILESTONE, milestone);
                return this;
            }
        }
    }
}
