package com.science.gtnl.client.yogsothoth;

import java.awt.Point;
import java.awt.Rectangle;

import com.science.gtnl.client.floatingwindow.FloatingWindowGeometry;

public class YogTerminalLayout extends FloatingWindowGeometry {

    public static final int CELL = 18;
    public static final int BORDER = 6;
    public static final int GRID_Y = 17;
    public static final int HEADER_Y = 5;
    public static final int BUTTON_SIZE = 10;
    public static final int BUTTON_GAP = 1;
    public static final int SCROLL_WIDTH = 6;
    public static final int SCROLL_SPACE = SCROLL_WIDTH + 3;
    public static final int MIN_COLUMNS = 5;
    public static final int MAX_ROWS = 8;
    public static final int MIN_WIDTH = BORDER * 2 + MIN_COLUMNS * CELL;
    public static final int MIN_HEIGHT = GRID_Y + CELL + BORDER;
    public static final int COMPACT_SIZE = 18;

    private int cells = 1;
    private int columns = 1;
    private int rows = 1;
    private int visibleRows = 1;

    public void setCells(int cells) {
        cells = Math.max(1, cells);
        if (this.cells == cells) return;
        this.cells = cells;
        markGeometryDirty();
    }

    @Override
    public int minWidth() {
        return MIN_WIDTH;
    }

    @Override
    public int minHeight() {
        return MIN_HEIGHT;
    }

    @Override
    public int defaultWidth() {
        return MIN_WIDTH;
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
    protected int defaultHeight(int guiHeight) {
        return MIN_HEIGHT;
    }

    @Override
    protected int fitWidth(int width) {
        int maxColumns = Math.max(MIN_COLUMNS, (width - BORDER * 2) / CELL);
        columns = Math.min(cells, maxColumns);
        rows = (cells + columns - 1) / columns;
        int fitting = Math.max(1, (screenHeight() - 2 - GRID_Y - BORDER) / CELL);
        visibleRows = Math.min(rows, Math.min(MAX_ROWS, fitting));
        int content = BORDER * 2 + columns * CELL + (scrolls() ? SCROLL_SPACE : 0);
        return Math.max(MIN_WIDTH, content);
    }

    @Override
    protected int fitHeight(int height) {
        return GRID_Y + visibleRows * CELL + BORDER;
    }

    @Override
    protected Point defaultPosition(int guiLeft, int guiTop, int guiWidth, int guiHeight, int width) {
        return new Point(guiLeft - width - 3, guiTop);
    }

    @Override
    protected Rectangle dragHandle(int width, int height, boolean expanded) {
        return expanded ? new Rectangle(0, 0, Math.max(1, width - buttonsWidth()), GRID_Y)
            : new Rectangle(0, 0, width, height);
    }

    private static int buttonsWidth() {
        return BORDER + 4 * (BUTTON_SIZE + BUTTON_GAP);
    }

    public int columns() {
        return columns;
    }

    public int visibleRows() {
        return visibleRows;
    }

    public boolean scrolls() {
        return rows > visibleRows;
    }

    public int maxScroll() {
        return Math.max(0, rows - visibleRows);
    }

    public int cellAt(int x, int y) {
        if (x < BORDER || y < GRID_Y) return -1;
        int column = (x - BORDER) / CELL;
        int row = (y - GRID_Y) / CELL;
        if (column >= columns || row >= visibleRows) return -1;
        return row * columns + column;
    }

    public int cellX(int cell) {
        return BORDER + cell % columns * CELL;
    }

    public int cellY(int cell) {
        return GRID_Y + cell / columns * CELL;
    }

    public int scrollX() {
        return area().width - BORDER - SCROLL_WIDTH;
    }

    public static int buttonRight(int button) {
        return BORDER + button * (BUTTON_SIZE + BUTTON_GAP);
    }

    public int titleWidth() {
        return area().width - BORDER - buttonsWidth();
    }

    public boolean dragHandleAt(int x, int y) {
        Rectangle area = area();
        if (x < 0 || y < 0 || x >= area.width || y >= area.height) return false;
        if (!expanded()) return x < 4 || y < 4 || x >= area.width - 4 || y >= area.height - 4;
        return y < GRID_Y && x < area.width - buttonsWidth();
    }
}
