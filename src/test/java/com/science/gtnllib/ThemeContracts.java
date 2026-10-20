package com.science.gtnllib;

import static com.science.gtnllib.GTNLLibContractTest.SOURCES;
import static com.science.gtnllib.GTNLLibContractTest.call;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.field;
import static com.science.gtnllib.GTNLLibContractTest.read;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.cleanroommc.modularui.api.IJsonSerializable;
import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.theme.ReloadThemeEvent;
import com.cleanroommc.modularui.theme.SelectableTheme;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.TextFieldTheme;
import com.cleanroommc.modularui.theme.ThemeAPI;
import com.cleanroommc.modularui.theme.ThemeManager;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.science.gtnllib.client.fx.FxColor;
import com.science.gtnllib.config.GTNLLibConfig;
import com.science.gtnllib.gui.layout.LayoutTheme;
import com.science.gtnllib.gui.theme.ButtonPurpose;
import com.science.gtnllib.gui.theme.LivePalette;
import com.science.gtnllib.gui.theme.PaletteThemes;
import com.science.gtnllib.gui.theme.ThemePalette;
import com.science.gtnllib.gui.theme.ThemeStyle;
import com.science.gtnllib.gui.theme.VariantTheme;
import com.science.gtnllib.gui.widget.CardDrawable;
import com.science.gtnllib.gui.widget.FillBar;
import com.science.gtnllib.gui.widget.FrameDrawable;
import com.science.gtnllib.gui.widget.SlideSwitch;
import com.science.gtnllib.gui.widget.StepSlider;

public class ThemeContracts {

    public static final ThemePalette SAMPLE = new ThemePalette(
        0xFFFF3D9A,
        0x80FF3D9A,
        0x26FF3D9A,
        0xFFFFFFFF,
        0xFF8E8796,
        0xF2211A27,
        0xF20D0A11,
        0x50000000,
        0x24FFFFFF,
        0x9614141E,
        0xA0FF3D9A,
        0x50FF3D9A,
        0x80000000,
        0xFF3A3640,
        0x50FF3D9A,
        0xFF1A1420,
        0xFF3A3640,
        0xFFF2EEF4,
        0xFF8C8794,
        0xFFF4F0F6,
        0xFF6E6876,
        0xFF9A94A2,
        0xFFFFE6F1,
        0xFF4A0A2A,
        0x40FFFFFF,
        0x99FFFFFF,
        0xFFE5484D);
    private static final String SAMPLE_THEME = "gtnllib:contract_sample", TEST_THEME = "gtnllib:contract_test";
    private static final int CYAN = 0x3DE8FF;
    private static final double HUE_TOLERANCE = 6;

    private ThemeContracts() {}

    static void run() throws Exception {
        DrawableSerialization.init();
        PaletteThemes.register(SAMPLE_THEME, SAMPLE);
        reloadThemes(SAMPLE_THEME);
        ITheme theme = IThemeApi.get()
            .getTheme(SAMPLE_THEME),
            plain = IThemeApi.get()
                .getDefaultTheme();
        check(
            theme.getId()
                .equals(SAMPLE_THEME),
            "a palette theme is registered under its id");
        check(
            theme.getParentTheme()
                .getId()
                .equals(ThemeAPI.DEFAULT_ID),
            "a palette theme inherits the ModularUI default theme unless a parent is given");
        sliders(theme, plain);
        controls(theme, plain);
        builder();
        follow();
        variants();
        choice();
        overrides();
    }

