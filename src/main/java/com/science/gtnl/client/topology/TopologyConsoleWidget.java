package com.science.gtnl.client.topology;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyLinkKind;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.client.topology.TopologyViewState.ConsoleTab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyConsoleWidget extends Widget<TopologyConsoleWidget> implements Interactable {

    private static final int PADDING = 11;
    private static final int ROW_HEIGHT = 20;
    private static final int ICON_SIZE = 16;
    private static final int TAB_TOP = 26;
    private static final int TAB_HEIGHT = 20;
    private static final int CHIP_HEIGHT = 12;
    private static final int CHIP_GAP = 4;
    private static final int SECTION_GAP = 15;
    private static final int PORT_SIZE = 7;
    private static final int PORT_GAP = 2;
    private static final int MAX_PORTS = 96;
    private static final int LOCATE_HEIGHT = 14;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();
    private final TopologyGraphWidget graph;

    private int scroll;
    private int listTop;
    private int rowsVisible;
    private int chipsTop;
    private int chipRows = 1;
    private int switchesTop;
    private int portsTop;
    private int portsHeight;

    public TopologyConsoleWidget(TopologyViewState state, TopologyGraphWidget graph) {
        this.state = state;
        this.graph = graph;
        transform((widget, stack) -> stack.translate(state.consoleWidth() * (1.0F - state.consoleSlide()), 0));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        int height = getArea().height;
        layout(width, height);
        updateRowHover();

        draw.begin();
        drawLocateButton(width);
        drawTabs(width);
        drawChips(width);
        drawSwitches(width);
        drawPorts(width);
        drawRowBackgrounds(width);
        draw.end();
        drawRowIcons();
        drawLabels(width, height);
    }

    private void layout(int width, int height) {
        chipsTop = PADDING + TAB_TOP + TAB_HEIGHT + SECTION_GAP;
        chipRows = countChipRows(width);
        switchesTop = chipsTop + chipRows * (CHIP_HEIGHT + CHIP_GAP) + SECTION_GAP;
        portsTop = switchesTop + CHIP_HEIGHT + SECTION_GAP + 6;
        int ports = Math.min(MAX_PORTS, Math.max(16, portCount()));
        portsHeight = ((ports + portsPerRow(width) - 1) / portsPerRow(width)) * (PORT_SIZE + PORT_GAP) + 8;
        listTop = portsTop + portsHeight + SECTION_GAP + 6;
        rowsVisible = Math.max(0, (height - listTop - PADDING) / ROW_HEIGHT);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, rowCount() - rowsVisible)));
    }

    private interface ChipVisitor {

        void accept(TopologyLinkKind kind, int x, int y, int chipWidth);
    }

    private void forEachChip(int width, ChipVisitor visitor) {
        int x = PADDING, y = chipsTop;
        for (TopologyLinkKind kind : TopologyLinkKind.values()) {
            int chipWidth = chipWidth(kind);
            if (x > PADDING && x + chipWidth > width - PADDING) {
                x = PADDING;
                y += CHIP_HEIGHT + CHIP_GAP;
            }
            visitor.accept(kind, x, y, chipWidth);
            x += chipWidth + CHIP_GAP;
        }
    }

    private int countChipRows(int width) {
        int[] bottom = { chipsTop };
        forEachChip(width, (kind, x, y, chipWidth) -> bottom[0] = Math.max(bottom[0], y));
        return (bottom[0] - chipsTop) / (CHIP_HEIGHT + CHIP_GAP) + 1;
    }

    private static int portsPerRow(int width) {
        return Math.max(1, (width - PADDING * 2 - 6) / (PORT_SIZE + PORT_GAP));
    }

    private void drawTabs(int width) {
        int y = PADDING + TAB_TOP;
        draw.rect(
            PADDING,
            y + TAB_HEIGHT - 1,
            width - PADDING,
            y + TAB_HEIGHT,
            TopologyPalette.opaque(TopologyPalette.STROKE));
        int x = PADDING;
        for (ConsoleTab tab : ConsoleTab.values()) {
            int tabWidth = tabWidth(tab);
            if (state.tab() == tab) {
                draw.rect(
                    x,
                    y + TAB_HEIGHT - 2,
                    x + tabWidth,
                    y + TAB_HEIGHT,
                    TopologyPalette.opaque(TopologyPalette.ACCENT));
            }
            x += tabWidth + 2;
        }
    }

    private void drawChips(int width) {
        forEachChip(width, (kind, x, y, chipWidth) -> {
            boolean on = state.kindVisible(kind);
            draw.roundedRect(
                x,
                y,
                x + chipWidth,
                y + CHIP_HEIGHT,
                CHIP_HEIGHT * 0.5F,
                TopologyPalette.withAlpha(TopologyPalette.PANEL_RAISED, on ? 1.0F : 0.4F));
            draw.roundedOutline(
                x,
                y,
                x + chipWidth,
                y + CHIP_HEIGHT,
                CHIP_HEIGHT * 0.5F,
                1,
                TopologyPalette.withAlpha(TopologyPalette.STROKE, on ? 1.0F : 0.4F));
            draw.disc(x + 7, y + CHIP_HEIGHT * 0.5F, 3, TopologyPalette.withAlpha(kind.color(), on ? 1.0F : 0.3F));
        });
    }

    private void drawSwitches(int width) {
        int x = PADDING;
        for (int index = 0; index < 3; index++) {
            int switchWidth = switchWidth(index);
            boolean on = switchState(index);
            draw.roundedRect(
                x,
                switchesTop,
                x + switchWidth,
                switchesTop + CHIP_HEIGHT,
                CHIP_HEIGHT * 0.5F,
                TopologyPalette.withAlpha(on ? TopologyPalette.PANEL_SELECTED : TopologyPalette.PANEL_RAISED, 1.0F));
            draw.roundedOutline(
                x,
                switchesTop,
                x + switchWidth,
                switchesTop + CHIP_HEIGHT,
                CHIP_HEIGHT * 0.5F,
                1,
                TopologyPalette.withAlpha(on ? TopologyPalette.ACCENT : TopologyPalette.STROKE, on ? 0.8F : 1.0F));
            x += switchWidth + 4;
            if (x > width - PADDING) break;
        }
    }

    private void drawPorts(int width) {
        draw.roundedRect(
            PADDING,
            portsTop,
            width - PADDING,
            portsTop + portsHeight,
            4,
            TopologyPalette.withAlpha(TopologyPalette.PANEL_RAISED, 1.0F));
        draw.roundedOutline(
            PADDING,
            portsTop,
            width - PADDING,
            portsTop + portsHeight,
            4,
            1,
            TopologyPalette.withAlpha(TopologyPalette.STROKE, 1.0F));
        TopologyNetwork network = selectedNetwork();
        if (network == null) return;
        int total = Math.min(MAX_PORTS, network.devices());
        int starving = Math.min(total, network.starving());
        int offline = Math.min(total - starving, Math.max(0, network.devices() - network.online() - starving));
        int perRow = Math.max(1, (width - PADDING * 2 - 6) / (PORT_SIZE + PORT_GAP));
        for (int index = 0; index < total; index++) {
            int column = index % perRow, row = index / perRow;
            float x = PADDING + 4 + column * (PORT_SIZE + PORT_GAP);
            float y = portsTop + 4 + row * (PORT_SIZE + PORT_GAP);
            int color = index < starving ? TopologyPalette.BAD
                : index < starving + offline ? TopologyPalette.TEXT_DIM : TopologyPalette.ACCENT;
            draw.roundedRect(x, y, x + PORT_SIZE, y + PORT_SIZE, 1.5F, TopologyPalette.withAlpha(color, 0.9F));
        }
    }

    private void drawRowBackgrounds(int width) {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        for (int index = 0; index < count; index++) {
            int y = listTop + index * ROW_HEIGHT;
            boolean hovered = isRowHovered(index);
            boolean selected = isRowSelected(scroll + index);
            if (selected) draw.rect(
                PADDING - 3,
                y - 1,
                width - PADDING + 3,
                y + ROW_HEIGHT - 1,
                TopologyPalette.withAlpha(TopologyPalette.PANEL_SELECTED, 1.0F));
            else if (hovered) draw.rect(
                PADDING - 3,
                y - 1,
                width - PADDING + 3,
                y + ROW_HEIGHT - 1,
                TopologyPalette.withAlpha(TopologyPalette.PANEL_HOVER, 1.0F));
            draw.rect(PADDING, y - 1, width - PADDING, y, TopologyPalette.withAlpha(TopologyPalette.DIVIDER, 1.0F));
        }
    }

    private void drawLabels(int width, int height) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            drawHeader(font, width);
            if (!selectedCoordinates().isEmpty()) {
                String locate = TopologyViewState.translate("locate");
                font.drawString(
                    locate,
                    width - PADDING - locateWidth() + (locateWidth() - font.getStringWidth(locate)) / 2,
                    PADDING,
                    TopologyPalette.opaque(isOverLocate() ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED));
            }
            int x = PADDING;
            for (ConsoleTab tab : ConsoleTab.values()) {
                int tabWidth = tabWidth(tab);
                font.drawString(
                    TopologyViewState.translate("tab." + tab.key()),
                    x + 5,
                    PADDING + 32,
                    TopologyPalette.opaque(state.tab() == tab ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED));
                x += tabWidth + 2;
            }
            section(font, chipsTop - 11, "section.kinds");
            forEachChip(
                width,
                (kind, chipX, chipY, chipWidth) -> font.drawString(
                    TopologyViewState.kindLabel(kind),
                    chipX + 13,
                    chipY + 3,
                    TopologyPalette.opaque(state.kindVisible(kind) ? TopologyPalette.TEXT : TopologyPalette.TEXT_DIM)));
            section(font, switchesTop - 11, "section.filter");
            x = PADDING;
            for (int index = 0; index < 3; index++) {
                int switchWidth = switchWidth(index);
                font.drawString(
                    switchLabel(index),
                    x + 6,
                    switchesTop + 3,
                    TopologyPalette.opaque(switchState(index) ? TopologyPalette.ACCENT : TopologyPalette.TEXT_MUTED));
                x += switchWidth + 4;
                if (x > width - PADDING) break;
            }
            section(font, portsTop - 11, "section.channels");
            section(font, listTop - 11, switch (state.tab()) {
                case BRIDGES -> "section.bridges";
                case DEVICES -> "section.devices";
                default -> "section.networks";
            });
            drawRows(font, width);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawLocateButton(int width) {
        if (selectedCoordinates().isEmpty()) return;
        int buttonWidth = locateWidth();
        boolean hovered = isOverLocate();
        float left = width - PADDING - buttonWidth;
        draw.roundedRect(
            left,
            PADDING - 3,
            left + buttonWidth,
            PADDING - 3 + LOCATE_HEIGHT,
            LOCATE_HEIGHT * 0.5F,
            TopologyPalette.withAlpha(hovered ? TopologyPalette.PANEL_SELECTED : TopologyPalette.PANEL_RAISED, 1.0F));
        draw.roundedOutline(
            left,
            PADDING - 3,
            left + buttonWidth,
            PADDING - 3 + LOCATE_HEIGHT,
            LOCATE_HEIGHT * 0.5F,
            1,
            TopologyPalette.withAlpha(hovered ? TopologyPalette.ACCENT_DEEP : TopologyPalette.STROKE, 1.0F));
    }

    private boolean handleLocate(int mouseX, int mouseY) {
        if (!isOverLocate()) return false;
        String coordinates = selectedCoordinates();
        if (coordinates.isEmpty()) return false;
        GuiScreen.setClipboardString(coordinates);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.topology.gui.located"));
        return true;
    }

    private boolean isOverLocate() {
        if (!isBelowMouse() || selectedCoordinates().isEmpty()) return false;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        return mouseX >= getArea().width - PADDING - locateWidth() && mouseX <= getArea().width - PADDING
            && mouseY >= PADDING - 3
            && mouseY <= PADDING - 3 + LOCATE_HEIGHT;
    }

    private static int locateWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.translate("locate")) + 14;
    }

    private String selectedCoordinates() {
        TopologyBridge bridge = state.snapshot()
            .bridge(state.selectedBridge());
        if (bridge != null) return TopologyViewState.coordinates(bridge.origin());
        TopologyNetwork network = selectedNetwork();
        return network == null ? "" : TopologyViewState.coordinates(network.pivot());
    }

    private void drawHeader(FontRenderer font, int width) {
        TopologyBridge bridge = state.snapshot()
            .bridge(state.selectedBridge());
        TopologyNetwork network = selectedNetwork();
        String title;
        int color;
        if (bridge != null) {
            title = TopologyViewState.kindLabel(bridge.kind());
            color = bridge.kind()
                .color();
        } else if (network != null) {
            title = TopologyViewState.label(network);
            color = TopologyPalette.outline(network);
        } else {
            title = TopologyViewState.translate("no_selection");
            color = TopologyPalette.TEXT_MUTED;
        }
        int titleRoom = width - PADDING * 2 - (selectedCoordinates().isEmpty() ? 0 : locateWidth() + 6);
        font.drawString(
            font.trimStringToWidth(title, Math.max(16, titleRoom)),
            PADDING,
            PADDING,
            TopologyPalette.opaque(color));
        String subtitle = bridge != null ? TopologyViewState.coordinates(bridge.origin())
            : network != null ? TopologyViewState.coordinates(network.pivot()) : "";
        font.drawString(
            font.trimStringToWidth(subtitle, width - PADDING * 2),
            PADDING,
            PADDING + 12,
            TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
    }

    private void drawRows(FontRenderer font, int width) {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        if (count == 0) {
            font.drawString(
                TopologyViewState.translate("no_matches"),
                PADDING,
                listTop + 4,
                TopologyPalette.opaque(TopologyPalette.TEXT_DIM));
            return;
        }
        if (state.tab() == ConsoleTab.BRIDGES) {
            List<TopologyBridge> bridges = state.filteredBridges();
            for (int index = 0; index < count; index++) {
                TopologyBridge bridge = bridges.get(scroll + index);
                int top = listTop + index * ROW_HEIGHT;
                int state1 = bridge.resolved() ? bridge.active() ? TopologyPalette.GOOD : TopologyPalette.WARN
                    : TopologyPalette.BAD;
                dot(font, top + 2, state1);
                row(
                    font,
                    width,
                    top,
                    TopologyViewState.kindLabel(bridge.kind()),
                    TopologyViewState.location(bridge.origin()),
                    bridge.kind()
                        .color(),
                    bridge.resolved() ? "" : TopologyViewState.translate("row.unresolved"));
            }
            return;
        }
        if (state.tab() == ConsoleTab.DEVICES) {
            List<TopologyDevice> devices = state.filteredDevices();
            for (int index = 0; index < count; index++) {
                TopologyDevice device = devices.get(scroll + index);
                int top = listTop + index * ROW_HEIGHT;
                dot(
                    font,
                    top + 2,
                    device.faulted() ? device.active() ? TopologyPalette.WARN : TopologyPalette.BAD
                        : TopologyPalette.GOOD);
                row(
                    font,
                    width,
                    top,
                    TopologyViewState.deviceName(device),
                    TopologyViewState.location(device.position()),
                    TopologyPalette.TEXT,
                    TopologyViewState.translate(
                        device.channelOk() ? device.active() ? "row.device_ok" : "row.device_offline"
                            : "row.device_no_channel"));
            }
            return;
        }
        List<TopologyNetwork> networks = state.filteredNetworks();
        for (int index = 0; index < count; index++) {
            TopologyNetwork network = networks.get(scroll + index);
            int top = listTop + index * ROW_HEIGHT;
            dot(font, top + 2, TopologyPalette.health(network));
            row(
                font,
                width,
                top,
                TopologyViewState.label(network),
                TopologyViewState.location(network.pivot()),
                network.isMain() ? TopologyPalette.ACCENT : TopologyPalette.TEXT,
                TopologyViewState.translate("row.depth", network.depth()));
        }
    }

    private void row(FontRenderer font, int width, int top, String title, String detail, int titleColor, String badge) {
        int textLeft = PADDING + 9 + ICON_SIZE + 3;
        int badgeWidth = badge.isEmpty() ? 0 : font.getStringWidth(badge) + 6;
        int room = Math.max(16, width - PADDING - textLeft - badgeWidth);
        font.drawString(font.trimStringToWidth(title, room), textLeft, top + 2, TopologyPalette.opaque(titleColor));
        font.drawString(
            font.trimStringToWidth(detail, room),
            textLeft,
            top + 11,
            TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
        if (!badge.isEmpty()) font.drawString(
            badge,
            width - PADDING - font.getStringWidth(badge),
            top + 6,
            TopologyPalette.opaque(TopologyPalette.TEXT_DIM));
    }

    private void drawRowIcons() {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        if (count == 0) return;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ConsoleTab tab = state.tab();
            List<TopologyBridge> bridgeRows = tab == ConsoleTab.BRIDGES ? state.filteredBridges() : List.of();
            List<TopologyDevice> deviceRows = tab == ConsoleTab.DEVICES ? state.filteredDevices() : List.of();
            List<TopologyNetwork> networkRows = tab == ConsoleTab.NODES ? state.filteredNetworks() : List.of();
            for (int index = 0; index < count; index++) {
                ItemStack icon = switch (tab) {
                    case BRIDGES -> bridgeRows.get(scroll + index)
                        .icon();
                    case DEVICES -> deviceRows.get(scroll + index)
                        .icon();
                    default -> networkRows.get(scroll + index)
                        .icon();
                };
                if (icon == null) continue;
                GuiDraw.drawItem(
                    icon,
                    PADDING + 9,
                    listTop + index * ROW_HEIGHT + 2,
                    ICON_SIZE,
                    ICON_SIZE,
                    getContext().getCurrentDrawingZ());
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void dot(FontRenderer font, int y, int color) {
        font.drawString("●", PADDING, y + 4, TopologyPalette.opaque(color));
    }

    private void section(FontRenderer font, int y, String key) {
        font.drawString(
            TopologyViewState.translate(key),
            PADDING,
            y,
            TopologyPalette.opaque(TopologyPalette.TEXT_MUTED));
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        if (handleLocate(mouseX, mouseY) || handleTabs(mouseX, mouseY)
            || handleChips(mouseX, mouseY)
            || handleSwitches(mouseX, mouseY)) {
            Interactable.playButtonClickSound();
            return Result.SUCCESS;
        }
        if (handleRows(mouseY)) {
            Interactable.playButtonClickSound();
            return Result.SUCCESS;
        }
        return Result.STOP;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        scroll = Math.max(0, Math.min(scroll - direction.modifier * 3, Math.max(0, rowCount() - rowsVisible)));
        return true;
    }

    private boolean handleTabs(int mouseX, int mouseY) {
        int y = PADDING + TAB_TOP;
        if (mouseY < y || mouseY > y + TAB_HEIGHT) return false;
        int x = PADDING;
        for (ConsoleTab tab : ConsoleTab.values()) {
            int tabWidth = tabWidth(tab);
            if (mouseX >= x && mouseX <= x + tabWidth) {
                state.tab(tab);
                scroll = 0;
                return true;
            }
            x += tabWidth + 2;
        }
        return false;
    }

    private boolean handleChips(int mouseX, int mouseY) {
        TopologyLinkKind[] hit = { null };
        forEachChip(
            getArea().width,
            (kind, x, y, chipWidth) -> {
                if (mouseX >= x && mouseX <= x + chipWidth && mouseY >= y && mouseY <= y + CHIP_HEIGHT) hit[0] = kind;
            });
        if (hit[0] == null) return false;
        state.toggleKind(hit[0]);
        return true;
    }

    private boolean handleSwitches(int mouseX, int mouseY) {
        if (mouseY < switchesTop || mouseY > switchesTop + CHIP_HEIGHT) return false;
        int x = PADDING;
        for (int index = 0; index < 3; index++) {
            int switchWidth = switchWidth(index);
            if (mouseX >= x && mouseX <= x + switchWidth) {
                toggleSwitch(index);
                return true;
            }
            x += switchWidth + 4;
        }
        return false;
    }

    private boolean handleRows(int mouseY) {
        int index = rowIndexAt(mouseY);
        if (index < 0) return false;
        if (state.tab() == ConsoleTab.DEVICES) {
            List<TopologyDevice> devices = state.filteredDevices();
            if (index >= devices.size()) return false;
            state.selectDevice(
                devices.get(index)
                    .id());
            return true;
        }
        if (state.tab() == ConsoleTab.BRIDGES) {
            List<TopologyBridge> bridges = state.filteredBridges();
            if (index >= bridges.size()) return false;
            state.selectBridge(
                bridges.get(index)
                    .id());
            return true;
        }
        List<TopologyNetwork> networks = state.filteredNetworks();
        if (index >= networks.size()) return false;
        int id = networks.get(index)
            .id();
        state.selectNetwork(id);
        graph.focusNetwork(id);
        return true;
    }

    private void updateRowHover() {
        if (!isBelowMouse() || !state.consoleOpen()) return;
        int index = rowIndexAt(getContext().getAbsMouseY() - getArea().y);
        if (index < 0) {
            state.hover(-1, -1);
            return;
        }
        if (state.tab() == ConsoleTab.DEVICES) {
            List<TopologyDevice> devices = state.filteredDevices();
            state.hover(
                -1,
                -1,
                index < devices.size() ? devices.get(index)
                    .id() : -1);
            return;
        }
        if (state.tab() == ConsoleTab.BRIDGES) {
            List<TopologyBridge> bridges = state.filteredBridges();
            state.hover(
                -1,
                index < bridges.size() ? bridges.get(index)
                    .id() : -1);
            return;
        }
        List<TopologyNetwork> networks = state.filteredNetworks();
        state.hover(
            index < networks.size() ? networks.get(index)
                .id() : -1,
            -1);
    }

    private int rowIndexAt(int mouseY) {
        if (mouseY < listTop || rowsVisible <= 0) return -1;
        int row = (mouseY - listTop) / ROW_HEIGHT;
        return row < 0 || row >= rowsVisible ? -1 : scroll + row;
    }

    private boolean isRowHovered(int visibleIndex) {
        if (!isBelowMouse()) return false;
        return rowIndexAt(getContext().getAbsMouseY() - getArea().y) == scroll + visibleIndex;
    }

    private boolean isRowSelected(int index) {
        if (state.tab() == ConsoleTab.DEVICES) {
            List<TopologyDevice> devices = state.filteredDevices();
            return index < devices.size() && devices.get(index)
                .id() == state.selectedDevice();
        }
        if (state.tab() == ConsoleTab.BRIDGES) {
            List<TopologyBridge> bridges = state.filteredBridges();
            return index < bridges.size() && bridges.get(index)
                .id() == state.selectedBridge();
        }
        List<TopologyNetwork> networks = state.filteredNetworks();
        return index < networks.size() && networks.get(index)
            .id() == state.selectedNetwork();
    }

    private int rowCount() {
        return switch (state.tab()) {
            case BRIDGES -> state.filteredBridges()
                .size();
            case DEVICES -> state.filteredDevices()
                .size();
            default -> state.filteredNetworks()
                .size();
        };
    }

    private int portCount() {
        TopologyNetwork network = selectedNetwork();
        return network == null ? 0 : network.devices();
    }

    private TopologyNetwork selectedNetwork() {
        TopologyBridge bridge = state.snapshot()
            .bridge(state.selectedBridge());
        if (bridge != null) return state.snapshot()
            .network(bridge.resolved() ? bridge.to() : bridge.from());
        TopologyNetwork selected = state.snapshot()
            .network(state.selectedNetwork());
        return selected != null ? selected : state.main();
    }

    private boolean switchState(int index) {
        return switch (index) {
            case 0 -> state.problemsOnly();
            case 1 -> state.showUnresolved();
            default -> state.depthLimit() != Integer.MAX_VALUE;
        };
    }

    private void toggleSwitch(int index) {
        switch (index) {
            case 0 -> state.toggleProblemsOnly();
            case 1 -> state.toggleShowUnresolved();
            default -> state.cycleDepthLimit();
        }
    }

    private String switchLabel(int index) {
        return switch (index) {
            case 0 -> TopologyViewState.translate("filter.problems");
            case 1 -> TopologyViewState.translate("filter.unresolved");
            default -> state.depthLimit() == Integer.MAX_VALUE ? TopologyViewState.translate("filter.depth_all")
                : TopologyViewState.translate("filter.depth", state.depthLimit());
        };
    }

    private static int tabWidth(ConsoleTab tab) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.translate("tab." + tab.key()))
            + 10;
    }

    private static int chipWidth(TopologyLinkKind kind) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.kindLabel(kind)) + 19;
    }

    private int switchWidth(int index) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(switchLabel(index)) + 12;
    }
}
