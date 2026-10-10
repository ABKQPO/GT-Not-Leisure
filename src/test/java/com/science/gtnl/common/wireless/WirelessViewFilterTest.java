package com.science.gtnl.common.wireless;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.packet.ToggleWirelessCardPacket;
import com.science.gtnl.common.packet.WirelessVisualisationPacket;

import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.Unpooled;

public final class WirelessViewFilterTest {

    public static void main(String[] args) {
        packetRoundTrip();
        visualisationPackets();
        layoutBounds();
        topologyRevision();
        var filter = WirelessViewFilter.parse("矿场", "-1", 1);
        check(filter.matches("矿场东", -1, "D-1 10, 64, 2", true, "paused"), "Combined name, dimension and state");
        check(!filter.matches("矿场东", 0, "", true, "paused"), "Dimension must match");
        check(!filter.matches("矿场东", -1, "", false, "active"), "Paused filter excludes active links");
        check(
            WirelessViewFilter.parse("CHEM", "", 0)
                .matches("Chemical", 7, "", false, "active"),
            "Case-insensitive name search");
        check(
            WirelessViewFilter.parse("-300", "", 0)
                .matches("", 0, "D0 -300, 64, 5", false, "waiting"),
            "Coordinate search");
        check(
            WirelessViewFilter.parse("", "", 2)
                .matches("", 0, "", true, "conflict"),
            "Paused conflicts remain searchable");
        for (String dimension : new String[] { "oops", "2147483648" }) {
            try {
                WirelessViewFilter.parse("", dimension, 0);
                throw new AssertionError("Invalid dimension accepted");
            } catch (IllegalArgumentException expected) {}
        }
        System.out
            .println("WirelessViewFilterTest: combined filters, case, coordinates and invalid dimensions passed.");
    }

    private static void topologyRevision() {
        IGridNode anchor = node(), removed = node(), replacement = node();
        var old = row(anchor, Set.of(anchor, removed));
        var unchanged = row(anchor, Set.of(anchor, removed));
        var replaced = row(anchor, Set.of(anchor, replacement));
        check(
            WirelessCardContainer.sameTargets(List.of(old), List.of(unchanged)),
            "A rebuilt snapshot of the same physical cluster retains its revision");
        check(
            !WirelessCardContainer.sameTargets(List.of(old), List.of(replaced)),
            "Replacing a non-anchor device invalidates old clicks even with identical device counts");
        check(
            !WirelessCardContainer.sameTargets(List.of(old), List.of(row(removed, old.members()))),
            "Changing the displayed anchor invalidates old clicks");
    }

    private static WirelessClusterManager.LinkView row(IGridNode anchor, Set<IGridNode> members) {
        return new WirelessClusterManager.LinkView(
            null,
            anchor,
            Set.of(),
            false,
            false,
            "active",
            1,
            null,
            false,
            -1,
            members);
    }

    private static IGridNode node() {
        return (IGridNode) Proxy.newProxyInstance(
            IGridNode.class.getClassLoader(),
            new Class<?>[] { IGridNode.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "TestNode";
            default -> null;
            });
    }

    private static void packetRoundTrip() {
        var wire = Unpooled.buffer();
        var encoded = Unpooled.buffer();
        var invalid = Unpooled.buffer();
        try {
            new WirelessCardContainer.Action(47, 9, 6, "矿场", "-1", 2).write(wire);
            var decoded = WirelessCardContainer.Action.read(wire.duplicate());
            check(
                decoded.text()
                    .equals("矿场"),
                "A valid Unicode payload must be accepted");
            decoded.write(encoded);
            check(wire.equals(encoded), "Unicode filter payload, session and revision must survive packet round trips");
            for (int length = 0; length < wire.readableBytes(); length++) {
                try {
                    WirelessCardContainer.Action.read(wire.slice(0, length));
                    throw new AssertionError("Truncated MUI action accepted at byte " + length);
                } catch (IllegalArgumentException | IndexOutOfBoundsException expected) {}
            }
            encoded.clear();
            String maximumText = "频".repeat(85) + "x";
            new WirelessCardContainer.Action(47, 9, 5, maximumText, "", 0).write(encoded);
            check(
                WirelessCardContainer.Action.read(encoded)
                    .text()
                    .equals(maximumText),
                "The 256-byte UTF-8 boundary must remain usable");
            encoded.clear();
            try {
                new WirelessCardContainer.Action(47, 9, 5, maximumText + "x", "", 0).write(encoded);
                throw new AssertionError("Oversized outgoing MUI action accepted");
            } catch (IllegalArgumentException expected) {}
            invalid.writeZero(20);
            invalid.writeShort(257);
            try {
                WirelessCardContainer.Action.read(invalid);
                throw new AssertionError("Oversized MUI action text accepted");
            } catch (IllegalArgumentException expected) {}
        } finally {
            wire.release();
            encoded.release();
            invalid.release();
        }
    }

