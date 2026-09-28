/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lighting-Tech-Reborn/AE2-Lighting-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.api.util.NormI8;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadViewMutable;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Draws a baked {@link BakedModel} into the current local coordinate frame.
 *
 * <p>
 * The caller owns the matrix stack: push a matrix, position the frame, then call {@link #drawWorld}. This helper owns
 * exactly one tessellator batch and fully restores the GL state it changes, so callers cannot leak alpha, blend or
 * lighting settings into the rest of the frame.
 *
 * <p>
 * Quad enumeration and per-quad vertex emission are delegated to GTNHLib: {@link BakedModel#getQuads} is called once
 * per {@link ModelQuadFacing} exactly as {@link ModelISBRH} itself does, and the vertices go through
 * {@link ModelISBRH#renderQuad}. Only the brightness sampling (which GTNHLib's ISBRH derives from a
 * {@code RenderBlocks} pass a special renderer does not have) and the no-cull policy are local.
 *
 * <p>
 * Item rendering must <em>not</em> use this: items keep the caller's lightmap and light directions.
 */
@SideOnly(Side.CLIENT)
public final class PigmeeFumoRenderHelper {

    private PigmeeFumoRenderHelper() {}

    /**
     * A quad context for callers that only need the model's geometry.
     *
     * <p>
     * Only {@code getQuadFacing} is read by the models this helper serves; the block state is deliberately left null
     * because a head-worn doll has no world block to build one from, and {@code PileOfQuads} never consults it.
     *
     * @param facing the facing of the quads being requested
     */
    private record PlainQuadContext(ModelQuadFacing facing) implements BakedModelQuadContext {

        @Override
        public BlockState getBlockState() {
            return null;
        }

        @Override
        public ModelQuadFacing getQuadFacing() {
            return facing;
        }

        @Override
        public Random getRandom() {
            return null;
        }

        @Override
        public Supplier<ModelQuadViewMutable> getQuadPool() {
            return null;
        }
    }

    /**
     * A quad from a baked model.
     *
     * <p>
     * The bucket a quad comes from is the <em>cull</em> face GTNHLib stored it under, not its outward direction: models
     * that never declare {@code cullface} put every quad in one bucket. The authoritative facing is therefore the
     * quad's own {@code getLightFace()}, which the bake sets from the face name.
     */
    public record FacedQuad(ModelQuadFacing lightFace, ModelQuadView quad) {}

    /**
     * @param model a non-null baked model
     * @return every quad of the model, in the same per-facing order {@link ModelISBRH} uses. Never null.
     */
    public static List<FacedQuad> getAllQuads(BakedModel model) {
        List<FacedQuad> quads = new ArrayList<>();
        for (ModelQuadFacing bucket : ModelQuadFacing.VALUES) {
            List<ModelQuadView> batch = model.getQuads(new PlainQuadContext(bucket));
            if (batch == null || batch.isEmpty()) continue;
            for (ModelQuadView quad : batch) {
                quads.add(new FacedQuad(quad.getLightFace(), quad));
            }
        }
        return quads;
    }

    /**
     * @param facing the quad's light face
     * @return the directional shade for that face, or 1 when the face is not a real direction
     */
    public static float shadeOf(ModelQuadFacing facing) {
        ForgeDirection direction = facing == null ? ForgeDirection.UNKNOWN : facing.toForgeDir();
        if (direction == ForgeDirection.UNKNOWN) return 1.0F;
        return ModelISBRH.diffuseLight(NormI8.pack(direction.offsetX, direction.offsetY, direction.offsetZ));
    }

    /**
     * Samples the seven lightmap values a world-space draw needs.
     *
     * <p>
     * Light is read straight from {@link IBlockAccess#getLightBrightnessForSkyBlocks}, which already applies the
     * day/night sky subtraction. Going through {@code Block#getMixedBrightnessForBlock} instead was the source of two
     * separate defects: it can hand back a stale zero while the light propagation pass is still running, which painted
     * one face black, and it made a doll standing on the ground read dimmer than the same doll worn on a head, which
     * goes through the player's own brightness and never touches that path.
     *
     * <p>
     * The block and all six neighbours are sampled and the <em>brightest</em> result wins. The doll block declares
     * {@code lightOpacity = 2}, which costs it two sky levels on its own tile (a measured {@code sky=13} against
     * {@code sky=15} directly above), so the brightest neighbour is what the player sees around the model; a small
     * model only occupies the middle of its block anyway.
     *
     * @param world non-null access
     * @param x     block X
     * @param y     block Y
     * @param z     block Z
     * @param out   a seven element array: the six faces in {@link ForgeDirection} order plus the block itself
     */
    public static void fillBrightness(IBlockAccess world, int x, int y, int z, int[] out) {
        int light = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            int neighbour = world
                .getLightBrightnessForSkyBlocks(x + face.offsetX, y + face.offsetY, z + face.offsetZ, 0);
            if (neighbour > light) {
                light = neighbour;
            }
        }
        for (int i = 0; i < out.length; i++) {
            out[i] = light;
        }
    }

    /**
     * Emits every quad of the model using the pre-baked shades and per-quad emission.
     *
     * @param quads      quads from {@link #getAllQuads}
     * @param brightness the seven element array filled by
     *                   {@link #fillBrightness(IBlockAccess, int, int, int, int[])}
     */
    public static void drawWorld(List<FacedQuad> quads, int[] brightness) {
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
            ModelISBRH isbrh = ModelISBRH.INSTANCE.get();
            tessellator.startDrawingQuads();
            for (FacedQuad entry : quads) {
                ModelQuadView quad = entry.quad();
                int light = brightness[entry.lightFace()
                    .ordinal()];
                int emission = quad.getEmissiveness();
                tessellator
                    .setBrightness(Math.max(light & 0xF00000, emission << 20) | Math.max(light & 0xF0, emission << 4));
                float shade = shadeOf(entry.lightFace());
                tessellator.setColorOpaque_F(shade, shade, shade);
                isbrh.renderQuad(quad, 0.0F, 0.0F, 0.0F, tessellator, null);
            }
            tessellator.draw();
        } finally {
            GL11.glPopAttrib();
        }
    }
}
