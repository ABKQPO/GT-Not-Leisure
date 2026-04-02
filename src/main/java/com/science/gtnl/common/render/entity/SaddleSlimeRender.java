package com.science.gtnl.common.render.entity;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SaddleSlimeRender extends RenderLiving {

    private static final ResourceLocation SLIME_TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/entity/saddle_slime.png");
    private final ModelBase scaleModel;

    public SaddleSlimeRender(ModelBase mainModel, ModelBase scaleModel, float shadowSize) {
        super(mainModel, shadowSize);
        this.scaleModel = scaleModel;
    }

    public int renderSlimePass(EntitySlime entitySlime, int renderPass, float partialTicks) {
        if (entitySlime.isInvisible()) {
            return 0;
        } else if (renderPass == 0) {
            this.setRenderPassModel(this.scaleModel);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        } else if (renderPass == 1) {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return -1;
        }

        return -1;
    }

    public void applySlimeScaling(EntitySlime entitySlime, float partialTicks) {
        float slimeSize = (float) entitySlime.getSlimeSize();
        float squishInterpolated = (entitySlime.prevSquishFactor
            + (entitySlime.squishFactor - entitySlime.prevSquishFactor) * partialTicks) / (slimeSize * 0.5F + 1.0F);
        float scale = 1.0F / (squishInterpolated + 1.0F);

        GL11.glScalef(scale * slimeSize, (1.0F / scale) * slimeSize, scale * slimeSize);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLiving, float partialTicks) {
        this.applySlimeScaling((EntitySlime) entityLiving, partialTicks);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLiving, int pass, float partialTicks) {
        return this.renderSlimePass((EntitySlime) entityLiving, pass, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return SLIME_TEXTURE;
    }
}
