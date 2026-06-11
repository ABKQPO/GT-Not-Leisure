package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.ManaTank;

import gregtech.common.gui.modularui.singleblock.base.MTEDigitalTankBaseGui;

public class ManaTankGui extends MTEDigitalTankBaseGui<ManaTank> {

    public ManaTankGui(ManaTank machine) {
        super(machine);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
