package com.science.gtnl.common.wireless;

public class WirelessCardActions {

    public static final int PREVIOUS_PAGE = 0;
    public static final int NEXT_PAGE = 1;
    public static final int TOGGLE_AUTO = 2;
    public static final int REFRESH = 3;
    public static final int BACK = 4;
    public static final int RENAME = 5;
    public static final int CYCLE_VISUALISATION = 9;

    public static final int TOGGLE_PAUSE = 10;
    public static final int OPEN_DETAILS = 20;
    public static final int COPY_POSITION = 30;
    public static final int HIGHLIGHT = 40;
    public static final int TELEPORT = 50;
    public static final int CLOSE_DETAILS = 60;
    public static final int TOGGLE_GROUPING = 61;
    public static final int SET_VISUALISATION = 62;
    private static final int ROW_STRIDE = 10;

    public static int type(int id) {
        return isRowAction(id) ? id - rowIndex(id) : id;
    }

    public static int rowIndex(int id) {
        return id % ROW_STRIDE;
    }

    public static boolean isRowAction(int id) {
        return id >= TOGGLE_PAUSE && id < TELEPORT + ROW_STRIDE;
    }
}
