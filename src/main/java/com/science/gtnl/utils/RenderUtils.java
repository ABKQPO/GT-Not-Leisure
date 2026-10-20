package com.science.gtnl.utils;

import java.util.EnumSet;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {

    /**
     * Draws only the specified faces in facings.
     */
    public static void drawCube(Tessellator tessellator, double x, double y, double z, double scaleX, double scaleY,
        double scaleZ, float[] rgb, EnumSet<ForgeDirection> facings) {

        if (facings.isEmpty()) return;

        double halfScaleX = scaleX / 2;
        double halfScaleY = scaleY / 2;
        double halfScaleZ = scaleZ / 2;

        double x1 = x - halfScaleX;
        double y1 = y - halfScaleY;
        double z1 = z - halfScaleZ;

        double x2 = x + halfScaleX;
        double y2 = y + halfScaleY;
        double z2 = z + halfScaleZ;

        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];
        float alpha = 0.9f;

        // Down
        if (facings.contains(ForgeDirection.DOWN)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x1, y1, z1);
            tessellator.addVertex(x2, y1, z1);
            tessellator.addVertex(x2, y1, z2);
            tessellator.addVertex(x1, y1, z2);
        }

        // Up
        if (facings.contains(ForgeDirection.UP)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x1, y2, z1);
            tessellator.addVertex(x1, y2, z2);
            tessellator.addVertex(x2, y2, z2);
            tessellator.addVertex(x2, y2, z1);
        }

        // North (Z-)
        if (facings.contains(ForgeDirection.NORTH)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x1, y1, z1);
            tessellator.addVertex(x1, y2, z1);
            tessellator.addVertex(x2, y2, z1);
            tessellator.addVertex(x2, y1, z1);
        }

        // South (Z+)
        if (facings.contains(ForgeDirection.SOUTH)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x1, y1, z2);
            tessellator.addVertex(x2, y1, z2);
            tessellator.addVertex(x2, y2, z2);
            tessellator.addVertex(x1, y2, z2);
        }

        // West (X-)
        if (facings.contains(ForgeDirection.WEST)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x1, y1, z1);
            tessellator.addVertex(x1, y1, z2);
            tessellator.addVertex(x1, y2, z2);
            tessellator.addVertex(x1, y2, z1);
        }

        // East (X+)
        if (facings.contains(ForgeDirection.EAST)) {
            tessellator.setColorRGBA_F(r, g, b, alpha);
            tessellator.addVertex(x2, y1, z1);
            tessellator.addVertex(x2, y2, z1);
            tessellator.addVertex(x2, y2, z2);
            tessellator.addVertex(x2, y1, z2);
        }
    }
}
