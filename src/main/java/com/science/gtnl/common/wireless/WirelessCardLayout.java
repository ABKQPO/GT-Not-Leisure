package com.science.gtnl.common.wireless;

/** Dimensions are scaled GUI pixels, not physical display pixels. Row IDs reserve ten values per action. */
public record WirelessCardLayout(int width, int height, int rows) {

    public static final int MIN_ROWS = 5;
    public static final int MAX_ROWS = 9;

    public static WirelessCardLayout forScreen(int width, int height) {
        int rows = Math.max(MIN_ROWS, Math.min(MAX_ROWS, (height - 8 - 112) / 24));
        return new WirelessCardLayout(Math.max(320, Math.min(440, width - 12)), 112 + rows * 24, rows);
    }

    public static boolean validRows(int rows) {
        return rows >= MIN_ROWS && rows <= MAX_ROWS;
    }
}
