package com.science.gtnl.common.render.entity;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.entity.EntityElainaBroom;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.render.model.MajoBroomModel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MajoBroomRender extends Render {

    public static final ResourceLocation TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID,
        "textures/entity/majo_broom.png");
    private static final ResourceLocation ELAINA_TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID,
        "textures/entity/elaina_broom.png");

    public MajoBroomRender() {
        shadowSize = 0.4F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + EntityMajoBroom.MODEL_LIFT, z);
        GL11.glRotatef(180.0F - yaw, 0, 1, 0);
        if (entity instanceof EntityMajoBroom broom) {
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            GL11.glRotatef(broom.getVisualPitch(partialTicks), 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.2F, 0.0F);
        }
        bindEntityTexture(entity);
        if (entity instanceof EntityElainaBroom) {
            MajoBroomModel.ELAINA_INSTANCE.render();
        } else {
            MajoBroomModel.INSTANCE.render();
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return entity instanceof EntityElainaBroom ? ELAINA_TEXTURE : TEXTURE;
    }
}
