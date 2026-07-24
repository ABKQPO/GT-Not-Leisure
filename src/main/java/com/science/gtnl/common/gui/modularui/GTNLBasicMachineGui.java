package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;

public class GTNLBasicMachineGui<T extends MTEBasicMachine> extends MTEBasicMachineBaseGui<T> {

    private final UITexture logoTexture;

    public GTNLBasicMachineGui(T machine, BasicUIProperties properties) {
        this(machine, properties, GTNLMui2Textures.PICTURE_GTNL_LOGO);
    }

    public GTNLBasicMachineGui(T machine, BasicUIProperties properties, UITexture logoTexture) {
        super(machine, properties);
        this.logoTexture = logoTexture;
        useGregTechLogo(true);
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new com.cleanroommc.modularui.api.drawable.IDrawable.DrawableWidget(logoTexture).size(18, 18);
    }
}
