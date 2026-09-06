package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.WirelessSteamDynamoHatch;

import gregtech.common.gui.modularui.hatch.MTEHatchOutputGui;

public class WirelessSteamDynamoHatchGui extends MTEHatchOutputGui {

    public WirelessSteamDynamoHatchGui(WirelessSteamDynamoHatch hatch) {
        super(hatch);
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO).size(SLOT_SIZE);
    }
}
