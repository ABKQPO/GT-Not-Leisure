package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;

import gregtech.api.modularui2.GTGuiTextures;

public enum UpgradeColor {

    // spotless:off

    BLUE(
        EternalGregTechWorkshopTextures.BACKGROUND_GLOW_BLUE,
        EternalGregTechWorkshopTextures.PICTURE_OVERLAY_BLUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_BLUE_OPAQUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_BLUE),

    PURPLE(
        EternalGregTechWorkshopTextures.BACKGROUND_GLOW_PURPLE,
        EternalGregTechWorkshopTextures.PICTURE_OVERLAY_PURPLE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_PURPLE_OPAQUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_PURPLE),

    ORANGE(
        EternalGregTechWorkshopTextures.BACKGROUND_GLOW_ORANGE,
        EternalGregTechWorkshopTextures.PICTURE_OVERLAY_ORANGE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_ORANGE_OPAQUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_ORANGE),

    GREEN(
        EternalGregTechWorkshopTextures.BACKGROUND_GLOW_GREEN,
        EternalGregTechWorkshopTextures.PICTURE_OVERLAY_GREEN,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_GREEN_OPAQUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_GREEN),

    RED(
        EternalGregTechWorkshopTextures.BACKGROUND_GLOW_RED,
        EternalGregTechWorkshopTextures.PICTURE_OVERLAY_RED,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_RED_OPAQUE,
        EternalGregTechWorkshopTextures.PICTURE_UPGRADE_CONNECTOR_RED),

    ;

    // spotless:on

    private final UITexture background;
    private final UITexture overlay;
    private final UITexture opaqueConnector;
    private final UITexture connector;

    UpgradeColor(UITexture background, UITexture overlay, UITexture opaqueConnector, UITexture connector) {
        this.background = background;
        this.overlay = overlay;
        this.opaqueConnector = opaqueConnector;
        this.connector = connector;
    }

    @Deprecated
    public UITexture getBackground() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return background;
    }

    @Deprecated
    public UITexture getOverlay() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return overlay;
    }

    @Deprecated
    public UITexture getOpaqueConnector() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return opaqueConnector;
    }

    @Deprecated
    public UITexture getConnector() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return connector;
    }

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
