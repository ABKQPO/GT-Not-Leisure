package com.science.gtnl.common.render.beamformer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.api.util.NormI8;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeamFormerItemRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return BeamFormerModel.INSTANCE.getItem(position(type)) != null;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (helper) {
            case INVENTORY_BLOCK -> false;
            case BLOCK_3D -> type == ItemRenderType.EQUIPPED;
            case EQUIPPED_BLOCK, ENTITY_ROTATION, ENTITY_BOBBING -> true;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Geometry model = BeamFormerModel.INSTANCE.getItem(position(type));
        if (model == null) return;
        boolean inventory = type == ItemRenderType.INVENTORY;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_TEXTURE_BIT);
        GL11.glPushMatrix();
        try {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            if (inventory) {
                GL11.glDisable(GL11.GL_LIGHTING);
            } else {
                // Keep the caller's world lightmap and light directions, including player rotation.
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_NORMALIZE);
            }
            GL11.glColor4f(1, 1, 1, 1);
            applyFrame(type);
            Tessellator tessellator = TessellatorManager.get();
            tessellator.startDrawingQuads();
            for (int i = 0; i < model.quads().length; i++) {
                ModelQuadView quad = model.quads()[i];
                float shade = inventory ? model.shades()[i] : 1;
                tessellator.setColorOpaque_F(shade, shade, shade);
                if (!inventory) {
                    int normal = quad.getComputedFaceNormal();
                    tessellator.setNormal(NormI8.unpackX(normal), NormI8.unpackY(normal), NormI8.unpackZ(normal));
                }
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
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static Position position(ItemRenderType type) {
        return switch (type) {
            case INVENTORY -> Position.GUI;
            case EQUIPPED -> Position.THIRDPERSON_RIGHTHAND;
            case EQUIPPED_FIRST_PERSON -> Position.FIRSTPERSON_RIGHTHAND;
            case ENTITY -> RenderItem.renderInFrame ? Position.FIXED : Position.GROUND;
            default -> Position.FIXED;
        };
    }

    private static void applyFrame(ItemRenderType type) {
        switch (type) {
            case INVENTORY -> {
                GL11.glTranslatef(8, 8, 0);
                GL11.glScalef(16, -16, 16);
            }
            case ENTITY -> GL11.glScalef(2, 2, 2);
            case EQUIPPED_FIRST_PERSON -> {
                // Cancel Forge's block offset and Minecraft's legacy item scale and resting yaw.
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                GL11.glScalef(1 / 0.4F, 1 / 0.4F, 1 / 0.4F);
                GL11.glRotatef(-45, 0, 1, 0);
            }
            case EQUIPPED -> {
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                GL11.glScalef(-1 / 0.375F, -1 / 0.375F, 1 / 0.375F);
                GL11.glRotatef(-45, 0, 1, 0);
                GL11.glRotatef(-20, 1, 0, 0);
                // RenderPlayer's block pose sits four model pixels in front of the arm's end-cap center.
                GL11.glTranslatef(0, 0, 4 / 16.0F);
                // Align the JSON forward axis with the player's right-hand frame.
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glRotatef(180, 0, 1, 0);
            }
            default -> {}
        }
    }
}