    private static void visualisationPackets() {
        var wire = Unpooled.buffer();
        var encoded = Unpooled.buffer();
        try {
            for (boolean visualisation : new boolean[] { false, true }) {
                wire.clear();
                encoded.clear();
                new ToggleWirelessCardPacket(false, 0, visualisation).toBytes(wire);
                var decoded = new ToggleWirelessCardPacket();
                decoded.fromBytes(wire.duplicate());
                check(!decoded.isInvalid(), "Both auto-connect and visualisation requests are accepted");
                decoded.toBytes(encoded);
                check(wire.equals(encoded), "Hotkey actions must remain distinguishable after serialization");
            }
            NBTTagCompound tag = new NBTTagCompound();
            new WirelessCardBinding(
                new WirelessChannelPrototype.Address(-1, 12, 64, -30, ForgeDirection.UNKNOWN),
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "Owner").write(tag);
            for (int length : new int[] { 0, 8 }) {
                wire.clear();
                encoded.clear();
                ByteBufUtils.writeTag(wire, tag);
                wire.writeInt(7); // The viewer may be in another dimension than the source controller.
                wire.writeInt(length);
                wire.writeZero(length); // Empty view or a native snapshot with zero nodes and links.
                var decoded = new WirelessVisualisationPacket();
                decoded.fromBytes(wire.duplicate());
                decoded.toBytes(encoded);
                check(wire.equals(encoded), "Source, owner, viewer dimension and native data must survive transport");
            }
            for (int length : new int[] { -1, 8, WirelessCardVisualisation.MAX_PACKET_BYTES + 1 }) {
                wire.clear();
                ByteBufUtils.writeTag(wire, tag);
                wire.writeInt(0);
                wire.writeInt(length);
                try {
                    new WirelessVisualisationPacket().fromBytes(wire);
                    throw new AssertionError("Invalid or truncated visualisation payload accepted");
                } catch (IllegalArgumentException expected) {}
            }
        } finally {
            wire.release();
            encoded.release();
        }
    }

    private static void layoutBounds() {
        var small = WirelessCardLayout.forScreen(320, 240);
        check(
            small.width() == 320 && small.height() == 232 && small.rows() == 5,
            "Moving the visualisation control to the footer restores five rows at minimum GUI size");
        var medium = WirelessCardLayout.forScreen(359, 269);
        check(
            medium.width() == 347 && medium.rows() == 6 && medium.height() == 256,
            "A little more screen space should add a sixth row");
        var large = WirelessCardLayout.forScreen(960, 540);
        check(
            large.width() == 440 && large.height() == 328 && large.rows() == 9,
            "Large displays use the bounded expanded layout");
        for (int height = 240; height <= 540; height++) {
            var layout = WirelessCardLayout.forScreen(640, height);
            check(
                layout.height() <= height - 8 && 82 + layout.rows() * 24 < layout.height() - 27,
                "Every visible row must fit above the footer at each screen size");
        }
        check(
            !WirelessCardLayout.validRows(4) && !WirelessCardLayout.validRows(10),
            "Client row requests must not overlap reserved action IDs or exceed bounds");
        for (int width = 320; width <= 960; width++) {
            var layout = WirelessCardLayout.forScreen(width, 540);
            check(
                layout.visualisationWidth() >= 66 && layout.visualisationWidth() <= 120,
                "Mode button width stays bounded at every GUI scale");
            check(
                layout.footerInfoX() >= 98 && layout.visualisationX() - layout.footerInfoX() - 4 >= 72
                    && layout.visualisationX() + layout.visualisationWidth() + 4 == layout.width() - 76,
                "Footer count clears pagination and mode button, which sits four pixels left of Refresh");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
