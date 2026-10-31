package com.gtnllib;

import static com.gtnltest.TestSupport.check;
import static com.gtnltest.TestSupport.near;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import com.gtnllib.client.fx.FxAngle;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxCamera;
import com.gtnllib.client.fx.world.FxMoteRenderer;
import com.gtnllib.client.fx.world.FxMoteRenderer.Hit;
import com.gtnllib.client.fx.world.FxMoteRenderer.Kind;
import com.gtnllib.client.fx.world.FxMoteRenderer.Mote;
import com.gtnllib.client.fx.world.FxMoteRenderer.Surface;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.SegmentSides;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.utils.Vec3d;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Tag("library")
public class FxTest extends LibraryTestSupport {

    @TestFactory
    Stream<DynamicTest> scenarios() {
        return Stream.of(
            DynamicTest.dynamicTest("easing", FxTest::easing),
            DynamicTest.dynamicTest("motes", FxTest::motes),
            DynamicTest.dynamicTest("moteLimit", FxTest::moteLimit),
            DynamicTest.dynamicTest("pixelSprites", FxTest::pixelSprites),
            DynamicTest.dynamicTest("pathJoins", FxTest::pathJoins),
            DynamicTest.dynamicTest("fadeOut", FxTest::fadeOut),
            DynamicTest.dynamicTest("frustum", FxTest::frustum),
            DynamicTest.dynamicTest("sectors", FxTest::sectors));
    }

    private static void sectors() {
        check(FxAngle.fullTurn((float) Math.PI), "a one-slot wheel highlight is a full ring, drawn without a seam");
        check(FxAngle.fullTurn(4F), "a sector wider than a turn is a full ring");
        check(!FxAngle.fullTurn((float) Math.PI / 2), "a two-slot wheel highlight is a half ring with its own edges");
    }

    private static void frustum() {
        check(FxCamera.inFrustum(new Vec3d(0, 0, -10), 0.5, 1, 1), "something straight ahead is in view");
        check(!FxCamera.inFrustum(new Vec3d(0, 0, 10), 3, 1, 1), "something well behind the camera is out of view");
        check(
            FxCamera.inFrustum(new Vec3d(0, 0, 2), 3, 1, 1),
            "something behind the camera but reaching past it is still in view");
        check(
            !FxCamera.inFrustum(new Vec3d(0, 0, 3.5), 3, 1, 1),
            "something just behind the camera that does not reach past it is out of view");
        check(!FxCamera.inFrustum(new Vec3d(20, 0, -5), 1, 1, 1), "something far to the right is out of view");
        check(!FxCamera.inFrustum(new Vec3d(-20, 0, -5), 1, 1, 1), "something far to the left is out of view");
        check(!FxCamera.inFrustum(new Vec3d(0, 20, -5), 1, 1, 1), "something far above is out of view");
        check(
            FxCamera.inFrustum(new Vec3d(5.5, 0, -5), 1, 1, 1),
            "something just past the right edge but reaching into view is still in view");
        check(
            !FxCamera.inFrustum(new Vec3d(5.5, 0, -5), 1, 2, 1),
            "a narrower view leaves out what a wider one would show");
    }

    private static void fadeOut() {
        Mote close = new Mote(Kind.DOT, new Vec3d(0.5, 0, 0), 0.03, 100, 0, 0);
        Mote distant = new Mote(Kind.DOT, new Vec3d(3, 0, 0), 0.03, 100, 0, 0);
        FxMoteRenderer.fadeOutNear(Arrays.asList(close, distant), Vec3d.ZERO, 2, 4, 10);
        near(1, close.fadeOutLeft(10), "a cut mote starts fading from the moment it is cut");
        near(0.5, close.fadeOutLeft(12), "a cut mote is half gone half way through its fade");
        check(close.faded(14), "a cut mote is gone once its fade time is over");
        check(!distant.faded(14) && distant.fadeOutLeft(14) == 1, "a mote outside the cut range keeps going");
        close.fadeOutFrom(13, 4);
        check(close.faded(14), "a later cut does not bring a fading mote back");
    }

