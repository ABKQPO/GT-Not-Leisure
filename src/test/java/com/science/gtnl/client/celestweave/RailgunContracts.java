package com.science.gtnl.client.celestweave;

import static com.science.gtnl.client.celestweave.CelestweaveContractTest.roundTrip;
import static com.science.gtnllib.GTNLLibContractTest.call;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.field;
import static com.science.gtnllib.GTNLLibContractTest.near;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.science.gtnl.client.fx.world.FxArcRenderer.Color;
import com.science.gtnl.client.fx.world.FxArcRenderer.Style;
import com.science.gtnl.client.railgun.RailgunBolts;
import com.science.gtnl.client.railgun.RailgunChargeState;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.client.railgun.RailgunOrbRenderer;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.RailgunChargeCapPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunDamage;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunFireService;
import com.science.gtnl.config.MainConfig;

public class RailgunContracts {

    private RailgunContracts() {}

    static void run() throws Exception {
        railgunCost();
        railgunDamage();
        railgunPackets();
        railgunCharge();
        railgunBolts();
        railgunOrbPulse();
        railgunColors();
    }

    private static void railgunCost() throws ReflectiveOperationException {
        var config = MainConfig.item.railgun;
        double[] saved = { config.chargeEuEhv1, config.chargeEuEhv2, config.chargeEuEhv3, config.shotEuEhv3,
            config.ehvLightningEu };
        config.chargeEuEhv1 = 10;
        config.chargeEuEhv2 = 100;
        config.chargeEuEhv3 = 1000;
        config.shotEuEhv3 = 5;
        config.ehvLightningEu = 2;
        int ehv2 = RailgunChargeTier.EHV2.chargeTicks, ehv3 = RailgunChargeTier.EHV3.chargeTicks;
        near(0, RailgunFireService.chargeCostUpTo(0, 1), "no charge costs nothing");
        near(ehv2 * 10, RailgunFireService.chargeCostUpTo(ehv2, 1), "charge up to EHV2 uses the first price");
        near(
            ehv2 * 10 + 6 * 100,
            RailgunFireService.chargeCostUpTo(ehv2 + 6, 1),
            "charge past EHV2 uses the second price");
        near(
            ehv2 * 10 + (ehv3 - ehv2) * 100 + 10 * 1000,
            RailgunFireService.chargeCostUpTo(ehv3 + 10, 1),
            "charge past EHV3 uses the third price");
        near(ehv2 / 2 * 10, RailgunFireService.chargeCostUpTo(ehv2, 2), "a faster charge pays fewer ticks");
        near(
            5 + RailgunChargeTier.EHV3.lightning * 2,
            RailgunFireService.shotCost(RailgunChargeTier.EHV3),
            "shot cost");
        near(
            RailgunFireService.chargeCostUpTo(ehv3, 1) + RailgunFireService.shotCost(RailgunChargeTier.EHV3),
            RailgunFireService.totalCostUpTo(ehv3, 1),
            "total cost adds the shot of the reached tier");
        double last = -1;
        boolean rising = true;
        for (int units = 0; units <= ehv3 + 20; units++) {
            double cost = RailgunFireService.chargeCostUpTo(units, 1.5);
            rising &= cost >= last;
            last = cost;
        }
        check(rising, "charge cost never drops as the charge grows");
        config.chargeEuEhv1 = -50;
        near(0, RailgunFireService.chargeCostUpTo(ehv2, 1), "negative prices count as free");
        config.chargeEuEhv1 = 10;
        Class<?> sessionType = Class.forName(RailgunFireService.class.getName() + "$ChargeSession");
        Constructor<?> create = sessionType
            .getDeclaredConstructor(long.class, double.class, ItemStack.class, int.class, int.class);
        create.setAccessible(true);
        Method cap = sessionType.getDeclaredMethod("capAtAffordable", long.class, double.class);
        cap.setAccessible(true);
        for (double energy : new double[] { 0, 150, 300, 5000, 30000 }) {
            int units = (int) cap.invoke(create.newInstance(0L, 1.0, null, 0, 0), 60L, energy);
            check(units >= 0 && units <= 60, "capped charge stays inside the held time " + energy);
            check(
                units == 0 || RailgunFireService.totalCostUpTo(units, 1) <= energy,
                "capped charge is affordable " + energy);
            check(
                units == 60 || RailgunFireService.totalCostUpTo(units + 1, 1) > energy,
                "capped charge stops at the last affordable tick " + energy);
        }
        config.chargeEuEhv1 = saved[0];
        config.chargeEuEhv2 = saved[1];
        config.chargeEuEhv3 = saved[2];
        config.shotEuEhv3 = saved[3];
        config.ehvLightningEu = saved[4];
    }

