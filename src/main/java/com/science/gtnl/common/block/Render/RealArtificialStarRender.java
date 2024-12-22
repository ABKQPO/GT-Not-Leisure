package com.science.gtnl.common.block.Render;

import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RealArtificialStarRender extends TileEntitySpecialRenderer {

    public static final ResourceLocation STARTEXTURE1 = new ResourceLocation(
        "sciencenotleisure:model/ArtificialStarRender.png");
    public static final IModelCustom MODEL1 = AdvancedModelLoader
        .loadModel(new ResourceLocation("sciencenotleisure:model/ArtificialStarRender.obj"));

    public RealArtificialStarRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileStar.class, this);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (!(tile instanceof TileStar star)) return;
        final double size = star.size;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glRotated(star.Rotation, 1, 1, 1);
        renderStar(star, size);
        GL11.glPopMatrix();
    }

    public void renderStar(TileStar tileStar, double size) {
        List<IModelCustom> models = tileStar.getModels();
        for (int i = 0; i < models.size(); i++) {
            IModelCustom model = models.get(i);
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.bindTexture(tileStar.getTexture(i));
            GL11.glScaled(size, size, size);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            model.renderAll();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
