package com.science.gtnl.common.gui.modularui.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.theme.ReloadThemeEvent;
import com.cleanroommc.modularui.theme.SelectableTheme;
import com.cleanroommc.modularui.theme.ThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.science.gtnl.common.gui.modularui.layout.LayoutTheme;
import com.science.gtnl.common.gui.modularui.widget.CardDrawable;
import com.science.gtnl.common.gui.modularui.widget.FillBar;
import com.science.gtnl.common.gui.modularui.widget.FrameDrawable;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.gui.modularui.widget.StepSlider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.modularui2.GTGuiThemes;

@SideOnly(Side.CLIENT)
public class PaletteThemes {

    private static final Map<String, Definition> THEMES = new LinkedHashMap<>();
    private static final List<BiConsumer<ThemeBuilder<?>, ThemePalette>> EXTENSIONS = new ArrayList<>();
    private static final List<LivePalette> LIVE = new ArrayList<>();
    private static final Map<String, Variant> VARIANTS = new HashMap<>();
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    private PaletteThemes() {}

    public static void register(String id, ThemePalette palette) {
        register(id, GTGuiThemes.STANDARD.getId(), palette, ThemeStyle.DEFAULT);
    }

    public static void register(String id, String parent, ThemePalette palette, ThemeStyle style) {
        THEMES.put(id, new Definition(parent, palette, style));
        rebuild(id);
        for (Runnable listener : LISTENERS) listener.run();
    }

    public static void onRegister(Runnable listener) {
        LISTENERS.add(listener);
        listener.run();
    }

    public static void extend(BiConsumer<ThemeBuilder<?>, ThemePalette> extension) {
        EXTENSIONS.add(extension);
        rebuild();
    }

    public static Set<String> ids() {
        return Collections.unmodifiableSet(THEMES.keySet());
    }

    public static ThemePalette palette(String id) {
        Definition definition = THEMES.get(id);
        return definition == null ? null : definition.palette();
    }

    public static void rebuild() {
        for (String id : THEMES.keySet()) rebuild(id);
    }

    private static void rebuild(String id) {
        Definition definition = THEMES.get(id);
        IThemeApi.get()
            .getJavaDefaultThemes(id)
            .clear();
        IThemeApi.get()
            .registerTheme(decorated(id, definition, definition.palette()));
    }

    private static ThemeBuilder<?> decorated(String id, Definition definition, ThemePalette palette) {
        ThemeBuilder<?> theme = build(id, definition.parent(), palette, definition.style());
        for (BiConsumer<ThemeBuilder<?>, ThemePalette> extension : EXTENSIONS) extension.accept(theme, palette);
        return theme;
    }

    public static LivePalette live(Supplier<String> theme, BooleanSupplier following) {
        LivePalette palette = new LivePalette(theme, following);
        LIVE.add(palette);
        return palette;
    }

    public static ITheme forScreen(ITheme theme) {
        for (LivePalette palette : LIVE) if (palette.themeId()
            .equals(theme.getId())) return palette.theme();
        return theme;
    }

    public static ITheme variant(String id, ThemePalette palette) {
        Definition definition = THEMES.get(id);
        if (definition == null || palette == null || palette.equals(definition.palette())) return IThemeApi.get()
            .getTheme(id);
        Variant cached = VARIANTS.get(id);
        if (cached != null && cached.palette()
            .equals(palette)) return cached.theme();
        ITheme theme = VariantTheme.parse(
            id,
            IThemeApi.get()
                .getTheme(definition.parent()),
            decorated(id, definition, palette).getJson());
        VARIANTS.put(id, new Variant(palette, theme));
        return theme;
    }

