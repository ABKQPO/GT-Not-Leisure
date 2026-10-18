package com.science.gtnl.common.render.entity;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.science.gtnl.api.armorflight.ArmorFlightPiece;
import com.science.gtnl.common.armorflight.ArmorFlight;
import com.science.gtnl.common.armorflight.FlightVec;
import com.science.gtnl.common.entity.EntityArmorFlightPiece;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmorFlightPieceRender extends Render {

    private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBiped inner = new ModelBiped(0.5F);
    private final ModelBiped outer = new ModelBiped(1.0F);

    public ArmorFlightPieceRender() {
        shadowSize = 0.15F;
    }

    @Override
    public void doRender(Entity e, double x, double y, double z, float entityYaw, float partialTick) {
        if (!(e instanceof EntityArmorFlightPiece entity)) return;
        ItemStack stack = entity.item();
        boolean ret = entity.isReturn();
        float age = entity.age(partialTick);
        if (stack == null || (ret && (age < 0 || age > entity.duration() + 1))) return;
        if (!ret && age < -ArmorFlight.HATCH_TICKS) return;
        FlightVec at = entity.flightPosition(partialTick);
        Entity target = entity.target();
        float bodyYaw = target != null ? entity.wearerYaw(target, partialTick) : 0;
        float height = target != null ? target.height : 1.8F;
        int flight = entity.duration();
        float latch = entity.latchAge(partialTick);

        float k;
        float thrustPitch = 0, jitterAmp;
        if (ret) {
            k = 1 - ArmorFlight.clamp((age - ArmorFlight.PURGE_TICKS) / 6.0F, 0, 1);
            jitterAmp = 2.5F * (1 - k);
        } else if (age < 0) {
            k = 1;
            jitterAmp = 1.5F;
        } else if (age < flight) {
            double t = age / flight;
            k = (float) ArmorFlight
                .ease(ArmorFlight.clamp((t - ArmorFlight.BRAKE_AT) / (1 - ArmorFlight.BRAKE_AT), 0, 1));
            double accel = t < 0.12 ? 1 - t / 0.12 : 0;
            double brake = t >= ArmorFlight.BRAKE_AT
                ? Math.pow(1 - (t - ArmorFlight.BRAKE_AT) / (1 - ArmorFlight.BRAKE_AT), 1.5)
                : 0;
            thrustPitch = (float) (-20 * accel + 30 * brake);
            jitterAmp = (float) (1.2 + 3.5 * accel + 4.0 * brake);
        } else if (latch < 0) {
            thrustPitch = 30 * (float) Math.exp(-(age - flight) / 2.0);
            k = 1;
            jitterAmp = 1.6F;
        } else {
            k = 1;
            jitterAmp = 0;
        }

        FlightVec v0 = entity.flightPosition(partialTick - 0.5F), v1 = entity.flightPosition(partialTick + 0.5F);
        FlightVec v2 = entity.flightPosition(partialTick + 1.5F);
        FlightVec vel = v1.subtract(v0), vel2 = v2.subtract(v1);
        float travelYaw = vel.horizontalDistanceSqr() > 1.0E-5 ? (float) Math.toDegrees(Math.atan2(-vel.x(), vel.z()))
            : bodyYaw;
        float nextYaw = vel2.horizontalDistanceSqr() > 1.0E-5 ? (float) Math.toDegrees(Math.atan2(-vel2.x(), vel2.z()))
            : travelYaw;
        float pitch = (float) Math.toDegrees(Math.atan2(vel.y(), Math.sqrt(vel.horizontalDistanceSqr()) + 1.0E-4));
        float roll = ArmorFlight.clamp(MathHelper.wrapAngleTo180_float(nextYaw - travelYaw) * 4.5F, -55.0F, 55.0F);
        float buffet = jitterAmp * (MathHelper.sin(age * 2.3F) + 0.5F * MathHelper.sin(age * 5.1F + 1.3F));
        float buffetYaw = jitterAmp * 0.6F * MathHelper.sin(age * 3.7F + 0.4F);

        float yaw = ArmorFlight.rotLerp(k, 180.0F - travelYaw, 180.0F - bodyYaw) + buffetYaw;
        float scale = ret
            ? 1 + 0.08F * (float) Math.sin(Math.min(age, ArmorFlight.PURGE_TICKS) / ArmorFlight.PURGE_TICKS * Math.PI)
            : ArmorFlight.latchScale(latch);
        float wobble = buffet - thrustPitch;

        ArmorFlightPiece piece = entity.piece();
        boolean flash = !ret && latch >= 0 && latch < 3.5F;
        int n = ArmorFlight.segments(piece);
        float[] open = new float[n];
        boolean[] seat = new boolean[n];
        if (ret) {
            Arrays.fill(open, (float) ArmorFlight.burstSpread(age));
        } else if (age >= flight) {
            float unfold = (float) ArmorFlight.unfold(age - flight);
            float sinceSnap = age - entity.snapStart();
            for (int j = 0; j < n; j++) {
                double close = sinceSnap < 0 ? 0 : ArmorFlight.segmentClose(sinceSnap, j, n);
                open[j] = unfold * (float) (1 - close);
                float lockAge = sinceSnap - ArmorFlight.segmentLockTick(j, n);
                seat[j] = lockAge >= 0 && lockAge < 3;
            }
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glTranslated(
            at.x() - RenderManager.renderPosX,
            at.y() - RenderManager.renderPosY,
            at.z() - RenderManager.renderPosZ);
        GL11.glRotatef(yaw, 0, 1, 0);
        GL11.glRotatef((1 - k) * -pitch * 0.8F + wobble, 1, 0, 0);
        GL11.glRotatef((1 - k) * roll + buffet * 0.5F, 0, 0, 1);
        GL11.glScalef(scale, scale, scale);
        boolean drawn = false;
        if (stack.getItem() instanceof ItemArmor armor && armor.armorType == piece.armorType) {
            drawn = renderArmor(stack, armor, piece, height, target, flash, open, seat, age);
        }
        if (!drawn) renderAsItem(stack, target, 0.55F + 0.2F * k);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    private void renderAsItem(ItemStack stack, Entity target, float s) {
        EntityLivingBase holder = target instanceof EntityLivingBase l ? l : Minecraft.getMinecraft().thePlayer;
        if (holder == null) return;
        GL11.glPushMatrix();
        GL11.glScalef(s, s, s);
        GL11.glTranslatef(-0.5F, -0.5F, 0.03125F);
        int passes = stack.getItem()
            .requiresMultipleRenderPasses()
                ? stack.getItem()
                    .getRenderPasses(stack.getItemDamage())
                : 1;
        for (int pass = 0; pass < passes; pass++) {
            RenderManager.instance.itemRenderer.renderItem(holder, stack, pass);
        }
        GL11.glPopMatrix();
    }

    private boolean renderArmor(ItemStack stack, ItemArmor armor, ArmorFlightPiece piece, float height, Entity target,
        boolean flash, float[] open, boolean[] seat, float ticks) {
        EntityLivingBase wearer = target instanceof EntityLivingBase l ? l : Minecraft.getMinecraft().thePlayer;
        if (wearer == null) return false;
        int slot = piece.armorType;
        ModelBiped base = slot == 2 ? inner : outer;
        base.bipedHead.showModel = slot == 0;
        base.bipedHeadwear.showModel = slot == 0;
        base.bipedBody.showModel = slot == 1 || slot == 2;
        base.bipedRightArm.showModel = slot == 1;
        base.bipedLeftArm.showModel = slot == 1;
        base.bipedRightLeg.showModel = slot == 2 || slot == 3;
        base.bipedLeftLeg.showModel = slot == 2 || slot == 3;
        base.isSneak = false;
        base.isChild = false;
        base.isRiding = false;
        base.aimedBow = false;
        base.heldItemLeft = 0;
        base.heldItemRight = 0;
        base.onGround = 0;
        GL11.glPushMatrix();
        try {
            ModelBiped model = ForgeHooksClient.getArmorModel(wearer, stack, slot, base);
            GL11.glTranslated(0, -height * piece.heightFraction, 0);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            GL11.glScalef(0.9375F, 0.9375F, 0.9375F);
            GL11.glTranslatef(0.0F, -24.0F * MODEL_SCALE - 0.0078125F, 0.0F);
            boolean anyOpen = false;
            for (float o : open) anyOpen |= o > 0.001F;
            if (model == base && anyOpen) {
                base.setRotationAngles(0, 0, 0, 0, 0, MODEL_SCALE, wearer);
                ModelRenderer[][] segs = segments(base, slot);
                for (int j = 0; j < segs.length && j < open.length; j++) {
                    ModelRenderer[] parts = segs[j];
                    float[][] saved = new float[parts.length][];
                    for (int i = 0; i < parts.length; i++) {
                        ModelRenderer part = parts[i];
                        saved[i] = new float[] { part.rotationPointX, part.rotationPointY, part.rotationPointZ,
                            part.rotateAngleX, part.rotateAngleY, part.rotateAngleZ };
                        displace(base, part, open[j]);
                    }
                    drawLayers(
                        () -> { for (ModelRenderer part : parts) part.render(MODEL_SCALE); },
                        wearer,
                        stack,
                        armor,
                        slot,
                        flash || seat[j],
                        ticks);
                    for (int i = 0; i < parts.length; i++) {
                        ModelRenderer part = parts[i];
                        part.rotationPointX = saved[i][0];
                        part.rotationPointY = saved[i][1];
                        part.rotationPointZ = saved[i][2];
                        part.rotateAngleX = saved[i][3];
                        part.rotateAngleY = saved[i][4];
                        part.rotateAngleZ = saved[i][5];
                    }
                }
                return true;
            }
            model.onGround = 0;
            model.isRiding = false;
            model.isChild = false;
            drawLayers(
                () -> model.render(wearer, 0, 0, 0, 0, 0, MODEL_SCALE),
                wearer,
                stack,
                armor,
                slot,
                flash,
                ticks);
            return true;
        } catch (RuntimeException ex) {
            return false;
        } finally {
            GL11.glPopMatrix();
        }
    }

    private void drawLayers(Runnable geometry, EntityLivingBase wearer, ItemStack stack, ItemArmor armor, int slot,
        boolean glint, float ticks) {
        bindTexture(RenderBiped.getArmorResource(wearer, stack, slot, null));
        int color = armor.getColor(stack);
        if (color != -1) {
            GL11.glColor3f((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);
            geometry.run();
            bindTexture(RenderBiped.getArmorResource(wearer, stack, slot, "overlay"));
        }
        GL11.glColor4f(1, 1, 1, 1);
        geometry.run();
        if (stack.isItemEnchanted() || glint) renderGlint(geometry, ticks);
    }

    private void renderGlint(Runnable geometry, float ticks) {
        bindTexture(GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDepthMask(false);
        for (int pass = 0; pass < 2; pass++) {
            GL11.glDisable(GL11.GL_LIGHTING);
            float f = 0.76F;
            GL11.glColor4f(0.5F * f, 0.25F * f, 0.8F * f, 1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            float shift = ticks * (0.001F + pass * 0.003F) * 20.0F;
            GL11.glScalef(0.33333334F, 0.33333334F, 0.33333334F);
            GL11.glRotatef(30.0F - pass * 60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0F, shift, 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            geometry.run();
        }
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glDepthMask(true);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    private static ModelRenderer[][] segments(ModelBiped m, int slot) {
        return switch (slot) {
            case 0 -> new ModelRenderer[][] { { m.bipedHead, m.bipedHeadwear } };
            case 1 -> new ModelRenderer[][] { { m.bipedBody }, { m.bipedRightArm }, { m.bipedLeftArm } };
            case 2 -> new ModelRenderer[][] { { m.bipedBody }, { m.bipedRightLeg }, { m.bipedLeftLeg } };
            case 3 -> new ModelRenderer[][] { { m.bipedRightLeg }, { m.bipedLeftLeg } };
            default -> new ModelRenderer[0][];
        };
    }

    private static void displace(ModelBiped m, ModelRenderer part, float o) {
        if (part == m.bipedHead || part == m.bipedHeadwear) {
            part.rotationPointY -= 6 * o;
            part.rotationPointZ += 2 * o;
            part.rotateAngleX -= 0.7F * o;
        } else if (part == m.bipedBody) {
            part.rotationPointZ -= 6 * o;
            part.rotationPointY += 1.5F * o;
            part.rotateAngleX += 0.35F * o;
        } else if (part == m.bipedRightArm) {
            part.rotationPointX -= 7 * o;
            part.rotationPointY -= 2 * o;
            part.rotateAngleZ += 0.95F * o;
            part.rotateAngleX -= 0.3F * o;
        } else if (part == m.bipedLeftArm) {
            part.rotationPointX += 7 * o;
            part.rotationPointY -= 2 * o;
            part.rotateAngleZ -= 0.95F * o;
            part.rotateAngleX -= 0.3F * o;
        } else if (part == m.bipedRightLeg) {
            part.rotationPointX -= 5 * o;
            part.rotationPointY += 2.5F * o;
            part.rotationPointZ -= 2 * o;
            part.rotateAngleZ += 0.4F * o;
        } else if (part == m.bipedLeftLeg) {
            part.rotationPointX += 5 * o;
            part.rotationPointY += 2.5F * o;
            part.rotationPointZ -= 2 * o;
            part.rotateAngleZ -= 0.4F * o;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }
}
