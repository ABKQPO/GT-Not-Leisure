package com.gtnllib.gui.widget;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widget.Widget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Breadcrumb extends Widget<Breadcrumb> implements Interactable {

    private static final String SEPARATOR = " > ", COLLAPSED = "...";
    private Supplier<List<String>> segments = List::of;
    private IntConsumer select = depth -> {};
    private int current = 0xFF404040, link = 0xFF1C4CA6, separator = 0xFF777777;
    private final List<int[]> spans = new ObjectArrayList<>();

    public Breadcrumb segments(Supplier<List<String>> segments) {
        this.segments = segments;
        return this;
    }

    public Breadcrumb onSelect(IntConsumer select) {
        this.select = select;
        return this;
    }

    public Breadcrumb colors(int current, int link, int separator) {
        this.current = current;
        this.link = link;
        this.separator = separator;
        return this;
    }

    public static List<String> fit(List<String> segments, int width, FontRenderer font) {
        List<String> fitted = new ObjectArrayList<>(segments);
        for (int i = 1; i < fitted.size() - 1 && width(fitted, font) > width; i++) fitted.set(i, COLLAPSED);
        if (!fitted.isEmpty() && width(fitted, font) > width) {
            int last = fitted.size() - 1;
            int room = width - width(fitted.subList(0, last), font) - (last > 0 ? font.getStringWidth(SEPARATOR) : 0);
            fitted.set(last, font.trimStringToWidth(fitted.get(last), Math.max(0, room)));
        }
        return fitted;
    }

    private static int width(List<String> segments, FontRenderer font) {
        int width = 0;
        for (String segment : segments) width += font.getStringWidth(segment);
        return width + Math.max(0, segments.size() - 1) * font.getStringWidth(SEPARATOR);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        List<String> labels = fit(segments.get(), getArea().w(), font);
        int y = (getArea().h() - font.FONT_HEIGHT) / 2 + 1, hovered = spanAt(context.getMouseX(), context.getMouseY());
        spans.clear();
        Platform.setupDrawFont();
        int x = 0;
        for (int i = 0; i < labels.size(); i++) {
            boolean last = i == labels.size() - 1;
            int width = font.getStringWidth(labels.get(i));
            font.drawString(labels.get(i), x, y, last ? current : link);
            if (!last) {
                if (i == hovered) GuiDraw.drawRect(x, y + font.FONT_HEIGHT - 1, width, 1, link);
                spans.add(new int[] { x, x + width });
                x += width;
                font.drawString(SEPARATOR, x, y, separator);
                x += font.getStringWidth(SEPARATOR);
            }
        }
    }

    private int spanAt(int mouseX, int mouseY) {
        if (mouseY < 0 || mouseY >= getArea().h()) return -1;
        for (int i = 0; i < spans.size(); i++) if (mouseX >= spans.get(i)[0] && mouseX < spans.get(i)[1]) return i;
        return -1;
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (mouseButton != 0) return Result.IGNORE;
        int depth = spanAt(getContext().getMouseX(), getContext().getMouseY());
        if (depth < 0) return Result.IGNORE;
        select.accept(depth);
        return Result.SUCCESS;
    }
}
