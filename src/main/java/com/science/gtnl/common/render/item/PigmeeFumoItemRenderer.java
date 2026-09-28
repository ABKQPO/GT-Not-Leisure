// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
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

// Inventory, hand, dropped and item-frame rendering for the Pigmee Fumo.
// Vertex emission and the per-face shade come from GTNHLib's ModelISBRH. The frame transforms are local:
// ModelISBRH.renderItem applies its own display, but that assumes an identity matrix, whereas a Forge
// IItemRenderer is handed an already-positioned frame.
// Fixed-function lighting stays off for every frame and the pre-baked per-face shade is used instead. GTNHLib's quad
// emitter issues no vertex normals, so lighting a held doll shaded it by whatever normal happened to be current.
// There is deliberately no head branch: the worn doll is drawn by PigmeeFumoHeadRenderer in head-bone space.
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
            // Forge's equipped path only translates by (-0.5, -0.5, -0.5) when this is true, which is exactly the
            // centring a doll drawn inside its 0..1 cell needs; the item path would apply the flat-item pose instead.
            case INVENTORY_BLOCK -> false;
            // RenderItem already lifted the item by the bob before calling Forge, and Forge's -bobing translate is
            // what cancels it again. Reporting true keeps that lift, which is the bob every dropped item has;
            // reporting false would drop the doll by up to 0.2 blocks and leave it static.
            case ENTITY_BOBBING -> true;
            // EntityItem's spin, which Forge applies about the origin; applyFrame recentres the model to match.
            case ENTITY_ROTATION -> true;
            // Leaving BLOCK_3D false for ENTITY keeps the dropped doll on Forge's flat-path 0.5 scale; the 3D path
            // would apply the 0.25 that this block's -1 render type implies.
            case BLOCK_3D -> type == ItemRenderType.EQUIPPED;
            case EQUIPPED_BLOCK -> true;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        BakedModel model = PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION);
        if (model == null) return;
        List<FacedQuad> quads = PigmeeFumoRenderHelper.getAllQuads(model);
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
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1, 1, 1, 1);
            applyFrame(type);
            Tessellator tessellator = TessellatorManager.get();
            tessellator.startDrawingQuads();
            for (FacedQuad entry : quads) {
                ModelQuadView quad = entry.quad();
                float shade = PigmeeFumoRenderHelper.shadeOf(entry.lightFace());
                tessellator.setColorOpaque_F(shade, shade, shade);
                isbrh.renderQuad(quad, 0.0F, 0.0F, 0.0F, tessellator, null);
            }
            tessellator.draw();
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    // Applies the frame that belongs to one render context.
    // type: context being drawn
    private static void applyFrame(ItemRenderType type) {
        switch (type) {
            case INVENTORY -> {
                GL11.glTranslatef(8, 8, 0);
                GL11.glScalef(16, -16, 16);
                // The slot carries no display transform of its own, unlike the equip frames handled below.
                PigmeeFumoModel.applyIconDisplay();
            }
            case EQUIPPED, EQUIPPED_FIRST_PERSON -> PigmeeFumoModel.applyHandDisplay();
            case ENTITY -> {
                // Forge rotates a dropped item about the origin before calling us, so the model has to be centred
                // there or it orbits that corner instead of spinning in place. Only X and Z are recentred: the spin
                // is about Y, and shifting Y would sink the model below the ground it sits on.
                GL11.glScalef(1.2F, 1.2F, 1.2F);
                GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
            }
            default -> {}
        }
    }
}
