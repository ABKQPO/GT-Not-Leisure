package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.me.ProcessingPatternSlotLayout;
import com.science.gtnl.common.me.processingPattern.ProcessingPatternCapacity;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.implementations.GuiPatternTermEx;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.container.implementations.ContainerPatternTermEx;

@Mixin(value = GuiPatternTermEx.class, remap = false)
public abstract class MixinGuiPatternTermEx extends GuiPatternTerm {

    protected MixinGuiPatternTermEx(InventoryPlayer inventoryPlayer, ITerminalHost terminalHost) {
        super(inventoryPlayer, terminalHost);
    }

    @Shadow
    @Final
    private ContainerPatternTermEx container;

    @Shadow
    @Final
    private GuiScrollbar processingScrollBar;

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void gtnl$configureProcessingScrollbar(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        this.processingScrollBar.setRange(0, this.container.getPatternInputPages() - 1, 1);
        this.processingScrollBar.setCurrentScroll(
            Math.max(0, Math.min(this.container.getPatternInputPages() - 1, this.container.activePageSync.get())));
    }

    @Inject(method = "updateSlotVisibility", at = @At("HEAD"), cancellable = true)
    private void gtnl$updateSlotVisibility(CallbackInfo callbackInfo) {
        if (ProcessingPatternSlotLayout.update(
            this.container,
            this.craftingSlots,
            this.outputSlots,
            this.rows * 18,
            getInputSlotOffsetX(),
            getInputSlotOffsetY(),
            getOutputSlotOffsetX(),
            getOutputSlotOffsetY())) callbackInfo.cancel();
    }
}
