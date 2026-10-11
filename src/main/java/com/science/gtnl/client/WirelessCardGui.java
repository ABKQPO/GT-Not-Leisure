package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.APPLY_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.CLOSE_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.COPY_POSITION;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;
import static com.science.gtnl.common.wireless.WirelessCardActions.SET_PAGE_SIZE;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;
import static net.minecraft.client.gui.Gui.drawRect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.drawable.text.TextRenderer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;

import appeng.client.gui.ScreenColor;
import appeng.core.localization.ColorUtils;
import appeng.core.localization.GuiText;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class WirelessCardGui extends ModularPanel {

    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_X = 34;
    private static final UITexture[] BUTTON_TEXTURES = { buttonTexture(0), buttonTexture(1), buttonTexture(2) };
    private static final UITexture TOGGLE_BACKGROUND = toggleTexture(255);
    private static final UITexture TOGGLE_ON = toggleTexture(129);
    private static final UITexture TOGGLE_OFF = toggleTexture(128);
    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRendererObj = mc.fontRenderer;
    private final WirelessCardContainer container;
    private final boolean detailView;
    private final List<CardButton> buttons = new ArrayList<>();
    private final TextFieldWidget search, dimension;
    private final IPanelHandler renamePanel;
    private final IPanelHandler detailsPanel;
    private NBTTagCompound snapshot = new NBTTagCompound();
    private boolean showingDetails, closingDetails;
    private int xSize = 320, ySize = 232;
    private int visibleRows = WirelessCardLayout.MIN_ROWS;
    private int layoutRetry;

    public WirelessCardGui(WirelessCardContainer container) {
        this(container, false);
    }

    private WirelessCardGui(WirelessCardContainer container, boolean detailView) {
        super(detailView ? "wireless_card_details" : "wireless_card");
        this.container = container;
        this.detailView = detailView;
        background(
            UITexture.builder()
                .location(new ResourceLocation("appliedenergistics2", "textures/guis/wireless_network_manager.png"))
                .imageSize(256, 256)
                .subAreaXYWH(0, 0, 106, 232)
                .adaptable(4)
                .build());
        search = textField(64).hintText(tr("search_placeholder"))
            .hintColor(ColorUtils.craftingTreeRequest.getColor());
        dimension = textField(12);
        child(search).child(dimension);
        renamePanel = IPanelHandler.simple(this, (parent, player) -> new RenameDialog(), true);
        detailsPanel = detailView ? null
            : IPanelHandler.simple(this, (parent, player) -> new WirelessCardGui(container, true), true);
        if (detailView) child(ButtonWidget.panelCloseButton());
        for (int id : new int[] { PREVIOUS_PAGE, NEXT_PAGE, TOGGLE_AUTO, REFRESH, BACK, RENAME, APPLY_FILTER,
            CYCLE_FILTER, CYCLE_VISUALISATION, TOGGLE_GROUPING }) addButton(id);
        for (int row = 0; row < WirelessCardLayout.MAX_ROWS; row++) {
            for (int base : new int[] { TOGGLE_PAUSE, OPEN_DETAILS, COPY_POSITION, HIGHLIGHT, TELEPORT })
                addButton(base + row);
        }
    }

    public ModularScreen screen() {
        return new ModularScreen(ScienceNotLeisure.MODID, this) {

            @Override
            public void onResize(int width, int height) {
                resizeLayout(width, height);
                for (ModularPanel panel : getPanelManager().getOpenPanels()) {
                    if (panel instanceof WirelessCardGui cardPanel && cardPanel.detailView)
                        cardPanel.resizeLayout(width, height);
                }
                super.onResize(width, height);
            }

            @Override
            public boolean onMousePressed(int button) {
                ModularPanel previous = getPanelManager().getTopMostPanel();
                if (button == 1) {
                    if (previous instanceof RenameDialog dialog) dialog.closeIfOpen();
                    else if (previous instanceof WirelessCardGui panel && (panel.detailView || panel.view()
                        .getBoolean("group"))) panel.action(BACK);
                    return true;
                }
                if (previous instanceof WirelessCardGui panel && button > 1
                    && WirelessCardInput.visualisationKey(button - 100)
                    && !panel.editingText()) {
                    panel.action(CYCLE_VISUALISATION);
                    return true;
                }
                boolean handled = super.onMousePressed(button);
                // The opening button clears focus after its callback returns.
                if (getPanelManager().getTopMostPanel() instanceof RenameDialog dialog && previous != dialog)
                    getContext().focus(dialog.name);
                return handled;
            }

            @Override
            public boolean onMouseRelease(int button) {
                return button == 1 || super.onMouseRelease(button);
            }
        };
    }

    @Override
    public void onOpen(ModularScreen screen) {
        closingDetails = false;
        if (detailView) resizeLayout(screen.getScreenArea().width, screen.getScreenArea().height);
        super.onOpen(screen);
        if (detailView) getContext().removeFocus();
    }

    @Override
    public boolean disablePanelsBelow() {
        return detailView;
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        if (detailView && key == Keyboard.KEY_ESCAPE) {
            closeIfOpen();
            return true;
        }
        if (WirelessCardInput.visualisationKey(key) && !editingText()) {
            action(CYCLE_VISUALISATION);
            return true;
        }
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
            action(detailView ? RENAME : APPLY_FILTER);
            return true;
        }
        return super.onKeyPressed(character, key);
    }

    @Override
    public void closeIfOpen() {
        if (detailView && !closingDetails) {
            closingDetails = true;
            if (container.display.getBoolean("detail")) container.sync.request(CLOSE_DETAILS, "", "", 0, 0);
        }
        super.closeIfOpen();
    }

    private NBTTagCompound view() {
        // Only the active view is synced. Keep the other panel's last page behind the modal window.
        return container.display.getBoolean("detail") == detailView ? container.display : snapshot;
    }

    private int rowTop() {
        return detailView ? 60 : 82;
    }

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.wireless.gui." + key, args);
    }

    private static UITexture buttonTexture(int state) {
        return UITexture.builder()
            .location(new ResourceLocation("minecraft", "textures/gui/widgets.png"))
            .imageSize(256, 256)
            .subAreaXYWH(0, 46 + state * 20, 200, 20)
            .adaptable(2)
            .build();
    }

    private static UITexture toggleTexture(int icon) {
        return UITexture.builder()
            .location(new ResourceLocation("appliedenergistics2", "textures/guis/states.png"))
            .imageSize(256, 256)
            .subAreaXYWH(icon % 16 * 16, icon / 16 * 16, 16, 16)
            .build();
    }

    private void resizeLayout(int width, int height) {
        var layout = WirelessCardLayout.forScreen(width, height);
        xSize = detailView ? Math.max(320, layout.width() - 24) : layout.width();
        ySize = detailView ? layout.height() - 22 : layout.height();
        visibleRows = layout.rows();
        layoutRetry = 0;
        size(xSize, ySize);
        int dx = xSize - 320;
        search.pos(38, 62)
            .size(96 + dx, 12);
        dimension.pos(148 + dx, 62)
            .size(34, 12);
        for (var button : buttons) {
            int id = button.id;
            if (isRowAction(id)) {
                int y = rowTop() + rowIndex(id) * ROW_HEIGHT;
                switch (type(id)) {
                    case TOGGLE_PAUSE -> button.pos(xSize - 28, y + 4)
                        .size(16, 16);
                    case OPEN_DETAILS -> button.pos(10, y)
                        .size(rowStateX() - 10, ROW_HEIGHT);
                    case COPY_POSITION -> button.pos(xSize - 96, y + 2)
                        .size(30, 20);
                    case HIGHLIGHT -> button.pos(xSize - 62, y + 2)
                        .size(30, 20);
                    case TELEPORT -> button.pos(xSize - 130, y + 2)
                        .size(30, 20);
                }
            } else switch (id) {
                case PREVIOUS_PAGE -> button.pos(10, ySize - 27)
                    .size(18, 20);
                case NEXT_PAGE -> button.pos(76, ySize - 27)
                    .size(18, 20);
                case TOGGLE_AUTO -> button.pos(xSize - 28, 10)
                    .size(16, 16);
                case REFRESH -> button.pos(xSize - 76, ySize - 27)
                    .size(64, 20);
                case BACK -> button.pos(layout.footerInfoX(), ySize - 27)
                    .size(36, 20);
                case RENAME -> button.pos(244 + dx, detailView ? 24 : 59)
                    .size(64, 20);
                case APPLY_FILTER -> button.pos(264 + dx, 59)
                    .size(44, 20);
                case TOGGLE_GROUPING -> button.pos(10, 59)
                    .size(24, 20);
                case CYCLE_FILTER -> button.pos(188 + dx, 59)
                    .size(72, 20);
                case CYCLE_VISUALISATION -> button.pos(layout.visualisationX(), ySize - 27)
                    .size(layout.visualisationWidth(), 20);
            }
        }
    }

    private TextFieldWidget textField(int max) {
        return new TextFieldWidget().background(
            new Rectangle().color(0xff000000),
            new Rectangle().color(0xffa0a0a0)
                .hollow(1))
            .setTooltipOverride(true)
            .value(new StringValue(""))
            .setMaxLength(max)
            .setTextColor(ColorUtils.searchboxText.getColor())
            .padding(2, 0);
    }

    private void addButton(int id) {
        CardButton button = new CardButton(id);
        buttons.add(button);
        child(button);
    }

    private final class CardButton extends ButtonWidget<CardButton> {

        private final int id;
        private final TextRenderer text = new TextRenderer();
        private boolean available, on;
        private String label = "";

        CardButton(int id) {
            this.id = id;
            text.setShadow(true);
            background(IDrawable.EMPTY).hoverBackground(IDrawable.EMPTY);
            if (type(id) == OPEN_DETAILS) tooltipBuilder(tooltip -> {
                NBTTagCompound row = view().getTagList("rows", 10)
                    .getCompoundTagAt(rowIndex(id));
                String name = rowName(row, ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon")));
                String targetName = row.getString("targetName");
                tooltip.addLine(targetName.isEmpty() || targetName.equals(name) ? name : name + " - " + targetName);
                tooltip.addLine(row.getString("positionLabel"));
                if (row.getBoolean("group")) tooltip.addLine(tr("loaded", row.getInteger("clusters")));
            }).tooltipAutoUpdate(true);
            if (id == TOGGLE_GROUPING) tooltipBuilder(
                tooltip -> tooltip.addLine(
                    GuiText.Types.getLocal() + ": "
                        + StatCollector
                            .translateToLocal(view().getBoolean("collapseTypes") ? "options.on" : "options.off")))
                                .tooltipAutoUpdate(true);
            onMousePressed(button -> {
                if (button != 0 || !available) return false;
                action(id);
                return true;
            });
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
            if (type(id) == OPEN_DETAILS) return;
            int width = getArea().width, height = getArea().height;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            try {
                // MUI's text-field stencil leaves texturing disabled. Its drawables establish their own
                // render state, whereas GuiButton.drawButton assumes the vanilla GUI has already done so.
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                ScreenColor.setGuiColor();
                if (id == TOGGLE_AUTO || type(id) == TOGGLE_PAUSE) {
                    TOGGLE_BACKGROUND.draw(0, 0, width, height);
                    ScreenColor.resetGuiColor();
                    (on ? TOGGLE_ON : TOGGLE_OFF).draw(0, 0, width, height);
                } else {
                    BUTTON_TEXTURES[!available ? 0 : isHovering() ? 2 : 1].draw(0, 0, width, height);
                    text.setColor(!available ? 0xffa0a0a0 : isHovering() ? 0xffffffA0 : 0xffe0e0e0);
                    text.setAlignment(Alignment.Center, width, height);
                    text.drawSimple(fontRendererObj.trimStringToWidth(label, Math.max(0, width - 4)));
                }
            } finally {
                GL11.glPopAttrib();
            }
        }
    }

    private int contentWidth() {
        return xSize - 212;
    }

    private boolean layoutReady() {
        return !closingDetails && container.display.getBoolean("detail") == detailView
            && container.display.hasKey("session")
            && container.display.getInteger("pageSize") == visibleRows;
    }

    private boolean editingText() {
        return search.isFocused() || dimension.isFocused();
    }

    private void action(int id) {
        if (detailView && id == BACK) {
            closeIfOpen();
            return;
        }
        if (!layoutReady()) return;
        snapshot = container.display;
        if (id == RENAME) {
            if (container.display.getBoolean("detail") && container.display.getBoolean("renameable"))
                renamePanel.openPanel();
            return;
        }
        if (type(id) == COPY_POSITION && rowIndex(id) < visibleRows) {
            var row = container.display.getTagList("rows", 10)
                .getCompoundTagAt(rowIndex(id));
            GuiScreen.setClipboardString(
                row.getString("position") + (row.getBoolean("group") ? "" : " / " + row.getString("side")));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.copied"));
            return;
        }
        int filterState = container.display.getInteger("filterState");
        if (id == CYCLE_FILTER) filterState = (filterState + 1) % 5;
        container.sync
            .request(id == CYCLE_FILTER ? APPLY_FILTER : id, search.getText(), dimension.getText(), filterState, 0);
    }

    private final class RenameDialog extends Dialog<Void> {

        private final TextFieldWidget name = textField(32);
        private String target;
        private long nameRevision;
        private boolean finished;

        RenameDialog() {
            super("wireless_card_rename");
            size(260, 96);
            background(WirelessCardGui.this.getBackground());
            child(ButtonWidget.panelCloseButton());
            child(
                IKey.str(GuiText.Renamer.getLocal())
                    .asWidget()
                    .color(ColorUtils.guiTextColorGray.getColor())
                    .pos(10, 10)
                    .size(220, 18));
            child(
                name.pos(10, 34)
                    .size(240, 16));
            child(dialogButton("gui.cancel", this::closeIfOpen).pos(10, 64));
            child(dialogButton("gui.done", this::submit).pos(134, 64));
        }

        private ButtonWidget<?> dialogButton(String key, Runnable action) {
            return new ButtonWidget<>().size(116, 20)
                .background(BUTTON_TEXTURES[1])
                .hoverBackground(BUTTON_TEXTURES[2])
                .overlay(
                    IKey.lang(key)
                        .color(0xffe0e0e0)
                        .shadow(true))
                .onMousePressed(button -> {
                    if (button != 0 || finished) return false;
                    action.run();
                    return true;
                });
        }

        @Override
        public void onOpen(ModularScreen screen) {
            NBTTagCompound data = container.display;
            target = data.getString("detailTarget");
            nameRevision = data.getLong("nameRevision");
            finished = false;
            String text = data.getBoolean("multipleNames") ? "" : data.getString("name");
            name.getStringValue()
                .setStringValue(text);
            name.setText(text);
            super.onOpen(screen);
            getContext().focus(name);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (!isOpen() || isClosing()) return;
            if (finished) closeIfOpen();
            else validateTarget();
        }

        private boolean validateTarget() {
            NBTTagCompound data = container.display;
            if (data.getBoolean("detail") && data.getBoolean("renameable")
                && target.equals(data.getString("detailTarget"))
                && nameRevision == data.getLong("nameRevision")) return true;
            closeIfOpen();
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
            return false;
        }

        private void submit() {
            if (finished || !layoutReady() || !validateTarget()) return;
            container.sync.request(RENAME, name.getText(), "", 0, nameRevision);
            closeIfOpen();
        }

        @Override
        public boolean onKeyPressed(char character, int key) {
            if (finished) return true;
            if (key == Keyboard.KEY_ESCAPE) {
                closeIfOpen();
                return true;
            }
            if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
                submit();
                return true;
            }
            return super.onKeyPressed(character, key);
        }

        @Override
        public void closeIfOpen() {
            // Retry closing after the opening animation, without allowing another submission.
            finished = true;
            super.closeIfOpen();
        }

        @Override
        public void onClose() {
            if (name.isFocused()) getContext().removeFocus();
            super.onClose();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        boolean detail = container.display.getBoolean("detail");
        if (detail == detailView) snapshot = container.display;
        if (!detailView && detail != showingDetails) {
            showingDetails = detail;
            if (detail) detailsPanel.openPanel();
            else detailsPanel.closePanel();
        } else if (detailView && (closingDetails || !detail)) {
            closeIfOpen();
            return;
        }
        if (!detailView && container.display.getInteger("pageSize") != visibleRows
            && container.display.hasKey("session")
            && layoutRetry-- <= 0) {
            container.sync.request(SET_PAGE_SIZE, "", "", visibleRows, 0);
            layoutRetry = 10;
        }
        NBTTagCompound data = view();
        boolean showFilters = !detailView && !detail && !detailsPanel.isPanelOpen();
        if (!showFilters && editingText()) getContext().removeFocus();
        search.setEnabled(showFilters);
        dimension.setEnabled(showFilters);
        NBTTagList rows = data.getTagList("rows", 10);
        int rowCount = Math.min(visibleRows, rows.tagCount());
        boolean ready = layoutReady();
        for (var button : buttons) {
            int id = button.id;
            int actionType = type(id);
            boolean visible = true;
            if (isRowAction(id)) {
                int index = rowIndex(id);
                if (index >= rowCount) {
                    button.setEnabled(false);
                    continue;
                }
                NBTTagCompound row = rows.getCompoundTagAt(index);
                if (actionType == TOGGLE_PAUSE) button.on = !row.getBoolean("paused");
                if (actionType == TOGGLE_PAUSE || actionType == HIGHLIGHT || actionType == TELEPORT)
                    visible = !row.getBoolean("group");
                if (actionType == COPY_POSITION) visible = !row.getBoolean("category");
            }
            button.available = ready;
            if (id == TOGGLE_AUTO) button.on = data.getBoolean("auto");
            if (id == BACK) visible = detailView || data.getBoolean("group");
            if (id == RENAME) {
                visible = detailView;
                button.available &= data.getBoolean("renameable");
            }
            if (id == APPLY_FILTER || id == CYCLE_FILTER
                || id == TOGGLE_AUTO
                || id == CYCLE_VISUALISATION
                || id == TOGGLE_GROUPING) visible = !detailView;
            if (id == TOGGLE_GROUPING) visible &= !data.getBoolean("typeGroup");
            if (actionType == OPEN_DETAILS) visible &= !detailView;
            button.setEnabled(visible);
            if (!visible) continue;
            if (id == PREVIOUS_PAGE) button.available &= data.getInteger("page") > 0;
            if (id == NEXT_PAGE) button.available &= data.getInteger("page") + 1 < data.getInteger("pages");
            button.label = switch (actionType) {
                case PREVIOUS_PAGE -> "<";
                case NEXT_PAGE -> ">";
                case REFRESH -> tr("refresh");
                case BACK -> tr("back_compact");
                case RENAME -> StatCollector.translateToLocal("selectWorld.rename");
                case APPLY_FILTER -> tr("search");
                case CYCLE_FILTER -> tr("filter." + data.getInteger("filterState"));
                case TOGGLE_GROUPING -> data.getBoolean("collapseTypes") ? "+" : "-";
                case TELEPORT -> tr("teleport");
                case HIGHLIGHT -> tr("highlight");
                case COPY_POSITION -> tr("copy");
                default -> "";
            };
            if (id == CYCLE_VISUALISATION) {
                int mode = data.getInteger("visualMode");
                String label = StatCollector
                    .translateToLocal(WirelessCardVisualisation.modeKey(mode) + (mode > 0 ? ".short" : ""));
                button.label = fontRendererObj.trimStringToWidth(
                    button.getArea().width < 102 ? label : tr("visualisation_button", label),
                    button.getArea().width - 8);
            }
        }
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            int count = Math.min(
                visibleRows,
                view().getTagList("rows", 10)
                    .tagCount());
            for (int row = 0; row < count; row++) {
                int y = rowTop() + ROW_HEIGHT - 1 + row * ROW_HEIGHT;
                drawRect(8, y, xSize - 8, y + 1, ColorUtils.craftingDiagnosticTerminalLine.getColor());
            }
            drawForeground();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static String entranceUsage(NBTTagCompound data, NBTTagCompound row) {
        if (!row.getBoolean("runtime")) return tr("no_runtime");
        if (!data.getBoolean("channels")) return tr("usage_disabled");
        return row.getInteger("usedChannels") < 0 ? tr("usage_waiting") : tr("usage", row.getInteger("usedChannels"));
    }

    private static int goodColor() {
        return ColorUtils.craftConfirmPercent50.getColor();
    }

    private static int badColor() {
        return ColorUtils.reshuffleStatusFailed.getColor();
    }

    private static int waitingColor() {
        return ColorUtils.craftConfirmPercent25.getColor();
    }

    private static int stateColor(NBTTagCompound row) {
        if (row.getBoolean("paused") || row.getBoolean("conflicted")) return badColor();
        if (row.getBoolean("stable") && row.getInteger("missing") > 0) return badColor();
        if (!row.getBoolean("stable") || !row.getString("state")
            .equals("active")) return waitingColor();
        // A live entrance is not proof of power or active devices.
        return row.getInteger("devices") > 0 && row.getInteger("online") == row.getInteger("devices") ? goodColor()
            : ColorUtils.guiTextColorGray.getColor();
    }

    private static String budgetText(NBTTagCompound data) {
        return !data.getBoolean("bound") ? tr("bind_hint")
            : !data.getBoolean("sourceLoaded") ? tr("source_waiting")
                : !data.getBoolean("channels") ? tr("unlimited")
                    : tr(
                        "budget_compact",
                        data.getInteger("used") < 0 ? "?" : data.getInteger("used"),
                        data.getInteger("capacity"));
    }

    private int textSegment(String text, int x, int y, int end, int color) {
        String fitted = fontRendererObj.trimStringToWidth(text, Math.max(0, end - x));
        fontRendererObj.drawString(fitted, x, y, color);
        return x + fontRendererObj.getStringWidth(fitted);
    }

    private void drawMetrics(NBTTagCompound data, int x, int y, int width, boolean compact) {
        int end = x + width;
        String entrances = "  " + tr(
            compact ? "entrances_compact" : "entrances",
            compact ? data.getInteger("entrances")
                : data.getInteger("entrances") + " / " + data.getInteger("entranceLimit"));
        int metricsEnd = compact ? end : Math.max(x, end - fontRendererObj.getStringWidth(entrances));
        int textColor = ColorUtils.guiTextColorGray.getColor();
        int online = data.getInteger("online"), devices = data.getInteger("devices");
        int onlineColor = data.getBoolean("paused") ? badColor()
            : !data.getBoolean("stable") ? waitingColor() : online == devices && devices > 0 ? goodColor() : textColor;
        x = textSegment(
            tr(compact ? "online_compact" : "online_metric", online, devices),
            x,
            y,
            metricsEnd,
            onlineColor);
        int missingColor = !data.getBoolean("stable") ? waitingColor()
            : data.getInteger("missing") > 0 ? badColor() : textColor;
        x = textSegment(
            "  " + tr(
                compact ? "missing_compact" : "missing_metric",
                data.getBoolean("stable") ? data.getInteger("missing") : "?"),
            x,
            y,
            metricsEnd,
            missingColor);
        textSegment(entrances, x, y, end, textColor);
    }

    private void ratioBar(int x, int y, int width, int height, int value, int total, int fill, int remainder) {
        drawRect(x, y, x + width, y + height, remainder | 0xFF000000);
        if (value < 0 || total <= 0) return;
        int filled = (int) Math.round(width * Math.max(0.0, Math.min(1.0, (double) value / total)));
        if (filled > 0) drawRect(x, y, x + filled, y + height, fill | 0xFF000000);
    }

    private void drawDeviceIcon(ItemStack icon, int x, int y) {
        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        var itemRender = GuiScreenAccessor.getItemRender();
        float renderZ = itemRender.zLevel;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glColor4f(1, 1, 1, 1);
            // Use MUI's item layer and Forge glint cleanup, while isolating custom item renderers
            // from the following text, rows and NEI. The icon remains 16x16 GUI pixels.
            GuiDraw.drawItem(icon, x, y, 16, 16, getContext().getCurrentDrawingZ());
        } finally {
            itemRender.zLevel = renderZ;
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        }
    }

    private void drawEmptyMessage(NBTTagCompound data, int color) {
        List<String> lines = fontRendererObj.listFormattedStringToWidth(
            tr(data.getBoolean("initializing") ? "initializing" : data.getBoolean("filtered") ? "no_matches" : "empty"),
            Math.min(300, xSize - 48));
        int lineHeight = fontRendererObj.FONT_HEIGHT + 3;
        int y = rowTop() + (visibleRows * ROW_HEIGHT - lines.size() * lineHeight) / 2;
        for (String line : lines) {
            fontRendererObj.drawString(line, (xSize - fontRendererObj.getStringWidth(line)) / 2, y, color);
            y += lineHeight;
        }
    }

    private static String rowName(NBTTagCompound row, ItemStack icon) {
        String name = row.getString("name");
        if (name.isEmpty()) name = icon == null ? "" : icon.getDisplayName();
        return row.getBoolean("shared") ? tr("shared", name) : name;
    }

    private int rowStateX() {
        return xSize - 172;
    }

    private void drawRowTitle(NBTTagCompound data, NBTTagCompound row, ItemStack icon, int y) {
        String name = (row.getBoolean("group") ? "> " : "") + rowName(row, icon);
        String position = row.getString("positionLabel");
        String count = row.getBoolean("category") ? " ×" + row.getInteger("items")
            : row.getBoolean("group") ? " [" + row.getInteger("clusters") + "]" : "";
        int width = contentWidth() - fontRendererObj.getStringWidth(count);
        int nameColor = ColorUtils.guiTextColorBlack.getColor();
        if (data.getBoolean("detail") || name.isEmpty()) {
            fontRendererObj.drawString(fontRendererObj.trimStringToWidth(position, width), CONTENT_X, y, nameColor);
            return;
        }
        int nameReserve = Math.min(24, fontRendererObj.getStringWidth(name));
        String suffix = " ("
            + fontRendererObj
                .trimStringToWidth(position, Math.max(0, width - nameReserve - fontRendererObj.getStringWidth(" ()")))
            + ")";
        String title = fontRendererObj
            .trimStringToWidth(name, Math.max(0, width - fontRendererObj.getStringWidth(suffix)));
        fontRendererObj.drawString(title, CONTENT_X, y, nameColor);
        fontRendererObj.drawString(
            suffix,
            CONTENT_X + fontRendererObj.getStringWidth(title),
            y,
            ColorUtils.craftingTreeTask.getColor());
        if (!count.isEmpty()) fontRendererObj.drawString(
            count,
            CONTENT_X + fontRendererObj.getStringWidth(title) + fontRendererObj.getStringWidth(suffix),
            y,
            stateColor(row));
    }

    private void drawForeground() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Platform.setupDrawFont();
        NBTTagCompound data = view();
        int textColor = ColorUtils.guiTextColorGray.getColor();
        ItemStack groupIcon = ItemStack.loadItemStackFromNBT(data.getCompoundTag("groupIcon"));
        String title = data.getBoolean("detail") ? tr("detail_title")
            : groupIcon == null ? tr("title")
                : groupIcon.getDisplayName() + " (" + data.getString("groupPositionLabel") + ")";
        if (!detailView && data.getBoolean("typeGroup")) {
            if (data.getBoolean("sharedGroup")) title = tr("shared", title);
            title = GuiText.Types.getLocal() + " > " + title;
        }
        fontRendererObj
            .drawString(fontRendererObj.trimStringToWidth(title, xSize - (detailView ? 40 : 140)), 10, 11, textColor);
        if (detailView) {
            fontRendererObj
                .drawString(fontRendererObj.trimStringToWidth(data.getString("name"), xSize - 94), 10, 29, textColor);
        } else {
            String autoLabel = fontRendererObj
                .trimStringToWidth(tr(data.getBoolean("auto") ? "auto_on" : "auto_off"), 94);
            fontRendererObj.drawString(
                autoLabel,
                xSize - 32 - fontRendererObj.getStringWidth(autoLabel),
                10 + (16 - fontRendererObj.FONT_HEIGHT) / 2,
                textColor);
            String source = data.getBoolean("bound") ? tr("source", data.getString("sourceLabel")) : tr("unbound");
            fontRendererObj.drawString(fontRendererObj.trimStringToWidth(source, xSize - 20), 10, 25, textColor);
            boolean budgetKnown = data.getBoolean("sourceLoaded") && data.getBoolean("channels")
                && data.getBoolean("stable")
                && data.getInteger("used") >= 0
                && data.getInteger("capacity") > 0;
            int budgetColor = !budgetKnown ? textColor
                : data.getInteger("used") >= data.getInteger("capacity") ? badColor() : goodColor();
            fontRendererObj
                .drawString(fontRendererObj.trimStringToWidth(budgetText(data), xSize - 116), 10, 36, budgetColor);
            ratioBar(
                xSize - 96,
                37,
                84,
                6,
                budgetKnown ? data.getInteger("used") : -1,
                data.getInteger("capacity"),
                budgetColor,
                ColorUtils.craftingTreeTask.getColor());
            fontRendererObj.drawString("D", xSize - 182, 64, textColor);
        }
        drawMetrics(data, 10, 47, xSize - 20, false);
        NBTTagList rows = data.getTagList("rows", 10);
        for (int index = 0; index < Math.min(visibleRows, rows.tagCount()); index++) {
            NBTTagCompound row = rows.getCompoundTagAt(index);
            int y = rowTop() + index * ROW_HEIGHT;
            ItemStack icon = ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon"));
            if (icon != null) drawDeviceIcon(icon, 12, y + 4);
            drawRowTitle(data, row, icon, y + 2);
            String state = tr("short_state." + row.getString("state"));
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(state, 36),
                rowStateX(),
                y + (ROW_HEIGHT - fontRendererObj.FONT_HEIGHT) / 2,
                stateColor(row));
            if (data.getBoolean("detail")) {
                fontRendererObj.drawString(
                    fontRendererObj.trimStringToWidth(entranceUsage(data, row), contentWidth()),
                    CONTENT_X,
                    y + 13,
                    stateColor(row));
            } else {
                drawMetrics(row, CONTENT_X, y + 13, contentWidth(), true);
                int missingColor = row.getBoolean("stable") && row.getInteger("missing") > 0 ? badColor()
                    : ColorUtils.craftingTreeTask.getColor();
                ratioBar(
                    rowStateX(),
                    y + 19,
                    36,
                    2,
                    row.getBoolean("stable") ? row.getInteger("online") : -1,
                    row.getInteger("devices"),
                    goodColor(),
                    missingColor);
            }
        }
        if (rows.tagCount() == 0) drawEmptyMessage(data, textColor);
        String page = fontRendererObj
            .trimStringToWidth((data.getInteger("page") + 1) + "/" + Math.max(1, data.getInteger("pages")), 40);
        fontRendererObj.drawString(page, 52 - fontRendererObj.getStringWidth(page) / 2, ySize - 20, textColor);
        if (!data.getBoolean("detail") && !data.getBoolean("group")) {
            var layout = new WirelessCardLayout(xSize, ySize, visibleRows);
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(
                    tr(data.getBoolean("filtered") ? "matched" : "loaded", data.getInteger("total")),
                    layout.visualisationX() - layout.footerInfoX() - 4),
                layout.footerInfoX(),
                ySize - 20,
                textColor);
        }
    }
}
