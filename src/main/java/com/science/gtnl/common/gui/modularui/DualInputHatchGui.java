package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.DualInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class DualInputHatchGui extends MTEHatchBaseGui<DualInputHatch> {

    public DualInputHatchGui(DualInputHatch hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(machine.getGUIWidth())
            .setHeight(machine.getGUIHeight())
            .doesAddGregTechLogo(false)
            .build();

        SlotLayout layout = SlotLayout.of(machine);
        addItemSlots(panel, layout);
        addFluidSlots(panel, layout);
        panel.child(createLogo());
        return panel;
    }

    public void addItemSlots(ModularPanel panel, SlotLayout layout) {
        for (int row = 0; row < layout.itemRows; row++) {
            for (int col = 0; col < layout.itemColumns; col++) {
                int slotIndex = row * layout.itemColumns + col;
                if (slotIndex < machine.itemSlotAmount - 1) {
                    panel.child(
                        new ItemSlot().slot(new ModularSlot(machine.inventoryHandler, slotIndex))
                            .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                            .pos(layout.centerX + col * 18 + 5, layout.centerY + row * 18));
                }
            }
        }
    }

    public void addFluidSlots(ModularPanel panel, SlotLayout layout) {
        for (int i = 0; i < machine.getFluidTanksForGui().length; i++) {
            panel.child(
                new FluidSlot().syncHandler(new FluidSlotSyncHandler(machine.getFluidTanksForGui()[i]))
                    .background(GTGuiTextures.SLOT_FLUID_TANK)
                    .pos(layout.centerX + 18 * layout.itemColumns + 5, layout.centerY + i * 18));
        }
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    public record SlotLayout(int itemColumns, int itemRows, int centerX, int centerY) {

        public static SlotLayout of(DualInputHatch hatch) {
            int itemColumns = Math.max(1, hatch.mTier);
            int itemRows = Math.max(1, hatch.mTier);
            int totalWidth = 9 * itemColumns + 36;
            int totalHeight = 5 * itemRows + 81;
            return new SlotLayout(itemColumns, itemRows, (176 - totalWidth) / 2, (166 - totalHeight) / 2);
        }
    }
}
