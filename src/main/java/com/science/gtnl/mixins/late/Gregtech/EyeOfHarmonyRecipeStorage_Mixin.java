package com.science.gtnl.mixins.late.Gregtech;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.Utils.recipes.EyeOfHarmonyRecipeFactory;
import com.science.gtnl.common.recipe.GregTech.CustomEyeOfHarmonyRecipe;

import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.recipe.EyeOfHarmonyRecipeStorage;

@SuppressWarnings("UnusedMixin")
@Mixin(value = EyeOfHarmonyRecipeStorage.class, remap = false)
public class EyeOfHarmonyRecipeStorage_Mixin {

    @Final
    @Shadow
    private HashMap<String, EyeOfHarmonyRecipe> recipeHashMap;

    @Redirect(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Ljava/util/HashMap;values()Ljava/util/Collection;", ordinal = 0))
    private Collection<EyeOfHarmonyRecipe> ensurePreRegistrationValuesCall(
        HashMap<String, EyeOfHarmonyRecipe> originalMap) {
        CustomEyeOfHarmonyRecipe.loadRecipes();
        originalMap.putAll(EyeOfHarmonyRecipeFactory.customRecipeHashMap);
        return originalMap.values();
    }

    @Inject(method = "recipeLookUp", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCustomRecipeLookup(ItemStack aStack, CallbackInfoReturnable<EyeOfHarmonyRecipe> cir) {
        String customKey = aStack.getUnlocalizedName() + "_" + aStack.getItemDamage();

        EyeOfHarmonyRecipe customRecipe = recipeHashMap.get(customKey);

        if (customRecipe != null) {
            cir.setReturnValue(customRecipe);
            cir.cancel();
        }
    }
}
