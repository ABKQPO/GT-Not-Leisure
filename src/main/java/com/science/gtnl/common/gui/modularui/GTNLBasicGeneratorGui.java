package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.api.metatileentity.implementations.MTEBasicGenerator;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicGeneratorBaseGui;

public class GTNLBasicGeneratorGui<T extends MTEBasicGenerator> extends MTEBasicGeneratorBaseGui<T> {

    private final UITexture logoTexture;

    public GTNLBasicGeneratorGui(T machine) {
        this(machine, GTNLMui2Textures.PICTURE_GTNL_LOGO);
    }

    public GTNLBasicGeneratorGui(T machine, UITexture logoTexture) {
        super(machine);
        this.logoTexture = logoTexture;
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new com.cleanroommc.modularui.api.drawable.IDrawable.DrawableWidget(logoTexture).size(18, 18);
    }
}
