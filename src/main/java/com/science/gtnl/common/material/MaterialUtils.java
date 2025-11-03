package com.science.gtnl.common.material;

import static gregtech.api.recipe.RecipeMaps.*;
import static gregtech.api.recipe.RecipeMaps.wiremillRecipes;
import static gregtech.api.util.GTRecipeBuilder.*;

import com.science.gtnl.client.GTNLCreativeTabs;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.metatileentity.implementations.MTECable;
import gregtech.api.metatileentity.implementations.MTEFluidPipe;
import gregtech.api.metatileentity.implementations.MTEItemPipe;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class MaterialUtils {

    public static void registerItemPipe(int ID, Werkstoff material, int aInvSlotCount, int aStepSize,
        boolean aIsRestrictive, int aTickTime) {
        registerItemPipe(ID, material.getBridgeMaterial(), aInvSlotCount, aStepSize, aIsRestrictive, aTickTime);
    }

    public static void registerItemPipe(int ID, Materials material, int aInvSlotCount, int aStepSize,
        boolean aIsRestrictive, int aTickTime) {
        String unName = material.mName;
        String name = GTLanguageManager.i18nPlaceholder ? "%material" : material.mDefaultLocalName;
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeTiny.get(material),
            new MTEItemPipe(
                ID,
                "GT_Pipe_" + unName + "_Tiny",
                "Tiny " + name + " Item Pipe",
                0.25F,
                material,
                Math.max(aInvSlotCount / 4, 1),
                aStepSize * 4,
                aIsRestrictive,
                aTickTime).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeSmall.get(material),
            new MTEItemPipe(
                ID + 1,
                "GT_Pipe_" + unName + "_Small",
                "Small " + name + " Item Pipe",
                0.375F,
                material,
                Math.max(aInvSlotCount / 2, 1),
                aStepSize * 2,
                aIsRestrictive,
                aTickTime).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeMedium.get(material),
            new MTEItemPipe(
                ID + 2,
                "GT_Pipe_" + unName,
                name + " Item Pipe",
                0.5F,
                material,
                aInvSlotCount,
                aStepSize,
                aIsRestrictive,
                aTickTime).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeLarge.get(material),
            new MTEItemPipe(
                ID + 3,
                "GT_Pipe_" + unName + "_Large",
                "Large " + name + " Item Pipe",
                0.75F,
                material,
                aInvSlotCount * 2,
                aStepSize / 2,
                aIsRestrictive,
                aTickTime).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeHuge.get(material),
            new MTEItemPipe(
                ID + 4,
                "GT_Pipe_" + unName + "_Huge",
                "Huge " + name + " Item Pipe",
                0.875F,
                material,
                aInvSlotCount * 4,
                aStepSize / 4,
                aIsRestrictive,
                aTickTime).getStackForm(1L));

        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeTiny, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeSmall, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeMedium, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeLarge, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeHuge, material, 1));
    }

    public static void registerFluidPipes(int startID, Werkstoff material, int transferRatePerSec, int heatResistance,
        boolean isGasProof) {
        registerFluidPipes(startID, material.getBridgeMaterial(), transferRatePerSec, heatResistance, isGasProof);
    }

    public static void registerFluidPipes(int startID, Materials material, int transferRatePerSec, int heatResistance,
        boolean isGasProof) {
        int transferRatePerTick = transferRatePerSec / 20;
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeTiny.get(material),
            new MTEFluidPipe(
                startID,
                "GT_Pipe_" + material.mDefaultLocalName + "_Tiny",
                "Tiny " + material.mDefaultLocalName + " Fluid Pipe",
                0.25F,
                material,
                transferRatePerTick * 2,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeSmall.get(material),
            new MTEFluidPipe(
                startID + 1,
                "GT_Pipe_" + material.mDefaultLocalName + "_Small",
                "Small " + material.mDefaultLocalName + " Fluid Pipe",
                0.375F,
                material,
                transferRatePerTick * 4,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeMedium.get(material),
            new MTEFluidPipe(
                startID + 2,
                "GT_Pipe_" + material.mDefaultLocalName,
                material.mDefaultLocalName + " Fluid Pipe",
                0.5F,
                material,
                transferRatePerTick * 12,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeLarge.get(material),
            new MTEFluidPipe(
                startID + 3,
                "GT_Pipe_" + material.mDefaultLocalName + "_Large",
                "Large " + material.mDefaultLocalName + " Fluid Pipe",
                0.75F,
                material,
                transferRatePerTick * 24,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeHuge.get(material),
            new MTEFluidPipe(
                startID + 4,
                "GT_Pipe_" + material.mDefaultLocalName + "_Huge",
                "Huge " + material.mDefaultLocalName + " Fluid Pipe",
                0.875F,
                material,
                transferRatePerTick * 48,
                heatResistance,
                isGasProof).getStackForm(1L));

        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeTiny, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeSmall, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeMedium, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeLarge, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.pipeHuge, material, 1));

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, material, 1),
                ItemList.Shape_Extruder_Pipe_Tiny.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeTiny, material, 2))
            .duration(material.getMass() * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, material, 1),
                ItemList.Shape_Extruder_Pipe_Small.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeSmall, material, 1))
            .duration(material.getMass() * 2 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, material, 3),
                ItemList.Shape_Extruder_Pipe_Medium.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeMedium, material, 1))
            .duration(material.getMass() * 6 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, material, 6),
                ItemList.Shape_Extruder_Pipe_Large.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeLarge, material, 1))
            .duration(material.getMass() * 12 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, material, 12),
                ItemList.Shape_Extruder_Pipe_Huge.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeHuge, material, 1))
            .duration(material.getMass() * 24 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Tiny.get(0))
            .fluidInputs(material.getMolten(1 * HALF_INGOTS))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeTiny, material, 1))
            .duration(material.getMass() * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Small.get(0))
            .fluidInputs(material.getMolten(1 * INGOTS))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeSmall, material, 1))
            .duration(material.getMass() * 2 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Medium.get(0))
            .fluidInputs(material.getMolten(3 * INGOTS))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeMedium, material, 1))
            .duration(material.getMass() * 6 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Large.get(0))
            .fluidInputs(material.getMolten(6 * INGOTS))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeLarge, material, 1))
            .duration(material.getMass() * 12 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Huge.get(0))
            .fluidInputs(material.getMolten(1728))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.pipeHuge, material, 1))
            .duration(material.getMass() * 24 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
    }

    public static void registerWires(int startID, Werkstoff material, int amperage, int voltage, int loss,
        boolean hasCover) {
        registerWires(startID, material.getBridgeMaterial(), amperage, voltage, loss, hasCover);
    }

    public static void registerWires(int ID, Materials material, int aAmperage, int aVoltage, int aLoss,
        boolean cover) {
        String unName = material.mDefaultLocalName.replace(" ", "_")
            .toLowerCase();
        String Name = material.mDefaultLocalName;
        String aTextWire1 = "wire.";
        String aTextCable1 = "cable.";
        String aTextWire2 = " Wire";
        String aTextCable2 = " Cable";
        int aLossInsulated = aLoss / 4;

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt01.get(material),
            new MTECable(
                ID + 0,
                aTextWire1 + unName + ".01",
                "1x " + Name + aTextWire2,
                0.125F,
                material,
                aLoss,
                1L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt02.get(material),
            new MTECable(
                ID + 1,
                aTextWire1 + unName + ".02",
                "2x " + Name + aTextWire2,
                0.25F,
                material,
                aLoss,
                2L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt04.get(material),
            new MTECable(
                ID + 2,
                aTextWire1 + unName + ".04",
                "4x " + Name + aTextWire2,
                0.375F,
                material,
                aLoss,
                4L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt08.get(material),
            new MTECable(
                ID + 3,
                aTextWire1 + unName + ".08",
                "8x " + Name + aTextWire2,
                0.5F,
                material,
                aLoss,
                8L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt12.get(material),
            new MTECable(
                ID + 4,
                aTextWire1 + unName + ".12",
                "12x " + Name + aTextWire2,
                0.625F,
                material,
                aLoss,
                12L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTOreDictUnificator.registerOre(
            OrePrefixes.wireGt16.get(material),
            new MTECable(
                ID + 5,
                aTextWire1 + unName + ".16",
                "16x " + Name + aTextWire2,
                0.75F,
                material,
                aLoss,
                16L * aAmperage,
                aVoltage,
                false,
                true).getStackForm(1L));

        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt01, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt02, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt04, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt08, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt12, material, 1));
        GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.wireGt16, material, 1));

        if (cover) {
            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt01.get(material),
                new MTECable(
                    ID + 6,
                    aTextCable1 + unName + ".01",
                    "1x " + Name + aTextCable2,
                    0.25F,
                    material,
                    aLossInsulated,
                    1L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt02.get(material),
                new MTECable(
                    ID + 7,
                    aTextCable1 + unName + ".02",
                    "2x " + Name + aTextCable2,
                    0.375F,
                    material,
                    aLossInsulated,
                    2L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt04.get(material),
                new MTECable(
                    ID + 8,
                    aTextCable1 + unName + ".04",
                    "4x " + Name + aTextCable2,
                    0.5F,
                    material,
                    aLossInsulated,
                    4L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt08.get(material),
                new MTECable(
                    ID + 9,
                    aTextCable1 + unName + ".08",
                    "8x " + Name + aTextCable2,
                    0.625F,
                    material,
                    aLossInsulated,
                    8L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt12.get(material),
                new MTECable(
                    ID + 10,
                    aTextCable1 + unName + ".12",
                    "12x " + Name + aTextCable2,
                    0.75F,
                    material,
                    aLossInsulated,
                    12L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTOreDictUnificator.registerOre(
                OrePrefixes.cableGt16.get(material),
                new MTECable(
                    ID + 11,
                    aTextCable1 + unName + ".16",
                    "16x " + Name + aTextCable2,
                    0.875F,
                    material,
                    aLossInsulated,
                    16L * aAmperage,
                    aVoltage,
                    true,
                    false).getStackForm(1L));

            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt01, material, 1));
            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt02, material, 1));
            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt04, material, 1));
            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt08, material, 1));
            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt12, material, 1));
            GTNLCreativeTabs.addToMachineList(GTOreDictUnificator.get(OrePrefixes.cableGt16, material, 1));
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 1), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt01, material, 2))
            .duration(5 * SECONDS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 1), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt02, material, 1))
            .duration(7 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 2), GTUtility.getIntegratedCircuit(4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt04, material, 1))
            .duration(10 * SECONDS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 4), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt08, material, 1))
            .duration(12 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 6), GTUtility.getIntegratedCircuit(12))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt12, material, 1))
            .duration(15 * SECONDS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, material, 8), GTUtility.getIntegratedCircuit(16))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt16, material, 1))
            .duration(17 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, material, 1), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt01, material, 1))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, material, 2), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt02, material, 1))
            .duration(5 * SECONDS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, material, 4), GTUtility.getIntegratedCircuit(4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt04, material, 1))
            .duration(7 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, material, 8), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt08, material, 1))
            .duration(10 * SECONDS)
            .eut(4)
            .addTo(wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, material, 12), GTUtility.getIntegratedCircuit(12))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt12, material, 1))
            .duration(12 * SECONDS + 10 * TICKS)
            .eut(4)
            .addTo(wiremillRecipes);
    }
}
