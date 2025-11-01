package com.reavaritia.common.block.extremeAnvil;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFallingBlockExtremeAnvil extends Render {

    private static final ExtremeAnvilModel model = new ExtremeAnvilModel();

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/blocks/ExtremeAnvil.png");

    public RenderFallingBlockExtremeAnvil() {
        this.shadowSize = 0.5F;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityExtremeAnvil fallingBlock = (EntityExtremeAnvil) entity;
        if (fallingBlock.getBlock() instanceof BlockExtremeAnvil) {
            int meta = fallingBlock.blockMetadata & 3;
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslatef((float) x, (float) y + 1.2F, (float) z);
            GL11.glRotatef(180, 0, 0, 1);

            GL11.glRotatef(meta * 90, 0, 1, 0);

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            this.bindEntityTexture(entity);
            model.render(entity, 0, 0, 0, 0, 0, 0.0625F);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