    private static void pathJoins() {
        Vec3d up = new Vec3d(0, 1, 0), right = new Vec3d(1, 0, 0), down = new Vec3d(0, -1, 0);
        List<SegmentSides> straight = FxRenderHelper.pathSides(Arrays.asList(up, up, up));
        boolean even = straight.size() == 3;
        for (SegmentSides sides : straight) even &= sides.start()
            .distanceToSqr(up) < 1.0E-12
            && sides.end()
                .distanceToSqr(up) < 1.0E-12;
        check(even, "a straight path keeps the same width at every point");
        List<SegmentSides> corner = FxRenderHelper.pathSides(Arrays.asList(up, right));
        Vec3d shared = corner.get(0)
            .end();
        check(
            shared.distanceToSqr(
                corner.get(1)
                    .start())
                < 1.0E-12,
            "both pieces meet at the same edge on a square corner");
        near(Math.sqrt(2), shared.length(), "a square corner widens so both pieces keep their width");
        near(shared.dot(up), shared.dot(right), "a corner joint sits halfway between the two pieces");
        double turn = Math.toRadians(250);
        Vec3d steep = new Vec3d(Math.cos(turn), Math.sin(turn), 0);
        List<SegmentSides> sharp = FxRenderHelper.pathSides(Arrays.asList(up, steep));
        check(
            sharp.get(0)
                .end()
                .distanceToSqr(up) < 1.0E-12
                && sharp.get(1)
                    .start()
                    .distanceToSqr(steep) < 1.0E-12,
            "a very sharp turn ends each piece square instead of folding into a spike");
        List<SegmentSides> back = FxRenderHelper.pathSides(Arrays.asList(up, down));
        check(
            back.get(0)
                .end()
                .distanceToSqr(up) < 1.0E-12
                && back.get(1)
                    .start()
                    .distanceToSqr(down) < 1.0E-12,
            "a path that doubles back keeps each piece's own width");
        List<Vec3d> stamps = new ObjectArrayList<>();
        DoubleArrayList fractions = new DoubleArrayList();
        FxRenderHelper.along(Arrays.asList(new Vec3d(0, 0, 0), right, new Vec3d(1, 1, 0)), 0.25, (point, fraction) -> {
            stamps.add(point);
            fractions.add(fraction);
        });
        check(stamps.size() == 9, "stamps every 0.25 along a 2 block path land 9 times, got " + stamps.size());
        check(
            stamps.get(8)
                .distanceToSqr(new Vec3d(1, 1, 0)) < 1.0E-12 && Math.abs(fractions.getDouble(8) - 1) < 1.0E-9
                && fractions.getDouble(0) == 0,
            "stamps run from the first point to the last, measured as a share of the path");
        check(
            stamps.get(5)
                .distanceToSqr(new Vec3d(1, 0.25, 0)) < 1.0E-12,
            "stamps keep their spacing round a corner");
    }

    private static void moteLimit() {
        int saved = GTNLLibConfig.maxWorldMotes;
        try {
            near(1024, GTNLLibConfig.DEFAULT_MAX_WORLD_MOTES, "at most 1024 world motes by default");
            GTNLLibConfig.maxWorldMotes = 300;
            near(300, FxMoteRenderer.limit(), "the mote cap follows the GTNLLib setting");
            GTNLLibConfig.maxWorldMotes = 10;
            near(64, FxMoteRenderer.limit(), "a tiny mote cap still keeps a few motes");
        } finally {
            GTNLLibConfig.maxWorldMotes = saved;
        }
    }

