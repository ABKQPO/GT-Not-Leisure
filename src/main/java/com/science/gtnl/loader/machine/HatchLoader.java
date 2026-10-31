package com.science.gtnl.loader.machine;

import java.util.Set;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.ImmutableSet;
import com.gtnllib.client.tooltip.AnimatedTooltipHandler;
import com.gtnllib.mod.ModList;
import com.science.gtnl.common.machine.basicMachine.DebugResearchStation;
import com.science.gtnl.common.machine.basicMachine.Enchanting;
import com.science.gtnl.common.machine.basicMachine.EnergyMonitor;
import com.science.gtnl.common.machine.basicMachine.Replicator;
import com.science.gtnl.common.machine.basicMachine.StellarIrisController;
import com.science.gtnl.common.machine.hatch.BeamlinePipeMirror;
import com.science.gtnl.common.machine.hatch.BreelSteamInputBus;
import com.science.gtnl.common.machine.hatch.BreelSteamOutputBus;
import com.science.gtnl.common.machine.hatch.CustomDroneDownLinkHatch;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.CustomMaintenanceHatch;
import com.science.gtnl.common.machine.hatch.DebugDataAccessHatch;
import com.science.gtnl.common.machine.hatch.DebugEnergyHatch;
import com.science.gtnl.common.machine.hatch.DualInputHatch;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.machine.hatch.HumongousDualInputHatch;
import com.science.gtnl.common.machine.hatch.HumongousNinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;
import com.science.gtnl.common.machine.hatch.LaserDynamoHatch;
import com.science.gtnl.common.machine.hatch.LaserEnergyHatch;
import com.science.gtnl.common.machine.hatch.ManaDynamoHatch;
import com.science.gtnl.common.machine.hatch.ManaEnergyHatch;
import com.science.gtnl.common.machine.hatch.MultiDynamoHatch;
import com.science.gtnl.common.machine.hatch.MultiEnergyHatch;
import com.science.gtnl.common.machine.hatch.NinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.NuclearFluidHatch;
import com.science.gtnl.common.machine.hatch.NuclearItemBus;
import com.science.gtnl.common.machine.hatch.OredictInputBusME;
import com.science.gtnl.common.machine.hatch.OriginalInputHatch;
import com.science.gtnl.common.machine.hatch.OriginalOutputHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.StandardDynamoHatch;
import com.science.gtnl.common.machine.hatch.StandardEnergyHatch;
import com.science.gtnl.common.machine.hatch.StronzeInputHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputProxy;
import com.science.gtnl.common.machine.hatch.SuperDataAccessHatch;
import com.science.gtnl.common.machine.hatch.SuperDualInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperInputBusME;
import com.science.gtnl.common.machine.hatch.SuperInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperVoidBus;
import com.science.gtnl.common.machine.hatch.SuperVoidHatch;
import com.science.gtnl.common.machine.hatch.TapDynamoHatch;
import com.science.gtnl.common.machine.hatch.TypeFilteredInputBusME;
import com.science.gtnl.common.machine.hatch.VaultPortHatch;
import com.science.gtnl.common.machine.hatch.WirelessMultiDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessMultiEnergyHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamEnergyHatch;
import com.science.gtnl.common.machine.multiblock.IndustrialCrucible;
import com.science.gtnl.common.machine.multiblock.NuclearReactor;
import com.science.gtnl.common.machine.multiblock.ResearchCenter;
import com.science.gtnl.common.machine.multiblock.SiphonTurbine;
import com.science.gtnl.common.machine.multiblock.SmallArcaneAssembler;
import com.science.gtnl.common.machine.multiblock.SmallInfusionMatrix;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.text.AnimatedText;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import tectech.thing.CustomItemList;

public class HatchLoader {

    public static void registry() {
        GTNLItemList.BreelReinforcedSteamInputBus.set(
            new BreelSteamInputBus(
                GTNLMachineID.BREEL_STEAM_INPUT_BUS.ID,
                "breel.input_bus.steam",
                "gtnl.hatch.breel.input_bus.steam.name",
                0));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.BreelReinforcedSteamInputBus.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.BreelReinforcedSteamInputBus.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.BreelReinforcedSteamOutputBus.set(
            new BreelSteamOutputBus(
                GTNLMachineID.BREEL_STEAM_OUTPUT_BUS.ID,
                "breel.output_bus.steam",
                "gtnl.hatch.breel.output_bus.steam.name",
                0));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.BreelReinforcedSteamOutputBus.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.BreelReinforcedSteamOutputBus.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.StronzeReinforcedSteamInputHatch.set(
            new StronzeInputHatch(
                GTNLMachineID.STRONZE_STEAM_INPUT_HATCH.ID,
                "stronze.input_hatch.steam",
                "gtnl.hatch.stronze.input_hatch.steam.name",
                0));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.StronzeReinforcedSteamInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.StronzeReinforcedSteamInputHatch.get(1), AnimatedText.AUTHOR_LINEBROKE);

