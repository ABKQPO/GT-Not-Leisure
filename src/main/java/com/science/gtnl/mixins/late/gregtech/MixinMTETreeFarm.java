package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.recipe.gtnl.SteamWoodcutterRecipes;

import gregtech.common.tileentities.machines.multi.MTETreeFarm;

@Mixin(value = MTETreeFarm.class, remap = false)
public class MixinMTETreeFarm {

    @Inject(
        method = "registerTreeProducts(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V",
        at = @At("TAIL"))
    private static void injectSteamWoodcutterRecipe(ItemStack saplingIn, ItemStack log, ItemStack saplingOut,
        ItemStack leaves, ItemStack fruit, CallbackInfo ci) {
        SteamWoodcutterRecipes.addFromTreeFarm(saplingIn, log, saplingOut, leaves, fruit);
    }
}