    private static void sliders(ITheme theme, ITheme plain) throws Exception {
        StepSlider.Theme slider = theme.getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        StepSlider.Theme defaults = StepSlider.Theme.KEY.getDefaultValue();
        check(
            defaults.track == 0xFF1E1A22 && defaults.fill == 0xFFB98A3C
                && defaults.cursor == 0xFFF4E2B0
                && defaults.cursorText == 0xFF442000
                && defaults.tick == 0x55FFFFFF
                && defaults.milestone == 0xB0FFFFFF,
            "slider default colors stay the old generic ones");
        check(
            plain.getWidgetTheme(StepSlider.Theme.KEY)
                .getTheme().fill == defaults.fill,
            "a theme without slider colors falls back to the slider defaults");
        StepSlider themed = new StepSlider();
        check(
            themed.getWidgetTheme(theme)
                .getTheme() == slider,
            "slider takes its widget theme from the screen theme");
        Class<?>[] resolve = { WidgetTheme.class };
        check(
            call(StepSlider.class, "resolveColors", themed, resolve, slider) == slider,
            "slider without explicit colors draws with the theme colors");
        check(
            call(
                StepSlider.class,
                "resolveColors",
                themed,
                resolve,
                plain.getFallback()
                    .getTheme())
                == defaults,
            "slider outside a slider theme draws with the default colors");
        StepSlider.Theme ticks = (StepSlider.Theme) call(
            StepSlider.class,
            "resolveColors",
            new StepSlider().tickColors(1, 2),
            resolve,
            slider);
        check(
            ticks.track == slider.track && ticks.fill == slider.fill
                && ticks.cursor == slider.cursor
                && ticks.cursorText == slider.cursorText
                && ticks.tick == 1
                && ticks.milestone == 2,
            "explicit tick colors override only the ticks");
        StepSlider.Theme main = (StepSlider.Theme) call(
            StepSlider.class,
            "resolveColors",
            new StepSlider().colors(3, 4, 5, 6),
            resolve,
            slider);
        check(
            main.track == 3 && main.fill == 4
                && main.cursor == 5
                && main.cursorText == 6
                && main.tick == slider.tick
                && main.milestone == slider.milestone,
            "explicit colors override only the track, fill and cursor");
    }

    private static void controls(ITheme theme, ITheme plain) throws Exception {
        JsonObject registered = IThemeApi.get()
            .getJavaDefaultThemes(SAMPLE_THEME)
            .get(0)
            .getJson()
            .getAsJsonObject(IThemeApi.PANEL.getFullName());
        check(
            "none".equals(
                registered.get(IThemeApi.BACKGROUND)
                    .getAsString()),
            "MUI2 overwrites the background of a registered theme when it loads it");
        check(
            read(SOURCES.resolve("client/GTNLLibClient.java"))
                .contains("EVENT_BUS.register(new PaletteThemes.Reloader())"),
            "GTNLLib rebuilds its palette themes before every theme reload");
        check(
            ThemeStyle.DEFAULT.equals(new ThemeStyle(6, 2, 3, 2, 10, 2, 4, 10, 8, 14, 16, 1F)),
            "default theme style keeps the old corner, card accent, spacing, button and row sizes");
        paletteControls("sample", theme, SAMPLE, ThemeStyle.DEFAULT);
        SlideSwitch.Theme slide = theme.getWidgetTheme(SlideSwitch.Theme.KEY)
            .getTheme();
        check(
            SlideSwitch.trackColor(slide, false, false) == slide.track
                && SlideSwitch.trackColor(slide, true, true) == slide.trackOn
                && SlideSwitch.trackColor(slide, true, false) == slide.trackDim,
            "switch track shows off, on and on without effect");
        check(
            SlideSwitch.textColor(slide, false, false, true) == slide.textOff
                && SlideSwitch.textColor(slide, false, false, false) == slide.textDim
                && SlideSwitch.textColor(slide, true, true, true) == slide.textOn
                && SlideSwitch.textColor(slide, true, false, true) == slide.textDim,
            "switch label tells off, unavailable, on and on without effect apart");
        check(
            FillBar.filledWidth(0.5, 160) == 80 && FillBar.filledWidth(-1, 160) == 0
                && FillBar.filledWidth(2, 160) == 160
                && FillBar.filledWidth(Double.NaN, 160) == 0,
            "fill bar clamps its value");
        WidgetTheme neutralCard = plain.getWidgetTheme(CardDrawable.KEY)
            .getTheme();
        SlideSwitch.Theme neutralSlide = plain.getWidgetTheme(SlideSwitch.Theme.KEY)
            .getTheme();
        FillBar.Theme neutralBar = plain.getWidgetTheme(FillBar.Theme.KEY)
            .getTheme();
        boolean neutral = neutralCard.getBackground() instanceof CardDrawable && !pink(neutralBar.track)
            && !pink(neutralBar.fill);
        for (Field color : SlideSwitch.Theme.class.getFields())
            if (color.getType() == int.class && !Modifier.isStatic(color.getModifiers()))
                neutral &= !pink(color.getInt(neutralSlide));
        for (String color : new String[] { "fill", "edge", "accent" })
            neutral &= !pink((int) field(neutralCard.getBackground(), color));
        check(neutral, "generic controls fall back to neutral colors outside a palette theme");
    }

