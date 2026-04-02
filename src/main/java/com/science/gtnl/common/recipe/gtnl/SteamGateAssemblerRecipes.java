package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class SteamGateAssemblerRecipes implements IRecipePool {

    public RecipeMap<?> SGAR = GTNLRecipeMaps.SteamGateAssemblerRecipes;

    @Override
    public void loadRecipes() {
        // Steam components
        ItemStack steamMotor = GTNLItemList.HydraulicMotor.get(1);
        ItemStack steamPump = GTNLItemList.HydraulicPump.get(1);
        ItemStack steamPiston = GTNLItemList.HydraulicPiston.get(1);
        ItemStack steamArm = GTNLItemList.HydraulicArm.get(1);
        ItemStack steamConveyor = GTNLItemList.HydraulicConveyor.get(1);
        ItemStack steamRegulator = GTNLItemList.HydraulicRegulator.get(1);
        ItemStack steamFieldGen = GTNLItemList.HydraulicVaporGenerator.get(1);
        ItemStack steamEmitter = GTNLItemList.HydraulicSteamJetSpewer.get(1);
        ItemStack steamSensor = GTNLItemList.HydraulicSteamReceiver.get(1);
        // Steamgate parts
        ItemStack gatePlate = GTNLItemList.SteamgateHeatContainmentPlate.get(1);
        ItemStack gateFrame = GTNLItemList.SteamgateFrame.get(1);
        ItemStack gateChevron = GTNLItemList.SteamgateChevron.get(1);
        ItemStack gateChevronUpgrade = GTNLItemList.SteamgateChevronUpgrade.get(1);
        ItemStack gateCrystal = GTNLItemList.SteamgateCoreCrystal.get(1);
        ItemStack gateIris = GTNLItemList.SteamgateIrisBlade.get(1);
        ItemStack gateIrisUpgrade = GTNLItemList.SteamgateIrisUpgrade.get(1);
        ItemStack gateRingBlock = GTNLItemList.SteamgateRingBlock.get(1);
        ItemStack gateChevronBlock = GTNLItemList.SteamgateChevronBlock.get(1);
        // Blocks
        ItemStack steelBlock = GTOreDictUnificator.get(OrePrefixes.block, Materials.Steel, 1);
        ItemStack bronzeBlock = GTOreDictUnificator.get(OrePrefixes.block, Materials.Bronze, 1);
        // Superdense plates
        ItemStack superdenseBronze = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Bronze, 1);
        ItemStack superdenseSteel = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Steel, 1);
        ItemStack superdenseBreel = GTNLMaterials.Breel.get(OrePrefixes.plateSuperdense, 1);
        ItemStack superdenseStronze = GTNLMaterials.Stronze.get(OrePrefixes.plateSuperdense, 1);
        ItemStack superdenseSteam = GTNLMaterials.CompressedSteam.get(OrePrefixes.plateSuperdense, 1);
        // Pipes
        ItemStack stronzePipe = GTNLMaterials.Stronze.get(OrePrefixes.pipeMedium, 1);
        ItemStack stronzePipeH = GTNLMaterials.Stronze.get(OrePrefixes.pipeHuge, 1);
        ItemStack breelPipeL = GTNLMaterials.Breel.get(OrePrefixes.pipeLarge, 1);
        // Plates
        ItemStack stronzePlate = GTNLMaterials.Stronze.get(OrePrefixes.plate, 1);
        ItemStack breelPlate = GTNLMaterials.Breel.get(OrePrefixes.plate, 1);
        // Rings
        ItemStack stronzeRing = GTNLMaterials.Stronze.get(OrePrefixes.ring, 1);
        ItemStack breelRing = GTNLMaterials.Breel.get(OrePrefixes.ring, 1);
        // Gears
        ItemStack stronzeGear = GTNLMaterials.Stronze.get(OrePrefixes.gearGt, 1);
        ItemStack breelGear = GTNLMaterials.Breel.get(OrePrefixes.gearGt, 1);
        ItemStack steamGear = GTNLMaterials.CompressedSteam.get(OrePrefixes.gearGt, 1);
        // Small gears
        ItemStack stronzeSmallGear = GTNLMaterials.Stronze.get(OrePrefixes.gearGtSmall, 1);
        ItemStack breelSmallGear = GTNLMaterials.Breel.get(OrePrefixes.gearGtSmall, 1);
        ItemStack steamSmallGear = GTNLMaterials.CompressedSteam.get(OrePrefixes.gearGtSmall, 1);
        // Long rods
        ItemStack stronzeLongRod = GTNLMaterials.Stronze.get(OrePrefixes.stickLong, 1);
        ItemStack breelLongRod = GTNLMaterials.Breel.get(OrePrefixes.stickLong, 1);
        ItemStack steamLongRod = GTNLMaterials.CompressedSteam.get(OrePrefixes.stickLong, 1);
        // Reinforced wood
        ItemStack ironWood = GTNLItemList.IronReinforcedWood.get(1);
        ItemStack bronzeWood = GTNLItemList.BronzeReinforcedWood.get(1);
        ItemStack steelWood = GTNLItemList.SteelReinforcedWood.get(1);
        // Gems
        ItemStack exDiam = GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Diamond, 1);
        ItemStack exEmer = GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Emerald, 1);
        ItemStack exRuby = GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Ruby, 1);
        ItemStack exSalt = GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Salt, 1);
        // Misc
        ItemStack ironPressure = new ItemStack(Blocks.heavy_weighted_pressure_plate, 1);
        ItemStack goldPressure = new ItemStack(Blocks.light_weighted_pressure_plate, 1);
        ItemStack largeCompressor = GTNLItemList.LargeSteamCompressor.get(1);
        ItemStack extractinator = GTNLItemList.SteamExtractinator.get(1);
        ItemStack bigSteamHatch = GTNLItemList.BigSteamInputHatch.get(1);
        ItemStack spdMb = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Molybdenum, 1);
        ItemStack spdBA = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.BlueAlloy, 1);
        ItemStack spdBe = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Beryllium, 1);
        ItemStack spdTh = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Thorium, 1);
        ItemStack spdSb = GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Antimony, 1);
        ItemStack Sadbapycat = GTNLItemList.SadBapyCatToken.get(1);

        // Blank recipe for copy-paste convenience
        /*
         * RecipeBuilder.builder()
         * .itemInputsAllowNulls(
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null,
         * null, null, null, null, null, null, null, null, null)
         * .itemOutputs(null)
         * .duration(20 * SECONDS)
         * .eut((int) TierEU.RECIPE_LV)
         * .addTo(SGAR);
         */

        // spotless:off

        // Steamgate controller
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                ironWood, steamFieldGen, steamFieldGen, steamEmitter, steamGear, steamEmitter, steamFieldGen, steamFieldGen, ironWood,
                steamFieldGen, steelWood, superdenseBreel, gatePlate, steamConveyor, gatePlate, superdenseStronze, steelWood, steamFieldGen,
                steamFieldGen, bronzeWood, bigSteamHatch, gatePlate, steamArm, gatePlate, bigSteamHatch, bronzeWood, steamFieldGen,
                steamEmitter, stronzeRing, extractinator, gateChevronBlock, gateChevronBlock, gateChevronBlock, extractinator, stronzeRing, steamEmitter,
                steamGear, breelRing, largeCompressor, gateChevronBlock, gateCrystal, gateChevronBlock, largeCompressor, breelRing, steamGear,
                steamEmitter, stronzeRing, extractinator, gateChevronBlock, gateChevronBlock, gateChevronBlock, extractinator, stronzeRing, steamEmitter,
                steamFieldGen, bronzeWood, bigSteamHatch, gatePlate, gateIrisUpgrade, gatePlate, bigSteamHatch, bronzeWood, steamFieldGen,
                steamFieldGen, steelWood, superdenseStronze, gatePlate, steamConveyor, gatePlate, superdenseBreel, steelWood, steamFieldGen,
                ironWood, steamFieldGen, steamFieldGen, steamEmitter, steamGear, steamEmitter, steamFieldGen, steamFieldGen, ironWood)
            .itemOutputs(GTNLItemList.Steamgate.get(1))
            .duration(120 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate Ring Block
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                bronzeWood, bronzeWood, bronzeWood, gateFrame, gateChevron, gateFrame, gateFrame, gatePlate, gatePlate,
                ironWood, ironWood, ironWood, gateFrame, gateFrame, gateFrame, gatePlate, steamFieldGen, steamFieldGen,
                bronzeWood, bronzeWood, bronzeWood, gateFrame, gateFrame, gatePlate, steamFieldGen, null, null,
                steelWood, steelWood, steelWood, gateFrame, gateFrame, gatePlate, steamFieldGen, null, null,
                steelWood, steelWood, steelWood, gateFrame, gateChevron, gatePlate, steamFieldGen, null, null,
                steelWood, steelWood, steelWood, gateFrame, gateFrame, gatePlate, steamFieldGen, null, null,
                bronzeWood, bronzeWood, bronzeWood, gateFrame, gateFrame, gatePlate, steamFieldGen, null, null,
                ironWood, ironWood, ironWood, gateFrame, gateFrame, gateFrame, gatePlate, steamFieldGen, steamFieldGen,
                bronzeWood, bronzeWood, bronzeWood, gateFrame, gateChevron, gateFrame, gateFrame, gatePlate, gatePlate)
            .itemOutputs(gateRingBlock)
            .duration(20 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate Chevron Block
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                null, null, null, null, bronzeWood, null, null, null, null,
                null, null, null, bronzeWood, ironWood, bronzeWood, null, null, null,
                null, null, bronzeWood, ironWood, steamFieldGen, ironWood, bronzeWood, null, null,
                null, bronzeWood, ironWood, steelWood, gateChevronUpgrade, steelWood, ironWood, bronzeWood, null,
                bronzeWood, ironWood, steamFieldGen, gateChevronUpgrade, gateRingBlock, gateChevronUpgrade, steamFieldGen, ironWood, bronzeWood,
                null, bronzeWood, ironWood, steelWood, gateChevronUpgrade, steelWood, ironWood, bronzeWood, null,
                null, null, bronzeWood, ironWood, steamFieldGen, ironWood, bronzeWood, null, null,
                null, null, null, bronzeWood, ironWood, bronzeWood, null, null, null,
                null, null, null, null, bronzeWood, null, null, null, null)
            .itemOutputs(gateChevronBlock)
            .duration(20 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate Iris Blade
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                null, null, null, null, null, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze,
                null, null, null, null, superdenseBronze, superdenseSteel, superdenseSteel, superdenseBronze, null,
                null, null, null, superdenseBronze, superdenseSteel, superdenseSteel, superdenseBronze, null, null,
                null, null, superdenseBronze, superdenseSteel, superdenseSteel, superdenseBronze, null, null, null,
                null, superdenseBronze, superdenseSteel, superdenseSteel, superdenseSteel, superdenseBronze, null, null, null,
                superdenseBronze, steamPiston, superdenseSteel, superdenseSteel, superdenseSteel, superdenseBronze, null, null, null,
                superdenseBronze, steamPiston, superdenseSteel, superdenseSteel, superdenseSteel, superdenseSteel, superdenseBronze, null, null,
                superdenseBronze, steelWood, steamPiston, steamPiston, superdenseSteel, superdenseSteel, superdenseSteel, superdenseBronze, null,
                bronzeWood, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze, superdenseBronze)
            .itemOutputs(GTNLItemList.SteamgateIrisBlade.get(1))
            .duration(1 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate Frame
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                steamLongRod, stronzeLongRod, breelLongRod, stronzeLongRod, steamLongRod, stronzeLongRod, breelLongRod, stronzeLongRod, steamLongRod,
                stronzeLongRod, breelGear, steamSmallGear, stronzeGear, steamSmallGear, stronzeGear, steamSmallGear, breelGear, stronzeLongRod,
                breelLongRod, stronzeSmallGear, steamGear, stronzeSmallGear, stronzeGear, stronzeSmallGear, steamGear, stronzeSmallGear, breelLongRod,
                stronzeLongRod, breelGear, breelSmallGear, stronzeGear, breelSmallGear, stronzeGear, breelSmallGear, breelGear, stronzeLongRod,
                steamLongRod, steamSmallGear, stronzeGear, breelSmallGear, steamGear, breelSmallGear, stronzeGear, steamSmallGear, steamLongRod,
                stronzeLongRod, breelGear, breelSmallGear, stronzeGear, breelSmallGear, stronzeGear, breelSmallGear, breelGear, stronzeLongRod,
                breelLongRod, stronzeSmallGear, steamGear, stronzeSmallGear, stronzeGear, stronzeSmallGear, steamGear, stronzeSmallGear, breelLongRod,
                stronzeLongRod, breelGear, steamSmallGear, stronzeGear, steamSmallGear, stronzeGear, steamSmallGear, breelGear, stronzeLongRod,
                steamLongRod, stronzeLongRod, breelLongRod, stronzeLongRod, steamLongRod, stronzeLongRod, breelLongRod, stronzeLongRod, steamLongRod)
            .itemOutputs(gateFrame)
            .duration(2)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate core crystal
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                null, null, null, null, steamFieldGen, null, null, null, null,
                null, null, null, null, steamMotor, null, null, null, null,
                null, null, null, null, steamPiston, null, null, null, null,
                null, null, null, steamMotor, superdenseSteam, steamMotor, null, null, null,
                steamFieldGen, steamMotor, steamPiston, superdenseSteam, Sadbapycat, superdenseSteam, steamPiston, steamMotor, steamFieldGen,
                null, null, null, steamMotor, superdenseSteam, steamMotor, null, null, null,
                null, null, null, null, steamPiston, null, null, null, null,
                null, null, null, null, steamMotor, null, null, null, null,
                null, null, null, null, steamFieldGen, null, null, null, null)
            .itemOutputs(gateCrystal)
            .duration(20 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate chevron
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                steelWood, steelWood, steelWood, steelWood, steelWood, steelWood, steelWood, steelWood, steelWood,
                steelWood, exDiam, exDiam, exDiam, steamConveyor, exDiam, exDiam, exDiam, steelWood,
                steelWood, exEmer, exEmer, exEmer, breelPipeL, exEmer, exEmer, exEmer, steelWood,
                steelWood, exRuby, exRuby, exRuby, breelPipeL, exRuby, exRuby, exRuby, steelWood,
                steelWood, exSalt, exSalt, exSalt, breelPipeL, exSalt, exSalt, exSalt, steelWood,
                null, steelWood, stronzePlate, stronzePlate, breelPipeL, stronzePlate, stronzePlate, steelWood, null,
                null, null, steelWood, stronzePlate, breelPipeL, stronzePlate, steelWood, null, null,
                null, null, null, steelWood, steamArm, steelWood, null, null, null,
                null, null, null, null, steelWood, null, null, null, null)
            .itemOutputs(gateChevron)
            .duration(1 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate chevron upgrade
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                null, null, null, null, null, null, null, null, null,
                gateFrame, steamPiston, gateFrame, steamFieldGen, gateFrame, steamPiston, gateFrame, null, null,
                null, gateFrame, gateChevron, steamPiston, gateChevron, gateFrame, null, null, null,
                null, null, steamSensor, steamFieldGen, steamSensor, null, null, null, null,
                null, null, gateFrame, steamPiston, gateFrame, steamFieldGen, gateFrame, steamPiston, gateFrame,
                null, null, null, gateFrame, gateChevron, steamPiston, gateChevron, gateFrame, null,
                null, null, null, null, steamEmitter, steamFieldGen, steamEmitter, null, null,
                null, null, null, null, null, gateFrame, null, null, null,
                null, null, null, null, null, null, null, null, null)
            .itemOutputs(gateChevronUpgrade)
            .duration(1 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        // Steamgate plate
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                ironPressure, goldPressure, ironPressure, goldPressure, ironPressure, goldPressure, ironPressure, goldPressure, ironPressure,
                superdenseSteel, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, superdenseSteel,
                superdenseBronze, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, superdenseBronze,
                superdenseSteel, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, superdenseSteel,
                stronzePipe, stronzePipe, stronzePipe, stronzePipe, steamRegulator, stronzePipe, stronzePipe, stronzePipe, stronzePipe,
                superdenseSteel, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, superdenseSteel,
                superdenseBronze, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, stronzePlate, superdenseBronze,
                superdenseSteel, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, breelPlate, superdenseSteel,
                ironPressure, goldPressure, ironPressure, goldPressure, ironPressure, goldPressure, ironPressure, goldPressure, ironPressure)
            .itemOutputs(gatePlate)
            .duration(1 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                bronzeWood, steelWood, gateIris, gateIris, gateIris, gateIris, gateIris, steelWood, bronzeWood,
                steelWood, gateIris, null, null, breelGear, null, null, gateIris, steelWood,
                gateIris, null, null, null, null, stronzeSmallGear, null, null, gateIris,
                gateIris, null, stronzeSmallGear, breelGear, null, breelGear, null, null, gateIris,
                gateIris, breelGear, null, null, superdenseSteam, null, null, breelGear, gateIris,
                gateIris, null, null, breelGear, null, breelGear, stronzeSmallGear, null, gateIris,
                gateIris, null, null, stronzeSmallGear, null, null, null, null, gateIris,
                steelWood, gateIris, null, null, breelGear, null, null, gateIris, steelWood,
                bronzeWood, steelWood, gateIris, gateIris, gateIris, gateIris, gateIris, steelWood, bronzeWood)
            .itemOutputs(gateIrisUpgrade)
            .duration(1 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                stronzePipeH, breelPlate, breelPlate, breelPlate, stronzePipeH, breelPlate, breelPlate, breelPlate, stronzePipeH,
                breelPlate, stronzePipeH, breelPlate, breelPlate, stronzePipeH, breelPlate, breelPlate, stronzePipeH, breelPlate,
                breelPlate, breelPlate, stronzePipeH, gateFrame, stronzePipeH, gateFrame, stronzePipeH, breelPlate, breelPlate,
                breelPlate, breelPlate, gateFrame, stronzePipeH, stronzePipeH, stronzePipeH, gateFrame, breelPlate, breelPlate,
                stronzePipeH, stronzePipeH, stronzePipeH, stronzePipeH, bigSteamHatch, stronzePipeH, stronzePipeH, stronzePipeH, stronzePipeH,
                breelPlate, breelPlate, gateFrame, stronzePipeH, stronzePipeH, stronzePipeH, gateFrame, breelPlate, breelPlate,
                breelPlate, breelPlate, stronzePipeH, gateFrame, stronzePipeH, gateFrame, stronzePipeH, breelPlate, breelPlate,
                breelPlate, stronzePipeH, breelPlate, breelPlate, stronzePipeH, breelPlate, breelPlate, stronzePipeH, breelPlate,
                stronzePipeH, breelPlate, breelPlate, breelPlate, stronzePipeH, breelPlate, breelPlate, breelPlate, stronzePipeH)
            .itemOutputs(GTNLItemList.SteamgateDialingDevice.get(1))
            .duration(120 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);


        // Sadbapycat Token
       RecipeBuilder.builder()
            .itemInputsAllowNulls(
                null, null, null, null, null, null, null, null, null,
                null, spdBA, spdBA, spdBA, spdBA, spdBA, spdBA, spdBA, null,
                null, spdBA, spdMb, spdSb, spdTh, spdSb, spdMb, spdBA, null,
                null, spdBA, spdSb, spdTh, spdBe, spdTh, spdSb, spdBA, null,
                null, spdBA, spdMb, spdBe, steamFieldGen, spdBe, spdMb, spdBA, null,
                null, spdBA, spdSb, spdTh, spdBe, spdTh, spdSb, spdBA, null,
                null, spdBA, spdMb, spdSb, spdTh, spdSb, spdMb, spdBA, null,
                null, spdBA, spdBA, spdBA, spdBA, spdBA, spdBA, spdBA, null,
                null, null, null, null, null, null, null, null, null)
            .itemOutputs(GTNLItemList.SadBapyCatToken.get(1))
            .duration(20 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(SGAR);

        //spotless:on
    }
}
