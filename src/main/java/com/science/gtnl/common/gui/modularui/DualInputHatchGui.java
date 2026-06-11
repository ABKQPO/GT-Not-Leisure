package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.DualInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class DualInputHatchGui {

    private final DualInputHatch hatch;

    public DualInputHatchGui(DualInputHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(hatch.getGUIWidth())
            .setHeight(hatch.getGUIHeight())
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build();

        SlotLayout layout = SlotLayout.of(hatch);
        addItemSlots(panel, layout);
        addFluidSlots(panel, layout);
        panel.child(createLogo());
        return panel;
    }

    private void addItemSlots(ModularPanel panel, SlotLayout layout) {
        for (int row = 0; row < layout.itemRows; row++) {
            for (int col = 0; col < layout.itemColumns; col++) {
                int slotIndex = row * layout.itemColumns + col;
                if (slotIndex < hatch.itemSlotAmount - 1) {
                    panel.child(
                        new ItemSlot().slot(new ModularSlot(hatch.inventoryHandler, slotIndex))
                            .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                            .pos(layout.centerX + col * 18 + 5, layout.centerY + row * 18));
                }
            }
        }
    }

    private void addFluidSlots(ModularPanel panel, SlotLayout layout) {
        for (int i = 0; i < hatch.getFluidTanksForGui().length; i++) {
            panel.child(
                new FluidSlot().syncHandler(new FluidSlotSyncHandler(hatch.getFluidTanksForGui()[i]))
                    .background(GTGuiTextures.SLOT_FLUID_TANK)
                    .pos(layout.centerX + 18 * layout.itemColumns + 5, layout.centerY + i * 18));
        }
    }

    private Widget<?> createLogo() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
            .size(18)
            .pos(169 + 4 * (hatch.mTier - 1) + hatch.mTier / 2, 102 + 14 * (hatch.mTier - 1));
    }

    private record SlotLayout(int itemColumns, int itemRows, int centerX, int centerY) {

        private static SlotLayout of(DualInputHatch hatch) {
            int itemColumns = Math.max(1, hatch.mTier);
            int itemRows = Math.max(1, hatch.mTier);
            int totalWidth = 9 * itemColumns + 36;
            int totalHeight = 5 * itemRows + 81;
            return new SlotLayout(itemColumns, itemRows, (176 - totalWidth) / 2, (166 - totalHeight) / 2);
        }
    }
}
