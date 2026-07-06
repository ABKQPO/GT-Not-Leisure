package com.science.gtnl.common.gui.modularui;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.science.gtnl.common.machine.multiblock.ResearchCenter;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

public class ResearchCenterGui extends MTEMultiBlockBaseGui<ResearchCenter> {

    public ResearchCenterGui(ResearchCenter multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildrenWidth()
            .heightRel(1)
            .child(createFilterSlots())
            .childIf(!machineModeIcons.isEmpty(), () -> createModeSwitchButton(syncManager));
    }

    private IWidget createFilterSlots() {
        Flow row = Flow.row()
            .coverChildren()
            .childPadding(0);
        for (int i = 0; i < 4; i++) {
            row.child(
                new PhantomItemSlot().slot(new ModularSlot(multiblock.gtnl$getResearchMarkerInventoryHandler(), i))
                    .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                    .backgroundOverlay(GTGuiTextures.OVERLAY_SLOT_FILTER)
                    .size(18, 18));
        }
        return row;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue outputsSyncer = new StringSyncValue(multiblock::getResearchOutputsForGui);
        LongSyncValue computationReqSyncer = new LongSyncValue(
            () -> multiblock.computationRequired,
            val -> multiblock.computationRequired = val);
        LongSyncValue computationRemSyncer = new LongSyncValue(
            () -> multiblock.computationRemaining,
            val -> multiblock.computationRemaining = val);
        IntSyncValue ticksUntilPacketLossFailSyncer = new IntSyncValue(
            () -> multiblock.ticksUntilPacketLossFail,
            val -> multiblock.ticksUntilPacketLossFail = val);

        syncManager.syncValue("researchCenterOutputs", outputsSyncer);
        syncManager.syncValue("computationRequired", computationReqSyncer);
        syncManager.syncValue("computationRemaining", computationRemSyncer);
        syncManager.syncValue("ticksUntilPacketLossFail", ticksUntilPacketLossFailSyncer);

        ListWidget<IWidget, ?> terminal = super.createTerminalTextWidget(syncManager, parent);
        terminal
            .child(
                IKey.dynamic(
                    () -> StatCollector
                        .translateToLocalFormatted("GT5U.gui.text.researching_item", outputsSyncer.getValue()))
                    .asWidget()
                    .setEnabledIf(
                        ignored -> !outputsSyncer.getValue()
                            .isEmpty()))
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "GT5U.gui.text.research_progress",
                        multiblock.getComputationConsumed(),
                        multiblock.getComputationRequired(),
                        formatNumber(getComputationProgress())))
                    .asWidget()
                    .setEnabledIf(
                        ignored -> multiblock.computationRequired > 0 && !outputsSyncer.getValue()
                            .isEmpty()))
            .child(IKey.dynamic(() -> {
                if (multiblock.ticksUntilPacketLossFail >= MTEResearchStation.PACKET_LOSS_DECAY_WINDOW) {
                    return EnumChatFormatting.YELLOW
                        + translateToLocalFormatted("tt.infodata.multi.connection_health.waiting")
                        + EnumChatFormatting.RESET;
                }
                return EnumChatFormatting.RED
                    + translateToLocalFormatted("tt.infodata.multi.connection_health.decoherence")
                    + EnumChatFormatting.RESET;
            })
                .asWidget()
                .setEnabledIf(
                    ignored -> multiblock.computationRequired > 0 && !outputsSyncer.getValue()
                        .isEmpty()
                        && multiblock.ticksUntilPacketLossFail < MTEResearchStation.PACKET_LOSS_FULL_WINDOW));
        return terminal;
    }

    private double getComputationProgress() {
        return 100d * (multiblock.getComputationRequired() > 0d
            ? (double) multiblock.getComputationConsumed() / multiblock.getComputationRequired()
            : 0d);
    }
}
