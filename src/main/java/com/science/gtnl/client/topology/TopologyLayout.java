package com.science.gtnl.client.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.MathHelper;

import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologyRelay;
import com.science.gtnl.api.topology.TopologySnapshot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Radial tree layout that sizes every network before it places any of them.
 * <p>
 * Each network carries a cloud of relays and devices, and that cloud has a measurable radius. The ring a network sits
 * on is pushed out far enough to clear the cloud of the ring inside it, and the angular wedge a network receives is
 * proportional to the width its own cloud needs. Because a child's wedge is always carved out of its parent's, and a
 * cloud always fits inside the wedge that paid for it, tree edges cannot cross and two networks cannot overlap.
 */
@SideOnly(Side.CLIENT)
public class TopologyLayout {

    private static final float TURN = (float) (Math.PI * 2);
    private static final float RING_GAP = 44.0F;
    private static final float NODE_ALLOWANCE = 26.0F;
    private static final float SATELLITE_PAD = 15.0F;

    private static final float MIN_DEVICE_ORBIT = 21.0F;
    private static final float DEVICE_SPACING = 18.0F;
    private static final float DEVICE_ARC = (float) (Math.PI * 1.5);

    private static final float MIN_RELAY_ORBIT = 36.0F;
    private static final float RELAY_SPACING = 34.0F;
    private static final float RELAY_ARC = (float) Math.PI;

    private static final float BRIDGE_SPREAD = 0.11F;
    private static final float BRIDGE_MIN_FRACTION = 0.28F;
    private static final float BRIDGE_MAX_FRACTION = 0.72F;
    private static final float LOOSE_END_ORBIT = 30.0F;
    private static final float LOOSE_END_ARC = (float) (Math.PI * 1.2);
    private static final float LOOSE_END_SPREAD = 0.55F;

    private static final int MAX_DEPTH_GUARD = 64;

    public static class Point {

        public float x, y, targetX, targetY, angle;
        public boolean placed;

        void retarget(float nextX, float nextY, float nextAngle) {
            targetX = nextX;
            targetY = nextY;
            angle = nextAngle;
            if (!placed) {
                x = nextX;
                y = nextY;
                placed = true;
            }
        }

        void approach(float factor) {
            x += (targetX - x) * factor;
            y += (targetY - y) * factor;
        }
    }

    private final Map<Integer, Point> networkPoints = new HashMap<>();
    private final Map<Integer, Point> relayPoints = new HashMap<>();
    private final Map<Integer, Point> devicePoints = new HashMap<>();
    private final Map<Integer, Point> bridgePoints = new HashMap<>();

    private final Map<Integer, TopologyBridge> parentBridge = new HashMap<>();
    private final Map<Integer, List<Integer>> networkChildren = new HashMap<>();
    private final Map<Integer, List<Integer>> relayChildren = new HashMap<>();
    private final Map<Integer, List<Integer>> deviceChildren = new HashMap<>();
    private final Map<Integer, Integer> depths = new HashMap<>();
    private final Map<Integer, Float> cloud = new HashMap<>();
    private final Map<Integer, Float> demand = new HashMap<>();
    private final Map<Integer, Float> localRadius = new HashMap<>();
    private final Map<Integer, Float> labelRoom = new HashMap<>();
    private final Map<Integer, Float> outward = new HashMap<>();

    private float[] ringRadii = { 0.0F };
    private float outerExtent = NODE_ALLOWANCE;
    private long builtRevision = -1;

    public void update(TopologySnapshot snapshot, long revision, float easing) {
        if (revision != builtRevision) {
            builtRevision = revision;
            rebuild(snapshot);
        }
        for (Point point : networkPoints.values()) point.approach(easing);
        // Everything hanging off a network follows its eased position exactly, parents before children.
        placeRelays(snapshot, true);
        placeDevices(snapshot, true);
        placeBridges(snapshot, true);
    }

    public Point network(int id) {
        return networkPoints.get(id);
    }

    public Point relay(int id) {
        return relayPoints.get(id);
    }

    public Point device(int id) {
        return devicePoints.get(id);
    }

    public Point bridge(int id) {
        return bridgePoints.get(id);
    }

    public float outerRadius() {
        return outerExtent;
    }

    public int ringCount() {
        return ringRadii.length;
    }

    public float ringRadius(int depth) {
        return ringRadii[MathHelper.clamp_int(depth, 0, ringRadii.length - 1)];
    }

    /**
     * Horizontal room a network caption may use, which is the narrower of its own cloud and the wedge it was given.
     */
    public float labelRoom(int networkId) {
        return labelRoom.getOrDefault(networkId, NODE_ALLOWANCE * 2.0F);
    }

    public TopologyBridge parentOf(int networkId) {
        return parentBridge.get(networkId);
    }

