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
import com.science.gtnl.api.topology.TopologySnapshot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyLayout {

    private static final float MIN_RING_STEP = 34.0F;
    private static final float BRIDGE_FRACTION = 0.54F;
    private static final float LANE_SPACING = 7.0F;
    private static final int MAX_TRACE_HOPS = 64;
    private static final float DEVICE_ORBIT = 1.9F;

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
    private final Map<Integer, Point> bridgePoints = new HashMap<>();
    private final Map<Integer, Point> devicePoints = new HashMap<>();
    private final Map<Integer, TopologyBridge> parentBridge = new HashMap<>();
    private final Map<Integer, Integer> parentNetwork = new HashMap<>();
    private long builtRevision = -1;
    private int ringCount = 1;
    private float ringStep = MIN_RING_STEP;
    private float canvasRadius = 120.0F;

    public void update(TopologySnapshot snapshot, long revision, float easing, float availableRadius) {
        float radius = Math.max(60.0F, availableRadius);
        boolean resized = Math.abs(radius - canvasRadius) > 0.5F;
        if (resized) canvasRadius = radius;
        if (revision != builtRevision || resized) {
            builtRevision = revision;
            rebuild(snapshot);
        }
        for (Point point : networkPoints.values()) point.approach(easing);
        // Boundaries and devices hang off their network, so they follow the eased positions exactly.
        placeBridges(snapshot, true);
        placeDevices(snapshot, true);
    }

    public Point network(int id) {
        return networkPoints.get(id);
    }

    public Point bridge(int id) {
        return bridgePoints.get(id);
    }

    public Point device(int id) {
        return devicePoints.get(id);
    }

    public float outerRadius() {
        return ringStep * Math.max(1, ringCount - 1);
    }

    public int ringCount() {
        return ringCount;
    }

    public float ringRadius(int depth) {
        return depth * ringStep;
    }

    public TopologyBridge parentOf(int networkId) {
        return parentBridge.get(networkId);
    }

    public Trace trace(int networkId) {
        Set<Integer> networks = new HashSet<>();
        Set<Integer> bridges = new HashSet<>();
        int current = networkId;
        networks.add(current);
        for (int hop = 0; hop < MAX_TRACE_HOPS; hop++) {
            TopologyBridge bridge = parentBridge.get(current);
            if (bridge == null) break;
            bridges.add(bridge.id());
            current = bridge.from();
            if (!networks.add(current)) break;
        }
        return new Trace(networks, bridges);
    }

    private void rebuild(TopologySnapshot snapshot) {
        parentBridge.clear();
        parentNetwork.clear();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!bridge.resolved() || bridge.loop()) continue;
            TopologyNetwork from = snapshot.network(bridge.from());
            TopologyNetwork to = snapshot.network(bridge.to());
            if (from == null || to == null || to.depth() <= from.depth()) continue;
            if (parentBridge.putIfAbsent(bridge.to(), bridge) == null) parentNetwork.put(bridge.to(), bridge.from());
        }

        Map<Integer, List<TopologyNetwork>> byDepth = new HashMap<>();
        int maxDepth = 0;
        for (TopologyNetwork network : snapshot.networks()) {
            byDepth.computeIfAbsent(network.depth(), ignored -> new ArrayList<>())
                .add(network);
            maxDepth = Math.max(maxDepth, network.depth());
        }
        ringCount = maxDepth + 1;
        // Spread the rings across whatever room the canvas has, leaving the outer ring inside the viewport.
        ringStep = Math.max(MIN_RING_STEP, canvasRadius / Math.max(1, maxDepth));

        Set<Integer> live = new HashSet<>();
        for (TopologyNetwork network : snapshot.networks()) live.add(network.id());
        networkPoints.keySet()
            .retainAll(live);

        Map<Integer, Float> angles = new HashMap<>();
        for (int depth = 0; depth <= maxDepth; depth++) {
            List<TopologyNetwork> ring = byDepth.get(depth);
            if (ring == null) continue;
            if (depth == 0) {
                for (TopologyNetwork network : ring) {
                    angles.put(network.id(), 0.0F);
                    point(networkPoints, network.id()).retarget(0, 0, 0);
                }
                continue;
            }
            // Sorting on the parent angle first is what keeps siblings adjacent on the ring.
            ring.sort((left, right) -> {
                float a = angles.getOrDefault(parentNetwork.getOrDefault(left.id(), 0), 0.0F);
                float b = angles.getOrDefault(parentNetwork.getOrDefault(right.id(), 0), 0.0F);
                return a == b ? Integer.compare(left.id(), right.id()) : Float.compare(a, b);
            });
            float radius = ringRadius(depth);
            int count = ring.size();
            for (int index = 0; index < count; index++) {
                TopologyNetwork network = ring.get(index);
                float angle = count == 1 ? -(float) Math.PI / 2
                    : (float) (-Math.PI / 2 + Math.PI * 2 * index / (double) count);
                angles.put(network.id(), angle);
                point(networkPoints, network.id())
                    .retarget(MathHelper.cos(angle) * radius, MathHelper.sin(angle) * radius, angle);
            }
        }
        placeBridges(snapshot, false);
        placeDevices(snapshot, false);
    }

    private void placeDevices(TopologySnapshot snapshot, boolean follow) {
        Set<Integer> live = new HashSet<>();
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Integer> totals = new HashMap<>();
        for (TopologyDevice device : snapshot.devices()) totals.merge(device.network(), 1, Integer::sum);
        for (TopologyDevice device : snapshot.devices()) {
            live.add(device.id());
            Point owner = networkPoints.get(device.network());
            if (owner == null) continue;
            int total = Math.max(1, totals.getOrDefault(device.network(), 1));
            int index = counts.merge(device.network(), 1, Integer::sum) - 1;
            float orbit = nodeOrbit(device.network()) * DEVICE_ORBIT;
            float angle = owner.angle + (float) (Math.PI * 2 * index / total);
            target(
                point(devicePoints, device.id()),
                owner.x + MathHelper.cos(angle) * orbit,
                owner.y + MathHelper.sin(angle) * orbit,
                follow);
        }
        devicePoints.keySet()
            .retainAll(live);
    }

    private float nodeOrbit(int networkId) {
        return Math.max(14.0F, ringStep * 0.26F);
    }

    private void placeBridges(TopologySnapshot snapshot, boolean follow) {
        Set<Integer> live = new HashSet<>();
        Map<Long, Integer> lanes = new HashMap<>();
        for (TopologyBridge bridge : snapshot.bridges()) {
            live.add(bridge.id());
            Point from = networkPoints.get(bridge.from());
            if (from == null) continue;
            Point to = bridge.resolved() ? networkPoints.get(bridge.to()) : null;
            Point own = point(bridgePoints, bridge.id());
            // Parallel boundaries between the same pair get their own lane offset so none of them hide the others.
            int lane = lanes.merge(laneKey(bridge), 1, Integer::sum) - 1;
            float offset = (lane % 2 == 0 ? 1 : -1) * ((lane + 1) / 2) * LANE_SPACING;
            if (to == null || to == from) {
                // An unresolved or looping boundary orbits its owner so it stays visible and selectable.
                float angle = from.angle + 0.34F + offset * 0.02F;
                float radius = 54.0F + Math.abs(offset);
                target(own, from.x + MathHelper.cos(angle) * radius, from.y + MathHelper.sin(angle) * radius, follow);
                continue;
            }
            float dx = to.x - from.x, dy = to.y - from.y;
            float length = MathHelper.sqrt_float(dx * dx + dy * dy);
            float nx = length < 1.0E-4F ? 0 : -dy / length;
            float ny = length < 1.0E-4F ? 0 : dx / length;
            target(
                own,
                from.x + dx * BRIDGE_FRACTION + nx * offset,
                from.y + dy * BRIDGE_FRACTION + ny * offset,
                follow);
        }
        bridgePoints.keySet()
            .retainAll(live);
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

    private static long laneKey(TopologyBridge bridge) {
        int low = Math.min(bridge.from(), bridge.to());
        int high = Math.max(bridge.from(), bridge.to());
        return ((long) low << 32) | (high & 0xFFFFFFFFL);
    }

    private static Point point(Map<Integer, Point> points, int id) {
        return points.computeIfAbsent(id, ignored -> new Point());
    }

    public record Trace(Set<Integer> networks, Set<Integer> bridges) {

        public static final Trace NONE = new Trace(Set.of(), Set.of());

        public boolean isEmpty() {
            return networks.isEmpty() && bridges.isEmpty();
        }

        public Trace with(int bridgeId) {
            Set<Integer> extended = new HashSet<>(bridges);
            extended.add(bridgeId);
            return new Trace(networks, extended);
        }
    }
}
