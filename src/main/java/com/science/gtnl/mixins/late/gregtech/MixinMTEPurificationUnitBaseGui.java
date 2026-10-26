package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.api.mixinHelper.IPurificationUnitLongParallel;
import com.science.gtnl.common.gui.modularui.multiblock.PurificationUnitGuiHooks;

import gregtech.common.gui.modularui.multiblock.MTEPurificationUnitBaseGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;

@Mixin(value = MTEPurificationUnitBaseGui.class, remap = false)
public abstract class MixinMTEPurificationUnitBaseGui extends MTEMultiBlockBaseGui<MTEPurificationUnitBase<?>> {

    @Unique
    private long gtnl$fallbackMaxParallelLong = 1;

    public MixinMTEPurificationUnitBaseGui(MTEPurificationUnitBase<?> multiblock) {
        super(multiblock);
    }

    @Inject(method = "registerSyncValues", at = @At("TAIL"))
    private void gtnl$registerLongParallelSync(PanelSyncManager syncManager, CallbackInfo ci) {
        LongSyncValue longParallelSyncer = new LongSyncValue(
            this::gtnl$getMaxParallelLong,
            this::gtnl$setMaxParallelLong).allowC2S();
        syncManager.syncValue(PurificationUnitGuiHooks.LONG_PARALLEL_SYNC_KEY, longParallelSyncer);
    }

    @Inject(method = "createParallelButton", at = @At("HEAD"), cancellable = true)
    private void gtnl$createLongParallelButton(PanelSyncManager syncManager, ModularPanel parent,
        CallbackInfoReturnable<IWidget> cir) {
        IWidget button = PurificationUnitGuiHooks.parallelButton(multiblock, syncManager, parent);
        if (button != null) cir.setReturnValue(button);
    }

    @Unique
    private long gtnl$getMaxParallelLong() {
        if (multiblock instanceof IPurificationUnitLongParallel longParallel) {
            return longParallel.gtnl$getMaxParallelLong();
        }
        return gtnl$fallbackMaxParallelLong;
    }

    @Unique
    private void gtnl$setMaxParallelLong(long value) {
        long maxParallel = Math.max(1, value);
        if (multiblock instanceof IPurificationUnitLongParallel longParallel) {
            longParallel.gtnl$setMaxParallelLong(maxParallel);
        } else {
            gtnl$fallbackMaxParallelLong = maxParallel;
        }
    }
}
