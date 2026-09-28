/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.science.gtnl.common.render.model.PigmeeFumoModel;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper.FacedQuad;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Inventory, hand, dropped and item-frame rendering for the Pigmee Fumo.
 *
 * <p>
 * Vertex emission and the per-face shade come from GTNHLib's {@link ModelISBRH}. The frame transforms and the
 * {@code gui} display application are local: GTNHLib applies the display itself inside its own item renderer, but that
 * only lines up when its identity-matrix assumption about the caller's frame holds, and a Forge {@link IItemRenderer}
 * is handed an already-positioned frame instead. Applying the display here reproduces the same {@code M * D * V} order.
 *
 * <p>
 * There is deliberately no head branch: the worn doll is drawn by {@link PigmeeFumoHeadRenderer} in head-bone space.
 */
@SideOnly(Side.CLIENT)
public class PigmeeFumoItemRenderer implements IItemRenderer {

    private final ModelISBRH isbrh = ModelISBRH.INSTANCE.get();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION) != null;
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
        BakedModel model = PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION);
        if (model == null) return;
        List<FacedQuad> quads = PigmeeFumoRenderHelper.getAllQuads(model);
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
            if (inventory) {
                // The GUI slot carries no display transform of its own, unlike the equip frames applyFrame handles.
                PigmeeFumoModel.applyIconDisplay(model);
            }
            Tessellator tessellator = TessellatorManager.get();
            tessellator.startDrawingQuads();
            for (FacedQuad entry : quads) {
                ModelQuadView quad = entry.quad();
                float shade = inventory ? PigmeeFumoRenderHelper.shadeOf(entry.lightFace()) : 1;
                tessellator.setColorOpaque_F(shade, shade, shade);
                isbrh.renderQuad(quad, 0.0F, 0.0F, 0.0F, tessellator, null);
            }
            tessellator.draw();
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
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
