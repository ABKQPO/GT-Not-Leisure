package com.science.gtnl.common.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnllib.client.fx.world.FxMuzzleAnchor;
import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ItemGunRender implements IItemRenderer {

    protected static final float PI = (float) Math.PI, HAND_SCALE = 0.72F;
    private static final float FULL_BRIGHT = 240F;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (type) {
            case ENTITY -> helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.ENTITY_BOBBING;
            case EQUIPPED, EQUIPPED_FIRST_PERSON -> helper == ItemRendererHelper.EQUIPPED_BLOCK;
            default -> false;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        switch (type) {
            case EQUIPPED -> thirdPerson(data);
            case EQUIPPED_FIRST_PERSON -> firstPerson();
            case ENTITY -> ground();
            case INVENTORY -> inventory();
            default -> {}
        }
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (type == ItemRenderType.INVENTORY) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        draw(type, item, data);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    protected void thirdPerson(Object[] data) {
        holdInRightHand();
    }

    /**
     * Moves the model from the item's origin to where it sits in the first person view.
     */
    protected abstract void firstPerson();

    /**
     * Moves the model from the item's origin to where it lies as a dropped item.
     */
    protected abstract void ground();

    /**
     * Moves the model from the item's origin into its inventory slot.
     */
    protected abstract void inventory();

    /**
     * Draws the model after the view transform and the solid model state are set; the matrix and the enable,
     * lighting, color and depth state are restored afterwards.
     */
    protected abstract void draw(ItemRenderType type, ItemStack item, Object[] data);

    protected static Entity holder(Object[] data) {
        for (Object datum : data) if (datum instanceof Entity entity) return entity;
        return null;
    }

    /**
     * Records where the muzzle is while the gun is drawn, so shots and muzzle effects start at the barrel the
     * player sees; {@code muzzle} is in the model's own coordinates.
     */
    protected static void anchorMuzzle(ItemRenderType type, Object[] data, Vec3d muzzle) {
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) FxMuzzleAnchor.captureFirstPerson(muzzle);
        else if (type == ItemRenderType.EQUIPPED) {
            Entity holder = holder(data);
            if (holder != null) FxMuzzleAnchor.capture(holder.getEntityId(), muzzle);
        }
    }

    protected static void holdInRightHand() {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(1.0F / 0.375F, 1.0F / 0.375F, 1.0F / 0.375F);
        GL11.glTranslatef(-0.25F + 0.0625F, -0.1875F - 0.4375F, 0.1875F - 0.0625F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0625F, 0.125F, -0.625F);
        GL11.glTranslatef(0.0F, 1.5F / 16.0F, -3.5F / 16.0F);
        GL11.glScalef(0.6F, 0.6F, 0.6F);
    }

    protected static float swingIntoView() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        float swing = player.getSwingProgress(minecraft.timer.renderPartialTicks);
        float swingSq = MathHelper.sin(swing * swing * PI);
        float swingRoot = MathHelper.sin(MathHelper.sqrt_float(swing) * PI);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(2.5F, 2.5F, 2.5F);
        GL11.glRotatef(swingRoot * 80.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(swingRoot * 20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(swingSq * 20.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        return swing;
    }

    protected static void tiltInHand() {
        GL11.glRotatef(-4.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-3.0F, 0.0F, 0.0F, 1.0F);
    }

    protected static void fullBright(Runnable draw) {
        float lastBrightnessX = OpenGlHelper.lastBrightnessX, lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, FULL_BRIGHT, FULL_BRIGHT);
        try {
            draw.run();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }
}
