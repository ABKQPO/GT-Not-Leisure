package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.Mods.*;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.common.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.common.recipe.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;

public class CircuitAssemblerRecipes implements IRecipePool {

    final RecipeMap<?> CAR = RecipeMaps.circuitAssemblerRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Battery_RE_ULV_Tantalum.get(4L),
                ItemList.Circuit_Parts_Wiring_Basic.get(4L),
                ItemList.Circuit_Parts_Coil.get(4L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 1, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                ItemList.Circuit_Primitive.get(1L))
            .fluidInputs(Materials.Tin.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticULV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(50)
            .eut(30)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_Advanced.get(4L),
                ItemList.Circuit_Parts_Wiring_Elite.get(4L),
                ItemList.Circuit_Parts_Wiring_Advanced.get(4L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 1, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                GTNLItemList.CircuitResonaticULV.get(1))
            .fluidInputs(Materials.Tin.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticLV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(90)
            .eut(120)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_Advanced.get(8L),
                ItemList.Circuit_Parts_Wiring_Elite.get(8L),
                ItemList.Circuit_Parts_Wiring_Advanced.get(8L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 1, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                GTNLItemList.CircuitResonaticLV.get(1))
            .fluidInputs(Materials.Tin.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticMV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(150)
            .eut(480)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeSMD.get(4L),
                ItemList.Circuit_Parts_TransistorSMD.get(4L),
                ItemList.Circuit_Parts_CapacitorSMD.get(4L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 2, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNLItemList.CircuitResonaticMV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticHV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(230)
            .eut(1920)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeSMD.get(8L),
                ItemList.Circuit_Parts_TransistorSMD.get(8L),
                ItemList.Circuit_Parts_CapacitorSMD.get(8L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 4, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNLItemList.CircuitResonaticHV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticEV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(330)
            .eut(7680)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeASMD.get(4L),
                ItemList.Circuit_Parts_TransistorASMD.get(4L),
                ItemList.Circuit_Parts_CapacitorASMD.get(4L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 4, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNLItemList.CircuitResonaticEV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticIV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(450)
            .eut(30720)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeASMD.get(8L),
                ItemList.Circuit_Parts_TransistorASMD.get(8L),
                ItemList.Circuit_Parts_CapacitorASMD.get(8L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 4, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNLItemList.CircuitResonaticIV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTNLItemList.CircuitResonaticLuV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(570)
            .eut(122880)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNLItemList.BiowareSMDDiode.get(16),
                GTNLItemList.BiowareSMDCapacitor.get(16),
                GTNLItemList.BiowareSMDTransistor.get(16),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 8, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNLItemList.CircuitResonaticLuV.get(1))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(144))
            .itemOutputs(GTNLItemList.CircuitResonaticZPM.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(710)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeXSMD.get(16L),
                ItemList.Circuit_Parts_TransistorXSMD.get(16L),
                ItemList.Circuit_Parts_CapacitorXSMD.get(16L),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 8, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNLItemList.CircuitResonaticZPM.get(1))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(288))
            .itemOutputs(GTNLItemList.CircuitResonaticUV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(730)
            .eut(1966080)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNLItemList.ExoticSMDDiode.get(16),
                GTNLItemList.ExoticSMDCapacitor.get(16),
                GTNLItemList.ExoticSMDTransistor.get(16),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 8, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNLItemList.CircuitResonaticUV.get(1))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(432))
            .itemOutputs(GTNLItemList.CircuitResonaticUHV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(750)
            .eut(7864320)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNLItemList.CosmicSMDDiode.get(16),
                GTNLItemList.CosmicSMDCapacitor.get(16),
                GTNLItemList.CosmicSMDTransistor.get(16),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 8, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNLItemList.CircuitResonaticUHV.get(1))
            .fluidInputs(MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(288))
            .itemOutputs(GTNLItemList.CircuitResonaticUEV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(770)
            .eut(31457280)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNLItemList.TemporallySMDDiode.get(16),
                GTNLItemList.TemporallySMDCapacitor.get(16),
                GTNLItemList.TemporallySMDTransistor.get(16),
                GTModHandler.getModItem(BartWorks.ID, "gt.bwMetaGeneratedItem0", 8, 3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNLItemList.CircuitResonaticUEV.get(1))
            .fluidInputs(MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(432))
            .itemOutputs(GTNLItemList.CircuitResonaticUIV.get(4))
            .specialValue(0)
            .noOptimize()
            .duration(790)
            .eut(125829120)
            .requiresCleanRoom()
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, ItemList.Circuit_Primitive.get(1L)),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTModHandler.getModItem(Minecraft.ID, "redstone", 1, 0))
            .fluidInputs(FluidRegistry.getFluidStack("refinedglue", 20))
            .itemOutputs(GTNLItemList.VerySimpleCircuit.get(2))
            .specialValue(0)
            .noOptimize()
            .duration(40)
            .eut(7)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(IndustrialCraft2.ID, "itemPartCircuit", 1)),
                GTNLItemList.VerySimpleCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.RedAlloy, 1))
            .fluidInputs(FluidRegistry.getFluidStack("refinedglue", 20))
            .itemOutputs(GTNLItemList.SimpleCircuit.get(2))
            .specialValue(0)
            .noOptimize()
            .duration(80)
            .eut(16)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, ItemList.Circuit_Good.get(1L)),
                GTModHandler.getModItem(Minecraft.ID, "paper", 1, 0),
                GTNLItemList.SimpleCircuit.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Diamond, 1))
            .fluidInputs(FluidRegistry.getFluidStack("refinedglue", 20))
            .itemOutputs(GTNLItemList.BasicCircuit.get(2))
            .specialValue(0)
            .noOptimize()
            .duration(160)
            .eut(30)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(IndustrialCraft2.ID, "itemPartCircuitAdv", 1)),
                ItemList.Circuit_Board_Coated_Basic.get(1L),
                GTNLItemList.BasicCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Obsidian, 1),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.RedAlloy, 1))
            .fluidInputs(FluidRegistry.getFluidStack("refinedglue", 20))
            .itemOutputs(GTNLItemList.AdvancedCircuit.get(1))
            .specialValue(0)
            .noOptimize()
            .duration(80)
            .eut(120)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Phenolic_Good.get(1L),
                GTNLItemList.AdvancedCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.RedAlloy, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTNLItemList.EliteCircuit.get(1))
            .specialValue(0)
            .noOptimize()
            .duration(200)
            .eut(480)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_BioCPU.get(1L),
                ItemList.Circuit_Chip_QuantumCPU.get(4),
                GTNLItemList.HighlyAdvancedSoc.get(1),
                GTNLItemList.BiowareSMDCapacitor.get(8),
                GTNLItemList.BiowareSMDTransistor.get(8),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Naquadah, 8))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(144))
            .itemOutputs(ItemList.Circuit_Bioprocessor.get(2))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(1L),
                ItemList.Circuit_Chip_CrystalCPU.get(1),
                ItemList.Circuit_Chip_NanoCPU.get(2),
                ItemList.Circuit_Parts_CapacitorASMD.get(6),
                ItemList.Circuit_Parts_TransistorASMD.get(6),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(ItemList.Circuit_Crystalprocessor.get(1))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(1L),
                ItemList.Circuit_Chip_CrystalSoC.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.YttriumBariumCuprate, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(ItemList.Circuit_Crystalprocessor.get(2))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(100)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(1L),
                ItemList.Circuit_Crystalprocessor.get(2),
                ItemList.Circuit_Parts_InductorASMD.get(4),
                ItemList.Circuit_Parts_CapacitorASMD.get(8),
                ItemList.Circuit_Chip_Ram.get(24),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Crystalcomputer.get(2))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(1L),
                ItemList.Circuit_Crystalcomputer.get(2),
                ItemList.Circuit_Chip_Ram.get(4),
                ItemList.Circuit_Chip_NOR.get(32),
                ItemList.Circuit_Chip_NAND.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NiobiumTitanium, 32))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Ultimatecrystalcomputer.get(1))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware_Extreme.get(1L),
                ItemList.Circuit_Chip_Stemcell.get(16),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Polybenzimidazole, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Electrum, 8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silicone, 16),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.HSSE, 8))
            .fluidInputs(Materials.GrowthMediumSterilized.getFluid(250))
            .itemOutputs(ItemList.Circuit_Chip_NeuroCPU.get(1))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_NeuroCPU.get(1L),
                ItemList.Circuit_Chip_CrystalCPU.get(1L),
                ItemList.Circuit_Chip_NanoCPU.get(1L),
                ItemList.Circuit_Parts_CapacitorASMD.get(8),
                ItemList.Circuit_Parts_TransistorASMD.get(8),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.YttriumBariumCuprate, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(ItemList.Circuit_Neuroprocessor.get(1))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_NeuroCPU.get(1L),
                GTNLItemList.HighlyAdvancedSoc.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.YttriumBariumCuprate, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Naquadah, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(ItemList.Circuit_Neuroprocessor.get(2))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(100)
            .eut(TierEU.RECIPE_UV)
            .addTo(CAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware_Extreme.get(1L),
                ItemList.Circuit_Neuroprocessor.get(2),
                ItemList.Circuit_Parts_InductorASMD.get(6),
                ItemList.Circuit_Parts_CapacitorASMD.get(12),
                ItemList.Circuit_Chip_Ram.get(24),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.YttriumBariumCuprate, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(ItemList.Circuit_Wetwarecomputer.get(2))
            .requiresCleanRoom()
            .specialValue(0)
            .noOptimize()
            .duration(400)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CAR);

    }
}
