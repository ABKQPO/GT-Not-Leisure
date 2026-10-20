package com.science.gtnl.common.gui.equipment;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunSetting;

public class RailgunGui extends EquipmentGui {

    private static final int SETTINGS_Y = 66, SETTING_WIDTH = 79, SETTING_STRIDE = 81, SETTING_ROW = 16;

    public RailgunGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        super(data, sync);
    }

    @Override
    public boolean holds(ItemStack stack) {
        return ElectromagneticRailgun.is(stack);
    }

    @Override
    public ModularPanel build() {
        RailgunSetting[] settings = RailgunSetting.values();
        ModularPanel panel = panel("electromagnetic_railgun", SETTINGS_Y + (settings.length + 1) / 2 * SETTING_ROW);
        title(panel, "item.gtnl.electromagnetic_railgun.name", 160);
        moduleSlots(
            panel,
            new ModuleSlots<>(RailgunData.MODULE_SLOTS, RailgunData.MODULE_STORAGE, RailgunModule::of),
            "railgun_modules",
            18,
            "item.gtnl.railgun.module_limits");
        energyBar(panel, () -> EquipmentEnergy.read(bound()), () -> RailgunEnergy.capacity(bound()), 42, 160);
        for (int i = 0; i < settings.length; i++) setting(panel, settings[i], i);
        bindFooter(panel);
        return panel;
    }

    private void setting(ModularPanel panel, RailgunSetting setting, int index) {
        BooleanSyncValue available = new BooleanSyncValue(() -> setting.available(bound()));
        sync.syncValue(setting.key + "Available", available);
        addToggle(
            panel,
            setting.key,
            "item.gtnl.railgun.setting." + setting.key,
            8 + index % 2 * SETTING_STRIDE,
            SETTINGS_Y + index / 2 * SETTING_ROW,
            SETTING_WIDTH,
            () -> setting.on(bound()),
            enabled -> {
                setting.set(bound(), enabled);
                changed();
            },
            available::getBoolValue,
            null);
    }
}
