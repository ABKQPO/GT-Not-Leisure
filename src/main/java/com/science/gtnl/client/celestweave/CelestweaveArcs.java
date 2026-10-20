package com.science.gtnl.client.celestweave;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.fx.FxColor;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveArcs {

    static final Random RNG = new Random();
    private static final float[] ANTENNA_A = { -5.25F, -9.2F, 6.9F }, ANTENNA_B = { 5.25F, -9.2F, 6.9F };
    private static final float[] CORE = { 0F, 3.25F, -4.3F }, CORE_INNER = { 0F, 3.25F, -4.35F };
    private static final float[] DASH_FROM = { 0F, 11F, 3.3F };
    private static final Seam[] SEAMS = {
        new Seam(CelestweaveArmor.CHEST, "bone:rightArm", -4.1F, 0.75F, -3F, -4.1F, 0.75F, 2.5F),
        new Seam(CelestweaveArmor.CHEST, "bone:leftArm", 4.1F, 0.75F, -3F, 4.1F, 0.75F, 2.5F),
        new Seam(CelestweaveArmor.CHEST, "bone:rightArm", -4.1F, 6.2F, 0F, -4.1F, 9.1F, 0F),
        new Seam(CelestweaveArmor.CHEST, "bone:leftArm", 4.1F, 6.2F, 0F, 4.1F, 9.1F, 0F),
        new Seam(CelestweaveArmor.LEGS, "bone:rightLeg", -3.1F, 0.8F, -1F, -3.1F, 4.8F, 1F),
        new Seam(CelestweaveArmor.LEGS, "bone:leftLeg", 3.1F, 0.8F, -1F, 3.1F, 4.8F, 1F),
        new Seam(CelestweaveArmor.CHEST, "bone:body", -2.6F, 3.25F, -4.1F, -4.3F, 4.4F, -3.6F),
        new Seam(CelestweaveArmor.CHEST, "bone:body", 2.6F, 3.25F, -4.1F, 4.3F, 4.4F, -3.6F),
        new Seam(CelestweaveArmor.CHEST, "bone:body", -2.5F, 1.5F, 4.6F, -2.5F, 8F, 4.6F),
        new Seam(CelestweaveArmor.CHEST, "bone:body", 2.5F, 1.5F, 4.6F, 2.5F, 8F, 4.6F),
        new Seam(CelestweaveArmor.HEAD, "bone:head", -1.6F, -10.85F, -2.2F, 1.6F, -10.85F, 2.2F) };

    static int total;

    private record Seam(int piece, String anchor, float[] a, float[] b) {

        Seam(int piece, String anchor, float ax, float ay, float az, float bx, float by, float bz) {
            this(piece, anchor, new float[] { ax, ay, az }, new float[] { bx, by, bz });
        }
    }

    private CelestweaveArcs() {}

    static boolean spawn(CelestweaveClientState state, String anchor, float[] a, float[] b, int segs, float spread,
        float width, long born, int lifetime, int rgb, CelestweaveArc.Kind kind) {
        if (total >= MainConfig.client.celestweaveMaxArcsTotal
            || state.arcs.size() >= MainConfig.client.celestweaveMaxArcsPerPlayer) return false;
        state.arcs.add(new CelestweaveArc(anchor, a, b, segs, spread, width, born, lifetime, rgb, kind));
        total++;
        return true;
    }

    static void tick(EntityLivingBase entity, CelestweaveClientState state, CelestweaveLook look, long now, int level) {
        state.arcs.removeIf(arc -> arc.expired(now));
        if (level <= 0 || look.fraction <= 0) return;
        CelestweaveCosmetic cosmetic = look.cosmetic;
        int glowColor = look.glowColor;
        boolean storm = entity.worldObj.isThundering();
        if (level >= 2 && look.tier > 0) {
            int want = Math.min(look.idleWant(storm), MainConfig.client.celestweaveMaxArcsPerPlayer);
            for (int n = state.countIdle(); n < want; n++) {
                Seam seam = SEAMS[RNG.nextInt(SEAMS.length)];
                if (!look.wears(seam.piece())) continue;
                spawn(
                    state,
                    seam.anchor(),
                    seam.a(),
                    seam.b(),
                    5,
                    0.8F * cosmetic.get(CelestweaveCosmeticOption.ARC_SPREAD),
                    0.11F,
                    now,
                    Math.round(rand(140F, 320F) / cosmetic.get(CelestweaveCosmeticOption.ARC_FREQ)),
                    glowColor,
                    CelestweaveArc.Kind.IDLE);
            }
            if (look.tier == 3 && cosmetic.event(CelestweaveCosmetic.EVENT_ANTENNA)
                && look.wears(CelestweaveArmor.HEAD)
                && !state.hasAntenna())
                spawn(
                    state,
                    "bone:head",
                    ANTENNA_A,
                    ANTENNA_B,
                    10,
                    2.2F,
                    0.15F,
                    now,
                    Math.round(rand(300F, 600F)),
                    glowColor,
                    CelestweaveArc.Kind.ANTENNA);
        }
        if (cosmetic.event(CelestweaveCosmetic.EVENT_LOW) && look.fraction < 0.1F
            && look.wears(CelestweaveArmor.CHEST)
            && RNG.nextFloat() < 0.15F) {
            float angle = rand(0F, (float) (Math.PI * 2));
            spawn(
                state,
                "bone:body",
                CORE,
                new float[] { (float) Math.cos(angle) * 2.2F, 3.25F + (float) Math.sin(angle) * 2.2F, -4.3F },
                3,
                0.6F,
                0.1F,
                now,
                90,
                glowColor,
                CelestweaveArc.Kind.BURST);
        }
        boolean hit = state.recentlyHit(now);
        if (look.wears(CelestweaveArmor.CHEST) && (look.tier >= 2 || hit)) {
            float rate = (look.tier == 2 ? 1.2F : look.tier == 3 ? 4F : 0F) * look.fraction + (hit ? 25F : 0F);
            float expected = rate / 20F;
            int count = (int) expected + (RNG.nextFloat() < expected % 1F ? 1 : 0);
            for (int i = 0; i < count; i++) {
                float angle = rand(0F, (float) (Math.PI * 2)), radius = rand(1.3F, 1.8F);
                spawn(
                    state,
                    "bone:body",
                    CORE_INNER,
                    new float[] { (float) Math.cos(angle) * radius, 3.25F + (float) Math.sin(angle) * radius, -4.35F },
                    3,
                    0.5F,
                    0.06F,
                    now,
                    Math.round(rand(70F, 160F)),
                    FxColor.mixWhite(glowColor, 0.4F),
                    CelestweaveArc.Kind.BURST);
            }
        }
        if (state.flying && look.visible(CelestweaveModule.FLIGHT) && cosmetic.event(CelestweaveCosmetic.EVENT_WING)) {
            float since = (now - state.wingT0) / 1000F;
            float rate = since < 0.6F ? 14F : 0.8F * (storm ? 2F : 1F);
            if (RNG.nextFloat() < rate / 20F) {
                int side = RNG.nextBoolean() ? 1 : -1;
                String anchor = "wing.fore." + (side > 0 ? "L" : "R");
                if (RNG.nextFloat() < 0.55F) {
                    int i = RNG.nextInt(CelestweaveAnimator.BLADES.length - 1);
                    float f = rand(0.7F, 0.95F);
                    float[] a = CelestweaveAnimator.bladeTip(i, f), b = CelestweaveAnimator.bladeTip(i + 1, f);
                    spawn(
                        state,
                        anchor,
                        new float[] { side * a[0], a[1], 0.2F },
                        new float[] { side * b[0], b[1], 0.2F },
                        6,
                        0.45F,
                        0.12F,
                        now,
                        Math.round(rand(90F, 170F)),
                        glowColor,
                        CelestweaveArc.Kind.BURST);
                } else spawn(
                    state,
                    anchor,
                    new float[] { side * rand(0F, 1.5F), 0F, 0.5F },
                    new float[] { side * rand(5F, CelestweaveAnimator.FORE_LENGTH), rand(-0.3F, 0.3F), 0.5F },
                    6,
                    0.5F,
                    0.12F,
                    now,
                    Math.round(rand(100F, 200F)),
                    glowColor,
                    CelestweaveArc.Kind.BURST);
            }
        }
    }

    static void hit(CelestweaveClientState state, CelestweaveLook look, long now) {
        int glowColor = look.glowColor;
        for (int i = 0; i < 6; i++) {
            float angle = rand(0F, (float) (Math.PI * 2)), radius = rand(5F, 8F);
            spawn(
                state,
                "bone:body",
                CORE,
                new float[] { (float) Math.cos(angle) * radius, 3.25F + (float) Math.sin(angle) * radius * 0.8F,
                    -4.3F - rand(1F, 4F) },
                7,
                1.1F,
                0.16F,
                now + i * 25L,
                Math.round(rand(200F, 300F)),
                glowColor,
                CelestweaveArc.Kind.BURST);
        }
        if (look.wears(CelestweaveArmor.HEAD)) spawn(
            state,
            "bone:head",
            ANTENNA_A,
            ANTENNA_B,
            10,
            2.5F,
            0.15F,
            now,
            260,
            glowColor,
            CelestweaveArc.Kind.BURST);
    }

    static void dash(CelestweaveClientState state, CelestweaveLook look, long now) {
        for (String leg : new String[] { "bone:rightLeg", "bone:leftLeg" }) for (int i = 0; i < 5; i++) spawn(
            state,
            leg,
            DASH_FROM,
            new float[] { rand(-3F, 3F), rand(10F, 12.5F), rand(9F, 16F) },
            7,
            1.4F,
            0.24F,
            now + i * 90L,
            230,
            FxColor.mixWhite(look.glowColor, 0.25F),
            CelestweaveArc.Kind.BURST);
    }

    static void render(CelestweaveArmorModel model, CelestweaveFrames frames, CelestweaveClientState state,
        CelestweaveLook look, CelestweavePose pose, float scale) {
        if (state.arcs.isEmpty()) return;
        long now = Minecraft.getSystemTime();
        float bright = look.cosmetic.get(CelestweaveCosmeticOption.ARC_BRIGHT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        Tessellator tessellator = Tessellator.instance;
        for (CelestweaveArc arc : state.arcs) {
            if (now < arc.born || arc.expired(now) || !arc.resolve(frames)) continue;
            if (arc.frame != null && !pose.visible(arc.frame)) continue;
            float age = (now - arc.born) / (float) arc.lifetime;
            float fade = Math.min(1F, (1F - age) * 1.6F), flicker = 0.55F + 0.45F * RNG.nextFloat();
            float outer = 0.45F * fade * flicker * bright, core = Math.min(1F, 0.95F * fade * flicker * bright);
            float[] points = arc.layout(now, RNG);
            GL11.glPushMatrix();
            model.enterAnchor(arc.bone, arc.frame, pose, scale);
            tessellator.startDrawingQuads();
            for (int i = 0; i < arc.segs; i++) {
                crossQuad(tessellator, points, i, arc.width * 3.2F, arc.rgb, outer);
                crossQuad(tessellator, points, i, arc.width, 0xFFFFFF, core);
            }
            tessellator.draw();
            GL11.glPopMatrix();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void crossQuad(Tessellator tessellator, float[] points, int i, float width, int rgb, float alpha) {
        float x0 = points[i * 3], y0 = points[i * 3 + 1], z0 = points[i * 3 + 2];
        float x1 = points[i * 3 + 3], y1 = points[i * 3 + 4], z1 = points[i * 3 + 5];
        float dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-4F) return;
        dx /= length;
        dy /= length;
        dz /= length;
        float extend = width * 0.25F;
        x0 -= dx * extend;
        y0 -= dy * extend;
        z0 -= dz * extend;
        x1 += dx * extend;
        y1 += dy * extend;
        z1 += dz * extend;
        float ux = dz, uz = -dx;
        float u = (float) Math.sqrt(ux * ux + uz * uz);
        if (u < 1.0E-4F) {
            ux = 1F;
            uz = 0F;
        } else {
            ux /= u;
            uz /= u;
        }
        float vx = dy * uz, vy = dz * ux - dx * uz, vz = -dy * ux;
        float half = width * 0.5F;
        FxColor.tessellate(tessellator, rgb, alpha);
        quad(tessellator, x0, y0, z0, x1, y1, z1, ux * half, 0F, uz * half);
        quad(tessellator, x0, y0, z0, x1, y1, z1, vx * half, vy * half, vz * half);
    }

    private static void quad(Tessellator tessellator, float x0, float y0, float z0, float x1, float y1, float z1,
        float ox, float oy, float oz) {
        tessellator.addVertex(x0 - ox, y0 - oy, z0 - oz);
        tessellator.addVertex(x1 - ox, y1 - oy, z1 - oz);
        tessellator.addVertex(x1 + ox, y1 + oy, z1 + oz);
        tessellator.addVertex(x0 + ox, y0 + oy, z0 + oz);
    }

    static float rand(float min, float max) {
        return min + RNG.nextFloat() * (max - min);
    }
}
