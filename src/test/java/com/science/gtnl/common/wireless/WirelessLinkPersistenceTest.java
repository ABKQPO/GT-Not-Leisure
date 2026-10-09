package com.science.gtnl.common.wireless;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

public final class WirelessLinkPersistenceTest {

    private static final Address RED = new Address(-1, -300, 64, 20, ForgeDirection.UNKNOWN);
    private static final Address BLUE = new Address(7, 200, 80, -30, ForgeDirection.UNKNOWN);
    private static final Slot CABLE = new Slot(ForgeDirection.UNKNOWN, false);
    private static final Slot PART = new Slot(ForgeDirection.NORTH, false);
    private static final Slot EXTERNAL = new Slot(ForgeDirection.NORTH, true);

    private static final class Node {

        final List<Node> neighbours = new ArrayList<>();
        boolean live = true;
        boolean controller;
    }

    private static final PhysicalClusterTracker.Topology<Node> GRAPH = new PhysicalClusterTracker.Topology<>() {

        public boolean isLive(Node node) {
            return node.live;
        }

        public Iterable<Node> neighbours(Node node) {
            return node.neighbours;
        }

        public boolean blocksWireless(Node node) {
            return node.controller;
        }
    };

    public static void main(String[] args) throws Exception {
        nbtRoundTrip();
        pausedRoundTrip();
        namedRoundTrip();
        verifiedEntrancesRoundTrip();
        VerifiedEntranceRecoveryTest.run();
        restoredConflictAndUnlink();
        staggeredReloadAndSplit();
        restoreLimitIsAtomic();
        System.out.println(
            "WirelessLinkPersistenceTest: NBT, reload, conflict provenance, unlink and atomic restore passed.");
    }

