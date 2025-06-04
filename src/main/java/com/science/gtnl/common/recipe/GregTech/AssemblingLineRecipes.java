package com.science.gtnl.common.recipe.GregTech;

import static bartworks.common.loaders.ItemRegistry.bw_realglas;
import static gregtech.api.enums.MetaTileEntityIDs.BioLab_LuV;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.*;
import static gregtech.api.util.GTRecipeConstants.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.block.BlockList;
import com.reavaritia.ReAvaItemList;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.WerkstoffLoader;
import ggfab.GGItemList;
import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;

public class AssemblingLineRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {

        ItemStack CrystalStuddedCosmicNeutroniumWand = GTModHandler.getModItem(Thaumcraft.ID, "WandCasting", 1, 9000);
        NBTTagCompound CrystalStuddedCosmicNeutroniumWandType = CrystalStuddedCosmicNeutroniumWand.getTagCompound();
        if (CrystalStuddedCosmicNeutroniumWandType != null) {
            CrystalStuddedCosmicNeutroniumWandType.setString("cap", "matrix");
            CrystalStuddedCosmicNeutroniumWandType.setString("rod", "infinity");
            CrystalStuddedCosmicNeutroniumWandType.setInteger("aer", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("aqua", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("ignis", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("ordo", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("perditio", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("terra", 999999900);
        } else {
            CrystalStuddedCosmicNeutroniumWandType = new NBTTagCompound();
            CrystalStuddedCosmicNeutroniumWandType.setString("cap", "matrix");
            CrystalStuddedCosmicNeutroniumWandType.setString("rod", "infinity");
            CrystalStuddedCosmicNeutroniumWandType.setInteger("aer", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("aqua", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("ignis", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("ordo", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("perditio", 999999900);
            CrystalStuddedCosmicNeutroniumWandType.setInteger("terra", 999999900);
            CrystalStuddedCosmicNeutroniumWand.setTagCompound(CrystalStuddedCosmicNeutroniumWandType);
        }

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Circuit_Chip_Biocell.get(1),
            10000000,
            2500,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { ItemList.Circuit_Board_Bio_Ultra.get(1), GTNLItemList.BiowareSMDCapacitor.get(8),
                GTNLItemList.BiowareSMDDiode.get(8), GTNLItemList.BiowareSMDResistor.get(8),
                GTNLItemList.BiowareSMDTransistor.get(8), GTNLItemList.BiowareSMDInductor.get(8),
                MaterialPool.Polyetheretherketone.get(OrePrefixes.foil, 2), ItemList.Circuit_Chip_Biocell.get(8),
                ItemList.Circuit_Parts_Chip_Bioware.get(8),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Naquadah, 16),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.NiobiumTitanium, 4) },
            new FluidStack[] { Materials.BioMediumSterilized.getFluid(1000), Materials.Plastic.getMolten(1296),
                Materials.PolyvinylChloride.getMolten(864), Materials.SolderingAlloy.getMolten(1296) },
            ItemList.Circuit_Chip_BioCPU.get(8),
            16 * SECONDS,
            (int) TierEU.RECIPE_UV);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
                CrystalStuddedCosmicNeutroniumWand,
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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .metadata(RESEARCH_ITEM, ItemList.Machine_Multi_BlastFurnace.get(1))
            .metadata(SCANNING, new Scanning(60 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTUtility.copyAmount(4, ItemRegistry.megaMachines[0]),
                GTUtility.copyAmount(4, ItemRegistry.megaMachines[0]),
                GTUtility.copyAmount(4, ItemRegistry.megaMachines[0]),
                GTUtility.copyAmount(4, ItemRegistry.megaMachines[0]),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 8L },
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
            .duration(30 * SECONDS)
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Circuit_Ultimatecrystalcomputer.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSE, 2L),
                ItemList.Circuit_Ultimatecrystalcomputer.get(2),
                ItemList.Circuit_Chip_Ram.get(32),
                ItemList.Circuit_Chip_HPIC.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.NiobiumTitanium, 8L),
                ItemList.Circuit_Parts_InductorASMD.get(8),
                ItemList.Circuit_Parts_CapacitorASMD.get(16),
                ItemList.Circuit_Parts_DiodeASMD.get(8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1440))
            .itemOutputs(ItemList.Circuit_Crystalmainframe.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(40 * SECONDS)
            .addTo(AssemblyLine);

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
                GregtechItemList.Controller_IsaMill.get(4),
                MaterialsAlloy.ZERON_100.getPlateDouble(16),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4L },
                new Object[] { OrePrefixes.circuit.get(Materials.IV), 8L },
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorLuV, 16L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 16L),
                ItemList.Component_Grinder_Tungsten.get(32),
                ItemList.Conveyor_Module_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16))
            .fluidInputs(Materials.Grade2PurifiedWater.getFluid(64000), Materials.Europium.getMolten(2304))
            .itemOutputs(GTNLItemList.IsaMill.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(40 * SECONDS)
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Circuit_Wetwarecomputer.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Circuit_Board_Wetware_Extreme.get(1),
                ItemList.Circuit_Wetwarecomputer.get(2),
                ItemList.Circuit_Parts_DiodeASMD.get(8),
                ItemList.Circuit_Chip_NOR.get(16),
                ItemList.Circuit_Chip_Ram.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.YttriumBariumCuprate, 24),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Europium, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
            .itemOutputs(ItemList.Circuit_Wetwaresupercomputer.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(40 * SECONDS)
            .addTo(AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Circuit_Wetwaresupercomputer.get(1L),
            384000,
            96,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 2),
                ItemList.Circuit_Wetwaresupercomputer.get(2L), ItemList.Circuit_Parts_DiodeASMD.get(32),
                ItemList.Circuit_Parts_CapacitorASMD.get(32), ItemList.Circuit_Parts_TransistorASMD.get(32),
                ItemList.Circuit_Parts_ResistorASMD.get(32), ItemList.Circuit_Parts_InductorASMD.get(32),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Polybenzimidazole, 64),
                ItemList.Circuit_Chip_Ram.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUV, 16),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Europium, 8), },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(2880), Materials.Polybenzimidazole.getMolten(1152) },
            ItemList.Circuit_Wetwaremainframe.get(1L),
            2000,
            300000);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

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
            GTNLItemList.AdvancedCircuitAssemblyLine.get(1),
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
                getModItem(GalacticraftAmunRa.ID, "item.baseItem", 64, 15),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Infinity, 64),
                ItemList.SpaceElevatorBaseCasing.get(64) },
            new FluidStack[] { MaterialPool.SuperMutatedLivingSolder.getFluidOrGas(4000),
                MaterialsUEVplus.MoltenProtoHalkoniteBase.getFluid(8000),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(16000), Materials.Infinity.getMolten(4608) },
            GTNLItemList.SuperSpaceElevator.get(1),
            9000,
            (int) TierEU.RECIPE_UEV);

        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47))
            .metadata(SCANNING, new Scanning(30 * MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                CustomItemList.Machine_Multi_Transformer.get(1),
                getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 4, 47),
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
            .addTo(AssemblyLine);

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
            .addTo(AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeAssembler.get(1),
            5000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeAssembler.get(64), GTNLItemList.PreciseAssembler.get(64),
                ItemList.AssemblingMachineUEV.get(64), GregtechItemList.NeutronPulseManipulator.get(32),
                new ItemStack(Loaders.componentAssemblylineCasing, 16, 9),
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
    }
}