    private static void builder() throws Exception {
        JsonElement golden = new JsonParser()
            .parse(read(Paths.get("src/test/resources/com/science/gtnllib/palette_theme.json")));
        check(
            golden.equals(
                PaletteThemes.build(SAMPLE_THEME, ThemeAPI.DEFAULT_ID, SAMPLE, ThemeStyle.DEFAULT)
                    .getJson()),
            "palette theme builder writes exactly the theme that was written by hand before");
        ThemePalette inverted = SAMPLE.map(color -> color ^ 0x00FFFFFF);
        List<Integer> before = components(SAMPLE), after = components(inverted);
        check(before.size() == 27 && after.size() == 27, "palette maps every color");
        for (int i = 0; i < after.size(); i++) check(
            !after.get(i)
                .equals(before.get(i)) && !pink(after.get(i)),
            "test palette color " + i + " differs from the sample palette and is not pink");
        ThemeStyle style = new ThemeStyle(4, 1, 2, 3, 8, 3, 6, 12, 6, 16, 20, 0.75F);
        PaletteThemes.register(TEST_THEME, ThemeAPI.DEFAULT_ID, inverted, style);
        reloadThemes(SAMPLE_THEME);
        ITheme theme = IThemeApi.get()
            .getTheme(TEST_THEME);
        check(
            theme.getId()
                .equals(TEST_THEME)
                && PaletteThemes.ids()
                    .contains(TEST_THEME)
                && inverted.equals(PaletteThemes.palette(TEST_THEME)),
            "a palette theme registers under its own id");
        paletteControls("test palette", theme, inverted, style);
        reloadThemes(SAMPLE_THEME);
        sameFrame(
            "test palette panel after a second reload",
            IThemeApi.get()
                .getTheme(TEST_THEME)
                .getPanelTheme()
                .getTheme()
                .getBackground(),
            new int[] { inverted.panelTop(), inverted.panelBottom(), inverted.accentDim(), inverted.accent(),
                style.cornerLength(), style.cornerThickness() });
    }

