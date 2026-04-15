package com.science.gtnl.common.render.entity;

import static net.minecraft.client.renderer.ItemRenderer.renderItemIn2D;

import java.awt.Color;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.loader.ItemLoader;

import Forge.NullPointerException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NullPointerExceptionRender extends Render {

    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(NullPointerException entityNPE, double x, double y, double z, float yaw, float partialTicks) {
        this.bindEntityTexture(entityNPE);
        GL11.glPushMatrix();

        long time = System.currentTimeMillis();
        float hue = (time % 15000L) / 15000.0f;
        int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        GL11.glColor3f(r, g, b);

        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(
            entityNPE.prevRotationYaw + (entityNPE.rotationYaw - entityNPE.prevRotationYaw) * partialTicks - 90.0F,
            0.0F,
            1.0F,
            0.0F);
        GL11.glRotatef(
            entityNPE.prevRotationPitch + (entityNPE.rotationPitch - entityNPE.prevRotationPitch) * partialTicks,
            0.0F,
            0.0F,
            1.0F);
        GL11.glScalef(5f, 5f, 5f);
        GL11.glTranslatef(0f, -0.5f, 0f);
        IIcon icon = Objects
            .requireNonNull(ItemLoader.nullPointerException.getIcon(new ItemStack(ItemLoader.nullPointerException), 0));
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);

        renderItemIn2D(
            Tessellator.instance,
            icon.getMaxU(),
            icon.getMinV(),
            icon.getMinU(),
            icon.getMaxV(),
            icon.getIconWidth(),
            icon.getIconHeight(),
            0.03F);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    public ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return arrowTextures;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRender((NullPointerException) entity, x, y, z, yaw, partialTicks);
    }
}
