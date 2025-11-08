package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.*;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.Particle;

public class CyclotronRecipes implements IRecipePool {

    public RecipeMap<?> CR = GTPPRecipeMaps.cyclotronRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 16))
            .itemOutputs(
                MaterialPool.Technetium.get(OrePrefixes.dust, 6),
                WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 6),
                WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 6),
                Particle.getBaseParticle(Particle.PROTON),
                Particle.getBaseParticle(Particle.NEUTRON))
            .outputChances(2500, 1500, 1000, 500, 150, 50)
            .fluidInputs(Materials.Deuterium.getGas(500))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CR);
    }
}