    private static void follow() {
        ThemePalette palette = SAMPLE;
        check(
            palette.follow(0xFF3D9A)
                .equals(palette),
            "following the sample pink accent gives back every sample color");
        check(
            palette.follow(0x12FF3D9A)
                .equals(palette),
            "the accent alpha is ignored");
        ThemePalette cyan = palette.follow(CYAN);
        same("cyan accent", cyan.accent(), 0xFF3DE8FF);
        same("cyan accent dim", cyan.accentDim(), 0x803DE8FF);
        same("cyan hover", cyan.hover(), 0x263DE8FF);
        same("cyan button hover", cyan.buttonHover(), 0xA03DE8FF);
        same("cyan tab hover", cyan.tabHover(), 0x503DE8FF);
        same("cyan slot hover", cyan.slotHover(), 0x503DE8FF);
        same("cyan danger", cyan.danger(), palette.danger());
        List<Integer> base = components(palette), tinted = components(cyan);
        for (int i = 0; i < base.size(); i++) {
            int from = base.get(i), to = tinted.get(i);
            double[] before = FxColor.hsl(from & 0xFFFFFF), after = FxColor.hsl(to & 0xFFFFFF);
            check(!pink(to), "cyan color " + i + " has no pink left");
            check(from >>> 24 == to >>> 24, "cyan color " + i + " keeps its transparency");
            if (before[1] < 0.5) check(
                Math.abs(before[2] - after[2]) < 0.01,
                "cyan color " + i + " keeps the brightness of the pink one");
        }
        check(
            FxColor.hsl(cyan.cursor() & 0xFFFFFF)[2] > 0.9 && FxColor.hsl(cyan.cursorText() & 0xFFFFFF)[2] < 0.25,
            "the slider tag stays a pale tint with dark text");
        check(
            Math.abs(hueOffset(cyan.cursor(), cyan.accent()) - hueOffset(palette.cursor(), palette.accent()))
                < HUE_TOLERANCE
                && Math.abs(hueOffset(cyan.panelTop(), cyan.accent()) - hueOffset(palette.panelTop(), palette.accent()))
                    < HUE_TOLERANCE,
            "tints and the dark panel keep their hue next to the new accent");
        ThemePalette navy = palette.follow(0x2040A0);
        check(
            Math.abs(whiteMix(navy.cursor(), navy.accent()) - whiteMix(palette.cursor(), palette.accent())) < 0.02
                && Math
                    .abs(blackMix(navy.cursorText(), navy.accent()) - blackMix(palette.cursorText(), palette.accent()))
                    < 0.02,
            "pale and dark tints keep their white and black mix toward a darker accent");
        check(
            FxColor.hsl(
                palette.follow(0xFFFFFF)
                    .panelTop() & 0xFFFFFF)[1]
                < 0.01,
            "a grey accent leaves the dark panel untinted");
        ThemePalette grey = palette.follow(0x808080), greyToCyan = grey.follow(CYAN);
        same("grey theme accent", grey.accent(), 0xFF808080);
        same("grey theme following cyan, accent", greyToCyan.accent(), 0xFF3DE8FF);
        same("grey theme following cyan, accent dim", greyToCyan.accentDim(), 0x803DE8FF);
        for (int accent : new int[] { 0x000000, 0xFFFFFF, 0x0000FF }) {
            int readable = palette.readableAccent(accent);
            check(
                FxColor.contrast(readable & 0xFFFFFF, palette.panelTop() & 0xFFFFFF) >= 3 && palette.follow(accent)
                    .accent() == readable,
                "a " + Integer.toHexString(accent) + " accent becomes readable on the dark panel");
        }
        check(palette.readableAccent(0x000000) != 0xFF000000, "a black accent is lifted");
        check(FxColor.hsl(palette.readableAccent(0xFFFFFF) & 0xFFFFFF)[2] <= 0.851, "a white accent is toned down");
        same("pink accent after the readability check", palette.readableAccent(0xFF3D9A), 0xFFFF3D9A);
        check(
            palette.selectedText() == palette.text() && palette.selectedTextShadow(),
            "pink tabs keep white text with a shadow");
        ThemePalette white = palette.follow(0xFFFFFF);
        int tab = FxColor.over(white.buttonHover(), white.panelTop() & 0xFFFFFF);
        check(
            white.selectedText() != white.text() && !white.selectedTextShadow()
                && FxColor.contrast(white.selectedText() & 0xFFFFFF, tab)
                    > FxColor.contrast(white.text() & 0xFFFFFF, tab),
            "bright tabs switch to dark text without a shadow");
        check(
            palette.follow(0x000000)
                .selectedText() == palette.text(),
            "dark tabs keep light text");
    }

    private static void variants() throws Exception {
        reloadThemes(SAMPLE_THEME);
        new PaletteThemes.Reloader().onThemeReload(new ReloadThemeEvent.Pre());
        ITheme registered = IThemeApi.get()
            .getTheme(SAMPLE_THEME);
        ITheme parsed = VariantTheme.parse(
            SAMPLE_THEME,
            IThemeApi.get()
                .getTheme(ThemeAPI.DEFAULT_ID),
            IThemeApi.get()
                .getJavaDefaultThemes(SAMPLE_THEME)
                .get(0)
                .getJson());
        for (WidgetThemeKey<?> key : IThemeApi.get()
            .getWidgetThemeKeys()) {
            WidgetThemeEntry<?> expected = registered.getWidgetTheme(key), actual = parsed.getWidgetTheme(key);
            check(
                sameWidgetTheme(expected.getTheme(), actual.getTheme())
                    && sameWidgetTheme(expected.getHoverTheme(), actual.getHoverTheme()),
                "a palette variant parses " + key.getFullName() + " the way ModularUI does");
        }
        reloadThemes(SAMPLE_THEME);
        int[] accent = { 0xFF000000 | CYAN };
        LivePalette manual = new LivePalette(() -> SAMPLE_THEME, () -> false);
        manual.accentFrom(() -> accent[0]);
        check(
            manual.current()
                .equals(SAMPLE)
                && manual.accented()
                    .equals(SAMPLE.follow(CYAN)),
            "a live palette that does not follow its accent keeps the theme colors");
        LivePalette following = new LivePalette(() -> SAMPLE_THEME, () -> true);
        following.accentFrom(() -> accent[0]);
        check(
            following.current()
                .equals(SAMPLE.follow(CYAN))
                && following.themed()
                    .equals(SAMPLE),
            "a live palette that follows its accent tints the theme colors");
        accent[0] = LivePalette.NO_ACCENT;
        following.refresh();
        check(
            following.current()
                .equals(SAMPLE),
            "without an accent a following live palette keeps the theme colors");
    }

