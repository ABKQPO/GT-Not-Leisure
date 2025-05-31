package com.science.gtnl.common.recipe.GregTech;

import static com.science.gtnl.loader.IScriptLoader.missing;
import static gregtech.api.enums.Mods.DraconicEvolution;
import static gregtech.api.enums.Mods.OpenBlocks;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class ChemicalBathRecipes implements IRecipePool {

    final RecipeMap<?> cBR = RecipeMaps.chemicalBathRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.TerraGlass.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.gaiaspirit", 288))
            .itemOutputs(GTNLItemList.GaiaGlass.get(1))
            .specialValue(0)
            .duration(200)
            .eut(30720)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(getModItem("Botania", "elfGlass", 1, 0, missing))
            .fluidInputs(FluidRegistry.getFluidStack("molten.terrasteel", 576))
            .itemOutputs(GTNLItemList.TerraGlass.get(1))
            .specialValue(0)
            .duration(200)
            .eut(7680)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(getModItem(OpenBlocks.ID, "sponge", 1))
            .fluidInputs(FluidRegistry.getFluidStack("dye.chemical.dyeyellow", 576))
            .itemOutputs(new ItemStack(Blocks.sponge, 1))
            .specialValue(0)
            .duration(100)
            .eut(16)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.dragon_egg, 1))
            .fluidInputs(Materials.DraconiumAwakened.getMolten(576))
            .itemOutputs(GTModHandler.getModItem(DraconicEvolution.ID, "dragonHeart", 1))
            .specialValue(0)
            .duration(100)
            .eut(1966080)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Pitchblende, 12))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(
                MaterialPool.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 2))
            .outputChances(10000, 10000, 5000)
            .specialValue(0)
            .duration(1800)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.crushedPurified, Materials.Pitchblende, 12))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(
                MaterialPool.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 2))
            .outputChances(10000, 10000, 5000)
            .specialValue(0)
            .duration(1800)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.UraniumSlag.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(4000))
            .itemOutputs(MaterialPool.UraniumChlorideSlag.get(OrePrefixes.dust, 1))
            .specialValue(0)
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);
    }
}
