package com.science.gtnl.common.gui.modularui;

import net.minecraft.entity.player.EntityPlayer;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.machine.basicMachine.StellarIrisController;
import com.science.gtnl.utils.world.stellar.StellarIrisTeamSnapshot;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;

import gregtech.common.gui.modularui.singleblock.base.MTETieredMachineBlockBaseGui;
import gregtech.common.gui.modularui.synchandler.NBTSerializableSyncHandler;

public class StellarIrisGui extends MTETieredMachineBlockBaseGui<StellarIrisController> {

    private static final String SNAPSHOT_SYNC_KEY = "stellarIrisSnapshot";
    private static final String ACTION_SYNC_KEY = "stellarIrisAction";

    private StellarIrisUpgradeTreeWidget treeWidget;
    private EntityPlayer openingPlayer;

    public StellarIrisGui(StellarIrisController machine) {
        super(machine);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        openingPlayer = guiData.getPlayer();
        treeWidget = new StellarIrisUpgradeTreeWidget();
        registerSyncValues(syncManager);
        return new ModularPanel("stellar_iris").fullScreenInvisible()
            .child(treeWidget.full());
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        StellarIrisUpgradeActionSyncHandler actionHandler = new StellarIrisUpgradeActionSyncHandler(openingPlayer);
        syncManager.syncValue(ACTION_SYNC_KEY, actionHandler);
        treeWidget.setActionHandler(actionHandler);
        syncManager.syncValue(
            SNAPSHOT_SYNC_KEY,
            new NBTSerializableSyncHandler<>(
                StellarIrisTeamSnapshot::new,
                () -> StellarIrisUpgradeManager.getSnapshot(openingPlayer),
                treeWidget::setSnapshot).withEqualityFunc((left, right) -> {
                    StellarIrisTeamSnapshot leftSnapshot = new StellarIrisTeamSnapshot();
                    StellarIrisTeamSnapshot rightSnapshot = new StellarIrisTeamSnapshot();
                    leftSnapshot.deserializeNBT(left);
                    rightSnapshot.deserializeNBT(right);
                    return leftSnapshot.sameAs(rightSnapshot);
                }));
    }
}
