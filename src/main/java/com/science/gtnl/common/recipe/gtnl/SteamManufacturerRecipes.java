package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class SteamManufacturerRecipes implements IRecipePool {

    public RecipeMap<?> SMFR = GTNLRecipeMaps.SteamManufacturerRecipes;
    public RecipeMap<?> As = RecipeMaps.assemblerRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cobblestone, 8), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(new ItemStack(Blocks.furnace, 8))
            .duration(1 * SECONDS)
            .eut(8)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", 3),
                GTOreDictUnificator.get(OrePrefixes.pipeSmall, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.plateTriple, Materials.Silver, 3),
                ItemList.Hull_HP_Bricks.get(1))
            .itemOutputs(ItemList.Machine_HP_Solar.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
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
            RecipeBuilder.builder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, aMat, 4), GTUtility.getIntegratedCircuit(24))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.frameGt, aMat, 1))
                .duration(5 * SECONDS)
                .eut(16)
                .addTo(SMFR);
        }

        Materials[] pipeMaterials = { Materials.Bronze, Materials.WroughtIron, Materials.Copper, Materials.Steel };

        for (Materials aMat : pipeMaterials) {
            RecipeBuilder.builder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.pipeMedium, aMat, 4), GTUtility.getIntegratedCircuit(9))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeQuadruple, aMat, 1))
                .duration(3 * SECONDS)
                .eut(4)
                .addTo(SMFR);

            RecipeBuilder.builder()
                .itemInputs(GTOreDictUnificator.get(OrePrefixes.pipeSmall, aMat, 9), GTUtility.getIntegratedCircuit(9))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeNonuple, aMat, 1))
                .duration(3 * SECONDS)
                .eut(4)
                .addTo(SMFR);
        }

        RecipeBuilder.builder()
            .itemInputs(MaterialsAlloy.TUMBAGA.getRod(4), GTUtility.getIntegratedCircuit(24))
            .itemOutputs(MaterialsAlloy.TUMBAGA.getFrameBox(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 4),
                GTNLItemList.IronTurbine.get(1))
            .itemOutputs(GTNLItemList.HydraulicMotor.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 1),
                GTNLItemList.IronTurbine.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicMotor.get(1))
            .itemOutputs(GTNLItemList.HydraulicPiston.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Rubber, 2),
                GTOreDictUnificator.get(OrePrefixes.bolt, Materials.CrudeSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Bronze, 1),
                GTNLItemList.BronzeTurbine.get(1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicPump.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 3),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicPiston.get(1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicArm.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.CrudeSteel, 1),
                GTNLItemList.HydraulicMotor.get(2))
            .itemOutputs(GTNLItemList.HydraulicConveyor.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.SteelTurbine.get(2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Bronze, 4),
                GTNLItemList.HydraulicPump.get(1))
            .itemOutputs(GTNLItemList.HydraulicRegulator.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Stronze.get(OrePrefixes.pipeHuge, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 4),
                GTNLItemList.HydraulicRegulator.get(1))
            .itemOutputs(GTNLItemList.HydraulicSteamReceiver.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Salt, 1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.stick, 4),
                GTNLMaterials.Breel.get(OrePrefixes.pipeHuge, 2))
            .itemOutputs(GTNLItemList.HydraulicSteamJetSpewer.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.CompressedSteamTurbine.get(1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plateSuperdense, 2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plateDouble, 4),
                GTNLItemList.HydraulicSteamJetSpewer.get(2))
            .itemOutputs(GTNLItemList.HydraulicVaporGenerator.get(1))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 4),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Iron, 2))
            .itemOutputs(GTNLItemList.IronTurbine.get(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Bronze, 2))
            .itemOutputs(GTNLItemList.BronzeTurbine.get(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.turbineBlade, Materials.Steel, 4),
                GTNLMaterials.Stronze.get(OrePrefixes.stickLong, 1))
            .itemOutputs(GTNLItemList.SteelTurbine.get(1))
            .duration(5 * SECONDS)
            .eut(512)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.CompressedSteam.get(OrePrefixes.turbineBlade, 4),
                GTNLMaterials.Breel.get(OrePrefixes.stickLong, 1))
            .itemOutputs(GTNLItemList.CompressedSteamTurbine.get(1))
            .duration(5 * SECONDS)
            .eut(512)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 3),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1),
                new ItemStack(Blocks.cobblestone, 4))
            .itemOutputs(new ItemStack(Blocks.piston, 1))
            .duration(3 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Brass, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Brass, 2),
                GTOreDictUnificator.get(OrePrefixes.springSmall, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.springSmall, Materials.Steel, 1))
            .itemOutputs(GTNLItemList.PrecisionSteamMechanism.get(1))
            .duration(3 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        if (Mods.StorageDrawers.isModLoaded()) {
            // Drawer template
            RecipeBuilder.builder()
                .itemInputs(new ItemStack(Blocks.piston, 1), GTOreDictUnificator.get("drawerBasic", 1))
                .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 3, 0))
                .duration(10 * SECONDS)
                .eut(TierEU.RECIPE_LV)
                .addTo(SMFR);

            // Drawer controller
            RecipeBuilder.builder()
                .itemInputs(
                    GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1, 0),
                    GTOreDictUnificator.get("drawerBasic", 1),
                    GTNLMaterials.Breel.get(OrePrefixes.gearGt, 2))
                .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "controller", 1, 0))
                .duration(5 * SECONDS)
                .eut(TierEU.RECIPE_LV)
                .addTo(SMFR);
        }

        // Spotless:off

        RecipeBuilder.builder()
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

        RecipeBuilder.builder()
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

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.GTFluidTank_ULV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 6),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeMedium, 2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GregtechItemList.Hatch_Input_Steam.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Fluid hatches

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1L, 0),
                ItemList.Casing_BronzePlatedBricks.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Rubber, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_ULV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1L, 0),
                ItemList.Casing_BronzePlatedBricks.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 6),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Rubber, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_ULV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_ULV.get(1L),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeLarge, 1),
                GTNLMaterials.Stronze.get(OrePrefixes.plate, 6),
                GTNLMaterials.Stronze.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_LV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_ULV.get(1L),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeLarge, 1),
                GTNLMaterials.Stronze.get(OrePrefixes.plate, 6),
                GTNLMaterials.Stronze.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_LV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_LV.get(1L),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeLarge, 1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 6),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(ItemList.Hatch_Input_MV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);
        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Output_LV.get(1L),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeLarge, 1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 6),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.gearGt, 1),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(ItemList.Hatch_Output_MV.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Buses

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.hopper, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GregtechItemList.Hatch_Input_Bus_Steam.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.hopper, 1),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GregtechItemList.Hatch_Output_Bus_Steam.get(1L))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Hatches

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTNLItemList.OriginalInputHatch.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTNLItemList.OriginalOutputHatch.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Machine Casings

        // Bronze
        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.brick_block, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 6))
            .itemOutputs(ItemList.Casing_BronzePlatedBricks.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemList.Casing_Gearbox_Bronze.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemList.Casing_Pipe_Bronze.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4))
            .itemOutputs(ItemList.Casing_Firebox_Bronze.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Steel
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(ItemList.Casing_SolidSteel.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(ItemList.Casing_Gearbox_Steel.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(ItemList.Casing_Pipe_Steel.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4))
            .itemOutputs(ItemList.Casing_Firebox_Steel.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Vibration Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Breel.get(OrePrefixes.plateDouble, 2),
                ItemList.Casing_SolidSteel.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 6))
            .itemOutputs(GTNLItemList.VibrationSafeCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.brick_block, 3),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 3))
            .itemOutputs(GTNLItemList.BronzeBrickCasing.get(2))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.brick_block, 3),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 3))
            .itemOutputs(GTNLItemList.SteelBrickCasing.get(2))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Extractinator Solid Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Breel.get(OrePrefixes.plateDouble, 1),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.CrudeSteel, 6))
            .itemOutputs(GTNLItemList.ConcentratingSieveMesh.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Stronze Wrapped Casingg
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 5),
                GTNLMaterials.Stronze.get(OrePrefixes.plate, 4))
            .itemOutputs(GTNLItemList.StronzeWrappedCasing.get(4))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Breel Pipe Casingg
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1),
                GTNLMaterials.Breel.get(OrePrefixes.pipeMedium, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 4))
            .itemOutputs(GTNLItemList.BreelPipeCasing.get(2))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Solar Cell Casing
        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.glass, 3),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeTiny, 2),
                ItemList.Machine_HP_Solar.get(1))
            .itemOutputs(GTNLItemList.SolarBoilingCell.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Hydraulic Assembling Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Stronze.get(OrePrefixes.pipeTiny, 4),
                GTNLMaterials.Breel.get(OrePrefixes.plate, 2),
                GTNLItemList.HydraulicArm.get(3))
            .itemOutputs(GTNLItemList.HydraulicAssemblingCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Hyper Pressure Breel Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Beryllium, 2),
                GTNLMaterials.Breel.get(OrePrefixes.plate, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Beryllium, 1))
            .itemOutputs(GTNLItemList.HyperPressureBreelCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Breel-Plated Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Breel.get(OrePrefixes.plate, 2),
                GTNLMaterials.Breel.get(OrePrefixes.pipeTiny, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.CrudeSteel, 1))
            .itemOutputs(GTNLItemList.BreelPlatedCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Compact Pipe Casing
        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.BreelPipeCasing.get(1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeTiny, 2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.plate, 6))
            .itemOutputs(GTNLItemList.SteamCompactPipeCasing.get(1))
            .duration(6 * SECONDS)
            .eut(24)
            .addTo(SMFR)
            .addTo(As);

        // Weighted Pressure Plates
        RecipeBuilder.builder()
            .itemInputs(Materials.Gold.getPlates(2), GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Steel, 4))
            .itemOutputs(new ItemStack(Blocks.light_weighted_pressure_plate))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(Materials.Iron.getPlates(2), GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Steel, 4))
            .itemOutputs(new ItemStack(Blocks.heavy_weighted_pressure_plate))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        // Machine Controllers

        // Lavamaker
        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.StronzeWrappedCasing.get(1),
                GTNLItemList.HydraulicMotor.get(2),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeMedium, 2),
                GTNLMaterials.Breel.get(OrePrefixes.pipeMedium, 2))
            .itemOutputs(GTNLItemList.SteamLavaMaker.get(1))
            .duration(10 * SECONDS)
            .eut(200)
            .addTo(SMFR)
            .addTo(As);

        // Supercompressor
        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.Controller_SteamCompressorMulti.get(64), GTNLItemList.HydraulicPump.get(4))
            .itemOutputs(GTNLItemList.MegaSteamCompressor.get(1))
            .duration(120 * SECONDS)
            .eut(1600)
            .addTo(SMFR)
            .addTo(As);

        // Progenitor
        RecipeBuilder.builder()
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
            .addTo(SMFR)
            .addTo(As);

        // Compact Fusion
        RecipeBuilder.builder()
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
            .addTo(SMFR)
            .addTo(As);

        // Pipeless
        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.BronzeReinforcedWood.get(4),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeHuge, 1),
                GTNLMaterials.Breel.get(OrePrefixes.pipeHuge, 1),
                ItemList.Hatch_Input_Bus_LV.get(1),
                GTNLItemList.HydraulicRegulator.get(2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.PipelessSteamHatch.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.BronzeReinforcedWood.get(4),
                GTNLMaterials.Stronze.get(OrePrefixes.pipeHuge, 1),
                GTNLMaterials.Breel.get(OrePrefixes.pipeHuge, 1),
                ItemList.Hatch_Output_Bus_LV.get(1),
                GTNLItemList.HydraulicRegulator.get(2),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.PipelessSteamVent.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        // Jetstream Hatch
        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.PipelessSteamHatch.get(4),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeHuge, 2),
                GTNLMaterials.Breel.get(OrePrefixes.plateSuperdense, 1))
            .itemOutputs(GTNLItemList.PipelessJetstreamHatch.get(1))
            .duration(20 * SECONDS)
            .eut(400)
            .addTo(SMFR)
            .addTo(As);

        // Jetstream Vent
        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.PipelessSteamVent.get(4),
                GTNLItemList.HydraulicVaporGenerator.get(1),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeHuge, 2),
                GTNLMaterials.Breel.get(OrePrefixes.plateSuperdense, 1))
            .itemOutputs(GTNLItemList.PipelessJetstreamVent.get(1))
            .duration(20 * SECONDS)
            .eut(400)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.PipelessSteamHatch.get(4),
                GTNLItemList.HydraulicSteamReceiver.get(2),
                GTNLMaterials.CompressedSteam.get(OrePrefixes.pipeHuge, 2),
                GTNLMaterials.Breel.get(OrePrefixes.plateSuperdense, 1))
            .itemOutputs(GTNLItemList.PipelessSteamCover.get(1))
            .duration(20 * SECONDS)
            .eut(400)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Brass, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTNLItemList.IndustrialSteamCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTNLItemList.AdvancedIndustrialSteamCasing.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Bronze, 4))
            .itemOutputs(GTNLItemList.BronzeMachineFrame.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Steel, 4))
            .itemOutputs(GTNLItemList.SteelMachineFrame.get(1))
            .duration(2 * SECONDS)
            .eut(16)
            .addTo(SMFR)
            .addTo(As);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 5),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Iron, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 2))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Gold, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Gold, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 3))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Obsidian, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Obsidian, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 4))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Diamond, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Diamond, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 5))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Tantalum, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Emerald, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 6))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Ruby, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Ruby, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 7))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgradeTemplate", 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Tanzanite, 4),
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.Tanzanite, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.StorageDrawers.ID, "upgrade", 1, 8))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SMFR);
    }
}
