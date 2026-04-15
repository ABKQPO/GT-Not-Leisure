package com.science.gtnl.common.render.item;

import static net.minecraft.client.renderer.ItemRenderer.renderItemIn2D;

import java.awt.Color;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemNullPointerExceptionRender implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        renderItem(type, item, false);
    }

    public static void renderItem(ItemRenderType type, ItemStack item, boolean renderInFrame) {
        GL11.glPushMatrix();

        long time = System.currentTimeMillis();
        float hue = (time % 15000L) / 15000.0f;
        int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        GL11.glColor3f(r, g, b);

        float scaleAnim = 1.0f + 0.1f * (float) Math.sin((time % 500L) / 500.0f * 2 * Math.PI);

        if (type == ItemRenderType.INVENTORY) {
            GL11.glScalef(80f * scaleAnim, 80f * scaleAnim, 80f * scaleAnim);
            GL11.glScalef(-1f, -1f, 1f);
            GL11.glRotatef(-45f, 0f, 0f, 1f);
            GL11.glTranslatef(-0.85f, -0.65f, 0f);
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(10f * scaleAnim, 10f * scaleAnim, 10f * scaleAnim);
            GL11.glRotatef(45f, 0f, 0f, 1f);
            GL11.glTranslatef(0.01f, -0.5f, 0f);
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glRotatef(110f, 0f, 0f, 1f);
            GL11.glRotatef(-80f, 1f, 0f, 0f);
            GL11.glScalef(20f * scaleAnim, 20f * scaleAnim, 20f * scaleAnim);
            GL11.glTranslatef(-0.08f, -0.53f, 0f);
        } else if (type == ItemRenderType.ENTITY) {
            GL11.glScalef(1f, -1f, 1f);
            GL11.glRotatef(110f, 0f, 0f, 0f);
            GL11.glScalef(50f * scaleAnim, 50f * scaleAnim, 50f * scaleAnim);
            if (!renderInFrame) {
                float rotation = (time % 6000L) / 6000.0f * 360.0f;
                GL11.glRotatef(rotation, 0f, 1f, 0f);
            }
            GL11.glTranslatef(-0.5f, -0.5f, 0f);
        }

        IIcon icon = Objects.requireNonNull(item.getItem())
            .getIcon(item, 0);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);

        if (icon != null) {
            renderItemIn2D(
                Tessellator.instance,
                icon.getMaxU(),
                icon.getMinV(),
                icon.getMinU(),
                icon.getMaxV(),
                icon.getIconWidth(),
                icon.getIconHeight(),
                0.03F);
        }

        GL11.glPopMatrix();
    }
}
