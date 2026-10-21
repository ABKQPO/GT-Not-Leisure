package com.science.gtnl.common.gui.equipment;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.LayoutMath;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.theme.ButtonPurpose;
import com.gtnllib.gui.widget.CloseButton;
import com.gtnllib.gui.widget.Divider;
import com.gtnllib.gui.widget.FillBar;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EquipmentGui {

    protected static final int WIDTH = 176, TITLE_Y = 6, TEXT_HEIGHT = 10, MAX_COLUMNS = 3;
    protected static final int SLOT_SIZE = 18;
    private static final int MAX_WIDTH = 256, SCREEN_MARGIN = 4, MIN_PAGE_ROWS = 3;
    private static final int INVENTORY_HEIGHT = 76, INVENTORY_BOTTOM = 7, BAR_HEIGHT = 5, MIN_BAR_WIDTH = 40;

    protected final PlayerInventoryGuiData data;
    protected final PanelSyncManager sync;
    protected final ItemStack stack;
    protected final boolean client;
    protected final LayoutTheme style;
    private ModularPanel panel;
    private int moduleSlots, heldSlots, energyWidth = -1;
    private IntSupplier energyLabel = () -> 0;

    protected EquipmentGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        this.data = data;
        this.sync = sync;
        this.stack = data.getUsedItemStack();
        if (!holds(stack)) throw new IllegalStateException("Missing equipment for " + getClass().getSimpleName());
        this.client = data.getPlayer().worldObj.isRemote;
        this.style = client ? themeStyle() : LayoutTheme.DEFAULT;
    }

    @SideOnly(Side.CLIENT)
    private static LayoutTheme themeStyle() {
        return EquipmentTheme.layout();
    }

    /**
     * Whether the stack is the kind of equipment this GUI configures.
     */
    public abstract boolean holds(ItemStack stack);

    /**
     * Builds the whole panel, including the player inventory.
     */
    public abstract ModularPanel build();

    public void syncView(boolean bare) {}

    protected ItemStack bound() {
        ItemStack used = data.getUsedItemStack();
        return holds(used) ? used : stack;
    }

    protected void changed() {
        data.getPlayer().inventory.markDirty();
    }

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    protected int footerHeight() {
        return INVENTORY_BOTTOM + INVENTORY_HEIGHT + style.spacingLarge;
    }

    protected int slotsTop() {
        return TITLE_Y + TEXT_HEIGHT + style.spacingSmall;
    }

    protected void reserveHeld(int slots) {
        heldSlots = slots;
    }

    private int slotRoom() {
        return contentWidth() - (heldSlots == 0 ? 0 : heldSlots * SLOT_SIZE + style.spacingMedium);
    }

    private int energyTop() {
        return slotsTop() + slotRows(moduleSlots, slotRoom()) * SLOT_SIZE + style.spacingMedium;
    }

    private boolean energyInline() {
        return energyLabel.getAsInt() + style.spacingMedium + MIN_BAR_WIDTH <= contentWidth();
    }

    protected int headerBottom() {
        return energyTop() + (energyInline() ? TEXT_HEIGHT : TEXT_HEIGHT + style.spacingSmall + BAR_HEIGHT)
            + style.spacingMedium;
    }

    protected int listWidth() {
        return panelWidth() - style.padding + style.spacingSmall;
    }

    protected int panelWidth() {
        if (panel == null || !panel.isValid()) return WIDTH;
        return LayoutMath.fit(Layout.screenWidth(panel), SCREEN_MARGIN, WIDTH, MAX_WIDTH);
    }

    protected int contentWidth() {
        return panelWidth() - style.padding * 2;
    }

    protected int fitHeight(int top, int needed) {
        if (panel == null || !panel.isValid()) return needed;
        return LayoutMath.fitRows(
            Layout.screenHeight(panel) - SCREEN_MARGIN * 2 - top - footerHeight(),
            style.rowHeight,
            MIN_PAGE_ROWS,
            LayoutMath.rows(needed, style.rowHeight));
    }

    protected Layout rows() {
        return Layout.rows(style, this::panelWidth);
    }

    protected int textWidth(String text) {
        return client ? Layout.textWidth(text, style) : 0;
    }

    protected int buttonWidth(String text, int padding, int min) {
        return Size.content(textWidth(text), padding)
            .atLeast(min)
            .atMost(contentWidth())
            .resolve(contentWidth());
    }

    protected int widestSwitch(Iterable<String> labels) {
        int widest = 0;
        for (String label : labels) widest = Math.max(widest, SlideSwitch.widthFor(textWidth(label)));
        return widest;
    }

    protected ModularPanel panel(String name, IntSupplier contentHeight) {
        panel = new ModularPanel(name);
        return panel.width(this::panelWidth, Unit.Measure.PIXEL)
            .height(() -> contentHeight.getAsInt() + footerHeight(), Unit.Measure.PIXEL)
            .child(new CloseButton().corner(style.padding, style));
    }

    protected void bindFooter(ModularPanel panel) {
        panel.child(
            new Divider().color(client ? dividerColor() : 0)
                .left(style.padding)
                .bottom(INVENTORY_BOTTOM + INVENTORY_HEIGHT + (style.spacingLarge - Divider.HEIGHT) / 2)
                .width(this::contentWidth, Unit.Measure.PIXEL)
                .height(Divider.HEIGHT));
        panel.bindPlayerInventory();
    }

    @SideOnly(Side.CLIENT)
    private static int dividerColor() {
        return EquipmentTheme.colors().accentDim;
    }

    protected static ButtonWidget<?> button(ButtonPurpose purpose) {
        ButtonWidget<?> button = new ButtonWidget<>();
        return purpose.apply(button);
    }

    protected void title(ModularPanel panel, String lang, int reserved) {
        panel.child(
            Layout.label(IKey.lang(lang), style)
                .pos(style.padding, TITLE_Y)
                .width(() -> contentWidth() - CloseButton.reserve(style.padding, style) - reserved, Unit.Measure.PIXEL)
                .height(TEXT_HEIGHT));
    }

    protected static int slotsPerRow(int slots, int room) {
        return LayoutMath.rows(slots, slotRows(slots, room));
    }

    protected static int slotRows(int slots, int room) {
        return LayoutMath.rows(slots, Math.max(1, room / SLOT_SIZE));
    }

    protected void moduleSlots(ModularPanel panel, ModuleSlots<?> handler, String group, String limits) {
        sync.registerSlotGroup(new SlotGroup(group, handler.getSlots(), 0, true));
        moduleSlots = handler.getSlots();
        IntSupplier perRow = () -> slotsPerRow(moduleSlots, slotRoom());
        for (int i = 0; i < handler.getSlots(); i++) {
            final int slot = i;
            panel
                .child(
                    new ItemSlot()
                        .slot(
                            new ModuleSlot(handler, i).filter(candidate -> handler.isItemValid(slot, candidate))
                                .slotGroup(group))
                        .overlay((context, x, top, width, height, theme) -> {
                            if (handler.getStackInSlot(slot) == null)
                                GTNLMui2Textures.OVERLAY_SLOT_MODULE.draw(context, x, top, width, height, theme);
                        })
                        .left(() -> style.padding + slot % perRow.getAsInt() * SLOT_SIZE, Unit.Measure.PIXEL)
                        .top(() -> slotsTop() + slot / perRow.getAsInt() * SLOT_SIZE, Unit.Measure.PIXEL)
                        .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(limits))));
        }
    }

    protected void energyBar(ModularPanel panel, DoubleSupplier energy, DoubleSupplier capacity) {
        LongSyncValue stored = new LongSyncValue(() -> (long) energy.getAsDouble());
        LongSyncValue max = new LongSyncValue(() -> (long) capacity.getAsDouble());
        sync.syncValue("energy", stored);
        sync.syncValue("capacity", max);
        energyLabel = () -> textWidth(energyText(max.getLongValue(), max.getLongValue()));
        panel.child(
            Layout.label(IKey.dynamic(() -> energyText(stored.getLongValue(), max.getLongValue())), style)
                .left(style.padding)
                .top(this::energyTop, Unit.Measure.PIXEL)
                .width(() -> energyInline() ? energyLabel.getAsInt() : contentWidth(), Unit.Measure.PIXEL)
                .height(TEXT_HEIGHT)
                .onUpdateListener(label -> {
                    int width = energyLabel.getAsInt();
                    if (width == energyWidth) return;
                    energyWidth = width;
                    panel.scheduleResize();
                }));
        panel.child(
            new FillBar().value(() -> stored.getLongValue() / (double) Math.max(1, max.getLongValue()))
                .left(
                    () -> style.padding + (energyInline() ? energyLabel.getAsInt() + style.spacingMedium : 0),
                    Unit.Measure.PIXEL)
                .top(
                    () -> energyTop()
                        + (energyInline() ? (TEXT_HEIGHT - BAR_HEIGHT) / 2 : TEXT_HEIGHT + style.spacingSmall),
                    Unit.Measure.PIXEL)
                .width(
                    () -> energyInline() ? contentWidth() - energyLabel.getAsInt() - style.spacingMedium
                        : contentWidth(),
                    Unit.Measure.PIXEL)
                .height(BAR_HEIGHT));
    }

    private static String energyText(long stored, long max) {
        return StatCollector.translateToLocalFormatted(
            "item.gtnl.equipment.gui.energy",
            EquipmentEnergy.format(stored),
            EquipmentEnergy.format(max));
    }

    protected BooleanSyncValue syncToggle(String key, BooleanSupplier getter, Consumer<Boolean> setter,
        BooleanSupplier allowed) {
        BooleanSyncValue value = new BooleanSyncValue(
            getter,
            enabled -> { if (allowed.getAsBoolean()) setter.accept(enabled); }).allowC2S();
        sync.syncValue(key, value);
        return value;
    }

    protected static void stateTooltip(RichTooltip tooltip, String hint, BooleanSupplier allowed, BooleanSupplier on) {
        if (hint != null) tooltip.addLine(IKey.lang(hint));
        tooltip.addLine(
            IKey.dynamic(
                () -> tr(
                    allowed.getAsBoolean()
                        ? on.getAsBoolean() ? "item.gtnl.equipment.gui.enabled" : "item.gtnl.equipment.gui.disabled"
                        : "item.gtnl.equipment.gui.unavailable")));
    }

    protected SlideSwitch toggle(String key, String lang, BooleanSupplier getter, Consumer<Boolean> setter,
        BooleanSupplier allowed, String hint) {
        BooleanSyncValue value = syncToggle(key, getter, setter, allowed);
        return new SlideSwitch().value(value::getBoolValue)
            .availableWhen(allowed)
            .label(IKey.dynamic(() -> tr(lang)))
            .tooltipBuilder(tooltip -> stateTooltip(tooltip, hint, allowed, value::getBoolValue))
            .onMousePressed(mouse -> {
                if (mouse != 0 || !allowed.getAsBoolean()) return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            });
    }

    private static class ModuleSlot extends ModularSlot {

        private final ModuleSlots<?> modules;
        private final int slot;

        private ModuleSlot(ModuleSlots<?> modules, int slot) {
            super(modules, slot);
            this.modules = modules;
            this.slot = slot;
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return modules.allowance(slot, stack);
        }
    }

    protected class ModuleSlots<T extends IEquipmentVariant> extends ItemStackHandler {

        private final EquipmentModules.Storage storage;
        private final Function<ItemStack, T> types;
        private NBTTagList synced, seen;
        protected int version;

        protected ModuleSlots(int size, EquipmentModules.Storage storage, Function<ItemStack, T> types) {
            super(size);
            this.storage = storage;
            this.types = types;
        }

        protected ItemStack[] load(ItemStack host) {
            return storage.moduleStacks(host);
        }

        protected void save(ItemStack host, ItemStack[] modules) {
            storage.writeModuleStacks(host, modules);
        }

        protected T type(ItemStack candidate) {
            return types.apply(candidate);
        }

        protected void reload() {
            ItemStack host = bound();
            NBTTagList current = storage.moduleList(host);
            if (current == seen) return;
            seen = current;
            if (current.equals(synced)) return;
            synced = (NBTTagList) current.copy();
            ItemStack[] loaded = load(host);
            for (int i = 0; i < Math.min(loaded.length, getSlots()); i++) stacks.set(i, loaded[i]);
            version++;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            reload();
            return super.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack candidate) {
            reload();
            super.setStackInSlot(slot, candidate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            reload();
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack candidate) {
            return allowance(slot, candidate);
        }

        protected int allowance(int slot, ItemStack candidate) {
            if (candidate == null) return 0;
            boolean energy = EnergyModuleItem.tier(candidate) > 0;
            T type = energy ? null : type(candidate);
            if (!energy && type == null) return 0;
            int used = 0;
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                ItemStack other = getStackInSlot(i);
                if (other != null && (energy ? EnergyModuleItem.tier(other) > 0 : type.equals(type(other))))
                    used += other.stackSize;
            }
            return Math.max(0, Math.min(candidate.getMaxStackSize(), (energy ? 1 : type.limit()) - used));
        }

        @Override
        public boolean isItemValid(int slot, ItemStack candidate) {
            return allowance(slot, candidate) > 0;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack candidate, boolean simulate) {
            if (candidate != null && !isItemValid(slot, candidate)) return candidate;
            reload();
            return super.insertItem(slot, candidate, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            ItemStack host = bound();
            save(host, stacks.toArray(new ItemStack[0]));
            seen = storage.moduleList(host);
            synced = (NBTTagList) seen.copy();
            version++;
            changed();
        }
    }
}
