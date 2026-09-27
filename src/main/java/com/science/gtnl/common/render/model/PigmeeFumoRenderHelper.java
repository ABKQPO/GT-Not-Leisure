/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.api.util.NormI8;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Draws baked {@link Geometry} into the current local coordinate frame.
 *
 * <p>
 * The caller owns the matrix stack: push a matrix, position the frame, then call {@link #drawWorld}. This
 * helper owns exactly one tessellator batch and fully restores the GL state it changes, so callers cannot
 * leak alpha, blend or lighting settings into the rest of the frame.
 *
 * <p>
 * World-space and head-local drawing share this path. Item rendering must <em>not</em> use it: items keep
 * the caller's lightmap and light directions, see {@code BeamFormerItemRenderer}.
 */
@SideOnly(Side.CLIENT)
public final class PigmeeFumoRenderHelper {

    private PigmeeFumoRenderHelper() {}

    /**
     * Emits every quad of the model using the pre-baked shades and per-quad emission.
     *
     * @param model      a non-null baked geometry; the caller has already applied its own transform
     * @param brightness seven lightmap samples: indices 0..5 are the faces in ForgeDirection order, index 6
     *                   is the block's own mixed brightness. The array is only read.
     */
    public static void drawWorld(Geometry model, int[] brightness) {
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_TEXTURE_BIT);
        try {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            // Shades are pre-baked into the quads; fixed-function lighting would darken them twice.
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = TessellatorManager.get();
            tessellator.startDrawingQuads();
            ModelQuadView[] quads = model.quads();
            for (int i = 0; i < quads.length; i++) {
                ModelQuadView quad = quads[i];
                int light = brightness[model.lightSides()[i]];
                int emission = quad.getEmissiveness();
                tessellator
                    .setBrightness(Math.max(light & 0xF00000, emission << 20) | Math.max(light & 0xF0, emission << 4));
                float shade = model.shades()[i];
                tessellator.setColorOpaque_F(shade, shade, shade);
                int normal = quad.getComputedFaceNormal();
                tessellator.setNormal(NormI8.unpackX(normal), NormI8.unpackY(normal), NormI8.unpackZ(normal));
                for (int vertex = 0; vertex < 4; vertex++) {
                    tessellator.addVertexWithUV(
                        quad.getX(vertex),
                        quad.getY(vertex),
                        quad.getZ(vertex),
                        quad.getTexU(vertex),
                        quad.getTexV(vertex));
                }
            }
            tessellator.draw();
        } finally {
            GL11.glPopAttrib();
        }
    }
}
