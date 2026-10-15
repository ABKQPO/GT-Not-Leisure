package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;

import appeng.api.networking.pathing.ControllerState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyHudWidget extends Widget<TopologyHudWidget> implements Interactable {

    private static final int ROW = 11;
    private static final int METER = 11;
    private static final int PADDING = 11;
    public static final int PREFERRED_HEIGHT = PADDING + 14 + 11 * ROW + 2 * METER + 14;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();

    private int cursor;

    public TopologyHudWidget(TopologyViewState state) {
        this.state = state;
        // Rides the console slide so the card always sits just left of the console, open or collapsed.
        transform((widget, stack) -> stack.translate(-state.consoleVisibleWidth(), 0));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        // The card surface itself is a ModularUI background drawable; only the contents are drawn here.
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            cursor = PADDING - 3;
            font.drawString(
                TopologyViewState.translate("hud.title"),
                PADDING,
                cursor,
                TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
            cursor += 14;
            TopologySnapshot snapshot = state.snapshot();
            TopologyNetwork main = state.main();
            if (main == null) {
                font.drawString(
                    TopologyViewState.translate(snapshot.bound() ? "unavailable" : "unbound"),
                    PADDING,
                    cursor,
                    TopologyPalette.opaque(TopologyPalette.WARN));
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
                TopologyViewState.offline(main) > 0 ? TopologyPalette.WARN : TopologyPalette.TEXT);
            row(
                font,
                width,
                "hud.starving",
                Integer.toString(main.starving()),
                main.starving() > 0 ? TopologyPalette.BAD : TopologyPalette.TEXT);
            row(font, width, "hud.nodes", Integer.toString(main.nodes()), TopologyPalette.TEXT);
            row(font, width, "hud.subnets", Integer.toString(subnets(snapshot)), TopologyPalette.TEXT);
            row(font, width, "hud.bridges", bridgeSummary(snapshot), TopologyPalette.TEXT);
            float charge = main.maxPower() <= 0 ? 0 : (float) (main.storedPower() / main.maxPower());
            row(
                font,
                width,
                "hud.energy",
                TopologyViewState.translate("hud.energy_value", compact(main.storedPower()), compact(main.maxPower())),
                TopologyPalette.TEXT);
            meter(width, charge, TopologyPalette.availability(charge));
            row(
                font,
                width,
                "hud.idle",
                TopologyViewState.translate("hud.idle_value", compact(main.idleDrain())),
                TopologyPalette.TEXT);
            row(font, width, "hud.cpus", Integer.toString(main.craftingCpu()), TopologyPalette.TEXT);
            if (snapshot.truncated()) font.drawString(
                font.trimStringToWidth(TopologyViewState.translate("hud.truncated"), width - PADDING * 2),
                PADDING,
                cursor + 3,
                TopologyPalette.opaque(TopologyPalette.WARN));
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void row(FontRenderer font, int width, String key, String value, int valueColor) {
        // Short cards simply stop drawing rather than spilling past their own border.
        if (cursor + ROW > getArea().height - 2) {
            cursor += ROW;
            return;
        }
        font.drawString(
            TopologyViewState.translate(key),
            PADDING,
            cursor,
            TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
        font.drawString(
            value,
            width - PADDING - font.getStringWidth(value),
            cursor,
            TopologyPalette.opaque(valueColor));
        cursor += ROW;
    }

    private void meter(int width, float ratio, int color) {
        if (cursor + METER > getArea().height - 2) {
            cursor += METER;
            return;
        }
        int barWidth = width - PADDING * 2;
        int top = cursor + 2;
        // The bar is vector geometry, so it has to be emitted in its own batch between two font passes.
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        draw.begin();
        draw.roundedRect(PADDING, top, PADDING + barWidth, top + 4, 2, TopologyPalette.withAlpha(0x222736, 1.0F));
        float filled = Math.max(0, Math.min(1, ratio)) * barWidth;
        if (filled > 1)
            draw.roundedRect(PADDING, top, PADDING + filled, top + 4, 2, TopologyPalette.withAlpha(color, 1.0F));
        draw.end();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        cursor += METER;
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        // The card floats over the canvas; swallowing the click stops a drag from panning the graph underneath.
        return Result.STOP;
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
        return main.controller() == ControllerState.CONTROLLER_ONLINE ? TopologyPalette.GOOD : TopologyPalette.TEXT;
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
