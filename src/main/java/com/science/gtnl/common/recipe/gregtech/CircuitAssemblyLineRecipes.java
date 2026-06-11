package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialMisc;
import tectech.thing.CustomItemList;

public class CircuitAssemblyLineRecipes implements IRecipePool {

    public RecipeMap<?> CALR = BartWorksRecipeMaps.circuitAssemblyLineRecipes;

    @Override
    public void loadRecipes() {
        if (MainConfig.recipe.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_OpticallyPerfectedCPUs.get(1),
                ItemList.Wrap_OpticallyCompatibleMemories.get(2),
                ItemList.Wrap_OpticalSMDCapacitors.get(16),
                ItemList.Wrap_OpticalSMDDiodes.get(16),
                CustomItemList.DATApipe.get(64),
                GTOreDictUnificator.get(OrePrefixes.rod, Materials.EnrichedHolmium, 32))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(288))
            .itemOutputs(ItemList.Circuit_OpticalProcessor.get(16))
            .special(ItemList.CircuitImprint_OpticalProcessor.get(0))
            .requiresCleanRoom()
            .duration(3600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_UltraBioMutatedCircuitBoards.get(1),
                ItemList.Circuit_Bioprocessor.get(32),
                GTNLItemList.BiowareSMDInductor.get(64),
                GTNLItemList.BiowareSMDCapacitor.get(64),
                ItemList.Wrap_RandomAccessMemoryChips.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 24))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(288))
            .itemOutputs(ItemList.Circuit_Biowarecomputer.get(16))
            .special(ItemList.CircuitImprint_BiowareAssembly.get(0))
            .requiresCleanRoom()
            .duration(3600)
            .eut(TierEU.RECIPE_UV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_BioProcessingUnits.get(1L),
                ItemList.Wrap_QBitProcessingUnits.get(4),
                GTNLItemList.HighlyAdvancedSoc.get(16),
                GTNLItemList.BiowareSMDCapacitor.get(64),
                GTNLItemList.BiowareSMDTransistor.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Naquadah, 8))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(288))
            .itemOutputs(ItemList.Circuit_Bioprocessor.get(32))
            .special(ItemList.CircuitImprint_BiowareProcessor.get(0))
            .requiresCleanRoom()
            .duration(2400)
            .eut(TierEU.RECIPE_UV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_EliteCircuitBoards.get(1L),
                ItemList.Wrap_CrystalProcessingUnits.get(1),
                ItemList.Wrap_NanocomponentCentralProcessingUnits.get(2),
                ItemList.Wrap_AdvancedSMDCapacitors.get(6),
                ItemList.Wrap_AdvancedSMDTransistors.get(6),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Crystalprocessor.get(16))
            .special(ItemList.CircuitImprint_CrystalProcessor.get(0))
            .requiresCleanRoom()
            .duration(2400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_EliteCircuitBoards.get(1L),
                ItemList.Wrap_CrystalSoCs.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.YttriumBariumCuprate, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Crystalprocessor.get(32))
            .special(ItemList.CircuitImprint_CrystalProcessor.get(0))
            .requiresCleanRoom()
            .duration(1200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_EliteCircuitBoards.get(1),
                ItemList.Circuit_Crystalprocessor.get(32),
                ItemList.Wrap_AdvancedSMDInductors.get(4),
                ItemList.Wrap_AdvancedSMDCapacitors.get(8),
                ItemList.Wrap_RandomAccessMemoryChips.get(24),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(ItemList.Circuit_Crystalcomputer.get(32))
            .special(ItemList.CircuitImprint_CrystalAssembly.get(0))
            .requiresCleanRoom()
            .duration(4800)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_EliteCircuitBoards.get(1),
                ItemList.Circuit_Crystalcomputer.get(32),
                ItemList.Wrap_RandomAccessMemoryChips.get(4),
                ItemList.Wrap_NORMemoryChips.get(32),
                ItemList.Wrap_NANDMemoryChips.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 32))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(ItemList.Circuit_Ultimatecrystalcomputer.get(16))
            .special(ItemList.CircuitImprint_CrystalSupercomputer.get(0))
            .requiresCleanRoom()
            .duration(4800)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_NeuroProcessingUnits.get(1),
                ItemList.Wrap_CrystalProcessingUnits.get(1),
                ItemList.Wrap_NanocomponentCentralProcessingUnits.get(1),
                ItemList.Wrap_AdvancedSMDCapacitors.get(8),
                ItemList.Wrap_AdvancedSMDTransistors.get(8),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.YttriumBariumCuprate, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Neuroprocessor.get(16))
            .special(ItemList.CircuitImprint_WetwareProcessor.get(0))
            .requiresCleanRoom()
            .duration(2400)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Wrap_ExtremeWetwareLifesupportCircuitBoards.get(1),
                ItemList.Circuit_Neuroprocessor.get(32),
                ItemList.Wrap_AdvancedSMDInductors.get(6),
                ItemList.Wrap_AdvancedSMDCapacitors.get(12),
                ItemList.Wrap_RandomAccessMemoryChips.get(24),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.YttriumBariumCuprate, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(ItemList.Circuit_Wetwarecomputer.get(32))
            .special(ItemList.CircuitImprint_WetwareAssembly.get(0))
            .requiresCleanRoom()
            .duration(4800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CALR);

        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSE, 32),
                ItemList.Circuit_Ultimatecrystalcomputer.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NiobiumTitanium, 32),
                ItemList.Wrap_AdvancedSMDInductors.get(8),
                ItemList.Wrap_AdvancedSMDCapacitors.get(16),
                ItemList.Wrap_AdvancedSMDDiodes.get(8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Crystalmainframe.get(16))
            .special(ItemList.CircuitImprint_CrystalMainframe.get(0))
            .eut(TierEU.RECIPE_LuV)
            .duration(4800)
            .requiresCleanRoom()
            .addTo(CALR);
    }
}
