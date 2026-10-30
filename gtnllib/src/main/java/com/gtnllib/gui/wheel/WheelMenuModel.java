package com.gtnllib.gui.wheel;

import java.util.List;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class WheelMenuModel {

    public static final int DEFAULT_SLOTS_PER_PAGE = 8;

    private final List<WheelEntry> rootEntries;
    private final int slotsPerPage;
    private final WheelStyle style;

    private WheelMenuModel(List<WheelEntry> rootEntries, int slotsPerPage, WheelStyle style) {
        if (slotsPerPage < 1) throw new IllegalArgumentException("slotsPerPage must be >= 1");
        this.rootEntries = List.copyOf(Objects.requireNonNull(rootEntries, "rootEntries"));
        this.slotsPerPage = slotsPerPage;
        this.style = Objects.requireNonNull(style, "style");
    }

    public static WheelMenuModel of(List<WheelEntry> rootEntries, int slotsPerPage, WheelStyle style) {
        return new WheelMenuModel(rootEntries, slotsPerPage, style);
    }

    public static int pageCount(int entryCount, int slotsPerPage) {
        if (slotsPerPage < 1) throw new IllegalArgumentException("slotsPerPage must be >= 1");
        return entryCount <= 0 ? 1 : (entryCount + slotsPerPage - 1) / slotsPerPage;
    }

    public List<WheelEntry> rootEntries() {
        return rootEntries;
    }

    public WheelStyle style() {
        return style;
    }

    public int pageCount(List<WheelEntry> entries) {
        return pageCount(
            Objects.requireNonNull(entries, "entries")
                .size(),
            slotsPerPage);
    }

    public WheelPageModel page(List<WheelEntry> entries, int pageIndex) {
        int pages = pageCount(entries);
        if (pageIndex < 0 || pageIndex >= pages)
            throw new IllegalArgumentException("pageIndex out of range: " + pageIndex);
        int start = pageIndex * slotsPerPage;
        int end = Math.min(start + slotsPerPage, entries.size());
        List<WheelEntry> slots = new ObjectArrayList<>(slotsPerPage);
        for (int i = start; i < end; i++) slots.add(entries.get(i));
        if (pages > 1) for (int i = slots.size(); i < slotsPerPage; i++) slots.add(WheelEntry.placeholder(i));
        return new WheelPageModel(pageIndex, slotsPerPage, slots);
    }
}
