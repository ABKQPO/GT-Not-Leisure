package com.science.gtnl.client.portalgun;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.MapMaker;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxMoteRenderer;
import com.gtnllib.client.fx.world.FxMoteRenderer.Kind;
import com.gtnllib.client.fx.world.FxMoteRenderer.Mote;
import com.gtnllib.client.fx.world.FxMuzzleAnchor;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.entity.EntityPortalGunOrb;
import com.science.gtnl.config.ClientConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class PortalGunOrbTrail {

    public static final double MUZZLE_RIGHT = 0.22, MUZZLE_DOWN = 0.32, MUZZLE_BLEND = 3.5, MIN_MUZZLE_DEPTH = 0.3;
    public static final double TRAIL_START = 0.7;
    public static final double[] RING_DISTANCES = { 1.3, 2.5, 3.7 };
    private static final double SPREAD_LIMIT = 2.5, MAX_CATCH_UP = 8, LOOK_BACK = 2.5, MIN_SPEED = 1.0E-4,
        OWN_SHOT = 1.5;
    private static final double PUFF_NEAR = 0.95, PUFF_FAR = 1.6;
    public static final double DOT_LIFETIME = 100, TWINKLE_CHANCE = 0.35;
    private static final double BOUNCE = 0.45, FRICTION = 0.7, SETTLE_TICKS = 3;
    private static final double DOT_DRAG = 0.96, SPLASH_SPEED = 0.08, SPLASH_SPREAD = 0.08;
    private static final int PUFFS = 4;
    private static final Random RNG = new Random();
    private static final Map<EntityPortalGunOrb, Trail> TRAILS = new MapMaker().weakKeys()
        .makeMap();

    private static class Trail {

        private final Vec3d heading, right, down, lateral;
        private final double depth;
        private final int glow, core;
        private final boolean shown;
        private final List<Mote> trailDots = new ObjectArrayList<>();
        private Vec3d last;
        private double travelled, spawned;
        private int rings;

        private Trail(EntityPortalGunOrb orb, Vec3d center, PortalGunPortalEffect effect, float partialTicks) {
            heading = heading(orb);
            double speed = new Vec3d(orb.motionX, orb.motionY, orb.motionZ).length();
            Vec3d origin = center.subtract(heading.scale(speed * (orb.ticksExisted + partialTicks)));
            right = Vec3d.rightOf(heading);
            down = heading.cross(right);
            EntityPlayer shooter = shooter(orb, origin);
            Vec3d anchor = shooter == null ? null : FxMuzzleAnchor.muzzle(shooter.getEntityId(), partialTicks);
            Vec3d toMuzzle = anchor != null ? anchor.subtract(origin)
                : right.scale(MUZZLE_RIGHT)
                    .add(down.scale(MUZZLE_DOWN))
                    .add(heading.scale(TRAIL_START));
            double ahead = toMuzzle.dot(heading);
            depth = Math.max(MIN_MUZZLE_DEPTH, Math.min(MUZZLE_BLEND / 2, ahead));
            lateral = toMuzzle.subtract(heading.scale(ahead));
            glow = effect.glowColor & 0xFFFFFF;
            core = effect.coreColor & 0xFFFFFF;
            shown = ClientConfig.enablePortalGunShowOthersFx
                || shooter != null && shooter == Minecraft.getMinecraft().thePlayer;
            last = center;
            travelled = center.subtract(origin)
                .length();
            spawned = Math.max(TRAIL_START, travelled);
            while (rings < RING_DISTANCES.length && RING_DISTANCES[rings] < travelled) rings++;
            if (shown && ClientConfig.enablePortalGunPuffs && travelled < PUFF_NEAR) puffs(origin, partialTicks);
        }

        private Vec3d offset(double distance) {
            return lateral.scale(muzzleWeight(distance, depth));
        }

        private Vec3d offset(double sigma, double limit) {
            return right.scale(gauss(RNG, sigma, limit))
                .add(down.scale(gauss(RNG, sigma, limit)));
        }

        private void puffs(Vec3d origin, float partialTicks) {
            for (int k = 0; k < PUFFS; k++) {
                double distance = PUFF_NEAR + (PUFF_FAR - PUFF_NEAR) * RNG.nextDouble();
                Vec3d at = origin.add(heading.scale(distance))
                    .add(offset(distance))
                    .add(offset(0.1, 0.2));
                Vec3d drift = heading.scale(0.07 + 0.09 * RNG.nextDouble())
                    .add(gauss(RNG, 0.015, 0.03), gauss(RNG, 0.015, 0.03), gauss(RNG, 0.015, 0.03));
                FxMoteRenderer.spawn(
                    new Mote(Kind.PUFF, at, 0.08 + 0.04 * RNG.nextDouble(), 8 + 4 * RNG.nextDouble(), glow, core)
                        .velocity(drift)
                        .delay(0.15 + k * 0.12),
                    partialTicks);
            }
        }

        private void follow(Vec3d center, float partialTicks) {
            double step = center.subtract(last)
                .length();
            if (step > 0) {
                if (shown) dots(center, step, partialTicks);
                for (; rings < RING_DISTANCES.length && RING_DISTANCES[rings] <= travelled + step; rings++)
                    if (shown && ClientConfig.enablePortalGunRings) FxMoteRenderer.spawn(
                        new Mote(Kind.RING, along(center, step, RING_DISTANCES[rings]), 0.4, 5, glow, core)
                            .facing(heading),
                        partialTicks);
            }
            last = center;
            travelled += step;
        }

        private Vec3d along(Vec3d center, double step, double distance) {
            double blend = MathUtils.clampUnit((distance - travelled) / step);
            return last.add(
                center.subtract(last)
                    .scale(blend))
                .add(offset(distance));
        }

        private void dots(Vec3d center, double step, float partialTicks) {
            spawned = Math.max(spawned, travelled + step - MAX_CATCH_UP);
            while (spawned < travelled + step) {
                Vec3d base = along(center, step, spawned);
                double sigma = ClientConfig.portalGunTrailWidth
                    * (0.6 + 0.4 * FxEasing.smoothStep((float) (spawned / 3)));
                for (int k = 0; k < ClientConfig.portalGunTrailCount; k++) {
                    Vec3d offset = offset(sigma, ClientConfig.portalGunTrailWidth * SPREAD_LIMIT);
                    Vec3d outward = offset.length() < MIN_SPEED ? right : offset.normalize();
                    Mote dot = dot(
                        base.add(offset),
                        ClientConfig.portalGunTrailDotSize * (0.6 + 0.8 * RNG.nextDouble()),
                        DOT_LIFETIME,
                        glow,
                        core,
                        outward.scale(ClientConfig.portalGunTrailSpray * (0.5 + RNG.nextDouble())),
                        DOT_DRAG,
                        ClientConfig.portalGunTrailGravity).hover(ClientConfig.portalGunTrailFallDelay * spawned);
                    twinkleSometimes(dot);
                    FxMoteRenderer.spawn(dot, partialTicks);
                    trailDots.add(dot);
                }
                spawned += (0.6 + 0.8 * RNG.nextDouble()) / ClientConfig.portalGunTrailDensity;
            }
        }

        private void land(EntityPortalGunOrb orb, float partialTicks) {
            if (!shown) return;
            Vec3d end = Vec3d.pos(orb)
                .add(0, orb.height / 2, 0);
            Vec3d motion = new Vec3d(orb.motionX, orb.motionY, orb.motionZ);
            Vec3d back = motion.length() < MIN_SPEED ? heading : motion.normalize();
            MovingObjectPosition hit = orb.worldObj.func_147447_a(
                end.subtract(back.scale(LOOK_BACK))
                    .toMc(),
                end.toMc(),
                false,
                true,
                false);
            boolean block = hit != null && hit.typeOfHit == MovingObjectType.BLOCK;
            ForgeDirection side = block ? ForgeDirection.getOrientation(hit.sideHit) : ForgeDirection.UNKNOWN;
            Vec3d at = block ? Vec3d.of(hit.hitVec) : end;
            Vec3d normal = block ? Vec3d.of(side) : back.scale(-1);
            if (ClientConfig.portalGunTrailCutRange > 0) FxMoteRenderer.fadeOutNear(
                trailDots,
                at,
                ClientConfig.portalGunTrailCutRange,
                ClientConfig.portalGunTrailCutTicks,
                orb.worldObj.getTotalWorldTime() + partialTicks);
            if (!ClientConfig.enablePortalGunSparks) return;
            FxMoteRenderer.spawn(new Mote(Kind.FLASH, at.add(normal.scale(0.15)), 0.9, 4, glow, core), partialTicks);
            Vec3d across = Vec3d.rightOf(normal), sideways = across.cross(normal);
            for (int k = 0; k < ClientConfig.portalGunSparkCount; k++) {
                double angle = RNG.nextDouble() * Math.PI * 2;
                Vec3d direction = across.scale(Math.cos(angle))
                    .add(sideways.scale(Math.sin(angle)))
                    .add(normal.scale(0.5 + 1.1 * RNG.nextDouble()))
                    .normalize();
                Mote dot = dot(
                    at.add(normal.scale(0.05)),
                    ClientConfig.portalGunTrailDotSize * (0.6 + 0.8 * RNG.nextDouble()),
                    DOT_LIFETIME,
                    glow,
                    core,
                    direction.scale(SPLASH_SPEED + SPLASH_SPREAD * RNG.nextDouble()),
                    DOT_DRAG,
                    ClientConfig.portalGunTrailGravity).delay(0.6 * RNG.nextDouble());
                twinkleSometimes(dot);
                FxMoteRenderer.spawn(dot, partialTicks);
            }
        }
    }

    public static Vec3d heading(Entity entity) {
        Vec3d moved = new Vec3d(
            entity.posX - entity.prevPosX,
            entity.posY - entity.prevPosY,
            entity.posZ - entity.prevPosZ);
        if (moved.length() > MIN_SPEED) return moved.normalize();
        Vec3d motion = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
        return motion.length() > MIN_SPEED ? motion.normalize() : new Vec3d(0, 0, 1);
    }

    /**
     * How much of the sideways step from the shot's real path to the muzzle is drawn {@code distance} blocks out:
     * up to the muzzle's depth the orb is drawn on the line from the eye through the muzzle, so it leaves the barrel
     * on screen, and then it eases back onto its real path by {@link #MUZZLE_BLEND}.
     */
    public static double muzzleWeight(double distance, double depth) {
        if (distance < depth) return Math.max(0, distance) / depth;
        return 1 - MathUtils.clampUnit((distance - depth) / (MUZZLE_BLEND - depth));
    }

    private static EntityPlayer shooter(EntityPortalGunOrb orb, Vec3d origin) {
        EntityPlayer nearest = null;
        double best = OWN_SHOT;
        for (Object entity : orb.worldObj.playerEntities) if (entity instanceof EntityPlayer player) {
            double distance = Vec3d.eye(player)
                .subtract(origin)
                .length();
            if (distance < best) {
                nearest = player;
                best = distance;
            }
        }
        return nearest;
    }

    public static Vec3d follow(EntityPortalGunOrb orb, Vec3d center, PortalGunPortalEffect effect, float partialTicks) {
        Trail trail = TRAILS.get(orb);
        if (trail == null) {
            // Why: the spawn message carries no motion, so the heading is unknown until the velocity packet lands.
            if (new Vec3d(orb.motionX, orb.motionY, orb.motionZ).length() < MIN_SPEED) return center;
            TRAILS.put(orb, trail = new Trail(orb, center, effect, partialTicks));
        }
        trail.follow(center, partialTicks);
        return center.add(trail.offset(trail.travelled));
    }

    public static Mote dot(Vec3d at, double size, double lifetime, int glow, int core, Vec3d velocity, double drag,
        double gravity) {
        return new Mote(Kind.DOT, at, size, lifetime, glow, core).velocity(velocity)
            .fall(drag, gravity)
            .bounce(BOUNCE, FRICTION, SETTLE_TICKS, true);
    }

    public static double gauss(Random random, double sigma, double limit) {
        return Math.max(-limit, Math.min(limit, random.nextGaussian() * sigma));
    }

    private static void twinkleSometimes(Mote dot) {
        if (RNG.nextDouble() < TWINKLE_CHANCE) dot.twinkle(RNG.nextFloat() * (float) Math.PI * 2);
    }

    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {
        if (TRAILS.isEmpty()) return;
        var world = Minecraft.getMinecraft().theWorld;
        for (Iterator<Map.Entry<EntityPortalGunOrb, Trail>> it = TRAILS.entrySet()
            .iterator(); it.hasNext();) {
            Map.Entry<EntityPortalGunOrb, Trail> entry = it.next();
            EntityPortalGunOrb orb = entry.getKey();
            if (orb.worldObj != world) it.remove();
            else if (orb.isDead) {
                it.remove();
                entry.getValue()
                    .land(orb, event.partialTicks);
            }
        }
    }
}
