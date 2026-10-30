package com.gtnllib.gui.wheel;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.cleanroommc.modularui.api.drawable.IDrawable;

public class WheelEntry {

    private static final BooleanSupplier ALWAYS = () -> true;

    private final String id;
    private final Supplier<String> label;
    private final IDrawable icon;
    private final IWheelEntryAction action;
    private final List<WheelEntry> submenu;
    private final BooleanSupplier enabled;
    private final BooleanSupplier visible;
    private final boolean placeholder;
    private final IWheelEntryAction rightClick;

    private WheelEntry(String id, Supplier<String> label, IDrawable icon, IWheelEntryAction action,
        List<WheelEntry> submenu, BooleanSupplier enabled, boolean placeholder) {
        this(id, label, icon, action, submenu, enabled, ALWAYS, placeholder, null);
    }

    private WheelEntry(String id, Supplier<String> label, IDrawable icon, IWheelEntryAction action,
        List<WheelEntry> submenu, BooleanSupplier enabled, BooleanSupplier visible, boolean placeholder,
        IWheelEntryAction rightClick) {
        this.id = Objects.requireNonNull(id, "id");
        this.label = Objects.requireNonNull(label, "label");
        this.icon = icon;
        this.action = action;
        this.submenu = List.copyOf(submenu);
        this.enabled = Objects.requireNonNull(enabled, "enabled");
        this.visible = Objects.requireNonNull(visible, "visible");
        this.placeholder = placeholder;
        this.rightClick = rightClick;
    }

    private static Supplier<String> fixed(String label) {
        Objects.requireNonNull(label, "label");
        return () -> label;
    }

    public static WheelEntry action(String id, String label, IWheelEntryAction action) {
        return new WheelEntry(
            id,
            fixed(label),
            null,
            Objects.requireNonNull(action, "action"),
            List.of(),
            ALWAYS,
            false);
    }

    public static WheelEntry action(String id, String label, IDrawable icon, IWheelEntryAction action) {
        return new WheelEntry(
            id,
            fixed(label),
            Objects.requireNonNull(icon, "icon"),
            Objects.requireNonNull(action, "action"),
            List.of(),
            ALWAYS,
            false);
    }

    public static WheelEntry submenu(String id, String label, List<WheelEntry> submenu) {
        return new WheelEntry(id, fixed(label), null, null, Objects.requireNonNull(submenu, "submenu"), ALWAYS, false);
    }

    public static WheelEntry submenu(String id, String label, IDrawable icon, List<WheelEntry> submenu) {
        return new WheelEntry(
            id,
            fixed(label),
            Objects.requireNonNull(icon, "icon"),
            null,
            Objects.requireNonNull(submenu, "submenu"),
            ALWAYS,
            false);
    }

    public static WheelEntry display(String id, String label, IDrawable icon) {
        return new WheelEntry(id, fixed(label), Objects.requireNonNull(icon, "icon"), null, List.of(), ALWAYS, false);
    }

    static WheelEntry placeholder(int slotIndex) {
        return new WheelEntry("__empty_slot_" + slotIndex, fixed(""), IDrawable.EMPTY, null, List.of(), ALWAYS, true);
    }

    public WheelEntry labelFrom(Supplier<String> label) {
        return new WheelEntry(id, label, icon, action, submenu, enabled, visible, placeholder, rightClick);
    }

    public WheelEntry enabledWhen(BooleanSupplier enabled) {
        return new WheelEntry(id, label, icon, action, submenu, enabled, visible, placeholder, rightClick);
    }

    public WheelEntry visibleWhen(BooleanSupplier visible) {
        return new WheelEntry(id, label, icon, action, submenu, enabled, visible, placeholder, rightClick);
    }

    public WheelEntry onRightClick(IWheelEntryAction rightClick) {
        return new WheelEntry(
            id,
            label,
            icon,
            action,
            submenu,
            enabled,
            visible,
            placeholder,
            Objects.requireNonNull(rightClick, "rightClick"));
    }

    public String id() {
        return id;
    }

    public String label() {
        String text = label.get();
        return text == null ? "" : text;
    }

    public IDrawable icon() {
        return icon;
    }

    public IWheelEntryAction action() {
        return action;
    }

    public IWheelEntryAction rightClick() {
        return rightClick;
    }

    public List<WheelEntry> submenu() {
        return submenu;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public boolean hasSubmenu() {
        return !submenu.isEmpty();
    }

    public boolean isVisible() {
        return placeholder || visible.getAsBoolean();
    }

    public boolean isEnabled() {
        return !placeholder && visible.getAsBoolean() && enabled.getAsBoolean();
    }

    public boolean isSelectable(WheelOpenMode mode) {
        if (!isEnabled()) return false;
        if (mode == WheelOpenMode.HOLD && hasSubmenu()) return false;
        return action != null || hasSubmenu();
    }
}