    private static void easing() {
        near(0, FxEasing.easeOutBack(0), "ease out back starts at 0");
        near(1, FxEasing.easeOutBack(1), "ease out back ends at 1");
        near(0, FxEasing.easeOutBack(-1), "ease out back clamps low");
        near(1, FxEasing.easeOutBack(2), "ease out back clamps high");
        float peak = 0;
        for (int i = 0; i <= 100; i++) peak = Math.max(peak, FxEasing.easeOutBack(i / 100F));
        check(peak > 1.05F && peak < 1.15F, "ease out back overshoots by about 10%, peak " + peak);
        near(0, FxEasing.smoothStep(2, 6, 1), "smooth step between edges is 0 before the first edge");
        near(0.5, FxEasing.smoothStep(2, 6, 4), "smooth step between edges is half way at the middle");
        near(1, FxEasing.smoothStep(2, 6, 7), "smooth step between edges is 1 after the second edge");
        near(
            FxEasing.smoothStep(0.25F),
            FxEasing.smoothStep(2, 6, 3),
            "smooth step between edges follows the plain smooth step over the scaled range");
    }

    private static void motes() {
        Vec3d origin = new Vec3d(1, 2, 3);
        near(
            0,
            new Mote(Kind.DOT, origin, 0.1, 10, 0, 0).at(5)
                .subtract(origin)
                .length(),
            "a mote without velocity stays where it was spawned");
        near(
            1.5,
            new Mote(Kind.DOT, origin, 0.1, 10, 0, 0).velocity(new Vec3d(0.1, 0, 0))
                .at(5)
                .x(),
            "a mote without drag drifts at its spawn speed");
        Mote spark = new Mote(Kind.SPARK, origin, 0.1, 10, 0, 0).velocity(new Vec3d(0, 0.3, 0))
            .fall(0.86, 0.018);
        near(
            2,
            spark.at(0)
                .y(),
            "a spark starts where it was spawned");
        check(
            spark.at(3)
                .y() > 2.5,
            "a spark first flies up, at " + spark.at(3)
                .y());
        double y = 0, velocity = 0.3, peak = 0;
        for (int tick = 1; tick <= 12; tick++) {
            y += velocity;
            velocity = velocity * 0.86 - 0.018;
            peak = Math.max(peak, y);
        }
        double closedPeak = 0;
        for (int i = 0; i <= 1200; i++) closedPeak = Math.max(
            closedPeak,
            spark.at(i / 100.0)
                .y() - 2);
        check(
            Math.abs(closedPeak - peak) < 0.15 * peak,
            "a spark rises about as high as with per-tick drag and gravity, " + closedPeak + " against " + peak);
        near(0xFFFFFF, FxMoteRenderer.sparkColor(0x204060, 0), "a fresh spark is white");
        near(0x204060, FxMoteRenderer.sparkColor(0x204060, 10), "an old spark takes the portal color");
        bouncing();
    }

