package com.science.gtnl.mixins.late.assembler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.glodblock.github.client.gui.GuiFluidPatternTerminal;
import com.glodblock.github.client.gui.GuiFluidPatternWireless;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.PktPatternTermUploadPattern;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.widgets.GuiTabButton;

@Mixin(GuiFluidPatternWireless.class)
public class MixinGuiFluidPatternWireless extends GuiFluidPatternTerminal {

    @Unique
    private GuiTabButton snl$uploadPatternButton;

    public MixinGuiFluidPatternWireless(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Intrinsic
    public void initGui() {
        super.initGui();
        this.pinsStateButton.yPosition = this.guiTop + this.ySize - 64;
        this.buttonList.add(
            snl$uploadPatternButton = new GuiTabButton(
                this.guiLeft + 173,
                this.guiTop + this.ySize - 155,
                GTNLItemList.AssemblerMatrix.get(1),
                I18n.format("gui.AssemblerMatrix.button.upload_pattern"),
                itemRender));
    }

    @Intrinsic
    protected void actionPerformed(final GuiButton btn) {
        if (btn == snl$uploadPatternButton) {
            ScienceNotLeisure.network.sendToServer(new PktPatternTermUploadPattern());
            return;
        }
        super.actionPerformed(btn);
    }

    @Intrinsic
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        updateButton(snl$uploadPatternButton, this.container.isCraftingMode());
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
    }
}
