package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.implementations.GuiPatternTermEx;
import appeng.client.gui.slots.VirtualMEPhantomSlot;
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
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        int inputPages = this.container.getPatternInputPages();
        int outputPages = this.container.getPatternOutputPages();
        int activePage = Math.max(0, Math.min(inputPages - 1, this.container.activePageSync.get()));
        boolean inverted = this.container.invertedSync.get();
        this.gtnl$layoutSlots(
            this.craftingSlots,
            !inverted,
            inputPages,
            activePage,
            getInputSlotOffsetX(),
            this.rows * 18 + getInputSlotOffsetY());
        this.gtnl$layoutSlots(
            this.outputSlots,
            inverted,
            outputPages,
            activePage,
            inverted ? getOutputSlotOffsetX() : 112,
            this.rows * 18 + getOutputSlotOffsetY());
        callbackInfo.cancel();
    }

    private void gtnl$layoutSlots(VirtualMEPhantomSlot[] slots, boolean largeSide, int pages, int activePage,
        int offsetX, int offsetY) {
        int slotsPerPage = largeSide ? ProcessingPatternCapacity.SLOTS_PER_PAGE
            : ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE;
        int firstSlot = activePage * slotsPerPage;
        int endSlot = Math.min(firstSlot + slotsPerPage, pages * slotsPerPage);
        for (int index = 0; index < slots.length; index++) {
            VirtualMEPhantomSlot slot = slots[index];
            boolean hidden = index < firstSlot || index >= endSlot;
            slot.setHidden(hidden);
            if (!hidden) {
                int pageSlot = index - firstSlot;
                slot.setX(offsetX + (largeSide ? pageSlot % 4 : 0) * 18);
                slot.setY(offsetY + (largeSide ? pageSlot / 4 : pageSlot) * 18);
            }
        }
    }
}
