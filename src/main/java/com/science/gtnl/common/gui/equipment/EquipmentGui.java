package com.science.gtnl.common.gui.equipment;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public abstract class EquipmentGui {

    protected static final int WIDTH = 176, BUTTON_HEIGHT = 14;
    private static final int STORED_EU_TEXTURE_WIDTH = 147;

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

    protected static ModularPanel panel(String name, int height) {
        return GTGuis.createPopUpPanel(name)
            .size(WIDTH, height);
    }

    protected static void title(ModularPanel panel, String lang, int width) {
        panel.child(
            IKey.lang(lang)
                .asWidget()
                .pos(8, 6)
                .size(width, 10));
    }

    protected void moduleSlots(ModularPanel panel, ModuleSlots handler, String group, int y, String limits) {
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
            new ProgressWidget().value(
                new DoubleValue.Dynamic(() -> stored.getLongValue() / (double) Math.max(1, max.getLongValue()), null))
                .texture(GTGuiTextures.PROGRESSBAR_STORED_EU, STORED_EU_TEXTURE_WIDTH)
                .pos(8, y + 16)
                .size(160, 5));
    }

    protected ButtonWidget<?> addToggle(ParentWidget<?> parent, String key, String lang, int x, int y, int width,
        BooleanSupplier getter, Consumer<Boolean> setter, BooleanSupplier allowed, String hint) {
        BooleanSyncValue value = new BooleanSyncValue(
            getter,
            enabled -> { if (allowed.getAsBoolean()) setter.accept(enabled); }).allowC2S();
        sync.syncValue(key, value);
        ButtonWidget<?> button = new ButtonWidget<>().pos(x, y)
            .size(width, BUTTON_HEIGHT)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                IKey.dynamic(() -> (allowed.getAsBoolean() ? value.getBoolValue() ? "§a" : "§7" : "§8") + tr(lang)))
            .tooltipBuilder(tooltip -> {
                if (hint != null) tooltip.addLine(IKey.lang(hint));
                tooltip
                    .addLine(
                        IKey.dynamic(
                            () -> tr(
                                allowed.getAsBoolean()
                                    ? value.getBoolValue() ? "item.gtnl.equipment.gui.enabled"
                                        : "item.gtnl.equipment.gui.disabled"
                                    : "item.gtnl.equipment.gui.unavailable")));
            })
            .onMousePressed(mouse -> {
                if (mouse != 0 || !allowed.getAsBoolean()) return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            });
        parent.child(button);
        return button;
    }

    protected abstract class ModuleSlots extends ItemStackHandler {

        private NBTTagList synced, seen;
        protected int version;

        protected ModuleSlots(int size) {
            super(size);
        }

        /**
         * The NBT list the modules of the host are stored in, used to notice outside changes.
         */
        protected abstract NBTTagList tag(ItemStack host);

        /**
         * Reads the module stacks of the host, one per slot.
         */
        protected abstract ItemStack[] load(ItemStack host);

        /**
         * Writes the module stacks back to the host.
         */
        protected abstract void save(ItemStack host, ItemStack[] modules);

        /**
         * The module type of an item for this host, or null when it can not be installed.
         */
        protected abstract Object type(ItemStack candidate);

        /**
         * How many modules of one type may be installed together.
         */
        protected int limit(Object type) {
            return 1;
        }

        protected void reload() {
            ItemStack host = bound();
            NBTTagList current = tag(host);
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
            Object type = energy ? null : type(candidate);
            if (!energy && type == null) return false;
            int count = 0;
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                ItemStack other = getStackInSlot(i);
                if (energy ? EnergyModuleItem.tier(other) > 0 : type.equals(type(other))) count++;
            }
            return count < (energy ? 1 : limit(type));
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
            seen = tag(host);
            synced = (NBTTagList) seen.copy();
            version++;
            changed();
        }
    }
}
