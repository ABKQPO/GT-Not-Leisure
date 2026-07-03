package com.science.gtnl.container;

import net.minecraft.entity.player.InventoryPlayer;

import com.science.gtnl.mixins.late.appliedEnergistics.AccessorContainerUpgradeable;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerFormationPlane;
import appeng.parts.automation.PartFormationPlane;
import appeng.util.Platform;
import lombok.Setter;

@Setter
public class ContainerActiveFormationPlane extends ContainerFormationPlane {

    @GuiSync(7)
    public SchedulingMode schedulingMode = SchedulingMode.DEFAULT;

    public ContainerActiveFormationPlane(InventoryPlayer ip, PartFormationPlane te) {
        super(ip, te);
    }

    @Override
    public void loadSettingsFromHost(final IConfigManager cm) {
        this.setFuzzyMode((FuzzyMode) cm.getSetting(Settings.FUZZY_MODE));
        this.setRedStoneMode((RedstoneMode) cm.getSetting(Settings.REDSTONE_CONTROLLED));
        this.setSchedulingMode((SchedulingMode) cm.getSetting(Settings.SCHEDULING_MODE));
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
            this.setSchedulingMode((SchedulingMode) configManager.getSetting(Settings.SCHEDULING_MODE));
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
