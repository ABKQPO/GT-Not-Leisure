package com.science.gtnl.common.recipe.gregtech;

import static bartworks.common.loaders.ItemRegistry.bw_realglas;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.block.BlockList;
import com.dreammaster.gthandler.CustomItemList;
import com.dreammaster.item.NHItemList;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import appeng.api.util.AEColor;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.Optional;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.item.ModItems;
import gtPlusPlus.core.item.crafting.ItemDummyResearch;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;

public class AssemblerRecipes implements IRecipePool {

    public RecipeMap<?> As = RecipeMaps.assemblerRecipes;
    public RecipeMap<?> HOR = GTNLRecipeMaps.HardOverrideRecipes;

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

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silicon, 8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Naquadah, 4))
            .fluidInputs(GTNLMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNLItemList.BiowareSMDCapacitor.get(16))
            .duration(100)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1))
            .fluidInputs(GTNLMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNLItemList.BiowareSMDDiode.get(16))
            .duration(100)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Naquadria, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Tritanium, 1))
            .fluidInputs(GTNLMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNLItemList.BiowareSMDResistor.get(16))
            .duration(100)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTNLMaterials.Germaniumtungstennitride.get(OrePrefixes.plate, 2),
                MaterialsAlloy.SILICON_CARBIDE.getPlate(2))
            .fluidInputs(GTNLMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNLItemList.BiowareSMDTransistor.get(16))
            .duration(100)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.NaquadahAlloy, 1))
            .fluidInputs(GTNLMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNLItemList.BiowareSMDInductor.get(16))
            .duration(100)
            .eut(491520)
            .requiresCleanRoom()
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.QuadrupleCompressedObsidian.get(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 4),
                GTModHandler.getModItem(Mods.ExtraUtilities.ID, "decorativeBlock2", 1, 7))
            .itemOutputs(GTNLItemList.FortifyGlowstone.get(1))
            .duration(200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "pylon", 1, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "pylon", 2, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "pylon", 4, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 7),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 8))
            .itemOutputs(GTNLItemList.ActivatedGaiaPylon.get(1))
            .duration(200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ItemList.Hatch_Input_LuV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.Terrasteel, 8),
                GTModHandler.getModItem(Mods.Botania.ID, "pylon", 4, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "pool", 1, 3),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L))
            .itemOutputs(GTNLItemList.FluidManaInputHatch.get(1))
            .duration(200)
            .eut(30720)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                ItemList.OreDrill2.get(1L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 8),
                new ItemStack(GregTechAPI.sLaserRender, 4, 0),
                ItemList.Emitter_EV.get(2L),
                ItemList.Sensor_EV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.gear, Materials.TungstenSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(9216))
            .itemOutputs(GTNLItemList.MeteorMiner.get(1))
            .duration(1200)
            .eut(30720)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_IV_Assembler.get(4L),
                ItemRefer.Precise_Assembler.get(2),
                ItemList.Machine_Multi_Assemblyline.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 16),
                ItemList.Robot_Arm_IV.get(8L),
                ItemList.Casing_Gearbox_TungstenSteel.get(8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 10L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 12L))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(256000))
            .itemOutputs(GTNLItemList.ComponentAssembler.get(1))
            .duration(600)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_IV.get(4L),
                ItemList.Casing_Autoclave.get(16L),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Desh, 32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4L),
                ItemList.Super_Tank_EV.get(4L),
                ItemList.Electric_Motor_IV.get(4L),
                ItemList.Electric_Pump_IV.get(8L),
                ItemList.Steam_Valve_IV.get(16L))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(64000))
            .itemOutputs(GTNLItemList.PetrochemicalPlant.get(1))
            .duration(1200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_EV.get(1L),
                ItemList.Robot_Arm_EV.get(16L),
                ItemList.Conveyor_Module_EV.get(4L),
                ItemList.Electric_Motor_EV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                ItemList.Casing_StableTitanium.get(4L),
                GregtechItemList.GTPP_Casing_EV.get(8L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(18432))
            .itemOutputs(GTNLItemList.ProcessingArray.get(1))
            .duration(600)
            .eut(1920)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 4L),
                GTNLItemList.PrecisionSteamMechanism.get(4),
                ItemList.Machine_Bricked_BlastFurnace.get(1L))
            .itemOutputs(GTNLItemList.BrickedBlastFurnace.get(1))
            .duration(600)
            .eut(16)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_EV_CircuitAssembler.get(1),
                ItemList.Robot_Arm_EV.get(2),
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 2L),
                ItemList.Conveyor_Module_EV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2L))
            .itemOutputs(GTNLItemList.LargeCircuitAssembler.get(1))
            .duration(200)
            .eut(1920)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                MaterialsAlloy.LEAGRISIUM.getPlateDouble(2),
                GregtechItemList.Casing_AdvancedVacuum.get(1),
                MaterialsAlloy.INCOLOY_MA956.getGear(2),
                ItemList.Electric_Piston_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1L),
                GregtechItemList.Gregtech_Computer_Cube.get(1L))
            .itemOutputs(GregtechItemList.Industrial_Cryogenic_Freezer.get(1))
            .duration(120)
            .eut(32070)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                MaterialsAlloy.HASTELLOY_N.getPlateDouble(2),
                GregtechItemList.Casing_Adv_BlastFurnace.get(1),
                MaterialsAlloy.HASTELLOY_W.getGear(2),
                ItemList.Robot_Arm_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1L),
                GregtechItemList.Gregtech_Computer_Cube.get(1L))
            .itemOutputs(GregtechItemList.Machine_Adv_BlastFurnace.get(1))
            .duration(120)
            .eut(32070)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Cryogenic_Freezer.get(4),
                GregtechItemList.Casing_AdvancedVacuum.get(4),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.HSSE, 16L),
                ItemList.Electric_Motor_IV.get(16L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L),
                MaterialsAlloy.INCOLOY_MA956.getGear(8),
                MaterialsAlloy.LEAGRISIUM.getPlateDouble(16),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 8L),
                GregtechItemList.Gregtech_Computer_Cube.get(1L))
            .itemOutputs(GTNLItemList.ColdIceFreezer.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(4608))
            .duration(120)
            .eut(32070)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Machine_Adv_BlastFurnace.get(4),
                GregtechItemList.Casing_Adv_BlastFurnace.get(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorLuV, 16L),
                ItemList.Robot_Arm_IV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L),
                MaterialsAlloy.INCOLOY_MA956.getGear(8),
                MaterialsAlloy.HASTELLOY_N.getPlateDouble(16),
                GregtechItemList.Gregtech_Computer_Cube.get(1L))
            .itemOutputs(GTNLItemList.BlazeBlastFurnace.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(4608))
            .duration(120)
            .eut(32070)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_LargeChemicalReactor.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorEV, 4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Polytetrafluoroethylene, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2L),
                ItemList.Electric_Motor_EV.get(4L),
                ItemList.Electric_Pump_EV.get(4L),
                WerkstoffLoader.Ruridit.get(OrePrefixes.rotor, 4))
            .itemOutputs(GTNLItemList.ChemicalPlant.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(1920)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Extruder.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.TPV, 4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Polytetrafluoroethylene, 4L),
                MaterialsAlloy.INCONEL_625.getPlateDouble(4),
                ItemList.Electric_Piston_IV.get(2L),
                ItemList.Conveyor_Module_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 1L))
            .itemOutputs(GTNLItemList.LargeExtruder.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_CuttingFactoryController.get(1L),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 4, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L),
                WerkstoffLoader.Ruridit.get(OrePrefixes.plateDouble, 4),
                ItemList.Electric_Motor_IV.get(2L),
                ItemList.Conveyor_Module_IV.get(2L),
                ItemList.Component_Sawblade_Diamond.get(2L))
            .itemOutputs(GTNLItemList.LargeCutter.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_MacerationStack.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.TungstenSteel, 4L),
                ItemList.Electric_Motor_IV.get(2L),
                ItemList.Electric_Piston_IV.get(2L),
                ItemList.Component_Grinder_Tungsten.get(4L))
            .itemOutputs(GTNLItemList.LargeMacerationTower.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Arc_Furnace.get(1L),
                GregtechItemList.Energy_Core_IV.get(2),
                ItemList.Field_Generator_IV.get(1L),
                ItemList.Emitter_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                MaterialsAlloy.ZERON_100.getPlateDense(2),
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.HSSG, 4L))
            .itemOutputs(GTNLItemList.LargeArcSmelter.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Sifter.get(1L),
                MaterialsAlloy.INCONEL_792.getFrameBox(4),
                GregtechItemList.Casing_SifterGrate.get(4),
                ItemList.Electric_Piston_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Osmiridium, 4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Osmium, 4L))
            .itemOutputs(GTNLItemList.LargeSiftingFunnel.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Casing_CokeOven.get(4L),
                ItemList.Machine_IV_Brewery.get(1L),
                ItemList.Machine_IV_Fermenter.get(1L),
                ItemList.Machine_IV_FluidHeater.get(1L),
                ItemList.GlassPHResistant.get(8),
                ItemList.Electric_Pump_IV.get(2L),
                MaterialsAlloy.TANTALLOY_61.getPlateDouble(16),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L))
            .itemOutputs(GTNLItemList.LargeBrewer.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_Lathe.get(1L),
                MaterialsAlloy.AQUATIC_STEEL.getFrameBox(4),
                ItemList.Electric_Motor_IV.get(2L),
                ItemList.Electric_Piston_IV.get(2L),
                WerkstoffLoader.AdemicSteel.get(OrePrefixes.plateDouble, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L))
            .itemOutputs(GTNLItemList.LargeIndustrialLathe.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_IndustrialCompressor.get(1L),
                MaterialsAlloy.MARAGING350.getFrameBox(4),
                ItemList.Electric_Piston_IV.get(4L),
                ItemList.Robot_Arm_IV.get(2L),
                WerkstoffLoader.AdemicSteel.get(OrePrefixes.plateDouble, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Osmiridium, 4L))
            .itemOutputs(GTNLItemList.LargeMaterialPress.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_WireFactory.get(1L),
                MaterialsAlloy.MARAGING350.getFrameBox(4),
                ItemList.Electric_Motor_IV.get(4L),
                ItemList.Electric_Piston_IV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.BlueSteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L))
            .itemOutputs(GTNLItemList.LargeWiremill.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_PlatePress.get(1L),
                MaterialsAlloy.MARAGING300.getFrameBox(4),
                ItemList.Electric_Motor_IV.get(4L),
                ItemList.Electric_Piston_IV.get(4L),
                ItemList.Conveyor_Module_IV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.RedSteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Tungsten, 4L))
            .itemOutputs(GTNLItemList.LargeBender.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_PlatePress.get(1L),
                MaterialsAlloy.MARAGING250.getFrameBox(4),
                ItemList.Electric_Motor_IV.get(4L),
                ItemList.Electric_Piston_IV.get(4L),
                ItemList.Robot_Arm_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Invar, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.TungstenSteel, 4L))
            .itemOutputs(GTNLItemList.LargeForming.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Electrolyzer.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 4, 0),
                ItemList.Electric_Pump_IV.get(2L),
                MaterialsAlloy.STELLITE.getPlateDouble(4),
                MaterialsAlloy.STELLITE.getRotor(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Osmium, 4L))
            .itemOutputs(GTNLItemList.LargeElectrolyzer.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_IndustrialElectromagneticSeparator.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                ItemList.Conveyor_Module_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.VanadiumGallium, 8L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Osmium, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                ItemList.Electromagnet_Iron.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Platinum, 4L))
            .itemOutputs(GTNLItemList.LargeElectromagnet.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.GT4_Multi_Crafter.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                new ItemStack(ModItems.itemCircuitLFTR, 1),
                GregtechItemList.TransmissionComponent_IV.get(4),
                ItemList.Electric_Motor_IV.get(8L),
                ItemList.Conveyor_Module_IV.get(8L),
                ItemList.Robot_Arm_IV.get(8L),
                GregtechItemList.Gregtech_Computer_Cube.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L))
            .itemOutputs(GTNLItemList.LargeAssembler.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Mixer.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Naquadah, 4L),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 4, 0),
                ItemList.Electric_Motor_IV.get(4L),
                MaterialsAlloy.ZIRCONIUM_CARBIDE.getPlateDouble(4),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.Osmium, 4L))
            .itemOutputs(GTNLItemList.LargeMixer.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_Centrifuge.get(1L),
                GregtechItemList.Industrial_ThermalCentrifuge.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Tungsten, 4L),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Platinum, 2L),
                ItemList.Electric_Motor_IV.get(4L),
                MaterialsAlloy.INCOLOY_DS.getPlateDouble(8),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Naquadah, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Platinum, 4L))
            .itemOutputs(GTNLItemList.LargeCentrifuge.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Neutronium, 4L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 4L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                GTUtility.getIntegratedCircuit(12))
            .itemOutputs(GTNLItemList.NeutroniumPipeCasing.get(1))
            .duration(100)
            .eut(30)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Neutronium, 2L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 4L),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.NeutroniumGearbox.get(1))
            .duration(100)
            .eut(122880)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Europium, 8L),
                ItemList.Electric_Motor_UHV.get(16L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.HSSS, 16L),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Infinity, 8L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSE, 16L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Naquadria, 64L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 4L),
                MaterialsAlloy.STELLITE.getPlate(32))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.RareEarthCentrifugal.get(1))
            .duration(1000)
            .eut(7864320)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 2L),
                ItemList.Casing_SolidSteel.get(1L),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L),
                ItemList.Electric_Motor_HV.get(2L))
            .fluidInputs(Materials.Polytetrafluoroethylene.getMolten(576))
            .itemOutputs(GTNLItemList.HeatVent.get(1))
            .duration(200)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_VacuumFreezer.get(1L),
                ItemList.Casing_FrostProof.get(2L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Gold, 4L),
                ItemList.Electric_Motor_HV.get(2L),
                ItemList.Electric_Pump_HV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2L),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 2L))
            .itemOutputs(GTNLItemList.VacuumFreezer.get(1))
            .fluidInputs(Materials.Aluminium.getMolten(1152))
            .duration(200)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Casing_ZPM.get(1L),
                MaterialsAlloy.ABYSSAL.getPlateDouble(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 4),
                ItemList.Field_Generator_UV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.NaquadahAlloy, 64),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.BlackPlutonium, 32),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Infinity, 2))
            .fluidInputs(new FluidStack(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getPlasma(), 16000))
            .itemOutputs(GTNLItemList.Antifreeze_Heatproof_Machine_Casing.get(1))
            .duration(200)
            .eut(1966080)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.block, Materials.BlackPlutonium, 4),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.CosmicNeutronium, 12),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Bedrockium, 8),
                GTModHandler.getModItem(Mods.Avaritia.ID, "Crystal_Matrix", 4),
                ItemList.Electric_Motor_UV.get(2L),
                ItemList.Sensor_UV.get(4L),
                ItemList.Emitter_UV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 4))
            .itemOutputs(ReAvaItemList.NeutronCollector.get(1))
            .duration(200)
            .eut(491520)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Item.getItemFromBlock(bw_realglas), 1, 4),
                ItemList.Casing_Fusion.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Naquadah, 1))
            .fluidInputs(Materials.NiobiumTitanium.getMolten(1152))
            .itemOutputs(GTNLItemList.FusionGlass.get(4))
            .duration(200)
            .eut(30720)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockSolarPanel", 1, 0),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemPowerConduit", 4, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ULV, 4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.RedAlloy, 4L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2L))
            .itemOutputs(GTNLItemList.EnergeticPhotovoltaicBlock.get(2))
            .duration(200)
            .eut(30)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockSolarPanel", 1, 1),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemPowerConduit", 8, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ULV, 8L),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.ElectricalSteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 2L))
            .itemOutputs(GTNLItemList.AdvancedPhotovoltaicBlock.get(2))
            .duration(200)
            .eut(120)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockSolarPanel", 1, 2),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemPowerConduit", 16, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ULV, 16L),
                GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.VibrantAlloy, 4L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 2L))
            .itemOutputs(GTNLItemList.VibrantPhotovoltaicBlock.get(2))
            .duration(200)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.TungstenCarbide, 3L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Ultimet, 4L),
                ItemList.Casing_MiningOsmiridium.get(1L),
                ItemList.Electric_Motor_IV.get(1L))
            .itemOutputs(GTNLItemList.CrushingWheels.get(2))
            .duration(50)
            .eut(16)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenCarbide, 3L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Ultimet, 4L),
                GregtechItemList.Casing_CuttingFactoryFrame.get(1L),
                ItemList.Electric_Motor_IV.get(1L))
            .itemOutputs(GTNLItemList.SlicingBlades.get(2))
            .duration(50)
            .eut(16)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_Bus_EV.get(1L),
                aeParts.iface()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.cardSpeed()
                    .maybeStack(2)
                    .orNull())
            .itemOutputs(
                ItemUtils
                    .getItemStack(ItemList.Hatch_Output_Bus_ME.get(1L), "{baseCapacity:9223372036854775807L}", null))
            .duration(300)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_ME.get(1L),
                aeBlocks.quantumRing()
                    .maybeStack(2)
                    .orNull(),
                aeBlocks.quantumLink()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.singularity()
                    .maybeStack(2)
                    .orNull(),
                aeBlocks.tinyTNT()
                    .maybeStack(1)
                    .orNull(),
                aeBlocks.craftingAccelerator()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(
                ItemUtils
                    .getItemStack(GTNLItemList.OutputHatchMEProxy.get(1L), "{baseCapacity:9223372036854775807L}", null))
            .duration(300)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_Bus_ME.get(1L),
                aeBlocks.quantumRing()
                    .maybeStack(2)
                    .orNull(),
                aeBlocks.quantumLink()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.singularity()
                    .maybeStack(2)
                    .orNull(),
                aeBlocks.tinyTNT()
                    .maybeStack(1)
                    .orNull(),
                aeBlocks.craftingAccelerator()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(
                ItemUtils
                    .getItemStack(GTNLItemList.OutputBusMEProxy.get(1L), "{baseCapacity:9223372036854775807L}", null))
            .duration(300)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_EV.get(1L),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "part_fluid_interface", 1),
                aeMaterials.cardSpeed()
                    .maybeStack(2)
                    .orNull())
            .itemOutputs(
                ItemUtils.getItemStack(ItemList.Hatch_Output_ME.get(1L), "{baseCapacity:9223372036854775807L}", null))
            .duration(300)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_ME.get(1L),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "part_fluid_interface", 1),
                ItemList.Electric_Pump_IV.get(1),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull())
            .itemOutputs(ItemList.Hatch_Input_ME_Advanced.get(1))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME.get(1L),
                aeParts.iface()
                    .maybeStack(1)
                    .orNull(),
                ItemList.Conveyor_Module_IV.get(1),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull())
            .itemOutputs(ItemList.Hatch_Input_Bus_ME_Advanced.get(1))
            .duration(300)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L),
                ItemList.Hatch_Input_Bus_LV.get(1L),
                ItemList.Hatch_Input_LV.get(1L),
                ItemList.Automation_ChestBuffer_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Bronze, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchLV.get(1))
            .duration(300)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Aluminium, 1L),
                ItemList.Hatch_Input_Bus_MV.get(1L),
                ItemList.Hatch_Input_MV.get(1L),
                ItemList.Automation_ChestBuffer_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Steel, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchMV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1L),
                ItemList.Hatch_Input_Bus_HV.get(1L),
                ItemList.Hatch_Input_HV.get(1L),
                ItemList.Automation_ChestBuffer_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.StainlessSteel, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchHV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L),
                ItemList.Hatch_Input_Bus_EV.get(1L),
                ItemList.Hatch_Input_EV.get(1L),
                ItemList.Automation_ChestBuffer_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Titanium, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchEV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L),
                ItemList.Hatch_Input_Bus_IV.get(1L),
                ItemList.Hatch_Input_IV.get(1L),
                ItemList.Automation_ChestBuffer_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.TungstenSteel, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchIV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Iridium, 1L),
                ItemList.Hatch_Input_Bus_LuV.get(1L),
                ItemList.Hatch_Input_LuV.get(1L),
                ItemList.Automation_ChestBuffer_LuV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.NiobiumTitanium, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchLuV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 1L),
                ItemList.Hatch_Input_Bus_ZPM.get(1L),
                ItemList.Hatch_Input_ZPM.get(1L),
                ItemList.Automation_ChestBuffer_ZPM.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Naquadah, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchZPM.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmium, 1L),
                ItemList.Hatch_Input_Bus_UV.get(1L),
                ItemList.Hatch_Input_UV.get(1L),
                ItemList.Automation_ChestBuffer_UV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.MysteriousCrystal, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchUV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                GregtechItemList.Hatch_SuperBus_Input_EV.get(1L),
                ItemList.Hatch_Input_UHV.get(1L),
                ItemList.Automation_ChestBuffer_UHV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Neutronium, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchUHV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UHV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bedrockium, 1L),
                GregtechItemList.Hatch_SuperBus_Input_IV.get(1L),
                ItemList.Hatch_Input_UEV.get(1L),
                ItemList.Automation_ChestBuffer_UEV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Infinity, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchUEV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UEV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.BlackPlutonium, 1L),
                GregtechItemList.Hatch_SuperBus_Input_LuV.get(1L),
                ItemList.Hatch_Input_UIV.get(1L),
                ItemList.Automation_ChestBuffer_UIV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, MaterialsUEVplus.TranscendentMetal, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchUIV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.SpaceTime, 1L),
                GregtechItemList.Hatch_SuperBus_Input_ZPM.get(1L),
                ItemList.Hatch_Input_UMV.get(1L),
                ItemList.Automation_ChestBuffer_UMV.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, MaterialsUEVplus.SpaceTime, 1L))
            .itemOutputs(GTNLItemList.DualInputHatchUMV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UMV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.MagMatter, 1L),
                GregtechItemList.Hatch_SuperBus_Input_UV.get(1L),
                ItemList.Hatch_Input_UXV.get(1L),
                ItemList.Automation_ChestBuffer_UMV.get(2),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, MaterialsUEVplus.SpaceTime, 2L))
            .itemOutputs(GTNLItemList.DualInputHatchUXV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UXV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator
                    .get(OrePrefixes.frameGt, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 1L),
                GregtechItemList.Hatch_SuperBus_Input_UV.get(1L),
                ItemList.Hatch_Input_MAX.get(1L),
                ItemList.Automation_ChestBuffer_UMV.get(4),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, MaterialsUEVplus.SpaceTime, 4L))
            .itemOutputs(GTNLItemList.DualInputHatchMAX.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTNLItemList.LargePyrolyseOven.get(2),
                GTNLItemList.LargeDistillery.get(4),
                ItemList.Emitter_IV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.StainlessSteel, 8L),
                ItemList.Electric_Pump_IV.get(8L),
                MaterialsAlloy.AQUATIC_STEEL.getPlate(16),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.StainlessSteel, 32L))
            .itemOutputs(GTNLItemList.WoodDistillation.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .duration(400)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemDummyResearch
                    .getResearchStack(ItemDummyResearch.ASSEMBLY_LINE_RESEARCH.RESEARCH_11_MOLECULAR_TRANSFORMER, 1),
                MaterialsAlloy.ZERON_100.getFrameBox(8),
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Platinum, 8L),
                MaterialsAlloy.BABBIT_ALLOY.getPlate(16),
                MaterialsAlloy.HG1223.getFineWire(32),
                ItemList.Emitter_LuV.get(8L),
                ItemList.Energy_LapotronicOrb.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 8L),
                ItemList.Field_Generator_EV.get(8L))
            .itemOutputs(GTNLItemList.MolecularTransformer.get(1))
            .fluidInputs(MaterialsAlloy.NITINOL_60.getFluidStack(2304))
            .duration(200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.PyrolyseOven.get(1),
                ItemList.Field_Generator_HV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.StainlessSteel, 2L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.StainlessSteel, 4L))
            .itemOutputs(GTNLItemList.LargePyrolyseOven.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(400)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                ItemList.Hull_LV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Steel, 32L),
                ItemList.Robot_Arm_LV.get(2L),
                ItemList.Emitter_LV.get(3L),
                ItemList.Sensor_LV.get(3L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 8L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Tin, 32L))
            .itemOutputs(GTNLItemList.LVParallelControllerCore.get(1))
            .fluidInputs(Materials.Cupronickel.getMolten(1296))
            .duration(400)
            .eut(30)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                ItemList.Hull_MV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Aluminium, 32L),
                ItemList.Robot_Arm_MV.get(4L),
                ItemList.Emitter_MV.get(2L),
                ItemList.Sensor_MV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 12L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.AnnealedCopper, 32L))
            .itemOutputs(GTNLItemList.MVParallelControllerCore.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .duration(400)
            .eut(120)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                ItemList.Hull_HV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.StainlessSteel, 32L),
                ItemList.Electric_Motor_HV.get(8L),
                ItemList.Robot_Arm_HV.get(8L),
                ItemList.Emitter_HV.get(4L),
                ItemList.Sensor_HV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 12L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.BlueAlloy, 32L))
            .itemOutputs(GTNLItemList.HVParallelControllerCore.get(1))
            .fluidInputs(Materials.EnergeticAlloy.getMolten(1296))
            .duration(400)
            .eut(480)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                ItemList.Hull_EV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Titanium, 32L),
                ItemList.Electric_Motor_EV.get(8L),
                ItemList.Robot_Arm_EV.get(8L),
                ItemList.Emitter_EV.get(4L),
                ItemList.Sensor_EV.get(4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 12L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Aluminium, 32L))
            .itemOutputs(GTNLItemList.EVParallelControllerCore.get(1))
            .fluidInputs(Materials.TPV.getMolten(1296))
            .duration(400)
            .eut(1920)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                ItemList.Hull_IV.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.TungstenSteel, 16L),
                ItemList.Electric_Motor_IV.get(16L),
                ItemList.Robot_Arm_IV.get(12L),
                ItemList.Emitter_IV.get(8L),
                ItemList.Sensor_IV.get(8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 12L),
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Tungsten, 32L))
            .itemOutputs(GTNLItemList.IVParallelControllerCore.get(1))
            .fluidInputs(Materials.HSSG.getMolten(1296))
            .duration(400)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                GregtechItemList.Hatch_Solidifier_I.get(1),
                GTNLItemList.NinefoldInputHatchIV.get(1),
                ItemList.Automation_ChestBuffer_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.TungstenSteel, 4L),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 1, 0))
            .itemOutputs(GTNLItemList.HumongousSolidifierHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(600)
            .eut(30720)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 2L),
                GregtechItemList.Hatch_Input_Cryotheum.get(1),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 2L),
                GregtechItemList.GTFluidTank_HV.get(1),
                MaterialsAlloy.MARAGING250.getPlateDouble(4))
            .itemOutputs(GTNLItemList.FluidIceInputHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .duration(200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 2L),
                GregtechItemList.Hatch_Input_Pyrotheum.get(1),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 2L),
                GregtechItemList.GTFluidTank_HV.get(1),
                MaterialsAlloy.MARAGING350.getPlateDouble(4))
            .itemOutputs(GTNLItemList.FluidBlazeInputHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .duration(200)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_AlloyBlastSmelter.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                MaterialsAlloy.ZIRCONIUM_CARBIDE.getPlateDouble(8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.HSSG, 8L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Tungsten, 8L),
                ItemList.IV_Coil.get(2))
            .itemOutputs(GTNLItemList.AlloyBlastSmelter.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(100)
            .eut(7680)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(CI.getEmptyCatalyst(1), GTNLMaterials.ZnFeAlCl.get(OrePrefixes.dust, 16))
            .itemOutputs(GTNLItemList.ZnFeAlClCatalyst.get(1))
            .duration(392)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.TungstenCarbide, 2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenCarbide, 2L),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 2, 0),
                GTOreDictUnificator.get(OrePrefixes.spring, Materials.Europium, 1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 1L),
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Platinum, 1L))
            .itemOutputs(GTNLItemList.BlackLight.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemUtils.getItemStack(
                    Mods.EnderIO.ID,
                    "blockCapBank",
                    0,
                    0,
                    "{storedEnergyRF:2500000,type:\"CREATIVE\"}",
                    null),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "draconium", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Mods.DraconicEvolution.ID, "draconium", 1, 2))
            .duration(20)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_WashPlant.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Osmium, 4L),
                ItemList.Conveyor_Module_IV.get(4),
                ItemList.Electric_Pump_IV.get(4),
                MaterialsAlloy.TALONITE.getFrameBox(4),
                MaterialsAlloy.LEAGRISIUM.getPlateDouble(4))
            .itemOutputs(GTNLItemList.LargeChemicalBath.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Machine_IV_Autoclave.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L),
                ItemList.Electric_Pump_IV.get(4),
                ItemList.Electric_Piston_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                new ItemStack(bw_realglas, 4, 2))
            .itemOutputs(GTNLItemList.LargeAutoclave.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Machine_IV_FluidSolidifier.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.TungstenSteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.TungstenSteel, 4L),
                ItemList.Electric_Piston_IV.get(4),
                MaterialsAlloy.INCONEL_792.getFrameBox(4),
                new ItemStack(bw_realglas, 4, 2))
            .itemOutputs(GTNLItemList.LargeSolidifier.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_IV_Extractor.get(1),
                ItemList.Machine_IV_FluidExtractor.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                ItemList.Electric_Piston_IV.get(4),
                ItemList.Electric_Pump_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Tungsten, 4L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4L),
                new ItemStack(bw_realglas, 4, 2))
            .itemOutputs(GTNLItemList.LargeExtractor.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.Machine_Multi_Infuser.get(1),
                GregtechItemList.TransmissionComponent_UV.get(4),
                ItemList.Field_Generator_UV.get(2),
                tectech.thing.CustomItemList.eM_Coil.get(8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 16L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUV, 8L))
            .itemOutputs(GTNLItemList.EnergyInfuser.get(1))
            .fluidInputs(Materials.CosmicNeutronium.getMolten(2304))
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Machine_IV_Canner.get(1),
                ItemList.Machine_IV_FluidCanner.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.TungstenSteel, 4L),
                ItemList.Electric_Piston_IV.get(4),
                ItemList.Electric_Pump_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.TungstenSteel, 4L),
                new ItemStack(bw_realglas, 4, 2))
            .itemOutputs(GTNLItemList.LargeCanning.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                LanthItemList.DIGESTER,
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.TungstenSteel, 2L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Platinum, 4L),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 4L),
                ItemList.Electric_Motor_IV.get(2),
                ItemList.Electric_Pump_IV.get(2),
                ItemList.Super_Tank_IV.get(1))
            .itemOutputs(GTNLItemList.Digester.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Controller_IndustrialForgeHammer.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                ItemList.Electric_Piston_IV.get(4),
                MaterialsAlloy.INCOLOY_DS.getFrameBox(4),
                MaterialsAlloy.ENERGYCRYSTAL.getPlateDouble(4),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Osmium, 4L),
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockDarkSteelAnvil", 4, 0))
            .itemOutputs(GTNLItemList.LargeHammer.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_DataAccess_EV.get(1),
                ItemList.Super_Chest_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2L),
                ItemList.Tool_DataOrb.get(16))
            .itemOutputs(GTNLItemList.SuperDataAccessHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Machine_Adv_DistillationTower.get(1),
                GregtechItemList.GTPP_Casing_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorLuV, 8L),
                ItemList.Electric_Pump_LuV.get(4),
                new ItemStack(bw_realglas, 4, 3))
            .itemOutputs(GTNLItemList.LargeDistillery.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Controller_Vacuum_Furnace.get(1),
                CI.getEnergyCore(6, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4L),
                ItemList.Robot_Arm_LuV.get(2),
                ItemList.Field_Generator_LuV.get(1),
                MaterialsAlloy.ZERON_100.getFrameBox(4),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.YttriumBariumCuprate, 4L),
                MaterialsAlloy.LEAGRISIUM.getPlateDouble(8))
            .itemOutputs(GTNLItemList.VacuumDryingFurnace.get(1))
            .fluidInputs(MaterialsAlloy.LAFIUM.getFluidStack(4608))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                new ItemStack(GregTechAPI.sBlockMachines, 2, MetaTileEntityIDs.BioVat.ID),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 8L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 16L),
                ItemList.Robot_Arm_EV.get(8),
                ItemList.Emitter_EV.get(8),
                ItemList.Field_Generator_HV.get(4),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Aluminium, 8L))
            .itemOutputs(GTNLItemList.Incubator.get(1))
            .fluidInputs(Materials.HSSG.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_IndustrialLaserEngraver.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4L),
                ItemList.Energy_LapotronicOrb2.get(2),
                ItemList.Field_Generator_LuV.get(2),
                ItemList.Robot_Arm_LuV.get(4),
                ItemList.Emitter_LuV.get(4),
                WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.plateDouble, 16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSS, 8L),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorLuV, 4L))
            .itemOutputs(GTNLItemList.LargeEngravingLaser.get(1))
            .fluidInputs(Materials.NaquadahAlloy.getMolten(576))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Amazon_Warehouse_Controller.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GregtechItemList.Casing_AmazonWarehouse.get(4),
                MaterialsAlloy.HASTELLOY_C276.getPlateDouble(4),
                ItemList.Robot_Arm_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Platinum, 4L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenCarbide, 4L))
            .itemOutputs(GTNLItemList.LargePacker.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_AlloySmelter.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Graphene, 4L),
                MaterialsAlloy.INCOLOY_MA956.getPlateDouble(4),
                MaterialsAlloy.TANTALUM_CARBIDE.getGear(4),
                ItemList.Field_Generator_IV.get(2),
                MaterialsAlloy.INCONEL_792.getFrameBox(4))
            .itemOutputs(GTNLItemList.LargeAlloySmelter.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Generator_Naquadah_Mark_II.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2L),
                ItemList.Electric_Pump_ZPM.get(8),
                ItemList.Field_Generator_ZPM.get(2),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Naquadah, 4L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 8L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 16L))
            .itemOutputs(GTNLItemList.LargeNaquadahReactor.get(1))
            .fluidInputs(Materials.Trinium.getMolten(2304))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemRefer.Precise_Assembler.get(1),
                ItemRefer.HiC_T2.get(4),
                ItemList.Tool_DataOrb.get(8),
                GGMaterial.hikarium.get(OrePrefixes.wireFine, 16),
                ItemList.Robot_Arm_IV.get(8),
                GGMaterial.marM200.get(OrePrefixes.plateDouble, 4),
                GGMaterial.lumiium.get(OrePrefixes.gearGtSmall, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmiridium, 4L),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.TungstenSteel, 4L))
            .itemOutputs(GTNLItemList.PreciseAssembler.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.dataOut_Hatch.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 1L),
                ItemList.Sensor_ZPM.get(1))
            .itemOutputs(tectech.thing.CustomItemList.dataOut_Wireless_Hatch.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(576))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.dataIn_Hatch.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 1L),
                ItemList.Emitter_ZPM.get(1))
            .itemOutputs(tectech.thing.CustomItemList.dataIn_Wireless_Hatch.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(576))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.dataOutAss_Hatch.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 1L),
                ItemList.Sensor_ZPM.get(1))
            .itemOutputs(tectech.thing.CustomItemList.dataOutAss_Wireless_Hatch.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(576))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.dataInAss_Hatch.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 1L),
                ItemList.Emitter_ZPM.get(1))
            .itemOutputs(tectech.thing.CustomItemList.dataInAss_Wireless_Hatch.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(576))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.TungstenSteel, 2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 4L),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.TungstensteelGearbox.get(1))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.DraconiumAwakened, 1L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.DraconiumAwakened, 6L),
                ItemList.Field_Generator_ZPM.get(1))
            .itemOutputs(GTNLItemList.PressureBalancedCasing.get(1))
            .fluidInputs(Materials.Draconium.getMolten(576))
            .duration(800)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.AlgaeFarm_Controller.get(1),
                GregtechItemList.Industrial_FishingPond.get(1),
                ItemList.Field_Generator_IV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.SuperconductorIV, 4L),
                ItemList.AcceleratorIV.get(2),
                GregtechItemList.GTPP_Casing_EV.get(4))
            .itemOutputs(GTNLItemList.FishingGround.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                tectech.thing.CustomItemList.eM_Hollow.get(2),
                GTModHandler.getModItem(Mods.DraconicEvolution.ID, "draconicCore", 1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.DraconiumAwakened, 4),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Mytryl, 4),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Plutonium, 4),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Quantium, 4),
                ItemList.Field_Generator_UV.get(2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 1))
            .itemOutputs(GTNLItemList.DimensionallyStableCasing.get(1))
            .fluidInputs(MaterialsElements.STANDALONE.DRAGON_METAL.getFluidStack(576))
            .duration(100)
            .eut(TierEU.RECIPE_UHV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                ItemList.Casing_StableTitanium.get(1),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Titanium, 2),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 4))
            .itemOutputs(ItemList.Casing_EngineIntake.get(4))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                ItemList.Machine_Multi_BlastFurnace.get(1),
                ItemList.Robot_Arm_HV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 3L),
                ItemList.Casing_CleanStainlessSteel.get(1),
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 2L))
            .itemOutputs(GTNLItemList.ElectricBlastFurnace.get(1))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                ItemList.Casing_Chemically_Inert.get(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Naquadah, 1L),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Europium, 6),
                GTOreDictUnificator.get(OrePrefixes.pipeSmall, Materials.NetherStar, 4),
                ItemList.Electric_Pump_ZPM.get(8))
            .fluidInputs(Materials.SuperCoolant.getFluid(8000))
            .itemOutputs(GTNLItemList.ChemicallyResistantCasing.get(1))
            .fluidOutputs()
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 8L),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 32),
                GTUtility.getIntegratedCircuit(6))
            .fluidInputs(GTNLMaterials.Polyetheretherketone.getMolten(36))
            .itemOutputs(ItemList.Circuit_Board_Coated_Basic.get(64))
            .fluidOutputs()
            .duration(1600)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_LV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 1),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsBotania.Manasteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.RedstoneAlloy, 2))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaDynamoHatchLV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_HV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 2),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsBotania.ElvenElementium, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.SuperconductorHV, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaDynamoHatchHV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_IV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsBotania.ElvenElementium, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.SuperconductorIV, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaDynamoHatchIV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_IV.get(2),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 8),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsBotania.Terrasteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.SuperconductorZPM, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaDynamoHatchZPM.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_LV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 1),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.Manasteel, 6L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.RedstoneAlloy, 2))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaEnergyHatchLV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_HV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 2),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.ElvenElementium, 6L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorHV, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaEnergyHatchHV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_IV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 4),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.ElvenElementium, 6L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorIV, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaEnergyHatchIV.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "spark", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 1, 8),
                ItemList.Super_Tank_IV.get(2),
                GTModHandler.getModItem(Mods.Botania.ID, "pump", 8),
                GTModHandler.getModItem(Mods.Botania.ID, "rfGenerator", 8),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.Terrasteel, 6L),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorZPM, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.ManaEnergyHatchZPM.get(1))
            .duration(160)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.GasCollectorMV.get(1),
                GTNLItemList.GasCollectorHV.get(1),
                GTNLItemList.GasCollectorEV.get(1),
                ItemList.Field_Generator_MV.get(2),
                ItemList.Field_Generator_HV.get(2),
                ItemList.Field_Generator_EV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 2L),
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.NiobiumTitanium, 1L),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.SterlingSilver, 4L))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.LargeGasCollector.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.LVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchLV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.MVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchMV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.HVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchHV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.EVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchEV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.IVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchIV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.LuVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchLuV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.ZPMParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchZPM.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UHVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUHV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UHV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UEVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUEV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UEV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UIVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUIV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UIV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UMVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUMV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UMV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.UXVParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchUXV.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_UXV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(24), GTNLItemList.MAXParallelControllerCore.get(1))
            .itemOutputs(GTNLItemList.ParallelControllerHatchMAX.get(1))
            .duration(20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                new ItemStack(Blocks.torch, 64),
                new ItemStack(Blocks.torch, 64),
                new ItemStack(Blocks.torch, 64),
                new ItemStack(Blocks.torch, 64),
                new ItemStack(Blocks.torch, 64),
                new ItemStack(Blocks.torch, 64))
            .itemOutputs(GTNLItemList.InfinityTorch.get(1))
            .duration(200)
            .eut(TierEU.MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6), new ItemStack(Items.paper, 7))
            .itemOutputs(GTNLItemList.CardboardBox.get(1))
            .fluidInputs(Materials.Glue.getFluid(200))
            .duration(20)
            .eut(16)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Void_Bus.get(1),
                ItemList.Automation_Filter_LV.get(1),
                ItemList.ItemFilter_Export.get(1))
            .itemOutputs(GTNLItemList.SuperVoidBus.get(1))
            .fluidInputs(Materials.Tin.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Hatch_Void.get(1), ItemList.Automation_Filter_LV.get(1), ItemList.FluidFilter.get(1))
            .itemOutputs(GTNLItemList.SuperVoidHatch.get(1))
            .fluidInputs(Materials.Tin.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                new ItemStack(Blocks.brick_block, 64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 48),
                GTNLItemList.HydraulicSteamJetSpewer.get(8),
                GTNLItemList.PrecisionSteamMechanism.get(16))
            .itemOutputs(GTNLItemList.SteamElevator.get(1))
            .fluidInputs(GTNLMaterials.Stronze.getMolten(1296))
            .duration(600)
            .eut(30)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                ItemList.Component_Grinder_Diamond.get(16),
                GTNLItemList.HydraulicMotor.get(32),
                GTNLItemList.HydraulicPiston.get(32),
                GTNLItemList.HydraulicPump.get(32),
                GTNLItemList.HydraulicConveyor.get(32))
            .itemOutputs(GTNLItemList.SteamOreProcessorModule.get(1))
            .fluidInputs(GTNLMaterials.Breel.getMolten(1296))
            .duration(600)
            .eut(30)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(1),
                GTNLItemList.HydraulicSteamJetSpewer.get(4),
                GTNLItemList.CompressedSteamTurbine.get(4),
                GTNLItemList.HydraulicSteamReceiver.get(2),
                GTNLItemList.PrecisionSteamMechanism.get(2),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "ItemBaubleBlanks", 1, 1))
            .itemOutputs(GTNLItemList.SteamFlightModule.get(1))
            .duration(400)
            .eut(100)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(1),
                new ItemStack(Items.brewing_stand, 1),
                GTNLItemList.HydraulicPump.get(4),
                GTNLItemList.HydraulicSteamJetSpewer.get(8),
                GTNLItemList.IronTurbine.get(8),
                new ItemStack(Items.gunpowder, 64))
            .itemOutputs(GTNLItemList.SteamBeaconModuleI.get(1))
            .duration(400)
            .eut(16)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamBeaconModuleI.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ULV, 2),
                GTNLItemList.HydraulicSteamReceiver.get(4),
                GTNLItemList.HydraulicRegulator.get(8),
                new ItemStack(Items.ender_pearl, 16),
                GTNLItemList.SteelTurbine.get(8))
            .fluidInputs(Materials.Tin.getMolten(1296))
            .itemOutputs(GTNLItemList.SteamBeaconModuleII.get(1))
            .duration(300)
            .eut(28)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamBeaconModuleII.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                GTNLItemList.HydraulicVaporGenerator.get(4),
                GTNLItemList.HydraulicRegulator.get(8),
                GTNLItemList.CompressedSteamTurbine.get(8),
                new ItemStack(Items.blaze_powder, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .itemOutputs(GTNLItemList.SteamBeaconModuleIII.get(1))
            .duration(200)
            .eut(100)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Hatch_Void_Bus.get(1), ItemList.Hatch_Void.get(1))
            .itemOutputs(GTNLItemList.VoidCover.get(1))
            .fluidInputs(Materials.Tin.getMolten(144))
            .duration(100)
            .eut(TierEU.LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Cover_Drain.get(1), GregtechItemList.Hatch_Reservoir.get(1))
            .itemOutputs(GTNLItemList.WaterCover.get(1))
            .duration(100)
            .eut(TierEU.LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                GTModHandler.getModItem(Mods.Forestry.ID, "alveary", 2, 0),
                GTModHandler.getModItem(Mods.Forestry.ID, "alveary", 2, 2),
                GTModHandler.getModItem(Mods.Forestry.ID, "alveary", 2, 6),
                GTModHandler.getModItem(Mods.Forestry.ID, "royalJelly", 4),
                GTModHandler.getModItem(Mods.Forestry.ID, "beeswax", 8),
                GTModHandler.getModItem(Mods.Forestry.ID, "pollen", 2),
                ItemList.IndustrialApiary_Upgrade_FLOWERING.get(2))
            .fluidInputs(Materials.Honey.getFluid(10000L))
            .itemOutputs(GTNLItemList.SteamBeeBreedingModule.get(1))
            .duration(300)
            .eut(TierEU.MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                GTModHandler.getModItem(Mods.Forestry.ID, "alveary", 2, 0),
                ItemList.IndustrialApiary_Upgrade_PRODUCTION.get(4),
                ItemList.IndustrialApiary_Upgrade_Acceleration_1.get(4),
                ItemList.IndustrialApiary_Upgrade_AUTOMATION.get(4))
            .fluidInputs(Materials.Honey.getFluid(10000L))
            .itemOutputs(GTNLItemList.SteamApiaryModule.get(1))
            .duration(300)
            .eut(TierEU.LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                GTModHandler.getModItem(Mods.ExtraUtilities.ID, "spike_base_diamond", 9, 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTNLItemList.HydraulicPiston.get(8),
                GTNLItemList.HydraulicConveyor.get(8))
            .fluidInputs(FluidRegistry.getFluidStack("xpjuice", 10000))
            .itemOutputs(GTNLItemList.SteamEntityCrusherModule.get(1))
            .duration(300)
            .eut(TierEU.LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME.get(1),
                aeParts.storageBus()
                    .maybeStack(1)
                    .orNull(),
                ItemList.Conveyor_Module_EV.get(2),
                aeMaterials.cardCapacity()
                    .maybeStack(9)
                    .orNull())
            .itemOutputs(GTNLItemList.SuperInputBusME.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(300)
            .eut(TierEU.EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SuperInputBusME.get(1),
                aeBlocks.chest()
                    .maybeStack(1)
                    .orNull(),
                ItemList.Conveyor_Module_ZPM.get(2),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(4)
                    .orNull())
            .itemOutputs(GTNLItemList.AdvancedSuperInputBusME.get(1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(1296))
            .duration(300)
            .eut(TierEU.ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_HV.get(1),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_interface", 1),
                aeMaterials.cardSpeed()
                    .maybeStack(4)
                    .orNull())
            .itemOutputs(ItemList.Hatch_Input_ME.get(1))
            .duration(300)
            .eut(TierEU.HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_ME.get(1),
                GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "part_fluid_storage_bus", 1),
                ItemList.Electric_Pump_EV.get(2),
                aeMaterials.cardCapacity()
                    .maybeStack(9)
                    .orNull())
            .itemOutputs(GTNLItemList.SuperInputHatchME.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(300)
            .eut(TierEU.EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SuperInputHatchME.get(1),
                aeBlocks.chest()
                    .maybeStack(2)
                    .orNull(),
                ItemList.Electric_Pump_ZPM.get(2),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(6)
                    .orNull())
            .itemOutputs(GTNLItemList.AdvancedSuperInputHatchME.get(1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(1296))
            .duration(300)
            .eut(TierEU.ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1L),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4L),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.StainlessSteelGearBox.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.GT4_Crop_Harvester_LV.get(2),
                new ItemStack(Blocks.dirt, 64),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockCrop", 64),
                GTModHandler.getModItem(Mods.Forestry.ID, "ffarm", 16, 4),
                GTModHandler.getModItem(Mods.Forestry.ID, "ffarm", 16, 2),
                GTModHandler.getModItem(Mods.Forestry.ID, "ffarm", 4, 5))
            .itemOutputs(GTNLItemList.SteamGreenhouseModule.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sunshine", 16000))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_HV.get(1),
                ItemList.Conveyor_Module_HV.get(4),
                ItemList.Robot_Arm_HV.get(2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plateDense, 4),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.screw, 16),
                ItemList.Hatch_Input_Bus_HV.get(1),
                ItemList.Hatch_Output_Bus_HV.get(1))
            .itemOutputs(GTNLItemList.VaultPortHatch.get(1))
            .fluidInputs(GTNLMaterials.Stronze.getMolten(4608))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.VibrationSafeCasing.get(16),
                ItemList.Super_Chest_LV.get(8),
                GTNLItemList.HydraulicSteamJetSpewer.get(4),
                GTNLItemList.CompressedSteamTurbine.get(4),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 16),
                ItemList.Cover_Screen.get(1))
            .itemOutputs(GTNLItemList.SteamItemVault.get(1))
            .fluidInputs(GTNLMaterials.Stronze.getMolten(9216))
            .duration(600)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchLV.get(1),
                ItemList.Super_Chest_LV.get(1),
                ItemList.Super_Tank_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Steel, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchLV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchMV.get(1),
                ItemList.Super_Chest_MV.get(1),
                ItemList.Super_Tank_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Aluminium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchMV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchHV.get(1),
                ItemList.Super_Chest_HV.get(1),
                ItemList.Super_Tank_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.StainlessSteel, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchHV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchEV.get(1),
                ItemList.Super_Chest_EV.get(1),
                ItemList.Super_Tank_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Titanium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchEV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchIV.get(1),
                ItemList.Super_Chest_IV.get(1),
                ItemList.Super_Tank_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.TungstenSteel, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchIV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchLuV.get(1),
                ItemList.Quantum_Chest_LV.get(1),
                ItemList.Quantum_Tank_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iridium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchLuV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchZPM.get(1),
                ItemList.Quantum_Chest_MV.get(1),
                ItemList.Quantum_Tank_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchZPM.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUV.get(1),
                ItemList.Quantum_Chest_HV.get(1),
                ItemList.Quantum_Tank_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Osmium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUHV.get(1),
                ItemList.Quantum_Chest_EV.get(1),
                ItemList.Quantum_Tank_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUHV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UHV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUEV.get(1),
                ItemList.Quantum_Chest_IV.get(1),
                ItemList.Quantum_Tank_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Bedrockium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUEV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UEV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUIV.get(1),
                ItemList.Quantum_Chest_IV.get(2),
                ItemList.Quantum_Tank_IV.get(2),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.BlackPlutonium, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUIV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUMV.get(1),
                ItemList.Quantum_Chest_IV.get(4),
                ItemList.Quantum_Tank_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.SpaceTime, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUMV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UMV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchUXV.get(1),
                ItemList.Quantum_Chest_IV.get(8),
                ItemList.Quantum_Tank_IV.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateDense, MaterialsUEVplus.MagMatter, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchUXV.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_UXV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DualInputHatchMAX.get(1),
                ItemList.Quantum_Chest_IV.get(16),
                ItemList.Quantum_Tank_IV.get(16),
                GTOreDictUnificator
                    .get(OrePrefixes.plateDense, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 4L),
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .itemOutputs(GTNLItemList.HumongousDualInputHatchMAX.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .duration(300)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                GTNLItemList.BreelPlatedCasing.get(12),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                GTNLItemList.HydraulicMotor.get(8),
                GTNLItemList.HydraulicPump.get(12),
                GTNLItemList.SteelTurbine.get(12))
            .itemOutputs(GTNLItemList.SteamOilDrillModuleI.get(1))
            .duration(300)
            .eut(28)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamOilDrillModuleI.get(1),
                GTNLItemList.HyperPressureBreelCasing.get(12),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 4),
                GTNLItemList.HydraulicPump.get(4),
                GTNLItemList.HydraulicRegulator.get(6),
                GTNLItemList.CompressedSteamTurbine.get(12))
            .itemOutputs(GTNLItemList.SteamOilDrillModuleII.get(1))
            .duration(300)
            .eut(80)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamOilDrillModuleII.get(1),
                GTNLItemList.HyperPressureBreelCasing.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 4),
                GTNLItemList.HydraulicRegulator.get(8),
                GTNLItemList.HydraulicVaporGenerator.get(3),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 12))
            .itemOutputs(GTNLItemList.SteamOilDrillModuleIII.get(1))
            .duration(300)
            .eut(300)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Casing_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2),
                ItemList.Electric_Pump_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.Titaniumonabariumdecacoppereikosaoxid, 1),
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Nichrome, 1))
            .itemOutputs(GTNLItemList.ElectrocellGenerator.get(1))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Energy_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2))
            .itemOutputs(GTNLItemList.EnergyHatchLV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_LV_ULV.get(1),
                GTNLItemList.EnergyHatchLV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(GTNLItemList.EnergyHatchLV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_LV_ULV.get(1),
                GTNLItemList.EnergyHatchLV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(GTNLItemList.EnergyHatchLV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Energy_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 2))
            .itemOutputs(GTNLItemList.EnergyHatchMV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_MV_LV.get(1),
                GTNLItemList.EnergyHatchMV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 4))
            .itemOutputs(GTNLItemList.EnergyHatchMV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_MV_LV.get(1),
                GTNLItemList.EnergyHatchMV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 6))
            .itemOutputs(GTNLItemList.EnergyHatchMV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Energy_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 2))
            .itemOutputs(GTNLItemList.EnergyHatchHV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_HV_MV.get(1),
                GTNLItemList.EnergyHatchHV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .itemOutputs(GTNLItemList.EnergyHatchHV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_HV_MV.get(1),
                GTNLItemList.EnergyHatchHV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 6))
            .itemOutputs(GTNLItemList.EnergyHatchHV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.EnergyHatchMAX.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 2))
            .itemOutputs(GTNLItemList.EnergyHatchMAX4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_MAX_UXV.get(1),
                GTNLItemList.EnergyHatchMAX4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 4))
            .itemOutputs(GTNLItemList.EnergyHatchMAX16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_MAX_UXV.get(1),
                GTNLItemList.EnergyHatchMAX16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 6))
            .itemOutputs(GTNLItemList.EnergyHatchMAX64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Dynamo_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2))
            .itemOutputs(GTNLItemList.DynamoHatchLV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_LV_ULV.get(1),
                GTNLItemList.DynamoHatchLV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(GTNLItemList.DynamoHatchLV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_LV_ULV.get(1),
                GTNLItemList.DynamoHatchLV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(GTNLItemList.DynamoHatchLV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Dynamo_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 2))
            .itemOutputs(GTNLItemList.DynamoHatchMV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_MV_LV.get(1),
                GTNLItemList.DynamoHatchMV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 4))
            .itemOutputs(GTNLItemList.DynamoHatchMV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_MV_LV.get(1),
                GTNLItemList.DynamoHatchMV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 6))
            .itemOutputs(GTNLItemList.DynamoHatchMV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Dynamo_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 2))
            .itemOutputs(GTNLItemList.DynamoHatchHV4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_HV_MV.get(1),
                GTNLItemList.DynamoHatchHV4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .itemOutputs(GTNLItemList.DynamoHatchHV16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_HV_MV.get(1),
                GTNLItemList.DynamoHatchHV16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.Gold, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 6))
            .itemOutputs(GTNLItemList.DynamoHatchHV64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.DynamoHatchMAX.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 2))
            .itemOutputs(GTNLItemList.DynamoHatchMAX4A.get(1))
            .fluidInputs(Materials.Silver.getMolten(144))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Transformer_MAX_UXV.get(1),
                GTNLItemList.DynamoHatchMAX4A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 4))
            .itemOutputs(GTNLItemList.DynamoHatchMAX16A.get(1))
            .fluidInputs(Materials.Electrum.getMolten(144))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.WetTransformer_MAX_UXV.get(1),
                GTNLItemList.DynamoHatchMAX16A.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt12, Materials.DraconiumAwakened, 2),
                GTOreDictUnificator
                    .get(OrePrefixes.plate, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 6))
            .itemOutputs(GTNLItemList.DynamoHatchMAX64A.get(1))
            .fluidInputs(Materials.Tungsten.getMolten(144))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.ShimmerBucket.get(1),
                GTModHandler.getModItem(Mods.OpenComputers.ID, "disassembler", 64))
            .itemOutputs(GTNLItemList.InfinityShimmerBucket.get(1))
            .fluidInputs(new FluidStack(BlockLoader.shimmerFluid, 100000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.HoneyBucket.get(1),
                GTModHandler.getModItem(Mods.PamsHarvestCraft.ID, "royaljellyItem", 64))
            .itemOutputs(GTNLItemList.InfinityHoneyBucket.get(1))
            .fluidInputs(new FluidStack(BlockLoader.honeyFluid, 100000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.water_bucket, 1), GregtechItemList.Hatch_Reservoir.get(1))
            .itemOutputs(GTNLItemList.InfinityWaterBucket.get(1))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.lava_bucket, 1),
                GTModHandler.getModItem(Mods.ThaumicExploration.ID, "everburnUrn", 1))
            .itemOutputs(GTNLItemList.InfinityLavaBucket.get(1))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                tectech.thing.CustomItemList.hatch_CreativeMaintenance.get(4),
                ItemList.WetTransformer_LuV_IV.get(4),
                ItemList.Field_Generator_IV.get(4),
                ItemList.Sensor_IV.get(8),
                ItemList.Emitter_IV.get(8),
                ItemList.Robot_Arm_IV.get(16),
                ItemList.Electric_Piston_IV.get(16),
                ItemList.Electric_Pump_IV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 16))
            .itemOutputs(GTNLItemList.AutoConfigurationMaintenanceHatch.get(1))
            .fluidInputs(Materials.TungstenSteel.getMolten(9216))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_DroneDownLink.get(4),
                ItemList.WetTransformer_LuV_IV.get(4),
                ItemList.Field_Generator_IV.get(4),
                ItemList.Sensor_IV.get(8),
                ItemList.Emitter_IV.get(8),
                ItemList.Robot_Arm_IV.get(16),
                ItemList.Electric_Piston_IV.get(16),
                ItemList.Electric_Pump_IV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 16))
            .itemOutputs(GTNLItemList.ConfigurationDroneDownLinkHatch.get(1))
            .fluidInputs(Materials.TungstenSteel.getMolten(9216))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Super_Tank_EV.get(1),
                GTModHandler.getModItem(Mods.Botania.ID, "pool", 4, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "spreader", 2, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "corporeaSpark", 4, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "pistonRelay", 4),
                GTModHandler.getModItem(Mods.Botania.ID, "manaBeacon", 1),
                ItemRefer.Fluid_Storage_Core_T2.get(2))
            .itemOutputs(GTNLItemList.ManaTank.get(1))
            .fluidInputs(MaterialsBotania.Terrasteel.getMolten(576))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.SiliconSG, 2L),
                new ItemStack(Items.skull, 1, 2),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemBasicCapacitor", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 0))
            .fluidInputs(Materials.EnergeticAlloy.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.SiliconSG, 2L),
                new ItemStack(Items.skull, 1, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.RedAlloy, 1L))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 1))
            .fluidInputs(Materials.Soularium.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.VividAlloy, 2L),
                new ItemStack(Items.skull, 1, 2),
                new ItemStack(Items.rotten_flesh, 64))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 2))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.SiliconSG, 2L),
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockEndermanSkull", 1, 0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.VibrantAlloy, 1L))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 3))
            .fluidInputs(Materials.Soularium.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 3),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.VividAlloy, 2L),
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockEndermanSkull", 1, 0),
                new ItemStack(Items.ender_pearl, 32))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 4))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.SiliconSG, 1L),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Diamond, 1L),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemMaterial", 2, 5))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "itemFrankenSkull", 1, 6))
            .fluidInputs(Materials.EnergeticAlloy.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.SiliconSG, 2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.VividAlloy, 2L),
                new ItemStack(Items.skull, 1, 2),
                new ItemStack(Items.ender_pearl, 16),
                new ItemStack(Items.ender_eye, 2))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "blockEndermanSkull", 1, 0))
            .fluidInputs(Materials.Soularium.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.VividAlloy, 2L),
                GTModHandler.getModItem(Mods.EnderIO.ID, "blockEndermanSkull", 1, 0),
                GTModHandler.getModItem(Mods.EnderIO.ID, "itemBasicCapacitor", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Mods.EnderIO.ID, "blockEndermanSkull", 1, 2))
            .fluidInputs(Materials.Soularium.getMolten(288))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                ItemList.Robot_Arm_LV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2L),
                new ItemStack(Blocks.redstone_block, 2),
                new ItemStack(Items.diamond_pickaxe, 1),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Steel, 2),
                GregtechItemList.TransmissionComponent_LV.get(1))
            .itemOutputs(GTNLItemList.VeinMiningPickaxe.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                ItemList.Casing_RobustTungstenSteel.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .itemOutputs(GTNLItemList.AssemblerMatrixWall.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.AssemblerMatrixWall.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmiridium, 4),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 4))
            .itemOutputs(GTNLItemList.AssemblerMatrixFrame.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTNLItemList.AssemblerMatrixFrame.get(2),
                ItemList.LargeMolecularAssembler.get(1),
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Hatch_Output_Bus_ME.get(1),
                ItemList.Field_Generator_IV.get(4))
            .itemOutputs(GTNLItemList.AssemblerMatrix.get(1))
            .duration(600)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.AssemblerMatrixWall.get(1),
                ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(8)
                    .orNull(),
                aeMaterials.engProcessor()
                    .maybeStack(16)
                    .orNull(),
                ItemList.Conveyor_Module_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1),
                aeItems.coloredPaintBall()
                    .stack(AEColor.Blue, 64))
            .itemOutputs(GTNLItemList.AssemblerMatrixPatternCore.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.AssemblerMatrixWall.get(1),
                aeBlocks.molecularAssembler()
                    .maybeStack(8)
                    .orNull(),
                aeMaterials.cardSpeed()
                    .maybeStack(40)
                    .orNull(),
                aeMaterials.logicProcessor()
                    .maybeStack(16)
                    .orNull(),
                ItemList.Robot_Arm_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1),
                aeItems.coloredPaintBall()
                    .stack(AEColor.Purple, 64))
            .itemOutputs(GTNLItemList.AssemblerMatrixCrafterCore.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.QuantumComputerCasing.get(4),
                GTNLItemList.QuantumComputerUnit.get(4),
                aeMaterials.cell16SpatialPart()
                    .maybeStack(4)
                    .orNull(),
                aeBlocks.controller()
                    .maybeStack(4)
                    .orNull(),
                GTNLItemList.ShatteredSingularity.get(4),
                ItemList.Emitter_LuV.get(4),
                ItemList.Sensor_LuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4))
            .itemOutputs(GTNLItemList.QuantumComputer.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(32000))
            .duration(600)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GregtechItemList.Industrial_MassFab.get(1),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 4),
                GregtechItemList.Casing_Containment.get(24),
                ItemList.Emitter_ZPM.get(1),
                ItemList.Sensor_ZPM.get(1),
                GregtechItemList.Casing_MatterGen.get(9),
                GregtechItemList.DehydratorCoilWireZPM.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 8))
            .itemOutputs(GTNLItemList.MassFabricator.get(1))
            .fluidInputs(GTNLMaterials.Polyetheretherketone.getMolten(1152))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.GalacticraftCore.ID, "tile.rocketWorkbench", 1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 16),
                ItemList.Casing_CleanStainlessSteel.get(8),
                ItemList.Conveyor_Module_HV.get(4),
                ItemList.Robot_Arm_HV.get(4),
                GTModHandler.getModItem(Mods.GalacticraftCore.ID, "item.basicItem", 1, 14),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 4))
            .itemOutputs(GTNLItemList.RocketAssembler.get(1))
            .fluidInputs(Materials.Polytetrafluoroethylene.getMolten(1152))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(1),
                GTNLItemList.IronTurbine.get(8),
                new ItemStack(Blocks.torch, 64),
                GTNLItemList.HydraulicSteamJetSpewer.get(2),
                GTNLItemList.HydraulicMotor.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2))
            .itemOutputs(GTNLItemList.SteamMonsterRepellentModuleI.get(1))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(2),
                GTNLItemList.SteelTurbine.get(8),
                GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "soul_torch", 32),
                GTNLItemList.HydraulicSteamJetSpewer.get(2),
                GTNLItemList.HydraulicSteamReceiver.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .itemOutputs(GTNLItemList.SteamMonsterRepellentModuleII.get(1))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(4),
                GTNLItemList.CompressedSteamTurbine.get(8),
                GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "soul_torch", 64),
                GTNLItemList.HydraulicSteamJetSpewer.get(2),
                GTNLItemList.HydraulicVaporGenerator.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8))
            .itemOutputs(GTNLItemList.SteamMonsterRepellentModuleIII.get(1))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteamCompactPipeCasing.get(1),
                GTModHandler.getModItem(Mods.Natura.ID, "Cloud", 64, 1),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockCrystal", 2, 6),
                GTNLItemList.HydraulicPump.get(4),
                GTNLItemList.HydraulicSteamReceiver.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .itemOutputs(GTNLItemList.SteamWeatherModule.get(1))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                ItemList.Casing_RobustTungstenSteel.get(1),
                ItemList.Tool_DataOrb.get(4),
                ItemRefer.HiC_T2.get(2),
                GGMaterial.lumiium.get(OrePrefixes.gearGtSmall, 2),
                GGMaterial.hikarium.get(OrePrefixes.gearGtSmall, 2))
            .itemOutputs(ItemList.Casing_DataDrive.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Naquadria, 1),
                ItemList.Radiation_Proof_Prismatic_Naquadah_Composite_Sheet.get(8),
                ItemList.Field_Generator_ZPM.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Europium, 2),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Naquadria, 2),
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Naquadah, 2))
            .itemOutputs(GTNLItemList.GravitationalFocusingLensBlock.get(1))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(1152))
            .duration(100)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Energy_LuV.get(6),
                ItemList.Cover_Wireless_Energy_ZPM.get(6),
                ItemList.Casing_Coil_Superconductor.get(12L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 6),
                GTOreDictUnificator.get(OrePrefixes.spring, Materials.Naquadah, 16),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.NaquadahAlloy, 32))
            .itemOutputs(GTNLItemList.HumongousWirelessDynamoHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1440))
            .duration(800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8), ItemList.Hull_LV.get(1))
            .itemOutputs(ItemList.Hatch_Maintenance.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(As)
            .addTo(HOR);

        RecipeBuilder.builder()
            .itemInputs(
                aeBlocks.iface()
                    .maybeStack(3)
                    .orNull(),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(4)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.TungstenSteel, 2),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTNLItemList.SuperInterface.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                aeBlocks.iface()
                    .maybeStack(3)
                    .orNull(),
                aeMaterials.cardPatternCapacity()
                    .maybeStack(4)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.TungstenSteel, 2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.PartSuperInterface.get(1))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.EnderElevatorSlab.get(2))
            .itemOutputs(GTNLItemList.EnderElevatorBlock.get(2))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.EnderElevatorCarpet.get(2))
            .itemOutputs(GTNLItemList.EnderElevatorSlab.get(2))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.PartBeamFormer.get(1))
            .itemOutputs(GTNLItemList.BlockBeamFormer.get(1))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.BlockBeamFormer.get(1))
            .itemOutputs(GTNLItemList.PartBeamFormer.get(1))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                aeMaterials.fluixPearl()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.purifiedFluixCrystal()
                    .maybeStack(2)
                    .orNull(),
                aeMaterials.wireless()
                    .maybeStack(1)
                    .orNull(),
                aeParts.cableSmart()
                    .stack(AEColor.Transparent, 1))
            .itemOutputs(GTNLItemList.PartBeamFormer.get(2))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(As);

        loadLamp();
        loadWirelessHatch();
        loadLaserHatch();

        if (Mods.NewHorizonsCoreMod.isModLoaded()) loadNHRecipe();

        if (MainConfig.recipe.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadLamp() {
        String[] lampTypes = { "Lamp", "LampBorderless", "LampOff", "LampOffBorderless" };

        String[] colors = { "Black", "Pink", "Red", "Orange", "Yellow", "Green", "Lime", "Blue", "LightBlue", "Cyan",
            "Brown", "Magenta", "Purple", "Gray", "LightGray", "White" };

        for (String color : colors) {
            String fluidName = "dye.chemical.dye" + color.toLowerCase();

            for (int i = 0; i < lampTypes.length; i++) {
                String lampType = lampTypes[i];
                int circuitConfig = i + 1;

                RecipeBuilder.builder()
                    .itemInputs(
                        GTOreDictUnificator.get(OrePrefixes.plate, Materials.Glass, 6L),
                        new ItemStack(Items.glowstone_dust, 1),
                        GTUtility.getIntegratedCircuit(circuitConfig))
                    .itemOutputs(
                        GTNLItemList.valueOf(color + lampType)
                            .get(1))
                    .fluidInputs(FluidRegistry.getFluidStack(fluidName, 144))
                    .duration(40)
                    .eut(TierEU.RECIPE_LV)
                    .addTo(As);
            }
        }
    }

    public void loadWirelessHatch() {
        ItemStack[] COIL = { ItemList.LV_Coil.get(1), ItemList.MV_Coil.get(2), ItemList.HV_Coil.get(3),
            ItemList.EV_Coil.get(3), ItemList.IV_Coil.get(4), ItemList.LuV_Coil.get(8), ItemList.ZPM_Coil.get(8),
            ItemList.UV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(12),
            ItemList.UHV_Coil.get(12), ItemList.UHV_Coil.get(16), ItemList.UHV_Coil.get(32) };

        ItemStack[] COIL_4A = { ItemList.LV_Coil.get(2), ItemList.MV_Coil.get(3), ItemList.HV_Coil.get(3),
            ItemList.EV_Coil.get(3), ItemList.IV_Coil.get(4), ItemList.LuV_Coil.get(8), ItemList.ZPM_Coil.get(8),
            ItemList.UV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(12),
            ItemList.UHV_Coil.get(12), ItemList.UHV_Coil.get(32), ItemList.UHV_Coil.get(64) };

        ItemStack[] CHIP = { GTOreDictUnificator.get(OrePrefixes.spring, Materials.Tin, 16),
            ItemList.Circuit_Chip_ULPIC.get(2), ItemList.Circuit_Chip_LPIC.get(2), ItemList.Circuit_Chip_PIC.get(3),
            ItemList.Circuit_Chip_HPIC.get(3), ItemList.Circuit_Chip_UHPIC.get(4), ItemList.Circuit_Chip_NPIC.get(4),
            ItemList.Circuit_Chip_PPIC.get(6), ItemList.Circuit_Chip_QPIC.get(6), ItemList.Circuit_Chip_QPIC.get(8),
            ItemList.Circuit_Chip_QPIC.get(8), ItemList.Circuit_Chip_QPIC.get(12), ItemList.Circuit_Chip_QPIC.get(16),
            ItemList.Circuit_Chip_QPIC.get(32) };

        ItemStack[] INDUCTOR = { ItemList.Circuit_Parts_Coil.get(4), ItemList.Circuit_Parts_Coil.get(8),
            ItemList.Circuit_Parts_InductorSMD.get(4), ItemList.Circuit_Parts_InductorSMD.get(8),
            ItemList.Circuit_Parts_InductorSMD.get(16), ItemList.Circuit_Parts_InductorSMD.get(32),
            ItemList.Circuit_Parts_InductorASMD.get(4), ItemList.Circuit_Parts_InductorASMD.get(8),
            ItemList.Circuit_Parts_InductorASMD.get(16), ItemList.Circuit_Parts_InductorXSMD.get(4),
            ItemList.Circuit_Parts_InductorXSMD.get(8), ItemList.Circuit_Parts_InductorXSMD.get(16),
            GTNLItemList.BiowareSMDInductor.get(8), GTNLItemList.BiowareSMDInductor.get(16) };

        ItemStack[] SUPER_CHEST = { ItemList.Super_Chest_LV.get(1), ItemList.Super_Chest_MV.get(1),
            ItemList.Super_Chest_HV.get(1), ItemList.Super_Chest_EV.get(1), ItemList.Super_Chest_IV.get(1),
            ItemList.Quantum_Chest_LV.get(1), ItemList.Quantum_Chest_MV.get(1), ItemList.Quantum_Chest_HV.get(1),
            ItemList.Quantum_Chest_EV.get(1), ItemList.Quantum_Chest_IV.get(1), ItemList.Quantum_Chest_IV.get(2),
            ItemList.Quantum_Chest_IV.get(4), ItemList.Quantum_Chest_IV.get(8), ItemList.Quantum_Chest_IV.get(16) };

        ItemStack[] OUTPUT_BUS = { ItemList.Hatch_Output_Bus_LV.get(1), ItemList.Hatch_Output_Bus_MV.get(1),
            ItemList.Hatch_Output_Bus_HV.get(1), ItemList.Hatch_Output_Bus_EV.get(1),
            ItemList.Hatch_Output_Bus_IV.get(1), GregtechItemList.Hatch_SuperBus_Output_LV.get(1),
            GregtechItemList.Hatch_SuperBus_Output_MV.get(1), GregtechItemList.Hatch_SuperBus_Output_HV.get(1),
            GregtechItemList.Hatch_SuperBus_Output_EV.get(1), GregtechItemList.Hatch_SuperBus_Output_IV.get(1),
            GregtechItemList.Hatch_SuperBus_Output_LuV.get(1), GregtechItemList.Hatch_SuperBus_Output_ZPM.get(1),
            GregtechItemList.Hatch_SuperBus_Output_UV.get(1), GregtechItemList.Hatch_SuperBus_Output_MAX.get(1), };

        for (int i = 0; i < 14; i++) {
            boolean isHighTier = i >= 11;

            OrePrefixes cable01 = isHighTier ? OrePrefixes.wireGt01 : OrePrefixes.cableGt01;
            OrePrefixes cable04 = isHighTier ? OrePrefixes.wireGt04 : OrePrefixes.cableGt04;

            RecipeBuilder.builder()
                .itemInputs(
                    ItemUtils.SENSOR[i].get(1),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.EnderPearl, 12),
                    GTOreDictUnificator.get(OrePrefixes.circuit, ItemUtils.TIER[i], 4),
                    COIL[i],
                    CHIP[i],
                    GTOreDictUnificator.get(cable01, ItemUtils.CABLE[i], 8),
                    GTOreDictUnificator.get(cable01, Materials.RedAlloy, 32),
                    GTOreDictUnificator.get(OrePrefixes.plate, ItemUtils.TIER_MATERIAL[i], 12))
                .itemOutputs(GTNLItemList.WIRELESS_ENERGY_COVER[i].get(1))
                .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                .duration(200)
                .eut(GTValues.VP[i + 1])
                .addTo(As)
                .addTo(HOR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTNLItemList.WIRELESS_ENERGY_COVER[i].get(2),
                    INDUCTOR[i],
                    GTOreDictUnificator.get(cable04, ItemUtils.CABLE[i], 4),
                    COIL_4A[i],
                    GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.BatteryAlloy, 2))
                .itemOutputs(GTNLItemList.WIRELESS_ENERGY_COVER_4A[i].get(1))
                .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                .duration(200)
                .eut(GTValues.VP[i + 1])
                .addTo(As);

            RecipeBuilder.builder()
                .itemInputs(OUTPUT_BUS[i], SUPER_CHEST[i])
                .itemOutputs(GTNLItemList.HUMONGOUS_OUTPUT_BUS[i].get(1))
                .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                .duration(100)
                .eut(GTValues.VP[i])
                .addTo(As);
        }
    }

    public void loadLaserHatch() {
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 9; i++) {
                int quantity;
                if (i < 4) {
                    quantity = 1;
                } else {
                    quantity = 1 << (i - 1);
                }

                OrePrefixes prefix = switch (i) {
                    case 0 -> OrePrefixes.wireGt01;
                    case 1 -> OrePrefixes.wireGt02;
                    case 2 -> OrePrefixes.wireGt04;
                    default -> OrePrefixes.wireGt08;
                };

                RecipeBuilder.builder()
                    .itemInputs(
                        ItemUtils.HULL[j + 4].get(1),
                        GTUtility
                            .copyAmountUnsafe(1 << i, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.SENSOR[j + 4].get(1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.ELECTRIC_PUMP[j + 4].get(1)),
                        GTUtility
                            .copyAmountUnsafe(quantity, GTOreDictUnificator.get(prefix, ItemUtils.CABLE[j + 4], 1)),
                        GTUtility.getIntegratedCircuit(i + 1))
                    .itemOutputs(GTNLItemList.LASER_ENERGY_HATCH[j][i].get(1))
                    .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                    .duration(50 << i)
                    .eut(GTValues.VP[j + 5])
                    .addTo(As)
                    .addTo(HOR);

                RecipeBuilder.builder()
                    .itemInputs(
                        ItemUtils.HULL[j + 4].get(1),
                        GTUtility
                            .copyAmountUnsafe(1 << i, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.EMITTER[j + 4].get(1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.ELECTRIC_PUMP[j + 4].get(1)),
                        GTUtility
                            .copyAmountUnsafe(quantity, GTOreDictUnificator.get(prefix, ItemUtils.CABLE[j + 4], 1)),
                        GTUtility.getIntegratedCircuit(i + 1))
                    .itemOutputs(GTNLItemList.LASER_DYNAMO_HATCH[j][i].get(1))
                    .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                    .duration(50 << i)
                    .eut(GTValues.VP[j + 5])
                    .addTo(As)
                    .addTo(HOR);
            }
        }
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

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.AssemblerMatrixWall.get(1),
                ItemList.AcceleratorLuV.get(4),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(4)
                    .orNull(),
                CustomItemList.EngineeringProcessorItemAdvEmeraldCore.get(16),
                ItemList.Field_Generator_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4),
                aeItems.coloredPaintBall()
                    .stack(AEColor.Red, 64))
            .itemOutputs(GTNLItemList.AssemblerMatrixSpeedCore.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
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
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2))
            .itemOutputs(GTNLItemList.QuantumComputerCraftingStorage128M.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCraftingStorage128M.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Quantium, 8),
                GTNLItemList.ShatteredSingularity.get(4),
                CustomItemList.EngravedQuantumChip.get(8),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(16))
            .itemOutputs(GTNLItemList.QuantumComputerCraftingStorage256M.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
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
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2))
            .itemOutputs(GTNLItemList.QuantumComputerAccelerator.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(4000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerAccelerator.get(1),
                GTNLItemList.QuantumComputerCraftingStorage256M.get(1),
                aeMaterials.singularity()
                    .maybeStack(4)
                    .orNull(),
                GTNLItemList.ShatteredSingularity.get(2),
                CustomItemList.EngravedQuantumChip.get(16),
                aeMaterials.engProcessor()
                    .maybeStack(16)
                    .orNull(),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(4))
            .itemOutputs(GTNLItemList.QuantumComputerCore.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCore.get(1),
                GTNLItemList.ShatteredSingularity.get(16),
                GTNLItemList.QuantumComputerAccelerator.get(4),
                ItemList.QuantumStar.get(4),
                CustomItemList.EngravedQuantumChip.get(16),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8))
            .itemOutputs(GTNLItemList.QuantumComputerMultiThreader.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTNLItemList.QuantumComputerUnit.get(1),
                GTNLItemList.QuantumComputerCore.get(1),
                GTNLItemList.ShatteredSingularity.get(16),
                GTNLItemList.QuantumComputerCraftingStorage256M.get(3),
                ItemList.QuantumStar.get(4),
                CustomItemList.EngravedQuantumChip.get(16),
                NHItemList.EngineeringProcessorSpatialPulsatingCore.getIS(8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8))
            .itemOutputs(GTNLItemList.QuantumComputerDataEntangler.get(1))
            .fluidInputs(GTNLMaterials.QuantumInfusion.getFluidOrGas(16000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.magicBeans", 64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.giantSword", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.giantPick", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.GiantObsidian", 16))
            .itemOutputs(GTNLItemList.GiantBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.nagaScale", 64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFSpiralBricks", 64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Steeleaf, 16))
            .itemOutputs(GTNLItemList.NagaBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 0),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.hydraChop", 64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.fieryBlood", 64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.FierySteel, 16))
            .itemOutputs(GTNLItemList.HydraBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 4),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.tripleBow", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFAuroraBrick", 64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.AuroraPillar", 64))
            .itemOutputs(GTNLItemList.SnowQueenBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 6),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.phantomHelm", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.phantomPlate", 1),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Knightmetal, 16))
            .itemOutputs(GTNLItemList.KnightPhantomBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 3),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFTowerDevice", 64, 0),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.TFTowerDevice", 64, 2),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.carminite", 32))
            .itemOutputs(GTNLItemList.UrGhastBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 5),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.meefStroganoff", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.meefSteak", 64),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.IronWood, 16))
            .itemOutputs(GTNLItemList.MinotaurBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 7),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.alphaFur", 16),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.iceBomb", 16),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.arcticFur", 64))
            .itemOutputs(GTNLItemList.AlphaYetiBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.book, 64),
                NHItemList.TwilightCrystal.getIS(64),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.trophy", 1, 2),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.scepterLifeDrain", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.scepterTwilight", 1),
                GTModHandler.getModItem(Mods.TwilightForest.ID, "item.scepterZombie", 1))
            .itemOutputs(GTNLItemList.LichBook.get(1))
            .fluidInputs(Materials.FierySteel.getFluid(4000))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Dynamo_IV.get(1),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 32, 0),
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "reactorReflectorThick", 1, 1),
                BlockList.TungstensteelPlatedReinforcedStone.getIS(2),
                CustomItemList.ReinforcedTungstenSteelIronPlate.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Tungsten, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 4))
            .itemOutputs(GTNLItemList.ExplosionDynamoHatch.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1296))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.MolybdenumDisilicide.get(OrePrefixes.ring, 32),
                NHItemList.MicaInsulatorFoil.getIS(16))
            .fluidInputs(GTNLMaterials.HSLASteel.getMolten(144))
            .itemOutputs(GTNLItemList.MolybdenumDisilicideCoil.get(1))
            .duration(500)
            .eut(1920)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Casing_Coil_Superconductor.get(1L),
                NHItemList.LaserEmitter.getIS(4),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 4),
                GregtechItemList.DehydratorCoilWireLuV.get(8),
                ItemList.LuV_Coil.get(4L),
                ItemList.Emitter_IV.get(2L),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 16))
            .fluidInputs(Materials.SolderingAlloy.getMolten(9216))
            .itemOutputs(GTNLItemList.LaserBeacon.get(1))
            .duration(400)
            .eut(30720)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_HV.get(1),
                NHItemList.AdsorptionFilter.getIS(1),
                ItemList.Electric_Pump_HV.get(2),
                ItemList.Electric_Motor_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2L),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Electrum, 2L))
            .itemOutputs(GTNLItemList.Desulfurizer.get(1))
            .duration(200)
            .eut(480)
            .addTo(As);
    }

    public void loadDeleteRecipe() {
        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_ZPM.get(1),
                ItemList.Machine_HV_LightningRod.get(1),
                GregtechItemList.Transformer_HA_LuV_IV.get(2),
                GregtechItemList.Hatch_Buffer_Dynamo_IV.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.VanadiumGallium, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Naquadah, 4),
                ItemList.Energy_LapotronicOrb2.get(2))
            .itemOutputs(ItemList.Machine_EV_LightningRod.get(1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(1152))
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_ZPM.get(2),
                ItemList.Machine_EV_LightningRod.get(1),
                GregtechItemList.Transformer_HA_ZPM_LuV.get(2),
                GregtechItemList.Hatch_Buffer_Dynamo_LuV.get(2),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.Naquadah, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 4),
                ItemList.Energy_Module.get(1))
            .itemOutputs(ItemList.Machine_IV_LightningRod.get(1))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(1152))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(As);
    }

}
