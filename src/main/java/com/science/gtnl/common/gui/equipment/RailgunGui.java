package com.science.gtnl.common.gui.equipment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.gui.modularui.layout.Layout;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunSetting;

public class RailgunGui extends EquipmentGui {

    private static final int SETTINGS_Y = 66, SLOTS_Y = 18, ENERGY_Y = 42;

    public RailgunGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        super(data, sync);
    }

    @Override
    public boolean holds(ItemStack stack) {
        return ElectromagneticRailgun.is(stack);
    }

    @Override
    public ModularPanel build() {
        List<SlideSwitch> switches = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (RailgunSetting setting : RailgunSetting.values()) {
            switches.add(setting(setting));
            labels.add(tr(lang(setting)));
        }
        Layout rows = rows();
        List<IWidget> grid = rows
            .grid(style.spacingSmall, rows.columns(style.spacingSmall, widestSwitch(labels), MAX_COLUMNS), switches);
        ModularPanel panel = panel("electromagnetic_railgun", SETTINGS_Y + grid.size() * style.rowHeight);
        title(panel, "item.gtnl.electromagnetic_railgun.name", contentWidth());
        moduleSlots(
            panel,
            new ModuleSlots<>(RailgunData.MODULE_SLOTS, RailgunData.MODULE_STORAGE, RailgunModule::of),
            "railgun_modules",
            SLOTS_Y,
            "item.gtnl.railgun.module_limits");
        energyBar(
            panel,
            () -> EquipmentEnergy.read(bound()),
            () -> RailgunEnergy.capacity(bound()),
            ENERGY_Y,
            contentWidth());
        panel.child(
            Flow.column()
                .pos(0, SETTINGS_Y)
                .size(WIDTH, grid.size() * style.rowHeight)
                .children(grid));
        bindFooter(panel);
        return panel;
    }

    private static String lang(RailgunSetting setting) {
        return "item.gtnl.railgun.setting." + setting.key;
    }

    private SlideSwitch setting(RailgunSetting setting) {
        BooleanSyncValue available = new BooleanSyncValue(() -> setting.available(bound()));
        sync.syncValue(setting.key + "Available", available);
        return toggle(setting.key, lang(setting), () -> setting.on(bound()), enabled -> {
            setting.set(bound(), enabled);
            changed();
        }, available::getBoolValue, null);
    }
}
