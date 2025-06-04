package com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW;

import static gregtech.api.util.GTUtility.formatNumbers;
import static gregtech.api.util.ParallelHelper.calculateChancedOutputMultiplier;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.misc.WirelessNetworkManager.strongCheckOrAddUser;
import static java.lang.Math.*;
import static kekztech.util.Util.toStandardForm;
import static net.minecraft.util.EnumChatFormatting.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import gregtech.common.tileentities.machines.MTEHatchOutputME;
import gtneioreplugin.plugin.block.ModBlocks;
import tectech.TecTech;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.recipe.TecTechRecipeMaps;
import tectech.util.CommonValues;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

public class ETGWEyeOfHarmonyModule extends EternalGregTechWorkshopModule {

    private BigInteger outputEU_BigInt = BigInteger.ZERO;
    private long startEU = 0;
    private long currentCircuitMultiplier = 0;
    private boolean enableRawStarMatter;

    private List<ItemStackLong> outputItems = new ArrayList<>();
    private List<FluidStackLong> outputFluids = new ArrayList<>();

    private final Map<Fluid, Long> validFluidMap = new HashMap<>() {

        private static final long serialVersionUID = -8452610443191188130L;

        {
            put(Materials.Hydrogen.mGas, 0L);
            put(Materials.Helium.mGas, 0L);
            put(MaterialsUEVplus.RawStarMatter.mFluid, 0L);
        }
    };

    private EyeOfHarmonyRecipe currentRecipe;

    // Counter for lag prevention.
    private long lagPreventer = 0;

    // Check for recipe every recipeCheckInterval ticks.
    private boolean recipeRunning = false;
    private long astralArrayAmount = 50000;
    private long parallelAmount = 65536;
    private long successfulParallelAmount = 0;
    private double hydrogenOverflowProbabilityAdjustment;
    private double heliumOverflowProbabilityAdjustment;
    private double stellarPlasmaOverflowProbabilityAdjustment;
    private double yield = 0;
    private BigInteger usedEU = BigInteger.ZERO;
    private FluidStackLong stellarPlasma;
    private FluidStackLong starMatter;

    // NBT save/load strings.
    private static final String EYE_OF_HARMONY = "eyeOfHarmonyOutput";
    private static final String NUMBER_OF_ITEMS_NBT_TAG = EYE_OF_HARMONY + "numberOfItems";
    private static final String NUMBER_OF_FLUIDS_NBT_TAG = EYE_OF_HARMONY + "numberOfFluids";
    private static final String ITEM_OUTPUT_NBT_TAG = EYE_OF_HARMONY + "itemOutput";
    private static final String FLUID_OUTPUT_NBT_TAG = EYE_OF_HARMONY + "fluidOutput";
    private static final String RECIPE_RUNNING_NBT_TAG = EYE_OF_HARMONY + "recipeRunning";
    private static final String CURRENT_RECIPE_STAR_MATTER_TAG = EYE_OF_HARMONY + "recipeStarMatter";
    private static final String CURRENT_RECIPE_STELLAR_PLASMA_TAG = EYE_OF_HARMONY + "recipeStellarPlasma";
    private static final String CURRENT_RECIPE_FIXED_OUTPUTS_TAG = EYE_OF_HARMONY + "recipeFixedOutputs";
    private static final String RECIPE_SUCCESS_CHANCE_NBT_TAG = EYE_OF_HARMONY + "recipeSuccessChance";
    private static final String ROCKET_TIER_NBT_TAG = EYE_OF_HARMONY + "rocketTier";
    private static final String CURRENT_CIRCUIT_MULTIPLIER_TAG = EYE_OF_HARMONY + "currentCircuitMultiplier";
    private static final String CALCULATED_EU_OUTPUT_NBT_TAG = EYE_OF_HARMONY + "outputEU_BigInt";
    private static final String PARALLEL_AMOUNT_NBT_TAG = EYE_OF_HARMONY + "parallelAmount";
    private static final String YIELD_NBT_TAG = EYE_OF_HARMONY + "yield";
    private static final String SUCCESSFUL_PARALLEL_AMOUNT_NBT_TAG = EYE_OF_HARMONY + "successfulParallelAmount";
    private static final String ASTRAL_ARRAY_AMOUNT_NBT_TAG = EYE_OF_HARMONY + "astralArrayAmount";
    private static final String CALCULATED_EU_INPUT_NBT_TAG = EYE_OF_HARMONY + "usedEU";
    private static final String EXTRA_PITY_CHANCE_BOOST_NBT_TAG = EYE_OF_HARMONY + "pityChance";
    private static final String PREVIOUS_RECIPE_CHANCE_NBT_TAG = EYE_OF_HARMONY + "previousChance";

