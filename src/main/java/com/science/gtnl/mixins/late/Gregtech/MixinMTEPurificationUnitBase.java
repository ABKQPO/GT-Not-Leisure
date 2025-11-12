package com.science.gtnl.mixins.late.Gregtech;

import static com.science.gtnl.utils.Utils.*;
import static gregtech.api.util.GTUtility.*;
import static gregtech.common.misc.WirelessNetworkManager.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayer;
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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.api.mixinHelper.IWirelessMode;

import gregtech.api.gui.modularui.GTUITextures;
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
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPurificationUnitBase.class, remap = false)
public abstract class MixinMTEPurificationUnitBase extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationUnitBase>
    implements IWirelessMode {

    @Shadow
    public ArrayList<FluidStack> storedFluids;

    @Shadow
    public abstract CheckRecipeResult overrideRecipeCheck();

    @Shadow
    public GTRecipe currentRecipe;

    @Shadow
    public int effectiveParallel;

    @Shadow
    public int maxParallel;

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
    public float currentRecipeChance;

    @Shadow
    public abstract float calculateFinalSuccessChance();

    @Shadow
    public abstract float calculateBoostedSuccessChance();

    @Shadow
    @Final
    public static float WATER_BOOST_NEEDED_FLUID;

    @Unique
    public long maxParallelLong = 1;

    @Unique
    public long effectiveParallelLong = 1;

    @Unique
    public BigInteger costingEU = BigInteger.ZERO;

    @Unique
    public String costingEUText = ZERO_STRING;

    @Getter
    @Setter
    @Unique
    public boolean wirelessMode;

    @Unique
    public UUID ownerUUID;

    public MixinMTEPurificationUnitBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public boolean supportsCraftingMEBuffer() {
        return wirelessMode;
    }

    @Inject(method = "checkProcessing", at = @At("HEAD"), cancellable = true)
    public void checkProcessing(CallbackInfoReturnable<CheckRecipeResult> cir) {
        if (controller != null) this.wirelessMode = ((IWirelessMode) controller).isWirelessMode();
        if (!wirelessMode) return;
        this.storedFluids = this.getStoredFluids();
        costingEU = BigInteger.ZERO;
        costingEUText = ZERO_STRING;

        CheckRecipeResult result = overrideRecipeCheck();
        if (result == null) result = findRecipeForInputsLong(storedFluids.toArray(new FluidStack[] {}));

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
            effectiveParallelLong = Math.min(maxParallelLong, Math.floorDiv(amountAvailable, waterInput.amount));
            // This should not happen, throw an error
            if (effectiveParallelLong == 0) {
                cir.setReturnValue(CheckRecipeResultRegistry.INTERNAL_ERROR);
                return;
            }

            BigInteger costEU = BigInteger.valueOf(effectiveParallelLong)
                .multiply(BigInteger.valueOf(getBasePowerUsage()));

            if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(NEGATIVE_ONE))) {
                cir.setReturnValue(CheckRecipeResultRegistry.insufficientPower(costEU.longValue()));
                return;
            }
            costingEU = costingEU.add(costEU);
            costingEUText = GTUtility.formatNumbers(costingEU);
        }

        cir.setReturnValue(result);
    }

    @Inject(method = "doPurificationRecipeCheck", at = @At("HEAD"))
    public void doPurificationRecipeCheck(CallbackInfoReturnable<Boolean> cir) {
        effectiveParallelLong = 1;
    }

    @Inject(method = "startCycle", at = @At("HEAD"), cancellable = true)
    public void startCycle(int cycleTime, int progressTime, CallbackInfo ci) {
        if (!wirelessMode) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        startRecipeProcessing();
        // Important to calculate this before depleting inputs, otherwise we may get issues with boost items
        // disappearing.
        this.currentRecipeChance = this.calculateBoostedSuccessChance();

        // Deplete inputs from water boost if enabled.
        if (isWaterBoostedList(this.currentRecipe)) {
            ArrayList<FluidStack> inputWater = this.getWaterBoostAmountList(this.currentRecipe);
            this.depleteInputList(inputWater, false);
        }

        // Consume inputs, only if debug mode is off
        if (!getController().debugModeOn()) {
            this.depleteRecipeInputsLong();
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
            long scaledAmount = effectiveParallelLong * output.amount;

            fluidOutputList.addAll(splitLongToFluidStacks(output, scaledAmount));
        }

        ItemStack[] recipeOutputs = this.currentRecipe.mOutputs;
        ItemStack[] itemOutputs = new ItemStack[recipeOutputs.length];
        int[] mChances = this.currentRecipe.mChances;

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
    public ArrayList<FluidStack> getWaterBoostAmountList(GTRecipe recipe) {

        // Recipes should always be constructed so that output water is always the first fluid output
        FluidStack outputWater = recipe.mFluidOutputs[0];
        long totalAmount = Math
            .round((double) outputWater.amount * WATER_BOOST_NEEDED_FLUID * this.effectiveParallelLong);

        return new ArrayList<>(splitLongToFluidStacks(outputWater, totalAmount));
    }

    @Unique
    private ArrayList<FluidStack> splitLongToFluidStacks(FluidStack template, long amount) {
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
        this.effectiveParallelLong = 1;
    }

    @Unique
    public CheckRecipeResult findRecipeForInputsLong(FluidStack[] fluidInputs, ItemStack... itemInputs) {
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

    @Unique
    public void depleteRecipeInputsLong() {
        for (int i = 0; i < this.currentRecipe.mFluidInputs.length; ++i) {
            FluidStack input = this.currentRecipe.mFluidInputs[i];
            ArrayList<FluidStack> fluidStacks = new ArrayList<>();
            fluidStacks.add(input);
            if (i == 0) {
                fluidStacks = splitLongToFluidStacks(input, input.amount * effectiveParallelLong);
            }
            this.depleteInputList(fluidStacks, false);
        }
    }

    @Unique
    public boolean isWaterBoostedList(GTRecipe recipe) {
        ArrayList<FluidStack> inputWater = getWaterBoostAmountList(recipe);
        // Simulate input drain to see if we can water boost
        return depleteInputList(inputWater, true);
    }

    @Unique
    public boolean depleteInputList(ArrayList<FluidStack> fluids, boolean simulate) {
        if (fluids == null || fluids.isEmpty()) return false;

        Map<Fluid, Long> mergedStorage = new HashMap<>();
        for (FluidStack stored : getStoredFluids()) {
            if (stored != null) {
                mergedStorage.merge(stored.getFluid(), (long) stored.amount, Long::sum);
            }
        }

        Map<Fluid, Long> mergedNeeded = new HashMap<>();
        for (FluidStack needed : fluids) {
            if (needed != null) {
                mergedNeeded.merge(needed.getFluid(), (long) needed.amount, Long::sum);
            }
        }

        for (Map.Entry<Fluid, Long> neededEntry : mergedNeeded.entrySet()) {
            long availableAmount = mergedStorage.getOrDefault(neededEntry.getKey(), 0L);
            if (availableAmount < neededEntry.getValue()) return false;
        }

        if (simulate) return true;
        for (FluidStack needed : fluids) {
            int remaining = needed.amount;

            while (remaining > 0) {
                int drainedThisRound = 0;

                for (MTEHatch hatch : getAllInputHatches()) {
                    int drained = drainFluid(hatch, new FluidStack(needed.getFluid(), remaining), true);
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
    public int drainFluid(MTEHatch hatch, FluidStack fluid, boolean doDrain) {
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
    private List<MTEHatch> getAllInputHatches() {
        List<MTEHatch> dualHatches = mDualInputHatches.stream()
            .map(h -> (MTEHatch) h)
            .collect(Collectors.toList());

        List<MTEHatch> allHatches = new ArrayList<>(mInputHatches);
        allHatches.addAll(dualHatches);

        return allHatches;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) {
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
                rList.addAll(Arrays.asList(dualInputHatch.getAllFluids()));
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
        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) {
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
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                rList.addAll(Arrays.asList(dualInputHatch.getAllItems()));
            }
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (stackInSlot1 != null && stackInSlot1.getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;

    }

    @Inject(method = "getActualPowerUsage", at = @At("TAIL"), cancellable = true)
    public void getActualPowerUsage(CallbackInfoReturnable<Long> cir) {
        if (wirelessMode) cir.setReturnValue(0L);
    }

    @Inject(method = "addUIWidgets", at = @At("HEAD"))
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext, CallbackInfo ci) {
        builder.widget(new FakeSyncWidget.BooleanSyncer(() -> wirelessMode, val -> wirelessMode = val));
    }

    /**
     * @reason Overwrites the original {@code createParallelWindow} method to provide a custom GUI for setting
     *         the parallel processing level of the purification unit.
     * @author GTNotLeisure
     */
    @Overwrite
    public ModularWindow createParallelWindow(final EntityPlayer player) {
        final int WIDTH = 158;
        final int HEIGHT = 52;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);

        Widget parallelWindow;
        if (wirelessMode) {
            parallelWindow = new NumericWidget().setSetter(val -> maxParallelLong = (long) val)
                .setGetter(() -> maxParallelLong)
                .setBounds(1, Long.MAX_VALUE)
                .setDefaultValue(1)
                .setScrollValues(1, 1000, 10000)
                .setTextAlignment(Alignment.Center)
                .setTextColor(Color.WHITE.normal)
                .setSize(150, 18)
                .setPos(4, 25)
                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                .attachSyncer(
                    new FakeSyncWidget.LongSyncer(() -> maxParallelLong, (val) -> maxParallelLong = val),
                    builder);
        } else {
            parallelWindow = new NumericWidget().setSetter(val -> maxParallel = (int) val)
                .setGetter(() -> maxParallel)
                .setBounds(1, Integer.MAX_VALUE)
                .setDefaultValue(1)
                .setScrollValues(1, 4, 64)
                .setTextAlignment(Alignment.Center)
                .setTextColor(Color.WHITE.normal)
                .setSize(150, 18)
                .setPos(4, 25)
                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                .attachSyncer(new FakeSyncWidget.IntegerSyncer(() -> maxParallel, (val) -> maxParallel = val), builder);
        }

        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("GTPP.CC.parallel")
                .setPos(3, 4)
                .setSize(150, 20))
            .widget(parallelWindow);
        return builder.build();
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        wirelessMode = aNBT.getBoolean("wirelessMode");
        if (wirelessMode) {
            maxParallelLong = aNBT.getLong("configuredParallel");
            effectiveParallelLong = aNBT.getLong("effectiveParallel");
        } else {
            maxParallel = (int) Math.min(Integer.MAX_VALUE, aNBT.getLong("configuredParallel"));
            effectiveParallel = (int) Math.min(Integer.MAX_VALUE, aNBT.getLong("effectiveParallel"));
        }
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        aNBT.setBoolean("wirelessMode", wirelessMode);
        if (wirelessMode) {
            aNBT.setLong("configuredParallel", maxParallelLong);
            aNBT.setLong("effectiveParallel", effectiveParallelLong);
        } else {
            aNBT.setInteger("configuredParallel", maxParallel);
            aNBT.setInteger("effectiveParallel", effectiveParallel);
        }
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        // If this purification unit is linked to a controller, add this info to the scanner output.
        if (getController() != null) {
            ret.add(
                StatCollector.translateToLocalFormatted(
                    "GT5U.infodata.purification_unit_base.linked_at",
                    controllerX,
                    controllerY,
                    controllerZ));

            // If recipe is running, display success chance
            if (this.mMaxProgresstime != 0) {
                ret.add(
                    StatCollector.translateToLocalFormatted(
                        "GT5U.infodata.purification_unit_base.success_chance",
                        EnumChatFormatting.YELLOW + GTUtility.formatNumbers(this.calculateFinalSuccessChance())
                            + "%"
                            + EnumChatFormatting.RESET));
            }

        } else ret.add(StatCollector.translateToLocal("GT5U.infodata.purification_unit_base.not_linked"));
        ret.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                "" + EnumChatFormatting.YELLOW
                    + (this.wirelessMode ? this.effectiveParallelLong : this.effectiveParallel)));
        if (wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        return ret.toArray(new String[0]);
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
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
    }

}
