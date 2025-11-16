package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;

import gregtech.api.enums.Materials;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.VoidProtectionHelper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class SteamOreProcessorModule extends SteamElevatorModule {

    public static IntOpenHashSet isCrushedOre = new IntOpenHashSet();
    public static IntOpenHashSet isCrushedPureOre = new IntOpenHashSet();
    public static IntOpenHashSet isPureDust = new IntOpenHashSet();
    public static IntOpenHashSet isImpureDust = new IntOpenHashSet();
    public static IntOpenHashSet isThermal = new IntOpenHashSet();
    public static IntOpenHashSet isOre = new IntOpenHashSet();
    public static boolean isInit = false;
    public ItemStack[] midProduct;
    public boolean mVoidStone = false;
    @Getter
    @Setter
    public int currentParallelism = 0;
    public int currentCircuitMultiplier = 0;
    public static final int MAX_PARA = 8;
    public static long RECIPE_EUT = 128;

    public SteamOreProcessorModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 7);
    }

    public SteamOreProcessorModule(String aName) {
        super(aName, 7);
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

        ArrayList<ItemStack> tInput = getStoredInputs();
        ArrayList<FluidStack> tInputFluid = getStoredFluids();
        if (tInput.isEmpty() || tInputFluid.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        currentCircuitMultiplier = 0;
        ItemStack circuit = getControllerSlot();
        if (GTUtility.isAnyIntegratedCircuit(circuit)) {
            currentCircuitMultiplier = MathHelper.clamp_int(circuit.getItemDamage(), 0, 24);
        }

        int powerMultiplier = (int) GTUtility.powInt(2, currentCircuitMultiplier);
        long requiredEUt = RECIPE_EUT * powerMultiplier;
        long availableEUt = GTUtility.roundUpVoltage(getBaseMetaTileEntity().getStoredEU());

        if (availableEUt < requiredEUt) {
            return CheckRecipeResultRegistry.insufficientPower(RECIPE_EUT);
        }

        int maxParallel = MAX_PARA * powerMultiplier;

        GTNL_OverclockCalculator calculator = new GTNL_OverclockCalculator().setEUt(availableEUt)
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
            if (isPureDust.contains(tID) || isImpureDust.contains(tID)
                || isCrushedPureOre.contains(tID)
                || isThermal.contains(tID)
                || isCrushedOre.contains(tID)
                || isOre.contains(tID)) {
                if (itemParallel + ore.stackSize <= currentParallel) {
                    itemParallel += ore.stackSize;
                } else {
                    itemParallel = currentParallel;
                    break;
                }
            }
        }
        currentParallel = itemParallel;
        if (currentParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        calculator.setCurrentParallel(currentParallel)
            .calculate();

        List<ItemStack> simulatedOres = new ArrayList<>();
        int remainingCost = currentParallel;
        for (ItemStack ore : tInput) {
            int tID = GTUtility.stackToInt(ore);
            if (tID == 0) continue;
            if (isPureDust.contains(tID) || isImpureDust.contains(tID)
                || isCrushedPureOre.contains(tID)
                || isThermal.contains(tID)
                || isCrushedOre.contains(tID)
                || isOre.contains(tID)) {
                if (remainingCost >= ore.stackSize) {
                    simulatedOres.add(GTUtility.copy(ore));
                    remainingCost -= ore.stackSize;
                } else {
                    simulatedOres.add(GTUtility.copyAmountUnsafe(remainingCost, ore));
                    break;
                }
            }
        }

        midProduct = simulatedOres.toArray(new ItemStack[0]);

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

        VoidProtectionHelper helper = new VoidProtectionHelper().setMachine(this)
            .setItemOutputs(midProduct)
            .setMaxParallel(currentParallel)
            .build();

        if (helper.isItemFull()) {
            return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
        }

        currentParallel = helper.getMaxParallel();
        setCurrentParallelism(currentParallel);

        int consumeLeft = currentParallel;
        for (ItemStack ore : tInput) {
            int tID = GTUtility.stackToInt(ore);
            if (tID == 0) continue;
            if (isPureDust.contains(tID) || isImpureDust.contains(tID)
                || isCrushedPureOre.contains(tID)
                || isThermal.contains(tID)
                || isCrushedOre.contains(tID)
                || isOre.contains(tID)) {
                if (consumeLeft >= ore.stackSize) {
                    consumeLeft -= ore.stackSize;
                    ore.stackSize = 0;
                } else {
                    ore.stackSize -= consumeLeft;
                    break;
                }
            }
        }

        depleteInput(GTModHandler.getDistilledWater(currentParallel * 10L), false);
        depleteInput(Materials.Lubricant.getFluid(currentParallel), false);

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        this.mOutputItems = midProduct;
        this.mMaxProgresstime = 128;
        this.lEUt = calculator.getConsumption();
        if (this.lEUt > 0) {
            this.lEUt = -this.lEUt;
        }
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public boolean checkTypes(int aID, IntOpenHashSet... aTables) {
        for (IntOpenHashSet set : aTables) {
            if (set.contains(aID)) {
                return true;
            }
        }
        return false;
    }

    public void doMac(IntOpenHashSet... aTables) {
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.maceratorRecipes.findRecipeQuery()
                        .caching(false)
                        .items(aStack)
                        .find();
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
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.oreWasherRecipes.findRecipeQuery()
                        .caching(false)
                        .items(aStack)
                        .fluids(GTModHandler.getDistilledWater(Integer.MAX_VALUE))
                        .find();
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
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.thermalCentrifugeRecipes.findRecipeQuery()
                        .caching(false)
                        .items(aStack)
                        .find();
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
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.centrifugeRecipes.findRecipeQuery()
                        .items(aStack)
                        .find();
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
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.sifterRecipes.findRecipeQuery()
                        .items(aStack)
                        .find();
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
        List<ItemStack> tProduct = new ArrayList<>();
        if (midProduct != null) {
            for (ItemStack aStack : midProduct) {
                int tID = GTUtility.stackToInt(aStack);
                if (checkTypes(tID, aTables)) {
                    GTRecipe tRecipe = RecipeMaps.chemicalBathRecipes.findRecipeQuery()
                        .items(aStack)
                        .fluids(getStoredFluids().toArray(new FluidStack[0]))
                        .find();
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

    public List<ItemStack> getOutputStack(GTRecipe aRecipe, int aTime) {
        List<ItemStack> tOutput = new ArrayList<>();
        for (int i = 0; i < aRecipe.mOutputs.length; i++) {
            if (aRecipe.getOutput(i) == null) {
                continue;
            }
            int tChance = aRecipe.getOutputChance(i);
            if (tChance == 10000) {
                tOutput.add(GTUtility.copyAmountUnsafe(aTime * aRecipe.getOutput(i).stackSize, aRecipe.getOutput(i)));
            } else {
                // Use Normal Distribution
                double u = aTime * (tChance / 10000D);
                double e = aTime * (tChance / 10000D) * (1 - (tChance / 10000D));
                Random random = new Random();
                int tAmount = (int) Math.ceil(Math.sqrt(e) * random.nextGaussian() + u);
                tOutput.add(GTUtility.copyAmountUnsafe(tAmount * aRecipe.getOutput(i).stackSize, aRecipe.getOutput(i)));
            }
        }
        return tOutput.stream()
            .filter(i -> (i != null && i.stackSize > 0))
            .collect(Collectors.toList());
    }

    public void doCompress(List<ItemStack> aList) {
        HashMap<Integer, Integer> rProduct = new HashMap<>();
        for (ItemStack stack : aList) {
            int tID = GTUtility.stackToInt(stack);
            if (mVoidStone) {
                if (GTUtility.areStacksEqual(Materials.Stone.getDust(1), stack)) {
                    continue;
                }
            }
            if (tID != 0) {
                if (rProduct.containsKey(tID)) {
                    rProduct.put(tID, rProduct.get(tID) + stack.stackSize);
                } else {
                    rProduct.put(tID, stack.stackSize);
                }
            }
        }
        midProduct = new ItemStack[rProduct.size()];
        int cnt = 0;
        for (Integer id : rProduct.keySet()) {
            ItemStack stack = GTUtility.intToStack(id);
            midProduct[cnt] = GTUtility.copyAmountUnsafe(rProduct.get(id), stack);
            cnt++;
        }
    }

    @Override
    public int clampRecipeOcCount(int value) {
        return Math.min(20, value);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mVoidStone = aNBT.getBoolean("mVoidStone");
        currentParallelism = aNBT.getInteger("currentParallelism");
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("mVoidStone", mVoidStone);
        aNBT.setInteger("currentParallelism", currentParallelism);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeName() {
        List<String> des = getDisplayMode(machineMode);
        return String.join("\n", des);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_BENDING);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_WASHPLANT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_SIMPLEWASHER);
    }

    @Override
    public int nextMachineMode() {
        return machineMode = (machineMode + 1) % 6;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (aPlayer.isSneaking()) {
            mVoidStone = !mVoidStone;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.void", mVoidStone));
            return;
        }
        machineMode = (machineMode + 1) % 6;
        List<String> des = getDisplayMode(machineMode);
        GTUtility.sendChatToPlayer(aPlayer, String.join("", des));
    }

    public static List<String> getDisplayMode(int mode) {
        EnumChatFormatting aqua = EnumChatFormatting.AQUA;
        String crush = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Macerate");
        String wash = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Ore_Washer")
            .replace(" ", " " + aqua);
        String thermal = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Thermal_Centrifuge")
            .replace(" ", " " + aqua);
        String centrifuge = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Centrifuge");
        String sifter = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Sifter");
        String chemWash = StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.Chemical_Bathing")
            .replace(" ", " " + aqua);
        String arrow = " " + aqua + "-> ";

        List<String> des = new ArrayList<>();
        des.add(StatCollector.translateToLocalFormatted("GT5U.multiblock.runningMode") + " ");

        switch (mode) {
            case 0 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + wash + arrow);
                des.add(aqua + thermal + arrow);
                des.add(aqua + crush + ' ');
            }
            case 1 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + wash + arrow);
                des.add(aqua + crush + arrow);
                des.add(aqua + centrifuge + ' ');
            }
            case 2 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + crush + arrow);
                des.add(aqua + centrifuge + ' ');
            }
            case 3 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + wash + arrow);
                des.add(aqua + sifter + ' ');
            }
            case 4 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + chemWash + arrow);
                des.add(aqua + crush + arrow);
                des.add(aqua + centrifuge + ' ');
            }
            case 5 -> {
                des.add(aqua + crush + arrow);
                des.add(aqua + chemWash + arrow);
                des.add(aqua + thermal + arrow);
                des.add(aqua + crush + ' ');
            }
            default -> des.add(StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor.WRONG_MODE"));
        }

        des.add(StatCollector.translateToLocalFormatted("GT5U.machines.oreprocessor2", 6.4));

        return des;

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

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void initHash() {
        for (String name : OreDictionary.getOreNames()) {
            if (name == null || name.isEmpty()) continue;
            if (name.startsWith("crushedPurified")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isCrushedPureOre.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("crushedCentrifuged")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isThermal.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("crushed")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isCrushedOre.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("dustImpure")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isImpureDust.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("dustPure")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isPureDust.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("ore")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isOre.add(GTUtility.stackToInt(ores.get(i)));
                }
            } else if (name.startsWith("rawOre")) {
                ArrayList<ItemStack> ores = OreDictionary.getOres(name);
                for (int i = 0, size = ores.size(); i < size; i++) {
                    isOre.add(GTUtility.stackToInt(ores.get(i)));
                }
            }
        }
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
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }
}
