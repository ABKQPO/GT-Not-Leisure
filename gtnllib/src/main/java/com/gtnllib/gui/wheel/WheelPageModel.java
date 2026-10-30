package com.gtnllib.gui.wheel;

import java.util.List;

public record WheelPageModel(int pageIndex, int slotsPerPage, List<WheelEntry> slots) {

    public WheelPageModel {
        slots = List.copyOf(slots);
        if (slotsPerPage < 1) throw new IllegalArgumentException("slotsPerPage must be >= 1");
        if (slots.size() > slotsPerPage) throw new IllegalArgumentException("slots size must not exceed slotsPerPage");
    }

    public int slotCount() {
        return slots.size();
    }

    public WheelEntry slot(int slotIndex) {
        return slots.get(slotIndex);
    }

    public boolean isSelectable(int slotIndex, WheelOpenMode mode) {
        return slot(slotIndex).isSelectable(mode);
    }
}
