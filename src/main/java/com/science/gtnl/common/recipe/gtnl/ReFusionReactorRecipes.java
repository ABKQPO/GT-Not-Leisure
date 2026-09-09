package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class ReFusionReactorRecipes implements IRecipePool {

    public RecipeMap<?> RFRR = GTNLRecipeMaps.RecombinationFusionReactorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility
                    .copyAmountUnsafe(Integer.MAX_VALUE, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1)))
            .itemOutputs(
                GTNLItemList.TrollFace.get(1)
                    .setStackDisplayName(
                        StatCollector.translateToLocal("gtnl.recipe.refusion_reactor.structure_incomplete")))
            .fluidOutputs(
                Materials.MagMatter.getMolten(Integer.MAX_VALUE),
                Materials.MHDCSM.getMolten(Integer.MAX_VALUE),
                Materials.Universium.getMolten(Integer.MAX_VALUE),
                Materials.WhiteDwarfMatter.getMolten(Integer.MAX_VALUE),
                Materials.BlackDwarfMatter.getMolten(Integer.MAX_VALUE),
                Materials.SpaceTime.getMolten(Integer.MAX_VALUE),
                Materials.TranscendentMetal.getMolten(Integer.MAX_VALUE),
                Materials.Eternity.getMolten(Integer.MAX_VALUE),
                Materials.PrimordialMatter.getFluid(Integer.MAX_VALUE),
                Materials.Space.getMolten(Integer.MAX_VALUE),
                Materials.Time.getMolten(Integer.MAX_VALUE),
                Materials.SixPhasedCopper.getMolten(Integer.MAX_VALUE),
                Materials.StargateCrystalSlurry.getFluid(Integer.MAX_VALUE),
                Materials.Antimatter.getFluid(Integer.MAX_VALUE))
            .outputChances(1)
            .duration(1)
            .eut(0)
            .addTo(RFRR);
    }
}
