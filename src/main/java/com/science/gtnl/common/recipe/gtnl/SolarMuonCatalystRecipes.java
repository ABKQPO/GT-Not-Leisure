package com.science.gtnl.common.recipe.gtnl;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.SolorMuonCatalystMetadata;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.material.MaterialsElements;

public class SolarMuonCatalystRecipes implements IRecipePool {

    public RecipeMap<?> SMCR = GTNLRecipeMaps.SolarMuonCatalystRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(ItemList.Quark_Creation_Catalyst_Strange.get(0))
            .fluidInputs(
                Materials.Lutetium.getPlasma(2304),
                Materials.Uranium.getPlasma(2304),
                Materials.Europium.getPlasma(2304),
                Materials.Silicon.getPlasma(2304),
                Materials.Silver.getPlasma(2304),
                Materials.Indium.getPlasma(2304),
                Materials.Hydrogen.getPlasma(16000),
                Materials.Fluorine.getPlasma(16000))
            .fluidOutputs(Materials.QuarkGluonPlasma.getFluid(6400000))
            .eut(2013265192)
            .duration(200)
            .addTo(SMCR);

        RecipeBuilder.builder()
            .metadata(SolorMuonCatalystMetadata.INSTANCE, true)
            .itemInputs(ItemList.Quark_Creation_Catalyst_Strange.get(0))
            .fluidInputs(
                Materials.Space.getMolten(100),
                Materials.Time.getMolten(50),
                Materials.Ichorium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.HYPOGEN.getPlasma(), 1440),
                Materials.Flerovium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getPlasma(), 1440),
                Materials.Bedrockium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.DRAGON_METAL.getPlasma(), 1440))
            .fluidOutputs(Materials.MagMatter.getMolten(64000))
            .eut(2013265192)
            .duration(200)
            .addTo(SMCR);
    }
}
