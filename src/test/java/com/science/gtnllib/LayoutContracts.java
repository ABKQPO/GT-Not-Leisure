package com.science.gtnllib;

import static com.science.gtnllib.GTNLLibContractTest.SOURCES;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.compact;
import static com.science.gtnllib.GTNLLibContractTest.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnllib.gui.layout.Layout;
import com.science.gtnllib.gui.layout.LayoutMath;
import com.science.gtnllib.gui.layout.LayoutTheme;
import com.science.gtnllib.gui.layout.Size;
import com.science.gtnllib.gui.widget.SlideSwitch;

public class LayoutContracts {

    private LayoutContracts() {}

    static void run() throws IOException {
        layoutRules();
        layoutSources();
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
    }
}
