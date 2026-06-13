package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.block.BlockList;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.Optional;
import ggfab.GGItemList;
import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
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
import gtPlusPlus.core.material.Particle;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;
import tectech.recipe.TTRecipeAdder;
import tectech.thing.CustomItemList;

@SuppressWarnings("deprecation")
public class AssemblingLineRecipes implements IRecipePool {

    public IRecipeMap AL = GTRecipeConstants.AssemblyLine;

    @Override
    public void loadRecipes() {
        var aeItems = AEApi.instance()
            .definitions()
            .items();
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();
        var aeParts = AEApi.instance()
            .definitions()
            .parts();
        var aeBlocks = AEApi.instance()
            .definitions()
            .blocks();

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            kubatech.api.enums.ItemList.ExtremeIndustrialGreenhouse.get(1),
            256000,
            1024,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hull_UV.get(16), kubatech.api.enums.ItemList.ExtremeIndustrialGreenhouse.get(64),
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockFarmStation", 64),
                GTModHandler.getModItem(Mods.RandomThings.ID, "fertilizedDirt", 64),
                ItemList.Field_Generator_UV.get(16), ItemList.Emitter_UV.get(16), ItemList.Sensor_UV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 8L },
                GTModHandler.getModItem(Mods.Botania.ID, "overgrowthSeed", 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 16L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 64L),
                GregtechItemList.Laser_Lens_Special.get(1), ItemList.Compressor_Casing.get(16),
                ItemList.Compressor_Pipe_Casing.get(16) },
            new FluidStack[] { Materials.BioMediumSterilized.getFluid(320000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(128000), Materials.Lubricant.getFluid(256000),
                Materials.Naquadria.getMolten(36864) },
            GTNLItemList.EdenGarden.get(1),
            30 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTModHandler.getModItem(Mods.Botania.ID, "lexicon", 1, 0))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(1 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Hull_ZPM.get(8),
                GTModHandler.getModItem(Mods.Botania.ID, "pylon", 4, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "pool", 16, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "spreader", 8, 3),
                CustomItemList.LASERpipe.get(64),
                GTModHandler.getModItem(Mods.Botania.ID, "alfheimPortal", 64, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "runeAltar", 64, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "corporeaSpark", 64, 0),
                ItemList.Sensor_ZPM.get(16),
                ItemList.Field_Generator_ZPM.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L },
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4L },
                ItemList.RadiantNaquadahAlloyCasing.get(16),
                ItemList.Casing_Fusion_Coil.get(16),
                GTModHandler.getModItem(Mods.Botania.ID, "storage", 32, 0))
            .fluidInputs(
                MaterialsBotania.ElvenElementium.getMolten(144 * 64),
                MaterialsBotania.Terrasteel.getMolten(144 * 32),
                MaterialsAlloy.INDALLOY_140.getFluidStack(256000))
            .itemOutputs(GTNLItemList.TeleportationArrayToAlfheim.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.MixerUIV.get(1),
            102400000,
            25565,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { GregtechItemList.Mega_AlloyBlastSmelter.get(64),
                MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(64),
                kubatech.api.enums.ItemList.DEFCCasingBase.get(32), kubatech.api.enums.ItemList.DEFCCasingT3.get(32),
                ItemList.Casing_Dim_Injector.get(32),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 4, 0),
                ItemList.Electric_Motor_UIV.get(64), ItemList.Electric_Pump_UIV.get(64),
                ItemList.Field_Generator_UIV.get(48), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 48L },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32L },
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 16),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.TranscendentMetal, 16) },
            new FluidStack[] { MaterialsUEVplus.SpaceTime.getMolten(73728),
                MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(73728),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(294912),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(540000) },
            GTNLItemList.SmeltingMixingFurnace.get(1),
            120 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UMV);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(20 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_UHV))
            .itemInputs(
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockStoneDevice", 64, 2),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockStoneDevice", 64, 2),
                ItemUtils.getItemStack(
                    Mods.Thaumcraft.ID,
                    "WandCasting",
                    1,
                    9000,
                    "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                    null),
                GTModHandler.getModItem(Mods.Avaritia.ID, "Akashic_Record", 1),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16L },
                ItemList.Robot_Arm_UEV.get(32),
                ItemList.Field_Generator_UEV.get(16),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(32),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 8),
                ItemList.EnergisedTesseract.get(8),
                GTModHandler.getModItem(Mods.WitchingGadgets.ID, "item.WG_Material", 1, 7),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(64)
                    .orNull())
            .fluidInputs(
                MaterialsUEVplus.ExcitedDTEC.getFluid(64000),
                Materials.StableBaryonicMatter.getFluid(64000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000))
            .itemOutputs(GTNLItemList.IndustrialArcaneAssembler.get(1))
            .eut(TierEU.RECIPE_UIV)
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ReAvaItemList.NeutronCollector.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(114 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
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
                GTModHandler.getModItem(Mods.Avaritia.ID, "Resource", 16, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUHV, 16L))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(2304), Materials.Grade7PurifiedWater.getFluid(16000))
            .itemOutputs(ReAvaItemList.DenseNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(60 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ReAvaItemList.DenseNeutronCollector.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(4 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
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
                GTModHandler.getModItem(Mods.Avaritia.ID, "Resource", 32, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16L))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(4608), Materials.Grade7PurifiedWater.getFluid(32000))
            .itemOutputs(ReAvaItemList.DenserNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(120 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ReAvaItemList.DenserNeutronCollector.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(120 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_UV))
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
                GTModHandler.getModItem(Mods.Avaritia.ID, "Resource", 64, 5),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 32L),
                GregtechItemList.Laser_Lens_Special.get(1))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(9216), Materials.Grade8PurifiedWater.getFluid(64000))
            .itemOutputs(ReAvaItemList.DensestNeutronCollector.get(1))
            .eut(TierEU.RECIPE_UEV)
            .duration(60 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.ElectricBlastFurnace.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(60 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(50 * GTRecipeBuilder.SECONDS)
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
            60 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1),
                aeMaterials.cell16384kPart()
                    .maybeStack(2)
                    .orNull(),
                ItemList.Hatch_Input_Bus_ME_Advanced.get(2),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(16)
                    .orNull(),
                aeBlocks.controller()
                    .maybeStack(1)
                    .orNull(),
                aeBlocks.energyCellDense()
                    .maybeStack(1)
                    .orNull(),
                aeParts.iface()
                    .maybeStack(5)
                    .orNull(),
                aeParts.interfaceTerminal()
                    .maybeStack(1)
                    .orNull())
            .fluidInputs(Materials.SolderingAlloy.getMolten(4608), MaterialsAlloy.INDALLOY_140.getFluidStack(2304))
            .itemOutputs(GTNLItemList.SuperCraftingInputBusME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_CraftingInput_Bus_ME.get(1),
            1920000,
            4000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hatch_CraftingInput_Bus_ME.get(4),
                GTModHandler.getModItem(Mods.AvaritiaAddons.ID, "CompressedChest", 4), aeMaterials.cell16384kPart()
                    .maybeStack(16)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_part", 16, 7),
                ItemList.Hatch_Input_Bus_ME_Advanced.get(4), ItemList.Hatch_Input_ME_Advanced.get(4),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(64)
                    .orNull(),
                aeMaterials.singularity()
                    .maybeStack(16)
                    .orNull() },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(1296),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(576), Materials.Infinity.getMolten(144),
                Materials.Grade5PurifiedWater.getFluid(8000), },
            GTNLItemList.SuperCraftingInputHatchME.get(1),
            60 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.DualInputHatchLuV.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                GTNLItemList.DualInputHatchLuV.get(1),
                ItemList.Emitter_LuV.get(1),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 4L },
                aeBlocks.iface()
                    .maybeStack(3)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_interface", 3),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull(),
                aeMaterials.cardCapacity()
                    .maybeStack(2)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576), Materials.Lubricant.getFluid(500))
            .itemOutputs(ItemList.Hatch_CraftingInput_Bus_ME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Hatch_CraftingInput_Bus_Slave.get(1),
            3840000,
            16000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { ItemList.Hatch_CraftingInput_Bus_Slave.get(1), ItemList.Tool_DataOrb.get(32),
                ItemList.Tool_DataStick.get(32), aeMaterials.wireless()
                    .maybeStack(32)
                    .orNull(),
                ItemList.Emitter_UV.get(4), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 1L },
                aeBlocks.quantumRing()
                    .maybeStack(8)
                    .orNull(),
                aeBlocks.quantumLink()
                    .maybeStack(1)
                    .orNull() },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(16000),
                Materials.Infinity.getMolten(36864), Materials.Grade6PurifiedWater.getFluid(16000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(25600) },
            GTNLItemList.SuperCraftingInputProxy.get(1),
            30 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemList.Hatch_CraftingInput_Bus_ME.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_LuV.get(1),
                ItemList.Sensor_LuV.get(2),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 1L },
                aeBlocks.quantumLink()
                    .maybeStack(1)
                    .orNull(),
                aeBlocks.quantumRing()
                    .maybeStack(2)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Europium, 32L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576), Materials.Lubricant.getFluid(500))
            .itemOutputs(ItemList.Hatch_CraftingInput_Bus_Slave.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(30 * GTRecipeBuilder.SECONDS)
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
                Materials.BatteryAlloy.getMolten(5760), GTNLMaterials.MolybdenumDisilicide.getMolten(1296) },
            CustomItemList.eM_Containment_Field.get(1),
            25 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.IVParallelControllerCore.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_LuV.get(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSS, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.HSSS, 32L),
                ItemList.Robot_Arm_LuV.get(12),
                ItemList.Emitter_LuV.get(8),
                ItemList.Sensor_LuV.get(8),
                ItemList.Field_Generator_LuV.get(2),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 12L },
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.VanadiumGallium, 32L))
            .fluidInputs(
                Materials.Polytetrafluoroethylene.getMolten(576),
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296))
            .itemOutputs(GTNLItemList.LuVParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.LuVParallelControllerCore.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.Hull_ZPM.get(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 32L),
                MaterialsAlloy.ZERON_100.getPlateDense(8),
                MaterialsAlloy.PIKYONIUM.getScrew(64),
                MaterialsAlloy.PIKYONIUM.getScrew(64),
                ItemList.Electric_Motor_ZPM.get(24L),
                ItemList.Robot_Arm_ZPM.get(12L),
                ItemList.Emitter_ZPM.get(12L),
                ItemList.Sensor_ZPM.get(12L),
                ItemList.Field_Generator_ZPM.get(4L),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Naquadah, 32L))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(2592),
                Materials.Europium.getMolten(2592),
                Materials.Trinium.getMolten(1296),
                Materials.Polybenzimidazole.getMolten(4608))
            .itemOutputs(GTNLItemList.ZPMParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.ZPMParallelControllerCore.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Hull_UV.get(32),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Osmiridium, 16L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Neutronium, 32L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Neutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Neutronium, 64L),
                ItemList.Electric_Motor_UV.get(32L),
                ItemList.Robot_Arm_UV.get(16L),
                ItemList.Emitter_UV.get(16L),
                ItemList.Sensor_UV.get(16L),
                ItemList.Field_Generator_UV.get(8L),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 32L))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(4608),
                Materials.Polybenzimidazole.getMolten(9216),
                Materials.Naquadria.getMolten(2592),
                Materials.Americium.getMolten(1296))
            .itemOutputs(GTNLItemList.UVParallelControllerCore.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UVParallelControllerCore.get(1),
            51200000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.Hull_MAX.get(32),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bedrockium, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 2L),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.CosmicNeutronium, 64L),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 4L), ItemList.Robot_Arm_UHV.get(32L),
                ItemList.Emitter_UHV.get(16L), ItemList.Sensor_UHV.get(16L), ItemList.Field_Generator_UHV.get(8L),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 32L),
                ItemList.Energy_Cluster.get(4), MaterialsElements.STANDALONE.ASTRAL_TITANIUM.getFrameBox(32) },
            new FluidStack[] { Materials.RadoxPolymer.getMolten(16000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(16000), Materials.Naquadria.getMolten(9216) },
            GTNLItemList.UHVParallelControllerCore.get(1),
            20 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UHVParallelControllerCore.get(1),
            204800000,
            204800,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { ItemList.Hull_UEV.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Infinity, 16L),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Infinity, 32L),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Infinity, 64L),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Infinity, 64L),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 1, 0),
                ItemList.Tesseract.get(4L), ItemList.Robot_Arm_UEV.get(32L), ItemList.Emitter_UEV.get(32L),
                ItemList.Sensor_UEV.get(32L), ItemList.Field_Generator_UEV.get(16L),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 32L) },
            new FluidStack[] { Materials.Grade7PurifiedWater.getFluid(64000), Materials.RadoxPolymer.getMolten(32000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(32000),
                WerkstoffLoader.Oganesson.getFluidOrGas(16000) },
            GTNLItemList.UEVParallelControllerCore.get(1),
            20 * GTRecipeBuilder.SECONDS,
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
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Mellion, 8L),
                ItemList.Robot_Arm_UIV.get(64L), ItemList.Emitter_UIV.get(32L), ItemList.Sensor_UIV.get(32L),
                ItemList.Field_Generator_UIV.get(32L), new Object[] { OrePrefixes.circuit.get(Materials.UMV), 32L },
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "awakenedCore", 8, 0), ItemList.Tesseract.get(16L),
                GTNLItemList.EnhancementCore.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 4L),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 16, 0),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64L) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(32000),
                GGMaterial.metastableOganesson.getMolten(36864),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(128000), MaterialsUEVplus.SpaceTime.getMolten(2304) },
            GTNLItemList.UIVParallelControllerCore.get(1),
            20 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.UIVParallelControllerCore.get(1),
            819200000,
            1638400,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { ItemList.Hull_UMV.get(64), MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsKevlar.Kevlar, 32L),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.EnrichedHolmium, 32L),
                GGMaterial.shirabon.get(OrePrefixes.plateDense, 32), ItemRefer.Compassline_Casing_UMV.get(16),
                CustomItemList.dataIn_Wireless_Hatch.get(8), ItemList.ZPM3.get(8), ItemList.Robot_Arm_UMV.get(64L),
                ItemList.Emitter_UMV.get(64L), ItemList.Sensor_UMV.get(64L), ItemList.Field_Generator_UMV.get(64L),
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 64L }, ItemList.EnergisedTesseract.get(32),
                ItemList.Transdimensional_Alignment_Matrix.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUMV, 64L) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(256000),
                GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(64000),
                MaterialsUEVplus.ExcitedDTEC.getFluid(64000), MaterialsUEVplus.SpaceTime.getMolten(9216) },
            GTNLItemList.UMVParallelControllerCore.get(1),
            20 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            Mods.DraconicEvolution.isModLoaded() ? kubatech.api.enums.ItemList.DraconicEvolutionFusionCrafter.get(1)
                : GTNLItemList.BlazeCubeBlock.get(1),
            25600000,
            51200,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] {
                Mods.DraconicEvolution.isModLoaded() ? kubatech.api.enums.ItemList.DraconicEvolutionFusionCrafter.get(1)
                    : GTNLItemList.BlazeCubeBlock.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Draconium, 4L),
                ItemList.Emitter_UHV.get(16), ItemList.Field_Generator_UHV.get(16),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "draconicCore", 32),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16L }, ItemList.ZPM2.get(4),
                ItemList.NuclearStar.get(4), GTModHandler.getModItem(Mods.DraconicEvolution.ID, "dragonHeart", 1),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "chaosShard", 1),
                GregtechItemList.Laser_Lens_Special.get(4) },
            new FluidStack[] { Materials.DraconiumAwakened.getMolten(36864), Materials.Void.getMolten(73728),
                MaterialsAlloy.INDALLOY_140.getFluidStack(32000), },
            GTNLItemList.DraconicFusionCrafting.get(1),
            120 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 4),
            1024000000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GregtechItemList.GTPP_Casing_UHV.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 4 }, ItemList.Field_Generator_UHV.get(8),
                ItemList.Robot_Arm_UHV.get(16), ItemList.Emitter_UHV.get(16),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFMagicLogSpecial", 64, 0),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.IronWood, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Steeleaf, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.FierySteel, 64L),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Knightmetal, 64L),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.magicMapFocus", 64, 0),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.mazeMapFocus", 32, 0),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.lampOfCinders", 1, 0) },
            new FluidStack[] { Materials.FierySteel.getFluid(32000), Materials.SolderingAlloy.getMolten(73728),
                MaterialsAlloy.INDALLOY_140.getFluidStack(36864), },
            GTNLItemList.LibraryOfRuina.get(1),
            300 * GTRecipeBuilder.SECONDS,
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
            300 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GregtechItemList.Controller_IsaMill.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(40 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.Incubator.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
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
                new ItemStack(ItemRegistry.bw_realglas, 32, 5))
            .fluidInputs(Materials.Grade5PurifiedWater.getFluid(32000), Materials.CosmicNeutronium.getMolten(2304))
            .itemOutputs(GTNLItemList.LargeIncubator.get(1))
            .eut(TierEU.RECIPE_UV)
            .duration(30 * GTRecipeBuilder.SECONDS)
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
                GTNLMaterials.Darmstadtium.get(OrePrefixes.stickLong, 8),
                MaterialsAlloy.INCOLOY_MA956.getPlateDouble(4),
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

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GregtechItemList.Controller_Flotation_Cell.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(30 * GTRecipeBuilder.SECONDS)
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

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemRegistry.megaMachines[4])
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(50 * GTRecipeBuilder.SECONDS)
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
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(4000),
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
                GTModHandler.getModItem(Mods.AvaritiaAddons.ID, "InfinityChest", 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 64),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.CosmicNeutronium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 2) },
            new FluidStack[] { Materials.SolderingAlloy.getMolten(23040),
                MaterialsAlloy.INDALLOY_140.getFluidStack(12800), Materials.Polytetrafluoroethylene.getMolten(46080),
                Materials.Grade6PurifiedWater.getFluid(64000) },
            GTNLItemList.GrandAssemblyLine.get(1),
            1200,
            (int) TierEU.RECIPE_UEV);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.lampOfCinders", 1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_UV))
            .itemInputs(
                new ItemStack(Items.book, 64),
                GTNLItemList.NagaBook.get(1),
                GTNLItemList.LichBook.get(1),
                GTNLItemList.MinotaurBook.get(1),
                GTNLItemList.HydraBook.get(1),
                GTNLItemList.KnightPhantomBook.get(1),
                GTNLItemList.UrGhastBook.get(1),
                GTNLItemList.AlphaYetiBook.get(1),
                GTNLItemList.SnowQueenBook.get(1),
                GTNLItemList.GiantBook.get(1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.mazebreakerPick", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 8),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.crumbleHorn", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.charmOfKeeping3", 8),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.charmOfLife2", 32),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFSapling", 64, 5))
            .fluidInputs(
                Materials.FierySteel.getFluid(64000),
                FluidRegistry.getFluidStack("xpjuice", 2560000),
                Materials.AdvancedGlue.getFluid(640000),
                MaterialMisc.ETHYL_CYANOACRYLATE.getFluidStack(320000))
            .itemOutputs(GTNLItemList.TwilightForestBook.get(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(60 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        if (Mods.BloodMagic.isModLoaded()) {
            RecipeBuilder.builder()
                .metadata(GTRecipeConstants.RESEARCH_ITEM, GTModHandler.getModItem(Mods.BloodMagic.ID, "Altar", 1))
                .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
                .itemInputs(
                    GTModHandler.getModItem(Mods.BloodMagic.ID, "masterStone", 32),
                    new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32L },
                    new Object[] { OrePrefixes.circuit.get(Materials.UV), 16L },
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Trinium, 16),
                    GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 64L),
                    ItemList.Field_Generator_ZPM.get(16),
                    GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 5),
                    ItemList.Electric_Pump_ZPM.get(32),
                    ItemList.Emitter_ZPM.get(32),
                    GTModHandler.getModItem(Mods.BloodArsenal.ID, "lp_materializer", 1),
                    GTModHandler.getModItem(Mods.BloodArsenal.ID, "life_infuser", 1),
                    GTModHandler.getModItem(Mods.BloodMagic.ID, "blockWritingTable", 1),
                    GTModHandler.getModItem(Mods.BloodMagic.ID, "activationCrystal", 1, 1),
                    GTModHandler.getModItem(Mods.BloodMagic.ID, "itemRitualDiviner", 1, 2))
                .fluidInputs(
                    Materials.Grade4PurifiedWater.getFluid(64000),
                    Materials.Americium.getMolten(4608),
                    Materials.Neutronium.getMolten(2304),
                    Materials.NaquadahEnriched.getMolten(1152))
                .itemOutputs(GTNLItemList.BloodSoulSacrificialArray.get(1))
                .eut(TierEU.RECIPE_UV)
                .duration(120 * GTRecipeBuilder.SECONDS)
                .addTo(AL);
        }

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.ChemicalPlant.get(1),
            12000,
            16,
            (int) TierEU.RECIPE_ZPM,
            1,
            new Object[] { GTNLItemList.ChemicalPlant.get(16),
                GTUtility.copyAmountUnsafe(16, ItemRegistry.megaMachines[3]),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 32 },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 64L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Naquadah, 16),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 8), ItemList.Field_Generator_ZPM.get(8),
                ItemList.Electric_Pump_ZPM.get(16), ItemList.Emitter_ZPM.get(16),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Europium, 48) },
            new FluidStack[] { Materials.Grade4PurifiedWater.getFluid(16000),
                GTNLMaterials.Polyetheretherketone.getMolten(4608), MaterialsAlloy.INDALLOY_140.getFluidStack(16000) },
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
                GTModHandler.getModItem(Mods.GalacticraftAmunRa.ID, "item.baseItem", 64, 15),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Infinity, 64),
                ItemList.SpaceElevatorBaseCasing.get(64) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(4000),
                MaterialsUEVplus.MoltenProtoHalkoniteBase.getFluid(8000),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(16000), Materials.Infinity.getMolten(4608) },
            GTNLItemList.SuperSpaceElevator.get(1),
            9000,
            (int) TierEU.RECIPE_UEV);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                CustomItemList.Machine_Multi_Transformer.get(1),
                aeMaterials.singularity()
                    .maybeStack(4)
                    .orNull(),
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
            .duration(120 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                new ItemStack(GregTechAPI.sBlockMachines, 1, MetaTileEntityIDs.BioLab_LuV.ID))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                new ItemStack(GregTechAPI.sBlockMachines, 8, MetaTileEntityIDs.BioLab_LuV.ID),
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
                GTNLMaterials.Polyetheretherketone.getMolten(1152),
                MaterialsAlloy.INDALLOY_140.getFluidStack(1152),
                Materials.Lubricant.getFluid(32000))
            .itemOutputs(GTNLItemList.LargeBioLab.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(60 * GTRecipeBuilder.SECONDS)
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
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(16000),
                Materials.SuperCoolant.getFluid(128000), Materials.Tin.getPlasma(36384),
                Materials.Lubricant.getFluid(64000) },
            GTNLItemList.IntegratedAssemblyFacility.get(1),
            2400,
            (int) TierEU.RECIPE_UIV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemList.AlloySmelterUIV.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(24 * GTRecipeBuilder.HOURS, TierEU.RECIPE_LV))
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
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemList.FluidExtractorUIV.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(24 * GTRecipeBuilder.HOURS, TierEU.RECIPE_LV))
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
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GregtechItemList.Mega_AlloyBlastSmelter.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(24 * GTRecipeBuilder.HOURS, TierEU.RECIPE_LV))
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
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.Laser_Cooling_Casing.get(1),
            2000000,
            48000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.EnhancementCore.get(1), GTNLItemList.Laser_Cooling_Casing.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16), ItemRefer.HiC_T4.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadah, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahEnriched, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Naquadria, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Ledox, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CallistoIce, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 4) },
            new FluidStack[] { Materials.Infinity.getMolten(288), Materials.SuperCoolant.getFluid(4000),
                Materials.UUMatter.getFluid(32000), GTNLMaterials.Polyetheretherketone.getMolten(2304) },
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
                GTNLMaterials.Polyetheretherketone.getMolten(2304) },
            GTNLItemList.Laser_Cooling_Casing.get(4),
            100,
            (int) TierEU.RECIPE_UV);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                aeMaterials.cardOreFilter()
                    .maybeStack(1)
                    .orNull())
            .metadata(GTRecipeConstants.SCANNING, new Scanning(4 * GTRecipeBuilder.HOURS, TierEU.RECIPE_HV))
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Conveyor_Module_IV.get(1),
                aeMaterials.cardOreFilter()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull(),
                aeBlocks.chest()
                    .maybeStack(1)
                    .orNull())
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(576), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.OredictInputBusME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(10 * GTRecipeBuilder.SECONDS)
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

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.DualInputHatchEV.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                GTNLItemList.SuperInputBusME.get(1),
                GTNLItemList.SuperInputHatchME.get(1),
                ItemList.Super_Chest_EV.get(1),
                ItemList.Super_Tank_EV.get(1),
                ItemList.Tool_DataOrb.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorLuV, 16),
                aeMaterials.cell256kPart()
                    .maybeStack(2)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_part", 2, 4),
                aeMaterials.cardCapacity()
                    .maybeStack(4)
                    .orNull(),
                aeBlocks.iface()
                    .maybeStack(4)
                    .orNull())
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152), Materials.Lubricant.getFluid(8000))
            .itemOutputs(GTNLItemList.SuperDualInputHatchME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.SuperDualInputHatchME.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_HV))
            .itemInputs(
                GTNLItemList.AdvancedSuperInputBusME.get(1),
                GTNLItemList.AdvancedSuperInputHatchME.get(1),
                ItemList.Quantum_Chest_LV.get(1),
                ItemList.Quantum_Tank_LV.get(1),
                ItemList.Tool_DataOrb.get(16),
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 16),
                aeMaterials.cell4096kPart()
                    .maybeStack(2)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_part", 2, 6),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(2)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_interface", 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(2304), Materials.Lubricant.getFluid(16000))
            .itemOutputs(GTNLItemList.AdvancedSuperDualInputHatchME.get(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemList.Automation_TypeFilter_IV.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(15 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_EV))
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Conveyor_Module_IV.get(1),
                ItemList.Automation_TypeFilter_IV.get(1),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull(),
                aeBlocks.chest()
                    .maybeStack(1)
                    .orNull())
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(720), Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNLItemList.TypeFilteredInputBusME.get(1))
            .eut(TierEU.RECIPE_IV)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .addTo(GTRecipeConstants.AssemblyLine);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.SuperDataAccessHatch.get(1),
            3072000,
            2048,
            102400000,
            1,
            new Object[] { CustomItemList.Machine_Multi_Research.get(64), CustomItemList.Machine_Multi_DataBank.get(64),
                CustomItemList.dataInAss_Wireless_Hatch.get(64), CustomItemList.dataOutAss_Wireless_Hatch.get(64),
                ItemList.SpaceElevatorModuleAssemblerT2.get(16), aeBlocks.craftingStorageSingularity()
                    .maybeStack(8)
                    .orNull(),
                CustomItemList.DATApipe.get(64),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, MaterialsUEVplus.SpaceTime, 64),
                ItemList.Tool_DataOrb.get(64), ItemList.Tool_DataStick.get(64), ItemList.Field_Generator_UIV.get(16),
                ItemList.ZPM4.get(16), new Object[] { OrePrefixes.circuit.get(Materials.UMV), 32L },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 32),
                GGMaterial.shirabon.get(OrePrefixes.plateSuperdense, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.ProtoHalkonite, 32) },
            new FluidStack[] { MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(512000),
                MaterialsUEVplus.TranscendentMetal.getMolten(368640),
                GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(640000),
                GGMaterial.metastableOganesson.getMolten(368640) },
            GTNLItemList.DebugDataAccessHatch.get(1),
            20000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeExtruder.get(1),
            1200000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeExtruder.get(64), new ItemStack(LanthItemList.ELECTRODE_CASING, 64),
                CustomItemList.eM_Power.get(32), GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 64),
                ItemList.BlockQuarkPipe.get(8), ItemList.Electric_Motor_UEV.get(32),
                ItemList.Electric_Piston_UEV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UIV), 8 },
                ItemRefer.HiC_T5.get(16), GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 8),
                GregtechItemList.Laser_Lens_Special.get(2) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.RadoxPolymer.getMolten(12000), GTNLMaterials.Polyetheretherketone.getMolten(32000),
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
                ItemList.RadiantNaquadahAlloyCasing.get(64), ItemList.Electric_Motor_UHV.get(64),
                ItemRefer.HiC_T5.get(16), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
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
                Materials.Neutronium.getMolten(147456), GTNLMaterials.Polyetheretherketone.getMolten(32000),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16 }, ItemRefer.HiC_T5.get(16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16),
                GregtechItemList.Laser_Lens_Special.get(2), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                GTNLMaterials.Polyetheretherketone.getMolten(32000), Materials.Lubricant.getFluid(256000),
                Materials.SuperCoolant.getFluid(256000) },
            GTNLItemList.HighEnergyLaserLathe.get(1),
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
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Piston_UHV.get(32), ItemRefer.HiC_T5.get(16),
                GregtechItemList.Laser_Lens_Special.get(2), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16 },
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16L),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16), },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                Materials.Lubricant.getFluid(64000), GTNLMaterials.Polyetheretherketone.getMolten(32000),
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
                ItemList.Electric_Piston_UEV.get(16), ItemList.Field_Generator_UEV.get(4), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32 }, GregtechItemList.Laser_Lens_Special.get(2),
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
            GTNLItemList.LargeElectromagnet.get(1),
            1000000,
            22000,
            (int) TierEU.RECIPE_UV,
            1,
            new Object[] { GTNLItemList.LargeElectromagnet.get(64), GTNLItemList.MolybdenumDisilicideCoil.get(64),
                CustomItemList.eM_Power.get(32), new ItemStack(LanthItemList.ELECTRODE_CASING, 32),
                ItemRefer.Speeding_Pipe.get(64), ItemList.Robot_Arm_UHV.get(16), ItemList.Emitter_UHV.get(16),
                ItemList.Sensor_UHV.get(16), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 32 },
                GregtechItemList.Laser_Lens_Special.get(1),
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
                CustomItemList.eM_Containment_Field.get(32), ItemList.Electric_Motor_UHV.get(16),
                ItemList.Field_Generator_UV.get(16), GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Infinity, 16),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16 }, ItemRefer.HiC_T5.get(16),
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
            new Object[] { GTNLItemList.LargeCircuitAssembler.get(64), GTUtility.copyAmount(64, ItemRegistry.cal),
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

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.VacuumFreezer.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(60 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(50 * GTRecipeBuilder.SECONDS)
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
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 64, 0),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 32),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, MaterialsUEVplus.SpaceTime, 32),
                GGMaterial.metastableOganesson.get(OrePrefixes.plateDense, 32),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.SixPhasedCopper, 1),
                GregtechItemList.Laser_Lens_Special.get(16) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(10000),
                MaterialsUEVplus.Creon.getMolten(5000), MaterialsUEVplus.Mellion.getMolten(9072),
                MaterialsUEVplus.SixPhasedCopper.getMolten(20736) },
            GTNLItemList.WirelessUpgradeChip.get(1),
            2000,
            (int) TierEU.RECIPE_UMV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, CustomItemList.DATApipe.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                CustomItemList.Machine_Multi_Computer.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorUV, 4),
                CustomItemList.DATApipe.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1))
            .fluidInputs(
                Materials.Grade4PurifiedWater.getFluid(32000),
                Materials.Osmium.getMolten(1296),
                Materials.UUMatter.getFluid(2560),
                GTNLMaterials.Polyetheretherketone.getMolten(1296))
            .itemOutputs(GTNLItemList.HighPerformanceComputationArray.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(50 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTNLItemList.AssemblerMatrixCrafterCore.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(5 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_UEV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Infinity, 32),
                aeBlocks.craftingStorageSingularity()
                    .maybeStack(1)
                    .orNull(),
                GTNLItemList.AssemblerMatrixWall.get(32),
                GTNLItemList.AssemblerMatrixCrafterCore.get(32),
                aeBlocks.craftingStorage16384k()
                    .maybeStack(32)
                    .orNull(),
                aeMaterials.cardSuperluminalSpeed()
                    .maybeStack(16)
                    .orNull(),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(32)
                    .orNull(),
                aeMaterials.cardSpeed()
                    .maybeStack(64)
                    .orNull(),
                aeMaterials.cardCapacity()
                    .maybeStack(64)
                    .orNull(),
                aeMaterials.singularity()
                    .maybeStack(64)
                    .orNull(),
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
            .duration(150 * GTRecipeBuilder.SECONDS)
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
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 64),
                GTUtility.copyAmountUnsafe(64, Particle.getBaseParticle(Particle.GRAVITON)),
                ItemList.Black_Hole_Stabilizer.get(32), ItemList.EnergisedTesseract.get(32),
                ItemList.Field_Generator_UMV.get(16), ItemList.Transdimensional_Alignment_Matrix.get(8),
                CustomItemList.astralArrayFabricator.get(1), aeItems.cellUniverse()
                    .maybeStack(1)
                    .orNull() },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(256000),
                MaterialsUEVplus.PhononMedium.getFluid(128000), MaterialsUEVplus.TranscendentMetal.getMolten(46080) },
            GTNLItemList.EyeOfHarmonyInjector.get(1),
            1000,
            (int) TierEU.RECIPE_UXV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTModHandler.getModItem(Mods.EnderStorage.ID, "enderChest", 1),
            512000,
            2048,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { aeBlocks.drive()
                .maybeStack(64)
                .orNull(),
                aeBlocks.creativeEnergyController()
                    .maybeStack(16)
                    .orNull(),
                GTNLItemList.QuantumComputerCore.get(16), ItemList.Quantum_Chest_EV.get(64),
                ItemList.Quantum_Tank_EV.get(64), ItemList.Field_Generator_UV.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UHV), 48L }, aeMaterials.singularity()
                    .maybeStack(64)
                    .orNull(),
                aeMaterials.cell16384kPart()
                    .maybeStack(64)
                    .orNull(),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_part", 64, 7),
                GregtechItemList.Gregtech_Computer_Cube.get(16),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 2, 0),
                GTNLItemList.EnhancementCore.get(1) },
            new FluidStack[] { GTNLMaterials.QuantumInfusion.getFluidOrGas(256000),
                MaterialsAlloy.INDALLOY_140.getFluidStack(147456), Materials.Infinity.getMolten(18432),
                MaterialsUEVplus.ExcitedDTPC.getFluid(64000) },
            GTNLItemList.SingularityDataHub.get(1),
            8000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.FuelRefiningComplex.get(1),
            32768000,
            4096,
            (int) TierEU.RECIPE_UIV,
            16,
            new Object[] { GTNLItemList.FuelRefiningComplex.get(4), ItemList.Neutronium_Active_Casing.get(64),
                new ItemStack(WerkstoffLoader.BWBlockCasings, 64, 31895),
                new ItemStack(WerkstoffLoader.BWBlockCasingsAdvanced, 64, 31895), ItemList.Electric_Pump_UEV.get(16),
                ItemList.Field_Generator_UEV.get(8), ItemRefer.HiC_T5.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L },
                GregtechItemList.Laser_Lens_Special.get(4),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 2, 0),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUEV, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Netherite, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Bedrockium, 4) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(128000),
                Materials.Lubricant.getFluid(128000), MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(23040),
                Materials.Naquadria.getMolten(46080) },
            GTNLItemList.AtomicEnergyExcitationPlant.get(1),
            4000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.MassFabricator.get(1),
            100000000,
            65536,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { GTNLItemList.MassFabricator.get(64), GregtechItemList.Casing_ElementalDuplicator.get(64),
                ItemList.Relativistic_Heat_Capacitor.get(64), ItemList.Thermal_Superconductor.get(64),
                GTNLItemList.EnhancementCore.get(64), ItemList.Field_Generator_UIV.get(64), ItemList.ZPM5.get(1),
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 64L },
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SuperconductorUIVBase, 8) },
            new FluidStack[] { MaterialsUEVplus.PhononMedium.getFluid(128000),
                MaterialsUEVplus.TranscendentMetal.getMolten(1024000), MaterialsUEVplus.ExcitedDTEC.getFluid(2048000),
                WerkstoffLoader.Oganesson.getFluidOrGas(1000000) },
            GTNLItemList.AdvancedMassFabricator.get(1),
            20000,
            (int) TierEU.RECIPE_UMV);

        RecipeBuilder.builder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, CustomItemList.Machine_Multi_DataBank.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(5 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
            .itemInputs(
                CustomItemList.Machine_Multi_DataBank.get(1),
                CustomItemList.eM_Computer_Bus.get(4),
                CustomItemList.DATApipe.get(32),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(16)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorZPM, 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 4L },
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Naquadah, 64),
                ItemList.Field_Generator_ZPM.get(1))
            .fluidInputs(
                Materials.UUMatter.getFluid(1000),
                Materials.Iridium.getMolten(1152),
                Materials.Osmium.getMolten(1152),
                Materials.SuperCoolant.getFluid(1000))
            .itemOutputs(GTNLItemList.DataCenter.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.OilDrillInfinite.get(1),
            2560000,
            4096,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { ItemList.OilDrillInfinite.get(64), ItemList.PlanetaryGasSiphonController.get(64),
                ItemRefer.HiC_T5.get(16), ItemList.Electric_Pump_UEV.get(32),
                CustomItemList.enderLinkFluidCover.get(32),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Infinity, 32),
                ItemList.Field_Generator_UEV.get(8), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                GregtechItemList.Laser_Lens_Special.get(8), GTNLItemList.EnhancementCore.get(4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(46080),
                GTNLMaterials.Polyetheretherketone.getMolten(36864), Materials.SuperCoolant.getFluid(256000),
                MaterialsAlloy.PIKYONIUM.getFluidStack(46080) },
            GTNLItemList.AdvancedInfiniteDriller.get(1),
            20000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemRegistry.megaMachines[3],
            1280000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTUtility.copyAmountUnsafe(16, ItemRegistry.megaMachines[3]),
                ItemList.Electric_Motor_UHV.get(16), ItemList.Electric_Pump_UHV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L }, GTNLItemList.EnhancementCore.get(1),
                GregtechItemList.Laser_Lens_Special.get(16), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeQuadruple, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.pipeQuadruple, Materials.Polytetrafluoroethylene, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Polytetrafluoroethylene, 1) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(23040),
                GTNLMaterials.Polyetheretherketone.getMolten(23040),
                Materials.Polytetrafluoroethylene.getMolten(163840), MaterialsAlloy.PIKYONIUM.getFluidStack(46080) },
            GTNLItemList.ChemicalComplex.get(1),
            2000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeWiremill.get(1),
            1000000,
            40000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.LargeWiremill.get(64), ItemList.WiremillUHV.get(8),
                ItemList.Electric_Motor_UHV.get(32), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L }, GTNLItemList.EnhancementCore.get(1),
                GregtechItemList.Laser_Lens_Special.get(16), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 12),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.InfinityCatalyst, 16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(32768),
                GTNLMaterials.Polyetheretherketone.getMolten(32768), Materials.Lubricant.getFluid(102400),
                MaterialsAlloy.PIKYONIUM.getFluidStack(23040) },
            GTNLItemList.MegaWiremill.get(1),
            1600,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            LanthItemList.BEAMLINE_PIPE,
            20000,
            16,
            (int) TierEU.RECIPE_ZPM,
            1,
            new Object[] { new ItemStack(LanthItemList.FOCUS_MANIPULATION_CASING, 1),
                GTUtility.copyAmount(4, LanthItemList.BEAMLINE_PIPE),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Bedrockium, 1),
                ItemList.Circuit_Chip_QuantumCPU.get(2), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 1L } },
            new FluidStack[] { Materials.Grade4PurifiedWater.getFluid(4000) },
            GTNLItemList.BeamlinePipeMirror.get(1),
            200,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.PCBFactory.get(1),
            128000,
            64,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Infinity, 32),
                ItemList.PCBFactory.get(4), new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16L },
                new Object[] { OrePrefixes.circuit.get(Materials.UV), 8L }, ItemList.Robot_Arm_UV.get(8),
                GregtechItemList.Gregtech_Computer_Cube.get(4), GregtechItemList.Energy_Core_ZPM.get(4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 2) },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(10368),
                Materials.EnrichedHolmium.getMolten(9216), Materials.Naquadria.getMolten(4608) },
            GTNLItemList.PCBFactory.get(1),
            1000,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.LargeNaquadahReactor.get(1),
            512000,
            1024,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.PrismaticNaquadah, 16),
                GTNLItemList.LargeNaquadahReactor.get(2), ItemList.Circuit_Chip_UHPIC.get(64),
                ItemList.Circuit_Chip_QPIC.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 4L },
                ItemList.Electric_Pump_UEV.get(2), ItemList.Field_Generator_UEV.get(2),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Infinity, 4),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDouble(4),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.NaquadahEnriched, 32), ItemList.ZPM2.get(1) },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(1296),
                Materials.SolderingAlloy.getMolten(1296), MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(864),
                GGMaterial.artheriumSn.getMolten(864) },
            GTNLItemList.HyperNaquadahReactor.get(1),
            500,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Transdimensional_Alignment_Matrix.get(1),
            2000000,
            40000,
            (int) TierEU.RECIPE_UMV,
            1,
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.SpaceTime, 48),
                GregtechItemList.SpaceTimeContinuumRipper.get(32),
                CustomItemList.TimeAccelerationFieldGeneratorTier0.get(4), CustomItemList.eM_Hollow.get(64),
                CustomItemList.EOH_Reinforced_Temporal_Casing.get(32),
                CustomItemList.EOH_Reinforced_Spatial_Casing.get(32), ItemList.Machine_Multi_PlasmaForge.get(8),
                ReAvaItemList.ChronarchsClock.get(1), GregtechItemList.Laser_Lens_Special.get(64) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(512000),
                MaterialsUEVplus.ExcitedDTEC.getFluid(2048000),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(256000),
                MaterialsUEVplus.SpaceTime.getMolten(96000) },
            GTNLItemList.TransdimensionalMnemonicMatrix.get(1),
            16000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.HyperNaquadahReactor.get(1),
            8192000,
            16384,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { ItemRefer.GravityStabilizationCasing.get(64), ItemRefer.MagneticFluxCasing.get(32),
                ItemRefer.AntimatterAnnihilationMatrix.get(4), GTNLItemList.HyperNaquadahReactor.get(2),
                ItemList.Electric_Pump_UMV.get(32), ItemList.Field_Generator_UMV.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SpaceTime, 2),
                GTOreDictUnificator.get(OrePrefixes.rotor, MaterialsUEVplus.ProtoHalkonite, 16),
                ItemList.NuclearStar.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UMV), 16L },
                ItemList.ZPM5.get(1),
                GTModHandler.getModItem(Mods.UniversalSingularities.ID, "universal.general.singularity", 4, 26),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUMV, 16),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.TranscendentMetal, 64) },
            new FluidStack[] { MaterialsUEVplus.Antimatter.getFluid(10), GGMaterial.shirabon.getMolten(18432),
                GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(512000),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(64000) },
            GTNLItemList.AdvancedHyperNaquadahReactor.get(1),
            32000,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            CustomItemList.Machine_Multi_QuarkGluonPlasmaModule.get(1),
            768000000,
            131072,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { CustomItemList.Machine_Multi_QuarkGluonPlasmaModule.get(16),
                ItemList.Machine_Multi_PlasmaForge.get(16), ItemRefer.Compact_Fusion_MK5.get(4), ItemList.ZPM5.get(4),
                CustomItemList.Godforge_HarmonicPhononTransmissionConduit.get(32), ItemList.Robot_Arm_UXV.get(16),
                ItemList.Conveyor_Module_UXV.get(32), ItemList.Electric_Pump_UXV.get(64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SuperconductorUMVBase, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.MagMatter, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Eternity, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Universium, 16),
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 64L } },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(147456),
                MaterialsUEVplus.ExcitedDTEC.getFluid(2048000), Materials.Lead.getPlasma(36864),
                MaterialsUEVplus.TranscendentMetal.getMolten(147456) },
            GTNLItemList.FOGSolarMuonCatalystModule.get(1),
            300 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UMV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GregtechItemList.Industrial_TreeFarm.get(1),
            14600000,
            18000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GregtechItemList.Industrial_TreeFarm.get(64), ItemList.BlockUltraVioletLaserEmitter.get(64),
                ItemList.BlockFlocculationCasing.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.RadoxPolymer, 64),
                ItemList.Electric_Pump_UHV.get(64), ItemList.Robot_Arm_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 32),
                GTNLItemList.EnhancementCore.get(8), GregtechItemList.Laser_Lens_Special.get(4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(76570),
                Materials.Xenoxene.getFluid(180000), GTNLMaterials.Polyetheretherketone.getMolten(28660),
                Materials.Lubricant.getFluid(100000) },
            GTNLItemList.TransliminalOasis.get(1),
            80 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Machine_Multi_TranscendentPlasmaMixer.get(1),
            819200000,
            32767,
            (int) TierEU.RECIPE_UXV,
            1,
            new Object[] { ItemList.Machine_Multi_TranscendentPlasmaMixer.get(64),
                GTModHandler.getModItem(Mods.GalacticraftAmunRa.ID, "tile.baseBlockRock", 64, 14),
                CustomItemList.EOH_Infinite_Energy_Casing.get(64), ItemList.Robot_Arm_UXV.get(64),
                ItemList.Electric_Motor_UXV.get(64), ItemList.Electric_Piston_UXV.get(64), ItemList.Emitter_UXV.get(64),
                ItemList.Sensor_UXV.get(64), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 64),
                GTNLItemList.UMVParallelControllerCore.get(48),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.Eternity, 64),
                GTNLItemList.WirelessUpgradeChip.get(1), GTNLItemList.TransdimensionalMnemonicMatrix.get(64),
                ItemList.Black_Hole_Stabilizer.get(16) },
            new FluidStack[] { MaterialsUEVplus.ExcitedDTSC.getFluid(4200000),
                MaterialsUEVplus.MagMatter.getMolten(60480), MaterialsUEVplus.QuarkGluonPlasma.getFluid(4200000),
                MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(604800) },
            GTNLItemList.MagneticConfinementDimensionalityShockDevice.get(1),
            42 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_MAX);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNLItemList.Digester.get(1),
            1800000,
            32767,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTNLItemList.Digester.get(64), GTUtility.copyAmount(64, LanthItemList.DISSOLUTION_TANK),
                ItemList.Quantum_Tank_IV.get(8), GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Holmium, 64),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TengamAttuned, 32),
                ItemList.Electric_Motor_UEV.get(16), ItemList.Electric_Piston_UEV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 8),
                GregtechItemList.Laser_Lens_Special.get(4), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 12) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(102000),
                Materials.Lubricant.getFluid(200000), GTNLMaterials.Polyetheretherketone.getMolten(32000),
                GGMaterial.enrichedNaquadahAlloy.getMolten(120240) },
            GTNLItemList.DissolutionCore.get(1),
            88 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UIV);

        if (Mods.MobsInfo.isModLoaded()) {
            TTRecipeAdder.addResearchableAssemblylineRecipe(
                kubatech.api.enums.ItemList.ExtremeEntityCrusher.get(1),
                6660000,
                666,
                (int) TierEU.RECIPE_UEV,
                1,
                new Object[] { kubatech.api.enums.ItemList.ExtremeEntityCrusher.get(64),
                    GTModHandler.getModItem(Mods.EnderIO.ID, "blockPoweredSpawner", 64),
                    ItemList.UltraHighStrengthConcrete.get(64),
                    GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StellarAlloy, 64),
                    GTModHandler.getModItem(Mods.BloodMagic.ID, "daggerOfSacrifice", 1),
                    ItemList.Electric_Motor_UEV.get(64), ItemList.Robot_Arm_UEV.get(16),
                    ItemList.Conveyor_Module_UEV.get(32),
                    GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 8), ItemList.NuclearStar.get(48),
                    GTModHandler.getModItem(Mods.EnderIO.ID, "itemMaterial", 64, 9), ItemRefer.HiC_T5.get(64),
                    GTNLItemList.EnhancementCore.get(16), GregtechItemList.Laser_Lens_Special.get(2),
                    GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 12) },
                new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(96000),
                    Materials.Lubricant.getFluid(128000), GTNLMaterials.Polyetheretherketone.getMolten(36864),
                    new FluidStack(GTPPFluids.Pyrotheum, 512000) },
                GTNLItemList.HighwayToHell.get(1),
                60 * GTRecipeBuilder.SECONDS,
                (int) TierEU.RECIPE_UIV);
        }

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GregtechItemList.Controller_ElementalDuplicator.get(1),
            22331100,
            32767,
            (int) TierEU.RECIPE_UIV,
            1,
            new Object[] { GregtechItemList.Controller_ElementalDuplicator.get(64),
                ItemRefer.ProtomatterActivationCoil.get(64), GTNLItemList.AssemblerMatrixSingularityCrafterCore.get(8),
                GTNLItemList.DebugDataAccessHatch.get(1), ItemRefer.Fluid_Storage_Core_T10.get(64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 64), ItemList.Field_Generator_UIV.get(16),
                ItemList.Electric_Pump_UIV.get(24), ItemList.Quark_Creation_Catalyst_Strange.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.ProtoHalkonite, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Mellion, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64),
                GregtechItemList.Laser_Lens_Special.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 32) },
            new FluidStack[] { MaterialsUEVplus.ExcitedDTEC.getFluid(1200000), Materials.UUMatter.getFluid(20000000),
                GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(256000),
                MaterialsUEVplus.PhononMedium.getFluid(128000) },
            GTNLItemList.ElementCopying.get(1),
            1000 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UMV);

        if (Mods.NewHorizonsCoreMod.isModLoaded()) loadNHRecipe();
    }

    @Optional.Method(modid = "dreamcraft")
    public void loadNHRecipe() {
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
            ItemRegistry.eic.copy(),
            51200000,
            51200,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTUtility.copyAmount(4, ItemRegistry.eic.copy()),
                BlockList.NaquadahPlatedReinforcedStone.getIS(16),
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
            120 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UEV);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.RocketControlComputer", 1, 4))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(4 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
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
            .duration(30 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        RecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.RocketControlComputer", 1, 7))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_ZPM))
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
            .duration(120 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

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
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.LuV), 2L },
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
            new Object[] { GTNLItemList.LargeCutter.get(64),
                GregtechItemList.Industrial_CuttingFactoryController.get(64), ItemList.Neutronium_Active_Casing.get(64),
                GregtechItemList.Casing_Autocrafter.get(12), ItemList.Electric_Motor_UHV.get(32),
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
                CustomItemList.eM_Power.get(32), GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 16),
                ItemList.Electric_Motor_UV.get(48), ItemList.Field_Generator_UV.get(12),
                ItemList.Electric_Pump_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Neutronium, 12), ItemRefer.HiC_T4.get(32),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Neutronium, 8),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.Longasssuperconductornameforuvwire, 12),
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
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64), ItemRefer.HiC_T5.get(16),
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
                ItemList.Neutronium_Casing.get(32),
                GTModHandler.getModItem(Mods.KekzTech.ID, "kekztech_lapotronicenergyunit_block", 64),
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
            GTNLItemList.LargeDistillery.get(1),
            1800000,
            40000,
            (int) TierEU.RECIPE_UHV,
            1,
            new Object[] { GTNLItemList.LargeDistillery.get(64), GTUtility.copyAmount(64, ItemRegistry.megaMachines[2]),
                ItemList.BlockNaquadriaReinforcedWaterPlantCasing.get(64), ItemList.Heating_Duct_Casing.get(64),
                ItemList.Electric_Motor_UHV.get(32), ItemList.Electric_Pump_UHV.get(32),
                ItemList.Field_Generator_UHV.get(16), ItemRefer.HiC_T5.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 16) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(294912),
                Materials.Lubricant.getFluid(128000), GTNLMaterials.Polyetheretherketone.getMolten(36864),
                MaterialsAlloy.PIKYONIUM.getFluidStack(36864) },
            GTNLItemList.ExtremeElectricFurnace.get(1),
            8000,
            (int) TierEU.RECIPE_UEV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemRegistry.megaMachines[4],
            2048000,
            4000,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { GTUtility.copyAmountUnsafe(16, ItemRegistry.megaMachines[4]),
                CustomItemList.tM_TeslaBase.get(64), GregtechItemList.Casing_Fusion_Internal.get(16),
                ItemList.Casing_Coil_AwakenedDraconium.get(64), ItemList.Electric_Pump_UHV.get(32),
                ItemList.Sensor_UHV.get(16), ItemList.Emitter_UHV.get(16), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                ItemRefer.HiC_T5.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(4), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
                ItemList.UHV_Coil.get(64),
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
                ItemRefer.HiC_T5.get(48), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 48L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(2), GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 4),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 48L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Ultimate, 64),
                ItemList.Electric_Piston_UEV.get(32), ItemList.Electric_Pump_UEV.get(32), ItemRefer.HiC_T5.get(16),
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 12L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                ItemList.EnergisedTesseract.get(16),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 1),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GTNLItemList.EnhancementCore.get(64),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 32, 0),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.TranscendentMetal, 8),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.ProtoHalkonite, 8) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(143856),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(143856),
                MaterialsUEVplus.TranscendentMetal.getMolten(143856) },
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
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                ItemRefer.HiC_T5.get(64), new Object[] { OrePrefixes.circuit.get(Materials.UEV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(1),
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
                GTModHandler.getModItem(Mods.GTPlusPlus.ID, "item.itemBufferCore10", 16),
                ItemList.Electric_Pump_UEV.get(32), ItemList.Electric_Piston_UEV.get(16), ItemList.Emitter_UEV.get(16),
                ItemList.Sensor_UEV.get(16), ItemRefer.HiC_T5.get(64),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 12L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 1),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(32),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 4),
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
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 24L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GTNLItemList.EnhancementCore.get(64),
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 24),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SixPhasedCopper, 4) },
            new FluidStack[] { GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(131072),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(131072),
                MaterialsUEVplus.TranscendentMetal.getMolten(131072) },
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
                .orNull(), GTModHandler.getModItem(Mods.AvaritiaAddons.ID, "InfinityChest", 8),
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 16L },
                GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 16),
                ItemList.ZPM3.get(1), GregtechItemList.Laser_Lens_Special.get(64),
                GTNLItemList.ShatteredSingularity.get(64), aeMaterials.singularity()
                    .maybeStack(64)
                    .orNull(),
                com.dreammaster.gthandler.CustomItemList.EngravedQuantumChip.get(64),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Quantium, 64),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Quantium, 64),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Quantium, 16),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Quantium, 64) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(131072),
                MaterialsUEVplus.DimensionallyShiftedSuperfluid.getFluid(256000),
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
            new Object[] { GTNLItemList.DecayHastener.get(32), GTUtility.copyAmount(64, Loaders.NA.copy()),
                GregtechItemList.COMET_Cyclotron.get(64), ItemRefer.Speeding_Pipe.get(64),
                CustomItemList.tM_TeslaBase.get(64), ItemList.Electric_Pump_UV.get(32), ItemList.Sensor_UV.get(32),
                ItemList.Emitter_UV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 16L },
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
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
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64),
                GregtechItemList.Laser_Lens_Special.get(4),
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
                com.dreammaster.gthandler.CustomItemList.HighEnergyFlowCircuit.get(64), ItemRefer.HiC_T5.get(32),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUHV, 16),
                GregtechItemList.Laser_Lens_Special.get(1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 16) },
            new FluidStack[] { MaterialsAlloy.INDALLOY_140.getFluidStack(64000), Materials.Lubricant.getFluid(140000),
                GTNLMaterials.Polyetheretherketone.getMolten(14400), MaterialsAlloy.CINOBITE.getFluidStack(186624) },
            GTNLItemList.GiantFlotationTank.get(1),
            100 * GTRecipeBuilder.SECONDS,
            (int) TierEU.RECIPE_UHV);
    }
}
