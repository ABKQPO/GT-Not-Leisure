package com.science.gtnl.client.topology;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyLimits;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologyRelay;
import com.science.gtnl.api.topology.TopologySnapshot;
import com.science.gtnl.client.topology.TopologyLayout.Point;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.IntArrayList;

@SideOnly(Side.CLIENT)
public class TopologyGraphWidget extends Widget<TopologyGraphWidget> implements Interactable {

    private static final float MAIN_RADIUS = 13.0F;
    private static final float DEEPEST_RADIUS = 6.5F;
    private static final float BRIDGE_RADIUS = 5.0F;
    private static final float RELAY_RADIUS = 7.0F;
    private static final float DEVICE_RADIUS = 5.5F;
    private static final float LAYOUT_RESPONSE = 11.0F;
    private static final int DRAG_SLOP = 3;

    private static final int MIN_ICON = 6;
    private static final int MAX_ICON = 32;
    private static final float ICON_FILL = 1.5F;
    private static final float BADGE_SCALE = 0.6F;
    private static final int MIN_BADGE = 6;

    private static final float LABEL_ZOOM_FLOOR = 0.4F;
    private static final float DEVICE_LABEL_ZOOM_FLOOR = 1.05F;
    private static final int LABEL_GAP = 2;
    private static final float RING_MARGIN = 10.0F;
    private static final int LABEL_LINE = 9;
    private static final int MIN_LABEL_WIDTH = 42;
    private static final int MAX_LABEL_WIDTH = 120;
    private static final int MAX_LABEL_LINES = 2;
    private static final int MAX_DEVICE_LABEL_LINES = 1;
    private static final int DEVICE_LABEL_WIDTH = 64;
    private static final String ELLIPSIS = "...";
    private static final int LABEL_PADDING = 1;
    private static final int MAX_LABELS = 192;

    private static final int MOUSE_LEFT = 0;
    private static final int MOUSE_RIGHT = 1;
    private static final int MOUSE_MIDDLE = 2;
    private static final int NO_TARGET = 0;
    private static final int NETWORK_KEY_BASE = 1 << 24;
    private static final int BRIDGE_KEY_BASE = 2 << 24;
    private static final int DEVICE_KEY_BASE = 3 << 24;
    private static final int RELAY_KEY_BASE = 4 << 24;
    private static final long DOUBLE_CLICK_MS = 350L;
    private static final float KEY_PAN_STEP = 18.0F;
    private static final float KEY_ZOOM_STEP = 1.2F;
    private static final float LINK_PICK_RANGE = 2.5F;

    /**
     * What the viewer is pointing at right now: the hovered element, or the selected one when the cursor is away.
     * The highlight it drives is deliberately one hop wide, so a dense fan of links can never light up two routes at
     * once, and nothing outside it is dimmed.
     */
    private record Focus(int device, int relay, int networkA, int networkB, int link) {

        static final Focus NONE = new Focus(-1, -1, -1, -1, -1);

        boolean litDevice(int id) {
            return id == device;
        }

        boolean litRelay(int id) {
            return id == relay;
        }

        boolean litNetwork(int id) {
            return id == networkA || id == networkB;
        }

        boolean litLink(int id) {
            return id == link;
        }
    }

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();
    private final IntArrayList placed = new IntArrayList();

    private int lastMouseX, lastMouseY;
    private int pressX, pressY;
    private boolean panning;
    private boolean dragged;
    private int viewWidth, viewHeight;
    private float centerX, centerY;
    private int mainId = -1;
    private int lastClicked = NO_TARGET;
    private long lastClickTime;

    public TopologyGraphWidget(TopologyViewState state) {
        this.state = state;
        tooltipDynamic(this::buildTooltip).tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE)
            .tooltipShowUpTimer(0)
            .tooltipAutoUpdate(true);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        viewWidth = getArea().width;
        viewHeight = getArea().height;
        // The console is an overlay, so the graph keeps its centre inside whatever area is still free.
        float covered = consoleCover();
        centerX = (viewWidth - covered) * 0.5F;
        centerY = viewHeight * 0.5F;
        TopologySnapshot snapshot = state.snapshot();
        TopologyNetwork main = state.main();
        mainId = main == null ? -1 : main.id();
        state.layout()
            .update(snapshot, state.viewRevision(), 1.0F - (float) Math.exp(-LAYOUT_RESPONSE * state.frameDelta()));
        if (isBelowMouse()) updateHover();

