package com.science.gtnl.common.wireless;

import static com.gtnltest.TestSupport.check;
import static com.science.gtnl.common.wireless.WirelessCopiedLinksTest.allocate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessCopiedLinksTest.TestHost;
import com.science.gtnl.common.wireless.WirelessCopiedLinksTest.TestWorld;
import com.science.gtnl.common.wireless.WirelessFrequencies.Entry;
import com.science.gtnl.common.wireless.WirelessFrequencies.Structure;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

@Tag("wireless")
public class WirelessFrequencyTest {

    private static final Address CONTROLLER = new Address(0, 10, 64, -20, ForgeDirection.UNKNOWN);
    private static final UUID OWNER = new UUID(7, 7), CREATOR = new UUID(8, 8);
    private static final WirelessFrequency ID = WirelessFrequency.of(new UUID(1, 2));
    private static final WirelessFrequency LEGACY = WirelessFrequency.legacy(CONTROLLER);
    private static final Slot CENTRE = new Slot(ForgeDirection.UNKNOWN, false);

    @TestFactory
    Stream<DynamicTest> scenarios() {
        return Stream.of(
            DynamicTest.dynamicTest("frequencyValues", WirelessFrequencyTest::frequencyValues),
            DynamicTest.dynamicTest("legacyLinks", WirelessFrequencyTest::legacyLinks),
            DynamicTest.dynamicTest("currentLinks", WirelessFrequencyTest::currentLinks),
            DynamicTest.dynamicTest("cards", WirelessFrequencyTest::cards),
            DynamicTest.dynamicTest("controllerEntries", WirelessFrequencyTest::controllerEntries),
            DynamicTest.dynamicTest("tileDataCopies", WirelessFrequencyTest::tileDataCopies),
            DynamicTest.dynamicTest("registryRules", WirelessFrequencyTest::registryRules),
            DynamicTest.dynamicTest("mergedFrequencies", WirelessFrequencyTest::mergedFrequencies),
            DynamicTest.dynamicTest("structureCache", WirelessFrequencyTest::structureCache));
    }

    private static void frequencyValues() {
        check(
            WirelessFrequency.legacy(new Address(0, 10, 64, -20, ForgeDirection.NORTH))
                .equals(LEGACY),
            "a legacy frequency ignores the side of the old controller position");
        boolean rejected = false;
        try {
            new WirelessFrequency(ID.id(), CONTROLLER);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "a frequency cannot be both an id and a legacy position");
    }

    private static void legacyLinks() {
        NBTTagCompound source = new NBTTagCompound();
        source.setInteger("dimension", 0);
        source.setInteger("x", 10);
        source.setInteger("y", 64);
        source.setInteger("z", -20);
        source.setInteger("side", ForgeDirection.UNKNOWN.ordinal());
        source.setBoolean("paused", true);
        source.setBoolean("entrance", true);
        NBTTagList frequencies = new NBTTagList();
        frequencies.appendTag(source);
        NBTTagCompound node = new NBTTagCompound();
        node.setInteger("side", ForgeDirection.UNKNOWN.ordinal());
        node.setBoolean("external", false);
        node.setString("kind", "cable");
        node.setTag("frequencies", frequencies);
        NBTTagList nodes = new NBTTagList();
        nodes.appendTag(node);
        NBTTagCompound payload = new NBTTagCompound();
        payload.setInteger("version", 1);
        payload.setTag("nodes", nodes);
        NBTTagCompound tileData = new NBTTagCompound();
        tileData.setTag(WirelessLinkData.KEY, payload);

        Claim legacy = WirelessLinkData.read(tileData)
            .get(CENTRE);
        check(
            legacy != null && legacy.frequencies()
                .equals(Set.of(LEGACY))
                && legacy.paused()
                    .equals(Set.of(LEGACY))
                && legacy.entrances()
                    .equals(Set.of(LEGACY)),
            "version 1 links read as legacy controller positions with their pause and entrance flags");
        Map<Slot, Claim> upgraded = Map.of(CENTRE, new Claim("cable", Set.of(ID), Set.of(ID), Set.of()));
        check(
            WirelessLinkData.write(tileData, upgraded) && tileData.getCompoundTag(WirelessLinkData.KEY)
                .getInteger("version") == 2,
            "migrated links overwrite a version 1 payload");
        check(
            WirelessLinkData.read(tileData)
                .equals(upgraded),
            "migrated links read back unchanged");
    }

    private static void currentLinks() {
        NBTTagCompound tileData = new NBTTagCompound();
        Map<Slot, Claim> claims = Map.of(
            CENTRE,
            new Claim("cable", Set.of(ID, LEGACY), Set.of(LEGACY), Set.of(ID)),
            new Slot(ForgeDirection.NORTH, true),
            new Claim("part", Set.of(ID), Set.of(), Set.of()));
        check(WirelessLinkData.write(tileData, claims), "links with ids and unresolved legacy labels are written");
        check(
            WirelessLinkData.read(tileData)
                .equals(claims),
            "unresolved legacy labels survive next to ids until their controller loads");
        tileData.getCompoundTag(WirelessLinkData.KEY)
            .setInteger("version", 3);
        check(!WirelessLinkData.write(tileData, Map.of()), "links written by a newer version are never overwritten");
    }