        Set<GTUtility.FluidId> acceptedFluids = new ObjectOpenHashSet<>();
        acceptedFluids.add(
            GTUtility.FluidId.create(
                GTNLMaterials.FluidMana.getFluidOrGas(1)
                    .getFluid()));

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            acceptedFluids.add(GTUtility.FluidId.create(FluidRegistry.getFluid("liquid mana")));
        }

        GTNLItemList.FluidManaInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.copyOf(acceptedFluids),
                512000,
                GTNLMachineID.FLUID_MANA_INPUT_HATCH.ID,
                "FluidManaInputHatch",
                "gtnl.hatch.fluid_mana_input.name",
                6));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FluidManaInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FluidIceInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.of(GTUtility.FluidId.create(Materials.Water.mSolid)),
                256000,
                GTNLMachineID.FLUID_ICE_INPUT_HATCH.ID,
                "FluidIceInputHatch",
                "gtnl.hatch.fluid_ice_input.name",
                5));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FluidIceInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FluidBlazeInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.of(GTUtility.FluidId.create(Materials.Blaze.mStandardMoltenFluid)),
                256000,
                GTNLMachineID.FLUID_BLAZE_INPUT_HATCH.ID,
                "FluidBlazeInputHatch",
                "gtnl.hatch.fluid_blaze_input.name",
                5));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FluidBlazeInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputHatchME.set(
            new SuperCraftingInputHatchME(
                GTNLMachineID.SUPER_CRAFTING_INPUT_HATCH_ME.ID,
                "SuperCraftingInputBuffer(ME)",
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_me.hatch.name"),
                true));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SuperCraftingInputHatchME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputBusME.set(
            new SuperCraftingInputHatchME(
                GTNLMachineID.SUPER_CRAFTING_INPUT_BUS_ME.ID,
                "SuperCraftingInputBus(ME)",
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_me.bus.name"),
                false));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperCraftingInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousSolidifierHatch.set(
            new HumongousSolidifierHatch(
                GTNLMachineID.HUMONGOUS_SOLIDIFIER_HATCH.ID,
                "HumongousSolidifierHatch",
                StatCollector.translateToLocal("gtnl.hatch.humongous_solidifier.name"),
                14));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousSolidifierHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DebugEnergyHatch.set(
            new DebugEnergyHatch(
                GTNLMachineID.DEBUG_ENERGY_HATCH.ID,
                "DebugEnergyHatch",
                StatCollector.translateToLocal("gtnl.hatch.debug_energy.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DebugEnergyHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchEV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_EV.ID,
                9,
                "NinefoldInputHatchEV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.ev.name"),
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchIV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_IV.ID,
                9,
                "NinefoldInputHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.iv.name"),
                5));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchLuV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_LUV.ID,
                9,
                "NinefoldInputHatchLuV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.luv.name"),
                6));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchLuV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchZPM.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_ZPM.ID,
                9,
                "NinefoldInputHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.zpm.name"),
                7));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UV.ID,
                9,
                "NinefoldInputHatchUV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.uv.name"),
                8));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUHV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UHV.ID,
                9,
                "NinefoldInputHatchUHV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.uhv.name"),
                9));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUEV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UEV.ID,
                9,
                "NinefoldInputHatchUEV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.uev.name"),
                10));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUIV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UIV.ID,
                9,
                "NinefoldInputHatchUIV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.uiv.name"),
                11));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUMV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UMV.ID,
                9,
                "NinefoldInputHatchUMV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.umv.name"),
                12));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUXV.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_UXV.ID,
                9,
                "NinefoldInputHatchUXV",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.uxv.name"),
                13));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchUXV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchMAX.set(
            new NinefoldInputHatch(
                GTNLMachineID.NINEFOLD_INPUT_HATCH_MAX.ID,
                9,
                "NinefoldInputHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.ninefold_input.max.name"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NinefoldInputHatchMAX.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousNinefoldInputHatch.set(
            new HumongousNinefoldInputHatch(
                GTNLMachineID.HUMONGOUS_NINEFOLD_INPUT_HATCH.ID,
                9,
                "HumongousNinefoldInputHatch",
                StatCollector.translateToLocal("gtnl.hatch.humongous_ninefold_input.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousNinefoldInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchLV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_LV.ID,
                "DualInputHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.lv.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchMV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_MV.ID,
                "DualInputHatchMV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.mv.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchHV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_HV.ID,
                "DualInputHatchHV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.hv.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchEV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_EV.ID,
                "DualInputHatchEV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.ev.name"),
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchIV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_IV.ID,
                "DualInputHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.iv.name"),
                5));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchLuV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_LUV.ID,
                "DualInputHatchLuV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.luv.name"),
                6));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchLuV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchZPM.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_ZPM.ID,
                "DualInputHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.zpm.name"),
                7));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UV.ID,
                "DualInputHatchUV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.uv.name"),
                8));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUHV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UHV.ID,
                "DualInputHatchUHV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.uhv.name"),
                9));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUEV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UEV.ID,
                "DualInputHatchUEV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.uev.name"),
                10));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUIV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UIV.ID,
                "DualInputHatchUIV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.uiv.name"),
                11));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUMV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UMV.ID,
                "DualInputHatchUMV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.umv.name"),
                12));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchUXV.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_UXV.ID,
                "DualInputHatchUXV",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.uxv.name"),
                13));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchUXV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DualInputHatchMAX.set(
            new DualInputHatch(
                GTNLMachineID.DUAL_INPUT_HATCH_MAX.ID,
                "DualInputHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.dual_input.max.name"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DualInputHatchMAX.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputProxy.set(
            new SuperCraftingInputProxy(
                GTNLMachineID.SUPER_CRAFTING_INPUT_PROXY.ID,
                "SuperCraftingInputProxy",
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.name")).getStackForm(1L));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperCraftingInputProxy.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperDataAccessHatch.set(
            new SuperDataAccessHatch(
                GTNLMachineID.SUPER_DATA_ACCESS_HATCH.ID,
                "SuperDataAccessHatch",
                StatCollector.translateToLocal("gtnl.hatch.super_data_access.name"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperDataAccessHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.BigSteamInputHatch.set(
            new CustomFluidHatch(
                SteamTypes.getSupportedFluids(),
                4096000,
                GTNLMachineID.BIG_STEAM_INPUT_HATCH.ID,
                "BigSteamInputHatch",
                "gtnl.hatch.big_steam_input.name",
                1,
                true));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.BigSteamInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchLV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_LV.ID,
                "ParallelControllerHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.lv.name"),
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchMV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_MV.ID,
                "ParallelControllerHatchMV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.mv.name"),
                2));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchHV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_HV.ID,
                "ParallelControllerHatchHV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.hv.name"),
                3));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchEV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_EV.ID,
                "ParallelControllerHatchEV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.ev.name"),
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchIV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_IV.ID,
                "ParallelControllerHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.iv.name"),
                5));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchLuV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_LUV.ID,
                "ParallelControllerHatchLuV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.luv.name"),
                6));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchLuV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchZPM.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_ZPM.ID,
                "ParallelControllerHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.zpm.name"),
                7));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UV.ID,
                "ParallelControllerHatchUV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.uv.name"),
                8));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUHV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UHV.ID,
                "ParallelControllerHatchUHV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.uhv.name"),
                9));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUEV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UEV.ID,
                "ParallelControllerHatchUEV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.uev.name"),
                10));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUIV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UIV.ID,
                "ParallelControllerHatchUIV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.uiv.name"),
                11));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUMV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UMV.ID,
                "ParallelControllerHatchUMV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.umv.name"),
                12));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUXV.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_UXV.ID,
                "ParallelControllerHatchUXV",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.uxv.name"),
                13));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchUXV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchMAX.set(
            new ParallelControllerHatch(
                GTNLMachineID.PARALLEL_CONTROLLER_HATCH_MAX.ID,
                "ParallelControllerHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.parallel_controller.max.name"),
                14));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ParallelControllerHatchMAX.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.TapDynamoHatchLV.set(
            new TapDynamoHatch(
                GTNLMachineID.TAP_DYNAMO_HATCH.ID,
                "TapDynamoHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.tap_dynamo.lv.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.TapDynamoHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PipelessSteamHatch.set(
            new WirelessSteamEnergyHatch(
                GTNLMachineID.PIPELESS_STEAM_HATCH.ID,
                "PipelessSteamHatch",
                "gtnl.hatch.pipeless_steam.hatch.name",
                0));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PipelessSteamHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PipelessSteamVent.set(
            new WirelessSteamDynamoHatch(
                GTNLMachineID.PIPELESS_STEAM_VENT.ID,
                "PipelessSteamVent",
                StatCollector.translateToLocal("gtnl.hatch.pipeless_steam.vent.name"),
                0));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PipelessSteamVent.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PipelessJetstreamHatch.set(
            new WirelessSteamEnergyHatch(
                GTNLMachineID.PIPELESS_JETSTREAM_HATCH.ID,
                "PipelessJetstreamHatch",
                "gtnl.hatch.pipeless_jetstream.hatch.name",
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PipelessJetstreamHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PipelessJetstreamVent.set(
            new WirelessSteamDynamoHatch(
                GTNLMachineID.PIPELESS_JETSTREAM_VENT.ID,
                "PipelessJetstreamVent",
                StatCollector.translateToLocal("gtnl.hatch.pipeless_jetstream.vent.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PipelessJetstreamVent.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AutoConfigurationMaintenanceHatch.set(
            new CustomMaintenanceHatch(
                GTNLMachineID.AUTO_CONFIGURATION_MAINTENANCE_HATCH.ID,
                "AutoConfigurationMaintenanceHatch",
                "gtnl.hatch.auto_configuration_maintenance.name",
                80,
                120,
                5,
                new String[] { StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.2"), }));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.AutoConfigurationMaintenanceHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ConfigurationDroneDownLinkHatch.set(
            new CustomDroneDownLinkHatch(
                GTNLMachineID.CONFIGURATION_DRONE_DOWN_LINK_HATCH.ID,
                "ConfigurationDroneDownLinkHatch",
                StatCollector.translateToLocal("gtnl.hatch.configuration_drone_downlink.name"),
                80,
                120,
                5,
                new String[] { StatCollector.translateToLocal("gtnl.hatch.configuration_drone_downlink.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.hatch.configuration_drone_downlink.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.hatch.configuration_drone_downlink.tooltip.2"), }));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ConfigurationDroneDownLinkHatch.get(1), AnimatedText.GTNL_LONEI);

        GTNLItemList.ExplosionDynamoHatch.set(
            new ExplosionDynamoHatch(
                GTNLMachineID.EXPLOSION_DYNAMO_HATCH.ID,
                "ExplosionDynamoHatch",
                StatCollector.translateToLocal("gtnl.hatch.explosion_dynamo.name"),
                5));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ExplosionDynamoHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DebugResearchStation.set(
            new DebugResearchStation(
                GTNLMachineID.DEBUG_RESEARCH_STATION.ID,
                "DebugResearchStation",
                StatCollector.translateToLocal("gtnl.machine.debug_research_station.name"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DebugResearchStation.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ResearchCenter.set(
            new ResearchCenter(
                GTNLMachineID.RESEARCH_CENTER.ID,
                "ResearchCenter",
                StatCollector.translateToLocal("gtnl.machine.research_center.name")));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.ResearchCenter.get(1),
            () -> StatCollector.translateToLocal("gtnl.machine.research_station.tooltip.0"));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ResearchCenter.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NuclearReactor.set(
            new NuclearReactor(
                GTNLMachineID.NUCLEAR_REACTOR.ID,
                "NuclearReactor",
                StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NuclearReactor.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SiphonTurbine.set(
            new SiphonTurbine(
                GTNLMachineID.SIPHON_TURBINE.ID,
                "SiphonTurbine",
                StatCollector.translateToLocal("gtnl.machine.siphon_turbine.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SiphonTurbine.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SmallInfusionMatrix.set(
            new SmallInfusionMatrix(
                GTNLMachineID.SMALL_INFUSION_MATRIX.ID,
                "SmallInfusionMatrix",
                StatCollector.translateToLocal("gtnl.machine.small_infusion_matrix.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallInfusionMatrix.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallInfusionMatrix.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.SmallArcaneAssembler.set(
            new SmallArcaneAssembler(
                GTNLMachineID.SMALL_ARCANE_ASSEMBLER.ID,
                "SmallArcaneAssembler",
                StatCollector.translateToLocal("gtnl.machine.small_arcane_assembler.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallArcaneAssembler.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmallArcaneAssembler.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.IndustrialCrucible.set(
            new IndustrialCrucible(
                GTNLMachineID.INDUSTRIAL_CRUCIBLE.ID,
                "IndustrialCrucible",
                StatCollector.translateToLocal("gtnl.machine.industrial_crucible.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.IndustrialCrucible.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.IndustrialCrucible.get(1), AnimatedText.AUTHOR_LINEBROKE);

        GTNLItemList.SuperInputBusME.set(
            new SuperInputBusME(
                GTNLMachineID.SUPER_INPUT_BUS_ME.ID,
                false,
                "SuperInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AdvancedSuperInputBusME.set(
            new SuperInputBusME(
                GTNLMachineID.ADVANCED_SUPER_INPUT_BUS_ME.ID,
                true,
                "AdvancedSuperInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.super_input_bus_me.advanced.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AdvancedSuperInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperInputHatchME.set(
            new SuperInputHatchME(
                GTNLMachineID.SUPER_INPUT_HATCH_ME.ID,
                false,
                "SuperInputHatchME",
                StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperInputHatchME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AdvancedSuperInputHatchME.set(
            new SuperInputHatchME(
                GTNLMachineID.ADVANCED_SUPER_INPUT_HATCH_ME.ID,
                true,
                "AdvancedSuperInputHatchME",
                StatCollector.translateToLocal("gtnl.hatch.super_input_hatch_me.advanced.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.AdvancedSuperInputHatchME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.Replicator.set(
            new Replicator(
                GTNLMachineID.REPLICATOR.ID,
                "Replicator",
                StatCollector.translateToLocal("gtnl.machine.replicator.name"),
                7));

        GTNLItemList.Enchanting.set(
            new Enchanting(
                GTNLMachineID.ENCHANTING.ID,
                "Enchanting",
                StatCollector.translateToLocal("gtnl.machine.enchanting.name"),
                7));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Enchanting.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyMonitor.set(
            new EnergyMonitor(
                GTNLMachineID.ENERGY_MONITOR.ID,
                "EnergyMonitor",
                StatCollector.translateToLocal("gtnl.machine.energy_monitor.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyMonitor.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.StellarIrisController.set(
            new StellarIrisController(
                GTNLMachineID.STELLAR_IRIS_CONTROLLER.ID,
                "StellarIrisController",
                StatCollector.translateToLocal("gtnl.machine.stellar_iris_controller.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.StellarIrisController.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.OredictInputBusME.set(
            new OredictInputBusME(
                GTNLMachineID.OREDICT_INPUT_BUS_HATCH_ME.ID,
                "OredictInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.oredict_input_bus_me.name"),
                false));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OredictInputBusME.get(1), AnimatedText.GTNL_SKYINR);

        GTNLItemList.VaultPortHatch.set(
            new VaultPortHatch(
                GTNLMachineID.VAULT_PORT_HATCH.ID,
                "VaultPortHatch",
                StatCollector.translateToLocal("gtnl.hatch.vault_port.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.VaultPortHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.OriginalInputHatch.set(
            new OriginalInputHatch(
                GTNLMachineID.ORIGINAL_INPUT_HATCH.ID,
                "OriginalInputHatch",
                StatCollector.translateToLocal("gtnl.hatch.original_input.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OriginalInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.OriginalOutputHatch.set(
            new OriginalOutputHatch(
                GTNLMachineID.ORIGINAL_OUTPUT_HATCH.ID,
                "OriginalOutputHatch",
                StatCollector.translateToLocal("gtnl.hatch.original_output.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OriginalOutputHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperVoidBus.set(
            new SuperVoidBus(
                GTNLMachineID.SUPER_VOID_BUS.ID,
                "SuperVoidBus",
                StatCollector.translateToLocal("gtnl.hatch.super_void_bus.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperVoidBus.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperVoidHatch.set(
            new SuperVoidHatch(
                GTNLMachineID.SUPER_VOID_HATCH.ID,
                "SuperVoidHatch",
                StatCollector.translateToLocal("gtnl.hatch.super_void_hatch.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperVoidHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NuclearItemBus.set(
            new NuclearItemBus(
                GTNLMachineID.NUCLEAR_ITEM_BUS.ID,
                "NuclearItemBus",
                StatCollector.translateToLocal("gtnl.hatch.nuclear_item_bus.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NuclearItemBus.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NuclearFluidHatch.set(
            new NuclearFluidHatch(
                GTNLMachineID.NUCLEAR_FLUID_HATCH.ID,
                "NuclearFluidHatch",
                StatCollector.translateToLocal("gtnl.hatch.nuclear_fluid_hatch.name"),
                3,
                512000)); // 64000 x 8
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NuclearFluidHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DebugDataAccessHatch.set(
            new DebugDataAccessHatch(
                GTNLMachineID.DEBUG_DATA_ACCESS_HATCH.ID,
                "DebugDataAccessHatch",
                StatCollector.translateToLocal("gtnl.hatch.debug_data_access.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DebugDataAccessHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchLV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_LV.ID,
                "HumongousDualInputHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.lv.name"),
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchMV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_MV.ID,
                "HumongousDualInputHatchMV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.mv.name"),
                2));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchHV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_HV.ID,
                "HumongousDualInputHatchHV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.hv.name"),
                3));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchEV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_EV.ID,
                "HumongousDualInputHatchEV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.ev.name"),
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchIV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_IV.ID,
                "HumongousDualInputHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.iv.name"),
                5));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchLuV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_LUV.ID,
                "HumongousDualInputHatchLuV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.luv.name"),
                6));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchLuV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchZPM.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_ZPM.ID,
                "HumongousDualInputHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.zpm.name"),
                7));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UV.ID,
                "HumongousDualInputHatchUV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.uv.name"),
                8));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUHV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UHV.ID,
                "HumongousDualInputHatchUHV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.uhv.name"),
                9));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUEV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UEV.ID,
                "HumongousDualInputHatchUEV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.uev.name"),
                10));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUEV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUIV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UIV.ID,
                "HumongousDualInputHatchUIV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.uiv.name"),
                11));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUMV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UMV.ID,
                "HumongousDualInputHatchUMV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.umv.name"),
                12));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUMV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUXV.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_UXV.ID,
                "HumongousDualInputHatchUXV",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.uxv.name"),
                13));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchUXV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchMAX.set(
            new HumongousDualInputHatch(
                GTNLMachineID.HUMONGOUS_DUAL_INPUT_HATCH_MAX.ID,
                "HumongousDualInputHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.humongous_dual_input.max.name"),
                14));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousDualInputHatchMAX.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SterileConfigurationMaintenanceHatch.set(
            new CustomMaintenanceHatch(
                GTNLMachineID.STERILE_CONFIGURATION_MAINTENANCE_HATCH.ID,
                "SterileConfigurationMaintenanceHatch",
                "gtnl.hatch.sterile_configuration_maintenance.name",
                70,
                130,
                9,
                new String[] { StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.2"), }));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SterileConfigurationMaintenanceHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousConfigurationMaintenanceHatch.set(
            new CustomMaintenanceHatch(
                GTNLMachineID.HUMONGOUS_CONFIGURATION_MAINTENANCE_HATCH.ID,
                "HumongousConfigurationMaintenanceHatch",
                "gtnl.hatch.humongous_configuration_maintenance.name",
                50,
                150,
                14,
                new String[] { StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.0"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.1"),
                    StatCollector.translateToLocal("gtnl.hatch.auto_configuration_maintenance.tooltip.2"), }));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousConfigurationMaintenanceHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchLV.set(
            new ManaDynamoHatch(
                GTNLMachineID.MANA_DYNAMO_HATCH_LV.ID,
                "ManaDynamoHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.mana_dynamo.lv.name"),
                1,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaDynamoHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchHV.set(
            new ManaDynamoHatch(
                GTNLMachineID.MANA_DYNAMO_HATCH_HV.ID,
                "ManaDynamoHatchHV",
                StatCollector.translateToLocal("gtnl.hatch.mana_dynamo.hv.name"),
                3,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaDynamoHatchHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchIV.set(
            new ManaDynamoHatch(
                GTNLMachineID.MANA_DYNAMO_HATCH_IV.ID,
                "ManaDynamoHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.mana_dynamo.iv.name"),
                5,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaDynamoHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchZPM.set(
            new ManaDynamoHatch(
                GTNLMachineID.MANA_DYNAMO_HATCH_ZPM.ID,
                "ManaDynamoHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.mana_dynamo.zpm.name"),
                7,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaDynamoHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchLV.set(
            new ManaEnergyHatch(
                GTNLMachineID.MANA_ENERGY_HATCH_LV.ID,
                "ManaEnergyHatchLV",
                StatCollector.translateToLocal("gtnl.hatch.mana_energy.lv.name"),
                1,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaEnergyHatchLV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchHV.set(
            new ManaEnergyHatch(
                GTNLMachineID.MANA_ENERGY_HATCH_HV.ID,
                "ManaEnergyHatchHV",
                StatCollector.translateToLocal("gtnl.hatch.mana_energy.hv.name"),
                3,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaEnergyHatchHV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchIV.set(
            new ManaEnergyHatch(
                GTNLMachineID.MANA_ENERGY_HATCH_IV.ID,
                "ManaEnergyHatchIV",
                StatCollector.translateToLocal("gtnl.hatch.mana_energy.iv.name"),
                5,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaEnergyHatchIV.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchZPM.set(
            new ManaEnergyHatch(
                GTNLMachineID.MANA_ENERGY_HATCH_ZPM.ID,
                "ManaEnergyHatchZPM",
                StatCollector.translateToLocal("gtnl.hatch.mana_energy.zpm.name"),
                7,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ManaEnergyHatchZPM.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.TypeFilteredInputBusME.set(
            new TypeFilteredInputBusME(
                GTNLMachineID.TYPE_FILTERED_INPUT_BUS_ME.ID,
                "TypeFilteredInputBusME",
                StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.name"),
                false));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.TypeFilteredInputBusME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SuperDualInputHatchME.set(
            new SuperDualInputHatchME(
                GTNLMachineID.SUPER_DUAL_INPUT_HATCH_ME.ID,
                "SuperDualInputHatchME",
                StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.name"),
                9,
                false));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperDualInputHatchME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AdvancedSuperDualInputHatchME.set(
            new SuperDualInputHatchME(
                GTNLMachineID.ADVANCED_SUPER_DUAL_INPUT_HATCH_ME.ID,
                "AdvancedSuperDualInputHatchME",
                StatCollector.translateToLocal("gtnl.hatch.super_dual_input_me.advanced.name"),
                10,
                true));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.AdvancedSuperDualInputHatchME.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV.set(ItemList.Hatch_Energy_LV.get(1));

        GTNLItemList.EnergyHatchLV4A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_LV_4A.ID,
                "EnergyHatchLV4A",
                StatCollector.translateToLocal("gtnl.hatch.energy.lv.4.name"),
                1,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchLV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV16A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_LV_16A.ID,
                "EnergyHatchLV16A",
                StatCollector.translateToLocal("gtnl.hatch.energy.lv.16.name"),
                1,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchLV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV64A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_LV_64A.ID,
                "EnergyHatchLV64A",
                StatCollector.translateToLocal("gtnl.hatch.energy.lv.64.name"),
                1,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchLV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV.set(ItemList.Hatch_Energy_MV.get(1));

        GTNLItemList.EnergyHatchMV4A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MV_4A.ID,
                "EnergyHatchMV4A",
                StatCollector.translateToLocal("gtnl.hatch.energy.mv.4.name"),
                2,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV16A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MV_16A.ID,
                "EnergyHatchMV16A",
                StatCollector.translateToLocal("gtnl.hatch.energy.mv.16.name"),
                2,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV64A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MV_64A.ID,
                "EnergyHatchMV64A",
                StatCollector.translateToLocal("gtnl.hatch.energy.mv.64.name"),
                2,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV.set(ItemList.Hatch_Energy_HV.get(1));

        GTNLItemList.EnergyHatchHV4A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_HV_4A.ID,
                "EnergyHatchHV4A",
                StatCollector.translateToLocal("gtnl.hatch.energy.hv.4.name"),
                3,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchHV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV16A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_HV_16A.ID,
                "EnergyHatchHV16A",
                StatCollector.translateToLocal("gtnl.hatch.energy.hv.16.name"),
                3,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchHV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV64A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_HV_64A.ID,
                "EnergyHatchHV64A",
                StatCollector.translateToLocal("gtnl.hatch.energy.hv.64.name"),
                3,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchHV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchEV.set(ItemList.Hatch_Energy_EV.get(1));

        GTNLItemList.EnergyHatchEV4A.set(CustomItemList.eM_energyMulti4_EV.get(1));

        GTNLItemList.EnergyHatchEV16A.set(CustomItemList.eM_energyMulti16_EV.get(1));

        GTNLItemList.EnergyHatchEV64A.set(CustomItemList.eM_energyMulti64_EV.get(1));

        GTNLItemList.EnergyHatchIV.set(ItemList.Hatch_Energy_IV.get(1));

        GTNLItemList.EnergyHatchIV4A.set(CustomItemList.eM_energyMulti4_IV.get(1));

        GTNLItemList.EnergyHatchIV16A.set(CustomItemList.eM_energyMulti16_IV.get(1));

        GTNLItemList.EnergyHatchIV64A.set(CustomItemList.eM_energyMulti64_IV.get(1));

        GTNLItemList.EnergyHatchLuV.set(ItemList.Hatch_Energy_LuV.get(1));

        GTNLItemList.EnergyHatchLuV4A.set(CustomItemList.eM_energyMulti4_LuV.get(1));

        GTNLItemList.EnergyHatchLuV16A.set(CustomItemList.eM_energyMulti16_LuV.get(1));

        GTNLItemList.EnergyHatchLuV64A.set(CustomItemList.eM_energyMulti64_LuV.get(1));

        GTNLItemList.EnergyHatchZPM.set(ItemList.Hatch_Energy_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM4A.set(CustomItemList.eM_energyMulti4_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM16A.set(CustomItemList.eM_energyMulti16_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM64A.set(CustomItemList.eM_energyMulti64_ZPM.get(1));

        GTNLItemList.EnergyHatchUV.set(ItemList.Hatch_Energy_UV.get(1));

        GTNLItemList.EnergyHatchUV4A.set(CustomItemList.eM_energyMulti4_UV.get(1));

        GTNLItemList.EnergyHatchUV16A.set(CustomItemList.eM_energyMulti16_UV.get(1));

        GTNLItemList.EnergyHatchUV64A.set(CustomItemList.eM_energyMulti64_UV.get(1));

        GTNLItemList.EnergyHatchUHV.set(ItemList.Hatch_Energy_UHV.get(1));

        GTNLItemList.EnergyHatchUHV4A.set(CustomItemList.eM_energyMulti4_UHV.get(1));

        GTNLItemList.EnergyHatchUHV16A.set(CustomItemList.eM_energyMulti16_UHV.get(1));

        GTNLItemList.EnergyHatchUHV64A.set(CustomItemList.eM_energyMulti64_UHV.get(1));

        GTNLItemList.EnergyHatchUEV.set(ItemList.Hatch_Energy_UEV.get(1));

        GTNLItemList.EnergyHatchUEV4A.set(CustomItemList.eM_energyMulti4_UEV.get(1));

        GTNLItemList.EnergyHatchUEV16A.set(CustomItemList.eM_energyMulti16_UEV.get(1));

        GTNLItemList.EnergyHatchUEV64A.set(CustomItemList.eM_energyMulti64_UEV.get(1));

        GTNLItemList.EnergyHatchUIV.set(ItemList.Hatch_Energy_UIV.get(1));

        GTNLItemList.EnergyHatchUIV4A.set(CustomItemList.eM_energyMulti4_UIV.get(1));

        GTNLItemList.EnergyHatchUIV16A.set(CustomItemList.eM_energyMulti16_UIV.get(1));

        GTNLItemList.EnergyHatchUIV64A.set(CustomItemList.eM_energyMulti64_UIV.get(1));

        GTNLItemList.EnergyHatchUMV.set(ItemList.Hatch_Energy_UMV.get(1));

        GTNLItemList.EnergyHatchUMV4A.set(CustomItemList.eM_energyMulti4_UMV.get(1));

        GTNLItemList.EnergyHatchUMV16A.set(CustomItemList.eM_energyMulti16_UMV.get(1));

        GTNLItemList.EnergyHatchUMV64A.set(CustomItemList.eM_energyMulti64_UMV.get(1));

        GTNLItemList.EnergyHatchUXV.set(ItemList.Hatch_Energy_UXV.get(1));

        GTNLItemList.EnergyHatchUXV4A.set(CustomItemList.eM_energyMulti4_UXV.get(1));

        GTNLItemList.EnergyHatchUXV16A.set(CustomItemList.eM_energyMulti16_UXV.get(1));

        GTNLItemList.EnergyHatchUXV64A.set(CustomItemList.eM_energyMulti64_UXV.get(1));

        GTNLItemList.EnergyHatchMAX.set(
            new StandardEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MAX.ID,
                "EnergyHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.energy.max.standard.name"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMAX.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX4A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MAX_4A.ID,
                "EnergyHatchMAX4A",
                StatCollector.translateToLocal("gtnl.hatch.energy.max.4.name"),
                14,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMAX4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX16A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MAX_16A.ID,
                "EnergyHatchMAX16A",
                StatCollector.translateToLocal("gtnl.hatch.energy.max.16.name"),
                14,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMAX16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX64A.set(
            new MultiEnergyHatch(
                GTNLMachineID.ENERGY_HATCH_MAX_64A.ID,
                "EnergyHatchMAX64A",
                StatCollector.translateToLocal("gtnl.hatch.energy.max.64.name"),
                14,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyHatchMAX64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.DynamoHatchLV.set(ItemList.Hatch_Dynamo_LV.get(1));

        GTNLItemList.DynamoHatchLV4A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_LV_4A.ID,
                "DynamoHatchLV4A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.lv.4.name"),
                1,
                4));

        GTNLItemList.DynamoHatchLV16A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_LV_16A.ID,
                "DynamoHatchLV16A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.lv.16.name"),
                1,
                16));

        GTNLItemList.DynamoHatchLV64A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_LV_64A.ID,
                "DynamoHatchLV64A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.lv.64.name"),
                1,
                64));

        GTNLItemList.DynamoHatchMV.set(ItemList.Hatch_Dynamo_MV.get(1));

        GTNLItemList.DynamoHatchMV4A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MV_4A.ID,
                "DynamoHatchMV4A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.mv.4.name"),
                2,
                4));

        GTNLItemList.DynamoHatchMV16A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MV_16A.ID,
                "DynamoHatchMV16A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.mv.16.name"),
                2,
                16));

        GTNLItemList.DynamoHatchMV64A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MV_64A.ID,
                "DynamoHatchMV64A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.mv.64.name"),
                2,
                64));

        GTNLItemList.DynamoHatchHV.set(ItemList.Hatch_Dynamo_HV.get(1));

        GTNLItemList.DynamoHatchHV4A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_HV_4A.ID,
                "DynamoHatchHV4A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.hv.4.name"),
                3,
                4));

        GTNLItemList.DynamoHatchHV16A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_HV_16A.ID,
                "DynamoHatchHV16A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.hv.16.name"),
                3,
                16));

        GTNLItemList.DynamoHatchHV64A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_HV_64A.ID,
                "DynamoHatchHV64A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.hv.64.name"),
                3,
                64));

        GTNLItemList.DynamoHatchEV.set(ItemList.Hatch_Dynamo_EV.get(1));

        GTNLItemList.DynamoHatchEV4A.set(CustomItemList.eM_dynamoMulti4_EV.get(1));

        GTNLItemList.DynamoHatchEV16A.set(CustomItemList.eM_dynamoMulti16_EV.get(1));

        GTNLItemList.DynamoHatchEV64A.set(CustomItemList.eM_dynamoMulti64_EV.get(1));

        GTNLItemList.DynamoHatchIV.set(ItemList.Hatch_Dynamo_IV.get(1));

        GTNLItemList.DynamoHatchIV4A.set(CustomItemList.eM_dynamoMulti4_IV.get(1));

        GTNLItemList.DynamoHatchIV16A.set(CustomItemList.eM_dynamoMulti16_IV.get(1));

        GTNLItemList.DynamoHatchIV64A.set(CustomItemList.eM_dynamoMulti64_IV.get(1));

        GTNLItemList.DynamoHatchLuV.set(ItemList.Hatch_Dynamo_LuV.get(1));

        GTNLItemList.DynamoHatchLuV4A.set(CustomItemList.eM_dynamoMulti4_LuV.get(1));

        GTNLItemList.DynamoHatchLuV16A.set(CustomItemList.eM_dynamoMulti16_LuV.get(1));

        GTNLItemList.DynamoHatchLuV64A.set(CustomItemList.eM_dynamoMulti64_LuV.get(1));

        GTNLItemList.DynamoHatchZPM.set(ItemList.Hatch_Dynamo_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM4A.set(CustomItemList.eM_dynamoMulti4_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM16A.set(CustomItemList.eM_dynamoMulti16_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM64A.set(CustomItemList.eM_dynamoMulti64_ZPM.get(1));

        GTNLItemList.DynamoHatchUV.set(ItemList.Hatch_Dynamo_UV.get(1));

        GTNLItemList.DynamoHatchUV4A.set(CustomItemList.eM_dynamoMulti4_UV.get(1));

        GTNLItemList.DynamoHatchUV16A.set(CustomItemList.eM_dynamoMulti16_UV.get(1));

        GTNLItemList.DynamoHatchUV64A.set(CustomItemList.eM_dynamoMulti64_UV.get(1));

        GTNLItemList.DynamoHatchUHV.set(ItemList.Hatch_Dynamo_UHV.get(1));

        GTNLItemList.DynamoHatchUHV4A.set(CustomItemList.eM_dynamoMulti4_UHV.get(1));

        GTNLItemList.DynamoHatchUHV16A.set(CustomItemList.eM_dynamoMulti16_UHV.get(1));

        GTNLItemList.DynamoHatchUHV64A.set(CustomItemList.eM_dynamoMulti64_UHV.get(1));

        GTNLItemList.DynamoHatchUEV.set(ItemList.Hatch_Dynamo_UEV.get(1));

        GTNLItemList.DynamoHatchUEV4A.set(CustomItemList.eM_dynamoMulti4_UEV.get(1));

        GTNLItemList.DynamoHatchUEV16A.set(CustomItemList.eM_dynamoMulti16_UEV.get(1));

        GTNLItemList.DynamoHatchUEV64A.set(CustomItemList.eM_dynamoMulti64_UEV.get(1));

        GTNLItemList.DynamoHatchUIV.set(ItemList.Hatch_Dynamo_UIV.get(1));

        GTNLItemList.DynamoHatchUIV4A.set(CustomItemList.eM_dynamoMulti4_UIV.get(1));

        GTNLItemList.DynamoHatchUIV16A.set(CustomItemList.eM_dynamoMulti16_UIV.get(1));

        GTNLItemList.DynamoHatchUIV64A.set(CustomItemList.eM_dynamoMulti64_UIV.get(1));

        GTNLItemList.DynamoHatchUMV.set(ItemList.Hatch_Dynamo_UMV.get(1));

        GTNLItemList.DynamoHatchUMV4A.set(CustomItemList.eM_dynamoMulti4_UMV.get(1));

        GTNLItemList.DynamoHatchUMV16A.set(CustomItemList.eM_dynamoMulti16_UMV.get(1));

        GTNLItemList.DynamoHatchUMV64A.set(CustomItemList.eM_dynamoMulti64_UMV.get(1));

        GTNLItemList.DynamoHatchUXV.set(ItemList.Hatch_Dynamo_UXV.get(1));

        GTNLItemList.DynamoHatchUXV4A.set(CustomItemList.eM_dynamoMulti4_UXV.get(1));

        GTNLItemList.DynamoHatchUXV16A.set(CustomItemList.eM_dynamoMulti16_UXV.get(1));

        GTNLItemList.DynamoHatchUXV64A.set(CustomItemList.eM_dynamoMulti64_UXV.get(1));

        GTNLItemList.DynamoHatchMAX.set(
            new StandardDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MAX.ID,
                "DynamoHatchMAX",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.max.standard.name"),
                14));

        GTNLItemList.DynamoHatchMAX4A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MAX_4A.ID,
                "DynamoHatchMAX4A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.max.4.name"),
                14,
                4));

        GTNLItemList.DynamoHatchMAX16A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MAX_16A.ID,
                "DynamoHatchMAX16A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.max.16.name"),
                14,
                16));

        GTNLItemList.DynamoHatchMAX64A.set(
            new MultiDynamoHatch(
                GTNLMachineID.DYNAMO_HATCH_MAX_64A.ID,
                "DynamoHatchMAX64A",
                StatCollector.translateToLocal("gtnl.hatch.dynamo.max.64.name"),
                14,
                64));

        GTNLItemList.WirelessEnergyHatchLV.set(ItemList.Wireless_Hatch_Energy_LV.get(1));

        GTNLItemList.WirelessEnergyHatchLV4A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LV_4A.ID,
                "WirelessEnergyHatchLV4A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.lv.4.name"),
                1,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.WirelessEnergyHatchLV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLV16A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LV_16A.ID,
                "WirelessEnergyHatchLV16A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.lv.16.name"),
                1,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLV64A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LV_64A.ID,
                "WirelessEnergyHatchLV64A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.lv.64.name"),
                1,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV.set(ItemList.Wireless_Hatch_Energy_MV.get(1));

        GTNLItemList.WirelessEnergyHatchMV4A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MV_4A.ID,
                "WirelessEnergyHatchMV4A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.mv.4.name"),
                2,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.WirelessEnergyHatchMV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV16A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MV_16A.ID,
                "WirelessEnergyHatchMV16A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.mv.16.name"),
                2,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV64A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MV_64A.ID,
                "WirelessEnergyHatchMV64A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.mv.64.name"),
                2,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV.set(ItemList.Wireless_Hatch_Energy_HV.get(1));

        GTNLItemList.WirelessEnergyHatchHV4A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_HV_4A.ID,
                "WirelessEnergyHatchHV4A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.hv.4.name"),
                3,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.WirelessEnergyHatchHV4A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV16A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_HV_16A.ID,
                "WirelessEnergyHatchHV16A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.hv.16.name"),
                3,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchHV16A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV64A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_HV_64A.ID,
                "WirelessEnergyHatchHV64A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.hv.64.name"),
                3,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchHV64A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchEV.set(ItemList.Wireless_Hatch_Energy_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV4A.set(CustomItemList.eM_energyWirelessMulti4_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV16A.set(CustomItemList.eM_energyWirelessMulti16_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV64A.set(CustomItemList.eM_energyWirelessMulti64_EV.get(1));

        GTNLItemList.LaserEnergyHatchIV256A.set(CustomItemList.eM_energyTunnel1_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV1024A.set(CustomItemList.eM_energyTunnel1_LuV.get(1));

        GTNLItemList.LaserEnergyHatchIV4096A.set(CustomItemList.eM_energyTunnel2_LuV.get(1));

        GTNLItemList.LaserEnergyHatchIV16384A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchIV65536A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchIV262144A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNLItemList.LaserEnergyHatchIV1048576A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNLItemList.LaserEnergyHatchIV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_IV_4194304A.ID,
                "LaserEnergyHatchIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.iv.4194304.name"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchIV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_IV_16777216A.ID,
                "LaserEnergyHatchIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.iv.16777216.name"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchLuV256A.set(CustomItemList.eM_energyTunnel1_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV1024A.set(CustomItemList.eM_energyTunnel2_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV4096A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchLuV16384A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchLuV65536A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNLItemList.LaserEnergyHatchLuV262144A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNLItemList.LaserEnergyHatchLuV1048576A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNLItemList.LaserEnergyHatchLuV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_LUV_4194304A.ID,
                "LaserEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.luv.4194304.name"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchLuV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchLuV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_LUV_16777216A.ID,
                "LaserEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.luv.16777216.name"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchLuV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchZPM256A.set(CustomItemList.eM_energyTunnel1_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM1024A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM4096A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM16384A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNLItemList.LaserEnergyHatchZPM65536A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNLItemList.LaserEnergyHatchZPM262144A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNLItemList.LaserEnergyHatchZPM1048576A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNLItemList.LaserEnergyHatchZPM4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_ZPM_4194304A.ID,
                "LaserEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.zpm.4194304.name"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchZPM4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchZPM16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_ZPM_16777216A.ID,
                "LaserEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.zpm.16777216.name"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchZPM16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUV256A.set(CustomItemList.eM_energyTunnel1_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV1024A.set(CustomItemList.eM_energyTunnel2_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV4096A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV16384A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV65536A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUV262144A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUV1048576A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UV_4194304A.ID,
                "LaserEnergyHatchUV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uv.4194304.name"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UV_16777216A.ID,
                "LaserEnergyHatchUV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uv.16777216.name"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUHV256A.set(CustomItemList.eM_energyTunnel1_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV1024A.set(CustomItemList.eM_energyTunnel2_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV4096A.set(CustomItemList.eM_energyTunnel3_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV16384A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV65536A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV262144A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUHV1048576A.set(CustomItemList.eM_energyTunnel6_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUHV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UHV_4194304A.ID,
                "LaserEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uhv.4194304.name"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUHV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUHV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UHV_16777216A.ID,
                "LaserEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uhv.16777216.name"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUHV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUEV256A.set(CustomItemList.eM_energyTunnel1_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV1024A.set(CustomItemList.eM_energyTunnel2_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV4096A.set(CustomItemList.eM_energyTunnel3_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV16384A.set(CustomItemList.eM_energyTunnel4_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV65536A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV262144A.set(CustomItemList.eM_energyTunnel6_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV1048576A.set(CustomItemList.eM_energyTunnel6_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUEV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UEV_4194304A.ID,
                "LaserEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uev.4194304.name"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUEV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUEV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UEV_16777216A.ID,
                "LaserEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uev.16777216.name"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUEV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUIV256A.set(CustomItemList.eM_energyTunnel1_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV1024A.set(CustomItemList.eM_energyTunnel2_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV4096A.set(CustomItemList.eM_energyTunnel3_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV16384A.set(CustomItemList.eM_energyTunnel4_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV65536A.set(CustomItemList.eM_energyTunnel5_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV262144A.set(CustomItemList.eM_energyTunnel6_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV1048576A.set(CustomItemList.eM_energyTunnel7_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UIV_4194304A.ID,
                "LaserEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uiv.4194304.name"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUIV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UIV_16777216A.ID,
                "LaserEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.uiv.16777216.name"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUMV256A.set(CustomItemList.eM_energyTunnel1_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV1024A.set(CustomItemList.eM_energyTunnel2_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV4096A.set(CustomItemList.eM_energyTunnel3_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV16384A.set(CustomItemList.eM_energyTunnel4_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV65536A.set(CustomItemList.eM_energyTunnel5_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV262144A.set(CustomItemList.eM_energyTunnel6_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV1048576A.set(CustomItemList.eM_energyTunnel7_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV4194304A.set(CustomItemList.eM_energyTunnel8_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_UMV_16777216A.ID,
                "LaserEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.umv.16777216.name"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchUMV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUXV256A.set(CustomItemList.eM_energyTunnel1_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV1024A.set(CustomItemList.eM_energyTunnel2_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV4096A.set(CustomItemList.eM_energyTunnel3_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV16384A.set(CustomItemList.eM_energyTunnel4_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV65536A.set(CustomItemList.eM_energyTunnel5_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV262144A.set(CustomItemList.eM_energyTunnel6_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV1048576A.set(CustomItemList.eM_energyTunnel7_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV4194304A.set(CustomItemList.eM_energyTunnel8_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV16777216A.set(CustomItemList.eM_energyTunnel9_UXV.get(1));

        GTNLItemList.LaserEnergyHatchMAX256A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_256A.ID,
                "LaserEnergyHatchMAX256A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.256.name"),
                14,
                256));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LaserEnergyHatchMAX256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX1024A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_1024A.ID,
                "LaserEnergyHatchMAX1024A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.1024.name"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX4096A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_4096A.ID,
                "LaserEnergyHatchMAX4096A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.4096.name"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX16384A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_16384A.ID,
                "LaserEnergyHatchMAX16384A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.16384.name"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX65536A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_65536A.ID,
                "LaserEnergyHatchMAX65536A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.65536.name"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX262144A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_262144A.ID,
                "LaserEnergyHatchMAX262144A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.262144.name"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX1048576A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_1048576A.ID,
                "LaserEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.1048576.name"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX4194304A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_4194304A.ID,
                "LaserEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.4194304.name"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX16777216A.set(
            new LaserEnergyHatch(
                GTNLMachineID.LASER_ENERGY_HATCH_MAX_16777216A.ID,
                "LaserEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_energy.max.16777216.name"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserEnergyHatchMAX16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchIV256A.set(CustomItemList.eM_dynamoTunnel1_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV1024A.set(CustomItemList.eM_dynamoTunnel1_LuV.get(1));

        GTNLItemList.LaserDynamoHatchIV4096A.set(CustomItemList.eM_dynamoTunnel2_LuV.get(1));

        GTNLItemList.LaserDynamoHatchIV16384A.set(CustomItemList.eM_dynamoTunnel2_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchIV65536A.set(CustomItemList.eM_dynamoTunnel3_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchIV262144A.set(CustomItemList.eM_dynamoTunnel3_UV.get(1));

        GTNLItemList.LaserDynamoHatchIV1048576A.set(CustomItemList.eM_dynamoTunnel4_UV.get(1));

        GTNLItemList.LaserDynamoHatchIV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_IV_4194304A.ID,
                "LaserDynamoHatchIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.iv.4194304.name"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchIV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_IV_16777216A.ID,
                "LaserDynamoHatchIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.iv.16777216.name"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchLuV256A.set(CustomItemList.eM_dynamoTunnel1_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV1024A.set(CustomItemList.eM_dynamoTunnel2_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV4096A.set(CustomItemList.eM_dynamoTunnel2_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchLuV16384A.set(CustomItemList.eM_dynamoTunnel3_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchLuV65536A.set(CustomItemList.eM_dynamoTunnel3_UV.get(1));

        GTNLItemList.LaserDynamoHatchLuV262144A.set(CustomItemList.eM_dynamoTunnel4_UV.get(1));

        GTNLItemList.LaserDynamoHatchLuV1048576A.set(CustomItemList.eM_dynamoTunnel4_UHV.get(1));

        GTNLItemList.LaserDynamoHatchLuV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_LUV_4194304A.ID,
                "LaserDynamoHatchLuV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.luv.4194304.name"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchLuV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchLuV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_LUV_16777216A.ID,
                "LaserDynamoHatchLuV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.luv.16777216.name"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchLuV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchZPM256A.set(CustomItemList.eM_dynamoTunnel1_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM1024A.set(CustomItemList.eM_dynamoTunnel2_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM4096A.set(CustomItemList.eM_dynamoTunnel3_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM16384A.set(CustomItemList.eM_dynamoTunnel3_UV.get(1));

        GTNLItemList.LaserDynamoHatchZPM65536A.set(CustomItemList.eM_dynamoTunnel4_UV.get(1));

        GTNLItemList.LaserDynamoHatchZPM262144A.set(CustomItemList.eM_dynamoTunnel4_UHV.get(1));

        GTNLItemList.LaserDynamoHatchZPM1048576A.set(CustomItemList.eM_dynamoTunnel5_UHV.get(1));

        GTNLItemList.LaserDynamoHatchZPM4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_ZPM_4194304A.ID,
                "LaserDynamoHatchZPM4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.zpm.4194304.name"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchZPM4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchZPM16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_ZPM_16777216A.ID,
                "LaserDynamoHatchZPM16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.zpm.16777216.name"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchZPM16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUV256A.set(CustomItemList.eM_dynamoTunnel1_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV1024A.set(CustomItemList.eM_dynamoTunnel2_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV4096A.set(CustomItemList.eM_dynamoTunnel3_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV16384A.set(CustomItemList.eM_dynamoTunnel4_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV65536A.set(CustomItemList.eM_dynamoTunnel4_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUV262144A.set(CustomItemList.eM_dynamoTunnel5_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUV1048576A.set(CustomItemList.eM_dynamoTunnel5_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UV_4194304A.ID,
                "LaserDynamoHatchUV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uv.4194304.name"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UV_16777216A.ID,
                "LaserDynamoHatchUV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uv.16777216.name"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUHV256A.set(CustomItemList.eM_dynamoTunnel1_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV1024A.set(CustomItemList.eM_dynamoTunnel2_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV4096A.set(CustomItemList.eM_dynamoTunnel3_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV16384A.set(CustomItemList.eM_dynamoTunnel4_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV65536A.set(CustomItemList.eM_dynamoTunnel5_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV262144A.set(CustomItemList.eM_dynamoTunnel5_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUHV1048576A.set(CustomItemList.eM_dynamoTunnel6_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUHV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UHV_4194304A.ID,
                "LaserDynamoHatchUHV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uhv.4194304.name"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUHV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUHV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UHV_16777216A.ID,
                "LaserDynamoHatchUHV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uhv.16777216.name"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUHV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUEV256A.set(CustomItemList.eM_dynamoTunnel1_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV1024A.set(CustomItemList.eM_dynamoTunnel2_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV4096A.set(CustomItemList.eM_dynamoTunnel3_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV16384A.set(CustomItemList.eM_dynamoTunnel4_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV65536A.set(CustomItemList.eM_dynamoTunnel5_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV262144A.set(CustomItemList.eM_dynamoTunnel6_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV1048576A.set(CustomItemList.eM_dynamoTunnel6_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUEV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UEV_4194304A.ID,
                "LaserDynamoHatchUEV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uev.4194304.name"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUEV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUEV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UEV_16777216A.ID,
                "LaserDynamoHatchUEV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uev.16777216.name"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUEV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUIV256A.set(CustomItemList.eM_dynamoTunnel1_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV1024A.set(CustomItemList.eM_dynamoTunnel2_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV4096A.set(CustomItemList.eM_dynamoTunnel3_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV16384A.set(CustomItemList.eM_dynamoTunnel4_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV65536A.set(CustomItemList.eM_dynamoTunnel5_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV262144A.set(CustomItemList.eM_dynamoTunnel6_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV1048576A.set(CustomItemList.eM_dynamoTunnel7_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UIV_4194304A.ID,
                "LaserDynamoHatchUIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uiv.4194304.name"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUIV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UIV_16777216A.ID,
                "LaserDynamoHatchUIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.uiv.16777216.name"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUMV256A.set(CustomItemList.eM_dynamoTunnel1_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV1024A.set(CustomItemList.eM_dynamoTunnel2_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV4096A.set(CustomItemList.eM_dynamoTunnel3_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV16384A.set(CustomItemList.eM_dynamoTunnel4_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV65536A.set(CustomItemList.eM_dynamoTunnel5_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV262144A.set(CustomItemList.eM_dynamoTunnel6_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV1048576A.set(CustomItemList.eM_dynamoTunnel7_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV4194304A.set(CustomItemList.eM_dynamoTunnel8_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_UMV_16777216A.ID,
                "LaserDynamoHatchUMV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.umv.16777216.name"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchUMV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUXV256A.set(CustomItemList.eM_dynamoTunnel1_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV1024A.set(CustomItemList.eM_dynamoTunnel2_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV4096A.set(CustomItemList.eM_dynamoTunnel3_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV16384A.set(CustomItemList.eM_dynamoTunnel4_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV65536A.set(CustomItemList.eM_dynamoTunnel5_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV262144A.set(CustomItemList.eM_dynamoTunnel6_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV1048576A.set(CustomItemList.eM_dynamoTunnel7_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV4194304A.set(CustomItemList.eM_dynamoTunnel8_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV16777216A.set(CustomItemList.eM_dynamoTunnel9_UXV.get(1));

        GTNLItemList.LaserDynamoHatchMAX256A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_256A.ID,
                "LaserDynamoHatchMAX256A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.256.name"),
                14,
                256));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LaserDynamoHatchMAX256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX1024A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_1024A.ID,
                "LaserDynamoHatchMAX1024A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.1024.name"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX4096A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_4096A.ID,
                "LaserDynamoHatchMAX4096A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.4096.name"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX16384A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_16384A.ID,
                "LaserDynamoHatchMAX16384A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.16384.name"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX65536A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_65536A.ID,
                "LaserDynamoHatchMAX65536A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.65536.name"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX262144A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_262144A.ID,
                "LaserDynamoHatchMAX262144A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.262144.name"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX1048576A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_1048576A.ID,
                "LaserDynamoHatchMAX1048576A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.1048576.name"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX4194304A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_4194304A.ID,
                "LaserDynamoHatchMAX4194304A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.4194304.name"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX16777216A.set(
            new LaserDynamoHatch(
                GTNLMachineID.LASER_DYNAMO_HATCH_MAX_16777216A.ID,
                "LaserDynamoHatchMAX16777216A",
                StatCollector.translateToLocal("gtnl.hatch.laser_dynamo.max.16777216.name"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LaserDynamoHatchMAX16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV.set(ItemList.Wireless_Hatch_Energy_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV4A.set(CustomItemList.eM_energyWirelessMulti4_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV16A.set(CustomItemList.eM_energyWirelessMulti16_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV64A.set(CustomItemList.eM_energyWirelessMulti64_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_256A.ID,
                "WirelessEnergyHatchIV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.256.name"),
                5,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_1024A.ID,
                "WirelessEnergyHatchIV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.1024.name"),
                5,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_4096A.ID,
                "WirelessEnergyHatchIV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.4096.name"),
                5,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_16384A.ID,
                "WirelessEnergyHatchIV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.16384.name"),
                5,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_65536A.ID,
                "WirelessEnergyHatchIV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.65536.name"),
                5,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_262144A.ID,
                "WirelessEnergyHatchIV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.262144.name"),
                5,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_1048576A.ID,
                "WirelessEnergyHatchIV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.1048576.name"),
                5,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_4194304A.ID,
                "WirelessEnergyHatchIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.4194304.name"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_IV_16777216A.ID,
                "WirelessEnergyHatchIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.iv.16777216.name"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV.set(ItemList.Wireless_Hatch_Energy_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV4A.set(CustomItemList.eM_energyWirelessMulti4_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV16A.set(CustomItemList.eM_energyWirelessMulti16_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV64A.set(CustomItemList.eM_energyWirelessMulti64_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_256A.ID,
                "WirelessEnergyHatchLuV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.256.name"),
                6,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_1024A.ID,
                "WirelessEnergyHatchLuV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.1024.name"),
                6,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_4096A.ID,
                "WirelessEnergyHatchLuV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.4096.name"),
                6,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_16384A.ID,
                "WirelessEnergyHatchLuV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.16384.name"),
                6,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_65536A.ID,
                "WirelessEnergyHatchLuV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.65536.name"),
                6,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_262144A.ID,
                "WirelessEnergyHatchLuV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.262144.name"),
                6,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_1048576A.ID,
                "WirelessEnergyHatchLuV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.1048576.name"),
                6,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_4194304A.ID,
                "WirelessEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.4194304.name"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_LUV_16777216A.ID,
                "WirelessEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.luv.16777216.name"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM.set(ItemList.Wireless_Hatch_Energy_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM4A.set(CustomItemList.eM_energyWirelessMulti4_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM16A.set(CustomItemList.eM_energyWirelessMulti16_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM64A.set(CustomItemList.eM_energyWirelessMulti64_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_256A.ID,
                "WirelessEnergyHatchZPM256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.256.name"),
                7,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_1024A.ID,
                "WirelessEnergyHatchZPM1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.1024.name"),
                7,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_4096A.ID,
                "WirelessEnergyHatchZPM4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.4096.name"),
                7,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_16384A.ID,
                "WirelessEnergyHatchZPM16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.16384.name"),
                7,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_65536A.ID,
                "WirelessEnergyHatchZPM65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.65536.name"),
                7,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_262144A.ID,
                "WirelessEnergyHatchZPM262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.262144.name"),
                7,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_1048576A.ID,
                "WirelessEnergyHatchZPM1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.1048576.name"),
                7,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_4194304A.ID,
                "WirelessEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.4194304.name"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_ZPM_16777216A.ID,
                "WirelessEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.zpm.16777216.name"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV.set(ItemList.Wireless_Hatch_Energy_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV4A.set(CustomItemList.eM_energyWirelessMulti4_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV16A.set(CustomItemList.eM_energyWirelessMulti16_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV64A.set(CustomItemList.eM_energyWirelessMulti64_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_256A.ID,
                "WirelessEnergyHatchUV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.256.name"),
                8,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_1024A.ID,
                "WirelessEnergyHatchUV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.1024.name"),
                8,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_4096A.ID,
                "WirelessEnergyHatchUV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.4096.name"),
                8,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_16384A.ID,
                "WirelessEnergyHatchUV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.16384.name"),
                8,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_65536A.ID,
                "WirelessEnergyHatchUV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.65536.name"),
                8,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_262144A.ID,
                "WirelessEnergyHatchUV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.262144.name"),
                8,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_1048576A.ID,
                "WirelessEnergyHatchUV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.1048576.name"),
                8,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_4194304A.ID,
                "WirelessEnergyHatchUV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.4194304.name"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UV_16777216A.ID,
                "WirelessEnergyHatchUV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uv.16777216.name"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV.set(ItemList.Wireless_Hatch_Energy_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV4A.set(CustomItemList.eM_energyWirelessMulti4_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV16A.set(CustomItemList.eM_energyWirelessMulti16_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV64A.set(CustomItemList.eM_energyWirelessMulti64_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_256A.ID,
                "WirelessEnergyHatchUHV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.256.name"),
                9,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_1024A.ID,
                "WirelessEnergyHatchUHV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.1024.name"),
                9,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_4096A.ID,
                "WirelessEnergyHatchUHV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.4096.name"),
                9,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_16384A.ID,
                "WirelessEnergyHatchUHV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.16384.name"),
                9,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_65536A.ID,
                "WirelessEnergyHatchUHV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.65536.name"),
                9,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_262144A.ID,
                "WirelessEnergyHatchUHV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.262144.name"),
                9,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_1048576A.ID,
                "WirelessEnergyHatchUHV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.1048576.name"),
                9,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_4194304A.ID,
                "WirelessEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.4194304.name"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UHV_16777216A.ID,
                "WirelessEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uhv.16777216.name"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV.set(ItemList.Wireless_Hatch_Energy_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV4A.set(CustomItemList.eM_energyWirelessMulti4_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV16A.set(CustomItemList.eM_energyWirelessMulti16_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV64A.set(CustomItemList.eM_energyWirelessMulti64_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_256A.ID,
                "WirelessEnergyHatchUEV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.256.name"),
                10,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_1024A.ID,
                "WirelessEnergyHatchUEV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.1024.name"),
                10,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_4096A.ID,
                "WirelessEnergyHatchUEV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.4096.name"),
                10,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_16384A.ID,
                "WirelessEnergyHatchUEV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.16384.name"),
                10,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_65536A.ID,
                "WirelessEnergyHatchUEV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.65536.name"),
                10,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_262144A.ID,
                "WirelessEnergyHatchUEV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.262144.name"),
                10,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_1048576A.ID,
                "WirelessEnergyHatchUEV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.1048576.name"),
                10,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_4194304A.ID,
                "WirelessEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.4194304.name"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UEV_16777216A.ID,
                "WirelessEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uev.16777216.name"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV.set(ItemList.Wireless_Hatch_Energy_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV4A.set(CustomItemList.eM_energyWirelessMulti4_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV16A.set(CustomItemList.eM_energyWirelessMulti16_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV64A.set(CustomItemList.eM_energyWirelessMulti64_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_256A.ID,
                "WirelessEnergyHatchUIV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.256.name"),
                11,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_1024A.ID,
                "WirelessEnergyHatchUIV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.1024.name"),
                11,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_4096A.ID,
                "WirelessEnergyHatchUIV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.4096.name"),
                11,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_16384A.ID,
                "WirelessEnergyHatchUIV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.16384.name"),
                11,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_65536A.ID,
                "WirelessEnergyHatchUIV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.65536.name"),
                11,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_262144A.ID,
                "WirelessEnergyHatchUIV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.262144.name"),
                11,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_1048576A.ID,
                "WirelessEnergyHatchUIV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.1048576.name"),
                11,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_4194304A.ID,
                "WirelessEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.4194304.name"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UIV_16777216A.ID,
                "WirelessEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.uiv.16777216.name"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV.set(ItemList.Wireless_Hatch_Energy_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV4A.set(CustomItemList.eM_energyWirelessMulti4_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV16A.set(CustomItemList.eM_energyWirelessMulti16_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV64A.set(CustomItemList.eM_energyWirelessMulti64_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_256A.ID,
                "WirelessEnergyHatchUMV256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.256.name"),
                12,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_1024A.ID,
                "WirelessEnergyHatchUMV1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.1024.name"),
                12,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_4096A.ID,
                "WirelessEnergyHatchUMV4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.4096.name"),
                12,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_16384A.ID,
                "WirelessEnergyHatchUMV16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.16384.name"),
                12,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_65536A.ID,
                "WirelessEnergyHatchUMV65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.65536.name"),
                12,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_262144A.ID,
                "WirelessEnergyHatchUMV262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.262144.name"),
                12,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_1048576A.ID,
                "WirelessEnergyHatchUMV1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.1048576.name"),
                12,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_4194304A.ID,
                "WirelessEnergyHatchUMV4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.4194304.name"),
                12,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_UMV_16777216A.ID,
                "WirelessEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.umv.16777216.name"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUXV.set(ItemList.Wireless_Hatch_Energy_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4A.set(CustomItemList.eM_energyWirelessMulti4_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16A.set(CustomItemList.eM_energyWirelessMulti16_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV64A.set(CustomItemList.eM_energyWirelessMulti64_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV256A.set(CustomItemList.eM_energyWirelessTunnel1_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV1024A.set(CustomItemList.eM_energyWirelessTunnel2_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4096A.set(CustomItemList.eM_energyWirelessTunnel3_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16384A.set(CustomItemList.eM_energyWirelessTunnel4_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV65536A.set(CustomItemList.eM_energyWirelessTunnel5_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV262144A.set(CustomItemList.eM_energyWirelessTunnel6_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV1048576A.set(CustomItemList.eM_energyWirelessTunnel7_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4194304A.set(CustomItemList.eM_energyWirelessTunnel8_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16777216A.set(CustomItemList.eM_energyWirelessTunnel9_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchMAX.set(ItemList.Wireless_Hatch_Energy_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX4A.set(CustomItemList.eM_energyWirelessMulti4_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX16A.set(CustomItemList.eM_energyWirelessMulti16_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX64A.set(CustomItemList.eM_energyWirelessMulti64_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX256A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_256A.ID,
                "WirelessEnergyHatchMAX256A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.256.name"),
                14,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX256A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX1024A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_1024A.ID,
                "WirelessEnergyHatchMAX1024A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.1024.name"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX1024A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX4096A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_4096A.ID,
                "WirelessEnergyHatchMAX4096A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.4096.name"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX4096A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX16384A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_16384A.ID,
                "WirelessEnergyHatchMAX16384A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.16384.name"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX16384A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX65536A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_65536A.ID,
                "WirelessEnergyHatchMAX65536A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.65536.name"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX65536A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX262144A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_262144A.ID,
                "WirelessEnergyHatchMAX262144A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.262144.name"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX262144A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX1048576A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_1048576A.ID,
                "WirelessEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.1048576.name"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX1048576A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX4194304A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_4194304A.ID,
                "WirelessEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.4194304.name"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX4194304A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX16777216A.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.WIRELESS_ENERGY_HATCH_MAX_16777216A.ID,
                "WirelessEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.max.16777216.name"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX16777216A.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousWirelessEnergyHatch.set(
            new WirelessMultiEnergyHatch(
                GTNLMachineID.HUMONGOUS_WIRELESS_ENERGY_HATCH.ID,
                "HumongousWirelessEnergyHatch",
                StatCollector.translateToLocal("gtnl.hatch.wireless_energy.humongous.name"),
                14,
                WirelessMultiEnergyHatch.HUMONGOUS_AMPERES));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousWirelessEnergyHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HumongousWirelessDynamoHatch.set(
            new WirelessMultiDynamoHatch(
                GTNLMachineID.HUMONGOUS_WIRELESS_DYNAMO_HATCH.ID,
                "HumongousWirelessDynamoHatch",
                StatCollector.translateToLocal("gtnl.hatch.wireless_dynamo.humongous.name"),
                14,
                Integer.MAX_VALUE));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HumongousWirelessDynamoHatch.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.BeamlinePipeMirror.set(
            new BeamlinePipeMirror(
                GTNLMachineID.BEAMLINE_PIPE_MIRROR.ID,
                "BeamlinePipeMirror",
                StatCollector.translateToLocal("gtnl.blockmachines.beamlinepipemirror.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.BeamlinePipeMirror.get(1), AnimatedText.GT_NOT_LEISURE);
    }
}
