package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

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

    private static final String THEME = "vanilla";
    private static final int SEARCH_HEIGHT = 12;
    private static final int MIN_TOP_BAR_WIDTH = 48;
    private static final int MIN_SEARCH_WIDTH = 48;
    private static final int MAX_SEARCH_WIDTH = 140;
    private static final int MIN_HUD_HEIGHT = 36;
    private static final int RAIL_GAP = 2;
    private static final int HANDLE_CLEARANCE = 44;

    private final TopologyViewState state;
    private final TopologyGraphWidget graph;
    private final TopologyConsoleWidget console;
    private final TopologyConsoleHandleWidget handle;
    private final TopologyHudWidget hud;
    private final TopologyToolRailWidget rail;
    private final TopologyTopBarWidget topBar;
    private final TopologySurfaceWidget searchInset;
    private final TextFieldWidget search;
    private final Runnable rescan;

    public NetworkTopologyGui(TopologyContainer container) {
        super("network_topology");
        this.state = container.viewState();
        // The whole viewer is one vanilla screen: the panel itself paints the themed background.
        full();

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
            public void highlight(int networkId, int bridgeId, int deviceId, int relayId) {
                container.sync.requestHighlight(networkId, bridgeId, deviceId, relayId);
            }

            @Override
            public void teleport(int networkId, int bridgeId, int deviceId, int relayId) {
                container.sync.requestTeleport(networkId, bridgeId, deviceId, relayId);
            }

            @Override
            public void console(boolean open) {
                container.sync.requestConsole(open);
            }

            @Override
            public void promote(int networkId) {
                container.sync.requestPromote(networkId);
                graph.fitToView();
            }
        });

        this.search = searchField();
        this.topBar = new TopologyTopBarWidget(state, rescan);
        this.rail = new TopologyToolRailWidget(state, graph, rescan);
        this.console = new TopologyConsoleWidget(state, graph);
        this.handle = new TopologyConsoleHandleWidget(state);
        this.hud = new TopologyHudWidget(state);

        this.searchInset = new TopologySurfaceWidget();

        // Child order is the paint order: the full screen canvas first, then every piece of chrome on top of it.
        child(graph);
        child(topBar);
        child(searchInset);
        child(search);
        child(rail);
        child(hud);
        child(console);
        child(handle);

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        applyMetrics(resolution.getScaledWidth(), resolution.getScaledHeight());
    }

    /**
     * Rebuilt from scratch for every screen size, so switching the gui scale while the viewer is open leaves every
     * control on screen and reachable.
     */
    private void applyMetrics(int screenWidth, int screenHeight) {
        state.screen(screenWidth, screenHeight);
        int edge = state.edge();
        int consoleWidth = state.consoleWidth();

        int railTop = edge;
        int railRoom = Math.max(TopologyTopBarWidget.HEIGHT, screenHeight - railTop - edge);
        rail.fit(railRoom);
        int railWidth = rail.railWidth();
        int railHeight = Math.min(rail.railHeight(), railRoom);

        // The status card sits in the top right corner, so the header beside it stops short of the room it needs.
        int hudWidth = state.hudWidth();
        boolean showHud = state.hudVisible();
        int hudHeight = Math
            .max(MIN_HUD_HEIGHT, Math.min(hud.preferredHeight(), screenHeight - edge * 2 - HANDLE_CLEARANCE));

        int contentLeft = edge + railWidth + RAIL_GAP;
        int headerRoom = screenWidth - contentLeft - edge - consoleWidth - (showHud ? hudWidth + RAIL_GAP : 0);
        int topBarWidth = Math.max(MIN_TOP_BAR_WIDTH, headerRoom);
        int searchWidth = Math.min(topBarWidth, MAX_SEARCH_WIDTH);
        boolean showSearch = searchWidth >= MIN_SEARCH_WIDTH;
        int searchTop = edge + TopologyTopBarWidget.HEIGHT + RAIL_GAP;
        int headerHeight = TopologyTopBarWidget.HEIGHT + (showSearch ? RAIL_GAP + SEARCH_HEIGHT : 0);

        // The star map owns the whole screen; every other surface is an opaque card floating on top of it.
        graph.left(0)
            .top(0)
            .width(screenWidth)
            .height(screenHeight);
        topBar.left(contentLeft)
            .top(edge)
            .width(topBarWidth)
            .height(TopologyTopBarWidget.HEIGHT);
        searchInset.setEnabled(showSearch);
        searchInset.left(contentLeft)
            .top(searchTop)
            .width(Math.max(MIN_SEARCH_WIDTH, searchWidth))
            .height(SEARCH_HEIGHT);
        search.setEnabled(showSearch);
        search.left(contentLeft)
            .top(searchTop)
            .width(Math.max(MIN_SEARCH_WIDTH, searchWidth))
            .height(SEARCH_HEIGHT);
        rail.left(edge)
            .top(railTop)
            .width(railWidth)
            .height(railHeight);
        hud.setEnabled(showHud);
        hud.right(edge)
            .top(edge)
            .width(hudWidth)
            .height(hudHeight);
        console.right(0)
            .top(0)
            .width(consoleWidth)
            .height(screenHeight);
        // Anchored to the bottom so it can never collide with the status card above it, whatever the screen size is.
        handle.right(0)
            .bottom(edge)
            .width(TopologyConsoleHandleWidget.WIDTH)
            .height(TopologyConsoleHandleWidget.grabHeight(screenHeight));
        state.chrome(
            new TopologyViewState.Rect(contentLeft, edge, topBarWidth, headerHeight),
            new TopologyViewState.Rect(edge, railTop, railWidth, railHeight));
    }

    private TextFieldWidget searchField() {
        return new TextFieldWidget().setTooltipOverride(true)
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
                return graph.onCanvasKey(key) || super.onKeyPressed(character, key);
            }
        }
        return true;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        // One frame clock for the whole viewer: the panel draws before its children, so every animation shares it.
        state.advanceFrame();
        state.clearHover();
        super.draw(context, widgetTheme);
    }

    public ModularScreen screen() {
        return new ModularScreen(ScienceNotLeisure.MODID, this) {

            @Override
            public void onResize(int width, int height) {
                applyMetrics(width, height);
                super.onResize(width, height);
            }
        }.useTheme(THEME);
    }
}
