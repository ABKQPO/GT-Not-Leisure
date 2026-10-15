package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.topology.TopologyContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NetworkTopologyGui extends ModularPanel {

    private static final int SEARCH_HEIGHT = 12;
    private static final int MIN_TOP_BAR_WIDTH = 60;
    private static final int MIN_SEARCH_WIDTH = 60;
    private static final int MAX_SEARCH_WIDTH = 150;

    private final TopologyViewState state;
    private final TopologyGraphWidget graph;
    private final TopologyConsoleWidget console;
    private final TopologyConsoleHandleWidget handle;
    private final TopologyHudWidget hud;
    private final TopologyToolRailWidget rail;
    private final TopologyTopBarWidget topBar;
    private final TextFieldWidget search;
    private final Runnable rescan;

    public NetworkTopologyGui(TopologyContainer container) {
        super("network_topology");
        this.state = container.viewState();
        // The graph paints its own backdrop, so the panel itself stays invisible and full screen.
        fullScreenInvisible();

        this.graph = new TopologyGraphWidget(state);
        this.rescan = () -> {
            container.sync.requestRescan();
            graph.fitToView();
        };
        state.actions(new TopologyViewState.Actions() {

            @Override
            public void rescan() {
                container.sync.requestRescan();
                graph.fitToView();
            }

            @Override
            public void highlight(int networkId, int bridgeId, int deviceId) {
                container.sync.requestHighlight(networkId, bridgeId, deviceId);
            }

            @Override
            public void teleport(int networkId, int bridgeId, int deviceId) {
                container.sync.requestTeleport(networkId, bridgeId, deviceId);
            }
        });

        this.search = searchField();
        this.topBar = new TopologyTopBarWidget(state, rescan);
        this.rail = new TopologyToolRailWidget(state, graph, rescan);
        this.console = new TopologyConsoleWidget(state, graph);
        this.handle = new TopologyConsoleHandleWidget(state);
        this.hud = new TopologyHudWidget(state);

        // Surfaces are ModularUI drawables so they are painted by its own pipeline; the widgets only add detail.
        child(graph.full());
        child(
            console.background(
                new Rectangle().color(TopologyPalette.withAlpha(TopologyPalette.PANEL, 0.97F)),
                new Rectangle().color(TopologyPalette.opaque(TopologyPalette.STROKE))
                    .hollow(1)));
        child(
            rail.background(
                new Rectangle().color(TopologyPalette.withAlpha(TopologyPalette.PANEL, 0.92F))
                    .cornerRadius(4),
                new Rectangle().color(TopologyPalette.opaque(TopologyPalette.STROKE))
                    .cornerRadius(4)
                    .hollow(1)));
        child(topBar);
        child(search);
        child(
            hud.background(
                new Rectangle().color(TopologyPalette.withAlpha(TopologyPalette.PANEL, 0.9F))
                    .cornerRadius(4),
                new Rectangle().color(TopologyPalette.opaque(TopologyPalette.STROKE))
                    .cornerRadius(4)
                    .hollow(1)));
        child(
            handle.background(
                new Rectangle().color(TopologyPalette.withAlpha(TopologyPalette.PANEL_RAISED, 0.96F))
                    .cornerRadius(3),
                new Rectangle().color(TopologyPalette.opaque(TopologyPalette.STROKE))
                    .cornerRadius(3)
                    .hollow(1)));

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        applyMetrics(resolution.getScaledWidth(), resolution.getScaledHeight());
    }

    private void applyMetrics(int screenWidth, int screenHeight) {
        state.screen(screenWidth, screenHeight);
        int edge = state.edge();
        int consoleWidth = state.consoleWidth();
        int hudWidth = state.hudWidth();
        int hudHeight = state.hudHeight(TopologyHudWidget.PREFERRED_HEIGHT);
        int railWidth = rail.slotSize() + 6;
        int railTop = TopologyTopBarWidget.HEIGHT + edge * 2;
        int railHeight = Math.min(rail.preferredHeight(), Math.max(rail.slotSize(), screenHeight - railTop - edge));
        int railColumn = edge + railWidth + edge;
        int topBarWidth = Math.max(MIN_TOP_BAR_WIDTH, screenWidth - railColumn - edge * 2 - hudWidth - consoleWidth);

        console.right(0)
            .fullHeight()
            .width(consoleWidth);
        rail.left(edge)
            .top(railTop)
            .width(railWidth)
            .height(railHeight);
        topBar.left(railColumn)
            .top(edge)
            .width(topBarWidth)
            .height(TopologyTopBarWidget.HEIGHT);
        search.left(railColumn)
            .top(edge + TopologyTopBarWidget.HEIGHT + 2)
            .width(Math.max(MIN_SEARCH_WIDTH, Math.min(topBarWidth, MAX_SEARCH_WIDTH)))
            .height(SEARCH_HEIGHT);
        hud.right(edge)
            .top(edge)
            .width(hudWidth)
            .height(hudHeight);
        handle.right(0)
            .top(edge + hudHeight + edge)
            .width(TopologyConsoleHandleWidget.WIDTH)
            .height(TopologyConsoleHandleWidget.grabHeight(screenHeight));
    }

    private TextFieldWidget searchField() {
        return new TextFieldWidget()
            .background(
                new Rectangle().color(TopologyPalette.withAlpha(TopologyPalette.PANEL_RAISED, 0.95F)),
                new Rectangle().color(TopologyPalette.opaque(TopologyPalette.STROKE))
                    .hollow(1))
            .setTooltipOverride(true)
            .hintText(StatCollector.translateToLocal("gtnl.topology.gui.search_hint"))
            .hintColor(TopologyPalette.opaque(TopologyPalette.TEXT_DIM))
            .setMaxLength(48)
            .setTextColor(TopologyPalette.opaque(TopologyPalette.TEXT))
            .autoUpdateOnChange(true)
            .value(new StringValue.Dynamic(state::query, state::query))
            .padding(2, 0);
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        if (search.isFocused()) return super.onKeyPressed(character, key);
        switch (key) {
            case Keyboard.KEY_F, Keyboard.KEY_SPACE -> graph.fitToView();
            case Keyboard.KEY_TAB -> state.toggleConsole();
            case Keyboard.KEY_R -> rescan.run();
            case Keyboard.KEY_1 -> state.tab(TopologyViewState.ConsoleTab.NODES);
            case Keyboard.KEY_2 -> state.tab(TopologyViewState.ConsoleTab.DEVICES);
            case Keyboard.KEY_3 -> state.tab(TopologyViewState.ConsoleTab.BRIDGES);
            case Keyboard.KEY_P -> state.toggleProblemsOnly();
            case Keyboard.KEY_H -> state.requestHighlight();
            case Keyboard.KEY_T -> state.requestTeleport();
            default -> {
                return super.onKeyPressed(character, key);
            }
        }
        return true;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        // One frame clock for the whole viewer: the panel draws before its children, so every animation shares it.
        state.advanceFrame();
        super.draw(context, widgetTheme);
    }

    public ModularScreen screen() {
        return new ModularScreen(ScienceNotLeisure.MODID, this) {

            @Override
            public void onResize(int width, int height) {
                applyMetrics(width, height);
                super.onResize(width, height);
            }
        };
    }
}
