package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gtPlusPlus.api.recipe.GTPPRecipeMaps.simpleWasherRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.drawable.UITexture;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;

import gnu.trove.map.hash.TIntIntHashMap;
import gregtech.api.enums.Materials;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class SteamOreProcessorModule extends SteamElevatorModule {

    private static final UITexture[] MODE_ICONS = { GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_MACERATOR,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_WASHER, GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_CENTRIFUGE,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_SIFTER, GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_BATH,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_THERMAL, GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_IOF_FORGE };

    public static final int MAX_PARA = 8;
    public static long RECIPE_EUT = 128;
    public static final int CACHE_MAX = 2048;
    public static IntOpenHashSet isCrushedOre = new IntOpenHashSet();
    public static IntOpenHashSet isCrushedPureOre = new IntOpenHashSet();
    public static IntOpenHashSet isPureDust = new IntOpenHashSet();
    public static IntOpenHashSet isImpureDust = new IntOpenHashSet();
    public static IntOpenHashSet isThermal = new IntOpenHashSet();
    public static IntOpenHashSet isOre = new IntOpenHashSet();
    public static IntOpenHashSet ALL_PROCESSABLE = new IntOpenHashSet();
    public static boolean isInit = false;
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> MAC_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> WASH_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> THERMAL_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> CENTRIFUGE_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> SIFTER_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> CHEMBATH_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> HAMMER_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final Int2ObjectLinkedOpenHashMap<GTRecipe> SIMPLE_WASHER_CACHE = new Int2ObjectLinkedOpenHashMap<>();
    public static final ThreadLocal<Random> RAND = ThreadLocal.withInitial(Random::new);
    public static final ThreadLocal<GTNLOverclockCalculator> OC_CALC = ThreadLocal
        .withInitial(GTNLOverclockCalculator::new);

    public ItemStack[] midProduct;
    public boolean mVoidStone = false;
    @Getter
    @Setter
    public int currentParallelism = 0;
    public int currentCircuitMultiplier = 0;
    public FluidStack[] recipeInputFluids = new FluidStack[0];

    private ItemStack stone;

    public SteamOreProcessorModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 8);
    }

    public SteamOreProcessorModule(String aName) {
        super(aName, 8);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamOreProcessorModule(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamOreProcessorModuleRecipeType");
    }

    @Override
    public int getMaxParallelRecipes() {
        return (int) (MAX_PARA * GTUtility.powInt(2, currentCircuitMultiplier));
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
        recipeInputFluids = tInputFluid.toArray(new FluidStack[0]);

        currentCircuitMultiplier = 0;
        ItemStack circuit = getControllerSlot();
        if (GTUtility.isAnyIntegratedCircuit(circuit)) {
            currentCircuitMultiplier = MathHelper.clamp_int(circuit.getItemDamage(), 0, 6);
        }

        int powerMultiplier = (int) GTUtility.powInt(2, currentCircuitMultiplier);
        long requiredEUt = RECIPE_EUT * powerMultiplier;
        long availableEUt = GTUtility.roundUpVoltage(getBaseMetaTileEntity().getStoredEU());

        if (availableEUt < requiredEUt) {
            return CheckRecipeResultRegistry.insufficientPower(RECIPE_EUT);
        }

        int maxParallel = MAX_PARA * powerMultiplier;

        GTNLOverclockCalculator calculator = OC_CALC.get()
            .reset()
            .setEUt(availableEUt)
            .setRecipeEUt(requiredEUt)
            .setDuration(128)
            .setParallel(maxParallel)
            .setNoOverclock(true);

        maxParallel = GTUtility.safeInt((long) (maxParallel * calculator.calculateMultiplierUnderOneTick()), 0);

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

        calculator.setCurrentParallel(currentParallel)
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

        midProduct = simulatedOres.toArray(new ItemStack[0]);

        switch (getProcessingMode()) {
            case MAC_WASH_THERMAL_MAC -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doThermal(isCrushedPureOre, isCrushedOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
            }
            case MAC_WASH_MAC_CENTRI -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doMac(isOre, isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case MAC_MAC_CENTRI -> {
                doMac(isOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case MAC_WASH_SIFT -> {
                doMac(isOre);
                doWash(isCrushedOre);
                doSift(isCrushedPureOre);
            }
            case MAC_CHEM_MAC_CENTRI -> {
                doMac(isOre);
                doChemWash(isCrushedOre, isCrushedPureOre);
                doMac(isCrushedOre, isCrushedPureOre);
                doCentrifuge(isImpureDust, isPureDust);
            }
            case MAC_CHEM_THERMAL_MAC -> {
                doMac(isOre);
                doChemWash(isCrushedOre, isCrushedPureOre);
                doThermal(isCrushedPureOre, isCrushedOre);
                doMac(isThermal, isOre, isCrushedOre, isCrushedPureOre);
            }
            case FORGE_FORGE_SIMPLEWASH -> {
                doForgeHammer(isOre);
                doForgeHammer(isThermal, isOre, isCrushedOre, isCrushedPureOre);
                doSimpleWash(isImpureDust, isPureDust);
            }
        }

        setCurrentParallelism(currentParallel);

        int consumeLeft = currentParallel;
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

        depleteInput(GTModHandler.getDistilledWater(currentParallel * 10L), false);
        depleteInput(Materials.Lubricant.getFluid(currentParallel), false);

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        this.mOutputItems = midProduct;
        this.mMaxProgresstime = getRecipeTickTime(getProcessingMode());
        this.lEUt = calculator.getConsumption();
        if (this.lEUt > 0) {
            this.lEUt = -this.lEUt;
        }
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamOreProcessorModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOreProcessorModule_08"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return super.getGui().withMachineModeIcons(MODE_ICONS);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after the Steam Ore Processor Module GUI is fully ported to mui2.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_SIMPLEWASHER);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
    }

    @Override
    public int nextMachineMode() {
        return machineMode = getProcessingMode().next()
            .ordinal();
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeName() {
        List<String> des = getDisplayMode(getProcessingMode());
        return String.join("\n", des);
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (aPlayer.isSneaking()) {
            mVoidStone = !mVoidStone;
            GTUtility.sendChatTrans(aPlayer, "GT5U.machines.oreprocessor.void", mVoidStone);
            return;
        }
        machineMode = getProcessingMode().next()
            .ordinal();
        List<String> des = getDisplayMode(getProcessingMode());
        GTUtility
            .sendChatTrans(aPlayer, StatCollector.translateToLocal("GT5U.MULTI_MACHINE_CHANGE"), String.join("", des));
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mVoidStone = aNBT.getBoolean("mVoidStone");
        currentParallelism = aNBT.getInteger("currentParallelism");
        machineMode = ProcessingMode.fromOrdinal(aNBT.getInteger("mMode"))
            .ordinal();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("mVoidStone", mVoidStone);
        aNBT.setInteger("currentParallelism", currentParallelism);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();

        currenttip.add(
            StatCollector.translateToLocal("Info_SteamOreProcessorModule_00") + EnumChatFormatting.BLUE
                + tag.getInteger("currentParallelism")
                + EnumChatFormatting.RESET);
        currenttip.addAll(getDisplayMode(tag.getInteger("mMode")));
        currenttip.add(
            StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.void", tag.getBoolean("mVoidStone")));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("mMode", machineMode);
        tag.setBoolean("mVoidStone", mVoidStone);
        tag.setInteger("currentParallelism", currentParallelism);
    }

    @Override
    public int clampRecipeOcCount(int value) {
        return Math.min(20, value);
    }

    public void doMac(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
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

    public void doWash(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
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

    public void doThermal(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
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

    public void doCentrifuge(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
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

    public void doSift(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
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

    public void doForgeHammer(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        HAMMER_CACHE,
                        tID,
                        () -> RecipeMaps.hammerRecipes.findRecipeQuery()
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

    public void doSimpleWash(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        SIMPLE_WASHER_CACHE,
                        tID,
                        () -> simpleWasherRecipes.findRecipeQuery()
                            .items(aStack)
                            .fluids(Materials.Water.getFluid(100))
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

    public void doChemWash(IntOpenHashSet... aTables) {
        ObjectArrayList<ItemStack> tProduct = new ObjectArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = getCachedRecipe(
                        CHEMBATH_CACHE,
                        tID,
                        () -> RecipeMaps.chemicalBathRecipes.findRecipeQuery()
                            .items(aStack)
                            .fluids(recipeInputFluids)
                            .find());
                    if (tRecipe != null && tRecipe.getRepresentativeFluidInput(0) != null) {
                        FluidStack tInputFluid = tRecipe.getRepresentativeFluidInput(0)
                            .copy();
                        int tStored = getFluidAmount(recipeInputFluids, tInputFluid);
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

    public int getFluidAmount(FluidStack aFluid) {
        return getFluidAmount(recipeInputFluids, aFluid);
    }

    public int getFluidAmount(FluidStack[] storedFluids, FluidStack aFluid) {
        int tAmt = 0;
        if (aFluid == null) return 0;
        for (FluidStack fluid : storedFluids) {
            if (aFluid.isFluidEqual(fluid)) {
                tAmt += fluid.amount;
            }
        }
        return tAmt;
    }

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
        ObjectArrayList<ItemStack> filteredOutput = new ObjectArrayList<>(tOutput.size());
        for (ItemStack output : tOutput) {
            if (output != null && output.stackSize > 0) {
                filteredOutput.add(output);
            }
        }
        return filteredOutput;
    }

    public void doCompress(List<ItemStack> aList) {
        TIntIntHashMap rProduct = new TIntIntHashMap();
        for (ItemStack stack : aList) {
            int tID = GTUtility.stackToInt(stack);
            if (mVoidStone && GTUtility.areStacksEqual(getStone(), stack)) {
                continue;
            }
            if (tID != 0) {
                rProduct.adjustOrPutValue(tID, stack.stackSize, stack.stackSize);
            }
        }
        midProduct = new ItemStack[rProduct.size()];

        int cnt = 0;
        var i = rProduct.iterator();

        while (i.hasNext()) {
            i.advance();
            ItemStack stack = GTUtility.intToStack(i.key());
            midProduct[cnt] = GTUtility.copyAmountUnsafe(i.value(), stack);
            cnt++;
        }
    }

    public static void initHash() {
        for (String name : OreDictionary.getOreNames()) {
            if (name == null || name.isEmpty()) continue;
            if (name.startsWith("crushedPurified")) registerOrePrefix(name, isCrushedPureOre);
            else if (name.startsWith("crushedCentrifuged")) registerOrePrefix(name, isThermal);
            else if (name.startsWith("crushed")) registerOrePrefix(name, isCrushedOre);
            else if (name.startsWith("dustImpure")) registerOrePrefix(name, isImpureDust);
            else if (name.startsWith("dustPure")) registerOrePrefix(name, isPureDust);
            else if (name.startsWith("ore") || name.startsWith("rawOre")) registerOrePrefix(name, isOre);
        }
        ALL_PROCESSABLE.addAll(isPureDust);
        ALL_PROCESSABLE.addAll(isImpureDust);
        ALL_PROCESSABLE.addAll(isCrushedPureOre);
        ALL_PROCESSABLE.addAll(isThermal);
        ALL_PROCESSABLE.addAll(isCrushedOre);
        ALL_PROCESSABLE.addAll(isOre);
    }

    public static GTRecipe getCachedRecipe(Int2ObjectLinkedOpenHashMap<GTRecipe> cache, int key,
        Supplier<GTRecipe> supplier) {
        synchronized (cache) {
            GTRecipe r = cache.get(key);
            if (r != null) return r;

            r = supplier.get();
            if (r != null) {
                cache.put(key, r);
                if (cache.size() > CACHE_MAX) cache.removeFirst();
            }
            return r;
        }
    }

    public static boolean checkTypes(int aID, IntOpenHashSet... aTables) {
        for (IntOpenHashSet set : aTables) {
            if (set.contains(aID)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getDisplayMode(int mode) {
        return getDisplayMode(ProcessingMode.fromOrdinal(mode));
    }

    private ItemStack getStone() {
        if (stone == null) {
            stone = Materials.Stone.getDust(1);
        }
        return stone;
    }

    private ProcessingMode getProcessingMode() {
        return ProcessingMode.fromOrdinal(machineMode);
    }

    private static int getRecipeTickTime(ProcessingMode mode) {
        return switch (mode) {
            case MAC_WASH_THERMAL_MAC -> 600;
            case MAC_WASH_MAC_CENTRI -> 300;
            case MAC_MAC_CENTRI -> 200;
            case MAC_WASH_SIFT -> 400;
            case MAC_CHEM_MAC_CENTRI -> 340;
            case MAC_CHEM_THERMAL_MAC -> 640;
            case FORGE_FORGE_SIMPLEWASH -> 20;
        };
    }

    private static void registerOrePrefix(String prefix, IntOpenHashSet target) {
        for (ItemStack stack : OreDictionary.getOres(prefix)) {
            target.add(GTUtility.stackToInt(stack));
        }
    }

    private static List<String> getDisplayMode(ProcessingMode mode) {
        EnumChatFormatting gray = EnumChatFormatting.GRAY;
        String crush = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Macerate");
        String wash = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Ore_Washer")
            .replace(" ", " " + gray);
        String thermal = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Thermal_Centrifuge")
            .replace(" ", " " + gray);
        String centrifuge = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Centrifuge");
        String sifter = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Sifter");
        String chemWash = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Chemical_Bathing")
            .replace(" ", " " + gray);
        String hammer = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Forge_Hammer");
        String simpleWasher = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Simple_Washer");
        String arrow = " " + gray + "-> ";

        List<String> des = new ArrayList<>();
        des.add(StatCollector.translateToLocalFormatted("GT5U.multiblock.runningMode") + " ");

        switch (mode) {
            case MAC_WASH_THERMAL_MAC -> {
                des.add(gray + crush + arrow);
                des.add(gray + wash + arrow);
                des.add(gray + thermal + arrow);
                des.add(gray + crush + ' ');
            }
            case MAC_WASH_MAC_CENTRI -> {
                des.add(gray + crush + arrow);
                des.add(gray + wash + arrow);
                des.add(gray + crush + arrow);
                des.add(gray + centrifuge + ' ');
            }
            case MAC_MAC_CENTRI -> {
                des.add(gray + crush + arrow);
                des.add(gray + crush + arrow);
                des.add(gray + centrifuge + ' ');
            }
            case MAC_WASH_SIFT -> {
                des.add(gray + crush + arrow);
                des.add(gray + wash + arrow);
                des.add(gray + sifter + ' ');
            }
            case MAC_CHEM_MAC_CENTRI -> {
                des.add(gray + crush + arrow);
                des.add(gray + chemWash + arrow);
                des.add(gray + crush + arrow);
                des.add(gray + centrifuge + ' ');
            }
            case MAC_CHEM_THERMAL_MAC -> {
                des.add(gray + crush + arrow);
                des.add(gray + chemWash + arrow);
                des.add(gray + thermal + arrow);
                des.add(gray + crush + ' ');
            }
            case FORGE_FORGE_SIMPLEWASH -> {
                des.add(gray + hammer + arrow);
                des.add(gray + hammer + arrow);
                des.add(gray + simpleWasher + ' ');
            }
        }

        des.add(StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor2", getRecipeTickTime(mode) / 20));
        return des;
    }

    private enum ProcessingMode {

        MAC_WASH_THERMAL_MAC,
        MAC_WASH_MAC_CENTRI,
        MAC_MAC_CENTRI,
        MAC_WASH_SIFT,
        MAC_CHEM_MAC_CENTRI,
        MAC_CHEM_THERMAL_MAC,
        FORGE_FORGE_SIMPLEWASH;

        private static final ProcessingMode[] VALUES = values();

        public static ProcessingMode fromOrdinal(int ordinal) {
            if (0 <= ordinal && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            return MAC_WASH_THERMAL_MAC;
        }

        public ProcessingMode next() {
            return fromOrdinal(ordinal() + 1);
        }
    }
}
