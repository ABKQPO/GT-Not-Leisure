package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.recipe.RecipeMaps.plasmaForgeRecipes;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.dreammaster.gthandler.DTPFCalculator;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;

public class PlasmaForgeRecipes implements IRecipePool {

    public RecipeMap<?> PFR = RecipeMaps.plasmaForgeRecipes;

    @Override
    public void loadRecipes() {
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Quantum_Tank_IV.get(4),
                ItemList.Tesseract.get(4),
                MaterialsElements.STANDALONE.DRAGON_METAL.getPlateDense(16),
                aeMaterials.cell128SpatialPart()
                    .maybeStack(16)
                    .orNull(),
                GTModHandler.getModItem(Mods.Avaritia.ID, "Singularity", 2, 0),
                ItemRefer.Fluid_Storage_Core_T8.get(2))
            .fluidInputs(GTNLMaterials.ExcitedNaquadahFuel.getFluidOrGas(1000))
            .itemOutputs(GTNLItemList.DepletedExcitedNaquadahFuelRod.get(1))
            .duration(20)
            .metadata(COIL_HEAT, 13501)
            .eut(31457280)
            .addTo(PFR);

        int awakened_heat = 10800;
        int infinity_heat = awakened_heat + 900;
        int hypogen_heat = infinity_heat + 900;
        int eternal_heat = hypogen_heat + 900;

