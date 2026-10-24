package com.science.gtnl.client.topology;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyToolRailWidget extends Widget<TopologyToolRailWidget> implements Interactable {

    private static final int MAX_SLOT = 20;
    private static final int MIN_SLOT = 9;
    private static final int MAX_GAP = 2;
    private static final int MARGIN = 0;
    private static final float CORNER = 3.0F;

    private record Tool(String glyph, String key, Runnable action, BooleanSupplier active) {}

    private final Tool[] tools;

    private final TopologyDraw draw = new TopologyDraw();

    private int slot = MAX_SLOT;
    private int gap = MAX_GAP;

    public TopologyToolRailWidget(TopologyViewState state, TopologyGraphWidget graph, Runnable rescan) {
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
        // The label rides the ModularUI tooltip layer, so it is never painted over by a neighbouring panel.
        tooltipDynamic(this::buildTooltip).tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE)
            .tooltipShowUpTimer(0)
            .tooltipAutoUpdate(true);
    }

    /**
     * Picks the largest slot size that still fits every tool in the given column height, giving up the spacing before
     * the slot size, so no entry is ever pushed out of reach at a high gui scale.
     */
    public void fit(int availableHeight) {
        int room = Math.max(0, availableHeight - MARGIN * 2);
        for (int candidate = MAX_GAP; candidate >= 0; candidate--) {
            int size = (room - candidate * (tools.length - 1)) / tools.length;
            if (size < MIN_SLOT) continue;
            slot = Math.min(size, MAX_SLOT);
            gap = candidate;
            return;
        }
        slot = MIN_SLOT;
        gap = 0;
    }

    public int railWidth() {
        return slot + MARGIN * 2;
    }

    public int railHeight() {
        return MARGIN * 2 + tools.length * slot + gap * (tools.length - 1);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int hovered = indexAt();
        draw.begin();
        for (int index = 0; index < tools.length; index++) {
            boolean active = tools[index].active()
                .getAsBoolean();
            TopologyChrome.pill(draw, MARGIN, slotTop(index), slot, slot, CORNER, index == hovered, active);
        }
        draw.end();
        drawGlyphs(hovered);
    }

    private void drawGlyphs(int hovered) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            for (int index = 0; index < tools.length; index++) {
                boolean active = tools[index].active()
                    .getAsBoolean();
                int color = active ? TopologyPalette.ACCENT
                    : index == hovered ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED;
                TopologyTheme.centeredLabel(
                    tools[index].glyph(),
                    MARGIN + slot / 2,
                    TopologyTheme.captionTop(slotTop(index), slot),
                    TopologyPalette.opaque(color),
                    false);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void buildTooltip(RichTooltip tooltip) {
        int index = indexAt();
        if (index >= 0) tooltip.add(TopologyViewState.translate(tools[index].key()));
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        int index = indexAt();
        if (index < 0) return Result.STOP;
        tools[index].action()
            .run();
        Interactable.playButtonClickSound();
        return Result.SUCCESS;
    }

    private int slotTop(int index) {
        return MARGIN + index * (slot + gap);
    }

    private int indexAt() {
        if (!isBelowMouse()) return -1;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        if (mouseX < MARGIN || mouseX > MARGIN + slot) return -1;
        int step = slot + gap;
        int index = (mouseY - MARGIN) / step;
        if (mouseY < MARGIN || index >= tools.length) return -1;
        return (mouseY - MARGIN) % step < slot ? index : -1;
    }
}
