package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.DebugResearchStation;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;
import gregtech.common.gui.modularui.util.MachineModularSlot;

public class DebugResearchStationGui extends MTEBasicMachineBaseGui<DebugResearchStation> {

    public DebugResearchStationGui(DebugResearchStation machine, BasicUIProperties properties) {
        super(machine, properties);
        useGregTechLogo(true);
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18, 18);
    }

    @Override
    protected Flow createItemRecipeArea(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildren()
            .horizontalCenter()
            .verticalCenter()
            .child(
                Flow.row()
                    .coverChildren()
                    .child(createDataStickSlot())
                    .child(createResearchInputSlot()))
            .child(
                createProgressBar(panel, syncManager).marginLeft(8)
                    .marginRight(8))
            .child(createResearchOutputSlot());
    }

    @Override
    protected boolean doesAddSpecialSlot() {
        return false;
    }

    @Override
    protected boolean doesAddCircuitSlot() {
        return false;
    }

    @Override
    protected Flow createBottomRightCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomRightCornerFlow(panel, syncManager).child(
            new PersistentFilterSlot().slot(new ModularSlot(machine.getResearchOutputFilterInventory(), 0))
                .marginLeft(5));
    }

    private ItemSlot createDataStickSlot() {
        return new ItemSlot()
            .slot(new MachineModularSlot(machine.inventoryHandler, machine.getSpecialSlotIndex(), baseMetaTileEntity))
            .backgroundOverlay(GTGuiTextures.OVERLAY_SLOT_DATA_STICK);
    }

    private ItemSlot createResearchInputSlot() {
        return new ItemSlot()
            .slot(new MachineModularSlot(machine.inventoryHandler, machine.getInputSlot(), baseMetaTileEntity))
            .backgroundOverlay(slotOverlayFunction.apply(0, false, false, false));
    }

    private ItemSlot createResearchOutputSlot() {
        return new ItemSlot()
            .slot(
                new MachineModularSlot(machine.inventoryHandler, machine.getOutputSlot(), baseMetaTileEntity)
                    .accessibility(false, true))
            .backgroundOverlay(slotOverlayFunction.apply(0, false, true, false));
    }
}
