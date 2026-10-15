package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.jetbrains.annotations.NotNull;
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
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;
import com.science.gtnl.client.topology.TopologyLayout.Point;
import com.science.gtnl.client.topology.TopologyLayout.Trace;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyGraphWidget extends Widget<TopologyGraphWidget> implements Interactable {

    private static final float MAIN_RADIUS = 13.0F;
    private static final float RING_ONE_RADIUS = 9.0F;
    private static final float DEEP_RADIUS = 7.0F;
    private static final float BRIDGE_RADIUS = 4.0F;
    private static final float DEVICE_RADIUS = 5.5F;
    private static final float MIN_ICON = 5.0F;
    private static final float LABEL_ZOOM_FLOOR = 0.52F;
    private static final int CURVE_SEGMENTS = 14;
    private static final float DIMMED = 0.1F;
    private static final float TRUNK_BEND = 0.55F;
    private static final float BRANCH_BEND = 0.30F;
    private static final float LAYOUT_RESPONSE = 11.0F;
    private static final float ICON_ZOOM_FLOOR = 0.42F;
    private static final int DRAG_SLOP = 3;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();

    private int lastMouseX, lastMouseY;
    private int pressX, pressY;
    private boolean panning;
    private boolean dragged;
    private int viewWidth, viewHeight;
    private float centerX, centerY;
    private int mainId = -1;

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
        centerX = (viewWidth - state.consoleVisibleWidth()) * 0.5F;
        centerY = viewHeight * 0.5F;
        TopologySnapshot snapshot = state.snapshot();
        TopologyNetwork main = state.main();
        mainId = main == null ? -1 : main.id();
        float available = Math.min(viewWidth - state.consoleVisibleWidth(), viewHeight) * 0.5F - 28.0F;
        state.layout()
            .update(
                snapshot,
                state.viewRevision(),
                1.0F - (float) Math.exp(-LAYOUT_RESPONSE * state.frameDelta()),
                available);
        if (isBelowMouse()) updateHover();

        draw.begin();
        drawBackdrop();
        if (snapshot.isEmpty()) {
            draw.end();
            drawCenteredNotice(snapshot);
            return;
        }
        drawDepthRings();
        Trace trace = resolveTrace();
        drawEdges(snapshot, trace);
        drawBridges(snapshot, trace);
        drawDevices(snapshot, trace);
        drawNetworks(snapshot, trace);
        draw.end();
        drawIcons(snapshot, trace);
        drawLabels(snapshot, trace);
    }

    private void drawIcons(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        if (zoom < ICON_ZOOM_FLOOR) return;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            for (TopologyNetwork network : snapshot.networks()) {
                ItemStack icon = network.icon();
                Point point = state.layout()
                    .network(network.id());
                if (icon == null || point == null || !state.passes(network)) continue;
                if (!trace.isEmpty() && !trace.networks()
                    .contains(network.id())) continue;
                int size = iconSize(radiusOf(network) * zoom);
                if (size <= 0) continue;
                GuiDraw.drawItem(
                    icon,
                    (int) screenX(point) - size / 2,
                    (int) screenY(point) - size / 2,
                    size,
                    size,
                    getContext().getCurrentDrawingZ());
            }
            for (TopologyDevice device : snapshot.devices()) {
                ItemStack icon = device.icon();
                Point point = state.layout()
                    .device(device.id());
                if (icon == null || point == null || !state.passes(device)) continue;
                if (!trace.isEmpty() && !trace.networks()
                    .contains(device.network())) continue;
                int size = iconSize(DEVICE_RADIUS * zoom);
                if (size <= 0) continue;
                GuiDraw.drawItem(
                    icon,
                    (int) screenX(point) - size / 2,
                    (int) screenY(point) - size / 2,
                    size,
                    size,
                    getContext().getCurrentDrawingZ());
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static int iconSize(float screenRadius) {
        float fit = screenRadius * 1.3F;
        return fit < MIN_ICON ? 0 : (int) Math.min(18.0F, fit);
    }

    private void drawBackdrop() {
        draw.rect(0, 0, viewWidth, viewHeight, TopologyPalette.opaque(TopologyPalette.BACKDROP_OUTER));
        draw.halo(centerX, centerY, 0, Math.max(viewWidth, viewHeight) * 0.72F, TopologyPalette.BACKDROP_INNER, 0.95F);
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

    private Trace resolveTrace() {
        int hoveredNetwork = state.hoveredNetwork();
        if (hoveredNetwork >= 0) return state.layout()
            .trace(hoveredNetwork);
        int hoveredBridge = state.hoveredBridge();
        if (hoveredBridge < 0) return Trace.NONE;
        TopologyBridge bridge = state.snapshot()
            .bridge(hoveredBridge);
        if (bridge == null) return Trace.NONE;
        return state.layout()
            .trace(bridge.resolved() ? bridge.to() : bridge.from())
            .with(bridge.id());
    }

    private void drawEdges(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!state.passes(bridge)) continue;
            Point from = state.layout()
                .network(bridge.from());
            Point own = state.layout()
                .bridge(bridge.id());
            if (from == null || own == null) continue;
            Point to = bridge.resolved() ? state.layout()
                .network(bridge.to()) : null;
            boolean lit = trace.bridges()
                .contains(bridge.id());
            float alpha = trace.isEmpty() ? (bridge.active() ? 0.34F : 0.13F) : lit ? 1.0F : DIMMED * 0.7F;
            int color = TopologyPalette.withAlpha(
                bridge.kind()
                    .color(),
                alpha);
            float weight = (lit ? 3.2F : bridge.from() == mainId ? 2.0F : 1.3F) * Math.max(0.6F, zoom);

            float ax = screenX(from), ay = screenY(from);
            float bx = screenX(own), by = screenY(own);
            if (lit) haloEdge(ax, ay, bx, by, TRUNK_BEND, weight);
            bundle(ax, ay, bx, by, TRUNK_BEND, weight, color);
            if (to == null) continue;

            float cx = screenX(to), cy = screenY(to);
            if (lit) haloEdge(bx, by, cx, cy, BRANCH_BEND, weight);
            bundle(bx, by, cx, cy, BRANCH_BEND, weight, color);
            if (bridge.active() && (trace.isEmpty() || lit)) drawFlow(bx, by, cx, cy, bridge, lit, zoom);
        }
    }

    private void drawFlow(float x1, float y1, float x2, float y2, TopologyBridge bridge, boolean lit, float zoom) {
        float controlX = control(x1, x2, centerX, BRANCH_BEND);
        float controlY = control(y1, y2, centerY, BRANCH_BEND);
        float progress = (state.animationTime() * 0.6F + bridge.id() * 0.13F) % 1.0F;
        draw.disc(
            TopologyDraw.curvePoint(x1, controlX, x2, progress),
            TopologyDraw.curvePoint(y1, controlY, y2, progress),
            1.8F * Math.max(0.6F, zoom),
            TopologyPalette.withAlpha(0xFFFFFF, lit ? 0.85F : 0.3F));
    }

    private void bundle(float x1, float y1, float x2, float y2, float bend, float weight, int color) {
        draw.curve(
            x1,
            y1,
            control(x1, x2, centerX, bend),
            control(y1, y2, centerY, bend),
            x2,
            y2,
            weight,
            color,
            CURVE_SEGMENTS);
    }

    private void haloEdge(float x1, float y1, float x2, float y2, float bend, float weight) {
        bundle(x1, y1, x2, y2, bend, weight * 2.6F, TopologyPalette.withAlpha(TopologyPalette.ACCENT, 0.14F));
    }

    private static float control(float start, float end, float center, float bend) {
        float middle = (start + end) * 0.5F;
        return middle + (center - middle) * bend;
    }

    private void drawBridges(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!state.passes(bridge)) continue;
            Point own = state.layout()
                .bridge(bridge.id());
            if (own == null) continue;
            boolean lit = trace.bridges()
                .contains(bridge.id());
            boolean focused = state.hoveredBridge() == bridge.id() || state.selectedBridge() == bridge.id();
            float alpha = trace.isEmpty() ? 1.0F : lit ? 1.0F : DIMMED;
            int color = bridge.kind()
                .color();
            float half = (focused ? BRIDGE_RADIUS + 2.1F : BRIDGE_RADIUS) * zoom;
            float x = screenX(own), y = screenY(own);
            if (half < 1.6F) {
                draw.disc(x, y, Math.max(1.0F, half), TopologyPalette.withAlpha(color, alpha));
                continue;
            }
            if (lit || focused) draw.halo(x, y, half, half * 2.6F, color, 0.42F * alpha);
            float top = y - half * 0.72F, bottom = y + half * 0.72F;
            draw.roundedRect(
                x - half,
                top,
                x + half,
                bottom,
                2.4F * zoom,
                TopologyPalette.withAlpha(bridge.active() ? color : TopologyPalette.PANEL_RAISED, alpha));
            draw.roundedOutline(
                x - half,
                top,
                x + half,
                bottom,
                2.4F * zoom,
                Math.max(0.8F, 1.1F * zoom),
                TopologyPalette.withAlpha(color, alpha));
            if (!bridge.resolved()) draw.ring(
                x,
                y,
                half * 1.7F,
                Math.max(0.8F, zoom),
                TopologyPalette.withAlpha(TopologyPalette.BAD, alpha * 0.8F));
        }
    }

    private void drawDevices(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        for (TopologyDevice device : snapshot.devices()) {
            Point point = state.layout()
                .device(device.id());
            Point owner = state.layout()
                .network(device.network());
            if (point == null || owner == null || !state.passes(device)) continue;
            boolean lit = trace.networks()
                .contains(device.network());
            boolean focused = state.hoveredDevice() == device.id() || state.selectedDevice() == device.id();
            float alpha = trace.isEmpty() ? 1.0F : lit ? 1.0F : DIMMED;
            int color = device.faulted() ? device.active() ? TopologyPalette.WARN : TopologyPalette.BAD
                : TopologyPalette.ACCENT;
            float x = screenX(point), y = screenY(point);
            draw.line(
                screenX(owner),
                screenY(owner),
                x,
                y,
                Math.max(0.6F, 0.9F * zoom),
                TopologyPalette.withAlpha(color, alpha * 0.4F));
            float radius = (focused ? DEVICE_RADIUS + 1.5F : DEVICE_RADIUS) * zoom;
            if (focused) draw.halo(x, y, radius, radius * 2.6F, color, 0.5F * alpha);
            draw.disc(x, y, radius, TopologyPalette.withAlpha(TopologyPalette.NODE_SHELL, alpha));
            draw.ring(x, y, radius, Math.max(0.8F, 1.2F * zoom), TopologyPalette.withAlpha(color, alpha));
        }
    }

    private void drawNetworks(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        for (TopologyNetwork network : snapshot.networks()) {
            Point point = state.layout()
                .network(network.id());
            if (point == null) continue;
            boolean lit = trace.networks()
                .contains(network.id());
            boolean focused = state.hoveredNetwork() == network.id();
            boolean selected = state.selectedNetwork() == network.id();
            float alpha = (trace.isEmpty() ? 1.0F : lit ? 1.0F : DIMMED) * (state.passes(network) ? 1.0F : 0.22F);
            float radius = radiusOf(network) * zoom;
            float x = screenX(point), y = screenY(point);
            int outline = TopologyPalette.outline(network);

            if (network.isMain() || lit || focused || selected) {
                float breathe = 1.0F + MathHelper.sin(state.animationTime() * 3.2F) * 0.06F;
                draw.halo(x, y, radius, radius * 3.4F * breathe, outline, 0.3F * alpha);
            }
            draw.disc(x, y, radius, TopologyPalette.withAlpha(TopologyPalette.NODE_SHELL, alpha));
            draw.ring(
                x,
                y,
                radius,
                Math.max(1.0F, (selected ? 2.4F : 1.6F) * zoom),
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

    private void drawLabels(TopologySnapshot snapshot, Trace trace) {
        float zoom = state.camera()
            .zoom();
        if (zoom < LABEL_ZOOM_FLOOR) return;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            for (TopologyNetwork network : snapshot.networks()) {
                Point point = state.layout()
                    .network(network.id());
                if (point == null || !state.passes(network)) continue;
                boolean lit = trace.networks()
                    .contains(network.id());
                float alpha = trace.isEmpty() ? 1.0F : lit ? 1.0F : 0.22F;
                int color = TopologyPalette
                    .withAlpha(network.isMain() ? TopologyPalette.TEXT : TopologyPalette.NODE_IDLE, alpha);
                String label = TopologyViewState.label(network);
                int offset = (int) (radiusOf(network) * zoom);
                GL11.glPushMatrix();
                GL11.glTranslatef(screenX(point), screenY(point), 0);
                if (network.isMain()) {
                    font.drawString(label, -font.getStringWidth(label) / 2, offset + 12, color);
                } else {
                    boolean flip = MathHelper.cos(point.angle) < 0;
                    GL11.glRotatef((float) Math.toDegrees(point.angle) + (flip ? 180 : 0), 0, 0, 1);
                    font.drawString(label, flip ? -offset - 7 - font.getStringWidth(label) : offset + 7, -4, color);
                }
                GL11.glPopMatrix();
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawCenteredNotice(TopologySnapshot snapshot) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String message = !snapshot.bound() ? TopologyViewState.translate("unbound")
            : !snapshot.available() ? TopologyViewState.translate("unavailable")
                : TopologyViewState.translate("scanning");
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        try {
            font.drawString(
                message,
                (int) centerX - font.getStringWidth(message) / 2,
                (int) centerY - 4,
                TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
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
    }

    private void deviceTooltip(RichTooltip tooltip, TopologyDevice device) {
        TopologyNetwork owner = state.snapshot()
            .network(device.network());
        int color = device.faulted() ? device.active() ? TopologyPalette.WARN : TopologyPalette.BAD
            : TopologyPalette.GOOD;
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
        if (mouseButton == 2) {
            fitToView();
            return Result.SUCCESS;
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
        updateHover();
        if (state.hoveredDevice() >= 0) {
            state.selectDevice(state.hoveredDevice());
            Interactable.playButtonClickSound();
            return false;
        }
        if (state.hoveredNetwork() >= 0) {
            state.selectNetwork(state.hoveredNetwork());
            Interactable.playButtonClickSound();
        } else if (state.hoveredBridge() >= 0) {
            state.selectBridge(state.hoveredBridge());
            Interactable.playButtonClickSound();
        }
        return false;
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

    public void updateHover() {
        if (panning) return;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        if (mouseX < 0 || mouseY < 0 || mouseX > getArea().width || mouseY > getArea().height) return;
        if (mouseX > getArea().width - state.consoleVisibleWidth()) return;
        TopologySnapshot snapshot = state.snapshot();
        float zoom = state.camera()
            .zoom();
        for (TopologyDevice device : snapshot.devices()) {
            Point point = state.layout()
                .device(device.id());
            if (point == null || !state.passes(device)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), DEVICE_RADIUS * zoom + 3.0F)) {
                state.hover(-1, -1, device.id());
                return;
            }
        }
        for (TopologyNetwork network : snapshot.networks()) {
            Point point = state.layout()
                .network(network.id());
            if (point == null || !state.passes(network)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), radiusOf(network) * zoom + 5.0F)) {
                state.hover(network.id(), -1);
                return;
            }
        }
        for (TopologyBridge bridge : snapshot.bridges()) {
            Point point = state.layout()
                .bridge(bridge.id());
            if (point == null || !state.passes(bridge)) continue;
            if (within(mouseX, mouseY, screenX(point), screenY(point), BRIDGE_RADIUS * zoom + 4.0F)) {
                state.hover(-1, bridge.id());
                return;
            }
        }
        state.hover(-1, -1, -1);
    }

    public void fitToView() {
        state.camera()
            .fit(
                state.layout()
                    .outerRadius() + MAIN_RADIUS * 2.5F,
                Math.max(64, getArea().width - (int) state.consoleVisibleWidth()),
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

    private static float radiusOf(TopologyNetwork network) {
        return network.depth() == 0 ? MAIN_RADIUS : network.depth() == 1 ? RING_ONE_RADIUS : DEEP_RADIUS;
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
