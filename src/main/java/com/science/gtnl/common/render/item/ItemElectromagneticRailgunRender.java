package com.science.gtnl.common.render.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.world.FxMuzzleAnchor;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.railgun.RailgunBolts;
import com.science.gtnl.client.railgun.RailgunBolts.Bolt;
import com.science.gtnl.client.railgun.RailgunCameraShake;
import com.science.gtnl.client.railgun.RailgunCameraShake.Axis;
import com.science.gtnl.client.railgun.RailgunCharge;
import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.client.railgun.RailgunOrbRenderer;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunSetting;
import com.science.gtnl.config.ClientConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemElectromagneticRailgunRender extends ItemGunRender {

    private static final String ROOT = RESOURCE_ROOT_ID + ":models/electromagnetic_railgun/";
    private static final float WEAPON_ROLL_DEGREES = 3.2F;
    private static final float WEAPON_PITCH_DEGREES = 2.4F;
    private static final float WEAPON_DRIFT = 0.01F;
    private static final float BOLT_GLOW_MIX = 0.3F, BOLT_GLOW_ALPHA = 0.5F, BOLT_CORE_MIX = 0.85F;
    private static final double BOLT_GLOW_WIDTH = 2.5;
    private static final Vec3d AXIS_X = new Vec3d(1, 0, 0), AXIS_Y = new Vec3d(0, 1, 0);
    private static final float[] BOLT_COLOR = new float[3];

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
    protected void draw(ItemRenderType type, ItemStack item, Object[] data) {
        renderModel(RailgunSetting.EHV_BEAM.active(item));
        if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            renderBodyBolts(holder(data));
    }

    @Override
    protected void thirdPerson(Object[] data) {
        holdInRightHand();
        anchorMuzzle(ItemRenderType.EQUIPPED, data, RailgunBolts.MUZZLE);
    }

    public static boolean keepsRaised(ItemRenderer renderer, ItemRenderer mainHand, EntityPlayer player) {
        return renderer != null && renderer == mainHand
            && ElectromagneticRailgun.isHeld(player)
            && player.getItemInUse() == player.getHeldItem();
    }

    private static void renderBodyBolts(Entity holder) {
        if (!ClientConfig.enableRailgunBloom || !(holder instanceof EntityPlayer player)) return;
        if (!RailgunCharge.charging(player)) return;
        if (!RailgunClientFx.visible(player.getEntityId(), Vec3d.pos(player))) return;
        int tier = RailgunCharge.tier(player)
            .ordinal();
        double age = RailgunCharge.age(player, Minecraft.getMinecraft().timer.renderPartialTicks);
        List<Bolt> bolts = RailgunBolts.body(player.getEntityId(), age, RailgunCharge.progress(player));
        if (bolts.isEmpty()) return;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDepthMask(false);
            fullBright(() -> {
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                for (Bolt bolt : bolts) {
                    boltStrip(
                        tessellator,
                        bolt,
                        tier,
                        BOLT_GLOW_MIX,
                        bolt.alpha() * BOLT_GLOW_ALPHA,
                        bolt.width() * BOLT_GLOW_WIDTH);
                    boltStrip(tessellator, bolt, tier, BOLT_CORE_MIX, bolt.alpha(), bolt.width());
                }
                tessellator.draw();
            });
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void boltStrip(Tessellator tessellator, Bolt bolt, int tier, float mix, float alpha, double width) {
        float[] rgb = RailgunOrbRenderer.color(tier, mix, BOLT_COLOR);
        tessellator.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
        List<Vec3d> points = bolt.points();
        for (int i = 1; i < points.size(); i++) {
            Vec3d from = points.get(i - 1), to = points.get(i);
            Vec3d direction = to.subtract(from)
                .normalize();
            Vec3d first = direction.cross(Math.abs(direction.y()) > 0.95 ? AXIS_X : AXIS_Y)
                .normalize()
                .scale(width);
            Vec3d second = direction.cross(first)
                .normalize()
                .scale(width);
            boltQuad(tessellator, from, to, first);
            boltQuad(tessellator, from, to, second);
        }
    }

    private static void boltQuad(Tessellator tessellator, Vec3d from, Vec3d to, Vec3d offset) {
        boltVertex(tessellator, from.subtract(offset));
        boltVertex(tessellator, from.add(offset));
        boltVertex(tessellator, to.add(offset));
        boltVertex(tessellator, to.subtract(offset));
    }

    private static void boltVertex(Tessellator tessellator, Vec3d point) {
        tessellator.addVertex(point.x(), point.y(), point.z());
    }

    private void renderModel(boolean extremeHighVoltage) {
        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(baseTexture);
        model.renderPart("base");
        fullBright(() -> {
            textureManager.bindTexture(extremeHighVoltage ? modeExtremeHighVoltage : modeHighVoltage);
            model.renderPart("mode");
            textureManager.bindTexture(extremeHighVoltage ? lightExtremeHighVoltage : lightHighVoltage);
            model.renderPart("light");
        });
    }

    @Override
    protected void firstPerson() {
        Minecraft minecraft = Minecraft.getMinecraft();
        float swing = swingIntoView();
        if (minecraft.thePlayer.getItemInUseCount() <= 0) {
            GL11.glTranslatef(
                MathHelper.sin(MathHelper.sqrt_float(swing) * PI) * 0.4F,
                -MathHelper.sin(MathHelper.sqrt_float(swing) * PI * 2.0F) * 0.2F,
                MathHelper.sin(swing * PI) * 0.2F);
        }
        GL11.glTranslatef(0.48F - 0.56F, -0.49F + 0.52F, -0.98F + 0.72F);
        tiltInHand();
        applyChargingShake(minecraft.timer.renderPartialTicks);
        GL11.glScalef(HAND_SCALE, HAND_SCALE, HAND_SCALE);
        FxMuzzleAnchor.captureFirstPerson(RailgunBolts.MUZZLE);
    }

    private static void applyChargingShake(float partialTicks) {
        float roll = RailgunCameraShake.tremor(Axis.ROLL, partialTicks);
        float pitch = RailgunCameraShake.tremor(Axis.PITCH, partialTicks);
        if (Math.abs(roll) < 1.0E-4F && Math.abs(pitch) < 1.0E-4F) return;
        GL11.glRotatef(roll * WEAPON_ROLL_DEGREES, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(pitch * WEAPON_PITCH_DEGREES, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, pitch * WEAPON_DRIFT, roll * WEAPON_DRIFT);
    }

    @Override
    protected void ground() {
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, 0.25F * 0.4F, 0.0F);

        GL11.glTranslatef(0.0F, 1.0F / 16.0F, 0.0F);
        GL11.glScalef(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected void inventory() {
        GL11.glTranslatef(8.0F, 8.0F, 0.0F);
        GL11.glScalef(16.0F, -16.0F, 16.0F);

        GL11.glTranslatef(-1.5F / 16.0F, 0.0F, 0.0F);
        GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
    }
}
