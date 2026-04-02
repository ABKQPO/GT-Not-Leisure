package com.science.gtnl.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.apache.commons.lang3.StringUtils;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.packet.MEChiselSyncParallel;
import com.science.gtnl.container.ContainerMEChisel;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.core.localization.GuiText;

public class GuiMEChisel extends AEBaseGui {

    private final TileEntityMEChisel te;
    private MEGuiTextField parallel;
    private int oldParallel;

    public GuiMEChisel(InventoryPlayer inventoryPlayer, TileEntityMEChisel te) {
        super(new ContainerMEChisel(inventoryPlayer, te));
        this.ySize = 166;
        this.te = te;
        this.oldParallel = te.getParallel();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.parallel = new MEGuiTextField(64, 12, I18n.format("text.me_chisel.gui.parallel")) {

            @Override
            public boolean textboxKeyTyped(char keyChar, int keyID) {
                if (!this.isFocused()) {
                    return false;
                } else {
                    String oldText = this.getText();
                    boolean handled = this.field.textboxKeyTyped(keyChar, keyID);
                    if (!handled && (keyID == 28 || keyID == 156 || keyID == 1)) {
                        this.setFocused(false);
                    }

                    if (handled) {
                        this.onTextChange(oldText);
                    }

                    if (!StringUtils.isNumeric(this.getText())) {
                        if (this.getText()
                            .isEmpty()) {
                            this.setText("1");
                            return true;
                        } else {
                            this.setText(oldText);
                            return false;
                        }
                    }

                    return handled;
                }
            }
        };
        this.parallel.setMaxStringLength(10);
        this.parallel.setText(te.getParallel() > 0 ? Integer.toString(te.getParallel()) : "1");
        this.parallel.x = this.guiLeft + 55;
        this.parallel.y = this.guiTop + 62;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.handleTooltip(mouseX, mouseY, this.parallel);
    }

    @Override
    public void mouseClicked(int xPos, int yPos, int button) {
        parallel.mouseClicked(xPos, yPos, button);
        super.mouseClicked(xPos, yPos, button);
    }

    @Override
    public void keyTyped(char character, int key) {
        if (!this.checkHotbarKeys(key)) {
            if (character == ' ') {
                if (this.parallel.getText()
                    .isEmpty() && this.parallel.isFocused()) {
                    return;
                }
            }

            if (!this.parallel.textboxKeyTyped(character, key)) {
                super.keyTyped(character, key);
                return;
            }
            var text = this.parallel.getText();
            long l;
            try {
                l = Long.parseLong(text);
            } catch (NumberFormatException e) {
                l = 1;
                this.parallel.setText("1");
            }
            if (l < 1 || l > Integer.MAX_VALUE) {
                l = 2147483647;
                this.parallel.setText("2147483647");
            }
            te.setParallel((int) l);
            if (te.getParallel() != oldParallel) {
                ScienceNotLeisure.network.sendToServer(new MEChiselSyncParallel(te));
                oldParallel = te.getParallel();
            }
        }
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.getGuiDisplayName(I18n.format("tile.MEChisel.name")), 8, 6, 4210752);
        this.fontRendererObj.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTexture("guis/chest.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        this.parallel.drawTextBox();
    }
}
