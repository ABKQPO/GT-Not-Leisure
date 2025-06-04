package com.science.gtnl.common.recipe.Special.OreDictionary;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class WoodDistillationRecipes implements IOreRecipeRegistrator {

    public WoodDistillationRecipes() {
        OrePrefixes.log.add(this);
    }

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        if (aOreDictName.equals("logWood")) {

            final RecipeMap<?> WDR = RecipeRegister.WoodDistillationRecipes;

            RecipeBuilder.builder()
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
                    FluidUtils.getFluidStack("bioethanol", 8))
                .specialValue(0)
                .duration(200)
                .eut(120)
                .addTo(WDR);
        }
    }
}
