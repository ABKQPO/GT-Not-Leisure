package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyLimits;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;

import appeng.api.networking.pathing.ControllerState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * Read-only summary card. It is intentionally not interactive: the canvas fills the whole screen behind it, and a
 * click on the card has to reach the star map rather than being swallowed here.
 */
public class TopologyHudWidget extends Widget<TopologyHudWidget> {

    private static final int ROWS = 11;
    private static final int ROW = 10;
    private static final int METER = 9;
    private static final int TITLE = 12;
    private static final int BAR_HEIGHT = 3;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();

    private int cursor;
    private int textColor;
    private int mutedColor;

    public TopologyHudWidget(TopologyViewState state) {
        this.state = state;
        // Rides the console slide so the card always sits just left of the console, open or collapsed.
        transform((widget, stack) -> stack.translate(-state.consoleVisibleWidth(), 0));
    }

    public int preferredHeight() {
        return padding() * 2 + TITLE + ROWS * ROW + 2 * METER;
    }

    private int padding() {
        return state.compact() ? 4 : 7;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        int padding = padding();
        textColor = TopologyPalette.opaque(TopologyPalette.TEXT);
        mutedColor = TopologyPalette.opaque(TopologyPalette.TEXT_MUTED);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            cursor = padding;
            TopologyTheme.label(TopologyViewState.translate("hud.title"), padding, cursor, mutedColor, false);
            cursor += TITLE;
            TopologySnapshot snapshot = state.snapshot();
            TopologyNetwork main = state.main();
            if (main == null) {
                TopologyTheme.label(
                    font.trimStringToWidth(
                        TopologyViewState.translate(snapshot.bound() ? "unavailable" : "unbound"),
                        width - padding * 2),
                    padding,
                    cursor,
                    TopologyPalette.opaque(TopologyPalette.WARN),
                    false);
                return;
            }
            float availability = main.devices() == 0 ? 1.0F : main.online() / (float) main.devices();
            row(font, width, "hud.controller", controllerLabel(main), controllerColor(main));
            row(
                font,
                width,
                "hud.devices",
                TopologyViewState.translate("hud.devices_value", main.online(), main.devices()),
                TopologyPalette.availability(availability));
            meter(width, availability, TopologyPalette.availability(availability));
            row(
                font,
                width,
                "hud.offline",
                Integer.toString(TopologyViewState.offline(main)),
                TopologyViewState.offline(main) > 0 ? TopologyPalette.WARN : 0);
            row(
                font,
                width,
                "hud.starving",
                Integer.toString(main.starving()),
                main.starving() > 0 ? TopologyPalette.BAD : 0);
            row(font, width, "hud.nodes", Integer.toString(main.nodes()), 0);
            row(font, width, "hud.subnets", Integer.toString(subnets(snapshot)), 0);
            int levels = levels(snapshot);
            row(
                font,
                width,
                "hud.levels",
                TopologyViewState.translate("hud.levels_value", levels, TopologyLimits.MAX_NESTING_LEVELS),
                levels >= TopologyLimits.MAX_NESTING_LEVELS ? TopologyPalette.WARN : 0);
            row(font, width, "hud.bridges", bridgeSummary(snapshot), 0);
            float charge = main.maxPower() <= 0 ? 0 : (float) (main.storedPower() / main.maxPower());
            row(
                font,
                width,
                "hud.energy",
                TopologyViewState.translate("hud.energy_value", compact(main.storedPower()), compact(main.maxPower())),
                0);
            meter(width, charge, TopologyPalette.availability(charge));
            row(font, width, "hud.idle", TopologyViewState.translate("hud.idle_value", compact(main.idleDrain())), 0);
            row(font, width, "hud.cpus", Integer.toString(main.craftingCpu()), 0);
            if (snapshot.truncated() && cursor + ROW <= getArea().height - padding) TopologyTheme.label(
                font.trimStringToWidth(TopologyViewState.translate("hud.truncated"), width - padding * 2),
                padding,
                cursor,
                TopologyPalette.opaque(TopologyPalette.WARN),
                false);
        } finally {
            GL11.glPopAttrib();
        }
    }

    /**
     * A zero accent means the value takes the plain theme text colour; anything else is a semantic health colour.
     */
    private void row(FontRenderer font, int width, String key, String value, int accent) {
        int padding = padding();
        // Short cards simply stop drawing rather than spilling past their own border.
        if (cursor + ROW > getArea().height - padding) {
            cursor += ROW;
            return;
        }
        int room = width - padding * 2 - font.getStringWidth(value) - 4;
        TopologyTheme.label(
            font.trimStringToWidth(TopologyViewState.translate(key), Math.max(8, room)),
            padding,
            cursor,
            mutedColor,
            false);
        TopologyTheme.label(
            value,
            width - padding - font.getStringWidth(value),
            cursor,
            accent == 0 ? textColor : TopologyPalette.opaque(accent),
            false);
        cursor += ROW;
    }

    private void meter(int width, float ratio, int color) {
        int padding = padding();
        if (cursor + METER > getArea().height - padding) {
            cursor += METER;
            return;
        }
        int barWidth = width - padding * 2;
        int top = cursor + 1;
        // The bar is vector geometry, so it has to be emitted in its own batch between two font passes.
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        draw.begin();
        draw.rect(
            padding,
            top,
            padding + barWidth,
            top + BAR_HEIGHT,
            TopologyPalette.withAlpha(TopologyPalette.METER_TRACK, 0.65F));
        float filled = Math.max(0, Math.min(1, ratio)) * barWidth;
        if (filled > 1) draw.rect(padding, top, padding + filled, top + BAR_HEIGHT, TopologyPalette.opaque(color));
        draw.end();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        cursor += METER;
    }

    private static String controllerLabel(TopologyNetwork main) {
        if (main.booting()) return TopologyViewState.translate("hud.state.booting");
        return switch (main.controller()) {
            case CONTROLLER_ONLINE -> TopologyViewState.translate("hud.state.online");
            case CONTROLLER_CONFLICT -> TopologyViewState.translate("hud.state.conflict");
            default -> TopologyViewState.translate("hud.state.ad_hoc");
        };
    }

    private static int controllerColor(TopologyNetwork main) {
        if (main.controller() == ControllerState.CONTROLLER_CONFLICT) return TopologyPalette.BAD;
        if (main.booting()) return TopologyPalette.WARN;
        return main.controller() == ControllerState.CONTROLLER_ONLINE ? TopologyPalette.GOOD : 0;
    }

    /**
     * How deep the discovery actually went, so a system that bumps into the nesting ceiling says so.
     */
    private static int levels(TopologySnapshot snapshot) {
        int deepest = 0;
        for (TopologyNetwork network : snapshot.networks()) deepest = Math.max(deepest, network.depth());
        return snapshot.networks()
            .isEmpty() ? 0 : deepest + 1;
    }

    private static int subnets(TopologySnapshot snapshot) {
        return Math.max(
            0,
            snapshot.networks()
                .size() - 1);
    }

    private static String bridgeSummary(TopologySnapshot snapshot) {
        int unresolved = 0;
        for (TopologyBridge bridge : snapshot.bridges()) {
            if (!bridge.resolved()) unresolved++;
        }
        int total = snapshot.bridges()
            .size();
        return unresolved == 0 ? Integer.toString(total)
            : TopologyViewState.translate("hud.bridges_value", total, unresolved);
    }

    public static String compact(double value) {
        double magnitude = Math.abs(value);
        if (magnitude >= 1.0E9) return String.format("%.2fG", value / 1.0E9);
        if (magnitude >= 1.0E6) return String.format("%.2fM", value / 1.0E6);
        if (magnitude >= 1.0E3) return String.format("%.1fk", value / 1.0E3);
        return String.format("%.0f", value);
    }
}
