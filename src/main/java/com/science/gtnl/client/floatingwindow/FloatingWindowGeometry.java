package com.science.gtnl.client.floatingwindow;

import java.awt.Point;
import java.awt.Rectangle;

import net.minecraft.client.gui.inventory.GuiContainer;

import com.cleanroommc.modularui.api.widget.ResizeDragArea;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiContainerAccessor;

public abstract class FloatingWindowGeometry {

    private final Rectangle bounds = new Rectangle();
    private Rectangle preferredBounds;
    private int defaultHeight;
    private boolean geometryDirty;
    private boolean expanded;
    private boolean hasRoom;
    private boolean snappedWidth;
    private boolean snappedHeight;
    private int screenWidth;
    private int screenHeight;

    public abstract int minWidth();

    public abstract int minHeight();

    public abstract int defaultWidth();

    protected abstract Rectangle dragHandle(int width, int height, boolean expanded);

    public int cellSize() {
        return 18;
    }

    public int compactSize() {
        return 18;
    }

    protected int restoredMinHeight() {
        return minHeight();
    }

    protected int defaultHeight(int guiHeight) {
        return Math.max(minHeight(), guiHeight);
    }

    protected int fitWidth(int width) {
        return width;
    }

    protected int fitHeight(int height) {
        return height;
    }

    protected final int screenHeight() {
        return screenHeight;
    }

    protected Point defaultPosition(int guiLeft, int guiTop, int guiWidth, int guiHeight, int width) {
        return new Point(guiLeft + guiWidth + 3, guiTop);
    }

    public boolean update(GuiContainer inventory, boolean collapsed) {
        GuiContainerAccessor gui = (GuiContainerAccessor) inventory;
        return update(
            inventory.width,
            inventory.height,
            gui.getGuiLeft(),
            gui.getGuiTop(),
            gui.getXSize(),
            gui.getYSize(),
            collapsed);
    }

    public boolean update(int screenWidth, int screenHeight, int guiLeft, int guiTop, int guiWidth, int height,
        boolean collapsed) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        defaultHeight = defaultHeight(height);
        int width = preferredBounds == null ? defaultWidth() : preferredBounds.width;
        Point anchor = preferredBounds == null ? defaultPosition(guiLeft, guiTop, guiWidth, height, width) : null;
        int x = preferredBounds == null ? anchor.x : preferredBounds.x;
        int y = preferredBounds == null ? anchor.y : preferredBounds.y;
        int panelHeight = preferredBounds == null ? defaultHeight : preferredBounds.height;

        int availableWidth = Math.max(0, screenWidth - 2);
        int availableHeight = Math.max(0, screenHeight - 2);
        hasRoom = availableWidth >= minWidth() && availableHeight >= minHeight()
            && (preferredBounds != null
                || x >= 0 && x + width <= screenWidth && y >= 0 && y + panelHeight <= screenHeight);
        boolean nextExpanded = !collapsed && hasRoom;
        if (nextExpanded) {
            width = snappedWidth ? snapSize(width, minWidth(), availableWidth)
                : Math.min(Math.max(minWidth(), width), availableWidth);
            panelHeight = snappedHeight ? snapSize(panelHeight, minHeight(), availableHeight)
                : Math.min(Math.max(minHeight(), panelHeight), availableHeight);
            width = Math.min(fitWidth(width), availableWidth);
            panelHeight = Math.min(fitHeight(panelHeight), availableHeight);
        } else {
            width = Math.max(0, Math.min(compactSize(), Math.min(screenWidth, screenHeight)));
            panelHeight = width;
        }
        Rectangle next = new Rectangle(x, y, width, panelHeight);
        keepDragHandleVisible(next, nextExpanded, screenWidth, screenHeight);
        boolean changed = !bounds.equals(next) || expanded != nextExpanded || geometryDirty;
        expanded = nextExpanded;
        bounds.setBounds(next);
        geometryDirty = false;
        return changed;
    }

    public void keepDragHandleVisible(Rectangle area, boolean expanded, int screenWidth, int screenHeight) {
        Rectangle handle = dragHandle(area.width, area.height, expanded);
        area.x = Math.max(-handle.x, Math.min(area.x, Math.max(0, screenWidth - handle.width) - handle.x));
        area.y = Math.max(-handle.y, Math.min(area.y, Math.max(0, screenHeight - handle.height) - handle.y));
    }

    public void reset() {
        restorePreference(null);
    }

    public record Preference(int x, int y, int width, int height, boolean snappedWidth, boolean snappedHeight) {}

    public Preference preference() {
        return preferredBounds == null ? null
            : new Preference(
                preferredBounds.x,
                preferredBounds.y,
                preferredBounds.width,
                preferredBounds.height,
                snappedWidth,
                snappedHeight);
    }

    public void restorePreference(Preference preference) {
        preferredBounds = preference == null ? null
            : new Rectangle(
                preference.x(),
                preference.y(),
                Math.max(minWidth(), preference.width()),
                Math.max(restoredMinHeight(), preference.height()));
        snappedWidth = preference != null && preference.snappedWidth();
        snappedHeight = preference != null && preference.snappedHeight();
        geometryDirty = true;
    }

    protected final void markGeometryDirty() {
        geometryDirty = true;
    }

    public void acceptPanelBounds(Rectangle next) {
        int width = expanded ? Math.max(minWidth(), next.width)
            : preferredBounds == null ? defaultWidth() : preferredBounds.width;
        int height = expanded ? Math.max(minHeight(), next.height)
            : preferredBounds == null ? defaultHeight : preferredBounds.height;

        if (next.width == bounds.width) width = preferredBounds == null ? defaultWidth() : preferredBounds.width;
        if (next.height == bounds.height) height = preferredBounds == null ? defaultHeight : preferredBounds.height;
        preferredBounds = new Rectangle(next.x, next.y, width, height);
    }

    public void acceptResizedBounds(Rectangle next, Rectangle start, ResizeDragArea edge, boolean centered) {
        if (!expanded) return;
        boolean horizontal = edge.left || edge.right;
        boolean vertical = edge.top || edge.bottom;
        int width = horizontal ? snapSize(next.width, minWidth(), screenWidth - 2)
            : preferredBounds == null ? start.width : preferredBounds.width;
        int height = vertical ? snapSize(next.height, minHeight(), screenHeight - 2)
            : preferredBounds == null ? start.height : preferredBounds.height;

        int x = horizontal ? start.x + anchorOffset(start.width, width, edge.left, centered)
            : preferredBounds == null ? start.x : preferredBounds.x;
        int y = vertical ? start.y + anchorOffset(start.height, height, edge.top, centered)
            : preferredBounds == null ? start.y : preferredBounds.y;
        snappedWidth |= horizontal;
        snappedHeight |= vertical;
        preferredBounds = new Rectangle(x, y, width, height);
    }

    private static int anchorOffset(int previous, int next, boolean fromStart, boolean centered) {
        return centered ? Math.floorDiv(previous - next, 2) : fromStart ? previous - next : 0;
    }

    private int snapSize(int requested, int minimum, int maximum) {
        int cell = cellSize();
        int maxSteps = Math.max(0, (maximum - minimum) / cell);
        long nearest = Math.max(0L, ((long) requested - minimum + cell / 2) / cell);
        return minimum + (int) Math.min(nearest, maxSteps) * cell;
    }

    public Rectangle bounds() {
        return new Rectangle(bounds);
    }

    protected final Rectangle area() {
        return bounds;
    }

    public boolean intersects(int x, int y, int width, int height) {
        return bounds.intersects(x, y, width, height);
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public boolean expanded() {
        return expanded;
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}
