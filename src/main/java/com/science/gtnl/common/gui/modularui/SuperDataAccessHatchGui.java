package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperDataAccessHatch;

import gregtech.api.enums.ItemList;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class SuperDataAccessHatchGui {

    private final SuperDataAccessHatch hatch;

    public SuperDataAccessHatchGui(SuperDataAccessHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(hatch.getGUIWidth())
            .setHeight(hatch.getGUIHeight())
            .doesAddGregTechLogo(false)
            .doesBindPlayerInventory(false)
            .build()
            .child(createDataStickGrid(syncManager))
            .child(createLogo());
    }

    private IWidget createDataStickGrid(PanelSyncManager syncManager) {
        return new ItemSlotGridBuilder(hatch.inventoryHandler, syncManager).size(9, 9)
            .slotGroupKey("data_inv")
            .filter(itemStack -> ItemList.Tool_DataStick.isStackEqual(itemStack, false, true))
            .itemSlotSupplier(
                () -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_STANDARD)
                    .backgroundOverlay(GTGuiTextures.OVERLAY_SLOT_CIRCUIT))
            .build()
            .pos(43, 18);
    }

    private IWidget createLogo() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
            .size(18)
            .pos(210, 162);
    }
}
