package com.science.gtnl.common.recipe.oreDictionary;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;

public class WoodDistillationRecipes implements IOreRecipeRegistrator {

    public WoodDistillationRecipes() {
        OrePrefixes.log.add(this);
    }

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        if (aOreDictName.equals("logWood")) {
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.copyAmount(16, aStack))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DarkAsh, 4L))
                .fluidInputs(Materials.Nitrogen.getGas(1000))
                .fluidOutputs(
                    Materials.Water.getFluid(400),
                    Materials.Methanol.getFluid(240),
                    Materials.Benzene.getFluid(175),
                    Materials.CarbonMonoxide.getGas(170),
                    Materials.Creosote.getFluid(150),
                    Materials.Dimethylbenzene.getFluid(120),
                    Materials.AceticAcid.getFluid(80),
                    Materials.Methane.getGas(60),
                    Materials.Acetone.getFluid(40),
                    Materials.Phenol.getFluid(35),
                    Materials.Toluene.getFluid(35),
                    Materials.Ethylene.getGas(10),
                    Materials.Hydrogen.getGas(10),
                    Materials.MethylAcetate.getFluid(8),
                    new FluidStack(GTPPFluids.CoalGas, 8),
                    Materials.Ethanol.getFluid(8))
                .duration(200)
                .eut(120)
                .addTo(RecipePool.WoodDistillationRecipes);
        }
    }
}
