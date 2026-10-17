package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.RailgunRenderHelper.Sprite;
import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunDefaults;
import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.common.railgun.RailgunRaycast;
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
    private static final double MIN_BLOCK_RANGE = 0.5;
    private static final double CLIPPED_RANGE = 1.5;
    private static final double MAX_FRAME_SECONDS = 0.25;
    private static final double PLASMA_WIDTH = 0.42;

    private static final double[] BODY_RADII = { 0.17, 0.1, 0.035 };
    private static final double[] BODY_SPIN = { 0.22, -0.34, 0.58 };
    private static final float[] BODY_ALPHA = { 0.32F, 0.52F, 0.8F };
    private static final double[] BLOOM_WIDTHS = { 1.35, 0.72, 0.32, 0.11 };
    private static final float[] BLOOM_ALPHA = { 0.2F, 0.46F, 0.78F, 1F };

    private static final float[] EHV_INNER = { 1F, 0.3F, 0.62F };
    private static final float[] EHV_OUTER = { 1F, 0.84F, 0.94F };
    private static final float[] HV_INNER = { 0.22F, 0.72F, 1F };
    private static final float[] HV_OUTER = { 0.84F, 0.97F, 1F };

    private record Shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, float intensity, long until) {}

    private record Beam(RailgunVec from, RailgunVec to, boolean ehv, float intensity, int seed) {}

    private static class HeldBeam {

        boolean ehv;
        double range;
        double targetRange;
        long lastUpdateTick;
        long lastFrameMillis;
    }

    private static final List<Shot> SHOTS = new ArrayList<>();
    private static final Map<Integer, HeldBeam> HELD = new HashMap<>();
    private static final Map<Integer, RailgunBeamUpdatePacket> PENDING = new ConcurrentHashMap<>();
    private static final Map<Integer, Long> LAST_ARC = new HashMap<>();
    private static boolean localRequested;

    public static void submit(RailgunBeamUpdatePacket packet) {
        if (PENDING.size() >= MAX_BEAMS && !PENDING.containsKey(packet.shooterId)) return;
        PENDING.put(packet.shooterId, packet);
    }

    public static void setLocalRequested(boolean requested) {
        localRequested = requested;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null) return;
        if (!requested) {
            HELD.remove(player.getEntityId());
            return;
        }
        ItemStack stack = player.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ElectromagneticRailgun)) return;
        if (HELD.containsKey(player.getEntityId()) || HELD.size() >= MAX_BEAMS) return;
        HELD.put(player.getEntityId(), seed(player, stack));
    }

    private static HeldBeam seed(EntityPlayer player, ItemStack stack) {
        HeldBeam held = new HeldBeam();
        held.ehv = ElectromagneticRailgun.isExtremeHighVoltage(stack);
        held.range = localRange(player, stack);
        held.targetRange = held.range;
        held.lastFrameMillis = Minecraft.getSystemTime();
        held.lastUpdateTick = player.worldObj == null ? 0 : player.worldObj.getTotalWorldTime();
        return held;
    }

    private static double localRange(EntityPlayer player, ItemStack stack) {
        double range = RailgunRaycast.effectiveRange(
            RailgunDefaults.BEAM_RANGE,
            RailgunData.modules(stack)
                .count(RailgunModuleType.RANGE));
        return clampRange(player, RailgunVec.eye(player, 1F), RailgunVec.look(player), range);
    }

    public static boolean localFiring() {
        return localRequested;
    }

    public static boolean active(int shooterId) {
        World world = Minecraft.getMinecraft().theWorld;
        HeldBeam beam = HELD.get(shooterId);
        return world != null && beam != null && world.getTotalWorldTime() - beam.lastUpdateTick <= UPDATE_TIMEOUT;
    }

    public static Set<Integer> shooters() {
        return new HashSet<>(HELD.keySet());
    }

    public static void shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, float intensity) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (SHOTS.size() >= MAX_SHOTS) SHOTS.remove(0);
        SHOTS.add(new Shot(shooterId, from, to, ehv, intensity, world.getTotalWorldTime() + SHOT_LIFETIME));
    }

    public static void clear() {
        SHOTS.clear();
        HELD.clear();
        PENDING.clear();
        LAST_ARC.clear();
        localRequested = false;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        RailgunCamera.capture();
        long now = world.getTotalWorldTime();
        SHOTS.removeIf(shot -> shot.until <= now);
        drain(now);
        HELD.entrySet()
            .removeIf(entry -> now - entry.getValue().lastUpdateTick > UPDATE_TIMEOUT);
        List<Beam> beams = collect(now, event.partialTicks);
        retainArcTimers(beams);
        spawnAmbientArcs(beams, now);
        if (beams.isEmpty()) return;
        double time = now + event.partialTicks;
        RailgunRenderHelper.render(() -> { for (Beam beam : beams) body(beam, time); });
        if (!MainConfig.client.enableRailgunBloom) return;
        drawPlasma(beams, time);
        RailgunRenderHelper.renderGlow(Sprite.STREAK, () -> { for (Beam beam : beams) bloom(beam, time); });
        RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (Beam beam : beams) caps(beam, time); });
    }

    private static void retainArcTimers(List<Beam> beams) {
        Set<Integer> active = new HashSet<>(beams.size());
        for (Beam beam : beams) active.add(beam.seed);
        LAST_ARC.keySet()
            .retainAll(active);
    }

    private static void drain(long now) {
        for (Map.Entry<Integer, RailgunBeamUpdatePacket> entry : PENDING.entrySet()) {
            RailgunBeamUpdatePacket packet = entry.getValue();
            if (!PENDING.remove(entry.getKey(), packet)) continue;
            apply(packet, now);
        }
    }

    private static void apply(RailgunBeamUpdatePacket packet, long now) {
        int shooterId = packet.shooterId;
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean local = minecraft.thePlayer != null && minecraft.thePlayer.getEntityId() == shooterId;
        if (packet.state == RailgunBeamUpdatePacket.STATE_STOP || local && !localRequested) {
            HELD.remove(shooterId);
            return;
        }
        HeldBeam held = HELD.get(shooterId);
        if (held == null) {
            if (HELD.size() >= MAX_BEAMS) return;
            held = new HeldBeam();
            held.range = packet.range();
            held.targetRange = held.range;
            held.lastFrameMillis = Minecraft.getSystemTime();
            HELD.put(shooterId, held);
        }
        held.ehv = packet.ehv;
        if (packet.state == RailgunBeamUpdatePacket.STATE_MOVE) held.targetRange = packet.range();
        held.lastUpdateTick = now;
    }

    private static List<Beam> collect(long now, float partialTicks) {
        List<Beam> beams = new ArrayList<>(SHOTS.size() + HELD.size());
        for (Shot shot : SHOTS) {
            RailgunVec from = RailgunRenderHelper.muzzle(shot.shooterId, shot.from, partialTicks);
            if (valid(from, shot.to)) beams.add(new Beam(from, shot.to, shot.ehv, shot.intensity, shot.shooterId));
        }
        World world = Minecraft.getMinecraft().theWorld;
        long millis = Minecraft.getSystemTime();
        HELD.forEach((id, held) -> {
            advance(held, millis);
            Entity shooter = world == null ? null : world.getEntityByID(id);
            RailgunVec origin = RailgunRenderHelper.muzzle(id, partialTicks);
            if (shooter == null || origin == null) return;
            RailgunVec end = aimEnd(
                shooter,
                partialTicks,
                Math.max(MIN_BEAM_LENGTH, Math.min(MAX_BEAM_LENGTH, held.range)));
            if (valid(origin, end)) beams.add(new Beam(origin, end, held.ehv, 1F, id));
        });
        return beams;
    }

    private static RailgunVec aimEnd(Entity shooter, float partialTicks, double range) {
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean firstPerson = shooter == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0;
        RailgunVec origin;
        RailgunVec direction;
        double reach = range;
        if (firstPerson) {
            origin = RailgunCamera.position();
            direction = RailgunCamera.forward();
            reach += origin.subtract(RailgunVec.eye(shooter, partialTicks))
                .length();
        } else {
            origin = RailgunVec.eye(shooter, partialTicks);
            direction = RailgunVec.look(shooter, partialTicks);
        }
        return origin.add(direction.scale(clampRange(shooter, origin, direction, reach)));
    }

    private static void advance(HeldBeam held, long millis) {
        double seconds = Math.min(MAX_FRAME_SECONDS, Math.max(0, (millis - held.lastFrameMillis) / 1000.0));
        held.lastFrameMillis = millis;
        double difference = held.targetRange - held.range;
        if (Math.abs(difference) > RailgunDefaults.BEAM_RANGE_SNAP) held.range = held.targetRange;
        else held.range += difference * (1 - Math.exp(-seconds * RailgunDefaults.BEAM_RANGE_RESPONSE));
    }

    private static double clampRange(Entity shooter, RailgunVec origin, RailgunVec direction, double range) {
        MovingObjectPosition hit = shooter.worldObj.func_147447_a(
            origin.toMc(),
            origin.add(direction.scale(range))
                .toMc(),
            false,
            true,
            false);
        if (hit == null) return range;
        double distance = RailgunVec.of(hit.hitVec)
            .subtract(origin)
            .length();
        if (distance < CLIPPED_RANGE && range > CLIPPED_RANGE * 4) return range;
        return Math.max(MIN_BLOCK_RANGE, Math.min(range, distance));
    }

    private static boolean valid(RailgunVec from, RailgunVec to) {
        if (from == null || to == null) return false;
        double length = to.subtract(from)
            .length();
        return length > MIN_BEAM_LENGTH && length < MAX_BEAM_LENGTH;
    }

    private static void drawPlasma(List<Beam> beams, double time) {
        if (!MainConfig.client.enableRailgunShaders) return;
        RailgunShaderEffect shader = RailgunShaderEffect.BEAM;
        if (!shader.begin()) return;
        try {
            shader.uniform1f("time", (float) (time * 0.05));
            RailgunRenderHelper.render(() -> { for (Beam beam : beams) plasma(beam); });
        } finally {
            shader.end();
        }
    }

    private static void plasma(Beam beam) {
        RailgunVec delta = beam.to.subtract(beam.from);
        double length = delta.length();
        for (int segment = 0; segment < BLOOM_SEGMENTS; segment++) {
            double start = segment / (double) BLOOM_SEGMENTS, end = (segment + 1) / (double) BLOOM_SEGMENTS;
            setColor(beam.ehv, 0.35F, 0.62F * beam.intensity);
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

    private static void body(Beam beam, double time) {
        RailgunVec delta = beam.to.subtract(beam.from);
        RailgunVec direction = delta.normalize();
        RailgunVec right = RailgunVec.rightOf(direction);
        RailgunVec up = right.cross(direction)
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
        setColor(beam.ehv, 0F, 0.62F * beam.intensity);
        RailgunRenderHelper.glow(beam.from, 0.55 * breathing, radians);
        setColor(beam.ehv, 1F, 1F * beam.intensity);
        RailgunRenderHelper.glow(beam.from, 0.18 * breathing, -radians);
        setColor(beam.ehv, 0F, 0.52F * beam.intensity);
        RailgunRenderHelper.glow(beam.to, 0.72 * breathing, -radians);
        setColor(beam.ehv, 1F, 1F * beam.intensity);
        RailgunRenderHelper.glow(beam.to, 0.22 * breathing, radians);
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

    private static void spawnAmbientArcs(List<Beam> beams, long now) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        for (Beam beam : beams) {
            if (now - LAST_ARC.getOrDefault(beam.seed, -100L) < ARC_INTERVAL) continue;
            LAST_ARC.put(beam.seed, now);
            RailgunVec point = beam.from.add(
                beam.to.subtract(beam.from)
                    .scale(world.rand.nextDouble()));
            RailgunVec end = point.add(
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2);
            RailgunArcRenderer.spawnChain(point, end, 4, beam.ehv);
        }
    }
}