    private static void cards() {
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setInteger("version", 1);
        legacy.setInteger("dimension", 0);
        legacy.setInteger("x", 10);
        legacy.setInteger("y", 64);
        legacy.setInteger("z", -20);
        legacy.setString("owner", OWNER.toString());
        legacy.setString("ownerName", "owner");
        NBTTagCompound item = new NBTTagCompound();
        item.setTag("GTNLWirelessCard", legacy);
        WirelessCardBinding read = WirelessCardBinding.read(item);
        check(
            new WirelessCardBinding(LEGACY, CONTROLLER, null, OWNER, "owner").equals(read),
            "a version 1 card reads as a legacy frequency at its old controller: " + read);
        NBTTagCompound rewritten = new NBTTagCompound();
        read.write(rewritten);
        check(read.equals(WirelessCardBinding.read(rewritten)), "an unmigrated card can still be written as it was");

        WirelessCardBinding current = new WirelessCardBinding(ID, CONTROLLER, CREATOR, OWNER, "owner");
        NBTTagCompound tag = new NBTTagCompound();
        current.write(tag);
        check(current.equals(WirelessCardBinding.read(tag)), "a version 2 card keeps its frequency id and creator");
        tag.getCompoundTag("GTNLWirelessCard")
            .setString("frequency", "not a uuid");
        check(WirelessCardBinding.read(tag) == null, "a corrupt frequency id is no binding");
    }

    private static void controllerEntries() {
        List<Entry> entries = List.of(new Entry(ID.id(), 0), new Entry(new UUID(3, 4), 2));
        NBTTagCompound tileData = new NBTTagCompound();
        WirelessFrequencies.write(tileData, entries);
        check(
            WirelessFrequencies.read(tileData)
                .equals(entries),
            "controller frequency entries keep their ids and adoption versions");
        WirelessFrequencies.write(tileData, List.of());
        check(!tileData.hasKey(WirelessFrequencies.KEY), "a controller without entries stores nothing");
        check(
            new UUID(1, 2).equals(WirelessFrequencies.canonical(List.of(new UUID(3, 4), new UUID(1, 2)))),
            "a structure carrying merged frequencies answers to the smallest id");
    }

    private static void tileDataCopies() {
        NBTTagCompound saved = new NBTTagCompound();
        WirelessFrequencies.write(saved, List.of(new Entry(ID.id(), 0)));
        WirelessLinkData.write(saved, Map.of(CENTRE, new Claim("cable", Set.of(ID), Set.of(), Set.of())));
        saved.setString("id", "AEController");
        NBTTagCompound data = IWirelessTileData.load(saved);
        NBTTagCompound moved = new NBTTagCompound();
        IWirelessTileData.save(data, moved);
        check(
            moved.hasKey(IWirelessTileData.KEY, 10) && moved.hasKey(IWirelessTileData.FREQUENCY_KEY, 10)
                && !moved.hasKey("id"),
            "a saved or moved tile carries both its links and its frequency id, nothing else");
        check(
            WirelessFrequencies.read(moved)
                .equals(WirelessFrequencies.read(saved)),
            "a moved controller keeps the same frequency entries");
        IWirelessTileData.save(null, moved);
        check(
            !moved.hasKey(IWirelessTileData.KEY) && !moved.hasKey(IWirelessTileData.FREQUENCY_KEY),
            "a tile without wireless data saves no stale keys");
    }

    private static void registryRules() {
        WirelessFrequencyWorldSavedData registry = new WirelessFrequencyWorldSavedData();
        UUID created = new UUID(5, 5), unseen = new UUID(6, 6);
        registry.create(created, CREATOR);
        check(
            CREATOR.equals(
                registry.frequency(created)
                    .creator())
                && registry.accept(created, 0),
            "a new frequency records its creator and accepts its first controller");
        long adopted = registry.adopt(created);
        check(
            adopted == 1 && !registry.accept(created, 0) && registry.accept(created, adopted),
            "after adoption the old controller is refused even while the new one is unloaded");
        check(
            registry.accept(unseen, 4) && registry.frequency(unseen)
                .creator() == null && !registry.accept(unseen, 3),
            "an unrecorded id is registered as first seen, with no creator, and older copies are refused");
        check(
            WirelessFrequencies
                .current(List.of(new Entry(created, 0), new Entry(unseen, 4), new Entry(created, adopted)), registry)
                .equals(List.of(new Entry(unseen, 4), new Entry(created, adopted))),
            "superseded controller copies are filtered out before any block can inherit them");
        check(!registry.migrated(created), "a frequency bound after the upgrade is not a migrated one");
        registry.mapLegacy(CONTROLLER, created);
        check(
            registry.migrated(created) && !registry.migrated(unseen),
            "only ids that old controller positions map to count as migrated");
        registry.setCreator(unseen, OWNER);
        NBTTagCompound tag = new NBTTagCompound();
        registry.writeToNBT(tag);
        WirelessFrequencyWorldSavedData reloaded = new WirelessFrequencyWorldSavedData();
        reloaded.readFromNBT(tag);
        check(
            reloaded.frequency(created)
                .equals(registry.frequency(created))
                && OWNER.equals(
                    reloaded.frequency(unseen)
                        .creator())
                && created.equals(reloaded.legacy(CONTROLLER)),
            "creators, adoption versions and old controller positions survive a restart");
        check(!reloaded.accept(created, 0), "a restart does not let the old controller back in");
        check(reloaded.migrated(created), "a restart keeps which ids came from migration");
    }

