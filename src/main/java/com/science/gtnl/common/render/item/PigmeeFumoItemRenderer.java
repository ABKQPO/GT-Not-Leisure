// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
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
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoAnimation;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoModel;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoRenderHelper;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoRenderHelper.FacedQuad;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PigmeeFumoItemRenderer implements IItemRenderer {

    public final ModelISBRH isbrh = ModelISBRH.INSTANCE.get();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION) != null;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (helper) {
            // Forge's equipped path only applies its (-0.5, -0.5, -0.5) centring translate when this returns true.
            case INVENTORY_BLOCK -> false;
            // RenderItem already applied the bob, and Forge's -bobing translate cancels it; true keeps that lift.
            case ENTITY_BOBBING -> true;
            // EntityItem's spin, which Forge applies about the origin; applyFrame recentres the model to match.
            case ENTITY_ROTATION -> true;
            // false for ENTITY keeps the flat-path 0.5 scale, not the 0.25 that this -1 render type's 3D path implies.
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
            if (type == ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                GL11.glRotatef(PigmeeFumoAnimation.currentDegrees(), 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            }
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

    public static void applyFrame(ItemRenderType type) {
        switch (type) {
            case INVENTORY -> {
                GL11.glTranslatef(8, 8, 0);
                GL11.glScalef(16, -16, 16);
                // The slot carries no display transform of its own, unlike the equip frames handled below.
                PigmeeFumoModel.applyIconDisplay();
            }
            case EQUIPPED, EQUIPPED_FIRST_PERSON -> PigmeeFumoModel.applyHandDisplay();
            case ENTITY -> {
                // Forge spins a dropped item about the origin, so X and Z are recentred to keep it spinning in place.
                GL11.glScalef(1.2F, 1.2F, 1.2F);
                GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
            }
            default -> {}
        }
    }
}
