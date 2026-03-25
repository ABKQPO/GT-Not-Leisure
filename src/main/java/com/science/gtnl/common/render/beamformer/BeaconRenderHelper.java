package com.science.gtnl.common.render.beamformer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeaconRenderHelper {

    private static final ResourceLocation BEACON_BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    private static double frac(double value) {
        return value - Math.floor(value);
    }

    public static void renderBeamSegment(double x, double y, double z, double partialTicks, double textureScale,
        double totalWorldTime, int yOffset, double height, float[] colors, double beamRadius, double glowRadius) {

        Minecraft.getMinecraft().renderEngine.bindTexture(BEACON_BEAM_TEXTURE);

        double i = yOffset + height;

        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Tessellator tess = Tessellator.instance;

        double d0 = totalWorldTime + partialTicks;
        double d1 = height < 0 ? d0 : -d0;
        double d2 = frac(d1 * 0.2D - Math.floor(d1 * 0.1D));

        float r = colors[0];
        float g = colors[1];
        float b = colors[2];

        double d3 = d0 * 0.025D * -1.5D;

        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * beamRadius;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * beamRadius;

        double d6 = 0.5D + Math.cos(d3 + Math.PI / 4D) * beamRadius;
        double d7 = 0.5D + Math.sin(d3 + Math.PI / 4D) * beamRadius;

        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * beamRadius;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * beamRadius;

        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * beamRadius;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * beamRadius;

        double d14 = -1.0D + d2;
        double d15 = height * textureScale * (0.5D / beamRadius) + d14;

        float alpha = 0.8f;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, alpha);

        tess.addVertexWithUV(x + d4, y + yOffset, z + d5, 0.0D, d14);
        tess.addVertexWithUV(x + d4, y + i, z + d5, 0.0D, d15);
        tess.addVertexWithUV(x + d6, y + i, z + d7, 1.0D, d15);
        tess.addVertexWithUV(x + d6, y + yOffset, z + d7, 1.0D, d14);

        tess.addVertexWithUV(x + d6, y + yOffset, z + d7, 0.0D, d14);
        tess.addVertexWithUV(x + d6, y + i, z + d7, 0.0D, d15);
        tess.addVertexWithUV(x + d10, y + i, z + d11, 1.0D, d15);
        tess.addVertexWithUV(x + d10, y + yOffset, z + d11, 1.0D, d14);

        tess.addVertexWithUV(x + d10, y + yOffset, z + d11, 0.0D, d14);
        tess.addVertexWithUV(x + d10, y + i, z + d11, 0.0D, d15);
        tess.addVertexWithUV(x + d8, y + i, z + d9, 1.0D, d15);
        tess.addVertexWithUV(x + d8, y + yOffset, z + d9, 1.0D, d14);

        tess.addVertexWithUV(x + d8, y + yOffset, z + d9, 0.0D, d14);
        tess.addVertexWithUV(x + d8, y + i, z + d9, 0.0D, d15);
        tess.addVertexWithUV(x + d4, y + i, z + d5, 1.0D, d15);
        tess.addVertexWithUV(x + d4, y + yOffset, z + d5, 1.0D, d14);

        tess.draw();

        GL11.glDepthMask(false);

        double min = 0.5D - glowRadius;
        double max = 0.5D + glowRadius;

        double d13 = -1.0D + d2;
        double d16 = height * textureScale + d13;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, 0.125F);

        tess.addVertexWithUV(x + min, y + yOffset, z + min, 0.0D, d13);
        tess.addVertexWithUV(x + min, y + i, z + min, 0.0D, d16);
        tess.addVertexWithUV(x + max, y + i, z + min, 1.0D, d16);
        tess.addVertexWithUV(x + max, y + yOffset, z + min, 1.0D, d13);

        tess.addVertexWithUV(x + max, y + yOffset, z + min, 0.0D, d13);
        tess.addVertexWithUV(x + max, y + i, z + min, 0.0D, d16);
        tess.addVertexWithUV(x + max, y + i, z + max, 1.0D, d16);
        tess.addVertexWithUV(x + max, y + yOffset, z + max, 1.0D, d13);

        tess.addVertexWithUV(x + max, y + yOffset, z + max, 0.0D, d13);
        tess.addVertexWithUV(x + max, y + i, z + max, 0.0D, d16);
        tess.addVertexWithUV(x + min, y + i, z + max, 1.0D, d16);
        tess.addVertexWithUV(x + min, y + yOffset, z + max, 1.0D, d13);

        tess.addVertexWithUV(x + min, y + yOffset, z + max, 0.0D, d13);
        tess.addVertexWithUV(x + min, y + i, z + max, 0.0D, d16);
        tess.addVertexWithUV(x + min, y + i, z + min, 1.0D, d16);
        tess.addVertexWithUV(x + min, y + yOffset, z + min, 1.0D, d13);

        tess.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_FOG);
    }
}