    private static void choice() {
        check(GTNLLibConfig.theme.isEmpty(), "the theme config starts empty, so every screen keeps its own default");
        String saved = GTNLLibConfig.theme;
        int[] registrations = { 0 };
        PaletteThemes.onRegister(() -> registrations[0]++);
        PaletteThemes.register(TEST_THEME, PaletteThemes.palette(TEST_THEME));
        check(registrations[0] == 2, "registering a palette theme notifies its listeners");
        List<String> choices = Arrays.asList(GTNLLibConfig.themes());
        check(
            choices.contains(SAMPLE_THEME) && choices.contains(TEST_THEME),
            "every registered palette theme is offered in the config");
        GTNLLibConfig.theme = "gtnllib:missing";
        check(
            PaletteThemes.selected(SAMPLE_THEME)
                .equals(SAMPLE_THEME),
            "an unknown theme id falls back to the screen's own theme");
        GTNLLibConfig.theme = TEST_THEME;
        check(
            PaletteThemes.selected(SAMPLE_THEME)
                .equals(TEST_THEME),
            "a registered theme chosen in the config wins over the screen's own theme");
        GTNLLibConfig.theme = saved;
    }

    private static void overrides() throws ReflectiveOperationException {
        StepSlider.Theme defaults = StepSlider.Theme.KEY.getDefaultValue();
        reloadThemes(
            SAMPLE_THEME,
            new JsonBuilder()
                .add(StepSlider.Theme.KEY.getFullName(), new JsonBuilder().add(StepSlider.Theme.FILL, 0xFF00FF00)));
        StepSlider.Theme overriddenSlider = IThemeApi.get()
            .getTheme(SAMPLE_THEME)
            .getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        check(
            overriddenSlider.fill == 0xFF00FF00 && overriddenSlider.track == defaults.track,
            "an override replaces the whole slider object");
        reloadThemes(
            SAMPLE_THEME,
            new JsonBuilder().add(
                LayoutTheme.KEY.getFullName(),
                new JsonBuilder().add(LayoutTheme.SPACING_SMALL, 5)
                    .add(LayoutTheme.ROW_HEIGHT, 3)
                    .add(LayoutTheme.TEXT_SCALE, -1)));
        LayoutTheme packed = LayoutTheme.of(
            IThemeApi.get()
                .getTheme(SAMPLE_THEME));
        check(
            packed.spacingSmall == 5 && packed.padding == ThemeStyle.DEFAULT.padding()
                && packed.rowHeight == packed.buttonHeight
                && packed.textScale == ThemeStyle.DEFAULT.textScale(),
            "a resource pack changes one spacing, keeps the others and cannot make rows shorter than buttons");
        reloadThemes(SAMPLE_THEME);
        check(
            LayoutTheme.of(
                IThemeApi.get()
                    .getTheme(SAMPLE_THEME)).spacingSmall
                == ThemeStyle.DEFAULT.spacingSmall(),
            "theme reload restores spacing");
    }

