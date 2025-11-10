package com.science.gtnl.mixins.late.assembler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.PktPatternTermUploadPattern;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.implementations.ContainerPatternTerm;

@Mixin(GuiPatternTerm.class)
public class MixinGuiPatternTerm extends GuiMEMonitorable {

    @Final
    @Shadow(remap = false)
    private ContainerPatternTerm container;

    @Unique
    private GuiTabButton snl$uploadPatternButton;

    @SuppressWarnings("DataFlowIssue")
    public MixinGuiPatternTerm() {
        super(null, null);
    }

    @Inject(
        method = "initGui",
        at = @At(value = "INVOKE", target = "Lappeng/client/gui/widgets/GuiImgButton;setHalfSize(Z)V", remap = false))
    private void injectInitGui(final CallbackInfo ci) {
        snl$uploadPatternButton = new GuiTabButton(
            this.guiLeft + 173,
            this.guiTop + this.ySize - 155,
            GTNLItemList.AssemblerMatrix.get(1),
            I18n.format("gui.AssemblerMatrix.button.upload_pattern"),
            itemRender);
        this.buttonList.add(this.snl$uploadPatternButton);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void injectActionPerformed(final GuiButton btn, final CallbackInfo ci) {
        if (btn == snl$uploadPatternButton) {
            ScienceNotLeisure.network.sendToServer(new PktPatternTermUploadPattern());
            ci.cancel();
        }
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void injectDrawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY,
        final CallbackInfo ci) {
        snl$uploadPatternButton.visible = this.container.isCraftingMode();
    }

}
