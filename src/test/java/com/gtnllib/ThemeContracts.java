package com.gtnllib;

import static com.gtnllib.GTNLLibContractTest.RESOURCES;
import static com.gtnllib.GTNLLibContractTest.SOURCES;
import static com.gtnllib.GTNLLibContractTest.call;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.read;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import net.minecraft.util.ResourceLocation;

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
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.theme.ButtonPurpose;
import com.gtnllib.gui.theme.LivePalette;
import com.gtnllib.gui.theme.MetalSkin;
import com.gtnllib.gui.theme.PaletteThemes;
import com.gtnllib.gui.theme.ThemePalette;
import com.gtnllib.gui.theme.ThemeSkin;
import com.gtnllib.gui.theme.ThemeSkin.Part;
import com.gtnllib.gui.theme.ThemeStyle;
import com.gtnllib.gui.theme.VariantTheme;
import com.gtnllib.gui.widget.CardDrawable;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.FillBar;
import com.gtnllib.gui.widget.FrameDrawable;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SkinDrawable;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;
import com.gtnllib.utils.ColorMath;

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
    private static final String SAMPLE_THEME = "gtnllib:contract_sample", TEST_THEME = "gtnllib:contract_test",
        SKIN_THEME = "gtnllib:contract_skin";
    private static final int CYAN = 0x3DE8FF;
    private static final double HUE_TOLERANCE = 6;
    private static final int MAX_GRAY_SPREAD = 12;

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
        skins();
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
            .parse(read(Paths.get("src/test/resources/com/gtnllib/palette_theme.json")));
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
            double[] before = ColorMath.hsl(from & 0xFFFFFF), after = ColorMath.hsl(to & 0xFFFFFF);
            check(!pink(to), "cyan color " + i + " has no pink left");
            check(from >>> 24 == to >>> 24, "cyan color " + i + " keeps its transparency");
            if (before[1] < 0.5) check(
                Math.abs(before[2] - after[2]) < 0.01,
                "cyan color " + i + " keeps the brightness of the pink one");
        }
        check(
            ColorMath.hsl(cyan.cursor() & 0xFFFFFF)[2] > 0.9 && ColorMath.hsl(cyan.cursorText() & 0xFFFFFF)[2] < 0.25,
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
            ColorMath.hsl(
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
                ColorMath.contrast(readable & 0xFFFFFF, palette.panelTop() & 0xFFFFFF) >= 3 && palette.follow(accent)
                    .accent() == readable,
                "a " + Integer.toHexString(accent) + " accent becomes readable on the dark panel");
        }
        check(palette.readableAccent(0x000000) != 0xFF000000, "a black accent is lifted");
        check(ColorMath.hsl(palette.readableAccent(0xFFFFFF) & 0xFFFFFF)[2] <= 0.851, "a white accent is toned down");
        same("pink accent after the readability check", palette.readableAccent(0xFF3D9A), 0xFFFF3D9A);
        check(
            palette.selectedText() == palette.text() && palette.selectedTextShadow(),
            "pink tabs keep white text with a shadow");
        ThemePalette white = palette.follow(0xFFFFFF);
        int tab = ColorMath.over(white.buttonHover(), white.panelTop() & 0xFFFFFF);
        check(
            white.selectedText() != white.text() && !white.selectedTextShadow()
                && ColorMath.contrast(white.selectedText() & 0xFFFFFF, tab)
                    > ColorMath.contrast(white.text() & 0xFFFFFF, tab),
            "bright tabs switch to dark text without a shadow");
        check(
            palette.follow(0x000000)
                .selectedText() == palette.text(),
            "dark tabs keep light text");
        same("textured buttons light up with the opaque hover color", palette.solidHover(), 0xFFFF3D9A);
        check(
            palette.textOn(palette.solidHover()) == (0xFF000000 | palette.panelBottom())
                && palette.textOn(0xFF101010) == palette.text(),
            "text on a solid pink button turns dark, text on a dark button stays light");
        check(
            palette.brightAccent() >>> 24 == 0xFF
                && ColorMath.hsl(palette.brightAccent() & 0xFFFFFF)[2] > ColorMath.hsl(palette.accent() & 0xFFFFFF)[2],
            "a hovered primary button is a lighter, opaque accent so it still reacts to the mouse");
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

    private static void skins() throws IOException, ReflectiveOperationException {
        SkinDrawable panel = new SkinDrawable()
            .texture(new ResourceLocation("gtnllib", "textures/test/panel.png"), 28, 28)
            .border(10, 10, 10, 10)
            .tiled(true)
            .glow(new ResourceLocation("gtnllib", "textures/test/panel_glow.png"));
        List<SkinDrawable.Piece> exact = panel.pieces(0, 0, 28, 28);
        boolean oneToOne = exact.size() == 9;
        for (SkinDrawable.Piece piece : exact)
            oneToOne &= Math.round(piece.u0() * 28) == piece.x() && Math.round(piece.v0() * 28) == piece.y()
                && Math.round((piece.u1() - piece.u0()) * 28) == piece.w()
                && Math.round((piece.v1() - piece.v0()) * 28) == piece.h();
        check(oneToOne, "a nine-slice texture drawn at its own size puts every texel on one pixel");
        List<SkinDrawable.Piece> wide = panel.pieces(5, 7, 200, 90);
        SkinDrawable.Piece corner = wide.get(0), middle = wide.get(4), last = wide.get(8);
        check(
            wide.size() == 9 && corner.x() == 5
                && corner.y() == 7
                && corner.w() == 10
                && corner.h() == 10
                && corner.tileWidth() == 0
                && last.x() == 195
                && last.y() == 87
                && last.w() == 10
                && last.h() == 10,
            "corners keep their texel size at the four corners of a larger area");
        check(
            middle.x() == 15 && middle.y() == 17
                && middle.w() == 180
                && middle.h() == 70
                && middle.tileWidth() == 8
                && middle.tileHeight() == 8
                && wide.get(1)
                    .tileWidth() == 8
                && wide.get(1)
                    .tileHeight() == 10
                && wide.get(3)
                    .tileWidth() == 10
                && wide.get(3)
                    .tileHeight() == 8,
            "a tiled texture repeats its edges and middle at their texel size");
        boolean stretched = true;
        for (SkinDrawable.Piece piece : panel.copy()
            .tiled(false)
            .pieces(0, 0, 200, 90)) stretched &= piece.tileWidth() == 0 && piece.tileHeight() == 0;
        check(stretched, "a texture that is not tiled stretches its edges and middle");
        check(
            Arrays.equals(SkinDrawable.fit(14, 4, 4), new int[] { 4, 4 })
                && Arrays.equals(SkinDrawable.fit(6, 4, 2), new int[] { 4, 2 })
                && Arrays.equals(SkinDrawable.fit(3, 4, 2), new int[] { 2, 1 })
                && Arrays.equals(SkinDrawable.fit(5, 0, 0), new int[] { 0, 0 }),
            "borders shrink in proportion when the area is smaller than both borders");
        int covered = 0;
        boolean positive = true;
        for (SkinDrawable.Piece piece : panel.pieces(0, 0, 12, 6)) {
            positive &= piece.w() > 0 && piece.h() > 0;
            covered += piece.w() * piece.h();
        }
        check(positive && covered == 12 * 6, "an area smaller than the borders is still covered exactly once");
        SkinDrawable lit = panel.copy()
            .glowColor(0xA0123456),
            bare = new SkinDrawable().texture(new ResourceLocation("gtnllib", "textures/test/slot.png"), 18, 18)
                .border(2, 2, 2, 2);
        check(
            lit.equals(DrawableSerialization.deserialize(DrawableSerialization.serialize(lit)))
                && bare.equals(DrawableSerialization.deserialize(DrawableSerialization.serialize(bare))),
            "a texture skin survives the theme JSON with its glow texture and glow color");
        IDrawable fallback = new Rectangle();
        SkinDrawable rule = new SkinDrawable()
            .texture(new ResourceLocation("gtnllib", "textures/test/divider.png"), 32, 2)
            .border(26, 0, 2, 0)
            .glow(new ResourceLocation("gtnllib", "textures/test/divider_glow.png"));
        ThemeSkin skin = ThemeSkin.NONE.with(Part.PANEL, panel)
            .with(Part.BUTTON, bare)
            .with(Part.SLIDER_TRACK, bare)
            .with(Part.SLIDER_TAG, lit)
            .with(Part.SWITCH_KNOB, bare)
            .with(Part.DIVIDER, rule)
            .with(Part.CLOSE, bare);
        SkinDrawable before = panel.copy();
        panel.border(1, 1, 1, 1);
        check(
            ThemeSkin.NONE.plain(Part.PANEL, () -> fallback) == fallback && !ThemeSkin.NONE.has(Part.PANEL)
                && skin.plain(Part.CARD, () -> fallback) == fallback
                && skin.plain(Part.PANEL, () -> fallback)
                    .equals(before)
                && skin.lit(Part.PANEL, 0xFF00FF00, () -> fallback)
                    .equals(
                        before.copy()
                            .glowColor(0xFF00FF00)),
            "a skin keeps its own copy of each texture and leaves missing parts to the drawn fallback");
        PaletteThemes.register(SKIN_THEME, ThemeAPI.DEFAULT_ID, SAMPLE, ThemeStyle.DEFAULT, skin);
        reloadThemes(SKIN_THEME);
        paletteControls(
            "skinned",
            IThemeApi.get()
                .getTheme(SKIN_THEME),
            SAMPLE,
            ThemeStyle.DEFAULT,
            skin);
        ThemePalette cyan = SAMPLE.follow(CYAN);
        paletteControls("skinned cyan", PaletteThemes.variant(SKIN_THEME, cyan), cyan, ThemeStyle.DEFAULT, skin);
        metalSkin();
        StepSlider.Theme recolored = (StepSlider.Theme) call(
            StepSlider.class,
            "resolveColors",
            new StepSlider().colors(1, 2, 3, 4),
            new Class<?>[] { WidgetTheme.class },
            IThemeApi.get()
                .getTheme(SKIN_THEME)
                .getWidgetTheme(StepSlider.Theme.KEY)
                .getTheme());
        check(
            recolored.fill == 2 && recolored.trackSkin.equals(skin.get(Part.SLIDER_TRACK))
                && recolored.tagSkin.equals(skin.get(Part.SLIDER_TAG)),
            "a slider with its own colors keeps the theme textures");
    }

    private static void metalSkin() throws IOException, ReflectiveOperationException {
        for (Part part : Part.values()) {
            check(MetalSkin.SKIN.has(part), "the metal skin has a texture for the " + part);
            if (!MetalSkin.SKIN.has(part)) continue;
            IDrawable skin = MetalSkin.SKIN.plain(part, () -> IDrawable.EMPTY);
            ResourceLocation texture = (ResourceLocation) field(skin, "texture"),
                glow = (ResourceLocation) field(skin, "glow");
            int width = (int) field(skin, "width"), height = (int) field(skin, "height");
            BufferedImage base = skinImage(texture);
            check(
                base != null && base.getWidth() == width && base.getHeight() == height,
                part + " texture " + texture + " exists at its declared size " + width + "x" + height);
            if (base != null)
                check(neutral(base), part + " texture is gray metal, so the screen can follow the glow color");
            if (glow == null) continue;
            BufferedImage mask = skinImage(glow);
            check(
                mask != null && mask.getWidth() == width && mask.getHeight() == height && whiteMask(mask),
                part + " glow " + glow + " is a white mask of the same size, tinted by the theme accent");
        }
    }

    private static BufferedImage skinImage(ResourceLocation location) throws IOException {
        Path path = RESOURCES.resolve(location.getResourcePath());
        return location.getResourceDomain()
            .equals(GTNLLib.MODID) && Files.isRegularFile(path) ? ImageIO.read(path.toFile()) : null;
    }

    private static boolean neutral(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getRGB(x, y), r = argb >> 16 & 255, g = argb >> 8 & 255, b = argb & 255;
            if ((argb >>> 24) != 0 && Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b)) > MAX_GRAY_SPREAD)
                return false;
        }
        return true;
    }

    private static boolean whiteMask(BufferedImage image) {
        boolean any = false;
        for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getRGB(x, y);
            if ((argb >>> 24) == 0) continue;
            any = true;
            if ((argb & 0xFFFFFF) != 0xFFFFFF) return false;
        }
        return any;
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
        paletteControls(label, theme, palette, style, ThemeSkin.NONE);
    }

    public static void paletteControls(String label, ITheme theme, ThemePalette palette, ThemeStyle style,
        ThemeSkin skin) throws ReflectiveOperationException {
        skinControls(label, theme, palette, skin);
        widgetSkins(label, theme, skin);
        boolean solid = skin.has(Part.BUTTON);
        int litText = solid ? palette.textOn(palette.solidHover()) : palette.selectedText(),
            brightText = solid ? palette.textOn(palette.brightAccent()) : palette.selectedText();
        WidgetTheme fallback = theme.getFallback()
            .getTheme();
        check(
            fallback.getTextColor() == palette.text() && fallback.getTextShadow() && fallback.getColor() == 0xFFFFFFFF,
            label + " text defaults to the palette text with a shadow");
        if (!skin.has(Part.PANEL)) sameFrame(
            label + " panel",
            theme.getPanelTheme()
                .getTheme()
                .getBackground(),
            new int[] { palette.panelTop(), palette.panelBottom(), palette.accentDim(), palette.accent(),
                style.cornerLength(), style.cornerThickness() });
        WidgetThemeEntry<WidgetTheme> button = theme.getButtonTheme();
        if (!skin.has(Part.BUTTON)) sameRectangle(
            label + " button",
            button.getTheme()
                .getBackground(),
            palette.button());
        if (!skin.has(Part.BUTTON)) sameRectangle(
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
            litText);
        check(
            button.getTheme()
                .getTextShadow()
                && button.getHoverTheme()
                    .getTextShadow() == (litText == palette.text()),
            label + " button text has a shadow, hovered button text follows its background");
        WidgetThemeEntry<WidgetTheme> close = theme.getWidgetTheme(IThemeApi.CLOSE_BUTTON);
        if (!skin.has(Part.CLOSE)) check(
            close.getTheme()
                .getBackground() == IDrawable.EMPTY,
            label + " close button has no background until hovered");
        if (!skin.has(Part.CLOSE)) sameRectangle(
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
        if (!skin.has(Part.CARD)) sameCard(
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
        if (!skin.has(Part.SLOT)) sameFrame(
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
        if (!skin.has(Part.SLOT)) sameFrame(
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
        if (!skin.has(Part.BUTTON)) sameRectangle(label + " tab", tab.getBackground(), palette.button());
        same(label + " tab text", tab.getTextColor(), palette.textOff());
        if (!skin.has(Part.BUTTON)) sameRectangle(
            label + " selected tab",
            tab.getSelected()
                .getBackground(),
            palette.buttonHover());
        same(
            label + " selected tab text",
            tab.getSelected()
                .getTextColor(),
            litText);
        check(
            !tab.getTextShadow() && tab.getSelected()
                .getTextShadow() == (litText == palette.text()),
            label + " unselected tab text has no shadow, selected tab text follows its background");
        if (!skin.has(Part.BUTTON)) sameRectangle(label + " tab hover", tabHover.getBackground(), palette.tabHover());
        if (!skin.has(Part.BUTTON)) sameRectangle(
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
        if (!skin.has(Part.BUTTON)) sameRectangle(
            label + " primary button",
            primary.getTheme()
                .getBackground(),
            palette.buttonHover());
        if (!skin.has(Part.BUTTON)) sameRectangle(
            label + " primary button hover",
            primary.getHoverTheme()
                .getBackground(),
            palette.accent());
        same(
            label + " primary button text",
            primary.getTheme()
                .getTextColor(),
            litText);
        same(
            label + " primary button hover text",
            primary.getHoverTheme()
                .getTextColor(),
            brightText);
        if (!skin.has(Part.BUTTON)) sameRectangle(
            label + " danger button",
            danger.getTheme()
                .getBackground(),
            palette.button());
        same(
            label + " danger button text",
            danger.getTheme()
                .getTextColor(),
            palette.danger());
        if (!skin.has(Part.BUTTON)) sameRectangle(
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

    private static void skinControls(String label, ITheme theme, ThemePalette palette, ThemeSkin skin) {
        WidgetThemeEntry<WidgetTheme> button = theme.getButtonTheme();
        WidgetThemeEntry<SelectableTheme> tabs = theme.getToggleButtonTheme();
        WidgetThemeEntry<?> primary = theme.getWidgetTheme(ButtonPurpose.PRIMARY.key),
            danger = theme.getWidgetTheme(ButtonPurpose.DANGER.key);
        skinned(
            label + " panel",
            theme.getPanelTheme()
                .getTheme()
                .getBackground(),
            skin,
            Part.PANEL,
            palette.accent());
        skinned(
            label + " card",
            theme.getWidgetTheme(CardDrawable.KEY)
                .getTheme()
                .getBackground(),
            skin,
            Part.CARD,
            palette.accent());
        skinned(
            label + " button",
            button.getTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            0);
        skinned(
            label + " button hover",
            button.getHoverTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.solidHover());
        skinned(
            label + " tab",
            tabs.getTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            0);
        skinned(
            label + " selected tab",
            tabs.getTheme()
                .getSelected()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.solidHover());
        skinned(
            label + " tab hover",
            tabs.getHoverTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.tabHover());
        skinned(
            label + " selected tab hover",
            tabs.getHoverTheme()
                .getSelected()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.solidHover());
        skinned(
            label + " primary button",
            primary.getTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.solidHover());
        skinned(
            label + " primary button hover",
            primary.getHoverTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.brightAccent());
        skinned(
            label + " danger button",
            danger.getTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            0);
        skinned(
            label + " danger button hover",
            danger.getHoverTheme()
                .getBackground(),
            skin,
            Part.BUTTON,
            palette.danger());
        WidgetThemeEntry<WidgetTheme> close = theme.getWidgetTheme(IThemeApi.CLOSE_BUTTON);
        skinned(
            label + " close button",
            close.getTheme()
                .getBackground(),
            skin,
            Part.CLOSE,
            0);
        skinned(
            label + " close button hover",
            close.getHoverTheme()
                .getBackground(),
            skin,
            Part.CLOSE,
            palette.danger());
        skinned(
            label + " item slot",
            theme.getItemSlotTheme()
                .getTheme()
                .getBackground(),
            skin,
            Part.SLOT,
            0);
        skinned(
            label + " text field",
            theme.getTextFieldTheme()
                .getTheme()
                .getBackground(),
            skin,
            Part.SLOT,
            0);
    }

    private static void widgetSkins(String label, ITheme theme, ThemeSkin skin) {
        WidgetThemeEntry<StepSlider.Theme> slider = theme.getWidgetTheme(StepSlider.Theme.KEY);
        WidgetThemeEntry<SlideSwitch.Theme> slide = theme.getWidgetTheme(SlideSwitch.Theme.KEY);
        WidgetThemeEntry<FillBar.Theme> bar = theme.getWidgetTheme(FillBar.Theme.KEY);
        WidgetThemeEntry<Divider.Theme> divider = theme.getWidgetTheme(Divider.Theme.KEY);
        for (StepSlider.Theme read : Arrays.asList(slider.getTheme(), slider.getHoverTheme())) check(
            Objects.equals(read.trackSkin, skin.get(Part.SLIDER_TRACK))
                && Objects.equals(read.fillSkin, skin.get(Part.SLIDER_FILL))
                && Objects.equals(read.tagSkin, skin.get(Part.SLIDER_TAG)),
            label + " slider track, fill and tag textures come from the skin, also while hovered");
        for (SlideSwitch.Theme read : Arrays.asList(slide.getTheme(), slide.getHoverTheme())) check(
            Objects.equals(read.trackSkin, skin.get(Part.SWITCH_TRACK))
                && Objects.equals(read.knobSkin, skin.get(Part.SWITCH_KNOB)),
            label + " switch track and knob textures come from the skin, also while hovered");
        for (FillBar.Theme read : Arrays.asList(bar.getTheme(), bar.getHoverTheme())) check(
            Objects.equals(read.trackSkin, skin.get(Part.SLIDER_TRACK))
                && Objects.equals(read.fillSkin, skin.get(Part.SLIDER_FILL)),
            label + " fill bar shares the slider track and fill textures");
        for (Divider.Theme read : Arrays.asList(divider.getTheme(), divider.getHoverTheme())) check(
            Objects.equals(read.skin, skin.get(Part.DIVIDER)),
            label + " section divider texture comes from the skin");
    }

    private static void skinned(String what, IDrawable drawable, ThemeSkin skin, Part part, int glow) {
        if (!skin.has(part)) return;
        check(
            skin.lit(part, glow, () -> IDrawable.EMPTY)
                .equals(drawable),
            what + " is the "
                + part
                + " texture"
                + (glow == 0 ? " without glow" : " glowing " + Integer.toHexString(glow)));
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
        return (ColorMath.hsl(color & 0xFFFFFF)[0] - ColorMath.hsl(accent & 0xFFFFFF)[0] + 360) % 360;
    }

    private static double whiteMix(int color, int accent) {
        double lightness = ColorMath.hsl(accent & 0xFFFFFF)[2];
        return (ColorMath.hsl(color & 0xFFFFFF)[2] - lightness) / (1 - lightness);
    }

    private static double blackMix(int color, int accent) {
        return ColorMath.hsl(color & 0xFFFFFF)[2] / ColorMath.hsl(accent & 0xFFFFFF)[2];
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
