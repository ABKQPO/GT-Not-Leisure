package com.science.gtnl.common.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.client.IItemRenderer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tectech.rendering.EOH.EOHRenderingUtils;

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
        Matrix4f model = new Matrix4f()
            .translate((float) (x + pos.posX + 0.5), (float) (y + pos.posY + 0.5), (float) (z + pos.posZ + 0.5));
        EntityLivingBase player = Minecraft.getMinecraft().renderViewEntity;
        EOHRenderingUtils.renderOuterSpaceShell(model, player.getDistance(x + pos.posX, y + pos.posY, z + pos.posZ));

        // Render the planets.
        renderOrbitObjects(machine, model);

        // Render the star itself.
        EOHRenderingUtils.renderEOHStar(model, IItemRenderer.ItemRenderType.INVENTORY, partialTicks, 2);
        GL11.glPopAttrib();

        GL11.glPopMatrix();
    }

    private static void renderOrbitObjects(EyeOfHarmonyInjector machine, Matrix4fc model) {
        if (machine.orbitingObjects != null) {

            if (machine.orbitingObjects.isEmpty()) {
                machine.generateImportantInfo();
            }

            EOHRenderingUtils.renderOrbits(model, machine.orbitingObjects, machine.angle, 2, 0.1f, STAR_RESCALE);
        }
    }

}
