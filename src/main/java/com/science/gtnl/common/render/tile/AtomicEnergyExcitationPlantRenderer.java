package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.science.gtnl.common.machine.multiblock.AtomicEnergyExcitationPlant;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AtomicEnergyExcitationPlantRenderer {

    public static ResourceLocation modelTexture = new ResourceLocation(
        References.MODID.toLowerCase(),
        "textures/models/stabilizer_sphere.png");
    public static IModelCustom stabilizerSphereModel = AdvancedModelLoader
        .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/stabilizer_sphere.obj"));

    public static void renderTileEntity(AtomicEnergyExcitationPlant machine, double x, double y, double z,
        float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        ChunkCoordinates pos = machine.getRenderPos();
        GL11.glTranslated(pos.posX, pos.posY, pos.posZ);
        GL11.glColor4f(0.0F, 2.0F, 0.0F, 1F);
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glScalef(0.4F, 0.4F, 0.4F);

        float red = 200 / 255F;
        float green = 200 / 255F;
        float blue = 200 / 255F;
        GL11.glColor4f(red, green, blue, 1F);
        GL11.glScalef(10, 10, 10);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(modelTexture);

        GL11.glRotatef(machine.rotation, 0F, 1F, 0F);
        stabilizerSphereModel.renderAll();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glRotatef(machine.rotation * 2, 0F, -1F, 0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glColor4f(0.0F, 1.0F, 1.0F, 0.5F);
        GL11.glColor4f(red, green, blue, 0.5F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glScalef(1.3F, 1.3F, 1.3F);
        stabilizerSphereModel.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
    }
}
