package com.science.gtnl.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.gtnllib.text.TextEffectColors;
import com.science.gtnl.utils.ColorUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityParticleBeam extends EntityFX {

    public double startX, startY, startZ;
    public double endX, endY, endZ;
    public double radius;

    public float renderSpeed = 1f;

    public EntityParticleBeam(double x, double y, double z, double masterX, double masterY, double masterZ,
        double radius) {
        super(Minecraft.getMinecraft().theWorld, x, y, z, 0.0, 0.0, 0.0);
        this.startX = x;
        this.startY = y;
        this.startZ = z;

        this.endX = masterX;
        this.endY = masterY;
        this.endZ = masterZ;

        this.radius = radius;

        this.particleRed = 1F;
        this.particleGreen = 1F;
        this.particleBlue = 1F;
        this.noClip = true;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.particleMaxAge = 4;
        this.setSize(1F, 1F);
    }

    public void update() {
        if (this.particleMaxAge - this.particleAge < 4) {
            this.particleMaxAge = this.particleAge + 4;
        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTick, float rotX, float rotXZ, float rotZ,
        float rotYZ, float rotXY) {
        tessellator.draw();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        double shiftX = startX - interpPosX;
        double shiftY = startY - interpPosY;
        double shiftZ = startZ - interpPosZ;
        GL11.glTranslated(shiftX, shiftY, shiftZ);

        double offsetX = endX - startX;
        double offsetY = endY - startY;
        double offsetZ = endZ - startZ;
        double offsetLength = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
        double diagonalLength = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);

        GL11.glRotated(Math.toDegrees(-Math.atan2(offsetZ, offsetX)) - 90.0, 0.0, 1.0, 0.0);
        GL11.glRotated(Math.toDegrees(-Math.atan2(diagonalLength, offsetY)) - 90.0, 1.0, 0.0, 0.0);

        renderStabilizerEffect(tessellator, offsetLength, partialTick);

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        ResourceHandler.bindDefaultParticles();
        tessellator.startDrawingQuads();
    }

    public void renderStabilizerEffect(Tessellator tessellator, double offsetLength, float partialTick) {
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // Draw Beams
        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 0.0, -0.35);
        ResourceHandler.bindResource("textures/particle/reactorBeam.png");
        drawBeam(
            tessellator,
            1.0,
            0.355,
            0.8,
            offsetLength,
            partialTick,
            true,
            false,
            ColorUtils.rgb(TextEffectColors.effectCyan));
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 0.0, 0.45);
        double coreSize = radius * 0.9;
        double scale = 0.355;
        drawBeam(
            tessellator,
            scale / coreSize,
            coreSize,
            offsetLength - radius * 2 / 2.5,
            offsetLength,
            partialTick,
            false,
            false,
            ColorUtils.rgb(TextEffectColors.effectCyan));
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 0.0, -0.35);
        drawBeam(
            tessellator,
            1.0,
            0.263,
            0.8,
            offsetLength,
            partialTick,
            true,
            true,
            ColorUtils.rgb(ColorUtils.particleOrange));
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 0.0, 0.45);
        coreSize = radius * 0.4;
        scale = 0.263;
        drawBeam(

            tessellator,
            scale / coreSize,
            coreSize,
            offsetLength - 0.5,
            offsetLength,
            partialTick,
            false,
            true,
            ColorUtils.rgb(ColorUtils.particleOrange));
        GL11.glPopMatrix();

        GL11.glShadeModel(GL11.GL_FLAT);
    }

    public void drawBeam(Tessellator tessellator, double sizeOrigin, double sizeTarget, double length,
        double offsetLength, float partialTick, boolean reverseTransparency, boolean reverseDirection, int color) {
        final double speed = 3.0;
        final double tickOffset = ((double) particleAge + partialTick) * (reverseDirection ? -0.01 : 0.01) * speed;

        double texV1 = offsetLength / 32.0 + tickOffset;
        double texV2 = -0.1 + tickOffset;

        tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
        tessellator.setBrightness(200);

        final int raysCount = 16;
        for (int ray = 0; ray <= raysCount; ++ray) {
            double texU = (double) ray / raysCount;
            double verX = Math.sin(texU * Math.PI * 2.13325) * sizeTarget;
            double verY = Math.cos(texU * Math.PI * 2.13325) * sizeTarget;

            tessellator.setColorRGBA(
                (color & 0xFF0000) >> 16,
                (color & 0xFF00) >> 8,
                color & 0xFF,
                reverseTransparency ? 0 : (int) (255.0 * renderSpeed));
            tessellator.addVertexWithUV(verX * sizeOrigin, verY * sizeOrigin, 0.0, texU, texV1);

            tessellator.setColorRGBA(
                (color & 0xFF0000) >> 16,
                (color & 0xFF00) >> 8,
                color & 0xFF,
                reverseTransparency ? (int) (255.0 * renderSpeed) : 0);
            tessellator.addVertexWithUV(verX, verY, length, texU, texV2);
        }
        tessellator.draw();
    }
}