    private static void railgunDamage() {
        near(10, RailgunDamage.finalDamage(10, 0, 0), "no armor takes full damage");
        near(5, RailgunDamage.finalDamage(10, 0, 0.5), "armor reduces damage");
        near(10, RailgunDamage.finalDamage(10, 1, 0.5), "full bypass ignores armor");
        near(7.5, RailgunDamage.finalDamage(10, 0.5, 0.5), "half bypass ignores half the armor");
        near(2, RailgunDamage.finalDamage(10, 0, 3), "armor reduction caps at 80 percent");
        near(10, RailgunDamage.finalDamage(10, 5, 0.5), "bypass above 1 counts as 1");
        near(0, RailgunDamage.finalDamage(-4, 0, 0), "negative damage counts as none");
    }

    private static void railgunPackets() throws ReflectiveOperationException {
        RailgunChargeCapPacket cap = roundTrip(
            new RailgunChargeCapPacket(11, 37),
            RailgunChargeCapPacket.class.getConstructor());
        check((int) field(cap, "shooterId") == 11 && (int) field(cap, "units") == 37, "charge cap names its shooter");
    }

    private static void railgunCharge() {
        int grace = RailgunData.CHARGE_RESUME_GRACE_TICKS;
        RailgunChargeState state = new RailgunChargeState();
        state.update(100, true, true, () -> 2);
        state.update(101, true, true, () -> 2);
        check(state.live(), "charge starts while using");
        check(state.units(110) == RailgunData.units(10, 2), "charge grows at the rate read on start");
        state.update(103, false, true, () -> 9);
        check(state.live(), "short gap while still holding keeps the charge");
        state.update(105, true, true, () -> 9);
        check(state.units(110) == RailgunData.units(10, 2), "resumed charge keeps its start and rate");
        state.update(106, false, false, () -> 1);
        check(!state.live() && state.units(110) == 0, "letting go ends the charge");
        state.update(106 + grace + 1, false, false, () -> 1);
        check(state.idle(106 + grace + 1), "ended charge goes idle");

        RailgunChargeState waiting = new RailgunChargeState();
        waiting.update(0, true, true, () -> 1);
        waiting.update(grace + 1, false, true, () -> 1);
        check(!waiting.live(), "gap longer than the grace ends the charge");
        waiting.update(grace + 2, true, true, () -> 1);
        check(waiting.units(grace + 12) == RailgunData.units(10, 1), "charge after a long gap starts over");

        RailgunChargeState capped = new RailgunChargeState();
        capped.update(0, true, true, () -> 1);
        capped.cap(5, 3);
        check(capped.units(100) == 5 && capped.units(100, 0.5F) == 5F, "energy cap stops the orb growing");

        RailgunChargeState early = new RailgunChargeState();
        early.cap(4, 50);
        check(!early.idle(50), "cap that arrives first is kept");
        early.update(52, true, false, () -> 1);
        check(early.units(80) == 4, "cap that arrives before the charge is seen still applies");
        RailgunChargeState stale = new RailgunChargeState();
        stale.cap(4, 0);
        check(stale.idle(grace + 1), "old cap alone does not keep a state alive");
        stale.update(100, true, true, () -> 1);
        check(stale.units(120) == RailgunData.units(20, 1), "old cap does not limit a new charge");

        RailgunChargeState fired = new RailgunChargeState();
        fired.update(0, true, true, () -> 1);
        fired.endUntilReleased(0);
        check(!fired.live(), "another player's shot ends their charge");
        fired.update(1, true, true, () -> 1);
        check(!fired.live(), "lingering use flag after firing does not restart the charge");
        fired.update(2, false, false, () -> 1);
        check(fired.idle(2), "fired charge goes idle once the use flag drops");
        fired.update(3, true, true, () -> 1);
        check(fired.live() && fired.units(5) == RailgunData.units(2, 1), "next charge starts fresh");
        RailgunChargeState held = new RailgunChargeState();
        held.update(0, true, true, () -> 1);
        held.endUntilReleased(1);
        held.update(1 + grace, true, true, () -> 1);
        check(!held.live(), "use flag is held back for the grace after a shot");
        held.update(2 + grace, true, true, () -> 1);
        check(
            held.live() && held.units(12 + grace) == RailgunData.units(10, 1),
            "a charge started right after a shot shows up once the grace passes");
        RailgunChargeState own = new RailgunChargeState();
        own.update(0, true, true, () -> 1);
        own.end();
        check(!own.live(), "own shot ends the charge");
        own.update(1, true, true, () -> 1);
        check(own.live() && own.units(5) == RailgunData.units(4, 1), "own new charge is not held back");

        RailgunChargeState slow = new RailgunChargeState();
        slow.update(0, true, false, () -> 0.2);
        check(slow.units(10) == RailgunData.units(10, 1), "charge rate never drops below 1");
    }

