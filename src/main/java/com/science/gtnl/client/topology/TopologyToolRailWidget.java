package com.science.gtnl.client.topology;

import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyToolRailWidget extends Widget<TopologyToolRailWidget> implements Interactable {

    private static final int SLOT_LARGE = 22;
    private static final int SLOT_SMALL = 16;
    private static final int TOP = 5;
    private static final int GLYPH_INSET = 3;

    private record Tool(String glyph, String key, Runnable action, BooleanSupplier active) {}

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();
    private final Tool[] tools;

    public TopologyToolRailWidget(TopologyViewState state, TopologyGraphWidget graph, Runnable rescan) {
        this.state = state;
        this.tools = new Tool[] { new Tool("G", "tool.fit", graph::fitToView, () -> false),
            new Tool("M", "tool.focus_main", () -> {
                if (state.main() == null) return;
                int id = state.main()
                    .id();
                state.selectNetwork(id);
                graph.focusNetwork(id);
            },
                () -> state.main() != null && state.selectedNetwork() == state.main()
                    .id()),
            new Tool(
                "C",
                "tool.devices",
                () -> state.tab(TopologyViewState.ConsoleTab.DEVICES),
                () -> state.tab() == TopologyViewState.ConsoleTab.DEVICES),
            new Tool(
                "B",
                "tool.bridges",
                () -> state.tab(TopologyViewState.ConsoleTab.BRIDGES),
                () -> state.tab() == TopologyViewState.ConsoleTab.BRIDGES),
            new Tool("!", "tool.problems", state::toggleProblemsOnly, state::problemsOnly),
            new Tool("D", "tool.depth", state::cycleDepthLimit, () -> state.depthLimit() != Integer.MAX_VALUE),
            new Tool("R", "tool.rescan", rescan, () -> false),
            new Tool(">", "tool.console", state::toggleConsole, state::consoleOpen) };
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int height = getArea().height;
        int hovered = indexAt(getContext().getAbsMouseY() - getArea().y);
        draw.begin();
        for (int index = 0; index < tools.length; index++) {
            boolean active = tools[index].active()
                .getAsBoolean();
            float top = TOP + index * slotSize();
            if (active || index == hovered) draw.roundedRect(
                GLYPH_INSET,
                top,
                width() - GLYPH_INSET,
                top + slotSize() - 4,
                5,
                TopologyPalette.withAlpha(active ? TopologyPalette.PANEL_SELECTED : TopologyPalette.PANEL_HOVER, 1.0F));
            if (active) draw.roundedRect(
                0,
                top + 4,
                2,
                top + slotSize() - 8,
                1,
                TopologyPalette.withAlpha(TopologyPalette.ACCENT, 1.0F));
        }
        draw.end();
        drawGlyphs(hovered);
    }

    private void drawGlyphs(int hovered) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            for (int index = 0; index < tools.length; index++) {
                Tool tool = tools[index];
                boolean active = tool.active()
                    .getAsBoolean();
                int color = active ? TopologyPalette.ACCENT
                    : index == hovered ? TopologyPalette.TEXT : TopologyPalette.TEXT_DIM;
                int top = TOP + index * slotSize();
                font.drawString(
                    tool.glyph(),
                    (width() - font.getStringWidth(tool.glyph())) / 2,
                    top + (slotSize() - 4 - font.FONT_HEIGHT) / 2 + 1,
                    TopologyPalette.opaque(color));
            }
            if (hovered < 0) return;
            String label = TopologyViewState.translate(tools[hovered].key());
            int labelWidth = font.getStringWidth(label) + 8;
            int top = TOP + hovered * slotSize() + 2;
            draw.begin();
            draw.roundedRect(
                width() + 4,
                top,
                width() + 4 + labelWidth,
                top + 14,
                3,
                TopologyPalette.withAlpha(TopologyPalette.PANEL_RAISED, 0.96F));
            draw.roundedOutline(
                width() + 4,
                top,
                width() + 4 + labelWidth,
                top + 14,
                3,
                1,
                TopologyPalette.withAlpha(TopologyPalette.STROKE, 1.0F));
            draw.end();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            font.drawString(label, width() + 8, top + 3, TopologyPalette.opaque(TopologyPalette.TEXT));
        } finally {
            GL11.glPopAttrib();
        }
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        int index = indexAt(getContext().getAbsMouseY() - getArea().y);
        if (index < 0) return Result.STOP;
        tools[index].action()
            .run();
        Interactable.playButtonClickSound();
        return Result.SUCCESS;
    }

    private int indexAt(int localY) {
        if (!isBelowMouse()) return -1;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        if (mouseX < 0 || mouseX > width()) return -1;
        int index = (localY - TOP) / slotSize();
        if (index < 0 || index >= tools.length) return -1;
        return (localY - TOP) % slotSize() <= slotSize() - 4 ? index : -1;
    }

    public int slotSize() {
        return state.screenHeight() < 230 ? SLOT_SMALL : SLOT_LARGE;
    }

    private int width() {
        return slotSize() + 6;
    }

    public int preferredHeight() {
        return TOP * 2 + tools.length * slotSize() - 4;
    }
}