        {
            // Astral Titanium
            int base_quantity = 512;
            int tier_up_multiplier = 2;

            int tier_1_quantity = 144 * base_quantity;
            int tier_2_quantity = tier_1_quantity * tier_up_multiplier;
            int tier_3_quantity = tier_2_quantity * tier_up_multiplier;
            int tier_4_quantity = tier_3_quantity * tier_up_multiplier;
            int tier_5_quantity = tier_4_quantity * tier_up_multiplier;

            DTPFCalculator astral_titanium = new DTPFCalculator().setBaseParallel(base_quantity)
                .setHighestCatalystTier(4)
                .calculateNonEBFRecipe(TierEU.RECIPE_ZPM, 1600);

            // Tier 5 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTSC.getFluid(astral_titanium.getCatalystAmount(4)),
                    Materials.Titanium.getMolten(tier_5_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(astral_titanium.getResidueAmount(4)),
                    MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFluidStack(tier_5_quantity))
                .duration(astral_titanium.getDuration(4))
                .eut(astral_titanium.getEUt(4))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 4 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTEC.getFluid(astral_titanium.getCatalystAmount(3)),
                    Materials.Titanium.getMolten(tier_4_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(astral_titanium.getResidueAmount(3)),
                    MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFluidStack(tier_4_quantity))
                .duration(astral_titanium.getDuration(3))
                .eut(astral_titanium.getEUt(3))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 3 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTRC.getFluid(astral_titanium.getCatalystAmount(2)),
                    Materials.Titanium.getMolten(tier_3_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(astral_titanium.getResidueAmount(2)),
                    MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFluidStack(tier_3_quantity))
                .duration(astral_titanium.getDuration(2))
                .eut(astral_titanium.getEUt(2))
                .metadata(COIL_HEAT, hypogen_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 2 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTPC.getFluid(astral_titanium.getCatalystAmount(1)),
                    Materials.Titanium.getMolten(tier_2_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(astral_titanium.getResidueAmount(1)),
                    MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFluidStack(tier_2_quantity))
                .duration(astral_titanium.getDuration(1))
                .eut(astral_titanium.getEUt(1))
                .metadata(COIL_HEAT, infinity_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 1 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTCC.getFluid(astral_titanium.getCatalystAmount(0)),
                    Materials.Titanium.getMolten(tier_1_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(astral_titanium.getResidueAmount(0)),
                    MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFluidStack(tier_1_quantity))
                .duration(astral_titanium.getDuration(0))
                .eut(astral_titanium.getEUt(0))
                .metadata(COIL_HEAT, awakened_heat)
                .addTo(plasmaForgeRecipes);
        }

        {
            // Celestial Tungsten
            int base_quantity = 512;
            int tier_up_multiplier = 2;

            int tier_1_quantity = 144 * base_quantity;
            int tier_2_quantity = tier_1_quantity * tier_up_multiplier;
            int tier_3_quantity = tier_2_quantity * tier_up_multiplier;
            int tier_4_quantity = tier_3_quantity * tier_up_multiplier;
            int tier_5_quantity = tier_4_quantity * tier_up_multiplier;

            DTPFCalculator celestial_tungsten = new DTPFCalculator().setBaseParallel(base_quantity)
                .setHighestCatalystTier(4)
                .calculateNonEBFRecipe(TierEU.RECIPE_UV, 800);

            // Tier 5 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTSC.getFluid(celestial_tungsten.getCatalystAmount(4)),
                    Materials.Tungsten.getMolten(tier_5_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(celestial_tungsten.getResidueAmount(4)),
                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(tier_5_quantity))
                .duration(celestial_tungsten.getDuration(4))
                .eut(celestial_tungsten.getEUt(4))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 4 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTEC.getFluid(celestial_tungsten.getCatalystAmount(3)),
                    Materials.Tungsten.getMolten(tier_4_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(celestial_tungsten.getResidueAmount(3)),
                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(tier_4_quantity))
                .duration(celestial_tungsten.getDuration(3))
                .eut(celestial_tungsten.getEUt(3))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 3 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTRC.getFluid(celestial_tungsten.getCatalystAmount(2)),
                    Materials.Tungsten.getMolten(tier_3_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(celestial_tungsten.getResidueAmount(2)),
                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(tier_3_quantity))
                .duration(celestial_tungsten.getDuration(2))
                .eut(celestial_tungsten.getEUt(2))
                .metadata(COIL_HEAT, hypogen_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 2 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTPC.getFluid(celestial_tungsten.getCatalystAmount(1)),
                    Materials.Tungsten.getMolten(tier_2_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(celestial_tungsten.getResidueAmount(1)),
                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(tier_2_quantity))
                .duration(celestial_tungsten.getDuration(1))
                .eut(celestial_tungsten.getEUt(1))
                .metadata(COIL_HEAT, infinity_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 1 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTCC.getFluid(celestial_tungsten.getCatalystAmount(0)),
                    Materials.Tungsten.getMolten(tier_1_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(celestial_tungsten.getResidueAmount(0)),
                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(tier_1_quantity))
                .duration(celestial_tungsten.getDuration(0))
                .eut(celestial_tungsten.getEUt(0))
                .metadata(COIL_HEAT, awakened_heat)
                .addTo(plasmaForgeRecipes);
        }

        {
            // Advanced Nitinol
            int base_quantity = 512;
            int tier_up_multiplier = 2;

            int tier_1_quantity = 144 * base_quantity;
            int tier_2_quantity = tier_1_quantity * tier_up_multiplier;
            int tier_3_quantity = tier_2_quantity * tier_up_multiplier;
            int tier_4_quantity = tier_3_quantity * tier_up_multiplier;
            int tier_5_quantity = tier_4_quantity * tier_up_multiplier;

            DTPFCalculator advanced_nitinol = new DTPFCalculator().setBaseParallel(base_quantity)
                .setHighestCatalystTier(4)
                .calculateNonEBFRecipe(TierEU.RECIPE_UV, 1600);

            // Tier 5 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTSC.getFluid(advanced_nitinol.getCatalystAmount(4)),
                    MaterialsAlloy.NITINOL_60.getFluidStack(tier_5_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(advanced_nitinol.getResidueAmount(4)),
                    MaterialsElements.STANDALONE.ADVANCED_NITINOL.getFluidStack(tier_5_quantity))
                .duration(advanced_nitinol.getDuration(4))
                .eut(advanced_nitinol.getEUt(4))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 4 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTEC.getFluid(advanced_nitinol.getCatalystAmount(3)),
                    MaterialsAlloy.NITINOL_60.getFluidStack(tier_4_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(advanced_nitinol.getResidueAmount(3)),
                    MaterialsElements.STANDALONE.ADVANCED_NITINOL.getFluidStack(tier_4_quantity))
                .duration(advanced_nitinol.getDuration(3))
                .eut(advanced_nitinol.getEUt(3))
                .metadata(COIL_HEAT, eternal_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 3 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTRC.getFluid(advanced_nitinol.getCatalystAmount(2)),
                    MaterialsAlloy.NITINOL_60.getFluidStack(tier_3_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(advanced_nitinol.getResidueAmount(2)),
                    MaterialsElements.STANDALONE.ADVANCED_NITINOL.getFluidStack(tier_3_quantity))
                .duration(advanced_nitinol.getDuration(2))
                .eut(advanced_nitinol.getEUt(2))
                .metadata(COIL_HEAT, hypogen_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 2 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTPC.getFluid(advanced_nitinol.getCatalystAmount(1)),
                    MaterialsAlloy.NITINOL_60.getFluidStack(tier_2_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(advanced_nitinol.getResidueAmount(1)),
                    MaterialsElements.STANDALONE.ADVANCED_NITINOL.getFluidStack(tier_2_quantity))
                .duration(advanced_nitinol.getDuration(1))
                .eut(advanced_nitinol.getEUt(1))
                .metadata(COIL_HEAT, infinity_heat)
                .addTo(plasmaForgeRecipes);

            // Tier 1 - Normal
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(4))
                .fluidInputs(
                    MaterialsUEVplus.ExcitedDTCC.getFluid(advanced_nitinol.getCatalystAmount(0)),
                    MaterialsAlloy.NITINOL_60.getFluidStack(tier_1_quantity))
                .fluidOutputs(
                    MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(advanced_nitinol.getResidueAmount(0)),
                    MaterialsElements.STANDALONE.ADVANCED_NITINOL.getFluidStack(tier_1_quantity))
                .duration(advanced_nitinol.getDuration(0))
                .eut(advanced_nitinol.getEUt(0))
                .metadata(COIL_HEAT, awakened_heat)
                .addTo(plasmaForgeRecipes);
        }
    }
}
