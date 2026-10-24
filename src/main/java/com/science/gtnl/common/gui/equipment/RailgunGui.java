package com.science.gtnl.common.gui.equipment;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.widget.SlideSwitch;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunSetting;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RailgunGui extends EquipmentGui {

    public RailgunGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        super(data, sync);
    }

    @Override
    public boolean holds(ItemStack stack) {
        return ElectromagneticRailgun.is(stack);
    }

    @Override
    public ModularPanel build() {
        List<SlideSwitch> switches = new ObjectArrayList<>();
        List<String> labels = new ObjectArrayList<>();
        for (RailgunSetting setting : RailgunSetting.values()) {
            switches.add(setting(setting));
            labels.add(tr(lang(setting)));
        }
        Layout.Grid grid = rows().adaptiveGrid(style.spacingSmall, widestSwitch(labels), MAX_COLUMNS, switches);
        ModularPanel panel = panel("electromagnetic_railgun", () -> headerBottom() + grid.contentHeight());
        title(panel, "item.gtnl.electromagnetic_railgun.name", 0);
        moduleSlots(
            panel,
            new ModuleSlots<>(RailgunData.MODULE_SLOTS, RailgunData.MODULE_STORAGE, RailgunModule::of),
            "railgun_modules",
            "item.gtnl.railgun.module_limits");
        energyBar(panel, () -> EquipmentEnergy.read(bound()), () -> RailgunEnergy.capacity(bound()));
        panel.child(
            grid.left(0)
                .top(this::headerBottom, Unit.Measure.PIXEL));
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
