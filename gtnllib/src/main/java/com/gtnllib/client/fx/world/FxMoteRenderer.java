package com.gtnllib.client.fx.world;

import java.util.Collection;
import java.util.List;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class FxMoteRenderer {

    public enum Kind {
        DOT,
        PUFF,
        SPARK,
        RING,
        FLASH
    }

    public interface Surface {

        /**
         * Finds the first solid surface crossed by a straight move.
         *
         * @param from where the move starts
         * @param to   where the move would end without any surface in the way
         * @return the first contact point and the outward normal of the surface there, or null when nothing is in the
         *         way
         */
        Hit hit(Vec3d from, Vec3d to);
    }

    public record Hit(Vec3d at, Vec3d normal) {}

    public static class Mote {

        private final Kind kind;
        private final double size, lifetime;
        private final int glow, core;
        private Vec3d origin, velocity = Vec3d.ZERO, normal = new Vec3d(0, 1, 0);
        private double drag = 1, gravity, delay, hover, born;
        private boolean twinkle, bounces, untilSecondLanding;
        private double restitution, friction, settleTicks;
        private float phase;
        private Vec3d position, previous, current;
        private int steps;
        private double landed = Double.NaN, due = Double.NaN, landedAgain = Double.NaN;
        private double fadeOutAt = Double.NaN, fadeOutTicks = 1;

        public Mote(Kind kind, Vec3d origin, double size, double lifetime, int glow, int core) {
            this.kind = kind;
            this.origin = origin;
            this.size = size;
            this.lifetime = lifetime;
            this.glow = glow;
            this.core = core;
        }

        public Mote velocity(Vec3d velocity) {
            this.velocity = velocity;
            return this;
        }

        public Mote fall(double drag, double gravity) {
            this.drag = drag;
            this.gravity = gravity;
            return this;
        }

        public Mote bounce(double restitution, double friction, double settleTicks, boolean untilSecondLanding) {
            bounces = true;
            this.restitution = restitution;
            this.friction = friction;
            this.settleTicks = settleTicks;
            this.untilSecondLanding = untilSecondLanding;
            return this;
        }

        public Mote delay(double ticks) {
            delay = ticks;
            return this;
        }

        public Mote hover(double ticks) {
            hover = Math.max(0, ticks);
            return this;
        }

        public Mote twinkle(float phase) {
            twinkle = true;
            this.phase = phase;
            return this;
        }

        public Mote facing(Vec3d normal) {
            this.normal = normal;
            return this;
        }

        public Vec3d at(double age) {
            double rate = -Math.log(drag);
            double travel = drag >= 1 ? age : (1 - Math.pow(drag, age)) / rate;
            double fall = drag >= 1 ? 0.5 * gravity * age * age : gravity / rate * (age - travel);
            return origin.add(velocity.scale(travel))
                .add(0, -fall, 0);
        }

        public void advance(double age, Surface surface) {
            if (!bounces) return;
            if (position == null) {
                position = previous = origin;
                current = velocity;
            }
            while (steps < age - hover && Double.isNaN(landedAgain)) step(surface);
        }

        public Vec3d shown(double age) {
            double moving = Math.max(0, age - hover);
            if (!bounces) return at(moving);
            if (position == null || age <= hover) return origin;
            double blend = MathHelper.clamp_double(moving - (steps - 1), 0, 1);
            return previous.add(
                position.subtract(previous)
                    .scale(blend));
        }

        public float settle(double age) {
            age -= hover;
            if (!bounces || Double.isNaN(landed)) return 1F;
            if (!untilSecondLanding) return 1 - FxEasing.smoothStep(0, (float) settleTicks, (float) (age - landed));
            if (age >= landedAgain) return 0F;
            return 1 - FxEasing.smoothStep(0, (float) (due - landed), (float) (age - landed));
        }

        public Mote fadeOutFrom(double age, double ticks) {
            if (Double.isNaN(fadeOutAt) || age < fadeOutAt) {
                fadeOutAt = age;
                fadeOutTicks = Math.max(ticks, 1.0E-3);
            }
            return this;
        }

        public float fadeOutLeft(double age) {
            return Double.isNaN(fadeOutAt) ? 1F
                : 1 - FxEasing.smoothStep(0, (float) fadeOutTicks, (float) (age - fadeOutAt));
        }

        public boolean faded(double age) {
            return age >= lifetime || settle(age) <= 0 || fadeOutLeft(age) <= 0;
        }

        private void step(Surface surface) {
            previous = position;
            Vec3d next = position.add(current);
            Hit hit = surface.hit(position, next);
            double into = hit == null ? 0 : current.dot(hit.normal());
            if (into < 0) {
                Vec3d along = current.subtract(
                    hit.normal()
                        .scale(into));
                current = along.scale(friction)
                    .subtract(
                        hit.normal()
                            .scale(into * restitution));
                next = hit.at()
                    .add(
                        hit.normal()
                            .scale(SURFACE_GAP));
                if (Double.isNaN(landed)) {
                    landed = steps + 1;
                    double up = hit.normal()
                        .y() > FLOOR_SLOPE ? -into * restitution : 0;
                    due = landed + Math.max(settleTicks, bounceTicks(up, drag, gravity));
                } else if (untilSecondLanding && Double.isNaN(landedAgain)) landedAgain = steps + 1;
            }
            position = next;
            current = current.scale(drag)
                .add(0, -gravity, 0);
            steps++;
        }
    }

    public static int bounceTicks(double up, double drag, double gravity) {
        double height = SURFACE_GAP, speed = up * drag - gravity;
        for (int tick = 1; tick < MAX_BOUNCE_TICKS; tick++) {
            height += speed;
            if (height < 0) return tick;
            speed = speed * drag - gravity;
        }
        return MAX_BOUNCE_TICKS;
    }

    private static final int MIN_LIMIT = 64, MAX_LIMIT = 8192, DEFAULT_LIMIT = 1024, MAX_BOUNCE_TICKS = 100;
    private static final double MAX_SIZE = 8, MAX_LIFETIME = 200, NEAR_CAMERA = 0.3;
    private static final double SURFACE_GAP = 0.02, FLOOR_SLOPE = 0.7;
    private static final int WHITE = 0xFFFFFF;

    private static final List<Mote> ACTIVE = new ObjectArrayList<>();
    private static IntSupplier limit = () -> DEFAULT_LIMIT;

    public static void limitWith(IntSupplier most) {
        limit = most;
    }

    public static int limit() {
        return Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, limit.getAsInt()));
    }

    public static void spawn(Mote mote, float partialTicks) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || mote == null
            || !mote.origin.finite()
            || !mote.velocity.finite()
            || !(mote.size > 0 && mote.size <= MAX_SIZE)
            || !(mote.lifetime > 0 && mote.lifetime <= MAX_LIFETIME)
            || !(mote.drag > 0 && mote.drag <= 1)
            || mote.bounces && !(mote.restitution >= 0 && mote.restitution <= 1
                && mote.friction >= 0
                && mote.friction <= 1
                && mote.settleTicks > 0))
            return;
        if (mote.bounces) mote.origin = unstick(world, mote.origin);
        int most = limit();
        while (ACTIVE.size() >= most) ACTIVE.remove(0);
        mote.born = world.getTotalWorldTime() + partialTicks + mote.delay;
        ACTIVE.add(mote);
    }

    public static void clear() {
        ACTIVE.clear();
    }

    public static void fadeOutNear(Collection<Mote> motes, Vec3d center, double radius, double ticks, double now) {
        for (Mote mote : motes) {
            double age = now - mote.born;
            if (mote.shown(Math.max(0, age))
                .distanceToSqr(center) <= radius * radius) mote.fadeOutFrom(age, ticks);
        }
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        double now = world.getTotalWorldTime() + event.partialTicks;
        Surface blocks = (from, to) -> blockHit(world, from, to);
        for (Mote mote : ACTIVE) mote.advance(now - mote.born, blocks);
        ACTIVE.removeIf(mote -> mote.faded(now - mote.born));
        if (ACTIVE.isEmpty()) return;
        for (Sprite sprite : new Sprite[] { Sprite.RADIAL, Sprite.SOFT_RING, Sprite.PIXEL_RING, Sprite.PIXEL_PUFF,
            Sprite.PIXEL_DOT, Sprite.PIXEL_SPARK })
            FxRenderHelper.renderGlow(sprite, () -> { for (Mote mote : ACTIVE) draw(mote, sprite, now - mote.born); });
    }

    private static Hit blockHit(World world, Vec3d from, Vec3d to) {
        MovingObjectPosition hit = world.func_147447_a(from.toMc(), to.toMc(), false, true, false);
        if (hit == null || hit.typeOfHit != MovingObjectType.BLOCK) return null;
        ForgeDirection side = ForgeDirection.getOrientation(hit.sideHit);
        return new Hit(Vec3d.of(hit.hitVec), Vec3d.of(side));
    }

    private static Vec3d unstick(World world, Vec3d at) {
        int x = MathHelper.floor_double(at.x()), y = MathHelper.floor_double(at.y()),
            z = MathHelper.floor_double(at.z());
        AxisAlignedBB box = world.getBlock(x, y, z)
            .getCollisionBoundingBoxFromPool(world, x, y, z);
        return box != null && box.isVecInside(at.toMc()) ? new Vec3d(at.x(), box.maxY + SURFACE_GAP, at.z()) : at;
    }

    private static void draw(Mote mote, Sprite sprite, double age) {
        if (age < 0) return;
        Vec3d at = mote.shown(age);
        if (FxCamera.position()
            .subtract(at)
            .length() < NEAR_CAMERA) return;
        float progress = (float) (age / mote.lifetime);
        float twinkle = mote.twinkle ? 0.62F + 0.38F * (float) Math.cos(age * 2.4 + mote.phase) : 1F;
        float remaining = mote.fadeOutLeft(age);
        if (remaining <= 0) return;
        switch (mote.kind) {
            case DOT -> {
                float scale = (mote.bounces ? Math.min(mote.settle(age), 1 - FxEasing.smoothStep(0.7F, 1, progress))
                    : 1 - FxEasing.smoothStep(0.35F, 1, progress)) * remaining;
                if (sprite == Sprite.RADIAL) quad(at, mote.size * 3.4 * scale, mote.glow, 0.34F * twinkle * scale);
                if (sprite == Sprite.PIXEL_DOT) {
                    quad(at, mote.size * scale, mote.core, twinkle * remaining);
                    quad(at, mote.size * 0.5 * scale, WHITE, 0.9F * twinkle * remaining);
                }
            }
            case PUFF -> {
                float scale = (0.7F + 0.5F * FxEasing.easeOutCubic(progress * 2.5F))
                    * (1 - FxEasing.smoothStep(0.55F, 1, progress))
                    * remaining;
                float fade = 0.95F * (1 - FxEasing.smoothStep(0.6F, 1, progress)) * remaining;
                if (sprite == Sprite.RADIAL) quad(at, mote.size * 2.6 * scale, mote.glow, 0.4F * fade);
                if (sprite == Sprite.PIXEL_PUFF) {
                    quad(at, mote.size * scale, mote.core, 0.8F * fade);
                    quad(highlight(at, mote.size * 0.18 * scale), mote.size * 0.7 * scale, WHITE, 0.55F * fade);
                }
            }
            case SPARK -> {
                float fade = (mote.bounces ? mote.settle(age) : 1 - FxEasing.smoothStep(0.55F, 1, progress))
                    * remaining;
                if (sprite == Sprite.RADIAL) quad(at, mote.size * 3 * fade, mote.glow, 0.45F * fade * twinkle);
                if (sprite == Sprite.PIXEL_SPARK)
                    quad(at, mote.size * 1.6, sparkColor(mote.core, (float) age), fade * twinkle);
            }
            case RING -> {
                double radius = mote.size * (0.2 + 0.8 * FxEasing.easeOutCubic(progress));
                float fade = (1 - FxEasing.smoothStep(0.3F, 1, progress)) * remaining;
                if (sprite == Sprite.SOFT_RING) flat(at, mote.normal, radius * 1.18, mote.glow, 0.5F * fade);
                if (sprite == Sprite.PIXEL_RING)
                    flat(at, mote.normal, radius, ColorMath.mixWhite(mote.core, 0.6F), fade);
            }
            case FLASH -> {
                if (sprite != Sprite.RADIAL) return;
                quad(at, mote.size * (1 + 0.4 * progress), mote.glow, 0.8F * (1 - progress) * remaining);
                quad(at, mote.size * 0.39, WHITE, (1 - progress) * remaining);
            }
        }
    }

    public static int sparkColor(int core, float ageTicks) {
        return ColorMath.lerp(WHITE, core, FxEasing.smoothStep(3, 7, ageTicks));
    }

    private static Vec3d highlight(Vec3d at, double offset) {
        Vec3d toCamera = FxCamera.position()
            .subtract(at)
            .normalize();
        Vec3d right = Vec3d.rightOf(toCamera);
        Vec3d up = right.cross(toCamera)
            .normalize();
        return at.add(
            up.subtract(right)
                .scale(offset));
    }

    private static void quad(Vec3d at, double radius, int rgb, float alpha) {
        if (radius <= 0 || alpha <= 0) return;
        ColorMath.tessellate(Tessellator.instance, rgb, alpha);
        FxRenderHelper.glow(at, radius, 0);
    }

    private static void flat(Vec3d at, Vec3d normal, double radius, int rgb, float alpha) {
        if (radius <= 0 || alpha <= 0) return;
        ColorMath.tessellate(Tessellator.instance, rgb, alpha);
        FxRenderHelper.planeGlow(at, normal, radius);
    }
}
