package com.science.gtnl.common.topology;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologyRelay;
import com.science.gtnl.api.topology.TopologySnapshot;

public class TopologyTransport {

    public static final int CHUNK_BYTES = 16 * 1024;
    public static final int MAX_PAYLOAD_BYTES = 2 * 1024 * 1024;

    private TopologyTransport() {}

    public static byte[] deflate(byte[] raw) {
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        try {
            deflater.setInput(raw);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(64, raw.length / 2));
            byte[] window = new byte[4096];
            while (!deflater.finished()) {
                int written = deflater.deflate(window);
                if (written <= 0) break;
                out.write(window, 0, written);
            }
            return out.toByteArray();
        } finally {
            deflater.end();
        }
    }

    public static byte[] inflate(byte[] compressed, int expectedLength) throws DataFormatException {
        if (expectedLength <= 0 || expectedLength > MAX_PAYLOAD_BYTES) throw new DataFormatException("Bad length");
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(compressed);
            byte[] out = new byte[expectedLength];
            int filled = 0;
            while (filled < expectedLength && !inflater.finished()) {
                int written = inflater.inflate(out, filled, expectedLength - filled);
                if (written <= 0) break;
                filled += written;
            }
            if (filled != expectedLength) throw new DataFormatException("Truncated payload");
            return out;
        } finally {
            inflater.end();
        }
    }

    public static long signature(TopologySnapshot snapshot) {
        long hash = 1125899906842597L;
        hash = hash * 31 + (snapshot.available() ? 1 : 0);
        hash = hash * 31 + (snapshot.truncated() ? 1 : 0);
        List<TopologyNetwork> networks = snapshot.networks();
        hash = hash * 31 + networks.size();
        for (TopologyNetwork network : networks) {
            hash = hash * 31 + network.id();
            hash = hash * 31 + network.depth();
            hash = hash * 31 + network.controller()
                .ordinal();
            hash = hash * 31 + (network.booting() ? 2 : 0) + (network.powered() ? 1 : 0);
            hash = hash * 31 + network.nodes();
            hash = hash * 31 + network.devices();
            hash = hash * 31 + network.online();
            hash = hash * 31 + network.starving();
            hash = hash * 31 + network.craftingCpu();
            hash = hash * 31 + powerBucket(network.storedPower(), network.maxPower());
            hash = hash * 31 + Math.round(network.idleDrain());
        }
        List<TopologyRelay> relays = snapshot.relays();
        hash = hash * 31 + relays.size();
        for (TopologyRelay relay : relays) {
            hash = hash * 31 + relay.network();
            hash = hash * 31 + relay.parent();
            hash = hash * 31 + relay.kind()
                .ordinal();
            hash = hash * 31 + (relay.active() ? 1 : 0);
            hash = hash * 31 + (relay.position() == null ? 0
                : relay.position()
                    .hashCode());
        }
        List<TopologyDevice> devices = snapshot.devices();
        hash = hash * 31 + devices.size();
        for (TopologyDevice device : devices) {
            hash = hash * 31 + device.network();
            hash = hash * 31 + device.relay();
            hash = hash * 31 + (device.active() ? 2 : 0) + (device.channelOk() ? 1 : 0);
            hash = hash * 31 + (device.position() == null ? 0
                : device.position()
                    .hashCode());
        }
        List<TopologyBridge> bridges = snapshot.bridges();
        hash = hash * 31 + bridges.size();
        for (TopologyBridge bridge : bridges) {
            hash = hash * 31 + bridge.kind()
                .ordinal();
            hash = hash * 31 + bridge.from();
            hash = hash * 31 + bridge.to();
            hash = hash * 31 + (bridge.active() ? 1 : 0);
            hash = hash * 31 + (bridge.origin() == null ? 0
                : bridge.origin()
                    .hashCode());
        }
        return hash;
    }

    private static long powerBucket(double stored, double max) {
        return max <= 0 ? 0 : Math.round(stored / max * 100.0);
    }
}
