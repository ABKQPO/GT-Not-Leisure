package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.MeteorMiner;

import gregtech.api.modularui2.GTGuiTextures;

public class MeteorMinerGui extends GTNLMultiBlockBaseGui<MeteorMiner> {

    public MeteorMinerGui(MeteorMiner multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createResetButton());
    }

    private IWidget createResetButton() {
        return new ButtonWidget<>().size(16, 16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_CYCLIC)
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (multiblock.getBaseMetaTileEntity() == null || !multiblock.getBaseMetaTileEntity()
                    .isServerSide()) {
                    return;
                }
                multiblock.startReset();
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("Tooltip_MeteorMiner_06")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }
}
