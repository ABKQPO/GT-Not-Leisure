package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;

import appeng.core.localization.GuiText;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessCardGroupPanel extends WirelessCardPanel {

    private boolean closing;

    public WirelessCardGroupPanel(WirelessCardContainer container) {
        super("wireless_card_group", container);
        child(ButtonWidget.panelCloseButton());
        addRowButtons(OPEN_DETAILS);
    }

    @Override
    boolean isDetailView() {
        return false;
    }

    @Override
    boolean isGroupView() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    int rowTop() {
        return 60;
    }

    @Override
    public NBTTagCompound view() {
        NBTTagCompound data = container.clientView()
            .groupDisplay();
        return data.getBoolean("group") ? data : snapshot;
    }

    @Override
    public boolean layoutReady() {
        return !closing && container.clientView()
            .groupDisplay()
            .getBoolean("group") && super.layoutReady();
    }

    @Override
    public void onOpen(ModularScreen screen) {
        closing = false;
        resizeLayout(screen.getScreenArea().width, screen.getScreenArea().height);
        super.onOpen(screen);
    }

    @Override
    public void closeIfOpen() {
        if (!closing) {
            closing = true;
            container.clientView()
                .closeGroup();
        }
        super.closeIfOpen();
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        var focused = getContext().getFocusedWidget()
            .getElement();
        if (focused != null && focused.getPanel() != this) return false;
        if (key == Keyboard.KEY_ESCAPE) {
            closeIfOpen();
            return true;
        }
        return isBelowMouse() && super.onKeyPressed(character, key);
    }

    @Override
    void confirm() {}

    @Override
    void resizePanel(WirelessCardLayout layout) {
        xSize = layout.width() - 24;
        ySize = layout.height() - 22;
    }

    @Override
    void layoutButton(WirelessCardButton button, WirelessCardLayout layout) {}

    @Override
    boolean updatePanel() {
        if (closing || !container.clientView()
            .groupDisplay()
            .getBoolean("group")) {
            super.closeIfOpen();
            return false;
        }
        return true;
    }

    @Override
    void drawHeader(NBTTagCompound data, int textColor) {
        ItemStack icon = ItemStack.loadItemStackFromNBT(data.getCompoundTag("groupIcon"));
        String title = icon == null ? tr("title") : icon.getDisplayName();
        if (data.getBoolean("sharedGroup")) title = tr("shared", title);
        if (data.getBoolean("typeGroup")) title = GuiText.Types.getLocal() + " > " + title;
        fontRendererObj.drawString(fontRendererObj.trimStringToWidth(title, xSize - 40), 10, 11, textColor);
        fontRendererObj.drawString(
            fontRendererObj.trimStringToWidth(data.getString("groupPositionLabel"), xSize - 20),
            10,
            29,
            textColor);
    }
}
