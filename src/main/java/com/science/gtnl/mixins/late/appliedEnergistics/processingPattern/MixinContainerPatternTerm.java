package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IProcessingPatternInventoryAccess;
import com.science.gtnl.api.mixinHelper.IProcessingPatternInventoryBatchAccess;
import com.science.gtnl.api.mixinHelper.IProcessingPatternOverflowAccess;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;

import appeng.api.parts.IPatternTerminal;
import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.parts.reporting.PartPatternTerminalEx;

@Mixin(value = ContainerPatternTerm.class, remap = false)
public abstract class MixinContainerPatternTerm extends ContainerMEMonitorable {

    @Shadow
    public abstract IPatternTerminal getPatternTerminal();

    protected MixinContainerPatternTerm(InventoryPlayer inventoryPlayer, ITerminalHost terminalHost) {
        super(inventoryPlayer, terminalHost, false);
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void gtnl$clearOverflow(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.getPatternTerminal() instanceof IProcessingPatternInventoryBatchAccess batchAccess) {
            batchAccess.gtnl$beginInventoryUpdate();
        }
        if (this.getPatternTerminal() instanceof IProcessingPatternOverflowAccess inventoryAccess) {
            inventoryAccess.gtnl$clearProcessingPatternOverflow();
        }
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void gtnl$finishClear(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.getPatternTerminal() instanceof IProcessingPatternInventoryBatchAccess batchAccess) {
            batchAccess.gtnl$endInventoryUpdate();
        }
    }

    @Inject(
        method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/parts/IPatternTerminal;getInventoryByName(Ljava/lang/String;)Lnet/minecraft/inventory/IInventory;"))
    private void gtnl$prepareProcessingPatternInventory(InventoryPlayer inventoryPlayer, ITerminalHost terminalHost,
        boolean craftingModeSupport, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (terminalHost instanceof PartPatternTerminalEx) {
            ((IProcessingPatternInventoryAccess) terminalHost).gtnl$resizeProcessingPatternInventory();
        }
    }
}
