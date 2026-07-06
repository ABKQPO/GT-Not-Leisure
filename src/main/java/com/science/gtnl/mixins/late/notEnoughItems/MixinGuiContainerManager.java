package com.science.gtnl.mixins.late.notEnoughItems;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IInfinityChestGui;

import codechicken.nei.guihook.GuiContainerManager;
import wanion.avaritiaddons.Avaritiaddons;
import wanion.avaritiaddons.network.InfinityChestClick;

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
    public void onBeforeWindowClick(int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (!(this instanceof IInfinityChestGui)) return;
        short nextTransactionID = window.mc.thePlayer.openContainer.getNextTransactionID(window.mc.thePlayer.inventory);

        ItemStack itemStack = window.mc.thePlayer.openContainer
            .slotClick(slotId, clickedButton, clickType, window.mc.thePlayer);

        Avaritiaddons.networkWrapper.sendToServer(
            new InfinityChestClick(
                window.inventorySlots.windowId,
                slotId,
                clickedButton,
                clickType,
                itemStack,
                nextTransactionID));

        ci.cancel();
    }

}
