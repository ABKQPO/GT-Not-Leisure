package com.gtnllib.gui.layout;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ScrollingTextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnllib.gui.theme.ButtonPurpose;
import com.gtnllib.gui.widget.CardDrawable;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.utils.TextRenderUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Layout {

    private static final int TITLE_HEIGHT = 10;

    private final LayoutTheme style;
    private final IntSupplier width;
    private final int inset;

    private Layout(LayoutTheme style, IntSupplier width, int inset) {
        this.style = style;
        this.width = width;
        this.inset = inset;
    }

    public static Layout rows(LayoutTheme style, int width) {
        return rows(style, () -> width);
    }

    public static Layout rows(LayoutTheme style, IntSupplier width) {
        return new Layout(style, width, style.padding);
    }

    public Layout inset(int inset) {
        return new Layout(style, width, inset);
    }

    public int width() {
        return width.getAsInt();
    }

    public int available() {
        return width() - inset * 2;
    }

    public int columns(int gap, int widest, int maxColumns) {
        return LayoutMath.columns(available(), gap, widest, maxColumns);
    }

    public Flow row(int gap) {
        return Flow.row()
            .width(this::width, Unit.Measure.PIXEL)
            .height(style.rowHeight)
            .padding(inset, inset, 0, 0)
            .childPadding(gap);
    }

    public Flow line(int gap, List<Size> sizes, List<? extends Widget<?>> children) {
        Track widths = new Track(this::available, available -> fit(sizes, available, gap));
        Flow row = row(gap);
        for (int i = 0; i < children.size(); i++) row.child(sized(children.get(i), widths, i));
        return row;
    }

    public List<IWidget> grid(int gap, int columns, List<? extends Widget<?>> cells) {
        Track widths = new Track(this::available, available -> LayoutMath.split(available, gap, columns));
        List<IWidget> rows = new ObjectArrayList<>();
        Flow row = null;
        for (int i = 0; i < cells.size(); i++) {
            if (i % columns == 0) rows.add(row = row(gap));
            row.child(sized(cells.get(i), widths, i % columns));
        }
        return rows;
    }

    public Grid adaptiveGrid(int gap, int widest, int maxColumns, List<? extends Widget<?>> cells) {
        return new Grid(this, gap, widest, maxColumns, cells);
    }

    public ParentWidget<?> section(IKey title, int color) {
        return new ParentWidget<>().width(this::width, Unit.Measure.PIXEL)
            .height(style.rowHeight)
            .child(
                label(title, style).color(color)
                    .shadow(true)
                    .left(inset)
                    .top((style.rowHeight - TITLE_HEIGHT) / 2)
                    .width(this::available, Unit.Measure.PIXEL)
                    .height(TITLE_HEIGHT))
            .child(
                new Divider().color(color)
                    .left(inset)
                    .bottom(0)
                    .width(this::available, Unit.Measure.PIXEL)
                    .height(Divider.HEIGHT));
    }

    public int stackHeight(List<? extends IWidget> rows) {
        int total = 0;
        for (IWidget row : rows) total += row instanceof Grid grid ? grid.contentHeight() : style.rowHeight;
        return total;
    }

    private IWidget sized(Widget<?> child, Track widths, int index) {
        if (!child.resizer()
            .hasHeight()) child.height(style.buttonHeight);
        return child.width(() -> widths.get()[index], Unit.Measure.PIXEL);
    }

    private static int[] fit(List<Size> sizes, int available, int gap) {
        int[] minimums = new int[sizes.size()];
        for (int i = 0; i < minimums.length; i++) minimums[i] = sizes.get(i)
            .min();
        return LayoutMath.shrink(LayoutMath.line(sizes, available, gap), minimums, available, gap);
    }

    public static ScrollingTextWidget label(IKey key, LayoutTheme style) {
        return new ScrollingTextWidget(key).scale(style.textScale);
    }

    public static IDrawable caption(IKey key, LayoutTheme style) {
        return (context, x, y, width, height, theme) -> TextRenderUtils.drawTextFitted(
            key.getFormatted(),
            x,
            y,
            width,
            height,
            Alignment.Center,
            theme.getTextColor(),
            theme.getTextShadow(),
            style.textScale);
    }

    public static ButtonWidget<?> button(IKey caption, int width, LayoutTheme style, Runnable action) {
        return ButtonPurpose.NORMAL.apply(new ButtonWidget<>())
            .size(width, style.buttonHeight)
            .overlay(caption(caption, style))
            .onMousePressed(mouse -> {
                action.run();
                return true;
            });
    }

    public static int cardHeader(LayoutTheme style) {
        return style.spacingMedium * 2 + TITLE_HEIGHT;
    }

    public static ParentWidget<?> card(IKey title, int titleColor, LayoutTheme style, int width, int height) {
        int inset = style.spacingMedium;
        return new ParentWidget<>().widgetTheme(CardDrawable.KEY)
            .size(width, height)
            .child(
                label(title, style).color(titleColor)
                    .shadow(true)
                    .pos(inset, inset)
                    .size(width - inset * 2, TITLE_HEIGHT));
    }

    public static int screenWidth(IWidget widget) {
        return widget.getScreen()
            .getScreenArea()
            .w();
    }

    public static int screenHeight(IWidget widget) {
        return widget.getScreen()
            .getScreenArea()
            .h();
    }

    @SideOnly(Side.CLIENT)
    public static int textWidth(String text, LayoutTheme style) {
        return style.scaled(Minecraft.getMinecraft().fontRenderer.getStringWidth(text));
    }

    @SideOnly(Side.CLIENT)
    public static int buttonWidth(LayoutTheme style, int least, String... captions) {
        int widest = 0;
        for (String caption : captions) widest = LayoutMath.widest(widest, textWidth(caption, style));
        return Size.content(widest, style.padding)
            .atLeast(least)
            .resolve(0);
    }

    @SideOnly(Side.CLIENT)
    public static int textLines(String text, int width, LayoutTheme style) {
        return Math.max(
            1,
            Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, (int) (width / style.textScale))
                .size());
    }

    public static class Grid extends ParentWidget<Grid> {

        private final Layout layout;
        private final int gap, widest, maxColumns, count;
        private final Track widths;

        private Grid(Layout layout, int gap, int widest, int maxColumns, List<? extends Widget<?>> cells) {
            this.layout = layout;
            this.gap = gap;
            this.widest = widest;
            this.maxColumns = maxColumns;
            this.count = cells.size();
            this.widths = new Track(layout::available, available -> LayoutMath.split(available, gap, columns()));
            width(layout::width, Unit.Measure.PIXEL).height(this::contentHeight, Unit.Measure.PIXEL);
            LayoutTheme style = layout.style;
            for (int i = 0; i < count; i++) {
                final int index = i;
                Widget<?> cell = cells.get(i);
                boolean centered = !cell.resizer()
                    .hasHeight();
                if (centered) cell.height(style.buttonHeight);
                int offset = centered ? (style.rowHeight - style.buttonHeight) / 2 : 0;
                child(
                    cell.left(() -> cellLeft(index), Unit.Measure.PIXEL)
                        .top(() -> cellTop(index) + offset, Unit.Measure.PIXEL)
                        .width(() -> cellWidth(index), Unit.Measure.PIXEL));
            }
        }

        public int columns() {
            return layout.columns(gap, widest, maxColumns);
        }

        public int rowCount() {
            return LayoutMath.rows(count, columns());
        }

        public int contentHeight() {
            return rowCount() * layout.style.rowHeight;
        }

        public int cellWidth(int index) {
            return widths.get()[index % columns()];
        }

        public int cellLeft(int index) {
            return layout.inset + LayoutMath.offsets(widths.get(), gap)[index % columns()];
        }

        public int cellTop(int index) {
            return index / columns() * layout.style.rowHeight;
        }
    }

    private static class Track {

        private final IntSupplier available;
        private final IntFunction<int[]> compute;
        private int measured;
        private int[] sizes;

        private Track(IntSupplier available, IntFunction<int[]> compute) {
            this.available = available;
            this.compute = compute;
        }

        private int[] get() {
            int now = available.getAsInt();
            if (sizes == null || now != measured) {
                measured = now;
                sizes = compute.apply(now);
            }
            return sizes;
        }
    }
}
