// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

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

// Draws a baked BakedModel into the current local coordinate frame.
// The caller owns the matrix stack: push a matrix, position the frame, then call drawWorld. This helper owns
// one tessellator batch and restores every GL setting it changes, so callers cannot leak alpha, blend or lighting into
// the rest of the frame.
// Quad enumeration and vertex emission are delegated to GTNHLib: getQuads is called once per
// ModelQuadFacing exactly as ModelQuadFacing does, and the vertices go through ModelQuadFacing.
// Only the brightness sampling is local, because GTNHLib's ISBRH derives its lightmap from a RenderBlocks pass
// that a special renderer does not have.
// Item rendering must not use this: items keep the caller's lightmap and light directions.
@SideOnly(Side.CLIENT)
public final class PigmeeFumoRenderHelper {

    private PigmeeFumoRenderHelper() {}

    // A quad context for callers that only need the model's geometry.
    // Only getQuadFacing is read by the models this helper serves; the block state is deliberately left null
    // because a head-worn doll has no world block to build one from, and PileOfQuads never consults it.
    // facing: the facing of the quads being requested
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

    // A quad from a baked model, paired with the face it actually belongs to.
    // That face is not the bucket the quad was found in: GTNHLib buckets quads by cull face, and a model that
    // never declares cullface puts every one of them in a single bucket. The bake sets getLightFace()
    // from the face name, so that is the authoritative facing.
    public record FacedQuad(ModelQuadFacing lightFace, ModelQuadView quad) {}

    // model: a non-null baked model
    // Returns every quad of the model, in the same per-facing order ModelISBRH uses. Never null.
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

    // facing: the quad's light face
    // Returns the directional shade for that face, or 1 when the face is not a real direction
    public static float shadeOf(ModelQuadFacing facing) {
        ForgeDirection direction = facing == null ? ForgeDirection.UNKNOWN : facing.toForgeDir();
        if (direction == ForgeDirection.UNKNOWN) return 1.0F;
        return ModelISBRH.diffuseLight(NormI8.pack(direction.offsetX, direction.offsetY, direction.offsetZ));
    }

    // Samples the seven lightmap values a world-space draw needs.
    // Light comes straight from getLightBrightnessForSkyBlocks, which already applies the day/night
    // sky subtraction. Block#getMixedBrightnessForBlock must not be used here: it can hand back a stale zero
    // while the propagation pass is still running, which paints a face black.
    // The block and its six neighbours are sampled and the brightest wins. The block declares lightOpacity = 2,
    // which costs its own tile two sky levels (13 against 15 directly above), and a small model only occupies the middle
    // of its block anyway.
    // world: non-null access
    // x: block X
    // y: block Y
    // z: block Z
    // out: a seven element array: the six faces in ForgeDirection order plus the block itself
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

    // Emits every quad of the model using the pre-baked shades and per-quad emission.
    // quads: quads from getAllQuads
    // brightness: the seven element array filled by
    // fillBrightness(IBlockAccess, int, int, int, int[])
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
                int light = brightness[entry.lightFace().ordinal()];
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
