package com.science.gtnl.common.gui.modularui.layout;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ScrollingTextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.modularui.widget.CardDrawable;
import com.science.gtnl.utils.RenderUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Layout {

    private static final int TITLE_HEIGHT = 10;

    private final LayoutTheme style;
    private final int width, inset;

    private Layout(LayoutTheme style, int width, int inset) {
        this.style = style;
        this.width = width;
        this.inset = inset;
    }

    public static Layout rows(LayoutTheme style, int width) {
        return new Layout(style, width, style.padding);
    }

    public Layout inset(int inset) {
        return new Layout(style, width, inset);
    }

    public int available() {
        return width - inset * 2;
    }

    public int columns(int gap, int widest, int maxColumns) {
        return LayoutMath.columns(available(), gap, widest, maxColumns);
    }

    public Flow row(int gap) {
        return Flow.row()
            .size(width, style.rowHeight)
            .padding(inset, inset, 0, 0)
            .childPadding(gap);
    }

    public Flow line(int gap, List<Size> sizes, List<? extends Widget<?>> children) {
        int[] widths = fit(sizes, available(), gap);
        Flow row = row(gap);
        for (int i = 0; i < children.size(); i++) row.child(sized(children.get(i), widths[i]));
        return row;
    }

    public List<IWidget> wrap(int gap, List<Size> sizes, List<? extends Widget<?>> children) {
        int available = available();
        int[] natural = new int[sizes.size()];
        for (int i = 0; i < natural.length; i++) natural[i] = sizes.get(i)
            .fills()
                ? sizes.get(i)
                    .min()
                : sizes.get(i)
                    .resolve(available);
        int[] lines = LayoutMath.wrap(natural, available, gap);
        List<IWidget> rows = new ArrayList<>();
        for (int start = 0, end = 1; end <= lines.length; end++) {
            if (end < lines.length && lines[end] == lines[start]) continue;
            rows.add(line(gap, sizes.subList(start, end), children.subList(start, end)));
            start = end;
        }
        return rows;
    }

    public List<IWidget> grid(int gap, int columns, List<? extends Widget<?>> cells) {
        int[] widths = LayoutMath.split(available(), gap, columns);
        List<IWidget> rows = new ArrayList<>();
        Flow row = null;
        for (int i = 0; i < cells.size(); i++) {
            if (i % columns == 0) rows.add(row = row(gap));
            row.child(sized(cells.get(i), widths[i % columns]));
        }
        return rows;
    }

    private IWidget sized(Widget<?> child, int width) {
        if (!child.resizer()
            .hasHeight()) child.height(style.buttonHeight);
        return child.width(width);
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
        return (context, x, y, width, height, theme) -> RenderUtils.drawTextFitted(
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

    public static int cardHeader(LayoutTheme style) {
        return style.spacingMedium * 2 + TITLE_HEIGHT;
    }

    public static ParentWidget<?> card(IKey title, int titleColor, LayoutTheme style, int width, int height) {
        int inset = style.spacingMedium, titleX = inset + style.spacingSmall;
        return new ParentWidget<>().widgetTheme(CardDrawable.KEY)
            .size(width, height)
            .child(
                label(title, style).color(titleColor)
                    .shadow(true)
                    .pos(titleX, inset)
                    .size(width - titleX - inset, TITLE_HEIGHT));
    }

    @SideOnly(Side.CLIENT)
    public static int textWidth(String text, LayoutTheme style) {
        return style.scaled(Minecraft.getMinecraft().fontRenderer.getStringWidth(text));
    }

    @SideOnly(Side.CLIENT)
    public static int textLines(String text, int width, LayoutTheme style) {
        return Math.max(
            1,
            Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, (int) (width / style.textScale))
                .size());
    }
}