    public static void reloadThemes(String id, JsonBuilder... overrides) throws ReflectiveOperationException {
        new PaletteThemes.Reloader().onThemeReload(new ReloadThemeEvent.Pre());
        for (JsonBuilder override : overrides) IThemeApi.get()
            .registerTheme(id, override);
        call(ThemeAPI.class, "onReload", ThemeAPI.INSTANCE, new Class<?>[0]);
        ThemeManager.loadThemes(new HashMap<>());
        new PaletteThemes.Reloader().onThemesLoaded(new ReloadThemeEvent.Post());
    }

    public static void paletteControls(String label, ITheme theme, ThemePalette palette, ThemeStyle style)
        throws ReflectiveOperationException {
        WidgetTheme fallback = theme.getFallback()
            .getTheme();
        check(
            fallback.getTextColor() == palette.text() && fallback.getTextShadow() && fallback.getColor() == 0xFFFFFFFF,
            label + " text defaults to the palette text with a shadow");
        sameFrame(
            label + " panel",
            theme.getPanelTheme()
                .getTheme()
                .getBackground(),
            new int[] { palette.panelTop(), palette.panelBottom(), palette.accentDim(), palette.accent(),
                style.cornerLength(), style.cornerThickness() });
        WidgetThemeEntry<WidgetTheme> button = theme.getButtonTheme();
        sameRectangle(
            label + " button",
            button.getTheme()
                .getBackground(),
            palette.button());
        sameRectangle(
            label + " button hover",
            button.getHoverTheme()
                .getBackground(),
            palette.buttonHover());
        same(
            label + " button text",
            button.getTheme()
                .getTextColor(),
            palette.text());
        same(
            label + " button hover text",
            button.getHoverTheme()
                .getTextColor(),
            palette.selectedText());
        check(
            button.getTheme()
                .getTextShadow()
                && button.getHoverTheme()
                    .getTextShadow() == palette.selectedTextShadow(),
            label + " button text has a shadow, hovered button text follows its background");
        WidgetThemeEntry<WidgetTheme> close = theme.getWidgetTheme(IThemeApi.CLOSE_BUTTON);
        check(
            close.getTheme()
                .getBackground() == IDrawable.EMPTY,
            label + " close button has no background until hovered");
        sameRectangle(
            label + " close button hover",
            close.getHoverTheme()
                .getBackground(),
            palette.hover());
        same(
            label + " close button text",
            close.getTheme()
                .getTextColor(),
            palette.text());
        check(
            close.getTheme()
                .getTextShadow(),
            label + " close button text has a shadow");
        sameCard(
            label,
            theme.getWidgetTheme(CardDrawable.KEY)
                .getTheme()
                .getBackground(),
            new int[] { palette.card(), palette.cardEdge(), palette.accent(), style.cardAccentTop(),
                style.cardAccentWidth(), style.cardAccentHeight() });
        WidgetThemeEntry<SlideSwitch.Theme> slideEntry = theme.getWidgetTheme(SlideSwitch.Theme.KEY);
        SlideSwitch.Theme slide = slideEntry.getTheme();
        same(label + " switch track", slide.track, palette.switchOff());
        same(label + " switch track on", slide.trackOn, palette.accent());
        same(label + " switch track dim", slide.trackDim, palette.accentDim());
        same(label + " switch knob", slide.knob, palette.knob());
        same(label + " switch knob off", slide.knobOff, palette.knobOff());
        same(label + " switch text on", slide.textOn, palette.textOn());
        same(label + " switch text dim", slide.textDim, palette.textDim());
        same(label + " switch text off", slide.textOff, palette.textOff());
        check(slide.getBackground() == null, label + " switch rows have no background until hovered");
        sameRectangle(
            label + " switch hover",
            slideEntry.getHoverTheme()
                .getBackground(),
            palette.hover());
        FillBar.Theme bar = theme.getWidgetTheme(FillBar.Theme.KEY)
            .getTheme();
        same(label + " fill bar track", bar.track, palette.track());
        same(label + " fill bar fill", bar.fill, palette.accent());
        StepSlider.Theme slider = theme.getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        same(label + " slider track", slider.track, palette.track());
        same(label + " slider fill", slider.fill, palette.accent());
        same(label + " slider cursor", slider.cursor, palette.cursor());
        same(label + " slider cursor text", slider.cursorText, palette.cursorText());
        same(label + " slider tick", slider.tick, palette.tick());
        same(label + " slider milestone", slider.milestone, palette.milestone());
        SlotTheme slot = theme.getItemSlotTheme()
            .getTheme();
        sameFrame(
            label + " item slot",
            slot.getBackground(),
            new int[] { palette.well(), palette.well(), palette.wellEdge(), 0, 0, 0 });
        same(label + " item slot hover", slot.getSlotHoverColor(), palette.slotHover());
        for (WidgetThemeKey<SlotTheme> key : Arrays.asList(
            IThemeApi.ITEM_SLOT_PLAYER,
            IThemeApi.ITEM_SLOT_PLAYER_HOTBAR,
            IThemeApi.ITEM_SLOT_PLAYER_MAIN_INV,
            IThemeApi.ITEM_SLOT_PLAYER_ARMOR))
            check(
                theme.getWidgetTheme(key)
                    .getTheme()
                    .getBackground() == slot.getBackground(),
                label + " player inventory slot " + key.getFullName() + " uses the item slot");
        TextFieldTheme field = theme.getTextFieldTheme()
            .getTheme();
        sameFrame(
            label + " text field",
            field.getBackground(),
            new int[] { palette.well(), palette.well(), palette.accentDim(), 0, 0, 0 });
        same(label + " text field text", field.getTextColor(), palette.text());
        same(label + " text field selection", field.getMarkedColor(), palette.accentDim());
        same(label + " text field hint", field.getHintColor(), palette.hint());
        check(!field.getTextShadow(), label + " text field text has no shadow");
        sameRectangle(
            label + " scrollbar",
            theme.getScrollbarTheme()
                .getTheme()
                .getBackground(),
            palette.accentDim());
        WidgetThemeEntry<SelectableTheme> tabs = theme.getToggleButtonTheme();
        SelectableTheme tab = tabs.getTheme(), tabHover = tabs.getHoverTheme();
        sameRectangle(label + " tab", tab.getBackground(), palette.button());
        same(label + " tab text", tab.getTextColor(), palette.textOff());
        sameRectangle(
            label + " selected tab",
            tab.getSelected()
                .getBackground(),
            palette.buttonHover());
        same(
            label + " selected tab text",
            tab.getSelected()
                .getTextColor(),
            palette.selectedText());
        check(
            !tab.getTextShadow() && tab.getSelected()
                .getTextShadow() == palette.selectedTextShadow(),
            label + " unselected tab text has no shadow, selected tab text follows its background");
        sameRectangle(label + " tab hover", tabHover.getBackground(), palette.tabHover());
        sameRectangle(
            label + " selected tab hover",
            tabHover.getSelected()
                .getBackground(),
            palette.buttonHover());
        LayoutTheme layout = LayoutTheme.of(theme);
        check(
            layout.spacingSmall == style.spacingSmall() && layout.spacingMedium == style.spacingMedium()
                && layout.spacingLarge == style.spacingLarge()
                && layout.padding == style.padding()
                && layout.buttonHeight == style.buttonHeight()
                && layout.rowHeight == style.rowHeight()
                && layout.textScale == style.textScale(),
            label + " spacing, padding, button and row heights and text scale come from the theme style");
        check(
            theme.getWidgetTheme(ButtonPurpose.NORMAL.key) == button
                && theme.getWidgetTheme(ButtonPurpose.TAB.key) == tabs,
            label + " normal buttons and tabs use the plain button and toggle button themes");
        WidgetThemeEntry<?> primary = theme.getWidgetTheme(ButtonPurpose.PRIMARY.key),
            danger = theme.getWidgetTheme(ButtonPurpose.DANGER.key),
            icon = theme.getWidgetTheme(ButtonPurpose.ICON.key);
        sameRectangle(
            label + " primary button",
            primary.getTheme()
                .getBackground(),
            palette.buttonHover());
        sameRectangle(
            label + " primary button hover",
            primary.getHoverTheme()
                .getBackground(),
            palette.accent());
        same(
            label + " primary button text",
            primary.getTheme()
                .getTextColor(),
            palette.selectedText());
        same(
            label + " primary button hover text",
            primary.getHoverTheme()
                .getTextColor(),
            palette.selectedText());
        sameRectangle(
            label + " danger button",
            danger.getTheme()
                .getBackground(),
            palette.button());
        same(
            label + " danger button text",
            danger.getTheme()
                .getTextColor(),
            palette.danger());
        sameRectangle(
            label + " danger button hover",
            danger.getHoverTheme()
                .getBackground(),
            palette.danger());
        same(
            label + " danger button hover text",
            danger.getHoverTheme()
                .getTextColor(),
            palette.text());
        check(
            icon.getTheme()
                .getBackground() == IDrawable.EMPTY,
            label + " icon button has no background until hovered");
        sameRectangle(
            label + " icon button hover",
            icon.getHoverTheme()
                .getBackground(),
            palette.hover());
        same(
            label + " icon button text",
            icon.getTheme()
                .getTextColor(),
            palette.text());
    }

