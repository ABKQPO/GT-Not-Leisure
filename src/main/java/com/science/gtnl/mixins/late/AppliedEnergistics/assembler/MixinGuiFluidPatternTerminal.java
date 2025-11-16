package com.science.gtnl.mixins.late.AppliedEnergistics.assembler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.glodblock.github.client.gui.GuiFluidPatternTerminal;
import com.glodblock.github.client.gui.base.FCGuiEncodeTerminal;
import com.glodblock.github.client.gui.container.base.FCContainerEncodeTerminal;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.PktPatternTermUploadPattern;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.widgets.GuiTabButton;

@Mixin(GuiFluidPatternTerminal.class)
public class MixinGuiFluidPatternTerminal extends FCGuiEncodeTerminal {

    @Unique
    private GuiTabButton snl$uploadPatternButton;

    public MixinGuiFluidPatternTerminal(InventoryPlayer inventoryPlayer, ITerminalHost te,
        FCContainerEncodeTerminal c) {
        super(inventoryPlayer, te, c);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void initGui(CallbackInfo ci) {
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

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        updateButton(snl$uploadPatternButton, this.container.isCraftingMode());
    }
}
