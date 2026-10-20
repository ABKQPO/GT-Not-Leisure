package com.science.gtnl.common.gui.modularui.wheel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class WheelNavigator {

    public enum Outcome {
        NONE,
        SUBMENU,
        ACTION
    }

    private final WheelMenuModel model;
    private final WheelOpenMode openMode;
    private final Deque<List<WheelEntry>> menuStack = new ArrayDeque<>();
    private List<WheelEntry> visible;
    private WheelPageModel page;
    private int pageIndex;
    private int pageCount = 1;

    public WheelNavigator(WheelMenuModel model, WheelOpenMode openMode) {
        this.model = Objects.requireNonNull(model, "model");
        this.openMode = Objects.requireNonNull(openMode, "openMode");
        menuStack.addLast(model.rootEntries());
        refresh();
    }

    public WheelMenuModel model() {
        return model;
    }

    public WheelOpenMode openMode() {
        return openMode;
    }

    public WheelPageModel page() {
        return page;
    }

    public int pageIndex() {
        return pageIndex;
    }

    public int pageCount() {
        return pageCount;
    }

    public int depth() {
        return menuStack.size();
    }

    public int slotCount() {
        return page.slotCount();
    }

    public WheelEntry entry(int slot) {
        return slot >= 0 && slot < page.slotCount() ? page.slot(slot) : null;
    }

    public boolean isSelectable(int slot) {
        WheelEntry entry = entry(slot);
        return entry != null && entry.isSelectable(openMode);
    }

    public boolean refresh() {
        List<WheelEntry> entries = menuStack.getLast();
        if (visible != null && sameVisible(entries)) return false;
        List<WheelEntry> shown = new ArrayList<>(entries.size());
        for (WheelEntry entry : entries) if (entry.isVisible()) shown.add(entry);
        visible = shown;
        pageCount = model.pageCount(shown);
        pageIndex = Math.min(pageIndex, pageCount - 1);
        page = model.page(shown, pageIndex);
        return true;
    }

    public Outcome confirm(int slot) {
        if (!isSelectable(slot)) return Outcome.NONE;
        WheelEntry entry = page.slot(slot);
        if (openMode == WheelOpenMode.TAP && entry.hasSubmenu()) {
            menuStack.addLast(entry.submenu());
            pageIndex = 0;
            reload();
            return Outcome.SUBMENU;
        }
        if (entry.action() == null) return Outcome.NONE;
        entry.action()
            .trigger(new WheelActionContext(pageIndex, slot, entry.id(), openMode));
        return Outcome.ACTION;
    }

    public boolean back() {
        if (menuStack.size() <= 1) return false;
        menuStack.removeLast();
        pageIndex = 0;
        reload();
        return true;
    }

    public boolean turnPage(int direction) {
        if (pageCount <= 1 || direction == 0) return false;
        pageIndex = Math.floorMod(pageIndex + direction, pageCount);
        reload();
        return true;
    }

    private void reload() {
        visible = null;
        refresh();
    }

    private boolean sameVisible(List<WheelEntry> entries) {
        int index = 0;
        for (WheelEntry entry : entries) {
            if (!entry.isVisible()) continue;
            if (index >= visible.size() || visible.get(index) != entry) return false;
            index++;
        }
        return index == visible.size();
    }
}
