package com.science.gtnl.common.render.tile;

import static tectech.Reference.*;
import static tectech.rendering.EOH.EOHRenderingUtils.*;
import static tectech.rendering.EOH.EOHRenderingUtils.renderBlockInWorld;
import static tectech.rendering.EOH.EOHRenderingUtils.renderStar;
import static tectech.rendering.EOH.EOHTileEntitySR.*;
import static tectech.thing.block.TileEntityEyeOfHarmony.*;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tectech.thing.block.TileEntityEyeOfHarmony;

@SideOnly(Side.CLIENT)
public class EyeOfHarmonyInjectorRenderer {

    public static float STAR_RESCALE = 1f;

    public static void renderTileEntity(EyeOfHarmonyInjector machine, double x, double y, double z,
        float partialTicks) {
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        ChunkCoordinates pos = machine.getRenderPos();
        GL11.glTranslated(pos.posX, pos.posY, pos.posZ);
        GL11.glTranslated(0.5, 0.5, 0.5);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        // Space shell.
        renderOuterSpaceShell();

        // Render the planets.
        renderOrbitObjects(machine);

        // Render the star itself.
        renderStar(IItemRenderer.ItemRenderType.INVENTORY, 2);
        GL11.glPopAttrib();

        GL11.glPopMatrix();
    }

    public static void renderOrbitObjects(EyeOfHarmonyInjector machine) {
        if (machine.orbitingObjects != null) {

            if (machine.orbitingObjects.isEmpty()) {
                machine.generateImportantInfo();
            }

            for (TileEntityEyeOfHarmony.OrbitingObject t : machine.orbitingObjects) {
                renderOrbit(machine, t);
            }
        }
    }

    public static void renderOrbit(EyeOfHarmonyInjector machine, TileEntityEyeOfHarmony.OrbitingObject orbitingObject) {
        // Render orbiting body.
        GL11.glPushMatrix();

        GL11.glRotatef(orbitingObject.zAngle, 0, 0, 1);
        GL11.glRotatef(orbitingObject.xAngle, 1, 0, 0);
        GL11.glRotatef((orbitingObject.rotationSpeed * 0.1f * machine.angle) % 360.0f, 0F, 1F, 0F);
        GL11.glTranslated(-0.5 - orbitingObject.distance - STAR_RESCALE, 0, 0);
        GL11.glRotatef((orbitingObject.orbitSpeed * 0.1f * machine.angle) % 360.0f, 0F, 1F, 0F);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        renderBlockInWorld(orbitingObject.block, 0, orbitingObject.scale);

        GL11.glPopMatrix();
    }

    public static void renderOuterSpaceShell() {

        // Save current OpenGL state.
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Begin animation.
        GL11.glPushMatrix();

        // Disables lighting, so star is always lit.
        GL11.glDisable(GL11.GL_LIGHTING);
        // Merges colors of the various layers of the star.
        // GL11.glEnable(GL11.GL_BLEND);

        // Bind animation to layer of star.
        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(new ResourceLocation(MODID, "models/spaceLayer.png"));

        float scale = 0.01f * 29f;
        // Scale the star up in the x, y and z directions.
        GL11.glScalef(scale, scale, scale);

        GL11.glColor4f(1, 1, 1, 1);

        spaceModel.renderAll();

        // Finish animation.
        GL11.glPopMatrix();

        // Restore previous OpenGL state.
        GL11.glPopAttrib();
    }

}
