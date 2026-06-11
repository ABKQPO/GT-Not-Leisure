package com.science.gtnl.common.gui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.science.gtnl.utils.enums.ModList;

import gregtech.api.enums.Mods;
import gregtech.api.modularui2.GTGuiTextures;

public class GTNLMui2Textures {

    private GTNLMui2Textures() {}

    public static final UITexture PICTURE_CIRCULATION = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/picture/circulation_");

    public static final UITexture PICTURE_GTNL_LOGO = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/picture/logo");

    public static final UITexture PICTURE_GTNL_STEAM_LOGO = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/picture/steam_logo");

    public static final UITexture BACKGROUND_GLOW_WHITE = UITexture
        .fullImage(Mods.TecTech.ID, "gui/background/white_glow");

    public static final UITexture BACKGROUND_SPACE = GTGuiTextures.BACKGROUND_SPACE;

    public static final UITexture BACKGROUND_STAR = GTGuiTextures.BACKGROUND_STAR;

    public static final UITexture PICTURE_GODFORGE_LOGO = GTGuiTextures.PICTURE_GODFORGE_LOGO;

    public static final UITexture OVERLAY_BUTTON_ARROW_GREEN_UP = UITexture
        .fullImage(Mods.GregTech.ID, "gui/overlay_button/arrow_green_up");

    public static final UITexture OVERLAY_BUTTON_MINUS_SMALL = UITexture
        .fullImage(Mods.GregTech.ID, "gui/overlay_button/minus_small");

    public static final UITexture OVERLAY_BUTTON_MINUS_LARGE = UITexture
        .fullImage(Mods.GregTech.ID, "gui/overlay_button/minus_large");

    public static final UITexture OVERLAY_BUTTON_PLANET_TELEPORT = UITexture
        .fullImage(Mods.GTNHIntergalactic.ID, "gui/overlay_button/planet_teleport.png");

    public static final UITexture OVERLAY_BUTTON_MACHINEMODE_SLICING = GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_SLICING;
}
