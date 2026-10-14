package com.science.gtnl.mixins.late.inventoryBogoSorter;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.bogosorter.ShortcutHandler;
import com.science.gtnl.client.meminiterminal.MiniTerminalBogoCompat;

@Mixin(value = ShortcutHandler.class, remap = false)
public abstract class MixinMiniTerminalShortcuts {

    @Inject(
        method = "moveSingleItem(Lnet/minecraft/client/gui/inventory/GuiContainer;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    private static void gtnl$moveSingleToMiniTerminal(GuiContainer gui, boolean emptyOnly,
        CallbackInfoReturnable<Boolean> cir) {
        if (MiniTerminalBogoCompat.shouldPrioritizeContainerInteraction(gui)) {
            cir.setReturnValue(false);
            return;
        }
        if (MiniTerminalBogoCompat.moveSingleItem(gui, emptyOnly)) cir.setReturnValue(true);
    }

    @Inject(
        method = "moveAllItems(Lnet/minecraft/client/gui/inventory/GuiContainer;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    private static void gtnl$moveAllToMiniTerminal(GuiContainer gui, boolean sameItemOnly,
        CallbackInfoReturnable<Boolean> cir) {
        if (MiniTerminalBogoCompat.shouldPrioritizeContainerInteraction(gui)) {
            cir.setReturnValue(false);
            return;
        }
        if (MiniTerminalBogoCompat.moveAllItems(gui, sameItemOnly)) cir.setReturnValue(true);
    }
}
