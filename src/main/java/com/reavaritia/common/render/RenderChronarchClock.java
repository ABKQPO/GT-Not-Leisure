package com.reavaritia.common.render;

import static com.science.gtnl.ScienceNotLeisure.*;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChronarchClock extends Render {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/ChronarchsClock.png");
    private static final ResourceLocation MODEL = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "model/ChronarchsClock.obj");

    private static final IModelCustom model = AdvancedModelLoader.loadModel(MODEL);

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);

        float time = entity.ticksExisted + partialTicks;

        float bob = (float) Math.sin(time * 0.05F);
        bob = bob * bob * 0.15F;
        GL11.glTranslatef(0.0F, bob, 0.0F);

        float rotation = (time * 1.0F) % 360.0F;
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

        bindTexture(TEXTURE);
        model.renderAll();

        GL11.glPopMatrix();
    }
}
