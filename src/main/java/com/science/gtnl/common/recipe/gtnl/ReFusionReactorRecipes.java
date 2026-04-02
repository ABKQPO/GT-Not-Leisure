package com.science.gtnl.common.recipe.gtnl;

import static com.science.gtnl.utils.enums.GTNLItemList.TrollFace;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
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
                TrollFace.get(1)
                    .setStackDisplayName(StatCollector.translateToLocal("RFRRRecipes.1")))
            .fluidOutputs(
                MaterialsUEVplus.MagMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Universium.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.WhiteDwarfMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.TranscendentMetal.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Eternity.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.PrimordialMatter.getFluid(Integer.MAX_VALUE),
                MaterialsUEVplus.Space.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.Time.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.SixPhasedCopper.getMolten(Integer.MAX_VALUE),
                MaterialsUEVplus.StargateCrystalSlurry.getFluid(Integer.MAX_VALUE),
                MaterialsUEVplus.Antimatter.getFluid(Integer.MAX_VALUE))
            .outputChances(1)
            .duration(1)
            .eut(0)
            .addTo(RFRR);
    }
}
