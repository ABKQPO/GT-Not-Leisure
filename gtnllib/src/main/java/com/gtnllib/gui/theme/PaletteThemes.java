package com.gtnllib.gui.theme;

import java.util.Collections;
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
import com.cleanroommc.modularui.theme.ThemeAPI;
import com.cleanroommc.modularui.theme.ThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.gtnllib.gui.theme.ThemeSkin.Part;
import com.gtnllib.gui.widget.CardDrawable;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.FillBar;
import com.gtnllib.gui.widget.FrameDrawable;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class PaletteThemes {

    private static final Map<String, Definition> THEMES = new Object2ObjectLinkedOpenHashMap<>();
    private static final List<BiConsumer<ThemeBuilder<?>, ThemePalette>> EXTENSIONS = new ObjectArrayList<>();
    private static final List<LivePalette> LIVE = new ObjectArrayList<>();
    private static final Map<String, Variant> VARIANTS = new Object2ObjectOpenHashMap<>();
    private static final List<Runnable> LISTENERS = new ObjectArrayList<>();
    private static Supplier<String> chosen = () -> "";

    public static void register(String id, ThemePalette palette) {
        register(id, ThemeAPI.DEFAULT_ID, palette, ThemeStyle.DEFAULT);
    }

    public static void register(String id, String parent, ThemePalette palette, ThemeStyle style) {
        register(id, parent, palette, style, ThemeSkin.NONE);
    }

    public static void register(String id, String parent, ThemePalette palette, ThemeStyle style, ThemeSkin skin) {
        THEMES.put(id, new Definition(parent, palette, style, skin));
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

    public static void chooseWith(Supplier<String> chosen) {
        PaletteThemes.chosen = chosen;
    }

    public static String selected(String fallback) {
        String id = chosen.get();
        return THEMES.containsKey(id) ? id : fallback;
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
        ThemeBuilder<?> theme = build(id, definition.parent(), palette, definition.style(), definition.skin());
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
        return build(id, parent, palette, style, ThemeSkin.NONE);
    }

    public static ThemeBuilder<?> build(String id, String parent, ThemePalette palette, ThemeStyle style,
        ThemeSkin skin) {
        boolean solid = skin.has(Part.BUTTON);
        int litText = solid ? palette.textOn(palette.solidHover()) : palette.selectedText(),
            brightText = solid ? palette.textOn(palette.brightAccent()) : palette.selectedText();
        boolean litShadow = litText == palette.text(), brightShadow = brightText == palette.text();
        ThemeBuilder<?> theme = new ThemeBuilder<>(id).parent(parent)
            .defaultColor(Color.WHITE.main)
            .defaultTextColor(palette.text())
            .defaultTextShadow(true)
            .background(
                IThemeApi.PANEL,
                skin.lit(
                    Part.PANEL,
                    palette.accent(),
                    () -> new FrameDrawable().fill(palette.panelTop(), palette.panelBottom())
                        .border(palette.accentDim())
                        .corners(palette.accent(), style.cornerLength(), style.cornerThickness())))
            .background(IThemeApi.BUTTON, skin.plain(Part.BUTTON, () -> new Rectangle().color(palette.button())))
            .hoverBackground(
                IThemeApi.BUTTON,
                skin.lit(Part.BUTTON, palette.solidHover(), () -> new Rectangle().color(palette.buttonHover())))
            .textHoverColor(IThemeApi.BUTTON, litText)
            .textHoverShadow(IThemeApi.BUTTON, litShadow)
            .hoverBackground(
                IThemeApi.CLOSE_BUTTON,
                skin.lit(Part.CLOSE, palette.danger(), () -> new Rectangle().color(palette.hover())))
            .widgetTheme(
                IThemeApi.TOGGLE_BUTTON,
                new SelectableTheme.Builder<>()
                    .background(skin.plain(Part.BUTTON, () -> new Rectangle().color(palette.button())))
                    .textColor(palette.textOff())
                    .selectedBackground(
                        skin.lit(Part.BUTTON, palette.solidHover(), () -> new Rectangle().color(palette.buttonHover())))
                    .selectedTextColor(litText))
            .textShadow(IThemeApi.TOGGLE_BUTTON, false)
            .widgetThemeHover(
                IThemeApi.TOGGLE_BUTTON,
                new SelectableTheme.Builder<>()
                    .background(
                        skin.lit(Part.BUTTON, palette.tabHover(), () -> new Rectangle().color(palette.tabHover())))
                    .selectedBackground(
                        skin.lit(
                            Part.BUTTON,
                            palette.solidHover(),
                            () -> new Rectangle().color(palette.buttonHover()))))
            .background(
                IThemeApi.ITEM_SLOT,
                skin.plain(
                    Part.SLOT,
                    () -> new FrameDrawable().fill(palette.well())
                        .border(palette.wellEdge())))
            .itemSlotHoverColor(palette.slotHover())
            .background(
                IThemeApi.TEXT_FIELD,
                skin.plain(
                    Part.SLOT,
                    () -> new FrameDrawable().fill(palette.well())
                        .border(palette.accentDim())))
            .textColor(IThemeApi.TEXT_FIELD, palette.text())
            .textShadow(IThemeApi.TEXT_FIELD, false)
            .textFieldMarkedColor(palette.accentDim())
            .textFieldHintColor(palette.hint())
            .background(IThemeApi.SCROLLBAR, new Rectangle().color(palette.accentDim()))
            .background(
                CardDrawable.KEY,
                skin.lit(
                    Part.CARD,
                    palette.accent(),
                    () -> new CardDrawable().fill(palette.card())
                        .edge(palette.cardEdge())
                        .accent(
                            palette.accent(),
                            style.cardAccentTop(),
                            style.cardAccentWidth(),
                            style.cardAccentHeight())))
            .widgetTheme(
                StepSlider.Theme.KEY,
                new StepSlider.Theme.Builder()
                    .colors(palette.track(), palette.accent(), palette.cursor(), palette.cursorText())
                    .tickColors(palette.tick(), palette.milestone())
                    .skins(skin.get(Part.SLIDER_TRACK), skin.get(Part.SLIDER_FILL), skin.get(Part.SLIDER_TAG)))
            .widgetTheme(
                SlideSwitch.Theme.KEY,
                new SlideSwitch.Theme.Builder().track(palette.switchOff(), palette.accent(), palette.accentDim())
                    .knob(palette.knob(), palette.knobOff())
                    .text(palette.textOn(), palette.textDim(), palette.textOff())
                    .skins(skin.get(Part.SWITCH_TRACK), skin.get(Part.SWITCH_KNOB)))
            .hoverBackground(SlideSwitch.Theme.KEY, new Rectangle().color(palette.hover()))
            .widgetTheme(
                FillBar.Theme.KEY,
                new FillBar.Theme.Builder().colors(palette.track(), palette.accent())
                    .skins(skin.get(Part.SLIDER_TRACK), skin.get(Part.SLIDER_FILL)))
            .widgetTheme(Divider.Theme.KEY, new Divider.Theme.Builder().skin(skin.get(Part.DIVIDER)))
            .widgetTheme(LayoutTheme.KEY, new LayoutTheme.Builder().values(style.layout()))
            .background(
                ButtonPurpose.PRIMARY.key,
                skin.lit(Part.BUTTON, palette.solidHover(), () -> new Rectangle().color(palette.buttonHover())))
            .hoverBackground(
                ButtonPurpose.PRIMARY.key,
                skin.lit(Part.BUTTON, palette.brightAccent(), () -> new Rectangle().color(palette.accent())))
            .textColor(ButtonPurpose.PRIMARY.key, litText)
            .textHoverColor(ButtonPurpose.PRIMARY.key, brightText)
            .textShadow(ButtonPurpose.PRIMARY.key, litShadow)
            .textHoverShadow(ButtonPurpose.PRIMARY.key, brightShadow)
            .background(
                ButtonPurpose.DANGER.key,
                skin.plain(Part.BUTTON, () -> new Rectangle().color(palette.button())))
            .hoverBackground(
                ButtonPurpose.DANGER.key,
                skin.lit(Part.BUTTON, palette.danger(), () -> new Rectangle().color(palette.danger())))
            .textColor(ButtonPurpose.DANGER.key, palette.danger())
            .textHoverColor(ButtonPurpose.DANGER.key, palette.text())
            .hoverBackground(ButtonPurpose.ICON.key, new Rectangle().color(palette.hover()));
        for (WidgetThemeKey<?> key : new WidgetThemeKey<?>[] { IThemeApi.CLOSE_BUTTON, ButtonPurpose.ICON.key })
            if (key != IThemeApi.CLOSE_BUTTON || !skin.has(Part.CLOSE))
                theme.mergeAdd(key.getFullName(), new JsonBuilder().add(IThemeApi.BACKGROUND, "empty"));
        if (skin.has(Part.CLOSE)) theme.background(IThemeApi.CLOSE_BUTTON, skin.get(Part.CLOSE));
        theme.mergeAdd(
            IThemeApi.TOGGLE_BUTTON.getFullName(),
            new JsonBuilder().add(IThemeApi.SELECTED_TEXT_SHADOW, litShadow));
        return theme;
    }

    private record Definition(String parent, ThemePalette palette, ThemeStyle style, ThemeSkin skin) {}

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
