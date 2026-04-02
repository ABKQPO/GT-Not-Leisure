package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class ChemicalBathRecipes implements IRecipePool {

    public RecipeMap<?> cBR = RecipeMaps.chemicalBathRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.TerraGlass.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.gaiaspirit", 288))
            .itemOutputs(GTNLItemList.GaiaGlass.get(1))
            .duration(200)
            .eut(30720)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.Botania.ID, "elfGlass", 1, 0))
            .fluidInputs(FluidRegistry.getFluidStack("molten.terrasteel", 576))
            .itemOutputs(GTNLItemList.TerraGlass.get(1))
            .duration(200)
            .eut(7680)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.OpenBlocks.ID, "sponge", 1))
            .fluidInputs(FluidRegistry.getFluidStack("dye.chemical.dyeyellow", 576))
            .itemOutputs(new ItemStack(Blocks.sponge, 1))
            .duration(100)
            .eut(16)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.dragon_egg, 1))
            .fluidInputs(Materials.DraconiumAwakened.getMolten(576))
            .itemOutputs(GTModHandler.getModItem(Mods.DraconicEvolution.ID, "dragonHeart", 1))
            .duration(100)
            .eut(1966080)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Pitchblende, 12))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(
                GTNLMaterials.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 2))
            .outputChances(10000, 10000, 5000)
            .duration(1800)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.crushedPurified, Materials.Pitchblende, 12))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(
                GTNLMaterials.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uraninite, 2))
            .outputChances(10000, 10000, 5000)
            .duration(1800)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.UraniumSlag.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(4000))
            .itemOutputs(GTNLMaterials.UraniumChlorideSlag.get(OrePrefixes.dust, 1))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(cBR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.sponge, 1))
            .fluidInputs(Materials.Water.getFluid(16000))
            .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "sponge", 1, 1))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(cBR);

        if (MainConfig.recipe.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        // 下界合金碎片概率移除 删除下界合金碎片种子产出
        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(ItemList.Netherite_Scrap_Seed.get(1))
            .fluidInputs(Materials.PoorNetherWaste.getFluid(16_000))
            .itemOutputs(ItemList.Brittle_Netherite_Scrap.get(3))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(cBR);

        // 下界合金碎片单步配方增产 x16
        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(ItemList.Hot_Netherite_Scrap.get(16), ItemList.Heavy_Hellish_Mud.get(16))
            .fluidInputs(Materials.PoorNetherWaste.getFluid(8_000))
            .itemOutputs(
                ItemList.Brittle_Netherite_Scrap.get(48),
                GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "netherite_scrap", 16))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(cBR);
    }
}
