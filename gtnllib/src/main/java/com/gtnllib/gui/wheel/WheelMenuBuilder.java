package com.gtnllib.gui.wheel;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.cleanroommc.modularui.api.drawable.IDrawable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class WheelMenuBuilder {

    private final List<WheelEntry> entries = new ObjectArrayList<>();
    private int slotsPerPage = WheelMenuModel.DEFAULT_SLOTS_PER_PAGE;
    private WheelStyle style = WheelStyle.DEFAULT;

    public static WheelMenuBuilder create() {
        return new WheelMenuBuilder();
    }

    public WheelMenuBuilder slotsPerPage(int slotsPerPage) {
        if (slotsPerPage < 1) throw new IllegalArgumentException("slotsPerPage must be >= 1");
        this.slotsPerPage = slotsPerPage;
        return this;
    }

    public WheelMenuBuilder style(WheelStyle style) {
        this.style = Objects.requireNonNull(style, "style");
        return this;
    }

    public WheelMenuBuilder entry(WheelEntry entry) {
        entries.add(Objects.requireNonNull(entry, "entry"));
        return this;
    }

    public WheelMenuBuilder action(String id, String label, IWheelEntryAction action) {
        return entry(WheelEntry.action(id, label, action));
    }

    public WheelMenuBuilder action(String id, String label, IDrawable icon, IWheelEntryAction action) {
        return entry(WheelEntry.action(id, label, icon, action));
    }

    public WheelMenuBuilder display(String id, String label, IDrawable icon) {
        return entry(WheelEntry.display(id, label, icon));
    }

    public WheelMenuBuilder submenu(String id, String label, Consumer<SubmenuBuilder> submenu) {
        return entry(WheelEntry.submenu(id, label, SubmenuBuilder.build(submenu)));
    }

    public WheelMenuBuilder submenu(String id, String label, IDrawable icon, Consumer<SubmenuBuilder> submenu) {
        return entry(WheelEntry.submenu(id, label, icon, SubmenuBuilder.build(submenu)));
    }

    public WheelMenuModel build() {
        return WheelMenuModel.of(entries, slotsPerPage, style);
    }

    public static class SubmenuBuilder {

        private final List<WheelEntry> entries = new ObjectArrayList<>();

        private static List<WheelEntry> build(Consumer<SubmenuBuilder> submenu) {
            SubmenuBuilder builder = new SubmenuBuilder();
            Objects.requireNonNull(submenu, "submenu")
                .accept(builder);
            return builder.entries;
        }

        public SubmenuBuilder action(String id, String label, IWheelEntryAction action) {
            entries.add(WheelEntry.action(id, label, action));
            return this;
        }

        public SubmenuBuilder action(String id, String label, IDrawable icon, IWheelEntryAction action) {
            entries.add(WheelEntry.action(id, label, icon, action));
            return this;
        }

        public SubmenuBuilder display(String id, String label, IDrawable icon) {
            entries.add(WheelEntry.display(id, label, icon));
            return this;
        }
    }
}
