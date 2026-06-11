package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.WirelessSteamDynamoHatch;

import gregtech.common.gui.modularui.hatch.MTEHatchOutputGui;

public class WirelessSteamDynamoHatchGui extends MTEHatchOutputGui {

    public WirelessSteamDynamoHatchGui(WirelessSteamDynamoHatch hatch) {
        super(hatch);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO;
    }
}
