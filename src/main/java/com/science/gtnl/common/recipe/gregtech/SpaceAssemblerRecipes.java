package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.registry.GameRegistry;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhintergalactic.recipe.IGRecipeMaps;
import tectech.thing.CustomItemList;

public class SpaceAssemblerRecipes implements IRecipePool {

    public RecipeMap<?> SAR = IGRecipeMaps.spaceAssemblerRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
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

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Energy_Module.get(1),
                new ItemStack(GameRegistry.findItem("miscutils", "itemDehydratorCoil"), 16, 3),
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

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.SpaceElevatorModuleAssemblerT2.get(1),
                ItemList.SpaceElevatorModuleAssemblerT2.get(1),
                ItemList.SpaceElevatorModuleAssemblerT2.get(1),
                ItemList.SpaceElevatorModuleAssemblerT2.get(1),
                ItemList.SpaceElevatorBaseCasing.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 12),
                MaterialsElements.STANDALONE.HYPOGEN.getScrew(16),
                MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.TranscendentMetal, 12),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.ProtoHalkonite, 16),
                ItemRefer.HiC_T5.get(36))
            .fluidInputs(
                Materials.Infinity.getMolten(14400),
                Materials.DimensionallyShiftedSuperfluid.getFluid(24000),
                Materials.UUMatter.getFluid(28000),
                MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(1296))
            .itemOutputs(GTNLItemList.SpaceAssembler.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 2)
            .duration(2400)
            .eut(TierEU.RECIPE_UEV)
            .addTo(SAR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Emitter_UXV.get(1),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                ItemList.Emitter_UXV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Eternity, 8),
                ItemList.Black_Hole_Opener.get(1),
                ItemList.Black_Hole_Opener.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Eternity, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Eternity, 8),
                ItemList.Black_Hole_Opener.get(1),
                ItemList.Black_Hole_Opener.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Eternity, 8),
                ItemList.Emitter_UXV.get(1),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "reactorCore", 16),
                ItemList.Emitter_UXV.get(1))
            .fluidInputs(
                Materials.Universium.getMolten(9216),
                Materials.MHDCSM.getMolten(9216),
                Materials.MagMatter.getMolten(9216),
                Materials.BlackDwarfMatter.getMolten(9216))
            .itemOutputs(ItemList.Black_Hole_Stabilizer.get(1))
            .metadata(IGRecipeMaps.MODULE_TIER, 3)
            .duration(2400)
            .eut(TierEU.RECIPE_UXV)
            .addTo(SAR);
    }
}
