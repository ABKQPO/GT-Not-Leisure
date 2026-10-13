package com.science.gtnl.mixins.late.inventoryBogoSorter;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.bogosorter.ShortcutHandler;
import com.science.gtnl.client.metoolbox.ToolboxBogoCompat;

@Mixin(value = ShortcutHandler.class, remap = false)
public abstract class MixinToolboxShortcuts {

    @Inject(
        method = "moveSingleItem(Lnet/minecraft/client/gui/inventory/GuiContainer;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    private static void gtnl$moveSingleToToolbox(GuiContainer gui, boolean emptyOnly,
        CallbackInfoReturnable<Boolean> cir) {
        if (ToolboxBogoCompat.moveSingleItem(gui, emptyOnly)) cir.setReturnValue(true);
    }

    @Inject(
        method = "moveAllItems(Lnet/minecraft/client/gui/inventory/GuiContainer;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    private static void gtnl$moveAllToToolbox(GuiContainer gui, boolean sameItemOnly,
        CallbackInfoReturnable<Boolean> cir) {
        if (ToolboxBogoCompat.moveAllItems(gui, sameItemOnly)) cir.setReturnValue(true);
    }
}
