package com.science.gtnl.common.recipe.GregTech;

import static com.science.gtnl.loader.IScriptLoader.missing;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.xmod.bop.blocks.BOPBlockRegistrator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CraftingTableRecipes implements IRecipePool {

    private static final long bits = GTModHandler.RecipeBits.NOT_REMOVABLE | GTModHandler.RecipeBits.REVERSIBLE
        | GTModHandler.RecipeBits.BUFFERED;
    private static final long bitsd = GTModHandler.RecipeBits.DISMANTLEABLE | bits;

    @Override
    public void loadRecipes() {

        addCraftingRecipe(
            GTNLItemList.LargeSteamCrusher.get(1),
            new Object[] { "ABA", "CDC", "EBE", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                ItemList.Component_Sawblade_Diamond.get(1), 'C', GregtechItemList.Controller_SteamMaceratorMulti.get(1),
                'D', GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Bronze, 1), 'E',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.CobaltBrass, 1) });

        addCraftingRecipe(
            GTNLItemList.BronzeBrickCasing.get(1),
            new Object[] { "AAA", "ABA", "ACA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', "craftingToolWrench", 'C', new ItemStack(Blocks.brick_block, 1) });

        addCraftingRecipe(
            GTNLItemList.SteelBrickCasing.get(1),
            new Object[] { "AAA", "ABA", "ACA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                'B', "craftingToolWrench", 'C', new ItemStack(Blocks.brick_block, 1) });

        addCraftingRecipe(
            GTNLItemList.CheatOreProcessingFactory.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', GTNLItemList.StargateSingularity.get(1), 'B',
                GTNLItemList.LargeSteamCrusher.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamFurnace.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', ItemList.Casing_Pipe_Bronze.get(1), 'C', ItemList.Casing_Firebox_Bronze.get(1), 'D',
                new ItemStack(Items.cauldron, 1), 'E', ItemList.Machine_Bronze_Furnace.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamAlloySmelter.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', GregtechItemList.BasicBronzeTurbine.get(1), 'C', ItemList.Casing_Firebox_Bronze.get(1), 'D',
                new ItemStack(Items.cauldron, 1), 'E', ItemList.Machine_Bronze_AlloySmelter.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamChemicalBath.get(1),
            new Object[] { "ABA", "CDC", "FEF", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Wood, 1), 'C',
                ItemList.Casing_Pipe_Bronze.get(1), 'D',
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Bronze, 1), 'E',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1), 'F',
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Bronze, 1) });

        addCraftingRecipe(
            GTNLItemList.PrecisionSteamMechanism.get(1),
            new Object[] { "ABA", "CDC", "EBE", 'A', GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 1),
                'B', GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Bronze, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.springSmall, Materials.Bronze, 1), 'D',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Steel, 1), 'E',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1) });

        addCraftingRecipe(
            GTNLItemList.PrimitiveDistillationTower.get(1),
            new Object[] { "ABA", "ABA", "CDC", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Steel, 1), 'B',
                ItemList.Casing_Pipe_Steel.get(1), 'C', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                'D', GTNLItemList.PrecisionSteamMechanism.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamCircuitAssembler.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                GTNLItemList.PrecisionSteamMechanism.get(1), 'C', GTNLItemList.SteamAssemblyCasing.get(1) });

        addCraftingRecipe(
            GTNLItemList.SteamAssemblyCasing.get(1),
            new Object[] { "ABA", "ACA", "ABA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', GTNLItemList.PrecisionSteamMechanism.get(1), 'C', ItemList.Casing_Gearbox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.SteamCracking.get(1),
            new Object[] { "ABA", "DCD", "ABA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', GTNLItemList.PrecisionSteamMechanism.get(1), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1), 'D',
                ItemList.Casing_Firebox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamThermalCentrifuge.get(1),
            new Object[] { "ABA", "DCD", "ABA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1), 'C',
                ItemList.Casing_Pipe_Bronze.get(1), 'D', ItemList.Casing_Firebox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.Desulfurizer.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', "circuitAdvanced", 'B', CustomItemList.AdsorptionFilter.get(1),
                'C', ItemList.Electric_Pump_HV.get(1), 'D', ItemList.Hull_HV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.Electrum, 1L), 'F',
                ItemList.Electric_Motor_HV.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeCircuitAssembler.get(1),
            new Object[] { "ABA", "CDC", "EBE", 'A', ItemList.Robot_Arm_EV.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 1L), 'C', "circuitData", 'D',
                ItemList.Machine_EV_CircuitAssembler.get(1), 'E', ItemList.Conveyor_Module_EV.get(1) });

        addCraftingRecipe(
            GTNLItemList.BrickedBlastFurnace.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1L), 'B',
                GTNLItemList.PrecisionSteamMechanism.get(1), 'C', ItemList.Machine_Bricked_BlastFurnace.get(1) });

        addCraftingRecipe(
            GTNLItemList.NeutroniumPipeCasing.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Neutronium, 1L), 'C',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L) });

        addCraftingRecipe(
            GTNLItemList.NeutroniumGearbox.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Neutronium, 1L), 'B',
                "craftingToolHardHammer", 'C', GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Neutronium, 1L),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L), 'E',
                "craftingToolWrench" });

        addCraftingRecipe(
            GTNLItemList.EnergeticPhotovoltaicPowerStation.get(1),
            new Object[] { "ABA", "BCB", "ADA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1L),
                'B', MaterialsAlloy.TUMBAGA.getBlock(1), 'C', GTNLItemList.EnergeticPhotovoltaicBlock.get(1), 'D',
                "circuitGood" });

        addCraftingRecipe(
            GTNLItemList.AdvancedPhotovoltaicPowerStation.get(1),
            new Object[] { "ABA", "BCB", "ADA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 1L),
                'B', GTModHandler.getModItem(EnderIO.ID, "blockIngotStorage", 1, 3), 'C',
                GTNLItemList.AdvancedPhotovoltaicBlock.get(1), 'D', "circuitAdvanced" });

        addCraftingRecipe(
            GTNLItemList.VibrantPhotovoltaicPowerStation.get(1),
            new Object[] { "ABA", "BCB", "ADA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 1L), 'B',
                GTModHandler.getModItem(EnderIO.ID, "blockIngotStorage", 1, 6), 'C',
                GTNLItemList.VibrantPhotovoltaicBlock.get(1), 'D', "circuitData" });

        addCraftingRecipe(
            GTNLItemList.TestItem.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A', new ItemStack(Items.golden_apple, 1, 1), 'B',
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 9), 'C', new ItemStack(Blocks.dragon_egg, 1) });

        addCraftingRecipe(
            GTNLItemList.CrushingWheels.get(2),
            new Object[] { "AAA", "BCB", "BDB", 'A',
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.TungstenCarbide, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Ultimet, 1L), 'C',
                ItemList.Casing_MiningOsmiridium.get(1L), 'D', ItemList.Electric_Motor_IV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.SlicingBlades.get(2),
            new Object[] { "AAA", "BCB", "BDB", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenCarbide, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Ultimet, 1L), 'C',
                GregtechItemList.Casing_CuttingFactoryFrame.get(1L), 'D', ItemList.Electric_Motor_IV.get(1L) });

        addCraftingRecipe(
            tectech.thing.CustomItemList.hatch_CreativeMaintenance.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', "circuitAdvanced", 'B', ItemList.Hatch_Maintenance.get(1L), 'C',
                ItemList.Robot_Arm_HV.get(1L), 'D', ItemList.Hull_HV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchEV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Titanium, 1L), 'B',
                ItemList.Hatch_Input_EV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchIV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.TungstenSteel, 1L), 'B',
                ItemList.Hatch_Input_IV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchLuV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.NiobiumTitanium, 1L), 'B',
                ItemList.Hatch_Input_LuV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchZPM.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Enderium, 1L), 'B',
                ItemList.Hatch_Input_ZPM.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Naquadah, 1L), 'B',
                ItemList.Hatch_Input_UV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUHV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Neutronium, 1L), 'B',
                ItemList.Hatch_Input_UHV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUEV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.NetherStar, 1L), 'B',
                ItemList.Hatch_Input_UEV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUIV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.MysteriousCrystal, 1L), 'B',
                ItemList.Hatch_Input_UIV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUMV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.DraconiumAwakened, 1L), 'B',
                ItemList.Hatch_Input_UMV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchUXV.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.Infinity, 1L), 'B',
                ItemList.Hatch_Input_UXV.get(1L) });

        addCraftingRecipe(
            GTNLItemList.NinefoldInputHatchMAX.get(1),
            new Object[] { " A ", " B ", "   ", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, MaterialsUEVplus.SpaceTime, 1L), 'B',
                ItemList.Hatch_Input_MAX.get(1L) });

        addCraftingRecipe(
            GTNLItemList.BlackLight.get(1),
            new Object[] { "ABA", "CDC", "EBF", 'A',
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.TungstenCarbide, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenCarbide, 1L), 'C',
                GTModHandler.getModItem(IndustrialCraft2.ID, "blockAlloyGlass", 1, 0, missing), 'D',
                GTOreDictUnificator.get(OrePrefixes.spring, Materials.Europium, 1L), 'E', "circuitElite", 'F',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Platinum, 1L) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamExtractor.get(1),
            new Object[] { "ABA", "CDE", "ABA", 'A', ItemList.Casing_Pipe_Bronze.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Bronze, 1L), 'C',
                ItemList.Casing_Gearbox_Bronze.get(1), 'D', ItemList.Machine_Bronze_Extractor.get(1), 'E',
                new ItemStack(Blocks.glass, 1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamOreWasher.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', ItemList.Casing_Gearbox_Bronze.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.WroughtIron, 1L), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'D',
                GregtechItemList.Controller_SteamWasherMulti.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamExtruder.get(1),
            new Object[] { "ABA", "CDC", "AAA", 'A', GregtechItemList.Casing_Machine_Custom_1.get(1), 'B',
                ItemList.Casing_Gearbox_Bronze.get(1), 'C', GTNLItemList.PrecisionSteamMechanism.get(1), 'D',
                "craftingPiston" });

        addCraftingRecipe(
            GTNLItemList.TungstensteelGearbox.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 1L), 'B', "craftingToolHardHammer",
                'C', GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.TungstenSteel, 1L), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L), 'E', "craftingToolWrench" });

        addCraftingRecipe(
            GTNLItemList.RecordNewHorizons.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTModHandler.getModItem(SGCraft.ID, "stargateRing", 1, 0, missing),
                'B', GTModHandler.getModItem(SGCraft.ID, "rfPowerUnit", 1, 0, missing), 'C',
                GTModHandler.getModItem(SGCraft.ID, "stargateRing", 1, 1, missing), 'D',
                GTModHandler.getModItem(SGCraft.ID, "stargateController", 1, 0, missing), 'E',
                GTModHandler.getModItem(SGCraft.ID, "stargateBase", 1, 0, missing) });

        addCraftingRecipe(
            GTNLItemList.SatietyRing.get(1),
            new Object[] { "AAA", "ABA", "AAA", 'A', new ItemStack(Items.golden_apple, 1, 1), 'B',
                GTModHandler.getModItem(Thaumcraft.ID, "ItemBaubleBlanks", 1, 1, missing) });

        addCraftingRecipe(
            GTNLItemList.KFCFamily.get(1),
            new Object[] { "AAA", "BCB", "AAA", 'A', GTModHandler.getModItem(PamsHarvestCraft.ID, "hotwingsItem", 1),
                'B', new ItemStack(Items.cooked_chicken, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.plateQuintuple, Materials.Paper, 1L) });

        addCraftingRecipe(
            GTNLItemList.BigSteamInputHatch.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.pipeHuge, Materials.Bronze, 1L), 'C',
                GTNLItemList.PrecisionSteamMechanism.get(1), 'D', GregtechItemList.Hatch_Input_Steam.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamCentrifuge.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                ItemList.Casing_Gearbox_Bronze.get(1), 'C', GTNLItemList.PrecisionSteamMechanism.get(1), 'D',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L), 'E',
                GregtechItemList.Controller_SteamCentrifugeMulti.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamHammer.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                new ItemStack(Blocks.anvil, 1), 'C', "craftingPiston", 'D', GTNLItemList.PrecisionSteamMechanism.get(1),
                'E', GregtechItemList.Controller_SteamForgeHammerMulti.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamCompressor.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                new ItemStack(Blocks.anvil, 1), 'C', ItemList.Casing_Gearbox_Bronze.get(1), 'D',
                MaterialsAlloy.TUMBAGA.getFrameBox(1), 'E', GregtechItemList.Controller_SteamCompressorMulti.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamSifter.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L), 'C',
                ItemList.Casing_Gearbox_Bronze.get(1), 'D', ItemList.Component_Filter.get(1), 'E',
                MaterialsAlloy.TUMBAGA.getFrameBox(1) });

        addCraftingRecipe(
            GregtechItemList.Hatch_Input_Bus_Steam.get(1),
            new Object[] { " A ", " B ", "   ", 'A', "craftingToolScrewdriver", 'B',
                GregtechItemList.Hatch_Output_Bus_Steam.get(1) });

        addCraftingRecipe(
            GregtechItemList.Hatch_Output_Bus_Steam.get(1),
            new Object[] { " A ", " B ", "   ", 'A', "craftingToolScrewdriver", 'B',
                GregtechItemList.Hatch_Input_Bus_Steam.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeBoilerBronze.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L), 'C', "circuitBasic", 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L), 'E',
                ItemList.Casing_Firebox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeBoilerSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'C', "circuitGood", 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L), 'E',
                ItemList.Casing_Firebox_Steel.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeBoilerTitanium.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 1L), 'C', "circuitAdvanced", 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L), 'E',
                ItemList.Casing_Firebox_Titanium.get(1) });

        addCraftingRecipe(
            GTNLItemList.LargeBoilerTungstenSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L), 'C', "circuitData", 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L), 'E',
                ItemList.Casing_Firebox_TungstenSteel.get(1) });

        addCraftingRecipe(
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrum, 2L),
            new Object[] { "AB ", "   ", "   ", 'A', GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1L) });

        addCraftingRecipe(
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 8L),
            new Object[] { "AB ", "   ", "   ", 'A', GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrum, 1L) });

        addCraftingRecipe(
            GTNLItemList.PlayerDoll.get(1),
            new Object[] { "ABC", "DEF", "GHI", 'A', new ItemStack(Blocks.wool, 1, 14), 'B',
                new ItemStack(Blocks.wool, 1, 1), 'C', new ItemStack(Blocks.wool, 1, 4), 'D',
                new ItemStack(Blocks.wool, 1, 0), 'E', new ItemStack(Blocks.iron_block, 1), 'F',
                new ItemStack(Blocks.wool, 1, 5), 'G', new ItemStack(Blocks.wool, 1, 10), 'H',
                new ItemStack(Blocks.wool, 1, 9), 'I', new ItemStack(Blocks.wool, 1, 11) });

        addCraftingRecipe(
            GTNLItemList.SteamAssemblerBronze.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.pipeSmall, Materials.Bronze, 1L), 'B',
                GTNLItemList.PrecisionSteamMechanism.get(1), 'C', ItemList.Casing_Gearbox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.SteamAssemblerSteel.get(1),
            new Object[] { "AAA", "BCB", "DBD", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1L), 'C',
                GTNLItemList.SteamAssemblerBronze.get(1), 'D',
                GTOreDictUnificator.get(OrePrefixes.pipeSmall, Materials.Steel, 1L) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamFormingPress.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                new ItemStack(Blocks.piston, 1), 'C', GTNLItemList.PrecisionSteamMechanism.get(1), 'D',
                ItemList.Casing_Gearbox_Bronze.get(1) });

        addCraftingRecipe(
            GTNLItemList.SteamTurbineLV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_LV.get(1), 'B', "circuitBasic", 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'D', ItemList.Hull_LV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Tin, 1L) });

        addCraftingRecipe(
            GTNLItemList.SteamTurbineMV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_MV.get(1), 'B', "circuitGood", 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Aluminium, 1L), 'D', ItemList.Hull_MV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.AnnealedCopper, 1L) });

        addCraftingRecipe(
            GTNLItemList.SteamTurbineHV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_HV.get(1), 'B', "circuitAdvanced", 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L), 'D', ItemList.Hull_HV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Gold, 1L) });

        addCraftingRecipe(
            GTNLItemList.SuperReachRing.get(1),
            new Object[] { "CB ", "BAB", " B ", 'A', GTModHandler.getModItem(Botania.ID, "reachRing", 1), 'B',
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsBotania.GaiaSpirit, 1L), 'C',
                GTModHandler.getModItem(Botania.ID, "lens", 1, 18) });

        addCraftingRecipe(
            GregtechItemList.Hatch_Reservoir.get(1),
            new Object[] { "ABA", "BCB", "DDD", 'A',
                GTModHandler.getModItem(IndustrialCraft2.ID, "blockAlloyGlass", 1, 0, missing), 'B',
                new ItemStack(Items.water_bucket, 1), 'C', ItemList.Hull_LV.get(1), 'D',
                new ItemStack(Items.diamond, 1) });

        addCraftingRecipe(
            GTNLItemList.LargeSteamMixer.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'C',
                GTNLItemList.PrecisionSteamMechanism.get(1), 'D', ItemList.Casing_Gearbox_Bronze.get(1), 'E',
                GregtechItemList.Controller_SteamMixerMulti.get(1) });

        addCraftingRecipe(
            GTNLItemList.ElectricBlastFurnace.get(1),
            new Object[] { "ABA", "CDC", "ECE", 'A', ItemList.Robot_Arm_HV.get(1), 'B',
                ItemList.Machine_Multi_BlastFurnace.get(1), 'C', "circuitData", 'D',
                ItemList.Casing_CleanStainlessSteel.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 1L) });

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorLV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            1);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorMV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            2);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorHV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            3);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorEV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            4);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorIV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            5);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorLuV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            6);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorZPM.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            7);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorUV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            8);

        GTModHandler.addMachineCraftingRecipe(
            GTNLItemList.GasCollectorUHV.get(1),
            bitsd,
            new Object[] { "ABA", "CDC", "AEA", 'A', new ItemStack(Blocks.iron_bars, 1), 'B',
                ItemList.FluidFilter.get(1), 'C', MTEBasicMachineWithRecipe.X.PUMP, 'D',
                MTEBasicMachineWithRecipe.X.HULL, 'E', MTEBasicMachineWithRecipe.X.CIRCUIT },
            9);

        addCraftingRecipe(
            GTNLItemList.SaplingBrickuoia.get(1),
            new Object[] { "AAA", "ABA", "ACA", 'A', new ItemStack(Blocks.brick_block, 1), 'B',
                new ItemStack(BOPBlockRegistrator.sapling_Rainforest, 1), 'C', new ItemStack(Items.dye, 1, 15) });
    }
}
