package com.science.gtnl.mixins.late.wirelessCraftingTerminal;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.yogsothoth.YogTerminals;

@Pseudo
@Mixin(targets = "net.p455w0rd.wirelesscraftingterminal.common.utils.RandomUtils", remap = false)
public abstract class MixinRandomUtilsYogTerminal {

    @Unique
    private static Class<?> gtnl$craftingTerminal;

    @Inject(
        method = "getWirelessTerm(Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true)
    private static void gtnl$openedYogTerminal(InventoryPlayer inventory, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack opened = YogTerminals.opened(inventory.player);
        if (opened == null) return;
        if (gtnl$craftingTerminal == null) {
            try {
                gtnl$craftingTerminal = Class
                    .forName("net.p455w0rd.wirelesscraftingterminal.api.IWirelessCraftingTerminalItem");
            } catch (ClassNotFoundException exception) {
                return;
            }
        }
        if (gtnl$craftingTerminal.isInstance(opened.getItem())) cir.setReturnValue(opened);
    }
}
