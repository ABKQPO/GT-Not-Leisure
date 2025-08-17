package com.science.gtnl.common.recipe.GTNL;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class RareEarthCentrifugalRecipes implements IRecipePool {

    final RecipeMap<?> RECR = RecipeRegister.RareEarthCentrifugalRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(MaterialPool.RareEarthMetal.get(OrePrefixes.dust, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Praseodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Terbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Dysprosium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thulium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Scandium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1))
            .specialValue(0)
            .noOptimize()
            .duration(800)
            .eut(491520)
            .addTo(RECR);
    }
}
