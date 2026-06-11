package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
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
    protected UITexture getLogoTexture() {
        return logoTexture;
    }
}
