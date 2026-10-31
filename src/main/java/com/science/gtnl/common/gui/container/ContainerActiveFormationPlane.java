package com.science.gtnl.common.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import com.science.gtnl.mixins.late.appliedEnergistics.AccessorContainerUpgradeable;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.container.implementations.ContainerFormationPlane;
import appeng.container.sync.SyncRegistrar;
import appeng.container.sync.handlers.ConfigEnumSyncHandler;
import appeng.parts.automation.PartFormationPlane;
import appeng.util.Platform;
import lombok.Setter;

@Setter
public class ContainerActiveFormationPlane extends ContainerFormationPlane {

    public SchedulingMode schedulingMode = SchedulingMode.DEFAULT;
    private final ConfigEnumSyncHandler<SchedulingMode> schedulingModeSync;

    public ContainerActiveFormationPlane(InventoryPlayer ip, PartFormationPlane te) {
        super(ip, te);
        SyncRegistrar sync = this.syncRegistrar();
        this.schedulingModeSync = sync.configEnum(
            "schedulingMode",
            Settings.SCHEDULING_MODE,
            SchedulingMode.class,
            this.getUpgradeable()
                .getConfigManager());
        this.schedulingModeSync.onClientChange((oldValue, newValue) -> this.schedulingMode = newValue);
    }

    @Override
    public void loadSettingsFromHost(final IConfigManager cm) {
        this.setFuzzyMode((FuzzyMode) cm.getSetting(Settings.FUZZY_MODE));
        this.setRedStoneMode((RedstoneMode) cm.getSetting(Settings.REDSTONE_CONTROLLED));
        this.schedulingModeSync.syncFromConfig();
        this.schedulingMode = this.schedulingModeSync.get();
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            var accessor = (AccessorContainerUpgradeable) this;
            var configManager = accessor.getUpgradeable()
                .getConfigManager();
            this.setFuzzyMode((FuzzyMode) configManager.getSetting(Settings.FUZZY_MODE));
            this.setPlaceMode((YesNo) configManager.getSetting(Settings.PLACE_BLOCK));
            this.schedulingModeSync.syncFromConfig();
            this.schedulingMode = this.schedulingModeSync.get();
        }

        this.standardDetectAndSendChanges();
    }

    public void setPlaceMode(final YesNo placeMode) {
        this.placeMode = placeMode;
    }

    public void setRedStoneMode(final RedstoneMode rsMode) {
        this.rsMode = rsMode;
    }

    @Override
    public SchedulingMode getSchedulingMode() {
        return this.schedulingMode;
    }
}
