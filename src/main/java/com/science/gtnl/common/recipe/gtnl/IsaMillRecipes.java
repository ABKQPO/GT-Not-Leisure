package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.metadata.IsaMillMetadata;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.util.minecraft.MaterialUtils;

public class IsaMillRecipes implements IRecipePool {

    public IsaMillMetadata ISAMILL_TIER = IsaMillMetadata.INSTANCE;
    public RecipeMap<?> IsaMR = RecipePool.IsaMillRecipes;

    @Override
    public void loadRecipes() {
        addIsaMillSet(Materials.Nickel);
        addIsaMillSet(Materials.Platinum);
        addIsaMillSet(Materials.Almandine);
        addIsaMillSet(Materials.Chalcopyrite);
        addIsaMillSet(Materials.Grossular);
        addIsaMillSet(Materials.Pyrope);
        addIsaMillSet(Materials.Spessartine);
        addIsaMillSet(Materials.Sphalerite);
        addIsaMillSet(Materials.Pentlandite);
        addIsaMillSet(Materials.Monazite);
        addIsaMillSet(Materials.Redstone);
        addIsaMillSet(Materials.NaquadahEnriched);

        addIsaMillRecipeCustom(Materials.Netherrack, new ItemStack(Blocks.netherrack, 16), 10, 96, 100, 1, 2400);
        addIsaMillRecipeCustom(Materials.Netherrack, new ItemStack(Blocks.netherrack, 16), 1, 64, 100, 2, 1200);
    }

    public void addIsaMillSet(Materials material) {
        Object[][] combos = { { OrePrefixes.ore, 1, 96, 100, 2, 4800 }, { OrePrefixes.ore, 10, 72, 100, 1, 2400 },
            { OrePrefixes.ore, 1, 96, 100, 2, 4800 }, { OrePrefixes.ore, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreNetherrack, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreNetherrack, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreEndstone, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreEndstone, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreBlackgranite, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreBlackgranite, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreRedgranite, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreRedgranite, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreMarble, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreMarble, 10, 72, 100, 1, 2400 },
            { OrePrefixes.oreBasalt, 1, 96, 100, 2, 4800 }, { OrePrefixes.oreBasalt, 10, 72, 100, 1, 2400 },
            { OrePrefixes.rawOre, 1, 48, 50, 2, 2400 }, { OrePrefixes.rawOre, 10, 36, 50, 1, 1200 },
            { OrePrefixes.crushed, 1, 48, 50, 2, 2400 }, { OrePrefixes.crushed, 10, 36, 50, 1, 1200 } };

        for (Object[] c : combos) {
            addIsaMillRecipe(material, (OrePrefixes) c[0], (int) c[1], (int) c[2], (int) c[3], (int) c[4], (int) c[5]);
        }
    }

    public void addIsaMillRecipe(Materials material, OrePrefixes prefix, int circuitNumber, int outputAmount,
        int fluidAmount, int tier, int duration) {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(circuitNumber),
                GTOreDictUnificator.get(prefix, material, prefix == OrePrefixes.rawOre ? 16L : 1L))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    outputAmount,
                    MaterialUtils.generateMaterialFromGtENUM(material)
                        .getMilled(1)))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", fluidAmount))
            .metadata(ISAMILL_TIER, tier)
            .duration(duration)
            .eut(1920)
            .addTo(IsaMR);
    }

    public void addIsaMillRecipeCustom(Materials material, ItemStack custom, int circuitNumber, int outputAmount,
        int fluidAmount, int tier, int duration) {
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(circuitNumber), custom)
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    outputAmount,
                    MaterialUtils.generateMaterialFromGtENUM(material)
                        .getMilled(1)))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", fluidAmount))
            .metadata(ISAMILL_TIER, tier)
            .duration(duration)
            .eut(1920)
            .addTo(IsaMR);
    }

}
