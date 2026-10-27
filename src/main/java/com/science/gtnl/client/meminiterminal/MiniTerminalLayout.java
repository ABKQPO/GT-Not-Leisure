package com.science.gtnl.client.meminiterminal;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import com.cleanroommc.modularui.widget.sizer.Area;
import com.science.gtnl.client.floatingwindow.FloatingWindowGeometry;

import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStackType;
import appeng.items.contents.PinList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MiniTerminalLayout extends FloatingWindowGeometry {

    public static final int COLUMNS = 5;
    public static final int MIN_COLUMNS = 2;
    public static final int ROWS = 6;
    public static final int CELL = 18;
    public static final int WIDTH = 111;
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
    private static final int FIXED_CONTROL_BUTTONS = 5;
    private static final int MIN_SCROLL_HEIGHT = 13;
    private static List<IAEStackType<?>> toggleTypes;
    private final Rectangle bounds = area();
    private int craftingPins;
    private int playerPins;
    private boolean playerFirst = true;

    static List<IAEStackType<?>> toggleTypes() {
        if (toggleTypes == null) {
            List<IAEStackType<?>> types = new ObjectArrayList<>();
            for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
                if (type != ITEM_STACK_TYPE && type.getButtonTexture() != null && type.getButtonIcon() != null)
                    types.add(type);
            }
            toggleTypes = Collections.unmodifiableList(types);
        }
        return toggleTypes;
    }

    static int controlButtons() {
        return FIXED_CONTROL_BUTTONS + toggleTypes().size();
    }

    static int minimumHeight() {
        int controlsBottom = HEADER_Y + (1 + controlButtons()) * BUTTON_HEIGHT + 2 + MIN_SCROLL_HEIGHT;
        int height = MIN_HEIGHT;
        while (PIN_Y + (height - PIN_Y - 4) / CELL * CELL < controlsBottom) height += CELL;
        return height;
    }

    public void configurePins(int craftingCount, int playerCount, boolean playerFirst) {
        int nextCrafting = Math.max(0, Math.min(PinList.CRAFTING_SLOTS, craftingCount));
        int nextPlayer = Math.max(0, Math.min(PinList.PLAYER_SLOTS, playerCount));
        if (craftingPins == nextCrafting && playerPins == nextPlayer && this.playerFirst == playerFirst) return;
        craftingPins = nextCrafting;
        playerPins = nextPlayer;
        this.playerFirst = playerFirst;
        markGeometryDirty();
    }

    @Override
    public int minWidth() {
        return MIN_WIDTH;
    }

    @Override
    public int minHeight() {
        return minimumHeight();
    }

    @Override
    public int defaultWidth() {
        return WIDTH;
    }

    @Override
    public int cellSize() {
        return CELL;
    }

    @Override
    public int compactSize() {
        return COMPACT_SIZE;
    }

    @Override
    protected int restoredMinHeight() {
        return MIN_HEIGHT;
    }

    @Override
    protected Rectangle dragHandle(int width, int height, boolean expanded) {
        return expanded ? new Rectangle(width - BUTTON_WIDTH - 4, HEADER_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
            : new Rectangle(0, 0, width, height);
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
        return Math.max(catalogY(), HEADER_Y + (1 + controlButtons()) * BUTTON_HEIGHT + 2);
    }

    public int scrollHeight() {
        return catalogY() + rows() * CELL - scrollY();
    }

    public int dragHandleX() {
        return bounds.width - BUTTON_WIDTH - 4;
    }

    public boolean dragHandleAt(int x, int y) {
        if (!bounds.contains(bounds.x + x, bounds.y + y)) return false;
        if (!expanded()) return x < 4 || y < 4 || x >= bounds.width - 4 || y >= bounds.height - 4;
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
