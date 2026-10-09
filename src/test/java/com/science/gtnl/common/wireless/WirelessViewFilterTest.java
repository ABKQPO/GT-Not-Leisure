package com.science.gtnl.common.wireless;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.common.packet.WirelessCardGuiPacket;

import appeng.api.networking.IGridNode;
import io.netty.buffer.Unpooled;

public final class WirelessViewFilterTest {

    public static void main(String[] args) {
        packetRoundTrip();
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
        NBTTagCompound display = new NBTTagCompound();
        display.setLong("session", 47);
        display.setLong("revision", 9);
        var wire = Unpooled.buffer();
        var encoded = Unpooled.buffer();
        var invalid = Unpooled.buffer();
        try {
            new WirelessCardGuiPacket(3, display, 6, "矿场", "-1", 2).toBytes(wire);
            var decoded = new WirelessCardGuiPacket();
            decoded.fromBytes(wire.duplicate());
            check(!decoded.isInvalid(), "A valid Unicode payload must be accepted");
            decoded.toBytes(encoded);
            check(wire.equals(encoded), "Unicode filter payload, session and revision must survive packet round trips");
            invalid.writeZero(24);
            invalid.writeShort(257);
            var malformed = new WirelessCardGuiPacket();
            malformed.fromBytes(invalid);
            check(malformed.isInvalid(), "The packet base must mark oversized text for rejection");
        } finally {
            wire.release();
            encoded.release();
            invalid.release();
        }
    }

    private static void layoutBounds() {
        var small = WirelessCardLayout.forScreen(320, 240);
        check(
            small.width() == 320 && small.height() == 232 && small.rows() == 5,
            "Minimum Minecraft GUI size retains five rows and the footer");
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
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
