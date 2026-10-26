package com.science.gtnl.loader.machine;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.machine.basicMachine.DieselGenerator;
import com.science.gtnl.common.machine.basicMachine.GasTurbine;
import com.science.gtnl.common.machine.basicMachine.HydraulicSuperBuffer;
import com.science.gtnl.common.machine.basicMachine.LootBagRedemption;
import com.science.gtnl.common.machine.basicMachine.ManaTank;
import com.science.gtnl.common.machine.basicMachine.SmallEssentiaSmeltery;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerBronze;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerSteel;
import com.science.gtnl.common.machine.basicMachine.SteamTurbine;
import com.science.gtnl.common.machine.hatch.EnergyTransferNode;
import com.science.gtnl.common.machine.hatch.OredictInputBusME;
import com.science.gtnl.common.machine.hatch.OutputBusMEProxy;
import com.science.gtnl.common.machine.hatch.OutputHatchMEProxy;
import com.science.gtnl.common.machine.hatch.TypeFilteredInputBusME;
import com.science.gtnl.common.machine.hatch.WirelessVacuumConveyorInputHatch;
import com.science.gtnl.common.machine.hatch.WirelessVacuumConveyorOutputHatch;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.material.MaterialUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.text.AnimatedText;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;

public class BasicMachineLoader {

    public static void registry() {
        registerWireAndPipe();
        registerMachines();
    }

    public static void registerWireAndPipe() {
        MaterialUtils
            .registerWires(GTNLMachineID.STAR_GATE_WIRE.ID, GTNLMaterials.Stargate, 2147483647, 2147483647, 0, false);
        MaterialUtils.registerItemPipe(GTNLMachineID.BLUE_ALLOY_PIPE.ID, Materials.BlueAlloy, 8, 2048, false, 20);
        MaterialUtils.registerFluidPipes(
            GTNLMachineID.COMPRESSED_STEAM_PIPE.ID,
            GTNLMaterials.CompressedSteam,
            250000,
            10000,
            true);
        MaterialUtils.registerFluidPipes(GTNLMachineID.STRONZE_PIPE.ID, GTNLMaterials.Stronze, 15000, 10000, true);
        MaterialUtils.registerFluidPipes(GTNLMachineID.BREEL_PIPE.ID, GTNLMaterials.Breel, 10000, 10000, true);
    }