    private void rebuild(TopologySnapshot snapshot) {
        parentBridge.clear();
        networkChildren.clear();
        relayChildren.clear();
        deviceChildren.clear();
        depths.clear();
        cloud.clear();
        demand.clear();
        localRadius.clear();
        labelRoom.clear();

        TopologyNetwork main = snapshot.networks()
            .isEmpty() ? null
                : snapshot.networks()
                    .get(0);
        if (main == null) {
            ringRadii = new float[] { 0.0F };
            outerExtent = NODE_ALLOWANCE;
            networkPoints.clear();
            return;
        }

        for (TopologyNetwork network : snapshot.networks()) depths.put(network.id(), network.depth());
        buildNetworkTree(snapshot, main);
        buildSatelliteTree(snapshot);
        measure(snapshot);
        placeNetworks(snapshot, main);
        placeRelays(snapshot, false);
        placeDevices(snapshot, false);
        placeBridges(snapshot, false);
    }

    private void buildNetworkTree(TopologySnapshot snapshot, TopologyNetwork main) {
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!bridge.resolved() || bridge.loop()) continue;
            TopologyNetwork from = snapshot.network(bridge.from());
            TopologyNetwork to = snapshot.network(bridge.to());
            if (from == null || to == null || to.depth() <= from.depth()) continue;
            if (parentBridge.putIfAbsent(bridge.to(), bridge) == null)
                networkChildren.computeIfAbsent(bridge.from(), ignored -> new ArrayList<>())
                    .add(bridge.to());
        }
        // Anything the discovery could not attach to the tree is adopted by the main network rather than dropped.
        for (TopologyNetwork network : snapshot.networks()) {
            if (network.id() == main.id() || parentBridge.containsKey(network.id())) continue;
            networkChildren.computeIfAbsent(main.id(), ignored -> new ArrayList<>())
                .add(network.id());
        }
    }

    private void buildSatelliteTree(TopologySnapshot snapshot) {
        for (TopologyRelay relay : snapshot.relays()) {
            relayChildren.computeIfAbsent(anchorKey(relay), ignored -> new ArrayList<>())
                .add(relay.id());
        }
        for (TopologyDevice device : snapshot.devices()) {
            deviceChildren.computeIfAbsent(anchorKey(device), ignored -> new ArrayList<>())
                .add(device.id());
        }
    }

    /**
     * Sizes every cloud bottom up, then every network, then every ring. Relay ids always name a parent before its
     * child, so walking them backwards visits children first without any recursion.
     */
    private void measure(TopologySnapshot snapshot) {
        List<TopologyRelay> relays = snapshot.relays();
        for (int index = relays.size() - 1; index >= 0; index--) {
            int key = relayKey(
                relays.get(index)
                    .id());
            cloud.put(key, cloudOf(key));
        }
        int maxDepth = 0;
        for (TopologyNetwork network : snapshot.networks()) {
            int key = networkKey(network.id());
            cloud.put(key, cloudOf(key));
            localRadius.put(network.id(), Math.max(NODE_ALLOWANCE, cloud.get(key)));
            maxDepth = Math.max(maxDepth, network.depth());
        }

        // A network needs at least the width of its own cloud, and at least as much as all of its children together.
        for (int depth = maxDepth; depth >= 0; depth--) {
            for (TopologyNetwork network : snapshot.networks()) {
                if (network.depth() != depth) continue;
                float children = 0.0F;
                for (int child : networkChildren.getOrDefault(network.id(), List.of())) {
                    children += demand.getOrDefault(child, 0.0F);
                }
                demand.put(network.id(), Math.max(localRadius.get(network.id()) * 2.0F, children));
            }
        }

        float[] widest = new float[maxDepth + 1];
        float[] needed = new float[maxDepth + 1];
        for (TopologyNetwork network : snapshot.networks()) {
            widest[network.depth()] = Math.max(widest[network.depth()], localRadius.get(network.id()));
            // Wedges are handed out in proportion to demand, so the ring has to be long enough for the demand total.
            needed[network.depth()] += demand.getOrDefault(network.id(), 0.0F);
        }
        ringRadii = new float[maxDepth + 1];
        for (int depth = 1; depth <= maxDepth; depth++) {
            float cleared = ringRadii[depth - 1] + widest[depth - 1] + widest[depth] + RING_GAP;
            // The ring must also be long enough around for every cloud on it to sit side by side.
            ringRadii[depth] = Math.max(cleared, needed[depth] / TURN);
        }
        outerExtent = ringRadii[maxDepth] + widest[maxDepth];
    }

    private float cloudOf(int anchor) {
        float radius = SATELLITE_PAD;
        List<Integer> devices = deviceChildren.getOrDefault(anchor, List.of());
        if (!devices.isEmpty()) radius = Math.max(radius, deviceOrbit(devices.size()) + SATELLITE_PAD);
        List<Integer> relays = relayChildren.getOrDefault(anchor, List.of());
        if (relays.isEmpty()) return radius;
        float orbit = relayOrbit(anchor);
        for (int child : relays) {
            radius = Math.max(radius, orbit + cloud.getOrDefault(relayKey(child), SATELLITE_PAD));
        }
        return radius;
    }

    private static float deviceOrbit(int count) {
        return Math.max(MIN_DEVICE_ORBIT, count * DEVICE_SPACING / DEVICE_ARC);
    }

    /**
     * Relays always sit outside the device ring of the same anchor, so a hop never runs through a device, and far
     * enough out that the arc they share is long enough to hold every child cloud side by side. Because each child
     * then receives a share of the arc proportional to its own width, two sub clouds cannot reach into each other.
     */
    private float relayOrbit(int anchor) {
        List<Integer> relays = relayChildren.getOrDefault(anchor, List.of());
        if (relays.isEmpty()) return MIN_RELAY_ORBIT;
        float spread = Math
            .max(MIN_RELAY_ORBIT, Math.max(relays.size() * RELAY_SPACING, childWidth(anchor)) / RELAY_ARC);
        int devices = deviceChildren.getOrDefault(anchor, List.of())
            .size();
        return devices == 0 ? spread : Math.max(spread, deviceOrbit(devices) + SATELLITE_PAD);
    }

    private float childWidth(int anchor) {
        float total = 0.0F;
        for (int child : relayChildren.getOrDefault(anchor, List.of())) {
            total += 2.0F * cloud.getOrDefault(relayKey(child), SATELLITE_PAD);
        }
        return total;
    }

    private void placeNetworks(TopologySnapshot snapshot, TopologyNetwork main) {
        Set<Integer> live = new HashSet<>();
        for (TopologyNetwork network : snapshot.networks()) live.add(network.id());
        networkPoints.keySet()
            .retainAll(live);
        point(networkPoints, main.id()).retarget(0, 0, 0);
        labelRoom.put(main.id(), localRadius.getOrDefault(main.id(), NODE_ALLOWANCE) * 2.0F);
        spread(main.id(), 0.0F, TURN, 0);
    }

    private void spread(int id, float from, float to, int guard) {
        List<Integer> children = guard >= MAX_DEPTH_GUARD ? List.of() : networkChildren.getOrDefault(id, List.of());
        if (children.isEmpty()) return;
        float total = 0.0F;
        for (int child : children) total += demand.getOrDefault(child, 1.0F);
        if (total <= 0.0F) return;
        float cursor = from;
        for (int child : children) {
            float span = (to - from) * demand.getOrDefault(child, 1.0F) / total;
            float angle = cursor + span * 0.5F;
            float radius = ringRadius(depths.getOrDefault(child, 1));
            point(networkPoints, child).retarget(MathHelper.cos(angle) * radius, MathHelper.sin(angle) * radius, angle);
            labelRoom.put(
                child,
                Math.min(localRadius.getOrDefault(child, NODE_ALLOWANCE) * 2.0F, Math.max(1.0F, span * radius)));
            spread(child, cursor, cursor + span, guard + 1);
            cursor += span;
        }
    }

    private void placeRelays(TopologySnapshot snapshot, boolean follow) {
        outward.clear();
        for (TopologyNetwork network : snapshot.networks()) {
            Point point = networkPoints.get(network.id());
            if (point != null)
                outward.put(networkKey(network.id()), network.isMain() ? -(float) Math.PI / 2 : point.angle);
        }
        Set<Integer> live = new HashSet<>();
        Map<Integer, Float> cursor = new HashMap<>();
        for (TopologyRelay relay : snapshot.relays()) {
            live.add(relay.id());
            int key = anchorKey(relay);
            Point anchor = relay.attachedToNetwork() ? networkPoints.get(relay.network())
                : relayPoints.get(relay.parent());
            if (anchor == null) continue;
            float base = outward.getOrDefault(key, -(float) Math.PI / 2);
            float width = childWidth(key);
            float own = 2.0F * cloud.getOrDefault(relayKey(relay.id()), SATELLITE_PAD);
            float span = width <= 0.0F ? RELAY_ARC : RELAY_ARC * own / width;
            float from = cursor.merge(key, span, Float::sum) - span;
            float angle = base - RELAY_ARC / 2 + from + span * 0.5F;
            float orbit = relayOrbit(key);
            Point place = point(relayPoints, relay.id());
            target(place, anchor.x + MathHelper.cos(angle) * orbit, anchor.y + MathHelper.sin(angle) * orbit, follow);
            place.angle = angle;
            outward.put(relayKey(relay.id()), angle);
        }
        relayPoints.keySet()
            .retainAll(live);
    }

    private void placeDevices(TopologySnapshot snapshot, boolean follow) {
        Set<Integer> live = new HashSet<>();
        Map<Integer, Integer> seen = new HashMap<>();
        for (TopologyDevice device : snapshot.devices()) {
            live.add(device.id());
            int key = anchorKey(device);
            Point owner = device.relay() == TopologyDevice.DIRECT ? networkPoints.get(device.network())
                : relayPoints.get(device.relay());
            if (owner == null) continue;
            int total = Math.max(
                1,
                deviceChildren.getOrDefault(key, List.of())
                    .size());
            int index = seen.merge(key, 1, Integer::sum) - 1;
            float orbit = deviceOrbit(total);
            // The arc is centred on the direction this anchor points away from its own parent, and stops short of
            // the far side so the caption below the anchor keeps its room.
            float base = outward.getOrDefault(key, -(float) Math.PI / 2);
            float angle = base - DEVICE_ARC / 2 + DEVICE_ARC * (index + 0.5F) / total;
            target(
                point(devicePoints, device.id()),
                owner.x + MathHelper.cos(angle) * orbit,
                owner.y + MathHelper.sin(angle) * orbit,
                follow);
        }
        devicePoints.keySet()
            .retainAll(live);
    }

    private void placeBridges(TopologySnapshot snapshot, boolean follow) {
        Set<Integer> live = new HashSet<>();
        Map<Long, Integer> pairTotals = new HashMap<>();
        Map<Long, Integer> pairSeen = new HashMap<>();
        Map<Integer, Integer> looseTotals = new HashMap<>();
        Map<Integer, Integer> looseSeen = new HashMap<>();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (bridge.resolved() && !bridge.loop()) pairTotals.merge(pairKey(bridge), 1, Integer::sum);
            else looseTotals.merge(bridge.from(), 1, Integer::sum);
        }
        for (TopologyBridge bridge : snapshot.bridges()) {
            live.add(bridge.id());
            Point from = networkPoints.get(bridge.from());
            if (from == null) continue;
            Point own = point(bridgePoints, bridge.id());
            Point to = bridge.resolved() && !bridge.loop() ? networkPoints.get(bridge.to()) : null;
            if (to == null) {
                // A link with no reachable far side hangs outside its owner's cloud, fanned inside a fixed arc.
                int total = Math.max(1, looseTotals.getOrDefault(bridge.from(), 1));
                int index = looseSeen.merge(bridge.from(), 1, Integer::sum) - 1;
                float step = total <= 1 ? 0.0F : Math.min(LOOSE_END_SPREAD, LOOSE_END_ARC / (total - 1));
                float base = outward.getOrDefault(networkKey(bridge.from()), -(float) Math.PI / 2);
                float angle = base + (index - (total - 1) * 0.5F) * step;
                float orbit = localRadius.getOrDefault(bridge.from(), NODE_ALLOWANCE) + LOOSE_END_ORBIT;
                target(own, from.x + MathHelper.cos(angle) * orbit, from.y + MathHelper.sin(angle) * orbit, follow);
                continue;
            }
            // Parallel boundaries slide along the straight link instead of being pushed sideways off it.
            long key = pairKey(bridge);
            int total = Math.max(1, pairTotals.getOrDefault(key, 1));
            int index = pairSeen.merge(key, 1, Integer::sum) - 1;
            float fraction = MathHelper.clamp_float(
                0.5F + (index - (total - 1) * 0.5F) * BRIDGE_SPREAD,
                BRIDGE_MIN_FRACTION,
                BRIDGE_MAX_FRACTION);
            target(own, from.x + (to.x - from.x) * fraction, from.y + (to.y - from.y) * fraction, follow);
        }
        bridgePoints.keySet()
            .retainAll(live);
    }

    private static int anchorKey(TopologyRelay relay) {
        return relay.attachedToNetwork() ? networkKey(relay.network()) : relayKey(relay.parent());
    }

    private static int anchorKey(TopologyDevice device) {
        return device.relay() == TopologyDevice.DIRECT ? networkKey(device.network()) : relayKey(device.relay());
    }

    private static int networkKey(int networkId) {
        return networkId;
    }

    private static int relayKey(int relayId) {
        return ~relayId;
    }

    private static void target(Point point, float x, float y, boolean immediate) {
        point.targetX = x;
        point.targetY = y;
        if (immediate || !point.placed) {
            point.x = x;
            point.y = y;
            point.placed = true;
        }
    }

    private static long pairKey(TopologyBridge bridge) {
        int low = Math.min(bridge.from(), bridge.to());
        int high = Math.max(bridge.from(), bridge.to());
        return ((long) low << 32) | (high & 0xFFFFFFFFL);
    }

    private static Point point(Map<Integer, Point> points, int id) {
        return points.computeIfAbsent(id, ignored -> new Point());
    }
}
