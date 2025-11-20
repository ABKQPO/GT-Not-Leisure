package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.enums.GTValues.RA;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.TierEU;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm;

@Mixin(value = MTETreeFarm.class, remap = false)
public class MixinMTETreeFarm {

    @Inject(
        method = "registerTreeProducts(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V",
        at = @At("TAIL"))
    private static void injectSteamWoodcutterRecipe(ItemStack saplingIn, ItemStack log, ItemStack saplingOut,
        ItemStack leaves, ItemStack fruit, CallbackInfo ci) {

        // Hijacking this for steam update. Yes this is gross, no I don't care, it's april fools baybee

        if (log == null) return;

        ItemStack newSaplingIn = saplingIn.copy();
        newSaplingIn.stackSize = 0;

        List<ItemStack> outputs = new ArrayList<>();
        List<Integer> chances = new ArrayList<>();

        ItemStack newLogOut = log.copy();
        newLogOut.stackSize = 48;
        outputs.add(newLogOut);
        chances.add(10000);

        if (saplingOut != null) {
            ItemStack newSaplingOut = saplingOut.copy();
            newSaplingOut.stackSize = 2;
            outputs.add(newSaplingOut);
            chances.add(1000);
        }
        if (leaves != null) {
            ItemStack newLeavesOut = leaves.copy();
            newLeavesOut.stackSize = 6;
            outputs.add(newLeavesOut);
            chances.add(1500);
        }
        if (fruit != null) {
            ItemStack newFruitOut = fruit.copy();
            newFruitOut.stackSize = 4;
            outputs.add(newFruitOut);
            chances.add(3000);
        }

        RA.stdBuilder()
            .itemInputs(newSaplingIn)
            .itemOutputs(outputs.toArray(new ItemStack[0]))
            .outputChances(
                chances.stream()
                    .mapToInt(i -> i)
                    .toArray())
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipePool.SteamWoodcutterRecipes);
    }
}
