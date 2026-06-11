package com.science.gtnl.common.gui.modularui;

import com.science.gtnl.api.IControllerUpgrade;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

public class GTNLControllerUpgradeGui<T extends MTEMultiBlockBase & IControllerUpgrade>
    extends GTNLMultiBlockBaseGui<T> {

    public GTNLControllerUpgradeGui(T multiblock) {
        super(multiblock);
    }
}
