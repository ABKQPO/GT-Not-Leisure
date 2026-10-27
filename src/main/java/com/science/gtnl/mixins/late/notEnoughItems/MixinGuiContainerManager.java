package com.science.gtnl.mixins.late.notEnoughItems;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IInfinityChestGui;
import com.science.gtnl.compat.avaritiaddons.InfinityChestCompat;

import codechicken.nei.guihook.GuiContainerManager;

@Mixin(value = GuiContainerManager.class, remap = false)
public class MixinGuiContainerManager {

    @Shadow(remap = false)
    public GuiContainer window;

    @Inject(
        method = "handleSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;windowClick(IIIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
            remap = true),
        cancellable = true,
        remap = false)
    public void onBeforeWindowClick(int slotIndex, int button, int modifiers, CallbackInfo ci) {
        if (!(this instanceof IInfinityChestGui)) return;
        InfinityChestCompat.sendSlotClick(window, slotIndex, button, modifiers);
        ci.cancel();
    }

}
