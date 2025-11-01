package com.science.gtnl.common.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;

@SideOnly(Side.CLIENT)
public class SteamRocketRender extends Render {

    private final ResourceLocation spaceshipTexture;

    public ModelBase modelSpaceship;
    public IModelCustom modelSpaceshipObj;

    public SteamRocketRender(ModelBase spaceshipModel, String textureDomain, String texture) {
        this(new ResourceLocation(textureDomain, "textures/model/" + texture + ".png"));
        this.modelSpaceship = spaceshipModel;
    }

    private SteamRocketRender(ResourceLocation texture) {
        this.spaceshipTexture = texture;
        this.shadowSize = 2F;
    }

    public ResourceLocation resourceLocation() {
        return this.spaceshipTexture;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.resourceLocation();
    }

    public void renderSpaceship(EntitySpaceshipBase entity, double par2, double par4, double par6, float par8,
        float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        final float var25 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9;

        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glRotatef(180.0F - par8, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-var25, 0.0F, 1.0F, 0.0F);
        final float var28 = entity.rollAmplitude - par9;

        if (var28 > 0.0F) {
            final float i = entity.getLaunched()
                ? (5 - MathHelper.floor_double((double) entity.timeUntilLaunch / 85)) / 10F
                : 0.3F;
            GL11.glRotatef(MathHelper.sin(var28) * var28 * i * par9, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.sin(var28) * var28 * i * par9, 1.0F, 0.0F, 1.0F);
        }

        this.bindEntityTexture(entity);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);

        if (this.modelSpaceshipObj != null) {
            this.modelSpaceshipObj.renderAll();
        } else {
            this.modelSpaceship.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderSpaceship((EntitySpaceshipBase) par1Entity, par2, par4, par6, par8, par9);
    }
}
