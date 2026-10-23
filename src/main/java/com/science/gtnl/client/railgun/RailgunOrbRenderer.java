package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.function.UnaryOperator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxArcRenderer.Color;
import com.gtnllib.client.fx.world.FxArcRenderer.Style;
import com.gtnllib.client.fx.world.FxCamera;
import com.gtnllib.client.fx.world.FxMuzzleAnchor;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.railgun.RailgunBolts.Bolt;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunOrbRenderer {

    private static final int MIN_TRAVEL_TICKS = 3;
    private static final int MAX_TRAVEL_TICKS = 8;
    private static final double BLOCKS_PER_TICK = 22;
    private static final int MAX_ORBS = 32;
    private static final int TRAIL_SEGMENTS = 7;
    private static final int SPHERE_LONGITUDES = 24;
    private static final int SPHERE_LATITUDES = 12;
    private static final double SPHERE_RIM = 1.35;

    private static final float[] COLOR = new float[3];
    public static final float CALM_UNTIL = 0.3F, FLARE_FROM = 0.85F;
    public static final double BREATH_SPEED = 0.12, THROB_SPEED = 0.9;
    public static final double BREATH_DEPTH = 0.03, THROB_DEPTH = 0.1;
    public static final double TREMBLE_DEPTH = 0.12, SHAKE_DEPTH = 0.15;
    public static final int FLARE_PERIOD = 6;
    private static final float[] HIGH_INNER = { 0.35F, 0.86F, 1F }, HIGH_OUTER = { 0.94F, 1F, 1F };
    private static final float[] EXTREME_INNER = { 1F, 0.42F, 0.68F }, EXTREME_OUTER = { 1F, 0.94F, 1F };
    private static final float[] BEAM_EHV_INNER = { 1F, 0.3F, 0.62F }, BEAM_EHV_OUTER = { 1F, 0.84F, 0.94F };
    private static final float[] BEAM_HV_INNER = { 0.22F, 0.72F, 1F }, BEAM_HV_OUTER = { 0.84F, 0.97F, 1F };
    public static final Style PLASMA_ARC = new Style(
        new Color(1F, 0.3F, 0.6F),
        new Color(1F, 0.55F, 0.75F),
        new Color(1F, 0.8F, 0.9F),
        new Color(1F, 0.38F, 0.68F),
        new Color(1F, 0.75F, 0.88F),
        1);
    public static final Style HIGH_VOLTAGE_ARC = new Style(
        new Color(0.25F, 0.75F, 1F),
        new Color(0.55F, 0.88F, 1F),
        new Color(0.85F, 0.97F, 1F),
        new Color(0.4F, 0.82F, 1F),
        new Color(0.7F, 0.94F, 1F),
        1);
    public static final Style IMPACT_SPARK_ARC = new Style(
        new Color(0.9F, 0.25F, 0.55F),
        new Color(0.95F, 0.535F, 0.715F),
        new Color(1F, 0.82F, 0.88F),
        new Color(0.95F, 0.33F, 0.63F),
        new Color(1F, 0.76F, 0.86F),
        0.8125);
    public static final int HUD_EMPTY_RGB = 0x3C6E8F, HUD_EHV1_RGB = 0x50CFFF, HUD_EHV2_RGB = 0x8798FF,
        HUD_EHV3_RGB = 0xFF509A;
    public static final float SHOCKWAVE_RED = 1F, SHOCKWAVE_GREEN = 0.5F, SHOCKWAVE_BLUE = 0.75F;

    private static final double ORB_MIN_RADIUS = 0.07;
    private static final double ORB_CHARGE_RADIUS = 0.12;
    private static final double ORB_TIER_RADIUS = 0.055;
    private static final double FLIGHT_SHRINK = 0.05;
    private static final float LAUNCHED_STRENGTH = 1F, LAUNCHED_BURST = 0.45F, LAUNCHED_TENSION = 0.6F;
    private static final int FILAMENTS = 3, FILAMENT_SEGMENTS = 8;
    private static final double FILAMENT_SPEED = 0.34, FILAMENT_WOBBLE_SPEED = 0.17, FILAMENT_REACH = 1.7;
    private static final double FILAMENT_WIDTH = 0.11, FILAMENT_MIN_WIDTH = 0.008, FILAMENT_CRACKLE = 0.09,
        FILAMENT_GAP = 0.1;
    private static final float FILAMENT_GLOW_ALPHA = 0.2F, FILAMENT_CORE_ALPHA = 0.8F;
    private static final float BOLT_GLOW_MIX = 0.3F, BOLT_GLOW_ALPHA = 0.55F, BOLT_CORE_MIX = 0.85F;
    private static final double BOLT_GLOW_WIDTH = 2.2, BOLT_CORE_WIDTH = 0.45;

    private static final double[] SHELL_RADII = { 1, 0.68, 0.42 };
    private static final double[] SHELL_MIX = { 0.15, 0.55, 1 };
    private static final float[] SHELL_ALPHA = { 0.62F, 0.85F, 1F };

    private record LaunchedOrb(int shooterId, Vec3d from, Vec3d target, int tier, long born, int travelTicks,
        Runnable onArrive) {}

    private record OrbViz(Vec3d center, Vec3d direction, Vec3d tail, double radius, int tier, int seed, double time,
        float strength, float burst, float tension, Vec3d anchor) {}

    private static final List<LaunchedOrb> ORBS = new ArrayList<>();

    public static void launch(int shooterId, Vec3d from, Vec3d target, int tier, Runnable onArrive) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (from == null || !from.finite() || target == null || !target.finite()) return;
        if (!MainConfig.client.enableRailgunBloom) {
            if (onArrive != null) onArrive.run();
            return;
        }
        if (ORBS.size() >= MAX_ORBS) land(ORBS.remove(0));
        ORBS.add(
            new LaunchedOrb(
                shooterId,
                from,
                target,
                tier,
                world.getTotalWorldTime(),
                travelTicks(from, target),
                onArrive));
    }

    public static int travelTicks(Vec3d from, Vec3d to) {
        double squared = from.distanceToSqr(to);
        if (squared <= 0) return MIN_TRAVEL_TICKS;
        double ticks = Math.sqrt(squared) / BLOCKS_PER_TICK;
        return Math.max(MIN_TRAVEL_TICKS, Math.min(MAX_TRAVEL_TICKS, (int) Math.round(ticks)));
    }

    public static void clear() {
        ORBS.clear();
    }

    public static float[] color(int tier, float mix, float[] out) {
        boolean extreme = tier >= RailgunChargeTier.EHV3.ordinal();
        return blend(extreme ? EXTREME_INNER : HIGH_INNER, extreme ? EXTREME_OUTER : HIGH_OUTER, mix, out);
    }

    public static float[] beamColor(boolean ehv, float mix, float[] out) {
        return blend(ehv ? BEAM_EHV_INNER : BEAM_HV_INNER, ehv ? BEAM_EHV_OUTER : BEAM_HV_OUTER, mix, out);
    }

    private static float[] blend(float[] inner, float[] outer, float mix, float[] out) {
        out[0] = inner[0] + (outer[0] - inner[0]) * mix;
        out[1] = inner[1] + (outer[1] - inner[1]) * mix;
        out[2] = inner[2] + (outer[2] - inner[2]) * mix;
        return out;
    }

    public static float tension(float progress) {
        if (!Float.isFinite(progress)) return 0F;
        return FxEasing.smoothStep(MathUtils.clampUnit((progress - CALM_UNTIL) / (1F - CALM_UNTIL)));
    }

    public static double pulse(float tension, double time) {
        return 1 + BREATH_DEPTH * (1 - tension) * Math.sin(time * BREATH_SPEED)
            + THROB_DEPTH * tension * tension * Math.sin(time * THROB_SPEED);
    }

    public static double tremble(float tension, int owner, long tick) {
        return 1 + TREMBLE_DEPTH * tension * tension * tension * (MathUtils.hashUnit(owner, -2, 0, tick) * 2 - 1);
    }

    public static double shake(float tension, int owner, long tick, int axis) {
        return SHAKE_DEPTH * tension * tension * tension * (MathUtils.hashUnit(owner, -3, axis, tick) * 2 - 1);
    }

    public static float flare(float tension, double time) {
        if (tension <= FLARE_FROM) return 0F;
        double phase = (time % FLARE_PERIOD + FLARE_PERIOD) % FLARE_PERIOD / FLARE_PERIOD;
        double decay = 1 - phase;
        return (float) ((tension - FLARE_FROM) / (1 - FLARE_FROM) * decay * decay * decay);
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        if (MainConfig.client.enableRailgunBloom) {
            FxCamera.capture();
            List<OrbViz> orbs = collect(now, event.partialTicks);
            if (!orbs.isEmpty()) {
                FxRenderHelper.renderGlow(Sprite.STREAK, () -> {
                    for (OrbViz orb : orbs) trail(orb);
                    for (OrbViz orb : orbs) filaments(orb);
                    for (OrbViz orb : orbs) boltGlow(orb);
                });
                FxRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (OrbViz orb : orbs) outerHalo(orb); });
                drawBody(orbs, now + event.partialTicks);
                FxRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (OrbViz orb : orbs) innerHalo(orb); });
                FxRenderHelper.render(() -> { for (OrbViz orb : orbs) boltCore(orb); });
            }
        }
        arrive(now);
    }

    private static void drawBody(List<OrbViz> orbs, double time) {
        if (MainConfig.client.enableRailgunShaders) {
            RailgunShaderEffect shader = RailgunShaderEffect.ORB;
            if (shader.begin()) {
                try {
                    shader.uniform1f("time", (float) (time * 0.05));
                    FxRenderHelper.render(() -> { for (OrbViz orb : orbs) shells(orb); });
                } finally {
                    shader.end();
                }
                return;
            }
        }
        FxRenderHelper.render(() -> { for (OrbViz orb : orbs) shells(orb); });
    }

    private static void shells(OrbViz orb) {
        for (int shell = 0; shell < SHELL_RADII.length; shell++) {
            float[] rgb = mix(orb.tier(), (float) SHELL_MIX[shell]);
            FxRenderHelper.sphere(
                orb.center(),
                orb.radius() * SHELL_RADII[shell],
                SPHERE_LONGITUDES,
                SPHERE_LATITUDES,
                rgb[0],
                rgb[1],
                rgb[2],
                SHELL_ALPHA[shell] * orb.strength(),
                SPHERE_RIM);
        }
    }

    private static void arrive(long now) {
        for (int index = ORBS.size() - 1; index >= 0; index--) {
            LaunchedOrb orb = ORBS.get(index);
            if (now < orb.born + orb.travelTicks) continue;
            land(ORBS.remove(index));
        }
    }

    private static void land(LaunchedOrb orb) {
        if (orb.onArrive != null) orb.onArrive.run();
    }

    private static List<OrbViz> collect(long now, float partialTicks) {
        List<OrbViz> orbs = new ArrayList<>(ORBS.size() + 1);
        for (Object entity : Minecraft.getMinecraft().theWorld.playerEntities) {
            OrbViz charging = charging((EntityPlayer) entity, now, partialTicks);
            if (charging != null) orbs.add(charging);
        }
        for (LaunchedOrb orb : ORBS) {
            OrbViz launched = launched(orb, now, partialTicks);
            if (launched != null) orbs.add(launched);
        }
        return orbs;
    }

    private static OrbViz charging(EntityPlayer player, long now, float partialTicks) {
        if (!RailgunCharge.charging(player)) return null;
        Vec3d at = Vec3d.pos(player);
        if (!RailgunClientFx.visible(player.getEntityId(), at)) return null;
        float progress = RailgunCharge.progress(player);
        Vec3d direction = Vec3d.look(player, partialTicks);
        float tension = tension(progress);
        int id = player.getEntityId();
        double time = now + partialTicks;
        double radius = (ORB_MIN_RADIUS + ORB_CHARGE_RADIUS * progress * progress) * pulse(tension, time)
            * tremble(tension, id, now);
        Vec3d right = Vec3d.rightOf(direction), up = right.cross(direction)
            .normalize();
        Vec3d anchor = FxMuzzleAnchor.muzzle(id, at, partialTicks);
        Vec3d muzzle = anchor
            .add(direction.scale(RailgunBolts.ORB_MUZZLE_GAP * (1 - progress) + radius * RailgunBolts.ORB_CLEARANCE))
            .add(right.scale(shake(tension, id, now, 0) * radius))
            .add(up.scale(shake(tension, id, now, 1) * radius));
        return new OrbViz(
            muzzle,
            direction,
            null,
            radius,
            RailgunCharge.tier(player)
                .ordinal(),
            id,
            time,
            0.35F + 0.65F * progress,
            progress,
            tension,
            anchor);
    }

    private static OrbViz launched(LaunchedOrb orb, long now, float partialTicks) {
        double raw = (now - orb.born + partialTicks) / orb.travelTicks;
        if (raw < 0) return null;
        double eased = 1 - (1 - Math.min(1, raw)) * (1 - Math.min(1, raw));
        Vec3d from = orb.from;
        Vec3d delta = orb.target.subtract(from);
        if (delta.length() < 1.0E-4) return null;
        Vec3d head = from.add(delta.scale(eased));
        double radius = ORB_MIN_RADIUS + ORB_TIER_RADIUS * orb.tier - FLIGHT_SHRINK * eased;
        return new OrbViz(
            head,
            delta.normalize(),
            from,
            radius,
            orb.tier,
            orb.shooterId,
            now + partialTicks,
            LAUNCHED_STRENGTH,
            LAUNCHED_BURST,
            LAUNCHED_TENSION,
            null);
    }

    private static List<Bolt> bolts(OrbViz orb) {
        return orb.burst() <= 0F ? List.of() : RailgunBolts.orb(orb.seed(), (long) orb.time(), orb.burst());
    }

    private static List<Bolt> legs(OrbViz orb) {
        if (orb.anchor() == null || orb.anchor()
            .distanceToSqr(orb.center()) < orb.radius() * orb.radius()) return List.of();
        return RailgunBolts.tethers(orb.seed(), (long) orb.time(), orb.burst(), orb.anchor(), orb.center());
    }

    private static void boltGlow(OrbViz orb) {
        for (Bolt bolt : bolts(orb)) glow(orb, bolt, local -> boltPoint(orb, local));
        for (Bolt bolt : legs(orb)) glow(orb, bolt, world -> world);
    }

    private static void boltCore(OrbViz orb) {
        for (Bolt bolt : bolts(orb)) core(orb, bolt, local -> boltPoint(orb, local));
        for (Bolt bolt : legs(orb)) core(orb, bolt, world -> world);
    }

    private static void glow(OrbViz orb, Bolt bolt, UnaryOperator<Vec3d> place) {
        setColor(orb.tier(), BOLT_GLOW_MIX, bolt.alpha() * BOLT_GLOW_ALPHA);
        FxRenderHelper.path(placed(bolt, place), bolt.width() * orb.radius() * BOLT_GLOW_WIDTH);
    }

    private static void core(OrbViz orb, Bolt bolt, UnaryOperator<Vec3d> place) {
        setColor(orb.tier(), BOLT_CORE_MIX, bolt.alpha());
        FxRenderHelper.path(placed(bolt, place), bolt.width() * orb.radius() * BOLT_CORE_WIDTH);
    }

    private static List<Vec3d> placed(Bolt bolt, UnaryOperator<Vec3d> place) {
        List<Vec3d> points = new ArrayList<>(
            bolt.points()
                .size());
        for (Vec3d point : bolt.points()) points.add(place.apply(point));
        return points;
    }

    private static Vec3d boltPoint(OrbViz orb, Vec3d local) {
        return orb.center()
            .add(local.scale(orb.radius()));
    }

    private static void outerHalo(OrbViz orb) {
        float tension = orb.tension(), radians = (float) (orb.time() * 0.8 + orb.seed());
        setColor(orb.tier(), 0F, 0.45F + 0.35F * tension);
        FxRenderHelper.glow(orb.center(), orb.radius() * (2.8 + 1.6 * tension), radians);
        float flare = flare(tension, orb.time());
        if (flare <= 0.01F) return;
        setColor(orb.tier(), 0.6F, 0.55F * flare);
        FxRenderHelper.glow(orb.center(), orb.radius() * (3.2 + 2.4 * flare), -radians);
    }

    private static void innerHalo(OrbViz orb) {
        double radius = orb.radius();
        float radians = (float) (orb.time() * 0.8 + orb.seed());
        setColor(orb.tier(), 0.5F, 0.9F);
        FxRenderHelper.glow(orb.center(), radius * (1.8 + 0.6 * orb.tension()), -radians);
        setColor(orb.tier(), 1F, 1F);
        FxRenderHelper.glow(orb.center(), radius, radians);
    }

    private static void trail(OrbViz orb) {
        Vec3d tail = orb.tail();
        if (tail == null) return;
        Vec3d delta = orb.center()
            .subtract(tail);
        for (int segment = 0; segment < TRAIL_SEGMENTS; segment++) {
            double start = segment / (double) TRAIL_SEGMENTS, end = (segment + 1) / (double) TRAIL_SEGMENTS;
            double width = orb.radius() * (0.02 + 1.6 * start * start);
            double nextWidth = orb.radius() * (0.02 + 1.6 * end * end);
            setColor(orb.tier(), (float) start, (float) (0.22 + 0.95 * start * start));
            FxRenderHelper.glowRibbon(tail.add(delta.scale(start)), tail.add(delta.scale(end)), width, nextWidth);
        }
    }

    private static void filaments(OrbViz orb) {
        Vec3d direction = orb.direction();
        Vec3d right = Vec3d.rightOf(direction);
        Vec3d up = right.cross(direction)
            .normalize();
        double reach = orb.radius() * FILAMENT_REACH;
        double width = Math.max(FILAMENT_MIN_WIDTH, orb.radius() * FILAMENT_WIDTH);
        long tick = (long) orb.time();
        int count = 1 + Math.round(orb.burst() * (FILAMENTS - 1));
        double agitation = 0.3 + 1.2 * orb.tension();
        for (int filament = 0; filament < count; filament++) {
            double phase = orb.time() * FILAMENT_SPEED + filament * (Math.PI * 2 / FILAMENTS) + orb.seed() * 0.37;
            double wobblePhase = orb.time() * FILAMENT_WOBBLE_SPEED + filament * 1.7 + orb.seed() * 0.11;
            float flicker = (float) (0.55 + 0.45 * MathUtils.hashUnit(orb.seed(), filament, -1, tick));
            List<Vec3d> run = new ArrayList<>(FILAMENT_SEGMENTS + 1);
            int first = 0;
            run.add(filamentPoint(orb, direction, right, up, reach, phase, wobblePhase, 0, 0, agitation));
            for (int segment = 1; segment <= FILAMENT_SEGMENTS; segment++) {
                double crackle = (MathUtils.hashUnit(orb.seed(), filament, segment, tick) * 2 - 1) * agitation;
                Vec3d current = filamentPoint(
                    orb,
                    direction,
                    right,
                    up,
                    reach,
                    phase,
                    wobblePhase,
                    segment / (double) FILAMENT_SEGMENTS,
                    crackle,
                    agitation);
                boolean gap = MathUtils.hashUnit(orb.seed(), filament, segment + FILAMENT_SEGMENTS, tick)
                    < FILAMENT_GAP * agitation;
                if (gap) {
                    filament(orb, run, first, width, flicker);
                    run = new ArrayList<>(FILAMENT_SEGMENTS + 1);
                    first = segment;
                }
                run.add(current);
            }
            filament(orb, run, first, width, flicker);
        }
    }

    private static void filament(OrbViz orb, List<Vec3d> run, int first, double width, float flicker) {
        IntToDoubleFunction taper = index -> 0.5 + 0.5 * Math.sin(Math.PI * (first + index) / FILAMENT_SEGMENTS);
        setColor(orb.tier(), 0.52F, FILAMENT_GLOW_ALPHA * flicker);
        FxRenderHelper.path(run, index -> width * 1.6 * taper.applyAsDouble(index), index -> {});
        setColor(orb.tier(), 0.92F, FILAMENT_CORE_ALPHA * flicker);
        FxRenderHelper.path(run, index -> width * 0.38 * taper.applyAsDouble(index), index -> {});
    }

    private static Vec3d filamentPoint(OrbViz orb, Vec3d direction, Vec3d right, Vec3d up, double reach, double phase,
        double wobblePhase, double position, double crackle, double agitation) {
        double along = (position - 0.5) * reach * 2;
        double radialPhase = phase + position * 2.4;
        double envelope = 0.6 + 0.4 * Math.sin(Math.PI * position);
        double wobble = orb.radius() * (0.22 + 0.12 * Math.sin(wobblePhase + position * 5.2)) * (0.6 + 0.4 * agitation);
        Vec3d radial = right.scale(Math.cos(radialPhase) * orb.radius() * envelope)
            .add(up.scale(Math.sin(radialPhase) * orb.radius() * envelope));
        Vec3d jitter = right.scale(Math.sin(wobblePhase * 0.9 + position * 8.0) * wobble)
            .add(up.scale(Math.cos(wobblePhase * 0.73 + position * 6.4) * wobble * 0.72));
        Vec3d crackleOffset = radial.normalize()
            .scale(crackle * orb.radius() * FILAMENT_CRACKLE);
        return orb.center()
            .add(direction.scale(along))
            .add(radial)
            .add(
                jitter.add(crackleOffset)
                    .scale(Math.sin(Math.PI * position)));
    }

    private static void setColor(int tier, float mix, float alpha) {
        float[] rgb = mix(tier, mix);
        Tessellator.instance.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
    }

    private static float[] mix(int tier, float mix) {
        return color(tier, mix, COLOR);
    }
}
