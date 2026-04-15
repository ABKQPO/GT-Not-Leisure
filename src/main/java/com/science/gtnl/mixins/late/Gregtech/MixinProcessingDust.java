package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gregtech.loaders.oreprocessing.ProcessingDust;

@Mixin(value = ProcessingDust.class, remap = false)
public abstract class MixinProcessingDust {

    @Inject(method = "registerOre", at = @At(value = "HEAD"))
    private void injectBeforeGetPlate(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack, CallbackInfo ci) {
        if (aPrefix != OrePrefixes.dust) return;
        if (aMaterial.mStandardMoltenFluid == null) return;
        if (aMaterial == Materials.Rubber || aMaterial == Materials.BorosilicateGlass) return;

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(aMaterial.getDust(1))
            .fluidInputs(aMaterial.getMolten(1 * GTRecipeBuilder.INGOTS))
            .duration(1 * GTRecipeBuilder.SECONDS + 12 * GTRecipeBuilder.TICKS)
            .eut(GTUtility.calculateRecipeEU(aMaterial, 8))
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", aMaterial.mLocalizedName);
    }
}