    private static void mergedFrequencies() {
        Map<String, List<String>> edges = Map.of("a", List.of("b"), "b", List.of("a"));
        PhysicalClusterTracker.Topology<String> topology = new PhysicalClusterTracker.Topology<>() {

            @Override
            public boolean isLive(String node) {
                return true;
            }

            @Override
            public Iterable<String> neighbours(String node) {
                return edges.getOrDefault(node, List.of());
            }

            @Override
            public boolean blocksWireless(String node) {
                return false;
            }
        };
        UnaryOperator<String> merged = label -> label.equals("F2") ? "F1" : label;
        UnaryOperator<String> separate = UnaryOperator.identity();
        PhysicalClusterTracker<String, String> tracker = new PhysicalClusterTracker<>(16);
        tracker.refresh(topology, List.of("a"), node -> Set.of(), merged);
        Cluster<String, String> cluster = tracker.clusterOf("a");
        tracker.link(cluster, "F2", merged);
        tracker.commitClaims(merged);
        check(
            tracker.claimsOf("b")
                .equals(Set.of("F2"))
                && tracker.clusterOf("b")
                    .frequencies()
                    .equals(Set.of("F1")),
            "a card linked while two controller groups are merged keeps its own id but joins the shared network");
        tracker.commitClaims(separate);
        check(
            tracker.clusterOf("a")
                .frequencies()
                .equals(Set.of("F2")),
            "splitting the controllers sends the devices back to the card's own frequency");
        tracker.commitClaims(merged);
        tracker.unlink(tracker.clusterOf("a"), "F1", merged);
        check(
            tracker.claimsOf("a")
                .isEmpty(),
            "disconnecting with the other card of the merged network removes the F2 labels");
        tracker.commitClaims(merged);
        tracker.link(tracker.clusterOf("a"), "F2", merged);
        tracker.commitClaims(merged);
        boolean accepted = true;
        try {
            tracker.link(tracker.clusterOf("a"), "F1", merged);
        } catch (IllegalArgumentException refused) {
            accepted = false;
        }
        check(accepted, "the other card of the merged network is not told the cluster belongs elsewhere");
    }

    private static void structureCache() throws ReflectiveOperationException {
        TestWorld world = allocate(TestWorld.class);
        world.tiles = new HashMap<>();
        world.loaded = true;
        Field provider = World.class.getDeclaredField("provider");
        provider.setAccessible(true);
        provider.set(world, new WorldProviderSurface());
        // AE's controller class needs Mekanism's API to load, so a lone cable host stands in for the structure.
        TestHost block = allocate(TestHost.class);
        block.data = new NBTTagCompound();
        WirelessFrequencies.write(block.data, List.of(new Entry(ID.id(), 0)));
        block.setWorldObj(world);
        block.yCoord = 64;
        world.tiles.put(new Address(0, 0, 64, 0, ForgeDirection.UNKNOWN), block);
        WirelessFrequencies.clear();
        Structure resolved = WirelessFrequencies.structure(block);
        WirelessFrequencies.tick();
        check(
            WirelessFrequencies.structure(block) == resolved,
            "a frequency structure is walked once and reused on later ticks while nothing changes");
        WirelessFrequencies.register(block);
        Structure registered = WirelessFrequencies.structure(block);
        check(registered != resolved, "a block carrying a frequency id that validates drops the cached structures");
        WirelessFrequencyWorldSavedData registry = new WirelessFrequencyWorldSavedData();
        registry.create(ID.id(), CREATOR);
        WirelessFrequencies.current(List.of(new Entry(ID.id(), 0)), registry);
        check(
            WirelessFrequencies.structure(block) == registered,
            "reading a copy the registry already knows keeps the cached structures");
        WirelessFrequencies.current(List.of(new Entry(ID.id(), 1)), registry);
        Structure moved = WirelessFrequencies.structure(block);
        check(moved != registered, "a newer copy that moves the registry on drops the cached structures");
        for (int i = 0; i < 100; i++) WirelessFrequencies.tick();
        check(
            WirelessFrequencies.structure(block) != moved,
            "cached frequency structures are walked again at least every 100 ticks");
        WirelessFrequencies.clear();
    }
}
