package com.gtnllib.gui.layout;

import java.util.Collections;
import java.util.List;

public class LayoutMath {

    public static int total(int[] sizes, int gap) {
        int total = 0;
        for (int size : sizes) total += size;
        return total + gap * Math.max(0, sizes.length - 1);
    }

    public static int widest(int... sizes) {
        int widest = 0;
        for (int size : sizes) widest = Math.max(widest, size);
        return widest;
    }

    public static int[] line(List<Size> sizes, int available, int gap) {
        int count = sizes.size();
        int[] result = new int[count];
        boolean[] settled = new boolean[count];
        int free = available - gap * Math.max(0, count - 1);
        for (int i = 0; i < count; i++) {
            Size size = sizes.get(i);
            if (size.fills()) continue;
            result[i] = size.resolve(available);
            settled[i] = true;
            free -= result[i];
        }
        int[] shares = new int[count];
        boolean clamped = true;
        while (clamped) {
            clamped = false;
            share(sizes, settled, free, shares);
            int frozen = 0;
            for (int i = 0; i < count; i++) {
                if (settled[i]) continue;
                int bounded = sizes.get(i)
                    .clamp(shares[i]);
                if (bounded == shares[i]) continue;
                result[i] = bounded;
                settled[i] = true;
                frozen += bounded;
                clamped = true;
            }
            free -= frozen;
        }
        int used = 0;
        for (int i = 0; i < count; i++) if (!settled[i]) {
            result[i] = shares[i];
            used += shares[i];
        }
        for (int i = 0, left = Math.max(0, free) - used; i < count && left > 0; i++) {
            if (settled[i] || result[i] >= sizes.get(i)
                .max()) continue;
            result[i]++;
            left--;
        }
        return result;
    }

    private static void share(List<Size> sizes, boolean[] settled, int free, int[] shares) {
        double weight = 0;
        for (int i = 0; i < settled.length; i++) if (!settled[i]) weight += sizes.get(i)
            .value();
        for (int i = 0; i < settled.length; i++) shares[i] = settled[i] || weight <= 0 ? 0
            : (int) (Math.max(0, free) * sizes.get(i)
                .value() / weight);
    }

    public static int[] split(int available, int gap, int count) {
        return line(Collections.nCopies(Math.max(0, count), Size.fill()), available, gap);
    }

    public static int columns(int available, int gap, int widest, int maxColumns) {
        if (maxColumns <= 1) return 1;
        if (widest <= 0) return maxColumns;
        return Math.max(1, Math.min(maxColumns, (available + gap) / (widest + gap)));
    }

    public static int rows(int count, int columns) {
        return columns <= 0 ? count : (count + columns - 1) / columns;
    }

    public static int[] offsets(int[] sizes, int gap) {
        int[] result = new int[sizes.length];
        for (int i = 1; i < sizes.length; i++) result[i] = result[i - 1] + sizes[i - 1] + gap;
        return result;
    }

    public static int fit(int space, int margin, int min, int max) {
        return Math.max(min, Math.min(max, space - margin * 2));
    }

    public static int fitRows(int space, int rowHeight, int minRows, int maxRows) {
        if (rowHeight <= 0) return 0;
        return Math.max(0, Math.min(maxRows, Math.max(minRows, space / rowHeight))) * rowHeight;
    }

    public static int[] shrink(int[] sizes, int[] minimums, int available, int gap) {
        int[] result = sizes.clone();
        int overflow = total(sizes, gap) - available, slack = 0;
        if (overflow <= 0) return result;
        for (int i = 0; i < sizes.length; i++) slack += Math.max(0, sizes[i] - minimums[i]);
        if (slack <= 0) return result;
        int cut = 0;
        for (int i = 0; i < sizes.length; i++) {
            int room = Math.max(0, sizes[i] - minimums[i]),
                taken = (int) Math.min(room, (long) Math.min(overflow, slack) * room / slack);
            result[i] -= taken;
            cut += taken;
        }
        for (int i = 0, left = Math.min(overflow, slack) - cut; i < sizes.length && left > 0; i++) {
            if (result[i] <= minimums[i]) continue;
            result[i]--;
            left--;
        }
        return result;
    }

    public static boolean stacks(int[] widths, int[] heights, int gap, int width, int height) {
        return total(widths, gap) > width && total(heights, gap) <= height;
    }
}
