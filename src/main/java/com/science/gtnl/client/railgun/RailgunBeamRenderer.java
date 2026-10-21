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

import com.gtnllib.client.fx.world.FxCamera;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunRaycast;
import com.science.gtnl.common.railgun.RailgunSetting;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunBeamRenderer {

    private static final double BEAM_RANGE_RESPONSE = 26;
    private static final double BEAM_RANGE_SNAP = 4;

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
    private static final double MUZZLE_CLEARANCE = 0.12;

    private static final double[] BODY_RADII = { 0.17, 0.1, 0.035 };
    private static final double[] BODY_SPIN = { 0.22, -0.34, 0.58 };
    private static final float[] BODY_ALPHA = { 0.32F, 0.52F, 0.8F };
    private static final double[] BLOOM_WIDTHS = { 1.35, 0.72, 0.32, 0.11 };
    private static final float[] BLOOM_ALPHA = { 0.2F, 0.46F, 0.78F, 1F };

    private static final float[] COLOR = new float[3];

    private record Shot(int shooterId, Vec3d from, Vec3d to, boolean ehv, float intensity, long until) {}

    private record Beam(Vec3d from, Vec3d to, boolean ehv, float intensity, int seed) {}

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
        if (!ElectromagneticRailgun.is(stack)) return;
        if (HELD.containsKey(player.getEntityId()) || HELD.size() >= MAX_BEAMS) return;
        HELD.put(player.getEntityId(), seed(player, stack));
    }

    private static HeldBeam seed(EntityPlayer player, ItemStack stack) {
        HeldBeam held = new HeldBeam();
        held.ehv = RailgunSetting.EHV_BEAM.active(stack);
        held.range = localRange(player, stack);
        held.targetRange = held.range;
        held.lastFrameMillis = Minecraft.getSystemTime();
        held.lastUpdateTick = player.worldObj == null ? 0 : player.worldObj.getTotalWorldTime();
        return held;
    }

    private static double localRange(EntityPlayer player, ItemStack stack) {
        double range = RailgunRaycast.effectiveRange(
            RailgunData.BEAM_RANGE,
            RailgunData.modules(stack)
                .count(RailgunModule.RANGE));
        return clampRange(player, Vec3d.eye(player, 1F), Vec3d.look(player), range);
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

    public static void shot(int shooterId, Vec3d from, Vec3d to, boolean ehv, float intensity) {
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
        FxCamera.capture();
        long now = world.getTotalWorldTime();
        SHOTS.removeIf(shot -> shot.until <= now);
        applyPending(now);
        HELD.entrySet()
            .removeIf(entry -> now - entry.getValue().lastUpdateTick > UPDATE_TIMEOUT);
        List<Beam> beams = collect(now, event.partialTicks);
        retainArcTimers(beams);
        spawnAmbientArcs(beams, now);
        if (beams.isEmpty()) return;
        double time = now + event.partialTicks;
        FxRenderHelper.render(() -> { for (Beam beam : beams) body(beam, time); });
        if (!MainConfig.client.enableRailgunBloom) return;
        drawPlasma(beams, time);
        FxRenderHelper.renderGlow(Sprite.STREAK, () -> { for (Beam beam : beams) bloom(beam, time); });
        FxRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (Beam beam : beams) caps(beam, time); });
    }

    private static void retainArcTimers(List<Beam> beams) {
        Set<Integer> active = new HashSet<>(beams.size());
        for (Beam beam : beams) active.add(beam.seed);
        LAST_ARC.keySet()
            .retainAll(active);
    }

    private static void applyPending(long now) {
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
            held.range = packet.range;
            held.targetRange = held.range;
            held.lastFrameMillis = Minecraft.getSystemTime();
            HELD.put(shooterId, held);
        }
        held.ehv = packet.ehv;
        held.targetRange = packet.range;
        held.lastUpdateTick = now;
    }

    private static List<Beam> collect(long now, float partialTicks) {
        List<Beam> beams = new ArrayList<>(SHOTS.size() + HELD.size());
        for (Shot shot : SHOTS) {
            if (!RailgunClientFx.visible(shot.shooterId, shot.from)) continue;
            if (valid(shot.from, shot.to))
                beams.add(new Beam(pastMuzzle(shot.from, shot.to), shot.to, shot.ehv, shot.intensity, shot.shooterId));
        }
        World world = Minecraft.getMinecraft().theWorld;
        long millis = Minecraft.getSystemTime();
        HELD.forEach((id, held) -> {
            advance(held, millis);
            Entity shooter = world == null ? null : world.getEntityByID(id);
            Vec3d origin = RailgunMuzzleAnchor.muzzle(id, partialTicks);
            if (shooter == null || origin == null || !RailgunClientFx.visible(id, origin)) return;
            Vec3d end = aimEnd(shooter, partialTicks, Math.max(MIN_BEAM_LENGTH, Math.min(MAX_BEAM_LENGTH, held.range)));
            if (valid(origin, end)) beams.add(new Beam(pastMuzzle(origin, end), end, held.ehv, 1F, id));
        });
        return beams;
    }

    private static Vec3d aimEnd(Entity shooter, float partialTicks, double range) {
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean firstPerson = shooter == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0;
        Vec3d origin;
        Vec3d direction;
        double reach = range;
        if (firstPerson) {
            origin = FxCamera.position();
            direction = RailgunMuzzleAnchor.firstPersonAim(partialTicks);
            reach += origin.subtract(Vec3d.eye(shooter, partialTicks))
                .length();
        } else {
            origin = Vec3d.eye(shooter, partialTicks);
            direction = Vec3d.look(shooter, partialTicks);
        }
        return origin.add(direction.scale(clampRange(shooter, origin, direction, reach)));
    }

    private static void advance(HeldBeam held, long millis) {
        double seconds = Math.min(MAX_FRAME_SECONDS, Math.max(0, (millis - held.lastFrameMillis) / 1000.0));
        held.lastFrameMillis = millis;
        double difference = held.targetRange - held.range;
        if (Math.abs(difference) > BEAM_RANGE_SNAP) held.range = held.targetRange;
        else held.range += difference * (1 - Math.exp(-seconds * BEAM_RANGE_RESPONSE));
    }

    private static double clampRange(Entity shooter, Vec3d origin, Vec3d direction, double range) {
        MovingObjectPosition hit = shooter.worldObj.func_147447_a(
            origin.toMc(),
            origin.add(direction.scale(range))
                .toMc(),
            false,
            true,
            false);
        if (hit == null) return range;
        double distance = Vec3d.of(hit.hitVec)
            .subtract(origin)
            .length();
        if (distance < CLIPPED_RANGE && range > CLIPPED_RANGE * 4) return range;
        return Math.max(MIN_BLOCK_RANGE, Math.min(range, distance));
    }

    private static Vec3d pastMuzzle(Vec3d from, Vec3d to) {
        Vec3d delta = to.subtract(from);
        double length = delta.length();
        return length <= MUZZLE_CLEARANCE * 2 ? from
            : from.add(
                delta.normalize()
                    .scale(MUZZLE_CLEARANCE));
    }

    private static boolean valid(Vec3d from, Vec3d to) {
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
            FxRenderHelper.render(() -> { for (Beam beam : beams) plasma(beam); });
        } finally {
            shader.end();
        }
    }

    private static void plasma(Beam beam) {
        Vec3d delta = beam.to.subtract(beam.from);
        double length = delta.length();
        for (int segment = 0; segment < BLOOM_SEGMENTS; segment++) {
            double start = segment / (double) BLOOM_SEGMENTS, end = (segment + 1) / (double) BLOOM_SEGMENTS;
            setColor(beam.ehv, 0.35F, 0.62F * beam.intensity);
            FxRenderHelper.uvRibbon(
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
        Vec3d delta = beam.to.subtract(beam.from);
        Vec3d direction = delta.normalize();
        Vec3d right = Vec3d.rightOf(direction);
        Vec3d up = right.cross(direction)
            .normalize();
        double breathing = 1 + Math.sin(time * 0.3 + beam.seed) * 0.08;
        for (int layer = 0; layer < BODY_RADII.length; layer++) {
            double radius = BODY_RADII[layer] * breathing;
            double spin = time * BODY_SPIN[layer] + beam.seed;
            for (int segment = 0; segment < BODY_SEGMENTS; segment++) {
                double start = segment / (double) BODY_SEGMENTS, end = (segment + 1) / (double) BODY_SEGMENTS;
                Vec3d startPoint = beam.from.add(delta.scale(start));
                Vec3d endPoint = beam.from.add(delta.scale(end));
                double rotation = spin + start * Math.PI * 2, nextRotation = spin + end * Math.PI * 2;
                float flow = (float) (0.78 + 0.22 * Math.sin(time * 0.55 - segment * 0.8 + beam.seed));
                setColor(beam.ehv, layer / (BODY_RADII.length - 1F), BODY_ALPHA[layer] * flow * beam.intensity);
                for (int face = 0; face < 4; face++) {
                    double angle = face * Math.PI / 2, next = (face + 1) * Math.PI / 2;
                    FxRenderHelper.vertex(corner(startPoint, right, up, rotation + angle, radius));
                    FxRenderHelper.vertex(corner(startPoint, right, up, rotation + next, radius));
                    FxRenderHelper.vertex(corner(endPoint, right, up, nextRotation + next, radius));
                    FxRenderHelper.vertex(corner(endPoint, right, up, nextRotation + angle, radius));
                }
            }
        }
    }

    private static void bloom(Beam beam, double time) {
        Vec3d delta = beam.to.subtract(beam.from);
        for (int segment = 0; segment < BLOOM_SEGMENTS; segment++) {
            double start = segment / (double) BLOOM_SEGMENTS, end = (segment + 1) / (double) BLOOM_SEGMENTS;
            Vec3d from = beam.from.add(delta.scale(start)), to = beam.from.add(delta.scale(end));
            float pulse = (float) (0.72 + 0.28 * Math.sin(time * 0.6 - segment * 0.85 + beam.seed));
            for (int layer = 0; layer < BLOOM_WIDTHS.length; layer++) {
                setColor(beam.ehv, layer / (BLOOM_WIDTHS.length - 1F), BLOOM_ALPHA[layer] * pulse * beam.intensity);
                FxRenderHelper
                    .glowRibbon(from, to, BLOOM_WIDTHS[layer] * taper(start), BLOOM_WIDTHS[layer] * taper(end));
            }
        }
    }

    private static void caps(Beam beam, double time) {
        double breathing = 1 + Math.sin(time * 0.4 + beam.seed) * 0.12;
        float radians = (float) (time * 0.7 + beam.seed);
        setColor(beam.ehv, 0F, 0.62F * beam.intensity);
        FxRenderHelper.glow(beam.from, 0.55 * breathing, radians);
        setColor(beam.ehv, 1F, 1F * beam.intensity);
        FxRenderHelper.glow(beam.from, 0.18 * breathing, -radians);
        setColor(beam.ehv, 0F, 0.52F * beam.intensity);
        FxRenderHelper.glow(beam.to, 0.72 * breathing, -radians);
        setColor(beam.ehv, 1F, 1F * beam.intensity);
        FxRenderHelper.glow(beam.to, 0.22 * breathing, radians);
    }

    private static void setColor(boolean ehv, float mix, float alpha) {
        float[] rgb = RailgunOrbRenderer.beamColor(ehv, mix, COLOR);
        Tessellator.instance.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
    }

    private static Vec3d corner(Vec3d center, Vec3d right, Vec3d up, double angle, double radius) {
        return center.add(right.scale(Math.cos(angle) * radius))
            .add(up.scale(Math.sin(angle) * radius));
    }

    private static void spawnAmbientArcs(List<Beam> beams, long now) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        for (Beam beam : beams) {
            if (now - LAST_ARC.getOrDefault(beam.seed, -100L) < ARC_INTERVAL) continue;
            LAST_ARC.put(beam.seed, now);
            Vec3d point = beam.from.add(
                beam.to.subtract(beam.from)
                    .scale(world.rand.nextDouble()));
            Vec3d end = point.add(
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2);
            RailgunArcs.chain(point, end, 4, beam.ehv);
        }
    }
}
