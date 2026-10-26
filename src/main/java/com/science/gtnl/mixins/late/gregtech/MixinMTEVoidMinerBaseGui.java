package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.modularui.multiblock.VoidMinerGuiHooks;
import com.science.gtnl.compat.vmtweak.VoidMinerTweak;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import gregtech.common.gui.modularui.multiblock.MTEVoidMinerBaseGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

@Mixin(value = MTEVoidMinerBaseGui.class, remap = false)
public abstract class MixinMTEVoidMinerBaseGui extends MTEMultiBlockBaseGui<MTEVoidMinerBase> {

    @Unique
    private static boolean gtnl$enableMixin = VoidMinerTweak.enabled();

    private MixinMTEVoidMinerBaseGui() {
        super(null);
    }

    @Inject(method = "createRightPanelGapRow", at = @At("HEAD"), require = 1, remap = false)
    private void gtnl$refreshFilterDropMap(ModularPanel parent, PanelSyncManager syncManager,
        CallbackInfoReturnable<Flow> cir) {
        if (!gtnl$enableMixin) return;
        VoidMinerGuiHooks.refreshClientDropMap(this.multiblock);
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        ListWidget<IWidget, ?> list = super.createTerminalTextWidget(syncManager, parent);
        if (gtnl$enableMixin) {
            VoidMinerGuiHooks.addOverrideLine(list, this.multiblock, baseMetaTileEntity, syncManager);
        }
        return list;
    }

    @Inject(method = "createRightPanelGapRow", at = @At("RETURN"), require = 1, remap = false)
    private void gtnl$onCreateRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager,
        CallbackInfoReturnable<Flow> cir) {
        if (!gtnl$enableMixin) return;
        VoidMinerGuiHooks.watchDimensionChanges(this.multiblock, syncManager);
    }

    @Inject(method = "createFilterPopup", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void gtnl$onCreateFilterPopup(PanelSyncManager syncManager, IPanelHandler panelHandler,
        CallbackInfoReturnable<ModularPanel> cir) {
        if (!gtnl$enableMixin) return;
        ModularPanel emptyPanel = VoidMinerGuiHooks.filterPopup(this.multiblock);
        if (emptyPanel != null) cir.setReturnValue(emptyPanel);
    }
}
