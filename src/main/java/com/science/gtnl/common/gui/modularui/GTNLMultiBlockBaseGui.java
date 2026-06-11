package com.science.gtnl.common.gui.modularui;

import java.util.Optional;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.api.IControllerInfo;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class GTNLMultiBlockBaseGui<T extends MTEMultiBlockBase> extends MTEMultiBlockBaseGui<T> {

    public GTNLMultiBlockBaseGui(T multiblock) {
        super(multiblock);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18)
            .marginTop(4);
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager syncManager) {
        super.initPanelMap(parent, syncManager);
        if (multiblock instanceof IControllerInfo controllerInfo && controllerInfo.supportsMachineInfo()) {
            new GTNLControllerInfoGui(controllerInfo).registerPanel(parent, syncManager, panelMap);
        }
        if (multiblock instanceof IGreenHouse greenHouse && greenHouse.supportsGreenHouseConfigurationPanel()) {
            new GTNLGreenHouseGui(greenHouse).registerPanel(parent, syncManager, panelMap);
        }
        if (multiblock instanceof IControllerUpgrade controllerUpgrade) {
            new GTNLControllerUpgradePanels(multiblock, controllerUpgrade, panelMap)
                .registerPanels(parent, syncManager);
        }
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        if (multiblock instanceof IGreenHouse greenHouse && greenHouse.supportsGreenHouseConfigurationPanel()) {
            new GTNLGreenHouseGui(greenHouse).registerSyncValues(syncManager);
        }
        if (multiblock instanceof IControllerUpgrade controllerUpgrade) {
            new GTNLControllerUpgradePanels(multiblock, controllerUpgrade, panelMap).registerSyncValues(syncManager);
        }
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        createControllerInfoButton().ifPresent(row::child);
        createGreenHouseConfigurationButton().ifPresent(row::child);
        createControllerUpgradeButton(syncManager).ifPresent(row::child);
        return row;
    }

    private Optional<IWidget> createControllerInfoButton() {
        if (!(multiblock instanceof IControllerInfo controllerInfo) || !controllerInfo.supportsMachineInfo()) {
            return Optional.empty();
        }
        return Optional.of(
            new GTNLControllerInfoGui(controllerInfo)
                .createMachineInfoButton(panelMap.get(GTNLControllerInfoGui.MACHINE_INFO_PANEL_KEY)));
    }

    private Optional<IWidget> createGreenHouseConfigurationButton() {
        if (!(multiblock instanceof IGreenHouse greenHouse) || !greenHouse.supportsGreenHouseConfigurationPanel()) {
            return Optional.empty();
        }
        return Optional.of(
            new GTNLGreenHouseGui(greenHouse)
                .createConfigurationButton(panelMap.get(GTNLGreenHouseGui.CONFIGURATION_PANEL_KEY)));
    }

    private Optional<IWidget> createControllerUpgradeButton(PanelSyncManager syncManager) {
        if (!(multiblock instanceof IControllerUpgrade controllerUpgrade)) {
            return Optional.empty();
        }
        return Optional.of(
            new GTNLControllerUpgradePanels(multiblock, controllerUpgrade, panelMap).createUpgradeButton(syncManager));
    }
}
