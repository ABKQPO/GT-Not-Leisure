package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.RailgunGlowSprites.Sprite;
import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.client.railgun.shader.RailgunShaders;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunVec;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunBeamRenderer {

    private static final int MAX_SHOTS = 64;
    private static final int MAX_BEAMS = 64;
    private static final int SHOT_LIFETIME = 6;
    private static final int UPDATE_TIMEOUT = 6;
    private static final int ARC_INTERVAL = 3;
    private static final int BODY_SEGMENTS = 10;
    private static final int BLOOM_SEGMENTS = 8;
    private static final double MAX_BEAM_LENGTH = 512;
    private static final double MIN_BEAM_LENGTH = 0.001;
    private static final double PLASMA_WIDTH = 0.3;

    private static final double[] BODY_RADII = { 0.17, 0.1, 0.035 };
    private static final double[] BODY_SPIN = { 0.22, -0.34, 0.58 };
    private static final float[] BODY_ALPHA = { 0.22F, 0.38F, 0.6F };
    private static final double[] BLOOM_WIDTHS = { 0.5, 0.18, 0.06 };
    private static final float[] BLOOM_ALPHA = { 0.13F, 0.3F, 0.95F };

    private static final float[] EHV_INNER = { 1F, 0.3F, 0.62F };
    private static final float[] EHV_OUTER = { 1F, 0.84F, 0.94F };
    private static final float[] HV_INNER = { 0.22F, 0.72F, 1F };
    private static final float[] HV_OUTER = { 0.84F, 0.97F, 1F };

    private record Shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, float intensity, long until) {}

    private record HeldBeam(RailgunVec from, RailgunVec to, boolean ehv, long lastUpdateTick) {}

    private record Beam(RailgunVec from, RailgunVec to, boolean ehv, float intensity, int seed) {}

    private static final List<Shot> SHOTS = new ArrayList<>();
    private static final Map<Integer, HeldBeam> HELD = new HashMap<>();
    private static final Map<Integer, Long> LAST_ARC = new HashMap<>();
    private static boolean localRequested;

    public static void setLocalRequested(boolean requested) {
        localRequested = requested;
        var player = Minecraft.getMinecraft().thePlayer;
        if (!requested && player != null) HELD.remove(player.getEntityId());
    }

    public static boolean localFiring() {
        return localRequested;
    }

    public static void update(RailgunBeamUpdatePacket packet) {
        var mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        if (!packet.active
            || mc.thePlayer != null && packet.shooterId == mc.thePlayer.getEntityId() && !localRequested) {
            HELD.remove(packet.shooterId);
            return;
        }
        if (HELD.size() < MAX_BEAMS || HELD.containsKey(packet.shooterId)) HELD
            .put(packet.shooterId, new HeldBeam(packet.from, packet.to, packet.ehv, mc.theWorld.getTotalWorldTime()));
    }

    public static boolean active(int shooterId) {
        var world = Minecraft.getMinecraft().theWorld;
        HeldBeam beam = HELD.get(shooterId);
        return world != null && beam != null && world.getTotalWorldTime() - beam.lastUpdateTick <= UPDATE_TIMEOUT;
    }

    public static Set<Integer> shooters() {
        return new HashSet<>(HELD.keySet());
    }

    public static void shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv) {
        shot(shooterId, from, to, ehv, 1F);
    }

    public static void shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, float intensity) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (SHOTS.size() >= MAX_SHOTS) SHOTS.remove(0);
        SHOTS.add(new Shot(shooterId, from, to, ehv, intensity, world.getTotalWorldTime() + SHOT_LIFETIME));
    }

    public static void clear() {
        SHOTS.clear();
        HELD.clear();
        LAST_ARC.clear();
        localRequested = false;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        SHOTS.removeIf(shot -> shot.until <= now);
        HELD.entrySet()
            .removeIf(entry -> now - entry.getValue().lastUpdateTick > UPDATE_TIMEOUT);
        LAST_ARC.keySet()
            .retainAll(HELD.keySet());
        spawnAmbientArcs(now);
        if (SHOTS.isEmpty() && HELD.isEmpty()) return;

        List<Beam> beams = collect(event.partialTicks);
        if (beams.isEmpty()) return;
        double time = now + event.partialTicks;
        RailgunRenderHelper.render(() -> { for (Beam beam : beams) body(beam, time); });
        if (!MainConfig.client.enableRailgunBloom) return;
        if (!drawPlasma(beams, time))
            RailgunRenderHelper.renderGlow(Sprite.STREAK, () -> { for (Beam beam : beams) bloom(beam, time); });
        RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (Beam beam : beams) caps(beam, time); });
    }

    private static boolean drawPlasma(List<Beam> beams, double time) {
        if (!MainConfig.client.enableRailgunShaders) return false;
        RailgunShaderEffect shader = RailgunShaders.BEAM;
        if (!shader.begin()) return false;
        try {
            shader.uniform1f("time", (float) (time * 0.05));
            RailgunRenderHelper.render(() -> { for (Beam beam : beams) plasma(beam); });
            return true;
        } finally {
            shader.end();
        }
    }

    private static void plasma(Beam beam) {
        RailgunVec delta = beam.to.subtract(beam.from);
        double length = delta.length();
        for (int segment = 0; segment < BLOOM_SEGMENTS; segment++) {
            double start = segment / (double) BLOOM_SEGMENTS, end = (segment + 1) / (double) BLOOM_SEGMENTS;
            setColor(beam.ehv, 0.35F, beam.intensity);
            RailgunRenderHelper.uvRibbon(
                beam.from.add(delta.scale(start)),
                beam.from.add(delta.scale(end)),
                PLASMA_WIDTH * taper(start),
                PLASMA_WIDTH * taper(end),
                start * length + beam.seed,
                end * length + beam.seed);
        }
    }

    private static double taper(double position) {
        return 0.6 + 0.4 * Math.sin(position * Math.PI);
    }

    private static List<Beam> collect(float partialTicks) {
        List<Beam> beams = new ArrayList<>(SHOTS.size() + HELD.size());
        for (Shot shot : SHOTS) {
            RailgunVec from = RailgunVisuals.muzzle(shot.shooterId, shot.from, partialTicks);
            if (valid(from, shot.to)) beams.add(new Beam(from, shot.to, shot.ehv, shot.intensity, shot.shooterId));
        }
        HELD.forEach((id, held) -> {
            RailgunVec from = RailgunVisuals.muzzle(id, held.from, partialTicks);
            if (valid(from, held.to)) beams.add(new Beam(from, held.to, held.ehv, 1F, id));
        });
        return beams;
    }

    private static boolean valid(RailgunVec from, RailgunVec to) {
        double length = to.subtract(from)
            .length();
        return length > MIN_BEAM_LENGTH && length < MAX_BEAM_LENGTH;
    }

    private static void body(Beam beam, double time) {
        RailgunVec delta = beam.to.subtract(beam.from);
        RailgunVec direction = delta.normalize();
        RailgunVec reference = Math.abs(direction.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec right = direction.cross(reference)
            .normalize();
        RailgunVec up = direction.cross(right)
            .normalize();
        double breathing = 1 + Math.sin(time * 0.3 + beam.seed) * 0.08;
        for (int layer = 0; layer < BODY_RADII.length; layer++) {
            double radius = BODY_RADII[layer] * breathing;
            double spin = time * BODY_SPIN[layer] + beam.seed;
            for (int segment = 0; segment < BODY_SEGMENTS; segment++) {
                double start = segment / (double) BODY_SEGMENTS, end = (segment + 1) / (double) BODY_SEGMENTS;
                RailgunVec startPoint = beam.from.add(delta.scale(start));
                RailgunVec endPoint = beam.from.add(delta.scale(end));
                double rotation = spin + start * Math.PI * 2, nextRotation = spin + end * Math.PI * 2;
                float flow = (float) (0.78 + 0.22 * Math.sin(time * 0.55 - segment * 0.8 + beam.seed));
                setColor(beam.ehv, layer / (BODY_RADII.length - 1F), BODY_ALPHA[layer] * flow * beam.intensity);
                for (int face = 0; face < 4; face++) {
                    double angle = face * Math.PI / 2, next = (face + 1) * Math.PI / 2;
                    RailgunRenderHelper.vertex(corner(startPoint, right, up, rotation + angle, radius));
                    RailgunRenderHelper.vertex(corner(startPoint, right, up, rotation + next, radius));
                    RailgunRenderHelper.vertex(corner(endPoint, right, up, nextRotation + next, radius));
                    RailgunRenderHelper.vertex(corner(endPoint, right, up, nextRotation + angle, radius));
                }
            }
        }
    }

    private static void bloom(Beam beam, double time) {
        RailgunVec delta = beam.to.subtract(beam.from);
        for (int segment = 0; segment < BLOOM_SEGMENTS; segment++) {
            double start = segment / (double) BLOOM_SEGMENTS, end = (segment + 1) / (double) BLOOM_SEGMENTS;
            RailgunVec from = beam.from.add(delta.scale(start)), to = beam.from.add(delta.scale(end));
            float pulse = (float) (0.72 + 0.28 * Math.sin(time * 0.6 - segment * 0.85 + beam.seed));
            for (int layer = 0; layer < BLOOM_WIDTHS.length; layer++) {
                setColor(beam.ehv, layer / (BLOOM_WIDTHS.length - 1F), BLOOM_ALPHA[layer] * pulse * beam.intensity);
                RailgunRenderHelper
                    .glowRibbon(from, to, BLOOM_WIDTHS[layer] * taper(start), BLOOM_WIDTHS[layer] * taper(end));
            }
        }
    }

    private static void caps(Beam beam, double time) {
        double breathing = 1 + Math.sin(time * 0.4 + beam.seed) * 0.12;
        float radians = (float) (time * 0.7 + beam.seed);
        setColor(beam.ehv, 0F, 0.42F * beam.intensity);
        RailgunRenderHelper.glow(beam.from, 0.3 * breathing, radians);
        setColor(beam.ehv, 1F, 0.85F * beam.intensity);
        RailgunRenderHelper.glow(beam.from, 0.1 * breathing, -radians);
        setColor(beam.ehv, 0F, 0.34F * beam.intensity);
        RailgunRenderHelper.glow(beam.to, 0.45 * breathing, -radians);
        setColor(beam.ehv, 1F, 0.8F * beam.intensity);
        RailgunRenderHelper.glow(beam.to, 0.14 * breathing, radians);
    }

    private static void setColor(boolean ehv, float mix, float alpha) {
        float[] inner = ehv ? EHV_INNER : HV_INNER, outer = ehv ? EHV_OUTER : HV_OUTER;
        Tessellator.instance.setColorRGBA_F(
            inner[0] + (outer[0] - inner[0]) * mix,
            inner[1] + (outer[1] - inner[1]) * mix,
            inner[2] + (outer[2] - inner[2]) * mix,
            alpha);
    }

    private static RailgunVec corner(RailgunVec center, RailgunVec right, RailgunVec up, double angle, double radius) {
        return center.add(right.scale(Math.cos(angle) * radius))
            .add(up.scale(Math.sin(angle) * radius));
    }

    private static void spawnAmbientArcs(long now) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        HELD.forEach((id, beam) -> {
            if (now - LAST_ARC.getOrDefault(id, -100L) < ARC_INTERVAL) return;
            LAST_ARC.put(id, now);
            RailgunVec point = beam.from.add(
                beam.to.subtract(beam.from)
                    .scale(world.rand.nextDouble()));
            RailgunVec end = point.add(
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2);
            RailgunArcRenderer.spawnChain(point, end, 4, beam.ehv);
        });
    }
}
