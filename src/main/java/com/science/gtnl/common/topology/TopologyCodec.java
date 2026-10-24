package com.science.gtnl.common.topology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyLimits;
import com.science.gtnl.api.topology.TopologyLinkKind;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologyRelay;
import com.science.gtnl.api.topology.TopologySnapshot;

import appeng.api.networking.pathing.ControllerState;

public class TopologyCodec {

    private static final int MAX_NETWORKS = 4096;
    private static final int MAX_BRIDGES = 16384;
    private static final int MAX_DEVICES = 65536;
    private static final int MAX_RELAYS = 4096;
    private static final ControllerState[] CONTROLLER_STATES = ControllerState.values();

    private TopologyCodec() {}

    public static void write(PacketBuffer buffer, TopologySnapshot snapshot) {
        buffer.writeLong(snapshot.revision());
        buffer.writeByte((snapshot.bound() ? 1 : 0) | (snapshot.available() ? 2 : 0) | (snapshot.truncated() ? 4 : 0));

        List<TopologyNetwork> networks = snapshot.networks();
        GridCoord anchor = networks.isEmpty() ? null
            : networks.get(0)
                .pivot();
        buffer.writeVarIntToBuffer(networks.size());
        for (TopologyNetwork network : networks) {
            buffer.writeVarIntToBuffer(network.id());
            buffer.writeVarIntToBuffer(network.depth());
            buffer.writeByte(
                network.controller()
                    .ordinal());
            buffer.writeByte((network.booting() ? 1 : 0) | (network.powered() ? 2 : 0));
            buffer.writeVarIntToBuffer(network.nodes());
            buffer.writeVarIntToBuffer(network.devices());
            buffer.writeVarIntToBuffer(network.online());
            buffer.writeVarIntToBuffer(network.starving());
            buffer.writeFloat((float) network.storedPower());
            buffer.writeFloat((float) network.maxPower());
            buffer.writeFloat((float) network.idleDrain());
            buffer.writeVarIntToBuffer(network.craftingCpu());
            writeCoord(buffer, network.pivot(), anchor);
            writeIcon(buffer, network.icon());
        }

        List<TopologyRelay> relays = snapshot.relays();
        buffer.writeVarIntToBuffer(relays.size());
        for (TopologyRelay relay : relays) {
            buffer.writeVarIntToBuffer(relay.network());
            buffer.writeVarIntToBuffer(relay.parent() + 1);
            buffer.writeByte(
                relay.kind()
                    .ordinal());
            buffer.writeBoolean(relay.active());
            writeCoord(buffer, relay.position(), anchor);
            writeIcon(buffer, relay.icon());
            writeIcon(buffer, relay.dimensionIcon());
        }

        List<TopologyDevice> devices = snapshot.devices();
        buffer.writeVarIntToBuffer(devices.size());
        for (TopologyDevice device : devices) {
            buffer.writeVarIntToBuffer(device.network());
            buffer.writeVarIntToBuffer(device.relay() + 1);
            buffer.writeByte((device.active() ? 1 : 0) | (device.channelOk() ? 2 : 0));
            writeCoord(buffer, device.position(), anchor);
            writeIcon(buffer, device.icon());
        }

        List<TopologyBridge> bridges = snapshot.bridges();
        buffer.writeVarIntToBuffer(bridges.size());
        for (TopologyBridge bridge : bridges) {
            buffer.writeByte(
                bridge.kind()
                    .ordinal());
            buffer.writeVarIntToBuffer(bridge.from());
            buffer.writeVarIntToBuffer(bridge.to() + 1);
            buffer.writeBoolean(bridge.active());
            writeCoord(buffer, bridge.origin(), anchor);
            writeCoord(buffer, bridge.farEnd(), anchor);
            writeIcon(buffer, bridge.icon());
            writeIcon(buffer, bridge.dimensionIcon());
        }
    }

