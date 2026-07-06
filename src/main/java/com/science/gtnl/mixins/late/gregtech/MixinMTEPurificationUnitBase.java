package com.science.gtnl.mixins.late.gregtech;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.ICostingEUHolder;
import com.science.gtnl.api.mixinHelper.IPurificationUnitLongParallel;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.metadata.PurificationPlantBaseChanceKey;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPurificationUnitBase.class, remap = false)
public abstract class MixinMTEPurificationUnitBase extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationUnitBase>
    implements IWirelessMode, ICostingEUHolder, IPurificationUnitLongParallel {

    @Shadow
    protected ArrayList<FluidStack> storedFluids;

    @Shadow
    public abstract CheckRecipeResult overrideRecipeCheck();

    @Shadow
    protected GTRecipe currentRecipe;

    @Shadow
    protected int effectiveParallel;

    @Shadow
    protected int maxParallel;

    @Shadow
    public abstract long getBasePowerUsage();

    @Shadow
    private MTEPurificationPlant controller;

    @Shadow
    public abstract MTEPurificationPlant getController();

    @Shadow
    private int controllerX;
    @Shadow
    private int controllerY;
    @Shadow
    private int controllerZ;
    @Shadow
    protected float currentRecipeChance;

    @Shadow
    public abstract float calculateFinalSuccessChance();

    @Shadow
    public abstract float calculateBoostedSuccessChance();

    @Shadow
    @Final
    public static float WATER_BOOST_NEEDED_FLUID;

    @Unique
    public long gtnl$maxParallelLong = 1;

    @Unique
    public long gtnl$effectiveParallelLong = 1;

    @Getter
    @Unique
    public BigInteger gtnl$costingEU = BigInteger.ZERO;

    @Unique
    public String gtnl$costingEUText = Utils.ZERO_STRING;

    @Getter
    @Setter
    @Unique
    public boolean gtnl$wirelessMode;

    @Unique
    public UUID gtnl$ownerUUID;

    public MixinMTEPurificationUnitBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.gtnl$ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public boolean supportsCraftingMEBuffer() {
        return gtnl$wirelessMode;
    }

    @Override
    public long gtnl$getMaxParallelLong() {
        return gtnl$maxParallelLong;
    }

    @Override
    public void gtnl$setMaxParallelLong(long maxParallel) {
        gtnl$maxParallelLong = Math.max(1, maxParallel);
    }

    @Override
    public long gtnl$getEffectiveParallelLong() {
        return gtnl$effectiveParallelLong;
    }

    @Inject(method = "checkProcessing", at = @At("HEAD"), cancellable = true)
    public void checkProcessing(CallbackInfoReturnable<CheckRecipeResult> cir) {
        if (controller != null) this.gtnl$wirelessMode = ((IWirelessMode) controller).isGtnl$wirelessMode();
        if (!gtnl$wirelessMode) return;
        this.storedFluids = this.getStoredFluids();
        gtnl$costingEU = BigInteger.ZERO;
        gtnl$costingEUText = Utils.ZERO_STRING;

        CheckRecipeResult result = overrideRecipeCheck();
        if (result == null) result = gtnl$findRecipeForInputsLong(storedFluids.toArray(new FluidStack[] {}));

        if (result.wasSuccessful()) {
            FluidStack waterInput = this.currentRecipe.mFluidInputs[0];
            // Count total available purified water input of the previous step
            long amountAvailable = 0;
            for (FluidStack fluid : this.storedFluids) {
                if (fluid.isFluidEqual(waterInput)) {
                    amountAvailable += fluid.amount;
                }
            }

            // Determine effective parallel
            gtnl$effectiveParallelLong = Math
                .min(gtnl$maxParallelLong, Math.floorDiv(amountAvailable, (long) waterInput.amount));
            // This should not happen, throw an error
            if (gtnl$effectiveParallelLong == 0) {
                cir.setReturnValue(GTNLParallelHelper.PARALLEL_ZERO);
                return;
            }

            BigInteger costEU = BigInteger.valueOf(gtnl$effectiveParallelLong)
                .multiply(BigInteger.valueOf(getBasePowerUsage()));

            if (!addEUToGlobalEnergyMap(gtnl$ownerUUID, costEU.multiply(Utils.NEGATIVE_ONE))) {
                cir.setReturnValue(CheckRecipeResultRegistry.insufficientPower(costEU.longValue()));
                return;
            }
            gtnl$costingEU = gtnl$costingEU.add(costEU);
            gtnl$costingEUText = NumberFormatUtil.formatNumber(gtnl$costingEU);
        }

        cir.setReturnValue(result);
    }

    @Inject(method = "doPurificationRecipeCheck", at = @At("HEAD"))
    public void doPurificationRecipeCheck(CallbackInfoReturnable<Boolean> cir) {
        gtnl$effectiveParallelLong = 1;
    }

    @Inject(method = "startCycle", at = @At("HEAD"), cancellable = true)
    public void startCycle(int cycleTime, int progressTime, CallbackInfo ci) {
        if (!gtnl$wirelessMode) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        startRecipeProcessing();
        // Important to calculate this before depleting inputs, otherwise we may get issues with boost items
        // disappearing.
        this.currentRecipeChance = this.calculateBoostedSuccessChance();

        // Deplete inputs from water boost if enabled.
        if (gtnl$isWaterBoostedList(this.currentRecipe)) {
            List<FluidStack> inputWater = this.gtnl$getWaterBoostAmountList(this.currentRecipe);
            this.gtnl$depleteInputList(inputWater, false);
        }

        // Consume inputs, only if debug mode is off
        if (!getController().debugModeOn()) {
            this.gtnl$depleteRecipeInputsLong();
        }
        // Initialize recipe and progress information.
        this.mMaxProgresstime = cycleTime;
        this.mProgresstime = progressTime;
        this.mEfficiency = 10000;
        // These need to be set so the GUI code can display the produced outputs

        // Make sure to scale purified water output with parallel amount.
        // Make sure to make a full copy of the array, so we don't go modifying recipes
        ArrayList<FluidStack> fluidOutputList = new ArrayList<>();
        for (int i = 0; i < this.currentRecipe.mFluidOutputs.length; ++i) {
            FluidStack output = this.currentRecipe.mFluidOutputs[i].copy();
            long scaledAmount = gtnl$effectiveParallelLong * output.amount;

            fluidOutputList.addAll(gtnl$splitLongToFluidStacks(output, scaledAmount));
        }

        ItemStack[] recipeOutputs = this.currentRecipe.mOutputs;
        ItemStack[] itemOutputs = new ItemStack[recipeOutputs.length];
        int[] mChances = this.currentRecipe.mOutputChances;

        // If this recipe has random item outputs, roll on it and add to outputs
        if (mChances != null) {
            // Roll on each output individually
            for (int i = 0; i < recipeOutputs.length; ++i) {
                // Recipes store probabilities as a value ranging from 1-10000
                int roll = random.nextInt(10000);
                if (roll <= mChances[i]) {
                    itemOutputs[i] = recipeOutputs[i].copy();
                }
            }
        } else {
            // Guaranteed item output
            for (int i = 0; i < recipeOutputs.length; ++i) {
                itemOutputs[i] = recipeOutputs[i].copy();
            }
        }

        this.mOutputFluids = fluidOutputList.toArray(new FluidStack[0]);
        this.mOutputItems = itemOutputs;
        // Set this value, so it can be displayed in Waila. Note that the logic for the units is
        // specifically overridden so setting this value does not actually drain power.
        // Instead, power is drained by the main purification plant controller.
        this.lEUt = 0;
        endRecipeProcessing();

        ci.cancel();
    }

    @Unique
    public List<FluidStack> gtnl$getWaterBoostAmountList(GTRecipe recipe) {

        // Recipes should always be constructed so that output water is always the first fluid output
        FluidStack outputWater = recipe.mFluidOutputs[0];
        long totalAmount = Math
            .round((double) outputWater.amount * WATER_BOOST_NEEDED_FLUID * this.gtnl$effectiveParallelLong);

        return new ArrayList<>(gtnl$splitLongToFluidStacks(outputWater, totalAmount));
    }

    @Unique
    private ArrayList<FluidStack> gtnl$splitLongToFluidStacks(FluidStack template, long amount) {
        ArrayList<FluidStack> list = new ArrayList<>();
        long remaining = amount;
        while (remaining > 0) {
            int split = (int) Math.min(Integer.MAX_VALUE, remaining);
            list.add(new FluidStack(template.getFluid(), split));
            remaining -= split;
        }
        return list;
    }

    @Inject(method = "endCycle", at = @At("TAIL"))
    private void onEndCycle(CallbackInfo ci) {
        this.gtnl$effectiveParallelLong = 1;
    }

    @Unique
    public CheckRecipeResult gtnl$findRecipeForInputsLong(FluidStack[] fluidInputs, ItemStack... itemInputs) {
        RecipeMap<?> recipeMap = this.getRecipeMap();

        // Grab a stream of recipes and find the one with the highest success chance
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

        this.currentRecipe = recipe;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Redirect(
        method = "calculateBoostedSuccessChance",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/tileentities/machines/multi/purification/MTEPurificationUnitBase;isWaterBoosted(Lgregtech/api/util/GTRecipe;)Z"))
    private boolean redirectWaterBoost(MTEPurificationUnitBase<?> instance, GTRecipe recipe) {
        if (gtnl$wirelessMode) {
            return gtnl$isWaterBoostedList(recipe);
        }
        return instance.isWaterBoosted(recipe);
    }

    @Unique
    public void gtnl$depleteRecipeInputsLong() {
        for (int i = 0; i < this.currentRecipe.mFluidInputs.length; ++i) {
            FluidStack input = this.currentRecipe.mFluidInputs[i];
            ArrayList<FluidStack> fluidStacks = new ArrayList<>();
            fluidStacks.add(input);
            if (i == 0) {
                fluidStacks = gtnl$splitLongToFluidStacks(input, input.amount * gtnl$effectiveParallelLong);
            }
            this.gtnl$depleteInputList(fluidStacks, false);
        }
    }

    @Unique
    public boolean gtnl$isWaterBoostedList(GTRecipe recipe) {
        List<FluidStack> inputWater = gtnl$getWaterBoostAmountList(recipe);
        // Simulate input drain to see if we can water boost
        return gtnl$depleteInputList(inputWater, true);
    }

    @Unique
    public boolean gtnl$depleteInputList(List<FluidStack> fluids, boolean simulate) {
        if (fluids == null || fluids.isEmpty()) return false;

        Object2LongOpenHashMap<Fluid> mergedStorage = new Object2LongOpenHashMap<>();
        for (FluidStack stored : getStoredFluids()) {
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

        for (Map.Entry<Fluid, Long> neededEntry : mergedNeeded.entrySet()) {
            long availableAmount = mergedStorage.getLong(neededEntry.getKey());
            if (availableAmount < neededEntry.getValue()) return false;
        }

        if (simulate) return true;
        for (FluidStack needed : fluids) {
            int remaining = needed.amount;

            while (remaining > 0) {
                int drainedThisRound = 0;

                for (MTEHatch hatch : gtnl$getAllInputHatches()) {
                    int drained = gtnl$drainFluid(hatch, new FluidStack(needed.getFluid(), remaining), true);
                    drainedThisRound += drained;
                }

                if (drainedThisRound <= 0) {
                    break;
                }

                remaining -= drainedThisRound;
            }
        }

        return true;
    }

    @Unique
    public int gtnl$drainFluid(MTEHatch hatch, FluidStack fluid, boolean doDrain) {
        if (fluid == null || hatch == null) return 0;

        if (supportsCraftingMEBuffer() && hatch instanceof IDualInputHatch tHatch && tHatch.supportsFluids()) {
            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : Lists.newArrayList(inventory.getFluidInputs())) {
                    if (stored != null && stored.amount > 0 && stored.isFluidEqual(fluid)) {
                        int deduct = Math.min(stored.amount, fluid.amount);
                        if (doDrain) stored.amount -= deduct;
                        return deduct;
                    }
                }
            }
        }

        if (hatch instanceof MTEHatchInput tHatch && tHatch.isValid()) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                meHatch.startRecipeProcessing();
                FluidStack drained = meHatch.drain(ForgeDirection.UNKNOWN, fluid, doDrain);
                meHatch.endRecipeProcessing(this);
                return drained != null ? Math.min(drained.amount, fluid.amount) : 0;
            } else {
                FluidStack drained = tHatch.drain(ForgeDirection.UNKNOWN, fluid, doDrain);
                return drained != null ? Math.min(drained.amount, fluid.amount) : 0;
            }
        }

        return 0;
    }

    @Unique
    private List<MTEHatch> gtnl$getAllInputHatches() {
        List<MTEHatch> dualHatches = mDualInputHatches.stream()
            .map(h -> (MTEHatch) h)
            .toList();

        List<MTEHatch> allHatches = new ArrayList<>(mInputHatches);
        allHatches.addAll(dualHatches);

        return GTUtility.filterValidMTEs(allHatches);
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = tHatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            setHatchRecipeMap(tHatch);
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        // Prevent the same fluid from different ME hatches from being recognized
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
                    }
                }
            } else {
                FluidStack fillableStack = tHatch.getFillableStack();
                if (fillableStack != null) {
                    rList.add(fillableStack);
                }
            }
        }

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                for (FluidStack fluidStack : dualInputHatch.getAllFluids()) {
                    if (fluidStack != null) {
                        rList.add(fluidStack);
                    }
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<ItemId, ItemStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                for (ItemStack itemStack : dualInputHatch.getAllItems()) {
                    if (itemStack != null) {
                        rList.add(itemStack);
                    }
                }
            }
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(stackInSlot1)) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;

    }

    @Inject(method = "getActualPowerUsage", at = @At("TAIL"), cancellable = true)
    public void getActualPowerUsage(CallbackInfoReturnable<Long> cir) {
        if (gtnl$wirelessMode) cir.setReturnValue(0L);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        if (aNBT.hasKey("wirelessMode")) gtnl$wirelessMode = aNBT.getBoolean("wirelessMode");
        if (aNBT.hasKey("configuredParallelLong")) gtnl$maxParallelLong = aNBT.getLong("configuredParallelLong");
        if (aNBT.hasKey("effectiveParallelLong")) gtnl$effectiveParallelLong = aNBT.getLong("effectiveParallelLong");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        aNBT.setBoolean("wirelessMode", gtnl$wirelessMode);
        aNBT.setLong("configuredParallelLong", gtnl$maxParallelLong);
        aNBT.setLong("effectiveParallelLong", gtnl$effectiveParallelLong);
    }

    @Inject(method = "getInfoData", at = @At("TAIL"))
    public void getInfoData(CallbackInfoReturnable<String[]> cir, @Local(name = "ret") ArrayList<String> ret) {
        ret.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                "" + EnumChatFormatting.YELLOW
                    + (this.gtnl$wirelessMode ? this.gtnl$effectiveParallelLong : this.effectiveParallel)
                    + "(Long)"));
        if (gtnl$wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + gtnl$costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Inject(method = "getWailaBody", at = @At("HEAD"))
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfo ci) {
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currenttip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currenttip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Inject(method = "getWailaNBTData", at = @At("HEAD"))
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z, CallbackInfo ci) {
        if (getBaseMetaTileEntity() != null) {
            tag.setBoolean("wirelessMode", gtnl$wirelessMode);
            if (gtnl$wirelessMode) tag.setString("costingEUText", gtnl$costingEUText);
        }
    }

}
