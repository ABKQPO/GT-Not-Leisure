package com.science.gtnllib;

import static com.science.gtnllib.GTNLLibContractTest.SOURCES;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.compact;
import static com.science.gtnllib.GTNLLibContractTest.field;
import static com.science.gtnllib.GTNLLibContractTest.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnllib.gui.layout.Layout;
import com.science.gtnllib.gui.layout.LayoutMath;
import com.science.gtnllib.gui.layout.LayoutTheme;
import com.science.gtnllib.gui.layout.Size;
import com.science.gtnllib.gui.widget.CloseButton;
import com.science.gtnllib.gui.widget.Divider;
import com.science.gtnllib.gui.widget.SlideSwitch;

public class LayoutContracts {

    private LayoutContracts() {}

    static void run() throws IOException, ReflectiveOperationException {
        layoutRules();
        adaptiveRules();
        layoutSources();
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
        List<SlideSwitch> five = new ArrayList<>();
        for (int i = 0; i < 5; i++) five.add(new SlideSwitch());
        Layout.Grid grid = rows.adaptiveGrid(2, 76, 3, five);
        check(
            rows.available() == 160 && grid.columns() == 2
                && grid.rowCount() == 3
                && grid.contentHeight() == 48
                && grid.cellLeft(1) == 89
                && grid.cellWidth(1) == 79
                && grid.cellTop(2) == 16
                && grid.getChildren()
                    .size() == 5,
            "an adaptive grid on a narrow panel keeps two columns");
        width[0] = 256;
        check(
            rows.available() == 240 && grid.columns() == 3
                && grid.rowCount() == 2
                && grid.contentHeight() == 32
                && grid.cellLeft(2) == 170
                && grid.cellWidth(2) == 78
                && grid.cellTop(3) == 16,
            "the same grid takes a third column once the panel grows");
        width[0] = 120;
        check(
            grid.columns() == 1 && grid.rowCount() == 5 && grid.cellWidth(4) == 104 && grid.cellLeft(4) == 8,
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
            SlideSwitch.widthFor(0) == 20 && shortSwitch == 76,
            "a switch needs its track, its label and a small margin");
        check(LayoutMath.columns(available, gap, shortSwitch, 2) == 2, "short labels keep two columns");
        for (int text : new int[] { 62, 120, 400 }) {
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
            rows.available() == 160 && rows.inset(4)
                .available() == 168,
            "rows keep the theme padding on both sides");
        List<SlideSwitch> five = new ArrayList<>();
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
