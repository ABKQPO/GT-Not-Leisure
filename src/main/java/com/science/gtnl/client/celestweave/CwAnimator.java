package com.science.gtnl.client.celestweave;

import java.awt.Color;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwAnimator {

    public static final float[][] BLADES = { { 5F, -0.3F, 13F, 4F }, { 4F, 0F, 12F, 4F }, { 3F, 0.3F, 11F, 4F },
        { 2F, 0.6F, 10F, 4F }, { 1F, 0.9F, 9F, 4F } };
    public static final float BLADE_GAP = 1F, FORE_LENGTH = 5F;
    static final int WING_PARTS = BLADES.length + 3;
    static final long WING_RETRACT_MS = 600;
    private static final float TWO_PI = (float) (Math.PI * 2);
    private static final Random RNG = new Random();

    private static int rotor, innerRotor, crystal, nucleus, shellA, swirl, swirlPane, halo, holoScreen, packBand,
        visorEyes;
    private static final int[] ROTOR_SEG = new int[8], INNER_SEG = new int[3], LED = new int[8], PIP = new int[3],
        BLADE_L = new int[5], BLADE_R = new int[5], NOZZLE = new int[2];
    private static int sparL, sparR, foreL, foreR, fieldL, fieldR;
    private static CwFrames bound;

    private CwAnimator() {}

    static void bind(CwFrames frames) {
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
        fill(LED, "core.led");
        fill(PIP, "core.pip");
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
        CwFrames.Frame frame = bound.frame(name);
        return frame == null ? -1 : frame.index;
    }

    private static void fill(int[] target, String prefix) {
        for (int i = 0; i < target.length; i++) target[i] = idx(prefix + i);
    }

    public static float seconds(EntityLivingBase entity) {
        return (entity.ticksExisted + Minecraft.getMinecraft().timer.renderPartialTicks) / 20F;
    }

    static CwPose pose(EntityLivingBase entity, CwClientState state, CwLook look, CwFrames frames) {
        int size = frames.frames.size();
        long frame = CelestweaveClientEvents.renderFrame();
        if (state.pose != null && state.pose.size() == size && state.poseFrame == frame) return state.pose;
        if (state.pose == null || state.pose.size() != size) state.pose = new CwPose(size);
        long nanos = System.nanoTime();
        float dt = state.poseFrame < 0 ? 0F : Math.max(0F, Math.min(0.05F, (nanos - state.lastNanos) / 1.0E9F));
        state.lastNanos = nanos;
        state.poseFrame = frame;
        apply(state.pose, look, state, Minecraft.getSystemTime(), seconds(entity), dt);
        return state.pose;
    }

    private static void apply(CwPose p, CwLook look, CwClientState st, long now, float t, float dt) {
        p.reset();
        int tier = look.tier, theme = look.theme;
        float charge = look.charge;
        float glow = st.hitGlow(now);
        float bright = tier == 0 ? 0.3F : 0.4F + 0.6F * charge;
        if (tier > 0 && charge < 0.1F && RNG.nextFloat() < 0.18F) bright *= 0.25F;
        float b = bright * (0.8F + 0.2F * MathHelper.sin(t * (1.5F + 1.2F * tier))) * (1F + 1.1F * glow);
        p.brightness = b;
        p.themeR = (theme >> 16 & 255) / 255F;
        p.themeG = (theme >> 8 & 255) / 255F;
        p.themeB = (theme & 255) / 255F;
        int tierRgb = look.tierColor();

        float spin = tier == 0 ? 0F : (0.35F + 0.45F * tier) * (0.4F + 0.6F * charge) * (1F + 3F * glow);
        st.rotor = wrap(st.rotor + spin * dt);
        st.inner = wrap(st.inner - 1.6F * spin * dt);
        if (tier > 0) st.crystal = wrap(st.crystal + 0.25F * dt);
        st.swirl = wrap(st.swirl + 1.3F * spin * dt);
        rotZ(p, rotor, st.rotor);
        rotZ(p, innerRotor, st.inner);
        rotZ(p, crystal, st.crystal);
        rotZ(p, swirl, st.swirl);

        for (int i = 0; i < ROTOR_SEG.length; i++) {
            p.color(ROTOR_SEG[i], tierRgb, b, 1F);
            p.hide(ROTOR_SEG[i], tier == 1 && i % 2 == 1);
        }
        p.hide(innerRotor, tier == 0);
        for (int index : INNER_SEG) p.color(index, CwLook.mixWhite(theme, Math.min(1F, 0.7F + 0.3F * glow)), b, 1F);
        p.color(shellA, tierRgb, 0.9F * b, 1F);
        p.color(nucleus, tier == 3 ? 0xFFFFFF : 0xFFE1F0, Math.min(1.5F, 1.25F * b), 1F);
        float nucleusScale = (tier == 0 ? 0.75F : 0.88F + 0.14F * MathHelper.sin(t * (3F + 2F * tier)))
            * (1F + 0.3F * glow);
        scale(p, nucleus, nucleusScale, nucleusScale, nucleusScale);

        p.hide(swirl, tier == 0);
        p.color(swirlPane, theme, 1F, Math.min(1F, 0.35F + 0.65F * b + 0.4F * glow));

        float haloAlpha = (tier == 3 ? 0.55F * charge : 0F) + 0.9F * glow;
        p.hide(halo, haloAlpha <= 0.01F);
        float haloScale = 1F + 0.35F * glow;
        scale(p, halo, haloScale, haloScale, 1F);
        p.color(
            halo,
            CwLook.mixWhite(theme, Math.min(0.6F, 0.4F * glow)),
            1F,
            Math.min(1F, haloAlpha * (0.85F + 0.15F * MathHelper.sin(t * 4F))));

        int lit = tier == 0 ? 0 : Math.round(charge * 8);
        for (int i = 0; i < 8; i++) {
            boolean on = i < lit;
            boolean low = charge < 0.2F && i == 0 && MathHelper.sin(t * 10F) < 0F;
            p.color(
                LED[i],
                on && !low ? (charge < 0.2F ? 0xFF783C : tierRgb) : 0x3C1428,
                on ? Math.max(0.6F, b) : 1F,
                1F);
        }
        for (int i = 0; i < 3; i++) p.color(PIP[i], i < tier ? 0xFFFFFF : 0x321E2D, 1F, 1F);
        p.color(visorEyes, CwLook.mixWhite(theme, 0.25F), Math.min(1.1F, b), 1F);

        if (look.packColor == CwLook.PACK_RAINBOW)
            p.color(packBand, Color.HSBtoRGB(t * 0.25F % 1F, 0.8F, 1F) & 0xFFFFFF, b, 1F);
        else if (look.packColor >= 0) p.color(packBand, look.packColor, b, 1F);

        if (holoScreen >= 0) {
            p.texFrame[holoScreen] = MathHelper.floor_float(t * 4F) & 3;
            p.color(holoScreen, 0xFFFFFF, 1F, RNG.nextFloat() < 0.04F ? 0.45F : 1F);
        }

        boolean dash = now - st.dashAt < 900;
        for (int index : NOZZLE) if (dash) p.color(index, 0xFFFFFF, 1F, 1F);

        boolean shown = look.wings && (st.flying || now - st.wingT0 < WING_RETRACT_MS);
        float[] wing = wingValues(st, now);
        float spar = wing[0], fore = wing[1], field = wing[WING_PARTS - 1];
        float hover = MathHelper.sin(1.6F * t);
        int flow = MathHelper.floor_float(t * 8F) & 3, scan = MathHelper.floor_float(t * 4F) & 3;
        int bladeRgb = CwLook.mixWhite(theme, 0.2F), fieldRgb = CwLook.mixWhite(theme, 0.1F);
        for (int side : new int[] { 1, -1 }) {
            int sparIndex = side > 0 ? sparL : sparR, foreIndex = side > 0 ? foreL : foreR,
                fieldIndex = side > 0 ? fieldL : fieldR;
            p.hide(sparIndex, !shown);
            if (!shown) continue;
            if (sparIndex >= 0) p.rz[sparIndex] = side * (1.2F * (1F - spar) - 0.03F * hover * spar);
            if (foreIndex >= 0) p.rz[foreIndex] = side * 1.9F * (1F - fore);
            if (fieldIndex >= 0) {
                p.hide(fieldIndex, field <= 0.001F);
                p.texFrame[fieldIndex] = scan;
                p.color(fieldIndex, fieldRgb, 1F, field * (0.5F + 0.1F * MathHelper.sin(2F * t)));
            }
            int[] blades = side > 0 ? BLADE_L : BLADE_R;
            for (int i = 0; i < blades.length; i++) {
                int index = blades[i];
                if (index < 0) continue;
                float e = wing[2 + i];
                if (e <= 0.001F) {
                    p.hidden[index] = true;
                    continue;
                }
                p.sx[index] = e;
                p.texFrame[index] = flow;
                float flick = RNG.nextFloat() < 0.02F ? 0.4F : 1F;
                p.color(index, bladeRgb, 1F, flick * (0.82F + 0.15F * MathHelper.sin(4F * t + 0.9F * i)));
            }
        }
    }

    static boolean wingToggle(CwClientState st, boolean flying, long now) {
        if (flying == st.flying) return false;
        st.wingSnap = wingValues(st, now);
        st.wingT0 = now;
        st.flying = flying;
        return true;
    }

    static float[] wingValues(CwClientState st, long now) {
        float x = (now - st.wingT0) / 1000F;
        float[] values = new float[WING_PARTS];
        int last = WING_PARTS - 1, blades = BLADES.length;
        if (st.flying) {
            values[0] = ease(x, 0F, 0.25F);
            values[1] = ease(x, 0.12F, 0.3F);
            for (int i = 0; i < blades; i++) values[2 + i] = ease(x, 0.35F + 0.06F * i, 0.22F);
            values[last] = ease(x, 0.75F, 0.35F);
        } else {
            values[0] = 1F - ease(x, 0.38F, 0.2F);
            values[1] = 1F - ease(x, 0.3F, 0.2F);
            for (int i = 0; i < blades; i++) values[2 + i] = 1F - ease(x, 0.05F + 0.04F * (blades - 1 - i), 0.15F);
            values[last] = 1F - ease(x, 0F, 0.15F);
        }
        float[] snap = st.wingSnap;
        if (snap != null && snap.length == WING_PARTS) for (int k = 0; k < WING_PARTS; k++)
            values[k] = st.flying ? Math.max(snap[k], values[k]) : Math.min(snap[k], values[k]);
        return values;
    }

    public static float[] bladeTip(int i, float f) {
        float[] blade = BLADES[i];
        float r = BLADE_GAP + blade[2] * f;
        return new float[] { blade[0] + r * MathHelper.cos(blade[1]), r * MathHelper.sin(blade[1]) };
    }

    private static float ease(float since, float delay, float duration) {
        float u = Math.max(0F, Math.min(1F, (since - delay) / duration));
        return 1F - (1F - u) * (1F - u) * (1F - u);
    }

    private static void rotZ(CwPose p, int index, float angle) {
        if (index >= 0) p.rz[index] = angle;
    }

    private static void scale(CwPose p, int index, float x, float y, float z) {
        if (index < 0) return;
        p.sx[index] = x;
        p.sy[index] = y;
        p.sz[index] = z;
    }

    private static float wrap(float angle) {
        return angle % TWO_PI;
    }
}