    private static void nbtRoundTrip() throws Exception {
        NBTTagCompound tile = new NBTTagCompound();
        tile.setString("otherMod", "preserved");
        Map<Slot, Claim> claims = Map.of(
            CABLE,
            new Claim("cable", Set.of(RED)),
            PART,
            new Claim("part", Set.of(BLUE)),
            EXTERNAL,
            new Claim("part", Set.of(RED, BLUE)));
        check(WirelessLinkData.write(tile, claims), "A changed binding marks the tile dirty");
        check(!WirelessLinkData.write(tile, claims), "An unchanged tick does not mark the tile dirty");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(tile, bytes);
        NBTTagCompound reloaded = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray()));
        check(
            WirelessLinkData.read(reloaded)
                .equals(claims),
            "Distinct part nodes and cross-dimension sources survive disk NBT");
        Map<Slot, Claim> remaining = WirelessLinkData.read(reloaded);
        remaining.remove(PART);
        WirelessLinkData.write(reloaded, remaining);
        check(
            WirelessLinkData.read(reloaded)
                .size() == 2,
            "Removing a part preserves cable and external-node provenance");
        WirelessLinkData.write(reloaded, Map.of());
        check(!reloaded.hasKey(WirelessLinkData.KEY), "Unlink removes the saved record");
        check("preserved".equals(reloaded.getString("otherMod")), "Other mods' tile tags remain intact");
        check(
            WirelessLinkData.read(new NBTTagCompound())
                .isEmpty(),
            "A replacement tile has no coordinate-based inheritance");
        NBTTagCompound invalid = (NBTTagCompound) tile.copy();
        invalid.getCompoundTag(WirelessLinkData.KEY)
            .setInteger("version", 99);
        check(
            WirelessLinkData.read(invalid)
                .isEmpty(),
            "Unknown versions fail closed");
        check(!WirelessLinkData.write(invalid, Map.of()), "Unknown future data is preserved");
        invalid = (NBTTagCompound) tile.copy();
        var entry = invalid.getCompoundTag(WirelessLinkData.KEY)
            .getTagList("nodes", 10)
            .getCompoundTagAt(0);
        entry.setInteger("side", 99);
        check(
            WirelessLinkData.read(invalid)
                .size() == 2,
            "Malformed node side is rejected");
        NBTTagCompound single = new NBTTagCompound();
        WirelessLinkData.write(single, Map.of(CABLE, new Claim("cable", Set.of(RED))));
        single.getCompoundTag(WirelessLinkData.KEY)
            .getTagList("nodes", 10)
            .getCompoundTagAt(0)
            .getTagList("frequencies", 10)
            .getCompoundTagAt(0)
            .removeTag("dimension");
        check(
            WirelessLinkData.read(single)
                .isEmpty(),
            "Missing source fields never bind to default coordinates");
    }

    private static void namedRoundTrip() throws Exception {
        NBTTagCompound tile = new NBTTagCompound();
        Claim named = new Claim("cable", Set.of(RED, BLUE), Set.of(RED), Map.of(RED, "矿场", BLUE, "化工区"));
        WirelessLinkData.write(tile, Map.of(CABLE, named));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(tile, bytes);
        Claim restored = WirelessLinkData
            .read(CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray())))
            .get(CABLE);
        check(
            named.equals(restored),
            "Names and pause state survive compressed saves independently for each frequency");
        Claim remaining = new Claim(restored.kind(), Set.of(BLUE), restored.paused(), restored.names());
        check(
            remaining.names()
                .equals(Map.of(BLUE, "化工区")),
            "Unlinking one frequency removes only its name");
        check(
            new Claim("old", Set.of(RED)).names()
                .isEmpty(),
            "Legacy claims are unnamed");
        check(
            WirelessLinkData.cleanName("a\nb\t\u00a7c")
                .equals("abc"),
            "Control and formatting characters are stripped");
        check(
            WirelessLinkData.cleanName("矿".repeat(40))
                .length() == 32,
            "Names have a bounded length");
        WirelessLinkData.write(tile, Map.of(CABLE, new Claim("cable", Set.of(RED), Set.of(), Map.of())));
        check(
            WirelessLinkData.read(tile)
                .get(CABLE)
                .names()
                .isEmpty(),
            "Clearing a name does not resurrect saved labels");
    }

    private static void verifiedEntrancesRoundTrip() throws Exception {
        NBTTagCompound tile = new NBTTagCompound();
        var claims = Map.of(
            CABLE,
            new Claim("cable", Set.of(RED, BLUE), Set.of(RED), Map.of(RED, "矿场"), Set.of(RED)),
            PART,
            new Claim("part", Set.of(RED)),
            EXTERNAL,
            new Claim("part", Set.of(BLUE)));
        WirelessLinkData.write(tile, claims);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(tile, bytes);
        var restored = WirelessLinkData
            .read(CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray())));
        check(
            restored.equals(claims),
            "Verified positions survive disk NBT with independent pause/name/frequency data");
        check(
            restored.get(PART)
                .entrances()
                .isEmpty()
                && restored.get(EXTERNAL)
                    .entrances()
                    .isEmpty(),
            "A cable's verified entry must not spread into part or external node slots");
        Claim original = restored.get(CABLE);
        Claim unlinked = new Claim(
            original.kind(),
            Set.of(BLUE),
            original.paused(),
            original.names(),
            original.entrances());
        check(
            unlinked.entrances()
                .isEmpty(),
            "Unlinking red must remove its entry even when blue remains");
        check(!WirelessLinkData.write(tile, claims), "Unchanged verified data does not dirty tiles repeatedly");
        check(
            new Claim("legacy", Set.of(RED)).entrances()
                .isEmpty(),
            "Legacy saves safely begin without hints");
        restored.remove(CABLE);
        WirelessLinkData.write(tile, restored);
        check(
            WirelessLinkData.read(tile)
                .values()
                .stream()
                .allMatch(
                    claim -> claim.entrances()
                        .isEmpty()),
            "Removing the saved entry does not transfer its hint to another node in the same host");
    }

    private static void pausedRoundTrip() throws Exception {
        NBTTagCompound tile = new NBTTagCompound();
        Claim paused = new Claim("cable", Set.of(RED, BLUE), Set.of(RED));
        WirelessLinkData.write(tile, Map.of(CABLE, paused));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(tile, bytes);
        NBTTagCompound loaded = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray()));
        check(
            WirelessLinkData.read(loaded)
                .get(CABLE)
                .equals(paused),
            "Pause survives disk NBT without removing either frequency");
        check(
            !WirelessLinkData.read(loaded)
                .get(CABLE)
                .paused()
                .contains(BLUE),
            "Pausing one frequency does not pause another conflict claim");
        Claim resumed = new Claim("cable", paused.frequencies());
        check(WirelessLinkData.write(loaded, Map.of(CABLE, resumed)), "Resuming marks saved data dirty");
        check(
            WirelessLinkData.read(loaded)
                .get(CABLE)
                .paused()
                .isEmpty(),
            "Resume removes the saved pause flag");
        check(
            WirelessLinkData.read(loaded)
                .get(CABLE)
                .frequencies()
                .equals(paused.frequencies()),
            "Resume retains frequency provenance");
        check(
            new Claim("cable", Set.of(BLUE), Set.of(RED)).paused()
                .isEmpty(),
            "Removing a frequency cannot leave an orphan pause");
        check(
            new Claim("old", Set.of(RED)).paused()
                .isEmpty(),
            "Old records default to enabled");
        WirelessLinkData.write(loaded, Map.of(CABLE, paused));
        WirelessLinkData.write(loaded, Map.of());
        check(!loaded.hasKey(WirelessLinkData.KEY), "Unlink clears the pause along with provenance");
    }

    private static void restoredConflictAndUnlink() {
        Node a = new Node(), b = new Node(), neutral = new Node();
        join(a, neutral);
        join(neutral, b);
        var tracker = new PhysicalClusterTracker<Node, Address>(100);
        Map<Node, Set<Address>> saved = Map.of(a, Set.of(RED), b, Set.of(BLUE));
        Set<Node> imported = Collections.newSetFromMap(new IdentityHashMap<>());
        tracker.refresh(GRAPH, List.of(a), node -> saved.getOrDefault(node, Set.of()));
        check(
            tracker.clusterOf(a)
                .conflicted(),
            "A saved blue node discovered through a red seed must suspend both frequencies");
        check(
            tracker.claimsOf(a)
                .equals(Set.of(RED))
                && tracker.claimsOf(b)
                    .equals(Set.of(BLUE)),
            "Reload preserves each side's provenance");
        check(
            tracker.claimsOf(neutral)
                .isEmpty(),
            "Conflict does not spread either saved frequency into neutral nodes");
        imported.addAll(
            tracker.clusterOf(a)
                .nodes());
        tracker.unlink(tracker.clusterOf(a), RED);
        tracker.refresh(
            GRAPH,
            List.of(a),
            node -> imported.contains(node) ? Set.of() : saved.getOrDefault(node, Set.of()));
        check(
            tracker.claimsOf(a)
                .equals(Set.of(BLUE)),
            "Unlink cannot resurrect stale red NBT before synchronization");
        tracker.unlink(tracker.clusterOf(b), BLUE);
        tracker.refresh(GRAPH, List.of(a), node -> Set.of());
        check(
            tracker.claimsOf(a)
                .isEmpty()
                && tracker.claimsOf(b)
                    .isEmpty(),
            "Unlink clears the whole loaded component");
    }

    private static void staggeredReloadAndSplit() {
        Node a = new Node(), b = new Node(), c = new Node();
        join(a, b);
        join(b, c);
        b.live = false;
        c.live = false;
        var tracker = new PhysicalClusterTracker<Node, Address>(100);
        tracker.refresh(GRAPH, List.of(a), node -> Set.of(RED));
        check(
            tracker.claimsOf(a)
                .equals(Set.of(RED)),
            "A loaded child restores before the other chunks");
        b.live = true;
        c.live = true;
        tracker.refresh(GRAPH, List.of(b, c), node -> Set.of(RED));
        check(
            tracker.clusters()
                .size() == 1,
            "Same-frequency reload merges into one component");
        b.live = false;
        tracker.refresh(GRAPH, List.of());
        check(
            tracker.clusters()
                .size() == 2 && tracker.claimsOf(a)
                    .equals(Set.of(RED))
                && tracker.claimsOf(c)
                    .equals(Set.of(RED)),
            "Both children retain restored provenance after a split");
        tracker.clear();
        tracker.refresh(GRAPH, List.of(a, c), node -> Set.of(RED));
        check(
            tracker.clusters()
                .size() == 2,
            "Restart recovers both disconnected children independently");
        Node controller = new Node();
        controller.controller = true;
        join(a, controller);
        tracker.clear();
        tracker.refresh(GRAPH, List.of(a), node -> node == a ? Set.of(RED) : Set.of());
        check(
            tracker.clusterOf(a)
                .blocked()
                && tracker.claimsOf(controller)
                    .isEmpty(),
            "Restoration never propagates a claim into a wired controller base");
    }

    private static void restoreLimitIsAtomic() {
        Node a = new Node(), b = new Node();
        join(a, b);
        var tracker = new PhysicalClusterTracker<Node, Address>(1);
        boolean limited = false;
        try {
            tracker.refresh(GRAPH, List.of(a), node -> Set.of(RED));
        } catch (PhysicalClusterTracker.ScanLimitException expected) {
            limited = true;
        }
        check(
            limited && tracker.clusters()
                .isEmpty(),
            "A partial saved restore is never committed");
        b.live = false;
        tracker.refresh(GRAPH, List.of(a), node -> Set.of(RED));
        check(
            tracker.claimsOf(a)
                .equals(Set.of(RED)),
            "Saved claims remain available after a failed bounded scan");
    }

    private static void join(Node a, Node b) {
        a.neighbours.add(b);
        b.neighbours.add(a);
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
