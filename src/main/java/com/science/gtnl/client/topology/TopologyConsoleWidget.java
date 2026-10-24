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
import com.cleanroommc.modularui.screen.RichTooltip;
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

    private static final int SWITCH_COUNT = 3;
    private static final int HEADER_HEIGHT = 21;
    private static final int DENSE_HEADER_HEIGHT = 11;
    private static final int DENSE_ROW_HEIGHT = 11;
    private static final int ROW_HEIGHT = 20;
    private static final int DETAIL_ROW_FLOOR = 18;
    private static final int TAB_HEIGHT = 13;
    private static final int CHIP_HEIGHT = 12;
    private static final int CHIP_GAP = 2;
    private static final int LABEL_HEIGHT = 9;
    private static final int SECTION_GAP = 4;
    private static final int LOCATE_HEIGHT = 12;
    private static final int PORT_SIZE = 6;
    private static final int PORT_GAP = 1;
    private static final int MAX_PORT_ROWS = 6;
    private static final int MIN_LIST_ROWS = 3;
    private static final int SCROLL_STEP = 3;
    private static final int SELECTION_KEY_STRIDE = 1 << 20;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();
    private final TopologyGraphWidget graph;

    private int scroll;
    private int revealedKey = Integer.MIN_VALUE;
    private boolean dense;
    private int padding;
    private int labelHeight;
    private int rowHeight;
    private int listTop;
    private int rowsVisible;
    private int tabsTop;
    private int chipsTop;
    private int chipRows = 1;
    private int switchesTop;
    private int portsTop;
    private int portsHeight;

    public TopologyConsoleWidget(TopologyViewState state, TopologyGraphWidget graph) {
        this.state = state;
        this.graph = graph;
        transform((widget, stack) -> stack.translate(state.consoleWidth() * (1.0F - state.consoleSlide()), 0));
        tooltipDynamic(this::buildTooltip).tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE)
            .tooltipShowUpTimer(0)
            .tooltipAutoUpdate(true);
    }

    /**
     * Names whatever control is under the cursor. It is what keeps the glyph-only chips and switches readable once the
     * column collapses to its dense layout.
     */
    private void buildTooltip(RichTooltip tooltip) {
        int mouseX = localMouseX();
        int mouseY = localMouseY();
        TopologyLinkKind kind = chipAt(mouseX, mouseY);
        if (kind != null) {
            tooltip.add(TopologyViewState.kindLabel(kind));
            return;
        }
        int index = switchAt(mouseX, mouseY);
        if (index >= 0) tooltip.add(switchLabel(index));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        int height = getArea().height;
        layout(width, height);
        updateRowHover();

        TopologyTheme.drawPanel(context, 0, 0, width, height);
        drawLocateButton(context, width);
        drawTabs(context, width);
        drawChips(context, width);
        drawSwitches(context, width);
        // Every vector detail shares one batch, so the whole console costs a single geometry draw call.
        draw.begin();
        drawChipSwatches(width);
        drawPorts(width);
        drawRowBackgrounds(context, width);
        drawRowDots();
        draw.end();
        drawRowIcons();
        drawLabels(context, width);
    }

    /**
     * At a high gui scale this column is only a handful of text lines wide and tall, so the chips lose their labels,
     * the section captions disappear and the channel strip is dropped entirely. Every control stays present and
     * clickable; only the decoration gives way.
     */
    private void layout(int width, int height) {
        dense = state.compact();
        padding = dense ? 4 : 8;
        labelHeight = dense ? 0 : LABEL_HEIGHT;
        rowHeight = dense ? DENSE_ROW_HEIGHT : ROW_HEIGHT;
        tabsTop = padding + (dense ? DENSE_HEADER_HEIGHT : HEADER_HEIGHT);
        chipsTop = tabsTop + TAB_HEIGHT + SECTION_GAP + labelHeight;
        chipRows = countChipRows(width);
        switchesTop = chipsTop + chipRows * (CHIP_HEIGHT + CHIP_GAP) + SECTION_GAP + labelHeight;
        portsTop = switchesTop + CHIP_HEIGHT + SECTION_GAP + labelHeight;
        portsHeight = portsHeight(width, height);
        listTop = portsTop + portsHeight + (portsHeight == 0 ? 0 : SECTION_GAP) + labelHeight;
        rowsVisible = Math.max(0, (height - listTop - padding) / rowHeight);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, rowCount() - rowsVisible)));
        revealSelection();
    }

    /**
     * Brings a freshly picked row into view once, so selecting something on the canvas does not leave the list parked
     * somewhere else. Later scrolling by hand is never overridden.
     */
    private void revealSelection() {
        int key = selectionKey();
        if (key == revealedKey) return;
        revealedKey = key;
        if (rowsVisible <= 0) return;
        int index = selectedRow();
        if (index < 0) return;
        if (index < scroll) scroll = index;
        else if (index >= scroll + rowsVisible) scroll = index - rowsVisible + 1;
    }

    private int selectionKey() {
        int id = switch (state.tab()) {
            case BRIDGES -> state.selectedBridge();
            case DEVICES -> state.selectedDevice();
            default -> state.selectedNetwork();
        };
        return state.tab()
            .ordinal() * SELECTION_KEY_STRIDE + id;
    }

    private int selectedRow() {
        int count = rowCount();
        for (int index = 0; index < count; index++) {
            if (isRowSelected(index)) return index;
        }
        return -1;
    }

    /**
     * The channel strip is dropped whole whenever keeping it would starve the list below the minimum useful number of
     * rows.
     */
    private int portsHeight(int width, int height) {
        int perRow = portsPerRow(width);
        int ports = Math.min(MAX_PORT_ROWS * perRow, Math.max(perRow, portCount()));
        int wanted = (ports + perRow - 1) / perRow * (PORT_SIZE + PORT_GAP) + 4;
        int listRoom = height - (portsTop + wanted + SECTION_GAP + labelHeight) - padding;
        return listRoom >= MIN_LIST_ROWS * rowHeight ? wanted : 0;
    }

    private interface ChipVisitor {

        void accept(TopologyLinkKind kind, int x, int y, int chipWidth);
    }

    private void forEachChip(int width, ChipVisitor visitor) {
        int x = padding, y = chipsTop;
        for (TopologyLinkKind kind : TopologyLinkKind.values()) {
            int chipWidth = dense ? CHIP_HEIGHT : Math.min(chipWidth(kind), Math.max(CHIP_HEIGHT, width - padding * 2));
            if (x > padding && x + chipWidth > width - padding) {
                x = padding;
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

    private int portsPerRow(int width) {
        return Math.max(1, (width - padding * 2 - 4) / (PORT_SIZE + PORT_GAP));
    }

    private void drawTabs(ModularGuiContext context, int width) {
        int x = padding;
        for (ConsoleTab tab : ConsoleTab.values()) {
            int tabWidth = tabWidth(tab);
            if (x + tabWidth > width - padding) break;
            TopologyTheme.drawButton(context, x, tabsTop, tabWidth, TAB_HEIGHT, isOverTab(tab), state.tab() == tab);
            x += tabWidth + CHIP_GAP;
        }
    }

    private void drawChips(ModularGuiContext context, int width) {
        forEachChip(
            width,
            (kind, x, y, chipWidth) -> TopologyTheme
                .drawButton(context, x, y, chipWidth, CHIP_HEIGHT, isOverChip(kind), state.kindVisible(kind)));
    }

    private void drawChipSwatches(int width) {
        forEachChip(
            width,
            (kind, x, y, chipWidth) -> draw.disc(
                dense ? x + chipWidth * 0.5F : x + 6,
                y + CHIP_HEIGHT * 0.5F,
                2.5F,
                TopologyPalette.withAlpha(kind.color(), state.kindVisible(kind) ? 1.0F : 0.35F)));
    }

    private void drawSwitches(ModularGuiContext context, int width) {
        int x = padding;
        for (int index = 0; index < SWITCH_COUNT; index++) {
            int switchWidth = switchWidth(index);
            if (x + switchWidth > width - padding) break;
            TopologyTheme
                .drawButton(context, x, switchesTop, switchWidth, CHIP_HEIGHT, isOverSwitch(index), switchState(index));
            x += switchWidth + CHIP_GAP;
        }
    }

    private void drawPorts(int width) {
        if (portsHeight == 0) return;
        draw.rect(
            padding,
            portsTop,
            width - padding,
            portsTop + portsHeight,
            TopologyPalette.withAlpha(TopologyPalette.METER_TRACK, 0.55F));
        TopologyNetwork network = selectedNetwork();
        if (network == null) return;
        int perRow = portsPerRow(width);
        int total = Math.min(MAX_PORT_ROWS * perRow, network.devices());
        int starving = Math.min(total, network.starving());
        int offline = Math.min(total - starving, Math.max(0, network.devices() - network.online() - starving));
        for (int index = 0; index < total; index++) {
            int column = index % perRow, row = index / perRow;
            float x = padding + 2 + column * (PORT_SIZE + PORT_GAP);
            float y = portsTop + 2 + row * (PORT_SIZE + PORT_GAP);
            int color = index < starving ? TopologyPalette.BAD
                : index < starving + offline ? TopologyPalette.NODE_IDLE : TopologyPalette.ACCENT;
            draw.rect(x, y, x + PORT_SIZE, y + PORT_SIZE, TopologyPalette.withAlpha(color, 0.95F));
        }
    }

    private void drawRowBackgrounds(ModularGuiContext context, int width) {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        int divider = TopologyPalette.withAlpha(TopologyTheme.textDim(context), 0.35F);
        for (int index = 0; index < count; index++) {
            int y = listTop + index * rowHeight;
            if (isRowSelected(scroll + index)) draw.rect(
                padding,
                y,
                width - padding,
                y + rowHeight - 1,
                TopologyPalette.withAlpha(TopologyPalette.ACCENT, 0.22F));
            else if (isRowHovered(index)) draw.rect(
                padding,
                y,
                width - padding,
                y + rowHeight - 1,
                TopologyPalette.withAlpha(TopologyPalette.ACCENT, 0.1F));
            draw.rect(padding, y + rowHeight - 1, width - padding, y + rowHeight, divider);
        }
    }

    private void drawRowDots() {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        if (count == 0) return;
        ConsoleTab tab = state.tab();
        List<TopologyBridge> bridges = tab == ConsoleTab.BRIDGES ? state.filteredBridges() : List.of();
        List<TopologyDevice> devices = tab == ConsoleTab.DEVICES ? state.filteredDevices() : List.of();
        List<TopologyNetwork> networks = tab == ConsoleTab.NODES ? state.filteredNetworks() : List.of();
        for (int index = 0; index < count; index++) {
            int color = switch (tab) {
                case BRIDGES -> rowHealth(bridges.get(scroll + index));
                case DEVICES -> rowHealth(devices.get(scroll + index));
                default -> TopologyPalette.health(networks.get(scroll + index));
            };
            draw.disc(
                padding + 3.5F,
                listTop + index * rowHeight + rowHeight * 0.5F,
                2.5F,
                TopologyPalette.opaque(color));
        }
    }

    private static int rowHealth(TopologyBridge bridge) {
        if (!bridge.resolved()) return TopologyPalette.BAD;
        return bridge.active() ? TopologyPalette.GOOD : TopologyPalette.WARN;
    }

    private static int rowHealth(TopologyDevice device) {
        if (!device.faulted()) return TopologyPalette.GOOD;
        return device.active() ? TopologyPalette.WARN : TopologyPalette.BAD;
    }

    private void drawLabels(ModularGuiContext context, int width) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int text = TopologyTheme.text(context);
        int muted = TopologyTheme.textMuted(context);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            drawHeader(font, width, muted);
            if (!selectedCoordinates().isEmpty()) {
                boolean hovered = isOverLocate();
                String locate = TopologyViewState.translate("locate");
                TopologyTheme.centeredLabel(
                    locate,
                    width - padding - locateWidth() / 2,
                    TopologyTheme.captionTop(padding, LOCATE_HEIGHT),
                    TopologyTheme.buttonText(context, hovered),
                    TopologyTheme.buttonShadow(context, hovered));
            }
            int x = padding;
            for (ConsoleTab tab : ConsoleTab.values()) {
                int tabWidth = tabWidth(tab);
                if (x + tabWidth > width - padding) break;
                boolean hovered = isOverTab(tab);
                TopologyTheme.centeredLabel(
                    font.trimStringToWidth(TopologyViewState.translate("tab." + tab.key()), tabWidth - 2),
                    x + tabWidth / 2,
                    TopologyTheme.captionTop(tabsTop, TAB_HEIGHT),
                    TopologyTheme.buttonText(context, hovered),
                    TopologyTheme.buttonShadow(context, hovered));
                x += tabWidth + CHIP_GAP;
            }
            section(chipsTop, "section.kinds", muted);
            if (!dense) forEachChip(width, (kind, chipX, chipY, chipWidth) -> {
                boolean hovered = isOverChip(kind);
                TopologyTheme.label(
                    font.trimStringToWidth(TopologyViewState.kindLabel(kind), Math.max(2, chipWidth - 13)),
                    chipX + 11,
                    TopologyTheme.captionTop(chipY, CHIP_HEIGHT),
                    TopologyTheme.buttonText(context, hovered),
                    TopologyTheme.buttonShadow(context, hovered));
            });
            section(switchesTop, "section.filter", muted);
            x = padding;
            for (int index = 0; index < SWITCH_COUNT; index++) {
                int switchWidth = switchWidth(index);
                if (x + switchWidth > width - padding) break;
                boolean hovered = isOverSwitch(index);
                TopologyTheme.centeredLabel(
                    dense ? switchGlyph(index) : switchLabel(index),
                    x + switchWidth / 2,
                    TopologyTheme.captionTop(switchesTop, CHIP_HEIGHT),
                    TopologyTheme.buttonText(context, hovered),
                    TopologyTheme.buttonShadow(context, hovered));
                x += switchWidth + CHIP_GAP;
            }
            if (portsHeight > 0) section(portsTop, "section.channels", muted);
            section(listTop, switch (state.tab()) {
                case BRIDGES -> "section.bridges";
                case DEVICES -> "section.devices";
                default -> "section.networks";
            }, muted);
            drawRows(font, width, text, muted);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawLocateButton(ModularGuiContext context, int width) {
        if (selectedCoordinates().isEmpty()) return;
        int buttonWidth = locateWidth();
        TopologyTheme.drawButton(
            context,
            width - padding - buttonWidth,
            padding,
            buttonWidth,
            LOCATE_HEIGHT,
            isOverLocate(),
            false);
    }

    private boolean handleLocate() {
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
        return mouseX >= getArea().width - padding - locateWidth() && mouseX <= getArea().width - padding
            && mouseY >= padding
            && mouseY <= padding + LOCATE_HEIGHT;
    }

    private boolean isOverTab(ConsoleTab tab) {
        return isBelowMouse() && tabAt(localMouseX(), localMouseY()) == tab;
    }

    private boolean isOverChip(TopologyLinkKind kind) {
        return isBelowMouse() && chipAt(localMouseX(), localMouseY()) == kind;
    }

    private boolean isOverSwitch(int index) {
        return isBelowMouse() && switchAt(localMouseX(), localMouseY()) == index;
    }

    private int localMouseX() {
        return getContext().getAbsMouseX() - getArea().x;
    }

    private int localMouseY() {
        return getContext().getAbsMouseY() - getArea().y;
    }

    private int locateWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.translate("locate")) + 10;
    }

    private String selectedCoordinates() {
        TopologyBridge bridge = state.snapshot()
            .bridge(state.selectedBridge());
        if (bridge != null) return TopologyViewState.coordinates(bridge.origin());
        TopologyNetwork network = selectedNetwork();
        return network == null ? "" : TopologyViewState.coordinates(network.pivot());
    }

    private void drawHeader(FontRenderer font, int width, int muted) {
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
            color = 0;
        }
        int titleRoom = width - padding * 2 - (selectedCoordinates().isEmpty() ? 0 : locateWidth() + 4);
        TopologyTheme.label(
            font.trimStringToWidth(title, Math.max(16, titleRoom)),
            padding,
            padding,
            color == 0 ? muted : TopologyPalette.opaque(color),
            false);
        if (dense) return;
        String subtitle = bridge != null ? TopologyViewState.coordinates(bridge.origin())
            : network != null ? TopologyViewState.coordinates(network.pivot()) : "";
        TopologyTheme.label(font.trimStringToWidth(subtitle, width - padding * 2), padding, padding + 11, muted, false);
    }

    private void drawRows(FontRenderer font, int width, int text, int muted) {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        if (count == 0) {
            TopologyTheme.label(
                font.trimStringToWidth(TopologyViewState.translate("no_matches"), width - padding * 2),
                padding,
                listTop + 2,
                muted,
                false);
            return;
        }
        if (state.tab() == ConsoleTab.BRIDGES) {
            List<TopologyBridge> bridges = state.filteredBridges();
            for (int index = 0; index < count; index++) {
                TopologyBridge bridge = bridges.get(scroll + index);
                int top = listTop + index * rowHeight;
                row(
                    font,
                    width,
                    top,
                    TopologyViewState.kindLabel(bridge.kind()),
                    TopologyViewState.location(bridge.origin()),
                    TopologyPalette.opaque(
                        bridge.kind()
                            .color()),
                    muted,
                    bridge.resolved() ? "" : TopologyViewState.translate("row.unresolved"));
            }
            return;
        }
        if (state.tab() == ConsoleTab.DEVICES) {
            List<TopologyDevice> devices = state.filteredDevices();
            for (int index = 0; index < count; index++) {
                TopologyDevice device = devices.get(scroll + index);
                int top = listTop + index * rowHeight;
                row(
                    font,
                    width,
                    top,
                    TopologyViewState.deviceName(device),
                    TopologyViewState.location(device.position()),
                    text,
                    muted,
                    TopologyViewState.translate(
                        device.channelOk() ? device.active() ? "row.device_ok" : "row.device_offline"
                            : "row.device_no_channel"));
            }
            return;
        }
        List<TopologyNetwork> networks = state.filteredNetworks();
        for (int index = 0; index < count; index++) {
            TopologyNetwork network = networks.get(scroll + index);
            int top = listTop + index * rowHeight;
            row(
                font,
                width,
                top,
                TopologyViewState.label(network),
                TopologyViewState.location(network.pivot()),
                network.isMain() ? TopologyPalette.opaque(TopologyPalette.ACCENT_DEEP) : text,
                muted,
                TopologyViewState.translate("row.depth", network.depth()));
        }
    }

    private void row(FontRenderer font, int width, int top, String title, String detail, int titleColor, int mutedColor,
        String badge) {
        int icon = iconSize();
        int textLeft = padding + 7 + icon + 2;
        int badgeWidth = badge.isEmpty() ? 0 : font.getStringWidth(badge) + 4;
        int room = Math.max(12, width - padding - textLeft - badgeWidth);
        boolean twoLine = rowHeight >= DETAIL_ROW_FLOOR;
        int titleTop = twoLine ? top + 2 : top + (rowHeight - font.FONT_HEIGHT) / 2;
        TopologyTheme.label(font.trimStringToWidth(title, room), textLeft, titleTop, titleColor, false);
        if (rowHeight >= DETAIL_ROW_FLOOR)
            TopologyTheme.label(font.trimStringToWidth(detail, room), textLeft, top + 11, mutedColor, false);
        if (!badge.isEmpty()) TopologyTheme.label(
            badge,
            width - padding - font.getStringWidth(badge),
            top + (rowHeight - font.FONT_HEIGHT) / 2,
            mutedColor,
            false);
    }

    private void drawRowIcons() {
        int count = Math.min(rowsVisible, rowCount() - scroll);
        if (count == 0) return;
        int icon = iconSize();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ConsoleTab tab = state.tab();
            List<TopologyBridge> bridgeRows = tab == ConsoleTab.BRIDGES ? state.filteredBridges() : List.of();
            List<TopologyDevice> deviceRows = tab == ConsoleTab.DEVICES ? state.filteredDevices() : List.of();
            List<TopologyNetwork> networkRows = tab == ConsoleTab.NODES ? state.filteredNetworks() : List.of();
            for (int index = 0; index < count; index++) {
                ItemStack stack = switch (tab) {
                    case BRIDGES -> bridgeRows.get(scroll + index)
                        .icon();
                    case DEVICES -> deviceRows.get(scroll + index)
                        .icon();
                    default -> networkRows.get(scroll + index)
                        .icon();
                };
                if (stack == null) continue;
                GuiDraw.drawItem(
                    stack,
                    padding + 7,
                    listTop + index * rowHeight + (rowHeight - icon) / 2,
                    icon,
                    icon,
                    getContext().getCurrentDrawingZ());
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private int iconSize() {
        return Math.min(16, rowHeight - 2);
    }

    private void section(int sectionTop, String key, int color) {
        if (labelHeight == 0) return;
        TopologyTheme.label(TopologyViewState.translate(key), padding, sectionTop - labelHeight, color, false);
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        int mouseX = localMouseX();
        int mouseY = localMouseY();
        if (handleLocate() || handleTabs(mouseX, mouseY)
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
        scroll = Math
            .max(0, Math.min(scroll - direction.modifier * SCROLL_STEP, Math.max(0, rowCount() - rowsVisible)));
        return true;
    }

    private ConsoleTab tabAt(int mouseX, int mouseY) {
        if (mouseY < tabsTop || mouseY > tabsTop + TAB_HEIGHT) return null;
        int x = padding;
        for (ConsoleTab tab : ConsoleTab.values()) {
            int tabWidth = tabWidth(tab);
            if (x + tabWidth > getArea().width - padding) break;
            if (mouseX >= x && mouseX <= x + tabWidth) return tab;
            x += tabWidth + CHIP_GAP;
        }
        return null;
    }

    private boolean handleTabs(int mouseX, int mouseY) {
        ConsoleTab tab = tabAt(mouseX, mouseY);
        if (tab == null) return false;
        state.tab(tab);
        scroll = 0;
        return true;
    }

    private TopologyLinkKind chipAt(int mouseX, int mouseY) {
        TopologyLinkKind[] hit = { null };
        forEachChip(
            getArea().width,
            (kind, x, y, chipWidth) -> {
                if (mouseX >= x && mouseX <= x + chipWidth && mouseY >= y && mouseY <= y + CHIP_HEIGHT) hit[0] = kind;
            });
        return hit[0];
    }

    private boolean handleChips(int mouseX, int mouseY) {
        TopologyLinkKind kind = chipAt(mouseX, mouseY);
        if (kind == null) return false;
        state.toggleKind(kind);
        return true;
    }

    private int switchAt(int mouseX, int mouseY) {
        if (mouseY < switchesTop || mouseY > switchesTop + CHIP_HEIGHT) return -1;
        int x = padding;
        for (int index = 0; index < SWITCH_COUNT; index++) {
            int switchWidth = switchWidth(index);
            if (x + switchWidth > getArea().width - padding) break;
            if (mouseX >= x && mouseX <= x + switchWidth) return index;
            x += switchWidth + CHIP_GAP;
        }
        return -1;
    }

    private boolean handleSwitches(int mouseX, int mouseY) {
        int index = switchAt(mouseX, mouseY);
        if (index < 0) return false;
        toggleSwitch(index);
        return true;
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
        int index = rowIndexAt(localMouseY());
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
        int row = (mouseY - listTop) / rowHeight;
        return row < 0 || row >= rowsVisible ? -1 : scroll + row;
    }

    private boolean isRowHovered(int visibleIndex) {
        return isBelowMouse() && rowIndexAt(localMouseY()) == scroll + visibleIndex;
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

    /**
     * Single glyph stand-ins used once the column is too narrow for a worded switch; the tooltip still spells them out.
     */
    private String switchGlyph(int index) {
        return switch (index) {
            case 0 -> "!";
            case 1 -> "?";
            default -> state.depthLimit() == Integer.MAX_VALUE ? "*" : Integer.toString(state.depthLimit());
        };
    }

    /**
     * A narrow column splits the strip into equal shares instead of dropping the last tab out of reach; the captions
     * are trimmed to whatever each share can hold.
     */
    private int tabWidth(ConsoleTab tab) {
        int tabs = ConsoleTab.values().length;
        int room = getArea().width - padding * 2 - CHIP_GAP * (tabs - 1);
        int natural = Minecraft.getMinecraft().fontRenderer
            .getStringWidth(TopologyViewState.translate("tab." + tab.key())) + 8;
        return dense ? Math.max(CHIP_HEIGHT, room / tabs) : natural;
    }

    private static int chipWidth(TopologyLinkKind kind) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.kindLabel(kind)) + 15;
    }

    private int switchWidth(int index) {
        return dense ? CHIP_HEIGHT : Minecraft.getMinecraft().fontRenderer.getStringWidth(switchLabel(index)) + 8;
    }
}
