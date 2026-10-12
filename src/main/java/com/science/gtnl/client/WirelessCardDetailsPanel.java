package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.CLOSE_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;

import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class WirelessCardDetailsPanel extends WirelessCardPanel {

    private final IPanelHandler renamePanel;
    private boolean closing;

    WirelessCardDetailsPanel(WirelessCardContainer container) {
        super("wireless_card_details", container);
        child(ButtonWidget.panelCloseButton());
        addButton(RENAME);
        renamePanel = IPanelHandler.simple(this, (parent, player) -> new WirelessCardRenameDialog(this), true);
    }

    @Override
    boolean isDetailView() {
        return true;
    }

    @Override
    int rowTop() {
        return 60;
    }

    @Override
    public boolean disablePanelsBelow() {
        return true;
    }

    @Override
    boolean layoutReady() {
        return !closing && super.layoutReady();
    }

    @Override
    void confirm() {
        action(RENAME);
    }

    @Override
    public void onOpen(ModularScreen screen) {
        closing = false;
        resizeLayout(screen.getScreenArea().width, screen.getScreenArea().height);
        super.onOpen(screen);
        getContext().removeFocus();
    }

    @Override
    public void closeIfOpen() {
        if (!closing) {
            closing = true;
            if (container.display.getBoolean("detail")) container.sync.request(CLOSE_DETAILS, 0, "", 0);
        }
        super.closeIfOpen();
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            closeIfOpen();
            return true;
        }
        return super.onKeyPressed(character, key);
    }

    @Override
    void resizePanel(WirelessCardLayout layout) {
        xSize = Math.max(320, layout.width() - 24);
        ySize = layout.height() - 22;
    }

    @Override
    void layoutButton(WirelessCardButton button, WirelessCardLayout layout) {
        if (button.id == RENAME) button.widget.pos(xSize - 76, 24)
            .size(64, 20);
    }

    @Override
    boolean updatePanel() {
        if (closing || !container.display.getBoolean("detail")) {
            closeIfOpen();
            return false;
        }
        return true;
    }

    @Override
    void action(int id) {
        if (id == BACK) {
            closeIfOpen();
            return;
        }
        if (id == RENAME) {
            if (layoutReady() && container.display.getBoolean("renameable")) renamePanel.openPanel();
            return;
        }
        super.action(id);
    }

    @Override
    void drawHeader(NBTTagCompound data, int textColor) {
        fontRendererObj
            .drawString(fontRendererObj.trimStringToWidth(tr("detail_title"), xSize - 40), 10, 11, textColor);
        fontRendererObj
            .drawString(fontRendererObj.trimStringToWidth(data.getString("name"), xSize - 94), 10, 29, textColor);
    }

    @Override
    void drawRowMetrics(NBTTagCompound data, NBTTagCompound row, int y) {
        fontRendererObj.drawString(
            fontRendererObj.trimStringToWidth(entranceUsage(data, row), contentWidth()),
            CONTENT_X,
            y + 13,
            stateColor(row));
    }
}
