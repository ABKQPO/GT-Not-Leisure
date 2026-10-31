package com.science.gtnl.compat.newhorizons;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.NANO_FORGE_TIER;
import static gtnhlanth.api.recipe.LanthanidesRecipeMaps.TARGET_CHAMBER_METADATA;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.block.BlockList;
import com.dreammaster.item.NHItemList;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.gtnl.CraftingTableRecipes;
import com.science.gtnl.config.RecipeConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import appeng.api.util.AEColor;
import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.Optional;
import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.objects.SubstituteFluidStack;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import gtnhlanth.common.item.MaskList;
import gtnhlanth.common.register.LanthItemList;
import gtnhlanth.common.tileentity.recipe.beamline.TargetChamberMetadata;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;

public class NHCoreRecipes {

    public static void assembler() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        assemblerRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void assemblerRecipes() {
        var aeItems = AEApi.instance()
            .definitions()
            .items();
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();
        var aeBlocks = AEApi.instance()
            .definitions()
            .blocks();

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.AssemblerMatrixWall.get(1),
                ItemList.AcceleratorLuV.get(4),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(4)
                    .orNull(),
                NHItemList.EngineeringProcessorItemAdvEmeraldCore.get(16),
                ItemList.Field_Generator_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4),
                aeItems.coloredPaintBall()
                    .stack(AEColor.Red, 64))
            .circuit(11)
            .itemOutputs(GTNLItemList.AssemblerMatrixSpeedCore.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                aeMaterials.cell16384kPart()
                    .maybeStack(6)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Quantium, 8),
                GTNLItemList.ShatteredSingularity.get(4),
                aeMaterials.cardCapacity()
                    .maybeStack(4)
                    .orNull(),
                aeMaterials.logicProcessor()
                    .maybeStack(16)
                    .orNull(),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerCraftingStorage128M.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCraftingStorage128M.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Quantium, 8),
                GTNLItemList.ShatteredSingularity.get(4),
                NHItemList.EngravedQuantumChip.get(8),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(16))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerCraftingStorage256M.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                aeBlocks.craftingStorage16384k()
                    .maybeStack(3)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Quantium, 8),
                GTNLItemList.ShatteredSingularity.get(4),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(4)
                    .orNull(),
                aeMaterials.calcProcessor()
                    .maybeStack(16)
                    .orNull(),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerAccelerator.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerAccelerator.get(1),
                GTNLItemList.QuantumComputerCraftingStorage256M.get(1),
                aeMaterials.singularity()
                    .maybeStack(4)
                    .orNull(),
                GTNLItemList.ShatteredSingularity.get(2),
                NHItemList.EngravedQuantumChip.get(16),
                aeMaterials.engProcessor()
                    .maybeStack(16)
                    .orNull(),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(4))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerCore.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCore.get(1),
                GTNLItemList.ShatteredSingularity.get(16),
                GTNLItemList.QuantumComputerAccelerator.get(4),
                ItemList.QuantumStar.get(4),
                NHItemList.EngravedQuantumChip.get(16),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerMultiThreader.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCore.get(1),
                GTNLItemList.ShatteredSingularity.get(16),
                GTNLItemList.QuantumComputerCraftingStorage256M.get(3),
                ItemList.QuantumStar.get(4),
                NHItemList.EngravedQuantumChip.get(16),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8))
            .circuit(11)
            .itemOutputs(GTNLItemList.QuantumComputerDataEntangler.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.TwilightForestItemMagicBeans.get(64),
                ModsItemlist.TwilightForestItemGiantSword.get(1),
                ModsItemlist.TwilightForestItemGiantPick.get(1),
                ModsItemlist.TwilightForestTileGiantObsidian.get(16))
            .itemOutputs(GTNLItemList.GiantBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.NagaTrophy.get(1),
                ModsItemlist.TwilightForestItemNagaScale.get(64),
                ModsItemlist.TwilightForestSpiralBricks.get(64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Steeleaf, 16))
            .itemOutputs(GTNLItemList.NagaBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.HydraTrophy.get(1),
                ModsItemlist.TwilightForestItemHydraChop.get(64),
                ModsItemlist.TwilightForestItemFieryBlood.get(64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.FierySteel, 16))
            .itemOutputs(GTNLItemList.HydraBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.SnowQueenTrophy.get(1),
                ModsItemlist.TwilightForestItemTripleBow.get(1),
                ModsItemlist.TwilightForestAuroraBrick.get(64),
                ModsItemlist.TwilightForestTileAuroraPillar.get(64))
            .itemOutputs(GTNLItemList.SnowQueenBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.KnightPhantomTrophy.get(1),
                ModsItemlist.TwilightForestItemPhantomHelm.get(1),
                ModsItemlist.TwilightForestItemPhantomPlate.get(1),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Knightmetal, 16))
            .itemOutputs(GTNLItemList.KnightPhantomBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.UrGhastTrophy.get(1),
                ModsItemlist.TwilightForestTowerDevice.get(64),
                ModsItemlist.TwilightForestVanishingBlock.get(64),
                ModsItemlist.TwilightForestItemCarminite.get(32))
            .itemOutputs(GTNLItemList.UrGhastBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.MinoshroomTrophy.get(1),
                ModsItemlist.TwilightForestItemMeefStroganoff.get(1),
                ModsItemlist.TwilightForestItemMeefSteak.get(64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.IronWood, 16))
            .itemOutputs(GTNLItemList.MinotaurBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.AlphaYetiTrophy.get(1),
                ModsItemlist.TwilightForestItemAlphaFur.get(16),
                ModsItemlist.TwilightForestItemIceBomb.get(16),
                ModsItemlist.TwilightForestItemArcticFur.get(64))
            .itemOutputs(GTNLItemList.AlphaYetiBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.get(64),
                ModsItemlist.LichTrophy.get(1),
                ModsItemlist.TwilightForestItemScepterLifeDrain.get(1),
                ModsItemlist.TwilightForestItemScepterTwilight.get(1),
                ModsItemlist.TwilightForestItemScepterZombie.get(1))
            .itemOutputs(GTNLItemList.LichBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Dynamo_IV.get(1),
                ItemList.ReinforcedGlass.get(32),
                ModsItemlist.IC2ThickNeutronReflector.get(1),
                BlockList.TungstensteelPlatedReinforcedStone.get(2),
                NHItemList.ReinforcedTungstenSteelIronPlate.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Tungsten, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 4))
            .itemOutputs(GTNLItemList.ExplosionDynamoHatch.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(1296))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.MolybdenumDisilicide.get(OrePrefixes.ring, 32),
                NHItemList.MicaInsulatorFoil.get(16))
            .fluidInputs(GTNLMaterials.HSLASteel.getMolten(144))
            .itemOutputs(GTNLItemList.MolybdenumDisilicideCoil.get(1))
            .duration(500)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Casing_Coil_Superconductor.get(1L),
                NHItemList.LaserEmitter.get(4),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 4),
                GregtechItemList.DehydratorCoilWireLuV.get(8),
                ItemList.LuV_Coil.get(4L),
                ItemList.Emitter_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 16))
            .fluidInputs(SubstituteFluidStack.soldering(9216))
            .itemOutputs(GTNLItemList.LaserBeacon.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_HV.get(1),
                NHItemList.AdsorptionFilter.get(1),
                ItemList.Electric_Pump_HV.get(2),
                ItemList.Electric_Motor_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Electrum, 2L))
            .itemOutputs(GTNLItemList.Desulfurizer.get(1))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    public static void assemblyLine() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        assemblyLineRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void assemblyLineRecipes() {
        var aeItems = AEApi.instance()
            .definitions()
            .items();
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();
        var aeBlocks = AEApi.instance()
            .definitions()
            .blocks();

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.ElectricImplosionCompressor.get(1),
            51200000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.ElectricImplosionCompressor.get(8), BlockList.NaquadahPlatedReinforcedStone.get(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Osmiridium, 32),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.NaquadahAlloy, 64),
                ItemList.Electric_Motor_UV.get(64), ItemList.Electric_Piston_UV.get(32),
                ItemList.Conveyor_Module_UV.get(32), ItemList.Field_Generator_UV.get(16), ItemList.Robot_Arm_UV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 4) },
            new FluidStack[] { Materials.Grade6PurifiedWater.getFluid(128000),
                MaterialsAlloy.BOTMIUM.getFluidStack(4608), WerkstoffLoader.Oganesson.getFluidOrGas(16000) },
            GTNLItemList.ElectricImplosionCompressor.get(1),
            120 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UEV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ModsItemlist.GalaxySpaceRocketControlComputerTier4.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(4 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                NHItemList.HeavyDutyPlateTier5.get(8),
                WerkstoffLoader.RhodiumPlatedPalladium.get(OrePrefixes.plate, 32),
                ItemList.Electric_Motor_LuV.get(4),
                ItemList.Electric_Pump_LuV.get(4),
                ItemList.Conveyor_Module_LuV.get(4),
                ItemList.Robot_Arm_LuV.get(4),
                ItemList.Emitter_LuV.get(4),
                ItemList.Sensor_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 8 },
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4L })
            .fluidInputs(
                Materials.Lubricant.getFluid(128000),
                SubstituteFluidStack.soldering(18432),
                Materials.SuperconductorLuVBase.getMolten(2304))
            .itemOutputs(GTNLItemList.MeteorMinerSchematic1.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ModsItemlist.GalaxySpaceRocketControlComputerTier7.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                NHItemList.HeavyDutyPlateTier7.get(8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmiridium, 32),
                ItemList.Electric_Motor_UV.get(4),
                ItemList.Electric_Pump_UV.get(4),
                ItemList.Conveyor_Module_UV.get(4),
                ItemList.Robot_Arm_UV.get(4),
                ItemList.Emitter_UV.get(4),
                ItemList.Sensor_UV.get(4),
                ItemList.Field_Generator_UV.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 32),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16 },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L })
            .fluidInputs(
                Materials.Lubricant.getFluid(256000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(128000),
                Materials.NaquadahAlloy.getMolten(144 * 128),
                Materials.SuperconductorUVBase.getMolten(144 * 32))
            .itemOutputs(GTNLItemList.MeteorMinerSchematic2.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(120 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                new ItemStack(GregTechAPI.sBlockMachines, 1, MetaTileEntityIDs.ManualTrafo.ID))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                CustomItemList.Machine_Multi_Transformer.get(1),
                new ItemStack(GregTechAPI.sBlockMachines, 1, MetaTileEntityIDs.ManualTrafo.ID),
                new ItemStack(ItemRegistry.BW_BLOCKS[2], 8, 1),
                ItemList.Circuit_Chip_NanoCPU.get(16),
                CustomItemList.LASERpipe.get(8),
                NHItemList.HighEnergyFlowCircuit.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2 },
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.NetherStar, 2))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(576), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.EnergyTransferNode.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeCutter.get(1),
            32768000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeCutter.get(64), ItemList.IndustrialWireFactory.get(64),
                ItemList.Neutronium_Active_Casing.get(64), GregtechItemList.Casing_Autocrafter.get(12),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Piston_UHV.get(32),
                ItemList.Conveyor_Module_UHV.get(32), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 12),
                NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 4), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(23040),
                Materials.SuperCoolant.getFluid(128000), Materials.UUMatter.getFluid(128000),
                Materials.Lubricant.getFluid(128000) },
            GTNLItemList.NeutroniumWireCutting.get(1),
            1000,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeMixer.get(1),
            16384000,
            20000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.LargeMixer.get(64), ItemList.IndustrialMixer.get(64),
                CustomItemList.eM_Power.get(32), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16),
                ItemList.Electric_Motor_UV.get(48), ItemList.Field_Generator_UV.get(12),
                ItemList.Electric_Pump_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Neutronium, 12), ItemRefer.HiC_T4.get(32),
                NHItemList.HighEnergyFlowCircuit.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUVBase, 12),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 4), },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(32000),
                MaterialsAlloy.STABALLOY.getFluidStack(128000), GTNLMaterials.Polyetheretherketone.getMolten(32000),
                Materials.Lubricant.getFluid(128000) },
            GTNLItemList.MegaMixer.get(1),
            1000,
            (int) TierEU.RECIPE_UV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeBender.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeBender.get(64), ItemList.Neutronium_Casing.get(64),
                CustomItemList.eM_Containment.get(32), ItemList.Electric_Motor_UHV.get(32),
                ItemList.Electric_Piston_UHV.get(32), ItemList.Field_Generator_UHV.get(8),
                NHItemList.HighEnergyFlowCircuit.get(64), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(64000), GTNLMaterials.Polyetheretherketone.getMolten(32000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(66816) },
            GTNLItemList.HeavyRolling.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeEngravingLaser.get(1),
            4000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.lens, Materials.Dilithium, 16),
                GTNLItemList.LargeEngravingLaser.get(64), ItemList.PrecisionLaserEngraverUHV.get(32),
                CustomItemList.eM_Containment.get(64), ItemRefer.Compact_Fusion_Coil_T2.get(32),
                new ItemStack(LanthItemList.ELECTRODE_CASING, 64), CustomItemList.eM_energyTunnel4_UHV.get(32),
                ItemList.Emitter_UHV.get(16), ItemList.Electric_Pump_UHV.get(16), ItemList.Electric_Piston_UHV.get(16),
                ItemList.Field_Generator_UHV.get(8), NHItemList.HighEnergyFlowCircuit.get(64),
                NHItemList.HighEnergyFlowCircuit.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 },
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), Materials.SuperconductorUHVBase.getMolten(27648),
                Materials.ExcitedDTPC.getFluid(1000) },
            GTNLItemList.EngravingLaserPlant.get(1),
            1800,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeArcSmelter.get(1),
            4000000,
            48000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.LargeArcSmelter.get(64), GTNLItemList.LargeArcSmelter.get(64),
                ItemList.ArcFurnaceUV.get(32), new ItemStack(LanthItemList.ELECTRODE_CASING, 64),
                ItemList.Neutronium_Casing.get(32), ModsItemlist.KekzTechLapotronicEnergyUnitBlock.get(64),
                ItemRefer.Field_Restriction_Coil_T2.get(4), ItemList.Robot_Arm_UV.get(16),
                ItemList.Electric_Motor_UV.get(16), ItemList.Conveyor_Module_UV.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8 }, GregtechItemList.Laser_Lens_Special.get(1),
                NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), new FluidStack(FluidRegistry.getFluid("oganesson"), 92160),
                new FluidStack(FluidRegistry.getFluid("plasma.celestialtungsten"), 64000) },
            GTNLItemList.MagneticEnergyReactionFurnace.get(1),
            2000,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeDistillery.get(1),
            1800000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeDistillery.get(64), ItemList.MegaDistillationTower.get(64),
                ItemList.BlockNaquadriaReinforcedWaterPlantCasing.get(64), ItemList.Heating_Duct_Casing.get(64),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Pump_UHV.get(32),
                ItemList.Field_Generator_UHV.get(16), ItemRefer.HiC_T5.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 1),
                GregtechItemList.Laser_Lens_Special.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 2), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.CosmicNeutronium.getMolten(9216), GTNLMaterials.Polyetheretherketone.getMolten(32000),
                Materials.SuperCoolant.getFluid(256000) },
            GTNLItemList.CompoundDistillationFractionator.get(1),
            2000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeAlloySmelter.get(1),
            1024000,
            4096,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeAlloySmelter.get(32), GregtechItemList.Industrial_AlloySmelter.get(32),
                GregtechItemList.Casing_BlastSmelter.get(64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUHV, 16),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Piston_UHV.get(32), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(294912),
                Materials.Lubricant.getFluid(128000), GTNLMaterials.Polyetheretherketone.getMolten(36864),
                MaterialsAlloy.PIKYONIUM.getFluidStack(36864) },
            GTNLItemList.ExtremeElectricFurnace.get(1),
            8000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.MegaOilCracker.get(1),
            2048000,
            4000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.MegaOilCracker.get(16), CustomItemList.tM_TeslaBase.get(64),
                GregtechItemList.Casing_Fusion_Internal.get(16), ItemList.Casing_Coil_AwakenedDraconium.get(64),
                ItemList.Electric_Pump_UHV.get(32), ItemList.Sensor_UHV.get(16), ItemList.Emitter_UHV.get(16),
                ItemRefer.HiC_T5.get(16), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 },
                NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GTNLMaterials.Polyetheretherketone.get(OrePrefixes.plateSuperdense, 8),
                GregtechItemList.Laser_Lens_Special.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(64000), GTNLMaterials.Polyetheretherketone.getMolten(64000),
                new FluidStack(GTPPFluids.Pyrotheum, 256000) },
            GTNLItemList.CrackerHub.get(1),
            2000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeChemicalBath.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeChemicalBath.get(64), ItemList.BlockHighPressureResistantCasing.get(64),
                ItemList.Electric_Pump_UHV.get(32), ItemList.Electric_Motor_UHV.get(32), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.Lubricant.getFluid(1024000), GTNLMaterials.Polyetheretherketone.getMolten(20736),
                MaterialsAlloy.PIKYONIUM.getFluidStack(20736) },
            GTNLItemList.MegaBathTank.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeCanning.get(1),
            2000000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeCanning.get(64), ItemList.Casing_Tank_10.get(32),
                ItemList.Electric_Pump_UHV.get(32), ItemList.Electric_Motor_UHV.get(32),
                ItemList.FluidRegulator_UHV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(8), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.MysteriousCrystal, 8) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(120000),
                Materials.Lubricant.getFluid(128000), GTNLMaterials.Polyetheretherketone.getMolten(163840),
                MaterialsAlloy.BOTMIUM.getFluidStack(144000) },
            GTNLItemList.MegaCanner.get(1),
            1100,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeElectrolyzer.get(1),
            1200000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeElectrolyzer.get(64), ItemList.Neutronium_Stable_Casing.get(64),
                new ItemStack(LanthItemList.ELECTRODE_CASING, 64), ItemList.Electric_Motor_UHV.get(32),
                ItemRefer.HiC_T5.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 },
                NHItemList.HighEnergyFlowCircuit.get(64), GregtechItemList.Laser_Lens_Special.get(4),
                GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4), ItemList.UHV_Coil.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.TengamPurified, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.TengamAttuned, 16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(60480),
                Materials.Lubricant.getFluid(64000), GTNLMaterials.Polyetheretherketone.getMolten(53280),
                Materials.SuperCoolant.getFluid(1000000) },
            GTNLItemList.GiantElectrochemicalWorkstation.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeBrewer.get(1),
            1440000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeBrewer.get(64), ItemList.Casing_Tank_10.get(64),
                ItemList.Casing_Tank_9.get(64), ItemList.Electric_Pump_UHV.get(32), ItemList.Field_Generator_UHV.get(8),
                ItemRefer.HiC_T5.get(48), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 48 },
                NHItemList.HighEnergyFlowCircuit.get(64), GregtechItemList.Laser_Lens_Special.get(2),
                GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUHV, 24),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(32767),
                Materials.Lubricant.getFluid(400000), Materials.BioMediumSterilized.getFluid(256000),
                Materials.BioMediumRaw.getFluid(512000) },
            GTNLItemList.MegaBrewer.get(1),
            1500,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeIncubator.get(1),
            4194304,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.LargeIncubator.get(8), ItemList.BlockUltraVioletLaserEmitter.get(64),
                ItemList.Emitter_UEV.get(32), ItemList.Field_Generator_UEV.get(8), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 48 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(2), GGMaterial.metastableOganesson.get(OrePrefixes.nanite, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 16),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(131072),
                Materials.Lubricant.getFluid(524288), Materials.StableBaryonicMatter.getFluid(64000) },
            GTNLItemList.MicroorganismMaster.get(1),
            4000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeExtractor.get(1),
            2000000,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.LargeExtractor.get(64), ItemList.Radiator_Fluid_Solidifier.get(64),
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.ZPM, 64), ItemList.Electric_Piston_UEV.get(32),
                ItemList.Electric_Pump_UEV.get(32), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(16),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 12),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.plateSuperdense, 8) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(100000),
                Materials.Lubricant.getFluid(512000), GGMaterial.atomicSeparationCatalyst.getMolten(230400) },
            GTNLItemList.PhaseChangeCube.get(1),
            2000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeMaterialPress.get(1),
            1440000,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.LargeMaterialPress.get(64), ItemList.BlockHighPressureResistantCasing.get(64),
                ItemList.Electric_Piston_UEV.get(64), ItemList.Electric_Motor_UEV.get(64), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(1), GGMaterial.metastableOganesson.get(OrePrefixes.nanite, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 16),
                WerkstoffLoader.HDCS.get(OrePrefixes.plateSuperdense, 8) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(50000),
                Materials.Lubricant.getFluid(128000), Materials.Neutronium.getMolten(144000),
                Materials.CosmicNeutronium.getMolten(144000) },
            GTNLItemList.HorizontalCompressor.get(1),
            1800,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.FluidHeaterUEV.get(1),
            1230000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.FluidHeaterUHV.get(16), ItemList.FluidHeaterUV.get(16),
                GregtechItemList.Casing_Adv_BlastFurnace.get(64), ItemList.FluidRegulator_UHV.get(16),
                ItemList.Emitter_UHV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(16),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.HellishMetal, 8) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(50000),
                Materials.Lubricant.getFluid(64000), new FluidStack(GTPPFluids.Pyrotheum, 256000),
                Materials.Helium.getPlasma(1024000) },
            GTNLItemList.MoltenCore.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeAutoclave.get(1),
            2560000,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.LargeAutoclave.get(64), GTNLItemList.HyperCore.get(16),
                CustomItemList.eM_Containment_Field.get(48), ItemList.Electric_Pump_UEV.get(32),
                ItemList.Emitter_UEV.get(16), ItemList.Sensor_UEV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 12 }, NHItemList.HighEnergyFlowCircuit.get(64),
                ItemList.EnergisedTesseract.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 24),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SuperconductorUEVBase, 4) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(123000),
                Materials.Lubricant.getFluid(123000), Materials.MysteriousCrystal.getMolten(1024000) },
            GTNLItemList.CrystalBuilder.get(1),
            1800,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.MegaMixer.get(1),
            100000000,
            65536,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { GTNLItemList.MegaMixer.get(4), ItemList.Machine_Multi_TranscendentPlasmaMixer.get(4),
                ItemList.Casing_Dim_Bridge.get(64), CustomItemList.eM_Containment_Field.get(48),
                ItemList.Electric_Motor_UEV.get(48), ItemList.Field_Generator_UEV.get(48),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GTNLItemList.EnhancementCore.get(64), ModsItemlist.EternalSingularity.get(32),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.TranscendentMetal, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.ProtoHalkonite, 8) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(143856),
                Materials.DimensionallyShiftedSuperfluid.getFluid(143856),
                Materials.TranscendentMetal.getMolten(143856) },
            GTNLItemList.KerrNewmanHomogenizer.get(1),
            4000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargePacker.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargePacker.get(64), ItemList.Casing_DataDrive.get(64),
                ItemList.Electric_Motor_UHV.get(16), ItemList.Electric_Piston_UHV.get(16), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(4),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Epoxid, 8) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(50000),
                Materials.Lubricant.getFluid(64000), GTNLMaterials.Polyetheretherketone.getMolten(28800),
                Materials.SuperCoolant.getFluid(1024000) },
            GTNLItemList.GeminiContainmentSystem.get(1),
            500,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeSiftingFunnel.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeSiftingFunnel.get(64), GregtechItemList.Casing_SifterGrate.get(64),
                GregtechItemList.Casing_SolarTower_HeatContainment.get(64), ItemList.Field_Generator_UHV.get(8),
                ItemRefer.HiC_T5.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                NHItemList.HighEnergyFlowCircuit.get(64), GregtechItemList.Laser_Lens_Special.get(1),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 1) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(30000),
                Materials.Lubricant.getFluid(256000), GTNLMaterials.Polyetheretherketone.getMolten(14400),
                Materials.SuperCoolant.getFluid(256000) },
            GTNLItemList.SmartSiftingHub.get(1),
            500,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.VacuumDryingFurnace.get(1),
            10000000,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.VacuumDryingFurnace.get(64),
                ModsItemlist.GTPlusPlusItemItemBufferCore10.get(16), ItemList.Electric_Pump_UEV.get(32),
                ItemList.Electric_Piston_UEV.get(16), ItemList.Emitter_UEV.get(16), ItemList.Sensor_UEV.get(16),
                ItemRefer.HiC_T5.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UIV), 12 },
                NHItemList.HighEnergyFlowCircuit.get(64), GregtechItemList.Laser_Lens_Special.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CrystallinePinkSlime, 4) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(100000),
                Materials.Lubricant.getFluid(1000000), Materials.Grade8PurifiedWater.getFluid(64000) },
            GTNLItemList.MegaVacuumDryingFurnace.get(1),
            1200,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.ElectricImplosionCompressor.get(1),
            10240000,
            65536,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTNLItemList.ElectricImplosionCompressor.get(32),
                ItemList.Machine_Multi_BlackHoleCompressor.get(2), ItemList.Casing_Autoclave.get(64),
                ItemList.Electric_Piston_UIV.get(32), ItemList.Field_Generator_UIV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(32),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 32),
                GGMaterial.enrichedNaquadahAlloy.get(OrePrefixes.plateSuperdense, 16) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(200000),
                Materials.Lubricant.getFluid(2000000), Materials.CosmicNeutronium.getMolten(1024000),
                Materials.Bedrockium.getMolten(1024000) },
            GTNLItemList.ExtremeCompressor.get(1),
            2000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.RareEarthCentrifugal.get(1),
            100000000,
            65536,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { GTNLItemList.RareEarthCentrifugal.get(24), ItemList.Casing_Electromagnetic_Separator.get(64),
                ItemList.Electric_Motor_UIV.get(64), ItemList.Field_Generator_UIV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 24 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GTNLItemList.EnhancementCore.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 24),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SixPhasedCopper, 4) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(131072),
                Materials.DimensionallyShiftedSuperfluid.getFluid(131072),
                Materials.TranscendentMetal.getMolten(131072) },
            GTNLItemList.AdvancedRareEarthCentrifugal.get(1),
            3000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            aeItems.cellSingularity()
                .maybeStack(1)
                .orNull(),
            2000000,
            40000,
            (int) TierEU.RECIPE_UIV,
            64,
            new Object[] { aeBlocks.craftingStorageSingularity()
                .maybeStack(1)
                .orNull(), ModsItemlist.AvaritiaAddonsInfinityChest.get(8),
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 16 },
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.TranscendentMetal, 16), ItemList.ZPM3.get(1),
                GregtechItemList.Laser_Lens_Special.get(64), GTNLItemList.ShatteredSingularity.get(64),
                aeMaterials.singularity()
                    .maybeStack(64)
                    .orNull(),
                NHItemList.EngravedQuantumChip.get(64),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Quantium, 64),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Quantium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Quantium, 16),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Quantium, 64) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(131072),
                Materials.DimensionallyShiftedSuperfluid.getFluid(256000),
                GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(64000),
                GTNLMaterials.QuantumInfusion.getFluidOrGas(256000) },
            GTNLItemList.QuantumComputerSingularityCore.get(1),
            18000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.DecayHastener.get(1),
            204800,
            4000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.DecayHastener.get(48), GTUtility.copyAmount(64, Loaders.NA.copy()),
                ItemRefer.Speeding_Pipe.get(64), CustomItemList.tM_TeslaBase.get(64), ItemList.Electric_Pump_UV.get(48),
                ItemList.Sensor_UV.get(48), ItemList.Emitter_UV.get(48),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 }, NHItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 16),
                GTNLMaterials.Polyetheretherketone.get(OrePrefixes.plateSuperdense, 8),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 2) },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(32000), Materials.Lubricant.getFluid(64000),
                GTNLMaterials.Polyetheretherketone.getMolten(64000), Materials.NaquadahEnriched.getMolten(14400) },
            GTNLItemList.FastNeutronBreederReactor.get(1),
            1000,
            (int) TierEU.RECIPE_UV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.NanoForge.get(1),
            37545000,
            32767,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.NanoForge.get(64), GTNLItemList.HyperCore.get(16),
                ItemRefer.Field_Restriction_Coil_T2.get(8), ItemList.Extreme_Density_Casing.get(64),
                ItemList.Electric_Motor_UHV.get(64), ItemList.Conveyor_Module_UHV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 16), ItemRefer.HiC_T5.get(32),
                NHItemList.HighEnergyFlowCircuit.get(64), GregtechItemList.Laser_Lens_Special.get(4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 32),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 32),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 32) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(320000),
                Materials.UUMatter.getFluid(190000), GTNLMaterials.Polyetheretherketone.getMolten(36000),
                Materials.Infinity.getMolten(20736) },
            GTNLItemList.SwarmCore.get(1),
            2000 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.FlotationCellRegulator.get(1),
            800000,
            32767,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.FlotationCellRegulator.get(64), GregtechItemList.Casing_Flotation_Cell.get(64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 32), ItemList.Electric_Motor_UHV.get(32),
                ItemList.Electric_Pump_UHV.get(16), MaterialsAlloy.CINOBITE.getGear(16),
                MaterialsAlloy.ABYSSAL.getPlateDouble(32), MaterialsAlloy.BOTMIUM.getRotor(4),
                NHItemList.HighEnergyFlowCircuit.get(64), ItemRefer.HiC_T5.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 16) },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(64000), Materials.Lubricant.getFluid(140000),
                GTNLMaterials.Polyetheretherketone.getMolten(14400), MaterialsAlloy.CINOBITE.getFluidStack(186624) },
            GTNLItemList.GiantFlotationTank.get(1),
            100 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);
    }

    public static void nanoForge() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        nanoForgeRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void nanoForgeRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                NHItemList.RadoxPolymerLens.get(0),
                GGMaterial.metastableOganesson.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_SoC2.get(64),
                ItemList.Circuit_Chip_SoC2.get(64))
            .itemOutputs(GGMaterial.metastableOganesson.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(500_000))
            .metadata(NANO_FORGE_TIER, 2)
            .duration(300 * SECONDS)
            .eut(40_000_000)
            .addTo(RecipeMaps.nanoForgeRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                NHItemList.ChromaticLens.get(0),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.block, 16),
                ItemList.Circuit_Chip_QPIC.get(64),
                ItemList.Circuit_Chip_QPIC.get(64),
                ItemList.Circuit_Chip_QPIC.get(64))
            .itemOutputs(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(1000_000))
            .metadata(NANO_FORGE_TIER, 2)
            .duration(300 * SECONDS)
            .eut(40_000_000)
            .addTo(RecipeMaps.nanoForgeRecipes);
    }

    public static void targetChamber() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        targetChamberRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void targetChamberRecipes() {
        ItemStack focusItem = new ItemStack(LanthItemList.maskMap.get(MaskList.CSOC), 0);

        RecipeBuilder.builder()
            .itemInputs(focusItem, ItemList.IC2_EnergyCrystal.getWildcard(1L))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, NHItemList.EngravedEnergyChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(4, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_IV)
            .addTo(LanthanidesRecipeMaps.targetChamberRecipes);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.plate, Materials.Diamond, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, NHItemList.EngravedDiamondCrystalChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(5, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(LanthanidesRecipeMaps.targetChamberRecipes);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.GarnetYellow, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, NHItemList.EngravedQuantumChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(6, 12, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(LanthanidesRecipeMaps.targetChamberRecipes);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.plate, Materials.Gold, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(1024, NHItemList.EngravedGoldChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(5, 12, 1)
                    .minFocus(35)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_IV)
            .addTo(LanthanidesRecipeMaps.targetChamberRecipes);

        RecipeBuilder.builder()
            .itemInputs(focusItem, NHItemList.ManyullynCrystal.get(1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, NHItemList.EngravedManyullynCrystalChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(6, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(LanthanidesRecipeMaps.targetChamberRecipes);
    }

    public static void craftingTable() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        craftingTableRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void craftingTableRecipes() {
        var aeItems = AEApi.instance()
            .definitions()
            .items();
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();
        var aeBlocks = AEApi.instance()
            .definitions()
            .blocks();

        GTModHandler.addCraftingRecipe(
            GTNLItemList.Desulfurizer.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', OrePrefixes.circuit.get(Materials.HV), 'B',
                NHItemList.AdsorptionFilter.get(1), 'C', ItemList.Electric_Pump_HV.get(1), 'D', ItemList.Hull_HV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Electrum, 1L), 'F',
                ItemList.Electric_Motor_HV.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.QuantumComputerUnit.get(1),
            new Object[] { "AB ", "CD ", "   ", 'A', aeBlocks.craftingUnit()
                .maybeStack(1)
                .orNull(), 'B',
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull(),
                'C', NHItemList.EngravedQuantumChip.get(1), 'D',
                NHItemList.EngineeringProcessorSpatialPulsatingCore.get(1) });
    }

    public static void fishingGround() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        fishingGroundRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void fishingGroundRecipes() {
        RecipeBuilder.builder()
            .itemInputs(NHItemList.MaceratedPlantmass.get(16))
            .circuit(9)
            .itemOutputs(
                GregtechItemList.AlgaeBiomass.get(64),
                GregtechItemList.GreenAlgaeBiomass.get(64),
                GregtechItemList.BrownAlgaeBiomass.get(64),
                GregtechItemList.GoldenBrownAlgaeBiomass.get(64),
                GregtechItemList.RedAlgaeBiomass.get(64))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.FishingGroundRecipes);
    }

    public static void primitiveBrickKiln() {
        if (!(Mods.NewHorizonsCoreMod.isModLoaded() && Mods.Railcraft.isModLoaded())) return;
        primitiveBrickKilnRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void primitiveBrickKilnRecipes() {
        RecipeBuilder.builder()
            .itemInputs(NHItemList.CokeOvenBrick.get(4))
            .itemOutputs(ItemList.CokeOvenCasing.get(1))
            .duration(200)
            .eut(16)
            .addTo(GTNLRecipeMaps.PrimitiveBrickKilnRecipes);
        RecipeBuilder.builder()
            .itemInputs(NHItemList.AdvancedCokeOvenBrick.get(4))
            .itemOutputs(ModsItemlist.RailcraftAdvancedCokeOvenBrick.get(1))
            .duration(200)
            .eut(16)
            .addTo(GTNLRecipeMaps.PrimitiveBrickKilnRecipes);
    }

    public static void twilightForest() {
        if (!Mods.NewHorizonsCoreMod.isModLoaded()) return;
        twilightForestRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    private static void twilightForestRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.NagaBook.get(0))
            .itemOutputs(
                ModsItemlist.NagaTrophy.get(1),
                ModsItemlist.TwilightForestItemNagaScale.get(32),
                NHItemList.NagaScaleFragment.get(32),
                NHItemList.NagaScaleChip.get(64))
            .outputChances(1000, 10000, 5000, 2500)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNLRecipeMaps.TheTwilightForestRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.LichBook.get(0))
            .itemOutputs(
                ModsItemlist.LichTrophy.get(1),
                NHItemList.LichBone.get(32),
                NHItemList.LichBoneFragment.get(32),
                NHItemList.LichBoneChip.get(64),
                new ItemStack(Items.ender_pearl, 32),
                new ItemStack(Items.book, 32),
                new ItemStack(Items.paper, 32))
            .outputChances(1000, 10000, 5000, 2500, 5000, 7500, 7500)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNLRecipeMaps.TheTwilightForestRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.UrGhastBook.get(0))
            .itemOutputs(
                ModsItemlist.UrGhastTrophy.get(1),
                ModsItemlist.TwilightForestItemFieryTears.get(12),
                ModsItemlist.TwilightForestItemCarminite.get(16),
                NHItemList.CarminiteFragment.get(32),
                NHItemList.CarminiteChip.get(64),
                ModsItemlist.TwilightForestItemSteeleafIngot.get(16),
                new ItemStack(Blocks.redstone_block, 4))
            .outputChances(1000, 10000, 10000, 5000, 2500, 5000, 7500)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNLRecipeMaps.TheTwilightForestRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.SnowQueenBook.get(0))
            .itemOutputs(
                ModsItemlist.SnowQueenTrophy.get(1),
                NHItemList.SnowQueenBlood.get(16),
                NHItemList.SnowQueenBloodDrop.get(32),
                new ItemStack(Blocks.packed_ice, 32),
                new ItemStack(Items.snowball, 64),
                ModsItemlist.TwilightForestAuroraBrick.get(64),
                ModsItemlist.TwilightForestTileAuroraPillar.get(64),
                ModsItemlist.TwilightForestItemIronwoodIngot.get(32),
                ModsItemlist.TwilightForestItemKnightMetal.get(32),
                ModsItemlist.TwilightForestItemArcticFur.get(32))
            .outputChances(1000, 7500, 5000, 8000, 10000, 7500, 7500, 5000, 5000, 8000)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNLRecipeMaps.TheTwilightForestRecipes);
    }

    public static void craftingTableExtra() {
        if (!(Mods.NewHorizonsCoreMod.isModLoaded() && RecipeConfig.enableSomethingRecipe)) return;
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 4L),
                ItemList.Hatch_Maintenance.get(2),
                ItemList.Robot_Arm_HV.get(2),
                ItemList.Hull_HV.get(1L))
            .itemOutputs(CustomItemList.hatch_CreativeMaintenance.get(1))
            .duration(0)
            .eut(0)
            .addTo(GTNLRecipeMaps.HardOverrideRecipes);

        GTModHandler.addCraftingRecipe(
            CustomItemList.hatch_CreativeMaintenance.get(1),
            CraftingTableRecipes.recipeFlags,
            new Object[] { "ABA", "CDC", "ABA", 'A', OrePrefixes.circuit.get(Materials.HV), 'B',
                ItemList.Hatch_Maintenance.get(1L), 'C', ItemList.Robot_Arm_HV.get(1L), 'D',
                ItemList.Hull_HV.get(1L) });
    }
}
