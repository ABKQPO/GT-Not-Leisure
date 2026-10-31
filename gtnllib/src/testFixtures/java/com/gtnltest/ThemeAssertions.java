package com.gtnltest;

import static com.gtnltest.TestSupport.call;
import static com.gtnltest.TestSupport.check;
import static com.gtnltest.TestSupport.field;
import static com.gtnltest.TestSupport.read;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IDrawable;
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
import com.gtnllib.gui.theme.ButtonPurpose;
import com.gtnllib.gui.theme.PaletteThemes;
import com.gtnllib.gui.theme.ThemePalette;
import com.gtnllib.gui.theme.ThemeSkin;
import com.gtnllib.gui.theme.ThemeSkin.Part;
import com.gtnllib.gui.theme.ThemeStyle;
import com.gtnllib.gui.widget.CardDrawable;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.FillBar;
import com.gtnllib.gui.widget.FrameDrawable;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class ThemeAssertions {

    private ThemeAssertions() {}

    public static boolean compactDrawable(String text) throws ReflectiveOperationException {
        Field chars = StepSlider.class.getDeclaredField("COMPACT_CHARS");
        chars.setAccessible(true);
        String charset = (String) chars.get(null);
        return text.chars()
            .allMatch(c -> charset.indexOf(c) >= 0);
    }

    public static void reloadThemes(String id, JsonBuilder... overrides) throws ReflectiveOperationException {
        new PaletteThemes.Reloader().onThemeReload(new ReloadThemeEvent.Pre());
        for (JsonBuilder override : overrides) IThemeApi.get()
            .registerTheme(id, override);
        call(ThemeAPI.class, "onReload", ThemeAPI.INSTANCE, new Class<?>[0]);
        ThemeManager.loadThemes(new Object2ObjectOpenHashMap<>());
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

    private static void sameRectangle(String what, IDrawable drawable, int color) throws ReflectiveOperationException {
        check(drawable instanceof Rectangle, what + " is a plain rectangle");
        if (drawable instanceof Rectangle)
            for (String corner : new String[] { "colorTL", "colorTR", "colorBL", "colorBR" })
                same(what + " " + corner, (int) field(drawable, corner), color);
    }

    public static void sameFrame(String what, IDrawable drawable, int[] values) throws ReflectiveOperationException {
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
