package com.science.gtnl.client.celestweave;

import java.awt.Color;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnllib.client.fx.FxAngle;
import com.science.gtnllib.client.fx.FxColor;
import com.science.gtnllib.client.fx.FxEasing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveAnimator {

    public static final float[][] BLADES = { { 5F, -0.3F, 13F, 4F }, { 4F, 0F, 12F, 4F }, { 3F, 0.3F, 11F, 4F },
        { 2F, 0.6F, 10F, 4F }, { 1F, 0.9F, 9F, 4F } };
    public static final float BLADE_GAP = 1F, FORE_LENGTH = 5F;
    static final int WING_PARTS = BLADES.length + 3;
    static final long WING_RETRACT_MS = 600;
    private static final Random RNG = new Random();

    private static int rotor, innerRotor, crystal, nucleus, shellA, swirl, swirlPane, halo, holoScreen, packBand,
        visorEyes;
    private static final int[] ROTOR_SEG = new int[8], INNER_SEG = new int[3], BLADE_L = new int[5],
        BLADE_R = new int[5], NOZZLE = new int[2];
    private static int sparL, sparR, foreL, foreR, fieldL, fieldR;
    private static CelestweaveFrames bound;

    private CelestweaveAnimator() {}

    static void bind(CelestweaveFrames frames) {
        bound = frames;
        rotor = idx("core.rotor");
        innerRotor = idx("core.innerRotor");
        crystal = idx("core.crystal");
        nucleus = idx("core.nucleus");
        shellA = idx("core.shellA");
        swirl = idx("core.swirl");
        swirlPane = idx("core.swirlPane");
        halo = idx("core.halo");
        holoScreen = idx("holo.screen");
        packBand = idx("pack.band");
        visorEyes = idx("visor.eyes");
        fill(ROTOR_SEG, "core.rotorSeg");
        fill(INNER_SEG, "core.innerSeg");
        fill(BLADE_L, "wing.blade.L");
        fill(BLADE_R, "wing.blade.R");
        sparL = idx("wing.spar.L");
        sparR = idx("wing.spar.R");
        foreL = idx("wing.fore.L");
        foreR = idx("wing.fore.R");
        fieldL = idx("wing.field.L");
        fieldR = idx("wing.field.R");
        fill(NOZZLE, "boots.heelNozzle");
    }

    private static int idx(String name) {
        CelestweaveFrames.Frame frame = bound.frame(name);
        return frame == null ? -1 : frame.index;
    }

    private static void fill(int[] target, String prefix) {
        for (int i = 0; i < target.length; i++) target[i] = idx(prefix + i);
    }

    public static float seconds(EntityLivingBase entity) {
        return (entity.ticksExisted + Minecraft.getMinecraft().timer.renderPartialTicks) / 20F;
    }

    static CelestweavePose pose(EntityLivingBase entity, CelestweaveClientState state, CelestweaveLook look,
        CelestweaveFrames frames) {
        int size = frames.frames.size();
        long frame = CelestweaveClientEvents.renderFrame();
        if (state.pose != null && state.pose.size() == size && state.poseFrame == frame) return state.pose;
        if (state.pose == null || state.pose.size() != size) state.pose = new CelestweavePose(size);
        long nanos = System.nanoTime();
        float dt = state.poseFrame < 0 ? 0F : Math.max(0F, Math.min(0.05F, (nanos - state.lastNanos) / 1.0E9F));
        state.lastNanos = nanos;
        state.poseFrame = frame;
        apply(state.pose, look, state, Minecraft.getSystemTime(), seconds(entity), dt);
        return state.pose;
    }

    private static void apply(CelestweavePose pose, CelestweaveLook look, CelestweaveClientState state, long now,
        float seconds, float dt) {
        pose.reset();
        int tier = look.tier, glowColor = look.glowColor;
        float fraction = look.fraction;
        float glow = state.hitGlow(now);
        float baseBrightness = tier == 0 ? 0.3F : 0.4F + 0.6F * fraction;
        if (tier > 0 && fraction < 0.1F && RNG.nextFloat() < 0.18F) baseBrightness *= 0.25F;
        float brightness = baseBrightness * (0.8F + 0.2F * MathHelper.sin(seconds * (1.5F + 1.2F * tier)))
            * (1F + 1.1F * glow);
        pose.brightness = brightness;
        pose.baseBrightness = baseBrightness * (1F + 1.1F * glow);
        pose.glowR = FxColor.red(glowColor);
        pose.glowG = FxColor.green(glowColor);
        pose.glowB = FxColor.blue(glowColor);
        int tierRgb = look.tierColor();

        float spin = tier == 0 ? 0F : (0.35F + 0.45F * tier) * (0.4F + 0.6F * fraction) * (1F + 3F * glow);
        state.rotor = wrap(state.rotor + spin * dt);
        state.inner = wrap(state.inner - 1.6F * spin * dt);
        if (tier > 0) state.crystal = wrap(state.crystal + 0.25F * dt);
        state.swirl = wrap(state.swirl + 1.3F * spin * dt);
        rotZ(pose, rotor, state.rotor);
        rotZ(pose, innerRotor, state.inner);
        rotZ(pose, crystal, state.crystal);
        rotZ(pose, swirl, state.swirl);

        for (int i = 0; i < ROTOR_SEG.length; i++) {
            pose.color(ROTOR_SEG[i], tierRgb, brightness, 1F);
            pose.hide(ROTOR_SEG[i], tier == 1 && i % 2 == 1);
        }
        pose.hide(innerRotor, tier == 0);
        for (int index : INNER_SEG)
            pose.color(index, FxColor.mixWhite(glowColor, Math.min(1F, 0.4F + 0.3F * glow)), brightness, 1F);
        pose.color(shellA, tierRgb, 0.9F * brightness, 1F);
        pose.color(nucleus, FxColor.mixWhite(glowColor, 0.6F), Math.min(1.2F, 1.25F * brightness), 1F);
        float nucleusScale = (tier == 0 ? 0.75F : 0.88F + 0.14F * MathHelper.sin(seconds * (3F + 2F * tier)))
            * (1F + 0.3F * glow);
        scale(pose, nucleus, nucleusScale, nucleusScale, nucleusScale);

        pose.hide(swirl, tier == 0);
        pose.color(swirlPane, glowColor, 1F, Math.min(1F, 0.35F + 0.65F * brightness + 0.4F * glow));

        float haloAlpha = (tier == 3 ? 0.55F * fraction : 0F) + 0.9F * glow;
        pose.hide(halo, haloAlpha <= 0.01F);
        float haloScale = 1F + 0.35F * glow;
        scale(pose, halo, haloScale, haloScale, 1F);
        pose.color(
            halo,
            FxColor.mixWhite(glowColor, Math.min(0.6F, 0.4F * glow)),
            1F,
            Math.min(1F, haloAlpha * (0.85F + 0.15F * MathHelper.sin(seconds * 4F))));

        pose.color(visorEyes, FxColor.mixWhite(glowColor, 0.25F), Math.min(1.1F, brightness), 1F);

        if (look.packColor == CelestweaveLook.PACK_RAINBOW)
            pose.color(packBand, Color.HSBtoRGB(seconds * 0.25F % 1F, 0.8F, 1F) & 0xFFFFFF, brightness, 1F);
        else if (look.packColor >= 0) pose.color(packBand, look.packColor, brightness, 1F);

        if (holoScreen >= 0) {
            pose.texFrame[holoScreen] = MathHelper.floor_float(seconds * 4F) & 3;
            pose.color(holoScreen, 0xFFFFFF, 1F, RNG.nextFloat() < 0.04F ? 0.45F : 1F);
        }

        boolean dash = now - state.dashAt < 900;
        for (int index : NOZZLE) if (dash) pose.color(index, 0xFFFFFF, 1F, 1F);

        boolean shown = look.visible(CelestweaveModule.FLIGHT)
            && (state.flying || now - state.wingT0 < WING_RETRACT_MS);
        float[] wing = wingValues(state, now);
        float spar = wing[0], fore = wing[1], field = wing[WING_PARTS - 1];
        float hover = MathHelper.sin(1.6F * seconds);
        int flow = MathHelper.floor_float(seconds * 8F) & 3, scan = MathHelper.floor_float(seconds * 4F) & 3;
        int bladeRgb = FxColor.mixWhite(glowColor, 0.2F), fieldRgb = FxColor.mixWhite(glowColor, 0.1F);
        for (int side : new int[] { 1, -1 }) {
            int sparIndex = side > 0 ? sparL : sparR, foreIndex = side > 0 ? foreL : foreR,
                fieldIndex = side > 0 ? fieldL : fieldR;
            pose.hide(sparIndex, !shown);
            if (!shown) continue;
            if (sparIndex >= 0) pose.rz[sparIndex] = side * (1.2F * (1F - spar) - 0.03F * hover * spar);
            if (foreIndex >= 0) pose.rz[foreIndex] = side * 1.9F * (1F - fore);
            if (fieldIndex >= 0) {
                pose.hide(fieldIndex, field <= 0.001F);
                pose.texFrame[fieldIndex] = scan;
                pose.color(fieldIndex, fieldRgb, 1F, field * (0.5F + 0.1F * MathHelper.sin(2F * seconds)));
            }
            int[] blades = side > 0 ? BLADE_L : BLADE_R;
            for (int i = 0; i < blades.length; i++) {
                int index = blades[i];
                if (index < 0) continue;
                float e = wing[2 + i];
                if (e <= 0.001F) {
                    pose.hidden[index] = true;
                    continue;
                }
                pose.sx[index] = e;
                pose.texFrame[index] = flow;
                float flick = RNG.nextFloat() < 0.02F ? 0.4F : 1F;
                pose.color(index, bladeRgb, 1F, flick * (0.82F + 0.15F * MathHelper.sin(4F * seconds + 0.9F * i)));
            }
        }
    }

    static boolean wingToggle(CelestweaveClientState state, boolean flying, long now) {
        if (flying == state.flying) return false;
        state.wingSnap = wingValues(state, now);
        state.wingT0 = now;
        state.flying = flying;
        return true;
    }

    static float[] wingValues(CelestweaveClientState state, long now) {
        float x = (now - state.wingT0) / 1000F;
        float[] values = new float[WING_PARTS];
        int last = WING_PARTS - 1, blades = BLADES.length;
        if (state.flying) {
            values[0] = FxEasing.easeOutCubic(x / 0.25F);
            values[1] = FxEasing.easeOutCubic((x - 0.12F) / 0.3F);
            for (int i = 0; i < blades; i++) values[2 + i] = FxEasing.easeOutCubic((x - (0.35F + 0.06F * i)) / 0.22F);
            values[last] = FxEasing.easeOutCubic((x - 0.75F) / 0.35F);
        } else {
            values[0] = 1F - FxEasing.easeOutCubic((x - 0.38F) / 0.2F);
            values[1] = 1F - FxEasing.easeOutCubic((x - 0.3F) / 0.2F);
            for (int i = 0; i < blades; i++)
                values[2 + i] = 1F - FxEasing.easeOutCubic((x - (0.05F + 0.04F * (blades - 1 - i))) / 0.15F);
            values[last] = 1F - FxEasing.easeOutCubic(x / 0.15F);
        }
        float[] snap = state.wingSnap;
        if (snap != null && snap.length == WING_PARTS) for (int k = 0; k < WING_PARTS; k++)
            values[k] = state.flying ? Math.max(snap[k], values[k]) : Math.min(snap[k], values[k]);
        return values;
    }

    public static float[] bladeTip(int i, float f) {
        float[] blade = BLADES[i];
        float r = BLADE_GAP + blade[2] * f;
        return new float[] { blade[0] + r * MathHelper.cos(blade[1]), r * MathHelper.sin(blade[1]) };
    }

    private static void rotZ(CelestweavePose pose, int index, float angle) {
        if (index >= 0) pose.rz[index] = angle;
    }

    private static void scale(CelestweavePose pose, int index, float x, float y, float z) {
        if (index < 0) return;
        pose.sx[index] = x;
        pose.sy[index] = y;
        pose.sz[index] = z;
    }

    private static float wrap(float angle) {
        return angle % FxAngle.TAU;
    }
}
