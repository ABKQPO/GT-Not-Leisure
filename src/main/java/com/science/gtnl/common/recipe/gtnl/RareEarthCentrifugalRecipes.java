package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.loader.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class RareEarthCentrifugalRecipes implements IRecipePool {

    public RecipeMap<?> RECR = RecipePool.RareEarthCentrifugalRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.RareEarthMetal.get(OrePrefixes.dust, 1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Lanthanum, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Neodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Promethium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Samarium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Europium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Praseodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Gadolinium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Terbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Dysprosium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Holmium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Erbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Thulium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Ytterbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Scandium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Lutetium, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Yttrium, 1))
            .duration(200)
            .eut(491520)
            .addTo(RECR);
    }
}
