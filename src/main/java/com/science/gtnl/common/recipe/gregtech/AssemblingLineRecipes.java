package com.science.gtnl.common.recipe.gregtech;

import static bartworks.common.loaders.ItemRegistry.*;
import static goodgenerator.loader.Loaders.*;
import static gregtech.api.enums.MetaTileEntityIDs.*;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTRecipeBuilder.*;
import static gregtech.api.util.GTRecipeConstants.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.block.BlockList;
import com.reavaritia.ReAvaItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.WerkstoffLoader;
import ggfab.GGItemList;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.Particle;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;
import tectech.thing.casing.TTCasingsContainer;

public class AssemblingLineRecipes implements IRecipePool {

    public IRecipeMap AL = GTRecipeConstants.AssemblyLine;

    @Override
    public void loadRecipes() {
        TTRecipeAdder.addResearchableAssemblylineRecipe(
            kubatech.api.enums.ItemList.ExtremeIndustrialGreenhouse.get(1),
            256000,
            1024,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hull_UV.get(16), kubatech.api.enums.ItemList.ExtremeIndustrialGreenhouse.get(64),
                GTModHandler.getModItem(EnderIO.ID, "blockFarmStation", 64),
                GTModHandler.getModItem(RandomThings.ID, "fertilizedDirt", 64), ItemList.Field_Generator_UV.get(16),
                ItemList.Emitter_UV.get(16), ItemList.Sensor_UV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8L },
                GTModHandler.getModItem(Botania.ID, "overgrowthSeed", 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 16L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64L),
                GregtechItemList.Laser_Lens_Special.get(1), ItemList.Compressor_Casing.get(16),
                ItemList.Compressor_Pipe_Casing.get(16) },
            new FluidStack[] { Materials.BioMediumSterilized.getFluid(320000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(128000), Materials.Lubricant.getFluid(256000),
                Materials.Naquadria.getMolten(36864) },
            GTNLItemList.EdenGarden.get(1),
            30 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(Botania.ID, "lexicon", 1, 0))
            .metadata(SCANNING, new Scanning(1 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Hull_ZPM.get(8),
                GTModHandler.getModItem(Botania.ID, "pylon", 4, 2),
                GTModHandler.getModItem(Botania.ID, "pool", 16, 3),
                GTModHandler.getModItem(Botania.ID, "spreader", 8, 3),
                CustomItemList.LASERpipe.get(64),
                GTModHandler.getModItem(Botania.ID, "alfheimPortal", 64, 0),
                GTModHandler.getModItem(Botania.ID, "runeAltar", 64, 0),
                GTModHandler.getModItem(Botania.ID, "corporeaSpark", 64, 0),
                ItemList.Sensor_ZPM.get(16),
                ItemList.Field_Generator_ZPM.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4L },
                ItemList.RadiantNaquadahAlloyCasing.get(16),
                ItemList.Casing_Fusion_Coil.get(16),
                GTModHandler.getModItem(Botania.ID, "storage", 32, 0))
            .fluidInputs(
                MaterialsBotania.ElvenElementium.getMolten(144 * 64),
                MaterialsBotania.Terrasteel.getMolten(144 * 32),
                MaterialsAlloy.INDALLOY_140.getFluidStack(256000))
            .itemOutputs(GTNLItemList.TeleportationArrayToAlfheim.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(300 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(GalaxySpace.ID, "item.RocketControlComputer", 1, 4))
            .metadata(SCANNING, new Scanning(4 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                com.dreammaster.gthandler.CustomItemList.HeavyDutyPlateTier5.get(8),
                WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.plate, 32),
                ItemList.Electric_Motor_LuV.get(4),
                ItemList.Electric_Pump_LuV.get(4),
                ItemList.Conveyor_Module_LuV.get(4),
                ItemList.Robot_Arm_LuV.get(4),
                ItemList.Emitter_LuV.get(4),
                ItemList.Sensor_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 8L },
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4L })
            .fluidInputs(
                Materials.Lubricant.getFluid(128000),
                Materials.SolderingAlloy.getMolten(18432),
                Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid.getMolten(2304))
            .itemOutputs(GTNLItemList.MeteorMinerSchematic1.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(GalaxySpace.ID, "item.RocketControlComputer", 1, 7))
            .metadata(SCANNING, new Scanning(10 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                com.dreammaster.gthandler.CustomItemList.HeavyDutyPlateTier7.get(8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmiridium, 32),
                ItemList.Electric_Motor_UV.get(4),
                ItemList.Electric_Pump_UV.get(4),
                ItemList.Conveyor_Module_UV.get(4),
                ItemList.Robot_Arm_UV.get(4),
                ItemList.Emitter_UV.get(4),
                ItemList.Sensor_UV.get(4),
                ItemList.Field_Generator_UV.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 32),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L })
            .fluidInputs(
                Materials.Lubricant.getFluid(256000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(128000),
                Materials.NaquadahAlloy.getMolten(144 * 128),
                Materials.Longasssuperconductornameforuvwire.getMolten(144 * 32))
            .itemOutputs(GTNLItemList.MeteorMinerSchematic2.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(120 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.MixerUIV.get(1),
            102400000,
            25565,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { GregtechItemList.Mega_AlloyBlastSmelter.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Infinity, 64),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Infinity, 32),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 4, 0),
                ItemList.Electric_Motor_UEV.get(32), ItemList.Electric_Pump_UEV.get(32),
                ItemList.Field_Generator_UEV.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 32),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 32L },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 8L },
                kubatech.api.enums.ItemList.DEFCCasingBase.get(64), kubatech.api.enums.ItemList.DEFCCasingT3.get(32),
                ItemList.Casing_Dim_Injector.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 16) },
            new FluidStack[] { MaterialsUEVplus.TranscendentMetal.getMolten(73728),
                MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(73728), Materials.Neutronium.getMolten(294912),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(540000) },
            GTNLItemList.SmeltingMixingFurnace.get(1),
            120 * SECONDS,
            (int) TierEU.RECIPE_UMV);

        GTValues.RA.stdBuilder()
            .metadata(
                RESEARCH_ITEM,
                GTModHandler.getModItem(ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 1))
            .metadata(SCANNING, new Scanning(20 * MINUTES, TierEU.RECIPE_UHV))
            .itemInputs(
                GTModHandler.getModItem(ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(Thaumcraft.ID, "blockStoneDevice", 64, 2),
                GTModHandler.getModItem(Thaumcraft.ID, "blockStoneDevice", 64, 2),
                ItemUtils.getItemStack(
                    Thaumcraft.ID,
                    "WandCasting",
                    1,
                    9000,
                    "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                    null),
                GTModHandler.getModItem(Avaritia.ID, "Akashic_Record", 1),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16L },
                ItemList.Robot_Arm_UEV.get(32),
                ItemList.Field_Generator_UEV.get(16),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(32),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 8),
                ItemList.EnergisedTesseract.get(8),
                GTModHandler.getModItem(WitchingGadgets.ID, "item.WG_Material", 1, 7),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 64, 56))
            .fluidInputs(
                MaterialsUEVplus.ExcitedDTEC.getFluid(64000),
                Materials.StableBaryonicMatter.getFluid(64000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000))
            .itemOutputs(GTNLItemList.IndustrialArcaneAssembler.get(1))
            .eut(TierEU.RECIPE_UIV)
            .duration(300 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ReAvaItemList.NeutronCollector.get(1))
            .metadata(SCANNING, new Scanning(114 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ReAvaItemList.NeutronCollector.get(1),
                ReAvaItemList.NeutronCollector.get(1),
                ReAvaItemList.NeutronCollector.get(1),
                ReAvaItemList.NeutronCollector.get(1),
                ItemList.Electric_Motor_UHV.get(4L),
                ItemList.Field_Generator_UHV.get(4L),
                ItemList.Emitter_UHV.get(4L),
                ItemList.Sensor_UHV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 5L),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4L },
                GTModHandler.getModItem(Avaritia.ID, "Resource", 16, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUHV, 16L))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(2304), Materials.Grade7PurifiedWater.getFluid(16000))
            .itemOutputs(ReAvaItemList.DenseNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(60 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ReAvaItemList.DenseNeutronCollector.get(1))
            .metadata(SCANNING, new Scanning(4 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ReAvaItemList.DenseNeutronCollector.get(1),
                ReAvaItemList.DenseNeutronCollector.get(1),
                ReAvaItemList.DenseNeutronCollector.get(1),
                ReAvaItemList.DenseNeutronCollector.get(1),
                ItemList.Electric_Motor_UHV.get(8L),
                ItemList.Field_Generator_UHV.get(8L),
                ItemList.Emitter_UHV.get(8L),
                ItemList.Sensor_UHV.get(8L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 10L),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8L },
                GTModHandler.getModItem(Avaritia.ID, "Resource", 32, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16L))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(4608), Materials.Grade7PurifiedWater.getFluid(32000))
            .itemOutputs(ReAvaItemList.DenserNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(120 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ReAvaItemList.DenserNeutronCollector.get(1))
            .metadata(SCANNING, new Scanning(120 * MINUTES, TierEU.RECIPE_UV))
            .itemInputs(
                ReAvaItemList.DenserNeutronCollector.get(1),
                ReAvaItemList.DenserNeutronCollector.get(1),
                ReAvaItemList.DenserNeutronCollector.get(1),
                ReAvaItemList.DenserNeutronCollector.get(1),
                ItemList.Electric_Motor_UEV.get(8L),
                ItemList.Field_Generator_UEV.get(8L),
                ItemList.Emitter_UEV.get(8L),
                ItemList.Sensor_UEV.get(8L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 32L),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                GTModHandler.getModItem(Avaritia.ID, "Resource", 64, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 32L),
                GregtechItemList.Laser_Lens_Special.get(1))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(9216), Materials.Grade8PurifiedWater.getFluid(64000))
            .itemOutputs(ReAvaItemList.DensestNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UEV)
            .duration(60 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemRegistry.eic.copy(),
            51200000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTUtility.copyAmount(4, ItemRegistry.eic.copy()),
                GTUtility.copyAmount(16, BlockList.NaquadahPlatedReinforcedStone.getIS()),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Osmiridium, 32),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.NaquadahAlloy, 64),
                ItemList.Electric_Motor_UV.get(64), ItemList.Electric_Piston_UV.get(32),
                ItemList.Conveyor_Module_UV.get(32), ItemList.Field_Generator_UV.get(16), ItemList.Robot_Arm_UV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 4L) },
            new FluidStack[] { Materials.Grade6PurifiedWater.getFluid(128000),
                MaterialsAlloy.BOTMIUM.getFluidStack(4608), WerkstoffLoader.Oganesson.getFluidOrGas(16000) },
            GTNLItemList.ElectricImplosionCompressor.get(1),
            120 * SECONDS,
            (int) TierEU.RECIPE_UEV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.ElectricBlastFurnace.get(1))
            .metadata(SCANNING, new Scanning(60 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTNLItemList.ElectricBlastFurnace.get(16),
                GTNLItemList.ElectricBlastFurnace.get(16),
                GTNLItemList.ElectricBlastFurnace.get(16),
                GTNLItemList.ElectricBlastFurnace.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L },
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.NaquadahAlloy, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Tritanium, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Americium, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.BlackPlutonium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 16L),
                ItemList.Field_Generator_ZPM.get(4L),
                ItemList.Energy_Module.get(1L))
            .fluidInputs(
                Materials.Grade4PurifiedWater.getFluid(64000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(14400),
                MaterialsAlloy.ZERON_100.getFluidStack(18432),
                Materials.SolderingAlloy.getMolten(36864))
            .itemOutputs(GTNLItemList.MegaBlastFurnace.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(50 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GregtechItemList.ChemicalPlant_Controller.get(1),
            51200000,
            25600,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GregtechItemList.ChemicalPlant_Controller.get(8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 8),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Neutronium, 32),
                ItemList.Electric_Motor_UV.get(16), ItemList.Electric_Pump_UV.get(16),
                ItemList.Field_Generator_ZPM.get(8), GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Osmium, 32),
                CI.getEmptyCatalyst(16), new Object[] { OrePrefixes.circuit.get(Materials.LuV), 24L },
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 20L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.ElectrumFlux, 32L) },
            new FluidStack[] { MaterialsKevlar.Kevlar.getMolten(23040), Materials.CosmicNeutronium.getMolten(4608),
                Materials.Grade6PurifiedWater.getFluid(32000), MaterialsAlloy.INDALLOY_140.getFluidStack(256000) },
            GTNLItemList.HandOfJohnDavisonRockefeller.get(1),
            60 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 2, 60),
                ItemList.Hatch_Input_Bus_ME_Advanced.get(2),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 16, 54),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockController", 1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockDenseEnergyCell", 1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiPart", 5, 440),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiPart", 1, 480))
            .fluidInputs(Materials.SolderingAlloy.getMolten(4608), MaterialsAlloy.INDALLOY_140.getFluidStack(2304))
            .itemOutputs(GTNLItemList.SuperCraftingInputBusME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_CraftingInput_Bus_ME.get(1),
            1920000,
            4000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hatch_CraftingInput_Bus_ME.get(1), ItemRefer.Fluid_Storage_Core_T6.get(4),
                GTModHandler.getModItem(AvaritiaAddons.ID, "CompressedChest", 4),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockCreativeEnergyCell", 1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 16, 60),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_part", 16, 7),
                ItemList.Hatch_Input_Bus_ME_Advanced.get(4), ItemList.Hatch_Input_ME_Advanced.get(4),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 64, 54) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(16000),
                Materials.Infinity.getMolten(18432), Materials.Grade5PurifiedWater.getFluid(64000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(512000) },
            GTNLItemList.SuperCraftingInputHatchME.get(1),
            60 * SECONDS,
            (int) TierEU.RECIPE_UV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.DualInputHatchLuV.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTNLItemList.DualInputHatchLuV.get(1),
                ItemList.Emitter_LuV.get(1),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4L },
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockInterface", 3),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_interface", 3),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 30),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 2, 27),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576), Materials.Lubricant.getFluid(500))
            .itemOutputs(ItemList.Hatch_CraftingInput_Bus_ME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_CraftingInput_Bus_Slave.get(1),
            3840000,
            16000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hatch_CraftingInput_Bus_Slave.get(1), ItemList.Tool_DataOrb.get(32),
                ItemList.Tool_DataStick.get(32),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 32, 41),
                ItemList.Emitter_UV.get(4), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 1L },
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockQuantumRing", 8),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockQuantumLinkChamber", 1), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(16000),
                Materials.Infinity.getMolten(36864), Materials.Grade6PurifiedWater.getFluid(16000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(25600) },
            GTNLItemList.SuperCraftingInputProxy.get(1),
            30 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hatch_CraftingInput_Bus_ME.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_LuV.get(1),
                ItemList.Sensor_LuV.get(2),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 1L },
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockQuantumLinkChamber", 1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockQuantumRing", 2),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576), Materials.Lubricant.getFluid(500))
            .itemOutputs(ItemList.Hatch_CraftingInput_Bus_Slave.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(30 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemRefer.Field_Restriction_Glass.get(1),
            51200000,
            12800,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { CustomItemList.eM_Hollow.get(1), ItemList.Field_Generator_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorLuV, 6L),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 4L }, ItemList.WetTransformer_UHV_UV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.VanadiumGallium, 6L),
                GGMaterial.enrichedNaquadahAlloy.get(OrePrefixes.plateDouble, 4) },
            new FluidStack[] { Materials.Lanthanum.getMolten(2304), Materials.CobaltBrass.getMolten(5760),
                Materials.BatteryAlloy.getMolten(5760), MaterialPool.MolybdenumDisilicide.getMolten(1296) },
            CustomItemList.eM_Containment_Field.get(1),
            25 * SECONDS,
            (int) TierEU.RECIPE_UV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.IVParallelControllerCore.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSS, 2L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.HSSS, 8L),
                ItemList.Robot_Arm_LuV.get(2),
                ItemList.Emitter_LuV.get(2),
                ItemList.Sensor_LuV.get(2),
                ItemList.Field_Generator_LuV.get(1),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 4L },
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.VanadiumGallium, 4L))
            .fluidInputs(
                Materials.Polytetrafluoroethylene.getMolten(576),
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296))
            .itemOutputs(GTNLItemList.LuVParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(20 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.LuVParallelControllerCore.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_ZPM.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 8L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 2L),
                MaterialsAlloy.ZERON_100.getPlateDense(2),
                MaterialsAlloy.PIKYONIUM.getScrew(64),
                MaterialsAlloy.PIKYONIUM.getScrew(64),
                ItemList.Electric_Motor_ZPM.get(8L),
                ItemList.Robot_Arm_ZPM.get(4L),
                ItemList.Emitter_ZPM.get(2L),
                ItemList.Sensor_ZPM.get(2L),
                ItemList.Field_Generator_ZPM.get(2L),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L },
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Naquadah, 4L))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(2592),
                Materials.Europium.getMolten(2592),
                Materials.Trinium.getMolten(1296),
                Materials.Polybenzimidazole.getMolten(4608))
            .itemOutputs(GTNLItemList.ZPMParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(20 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.ZPMParallelControllerCore.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Hull_UV.get(8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Osmiridium, 4L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Neutronium, 8L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Neutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Neutronium, 64L),
                ItemList.Electric_Motor_UV.get(16L),
                ItemList.Robot_Arm_UV.get(8L),
                ItemList.Emitter_UV.get(4L),
                ItemList.Sensor_UV.get(4L),
                ItemList.Field_Generator_UV.get(2L),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 8L))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(4608),
                Materials.Polybenzimidazole.getMolten(9216),
                Materials.Naquadria.getMolten(2592),
                Materials.Americium.getMolten(1296))
            .itemOutputs(GTNLItemList.UVParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(20 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UVParallelControllerCore.get(1),
            51200000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.Hull_MAX.get(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bedrockium, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 1L),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.CosmicNeutronium, 16L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 4L), ItemList.Robot_Arm_UHV.get(16L),
                ItemList.Emitter_UHV.get(8L), ItemList.Sensor_UHV.get(8L), ItemList.Field_Generator_UHV.get(4L),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16L),
                ItemList.Energy_Cluster.get(4), MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFrameBox(16) },
            new FluidStack[] { Materials.RadoxPolymer.getMolten(16000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(16000), Materials.Naquadria.getMolten(9216) },
            GTNLItemList.UHVParallelControllerCore.get(1),
            20 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UHVParallelControllerCore.get(1),
            204800000,
            204800,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { ItemList.Hull_UEV.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 16L),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Infinity, 16L),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Infinity, 32L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Infinity, 64L),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 1, 0), ItemList.Tesseract.get(4L),
                ItemList.Robot_Arm_UEV.get(32L), ItemList.Emitter_UEV.get(16L), ItemList.Sensor_UEV.get(16L),
                ItemList.Field_Generator_UEV.get(8L), new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 32L) },
            new FluidStack[] { Materials.Grade7PurifiedWater.getFluid(64000), Materials.RadoxPolymer.getMolten(32000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(32000),
                WerkstoffLoader.Oganesson.getFluidOrGas(16000) },
            GTNLItemList.UEVParallelControllerCore.get(1),
            20 * SECONDS,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UEVParallelControllerCore.get(1),
            819200000,
            819200,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { ItemList.Hull_UIV.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.TranscendentMetal, 64L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, MaterialsUEVplus.ProtoHalkonite, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Mellion, 4L),
                ItemList.Robot_Arm_UIV.get(64L), ItemList.Emitter_UIV.get(32L), ItemList.Sensor_UIV.get(32L),
                ItemList.Field_Generator_UIV.get(32L), new Object[] { OrePrefixes.circuit.get(Materials.UMV), 32L },
                GTModHandler.getModItem(DraconicEvolution.ID, "awakenedCore", 8, 0), ItemList.Tesseract.get(16L),
                GTNLItemList.EnhancementCore.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 4L),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 4, 0),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64L) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(32000),
                GGMaterial.metastableOganesson.getMolten(36864),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(128000), MaterialsUEVplus.SpaceTime.getMolten(2304) },
            GTNLItemList.UIVParallelControllerCore.get(1),
            20 * SECONDS,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UIVParallelControllerCore.get(1),
            819200000,
            1638400,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { ItemList.Hull_UMV.get(64), MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsKevlar.Kevlar, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.EnrichedHolmium, 16L),
                GGMaterial.shirabon.get(OrePrefixes.plateDense, 32), ItemRefer.Compassline_Casing_UMV.get(8),
                CustomItemList.dataIn_Wireless_Hatch.get(8), ItemList.ZPM3.get(8), ItemList.Robot_Arm_UMV.get(64L),
                ItemList.Emitter_UMV.get(64L), ItemList.Sensor_UMV.get(64L), ItemList.Field_Generator_UMV.get(64L),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64L }, ItemList.EnergisedTesseract.get(32),
                ItemList.Transdimensional_Alignment_Matrix.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUMV, 64L) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(256000),
                MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(64000),
                MaterialsUEVplus.ExcitedDTEC.getFluid(64000), MaterialsUEVplus.SpaceTime.getMolten(9216) },
            GTNLItemList.UMVParallelControllerCore.get(1),
            20 * SECONDS,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            DraconicEvolution.isModLoaded() ? kubatech.api.enums.ItemList.DraconicEvolutionFusionCrafter.get(1)
                : GTNLItemList.BlazeCubeBlock.get(1),
            25600000,
            51200,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] {
                DraconicEvolution.isModLoaded() ? kubatech.api.enums.ItemList.DraconicEvolutionFusionCrafter.get(1)
                    : GTNLItemList.BlazeCubeBlock.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Draconium, 4L),
                ItemList.Emitter_UHV.get(16), ItemList.Field_Generator_UHV.get(16),
                GTModHandler.getModItem(DraconicEvolution.ID, "draconicCore", 32),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16L }, ItemList.ZPM2.get(4),
                ItemList.NuclearStar.get(4), GTModHandler.getModItem(DraconicEvolution.ID, "dragonHeart", 1),
                GTModHandler.getModItem(DraconicEvolution.ID, "chaosShard", 1),
                GregtechItemList.Laser_Lens_Special.get(4) },
            new FluidStack[] { Materials.DraconiumAwakened.getMolten(36864), Materials.Void.getMolten(73728),
                MaterialsAlloy.INDALLOY_140.getFluidStack(32000), },
            GTNLItemList.DraconicFusionCrafting.get(1),
            120 * SECONDS,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 4),
            1024000000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GregtechItemList.GTPP_Casing_UHV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4 }, ItemList.Field_Generator_UHV.get(8),
                ItemList.Robot_Arm_UHV.get(16), ItemList.Emitter_UHV.get(16),
                GTModHandler.getModItem(TwilightForest.ID, "tile.TFMagicLogSpecial", 64, 0),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.IronWood, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Steeleaf, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.FierySteel, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Knightmetal, 64L),
                GTModHandler.getModItem(TwilightForest.ID, "item.magicMapFocus", 64, 0),
                GTModHandler.getModItem(TwilightForest.ID, "item.mazeMapFocus", 32, 0),
                GTModHandler.getModItem(TwilightForest.ID, "item.lampOfCinders", 1, 0) },
            new FluidStack[] { Materials.FierySteel.getFluid(32000), Materials.SolderingAlloy.getMolten(73728),
                MaterialsAlloy.INDALLOY_140.getFluidStack(36864), },
            GTNLItemList.LibraryOfRuina.get(1),
            300 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Machine_Multi_Furnace.get(1),
            512000,
            512,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.Machine_Multi_Furnace.get(16), ItemList.Machine_Multi_Furnace.get(16),
                ItemList.Machine_Multi_Furnace.get(16), ItemList.Machine_Multi_Furnace.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32L }, ItemList.Field_Generator_UV.get(16),
                ItemList.Emitter_UV.get(32), ItemList.Sensor_UHV.get(32), GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 32L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.DraconiumAwakened, 4L) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(640000),
                Materials.CosmicNeutronium.getMolten(9216), Materials.Grade6PurifiedWater.getFluid(64000), },
            GTNLItemList.ReactionFurnace.get(1),
            300 * SECONDS,
            (int) TierEU.RECIPE_UHV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GregtechItemList.Controller_IsaMill.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GregtechItemList.Controller_IsaMill.get(1),
                MaterialsAlloy.ZERON_100.getPlateDouble(8),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2L },
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 4L },
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorLuV, 4L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 8L),
                ItemList.Component_Grinder_Tungsten.get(8),
                ItemList.Conveyor_Module_LuV.get(4),
                ItemList.Electric_Motor_LuV.get(8))
            .fluidInputs(Materials.Grade2PurifiedWater.getFluid(32000), Materials.Europium.getMolten(1296))
            .itemOutputs(GTNLItemList.IsaMill.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(40 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.Incubator.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                GTNLItemList.Incubator.get(4),
                ItemList.ActivatedCarbonFilterMesh.get(32),
                ItemList.Field_Generator_UV.get(16),
                ItemList.Emitter_UV.get(32),
                ItemList.Sensor_UV.get(32),
                ItemList.Robot_Arm_UV.get(32),
                ItemList.Conveyor_Module_UV.get(32),
                ItemList.Electric_Pump_UV.get(48),
                GGMaterial.lumiium.get(OrePrefixes.cableGt08, 32),
                new ItemStack(bw_realglas, 32, 5))
            .fluidInputs(Materials.Grade5PurifiedWater.getFluid(32000), Materials.CosmicNeutronium.getMolten(2304))
            .itemOutputs(GTNLItemList.LargeIncubator.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(30 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.AmplifabricatorZPM.get(1L),
            512000,
            256,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { ItemList.AmplifabricatorZPM.get(8), ItemList.Electric_Pump_ZPM.get(32),
                ItemList.Field_Generator_ZPM.get(16), new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16 },
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Trinium, 24), ItemList.Energy_Module.get(8) },
            new FluidStack[] { Materials.Tritanium.getMolten(4608), Materials.Grade7PurifiedWater.getFluid(32000),
                MaterialsAlloy.ZERON_100.getFluidStack(9216) },
            GTNLItemList.MatterFabricator.get(1),
            200,
            (int) TierEU.RECIPE_UV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.AcceleratorZPM.get(1L),
            102400,
            32,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { ItemList.Neutron_Reflector.get(8), ItemList.Field_Generator_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 4 },
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Naquadria, 16),
                MaterialPool.Darmstadtium.get(OrePrefixes.stickLong, 8), MaterialsAlloy.INCOLOY_MA956.getPlateDouble(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Thorium, 16) },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(1296),
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296) },
            GTNLItemList.DecayHastener.get(1),
            400,
            (int) TierEU.RECIPE_UV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.AlloyBlastSmelter.get(1),
            1024000,
            2048,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.AlloyBlastSmelter.get(2), GTNLItemList.AlloyBlastSmelter.get(2),
                GTNLItemList.AlloyBlastSmelter.get(2), GTNLItemList.AlloyBlastSmelter.get(2), ItemList.UHV_Coil.get(32),
                ItemList.Conveyor_Module_UV.get(16), ItemList.Circuit_Chip_PPIC.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16 },
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32 }, MaterialsAlloy.PIKYONIUM.getFrameBox(32),
                MaterialsAlloy.CINOBITE.getPlateDense(12),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 8) },
            new FluidStack[] { Materials.Grade6PurifiedWater.getFluid(16000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(18432), MaterialsAlloy.INDALLOY_140.getFluidStack(4608) },
            GTNLItemList.MegaAlloyBlastSmelter.get(1),
            400,
            (int) TierEU.RECIPE_UV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GregtechItemList.Controller_Flotation_Cell.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GregtechItemList.Controller_Flotation_Cell.get(1),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4 },
                ItemList.Electric_Motor_LuV.get(8),
                ItemList.Electric_Piston_LuV.get(8),
                MaterialsAlloy.HASTELLOY_C276.getPlateDouble(16),
                MaterialsAlloy.STELLITE.getGear(16),
                WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.rotor, 16),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.YttriumBariumCuprate, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152), MaterialsAlloy.INCONEL_690.getFluidStack(1152))
            .itemOutputs(GTNLItemList.FlotationCellRegulator.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.OreDrill3.get(1),
            10240000,
            51200,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { ItemRegistry.voidminer[2].copy(), ItemList.OilDrillInfinite.get(1),
                ItemList.Robot_Arm_UV.get(4), MaterialsAlloy.STELLITE.getGear(16), ItemList.Conveyor_Module_UV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 4),
                MaterialsAlloy.PIKYONIUM.getPlateDouble(8) },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(2880), GGMaterial.artheriumSn.getMolten(2880) },
            GTNLItemList.ResourceCollectionModule.get(1),
            1200,
            (int) TierEU.RECIPE_UV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemRegistry.megaMachines[4])
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.MixerLuV.get(4),
                ItemList.CentrifugeLuV.get(4),
                ItemList.DistilleryLuV.get(4),
                ItemList.ChemicalReactorLuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.TungstenSteel, 8L),
                ItemList.Emitter_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 4L },
                ItemList.Electric_Piston_LuV.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.HSSE, 16L))
            .fluidInputs(
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Polytetrafluoroethylene.getMolten(2304),
                MaterialsAlloy.AQUATIC_STEEL.getFluidStack(1152))
            .itemOutputs(GTNLItemList.FuelRefiningComplex.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(50 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            CustomItemList.Godforge_SingularityShieldingCasing.get(1),
            819200000,
            512000,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { CustomItemList.Godforge_SingularityShieldingCasing.get(1), ItemList.Emitter_UMV.get(4),
                ItemList.Sensor_UMV.get(4), new Object[] { OrePrefixes.circuit.get(Materials.UXV), 4 },
                ItemList.Field_Generator_UMV.get(16), GTNLItemList.EnhancementCore.get(64), ItemList.UHV_Coil.get(64),
                ItemList.UHV_Coil.get(64), ItemList.ZPM5.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, MaterialsUEVplus.WhiteDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, MaterialsUEVplus.BlackDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, MaterialsUEVplus.MagMatter, 8) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(4000),
                MaterialsUEVplus.Mellion.getMolten(4608), Materials.Europium.getMolten(9216),
                GGMaterial.tairitsu.getMolten(9216) },
            GTNLItemList.RealArtificialStar.get(1),
            1800,
            (int) TierEU.RECIPE_UXV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GGItemList.AdvAssLine.get(1),
            20480000,
            12800,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GGItemList.AdvAssLine.get(4), GGItemList.AdvAssLine.get(4), GGItemList.AdvAssLine.get(4),
                GGItemList.AdvAssLine.get(4), GregtechItemList.TransmissionComponent_UHV.get(32),
                ItemList.Robot_Arm_UHV.get(32), ItemList.Conveyor_Module_UHV.get(32),
                ItemList.Field_Generator_UHV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTModHandler.getModItem(AvaritiaAddons.ID, "InfinityChest", 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 64),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 2) },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(23040),
                MaterialsAlloy.INDALLOY_140.getFluidStack(12800), Materials.Polytetrafluoroethylene.getMolten(46080),
                Materials.Grade6PurifiedWater.getFluid(64000) },
            GTNLItemList.GrandAssemblyLine.get(1),
            1200,
            (int) TierEU.RECIPE_UEV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(TwilightForest.ID, "item.lampOfCinders", 1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_UV))
            .itemInputs(
                new ItemStack(Items.book, 64),
                GTNLItemList.NagaBook.get(4),
                GTNLItemList.LichBook.get(4),
                GTNLItemList.MinotaurBook.get(4),
                GTNLItemList.HydraBook.get(4),
                GTNLItemList.KnightPhantomBook.get(4),
                GTNLItemList.UrGhastBook.get(4),
                GTNLItemList.AlphaYetiBook.get(4),
                GTNLItemList.SnowQueenBook.get(4),
                GTNLItemList.GiantBook.get(4),
                GTModHandler.getModItem(TwilightForest.ID, "item.mazebreakerPick", 1),
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 8),
                GTModHandler.getModItem(TwilightForest.ID, "item.crumbleHorn", 1),
                GTModHandler.getModItem(TwilightForest.ID, "item.charmOfKeeping3", 8),
                GTModHandler.getModItem(TwilightForest.ID, "item.charmOfLife2", 32),
                GTModHandler.getModItem(TwilightForest.ID, "tile.TFSapling", 64, 5))
            .fluidInputs(
                Materials.FierySteel.getFluid(64000),
                FluidRegistry.getFluidStack("xpjuice", 2560000),
                Materials.AdvancedGlue.getFluid(640000),
                MaterialMisc.ETHYL_CYANOACRYLATE.getFluidStack(320000))
            .itemOutputs(GTNLItemList.TwilightForestBook.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(60 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(BloodMagic.ID, "Altar", 1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                GTModHandler.getModItem(BloodMagic.ID, "masterStone", 32),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Trinium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 64L),
                ItemList.Field_Generator_ZPM.get(16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 5),
                ItemList.Electric_Pump_ZPM.get(32),
                ItemList.Emitter_ZPM.get(32),
                GTModHandler.getModItem(BloodArsenal.ID, "lp_materializer", 1),
                GTModHandler.getModItem(BloodArsenal.ID, "life_infuser", 1),
                GTModHandler.getModItem(BloodMagic.ID, "blockWritingTable", 1),
                GTModHandler.getModItem(BloodMagic.ID, "activationCrystal", 1, 1),
                GTModHandler.getModItem(BloodMagic.ID, "itemRitualDiviner", 1, 2))
            .fluidInputs(
                Materials.Grade4PurifiedWater.getFluid(64000),
                Materials.Americium.getMolten(4608),
                Materials.Neutronium.getMolten(2304),
                Materials.NaquadahEnriched.getMolten(1152))
            .itemOutputs(GTNLItemList.BloodSoulSacrificialArray.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(120 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            new ItemStack(ItemRegistry.cal.getItem(), 1),
            256000,
            1024,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { new ItemStack(ItemRegistry.cal.getItem(), 4), new ItemStack(ItemRegistry.cal.getItem(), 4),
                new ItemStack(ItemRegistry.cal.getItem(), 4), new ItemStack(ItemRegistry.cal.getItem(), 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Infinity, 16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 64L),
                ItemList.Field_Generator_UHV.get(16), ItemList.Conveyor_Module_UHV.get(32),
                ItemList.Robot_Arm_UHV.get(32), GregtechItemList.Laser_Lens_Special.get(1),
                ItemList.Emitter_ZPM.get(32), GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 5),
                ItemList.Sensor_UHV.get(32) },
            new FluidStack[] { Materials.Grade6PurifiedWater.getFluid(64000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(32000), Materials.CosmicNeutronium.getMolten(2304) },
            GTNLItemList.CircuitComponentAssemblyLine.get(1),
            4800,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.ChemicalPlant.get(1),
            12000,
            16,
            (int) TierEU.RECIPE_ZPM,
            1,
            new Object[] { GTNLItemList.ChemicalPlant.get(16),
                new ItemStack(ItemRegistry.megaMachines[3].getItem(), 16, 13366),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 64L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Naquadah, 16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 8), ItemList.Field_Generator_ZPM.get(8),
                ItemList.Electric_Pump_ZPM.get(16), ItemList.Emitter_ZPM.get(16),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Europium, 48) },
            new FluidStack[] { Materials.Grade4PurifiedWater.getFluid(64000),
                MaterialPool.Polyetheretherketone.getMolten(4608), MaterialsAlloy.INDALLOY_140.getFluidStack(16000) },
            GTNLItemList.ShallowChemicalCoupling.get(1),
            2400,
            (int) TierEU.RECIPE_ZPM);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.SpaceElevatorController.get(1),
            96000,
            256,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.SpaceElevatorController.get(2), ItemList.SpaceElevatorController.get(2),
                ItemList.SpaceElevatorController.get(2), ItemList.SpaceElevatorController.get(2),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.TranscendentMetal, 32),
                ItemList.Field_Generator_UEV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 }, ItemList.Circuit_Chip_QPIC.get(64),
                GTModHandler.getModItem(GalacticraftAmunRa.ID, "item.baseItem", 64, 15),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Infinity, 64),
                ItemList.SpaceElevatorBaseCasing.get(64) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(4000),
                MaterialsUEVplus.MoltenProtoHalkoniteBase.getFluid(8000),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(16000), Materials.Infinity.getMolten(4608) },
            GTNLItemList.SuperSpaceElevator.get(1),
            9000,
            (int) TierEU.RECIPE_UEV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                CustomItemList.Machine_Multi_Transformer.get(1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 47),
                ItemList.Field_Generator_LuV.get(2),
                ItemList.Emitter_LuV.get(4),
                ItemList.Casing_Fusion_Coil.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 1L },
                CustomItemList.LASERpipe.get(32))
            .fluidInputs(
                Materials.Europium.getMolten(1728),
                Materials.NaquadahAlloy.getMolten(3456),
                Materials.SuperCoolant.getFluid(6912))
            .itemOutputs(ItemList.WormholeGenerator.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(120 * SECONDS)
            .addTo(AL);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, new ItemStack(GregTechAPI.sBlockMachines, 1, BioLab_LuV.ID))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                new ItemStack(GregTechAPI.sBlockMachines, 8, BioLab_LuV.ID),
                WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.plateSuperdense, 1),
                WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.ring, 32),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Naquadah, 4),
                ItemList.Field_Generator_LuV.get(2),
                ItemList.Emitter_LuV.get(4),
                ItemList.Sensor_LuV.get(4),
                ItemList.Electric_Pump_LuV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 4L },
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 8L },
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 16L },
                ItemList.Casing_Vent.get(32))
            .fluidInputs(
                MaterialPool.Polyetheretherketone.getMolten(1152),
                MaterialsAlloy.INDALLOY_140.getFluidStack(1152),
                Materials.Lubricant.getFluid(32000))
            .itemOutputs(GTNLItemList.LargeBioLab.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(60 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeAssembler.get(1),
            5000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeAssembler.get(64), GTNLItemList.PreciseAssembler.get(64),
                ItemList.AssemblingMachineUEV.get(64), GregtechItemList.NeutronPulseManipulator.get(32),
                ItemRefer.Compassline_Casing_UEV.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16 }, ItemList.Conveyor_Module_UEV.get(32),
                ItemList.Robot_Arm_UEV.get(32), ItemList.Field_Generator_UEV.get(8), ItemList.Tesseract.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 2) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(16000),
                Materials.SuperCoolant.getFluid(128000), Materials.Tin.getPlasma(36384),
                Materials.Lubricant.getFluid(64000) },
            GTNLItemList.IntegratedAssemblyFacility.get(1),
            2400,
            (int) TierEU.RECIPE_UIV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.AlloySmelterUIV.get(1))
            .metadata(SCANNING, new Scanning(24 * HOURS, TierEU.RECIPE_LV))
            .itemInputs(
                CustomItemList.Godforge_SingularityShieldingCasing.get(4),
                ItemList.AlloySmelterUIV.get(64),
                ItemList.AlloySmelterUIV.get(64),
                ItemList.ZPM4.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 16),
                ItemList.Robot_Arm_UIV.get(16),
                ItemList.Conveyor_Module_UIV.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Creon, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Mellion, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L })
            .fluidInputs(
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(147456),
                MaterialsUEVplus.ExcitedDTEC.getFluid(2048000),
                Materials.Lead.getPlasma(36864),
                MaterialsUEVplus.TranscendentMetal.getMolten(147456))
            .itemOutputs(GTNLItemList.FOGAlloySmelterModule.get(1))
            .eut(TierEU.RECIPE_UMV)
            .duration(300 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.FluidExtractorUIV.get(1))
            .metadata(SCANNING, new Scanning(24 * HOURS, TierEU.RECIPE_LV))
            .itemInputs(
                CustomItemList.Godforge_SingularityShieldingCasing.get(4),
                ItemList.ExtractorUIV.get(64),
                ItemList.FluidExtractorUIV.get(64),
                ItemList.ZPM4.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 16),
                ItemList.Robot_Arm_UIV.get(16),
                ItemList.Conveyor_Module_UIV.get(32),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Relativistic_Heat_Capacitor.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Creon, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Mellion, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L })
            .fluidInputs(
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(147456),
                MaterialsUEVplus.ExcitedDTEC.getFluid(2048000),
                Materials.Lead.getPlasma(36864),
                MaterialsUEVplus.TranscendentMetal.getMolten(147456))
            .itemOutputs(GTNLItemList.FOGExtractorModule.get(1))
            .eut(TierEU.RECIPE_UMV)
            .duration(300 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GregtechItemList.Mega_AlloyBlastSmelter.get(1))
            .metadata(SCANNING, new Scanning(24 * HOURS, TierEU.RECIPE_LV))
            .itemInputs(
                CustomItemList.Godforge_SingularityShieldingCasing.get(4),
                GregtechItemList.Mega_AlloyBlastSmelter.get(64),
                GregtechItemList.Mega_AlloyBlastSmelter.get(64),
                ItemList.ZPM4.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 16),
                ItemList.Robot_Arm_UIV.get(16),
                ItemList.Conveyor_Module_UIV.get(32),
                ItemList.Electric_Pump_UIV.get(64),
                ItemList.Relativistic_Heat_Capacitor.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Creon, 8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.Mellion, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L })
            .fluidInputs(
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(147456),
                MaterialsUEVplus.ExcitedDTEC.getFluid(2048000),
                MaterialsUEVplus.PhononMedium.getFluid(32000),
                MaterialsUEVplus.TranscendentMetal.getMolten(147456))
            .itemOutputs(GTNLItemList.FOGAlloyBlastSmelterModule.get(1))
            .eut(TierEU.RECIPE_UMV)
            .duration(300 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.Laser_Cooling_Casing.get(1),
            2000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.EnhancementCore.get(1), GTNLItemList.Laser_Cooling_Casing.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16), new ItemStack(huiCircuit, 4, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Ledox, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.RadoxPolymer, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 4) },
            new FluidStack[] { Materials.Infinity.getMolten(288), Materials.SuperCoolant.getFluid(4000),
                Materials.UUMatter.getFluid(32000), MaterialPool.Polyetheretherketone.getMolten(2304) },
            GTNLItemList.HyperCore.get(1),
            120,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Coolant_Duct_Casing.get(1),
            800000,
            10000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Americium, 4),
                ItemList.Electric_Pump_UV.get(8),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.NetherStar, 8), ItemList.Emitter_ZPM.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Vinteum, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Ledox, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CallistoIce, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.EnrichedHolmium, 32),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Thulium, 2), },
            new FluidStack[] { Materials.SuperCoolant.getFluid(8000), Materials.UUMatter.getFluid(32000),
                MaterialPool.Polyetheretherketone.getMolten(2304) },
            GTNLItemList.Laser_Cooling_Casing.get(4),
            100,
            (int) TierEU.RECIPE_UV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 55))
            .metadata(SCANNING, new Scanning(4 * HOURS, TierEU.RECIPE_HV))
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Conveyor_Module_IV.get(1),
                ItemList.Emitter_IV.get(1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 55),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 30),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockChest", 1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(576), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.OredictInputBusME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(10 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, new ItemStack(GregTechAPI.sBlockMachines, 1, MetaTileEntityIDs.ManualTrafo.ID))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                CustomItemList.Machine_Multi_Transformer.get(1),
                new ItemStack(GregTechAPI.sBlockMachines, 1, MetaTileEntityIDs.ManualTrafo.ID),
                new ItemStack(ItemRegistry.BW_BLOCKS[2], 8, 1),
                ItemList.Circuit_Chip_NanoCPU.get(16),
                CustomItemList.LASERpipe.get(8),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2L },
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.NetherStar, 2))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(576), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.EnergyTransferNode.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_Energy_UXV.get(1),
            768000,
            512,
            51200000,
            1,
            new Object[] { ItemList.Hull_MAXV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUMV, 4),
                ItemList.Circuit_Chip_QPIC.get(32), new Object[] { OrePrefixes.circuit.get(Materials.MAX), 2L },
                ItemList.UHV_Coil.get(64), ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Electric_Pump_MAX.get(1) },
            new FluidStack[] { Materials.SuperCoolant.getFluid(128000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(23040), Materials.UUMatter.getFluid(128000) },
            GTNLItemList.EnergyHatchMAX.get(1),
            1000,
            (int) TierEU.RECIPE_MAX);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_Dynamo_UXV.get(1),
            768000,
            512,
            51200000,
            1,
            new Object[] { ItemList.Hull_MAXV.get(1),
                GTOreDictUnificator.get(OrePrefixes.spring, Materials.SuperconductorUMVBase, 32),
                ItemList.Circuit_Chip_QPIC.get(32), new Object[] { OrePrefixes.circuit.get(Materials.MAX), 2L },
                ItemList.UHV_Coil.get(64), ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Electric_Pump_MAX.get(1) },
            new FluidStack[] { Materials.SuperCoolant.getFluid(128000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(23040), Materials.UUMatter.getFluid(128000) },
            GTNLItemList.DynamoHatchMAX.get(1),
            1000,
            (int) TierEU.RECIPE_MAX);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.DualInputHatchEV.get(1))
            .metadata(SCANNING, new Scanning(10 * MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                GTNLItemList.SuperInputBusME.get(1),
                GTNLItemList.SuperInputHatchME.get(1),
                ItemList.Super_Chest_EV.get(1),
                ItemList.Super_Tank_EV.get(1),
                ItemList.Tool_DataOrb.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorLuV, 16),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 2, 57),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_part", 2, 4),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 27),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockInterface", 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152), Materials.Lubricant.getFluid(8000))
            .itemOutputs(GTNLItemList.SuperDualInputHatchME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(15 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.SuperDualInputHatchME.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                GTNLItemList.AdvancedSuperInputBusME.get(1),
                GTNLItemList.AdvancedSuperInputHatchME.get(1),
                ItemList.Quantum_Chest_LV.get(1),
                ItemList.Quantum_Tank_LV.get(1),
                ItemList.Tool_DataOrb.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 16),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 2, 59),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_part", 2, 6),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 2, 56),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_interface", 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(2304), Materials.Lubricant.getFluid(16000))
            .itemOutputs(GTNLItemList.AdvancedSuperDualInputHatchME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(15 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Automation_TypeFilter_IV.get(1))
            .metadata(SCANNING, new Scanning(15 * MINUTES, TierEU.RECIPE_EV))
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Conveyor_Module_IV.get(1),
                ItemList.Emitter_IV.get(1),
                ItemList.Automation_TypeFilter_IV.get(1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 30),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockChest", 1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(720), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.TypeFilteredInputBusME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(15 * SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.SuperDataAccessHatch.get(1),
            3072000,
            2048,
            102400000,
            1,
            new Object[] { CustomItemList.Machine_Multi_Research.get(64), CustomItemList.Machine_Multi_DataBank.get(64),
                CustomItemList.dataInAss_Wireless_Hatch.get(64), CustomItemList.dataOutAss_Wireless_Hatch.get(64),
                ItemList.SpaceElevatorModuleAssemblerT2.get(16),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockSingularityCraftingStorage", 8),
                CustomItemList.DATApipe.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, MaterialsUEVplus.SpaceTime, 64),
                ItemList.Tool_DataOrb.get(64), ItemList.Tool_DataStick.get(64), ItemList.Field_Generator_UIV.get(16),
                ItemList.ZPM4.get(16), new Object[] { OrePrefixes.circuit.get(Materials.UMV), 32L },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 32),
                GGMaterial.shirabon.get(OrePrefixes.plateSuperdense, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.ProtoHalkonite, 32) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(512000),
                MaterialsUEVplus.TranscendentMetal.getMolten(368640),
                MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(640000),
                GGMaterial.metastableOganesson.getMolten(368640) },
            GTNLItemList.DebugDataAccessHatch.get(1),
            20000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeCutter.get(1),
            32768000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeCutter.get(64),
                GregtechItemList.Industrial_CuttingFactoryController.get(64), ItemList.Neutronium_Active_Casing.get(64),
                ItemList.Casing_ContainmentField.get(12), ItemList.Electric_Motor_UHV.get(32),
                ItemList.Electric_Piston_UHV.get(32), ItemList.Conveyor_Module_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 12),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
            new Object[] { GTNLItemList.LargeMixer.get(64), GregtechItemList.Industrial_Mixer.get(64),
                new ItemStack(TTCasingsContainer.sBlockCasingsTT, 32, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16), ItemList.Electric_Motor_UV.get(48),
                ItemList.Field_Generator_UV.get(12), ItemList.Electric_Pump_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Neutronium, 12), new ItemStack(huiCircuit, 32, 3),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.Longasssuperconductornameforuvwire, 12),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 4), },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(32000),
                MaterialsAlloy.STABALLOY.getFluidStack(128000), MaterialPool.Polyetheretherketone.getMolten(32000),
                Materials.Lubricant.getFluid(128000) },
            GTNLItemList.MegaMixer.get(1),
            1000,
            (int) TierEU.RECIPE_UV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeMaterialPress.get(1),
            1200000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeMaterialPress.get(64), new ItemStack(LanthItemList.ELECTRODE_CASING, 64),
                new ItemStack(TTCasingsContainer.sBlockCasingsTT, 32, 0),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 64),
                new ItemStack(GregTechAPI.sBlockCasings9, 8, 14), ItemList.Electric_Motor_UEV.get(32),
                ItemList.Electric_Piston_UEV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UIV), 8 },
                new ItemStack(huiCircuit, 16, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 8),
                GregtechItemList.Laser_Lens_Special.get(2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.RadoxPolymer.getMolten(12000), MaterialPool.Polyetheretherketone.getMolten(32000),
                new FluidStack(FluidRegistry.getFluid("plasma.celestialtungsten"), 64000) },
            GTNLItemList.AetronPressor.get(1),
            600,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeMacerationTower.get(1),
            1200000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeMacerationTower.get(64), GregtechItemList.Maceration_Upgrade_Chip.get(64),
                new ItemStack(GregTechAPI.sBlockCasings8, 64, 10), ItemList.Electric_Motor_UHV.get(64),
                new ItemStack(huiCircuit, 16, 4), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GregtechItemList.Laser_Lens_Special.get(4), GTNLItemList.EnhancementCore.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Neutronium.getMolten(147456), MaterialPool.Polyetheretherketone.getMolten(32000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(18432) },
            GTNLItemList.NanoPhagocytosisPlant.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeIndustrialLathe.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeIndustrialLathe.get(64), GTNLItemList.Laser_Cooling_Casing.get(16),
                ItemList.Neutronium_Casing.get(64), ItemList.Electric_Motor_UHV.get(32),
                ItemList.Electric_Piston_UHV.get(32), ItemList.Field_Generator_UHV.get(8), ItemList.Emitter_UHV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16 }, new ItemStack(huiCircuit, 16, 4),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16),
                GregtechItemList.Laser_Lens_Special.get(2), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                MaterialPool.Polyetheretherketone.getMolten(32000), Materials.Lubricant.getFluid(256000),
                Materials.SuperCoolant.getFluid(256000) },
            GTNLItemList.HighEnergyLaserLathe.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeBender.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeBender.get(64), ItemList.Neutronium_Casing.get(64),
                new ItemStack(TTCasingsContainer.sBlockCasingsTT, 32, 4), ItemList.Electric_Motor_UHV.get(32),
                ItemList.Electric_Piston_UHV.get(32), ItemList.Field_Generator_UHV.get(8),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                new ItemStack(huiCircuit, 16, 4), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(64000), MaterialPool.Polyetheretherketone.getMolten(32000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(66816) },
            GTNLItemList.HeavyRolling.get(1),
            1000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeForming.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeForming.get(64),
                new ItemStack(LanthItemList.SHIELDED_ACCELERATOR_CASING, 64), ItemList.Casing_Advanced_Iridium.get(64),
                ItemList.Neutronium_Active_Casing.get(64), ItemList.Casing_Dim_Injector.get(64),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Piston_UHV.get(32),
                new ItemStack(huiCircuit, 16, 4), GregtechItemList.Laser_Lens_Special.get(2),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16L),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(64000), MaterialPool.Polyetheretherketone.getMolten(32000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(66816) },
            GTNLItemList.SuperconductingMagneticPresser.get(1),
            2000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeHammer.get(1),
            8000000,
            32000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.ElectricImplosionCompressor.get(32), GTNLItemList.LargeHammer.get(64),
                ItemList.Casing_Dim_Bridge.get(4), ItemList.Casing_Dim_Bridge.get(4),
                ItemList.Casing_Advanced_Iridium.get(16), ItemList.Electric_Motor_UEV.get(16),
                ItemList.Electric_Piston_UEV.get(16), ItemList.Field_Generator_UEV.get(4),
                new ItemStack(huiCircuit, 16, 4), new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32 },
                GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.Duranium, 16L), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.SuperCoolant.getFluid(12800), Materials.Lubricant.getFluid(64000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(66816) },
            GTNLItemList.FieldForgePress.get(1),
            1100,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeElectrolyzer.get(1),
            1000000,
            22000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.LargeElectrolyzer.get(64), GTNLItemList.LargeElectromagnet.get(64),
                GTNLItemList.MolybdenumDisilicideCoil.get(64), new ItemStack(TTCasingsContainer.sBlockCasingsTT, 32, 0),
                new ItemStack(LanthItemList.ELECTRODE_CASING, 32), ItemRefer.Speeding_Pipe.get(64),
                ItemList.Robot_Arm_UHV.get(16), ItemList.Emitter_UHV.get(16), ItemList.Sensor_UHV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 }, GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 16), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.SuperCoolant.getFluid(128000), Materials.Longasssuperconductornameforuhvwire.getMolten(27648),
                new FluidStack(MaterialsElements.getInstance().XENON.getPlasma(), 9216) },
            GTNLItemList.SuperconductingElectromagnetism.get(1),
            1200,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeCentrifuge.get(1),
            4000000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GregtechItemList.Industrial_Centrifuge.get(64), GTNLItemList.LargeCentrifuge.get(64),
                ItemList.Casing_Dim_Injector.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.EnrichedHolmium, 32),
                new ItemStack(TTCasingsContainer.sBlockCasingsTT, 32, 6), ItemList.Electric_Motor_UHV.get(16),
                ItemList.Field_Generator_UV.get(16), GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Infinity, 16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16 }, new ItemStack(huiCircuit, 16, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 4),
                GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Osmiridium, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.SuperCoolant.getFluid(128000), Materials.Longasssuperconductornameforuhvwire.getMolten(27648),
                Materials.Lubricant.getFluid(20480000) },
            GTNLItemList.VortexMatterCentrifuge.get(1),
            1000,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeEngravingLaser.get(1),
            4000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.lens, Materials.Dilithium, 16),
                GTNLItemList.LargeEngravingLaser.get(64), ItemList.PrecisionLaserEngraverUHV.get(32),
                new ItemStack(TTCasingsContainer.sBlockCasingsTT, 64, 4), ItemRefer.Compact_Fusion_Coil_T2.get(32),
                new ItemStack(LanthItemList.ELECTRODE_CASING, 64), new ItemStack(GregTechAPI.sBlockMachines, 32, 15164),
                ItemList.Emitter_UHV.get(16), ItemList.Electric_Pump_UHV.get(16), ItemList.Electric_Piston_UHV.get(16),
                ItemList.Field_Generator_UHV.get(8),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 },
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), Materials.Longasssuperconductornameforuhvwire.getMolten(27648),
                MaterialsUEVplus.ExcitedDTPC.getFluid(1000) },
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
                new ItemStack(GregTechAPI.sBlockCasings10, 32, 6),
                GTModHandler.getModItem(KekzTech.ID, "kekztech_lapotronicenergyunit_block", 64),
                ItemRefer.Field_Restriction_Coil_T2.get(4), ItemList.Robot_Arm_UV.get(16),
                ItemList.Electric_Motor_UV.get(16), ItemList.Conveyor_Module_UV.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8 }, GregtechItemList.Laser_Lens_Special.get(1),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), new FluidStack(FluidRegistry.getFluid("oganesson"), 92160),
                new FluidStack(FluidRegistry.getFluid("plasma.celestialtungsten"), 64000) },
            GTNLItemList.MagneticEnergyReactionFurnace.get(1),
            2000,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.PreciseAssembler.get(1),
            8000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemRefer.Precise_Assembler.get(64), GTNLItemList.PreciseAssembler.get(32),
                GTNLItemList.PreciseAssembler.get(32), ItemList.Casing_Dim_Injector.get(48),
                ItemRefer.Compassline_Casing_UEV.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(8),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16 }, ItemList.Conveyor_Module_UEV.get(32),
                ItemList.Robot_Arm_UEV.get(32), ItemList.Field_Generator_UEV.get(8),
                GregtechItemList.Laser_Lens_Special.get(4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), Materials.Radon.getPlasma(9216),
                Materials.Lubricant.getFluid(64000) },
            GTNLItemList.NanoAssemblerMarkL.get(1),
            2400,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeCircuitAssembler.get(1),
            8000000,
            30000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.LargeCircuitAssembler.get(64), GTUtility.copyAmount(64, cal),
                ItemList.Casing_Assembler.get(64), ItemList.SpaceElevatorBaseCasing.get(32),
                ItemRefer.Compassline_Casing_ZPM.get(16), ItemList.Robot_Arm_UHV.get(48),
                ItemList.Conveyor_Module_UHV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 8),
                GregtechItemList.Laser_Lens_Special.get(2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 64) },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(64000),
                Materials.SuperCoolant.getFluid(128000), MaterialsAlloy.TITANSTEEL.getFluidStack(9216),
                Materials.Lubricant.getFluid(64000) },
            GTNLItemList.NanitesCircuitAssemblyFactory.get(1),
            2400,
            (int) TierEU.RECIPE_UHV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.VacuumFreezer.get(1))
            .metadata(SCANNING, new Scanning(60 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTNLItemList.VacuumFreezer.get(32),
                GTNLItemList.VacuumFreezer.get(32),
                GTNLItemList.VacuumFreezer.get(32),
                GTNLItemList.VacuumFreezer.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Aluminium, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Tritanium, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Americium, 16L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.BlackPlutonium, 16L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 16L),
                ItemList.Field_Generator_ZPM.get(4L),
                ItemList.Energy_Module.get(1L),
                ItemList.Energy_Module.get(1L))
            .fluidInputs(
                Materials.Grade4PurifiedWater.getFluid(64000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(14400),
                MaterialsAlloy.LAFIUM.getFluidStack(20736),
                Materials.SolderingAlloy.getMolten(36864))
            .itemOutputs(GTNLItemList.CompoundExtremeCoolingUnit.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(50 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.WirelessEnergyHatchUIV65536A.get(1),
            151248000,
            150000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.UIVParallelControllerCore.get(1),
                CustomItemList.Machine_Multi_Transformer.get(64), ItemRefer.Compact_Fusion_Coil_T4.get(64),
                GTNLItemList.WirelessEnergyHatchUMV4194304A.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 24 },
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 64 }, ItemList.Field_Generator_UIV.get(48),
                ItemList.Thermal_Superconductor.get(24),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 64, 0),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 32),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, MaterialsUEVplus.SpaceTime, 32),
                GGMaterial.metastableOganesson.get(OrePrefixes.plateDense, 32),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.SixPhasedCopper, 1),
                GregtechItemList.Laser_Lens_Special.get(16) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(10000),
                MaterialsUEVplus.Creon.getMolten(5000), MaterialsUEVplus.Mellion.getMolten(9072),
                MaterialsUEVplus.SixPhasedCopper.getMolten(20736) },
            GTNLItemList.WirelessUpgradeChip.get(1),
            2000,
            (int) TierEU.RECIPE_UMV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, CustomItemList.DATApipe.get(1))
            .metadata(SCANNING, new Scanning(10 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                CustomItemList.Machine_Multi_Computer.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUV, 4),
                CustomItemList.DATApipe.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1))
            .fluidInputs(
                Materials.Grade3PurifiedWater.getFluid(32000),
                Materials.Osmium.getMolten(1296),
                Materials.UUMatter.getFluid(2560),
                MaterialPool.Polyetheretherketone.getMolten(1296))
            .itemOutputs(GTNLItemList.HighPerformanceComputationArray.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(50 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeDistillery.get(1),
            1800000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeDistillery.get(64), GTUtility.copyAmount(64, megaMachines[2]),
                ItemList.BlockNaquadriaReinforcedWaterPlantCasing.get(64), ItemList.Heating_Duct_Casing.get(64),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Pump_UHV.get(32),
                ItemList.Field_Generator_UHV.get(16), new ItemStack(huiCircuit, 32, 4),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 1),
                GregtechItemList.Laser_Lens_Special.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 2), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.CosmicNeutronium.getMolten(9216), MaterialPool.Polyetheretherketone.getMolten(32000),
                Materials.SuperCoolant.getFluid(256000) },
            GTNLItemList.CompoundDistillationFractionator.get(1),
            2000,
            (int) TierEU.RECIPE_UEV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, GTNLItemList.AssemblerMatrixCrafterCore.get(1))
            .metadata(SCANNING, new Scanning(5 * MINUTES, TierEU.RECIPE_UEV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Infinity, 32),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockSingularityCraftingStorage", 1),
                GTNLItemList.AssemblerMatrixWall.get(32),
                GTNLItemList.AssemblerMatrixCrafterCore.get(32),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockAdvancedCraftingUnit", 32, 3),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 16, 67),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 32, 56),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 64, 30),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 64, 27),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 64, 47),
                ItemList.Tesseract.get(16),
                ItemList.Field_Generator_UEV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 4L },
                ItemList.SuperconductorComposite.get(16),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUEV, 16))
            .fluidInputs(
                Materials.UUMatter.getFluid(256000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(18432),
                MaterialsUEVplus.SpaceTime.getMolten(1296))
            .itemOutputs(GTNLItemList.AssemblerMatrixSingularityCrafterCore.get(1))
            .eut(TierEU.RECIPE_UIV)
            .duration(150 * SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            CustomItemList.Machine_Multi_EyeOfHarmony.get(1),
            1919810,
            65536,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { ItemRefer.ProtomatterActivationCoil.get(16), ItemList.Extreme_Density_Casing.get(16),
                CustomItemList.Godforge_StellarEnergySiphonCasing.get(16), ItemList.MagneticAnchorCasing.get(16),
                ItemRefer.AntimatterForge.get(2), ItemList.Machine_Multi_BlackHoleCompressor.get(2),
                CustomItemList.Machine_Multi_ForgeOfGods.get(2), CustomItemList.Machine_Multi_EyeOfHarmony.get(2),
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 64),
                GTUtility.copyAmountUnsafe(64, Particle.getBaseParticle(Particle.GRAVITON)),
                ItemList.Black_Hole_Opener.get(32), ItemList.EnergisedTesseract.get(32),
                ItemList.Field_Generator_UMV.get(16), ItemList.Transdimensional_Alignment_Matrix.get(8),
                CustomItemList.astralArrayFabricator.get(1),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemExtremeStorageCell.Universe", 1) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(256000),
                MaterialsUEVplus.PhononMedium.getFluid(128000), MaterialsUEVplus.TranscendentMetal.getMolten(46080) },
            GTNLItemList.EyeOfHarmonyInjector.get(1),
            1000,
            (int) TierEU.RECIPE_UXV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_storage.singularity", 1),
            512000,
            2048,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockDrive", 64),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockCreativeEnergyController", 32),
                GTNLItemList.QuantumComputerCore.get(16), ItemRefer.YOTTank_Cell_T8.get(16),
                ItemList.Quantum_Tank_IV.get(64), ItemList.Quantum_Chest_IV.get(64),
                ItemList.Field_Generator_UEV.get(4), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 16),
                GregtechItemList.Laser_Lens_Special.get(8), ItemList.Black_Hole_Opener.get(8), ItemList.ZPM3.get(2),
                GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemExtremeStorageCell.Singularity", 1),
                GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_storage.singularity", 1) },
            new FluidStack[] { MaterialPool.QuantumInfusion.getFluidOrGas(256000),
                MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(64000), Materials.Infinity.getMolten(18432),
                MaterialsUEVplus.TranscendentMetal.getMolten(9216) },
            GTNLItemList.SingularityDataHub.get(1),
            8000,
            (int) TierEU.RECIPE_UIV);
    }
}
