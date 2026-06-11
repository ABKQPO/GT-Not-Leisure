package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Size;

public enum MilestoneIcon {

    CHARGE(60, 75, "power"),
    CONVERSION(54, 75, "recipe"),
    CATALYST(75, 75, "fuel"),
    COMPOSITION(75, 75, "purchasable");

    public static final MilestoneIcon[] VALUES = values();

    private final int width;
    private final int height;
    private final String name;

    MilestoneIcon(int width, int height, String shortName) {
        this.width = width;
        this.height = height;
        this.name = "gt.blockmachines.multimachine.FOG." + shortName + "milestone";
    }

    @Deprecated
    public UITexture getSymbol() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return switch (this) {
            case CHARGE -> EternalGregTechWorkshopTextures.PICTURE_GODFORGE_MILESTONE_CHARGE;
            case CONVERSION -> EternalGregTechWorkshopTextures.PICTURE_GODFORGE_MILESTONE_CONVERSION;
            case CATALYST -> EternalGregTechWorkshopTextures.PICTURE_GODFORGE_MILESTONE_CATALYST;
            case COMPOSITION -> EternalGregTechWorkshopTextures.PICTURE_GODFORGE_MILESTONE_COMPOSITION;
        };
    }

    @Deprecated
    public Size getSize() {
        // TODO: Remove this MUI1 size getter after Eternal GregTech Workshop fallback windows are removed.
        return new Size(width, height);
    }

    public float getWidthRatio() {
        return (float) width / height;
    }

    public String getNameText() {
        return StatCollector.translateToLocal(name);
    }
}
