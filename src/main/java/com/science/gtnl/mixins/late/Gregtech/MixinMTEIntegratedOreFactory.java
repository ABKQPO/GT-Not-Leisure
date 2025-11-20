package com.science.gtnl.mixins.late.Gregtech;

import static com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamOreProcessorModule.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEIntegratedOreFactory;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mixin(value = MTEIntegratedOreFactory.class, remap = false)
public abstract class MixinMTEIntegratedOreFactory
    extends MTEExtendedPowerMultiBlockBase<MixinMTEIntegratedOreFactory> {

    @Shadow
    private ItemStack[] sMidProduct;

    @Shadow
    protected abstract void setCurrentParallelism(int parallelism);

    @Shadow
    private boolean sVoidStone;

    @Shadow
    @Final
    private static long RECIPE_EUT;

    @Shadow
    private static int getTime(int mode) {
        return 0;
    }

    @Shadow
    private int sMode;

    public MixinMTEIntegratedOreFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @ModifyArg(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/util/HatchElementBuilder;atLeast([Lgregtech/api/interfaces/IHatchElement;)Lgregtech/api/util/HatchElementBuilder;"),
        index = 0)
    private static IHatchElement<?>[] modifyAtLeastArgs(IHatchElement<?>[] elements) {
        for (IHatchElement<?> e : elements) {
            if (e == HatchElement.Energy) {
                return new IHatchElement<?>[] { InputHatch, OutputBus, InputBus, Maintenance, Energy.or(ExoticEnergy) };
            }
        }
        return elements;
    }

    @Override
    public long getMaxInputEu() {
        long exoticEu = ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches);
        long normalEu = ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches);
        return Math.max(exoticEu, normalEu);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (!isInit) {
            initHash();
            isInit = true;
        }

        List<ItemStack> tInput = getStoredInputs();
        List<FluidStack> tInputFluid = getStoredFluids();
        if (tInput.isEmpty() || tInputFluid.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        long requiredEUt = RECIPE_EUT;
        long availableEUt = GTUtility.roundUpVoltage(getMaxInputEu());

        if (availableEUt < requiredEUt) {
            return CheckRecipeResultRegistry.insufficientPower(RECIPE_EUT);
        }

        int maxParallel = 65536 * GTUtility.getTier(getMaxInputEu());

        GTNL_OverclockCalculator calculator = OC_CALC.get()
            .reset()
            .setEUt(availableEUt)
            .setRecipeEUt(requiredEUt)
            .setDuration(getTime(sMode))
            .setParallel(maxParallel)
            .enablePerfectOC();

        maxParallel = GTUtility.safeInt((long) (maxParallel * calculator.calculateMultiplierUnderOneTick()), 0);

        int maxParallelBeforeBatchMode = maxParallel;
        if (isBatchModeEnabled()) {
            maxParallel = GTUtility.safeInt((long) maxParallel * getMaxBatchSize(), 0);
        }

        int currentParallel = (int) Math.min(maxParallel, availableEUt / requiredEUt);

        int tLube = 0;
        int tWater = 0;
        for (FluidStack fluid : tInputFluid) {
            if (fluid == null) continue;
            if (fluid.equals(GTModHandler.getDistilledWater(1L))) {
                tWater += fluid.amount;
            } else if (fluid.equals(Materials.Lubricant.getFluid(1L))) {
                tLube += fluid.amount;
            }
        }
        currentParallel = Math.min(currentParallel, tLube);
        currentParallel = Math.min(currentParallel, tWater / 10);
        if (currentParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        int itemParallel = 0;
        for (ItemStack ore : tInput) {
            int tID = GTUtility.stackToInt(ore);
            if (tID == 0) continue;
            if (!ALL_PROCESSABLE.contains(tID)) continue;
            int add = Math.min(ore.stackSize, currentParallel - itemParallel);
            if (add <= 0) break;
            itemParallel += add;
            if (itemParallel >= currentParallel) break;
        }
        currentParallel = itemParallel;
        if (currentParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        int currentParallelBeforeBatchMode = Math.min(currentParallel, maxParallelBeforeBatchMode);

        calculator.setCurrentParallel(currentParallelBeforeBatchMode)
            .calculate();

        ObjectArrayList<ItemStack> simulatedOres = new ObjectArrayList<>();
        int remainingCost = currentParallel;
        for (ItemStack ore : tInput) {
            if (remainingCost <= 0) break;
            int tID = GTUtility.stackToInt(ore);
            if (tID == 0) continue;
            if (!ALL_PROCESSABLE.contains(tID)) continue;
            if (remainingCost >= ore.stackSize) {
                simulatedOres.add(GTUtility.copy(ore));
                remainingCost -= ore.stackSize;
            } else {
                simulatedOres.add(GTUtility.copyAmountUnsafe(remainingCost, ore));
                break;
            }
        }

        sMidProduct = simulatedOres.toArray(new ItemStack[0]);

        switch (machineMode) {
            case 0 -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doThermal(isCrushedPureOre, isCrushedOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
            }
            case 1 -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doMac(isOre, isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case 2 -> {
                doMac(isOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case 3 -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doSift(isCrushedPureOre);
            }
            case 4 -> {
                doMac(isOre);
                doChemWash(isCrushedOre, isCrushedPureOre);
                doMac(isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case 5 -> {
                doMac(isOre);
                doChemWash(isCrushedOre, isCrushedPureOre);
                doThermal(isCrushedPureOre, isCrushedOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
            }
            default -> {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }

        double batchMultiplierMax = 1;
        // In case batch mode enabled
        if (currentParallel > maxParallelBeforeBatchMode && calculator.getDuration() < getMaxBatchSize()) {
            batchMultiplierMax = (double) getMaxBatchSize() / calculator.getDuration();
            batchMultiplierMax = Math.min(batchMultiplierMax, (double) currentParallel / maxParallelBeforeBatchMode);
        }

        int finalParallel = (int) (batchMultiplierMax * currentParallelBeforeBatchMode);

        setCurrentParallelism(finalParallel);

        int consumeLeft = finalParallel;
        for (ItemStack ore : tInput) {
            int tID = GTUtility.stackToInt(ore);
            if (tID == 0) continue;
            if (!ALL_PROCESSABLE.contains(tID)) continue;
            if (consumeLeft >= ore.stackSize) {
                consumeLeft -= ore.stackSize;
                ore.stackSize = 0;
            } else {
                ore.stackSize -= consumeLeft;
                break;
            }
        }

        depleteInput(GTModHandler.getDistilledWater(finalParallel * 10L), false);
        depleteInput(Materials.Lubricant.getFluid(finalParallel), false);

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        this.mOutputItems = sMidProduct;
        this.mMaxProgresstime = (int) (calculator.getDuration() * batchMultiplierMax);
        this.lEUt = calculator.getConsumption();
        if (this.lEUt > 0) {
            this.lEUt = -this.lEUt;
        }
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Unique
    public void doMac(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    // cache by intID
                    GTRecipe tRecipe = getCachedRecipe(
                        MAC_CACHE,
                        tID,
                        () -> RecipeMaps.maceratorRecipes.findRecipeQuery()
                            .caching(false)
                            .items(aStack)
                            .find());
                    if (tRecipe != null) {
                        tProduct.addAll(getOutputStack(tRecipe, aStack.stackSize));
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public void doWash(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        WASH_CACHE,
                        tID,
                        () -> RecipeMaps.oreWasherRecipes.findRecipeQuery()
                            .caching(false)
                            .items(aStack)
                            .fluids(GTModHandler.getDistilledWater(Integer.MAX_VALUE))
                            .find());
                    if (tRecipe != null) {
                        tProduct.addAll(getOutputStack(tRecipe, aStack.stackSize));
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public void doThermal(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        THERMAL_CACHE,
                        tID,
                        () -> RecipeMaps.thermalCentrifugeRecipes.findRecipeQuery()
                            .caching(false)
                            .items(aStack)
                            .find());
                    if (tRecipe != null) {
                        tProduct.addAll(getOutputStack(tRecipe, aStack.stackSize));
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public void doCentrifuge(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        CENTRIFUGE_CACHE,
                        tID,
                        () -> RecipeMaps.centrifugeRecipes.findRecipeQuery()
                            .items(aStack)
                            .find());
                    if (tRecipe != null) {
                        tProduct.addAll(getOutputStack(tRecipe, aStack.stackSize));
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public void doSift(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        SIFTER_CACHE,
                        tID,
                        () -> RecipeMaps.sifterRecipes.findRecipeQuery()
                            .items(aStack)
                            .find());
                    if (tRecipe != null) {
                        tProduct.addAll(getOutputStack(tRecipe, aStack.stackSize));
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public void doChemWash(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (sMidProduct != null) {
            for (ItemStack aStack : sMidProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypesFast(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        CHEMBATH_CACHE,
                        tID,
                        () -> RecipeMaps.chemicalBathRecipes.findRecipeQuery()
                            .items(aStack)
                            .fluids(getStoredFluids().toArray(new FluidStack[0]))
                            .find());
                    if (tRecipe != null && tRecipe.getRepresentativeFluidInput(0) != null) {
                        FluidStack tInputFluid = tRecipe.getRepresentativeFluidInput(0)
                            .copy();
                        int tStored = getFluidAmount(tInputFluid);
                        int tWashed = Math.min(tStored / tInputFluid.amount, aStack.stackSize);
                        depleteInput(new FluidStack(tInputFluid.getFluid(), tWashed * tInputFluid.amount), false);
                        tProduct.addAll(getOutputStack(tRecipe, tWashed));
                        if (tWashed < aStack.stackSize) {
                            tProduct.add(GTUtility.copyAmountUnsafe(aStack.stackSize - tWashed, aStack));
                        }
                    } else {
                        tProduct.add(aStack);
                    }
                } else {
                    tProduct.add(aStack);
                }
            }
        }
        doCompress(tProduct);
    }

    @Unique
    public boolean checkTypesFast(int aID, IntOpenHashSet... aTables) {
        for (IntOpenHashSet set : aTables) {
            if (set.contains(aID)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    public int getFluidAmount(FluidStack aFluid) {
        int tAmt = 0;
        if (aFluid == null) return 0;
        for (FluidStack fluid : getStoredFluids()) {
            if (aFluid.isFluidEqual(fluid)) {
                tAmt += fluid.amount;
            }
        }
        return tAmt;
    }

    @Unique
    public List<ItemStack> getOutputStack(GTRecipe aRecipe, int aTime) {
        ObjectArrayList<ItemStack> tOutput = new ObjectArrayList<>();
        Random random = RAND.get();
        for (int i = 0; i < aRecipe.mOutputs.length; i++) {
            if (aRecipe.getOutput(i) == null) {
                continue;
            }
            int tChance = aRecipe.getOutputChance(i);
            if (tChance == 10000) {
                tOutput.add(GTUtility.copyAmountUnsafe(aTime * aRecipe.getOutput(i).stackSize, aRecipe.getOutput(i)));
            } else {
                double u = aTime * (tChance / 10000D);
                double e = aTime * (tChance / 10000D) * (1 - (tChance / 10000D));
                int tAmount = (int) Math.ceil(Math.sqrt(e) * random.nextGaussian() + u);
                tOutput.add(
                    GTUtility
                        .copyAmountUnsafe(Math.max(0, tAmount) * aRecipe.getOutput(i).stackSize, aRecipe.getOutput(i)));
            }
        }
        return tOutput.stream()
            .filter(i -> (i != null && i.stackSize > 0))
            .collect(Collectors.toList());
    }

    @Unique
    public void doCompress(ObjectArrayList<ItemStack> aList) {
        Int2IntOpenHashMap rProduct = new Int2IntOpenHashMap();
        for (ItemStack stack : aList) {
            int tID = GTUtility.stackToInt(stack);
            if (sVoidStone) {
                if (GTUtility.areStacksEqual(Materials.Stone.getDust(1), stack)) {
                    continue;
                }
            }
            if (tID != 0) {
                rProduct.put(tID, rProduct.getOrDefault(tID, 0) + stack.stackSize);
            }
        }
        sMidProduct = new ItemStack[rProduct.size()];
        int cnt = 0;
        for (Int2IntOpenHashMap.Entry e : rProduct.int2IntEntrySet()) {
            ItemStack stack = GTUtility.intToStack(e.getIntKey());
            sMidProduct[cnt] = GTUtility.copyAmountUnsafe(e.getIntValue(), stack);
            cnt++;
        }
    }

    /**
     * @reason Overrides the original tooltip builder method to use translated tooltips
     *         from the lang file using StatCollector.
     *
     * @return the customized MultiblockTooltipBuilder
     * @author GTNotLeisure
     */
    @Overwrite
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IntegratedOreFactoryRecipeType"))
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo("")
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .beginStructureBlock(6, 12, 11, false)
            .addController(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_08"))
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_00"), 128, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_01"), 105, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_02"), 48, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_03"), 30, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_04"), 16, false)
            .addCasingInfoExactly(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_05"), 16, false)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_06"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_07"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_08"), 2)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_09"), 3)
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_10"), 3)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_IntegratedOreFactory_Casing_11"), 4)
            .toolTipFinisher();
        return tt;
    }
}
