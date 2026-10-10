package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.science.gtnl.client.WirelessCardInput;

import appeng.client.render.NetworkVisualiserRender;
import appeng.items.tools.ToolNetworkVisualiser.VLink;
import appeng.items.tools.ToolNetworkVisualiser.VNode;

/** Let the native renderer consume a validated card snapshot without swapping the player's inventory item. */
@Mixin(value = NetworkVisualiserRender.class, remap = false)
public abstract class MixinWirelessVisualiserRender {

    @Inject(method = "networkVisualiser", at = @At("HEAD"))
    private static void gtnl$invalidateSnapshot(ArrayList<VNode> nodes, ArrayList<VLink> links, CallbackInfo ci) {
        // A late native-tool reply must not be mistaken for the card's bound network. The qualified
        // card handler restores its cache only after it has applied its own snapshot to AE's renderer.
        WirelessCardInput.clearVisualisation();
    }

    @ModifyExpressionValue(
        method = "renderNetwork",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;",
            remap = true))
    private ItemStack gtnl$cardVisualiser(ItemStack held) {
        return WirelessCardInput.visualiserStack(held);
    }
}
