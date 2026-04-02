package com.science.gtnl.mixins.late.Gregtech.AssLineRemover;

import static com.science.gtnl.utils.recipes.AssLineRecipeHook.RECIPE_TO_REMOVE;

import java.util.Objects;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.recipes.AssLineRecipeHook;

import cpw.mods.fml.common.registry.GameData;
import tectech.recipe.TTRecipeAdder;

@Mixin(value = TTRecipeAdder.class, remap = false)
public class MixinTTRecipeAdder {

    @Inject(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Lnet/minecraft/item/ItemStack;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private static void science$TTHook$1(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, ItemStack[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt,
        CallbackInfoReturnable<Boolean> cir) {
        if (science$TTCheck(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aOutput,
            assDuration,
            assEUt)) cir.setReturnValue(true);
    }

    @Inject(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Ljava/lang/Object;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private static void science$TTHook$2(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, Object[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt,
        CallbackInfoReturnable<Boolean> cir) {
        if (science$TTCheck(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aOutput,
            assDuration,
            assEUt)) cir.setReturnValue(true);
    }

    @Unique
    private static boolean science$TTCheck(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, ItemStack aOutput, int assDuration,
        int assEUt) {

        if (aResearchItem == null || aOutput == null) return false;

        String name = GameData.getItemRegistry()
            .getNameForObject(aResearchItem.getItem());
        int amount = aResearchItem.stackSize;
        int meta = Items.feather.getDamage(aResearchItem);
        // noinspection StringBufferReplaceableByString
        String key = new StringBuilder().append(name)
            .append("@")
            .append(amount)
            .append("@")
            .append(meta)
            .append("@")
            .append(totalComputationRequired)
            .append("@")
            .append(computationRequiredPerSec)
            .append("@")
            .append(researchEUt)
            .append("@")
            .append(researchAmperage)
            .toString();

        if (RECIPE_TO_REMOVE.containsKey(key)) {
            name = GameData.getItemRegistry()
                .getNameForObject(aOutput.getItem());
            amount = aOutput.stackSize;
            meta = Items.feather.getDamage(aOutput);
            // noinspection StringBufferReplaceableByString
            String outputItemKey = new StringBuilder().append(name)
                .append("@")
                .append(amount)
                .append("@")
                .append(meta)
                .append("@")
                .append(assDuration)
                .append("@")
                .append(assEUt)
                .toString();

            for (AssLineRecipeHook.RemovalRecipe removalRecipe : RECIPE_TO_REMOVE.get(key)) {
                if (Objects.equals(removalRecipe.removalKey, outputItemKey)) {
                    removalRecipe.hasNotBeenRemoved = false;
                    return true;
                }
            }
        }
        return false;
    }
}
