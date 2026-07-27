package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.DebugResearchStation;

import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;

public class DebugResearchStationGui extends MTEBasicMachineBaseGui<DebugResearchStation> {

    public DebugResearchStationGui(DebugResearchStation machine, BasicUIProperties properties) {
        super(machine, properties);
        useGregTechLogo(true);
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18, 18);
    }
}
