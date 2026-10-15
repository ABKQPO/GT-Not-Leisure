package com.science.gtnl.client.topology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

import net.minecraft.util.StatCollector;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyLinkKind;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;
import com.science.gtnl.common.topology.TopologyContainer;
import com.science.gtnl.common.topology.TopologyTransport;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyViewState {

    private static final int MIN_CONSOLE_WIDTH = 104;
    private static final int MAX_CONSOLE_WIDTH = 176;
    private static final int MIN_HUD_WIDTH = 104;
    private static final int MAX_HUD_WIDTH = 150;
    private static final float CONSOLE_RESPONSE = 13.0F;
    private static final float DEFAULT_DELTA = 1.0F / 60.0F;
    private static final float MAX_DELTA = 0.1F;

    public interface Actions {

        void rescan();

        void highlight(int networkId, int bridgeId, int deviceId);

        void teleport(int networkId, int bridgeId, int deviceId);
    }

    public enum ConsoleTab {

        NODES,
        DEVICES,
        BRIDGES;

        public String key() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private final boolean[] kindVisible = new boolean[TopologyLinkKind.values().length];
    private final TopologyCamera camera = new TopologyCamera();
    private final TopologyLayout layout = new TopologyLayout();

    private TopologySnapshot snapshot = TopologySnapshot.EMPTY;
    private Actions actions;

    private byte[] incoming;
    private long incomingRevision = -1;
    private int incomingRaw;
    private int received;

    private int screenWidth = 427;
    private int screenHeight = 251;
    private long frameNano;
    private float frameDelta = DEFAULT_DELTA;
    private float animationTime;
    private float consoleSlide = 1.0F;

    private long viewRevision;
    private long filterRevision = -1;
    private String query = "";
    private int depthLimit = Integer.MAX_VALUE;
    private boolean problemsOnly;
    private boolean showUnresolved = true;
    private boolean consoleOpen = true;
    private ConsoleTab tab = ConsoleTab.NODES;

    private int selectedNetwork = -1;
    private int selectedBridge = -1;
    private int hoveredNetwork = -1;
    private int hoveredBridge = -1;
    private int hoveredDevice = -1;
    private int selectedDevice = -1;

    private List<TopologyNetwork> filteredNetworks = List.of();
    private List<TopologyBridge> filteredBridges = List.of();
    private List<TopologyDevice> filteredDevices = List.of();

    public TopologyViewState() {
        for (int index = 0; index < kindVisible.length; index++) kindVisible[index] = true;
    }

    public void advanceFrame() {
        long now = System.nanoTime();
        frameDelta = frameNano == 0 ? DEFAULT_DELTA : Math.min(MAX_DELTA, (now - frameNano) / 1.0E9F);
        frameNano = now;
        animationTime += frameDelta;
        camera.advance(frameDelta);
        float target = consoleOpen ? 1.0F : 0.0F;
        consoleSlide += (target - consoleSlide) * (1.0F - (float) Math.exp(-CONSOLE_RESPONSE * frameDelta));
        if (Math.abs(target - consoleSlide) < 0.001F) consoleSlide = target;
    }

    public void screen(int width, int height) {
        screenWidth = Math.max(80, width);
        screenHeight = Math.max(60, height);
    }

    public int screenWidth() {
        return screenWidth;
    }

    public int screenHeight() {
        return screenHeight;
    }

    public int edge() {
        return screenHeight < 200 || screenWidth < 320 ? 4 : 8;
    }

    public int consoleWidth() {
        return clamp(Math.round(screenWidth * 0.33F), MIN_CONSOLE_WIDTH, MAX_CONSOLE_WIDTH);
    }

    public int hudWidth() {
        return clamp(Math.round(screenWidth * 0.30F), MIN_HUD_WIDTH, MAX_HUD_WIDTH);
    }

    public int hudHeight(int preferred) {
        return Math.max(40, Math.min(preferred, screenHeight / 2));
    }

    private static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    public float frameDelta() {
        return frameDelta;
    }

    public float animationTime() {
        return animationTime;
    }

    public float consoleSlide() {
        return consoleSlide;
    }

    public float consoleVisibleWidth() {
        return consoleWidth() * consoleSlide;
    }

    public void acceptChunk(long revision, int rawLength, int total, int offset, byte[] chunk) {
        if (revision < incomingRevision) return;
        if (offset == 0) {
            incoming = new byte[total];
            incomingRevision = revision;
            incomingRaw = rawLength;
            received = 0;
        }
        if (incoming == null || revision != incomingRevision
            || incoming.length != total
            || incomingRaw != rawLength
            || offset != received) return;
        System.arraycopy(chunk, 0, incoming, offset, chunk.length);
        received += chunk.length;
        if (received != total) return;
        byte[] complete = incoming;
        incoming = null;
        try {
            accept(TopologyContainer.decode(TopologyTransport.inflate(complete, rawLength)));
        } catch (DataFormatException malformed) {
            ScienceNotLeisure.LOG.warn("Cannot inflate network topology snapshot", malformed);
        } catch (IOException | RuntimeException failure) {
            ScienceNotLeisure.LOG.warn("Cannot decode network topology snapshot", failure);
        }
    }

    private void accept(TopologySnapshot next) {
        snapshot = next;
        if (next.network(selectedNetwork) == null) {
            TopologyNetwork main = next.networks()
                .isEmpty() ? null
                    : next.networks()
                        .get(0);
            selectedNetwork = main == null ? -1 : main.id();
        }
        if (selectedBridge >= 0 && next.bridge(selectedBridge) == null) selectedBridge = -1;
        invalidate();
    }

    public TopologySnapshot snapshot() {
        return snapshot;
    }

    public void actions(Actions value) {
        actions = value;
    }

    public void requestRescan() {
        if (actions != null) actions.rescan();
    }

    public void requestHighlight() {
        Target target = actionTarget();
        if (target != null) actions.highlight(target.network(), target.bridge(), target.device());
    }

    public void requestTeleport() {
        Target target = actionTarget();
        if (target != null) actions.teleport(target.network(), target.bridge(), target.device());
    }

    private record Target(int network, int bridge, int device) {}

    private Target actionTarget() {
        if (actions == null) return null;
        int device = hoveredDevice >= 0 ? hoveredDevice : selectedDevice;
        if (device >= 0 && snapshot.device(device) != null) return new Target(-1, -1, device);
        int bridge = hoveredBridge >= 0 ? hoveredBridge : selectedBridge;
        if (bridge >= 0) return new Target(-1, bridge, -1);
        int network = hoveredNetwork >= 0 ? hoveredNetwork : selectedNetwork;
        return network >= 0 ? new Target(network, -1, -1) : null;
    }

    public TopologyCamera camera() {
        return camera;
    }

    public TopologyLayout layout() {
        return layout;
    }

    public long viewRevision() {
        return viewRevision;
    }

    public TopologyNetwork main() {
        return snapshot.networks()
            .isEmpty() ? null
                : snapshot.networks()
                    .get(0);
    }

    public boolean kindVisible(TopologyLinkKind kind) {
        return kindVisible[kind.ordinal()];
    }

    public void toggleKind(TopologyLinkKind kind) {
        kindVisible[kind.ordinal()] = !kindVisible[kind.ordinal()];
        invalidate();
    }

    public String query() {
        return query;
    }

    public void query(String value) {
        String next = value == null ? "" : value;
        if (next.equals(query)) return;
        query = next;
        invalidate();
    }

    public int depthLimit() {
        return depthLimit;
    }

    public void cycleDepthLimit() {
        depthLimit = depthLimit == Integer.MAX_VALUE ? 1 : depthLimit >= 3 ? Integer.MAX_VALUE : depthLimit + 1;
        invalidate();
    }

    public boolean problemsOnly() {
        return problemsOnly;
    }

    public void toggleProblemsOnly() {
        problemsOnly = !problemsOnly;
        invalidate();
    }

    public boolean showUnresolved() {
        return showUnresolved;
    }

    public void toggleShowUnresolved() {
        showUnresolved = !showUnresolved;
        invalidate();
    }

    public boolean consoleOpen() {
        return consoleOpen;
    }

    public void toggleConsole() {
        consoleOpen = !consoleOpen;
    }

    public ConsoleTab tab() {
        return tab;
    }

    public void tab(ConsoleTab next) {
        tab = next;
    }

    public int selectedNetwork() {
        return selectedNetwork;
    }

    public int selectedBridge() {
        return selectedBridge;
    }

    public void selectNetwork(int id) {
        selectedNetwork = id;
        selectedBridge = -1;
        selectedDevice = -1;
    }

    public void selectBridge(int id) {
        selectedBridge = id;
        selectedNetwork = -1;
        selectedDevice = -1;
    }

    public int hoveredNetwork() {
        return hoveredNetwork;
    }

    public int hoveredBridge() {
        return hoveredBridge;
    }

    public int hoveredDevice() {
        return hoveredDevice;
    }

    public int selectedDevice() {
        return selectedDevice;
    }

    public void selectDevice(int id) {
        selectedDevice = id;
    }

    public void hover(int networkId, int bridgeId) {
        hover(networkId, bridgeId, -1);
    }

    public void hover(int networkId, int bridgeId, int deviceId) {
        hoveredNetwork = networkId;
        hoveredBridge = bridgeId;
        hoveredDevice = deviceId;
    }

    public List<TopologyNetwork> filteredNetworks() {
        rebuildFilter();
        return filteredNetworks;
    }

    public List<TopologyBridge> filteredBridges() {
        rebuildFilter();
        return filteredBridges;
    }

    public List<TopologyDevice> filteredDevices() {
        rebuildFilter();
        return filteredDevices;
    }

    public boolean passes(TopologyDevice device) {
        if (problemsOnly && !device.faulted()) return false;
        if (query.isEmpty()) return true;
        String needle = query.toLowerCase(Locale.ROOT);
        return deviceName(device).toLowerCase(Locale.ROOT)
            .contains(needle) || coordinates(device.position()).contains(needle);
    }

    public static String deviceName(TopologyDevice device) {
        if (device.icon() != null) {
            try {
                return device.icon()
                    .getDisplayName();
            } catch (RuntimeException ignored) {
                // A broken item name must never take down the viewer.
            }
        }
        return translate("device");
    }

    public boolean passes(TopologyNetwork network) {
        if (network.depth() > depthLimit) return false;
        if (problemsOnly && TopologyPalette.health(network) == TopologyPalette.GOOD) return false;
        return query.isEmpty() || matches(network);
    }

    public boolean passes(TopologyBridge bridge) {
        if (!kindVisible(bridge.kind())) return false;
        if (!showUnresolved && !bridge.resolved()) return false;
        if (problemsOnly && bridge.active() && bridge.resolved()) return false;
        return query.isEmpty() || matches(bridge);
    }

    private boolean matches(TopologyNetwork network) {
        String needle = query.toLowerCase(Locale.ROOT);
        return label(network).toLowerCase(Locale.ROOT)
            .contains(needle) || coordinates(network.pivot()).contains(needle);
    }

    private boolean matches(TopologyBridge bridge) {
        String needle = query.toLowerCase(Locale.ROOT);
        return kindLabel(bridge.kind()).toLowerCase(Locale.ROOT)
            .contains(needle) || coordinates(bridge.origin()).contains(needle);
    }

    private void invalidate() {
        viewRevision++;
        filterRevision = -1;
    }

    private void rebuildFilter() {
        if (filterRevision == viewRevision) return;
        filterRevision = viewRevision;
        List<TopologyNetwork> networks = new ArrayList<>();
        for (TopologyNetwork network : snapshot.networks()) {
            if (passes(network)) networks.add(network);
        }
        List<TopologyBridge> bridges = new ArrayList<>();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (passes(bridge)) bridges.add(bridge);
        }
        List<TopologyDevice> devices = new ArrayList<>();
        // The device list always describes the focused network, otherwise a large system floods the console.
        int focus = focusedNetwork();
        for (TopologyDevice device : snapshot.devices()) {
            if (device.network() == focus && passes(device)) devices.add(device);
        }
        filteredNetworks = networks;
        filteredBridges = bridges;
        filteredDevices = devices;
    }

    public int focusedNetwork() {
        TopologyBridge bridge = snapshot.bridge(selectedBridge);
        if (bridge != null) return bridge.resolved() ? bridge.to() : bridge.from();
        if (snapshot.network(selectedNetwork) != null) return selectedNetwork;
        TopologyNetwork main = main();
        return main == null ? -1 : main.id();
    }

    public static String label(TopologyNetwork network) {
        if (network.isMain()) return translate("main");
        if (network.icon() != null) {
            try {
                return network.icon()
                    .getDisplayName();
            } catch (RuntimeException ignored) {
                // A broken item name must never take down the viewer.
            }
        }
        return translate("subnet", network.id());
    }

    public static int offline(TopologyNetwork network) {
        return Math.max(0, network.devices() - network.online());
    }

    public static String deviceSummary(TopologyNetwork network) {
        return translate("devices_summary", network.online(), offline(network), network.starving());
    }

    public static String location(GridCoord coord) {
        return coord == null ? translate("unknown_position") : translate("location", coord.dimension(), coord.format());
    }

    public static String kindLabel(TopologyLinkKind kind) {
        return StatCollector.translateToLocal("gtnl.topology.kind." + kind.key());
    }

    public static String coordinates(GridCoord coord) {
        return coord == null ? "" : coord.dimension() + " / " + coord.format();
    }

    public static String translate(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.topology.gui." + key, args);
    }
}
