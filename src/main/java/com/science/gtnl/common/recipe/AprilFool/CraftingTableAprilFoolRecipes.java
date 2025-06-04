package com.science.gtnl.common.recipe.AprilFool;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CraftingTableAprilFoolRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {

        GTModHandler.addCraftingRecipe(
            GTNLItemList.VibrationSafeCasing.get(1),
            new Object[] { "AAA", "BCB", "AAA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1), 'B',
                MaterialPool.Breel.get(OrePrefixes.plateDouble, 1), 'C',
                ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 0, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteelBrickCasing.get(1),
            new Object[] { "AAA", "BBB", "BBB", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1), 'B',
                new ItemStack(Blocks.brick_block, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.ConcentratingSieveMesh.get(1),
            new Object[] { "AAA", "wBh", "AAA", 'A',
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.CrudeSteel, 1), 'B',
                MaterialPool.Breel.get(OrePrefixes.plateDouble, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.StronzeWrappedCasing.get(4),
            new Object[] { "ABA", "BBB", "ABA", 'A', MaterialPool.Stronze.get(OrePrefixes.plate, 1), 'B',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.BreelPipeCasing.get(2),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1), 'B',
                MaterialPool.Breel.get(OrePrefixes.pipeMedium, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SolarBoilingCell.get(1),
            new Object[] { "AAA", "BCB", 'A', new ItemStack(Blocks.glass, 1), 'B',
                MaterialPool.Stronze.get(OrePrefixes.pipeTiny, 1), 'C', ItemList.Machine_HP_Solar.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicAssemblingCasing.get(1),
            new Object[] { "ABA", "CCC", "ABA", 'A', MaterialPool.Stronze.get(OrePrefixes.pipeTiny, 1), 'B',
                MaterialPool.Breel.get(OrePrefixes.plate, 1), 'C', GTNLItemList.HydraulicArm.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HyperPressureBreelCasing.get(1),
            new Object[] { "AAA", "BCB", "AAA", 'A', MaterialPool.Breel.get(OrePrefixes.plate, 1), 'B',
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Beryllium, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Beryllium, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.BreelPlatedCasing.get(1),
            new Object[] { "AAA", "BCB", "AAA", 'A', MaterialPool.Breel.get(OrePrefixes.pipeTiny, 1), 'B',
                MaterialPool.Breel.get(OrePrefixes.plate, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1) });

        // Steam Extractinator
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamExtractinator.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', GTNLItemList.VibrationSafeCasing.get(1), 'B',
                ItemList.Casing_BronzePlatedBricks.get(1), 'C', GTNLItemList.HydraulicPump.get(1), 'D',
                MaterialPool.Stronze.get(OrePrefixes.pipeLarge, 1) });

        // Steam Fuser
        GTModHandler.addCraftingRecipe(
            GTNLItemList.LargeSteamAlloySmelter.get(1),
            new Object[] { "ABA", "CDC", "ABA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                new ItemStack(Blocks.furnace, 1), 'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1),
                'D', new ItemStack(Blocks.nether_brick, 1) });

        // Steam Conformer
        GTModHandler.addCraftingRecipe(
            GTNLItemList.LargeSteamExtruder.get(1),
            new Object[] { "BCA", "DEF", "AGA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                GTNLItemList.HydraulicMotor.get(1), 'C', GTNLItemList.HydraulicPiston.get(1), 'D',
                new ItemStack(Blocks.anvil, 1), 'E', GregtechItemList.Controller_SteamForgeHammerMulti.get(1), 'F',
                GTOreDictUnificator.get(OrePrefixes.plateQuintuple, Materials.Steel, 1), 'G',
                GTNLItemList.HydraulicConveyor.get(1) });

        // Steam Rock Breaker
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamRockBreaker.get(1),
            new Object[] { "ABA", "CDE", "ABA", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 12, 1), 'C', new ItemStack(Items.water_bucket, 1),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1), 'E',
                new ItemStack(Items.lava_bucket, 1) });

        // Steam Carpenter
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamCarpenter.get(1),
            new Object[] { "ABA", "CDC", "EEE", 'A', ItemList.Casing_BronzePlatedBricks.get(1), 'B',
                GTNLItemList.HydraulicPiston.get(1), 'D', new ItemStack(Blocks.glass, 1), 'C',
                GTNLItemList.HydraulicArm.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1) });

        // Steam Wood Cutter
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamWoodcutter.get(1),
            new Object[] { "AAA", "DCD", "EBE", 'A', GTNLItemList.BronzeReinforcedWood.get(1), 'B',
                GTNLItemList.HydraulicPump.get(1), 'D', new ItemStack(Blocks.glass, 1), 'C',
                new ItemStack(Blocks.dirt, 1), 'E', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1) });

        // Steam Manufactrer
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamManufacturer.get(1),
            new Object[] { "AAA", "DCD", "EBE", 'A', GTNLItemList.HydraulicArm.get(1), 'B',
                GTNLItemList.HydraulicConveyor.get(1), 'D', GTNLItemList.HydraulicAssemblingCasing.get(1), 'C',
                MaterialPool.Breel.get(OrePrefixes.plateDouble, 1), 'E', ItemList.Casing_Gearbox_Steel.get(1) });

        // Mega Solar Boiler
        GTModHandler.addCraftingRecipe(
            GTNLItemList.MegaSolarBoiler.get(1),
            new Object[] { "AAA", "DCD", "EBE", 'A', GTNLItemList.SolarBoilingCell.get(1), 'B',
                ItemList.Casing_SolidSteel.get(1), 'D', GTNLItemList.HydraulicPump.get(1), 'C', ItemList.Hull_HP, 'E',
                ItemList.Casing_BronzePlatedBricks });

        // Cactus Wonder
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamCactusWonder.get(1),
            new Object[] { "ABA", "ACA", "ABA", 'A', new ItemStack(Blocks.cactus, 1), 'B',
                ItemList.Casing_BronzePlatedBricks.get(1), 'C', GTNLItemList.HydraulicRegulator.get(1) });

        // Fusion Reactor
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamFusionReactor.get(1),
            new Object[] { "ABA", "DCD", "ABA", 'A', MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1), 'C',
                GTNLItemList.MegaSolarBoiler.get(1), 'B',
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1), 'D',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Beryllium, 1) });

        // Infernal Coke Oven
        GTModHandler.addCraftingRecipe(
            GTNLItemList.SteamInfernalCokeOven.get(1),
            new Object[] { "ABA", "DCD", "ABA", 'A', new ItemStack(Blocks.nether_brick, 1), 'C',
                ItemList.Machine_Bricked_BlastFurnace.get(1), 'B', MaterialPool.Breel.get(OrePrefixes.plate, 1), 'D',
                MaterialPool.Stronze.get(OrePrefixes.plate, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicMotor.get(1),
            new Object[] { "ABC", "BDB", "CBA", 'A', GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1),
                'B', GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 1), 'D',
                GregtechItemList.BasicIronTurbine.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicPiston.get(1),
            new Object[] { "AAA", "BCC", "DEF", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1), 'D',
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 1), 'C',
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 1), 'B',
                GregtechItemList.BasicIronTurbine.get(1), 'E', GTNLItemList.HydraulicMotor.get(1), 'F',
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicPump.get(1),
            new Object[] { "ABC", "sDw", "CEA", 'A', GTNLItemList.HydraulicMotor.get(1), 'B',
                GregtechItemList.BasicBronzeTurbine.get(1), 'C',
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Rubber, 1), 'D',
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Bronze, 1), 'E',
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.CrudeSteel, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicArm.get(1),
            new Object[] { "AAA", "BCB", "DEC", 'A',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1), 'B',
                GTNLItemList.HydraulicMotor.get(1), 'C', GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 1),
                'D', GTNLItemList.HydraulicPiston.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.gear, Materials.CrudeSteel, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicConveyor.get(1),
            new Object[] { "AAA", "BCB", "AAA", 'A', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1),
                'B', GTNLItemList.HydraulicMotor.get(1), 'C',
                GTOreDictUnificator.get(OrePrefixes.gear, Materials.CrudeSteel, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicRegulator.get(1),
            new Object[] { "ABC", "BDB", "CBA", 'A', GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1),
                'B', GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 1), 'C',
                GTNLItemList.SteelTurbine.get(1), 'D', GTNLItemList.HydraulicPump.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicSteamReceiver.get(1),
            new Object[] { "ABC", "ACB", "DAA", 'A', MaterialPool.CompressedSteam.get(OrePrefixes.plate, 1), 'B',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1), 'C',
                MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1), 'D', GTNLItemList.HydraulicPump.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicSteamJetSpewer.get(1),
            new Object[] { "ABC", "BDB", "CBA", 'A', MaterialPool.CompressedSteam.get(OrePrefixes.plate, 1), 'B',
                MaterialPool.CompressedSteam.get(OrePrefixes.stick, 1), 'C',
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 1), 'D',
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Salt, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.HydraulicVaporGenerator.get(1),
            new Object[] { "ABC", "BDB", "CBA", 'A', MaterialPool.CompressedSteam.get(OrePrefixes.plateSuperdense, 1),
                'B', MaterialPool.CompressedSteam.get(OrePrefixes.plateDouble, 1), 'C',
                GTNLItemList.HydraulicSteamJetSpewer.get(1), 'D', GTNLItemList.CompressedSteamTurbine.get(1) });

        // Pipeless Hatch
        GTModHandler.addCraftingRecipe(
            GTNLItemList.PipelessSteamHatch.get(1),
            new Object[] { "AEA", "CBD", "AEA", 'A', GTNLItemList.BronzeReinforcedWood.get(1), 'B',
                ItemList.Hatch_Input_Bus_LV.get(1), 'C', MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1), 'D',
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 1), 'E', GTNLItemList.HydraulicRegulator.get(1) });

        // Pipeless Vent
        GTModHandler.addCraftingRecipe(
            GTNLItemList.PipelessSteamVent.get(1),
            new Object[] { "AEA", "CBD", "AEA", 'A', GTNLItemList.BronzeReinforcedWood.get(1), 'B',
                ItemList.Hatch_Output_Bus_LV.get(1), 'C', MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1), 'D',
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 1), 'E', GTNLItemList.HydraulicRegulator.get(1) });

    }
}
