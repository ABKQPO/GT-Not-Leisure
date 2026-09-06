package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Alignment.MainAxis;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.multiblock.wireless.TreeDiagram;

import gregtech.api.modularui2.GTGuiTextures;

public class TreeDiagramGui extends GTNLMultiBlockBaseGui<TreeDiagram> {

    private static final String POWER_PANEL_KEY = "treeDiagramPowerPanel";
    private static final String NANITE_SLOT_GROUP = "treeDiagramNanites";
    private static final int POWER_PANEL_WIDTH = 120;
    private static final int POWER_PANEL_HEIGHT = 172;
    private static final int NANITE_SHIFT_CLICK_PRIORITY = SlotGroup.STORAGE_SLOT_PRIO - 1;

    public TreeDiagramGui(TreeDiagram multiblock) {
        super(multiblock);
    }

    @Override
    public ButtonWidget<?> createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        IPanelHandler powerPanel = syncManager.syncedPanel(
            POWER_PANEL_KEY,
            true,
            (panelSyncManager, panelHandler) -> openPowerControlPanel(parent, syncManager));
        return new ButtonWidget<>().marginLeft(4)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_POWER_PANEL)
            .onMousePressed(mouseButton -> {
                if (powerPanel.isPanelOpen()) {
                    powerPanel.closePanel();
                } else {
                    powerPanel.openPanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("GT5U.gui.button.power_panel")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel openPowerControlPanel(ModularPanel parent, PanelSyncManager syncManager) {
        return new ModularPanel(POWER_PANEL_KEY).relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(POWER_PANEL_WIDTH, POWER_PANEL_HEIGHT)
            .child(
                Flow.column()
                    .full()
                    .padding(3)
                    .child(makeTitleTextWidget())
                    .child(
                        IKey.lang("GTPP.CC.parallel")
                            .asWidget()
                            .marginBottom(4))
                    .child(makeParallelConfigurator())
                    .child(makePowerfailEventsToggleRow())
                    .child(createNaniteSlotGrid(syncManager).marginTop(10)));
    }

    @Override
    public IWidget makeTitleTextWidget() {
        return new TextWidget<>(
            EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("GT5U.gui.text.power_panel"))
                .textAlign(Alignment.Center)
                .size(POWER_PANEL_WIDTH, 18)
                .marginBottom(4);
    }

    private IWidget makeParallelConfigurator() {
        IntSyncValue maxParallelSyncer = new IntSyncValue(
            multiblock::getMaxParallelRecipes,
            multiblock::setMaxParallelForPanel);
        BooleanSyncValue alwaysMaxParallelSyncer = new BooleanSyncValue(
            multiblock::isAlwaysMaxParallel,
            multiblock::setAlwaysMaxParallel).allowC2S();
        IntSyncValue powerPanelMaxParallelSyncer = new IntSyncValue(
            multiblock::getPowerPanelMaxParallel,
            multiblock::setPowerPanelMaxParallel).allowC2S();
        return Flow.row()
            .fullWidth()
            .marginBottom(4)
            .height(18)
            .paddingLeft(3)
            .paddingRight(3)
            .mainAxisAlignment(MainAxis.CENTER)
            .child(
                new TextFieldWidget().value(powerPanelMaxParallelSyncer)
                    .setTextAlignment(Alignment.Center)
                    .numbersInt(
                        () -> alwaysMaxParallelSyncer.getValue() ? maxParallelSyncer.getValue() : 1,
                        maxParallelSyncer::getValue)
                    .tooltipBuilder(
                        tooltip -> tooltip.addLine(
                            IKey.dynamic(
                                () -> alwaysMaxParallelSyncer.getValue()
                                    ? StatCollector.translateToLocalFormatted(
                                        "GT5U.gui.text.lockedvalue",
                                        maxParallelSyncer.getValue())
                                    : StatCollector.translateToLocalFormatted(
                                        "GT5U.gui.text.rangedvalue",
                                        1,
                                        maxParallelSyncer.getValue()))))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .size(70, 14)
                    .marginBottom(4)
                    .marginRight(16))
            .child(
                new ButtonWidget<>()
                    .overlay(
                        new DynamicDrawable(
                            () -> alwaysMaxParallelSyncer.getValue() ? GTGuiTextures.OVERLAY_BUTTON_CHECKMARK
                                : GTGuiTextures.OVERLAY_BUTTON_CROSS))
                    .onMousePressed(mouseButton -> {
                        alwaysMaxParallelSyncer.setValue(!alwaysMaxParallelSyncer.getValue());
                        powerPanelMaxParallelSyncer.setValue(maxParallelSyncer.getValue());
                        return true;
                    })
                    .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("GT5U.gui.button.max_parallel")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY));
    }

    private IWidget makePowerfailEventsToggleRow() {
        BooleanSyncValue powerfailSyncer = new BooleanSyncValue(
            multiblock::makesPowerfailEvents,
            multiblock::setPowerfailEventCreationStatus).allowC2S();
        return Flow.row()
            .fullWidth()
            .height(18)
            .paddingLeft(3)
            .paddingRight(3)
            .mainAxisAlignment(MainAxis.CENTER)
            .child(
                new TextWidget<>(IKey.lang("GT5U.gui.text.powerfail_events")).height(18)
                    .marginRight(2))
            .child(
                new ToggleButton().value(powerfailSyncer)
                    .overlay(true, GTGuiTextures.OVERLAY_BUTTON_CHECKMARK)
                    .overlay(false, GTGuiTextures.OVERLAY_BUTTON_CROSS));
    }

    private Grid createNaniteSlotGrid(PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(NANITE_SLOT_GROUP, 2, NANITE_SHIFT_CLICK_PRIORITY);
        GTNLMui2ItemHandlerAdapter handler = new GTNLMui2ItemHandlerAdapter(multiblock.getNaniteSlotHandler());
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                2,
                2,
                (x, y, index) -> new ItemSlot()
                    .slot(
                        new ModularSlot(handler, index).filter(multiblock::isValidNaniteStack)
                            .slotGroup(NANITE_SLOT_GROUP))
                    .background(GTGuiTextures.SLOT_ITEM_STANDARD));
    }
}
