package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.yogsothoth.YogTerminalService;

@Mixin(Container.class)
public abstract class MixinContainerYogTerminal {

    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    private void gtnl$lockYogTerminal(int slotId, int button, int mode, EntityPlayer player,
        CallbackInfoReturnable<ItemStack> cir) {
        if (player == null) return;
        if (YogTerminalService.blocksClick((Container) (Object) this, slotId, button, mode, player)) {
            cir.setReturnValue(null);
            return;
        }
        YogTerminalService.beforeClick(player);
    }

    @Inject(method = "slotClick", at = @At("RETURN"))
    private void gtnl$returnYogTerminal(int slotId, int button, int mode, EntityPlayer player,
        CallbackInfoReturnable<ItemStack> cir) {
        if (player != null) YogTerminalService.afterClick((Container) (Object) this, player);
    }
}