    public static ThemeBuilder<?> build(String id, String parent, ThemePalette palette, ThemeStyle style) {
        ThemeBuilder<?> theme = new ThemeBuilder<>(id).parent(parent)
            .defaultColor(Color.WHITE.main)
            .defaultTextColor(palette.text())
            .defaultTextShadow(true)
            .background(
                IThemeApi.PANEL,
                new FrameDrawable().fill(palette.panelTop(), palette.panelBottom())
                    .border(palette.accentDim())
                    .corners(palette.accent(), style.cornerLength(), style.cornerThickness()))
            .background(IThemeApi.BUTTON, new Rectangle().color(palette.button()))
            .hoverBackground(IThemeApi.BUTTON, new Rectangle().color(palette.buttonHover()))
            .textHoverColor(IThemeApi.BUTTON, palette.selectedText())
            .textHoverShadow(IThemeApi.BUTTON, palette.selectedTextShadow())
            .hoverBackground(IThemeApi.CLOSE_BUTTON, new Rectangle().color(palette.hover()))
            .widgetTheme(
                IThemeApi.TOGGLE_BUTTON,
                new SelectableTheme.Builder<>().background(new Rectangle().color(palette.button()))
                    .textColor(palette.textOff())
                    .selectedBackground(new Rectangle().color(palette.buttonHover()))
                    .selectedTextColor(palette.selectedText()))
            .textShadow(IThemeApi.TOGGLE_BUTTON, false)
            .widgetThemeHover(
                IThemeApi.TOGGLE_BUTTON,
                new SelectableTheme.Builder<>().background(new Rectangle().color(palette.tabHover()))
                    .selectedBackground(new Rectangle().color(palette.buttonHover())))
            .background(
                IThemeApi.ITEM_SLOT,
                new FrameDrawable().fill(palette.well())
                    .border(palette.wellEdge()))
            .itemSlotHoverColor(palette.slotHover())
            .background(
                IThemeApi.TEXT_FIELD,
                new FrameDrawable().fill(palette.well())
                    .border(palette.accentDim()))
            .textColor(IThemeApi.TEXT_FIELD, palette.text())
            .textShadow(IThemeApi.TEXT_FIELD, false)
            .textFieldMarkedColor(palette.accentDim())
            .textFieldHintColor(palette.hint())
            .background(IThemeApi.SCROLLBAR, new Rectangle().color(palette.accentDim()))
            .background(
                CardDrawable.KEY,
                new CardDrawable().fill(palette.card())
                    .edge(palette.cardEdge())
                    .accent(palette.accent(), style.cardAccentTop(), style.cardAccentWidth(), style.cardAccentHeight()))
            .widgetTheme(
                StepSlider.Theme.KEY,
                new StepSlider.Theme.Builder()
                    .colors(palette.track(), palette.accent(), palette.cursor(), palette.cursorText())
                    .tickColors(palette.tick(), palette.milestone()))
            .widgetTheme(
                SlideSwitch.Theme.KEY,
                new SlideSwitch.Theme.Builder().track(palette.switchOff(), palette.accent(), palette.accentDim())
                    .knob(palette.knob(), palette.knobOff())
                    .text(palette.textOn(), palette.textDim(), palette.textOff()))
            .hoverBackground(SlideSwitch.Theme.KEY, new Rectangle().color(palette.hover()))
            .widgetTheme(FillBar.Theme.KEY, new FillBar.Theme.Builder().colors(palette.track(), palette.accent()))
            .widgetTheme(LayoutTheme.KEY, new LayoutTheme.Builder().style(style))
            .background(ButtonPurpose.PRIMARY.key, new Rectangle().color(palette.buttonHover()))
            .hoverBackground(ButtonPurpose.PRIMARY.key, new Rectangle().color(palette.accent()))
            .textColor(ButtonPurpose.PRIMARY.key, palette.selectedText())
            .textHoverColor(ButtonPurpose.PRIMARY.key, palette.selectedText())
            .textShadow(ButtonPurpose.PRIMARY.key, palette.selectedTextShadow())
            .textHoverShadow(ButtonPurpose.PRIMARY.key, palette.selectedTextShadow())
            .background(ButtonPurpose.DANGER.key, new Rectangle().color(palette.button()))
            .hoverBackground(ButtonPurpose.DANGER.key, new Rectangle().color(palette.danger()))
            .textColor(ButtonPurpose.DANGER.key, palette.danger())
            .textHoverColor(ButtonPurpose.DANGER.key, palette.text())
            .hoverBackground(ButtonPurpose.ICON.key, new Rectangle().color(palette.hover()));
        for (WidgetThemeKey<?> key : new WidgetThemeKey<?>[] { IThemeApi.CLOSE_BUTTON, ButtonPurpose.ICON.key })
            theme.mergeAdd(key.getFullName(), new JsonBuilder().add(IThemeApi.BACKGROUND, "empty"));
        theme.mergeAdd(
            IThemeApi.TOGGLE_BUTTON.getFullName(),
            new JsonBuilder().add(IThemeApi.SELECTED_TEXT_SHADOW, palette.selectedTextShadow()));
        return theme;
    }

    private record Definition(String parent, ThemePalette palette, ThemeStyle style) {}

    private record Variant(ThemePalette palette, ITheme theme) {}

    public static class Reloader {

        @SubscribeEvent
        public void onThemeReload(ReloadThemeEvent.Pre event) {
            rebuild();
        }

        @SubscribeEvent
        public void onThemesLoaded(ReloadThemeEvent.Post event) {
            VARIANTS.clear();
        }
    }
}
