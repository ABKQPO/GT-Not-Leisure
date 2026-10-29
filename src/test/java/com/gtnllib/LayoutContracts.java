package com.gtnllib;

import static com.gtnllib.GTNLLibContractTest.SOURCES;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.read;
import static com.gtnllib.GTNLLibContractTest.rejects;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.LayoutMath;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.settings.BlockPosFields;
import com.gtnllib.gui.settings.SettingControls;
import com.gtnllib.gui.settings.SettingControls.Slider;
import com.gtnllib.gui.settings.SettingControls.Toggle;
import com.gtnllib.gui.widget.CloseButton;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.FadingNotice;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;
import com.gtnllib.utils.HotbarMessages;
import com.gtnllib.utils.MathUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class LayoutContracts {

    private LayoutContracts() {}

    static void run() throws IOException, ReflectiveOperationException {
        layoutRules();
        adaptiveRules();
        mathRules();
        noticeRules();
        layoutSources();
        settingControls();
        blockPosFields();
    }

    private static void blockPosFields() {
        check(BlockPosFields.typed("12", 12.37, 0.5) == 12.37, "an untouched block field keeps the exact coordinate");
        check(BlockPosFields.typed("-3", -2.5, 0.5) == -2.5, "an untouched negative block keeps its exact coordinate");
        check(BlockPosFields.typed("40", 12.37, 0.5) == 40.5, "a typed block on X or Z is its middle");
        check(BlockPosFields.typed("70", 64, 0) == 70, "a typed block on Y is its floor");
        check(BlockPosFields.typed("1,234", 0, 0.5) == 1234.5, "a grouped number still reads as one block");
        check(
            BlockPosFields.typed("", 7.25, 0.5) == 7.25 && BlockPosFields.typed("-", 7.25, 0.5) == 7.25,
            "a field that is not a number keeps the exact coordinate");
    }

    private static void settingControls() throws ReflectiveOperationException {
        int savedMotes = GTNLLibConfig.maxWorldMotes;
        float savedScale = GTNLLibConfig.hudScale;
        boolean savedHud = GTNLLibConfig.enableHud;
        try {
            Slider motes = SettingControls.slider(null, GTNLLibConfig.class, "maxWorldMotes", 128, StepSlider.whole());
            Config.RangeInt range = GTNLLibConfig.class.getField("maxWorldMotes")
                .getAnnotation(Config.RangeInt.class);
            check(
                motes.min() == range.min() && motes.max() == range.max() && motes.steps() == 31,
                "a setting slider takes its range from the config annotation");
            check(
                motes.initial() == GTNLLibConfig.DEFAULT_MAX_WORLD_MOTES,
                "a setting slider takes its default from the config annotation");
            check(
                motes.lang()
                    .equals("gtnllib.gui.config.client.max_world_motes"),
                "a setting slider takes its lang key from the config annotation");
            motes.set(1000.4);
            check(GTNLLibConfig.maxWorldMotes == 1024, "a setting slider snaps an int setting to its step");
            motes.set(300);
            check(GTNLLibConfig.maxWorldMotes == 256, "a setting slider rounds to the nearest step");
            check(motes.value() == 256, "a setting slider reads the field back");
            motes.reset();
            check(GTNLLibConfig.maxWorldMotes == 1024, "resetting a setting slider writes its default back");
            Slider scale = SettingControls.slider(null, GTNLLibConfig.class, "hudScale", 0.25, StepSlider.decimals(2));
            scale.set(1.6);
            check(GTNLLibConfig.hudScale == 1.5F, "a setting slider writes a float setting");
            Toggle hud = SettingControls.toggle(null, GTNLLibConfig.class, "enableHud");
            GTNLLibConfig.enableHud = !hud.initial();
            check(hud.on() != hud.initial(), "a setting switch reads the field");
            hud.reset();
            check(GTNLLibConfig.enableHud == hud.initial(), "resetting a setting switch writes its default back");
            check(
                SettingControls.tagWidth(Arrays.asList(motes, scale, hud))
                    == Math.max(motes.widestTag(), scale.widestTag()),
                "the shared value tag is as wide as the widest slider value");
            check(
                rejects(() -> SettingControls.slider(null, GTNLLibConfig.class, "missing", 1, StepSlider.whole())),
                "a setting slider for a field that does not exist is refused");
            check(
                rejects(() -> SettingControls.slider(null, GTNLLibConfig.class, "theme", 1, StepSlider.whole())),
                "a setting slider for a text setting is refused");
            check(
                rejects(() -> SettingControls.toggle(null, GTNLLibConfig.class, "hudScale")),
                "a setting switch for a number setting is refused");
        } finally {
            GTNLLibConfig.maxWorldMotes = savedMotes;
            GTNLLibConfig.hudScale = savedScale;
            GTNLLibConfig.enableHud = savedHud;
        }
    }

    private static void mathRules() {
        check(
            MathUtils.clampUnit(-0.5F) == 0F && MathUtils.clampUnit(1.5F) == 1F
                && MathUtils.clampUnit(0.25F) == 0.25F
                && MathUtils.clampUnit(-2.0) == 0
                && MathUtils.clampUnit(3.0) == 1
                && MathUtils.clampUnit(0.75) == 0.75,
            "values clamp into 0..1 and values inside stay as they are");
        check(
            Float.isNaN(MathUtils.clampUnit(Float.NaN)) && Double.isNaN(MathUtils.clampUnit(Double.NaN)),
            "the plain clamp passes NaN through like the code it replaced");
        check(
            MathUtils.clampUnit(Float.NaN, 0F) == 0F && MathUtils.clampUnit(Float.POSITIVE_INFINITY, 1F) == 1F
                && MathUtils.clampUnit(Float.NEGATIVE_INFINITY, 0.5F) == 0.5F
                && MathUtils.clampUnit(2F, 0F) == 1F,
            "the clamp with a fallback replaces every non-finite value and clamps the rest");
    }

    private static void noticeRules() {
        check(
            FadingNotice.opacity(0, 3000) == 1F && FadingNotice.opacity(2500, 3000) == 1F
                && FadingNotice.opacity(2750, 3000) == 0.5F
                && FadingNotice.opacity(3000, 3000) == 0F
                && FadingNotice.opacity(-1, 3000) == 0F,
            "a notice stays solid, fades out over its last half second and is gone afterwards");
        IChatComponent plain = new ChatComponentText("plain");
        IChatComponent red = new ChatComponentText("red");
        red.getChatStyle()
            .setColor(EnumChatFormatting.RED);
        check(
            HotbarMessages.withDefaultColor(plain, EnumChatFormatting.WHITE)
                .getChatStyle()
                .getColor() == EnumChatFormatting.WHITE
                && plain.getChatStyle()
                    .getColor() == null
                && HotbarMessages.withDefaultColor(red, EnumChatFormatting.WHITE)
                    .getChatStyle()
                    .getColor() == EnumChatFormatting.RED,
            "a hotbar message without a colour turns white on a copy, and a coloured one keeps its colour");
    }

    private static void adaptiveRules() throws ReflectiveOperationException {
        check(
            Arrays.equals(LayoutMath.offsets(new int[] { 79, 79 }, 2), new int[] { 0, 81 })
                && LayoutMath.offsets(new int[0], 2).length == 0,
            "cells start after the cells and gaps before them");
        check(
            LayoutMath.fit(480, 4, 176, 256) == 256 && LayoutMath.fit(220, 4, 176, 256) == 212
                && LayoutMath.fit(100, 4, 176, 256) == 176,
            "a panel follows the screen between its narrowest and widest size");
        check(
            LayoutMath.fitRows(77, 16, 3, 11) == 64 && LayoutMath.fitRows(500, 16, 3, 11) == 176
                && LayoutMath.fitRows(10, 16, 3, 11) == 48
                && LayoutMath.fitRows(500, 16, 3, 2) == 32
                && LayoutMath.fitRows(100, 0, 3, 11) == 0,
            "a scrolling area shows whole rows, at least the minimum, never more than its content");
        int[] width = { 176 };
        Layout rows = Layout.rows(LayoutTheme.DEFAULT, () -> width[0]);
        List<SlideSwitch> five = new ObjectArrayList<>();
        for (int i = 0; i < 5; i++) five.add(new SlideSwitch());
        Layout.Grid grid = rows.adaptiveGrid(2, 76, 3, five);
        check(
            rows.available() == 156 && grid.columns() == 2
                && grid.rowCount() == 3
                && grid.contentHeight() == 48
                && grid.cellLeft(1) == 89
                && grid.cellWidth(1) == 77
                && grid.cellTop(2) == 16
                && grid.getChildren()
                    .size() == 5,
            "an adaptive grid on a narrow panel keeps two columns");
        width[0] = 256;
        check(
            rows.available() == 236 && grid.columns() == 3
                && grid.rowCount() == 2
                && grid.contentHeight() == 32
                && grid.cellLeft(2) == 169
                && grid.cellWidth(2) == 77
                && grid.cellTop(3) == 16,
            "the same grid takes a third column once the panel grows");
        width[0] = 120;
        check(
            grid.columns() == 1 && grid.rowCount() == 5 && grid.cellWidth(4) == 100 && grid.cellLeft(4) == 10,
            "and falls back to one full-width column when the panel shrinks");
        check(
            rows.stackHeight(Arrays.asList(rows.section(IKey.str("title"), 0), grid)) == 16 + grid.contentHeight(),
            "a stack of rows adds one row per line and the current height of each grid");
        IWidget rule = rows.section(IKey.str("title"), 0xFF123456)
            .getChildren()
            .get(1);
        check(
            rule instanceof Divider && (int) field(rule, "color") == 0xFF123456,
            "a section title sits over a themed divider in the title color");
        check(
            CloseButton.inset(8, LayoutTheme.DEFAULT) == 6 && CloseButton.inset(10, LayoutTheme.DEFAULT) == 8
                && CloseButton.reserve(8, LayoutTheme.DEFAULT) == 11
                && CloseButton.inset(1, LayoutTheme.DEFAULT) == 0,
            "a close button sits one small gap outside the content padding and the title leaves room for it");
    }

    private static void layoutRules() {
        int available = 160, gap = 2, shortSwitch = SlideSwitch.widthFor(56);
        check(
            SlideSwitch.widthFor(0) == 17 && shortSwitch == 73,
            "a switch needs its track, its label and a small margin");
        check(LayoutMath.columns(available, gap, shortSwitch, 2) == 2, "short labels keep two columns");
        for (int text : new int[] { 64, 120, 400 }) {
            int columns = LayoutMath.columns(available, gap, SlideSwitch.widthFor(text), 2);
            check(
                columns == 1 && LayoutMath.total(LayoutMath.split(available, gap, columns), gap) == available,
                "a " + text + " pixel label drops to one column that stays inside the panel");
        }
        check(
            LayoutMath.columns(available, gap, 79, 2) == 2 && LayoutMath.columns(available, gap, 80, 2) == 1,
            "two columns until a cell no longer fits");
        check(LayoutMath.columns(available, gap, 0, 2) == 2, "unmeasured labels on the server keep the most columns");
        check(LayoutMath.columns(available, 1, 10, 7) == 7, "a column count is an upper bound");
        for (int tabs = 1; tabs <= 8; tabs++) {
            int[] widths = LayoutMath.split(available, gap, tabs);
            int narrowest = Integer.MAX_VALUE, widest = 0;
            for (int width : widths) {
                narrowest = Math.min(narrowest, width);
                widest = Math.max(widest, width);
            }
            check(
                widths.length == tabs && LayoutMath.total(widths, gap) == available && widest - narrowest <= 1,
                tabs + " tabs share the row evenly and fill it");
        }
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(Size.fixed(20), Size.fill(), Size.percent(0.25F), Size.content(24, 3)),
                    available,
                    gap),
                new int[] { 20, 64, 40, 30 }),
            "fixed, fill, percent and content sizes share one row");
        check(
            Arrays.equals(LayoutMath.line(Arrays.asList(Size.fill(1), Size.fill(3)), 100, 0), new int[] { 25, 75 }),
            "fill splits the rest by weight");
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(
                        Size.fill()
                            .atMost(30),
                        Size.fill()),
                    100,
                    0),
                new int[] { 30, 70 }),
            "a capped fill hands the rest to the other fill");
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(
                        Size.fill()
                            .atLeast(80),
                        Size.fill()),
                    100,
                    0),
                new int[] { 80, 20 }),
            "a minimum wins over an even share");
        check(
            Size.percent(0.5F)
                .atMost(30)
                .resolve(100) == 30
                && Size.content(10)
                    .atLeast(72)
                    .resolve(0) == 72
                && Size.fixed(14)
                    .resolve(500) == 14,
            "minimum and maximum clamp every size kind");
        int[] shrunk = LayoutMath.shrink(new int[] { 100, 100 }, new int[] { 40, 80 }, 150, 0);
        check(
            LayoutMath.total(shrunk, 0) == 150 && shrunk[0] >= 40 && shrunk[1] >= 80 && shrunk[0] < shrunk[1],
            "shrinking takes the overflow from the room above each minimum");
        check(
            Arrays.equals(LayoutMath.shrink(new int[] { 50, 50 }, new int[] { 50, 50 }, 80, 0), new int[] { 50, 50 }),
            "nothing shrinks below its minimum");
        Layout rows = Layout.rows(LayoutTheme.DEFAULT, 176);
        check(
            rows.available() == 156 && rows.inset(4)
                .available() == 168,
            "rows keep the theme padding on both sides");
        List<SlideSwitch> five = new ObjectArrayList<>();
        for (int i = 0; i < 5; i++) five.add(new SlideSwitch());
        List<IWidget> grid = rows.grid(gap, 2, five);
        check(
            grid.size() == 3 && ((Flow) grid.get(0)).getChildren()
                .size() == 2
                && ((Flow) grid.get(2)).getChildren()
                    .size() == 1,
            "a grid fills its rows left to right");
    }

    private static void layoutSources() throws IOException {
        String layout = read(SOURCES.resolve("gui/layout/Layout.java"));
        check(layout.contains("widgetTheme(CardDrawable.KEY)"), "layout cards draw with the theme card");
        check(
            compact(layout).contains("returnnewScrollingTextWidget(key)") && compact(layout).contains(
                "TextRenderUtils.drawTextFitted(key.getFormatted(),x,y,width,height,Alignment.Center,theme.getTextColor(),theme.getTextShadow(),style.textScale)"),
            "labels and button captions cut text that does not fit instead of spilling out");
        check(
            compact(layout).contains("returnchild.width(()->widths.get()[index],Unit.Measure.PIXEL);")
                && compact(layout).contains(".width(this::width,Unit.Measure.PIXEL)"),
            "rows and their cells read their width when the screen lays out, so they follow a resized panel");
    }
}
