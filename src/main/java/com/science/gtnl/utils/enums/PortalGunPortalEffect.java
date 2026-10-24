package com.science.gtnl.utils.enums;

import java.util.Locale;

// Why: the early coremod loads MainConfig before mixins, so Minecraft or mod classes here break other mixins.
public enum PortalGunPortalEffect {

    PIXEL_SWIRL(Kind.SPRITE, OrbStyle.GLOW, 0xCC59FF26, 0xFFD9FFA6, "portal_gun_portal", 24, 1, 50, 76, 36, 32),
    ELECTRIC_DOOR(Kind.FRAMED, OrbStyle.GLOW, 0xCCC03ACC, 0xFFB482E6, "portal_gun_portal_door", 60, 1, 28, 56, 37, 32),
    FLUID_VORTEX(Kind.VORTEX, OrbStyle.JET, 0xE680E8FF, 0xE6BFF7FF, "portal_gun_portal_vortex", 60, 1, 36, 58, 36, 32),
    GOLDEN_SWIRL(Kind.SPRITE, OrbStyle.GLOW, 0xCCFFCC33, 0xFFFFF59E, "portal_gun_portal_gold", 24, 1, 50, 76, 36, 32);

    public enum Kind {
        SPRITE,
        FRAMED,
        VORTEX
    }

    public enum OrbStyle {
        GLOW,
        JET
    }

    public final Kind kind;
    public final OrbStyle orbStyle;
    public final int glowColor, coreColor;
    public final String texture;
    public final int frames, frameTicks, frameWidth, frameHeight, standHalfHeight, pixelsPerBlock;

    PortalGunPortalEffect(Kind kind, OrbStyle orbStyle, int glowColor, int coreColor, String texture, int frames,
        int frameTicks, int frameWidth, int frameHeight, int standHalfHeight, int pixelsPerBlock) {
        this.kind = kind;
        this.orbStyle = orbStyle;
        this.glowColor = glowColor;
        this.coreColor = coreColor;
        this.texture = texture;
        this.frames = frames;
        this.frameTicks = frameTicks;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.standHalfHeight = standHalfHeight;
        this.pixelsPerBlock = pixelsPerBlock;
    }

    public PortalGunPortalEffect next() {
        PortalGunPortalEffect[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public String langKey() {
        return "item.gtnl.portal_gun.effect." + name().toLowerCase(Locale.ROOT);
    }

    public String texturePath() {
        return "textures/entity/" + texture + ".png";
    }

    public double halfWidth() {
        return frameWidth / 2.0 / pixelsPerBlock;
    }

    public double halfHeight() {
        return frameHeight / 2.0 / pixelsPerBlock;
    }

    public double standHalfHeightBlocks() {
        return standHalfHeight / (double) pixelsPerBlock;
    }
}
