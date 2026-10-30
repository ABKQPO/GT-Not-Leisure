package com.gtnllib.client.hud;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.IGuiAction;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.Widget;
import com.gtnllib.client.hud.HudLayout.Entry;
import com.gtnllib.client.hud.HudLayout.Placement;
import com.gtnllib.client.hud.HudSnap.Box;
import com.gtnllib.client.hud.HudSnap.Snap;
import com.gtnllib.utils.TextRenderUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class HudCanvas extends Widget<HudCanvas> implements Interactable {

    private static final String LANG = "gtnllib.hud.editor.";
    private static final int HISTORY = 100, SHADE = 0x30000000, FRAME = 0x60FFFFFF, HIDDEN = 0x80808080,
        TEXT = 0xFFFFFFFF, LABEL_GAP = 11, LABEL_HEIGHT = 10, LABEL_WIDTH = 400;

    private final HudHistory<Map<String, Entry>> history = new HudHistory<>(HISTORY);
    private HudElement selected, dragging;
    private Object dragGroup;
    private float grabX, grabY, guideX = Float.NaN, guideY = Float.NaN;
    private boolean showAll, dirty;
    private int reserved;

    public HudCanvas() {
        disableThemeBackground(true);
        disableHoverThemeBackground(true);
        listenGuiAction((IGuiAction.MouseReleased) button -> {
            if (button == 0) endDrag();
            return false;
        });
    }

    public HudCanvas reserveTop(int height) {
        this.reserved = height;
        return this;
    }

    public HudElement selected() {
        return selected;
    }

    public void select(HudElement element) {
        if (selected == element) return;
        selected = element;
        dirty = true;
    }

    public boolean takeDirty() {
        boolean was = dirty;
        dirty = false;
        return was;
    }

    public boolean showAll() {
        return showAll;
    }

    public void toggleShowAll() {
        showAll = !showAll;
        if (selected != null && !candidates().contains(selected)) select(null);
    }

    public List<HudElement> candidates() {
        return HudRegistry.candidates(showAll);
    }

    public void change(Object group, Runnable mutation) {
        history.record(HudLayout::snapshot, group);
        mutation.run();
    }

    public void seal() {
        history.seal();
    }

    public boolean canUndo() {
        return history.canUndo();
    }

    public boolean canRedo() {
        return history.canRedo();
    }

    public void undo() {
        restore(history.undo(HudLayout.snapshot()));
    }

    public void redo() {
        restore(history.redo(HudLayout.snapshot()));
    }

    private void restore(Map<String, Entry> snapshot) {
        if (snapshot == null) return;
        HudLayout.restore(snapshot);
        dirty = true;
    }

    public void resetAll() {
        change(null, HudLayout::reset);
        dirty = true;
    }

    public void resetSelected() {
        HudElement element = selected;
        if (element == null) return;
        change(null, () -> HudLayout.resetElement(element));
        dirty = true;
    }

    public void rebuild() {
        dirty = true;
    }

    public boolean onLeftHalf(HudElement element) {
        Placement place = place(element);
        return place.x() < getArea().w() / 2F;
    }

    private int width() {
        return getArea().w();
    }

    private int height() {
        return getArea().h();
    }

    private Placement place(HudElement element) {
        return HudLayout.preview(element, width(), height());
    }

    private Box box(HudElement element) {
        Placement place = place(element);
        return Box.of(element, place.x(), place.y(), place.scale());
    }

    private HudElement elementAt(int mouseX, int mouseY) {
        HudElement best = null;
        float smallest = Float.MAX_VALUE;
        for (HudElement element : candidates()) {
            Box box = box(element);
            if (mouseX < box.minX() || mouseX > box.maxX() || mouseY < box.minY() || mouseY > box.maxY()) continue;
            float area = (box.maxX() - box.minX()) * (box.maxY() - box.minY());
            if (area < smallest) {
                smallest = area;
                best = element;
            }
        }
        return best;
    }

    private List<Box> others(HudElement moving) {
        List<Box> others = new ObjectArrayList<>();
        for (HudElement element : candidates())
            if (element != moving && !HudLayout.hidden(element)) others.add(box(element));
        return others;
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        int mouseX = getContext().getMouseX(), mouseY = getContext().getMouseY();
        HudElement element = elementAt(mouseX, mouseY);
        if (mouseButton == 0) {
            endDrag();
            select(element);
            if (element != null) {
                Placement place = place(element);
                dragging = element;
                dragGroup = new Object();
                grabX = mouseX - place.x();
                grabY = mouseY - place.y();
            }
            return Result.SUCCESS;
        }
        if (mouseButton == 1 && element != null) {
            change(null, () -> HudLayout.toggle(element));
            return Result.SUCCESS;
        }
        return Result.IGNORE;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        if (dragging == null || mouseButton != 0) return;
        HudElement moving = dragging;
        Placement place = place(moving);
        float x = getContext().getMouseX() - grabX, y = getContext().getMouseY() - grabY;
        guideX = guideY = Float.NaN;
        if (!Interactable.hasAltDown()) {
            Snap snap = HudSnap
                .snap(Box.of(moving, x, y, place.scale()), others(moving), width(), height(), HudSnap.RANGE);
            x = snap.x();
            y = snap.y();
            guideX = snap.guideX();
            guideY = snap.guideY();
        }
        float toX = HudLayout.clampX(moving, x, place.scale(), width()),
            toY = HudLayout.clampY(moving, y, place.scale(), height());
        if (toX == place.x() && toY == place.y()) return;
        change(dragGroup, () -> HudLayout.move(moving, toX, toY, width(), height()));
    }

    private void endDrag() {
        dragging = null;
        guideX = guideY = Float.NaN;
        history.seal();
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        if (dragging != null) return false;
        HudElement element = elementAt(getContext().getMouseX(), getContext().getMouseY());
        if (element == null) return false;
        change("scale:" + element.id, () -> HudLayout.rescale(element, scrollDirection.isUp() ? 1 : -1));
        return true;
    }

    public boolean key(int keyCode) {
        boolean shift = Interactable.hasShiftDown();
        if (Interactable.hasControlDown() && (keyCode == Keyboard.KEY_Z || keyCode == Keyboard.KEY_Y)) {
            if (keyCode == Keyboard.KEY_Y || shift) redo();
            else undo();
            return true;
        }
        if (selected == null) return false;
        int step = HudSnap.nudge(shift);
        switch (keyCode) {
            case Keyboard.KEY_LEFT -> nudge(-step, 0);
            case Keyboard.KEY_RIGHT -> nudge(step, 0);
            case Keyboard.KEY_UP -> nudge(0, -step);
            case Keyboard.KEY_DOWN -> nudge(0, step);
            case Keyboard.KEY_ESCAPE -> select(null);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void nudge(int dx, int dy) {
        HudElement element = selected;
        Placement place = place(element);
        float[] to = HudSnap.nudged(element, place, dx, dy, width(), height());
        if (to[0] == place.x() && to[1] == place.y()) return;
        change("nudge:" + element.id, () -> HudLayout.move(element, to[0], to[1], width(), height()));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = width(), height = height(), guide = HudColor.guide(), selection = HudColor.selection();
        GuiDraw.drawRect(0, 0, width, height, SHADE);
        if (!Float.isNaN(guideX)) GuiDraw.drawRect(Math.round(guideX), 0, 1, height, guide);
        if (!Float.isNaN(guideY)) GuiDraw.drawRect(0, Math.round(guideY), width, 1, guide);
        HudElement hovered = dragging != null ? dragging : elementAt(context.getMouseX(), context.getMouseY());
        List<HudElement> candidates = candidates();
        boolean player = Minecraft.getMinecraft().thePlayer != null;
        for (HudElement element : candidates) {
            boolean hidden = HudLayout.hidden(element);
            if (!hidden && player) element.preview(place(element), context.getPartialTicks());
            outline(
                box(element),
                element == hovered || element == selected ? selection : hidden ? HIDDEN : FRAME,
                element == selected);
        }
        if (hovered != null) label(hovered);
        else if (selected != null) label(selected);
        if (candidates.isEmpty())
            text(StatCollector.translateToLocal(LANG + "empty"), width / 2, height / 2 - LABEL_HEIGHT / 2);
    }

    private static void outline(Box box, int color, boolean thick) {
        int x0 = Math.round(box.minX()), y0 = Math.round(box.minY()), x1 = Math.round(box.maxX()),
            y1 = Math.round(box.maxY()), line = thick ? 2 : 1;
        GuiDraw.drawRect(x0, y0, x1 - x0, line, color);
        GuiDraw.drawRect(x0, y1 - line, x1 - x0, line, color);
        GuiDraw.drawRect(x0, y0, line, y1 - y0, color);
        GuiDraw.drawRect(x1 - line, y0, line, y1 - y0, color);
    }

    private void label(HudElement element) {
        String text = StatCollector.translateToLocal(element.lang()) + " ×"
            + String.format(Locale.ROOT, "%.1f", HudLayout.scale(element));
        if (HudLayout.hidden(element)) text += " " + StatCollector.translateToLocal(LANG + "hidden");
        Box box = box(element);
        int x = Math.round((box.minX() + box.maxX()) / 2), y = Math.round(box.minY()) - LABEL_GAP;
        if (y < reserved) y = Math.round(box.maxY()) + 3;
        text(text, x, y);
    }

    private static void text(String text, int centerX, int y) {
        TextRenderUtils
            .drawTextLine(text, centerX - LABEL_WIDTH / 2, y, LABEL_WIDTH, LABEL_HEIGHT, Alignment.Center, TEXT, true);
    }
}
