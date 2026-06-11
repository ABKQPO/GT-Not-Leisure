package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DrawableStack;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamElevator;

import gregtech.api.modularui2.GTGuiTextures;

public class SteamElevatorGui extends GTNLSteamMultiBlockBaseGui {

    private static final String MACHINE_SYNC_KEY = "steamElevatorMachine";
    private static final String ALLOWED_TO_WORK_SYNC_KEY = "steamElevatorAllowedToWork";
    private static final String MODULE_COUNT_SYNC_KEY = "steamElevatorModuleCount";
    private static final String TELEPORT_SYNC_KEY = "steamElevatorTeleport";

    private final SteamElevator steamElevator;

    public SteamElevatorGui(SteamElevator multiblock) {
        super(multiblock);
        this.steamElevator = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(MACHINE_SYNC_KEY, new BooleanSyncValue(steamElevator::isMachineForGui));
        syncManager.syncValue(ALLOWED_TO_WORK_SYNC_KEY, new BooleanSyncValue(steamElevator::isAllowedToWorkForGui));
        syncManager.syncValue(MODULE_COUNT_SYNC_KEY, new IntSyncValue(steamElevator::getNumberOfModulesForGui));
        syncManager.syncValue(TELEPORT_SYNC_KEY, new InteractionSyncHandler().setOnMousePressed(mouseData -> {
            if (!mouseData.isClient() && mouseData.mouseButton == 0) {
                steamElevator.openCelestialSelection(syncManager.getPlayer());
            }
        }));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        BooleanSyncValue machineSyncer = syncManager.findSyncHandler(MACHINE_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue allowedToWorkSyncer = syncManager
            .findSyncHandler(ALLOWED_TO_WORK_SYNC_KEY, BooleanSyncValue.class);
        IntSyncValue moduleCountSyncer = syncManager.findSyncHandler(MODULE_COUNT_SYNC_KEY, IntSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.lang("gt.interact.desc.mb.incomplete")
                .color(Color.WHITE.main)
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .setEnabledIf(widget -> !machineSyncer.getBoolValue())
                .fullWidth())
            .child(
                IKey.lang("gt.blockmachines.multimachine.ig.elevator.gui.ready")
                    .color(Color.WHITE.main)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .setEnabledIf(widget -> machineSyncer.getBoolValue())
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocal("gt.blockmachines.multimachine.ig.elevator.gui.numOfModules")
                        + ": "
                        + moduleCountSyncer.getIntValue())
                    .color(Color.WHITE.main)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .setEnabledIf(widget -> allowedToWorkSyncer.getBoolValue())
                    .fullWidth());
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createTeleportButton(syncManager));
    }

    private IWidget createTeleportButton(PanelSyncManager syncManager) {
        InteractionSyncHandler teleportSyncer = syncManager
            .findSyncHandler(TELEPORT_SYNC_KEY, InteractionSyncHandler.class);
        return new ButtonWidget<>().size(16, 16)
            .playClickSound(false)
            .background(
                new DrawableStack(GTGuiTextures.BUTTON_STANDARD, GTNLMui2Textures.OVERLAY_BUTTON_PLANET_TELEPORT))
            .syncHandler(teleportSyncer)
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("ig.button.travel")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }
}
