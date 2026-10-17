package com.science.gtnl.common.gui;

import java.util.function.BooleanSupplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public final class RailgunGui {

    private static final int STORED_EU_TEXTURE_WIDTH = 147;
    private static final int STORED_EU_BAR_HEIGHT = 5;

    private final ItemStack stack;
    private final PanelSyncManager sync;
    private final PlayerInventoryGuiData data;

    public RailgunGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        this.data = data;
        this.stack = data.getUsedItemStack();
        this.sync = sync;
        if (stack == null || !(stack.getItem() instanceof ElectromagneticRailgun))
            throw new IllegalStateException("Missing railgun");
    }

    public ModularPanel build() {
        ModularPanel panel = GTGuis.createPopUpPanel("electromagnetic_railgun")
            .size(176, 222);
        sync.registerSlotGroup(new SlotGroup("railgun_modules", 8, 0, true));
        var modules = new ModuleHandler();
        panel.child(
            IKey.lang("item.gtnl.electromagnetic_railgun.name")
                .asWidget()
                .pos(8, 6)
                .size(160, 14));
        for (int i = 0; i < RailgunData.MODULE_SLOTS; i++) {
            final int slot = i;
            panel.child(
                new ItemSlot().slot(
                    new ModularSlot(modules, i).filter(item -> modules.isItemValid(slot, item))
                        .slotGroup("railgun_modules"))
                    .pos(16 + i * 18, 24)
                    .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("gtnl.railgun.module_limits"))));
        }
        LongSyncValue energy = new LongSyncValue(() -> (long) RailgunEnergy.read(stack));
        LongSyncValue capacity = new LongSyncValue(() -> (long) RailgunEnergy.capacity(stack));
        BooleanSyncValue terrainAllowed = new BooleanSyncValue(() -> MainConfig.item.railgun.terrainEnabled);
        BooleanSyncValue ehvInstalled = new BooleanSyncValue(
            () -> RailgunData.modules(stack)
                .count(RailgunModuleType.EHV_BEAM) > 0);
        sync.syncValue("energy", energy);
        sync.syncValue("capacity", capacity);
        sync.syncValue("terrainAllowed", terrainAllowed);
        sync.syncValue("ehvInstalled", ehvInstalled);
        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "gtnl.railgun.energy",
                    (double) energy.getLongValue(),
                    (double) capacity.getLongValue()))
                .asWidget()
                .pos(8, 46)
                .size(160, 12));
        panel.child(
            new ProgressWidget().progress(() -> energy.getLongValue() / (double) Math.max(1, capacity.getLongValue()))
                .texture(GTGuiTextures.PROGRESSBAR_STORED_EU, STORED_EU_TEXTURE_WIDTH)
                .pos(8, 60)
                .size(160, STORED_EU_BAR_HEIGHT));
        addToggle(panel, "terrain", 8, 72, terrainAllowed::getBoolValue);
        addToggle(panel, "pvp", 91, 72, () -> true);
        addToggle(panel, "sound", 8, 92, () -> true);
        addToggle(panel, "splash", 91, 92, () -> true);
        addToggle(panel, "chain", 8, 112, () -> true);
        addToggle(panel, "ehvBeam", 91, 112, ehvInstalled::getBoolValue)
            .setEnabledIf(widget -> ehvInstalled.getBoolValue());
        panel.bindPlayerInventory();
        return panel;
    }

    private ButtonWidget<?> addToggle(ModularPanel panel, String key, int x, int y, BooleanSupplier allowed) {
        BooleanSyncValue value = new BooleanSyncValue(
            () -> RailgunData.settings(stack)
                .write()
                .getBoolean(key),
            enabled -> {
                if (allowed.getAsBoolean()) {
                    RailgunData.setSetting(stack, key, enabled);
                    data.getPlayer().inventory.markDirty();
                }
            }).allowC2S();
        sync.syncValue(key, value);
        ButtonWidget<?> button = new ButtonWidget<>().pos(x, y)
            .size(77, 18)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                IKey.dynamic(
                    () -> (allowed.getAsBoolean() ? value.getBoolValue() ? "§a" : "§7" : "§8")
                        + StatCollector.translateToLocal("gtnl.railgun.setting." + key)))
            .tooltipBuilder(
                tooltip -> tooltip.addLine(
                    IKey.dynamic(
                        () -> StatCollector.translateToLocal(
                            allowed.getAsBoolean()
                                ? value.getBoolValue() ? "gtnl.railgun.enabled" : "gtnl.railgun.disabled"
                                : "gtnl.railgun.unavailable"))))
            .onMousePressed(mouse -> {
                if (mouse != 0 || !allowed.getAsBoolean()) return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            });
        panel.child(button);
        return button;
    }

    private final class ModuleHandler extends ItemStackHandler {

        ModuleHandler() {
            super(RailgunData.moduleStacks(stack));
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack item) {
            RailgunModuleType type = RailgunModuleType.of(item);
            if (type == null) return false;
            int count = 0;
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                RailgunModuleType other = RailgunModuleType.of(getStackInSlot(i));
                if (other == type || type.energy() && other != null && other.energy()) count++;
            }
            return count < type.limit;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack item, boolean simulate) {
            return item != null && !isItemValid(slot, item) ? item : super.insertItem(slot, item, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            RailgunData.setModuleStacks(stack, stacks.toArray(new ItemStack[0]));
            data.getPlayer().inventory.markDirty();
        }
    }
}
