package com.gtnllib.gui.settings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.DoubleFunction;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;

public class SettingControls {

    private static final String TOOLTIP = ".tooltip";

    public interface Control {

        /**
         * @return the lang key from the field's {@code @LangKey}; the hover text uses the same key plus ".tooltip"
         */
        String lang();

        /**
         * Builds one row: the setting's name and a slider, or a switch carrying the name. Every change is written to
         * the field at once and followed by {@code save}.
         *
         * @param sliderWidth width of a slider in pixels, ignored by switches
         * @param tagWidth    width of a slider's value tag, shared by the rows of a page so the tags line up
         */
        IWidget widget(Layout rows, LayoutTheme style, int sliderWidth, int tagWidth, Runnable save);

        /**
         * Writes the default from the field's {@code @Default...} annotation back into the field.
         */
        void reset();
    }

    public record Slider(String lang, Object owner, Field field, double min, double max, double initial, double step,
        DoubleFunction<String> format) implements Control {

        public int steps() {
            return (int) Math.round((max - min) / step);
        }

        public double value() {
            return ((Number) read(owner, field)).doubleValue();
        }

        public void set(double value) {
            store(min + Math.round((value - min) / step) * step);
        }

        public int widestTag() {
            int widest = 0;
            for (int i = 0; i <= steps(); i++)
                widest = Math.max(widest, StepSlider.fittedTagWidth(format.apply(min + (max - min) * i / steps())));
            return widest;
        }

        @Override
        public IWidget widget(Layout rows, LayoutTheme style, int sliderWidth, int tagWidth, Runnable save) {
            return rows.line(
                style.spacingMedium,
                Arrays.asList(Size.fill(), Size.fixed(sliderWidth)),
                Arrays.asList(
                    Layout.label(IKey.lang(lang), style)
                        .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(lang + TOOLTIP))),
                    new StepSlider().value(this::value, this::set)
                        .range(min, max, steps())
                        .format(format)
                        .tagWidth(tagWidth)
                        .onRelease(save)));
        }

        @Override
        public void reset() {
            store(initial);
        }

        private void store(double value) {
            Class<?> type = field.getType();
            if (type == int.class) write(owner, field, (int) Math.round(value));
            else if (type == float.class) write(owner, field, (float) value);
            else write(owner, field, value);
        }
    }

    public record Toggle(String lang, Object owner, Field field, boolean initial) implements Control {

        public boolean on() {
            return (boolean) read(owner, field);
        }

        @Override
        public IWidget widget(Layout rows, LayoutTheme style, int sliderWidth, int tagWidth, Runnable save) {
            return rows.line(
                0,
                Collections.singletonList(Size.fill()),
                Collections.singletonList(
                    new SlideSwitch().value(this::on)
                        .label(IKey.lang(lang))
                        .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(lang + TOOLTIP)))
                        .onMousePressed(mouse -> {
                            if (mouse != 0) return false;
                            write(owner, field, !on());
                            save.run();
                            return true;
                        })));
        }

        @Override
        public void reset() {
            write(owner, field, initial);
        }
    }

    public static Slider slider(Object owner, Class<?> type, String name, double step, DoubleFunction<String> format) {
        Field field = field(type, name);
        double min, max, initial;
        if (field.getType() == int.class) {
            Config.RangeInt range = annotation(field, Config.RangeInt.class);
            min = range.min();
            max = range.max();
            initial = annotation(field, Config.DefaultInt.class).value();
        } else if (field.getType() == float.class) {
            Config.RangeFloat range = annotation(field, Config.RangeFloat.class);
            min = range.min();
            max = range.max();
            initial = annotation(field, Config.DefaultFloat.class).value();
        } else if (field.getType() == double.class) {
            Config.RangeDouble range = annotation(field, Config.RangeDouble.class);
            min = range.min();
            max = range.max();
            initial = annotation(field, Config.DefaultDouble.class).value();
        } else throw new IllegalArgumentException(field + " is not an int, float or double setting");
        return new Slider(lang(field), owner, field, min, max, initial, step, format);
    }

    public static Toggle toggle(Object owner, Class<?> type, String name) {
        Field field = field(type, name);
        if (field.getType() != boolean.class) throw new IllegalArgumentException(field + " is not a switch setting");
        return new Toggle(lang(field), owner, field, annotation(field, Config.DefaultBoolean.class).value());
    }

    public static int tagWidth(Collection<? extends Control> controls) {
        int widest = 0;
        for (Control control : controls)
            if (control instanceof Slider slider) widest = Math.max(widest, slider.widestTag());
        return widest;
    }

    private static Field field(Class<?> type, String name) {
        try {
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(type.getName() + " has no public setting " + name, e);
        }
    }

    private static String lang(Field field) {
        return annotation(field, Config.LangKey.class).value();
    }

    private static <A extends Annotation> A annotation(Field field, Class<A> kind) {
        A found = field.getAnnotation(kind);
        if (found == null) throw new IllegalArgumentException(field + " has no @" + kind.getSimpleName());
        return found;
    }

    private static Object read(Object owner, Field field) {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(field + " cannot be read", e);
        }
    }

    private static void write(Object owner, Field field, Object value) {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(field + " cannot be written", e);
        }
    }
}
