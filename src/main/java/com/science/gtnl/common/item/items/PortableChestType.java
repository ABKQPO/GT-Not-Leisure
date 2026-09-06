package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.util.ResourceLocation;

public enum PortableChestType {

    COPPER(184, 184, 9, 5, "copper"),
    IRON(184, 202, 9, 6, "iron"),
    SILVER(184, 238, 9, 8, "silver"),
    STEEL(184, 238, 9, 8, "silver"),
    GOLD(184, 256, 9, 9, "gold"),
    DIAMOND(238, 256, 12, 9, "diamond"),
    CRYSTAL(238, 256, 12, 9, "diamond"),
    OBSIDIAN(238, 256, 12, 9, "diamond"),
    NETHERITE(292, 256, 15, 9, "netherite"),
    DARKSTEEL(292, 256, 15, 9, "netherite");

    public final int xSize;
    public final int ySize;
    public final int rows;
    public final int cols;
    public final ResourceLocation guiResource;

    private PortableChestType(int xSize, int ySize, int rows, int cols, String textureName) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.rows = rows;
        this.cols = cols;
        this.guiResource = new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/" + textureName + ".png");
    }

    public int getCapacity() {
        return rows * cols;
    }
}
