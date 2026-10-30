package com.gtnllib.gui.theme;

import net.minecraft.util.ResourceLocation;

import com.gtnllib.GTNLLib;
import com.gtnllib.gui.theme.ThemeSkin.Part;
import com.gtnllib.gui.widget.SkinDrawable;

public class MetalSkin {

    private static final String ROOT = "textures/gui/skin/metal/";
    private static final int PANEL_SIZE = 28, PANEL_BORDER = 10, CARD_WIDTH = 16, CARD_HEIGHT = 24, CARD_BORDER = 4,
        CARD_HEADER = 12, BUTTON_SIZE = 16, BUTTON_BORDER = 4, SLOT_SIZE = 18, SLOT_BORDER = 2, TRACK_WIDTH = 8,
        TRACK_HEIGHT = 6, TRACK_END = 2, TRACK_EDGE = 1, FILL_WIDTH = 8, FILL_HEIGHT = 4, TAG_WIDTH = 8, TAG_HEIGHT = 9,
        TAG_BORDER = 2, SWITCH_WIDTH = 12, SWITCH_HEIGHT = 6, SWITCH_END = 3, KNOB_SIZE = 6, DIVIDER_WIDTH = 32,
        DIVIDER_HEIGHT = 2, DIVIDER_GLOW = 26, DIVIDER_END = 2, CLOSE_SIZE = 11, CLOSE_BORDER = 3;
    public static final ThemeSkin SKIN = ThemeSkin.NONE
        .with(
            Part.PANEL,
            skin("panel", PANEL_SIZE, PANEL_SIZE).border(PANEL_BORDER, PANEL_BORDER, PANEL_BORDER, PANEL_BORDER)
                .tiled(true)
                .glow(texture("panel_glow")))
        .with(
            Part.CARD,
            skin("card", CARD_WIDTH, CARD_HEIGHT).border(CARD_BORDER, CARD_HEADER, CARD_BORDER, CARD_BORDER)
                .glow(texture("card_glow")))
        .with(
            Part.BUTTON,
            skin("button", BUTTON_SIZE, BUTTON_SIZE).border(BUTTON_BORDER, BUTTON_BORDER, BUTTON_BORDER, BUTTON_BORDER)
                .glow(texture("button_glow")))
        .with(Part.SLOT, skin("slot", SLOT_SIZE, SLOT_SIZE).border(SLOT_BORDER, SLOT_BORDER, SLOT_BORDER, SLOT_BORDER))
        .with(
            Part.SLIDER_TRACK,
            skin("slider_track", TRACK_WIDTH, TRACK_HEIGHT).border(TRACK_END, TRACK_EDGE, TRACK_END, TRACK_EDGE))
        .with(
            Part.SLIDER_FILL,
            skin("slider_fill", FILL_WIDTH, FILL_HEIGHT).tiled(true)
                .glow(texture("slider_fill_glow")))
        .with(
            Part.SLIDER_TAG,
            skin("slider_tag", TAG_WIDTH, TAG_HEIGHT).border(TAG_BORDER, TAG_BORDER, TAG_BORDER, TAG_BORDER)
                .glow(texture("slider_tag_glow")))
        .with(
            Part.SWITCH_TRACK,
            skin("switch_track", SWITCH_WIDTH, SWITCH_HEIGHT).border(SWITCH_END, TRACK_EDGE, SWITCH_END, TRACK_EDGE)
                .glow(texture("switch_track_glow")))
        .with(Part.SWITCH_KNOB, skin("switch_knob", KNOB_SIZE, KNOB_SIZE).glow(texture("switch_knob_glow")))
        .with(
            Part.DIVIDER,
            skin("divider", DIVIDER_WIDTH, DIVIDER_HEIGHT).border(DIVIDER_GLOW, 0, DIVIDER_END, 0)
                .glow(texture("divider_glow")))
        .with(
            Part.CLOSE,
            skin("close", CLOSE_SIZE, CLOSE_SIZE).border(CLOSE_BORDER, CLOSE_BORDER, CLOSE_BORDER, CLOSE_BORDER)
                .glow(texture("close_glow")));

    private static SkinDrawable skin(String name, int width, int height) {
        return new SkinDrawable().texture(texture(name), width, height);
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(GTNLLib.MODID, ROOT + name + ".png");
    }
}
