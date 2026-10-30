package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import net.minecraft.util.StatCollector;

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

    public float getWidthRatio() {
        return (float) width / height;
    }

    public String getNameText() {
        return StatCollector.translateToLocal(name);
    }
}
