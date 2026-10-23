package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.machine.multiblock.wireless.HighwayToHell;

import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.modularui2.GTGuis;

public class HighwayToHellGui extends GTNLMultiBlockBaseGui<HighwayToHell> {

    public static final String TURBINE_SLOT_GROUP = "highway_to_hell_turbines";
    public static final int TURBINE_SHIFT_CLICK_PRIORITY = 50;

    public HighwayToHellGui(HighwayToHell multiblock) {
        super(multiblock);
    }

    @Override
    public Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createTurbineHolderButton(syncManager, parent));
    }

    public IWidget createTurbineHolderButton(PanelSyncManager syncManager, ModularPanel parent) {
        IPanelHandler turbinePanel = syncManager.syncedPanel(
            "highwayToHellTurbinePanel",
            true,
            (panelSyncManager, syncHandler) -> openTurbineHolderPanel(parent, panelSyncManager));

        return new ButtonWidget<>().size(18, 18)
            .overlay(GuiTextures.GEAR)
            .onMousePressed(mouseButton -> {
                if (turbinePanel.isPanelOpen()) {
                    turbinePanel.closePanel();
                } else {
                    turbinePanel.openPanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("GT5U.gui.button.turbinemenu")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    public ModularPanel openTurbineHolderPanel(ModularPanel parent, PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(TURBINE_SLOT_GROUP, 4, TURBINE_SHIFT_CLICK_PRIORITY);

        return GTGuis.createPopUpPanel("highwayToHellTurbinePanel")
            .relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(118, 86)
            .child(
                Flow.column()
                    .full()
                    .padding(5)
                    .childPadding(4)
                    .child(
                        new TextWidget<>(IKey.lang("GT5U.gui.text.turbine_title")).size(96, 18)
                            .alignment(Alignment.Center))
                    .child(
                        SlotGroupWidget.builder()
                            .matrix("II", "II")
                            .key('I', this::createTurbineSlot)
                            .build()));
    }

    public IWidget createTurbineSlot(int index) {
        return new ItemSlot().slot(
            new ModularSlot(multiblock.turbineHolder, index).slotGroup(TURBINE_SLOT_GROUP)
                .filter(HighwayToHellGui::isValidTurbine));
    }

    public static boolean isValidTurbine(ItemStack aTurbine) {
        return (aTurbine != null && aTurbine.getItem() instanceof MetaGeneratedTool
            && aTurbine.getItemDamage() >= 170
            && aTurbine.getItemDamage() <= 176);
    }
}
