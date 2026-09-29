// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.model.pigmeeModel;

import java.util.ArrayList;
import java.util.Arrays;
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

@SideOnly(Side.CLIENT)
public final class PigmeeFumoRenderHelper {

    public record PlainQuadContext(ModelQuadFacing facing) implements BakedModelQuadContext {

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

    public record FacedQuad(ModelQuadFacing lightFace, ModelQuadView quad) {}

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

    public static float shadeOf(ModelQuadFacing facing) {
        ForgeDirection direction = facing == null ? ForgeDirection.UNKNOWN : facing.toForgeDir();
        if (direction == ForgeDirection.UNKNOWN) return 1.0F;
        return ModelISBRH.diffuseLight(NormI8.pack(direction.offsetX, direction.offsetY, direction.offsetZ));
    }

    public static void fillBrightness(IBlockAccess world, int x, int y, int z, int[] out) {
        int light = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            int neighbour = world
                .getLightBrightnessForSkyBlocks(x + face.offsetX, y + face.offsetY, z + face.offsetZ, 0);
            if (neighbour > light) {
                light = neighbour;
            }
        }
        Arrays.fill(out, light);
    }

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
