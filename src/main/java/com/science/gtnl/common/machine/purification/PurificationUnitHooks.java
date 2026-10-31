package com.science.gtnl.common.machine.purification;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.gtnllib.utils.MathUtils;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.common.machine.hatch.HatchFluidDrain;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;

import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.metadata.PurificationPlantBaseChanceKey;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.multi.purification.LinkedPurificationUnit;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBaryonicPerfection;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class PurificationUnitHooks {

    public static IPurificationUnitHost host(MTEPurificationUnitBase<?> unit) {
        return (IPurificationUnitHost) unit;
    }

    public static CheckRecipeResult checkWirelessProcessing(MTEPurificationUnitBase<?> unit) {
        IPurificationUnitHost host = host(unit);
        PurificationUnitState state = host.gtnl$state();
        MTEPurificationPlant controller = host.gtnl$controller();
        if (controller != null) state.setWirelessMode(((IWirelessMode) controller).isGtnl$wirelessMode());
        if (!state.isWirelessMode()) return null;
        host.gtnl$setStoredFluids(unit.getStoredFluids());
        state.resetCost();

        CheckRecipeResult result = unit.overrideRecipeCheck();
        if (result == null) result = findRecipeForInputsLong(
            unit,
            host.gtnl$storedFluids()
                .toArray(new FluidStack[] {}));

        if (result.wasSuccessful()) {
            FluidStack waterInput = host.gtnl$currentRecipe().mFluidInputs[0];
            long amountAvailable = 0;
            for (FluidStack fluid : host.gtnl$storedFluids()) {
                if (fluid.isFluidEqual(waterInput)) {
                    amountAvailable += fluid.amount;
                }
            }

            if (state.limitParallel(Math.floorDiv(amountAvailable, (long) waterInput.amount)) == 0) {
                return GTNLParallelHelper.PARALLEL_ZERO;
            }

            BigInteger costEU = BigInteger.valueOf(state.getEffectiveParallel())
                .multiply(BigInteger.valueOf(unit.getBasePowerUsage()));

            if (!addEUToGlobalEnergyMap(state.getOwnerUUID(), costEU.multiply(MathUtils.NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
            }
            state.addCost(costEU);
        }
        return result;
    }

    public static boolean startWirelessCycle(MTEPurificationUnitBase<?> unit, int cycleTime, int progressTime) {
        IPurificationUnitHost host = host(unit);
        PurificationUnitState state = host.gtnl$state();
        if (!state.isWirelessMode()) return false;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        unit.startRecipeProcessing();
        // Calculate the chance before depleting inputs, otherwise boost items may already be gone.
        host.gtnl$setCurrentRecipeChance(unit.calculateBoostedSuccessChance());
        GTRecipe recipe = host.gtnl$currentRecipe();

        if (isWaterBoostedList(unit, recipe)) {
            depleteInputList(unit, waterBoostAmountList(unit, recipe), false);
        }

        if (!unit.getController()
            .debugModeOn()) {
            depleteRecipeInputsLong(unit);
        }
        unit.mMaxProgresstime = cycleTime;
        unit.mProgresstime = progressTime;
        unit.mEfficiency = 10000;

        ObjectArrayList<FluidStack> fluidOutputList = new ObjectArrayList<>();
        for (int i = 0; i < recipe.mFluidOutputs.length; ++i) {
            FluidStack output = recipe.mFluidOutputs[i].copy();
            long scaledAmount = state.getEffectiveParallel() * output.amount;
            fluidOutputList.addAll(splitLongToFluidStacks(output, scaledAmount));
        }

        ItemStack[] recipeOutputs = recipe.mOutputs;
        ItemStack[] itemOutputs = new ItemStack[recipeOutputs.length];
        int[] mChances = recipe.mOutputChances;

        if (mChances != null) {
            for (int i = 0; i < recipeOutputs.length; ++i) {
                int roll = random.nextInt(10000);
                if (roll <= mChances[i]) {
                    itemOutputs[i] = recipeOutputs[i].copy();
                }
            }
        } else {
            for (int i = 0; i < recipeOutputs.length; ++i) {
                itemOutputs[i] = recipeOutputs[i].copy();
            }
        }

        unit.mOutputFluids = fluidOutputList.toArray(new FluidStack[0]);
        unit.mOutputItems = itemOutputs;
        // Only shown in Waila: the plant controller drains the power, not the unit.
        unit.lEUt = 0;
        unit.endRecipeProcessing();
        return true;
    }

    public static List<FluidStack> waterBoostAmountList(MTEPurificationUnitBase<?> unit, GTRecipe recipe) {
        // Output water is always the first fluid output.
        FluidStack outputWater = recipe.mFluidOutputs[0];
        long totalAmount = Math.round(
            (double) outputWater.amount * MTEPurificationUnitBase.WATER_BOOST_NEEDED_FLUID
                * host(unit).gtnl$state()
                    .getEffectiveParallel());
        List<FluidStack> stacks = new ObjectArrayList<>();
        splitLongToFluidStacks(outputWater, totalAmount, stacks);
        return stacks;
    }

    public static ArrayList<FluidStack> splitLongToFluidStacks(FluidStack template, long amount) {
        ArrayList<FluidStack> list = new ArrayList<>();
        splitLongToFluidStacks(template, amount, list);
        return list;
    }

    private static void splitLongToFluidStacks(FluidStack template, long amount, List<FluidStack> list) {
        long remaining = amount;
        while (remaining > 0) {
            int split = (int) Math.min(Integer.MAX_VALUE, remaining);
            list.add(new FluidStack(template.getFluid(), split));
            remaining -= split;
        }
    }

    public static CheckRecipeResult findRecipeForInputsLong(MTEPurificationUnitBase<?> unit, FluidStack[] fluidInputs,
        ItemStack... itemInputs) {
        RecipeMap<?> recipeMap = unit.getRecipeMap();

        Stream<GTRecipe> recipes = recipeMap.findRecipeQuery()
            .fluids(fluidInputs)
            .items(itemInputs)
            .findAll();
        GTRecipe recipe = recipes
            .max(Comparator.comparing(r -> r.getMetadataOrDefault(PurificationPlantBaseChanceKey.INSTANCE, 0.0f)))
            .orElse(null);

        if (recipe == null) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        host(unit).gtnl$setCurrentRecipe(recipe);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public static void depleteRecipeInputsLong(MTEPurificationUnitBase<?> unit) {
        GTRecipe recipe = host(unit).gtnl$currentRecipe();
        long effectiveParallel = host(unit).gtnl$state()
            .getEffectiveParallel();
        for (int i = 0; i < recipe.mFluidInputs.length; ++i) {
            FluidStack input = recipe.mFluidInputs[i];
            List<FluidStack> fluidStacks = new ObjectArrayList<>();
            if (i == 0) {
                splitLongToFluidStacks(input, input.amount * effectiveParallel, fluidStacks);
            } else fluidStacks.add(input);
            depleteInputList(unit, fluidStacks, false);
        }
    }

    public static boolean isWaterBoostedList(MTEPurificationUnitBase<?> unit, GTRecipe recipe) {
        List<FluidStack> inputWater = waterBoostAmountList(unit, recipe);
        return depleteInputList(unit, inputWater, true);
    }

    public static boolean depleteInputList(MTEPurificationUnitBase<?> unit, List<FluidStack> fluids, boolean simulate) {
        if (fluids == null || fluids.isEmpty()) return false;

        Object2LongOpenHashMap<Fluid> mergedStorage = new Object2LongOpenHashMap<>();
        for (FluidStack stored : unit.getStoredFluids()) {
            if (stored != null) {
                mergedStorage.addTo(stored.getFluid(), stored.amount);
            }
        }

        Object2LongOpenHashMap<Fluid> mergedNeeded = new Object2LongOpenHashMap<>();
        for (FluidStack needed : fluids) {
            if (needed != null) {
                mergedNeeded.addTo(needed.getFluid(), needed.amount);
            }
        }

        for (Object2LongMap.Entry<Fluid> neededEntry : Object2LongMaps.fastIterable(mergedNeeded)) {
            long availableAmount = mergedStorage.getLong(neededEntry.getKey());
            if (availableAmount < neededEntry.getLongValue()) return false;
        }

        if (simulate) return true;
        List<MTEHatch> hatches = allInputHatches(unit);
        for (FluidStack needed : fluids) {
            HatchFluidDrain.drainAcross(
                hatches,
                needed.amount,
                (hatch, upTo) -> drainFluid(unit, hatch, new FluidStack(needed.getFluid(), upTo), true));
        }

        return true;
    }

    public static int drainFluid(MTEPurificationUnitBase<?> unit, MTEHatch hatch, FluidStack fluid, boolean doDrain) {
        if (fluid == null || hatch == null) return 0;

        if (host(unit).gtnl$supportsCraftingMEBuffer() && hatch instanceof IDualInputHatch tHatch
            && tHatch.supportsFluids()) {
            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : new ObjectArrayList<>(inventory.getFluidInputs())) {
                    if (stored != null && stored.amount > 0 && stored.isFluidEqual(fluid)) {
                        int deduct = Math.min(stored.amount, fluid.amount);
                        if (doDrain) stored.amount -= deduct;
                        return deduct;
                    }
                }
            }
        }

        if (hatch instanceof MTEHatchInput tHatch && tHatch.isValid()) {
            FluidStack drained = tHatch.drain(ForgeDirection.UNKNOWN, fluid, doDrain);
            return drained != null ? Math.min(drained.amount, fluid.amount) : 0;
        }

        return 0;
    }

    private static List<MTEHatch> allInputHatches(MTEPurificationUnitBase<?> unit) {
        List<MTEHatch> dualHatches = unit.mDualInputHatches.stream()
            .map(h -> (MTEHatch) h)
            .toList();

        List<MTEHatch> allHatches = new ObjectArrayList<>(unit.mInputHatches);
        allHatches.addAll(dualHatches);

        return GTUtility.filterValidMTEs(allHatches);
    }

    public static void addParallelInfo(MTEPurificationUnitBase<?> unit, List<String> ret) {
        IPurificationUnitHost host = host(unit);
        PurificationUnitState state = host.gtnl$state();
        ret.add(
            IGregTechDeviceInformation.encode(
                "GT5U.infodata.parallel.current",
                "" + EnumChatFormatting.YELLOW
                    + (state.isWirelessMode() ? state.getEffectiveParallel() : host.gtnl$effectiveParallel())
                    + "(Long)"));
        if (state.isWirelessMode()) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + IGregTechDeviceInformation.encode("gtnl.waila.wireless.mode"));
            ret.add(
                EnumChatFormatting.AQUA + IGregTechDeviceInformation.encode("gtnl.waila.wireless.current_eu_cost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + state.getCostingEUText()
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    public static void addWirelessWailaBody(List<String> tip, NBTTagCompound tag) {
        if (!tag.getBoolean("wirelessMode")) return;
        tip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("gtnl.waila.wireless.mode"));
        tip.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.wireless.current_eu_cost")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingEUText")
                + EnumChatFormatting.RESET
                + " EU");
    }

    public static void writeWirelessWailaData(NBTTagCompound tag, boolean wirelessMode, String costingEUText) {
        tag.setBoolean("wirelessMode", wirelessMode);
        if (wirelessMode) tag.setString("costingEUText", costingEUText);
    }

    // Returns whether a T8 unit is linked; the plant then skips GT's energy hatch check.
    public static boolean linkWirelessMode(List<LinkedPurificationUnit> linkedUnits, IWirelessMode plantMode,
        boolean noEnergyHatch) {
        boolean t8water = false;
        for (LinkedPurificationUnit unit : linkedUnits) {
            if (unit.metaTileEntity() instanceof MTEPurificationUnitBaryonicPerfection) {
                if (noEnergyHatch) {
                    plantMode.setGtnl$wirelessMode(true);
                }
                t8water = true;
                break;
            }
        }

        for (LinkedPurificationUnit unit : linkedUnits) {
            ((IWirelessMode) unit.metaTileEntity()).setGtnl$wirelessMode(plantMode.isGtnl$wirelessMode());
        }
        return t8water;
    }

    public static boolean drainFromDualHatches(List<IDualInputHatch> hatches, FluidStack inputCost) {
        if (inputCost == null) return false;
        for (IDualInputHatch tHatch : hatches) {
            if (!tHatch.supportsFluids()) continue;

            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : inventory.getFluidInputs()) {
                    if (stored != null && stored.amount >= inputCost.amount && stored.isFluidEqual(inputCost)) {
                        stored.amount -= inputCost.amount;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String[] plantInfoData(String[] base, boolean wirelessMode, String costingEUText) {
        List<String> ret = new ObjectArrayList<>(Arrays.asList(base));
        if (wirelessMode) {
            ret.add(
                IGregTechDeviceInformation.encode("gtnl.waila.wireless.mode.format", EnumChatFormatting.LIGHT_PURPLE));
            ret.add(
                IGregTechDeviceInformation.encode(
                    "gtnl.waila.wireless.current_eu_cost.format",
                    EnumChatFormatting.AQUA,
                    EnumChatFormatting.RESET,
                    EnumChatFormatting.GOLD,
                    costingEUText,
                    EnumChatFormatting.RESET));
        }
        return ret.toArray(new String[0]);
    }
}
