package com.science.gtnl.common.gui.equipment;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.science.gtnl.client.hud.HudEditor;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.gui.modularui.widget.FillBar;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EquipmentGui {

    protected static final int WIDTH = 176, BUTTON_HEIGHT = 14;
    private static final int CLOSE_SIZE = 10, CLOSE_INSET = 4;
    private static final int INVENTORY_HEIGHT = 76, INVENTORY_BOTTOM = 7, FOOTER_GAP = 4, HUD_BUTTON_WIDTH = 56,
        HUD_BUTTON_SIDE = 8, HUD_BUTTON_BOTTOM = INVENTORY_BOTTOM + INVENTORY_HEIGHT + FOOTER_GAP,
        FOOTER_HEIGHT = HUD_BUTTON_BOTTOM + BUTTON_HEIGHT;

    protected final PlayerInventoryGuiData data;
    protected final PanelSyncManager sync;
    protected final ItemStack stack;
    protected final boolean client;

    protected EquipmentGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        this.data = data;
        this.sync = sync;
        this.stack = data.getUsedItemStack();
        if (!holds(stack)) throw new IllegalStateException("Missing equipment for " + getClass().getSimpleName());
        this.client = data.getPlayer().worldObj.isRemote;
    }

    /**
     * Whether the stack is the kind of equipment this GUI configures.
     */
    public abstract boolean holds(ItemStack stack);

    /**
     * Builds the whole panel, including the player inventory.
     */
    public abstract ModularPanel build();

    /**
     * Swaps the module slots to their network view while the container syncs; no-op unless a GUI hides data.
     */
    public void syncView(boolean bare) {}

    protected ItemStack bound() {
        ItemStack used = data.getUsedItemStack();
        return holds(used) ? used : stack;
    }

    protected void changed() {
        data.getPlayer().inventory.markDirty();
    }

    protected static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    protected static ModularPanel panel(String name, int contentHeight) {
        return new ModularPanel(name).size(WIDTH, contentHeight + FOOTER_HEIGHT)
            .child(
                closeButton().size(CLOSE_SIZE, CLOSE_SIZE)
                    .top(CLOSE_INSET)
                    .right(CLOSE_INSET));
    }

    protected static void bindFooter(ModularPanel panel) {
        panel.child(
            standardButton(HUD_BUTTON_WIDTH, BUTTON_HEIGHT).right(HUD_BUTTON_SIDE)
                .bottom(HUD_BUTTON_BOTTOM)
                .overlay(IKey.lang("gtnl.hud.editor.open"))
                .onMousePressed(mouse -> {
                    if (mouse != 0) return false;
                    openHudEditor();
                    return true;
                }));
        panel.bindPlayerInventory();
    }

    @SideOnly(Side.CLIENT)
    private static void openHudEditor() {
        Minecraft.getMinecraft()
            .displayGuiScreen(null);
        HudEditor.open();
    }

    public static ButtonWidget<?> closeButton() {
        ButtonWidget<?> button = new ButtonWidget<>();
        return button.widgetTheme(IThemeApi.CLOSE_BUTTON)
            .overlay(IKey.str("×"))
            .onMousePressed(mouse -> {
                if (mouse != 0) return false;
                button.getPanel()
                    .closeIfOpen();
                return true;
            });
    }

    protected static void title(ModularPanel panel, String lang, int width) {
        panel.child(
            IKey.lang(lang)
                .asWidget()
                .pos(8, 6)
                .size(width, 10));
    }

    protected void moduleSlots(ModularPanel panel, ModuleSlots<?> handler, String group, int y, String limits) {
        sync.registerSlotGroup(new SlotGroup(group, handler.getSlots(), 0, true));
        for (int i = 0; i < handler.getSlots(); i++) {
            final int slot = i;
            panel.child(
                new ItemSlot()
                    .slot(
                        new ModularSlot(handler, i).filter(candidate -> handler.isItemValid(slot, candidate))
                            .slotGroup(group))
                    .pos(16 + i * 18, y)
                    .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(limits))));
        }
    }

    protected void energyBar(ModularPanel panel, DoubleSupplier energy, DoubleSupplier capacity, int y,
        int labelWidth) {
        LongSyncValue stored = new LongSyncValue(() -> (long) energy.getAsDouble());
        LongSyncValue max = new LongSyncValue(() -> (long) capacity.getAsDouble());
        sync.syncValue("energy", stored);
        sync.syncValue("capacity", max);
        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "item.gtnl.equipment.gui.energy",
                    EquipmentEnergy.format(stored.getLongValue()),
                    EquipmentEnergy.format(max.getLongValue())))
                .asWidget()
                .pos(8, y)
                .size(labelWidth, 10));
        panel.child(
            new FillBar().value(() -> stored.getLongValue() / (double) Math.max(1, max.getLongValue()))
                .pos(8, y + 16)
                .size(160, 5));
    }

    protected static ButtonWidget<?> standardButton(int x, int y, int width, int height) {
        return standardButton(width, height).pos(x, y);
    }

    private static ButtonWidget<?> standardButton(int width, int height) {
        return new ButtonWidget<>().size(width, height);
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

    protected void addToggle(ParentWidget<?> parent, String key, String lang, int x, int y, int width,
        BooleanSupplier getter, Consumer<Boolean> setter, BooleanSupplier allowed, String hint) {
        BooleanSyncValue value = syncToggle(key, getter, setter, allowed);
        parent.child(
            new SlideSwitch().value(value::getBoolValue)
                .availableWhen(allowed)
                .label(IKey.dynamic(() -> tr(lang)))
                .pos(x, y)
                .size(width, BUTTON_HEIGHT)
                .tooltipBuilder(tooltip -> stateTooltip(tooltip, hint, allowed, value::getBoolValue))
                .onMousePressed(mouse -> {
                    if (mouse != 0 || !allowed.getAsBoolean()) return false;
                    value.setBoolValue(!value.getBoolValue());
                    return true;
                }));
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
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack candidate) {
            boolean energy = EnergyModuleItem.tier(candidate) > 0;
            T type = energy ? null : type(candidate);
            if (!energy && type == null) return false;
            int count = 0;
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                ItemStack other = getStackInSlot(i);
                if (energy ? EnergyModuleItem.tier(other) > 0 : type.equals(type(other))) count++;
            }
            return count < (energy ? 1 : type.limit());
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
