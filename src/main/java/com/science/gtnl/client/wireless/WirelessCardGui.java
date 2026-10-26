package com.science.gtnl.client.wireless;

import static com.science.gtnl.common.wireless.WirelessCardActions.APPLY_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;

import appeng.core.localization.ColorUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessCardGui extends WirelessCardPanel {

    private final TextFieldWidget search, dimension;
    private final IPanelHandler detailsPanel, groupPanel;
    private boolean showingDetails;
    private String searchText = "", dimensionText = "";

    public WirelessCardGui(WirelessCardContainer container) {
        super("wireless_card", container);
        search = textField(64).hintText(tr("search_placeholder"))
            .hintColor(ColorUtils.craftingTreeRequest.getColor());
        dimension = textField(12);
        search.autoUpdateOnChange(true)
            .value(new StringValue.Dynamic(() -> searchText, text -> {
                if (searchText.equals(text)) return;
                searchText = text;
                applyFilters(false, false);
            }));
        dimension.autoUpdateOnChange(true)
            .value(new StringValue.Dynamic(() -> dimensionText, text -> {
                if (dimensionText.equals(text)) return;
                dimensionText = text;
                applyFilters(false, false);
            }));
        child(search).child(dimension);
        groupPanel = IPanelHandler.simple(this, (parent, player) -> new WirelessCardGroupPanel(container), true);
        detailsPanel = IPanelHandler.simple(this, (parent, player) -> new WirelessCardDetailsPanel(container), true);
        for (int id : new int[] { TOGGLE_AUTO, CYCLE_FILTER, CYCLE_VISUALISATION, TOGGLE_GROUPING }) addButton(id);
        addRowButtons(OPEN_DETAILS);
    }

    @Override
    boolean isDetailView() {
        return false;
    }

    @Override
    int rowTop() {
        return 82;
    }

    @Override
    void confirm() {
        action(APPLY_FILTER);
    }

    @Override
    boolean editingText() {
        return search.isFocused() || dimension.isFocused();
    }

    public ModularScreen screen() {
        return new ModularScreen(ScienceNotLeisure.MODID, this) {

            @Override
            public void onResize(int width, int height) {
                resizeLayout(width, height);
                for (ModularPanel panel : getPanelManager().getOpenPanels()) {
                    if (panel instanceof WirelessCardPanel cardPanel && panel != WirelessCardGui.this)
                        cardPanel.resizeLayout(width, height);
                }
                super.onResize(width, height);
            }

            @Override
            public boolean onMousePressed(int button) {
                ModularPanel previous = getPanelManager().getTopMostPanel();
                ModularPanel hovered = hoveredPanel();
                if (button == 1) {
                    if (hovered == null) return true;
                    if (hovered.getTopHovering() instanceof TextFieldWidget) super.onMousePressed(button);
                    else if (hovered instanceof WirelessCardRenameDialog dialog) dialog.closeIfOpen();
                    else if (hovered instanceof WirelessCardPanel panel && (panel.isDetailView() || panel.view()
                        .getBoolean("group"))) panel.action(BACK);
                    return true;
                }
                if (hovered instanceof WirelessCardPanel panel && button > 1
                    && WirelessCardInput.visualisationKey(button - 100)
                    && !(getContext().getFocusedWidget()
                        .getElement() instanceof TextFieldWidget)) {
                    panel.action(CYCLE_VISUALISATION);
                    return true;
                }
                boolean handled = super.onMousePressed(button);
                // The opening button clears focus after its callback returns.
                if (getPanelManager().getTopMostPanel() instanceof WirelessCardRenameDialog dialog
                    && previous != dialog) getContext().focus(dialog.name);
                return handled;
            }

            private ModularPanel hoveredPanel() {
                for (ModularPanel panel : getPanelManager().getOpenPanels()) {
                    if (panel.isBelowMouse()) return panel;
                    if (panel.disablePanelsBelow()) break;
                }
                return null;
            }

            @Override
            public boolean onMouseRelease(int button) {
                return super.onMouseRelease(button) || button == 1;
            }
        };
    }

    @Override
    void resizePanel(WirelessCardLayout layout) {
        xSize = layout.width();
        ySize = layout.height();
        container.clientView()
            .resize(layout.rows());
        int dx = xSize - 320;
        search.pos(10, 62)
            .size(124 + dx, 12);
        dimension.pos(148 + dx, 62)
            .size(34, 12);
    }

    @Override
    void layoutButton(WirelessCardButton control, WirelessCardLayout layout) {
        var button = control.widget;
        int dx = xSize - 320;
        switch (control.id) {
            case TOGGLE_AUTO -> button.pos(xSize - 28, 10)
                .size(16, 16);
            case TOGGLE_GROUPING -> button.pos(264 + dx, 59)
                .size(44, 20);
            case CYCLE_FILTER -> button.pos(188 + dx, 59)
                .size(72, 20);
            case CYCLE_VISUALISATION -> button.pos(layout.visualisationX(), ySize - 27)
                .size(layout.visualisationWidth(), 20);
        }
    }

    @Override
    boolean updatePanel() {
        boolean detail = container.display.getBoolean("detail");
        if (!detail) {
            if (container.clientView()
                .groupDisplay()
                .getBoolean("group")) groupPanel.openPanel();
            else groupPanel.closePanel();
        }
        if (detail != showingDetails) {
            showingDetails = detail;
            if (detail) detailsPanel.openPanel();
            else detailsPanel.closePanel();
        }
        boolean showFilters = !detail && !detailsPanel.isPanelOpen();
        if (!showFilters && editingText()) getContext().removeFocus();
        search.setEnabled(showFilters);
        dimension.setEnabled(showFilters);
        return true;
    }

    @Override
    public void sendAction(int id) {
        if (id == APPLY_FILTER || id == CYCLE_FILTER) {
            applyFilters(true, id == CYCLE_FILTER);
            return;
        }
        super.sendAction(id);
    }

    private void applyFilters(boolean reportInvalid, boolean cycle) {
        int state = container.display.getInteger("filterState");
        if (cycle) state = (state + 1) % 5;
        if (!container.clientView()
            .filter(searchText, dimensionText, state) && reportInvalid)
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.filter_invalid"));
    }

    @Override
    void drawHeader(NBTTagCompound data, int textColor) {
        fontRendererObj.drawString(fontRendererObj.trimStringToWidth(tr("title"), xSize - 140), 10, 11, textColor);
        String autoLabel = fontRendererObj.trimStringToWidth(tr(data.getBoolean("auto") ? "auto_on" : "auto_off"), 94);
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

    @Override
    void drawFooter(NBTTagCompound data, int textColor) {
        if (!data.getBoolean("group")) {
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
