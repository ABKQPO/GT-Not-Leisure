package com.science.gtnl.common.recipe.AprilFool;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class SteamManufacturerRecipes implements IRecipePool {

    final RecipeMap<?> SMFR = RecipeRegister.SteamManufacturerRecipes;

    @Override
    public void loadRecipes() {

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.cobblestone, 8), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(new ItemStack(Blocks.furnace, 8))
            .duration(1 * SECONDS)
            .eut(8)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem(IndustrialCraft2.ID, "blockAlloyGlass", 3),
                GTOreDictUnificator.get(OrePrefixes.pipeSmall, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Silver, 3),
                ItemList.Hull_HP_Bricks.get(1))
            .itemOutputs(ItemList.Machine_HP_Solar.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 5),
                new ItemStack(Blocks.brick_block, 3))
            .itemOutputs(ItemList.Hull_HP_Bricks.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        Materials[] Tier1Materials = { Materials.Bronze, Materials.Iron, Materials.Copper, Materials.Tin,
            Materials.Brass, Materials.Steel, Materials.WroughtIron, Materials.CrudeSteel };

        for (Materials aMat : Tier1Materials) {
            RA.stdBuilder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, aMat, 4), GTUtility.getIntegratedCircuit(24))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.frameGt, aMat, 1))
                .duration(5 * SECONDS)
                .eut(16)
                .addTo(SMFR);
        }

        Materials[] pipeMaterials = { Materials.Bronze, Materials.WroughtIron, Materials.Copper, Materials.Steel };

        for (Materials aMat : pipeMaterials) {
            RA.stdBuilder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.pipeMedium, aMat, 4), GTUtility.getIntegratedCircuit(9))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeQuadruple, aMat, 1))
                .duration(3 * SECONDS)
                .eut(4)
                .addTo(SMFR);

            RA.stdBuilder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.pipeSmall, aMat, 9), GTUtility.getIntegratedCircuit(9))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeNonuple, aMat, 1))
                .duration(3 * SECONDS)
                .eut(4)
                .addTo(SMFR);
        }

        RA.stdBuilder()
            .itemInputs(MaterialsAlloy.TUMBAGA.getRod(4), GTUtility.getIntegratedCircuit(24))
            .itemOutputs(MaterialsAlloy.TUMBAGA.getFrameBox(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 4),
                GregtechItemList.BasicIronTurbine.get(1))
            .itemOutputs(GTNLItemList.HydraulicMotor.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 1),
                GregtechItemList.BasicIronTurbine.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicMotor.get(1))
            .itemOutputs(GTNLItemList.HydraulicPiston.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Rubber, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.CrudeSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Bronze, 1),
                GregtechItemList.BasicBronzeTurbine.get(1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicPump.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 3),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicPiston.get(1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicArm.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicConveyor.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GregtechItemList.BasicIronTurbine.get(2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 4),
                GTNLItemList.HydraulicPump.get(1))
            .itemOutputs(GTNLItemList.HydraulicRegulator.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 2),
                MaterialPool.CompressedSteam.get(OrePrefixes.plate, 4),
                GTNLItemList.HydraulicRegulator.get(1))
            .itemOutputs(GTNLItemList.HydraulicSteamReceiver.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Salt, 1),
                MaterialPool.CompressedSteam.get(OrePrefixes.plate, 2),
                MaterialPool.CompressedSteam.get(OrePrefixes.stick, 4),
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 2))
            .itemOutputs(GTNLItemList.HydraulicSteamJetSpewer.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.CompressedSteamTurbine.get(1),
                MaterialPool.CompressedSteam.get(OrePrefixes.plateSuperdense, 2),
                MaterialPool.CompressedSteam.get(OrePrefixes.plateDouble, 4),
                GTNLItemList.HydraulicSteamJetSpewer.get(2))
            .itemOutputs(GTNLItemList.HydraulicVaporGenerator.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2))
            .itemOutputs(GregtechItemList.BasicIronTurbine.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 2))
            .itemOutputs(GregtechItemList.BasicBronzeTurbine.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.turbineBlade, Materials.Steel, 4),
                MaterialPool.Stronze.get(OrePrefixes.stickLong, 1))
            .itemOutputs(GTNLItemList.SteelTurbine.get(1))
            .duration(23 * SECONDS)
            .eut(512)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                MaterialPool.CompressedSteam.get(OrePrefixes.turbineBlade, 4),
                MaterialPool.Breel.get(OrePrefixes.stickLong, 1))
            .itemOutputs(GTNLItemList.CompressedSteamTurbine.get(1))
            .duration(23 * SECONDS)
            .eut(512)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 3),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1),
                new ItemStack(Blocks.cobblestone, 4))
            .itemOutputs(new ItemStack(Blocks.piston, 1))
            .duration(3 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        if (StorageDrawers.isModLoaded()) {
            // Drawer template
            GTValues.RA.stdBuilder()
                .itemInputs(new ItemStack(Blocks.piston, 1), GTOreDictUnificator.get("drawerBasic", 1))
                .itemOutputs(GTModHandler.getModItem(StorageDrawers.ID, "upgradeTemplate", 3, 0))
                .duration(10 * SECONDS)
                .eut(TierEU.RECIPE_LV)
                .addTo(SMFR);

            // Drawer controller
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTModHandler.getModItem(StorageDrawers.ID, "upgradeTemplate", 1, 0),
                    GTOreDictUnificator.get("drawerBasic", 1),
                    MaterialPool.Breel.get(OrePrefixes.gearGt, 2))
                .itemOutputs(GTModHandler.getModItem(StorageDrawers.ID, "controller", 1, 0))
                .duration(5 * SECONDS)
                .eut(TierEU.RECIPE_LV)
                .addTo(SMFR);
        }

        // Spotless:off

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Tin, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Clay, 1),
                GTUtility.getIntegratedCircuit(4))
            .itemOutputs(GregtechItemList.GTFluidTank_ULV.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.chest, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 5),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(new ItemStack(Blocks.hopper, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Hatches

        // Steam Hatch

        RA.stdBuilder()
            .itemInputs(
                GregtechItemList.GTFluidTank_ULV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 6),
                MaterialPool.Stronze.get(OrePrefixes.pipeMedium, 2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GregtechItemList.Hatch_Input_Steam.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Fluid hatches

        RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem(BuildCraftFactory.ID, "tankBlock", 1L, 0),
                ItemList.Casing_BronzePlatedBricks.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Rubber, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_ULV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem(BuildCraftFactory.ID, "tankBlock", 1L, 0),
                ItemList.Casing_BronzePlatedBricks.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Rubber, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_ULV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Input_ULV.get(1L),
                MaterialPool.Stronze.get(OrePrefixes.pipeLarge, 1),
                MaterialPool.Stronze.get(OrePrefixes.plate, 6),
                MaterialPool.Stronze.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_LV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_ULV.get(1L),
                MaterialPool.Stronze.get(OrePrefixes.pipeLarge, 1),
                MaterialPool.Stronze.get(OrePrefixes.plate, 6),
                MaterialPool.Stronze.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_LV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Input_LV.get(1L),
                MaterialPool.CompressedSteam.get(OrePrefixes.pipeLarge, 1),
                MaterialPool.CompressedSteam.get(OrePrefixes.plate, 6),
                MaterialPool.CompressedSteam.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_MV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_LV.get(1L),
                MaterialPool.CompressedSteam.get(OrePrefixes.pipeLarge, 1),
                MaterialPool.CompressedSteam.get(OrePrefixes.plate, 6),
                MaterialPool.CompressedSteam.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_MV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Buses

        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.hopper, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GregtechItemList.Hatch_Input_Bus_Steam.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.hopper, 1),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GregtechItemList.Hatch_Output_Bus_Steam.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Machine Casings

        // Bronze
        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.brick_block, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 6))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings1, 10, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 2, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 12, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings3, 13, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Steel
        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 0, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 2, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 13, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings3, 14, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 2),
                ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 3, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(ItemUtils.simpleMetaStack(ModBlocks.blockCasingsMisc, 3, 1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Vibration Casing
        RA.stdBuilder()
            .itemInputs(
                MaterialPool.Breel.get(OrePrefixes.plateDouble, 2),
                ItemUtils.simpleMetaStack(GregTechAPI.sBlockCasings2, 0, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 6))
            .itemOutputs(GTNLItemList.VibrationSafeCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Extractinator Solid Casing
        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.brick_block, 6),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 3))
            .itemOutputs(GTNLItemList.SteelBrickCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Extractinator Solid Casing
        RA.stdBuilder()
            .itemInputs(
                MaterialPool.Breel.get(OrePrefixes.plateDouble, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.CrudeSteel, 6))
            .itemOutputs(GTNLItemList.ConcentratingSieveMesh.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Stronze Wrapped Casingg
        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 5),
                MaterialPool.Stronze.get(OrePrefixes.plate, 4))
            .itemOutputs(GTNLItemList.StronzeWrappedCasing.get(4))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Breel Pipe Casingg
        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1),
                MaterialPool.Breel.get(OrePrefixes.pipeMedium, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 4))
            .itemOutputs(GTNLItemList.BreelPipeCasing.get(2))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Solar Cell Casing
        RA.stdBuilder()
            .itemInputs(
                new ItemStack(Blocks.glass, 3),
                MaterialPool.Stronze.get(OrePrefixes.pipeTiny, 2),
                ItemList.Machine_HP_Solar.get(1))
            .itemOutputs(GTNLItemList.SolarBoilingCell.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Hydraulic Assembling Casing
        RA.stdBuilder()
            .itemInputs(
                MaterialPool.Stronze.get(OrePrefixes.pipeTiny, 4),
                MaterialPool.Breel.get(OrePrefixes.plate, 2),
                GTNLItemList.HydraulicArm.get(3))
            .itemOutputs(GTNLItemList.HydraulicAssemblingCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Hyper Pressure Breel Casing
        RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Beryllium, 2),
                MaterialPool.Breel.get(OrePrefixes.plate, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Beryllium, 1))
            .itemOutputs(GTNLItemList.HyperPressureBreelCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Breel-Plated Casing
        RA.stdBuilder()
            .itemInputs(
                MaterialPool.Breel.get(OrePrefixes.plate, 2),
                MaterialPool.Breel.get(OrePrefixes.pipeTiny, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1))
            .itemOutputs(GTNLItemList.BreelPlatedCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Compact Pipe Casing
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.BreelPipeCasing.get(1),
                MaterialPool.CompressedSteam.get(OrePrefixes.pipeTiny, 2),
                MaterialPool.CompressedSteam.get(OrePrefixes.plate, 6))
            .itemOutputs(GTNLItemList.SteamCompactPipeCasing.get(1))
            .duration(6 * SECONDS)
            .eut(24)
            .addTo(SMFR);

        // Weighted Pressure Plates
        RA.stdBuilder()
            .itemInputs(Materials.Gold.getPlates(2), GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Steel, 4))
            .itemOutputs(new ItemStack(Blocks.light_weighted_pressure_plate))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(Materials.Iron.getPlates(2), GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Steel, 4))
            .itemOutputs(new ItemStack(Blocks.heavy_weighted_pressure_plate))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Machine Controllers

        // Lavamaker
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.StronzeWrappedCasing.get(1),
                GTNLItemList.HydraulicMotor.get(2),
                MaterialPool.Stronze.get(OrePrefixes.pipeMedium, 2),
                MaterialPool.Breel.get(OrePrefixes.pipeMedium, 2))
            .itemOutputs(GTNLItemList.SteamLavaMaker.get(1))
            .duration(10 * SECONDS)
            .eut(200)
            .addTo(SMFR);

        // Supercompressor
        RA.stdBuilder()
            .itemInputs(GregtechItemList.Controller_SteamCompressorMulti.get(64), GTNLItemList.HydraulicPump.get(4))
            .itemOutputs(GTNLItemList.MegaSteamCompressor.get(1))
            .duration(120 * SECONDS)
            .eut(1600)
            .addTo(SMFR);

        // Progenitor
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.SteamManufacturer.get(64),
                GTNLItemList.HydraulicRegulator.get(16),
                GTNLItemList.HydraulicArm.get(8),
                GTNLItemList.HydraulicSteamReceiver.get(4),
                GTNLItemList.HydraulicSteamJetSpewer.get(4),
                GTNLItemList.HydraulicVaporGenerator.get(2),
                GTNLItemList.StronzeWrappedCasing.get(32),
                GTNLItemList.BreelPipeCasing.get(32),
                GTNLItemList.SteelReinforcedWood.get(32))
            .itemOutputs(GTNLItemList.SteamGateAssembler.get(1))
            .duration(120 * SECONDS)
            .eut(1600)
            .addTo(SMFR);

        // Compact Fusion
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.SteamFusionReactor.get(64),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLItemList.HydraulicVaporGenerator.get(1))
            .itemOutputs(GTNLItemList.HighPressureSteamFusionReactor.get(1))
            .duration(120 * SECONDS)
            .eut(1600)
            .addTo(SMFR);

        // Pipeless
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.BronzeReinforcedWood.get(4),
                MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1),
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 1),
                ItemList.Hatch_Input_Bus_LV.get(1),
                GTNLItemList.HydraulicRegulator.get(2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.PipelessSteamHatch.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.BronzeReinforcedWood.get(4),
                MaterialPool.Stronze.get(OrePrefixes.pipeHuge, 1),
                MaterialPool.Breel.get(OrePrefixes.pipeHuge, 1),
                ItemList.Hatch_Output_Bus_LV.get(1),
                GTNLItemList.HydraulicRegulator.get(2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.PipelessSteamVent.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Jetstream Hatch
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.PipelessSteamHatch.get(4),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                MaterialPool.CompressedSteam.get(OrePrefixes.pipeHuge, 2),
                MaterialPool.Breel.get(OrePrefixes.plateSuperdense, 1))
            .itemOutputs(GTNLItemList.PipelessJetstreamHatch.get(1))
            .duration(20 * SECONDS)
            .eut(400)
            .addTo(SMFR);

        // Jetstream Vent
        RA.stdBuilder()
            .itemInputs(
                GTNLItemList.PipelessSteamVent.get(4),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                MaterialPool.CompressedSteam.get(OrePrefixes.pipeHuge, 2),
                MaterialPool.Breel.get(OrePrefixes.plateSuperdense, 1))
            .itemOutputs(GTNLItemList.PipelessJetstreamVent.get(1))
            .duration(20 * SECONDS)
            .eut(400)
            .addTo(SMFR);

    }
}
