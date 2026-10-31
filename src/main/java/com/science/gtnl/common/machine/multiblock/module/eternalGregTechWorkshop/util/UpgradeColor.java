package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import com.cleanroommc.modularui.api.drawable.IDrawable;

import gregtech.api.modularui2.GTGuiTextures;

public enum UpgradeColor {

    BLUE,
    PURPLE,
    ORANGE,
    GREEN,
    RED;

    public IDrawable getMui2Background() {
        return switch (this) {
            case BLUE -> GTGuiTextures.BACKGROUND_GLOW_BLUE;
            case PURPLE -> GTGuiTextures.BACKGROUND_GLOW_PURPLE;
            case ORANGE -> GTGuiTextures.BACKGROUND_GLOW_ORANGE;
            case GREEN -> GTGuiTextures.BACKGROUND_GLOW_GREEN;
            case RED -> GTGuiTextures.BACKGROUND_GLOW_RED;
        };
    }

    public IDrawable getMui2Overlay() {
        return switch (this) {
            case BLUE -> GTGuiTextures.PICTURE_OVERLAY_BLUE;
            case PURPLE -> GTGuiTextures.PICTURE_OVERLAY_PURPLE;
            case ORANGE -> GTGuiTextures.PICTURE_OVERLAY_ORANGE;
            case GREEN -> GTGuiTextures.PICTURE_OVERLAY_GREEN;
            case RED -> GTGuiTextures.PICTURE_OVERLAY_RED;
        };
    }

    public IDrawable getMui2OpaqueConnector() {
        return switch (this) {
            case BLUE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_BLUE_OPAQUE;
            case PURPLE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_PURPLE_OPAQUE;
            case ORANGE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_ORANGE_OPAQUE;
            case GREEN -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_GREEN_OPAQUE;
            case RED -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_RED_OPAQUE;
        };
    }

    public IDrawable getMui2Connector() {
        return switch (this) {
            case BLUE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_BLUE;
            case PURPLE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_PURPLE;
            case ORANGE -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_ORANGE;
            case GREEN -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_GREEN;
            case RED -> GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_RED;
        };
    }
}
