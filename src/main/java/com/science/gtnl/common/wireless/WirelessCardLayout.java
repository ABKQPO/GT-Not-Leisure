package com.science.gtnl.common.wireless;

public record WirelessCardLayout(int width, int height, int rows) {

    public static final int MIN_ROWS = 5;
    public static final int MAX_ROWS = 9;

    public static WirelessCardLayout forScreen(int width, int height) {
        int rows = Math.max(MIN_ROWS, Math.min(MAX_ROWS, (height - 8 - 112) / 24));
        return new WirelessCardLayout(Math.max(320, Math.min(520, width - 12)), 112 + rows * 24, rows);
    }

    public int visualisationWidth() {
        return Math.max(66, Math.min(120, width - 250));
    }

    public int visualisationX() {
        return width - 34 - visualisationWidth();
    }

    public static boolean validRows(int rows) {
        return rows >= MIN_ROWS && rows <= MAX_ROWS;
    }
}