    public static void registerMachines() {
        GTNLItemList.SteamTurbineLV.set(
            new SteamTurbine(
                GTNLMachineID.STEAM_TURBINE_LV.ID,
                "SteamTurbineLV",
                StatCollector.translateToLocal("gtnl.machine.steam_turbine.lv.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamTurbineLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamTurbineMV.set(
            new SteamTurbine(
                GTNLMachineID.STEAM_TURBINE_MV.ID,
                "SteamTurbineMV",
                StatCollector.translateToLocal("gtnl.machine.steam_turbine.mv.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamTurbineMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamTurbineHV.set(
            new SteamTurbine(
                GTNLMachineID.STEAM_TURBINE_HV.ID,
                "SteamTurbineHV",
                StatCollector.translateToLocal("gtnl.machine.steam_turbine.hv.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamTurbineHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamAssemblerBronze.set(
            new SteamAssemblerBronze(
                GTNLMachineID.STEAM_ASSEMBLER_BRONZE.ID,
                "SteamAssembler",
                StatCollector.translateToLocal("gtnl.machine.steam_assembler.bronze.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamAssemblerBronze.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamAssemblerSteel.set(
            new SteamAssemblerSteel(
                GTNLMachineID.STEAM_ASSEMBLER_STEEL.ID,
                "HighPressureSteamAssembler",
                StatCollector.translateToLocal("gtnl.machine.steam_assembler.steel.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamAssemblerSteel.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HydraulicSuperBuffer.set(
            new HydraulicSuperBuffer(
                GTNLMachineID.HYDRAULIC_SUPER_BUFFER.ID,
                "HydraulicSuperBuffer",
                StatCollector.translateToLocal("gtnl.machine.hydraulic_super_buffer.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HydraulicSuperBuffer.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaTank.set(
            new ManaTank(
                GTNLMachineID.MANA_TANK.ID,
                "ManaTank",
                StatCollector.translateToLocal("gtnl.machine.mana_tank.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaTank.get(1), AnimatedText.GT_NOT_LEISURE);

        for (GasCollectorTier tier : GasCollectorTier.values()) {
            IMetaTileEntity mte = new MTEBasicMachineWithRecipe(
                tier.id.ID,
                tier.unlocalizedName,
                StatCollector.translateToLocal(tier.displayNameKey),
                tier.tier,
                new String[] { StatCollector.translateToLocal(tier.tooltipKey),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("gtnl.machine.gas_collector.recipe_type")
                        + EnumChatFormatting.RESET },
                GTNLRecipeMaps.GasCollectorRecipes,
                3,
                3,
                false,
                true,
                tier.tier * 100000000,
                null,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null);

            tier.itemEnum.set(mte);

            AnimatedTooltipHandler.addItemTooltip(tier.itemEnum.get(1), AnimatedText.GT_NOT_LEISURE);
        }

        GTNLItemList.GasTurbineLV.set(
            new GasTurbine(
                GTNLMachineID.GAS_TURBINE_LV.ID,
                "GasTurbineLV",
                StatCollector.translateToLocal("gtnl.machine.gas_turbine.lv.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GasTurbineLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.GasTurbineMV.set(
            new GasTurbine(
                GTNLMachineID.GAS_TURBINE_MV.ID,
                "GasTurbineMV",
                StatCollector.translateToLocal("gtnl.machine.gas_turbine.mv.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GasTurbineMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.GasTurbineHV.set(
            new GasTurbine(
                GTNLMachineID.GAS_TURBINE_HV.ID,
                "GasTurbineHV",
                StatCollector.translateToLocal("gtnl.machine.gas_turbine.hv.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GasTurbineHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DieselGeneratorLV.set(
            new DieselGenerator(
                GTNLMachineID.DIESEL_GENERATOR_LV.ID,
                "DieselGeneratorLV",
                StatCollector.translateToLocal("gtnl.machine.diesel_generator.lv.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DieselGeneratorLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DieselGeneratorMV.set(
            new DieselGenerator(
                GTNLMachineID.DIESEL_GENERATOR_MV.ID,
                "DieselGeneratorMV",
                StatCollector.translateToLocal("gtnl.machine.diesel_generator.mv.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DieselGeneratorMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DieselGeneratorHV.set(
            new DieselGenerator(
                GTNLMachineID.DIESEL_GENERATOR_HV.ID,
                "DieselGeneratorHV",
                StatCollector.translateToLocal("gtnl.machine.diesel_generator.hv.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DieselGeneratorHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SmallEssentiaSmelteryLV.set(
            new SmallEssentiaSmeltery(
                GTNLMachineID.SMALL_ESSENTIA_SMELTERY_LV.ID,
                "SmallEssentiaSmelteryLV",
                StatCollector.translateToLocal("gtnl.machine.small_essentia_smeltery.lv.name"),
                1));
        GTNLItemList.SmallEssentiaSmelteryMV.set(
            new SmallEssentiaSmeltery(
                GTNLMachineID.SMALL_ESSENTIA_SMELTERY_MV.ID,
                "SmallEssentiaSmelteryMV",
                StatCollector.translateToLocal("gtnl.machine.small_essentia_smeltery.mv.name"),
                2));
        GTNLItemList.SmallEssentiaSmelteryHV.set(
            new SmallEssentiaSmeltery(
                GTNLMachineID.SMALL_ESSENTIA_SMELTERY_HV.ID,
                "SmallEssentiaSmelteryHV",
                StatCollector.translateToLocal("gtnl.machine.small_essentia_smeltery.hv.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallEssentiaSmelteryLV.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SmallEssentiaSmelteryLV.get(1), AnimatedText.AUTHOR_LINEBROKE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallEssentiaSmelteryMV.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SmallEssentiaSmelteryMV.get(1), AnimatedText.AUTHOR_LINEBROKE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallEssentiaSmelteryHV.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SmallEssentiaSmelteryHV.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.EnergyTransferNode.set(
            new EnergyTransferNode(
                GTNLMachineID.ENERGY_TRANSFER_NODE.ID,
                "EnergyTransferNode",
                StatCollector.translateToLocal("gtnl.hatch.energy_transfer_node.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyTransferNode.get(1), AnimatedText.GT_NOT_LEISURE);

        if (Mods.EnhancedLootBags.isModLoaded()) {
            GTNLItemList.LootBagRedemption.set(
                new LootBagRedemption(
                    GTNLMachineID.LOOT_BAG_REDEMPTION.ID,
                    "LootBagRedemption",
                    StatCollector.translateToLocal("gtnl.machine.loot_bag_redemption.name"),
                    14));
            AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LootBagRedemption.get(1), AnimatedText.GT_NOT_LEISURE);
        }

        GTNLItemList.SuperOredictInputBusME.set(
            new OredictInputBusME(
                GTNLMachineID.SUPER_OREDICT_INPUT_BUS_HATCH_ME.ID,
                "SuperOredictInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.advanced.name"),
                true));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperOredictInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperTypeFilteredInputBusME.set(
            new TypeFilteredInputBusME(
                GTNLMachineID.SUPER_TYPE_FILTERED_INPUT_BUS_ME.ID,
                "SuperTypeFilteredInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.advanced.name"),
                true));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SuperTypeFilteredInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.OutputBusMEProxy.set(
            new OutputBusMEProxy(
                GTNLMachineID.OUTPUT_BUS_ME_PROXY.ID,
                "OutputBusMEProxy",
                StatCollector.translateToLocal("gtnl.hatch.output_bus_me_proxy.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OutputBusMEProxy.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.OutputHatchMEProxy.set(
            new OutputHatchMEProxy(
                GTNLMachineID.OUTPUT_HATCH_ME_PROXY.ID,
                "OutputHatchMEProxy",
                StatCollector.translateToLocal("gtnl.hatch.output_hatch_me_proxy.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OutputHatchMEProxy.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessVacuumConveyorInputHatch.set(
            new WirelessVacuumConveyorInputHatch(
                GTNLMachineID.WIRELESS_VACUUM_CONVEYOR_INPUT_HATCH.ID,
                "WirelessVacuumConveyorInputHatch",
                StatCollector.translateToLocal("gtnl.hatch.wireless_vacuum_conveyor.input.name"),
                11));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessVacuumConveyorInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessVacuumConveyorOutputHatch.set(
            new WirelessVacuumConveyorOutputHatch(
                GTNLMachineID.WIRELESS_VACUUM_CONVEYOR_OUTPUT_HATCH.ID,
                "WirelessVacuumConveyorOutputHatch",
                StatCollector.translateToLocal("gtnl.hatch.wireless_vacuum_conveyor.output.name"),
                11));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessVacuumConveyorOutputHatch.get(1), AnimatedText.GT_NOT_LEISURE);
    }

    public enum GasCollectorTier {

        LV(GTNLItemList.GasCollectorLV, GTNLMachineID.GAS_COLLECTOR_LV, "GasCollectorLV",
            "gtnl.machine.gas_collector.lv.name", 1, "gtnl.machine.gas_collector.tooltip.0"),
        MV(GTNLItemList.GasCollectorMV, GTNLMachineID.GAS_COLLECTOR_MV, "GasCollectorMV",
            "gtnl.machine.gas_collector.mv.name", 2, "gtnl.machine.gas_collector.tooltip.0"),
        HV(GTNLItemList.GasCollectorHV, GTNLMachineID.GAS_COLLECTOR_HV, "GasCollectorHV",
            "gtnl.machine.gas_collector.hv.name", 3, "gtnl.machine.gas_collector.tooltip.0"),
        EV(GTNLItemList.GasCollectorEV, GTNLMachineID.GAS_COLLECTOR_EV, "GasCollectorEV",
            "gtnl.machine.gas_collector.ev.name", 4, "gtnl.machine.gas_collector.tooltip.0"),
        IV(GTNLItemList.GasCollectorIV, GTNLMachineID.GAS_COLLECTOR_IV, "GasCollectorIV",
            "gtnl.machine.gas_collector.iv.name", 5, "gtnl.machine.gas_collector.tooltip.1"),
        LuV(GTNLItemList.GasCollectorLuV, GTNLMachineID.GAS_COLLECTOR_LUV, "GasCollectorLuV",
            "gtnl.machine.gas_collector.luv.name", 6, "gtnl.machine.gas_collector.tooltip.1"),
        ZPM(GTNLItemList.GasCollectorZPM, GTNLMachineID.GAS_COLLECTOR_ZPM, "GasCollectorZPM",
            "gtnl.machine.gas_collector.zpm.name", 7, "gtnl.machine.gas_collector.tooltip.1"),
        UV(GTNLItemList.GasCollectorUV, GTNLMachineID.GAS_COLLECTOR_UV, "GasCollectorUV",
            "gtnl.machine.gas_collector.uv.name", 8, "gtnl.machine.gas_collector.tooltip.2"),
        UHV(GTNLItemList.GasCollectorUHV, GTNLMachineID.GAS_COLLECTOR_UHV, "GasCollectorUHV",
            "gtnl.machine.gas_collector.uhv.name", 9, "gtnl.machine.gas_collector.tooltip.2"),
        UEV(GTNLItemList.GasCollectorUEV, GTNLMachineID.GAS_COLLECTOR_UEV, "GasCollectorUEV",
            "gtnl.machine.gas_collector.uev.name", 10, "gtnl.machine.gas_collector.tooltip.2"),
        UIV(GTNLItemList.GasCollectorUIV, GTNLMachineID.GAS_COLLECTOR_UIV, "GasCollectorUIV",
            "gtnl.machine.gas_collector.uiv.name", 11, "gtnl.machine.gas_collector.tooltip.2"),
        UMV(GTNLItemList.GasCollectorUMV, GTNLMachineID.GAS_COLLECTOR_UMV, "GasCollectorUMV",
            "gtnl.machine.gas_collector.umv.name", 12, "gtnl.machine.gas_collector.tooltip.2"),
        UXV(GTNLItemList.GasCollectorUXV, GTNLMachineID.GAS_COLLECTOR_UXV, "GasCollectorUXV",
            "gtnl.machine.gas_collector.uxv.name", 13, "gtnl.machine.gas_collector.tooltip.3");

        public final GTNLItemList itemEnum;
        public final GTNLMachineID id;
        public final String unlocalizedName;
        public final String displayNameKey;
        public final int tier;
        public final String tooltipKey;

        GasCollectorTier(GTNLItemList itemEnum, GTNLMachineID id, String name, String displayNameKey, int tier,
            String tooltipKey) {
            this.itemEnum = itemEnum;
            this.id = id;
            this.unlocalizedName = name;
            this.displayNameKey = displayNameKey;
            this.tier = tier;
            this.tooltipKey = tooltipKey;
        }
    }
}