        // A themed display inset gives the star map its dark ground while the chrome on top stays flat.
        TopologyTheme.drawDisplay(context, 0, 0, viewWidth, viewHeight);
        draw.begin();
        drawBackdrop();
        if (snapshot.isEmpty()) {
            draw.end();
            drawCenteredNotice(snapshot);
            return;
        }
        drawDepthRings();
        Focus focus = resolveFocus();
        drawEdges(snapshot, focus);
        drawDevices(snapshot, focus);
        drawRelays(snapshot, focus);
        drawBridges(snapshot, focus);
        drawNetworks(snapshot, focus);
        draw.end();
        drawIcons(snapshot);
        drawLabels(snapshot, focus);
    }

    private void drawBackdrop() {
        draw.rect(0, 0, viewWidth, viewHeight, TopologyPalette.withAlpha(TopologyPalette.BACKDROP_OUTER, 0.88F));
        draw.halo(centerX, centerY, 0, Math.max(viewWidth, viewHeight) * 0.72F, TopologyPalette.BACKDROP_INNER, 0.85F);
    }

    private float consoleCover() {
        return state.consoleVisibleWidth();
    }

    private void drawDepthRings() {
        TopologyCamera camera = state.camera();
        float originX = centerX - camera.x() * camera.zoom();
        float originY = centerY - camera.y() * camera.zoom();
        float reach = Math.max(viewWidth, viewHeight) * 2.4F;
        for (int depth = 1; depth < state.layout()
            .ringCount(); depth++) {
            float radius = state.layout()
                .ringRadius(depth) * camera.zoom();
            if (radius < 8 || radius > reach) continue;
            draw.ring(originX, originY, radius, 1.0F, TopologyPalette.withAlpha(TopologyPalette.RING_GUIDE, 0.14F));
        }
    }

    /**
     * Resolved once per frame. A focused device lights only the spoke up to its own network's controller, and a
     * focused network lights only the single link it hangs from, never the whole chain back to the main network.
     */
    private Focus resolveFocus() {
        TopologySnapshot snapshot = state.snapshot();
        TopologyDevice device = snapshot
            .device(state.hoveredDevice() >= 0 ? state.hoveredDevice() : state.selectedDevice());
        if (device != null) return new Focus(device.id(), device.relay(), device.network(), -1, -1);

        TopologyRelay relay = snapshot.relay(state.hoveredRelay() >= 0 ? state.hoveredRelay() : state.selectedRelay());
        if (relay != null) return new Focus(-1, relay.id(), relay.network(), -1, -1);

        TopologyBridge bridge = snapshot
            .bridge(state.hoveredBridge() >= 0 ? state.hoveredBridge() : state.selectedBridge());
        if (bridge != null) return new Focus(-1, -1, bridge.from(), bridge.to(), bridge.id());

        int networkId = state.hoveredNetwork() >= 0 ? state.hoveredNetwork()
            : snapshot.network(state.selectedNetwork()) != null ? state.selectedNetwork() : -1;
        if (networkId < 0) return Focus.NONE;
        TopologyBridge parent = state.layout()
            .parentOf(networkId);
        return new Focus(-1, -1, networkId, -1, parent == null ? -1 : parent.id());
    }

    private void drawEdges(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!state.passes(bridge)) continue;
            Point from = state.layout()
                .network(bridge.from());
            Point own = state.layout()
                .bridge(bridge.id());
            if (from == null || own == null) continue;
            Point to = bridge.resolved() && !bridge.loop() ? state.layout()
                .network(bridge.to()) : null;
            boolean lit = focus.litLink(bridge.id());
            // Nothing is ever pushed into the background: focus only brightens, it never dims the rest of the graph.
            int color = TopologyPalette.withAlpha(
                bridge.kind()
                    .color(),
                lit ? 1.0F : bridge.active() ? 0.52F : 0.26F);
            float weight = (lit ? 3.0F : bridge.from() == mainId ? 1.9F : 1.4F) * Math.max(0.7F, zoom);

            float ax = screenX(from), ay = screenY(from);
            float bx = screenX(own), by = screenY(own);
            if (lit) halo(ax, ay, bx, by, weight);
            draw.line(ax, ay, bx, by, weight, color);
            if (to == null) continue;

            float cx = screenX(to), cy = screenY(to);
            if (lit) halo(bx, by, cx, cy, weight);
            draw.line(bx, by, cx, cy, weight, color);
            if (bridge.active()) drawFlow(bx, by, cx, cy, bridge, lit, zoom);
        }
    }

    private void halo(float x1, float y1, float x2, float y2, float weight) {
        draw.line(x1, y1, x2, y2, weight * 2.6F, TopologyPalette.withAlpha(TopologyPalette.ACCENT, 0.18F));
    }

    private void drawFlow(float x1, float y1, float x2, float y2, TopologyBridge bridge, boolean lit, float zoom) {
        float progress = (state.animationTime() * 0.6F + bridge.id() * 0.13F) % 1.0F;
        draw.disc(
            x1 + (x2 - x1) * progress,
            y1 + (y2 - y1) * progress,
            1.8F * Math.max(0.7F, zoom),
            TopologyPalette.withAlpha(0xFFFFFF, lit ? 0.9F : 0.34F));
    }

    private void drawBridges(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!state.passes(bridge)) continue;
            Point own = state.layout()
                .bridge(bridge.id());
            if (own == null) continue;
            boolean lit = focus.litLink(bridge.id());
            int color = bridge.kind()
                .color();
            float radius = (lit ? BRIDGE_RADIUS + 2.0F : BRIDGE_RADIUS) * zoom;
            float x = screenX(own), y = screenY(own);
            if (lit) draw.halo(x, y, radius, radius * 2.6F, color, 0.45F);
            draw.disc(x, y, radius, TopologyPalette.opaque(TopologyPalette.NODE_SHELL));
            draw.ring(
                x,
                y,
                radius,
                Math.max(0.9F, (lit ? 1.8F : 1.2F) * zoom),
                TopologyPalette.opaque(bridge.active() ? color : TopologyPalette.NODE_IDLE));
            if (!bridge.resolved()) draw
                .ring(x, y, radius * 1.6F, Math.max(0.8F, zoom), TopologyPalette.withAlpha(TopologyPalette.BAD, 0.8F));
        }
    }

    private void drawDevices(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        for (TopologyDevice device : snapshot.devices()) {
            Point point = state.layout()
                .device(device.id());
            Point owner = state.layout()
                .network(device.network());
            if (point == null || owner == null || !state.passes(device)) continue;
            boolean lit = focus.litDevice(device.id());
            int color = TopologyPalette.deviceState(device.active(), device.channelOk());
            float x = screenX(point), y = screenY(point);
            // A faulty device paints its own spoke from the start: a problem must be visible without hovering it.
            // Healthy spokes stay neutral slate, which never reads as one of the coloured cross network links.
            int spoke = device.faulted() ? TopologyPalette.withAlpha(color, lit ? 1.0F : 0.85F)
                : TopologyPalette.withAlpha(lit ? color : TopologyPalette.SPOKE, lit ? 1.0F : 0.55F);
            draw.line(
                screenX(owner),
                screenY(owner),
                x,
                y,
                Math.max(0.7F, (lit || device.faulted() ? 1.8F : 1.0F) * zoom),
                spoke);
            float radius = (lit ? DEVICE_RADIUS + 1.5F : DEVICE_RADIUS) * zoom;
            if (lit) draw.halo(x, y, radius, radius * 2.6F, color, 0.5F);
            draw.disc(x, y, radius, TopologyPalette.opaque(TopologyPalette.NODE_SHELL));
            draw.ring(x, y, radius, Math.max(0.8F, (lit ? 1.8F : 1.2F) * zoom), TopologyPalette.opaque(color));
        }
    }

    /**
     * Relays and the hop lines between them. The hop keeps its link kind colour, which is what separates a wireless
     * or quantum leg from an ordinary device spoke at a glance.
     */
    private void drawRelays(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        for (TopologyRelay relay : snapshot.relays()) {
            Point point = state.layout()
                .relay(relay.id());
            Point anchor = relay.attachedToNetwork() ? state.layout()
                .network(relay.network())
                : state.layout()
                    .relay(relay.parent());
            if (point == null || anchor == null || !state.passes(relay)) continue;
            boolean lit = focus.litRelay(relay.id());
            int color = relay.kind()
                .color();
            float x = screenX(point), y = screenY(point);
            float weight = Math.max(0.9F, (lit ? 2.6F : 1.6F) * zoom);
            if (lit) halo(screenX(anchor), screenY(anchor), x, y, weight);
            draw.line(
                screenX(anchor),
                screenY(anchor),
                x,
                y,
                weight,
                TopologyPalette.withAlpha(color, lit ? 1.0F : relay.active() ? 0.7F : 0.3F));
            float radius = (lit ? RELAY_RADIUS + 1.5F : RELAY_RADIUS) * zoom;
            if (lit) draw.halo(x, y, radius, radius * 2.6F, color, 0.45F);
            draw.disc(x, y, radius, TopologyPalette.opaque(TopologyPalette.NODE_SHELL));
            draw.ring(
                x,
                y,
                radius,
                Math.max(0.9F, (lit ? 1.8F : 1.2F) * zoom),
                TopologyPalette.opaque(relay.active() ? color : TopologyPalette.BAD));
        }
    }

    private void drawNetworks(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        for (TopologyNetwork network : snapshot.networks()) {
            Point point = state.layout()
                .network(network.id());
            if (point == null) continue;
            boolean lit = focus.litNetwork(network.id());
            boolean selected = state.selectedNetwork() == network.id();
            float alpha = state.passes(network) ? 1.0F : 0.22F;
            float radius = radiusOf(network) * zoom;
            float x = screenX(point), y = screenY(point);
            int outline = TopologyPalette.outline(network);

            if (network.isMain() || lit) {
                float breathe = 1.0F + MathHelper.sin(state.animationTime() * 3.2F) * 0.06F;
                draw.halo(x, y, radius, radius * 3.4F * breathe, outline, (lit ? 0.42F : 0.3F) * alpha);
            }
            draw.disc(x, y, radius, TopologyPalette.withAlpha(TopologyPalette.NODE_SHELL, alpha));
            draw.ring(
                x,
                y,
                radius,
                Math.max(1.0F, (lit || selected ? 2.4F : 1.6F) * zoom),
                TopologyPalette.withAlpha(outline, alpha));

            if (network.devices() > 0) {
                float ratio = network.online() / (float) network.devices();
                draw.arc(
                    x,
                    y,
                    radius + 3.5F * zoom,
                    Math.max(1.0F, 2.2F * zoom),
                    0.0F,
                    Math.min(1.0F, ratio),
                    TopologyPalette.withAlpha(TopologyPalette.availability(ratio), alpha));
                if (network.starving() > 0) draw.arc(
                    x,
                    y,
                    radius + 6.5F * zoom,
                    Math.max(1.0F, 1.6F * zoom),
                    0.0F,
                    Math.min(1.0F, network.starving() / (float) network.devices()),
                    TopologyPalette.withAlpha(TopologyPalette.BAD, alpha));
            }
            if (selected) draw.ring(
                x,
                y,
                radius + 9.0F * zoom,
                Math.max(0.8F, zoom),
                TopologyPalette.withAlpha(TopologyPalette.ACCENT, alpha * 0.7F));
        }
    }

    /**
     * Icons are never dropped for being small: a node without its item is unrecognisable, which is worse than a
     * cramped one. They are only clamped to a readable size range.
     */
    private void drawIcons(TopologySnapshot snapshot) {
        float zoom = state.camera()
            .zoom();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            for (TopologyNetwork network : snapshot.networks()) {
                Point point = state.layout()
                    .network(network.id());
                if (point != null && state.passes(network)) icon(network.icon(), point, radiusOf(network) * zoom);
            }
            for (TopologyBridge bridge : snapshot.bridges()) {
                Point point = state.layout()
                    .bridge(bridge.id());
                if (point == null || !state.passes(bridge)) continue;
                int size = icon(bridge.icon(), point, BRIDGE_RADIUS * zoom);
                // A quantum ring or any other link whose far side is elsewhere wears that dimension's surface block.
                if (bridge.crossesDimension()) badge(bridge.dimensionIcon(), point, size);
            }
            for (TopologyRelay relay : snapshot.relays()) {
                Point point = state.layout()
                    .relay(relay.id());
                if (point == null || !state.passes(relay)) continue;
                int size = icon(relay.icon(), point, RELAY_RADIUS * zoom);
                // A hop that lands in another dimension wears that dimension's surface block.
                badge(relay.dimensionIcon(), point, size);
            }
            for (TopologyDevice device : snapshot.devices()) {
                Point point = state.layout()
                    .device(device.id());
                if (point != null && state.passes(device)) icon(device.icon(), point, DEVICE_RADIUS * zoom);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private int icon(ItemStack stack, Point point, float screenRadius) {
        int size = MathHelper.clamp_int(Math.round(screenRadius * ICON_FILL), MIN_ICON, MAX_ICON);
        if (stack == null) return size;
        GuiDraw.drawItem(
            stack,
            (int) screenX(point) - size / 2,
            (int) screenY(point) - size / 2,
            size,
            size,
            getContext().getCurrentDrawingZ());
        return size;
    }

    /**
     * Small emblem pinned to the upper right of a node icon. It overhangs the icon slightly so it still reads as a
     * marker on top of the node rather than as part of the item itself.
     */
    private void badge(ItemStack stack, Point point, int iconSize) {
        if (stack == null) return;
        int size = Math.max(MIN_BADGE, Math.round(iconSize * BADGE_SCALE));
        int left = (int) screenX(point) + iconSize / 2 - size / 2;
        int top = (int) screenY(point) - iconSize / 2 - size / 2;
        GuiDraw.drawItem(stack, left, top, size, size, getContext().getCurrentDrawingZ());
    }

    /**
     * Captions always read upright and hang directly under their node. Network names stay visible down to a very
     * small zoom; device names only appear once there is actually room for them, or while one is focused.
     */
    /**
     * Captions are laid out most important first and a caption that would collide with one already placed is simply
     * dropped. A crowded graph therefore thins out instead of turning into an unreadable pile, and zooming in brings
     * the dropped names back as room appears.
     */
    private void drawLabels(TopologySnapshot snapshot, Focus focus) {
        float zoom = state.camera()
            .zoom();
        if (zoom < LABEL_ZOOM_FLOOR) return;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean deviceNames = zoom >= DEVICE_LABEL_ZOOM_FLOOR;
        placed.clear();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            for (TopologyNetwork network : snapshot.networks()) {
                Point point = state.layout()
                    .network(network.id());
                if (point == null || !state.passes(network)) continue;
                int color = TopologyPalette.opaque(
                    network.isMain() || focus.litNetwork(network.id()) ? TopologyPalette.TEXT
                        : TopologyPalette.NODE_IDLE);
                // Clears the availability arcs and the selection ring, both of which scale with the zoom.
                int top = (int) screenY(point) + Math.round((radiusOf(network) + RING_MARGIN) * zoom) + LABEL_GAP;
                caption(
                    font,
                    TopologyViewState.label(network),
                    (int) screenX(point),
                    top,
                    labelWrapWidth(network, zoom),
                    color,
                    MAX_LABEL_LINES);
            }
            for (TopologyRelay relay : snapshot.relays()) {
                boolean lit = focus.litRelay(relay.id());
                if (!deviceNames && !lit) continue;
                Point point = state.layout()
                    .relay(relay.id());
                if (point == null || !state.passes(relay)) continue;
                int top = (int) screenY(point) + Math.round(RELAY_RADIUS * zoom) + LABEL_GAP;
                caption(
                    font,
                    TopologyViewState.relayName(relay),
                    (int) screenX(point),
                    top,
                    DEVICE_LABEL_WIDTH,
                    TopologyPalette.opaque(
                        lit ? TopologyPalette.TEXT
                            : relay.kind()
                                .color()),
                    MAX_DEVICE_LABEL_LINES);
            }
            for (TopologyDevice device : snapshot.devices()) {
                boolean lit = focus.litDevice(device.id());
                if (!deviceNames && !lit) continue;
                Point point = state.layout()
                    .device(device.id());
                if (point == null || !state.passes(device)) continue;
                int top = (int) screenY(point) + Math.round(DEVICE_RADIUS * zoom) + LABEL_GAP;
                caption(
                    font,
                    TopologyViewState.deviceName(device),
                    (int) screenX(point),
                    top,
                    DEVICE_LABEL_WIDTH,
                    TopologyPalette.opaque(lit ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED),
                    MAX_DEVICE_LABEL_LINES);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    /**
     * Draws a caption unless its box overlaps one already drawn this frame.
     */
    private void caption(FontRenderer font, String text, int centreX, int top, int wrapWidth, int color, int maxLines) {
        List<String> lines = wrap(font, text, wrapWidth);
        int drawn = Math.min(lines.size(), maxLines);
        if (drawn == 0) return;
        int widest = 0;
        for (int index = 0; index < drawn; index++) widest = Math.max(widest, font.getStringWidth(lines.get(index)));
        int left = centreX - widest / 2;
        int height = drawn * LABEL_LINE;
        if (!reserve(left, top, widest, height)) return;
        for (int index = 0; index < drawn; index++) {
            String line = lines.get(index);
            // The last visible line ends in an ellipsis, so a clipped caption reads as clipped instead of as the name.
            if (index == maxLines - 1 && lines.size() > maxLines)
                line = font.trimStringToWidth(line, wrapWidth - font.getStringWidth(ELLIPSIS)) + ELLIPSIS;
            font.drawString(line, centreX - font.getStringWidth(line) / 2, top + index * LABEL_LINE, color);
        }
    }

    private boolean reserve(int left, int top, int width, int height) {
        if (width <= 0 || height <= 0) return false;
        int right = left + width + LABEL_PADDING;
        int bottom = top + height + LABEL_PADDING;
        int padLeft = left - LABEL_PADDING;
        int padTop = top - LABEL_PADDING;
        for (int index = 0; index < placed.size(); index += 4) {
            if (padLeft < placed.getInt(index + 2) && right > placed.getInt(index)
                && padTop < placed.getInt(index + 3)
                && bottom > placed.getInt(index + 1)) return false;
        }
        if (placed.size() >= MAX_LABELS * 4) return false;
        placed.add(padLeft);
        placed.add(padTop);
        placed.add(right);
        placed.add(bottom);
        return true;
    }

    private int labelWrapWidth(TopologyNetwork network, float zoom) {
        float slot = state.layout()
            .labelRoom(network.id()) * zoom
            * 0.92F;
        return MathHelper.clamp_int(Math.round(slot), MIN_LABEL_WIDTH, MAX_LABEL_WIDTH);
    }

    @SuppressWarnings("unchecked")
    private static List<String> wrap(FontRenderer font, String text, int wrapWidth) {
        return font.listFormattedStringToWidth(text, wrapWidth);
    }

    private void drawCenteredNotice(TopologySnapshot snapshot) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String message = !snapshot.bound() ? TopologyViewState.translate("unbound")
            : !snapshot.available() ? TopologyViewState.translate("unavailable")
                : TopologyViewState.translate("scanning");
        // These sentences are long enough to run off a small screen, so the empty state wraps like any caption does.
        int wrapWidth = Math.max(MIN_LABEL_WIDTH, Math.round((viewWidth - consoleCover()) * 0.7F));
        List<String> lines = wrap(font, message, wrapWidth);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            int top = (int) centerY - lines.size() * LABEL_LINE / 2;
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                font.drawString(
                    line,
                    (int) centerX - font.getStringWidth(line) / 2,
                    top + index * LABEL_LINE,
                    TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void buildTooltip(RichTooltip tooltip) {
        TopologyDevice device = state.snapshot()
            .device(state.hoveredDevice());
        if (device != null) {
            deviceTooltip(tooltip, device);
            return;
        }
        TopologyRelay relay = state.snapshot()
            .relay(state.hoveredRelay());
        if (relay != null) {
            relayTooltip(tooltip, relay);
            return;
        }
        TopologyNetwork network = state.snapshot()
            .network(state.hoveredNetwork());
        if (network != null) {
            networkTooltip(tooltip, network);
            return;
        }
        TopologyBridge bridge = state.snapshot()
            .bridge(state.hoveredBridge());
        if (bridge != null) bridgeTooltip(tooltip, bridge);
    }

    private void networkTooltip(RichTooltip tooltip, TopologyNetwork network) {
        tooltip.textColor(TopologyPalette.opaque(TopologyPalette.outline(network)))
            .add(TopologyViewState.label(network))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.location(network.pivot()))
            .newLine()
            .add(TopologyViewState.translate("tooltip.depth", network.depth()))
            .newLine()
            .add(TopologyViewState.translate("tooltip.nodes", network.nodes(), network.devices()))
            .newLine()
            .textColor(
                TopologyPalette.opaque(
                    TopologyPalette
                        .availability(network.devices() == 0 ? 1.0F : network.online() / (float) network.devices())))
            .add(
                TopologyViewState.translate(
                    "tooltip.online",
                    network.online(),
                    TopologyViewState.offline(network),
                    network.devices()))
            .newLine()
            .textColor(
                TopologyPalette.opaque(network.starving() > 0 ? TopologyPalette.BAD : TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.translate("tooltip.starving", network.starving()))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.translate("tooltip.controller", controllerName(network)))
            .newLine()
            .add(
                TopologyViewState.translate(
                    "tooltip.energy",
                    TopologyHudWidget.compact(network.storedPower()),
                    TopologyHudWidget.compact(network.maxPower()),
                    TopologyHudWidget.compact(network.idleDrain())))
            .newLine()
            .add(TopologyViewState.translate("tooltip.cpus", network.craftingCpu()))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.ACCENT_DEEP))
            .add(TopologyViewState.translate("tooltip.actions"));
        if (!network.isMain()) tooltip.newLine()
            .add(TopologyViewState.translate("tooltip.promote"));
    }

    private void relayTooltip(RichTooltip tooltip, TopologyRelay relay) {
        TopologyNetwork owner = state.snapshot()
            .network(relay.network());
        tooltip.textColor(
            TopologyPalette.opaque(
                relay.kind()
                    .color()))
            .add(TopologyViewState.relayName(relay))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.location(relay.position()))
            .newLine()
            .add(TopologyViewState.translate("tooltip.relay_kind", TopologyViewState.kindLabel(relay.kind())))
            .newLine()
            .add(
                TopologyViewState.translate(
                    "tooltip.device_network",
                    owner == null ? TopologyViewState.translate("unknown_network") : TopologyViewState.label(owner)))
            .newLine()
            .textColor(TopologyPalette.opaque(relay.active() ? TopologyPalette.GOOD : TopologyPalette.BAD))
            .add(TopologyViewState.translate(relay.active() ? "tooltip.relay_up" : "tooltip.relay_down"))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.ACCENT_DEEP))
            .add(TopologyViewState.translate("tooltip.actions"));
    }

    private void deviceTooltip(RichTooltip tooltip, TopologyDevice device) {
        TopologyNetwork owner = state.snapshot()
            .network(device.network());
        int color = TopologyPalette.deviceState(device.active(), device.channelOk());
        tooltip.textColor(TopologyPalette.opaque(TopologyPalette.TEXT))
            .add(TopologyViewState.deviceName(device))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.location(device.position()))
            .newLine()
            .add(
                TopologyViewState.translate(
                    "tooltip.device_network",
                    owner == null ? TopologyViewState.translate("unknown_network") : TopologyViewState.label(owner)))
            .newLine()
            .textColor(TopologyPalette.opaque(color))
            .add(TopologyViewState.translate(device.active() ? "tooltip.device_online" : "tooltip.device_offline"))
            .newLine()
            .textColor(TopologyPalette.opaque(device.channelOk() ? TopologyPalette.TEXT_MUTED : TopologyPalette.BAD))
            .add(
                TopologyViewState
                    .translate(device.channelOk() ? "tooltip.device_channel_ok" : "tooltip.device_channel_missing"))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.ACCENT_DEEP))
            .add(TopologyViewState.translate("tooltip.actions"));
    }

    private void bridgeTooltip(RichTooltip tooltip, TopologyBridge bridge) {
        TopologySnapshot snapshot = state.snapshot();
        TopologyNetwork from = snapshot.network(bridge.from());
        TopologyNetwork to = bridge.resolved() ? snapshot.network(bridge.to()) : null;
        tooltip.textColor(
            TopologyPalette.opaque(
                bridge.kind()
                    .color()))
            .add(TopologyViewState.kindLabel(bridge.kind()))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.TEXT_MUTED))
            .add(TopologyViewState.location(bridge.origin()))
            .newLine()
            .add(
                TopologyViewState.translate(
                    "tooltip.bridge_from",
                    from == null ? TopologyViewState.translate("unknown_network") : TopologyViewState.label(from)))
            .newLine()
            .add(
                TopologyViewState.translate(
                    "tooltip.bridge_to",
                    to == null ? TopologyViewState.translate("unknown_network") : TopologyViewState.label(to)));
        if (bridge.farEnd() != null) tooltip.newLine()
            .add(TopologyViewState.translate("tooltip.bridge_far", TopologyViewState.location(bridge.farEnd())));
        if (bridge.farEnd() != null && bridge.origin()
            .dimension()
            != bridge.farEnd()
                .dimension())
            tooltip.newLine()
                .textColor(TopologyPalette.opaque(TopologyPalette.ACCENT))
                .add(TopologyViewState.translate("tooltip.cross_dimension"));
        tooltip.newLine()
            .textColor(
                TopologyPalette.opaque(
                    bridge.resolved() ? bridge.active() ? TopologyPalette.GOOD : TopologyPalette.WARN
                        : TopologyPalette.BAD))
            .add(
                TopologyViewState.translate(
                    bridge.resolved() ? bridge.active() ? "tooltip.bridge_active" : "tooltip.bridge_idle"
                        : "tooltip.bridge_unresolved"))
            .newLine()
            .textColor(TopologyPalette.opaque(TopologyPalette.ACCENT_DEEP))
            .add(TopologyViewState.translate("tooltip.actions"));
    }

    private static String controllerName(TopologyNetwork network) {
        if (network.booting()) return TopologyViewState.translate("hud.state.booting");
        return switch (network.controller()) {
            case CONTROLLER_ONLINE -> TopologyViewState.translate("hud.state.online");
            case CONTROLLER_CONFLICT -> TopologyViewState.translate("hud.state.conflict");
            default -> TopologyViewState.translate("hud.state.ad_hoc");
        };
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        lastMouseX = pressX = getContext().getAbsMouseX();
        lastMouseY = pressY = getContext().getAbsMouseY();
        dragged = false;
        if (mouseButton == MOUSE_MIDDLE) {
            fitToView();
            return Result.SUCCESS;
        }
        if (mouseButton == MOUSE_LEFT && Interactable.hasControlDown()) {
            updateHover();
            // Promoting a subnet re-binds the tool in place, which is far quicker than walking back to the device.
            if (state.hoveredNetwork() >= 0 && state.hoveredNetwork() != mainId) {
                state.requestPromote(state.hoveredNetwork());
                Interactable.playButtonClickSound();
                return Result.SUCCESS;
            }
        }
        // The right button acts on whatever is under the cursor, so locating a machine never needs the keyboard.
        if (mouseButton == MOUSE_RIGHT) {
            updateHover();
            if (hoveredKey() != NO_TARGET) {
                if (Interactable.hasShiftDown()) state.requestTeleport();
                else state.requestHighlight();
                Interactable.playButtonClickSound();
                return Result.SUCCESS;
            }
        }
        // Dragging always pans, including when the press started on a node; selection is resolved on release.
        panning = true;
        return Result.SUCCESS;
    }

    @Override
    public boolean onMouseRelease(int mouseButton) {
        panning = false;
        if (dragged) {
            dragged = false;
            return false;
        }
        if (mouseButton != MOUSE_LEFT) return false;
        updateHover();
        int target = hoveredKey();
        if (target == NO_TARGET) {
            // Clicking bare canvas drops the focus, which is the obvious way to put the highlight away again.
            lastClicked = NO_TARGET;
            state.clearSelection();
            return false;
        }
        // A second click on the same thing flies the camera to it, which is the obvious way to drill into a subnet.
        long now = Minecraft.getSystemTime();
        boolean again = target == lastClicked && now - lastClickTime <= DOUBLE_CLICK_MS;
        lastClicked = target;
        lastClickTime = now;
        if (state.hoveredDevice() >= 0) state.selectDevice(state.hoveredDevice());
        else if (state.hoveredRelay() >= 0) state.selectRelay(state.hoveredRelay());
        else if (state.hoveredNetwork() >= 0) state.selectNetwork(state.hoveredNetwork());
        else state.selectBridge(state.hoveredBridge());
        if (again) focusHovered();
        Interactable.playButtonClickSound();
        return false;
    }

    /**
     * One stable identity for whatever is hovered, so a double click can tell "the same node again" from "a different
     * node in the same spot".
     */
    private int hoveredKey() {
        if (state.hoveredDevice() >= 0) return DEVICE_KEY_BASE + state.hoveredDevice();
        if (state.hoveredRelay() >= 0) return RELAY_KEY_BASE + state.hoveredRelay();
        if (state.hoveredNetwork() >= 0) return NETWORK_KEY_BASE + state.hoveredNetwork();
        if (state.hoveredBridge() >= 0) return BRIDGE_KEY_BASE + state.hoveredBridge();
        return NO_TARGET;
    }

    private void focusHovered() {
        Point point = state.hoveredDevice() >= 0 ? state.layout()
            .device(state.hoveredDevice())
            : state.hoveredRelay() >= 0 ? state.layout()
                .relay(state.hoveredRelay())
                : state.hoveredNetwork() >= 0 ? state.layout()
                    .network(state.hoveredNetwork())
                    : state.layout()
                        .bridge(state.hoveredBridge());
        if (point != null) state.camera()
            .focus(point.targetX, point.targetY);
    }

    /**
     * Keyboard steering for the canvas. It keeps the viewer usable without a mouse wheel and gives trackpad players a
     * reliable way to zoom.
     */
    public boolean onCanvasKey(int key) {
        TopologyCamera camera = state.camera();
        switch (key) {
            case Keyboard.KEY_LEFT -> camera.pan(KEY_PAN_STEP, 0);
            case Keyboard.KEY_RIGHT -> camera.pan(-KEY_PAN_STEP, 0);
            case Keyboard.KEY_UP -> camera.pan(0, KEY_PAN_STEP);
            case Keyboard.KEY_DOWN -> camera.pan(0, -KEY_PAN_STEP);
            case Keyboard.KEY_ADD, Keyboard.KEY_EQUALS -> camera.zoomBy(KEY_ZOOM_STEP, 0, 0);
            case Keyboard.KEY_SUBTRACT, Keyboard.KEY_MINUS -> camera.zoomBy(1.0F / KEY_ZOOM_STEP, 0, 0);
            case Keyboard.KEY_HOME -> camera.recenter();
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        int mouseX = getContext().getAbsMouseX();
        int mouseY = getContext().getAbsMouseY();
        if (Math.abs(mouseX - pressX) > DRAG_SLOP || Math.abs(mouseY - pressY) > DRAG_SLOP) dragged = true;
        if (panning) state.camera()
            .pan(mouseX - lastMouseX, mouseY - lastMouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        state.camera()
            .zoomBy(
                direction.isUp() ? 1.12F : 1.0F / 1.12F,
                getContext().getAbsMouseX() - getArea().x - centerX,
                getContext().getAbsMouseY() - getArea().y - centerY);
        return true;
    }

    /**
     * Nodes are picked by containment, links by the nearest one inside a tight radius. Taking the single nearest
     * rather than every match in range is what stops a dense fan of links from lighting up more than one route.
     */
    public void updateHover() {
        if (panning) return;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        if (mouseX < 0 || mouseY < 0 || mouseX > getArea().width || mouseY > getArea().height) return;
        if (state.pointerBlocked(mouseX, mouseY)) return;
        TopologySnapshot snapshot = state.snapshot();
        float zoom = state.camera()
            .zoom();
        for (TopologyDevice device : snapshot.devices()) {
            Point point = state.layout()
                .device(device.id());
            if (point == null || !state.passes(device)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), DEVICE_RADIUS * zoom + 2.0F)) {
                state.hover(-1, -1, device.id(), -1);
                return;
            }
        }
        for (TopologyRelay relay : snapshot.relays()) {
            Point point = state.layout()
                .relay(relay.id());
            if (point == null || !state.passes(relay)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), RELAY_RADIUS * zoom + 2.0F)) {
                state.hover(-1, -1, -1, relay.id());
                return;
            }
        }
        for (TopologyNetwork network : snapshot.networks()) {
            Point point = state.layout()
                .network(network.id());
            if (point == null || !state.passes(network)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), radiusOf(network) * zoom + 3.0F)) {
                state.hover(network.id(), -1);
                return;
            }
        }
        state.hover(-1, pickLink(snapshot, mouseX, mouseY, zoom), -1, -1);
    }

    private int pickLink(TopologySnapshot snapshot, int mouseX, int mouseY, float zoom) {
        float best = Math.max(LINK_PICK_RANGE, 1.6F * zoom);
        int picked = -1;
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!state.passes(bridge)) continue;
            Point own = state.layout()
                .bridge(bridge.id());
            Point from = state.layout()
                .network(bridge.from());
            if (own == null || from == null) continue;
            float ownX = screenX(own), ownY = screenY(own);
            // The marker itself is pulled in closer than the line, so clicking the badge always beats the wire.
            float distance = distance(mouseX, mouseY, ownX, ownY) - BRIDGE_RADIUS * zoom;
            distance = Math.min(distance, segmentDistance(mouseX, mouseY, screenX(from), screenY(from), ownX, ownY));
            Point to = bridge.resolved() && !bridge.loop() ? state.layout()
                .network(bridge.to()) : null;
            if (to != null)
                distance = Math.min(distance, segmentDistance(mouseX, mouseY, ownX, ownY, screenX(to), screenY(to)));
            if (distance < best) {
                best = distance;
                picked = bridge.id();
            }
        }
        return picked;
    }

    private static float segmentDistance(float px, float py, float ax, float ay, float bx, float by) {
        float dx = bx - ax, dy = by - ay;
        float lengthSquared = dx * dx + dy * dy;
        if (lengthSquared < 1.0E-4F) return distance(px, py, ax, ay);
        float t = MathHelper.clamp_float(((px - ax) * dx + (py - ay) * dy) / lengthSquared, 0.0F, 1.0F);
        return distance(px, py, ax + dx * t, ay + dy * t);
    }

    private static float distance(float px, float py, float x, float y) {
        float dx = px - x, dy = py - y;
        return MathHelper.sqrt_float(dx * dx + dy * dy);
    }

    public void fitToView() {
        state.camera()
            .fit(
                state.layout()
                    .outerRadius() + MAIN_RADIUS * 2.5F,
                Math.max(64, getArea().width - (int) consoleCover()),
                Math.max(64, getArea().height));
    }

    public void focusNetwork(int id) {
        Point point = state.layout()
            .network(id);
        if (point != null) state.camera()
            .focus(point.targetX, point.targetY);
    }

    private static boolean within(float mouseX, float mouseY, float x, float y, float radius) {
        float dx = mouseX - x, dy = mouseY - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    /**
     * Node size falls off with nesting so five levels stay legible at once, with the outermost ring still large
     * enough to click.
     */
    private static float radiusOf(TopologyNetwork network) {
        float progress = TopologyLimits.clampDepth(network.depth()) / (float) TopologyLimits.MAX_DEPTH;
        return MAIN_RADIUS + (DEEPEST_RADIUS - MAIN_RADIUS) * MathHelper.sqrt_float(progress);
    }

    private float screenX(Point point) {
        TopologyCamera camera = state.camera();
        return centerX + (point.x - camera.x()) * camera.zoom();
    }

    private float screenY(Point point) {
        TopologyCamera camera = state.camera();
        return centerY + (point.y - camera.y()) * camera.zoom();
    }
}
