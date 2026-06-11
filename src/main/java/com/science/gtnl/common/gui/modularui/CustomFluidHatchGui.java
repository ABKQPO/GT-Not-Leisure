package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;

import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class CustomFluidHatchGui extends MTEHatchBaseGui<CustomFluidHatch> {

    public CustomFluidHatchGui(CustomFluidHatch hatch) {
        super(hatch);
    }

    @Override
    protected UITexture getLogoTexture() {
        return machine.usesSteamLogoForMui2() ? GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO
            : GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