    private static void railgunBolts() {
        check(RailgunBolts.orbArcs(0F) == 0 && RailgunBolts.orbArcs(0.04F) == 0, "no orb arcs before charging");
        check(RailgunBolts.orbArcs(RailgunBolts.MIN_INTENSITY) == 1, "orb starts with one surface arc");
        check(RailgunBolts.orbArcs(1F) == RailgunBolts.ORB_MAX_ARCS, "full charge shows every surface arc");
        check(
            RailgunBolts.dischargeSlots(RailgunBolts.DISCHARGE_START) == 0
                && RailgunBolts.dischargeChance(RailgunBolts.DISCHARGE_START) == 0,
            "no discharges before half charge");
        check(RailgunBolts.dischargeSlots(1F) == RailgunBolts.ORB_MAX_DISCHARGES, "full charge allows a discharge");
        check(
            RailgunBolts.orb(1, 0, Float.NaN)
                .isEmpty(),
            "unreadable charge draws no lightning");
        int previousArcs = 0, previousSlots = 0;
        double previousChance = 0;
        for (int step = 0; step <= 20; step++) {
            float charge = step / 20F;
            int arcs = RailgunBolts.orbArcs(charge), slots = RailgunBolts.dischargeSlots(charge);
            double chance = RailgunBolts.dischargeChance(charge);
            check(
                arcs >= previousArcs && slots >= previousSlots && chance >= previousChance,
                "orb lightning never thins out as charge rises " + step);
            previousArcs = arcs;
            previousSlots = slots;
            previousChance = chance;
        }
        check(
            RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(7, 100, 0.6F)),
            "orb lightning is repeatable");
        check(
            !RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(7, 103, 0.6F)),
            "orb lightning flickers");
        check(
            !RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(8, 100, 0.6F)),
            "each shooter has its own bolts");
        EquipmentVec origin = new EquipmentVec(0, 0, 0);
        int fullArcs = RailgunBolts.orbArcs(1F), discharges = 0, ticks = 300;
        double farthest = 0;
        for (long tick = 0; tick < ticks; tick++) {
            check(
                RailgunBolts.orb(3, tick, 0.4F)
                    .size() == RailgunBolts.orbArcs(0.4F),
                "only surface arcs below half charge");
            List<RailgunBolts.Bolt> bolts = RailgunBolts.orb(3, tick, 1F);
            check(bolts.size() <= fullArcs + RailgunBolts.ORB_MAX_DISCHARGES, "orb lightning stays sparse");
            for (int index = 0; index < bolts.size(); index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                if (index >= fullArcs) {
                    discharges++;
                    check(
                        Math.abs(
                            Math.sqrt(
                                points.get(0)
                                    .distanceToSqr(origin))
                                - 1)
                            < 1.0E-9,
                        "discharge leaves from the orb surface");
                    continue;
                }
                for (EquipmentVec point : points) {
                    double distance = Math.sqrt(point.distanceToSqr(origin));
                    if (distance < RailgunBolts.ARC_LOW - 1.0E-9 || distance > RailgunBolts.ARC_HIGH + 1.0E-9) {
                        check(false, "surface arcs hug the orb");
                        break;
                    }
                }
            }
            farthest = Math.max(farthest, reach(bolts, origin));
        }
        check(
            discharges > ticks / 10 && discharges < ticks * RailgunBolts.ORB_MAX_DISCHARGES,
            "discharges come and go");
        check(
            farthest <= (1 + RailgunBolts.DISCHARGE_MIN_REACH + RailgunBolts.DISCHARGE_REACH_GAIN) * 1.3,
            "discharges stay near the orb");

        check(
            RailgunBolts.body(1, 0, 0.04F)
                .isEmpty(),
            "no gun lightning before charging");
        check(RailgunBolts.flowsPerSide(1F) == RailgunBolts.FLOW_MAX_PER_SIDE, "full charge fills the groove current");
        check(RailgunBolts.sparkChance(RailgunBolts.SPARK_START) == 0, "no sparks before the spark threshold");
        check(RailgunBolts.hopChance(RailgunBolts.HOP_START) == 0, "no hops before the hop threshold");
        check(RailgunBolts.flowPeriod(1F) < RailgunBolts.flowPeriod(0.1F), "current flows faster as charge rises");
        check(
            RailgunBolts.flowSpan(1F) <= RailgunBolts.GROOVE_CENTER - RailgunBolts.GROOVE_FRONT + 1.0E-9
                && RailgunBolts.flowSpan(1F) <= RailgunBolts.GROOVE_BACK - RailgunBolts.GROOVE_CENTER + 1.0E-9,
            "current stays within the groove");
        int flows = 0, sparks = 0, hops = 0, forward = 0, backward = 0, across = 0, liftUp = 0, liftDown = 0;
        int anchorsFront = 0, anchorsBack = 0;
        boolean moves = false, earlyQuiet = true;
        for (int frame = 0; frame < 400; frame++) {
            double time = 1000 + frame * 0.5;
            List<RailgunBolts.Bolt> bolts = RailgunBolts.body(5, time, 1F);
            check(
                bolts.size() <= 2 + RailgunBolts.ANCHORS_PER_SIDE * 2
                    + RailgunBolts.FLOW_MAX_PER_SIDE * 2
                    + 2
                    + RailgunBolts.HOP_SLOTS,
                "gun lightning stays sparse");
            moves |= !bolts.equals(RailgunBolts.body(5, time + 0.25, 1F));
            for (int index = 0; index < 2; index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                check(
                    Math.abs(
                        points.get(0)
                            .z() - RailgunBolts.GROOVE_CENTER)
                        < 1.0E-9
                        && points.get(points.size() - 1)
                            .equals(RailgunBolts.MUZZLE)
                        && inGroove(points.subList(0, points.size() - 1)),
                    "tether runs from the groove centre to the muzzle");
            }
            int fixed = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2;
            for (int index = 2; index < fixed; index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                double reach = points.get(points.size() - 1)
                    .z() - RailgunBolts.GROOVE_CENTER;
                check(
                    inGroove(points) && Math.abs(
                        points.get(0)
                            .z() - RailgunBolts.GROOVE_CENTER)
                        < 1.0E-9
                        && Math.abs(reach) <= RailgunBolts.ANCHOR_MIN_REACH + RailgunBolts.ANCHOR_REACH_GAIN + 1.0E-9,
                    "fixed arcs reach out from the groove centre");
                if (reach > 0) anchorsBack++;
                else anchorsFront++;
            }
            for (int index = fixed; index < bolts.size(); index++) {
                RailgunBolts.Bolt bolt = bolts.get(index);
                List<EquipmentVec> points = bolt.points();
                EquipmentVec first = points.get(0), last = points.get(points.size() - 1);
                check(bolt.width() <= 0.0075, "gun lightning is thin");
                if (inGroove(points)) {
                    flows++;
                    check(
                        Math.abs(last.z() - first.z())
                            <= RailgunBolts.FLOW_LENGTH + RailgunBolts.FLOW_LENGTH_GAIN + 1.0E-9,
                        "groove current is short");
                } else if (Math.abs(Math.abs(first.x()) - RailgunBolts.GROOVE_OUTER) < 1.0E-9) {
                    sparks++;
                    check(Math.abs(last.x()) > RailgunBolts.GROOVE_OUTER, "sparks leap outward from the groove");
                } else {
                    hops++;
                    check(onEdge(first) && onEdge(last), "hops jump between points on the gun");
                    if (last.z() > first.z()) forward++;
                    else if (last.z() < first.z()) backward++;
                    if (Math.abs(last.x() - first.x()) > 1.0E-9 || Math.abs(last.y() - first.y()) > 1.0E-9) across++;
                    double bend = points.get(points.size() / 2)
                        .y() - (first.y() + last.y()) / 2;
                    if (bend > 0) liftUp++;
                    else liftDown++;
                }
            }
            List<RailgunBolts.Bolt> early = RailgunBolts.body(5, time, 0.15F);
            for (int index = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2; index < early.size(); index++)
                earlyQuiet &= inGroove(
                    early.get(index)
                        .points());
        }
        check(flows > 0 && sparks > 0 && hops > 0, "full charge shows current, sparks and hops");
        check(forward > 0 && backward > 0, "hops jump both ways along the gun");
        check(anchorsFront > 0 && anchorsBack > 0, "fixed arcs reach toward both ends of the groove");
        check(across > 0, "some hops jump across to another edge");
        check(liftUp > 0 && liftDown > 0, "hops arch in different directions");
        check(moves, "groove current moves smoothly between ticks");
        check(earlyQuiet, "early charge only shows the tether and the groove current");
        int matched = 0, lost = 0;
        for (int frame = 0; frame < 1200; frame++) {
            double age = 2 + frame * 0.05, next = age + 0.05;
            float charge = (float) Math.min(1, age / 60), nextCharge = (float) Math.min(1, next / 60);
            List<double[]> before = currents(RailgunBolts.body(5, age, charge));
            List<double[]> after = currents(RailgunBolts.body(5, next, nextCharge));
            for (double[] current : before) {
                boolean found = false;
                for (double[] later : after)
                    found |= Math.abs(current[0] - later[0]) < 0.03 && Math.abs(current[1] - later[1]) < 0.03;
                if (found) matched++;
                else if (Math.abs(current[1] - RailgunBolts.GROOVE_CENTER) < RailgunBolts.flowSpan(charge) - 0.03)
                    lost++;
            }
        }
        check(matched > 0 && lost == 0, "groove current flows smoothly while the charge rises");
        EquipmentVec legFrom = new EquipmentVec(1, 2, 3), legTo = legFrom.add(0, 0, 0.5);
        List<RailgunBolts.Bolt> legs = RailgunBolts.tethers(9, 40, 0.5F, legFrom, legTo);
        check(legs.size() == 2, "two tethers bind the orb");
        for (RailgunBolts.Bolt leg : legs) check(
            leg.points()
                .get(0)
                .equals(legFrom)
                && Math.sqrt(
                    leg.points()
                        .get(
                            leg.points()
                                .size() - 1)
                        .distanceToSqr(legTo))
                    < 0.25,
            "tether runs from the muzzle into the orb");
        check(
            RailgunBolts.tethers(9, 40, 0.01F, legFrom, legTo)
                .isEmpty()
                && RailgunBolts.tethers(9, 40, 1F, null, legTo)
                    .isEmpty(),
            "no tether before charging or without a muzzle");

        float[] color = new float[3];
        RailgunOrbRenderer.color(RailgunChargeTier.EHV2.ordinal(), 0F, color);
        check(color[2] > color[0], "lightning is blue before full charge");
        RailgunOrbRenderer.color(RailgunChargeTier.EHV3.ordinal(), 0F, color);
        check(color[0] > color[2], "lightning turns pink at full charge");
        RailgunOrbRenderer.color(RailgunChargeTier.EHV3.ordinal(), 1F, color);
        check(color[0] > 0.9F && color[1] > 0.9F && color[2] > 0.9F, "lightning core is near white");
    }

    private static List<double[]> currents(List<RailgunBolts.Bolt> bolts) {
        List<double[]> currents = new ArrayList<>();
        for (int index = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2; index < bolts.size(); index++) {
            List<EquipmentVec> points = bolts.get(index)
                .points();
            if (inGroove(points)) currents.add(
                new double[] { points.get(0)
                    .z(),
                    points.get(points.size() - 1)
                        .z() });
        }
        return currents;
    }

    private static boolean inGroove(List<EquipmentVec> points) {
        for (EquipmentVec point : points) {
            if (point.z() < RailgunBolts.GROOVE_FRONT - 1.0E-9 || point.z() > RailgunBolts.GROOVE_BACK + 1.0E-9
                || point.y() < RailgunBolts.GROOVE_LOW - 1.0E-9
                || point.y() > RailgunBolts.GROOVE_HIGH + 1.0E-9
                || Math.abs(point.x()) < RailgunBolts.GROOVE_INNER - 1.0E-9
                || Math.abs(point.x()) > RailgunBolts.GROOVE_OUTER + 1.0E-9) return false;
        }
        return true;
    }

    private static boolean onEdge(EquipmentVec point) {
        for (RailgunBolts.Edge edge : RailgunBolts.HOP_EDGES) {
            for (int side = -1; side <= 1; side += 2) {
                EquipmentVec from = edge.at(0, side), to = edge.at(1, side);
                EquipmentVec along = to.subtract(from);
                double t = point.subtract(from)
                    .dot(along) / along.dot(along);
                if (t < -1.0E-9 || t > 1 + 1.0E-9) continue;
                if (Math.sqrt(point.distanceToSqr(from.add(along.scale(t)))) < 1.0E-9) return true;
            }
        }
        return false;
    }

    private static double reach(List<RailgunBolts.Bolt> bolts, EquipmentVec origin) {
        double farthest = 0;
        for (RailgunBolts.Bolt bolt : bolts) for (EquipmentVec point : bolt.points())
            farthest = Math.max(farthest, Math.sqrt(point.distanceToSqr(origin)));
        return farthest;
    }

    private static void railgunOrbPulse() {
        near(0, RailgunOrbRenderer.tension(RailgunOrbRenderer.CALM_UNTIL), "orb stays calm early in the charge");
        near(0, RailgunOrbRenderer.tension(Float.NaN), "unreadable charge keeps the orb calm");
        near(1, RailgunOrbRenderer.tension(1F), "orb is at full tension when fully charged");
        float previous = 0F;
        for (int step = 0; step <= 40; step++) {
            float tension = RailgunOrbRenderer.tension(step / 40F);
            check(tension >= previous, "orb tension never drops as charge rises " + step);
            previous = tension;
        }
        double calmSwing = 0, fullSwing = 0, largestJump = 0;
        for (int sample = 0; sample < 400; sample++) {
            double time = 1_000_000 + sample * 0.37;
            calmSwing = Math.max(calmSwing, Math.abs(RailgunOrbRenderer.pulse(0F, time) - 1));
            fullSwing = Math.max(fullSwing, Math.abs(RailgunOrbRenderer.pulse(1F, time) - 1));
            for (int step = 0; step < 100; step++) largestJump = Math.max(
                largestJump,
                Math.abs(
                    RailgunOrbRenderer.pulse((step + 1) / 100F, time) - RailgunOrbRenderer.pulse(step / 100F, time)));
        }
        check(calmSwing <= RailgunOrbRenderer.BREATH_DEPTH + 1.0E-9, "calm orb only breathes gently");
        check(fullSwing > calmSwing * 2, "charged orb throbs harder than a calm one");
        check(largestJump < 0.01, "orb size never jumps as charge rises");
        near(1, RailgunOrbRenderer.tremble(0F, 4, 123), "calm orb does not tremble");
        near(0, RailgunOrbRenderer.shake(0F, 4, 123, 0), "calm orb does not shake");
        boolean trembles = false;
        for (long tick = 0; tick < 40; tick++) {
            double tremble = RailgunOrbRenderer.tremble(1F, 4, tick);
            check(Math.abs(tremble - 1) <= RailgunOrbRenderer.TREMBLE_DEPTH + 1.0E-9, "tremble stays bounded");
            check(
                Math.abs(RailgunOrbRenderer.shake(1F, 4, tick, 1)) <= RailgunOrbRenderer.SHAKE_DEPTH + 1.0E-9,
                "shake stays bounded");
            trembles |= Math.abs(tremble - 1) > 0.01;
        }
        check(trembles, "fully charged orb trembles");
        near(0, RailgunOrbRenderer.flare(RailgunOrbRenderer.FLARE_FROM, 0), "no flare before the critical point");
        near(1, RailgunOrbRenderer.flare(1F, RailgunOrbRenderer.FLARE_PERIOD * 7), "full charge flares at each beat");
        check(
            RailgunOrbRenderer.flare(1F, RailgunOrbRenderer.FLARE_PERIOD * 7 + RailgunOrbRenderer.FLARE_PERIOD / 2.0)
                < 0.2F,
            "flare fades between beats");
    }

    private static void railgunColors() throws ReflectiveOperationException {
        float[] color = new float[3];
        float[] ehvBeam = { 1F, 0.3F, 0.62F }, ehvBeamRim = { 1F, 0.84F, 0.94F };
        float[] beam = { 0.22F, 0.72F, 1F }, beamRim = { 0.84F, 0.97F, 1F };
        float[] orb = { 0.35F, 0.86F, 1F }, orbRim = { 0.94F, 1F, 1F };
        float[] fullOrb = { 1F, 0.42F, 0.68F }, fullOrbRim = { 1F, 0.94F, 1F };
        int ehv2 = RailgunChargeTier.EHV2.ordinal(), ehv3 = RailgunChargeTier.EHV3.ordinal();
        for (float mix : new float[] { 0F, 0.25F, 1F }) {
            check(
                Arrays.equals(RailgunOrbRenderer.beamColor(true, mix, color), blended(ehvBeam, ehvBeamRim, mix)),
                "EHV beam colour at mix " + mix);
            check(
                Arrays.equals(RailgunOrbRenderer.beamColor(false, mix, color), blended(beam, beamRim, mix)),
                "high voltage beam colour at mix " + mix);
            check(
                Arrays.equals(RailgunOrbRenderer.color(ehv2, mix, color), blended(orb, orbRim, mix)),
                "orb colour before full charge at mix " + mix);
            check(
                Arrays.equals(RailgunOrbRenderer.color(ehv3, mix, color), blended(fullOrb, fullOrbRim, mix)),
                "orb colour at full charge at mix " + mix);
        }
        check(
            RailgunOrbRenderer.PLASMA_ARC.equals(
                new Style(
                    new Color(1F, 0.3F, 0.6F),
                    new Color(1F, 0.55F, 0.75F),
                    new Color(1F, 0.8F, 0.9F),
                    new Color(1F, 0.38F, 0.68F),
                    new Color(1F, 0.75F, 0.88F),
                    1)),
            "plasma arc colours");
        check(
            RailgunOrbRenderer.HIGH_VOLTAGE_ARC.equals(
                new Style(
                    new Color(0.25F, 0.75F, 1F),
                    new Color(0.55F, 0.88F, 1F),
                    new Color(0.85F, 0.97F, 1F),
                    new Color(0.4F, 0.82F, 1F),
                    new Color(0.7F, 0.94F, 1F),
                    1)),
            "high voltage arc colours");
        check(
            RailgunOrbRenderer.IMPACT_SPARK_ARC.equals(
                new Style(
                    new Color(0.9F, 0.25F, 0.55F),
                    new Color(0.95F, 0.535F, 0.715F),
                    new Color(1F, 0.82F, 0.88F),
                    new Color(0.95F, 0.33F, 0.63F),
                    new Color(1F, 0.76F, 0.86F),
                    0.8125)),
            "impact spark colours");
        int[] hud = { 0x3C6E8F, 0x50CFFF, 0x8798FF, 0xFF509A };
        float[] stops = { 0, RailgunChargeTier.EHV1.chargeTicks, RailgunChargeTier.EHV2.chargeTicks,
            RailgunChargeTier.EHV3.chargeTicks };
        for (int i = 0; i < hud.length; i++) check(
            (int) call(RailgunHudRenderer.class, "chargeRgb", null, new Class<?>[] { float.class }, stops[i]) == hud[i],
            "charge HUD colour at stop " + i);
        check(
            RailgunOrbRenderer.SHOCKWAVE_RED == 1F && RailgunOrbRenderer.SHOCKWAVE_GREEN == 0.5F
                && RailgunOrbRenderer.SHOCKWAVE_BLUE == 0.75F,
            "impact shockwave colour");
    }

    private static float[] blended(float[] core, float[] rim, float mix) {
        return new float[] { core[0] + (rim[0] - core[0]) * mix, core[1] + (rim[1] - core[1]) * mix,
            core[2] + (rim[2] - core[2]) * mix };
    }
}
