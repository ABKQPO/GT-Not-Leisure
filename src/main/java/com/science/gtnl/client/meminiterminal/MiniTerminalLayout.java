package com.science.gtnl.client.meminiterminal;

import java.awt.Rectangle;

import net.minecraft.client.gui.inventory.GuiContainer;

import com.cleanroommc.modularui.api.widget.ResizeDragArea;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiContainerAccessor;
import com.cleanroommc.modularui.widget.sizer.Area;

import appeng.items.contents.PinList;

public final class MiniTerminalLayout {

    public static final int COLUMNS = 5;
    public static final int MIN_COLUMNS = 2;
    public static final int ROWS = 6;
    public static final int CELL = 18;
    public static final int WIDTH = 111;
    // Two columns leave room for the search field and all three header buttons.
    public static final int MIN_WIDTH = WIDTH - (COLUMNS - MIN_COLUMNS) * CELL;

    public static final int MIN_HEIGHT = 94;
    public static final int COMPACT_SIZE = 18;
    public static final int GRID_X = 4;
    public static final int HEADER_Y = 3;
    public static final int HEADER_SIZE = 12;
    public static final int SEARCH_X = GRID_X;
    public static final int BUTTON_WIDTH = 10;
    public static final int BUTTON_HEIGHT = 10;
    public static final int BUTTON_STEP = BUTTON_HEIGHT;
    public static final int SEARCH_RIGHT = BUTTON_WIDTH + 2 * BUTTON_STEP + 6;
    public static final int SEARCH_WIDTH = WIDTH - SEARCH_X - SEARCH_RIGHT;
    public static final int PIN_Y = 16;
    public static final int SCROLL_WIDTH = 8;
    private final Rectangle bounds = new Rectangle();
    private Rectangle preferredBounds;
    private int defaultHeight = 166;
    private int craftingPins;
    private int playerPins;
    private boolean playerFirst = true;
    private boolean geometryDirty;
    private boolean expanded;
    private boolean hasRoom;
    private boolean snappedWidth;
    private boolean snappedHeight;
    private int screenWidth;
    private int screenHeight;

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

    boolean update(int screenWidth, int screenHeight, int guiLeft, int guiTop, int guiWidth, int height,
        boolean collapsed) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        defaultHeight = Math.max(MIN_HEIGHT, height);
        int x = preferredBounds == null ? guiLeft + guiWidth + 3 : preferredBounds.x;
        int y = preferredBounds == null ? guiTop : preferredBounds.y;
        int width = preferredBounds == null ? WIDTH : preferredBounds.width;
        int panelHeight = preferredBounds == null ? defaultHeight : preferredBounds.height;

