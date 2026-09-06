package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.ManaTank;

import gregtech.common.gui.modularui.singleblock.base.MTEDigitalTankBaseGui;

public class ManaTankGui extends MTEDigitalTankBaseGui<ManaTank> {

    public ManaTankGui(ManaTank machine) {
        super(machine);
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(SLOT_SIZE);
    }
}
