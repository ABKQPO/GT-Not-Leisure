package com.gtnllib.gui.theme;

import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.theme.SelectableTheme;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.TextFieldTheme;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LivePalette {

    public static final int NO_ACCENT = 0;
    private static final long POLL_NANOS = 50_000_000L;

    private final Supplier<String> theme;
    private final BooleanSupplier following;
    private final ITheme view = new View();
    private IntSupplier accent = () -> NO_ACCENT;
    private Snapshot snapshot;
    private long polled;

    public LivePalette(Supplier<String> theme, BooleanSupplier following) {
        this.theme = theme;
        this.following = following;
    }

    public void accentFrom(IntSupplier accent) {
        this.accent = accent;
        refresh();
    }

    public void refresh() {
        snapshot = null;
    }

    public String themeId() {
        return snapshot().theme();
    }

    public int accent() {
        return snapshot().accent();
    }

    public ThemePalette themed() {
        return snapshot().themed();
    }

    public ThemePalette accented() {
        return snapshot().accented();
    }

    public ThemePalette current() {
        Snapshot current = snapshot();
        return current.following() ? current.accented() : current.themed();
    }

    public ITheme theme() {
        return view;
    }

    private Snapshot snapshot() {
        long now = System.nanoTime();
        if (snapshot != null && now - polled < POLL_NANOS) return snapshot;
        polled = now;
        String id = theme.get();
        boolean follow = following.getAsBoolean();
        int color = accent.getAsInt();
        if (snapshot == null || !snapshot.matches(id, follow, color)) {
            ThemePalette base = PaletteThemes.palette(id);
            ThemePalette tinted = base == null || color == NO_ACCENT ? base : base.follow(color);
            snapshot = new Snapshot(id, follow, color, base, tinted);
        }
        return snapshot;
    }

    private record Snapshot(String theme, boolean following, int accent, ThemePalette themed, ThemePalette accented) {

        private boolean matches(String theme, boolean following, int accent) {
            return this.theme.equals(theme) && this.following == following && this.accent == accent;
        }
    }

    private class View implements ITheme {

        private ITheme delegate() {
            return PaletteThemes.variant(themeId(), current());
        }

        @Override
        public String getId() {
            return delegate().getId();
        }

        @Override
        public ITheme getParentTheme() {
            return delegate().getParentTheme();
        }

        @Override
        public Collection<WidgetThemeEntry<?>> getWidgetThemes() {
            return delegate().getWidgetThemes();
        }

        @Override
        public WidgetThemeEntry<WidgetTheme> getFallback() {
            return delegate().getFallback();
        }

        @Override
        public WidgetThemeEntry<WidgetTheme> getPanelTheme() {
            return delegate().getPanelTheme();
        }

        @Override
        public WidgetThemeEntry<WidgetTheme> getButtonTheme() {
            return delegate().getButtonTheme();
        }

        @Override
        public WidgetThemeEntry<WidgetTheme> getScrollbarTheme() {
            return delegate().getScrollbarTheme();
        }

        @Override
        public WidgetThemeEntry<SlotTheme> getItemSlotTheme() {
            return delegate().getItemSlotTheme();
        }

        @Override
        public WidgetThemeEntry<SlotTheme> getFluidSlotTheme() {
            return delegate().getFluidSlotTheme();
        }

        @Override
        public WidgetThemeEntry<TextFieldTheme> getTextFieldTheme() {
            return delegate().getTextFieldTheme();
        }

        @Override
        public WidgetThemeEntry<SelectableTheme> getToggleButtonTheme() {
            return delegate().getToggleButtonTheme();
        }

        @Override
        public <T extends WidgetTheme> WidgetThemeEntry<T> getWidgetTheme(WidgetThemeKey<T> key) {
            return delegate().getWidgetTheme(key);
        }
    }
}