        int availableWidth = Math.max(0, screenWidth - 2);
        int availableHeight = Math.max(0, screenHeight - 2);
        hasRoom = availableWidth >= MIN_WIDTH && availableHeight >= MIN_HEIGHT
            && (preferredBounds != null
                || x >= 0 && x + width <= screenWidth && y >= 0 && y + panelHeight <= screenHeight);
        boolean nextExpanded = !collapsed && hasRoom;
        if (nextExpanded) {
            width = snappedWidth ? snapSize(width, MIN_WIDTH, availableWidth)
                : Math.min(Math.max(MIN_WIDTH, width), availableWidth);
            panelHeight = snappedHeight ? snapSize(panelHeight, MIN_HEIGHT, availableHeight)
                : Math.min(Math.max(MIN_HEIGHT, panelHeight), availableHeight);
        } else {
            width = Math.max(0, Math.min(COMPACT_SIZE, Math.min(screenWidth, screenHeight)));
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

    static void keepDragHandleVisible(Rectangle area, boolean expanded, int screenWidth, int screenHeight) {
        int handleX = expanded ? area.width - BUTTON_WIDTH - 4 : 0;
        int handleY = expanded ? HEADER_Y : 0;
        int handleWidth = expanded ? BUTTON_WIDTH : area.width;
        int handleHeight = expanded ? BUTTON_HEIGHT : area.height;
        area.x = Math.max(-handleX, Math.min(area.x, Math.max(0, screenWidth - handleWidth) - handleX));
        area.y = Math.max(-handleY, Math.min(area.y, Math.max(0, screenHeight - handleHeight) - handleY));
    }

    public void reset() {
        restorePreference(null);
    }

    record WindowPreference(int x, int y, int width, int height, boolean snappedWidth, boolean snappedHeight) {}

    WindowPreference preference() {
        return preferredBounds == null ? null
            : new WindowPreference(
                preferredBounds.x,
                preferredBounds.y,
                preferredBounds.width,
                preferredBounds.height,
                snappedWidth,
                snappedHeight);
    }

    void restorePreference(WindowPreference preference) {
        preferredBounds = preference == null ? null
            : new Rectangle(
                preference.x(),
                preference.y(),
                Math.max(MIN_WIDTH, preference.width()),
                Math.max(MIN_HEIGHT, preference.height()));
        snappedWidth = preference != null && preference.snappedWidth();
        snappedHeight = preference != null && preference.snappedHeight();
        geometryDirty = true;
    }

    public void configurePins(int craftingCount, int playerCount, boolean playerFirst) {
        int nextCrafting = Math.max(0, Math.min(PinList.CRAFTING_SLOTS, craftingCount));
        int nextPlayer = Math.max(0, Math.min(PinList.PLAYER_SLOTS, playerCount));
        if (craftingPins == nextCrafting && playerPins == nextPlayer && this.playerFirst == playerFirst) return;
        craftingPins = nextCrafting;
        playerPins = nextPlayer;
        this.playerFirst = playerFirst;
        geometryDirty = true;
    }

    void acceptPanelBounds(Rectangle next) {
        int width = expanded ? Math.max(MIN_WIDTH, next.width)
            : preferredBounds == null ? WIDTH : preferredBounds.width;
        int height = expanded ? Math.max(MIN_HEIGHT, next.height)
            : preferredBounds == null ? defaultHeight : preferredBounds.height;

        if (preferredBounds != null) {
            if (next.width == bounds.width) width = preferredBounds.width;
            if (next.height == bounds.height) height = preferredBounds.height;
        }
        preferredBounds = new Rectangle(next.x, next.y, width, height);
    }

    void acceptResizedBounds(Rectangle next, Rectangle start, ResizeDragArea edge, boolean centered) {
        if (!expanded) return;
        boolean horizontal = edge.left || edge.right;
        boolean vertical = edge.top || edge.bottom;
        int width = horizontal
            ? snapSize(next.width, MIN_WIDTH, screenWidth - 2)
            : preferredBounds == null ? start.width : preferredBounds.width;
        int height = vertical
            ? snapSize(next.height, MIN_HEIGHT, screenHeight - 2)
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

    private static int snapSize(int requested, int minimum, int maximum) {
        int maxSteps = Math.max(0, (maximum - minimum) / CELL);
        long nearest = Math.max(0L, ((long) requested - minimum + CELL / 2) / CELL);
        return minimum + (int) Math.min(nearest, maxSteps) * CELL;
    }

    public Rectangle bounds() {
        return new Rectangle(bounds);
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

    public int columns() {
        return Math.max(MIN_COLUMNS, (bounds.width - 21) / CELL);
    }

    public int pinRows() {
        return rowsForPins(visibleCraftingPins()) + rowsForPins(visiblePlayerPins());
    }

    public int visiblePlayerPins() {
        return Math.min(playerPins, availablePinRows() * columns());
    }

    public int visibleCraftingPins() {
        int remainingRows = availablePinRows() - rowsForPins(visiblePlayerPins());
        return Math.min(craftingPins, remainingRows * columns());
    }

    public int pinCount() {
        return visibleCraftingPins() + visiblePlayerPins();
    }

    public int pinIndex(int visualIndex) {
        if (visualIndex < 0 || visualIndex >= pinCount()) return -1;
        int firstCount = firstPinCount();
        if (visualIndex < firstCount) return (playerFirst ? PinList.PLAYER_OFFSET : 0) + visualIndex;
        return (playerFirst ? 0 : PinList.PLAYER_OFFSET) + visualIndex - firstCount;
    }

    public boolean fitsPins(int craftingCount, int playerCount) {
        if (craftingCount < 0 || craftingCount > PinList.CRAFTING_SLOTS
            || playerCount < 0
            || playerCount > PinList.PLAYER_SLOTS) return false;
        int pinRows = rowsForPins(craftingCount) + rowsForPins(playerCount);
        int requiredHeight = PIN_Y + pinRows * CELL + (pinRows == 0 ? 0 : 2) + CELL + 4;
        return bounds.width >= MIN_WIDTH && bounds.height >= requiredHeight;
    }

    private int rowsForPins(int count) {
        return (count + columns() - 1) / columns();
    }

    private int availablePinRows() {
        return Math.max(0, (bounds.height - PIN_Y - 2 - CELL - 4) / CELL);
    }

    private int firstPinCount() {
        return playerFirst ? visiblePlayerPins() : visibleCraftingPins();
    }

    public int catalogY() {
        int pinRows = pinRows();
        return PIN_Y + pinRows * CELL + (pinRows == 0 ? 0 : 2);
    }

    public int rows() {
        return Math.max(1, (bounds.height - catalogY() - 4) / CELL);
    }

    public int catalogSlots() {
        return columns() * rows();
    }

    public int scrollX() {
        return bounds.width - 13;
    }

    public int scrollY() {
        return Math.max(catalogY(), HEADER_Y + 7 * BUTTON_HEIGHT + 2);
    }

    public int scrollHeight() {
        return catalogY() + rows() * CELL - scrollY();
    }

    public int dragHandleX() {
        return bounds.width - BUTTON_WIDTH - 4;
    }

    public boolean dragHandleAt(int x, int y) {
        if (!bounds.contains(bounds.x + x, bounds.y + y)) return false;
        if (!expanded) return x < 4 || y < 4 || x >= bounds.width - 4 || y >= bounds.height - 4;
        return Area.isInside(dragHandleX(), HEADER_Y, BUTTON_WIDTH, BUTTON_HEIGHT, x, y);
    }

    public int slotX(int slot, boolean pin) {
        if (pin && slot >= firstPinCount()) slot -= firstPinCount();
        return GRID_X + slot % columns() * CELL;
    }

    public int slotY(int slot, boolean pin) {
        if (!pin) return catalogY() + slot / columns() * CELL;
        int firstCount = firstPinCount();
        if (slot < firstCount) return PIN_Y + slot / columns() * CELL;
        return PIN_Y + (rowsForPins(firstCount) + (slot - firstCount) / columns()) * CELL;
    }

}