    // Sub tags, less specific names required.
    private static final String STACK_SIZE = "stackSize";
    private static final String ITEM_STACK_NBT_TAG = "itemStack";
    private static final String FLUID_AMOUNT = "fluidAmount";
    private static final String FLUID_STACK_NBT_TAG = "fluidStack";

    // Tags for pre-setting
    public static final String PLANET_BLOCK = "planetBlock";

    public ETGWEyeOfHarmonyModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ETGWEyeOfHarmonyModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ETGWEyeOfHarmonyModule(this.mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        // Only for visual
        return TecTechRecipeMaps.eyeOfHarmonyRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ETGWEyeOfHarmonyModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_10"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 5, 7, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ETGWEyeOfHarmonyModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        enableRawStarMatter = !enableRawStarMatter;
        aPlayer.addChatMessage(
            new ChatComponentText(
                "Use Raw Star Matter are now " + (enableRawStarMatter ? "enabled" : "disabled") + "."));
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return null;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerStack = getControllerSlot();
        if (controllerStack == null) {
            return SimpleCheckRecipeResult.ofFailure("no_planet_block");
        }

        lagPreventer++;
        if (lagPreventer < 60) {
            lagPreventer = 0;
            // No item in multi gui slot.

            currentRecipe = TecTech.eyeOfHarmonyRecipeStorage.recipeLookUp(controllerStack);
            if (currentRecipe == null) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            CheckRecipeResult result = processRecipe(currentRecipe);
            if (!result.wasSuccessful()) currentRecipe = null;
            return result;
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public CheckRecipeResult processRecipe(EyeOfHarmonyRecipe recipeObject) {
        // Get circuit damage, clamp it and then use it later for overclocking.
        currentCircuitMultiplier = 0;
        for (ItemStack itemStack : mInputBusses.get(0)
            .getRealInventory()) {
            if (GTUtility.isAnyIntegratedCircuit(itemStack)) {
                currentCircuitMultiplier = MathHelper.clamp_int(itemStack.getItemDamage(), 0, 24);
                break;
            }
        }

        if (enableRawStarMatter) {
            if (getStellarPlasmaStored() < currentRecipe.getHeliumRequirement() * 0.8) {
                return SimpleCheckRecipeResult.ofFailure("no_stellar_plasma");
            }
        } else {
            if (getHydrogenStored() < currentRecipe.getHydrogenRequirement()) {
                return SimpleCheckRecipeResult.ofFailure("no_hydrogen");
            }

            if (getHeliumStored() < currentRecipe.getHeliumRequirement()) {
                return SimpleCheckRecipeResult.ofFailure("no_helium");
            }
        }

        // Determine EU recipe input
        startEU = recipeObject.getEUStartCost();

        // Calculate normal EU values
        outputEU_BigInt = BigInteger.valueOf(recipeObject.getEUOutput() * 100);
        usedEU = BigInteger.valueOf(-startEU)
            .multiply(BigInteger.valueOf((long) Math.pow(8, currentCircuitMultiplier)));

        // Remove EU from the users network.
        if (!addEUToGlobalEnergyMap(ownerUUID, usedEU)) {
            return CheckRecipeResultRegistry.insufficientStartupPower(usedEU.abs());
        }

        mMaxProgresstime = (int) (recipeObject.getRecipeTimeInTicks() * 0.1
            / Math.max(1, pow(2, currentCircuitMultiplier)));

        calculateInputFluidExcessValues(recipeObject.getHydrogenRequirement(), recipeObject.getHeliumRequirement());
        // If pityChance needs to be reset, it will be set to Double.MIN_Value at the end of the recipe.
        if (pityChance == Double.MIN_VALUE) {
            pityChance = currentRecipe.getBaseRecipeSuccessChance();
        }

        successChance = recipeChanceCalculator();
        currentRecipeRocketTier = currentRecipe.getRocketTier();

        // Reduce internal storage by input fluid quantity required for recipe.
        if (enableRawStarMatter) {
            validFluidMap.put(MaterialsUEVplus.RawStarMatter.mFluid, 0L);
        } else {
            validFluidMap.put(Materials.Hydrogen.mGas, 0L);
            validFluidMap.put(Materials.Helium.mGas, 0L);
        }

        // Return copies of the output objects.
        outputFluids = recipeObject.getOutputFluids();
        outputItems = recipeObject.getOutputItems();

        // Star matter is always the last element in the array.
        starMatter = new FluidStackLong(outputFluids.get(outputFluids.size() - 1));

        // And stellar plasma is the second last.
        stellarPlasma = new FluidStackLong(outputFluids.get(outputFluids.size() - 2));

        successfulParallelAmount = (long) calculateChancedOutputMultiplier(
            (int) ((successChance + pityChance) * 10000),
            65536);
        // Iterate over item output list and apply yield & successful parallel values.
        for (ItemStackLong itemStackLong : outputItems) {
            itemStackLong.stackSize *= successfulParallelAmount;
        }

        // Iterate over fluid output list and apply yield & successful parallel values.
        for (FluidStackLong fluidStackLong : outputFluids) {
            fluidStackLong.amount *= successfulParallelAmount;
        }

        updateSlots();

        recipeRunning = true;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private void calculateInputFluidExcessValues(final long hydrogenRecipeRequirement,
        final long heliumRecipeRequirement) {

        double hydrogenStored = getHydrogenStored();
        double heliumStored = getHeliumStored();
        double stellarPlasmaStored = getStellarPlasmaStored();

        double hydrogenExcessPercentage = hydrogenStored / hydrogenRecipeRequirement - 1;
        double heliumExcessPercentage = heliumStored / heliumRecipeRequirement - 1;
        double stellarPlasmaExcessPercentage = stellarPlasmaStored
            / (heliumRecipeRequirement * (12.4 / 1_000_000f) * parallelAmount) - 1;

        hydrogenOverflowProbabilityAdjustment = 1 - exp(-pow(30 * hydrogenExcessPercentage, 2));
        heliumOverflowProbabilityAdjustment = 1 - exp(-pow(30 * heliumExcessPercentage, 2));
        stellarPlasmaOverflowProbabilityAdjustment = 1 - exp(-pow(30 * stellarPlasmaExcessPercentage, 2));
    }

    private double recipeChanceCalculator() {
        double chance = 0.6;

        if (parallelAmount > 1) {
            chance -= stellarPlasmaOverflowProbabilityAdjustment;
        } else {
            chance -= (hydrogenOverflowProbabilityAdjustment + heliumOverflowProbabilityAdjustment);
        }

        return MathHelper.clamp_double(chance, 0.0, 1.0);
    }

    private long getHydrogenStored() {
        return validFluidMap.get(Materials.Hydrogen.mGas);
    }

    private long getHeliumStored() {
        return validFluidMap.get(Materials.Helium.mGas);
    }

    private long getStellarPlasmaStored() {
        return validFluidMap.get(MaterialsUEVplus.RawStarMatter.mFluid);
    }

    private double pityChance;
    private double successChance;
    private double previousRecipeChance;
    private long currentRecipeRocketTier;

    private void outputFailedChance() {
        long failedParallelAmount = parallelAmount - successfulParallelAmount;
        if (failedParallelAmount > 0) {
            outputFluidToAENetwork(
                MaterialsUEVplus.SpaceTime.getMolten(1),
                (long) ((0.5 * 14_400L * pow(2, currentRecipeRocketTier + 1)) * failedParallelAmount));
            pityChance += 0.1;
        } else {
            pityChance = Double.MIN_VALUE;
        }
        outputAfterRecipe();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            mTotalRunTime++;
            if (mEfficiency < 0) {
                mEfficiency = 0;
            }

            if (--mUpdate == 0 || --mStartUpCheck == 0
                || cyclicUpdate()
                || aBaseMetaTileEntity.hasWorkJustBeenEnabled()) {
                clearHatches();

                if (aBaseMetaTileEntity instanceof BaseTileEntity) {
                    ((BaseTileEntity) aBaseMetaTileEntity).ignoreUnloadedChunks = mMachine;
                }
                mMachine = checkMachine(aBaseMetaTileEntity, mInventory[1]);
            }

            if (mStartUpCheck < 0) {
                if (mMachine) {
                    byte Tick = (byte) (aTick % 20);
                    if (CommonValues.MULTI_CHECK_AT == Tick) {
                        checkMaintenance();
                    }

                    if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) {
                        if (onRunningTick(mInventory[1])) {
                            if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
                                stopMachine(ShutDownReasonRegistry.POLLUTION_FAIL);
                            }

                            if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
                                outputAfterRecipe();
                                addClassicOutputs();
                                updateSlots();
                                mProgresstime = 0;
                                mMaxProgresstime = 0;
                                mEfficiencyIncrease = 0;
                                mLastWorkingTick = mTotalRunTime;

                                if (aBaseMetaTileEntity.isAllowedToWork()) {
                                    if (checkRecipe()) {
                                        mEfficiency = 10000;
                                    } else {
                                        afterRecipeCheckFailed();
                                    }
                                    updateSlots();
                                } else {
                                    stopMachine(ShutDownReasonRegistry.NONE);
                                }
                            }
                        }
                    } else if (CommonValues.RECIPE_AT == Tick || aBaseMetaTileEntity.hasWorkJustBeenEnabled()) {
                        if (aBaseMetaTileEntity.isAllowedToWork()) {
                            if (checkRecipe()) {
                                mEfficiency = 10000;
                            } else {
                                afterRecipeCheckFailed();
                            }
                            updateSlots();
                        }
                    }

                } else if (aBaseMetaTileEntity.isAllowedToWork()) {
                    stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                }
            }

            aBaseMetaTileEntity.setActive(mMaxProgresstime > 0);
            boolean active = aBaseMetaTileEntity.isActive() && mPollution > 0;
            setMufflers(active);
        } else {
            doActivitySound(getActivitySoundLoop());
        }
    }

    private boolean cyclicUpdate() {
        if (mUpdate <= -1000) {
            mUpdate = 0;
            return true;
        }
        return false;
    }

    protected void afterRecipeCheckFailed() {
        mOutputItems = null;
        mOutputFluids = null;
        mEfficiency = 0;
        mEfficiencyIncrease = 0;
        mProgresstime = 0;
        mMaxProgresstime = 0;
    }

    protected void addClassicOutputs() {
        if (mOutputItems != null) {
            for (ItemStack tStack : mOutputItems) {
                if (tStack != null) {
                    addOutput(tStack);
                }
            }
        }
        mOutputItems = null;

        if (mOutputFluids != null) {
            if (mOutputFluids.length == 1) {
                for (FluidStack tStack : mOutputFluids) {
                    if (tStack != null) {
                        addOutput(tStack);
                    }
                }
            } else if (mOutputFluids.length > 1) {
                addFluidOutputs(mOutputFluids);
            }
        }
        mOutputFluids = null;
    }

    public void outputAfterRecipe() {
        recipeRunning = false;

        // Output EU
        addEUToGlobalEnergyMap(ownerUUID, outputEU_BigInt);

        startEU = 0;
        outputEU_BigInt = BigInteger.ZERO;

        outputFailedChance();

        if (successfulParallelAmount > 0) {
            for (ItemStackLong itemStack : outputItems) {
                outputItemToAENetwork(itemStack.itemStack, itemStack.stackSize);
            }

            for (FluidStackLong fluidStack : outputFluids) {
                outputFluidToAENetwork(fluidStack.fluidStack, fluidStack.amount);
            }
        }

        // Clear the array list for new recipes.
        outputItems = new ArrayList<>();
        outputFluids = new ArrayList<>();
    }

    private void outputItemToAENetwork(ItemStack item, long amount) {
        if (item == null || amount <= 0) return;

        while (amount >= Integer.MAX_VALUE) {
            ItemStack tmpItem = item.copy();
            tmpItem.stackSize = Integer.MAX_VALUE;
            mOutputBusses.get(0)
                .storePartial(tmpItem);
            amount -= Integer.MAX_VALUE;
        }
        ItemStack tmpItem = item.copy();
        tmpItem.stackSize = (int) amount;
        mOutputBusses.get(0)
            .storePartial(tmpItem);
    }

    private void outputFluidToAENetwork(FluidStack fluid, long amount) {
        if (fluid == null || amount <= 0) return;

        while (amount >= Integer.MAX_VALUE) {
            FluidStack tmpFluid = fluid.copy();
            tmpFluid.amount = Integer.MAX_VALUE;
            ((MTEHatchOutputME) mOutputHatches.get(0)).tryFillAE(tmpFluid);
            amount -= Integer.MAX_VALUE;
        }
        FluidStack tmpFluid = fluid.copy();
        tmpFluid.amount = (int) amount;
        ((MTEHatchOutputME) mOutputHatches.get(0)).tryFillAE(tmpFluid);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPreTick(aBaseMetaTileEntity, aTick);

        if (aTick == 1) {
            ownerUUID = getBaseMetaTileEntity().getOwnerUuid();
            strongCheckOrAddUser(ownerUUID);
        }

        if (!recipeRunning && mMachine) {
            if ((aTick % 20) == 0) {
                drainFluidFromHatchesAndStoreInternally();
            }
        }
    }

    private void drainFluidFromHatchesAndStoreInternally() {
        List<FluidStack> fluidStacks = getStoredFluids();
        for (FluidStack fluidStack : fluidStacks) {
            if (validFluidMap.containsKey(fluidStack.getFluid())) {
                validFluidMap.merge(fluidStack.getFluid(), (long) fluidStack.amount, Long::sum);
                fluidStack.amount = 0;
            }
        }
        updateSlots();
    }

    @Override
    public void initDefaultModes(NBTTagCompound aNBT) {
        super.initDefaultModes(aNBT);
        if (aNBT != null && aNBT.hasKey(PLANET_BLOCK) && getControllerSlot() == null) {
            mInventory[getControllerSlotIndex()] = new ItemStack(ModBlocks.getBlock(aNBT.getString(PLANET_BLOCK)));
            aNBT.removeTag(PLANET_BLOCK);
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound NBT) {
        if (astralArrayAmount > 0) NBT.setLong(ASTRAL_ARRAY_AMOUNT_NBT_TAG, astralArrayAmount);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        // Save the quantity of fluid stored inside the controller.
        validFluidMap.forEach((key, value) -> aNBT.setLong("stored." + key.getUnlocalizedName(), value));

        aNBT.setBoolean(RECIPE_RUNNING_NBT_TAG, recipeRunning);
        aNBT.setDouble(RECIPE_SUCCESS_CHANCE_NBT_TAG, successChance);
        aNBT.setLong(ROCKET_TIER_NBT_TAG, currentRecipeRocketTier);
        aNBT.setLong(PARALLEL_AMOUNT_NBT_TAG, parallelAmount);
        aNBT.setLong(CURRENT_CIRCUIT_MULTIPLIER_TAG, currentCircuitMultiplier);
        aNBT.setLong(SUCCESSFUL_PARALLEL_AMOUNT_NBT_TAG, successfulParallelAmount);
        aNBT.setDouble(YIELD_NBT_TAG, yield);
        aNBT.setLong(ASTRAL_ARRAY_AMOUNT_NBT_TAG, astralArrayAmount);
        aNBT.setByteArray(CALCULATED_EU_OUTPUT_NBT_TAG, outputEU_BigInt.toByteArray());
        aNBT.setByteArray(CALCULATED_EU_INPUT_NBT_TAG, usedEU.toByteArray());
        aNBT.setDouble(EXTRA_PITY_CHANCE_BOOST_NBT_TAG, pityChance);
        aNBT.setDouble(PREVIOUS_RECIPE_CHANCE_NBT_TAG, previousRecipeChance);

        // Store damage values/stack sizes of GT items being outputted.
        NBTTagCompound itemStackListNBTTag = new NBTTagCompound();
        itemStackListNBTTag.setLong(NUMBER_OF_ITEMS_NBT_TAG, outputItems.size());

        int index = 0;
        for (ItemStackLong itemStackLong : outputItems) {
            // Save stack size to NBT.
            itemStackListNBTTag.setLong(index + STACK_SIZE, itemStackLong.stackSize);

            // Save ItemStack to NBT.
            aNBT.setTag(index + ITEM_STACK_NBT_TAG, itemStackLong.itemStack.writeToNBT(new NBTTagCompound()));

            index++;
        }

        aNBT.setTag(ITEM_OUTPUT_NBT_TAG, itemStackListNBTTag);

        // Store damage values/stack sizes of GT fluids being outputted.
        NBTTagCompound fluidStackListNBTTag = new NBTTagCompound();
        fluidStackListNBTTag.setLong(NUMBER_OF_FLUIDS_NBT_TAG, outputFluids.size());

        int indexFluids = 0;
        for (FluidStackLong fluidStackLong : outputFluids) {
            // Save fluid amount to NBT.
            fluidStackListNBTTag.setLong(indexFluids + FLUID_AMOUNT, fluidStackLong.amount);

            // Save FluidStack to NBT.
            aNBT.setTag(indexFluids + FLUID_STACK_NBT_TAG, fluidStackLong.fluidStack.writeToNBT(new NBTTagCompound()));

            indexFluids++;
        }

        aNBT.setTag(FLUID_OUTPUT_NBT_TAG, fluidStackListNBTTag);

        if (starMatter != null && starMatter.fluidStack != null) {

            NBTTagCompound fixedRecipeOutputs = new NBTTagCompound();

            fixedRecipeOutputs.setLong(0 + FLUID_AMOUNT, starMatter.amount);
            aNBT.setTag(CURRENT_RECIPE_STAR_MATTER_TAG, starMatter.fluidStack.writeToNBT(new NBTTagCompound()));

            fixedRecipeOutputs.setLong(1 + FLUID_AMOUNT, stellarPlasma.amount);
            aNBT.setTag(CURRENT_RECIPE_STELLAR_PLASMA_TAG, stellarPlasma.fluidStack.writeToNBT(new NBTTagCompound()));

            aNBT.setTag(CURRENT_RECIPE_FIXED_OUTPUTS_TAG, fixedRecipeOutputs);
        }

        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {

        // Load the quantity of fluid stored inside the controller.
        validFluidMap
            .forEach((key, value) -> validFluidMap.put(key, aNBT.getLong("stored." + key.getUnlocalizedName())));

        // Load other stuff from NBT.
        recipeRunning = aNBT.getBoolean(RECIPE_RUNNING_NBT_TAG);
        successChance = aNBT.getDouble(RECIPE_SUCCESS_CHANCE_NBT_TAG);
        currentRecipeRocketTier = aNBT.getLong(ROCKET_TIER_NBT_TAG);
        currentCircuitMultiplier = aNBT.getLong(CURRENT_CIRCUIT_MULTIPLIER_TAG);
        parallelAmount = aNBT.getLong(PARALLEL_AMOUNT_NBT_TAG);
        yield = aNBT.getDouble(YIELD_NBT_TAG);
        successfulParallelAmount = aNBT.getLong(SUCCESSFUL_PARALLEL_AMOUNT_NBT_TAG);
        astralArrayAmount = aNBT.getLong(ASTRAL_ARRAY_AMOUNT_NBT_TAG);
        if (aNBT.hasKey(CALCULATED_EU_OUTPUT_NBT_TAG))
            outputEU_BigInt = new BigInteger(aNBT.getByteArray(CALCULATED_EU_OUTPUT_NBT_TAG));
        if (aNBT.hasKey(CALCULATED_EU_INPUT_NBT_TAG))
            usedEU = new BigInteger(aNBT.getByteArray(CALCULATED_EU_INPUT_NBT_TAG));
        pityChance = aNBT.getDouble(EXTRA_PITY_CHANCE_BOOST_NBT_TAG);
        previousRecipeChance = aNBT.getDouble(PREVIOUS_RECIPE_CHANCE_NBT_TAG);

        // Load damage values/stack sizes of GT items being outputted and convert back to items.
        NBTTagCompound tempItemTag = aNBT.getCompoundTag(ITEM_OUTPUT_NBT_TAG);

        // Iterate over all stored items.
        for (int index = 0; index < tempItemTag.getInteger(NUMBER_OF_ITEMS_NBT_TAG); index++) {

            // Load stack size from NBT.
            long stackSize = tempItemTag.getLong(index + STACK_SIZE);

            // Load ItemStack from NBT.
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag(index + ITEM_STACK_NBT_TAG));

            outputItems.add(new ItemStackLong(itemStack, stackSize));
        }

        // Load damage values/fluid amounts of GT fluids being outputted and convert back to fluids.
        NBTTagCompound tempFluidTag = aNBT.getCompoundTag(FLUID_OUTPUT_NBT_TAG);

        // Iterate over all stored fluids.
        for (int indexFluids = 0; indexFluids < tempFluidTag.getInteger(NUMBER_OF_FLUIDS_NBT_TAG); indexFluids++) {

            // Load fluid amount from NBT.
            long fluidAmount = tempFluidTag.getLong(indexFluids + FLUID_AMOUNT);

            // Load FluidStack from NBT.
            FluidStack fluidStack = FluidStack
                .loadFluidStackFromNBT(aNBT.getCompoundTag(indexFluids + FLUID_STACK_NBT_TAG));

            outputFluids.add(new FluidStackLong(fluidStack, fluidAmount));
        }

        tempFluidTag = aNBT.getCompoundTag(CURRENT_RECIPE_FIXED_OUTPUTS_TAG);
        starMatter = new FluidStackLong(
            FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag(CURRENT_RECIPE_STAR_MATTER_TAG)),
            tempFluidTag.getLong(0 + FLUID_AMOUNT));
        stellarPlasma = new FluidStackLong(
            FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag(CURRENT_RECIPE_STELLAR_PLASMA_TAG)),
            tempFluidTag.getLong(1 + FLUID_AMOUNT));

        super.loadNBTData(aNBT);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        super.checkMachine(aBaseMetaTileEntity, aStack);
        if (!mDualInputHatches.isEmpty()) {
            return false;
        }

        if (mOutputBusses.size() != 1) {
            return false;
        }

        if (!(mOutputBusses.get(0) instanceof MTEHatchOutputBusME)) {
            return false;
        }

        if (mOutputHatches.size() != 1) {
            return false;
        }

        if (!(mOutputHatches.get(0) instanceof MTEHatchOutputME)) {
            return false;
        }

        if (mInputBusses.size() != 1) {
            return false;
        }

        if (mInputBusses.get(0) instanceof MTEHatchInputBusME) {
            return false;
        }

        if (!mEnergyHatches.isEmpty()) {
            return false;
        }

        if (!mExoticEnergyHatches.isEmpty()) {
            return false;
        }

        return mInputHatches.size() == 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_EYE_OF_HARMONY_LOOP;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>(Arrays.asList(super.getInfoData()));
        str.add(
            GOLD.toString() + STRIKETHROUGH
                + "-------------"
                + RESET
                + GOLD
                + " Control Block Statistics "
                + STRIKETHROUGH
                + "-------------");
        str.add(
            GOLD.toString() + STRIKETHROUGH
                + "-----------------"
                + RESET
                + GOLD
                + " Internal Storage "
                + STRIKETHROUGH
                + "----------------");
        validFluidMap.forEach(
            (key, value) -> str.add(BLUE + key.getLocalizedName() + RESET + " : " + RED + formatNumbers(value)));
        str.add(BLUE + "Astral Array Fabricators" + RESET + " : " + RED + formatNumbers(astralArrayAmount));
        if (recipeRunning) {
            str.add(
                GOLD.toString() + STRIKETHROUGH
                    + "-----------------"
                    + RESET
                    + GOLD
                    + " Other Stats "
                    + STRIKETHROUGH
                    + "-----------------");
            str.add("Recipe Success Chance: " + RED + formatNumbers(successChance) + RESET + "%");
            str.add("Recipe Yield: " + RED + formatNumbers(100 * yield) + RESET + "%");
            str.add("Effective Astral Array Fabricators: " + RED + formatNumbers(astralArrayAmount));
            str.add("Total Parallel: " + RED + formatNumbers(parallelAmount));
            str.add("EU Output: " + RED + toStandardForm(outputEU_BigInt) + RESET + " EU");
            str.add("EU Input:  " + RED + toStandardForm(usedEU.abs()) + RESET + " EU");
            int currentMaxProgresstime = Math.max(maxProgresstime(), 1);
            if (starMatter != null && starMatter.fluidStack != null) {
                FluidStackLong starMatterOutput = new FluidStackLong(
                    starMatter.fluidStack,
                    (long) (starMatter.amount * yield * successChance * parallelAmount));
                str.add(
                    "Average " + starMatterOutput.fluidStack.getLocalizedName()
                        + " Output: "
                        + RED
                        + formatNumbers(starMatterOutput.amount)
                        + RESET
                        + " L, "
                        + YELLOW
                        + formatNumbers(starMatterOutput.amount * 20.0 / currentMaxProgresstime)
                        + RESET
                        + " L/s");

                FluidStackLong stellarPlasmaOutput = new FluidStackLong(
                    MaterialsUEVplus.RawStarMatter.getFluid(0),
                    (long) (stellarPlasma.amount * yield * successChance * parallelAmount));
                str.add(
                    "Average " + stellarPlasmaOutput.fluidStack.getLocalizedName()
                        + " Output: "
                        + RED
                        + formatNumbers(stellarPlasmaOutput.amount)
                        + RESET
                        + " L, "
                        + YELLOW
                        + formatNumbers(stellarPlasmaOutput.amount * 20.0 / currentMaxProgresstime)
                        + RESET
                        + " L/s");
            }
            BigInteger euPerTick = (outputEU_BigInt.subtract(usedEU.abs()))
                .divide(BigInteger.valueOf(currentMaxProgresstime));

            str.add("Estimated EU/t: " + RED + toStandardForm(euPerTick) + RESET + " EU/t");
        }
        str.add(GOLD.toString() + STRIKETHROUGH + "-----------------------------------------------------");
        return str.toArray(new String[0]);
    }
}
