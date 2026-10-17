package com.science.gtnl.common.render.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.science.gtnl.client.railgun.RailgunCameraShake;
import com.science.gtnl.client.railgun.RailgunCameraShake.Axis;
import com.science.gtnl.client.railgun.RailgunMuzzleAnchor;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemElectromagneticRailgunRender implements IItemRenderer {

    private static final String ROOT = RESOURCE_ROOT_ID + ":models/electromagnetic_railgun/";
    private static final float PI = (float) Math.PI;
    private static final float WEAPON_ROLL_DEGREES = 3.2F;
    private static final float WEAPON_PITCH_DEGREES = 2.4F;
    private static final float WEAPON_DRIFT = 0.01F;

    private final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(ROOT + "electromagnetic_railgun.obj"));
    private final ResourceLocation baseTexture = new ResourceLocation(ROOT + "base.png");
    private final ResourceLocation modeHighVoltage = new ResourceLocation(ROOT + "mode_high_voltage.png");
    private final ResourceLocation modeExtremeHighVoltage = new ResourceLocation(
        ROOT + "mode_extreme_high_voltage.png");
    private final ResourceLocation lightHighVoltage = new ResourceLocation(ROOT + "light_high_voltage.png");
    private final ResourceLocation lightExtremeHighVoltage = new ResourceLocation(
        ROOT + "light_extreme_high_voltage.png");

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
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_COLOR_BUFFER_BIT);

        switch (type) {
            case EQUIPPED -> {
                applyThirdPerson();
                anchorMuzzle(data);
            }
            case EQUIPPED_FIRST_PERSON -> applyFirstPerson();
            case ENTITY -> applyGround();
            case INVENTORY -> applyGui();
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

        renderModel(ElectromagneticRailgun.isExtremeHighVoltage(item));

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void anchorMuzzle(Object[] data) {
        if (data.length < 2 || !(data[1] instanceof Entity entity)) return;
        RailgunMuzzleAnchor.capture(entity.getEntityId());
    }

    private void renderModel(boolean extremeHighVoltage) {
        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(baseTexture);
        model.renderPart("base");

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        textureManager.bindTexture(extremeHighVoltage ? modeExtremeHighVoltage : modeHighVoltage);
        model.renderPart("mode");
        textureManager.bindTexture(extremeHighVoltage ? lightExtremeHighVoltage : lightHighVoltage);
        model.renderPart("light");

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }

    private static void applyThirdPerson() {
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

    private static void applyFirstPerson() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        float partialTicks = mc.timer.renderPartialTicks;
        float swing = player.getSwingProgress(partialTicks);
        float swingSq = MathHelper.sin(swing * swing * PI);
        float swingRoot = MathHelper.sin(MathHelper.sqrt_float(swing) * PI);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(2.5F, 2.5F, 2.5F);
        GL11.glRotatef(swingRoot * 80.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(swingRoot * 20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(swingSq * 20.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        if (player.getItemInUseCount() <= 0) {
            GL11.glTranslatef(
                swingRoot * 0.4F,
                -MathHelper.sin(MathHelper.sqrt_float(swing) * PI * 2.0F) * 0.2F,
                MathHelper.sin(swing * PI) * 0.2F);
        }
        GL11.glTranslatef(0.48F - 0.56F, -0.49F + 0.52F, -0.98F + 0.72F);

        GL11.glRotatef(-4.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-3.0F, 0.0F, 0.0F, 1.0F);

        applyChargingShake(partialTicks);

        GL11.glScalef(0.72F, 0.72F, 0.72F);
    }

    private static void applyChargingShake(float partialTicks) {
        float roll = RailgunCameraShake.tremor(Axis.ROLL, partialTicks);
        float pitch = RailgunCameraShake.tremor(Axis.PITCH, partialTicks);
        if (Math.abs(roll) < 1.0E-4F && Math.abs(pitch) < 1.0E-4F) return;
        GL11.glRotatef(roll * WEAPON_ROLL_DEGREES, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(pitch * WEAPON_PITCH_DEGREES, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, pitch * WEAPON_DRIFT, roll * WEAPON_DRIFT);
    }

    private static void applyGround() {
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, 0.25F * 0.4F, 0.0F);

        GL11.glTranslatef(0.0F, 1.0F / 16.0F, 0.0F);
        GL11.glScalef(0.4F, 0.4F, 0.4F);
    }

    private static void applyGui() {
        GL11.glTranslatef(8.0F, 8.0F, 0.0F);
        GL11.glScalef(16.0F, -16.0F, 16.0F);

        GL11.glTranslatef(-1.5F / 16.0F, 0.0F, 0.0F);
        GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
    }
}