    public static void same(String what, int actual, int expected) {
        check(
            actual == expected,
            what + " color: expected " + Integer.toHexString(expected) + ", got " + Integer.toHexString(actual));
    }

    private static boolean pink(int color) {
        return (color & 0xFFFFFF) == 0xFF3D9A;
    }

    private static double hueOffset(int color, int accent) {
        return (FxColor.hsl(color & 0xFFFFFF)[0] - FxColor.hsl(accent & 0xFFFFFF)[0] + 360) % 360;
    }

    private static double whiteMix(int color, int accent) {
        double lightness = FxColor.hsl(accent & 0xFFFFFF)[2];
        return (FxColor.hsl(color & 0xFFFFFF)[2] - lightness) / (1 - lightness);
    }

    private static double blackMix(int color, int accent) {
        return FxColor.hsl(color & 0xFFFFFF)[2] / FxColor.hsl(accent & 0xFFFFFF)[2];
    }

    private static boolean sameWidgetTheme(Object expected, Object actual) throws IllegalAccessException {
        if (expected == actual) return true;
        if (expected == null || actual == null || expected.getClass() != actual.getClass()) return false;
        if (expected instanceof IJsonSerializable serializable) {
            JsonObject first = new JsonObject(), second = new JsonObject();
            serializable.saveToJson(first);
            ((IJsonSerializable) actual).saveToJson(second);
            return first.equals(second);
        }
        for (Class<?> type = expected.getClass(); type != Object.class; type = type.getSuperclass())
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Object first = field.get(expected), second = field.get(actual);
                if (field.getType()
                    .isPrimitive() ? !first.equals(second) : !sameWidgetTheme(first, second)) return false;
            }
        return true;
    }

    private static List<Integer> components(ThemePalette palette) {
        List<Integer> colors = new ArrayList<>();
        palette.map(color -> {
            colors.add(color);
            return color;
        });
        return colors;
    }

    private static void sameRectangle(String what, IDrawable drawable, int color) throws ReflectiveOperationException {
        check(drawable instanceof Rectangle, what + " is a plain rectangle");
        if (drawable instanceof Rectangle)
            for (String corner : new String[] { "colorTL", "colorTR", "colorBL", "colorBR" })
                same(what + " " + corner, (int) field(drawable, corner), color);
    }

    private static void sameFrame(String what, IDrawable drawable, int[] values) throws ReflectiveOperationException {
        check(drawable instanceof FrameDrawable, what + " is a frame");
        if (!(drawable instanceof FrameDrawable)) return;
        String[] names = { "top", "bottom", "border", "corner", "cornerLength", "cornerThickness" };
        for (int i = 0; i < names.length; i++) same(what + " " + names[i], (int) field(drawable, names[i]), values[i]);
    }

    private static void sameCard(String label, IDrawable drawable, int[] values) throws ReflectiveOperationException {
        check(drawable instanceof CardDrawable, label + " card is a card");
        if (!(drawable instanceof CardDrawable)) return;
        String[] names = { "fill", "edge", "accent", "accentTop", "accentWidth", "accentHeight" };
        for (int i = 0; i < names.length; i++)
            same(label + " card " + names[i], (int) field(drawable, names[i]), values[i]);
    }
}