    public static TopologySnapshot read(PacketBuffer buffer) throws IOException {
        long revision = buffer.readLong();
        int flags = buffer.readUnsignedByte();

        int networkCount = buffer.readVarIntFromBuffer();
        if (networkCount < 0 || networkCount > MAX_NETWORKS) throw new IOException("Bad network count");
        List<TopologyNetwork> networks = new ArrayList<>(networkCount);
        GridCoord anchor = null;
        for (int index = 0; index < networkCount; index++) {
            int id = buffer.readVarIntFromBuffer();
            int depth = buffer.readVarIntFromBuffer();
            int controller = buffer.readUnsignedByte();
            int state = buffer.readUnsignedByte();
            int nodes = buffer.readVarIntFromBuffer();
            int devices = buffer.readVarIntFromBuffer();
            int online = buffer.readVarIntFromBuffer();
            int starving = buffer.readVarIntFromBuffer();
            float stored = buffer.readFloat();
            float max = buffer.readFloat();
            float idle = buffer.readFloat();
            int cpus = buffer.readVarIntFromBuffer();
            GridCoord pivot = readCoord(buffer, anchor);
            ItemStack icon = readIcon(buffer);
            if (id < 0 || depth < 0
                || nodes < 0
                || devices < 0
                || online < 0
                || starving < 0
                || cpus < 0
                || depth > TopologyLimits.MAX_DEPTH
                || controller >= CONTROLLER_STATES.length) throw new IOException("Bad network entry");
            if (anchor == null) anchor = pivot;
            networks.add(
                new TopologyNetwork(
                    id,
                    pivot == null ? 0 : pivot.dimension(),
                    pivot,
                    icon,
                    CONTROLLER_STATES[controller],
                    (state & 1) != 0,
                    (state & 2) != 0,
                    nodes,
                    devices,
                    online,
                    starving,
                    stored,
                    max,
                    idle,
                    cpus,
                    depth));
        }

        int relayCount = buffer.readVarIntFromBuffer();
        if (relayCount < 0 || relayCount > MAX_RELAYS) throw new IOException("Bad relay count");
        List<TopologyRelay> relays = new ArrayList<>(relayCount);
        for (int index = 0; index < relayCount; index++) {
            int network = buffer.readVarIntFromBuffer();
            int parent = buffer.readVarIntFromBuffer() - 1;
            TopologyLinkKind kind = TopologyLinkKind.byOrdinal(buffer.readUnsignedByte());
            boolean active = buffer.readBoolean();
            GridCoord position = readCoord(buffer, anchor);
            ItemStack icon = readIcon(buffer);
            ItemStack dimensionIcon = readIcon(buffer);
            // A relay may only hang off one that was already read, which keeps the tree acyclic by construction.
            if (network < 0 || parent < TopologyRelay.ROOT || parent >= index) throw new IOException("Bad relay entry");
            relays.add(new TopologyRelay(index, network, parent, position, icon, dimensionIcon, kind, active));
        }

        int deviceCount = buffer.readVarIntFromBuffer();
        if (deviceCount < 0 || deviceCount > MAX_DEVICES) throw new IOException("Bad device count");
        List<TopologyDevice> devices = new ArrayList<>(deviceCount);
        for (int index = 0; index < deviceCount; index++) {
            int network = buffer.readVarIntFromBuffer();
            int relay = buffer.readVarIntFromBuffer() - 1;
            int flags2 = buffer.readUnsignedByte();
            GridCoord position = readCoord(buffer, anchor);
            ItemStack icon = readIcon(buffer);
            if (network < 0 || relay < TopologyDevice.DIRECT || relay >= relayCount)
                throw new IOException("Bad device entry");
            devices
                .add(new TopologyDevice(index, network, relay, position, icon, (flags2 & 1) != 0, (flags2 & 2) != 0));
        }

        int bridgeCount = buffer.readVarIntFromBuffer();
        if (bridgeCount < 0 || bridgeCount > MAX_BRIDGES) throw new IOException("Bad bridge count");
        List<TopologyBridge> bridges = new ArrayList<>(bridgeCount);
        for (int index = 0; index < bridgeCount; index++) {
            TopologyLinkKind kind = TopologyLinkKind.byOrdinal(buffer.readUnsignedByte());
            int from = buffer.readVarIntFromBuffer();
            int to = buffer.readVarIntFromBuffer() - 1;
            boolean active = buffer.readBoolean();
            GridCoord origin = readCoord(buffer, anchor);
            GridCoord far = readCoord(buffer, anchor);
            ItemStack icon = readIcon(buffer);
            ItemStack dimensionIcon = readIcon(buffer);
            if (from < 0 || to < TopologyBridge.UNRESOLVED || origin == null) throw new IOException("Bad bridge entry");
            bridges.add(new TopologyBridge(index, kind, from, to, origin, far, icon, dimensionIcon, active));
        }
        return new TopologySnapshot(
            networks,
            relays,
            devices,
            bridges,
            (flags & 1) != 0,
            (flags & 2) != 0,
            (flags & 4) != 0,
            revision);
    }

    private static void writeCoord(PacketBuffer buffer, GridCoord coord, GridCoord anchor) {
        if (coord == null) {
            buffer.writeByte(0);
            return;
        }
        buffer.writeByte(
            1 + coord.side()
                .ordinal());
        writeZigZag(buffer, coord.dimension() - (anchor == null ? 0 : anchor.dimension()));
        writeZigZag(buffer, coord.x() - (anchor == null ? 0 : anchor.x()));
        writeZigZag(buffer, coord.y() - (anchor == null ? 0 : anchor.y()));
        writeZigZag(buffer, coord.z() - (anchor == null ? 0 : anchor.z()));
    }

    private static GridCoord readCoord(PacketBuffer buffer, GridCoord anchor) {
        int marker = buffer.readUnsignedByte();
        if (marker == 0) return null;
        ForgeDirection side = ForgeDirection.getOrientation(marker - 1);
        int dimension = readZigZag(buffer) + (anchor == null ? 0 : anchor.dimension());
        int x = readZigZag(buffer) + (anchor == null ? 0 : anchor.x());
        int y = readZigZag(buffer) + (anchor == null ? 0 : anchor.y());
        int z = readZigZag(buffer) + (anchor == null ? 0 : anchor.z());
        return new GridCoord(dimension, x, y, z, side);
    }

    private static void writeZigZag(PacketBuffer buffer, int value) {
        buffer.writeVarIntToBuffer((value << 1) ^ (value >> 31));
    }

    private static int readZigZag(PacketBuffer buffer) {
        int raw = buffer.readVarIntFromBuffer();
        return (raw >>> 1) ^ -(raw & 1);
    }

    private static void writeIcon(PacketBuffer buffer, ItemStack icon) {
        if (icon == null || icon.getItem() == null) {
            buffer.writeShort(-1);
            return;
        }
        buffer.writeShort(Item.getIdFromItem(icon.getItem()));
        buffer.writeShort(icon.getItemDamage());
    }

    private static ItemStack readIcon(PacketBuffer buffer) {
        int id = buffer.readShort();
        if (id < 0) return null;
        int meta = buffer.readShort();
        Item item = Item.getItemById(id);
        return item == null ? null : new ItemStack(item, 1, Math.max(0, meta));
    }
}
