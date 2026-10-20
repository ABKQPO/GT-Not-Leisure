package com.science.gtnl.client.celestweave;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.fx.FxColor;
import com.science.gtnl.client.fx.FxEasing;
import com.science.gtnl.client.hud.HudEditor;
import com.science.gtnl.client.hud.HudLayout;
import com.science.gtnl.client.hud.HudLayout.Placement;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveHud {

    private static final int HIT_LIFETIME = 900, SAVE_LIFETIME = 1400, FADE = 500, READY_FLASH = 350, WINDOW_MS = 12000,
        STALE_MS = 7000, SAMPLES = 32, GROUND_SCAN = 96;
    private static final int LOW_RGB = 0xFF3B3B, PANEL_RGB = 0x0C1622, PANEL_HI_RGB = 0x13212F, TRACK_RGB = 0x1C2C3B,
        DIM_RGB = 0x9FB0BE;
    private static final float LOW = 0.15F, SMALL = 0.75F, CORE_RADIUS = 8F, RING_INNER = 13F, RING_OUTER = 16.5F,
        DASH_RADIUS = 21F, DASH_SPAN = 50F, LADDER_HALF = 36F, LADDER_SCALE = 3F, PREVIEW_FRACTION = 0.7F;
    private static final double PREVIEW_STORED = 7_000_000;
    private static final long PREVIEW_DASH_AGE = 600, PREVIEW_DASH_LIFETIME = 1000;
    private static final float[] PREVIEW_PIECES = { 0.9F, 0.7F, 0.6F, 0.5F };
    private static final float[] CORE = polygon(CORE_RADIUS, 0), CORE_INNER = polygon(CORE_RADIUS - 1.2F, 0),
        CORE_DOT = polygon(2.2F, 0), RING = polygon(RING_INNER, Math.PI / 6);
    private static final double[] HISTORY = new double[SAMPLES];
    private static final long[] TIMES = new long[SAMPLES];
    private static long hitAt = CelestweaveClientState.NEVER, wakeAt = CelestweaveClientState.NEVER,
        landedAt = CelestweaveClientState.NEVER;
    private static int hitLifetime = HIT_LIFETIME, signature, wornMask = -1, historySize, historyHead;
    private static boolean save, omni, flying, signed;
    private static float hitX, hitZ, climb, rate;
    private static double lastSeen = Double.NaN;

    static void hit(float x, float z, boolean saved, boolean ambient) {
        hitX = x;
        hitZ = z;
        save = saved;
        omni = ambient;
        hitLifetime = saved ? SAVE_LIFETIME : HIT_LIFETIME;
        hitAt = wakeAt = Minecraft.getSystemTime();
    }

    static void clear() {
        hitAt = wakeAt = landedAt = CelestweaveClientState.NEVER;
        signed = flying = false;
        climb = 0F;
        resetRate();
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !MainConfig.client.enableCelestweaveHud) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null || minecraft.renderViewEntity != player
            || minecraft.gameSettings.showDebugInfo
            || HudEditor.isOpen()) return;
        double stored = 0, capacity = 0;
        float[] pieces = { -1F, -1F, -1F, -1F };
        int status = 1, mask = 0;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            status *= 31;
            if (stack == null) continue;
            mask |= 1 << piece;
            status += 1 + CelestweaveData.activeModules(stack)
                .hashCode();
            stored += EquipmentEnergy.read(stack);
            capacity += CelestweaveEnergy.capacity(stack);
            pieces[piece] = (float) CelestweaveEnergy.fraction(stack);
        }
        if (mask == 0) {
            signed = flying = false;
            resetRate();
            return;
        }
        long now = Minecraft.getSystemTime();
        if (mask != wornMask) {
            resetRate();
            wornMask = mask;
        }
        sample(stored, now);
        CelestweaveClientState state = CelestweaveClientState.of(player);
        CelestweaveLook look = CelestweaveClientEvents.look(player, state);
        float fraction = capacity <= 0 ? 0F : (float) Math.min(1, stored / capacity);
        boolean low = fraction < LOW && !EquipmentEnergy.free(player);
        boolean nowFlying = look.flightActive && player.capabilities.isFlying;
        if (flying && !nowFlying) landedAt = now;
        flying = nowFlying;
        status = status * 4 + (flying ? 2 : 0) + (low ? 1 : 0);
        if (!signed || status != signature) {
            signed = true;
            signature = status;
            wakeAt = now;
        }
        long dashLife = MainConfig.item.celestweave.dashCooldown * 50L, dashAge = now - state.dashAt;
        if (flying || low || dashAge >= 0 && dashAge < dashLife + READY_FLASH) wakeAt = now;
        float awake = fade(now - wakeAt - (long) (MainConfig.client.celestweaveHudIdleSeconds * 1000));
        float idle = MainConfig.client.celestweaveHudIdleAlpha;
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        FontRenderer font = minecraft.fontRenderer;
        open();
        try {
            int rgb = low ? LOW_RGB : look.glowColor;
            float pulse = low ? 0.5F + 0.5F * (float) Math.sin(now * Math.PI * 2 / 600.0) : 0F;
            Placement corePlace = HudLayout.place(CelestweaveHudElement.CORE.hud, width, height);
            if (corePlace != null) {
                begin(corePlace);
                float alpha = idle + (1 - idle) * awake;
                if (alpha > 0.01F) core(fraction, rgb, pulse, alpha);
                if (FxEasing.easeOutCubic(awake) > 0.02F)
                    readout(font, fraction, stored, pieces, rgb, look.glowColor, FxEasing.easeOutCubic(awake));
                end();
            }
            Placement ringPlace = HudLayout.place(CelestweaveHudElement.SHIELD_RING.hud, width, height);
            if (ringPlace != null) {
                begin(ringPlace);
                ring(player, look.glowColor, now);
                end();
            }
            Placement dashPlace = HudLayout.place(CelestweaveHudElement.DASH_ARC.hud, width, height);
            if (dashPlace != null) {
                begin(dashPlace);
                dash(look.worn[CelestweaveArmor.BOOTS], look.glowColor, dashAge, dashLife);
                end();
            }
            float ladder = flying ? 1F : fade(now - landedAt);
            Placement ladderPlace = ladder > 0 ? HudLayout.place(CelestweaveHudElement.ALTIMETER.hud, width, height)
                : null;
            if (ladderPlace != null) {
                begin(ladderPlace);
                ladder(font, player, look.worn[CelestweaveArmor.LEGS], look.glowColor, ladder, event.partialTicks);
                end();
            }
        } finally {
            close();
        }
    }

    static void preview(CelestweaveHudElement element, Placement place, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null) return;
        CelestweaveLook look = CelestweaveClientEvents.look(player, CelestweaveClientState.of(player));
        FontRenderer font = minecraft.fontRenderer;
        open();
        try {
            begin(place);
            switch (element) {
                case CORE -> {
                    core(PREVIEW_FRACTION, look.glowColor, 0F, 1F);
                    readout(font, PREVIEW_FRACTION, PREVIEW_STORED, PREVIEW_PIECES, look.glowColor, look.glowColor, 1F);
                }
                case SHIELD_RING -> shield(look.glowColor, 0F, 1F, false, 0, false);
                case DASH_ARC -> dashArc(look.glowColor, PREVIEW_DASH_AGE, PREVIEW_DASH_LIFETIME);
                case ALTIMETER -> ladder(
                    font,
                    player,
                    look.worn[CelestweaveArmor.LEGS],
                    look.glowColor,
                    1F,
                    partialTicks);
                default -> {}
            }
            end();
        } finally {
            close();
        }
    }

    private static void open() {
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
    }

    private static void close() {
        GL11.glPopAttrib();
    }

    private static void core(float fraction, int rgb, float pulse, float alpha) {
        Tessellator tessellator = Tessellator.instance;
        fan(tessellator, CORE, 0x000000, 0.45F * alpha);
        float level = CORE_RADIUS - 2 * CORE_RADIUS * fraction;
        float[] fill = clipBelow(CORE, level);
        fan(tessellator, fill, rgb, (0.65F + 0.25F * pulse) * alpha);
        tessellator.startDrawingQuads();
        float[] span = span(fill, level);
        if (span != null && fraction < 0.995F) {
            FxColor.tessellate(tessellator, FxColor.mixWhite(rgb, 0.75F), 0.9F * alpha);
            rect(tessellator, span[0] + 0.6F, level - 0.4F, span[1] - 0.6F, level + 0.4F);
        }
        FxColor.tessellate(tessellator, FxColor.mixWhite(rgb, 0.35F), (0.85F + 0.15F * pulse) * alpha);
        outline(tessellator, CORE, CORE_INNER);
        tessellator.draw();
        fan(tessellator, CORE_DOT, FxColor.mixWhite(rgb, 0.7F), 0.9F * alpha);
    }

    private static void readout(FontRenderer font, float fraction, double stored, float[] pieces, int rgb,
        int glowColor, float slide) {
        String percent = Math.round(fraction * 100) + "%", energy = Utils.shortFormat((long) stored) + " EU";
        String flow = Math.abs(rate) < 0.5F ? ""
            : (rate > 0 ? "-" : "+") + Utils.shortFormat(Math.round(Math.abs(rate))) + "/t";
        String eta = rate >= 0.5F ? duration(stored / rate / 20) : "";
        int split = font.getStringWidth(percent) + 4;
        float second = 19 + (font.getStringWidth(flow) + (eta.isEmpty() ? 0 : font.getStringWidth(eta) + 6)) * SMALL;
        float inner = Math.max(split + font.getStringWidth(energy), second);
        float right = -CORE_RADIUS - 4 + (1 - slide) * 6, left = right - inner - 7;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        screen(tessellator, left, -11, right, 9, glowColor, slide);
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++)
            cell(tessellator, left + 4 + piece * 4, 1.5F, pieces[piece], glowColor, slide);
        tessellator.draw();
        text(font, percent, left + 4, -9, FxColor.mixWhite(rgb, 0.4F), slide);
        text(font, energy, left + 4 + split, -9, DIM_RGB, slide);
        small(font, flow, left + 23, 1.5F, rate > 0 ? DIM_RGB : FxColor.mixWhite(glowColor, 0.4F), slide);
        if (!eta.isEmpty()) small(font, eta, left + 23 + (font.getStringWidth(flow) + 6) * SMALL, 1.5F, DIM_RGB, slide);
    }

    private static void cell(Tessellator tessellator, float x, float y, float fraction, int glowColor, float alpha) {
        FxColor.tessellate(tessellator, TRACK_RGB, (fraction < 0 ? 0.35F : 0.9F) * alpha);
        rect(tessellator, x, y, x + 3, y + 6);
        if (fraction <= 0) return;
        FxColor.tessellate(tessellator, fraction < LOW ? LOW_RGB : glowColor, alpha);
        rect(tessellator, x, y + 6 * (1 - fraction), x + 3, y + 6);
    }

    private static void ring(EntityPlayer player, int glowColor, long now) {
        long age = now - hitAt;
        if (age < 0 || age >= hitLifetime) return;
        float progress = age / (float) hitLifetime;
        float envelope = Math.min(1F, age / 60F) * (1 - progress) * (1 - progress);
        double dx = hitX - player.posX, dz = hitZ - player.posZ, yaw = Math.toRadians(player.rotationYaw);
        double forward = dx * -Math.sin(yaw) + dz * Math.cos(yaw), lateral = dx * -Math.cos(yaw) + dz * -Math.sin(yaw);
        boolean directional = !save && !omni && forward * forward + lateral * lateral > 0.16;
        shield(glowColor, progress, envelope, directional, Math.atan2(lateral, forward), save);
    }

    private static void shield(int glowColor, float progress, float envelope, boolean directional, double angle,
        boolean saved) {
        int rgb = FxColor.mixWhite(glowColor, saved ? 0.55F : 0.25F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int segment = 0; segment < 6; segment++) {
            float weight = saved ? 1F : directional ? segmentWeight(angle, segment) : 0.45F;
            float[] outer = polygon(RING_OUTER + 2.5F * weight * (1 - progress), Math.PI / 6);
            FxColor.tessellate(tessellator, rgb, envelope * (0.18F + 0.82F * weight));
            edge(tessellator, outer, RING, segment, 0.08F);
        }
        if (saved) {
            FxColor.tessellate(tessellator, rgb, 0.6F * (1 - progress));
            outline(
                tessellator,
                polygon(RING_OUTER + 1 + 14 * progress, Math.PI / 6),
                polygon(RING_OUTER + 14 * progress, Math.PI / 6));
        }
        tessellator.draw();
        if (!directional) return;
        float sin = (float) Math.sin(angle), cos = (float) -Math.cos(angle), base = RING_OUTER + 4.5F + 2 * progress;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        FxColor.tessellate(tessellator, FxColor.mixWhite(glowColor, 0.6F), envelope);
        tessellator.addVertex(sin * (base + 4), cos * (base + 4), 0);
        tessellator.addVertex(sin * base - cos * 3, cos * base + sin * 3, 0);
        tessellator.addVertex(sin * base + cos * 3, cos * base - sin * 3, 0);
        tessellator.draw();
    }

    private static void dash(ItemStack boots, int glowColor, long age, long lifetime) {
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.DASH)) return;
        if (age < 0 || age >= lifetime + READY_FLASH) return;
        dashArc(glowColor, age, lifetime);
    }

    private static void dashArc(int glowColor, long age, long lifetime) {
        float progress = lifetime <= 0 ? 1F : Math.min(1F, age / (float) lifetime);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        arc(tessellator, 1F, DASH_RADIUS, 1.2F, 0x000000, 0.4F);
        for (int tick = 0; tick <= 4; tick++) {
            double a = Math.toRadians(180 + DASH_SPAN - 2 * DASH_SPAN * tick / 4.0);
            float sin = (float) Math.sin(a), cos = (float) -Math.cos(a), length = tick % 4 == 0 ? 3.2F : 2F;
            FxColor.tessellate(tessellator, 0xFFFFFF, progress >= tick / 4F ? 0.7F : 0.3F);
            tessellator.addVertex(sin * (DASH_RADIUS + 1.4F) - cos * 0.4F, cos * (DASH_RADIUS + 1.4F) + sin * 0.4F, 0);
            tessellator.addVertex(sin * (DASH_RADIUS + 1.4F) + cos * 0.4F, cos * (DASH_RADIUS + 1.4F) - sin * 0.4F, 0);
            tessellator.addVertex(
                sin * (DASH_RADIUS + 1.4F + length) + cos * 0.4F,
                cos * (DASH_RADIUS + 1.4F + length) - sin * 0.4F,
                0);
            tessellator.addVertex(
                sin * (DASH_RADIUS + 1.4F + length) - cos * 0.4F,
                cos * (DASH_RADIUS + 1.4F + length) + sin * 0.4F,
                0);
        }
        if (progress < 1) arc(tessellator, progress, DASH_RADIUS, 1F, glowColor, 0.85F);
        else {
            float flash = fade((age - lifetime) * FADE / READY_FLASH);
            arc(tessellator, 1F, DASH_RADIUS, 1.2F + 1.2F * flash, FxColor.mixWhite(glowColor, 0.6F), flash);
        }
        tessellator.draw();
    }

    private static void ladder(FontRenderer font, EntityPlayer player, ItemStack legs, int glowColor, float alpha,
        float partialTicks) {
        double feet = player.boundingBox.minY - (player.posY - player.prevPosY) * (1 - partialTicks);
        climb += ((float) player.motionY * 20 - climb) * 0.2F;
        int ground = groundBelow(
            player.worldObj,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(feet),
            MathHelper.floor_double(player.posZ));
        int bright = FxColor.mixWhite(glowColor, 0.4F);
        String altitude = "Y " + (int) Math.floor(feet);
        float label = -8 - font.getStringWidth(altitude);
        double span = LADDER_HALF / LADDER_SCALE;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        screen(tessellator, label - 3, -6, -6, 6, glowColor, alpha);
        FxColor.tessellate(tessellator, 0xFFFFFF, 0.35F * alpha);
        rect(tessellator, -0.5F, -LADDER_HALF, 0.5F, LADDER_HALF);
        for (long mark = (long) Math.ceil((feet - span) / 2) * 2; mark <= feet + span; mark += 2) {
            float y = (float) (-(mark - feet) * LADDER_SCALE), edge = Math.abs(y) / LADDER_HALF;
            FxColor.tessellate(tessellator, mark % 10 == 0 ? bright : 0xFFFFFF, 0.75F * alpha * (1 - edge * edge));
            rect(tessellator, 0.5F, y - 0.5F, mark % 10 == 0 ? 6.5F : 3.5F, y + 0.5F);
        }
        if (ground != Integer.MIN_VALUE && feet - ground <= span) {
            float y = (float) ((feet - ground) * LADDER_SCALE);
            FxColor.tessellate(tessellator, glowColor, 0.55F * alpha);
            rect(tessellator, -3F, y - 0.75F, 9F, y + 0.75F);
            for (int i = 0; i < 4; i++) rect(tessellator, -2F + i * 3, y + 0.75F, -1F + i * 3, y + 2.5F);
        }
        tessellator.draw();
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        FxColor.tessellate(tessellator, bright, 0.95F * alpha);
        tessellator.addVertex(-1, 0, 0);
        tessellator.addVertex(-5, 3, 0);
        tessellator.addVertex(-5, -3, 0);
        float arrow = Math.abs(climb) < 0.05F ? 0 : Math.signum(climb), below = LADDER_HALF + 7.5F;
        if (arrow != 0) {
            FxColor.tessellate(tessellator, 0xFFFFFF, 0.8F * alpha);
            tessellator.addVertex(-8, below - arrow * 2, 0);
            tessellator.addVertex(-10.5F, below + arrow * 1.5F, 0);
            tessellator.addVertex(-5.5F, below + arrow * 1.5F, 0);
        }
        tessellator.draw();
        text(font, altitude, label, -4, 0xFFFFFF, alpha);
        String height = ground == Integer.MIN_VALUE ? "--" : String.format(Locale.ROOT, "%.1f", feet - ground);
        small(font, "H " + height, label, 8, DIM_RGB, alpha);
        for (long mark = (long) Math.ceil((feet - span) / 10) * 10; mark <= feet + span; mark += 10) {
            float y = (float) (-(mark - feet) * LADDER_SCALE), edge = Math.abs(y) / LADDER_HALF;
            if (edge < 0.85F && Math.abs(y) > 7)
                small(font, String.valueOf(mark), 8, y - 3, DIM_RGB, alpha * (1 - edge));
        }
        if (legs != null) {
            String level = "Lv" + CelestweaveModuleSetting.FLIGHT_SPEED.get(legs);
            text(font, level, -font.getStringWidth(level) / 2F, -LADDER_HALF - 10, bright, alpha);
        }
        String speed = String.format(Locale.ROOT, "%.1f", Math.abs(climb));
        text(font, speed, -4, LADDER_HALF + 4, DIM_RGB, alpha);
    }

    private static int groundBelow(World world, int x, int y, int z) {
        for (int below = y - 1; below >= Math.max(0, y - GROUND_SCAN); below--) if (world.getBlock(x, below, z)
            .getMaterial()
            .blocksMovement()) return below + 1;
        return Integer.MIN_VALUE;
    }

    private static void sample(double stored, long now) {
        boolean changed = stored != lastSeen;
        if (!Double.isNaN(lastSeen) && changed) {
            TIMES[historyHead] = now;
            HISTORY[historyHead] = stored;
            historyHead = (historyHead + 1) % SAMPLES;
            if (historySize < SAMPLES) historySize++;
        }
        lastSeen = stored;
        while (historySize > 1 && now - TIMES[oldest()] > WINDOW_MS) historySize--;
        if (historySize > 0 && now - TIMES[(historyHead - 1 + SAMPLES) % SAMPLES] > STALE_MS) historySize = 0;
        if (historySize < 2) rate = 0F;
        else if (changed) rate = drainRate(HISTORY[oldest()], stored, now - TIMES[oldest()]);
    }

    private static int oldest() {
        return (historyHead - historySize + SAMPLES) % SAMPLES;
    }

    private static void resetRate() {
        historySize = historyHead = 0;
        lastSeen = Double.NaN;
        wornMask = -1;
        rate = 0F;
    }

    static float drainRate(double oldest, double newest, long elapsedMs) {
        return elapsedMs <= 0 ? 0F : (float) ((oldest - newest) / (elapsedMs / 50.0));
    }

    static String duration(double seconds) {
        if (!Double.isFinite(seconds) || seconds < 0) return "";
        long total = Math.round(seconds);
        if (total >= 360000) return ">99h";
        if (total >= 3600) return String.format(Locale.ROOT, "%d:%02d:%02d", total / 3600, total / 60 % 60, total % 60);
        return String.format(Locale.ROOT, "%d:%02d", total / 60, total % 60);
    }

    private static void begin(Placement place) {
        GL11.glPushMatrix();
        GL11.glTranslatef(place.x(), place.y(), 0);
        GL11.glScalef(place.scale(), place.scale(), 1);
    }

    private static void end() {
        GL11.glPopMatrix();
    }

    static float fade(long overdue) {
        return overdue <= 0 ? 1F : Math.max(0F, 1F - overdue / (float) FADE);
    }

    static float segmentWeight(double angle, int segment) {
        float facing = (float) Math.max(0, Math.cos(angle - segment * Math.PI / 3));
        return facing * facing;
    }

    static float[] polygon(float radius, double phase) {
        float[] points = new float[12];
        for (int i = 0; i < 6; i++) {
            double angle = phase + i * Math.PI / 3;
            points[i * 2] = (float) (radius * Math.sin(angle));
            points[i * 2 + 1] = (float) (-radius * Math.cos(angle));
        }
        return points;
    }

    static float[] clipBelow(float[] polygon, float level) {
        int count = polygon.length / 2, size = 0;
        float[] clipped = new float[polygon.length * 2];
        for (int i = 0; i < count; i++) {
            int next = (i + 1) % count;
            float ax = polygon[i * 2], ay = polygon[i * 2 + 1], bx = polygon[next * 2], by = polygon[next * 2 + 1];
            boolean aInside = ay >= level, bInside = by >= level;
            if (aInside) {
                clipped[size++] = ax;
                clipped[size++] = ay;
            }
            if (aInside != bInside) {
                clipped[size++] = ax + (bx - ax) * (level - ay) / (by - ay);
                clipped[size++] = level;
            }
        }
        return Arrays.copyOf(clipped, size);
    }

    static float[] span(float[] polygon, float level) {
        float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
        for (int i = 0; i < polygon.length; i += 2) {
            if (Math.abs(polygon[i + 1] - level) > 1.0E-3F) continue;
            min = Math.min(min, polygon[i]);
            max = Math.max(max, polygon[i]);
        }
        return max - min > 1.0E-3F ? new float[] { min, max } : null;
    }

    private static void fan(Tessellator tessellator, float[] polygon, int rgb, float alpha) {
        if (polygon.length < 6) return;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        FxColor.tessellate(tessellator, rgb, alpha);
        for (int i = 0; i < polygon.length; i += 2) tessellator.addVertex(polygon[i], polygon[i + 1], 0);
        tessellator.draw();
    }

    private static void outline(Tessellator tessellator, float[] outer, float[] inner) {
        for (int i = 0; i < 6; i++) edge(tessellator, outer, inner, i, 0F);
    }

    private static void edge(Tessellator tessellator, float[] outer, float[] inner, int index, float gap) {
        int next = (index + 1) % 6;
        vertex(tessellator, outer, index, next, gap);
        vertex(tessellator, outer, index, next, 1 - gap);
        vertex(tessellator, inner, index, next, 1 - gap);
        vertex(tessellator, inner, index, next, gap);
    }

    private static void vertex(Tessellator tessellator, float[] polygon, int from, int to, float t) {
        tessellator.addVertex(
            polygon[from * 2] + (polygon[to * 2] - polygon[from * 2]) * t,
            polygon[from * 2 + 1] + (polygon[to * 2 + 1] - polygon[from * 2 + 1]) * t,
            0);
    }

    private static void arc(Tessellator tessellator, float progress, float radius, float half, int rgb, float alpha) {
        FxColor.tessellate(tessellator, rgb, alpha);
        int steps = Math.max(1, Math.round(24 * progress));
        double start = Math.toRadians(180 + DASH_SPAN), sweep = -Math.toRadians(2 * DASH_SPAN) * progress;
        for (int i = 0; i < steps; i++) {
            double a = start + sweep * i / steps, b = start + sweep * (i + 1) / steps;
            float ax = (float) Math.sin(a), ay = (float) -Math.cos(a), bx = (float) Math.sin(b),
                by = (float) -Math.cos(b);
            tessellator.addVertex(ax * (radius + half), ay * (radius + half), 0);
            tessellator.addVertex(bx * (radius + half), by * (radius + half), 0);
            tessellator.addVertex(bx * (radius - half), by * (radius - half), 0);
            tessellator.addVertex(ax * (radius - half), ay * (radius - half), 0);
        }
    }

    private static void screen(Tessellator tessellator, float x0, float y0, float x1, float y1, int accent,
        float alpha) {
        FxColor.tessellate(tessellator, PANEL_RGB, 0.78F * alpha);
        rect(tessellator, x0, y0, x1, y1);
        FxColor.tessellate(tessellator, PANEL_HI_RGB, 0.78F * alpha);
        rect(tessellator, x0, y0, x1, Math.min(y1, y0 + 3));
        FxColor.tessellate(tessellator, accent, 0.9F * alpha);
        corner(tessellator, x0, y0, 1, 1);
        corner(tessellator, x1, y0, -1, 1);
        corner(tessellator, x0, y1, 1, -1);
        corner(tessellator, x1, y1, -1, -1);
    }

    private static void corner(Tessellator tessellator, float x, float y, float dx, float dy) {
        rect(tessellator, Math.min(x, x + 3 * dx), Math.min(y, y + dy), Math.max(x, x + 3 * dx), Math.max(y, y + dy));
        rect(
            tessellator,
            Math.min(x, x + dx),
            Math.min(y + dy, y + 3 * dy),
            Math.max(x, x + dx),
            Math.max(y + dy, y + 3 * dy));
    }

    private static void rect(Tessellator tessellator, float x0, float y0, float x1, float y1) {
        tessellator.addVertex(x0, y0, 0);
        tessellator.addVertex(x0, y1, 0);
        tessellator.addVertex(x1, y1, 0);
        tessellator.addVertex(x1, y0, 0);
    }

    private static void small(FontRenderer font, String text, float x, float y, int rgb, float alpha) {
        if (text.isEmpty()) return;
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(SMALL, SMALL, 1);
        text(font, text, 0, 0, rgb, alpha);
        GL11.glPopMatrix();
    }

    private static void text(FontRenderer font, String text, float x, float y, int rgb, float alpha) {
        int opacity = Math.round(Math.max(0F, Math.min(1F, alpha)) * 255);
        if (opacity < 8) return;
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        font.drawStringWithShadow(text, Math.round(x), Math.round(y), opacity << 24 | rgb & 0xFFFFFF);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
