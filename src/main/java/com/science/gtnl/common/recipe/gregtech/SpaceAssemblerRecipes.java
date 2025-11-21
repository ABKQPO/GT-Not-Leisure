package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import goodgenerator.util.ItemRefer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.item.ModItems;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhintergalactic.recipe.IGRecipeMaps;
import tectech.thing.CustomItemList;

public class SpaceAssemblerRecipes implements IRecipePool {

    public RecipeMap<?> SAR = IGRecipeMaps.spaceAssemblerRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_Optical.get(1),
                ItemList.Circuit_Chip_Ram.get(16),
                CustomItemList.DATApipe.get(8),
                ItemList.Circuit_Chip_CrystalCPU.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Tritanium, 8),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.HSSS, 8),
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Diamond, 1),
                MaterialsAlloy.HASTELLOY_C276.getPlate(2))
            .fluidInputs(Materials.Sunnarium.getMolten(10), Materials.ElectrumFlux.getMolten(288))
            .itemOutputs(ItemList.Optically_Compatible_Memory.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 1)
            .duration(240)
            .eut(TierEU.RECIPE_UV)
            .addTo(SAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Energy_Module.get(1),
                new ItemStack(ModItems.itemDehydratorCoil, 16, 3),
                MaterialsAlloy.PIKYONIUM.getPlate(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4),
                ItemList.Field_Generator_ZPM.get(2))
            .fluidInputs(
                MaterialsAlloy.PIKYONIUM.getFluidStack(8352),
                MaterialsAlloy.ENERGYCRYSTAL.getFluidStack(9216),
                MaterialsAlloy.TRINIUM_NAQUADAH_CARBON.getFluidStack(4320),
                MaterialsAlloy.INDALLOY_140.getFluidStack(2304))
            .itemOutputs(ItemList.ZPM.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(SAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.SpaceElevatorModuleMinerT2.get(1),
                ItemList.SpaceElevatorModuleMinerT2.get(1),
                ItemList.SpaceElevatorModuleMinerT2.get(1),
                ItemList.SpaceElevatorModuleMinerT2.get(1),
                ItemList.SpaceElevatorBaseCasing.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 16),
                MaterialsElements.STANDALONE.HYPOGEN.getScrew(32),
                MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.TranscendentMetal, 16),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, MaterialsUEVplus.ProtoHalkonite, 24),
                ItemRefer.HiC_T5.get(48))
            .fluidInputs(
                Materials.Infinity.getMolten(14400),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(30000),
                Materials.UUMatter.getFluid(32000),
                MaterialsUEVplus.SpaceTime.getMolten(1296))
            .itemOutputs(GTNLItemList.SpaceAssembler.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 3)
            .duration(2400)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SAR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Emitter_UXV.get(1),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                ItemList.Emitter_UXV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Eternity, 8),
                ItemList.Black_Hole_Opener.get(1),
                ItemList.Black_Hole_Opener.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Eternity, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Eternity, 8),
                ItemList.Black_Hole_Opener.get(1),
                ItemList.Black_Hole_Opener.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Eternity, 8),
                ItemList.Emitter_UXV.get(1),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                ItemList.Emitter_UXV.get(1))
            .fluidInputs(
                MaterialsUEVplus.Universium.getMolten(9216),
                MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(9216),
                MaterialsUEVplus.MagMatter.getMolten(9216),
                MaterialsUEVplus.BlackDwarfMatter.getMolten(9216))
            .itemOutputs(ItemList.Black_Hole_Stabilizer.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 3)
            .duration(2400)
            .eut(TierEU.RECIPE_UXV)
            .addTo(SAR);
    }
}