    private static void bouncing() {
        Surface floor = (from, to) -> {
            if (to.y() >= 2) return null;
            double t = (from.y() - 2) / (from.y() - to.y());
            return new Hit(
                from.add(
                    to.subtract(from)
                        .scale(t)),
                new Vec3d(0, 1, 0));
        };
        near(
            10,
            FxMoteRenderer.bounceTicks(0.1, 1, 0.02),
            "a hop at 0.1 under 0.02 gravity lands again after 10 ticks");
        near(
            9,
            FxMoteRenderer.bounceTicks(0.095, 1, 0.02),
            "a hop that only just dips under the floor counts as landed");
        Mote ball = new Mote(Kind.DOT, new Vec3d(0, 3, 0), 0.03, 100, 0, 0).velocity(new Vec3d(0.01, 0, 0))
            .fall(0.96, 0.02)
            .bounce(0.45, 0.7, 3, true);
        DoubleArrayList landings = new DoubleArrayList();
        double lowest = Double.MAX_VALUE, last = 3;
        for (int i = 1; i <= 2000; i++) {
            double age = i / 20.0;
            ball.advance(age, floor);
            double y = ball.shown(age)
                .y();
            lowest = Math.min(lowest, y);
            if (Math.abs(y - 2.02) < 1.0E-9 && last > y + 1.0E-9) landings.add(age);
            last = y;
        }
        check(lowest >= 2 - 1.0E-9, "a bouncing dot never sinks into the floor, lowest " + lowest);
        check(
            landings.size() == 2,
            "a dot lands, bounces once and is gone at its second landing, landings " + landings);
        if (landings.size() == 2) {
            double first = landings.getDouble(0), second = landings.getDouble(1);
            near(1, ball.settle(first - 0.5), "a dot keeps its full size until it first touches the ground");
            float half = ball.settle((first + second) / 2);
            check(half > 0.2F && half < 0.8F, "a dot is half shrunk halfway between its two landings, at " + half);
            check(ball.settle(second - 0.05) < 0.05F, "a dot has almost vanished just before it lands again");
            check(ball.faded(second), "a dot is gone once it lands the second time");
            check(!ball.faded(first + 0.5), "a dot is still shown while it bounces");
        }
        Mote floating = new Mote(Kind.DOT, new Vec3d(0, 3, 0), 0.03, 100, 0, 0).velocity(new Vec3d(0.01, 0, 0))
            .fall(0.96, 0.02)
            .bounce(0.45, 0.7, 3, true)
            .hover(6);
        floating.advance(5.5, floor);
        near(
            3,
            floating.shown(5.5)
                .y(),
            "a dot still floating in place has not started to fall");
        floating.advance(7, floor);
        Vec3d justLet = floating.shown(7);
        check(
            Math.abs(justLet.x() - 0.01) < 1.0E-9 && Math.abs(justLet.y() - 3) < 1.0E-9,
            "one tick after floating a dot has only drifted its first step from where it floated, at " + justLet);
        floating.advance(9, floor);
        check(
            floating.shown(9)
                .y() < 2.99,
            "a dot falls once its float time is over, at " + floating.shown(9)
                .y());
        near(1, floating.settle(8), "a dot that only just started falling keeps its full size");
        Mote spark = new Mote(Kind.SPARK, new Vec3d(0, 2.5, 0), 0.03, 30, 0, 0).velocity(new Vec3d(0, -0.2, 0))
            .fall(0.86, 0.018)
            .bounce(0.45, 0.7, 5, false);
        spark.advance(40, floor);
        check(spark.settle(2) == 1 && spark.faded(10), "a spark keeps its look until its first bounce, then fades out");
    }

    private static void pixelSprites() {
        int[] rows = new int[8];
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) if (FxRenderHelper.alpha(Sprite.PIXEL_DOT, x, y) > 0.5F) rows[y]++;
        check(
            Arrays.equals(rows, new int[] { 4, 6, 8, 8, 8, 8, 6, 4 }),
            "the pixel dot is a round disc without notches, rows " + Arrays.toString(rows));
        near(0, FxRenderHelper.alpha(Sprite.PIXEL_RING, 16, 16), "the pixel ring is hollow");
        near(1, FxRenderHelper.alpha(Sprite.PIXEL_RING, 16, 1), "the pixel ring is drawn near the edge");
        near(1, FxRenderHelper.alpha(Sprite.PIXEL_SPARK, 2, 2), "the spark has a bright center");
        near(0, FxRenderHelper.alpha(Sprite.PIXEL_SPARK, 0, 0), "the spark is a plus with empty corners");
        check(
            FxRenderHelper.alpha(Sprite.PIXEL_PUFF, 7, 7) == 1 && FxRenderHelper.alpha(Sprite.PIXEL_PUFF, 0, 0) == 0,
            "the puff is a solid round ball");
        check(
            FxRenderHelper.alpha(Sprite.RADIAL, 64, 64) > 0.95F && FxRenderHelper.alpha(Sprite.RADIAL, 0, 64) < 0.01F,
            "the radial glow fades from the center to the edge");
        check(
            FxRenderHelper.alpha(Sprite.SOFT_RING, 32, 32) < 0.01F
                && FxRenderHelper.alpha(Sprite.SOFT_RING, 32, 3) > 0.9F,
            "the soft ring glows around the pixel ring's radius");
    }
}
