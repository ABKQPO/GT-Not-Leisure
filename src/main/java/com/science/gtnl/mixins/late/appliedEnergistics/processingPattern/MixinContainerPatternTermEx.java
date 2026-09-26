package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;

import appeng.api.storage.ITerminalHost;
import appeng.api.storage.StorageName;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.implementations.ContainerPatternTermEx;
import appeng.util.Platform;

@Mixin(value = ContainerPatternTermEx.class, remap = false)
public abstract class MixinContainerPatternTermEx extends ContainerPatternTerm {

    @Unique
    private int gtnl$openedPages;

    protected MixinContainerPatternTermEx(InventoryPlayer inventoryPlayer, ITerminalHost terminalHost) {
        super(inventoryPlayer, terminalHost, false);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gtnl$rememberPageCount(InventoryPlayer inventoryPlayer, ITerminalHost terminalHost,
        CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        this.gtnl$openedPages = ProcessingPatternCapacity.pageCount();
    }

    @Inject(method = "detectAndSendChanges", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$closeStaleContainer(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (Platform.isServer() && (this.gtnl$openedPages != ProcessingPatternCapacity.pageCount()
            || this.inputsSync.get() != this.getPatternTerminal()
                .getAEInventoryByName(StorageName.CRAFTING_INPUT))) {
            this.getInventoryPlayer().player.closeScreen();
            callbackInfo.cancel();
        }
    }

    /** Returns the configured number of input pages. */
    @Inject(method = "getPatternInputPages", at = @At("HEAD"), cancellable = true)
    private void gtnl$getPatternInputPages(CallbackInfoReturnable<Integer> callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        callbackInfo
            .setReturnValue(this.gtnl$openedPages == 0 ? ProcessingPatternCapacity.pageCount() : this.gtnl$openedPages);
    }

    /** Returns the configured number of output pages. */
    @Inject(method = "getPatternOutputPages", at = @At("HEAD"), cancellable = true)
    private void gtnl$getPatternOutputPages(CallbackInfoReturnable<Integer> callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        callbackInfo
            .setReturnValue(this.gtnl$openedPages == 0 ? ProcessingPatternCapacity.pageCount() : this.gtnl$openedPages);
    }
}
